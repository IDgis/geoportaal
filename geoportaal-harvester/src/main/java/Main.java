import static models.QAccess.access;
import static models.QAnyText.anyText;
import static models.QDocSubject.docSubject;
import static models.QDocument.document;
import static models.QDocumentSearch.documentSearch;
import static models.QMdType.mdType;
import static models.QMdTypeLabel.mdTypeLabel;
import static models.QSubject.subject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.MultiStatus;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.client.methods.DavMethod;
import org.apache.jackrabbit.webdav.client.methods.PropFindMethod;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.querydsl.sql.Configuration;
import com.querydsl.sql.PostgreSQLTemplates;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.SQLTemplates;

public class Main {
	public static void main(String[] args) throws Exception {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(System.getenv("DB_DRIVER"));
		dataSource.setUrl(System.getenv("DB_URL"));
		dataSource.setUsername(System.getenv("DB_USER"));
		dataSource.setPassword(System.getenv("DB_PASSWORD"));
		
		PlatformTransactionManager TransactionManager = new DataSourceTransactionManager(dataSource);
		TransactionTemplate tt = new TransactionTemplate(TransactionManager);
		
		HostConfiguration hostConfig = new HostConfiguration();
		HttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
		HttpConnectionManagerParams params = new HttpConnectionManagerParams();
		connectionManager.setParams(params);
		HttpClient client = new HttpClient(connectionManager);
		client.setHostConfiguration(hostConfig);
		
		SQLTemplates templates = PostgreSQLTemplates.builder().printSchema().build();
		Configuration configuration = new Configuration(templates);
		SQLQueryFactory qf = new SQLQueryFactory(configuration, () -> DataSourceUtils.getConnection(dataSource));
		
		setUrl(qf, System.getenv("DATASET_URL"), System.getenv("DATASET_NAME"), System.getenv("DATASET_LABEL"), System.getenv("LANGUAGE"));
		setUrl(qf, System.getenv("SERVICE_URL"), System.getenv("SERVICE_NAME"), System.getenv("SERVICE_LABEL"), System.getenv("LANGUAGE"));
		setUrl(qf, System.getenv("DC_URL"), System.getenv("DC_NAME"), System.getenv("DC_LABEL"), System.getenv("LANGUAGE"));
		
		tt.execute((status) -> {
			try {			
				// Delete values from previous harvest
				qf.delete(anyText)
					.execute();
				
				qf.delete(docSubject)
					.execute();
				
				qf.delete(document)
					.execute();
				
				// Restart sequences
				executeStatement(dataSource, "alter sequence \"" 
						+ anyText.getSchemaName() + "\".\"" 
						+ anyText.getTableName() + "_id_seq\" restart with 1");
				
				executeStatement(dataSource, "alter sequence \"" 
						+ docSubject.getSchemaName() + "\".\"" 
						+ docSubject.getTableName() + "_id_seq\" restart with 1");
				
				executeStatement(dataSource, "alter sequence \"" 
						+ document.getSchemaName() + "\".\"" 
						+ document.getTableName() + "_id_seq\" restart with 1");
				
				DavMethod pFindDataset = new PropFindMethod(System.getenv("DATASET_URL"), DavConstants.PROPFIND_ALL_PROP, DavConstants.DEPTH_1);
				DavMethod pFindService = new PropFindMethod(System.getenv("SERVICE_URL"), DavConstants.PROPFIND_ALL_PROP, DavConstants.DEPTH_1);
				DavMethod pFindDc = new PropFindMethod(System.getenv("DC_URL"), DavConstants.PROPFIND_ALL_PROP, DavConstants.DEPTH_1);
				
				// Fill database
				executeWebDav(qf, client, pFindDataset, System.getenv("DATASET_URL"), MetadataType.DATASET);
				executeWebDav(qf, client, pFindService, System.getenv("SERVICE_URL"), MetadataType.SERVICE);
				executeWebDav(qf, client, pFindDc, System.getenv("DC_URL"), MetadataType.DC);
				
				// Refresh materialized view
				executeStatement(dataSource, "refresh materialized view concurrently \"" 
						+ documentSearch.getSchemaName() + "\".\"" 
						+ documentSearch.getTableName() + "\"");
				
				return true;
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
		});
	}
	
	public static void executeWebDav(SQLQueryFactory qf, HttpClient client, DavMethod pFind, String url, MetadataType metadataType) throws Exception {
		client.executeMethod(pFind);
		MultiStatus multiStatus = pFind.getResponseBodyAsMultiStatus();
		MultiStatusResponse[] responses = multiStatus.getResponses();
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder db = dbf.newDocumentBuilder();
		
		for (int i = 0; i < responses.length; i++) {
			String href = responses[i].getHref();
			
			if(href.endsWith(".xml")) {
				String filename = href.substring(href.lastIndexOf("/") + 1);
				
				try(InputStream input = new URL(url + filename).openStream();) {
					Document doc = db.parse(input);
					switch(metadataType) {
						case DATASET:
							convertDatasetValues(qf, doc);
							break;
						case SERVICE:
							convertServiceValues(qf, doc);
							break;
						case DC:
							convertDcValues(qf, doc);
					}
				}
			}
		}
	}
	
	public static void convertDatasetValues(SQLQueryFactory qf, Document d) throws Exception {
		// construct namespace context
		Map<String, String> ns = new HashMap<>();
		ns.put("gmd", "http://www.isotc211.org/2005/gmd");
		ns.put("gco", "http://www.isotc211.org/2005/gco");
		
		Map<String, String> pf = new HashMap<>();
		for(Map.Entry<String, String> e : ns.entrySet()) {
			pf.put(e.getValue(), e.getKey());
		}
		
		MetadataDocument metaDoc = parseDocument(d, ns, pf);
		
		List<String> listUuid = metaDoc.getStrings(DatasetPath.UUID.path());
		List<String> listTitle = metaDoc.getStrings(DatasetPath.TITLE.path());
		List<String> listDate = metaDoc.getStrings(DatasetPath.DATE.path());
		List<String> listOrganisationCreator = metaDoc.getStrings(DatasetPath.ORGANISATION_CREATOR.path());
		List<String> listDescription = metaDoc.getStrings(DatasetPath.ABSTRACT.path());
		List<String> listThumbnail = metaDoc.getStrings(DatasetPath.THUMBNAIL.path());
		
		List<String> listSubject = metaDoc.getStrings(DatasetPath.SUBJECT.path());
		
		List<String> listIndividualNameCreator = metaDoc.getStrings(DatasetPath.INDIVIDUAL_NAME_CREATOR.path());
		List<String> listAltTitle = metaDoc.getStrings(DatasetPath.ALT_TITLE.path());
		List<String> listMdId = metaDoc.getStrings(DatasetPath.MD_ID.path());
		List<String> listDataId = metaDoc.getStrings(DatasetPath.DATA_ID.path());
		List<String> listKeyword = metaDoc.getStrings(DatasetPath.KEYWORD.path());
		List<String> listIndividualNameContact = metaDoc.getStrings(DatasetPath.INDIVIDUAL_NAME_CONTACT.path());
		List<String> listOrganisationContact = metaDoc.getStrings(DatasetPath.ORGANISATION_CONTACT.path());
		List<String> listIndividualNameDistributor = metaDoc.getStrings(DatasetPath.INDIVIDUAL_NAME_DISTRIBUTOR.path());
		List<String> listOrganisationDistributor = metaDoc.getStrings(DatasetPath.ORGSANISATION_DISTRIBUTOR.path());
		List<String> listGeoArea = metaDoc.getStrings(DatasetPath.GEO_AREA.path());
		List<String> listPurpose = metaDoc.getStrings(DatasetPath.PURPOSE.path());
		List<String> listUseLimitation = metaDoc.getStrings(DatasetPath.USE_LIMITATION.path());
		List<String> listDescriptionSource = metaDoc.getStrings(DatasetPath.DESCRIPTION_SOURCE.path());
		List<String> listPotentialUse = metaDoc.getStrings(DatasetPath.POTENTIAL_USE.path());
		List<String> listOtherConstraint = metaDoc.getStrings(DatasetPath.OTHER_CONSTRAINT.path());
		List<String> listRelatedDataset = metaDoc.getStrings(DatasetPath.RELATED_DATASET.path());
		
		
		Integer mdTypeId = qf.select(mdType.id)
				.from(mdType)
				.where(mdType.url.eq(System.getenv("DATASET_URL")))
				.fetchOne();
		
		LocalDate finalLD = null;
		for(String date : listDate) {
			if(finalLD == null) {
				finalLD = LocalDate.parse(date);
			} else {
				LocalDate tempLD = LocalDate.parse(date);
				if(tempLD.isAfter(finalLD)) {
					finalLD = LocalDate.parse(date);
				}
			}
		}
		
		Timestamp ts;
		if(finalLD != null) {
			ts = Timestamp.valueOf(finalLD.atStartOfDay());
		} else {
			ts = null;
		}
		
		List<String> listFinalThumbnail = new ArrayList<>();
		for(String thumbnail : listThumbnail) {
			String finalThumbnail = "";
			if(thumbnail.trim().startsWith("http")) {
				finalThumbnail = thumbnail.trim().replaceAll("\\\\", "/");
			}
			
			listFinalThumbnail.add(finalThumbnail);
		}
		
		Integer accessId;
		if(Math.random() < 0.5) {
			accessId = 1;
		} else {
			accessId = 2;
		}
		
		try {
			qf.insert(document)
			.set(document.uuid, getValueFromList(listUuid))
			.set(document.mdTypeId, mdTypeId)
			.set(document.title, getValueFromList(listTitle))
			.set(document.date, ts)
			.set(document.creator, getValueFromList(listOrganisationCreator))
			.set(document.description, getValueFromList(listDescription))
			.set(document.thumbnail, getValueFromList(listFinalThumbnail))
			.set(document.accessId, accessId)
			.execute();
		} catch(Exception e) {
			throw new Exception(e.getMessage() + " " + getValueFromList(listUuid));
		}
		
		Integer docId = qf.select(document.id)
				.from(document)
				.where(document.uuid.eq(getValueFromList(listUuid)))
				.fetchOne();
		
		for(String subjectOne : listSubject) {
			Integer subjectId = qf.select(subject.id)
					.from(subject)
					.where(subject.name.eq(subjectOne))
					.fetchOne();
			
			qf.insert(docSubject)
					.set(docSubject.documentId, docId)
					.set(docSubject.subjectId, subjectId)
					.execute();
		}
		
		setAnyText(qf, docId, listIndividualNameCreator);
		setAnyText(qf, docId, listAltTitle);
		setAnyText(qf, docId, listMdId);
		setAnyText(qf, docId, listDataId);
		setAnyText(qf, docId, listKeyword);
		setAnyText(qf, docId, listIndividualNameContact);
		setAnyText(qf, docId, listOrganisationContact);
		setAnyText(qf, docId, listIndividualNameDistributor);
		setAnyText(qf, docId, listOrganisationDistributor);
		setAnyText(qf, docId, listGeoArea);
		setAnyText(qf, docId, listPurpose);
		setAnyText(qf, docId, listUseLimitation);
		setAnyText(qf, docId, listDescriptionSource);
		setAnyText(qf, docId, listPotentialUse);
		setAnyText(qf, docId, listOtherConstraint);
		setAnyText(qf, docId, listRelatedDataset);
		
	}
	
	public static void convertServiceValues(SQLQueryFactory qf, Document d) throws Exception {
		// construct namespace context
		Map<String, String> ns = new HashMap<>();
		ns.put("gmd", "http://www.isotc211.org/2005/gmd");
		ns.put("gco", "http://www.isotc211.org/2005/gco");
		ns.put("srv", "http://www.isotc211.org/2005/srv");
		
		Map<String, String> pf = new HashMap<>();
		for(Map.Entry<String, String> e : ns.entrySet()) {
			pf.put(e.getValue(), e.getKey());
		}
		
		MetadataDocument metaDoc = parseDocument(d, ns, pf);
		
		List<String> listUuid = metaDoc.getStrings(ServicePath.UUID.path());
		List<String> listTitle = metaDoc.getStrings(ServicePath.TITLE.path());
		List<String> listDate = metaDoc.getStrings(ServicePath.DATE.path());
		List<String> listOrganisationCreator = metaDoc.getStrings(ServicePath.ORGANISATION_CREATOR.path());
		List<String> listDescription = metaDoc.getStrings(ServicePath.ABSTRACT.path());
		
		List<String> listUseLimitation = metaDoc.getStrings(ServicePath.USE_LIMITATION.path());
		List<String> listOtherConstraint = metaDoc.getStrings(ServicePath.OTHER_CONSTRAINT.path());
		List<String> listIndividualNameCreator = metaDoc.getStrings(ServicePath.INDIVIDUAL_NAME_CREATOR.path());
		List<String> listOrganisationContact = metaDoc.getStrings(ServicePath.ORGANISATION_CONTACT.path());
		List<String> listIndividualNameContact = metaDoc.getStrings(ServicePath.INDIVIDUAL_NAME_CONTACT.path());
		List<String> listGeoArea = metaDoc.getStrings(ServicePath.GEO_AREA.path());
		List<String> listLayer = metaDoc.getStrings(ServicePath.LAYER.path());
		List<String> listAttachedFile = metaDoc.getStrings(ServicePath.ATTACHED_FILE.path());
		List<String> listKeyword = metaDoc.getStrings(ServicePath.KEYWORD.path());
		
		Integer mdTypeId = qf.select(mdType.id)
				.from(mdType)
				.where(mdType.url.eq(System.getenv("SERVICE_URL")))
				.fetchOne();
		
		LocalDate finalLD = null;
		for(String date : listDate) {
			if(finalLD == null) {
				finalLD = LocalDate.parse(date);
			} else {
				LocalDate tempLD = LocalDate.parse(date);
				if(tempLD.isAfter(finalLD)) {
					finalLD = LocalDate.parse(date);
				}
			}
		}
		
		Timestamp ts;
		if(finalLD != null) {
			ts = Timestamp.valueOf(finalLD.atStartOfDay());
		} else {
			ts = null;
		}
		
		String thumbnail = null;
		
		Integer accessId;
		if(Math.random() < 0.5) {
			accessId = 1;
		} else {
			accessId = 2;
		}
		
		try {
			qf.insert(document)
				.set(document.uuid, getValueFromList(listUuid))
				.set(document.mdTypeId, mdTypeId)
				.set(document.title, getValueFromList(listTitle))
				.set(document.date, ts)
				.set(document.creator, getValueFromList(listOrganisationCreator))
				.set(document.description, getValueFromList(listDescription))
				.set(document.thumbnail, thumbnail)
				.set(document.accessId, accessId)
				.execute();
		} catch(Exception e) {
			e.printStackTrace();
			throw new Exception(e.getCause() + " " + getValueFromList(listUuid));
		}
		
		Integer docId = qf.select(document.id)
				.from(document)
				.where(document.uuid.eq(getValueFromList(listUuid)))
				.fetchOne();
		
		setAnyText(qf, docId, listUseLimitation);
		setAnyText(qf, docId, listOtherConstraint);
		setAnyText(qf, docId, listIndividualNameCreator);
		setAnyText(qf, docId, listOrganisationContact);
		setAnyText(qf, docId, listIndividualNameContact);
		setAnyText(qf, docId, listGeoArea);
		setAnyText(qf, docId, listLayer);
		setAnyText(qf, docId, listAttachedFile);
		setAnyText(qf, docId, listKeyword);
	}
	
	public static void convertDcValues(SQLQueryFactory qf, Document d) throws Exception {
		// construct namespace context
		Map<String, String> ns = new HashMap<>();
		ns.put("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		ns.put("dc", "http://purl.org/dc/elements/1.1/");
		ns.put("dcterms", "http://purl.org/dc/terms/");
		
		Map<String, String> pf = new HashMap<>();
		for(Map.Entry<String, String> e : ns.entrySet()) {
			pf.put(e.getValue(), e.getKey());
		}
		
		MetadataDocument metaDoc = parseDocument(d, ns, pf);
		
		List<String> listUuid = metaDoc.getStrings(DcPath.UUID.path());
		List<String> listTitle = metaDoc.getStrings(DcPath.TITLE.path());
		List<String> listDate = metaDoc.getStrings(DcPath.DATE.path());
		List<String> listOrganisationCreator = metaDoc.getStrings(DcPath.ORGANISATION_CREATOR.path());
		List<String> listDescription = metaDoc.getStrings(DcPath.ABSTRACT.path());
		
		List<String> listLocation = metaDoc.getStrings(DcPath.LOCATION.path());
		List<String> listNumber = metaDoc.getStrings(DcPath.NUMBER.path());
		List<String> listType = metaDoc.getStrings(DcPath.TYPE.path());
		List<String> listPublisher = metaDoc.getStrings(DcPath.PUBLISHER.path());
		List<String> listContributor = metaDoc.getStrings(DcPath.CONTRIBUTOR.path());
		List<String> listRights = metaDoc.getStrings(DcPath.RIGHTS.path());
		List<String> listFormat = metaDoc.getStrings(DcPath.FORMAT.path());
		List<String> listSource = metaDoc.getStrings(DcPath.SOURCE.path());
		List<String> listSubject = metaDoc.getStrings(DcPath.SUBJECT.path());
		List<String> listLanguage = metaDoc.getStrings(DcPath.LANGUAGE.path());
		
		Integer mdTypeId = qf.select(mdType.id)
				.from(mdType)
				.where(mdType.url.eq(System.getenv("DC_URL")))
				.fetchOne();
		
		LocalDate ld = LocalDate.parse(getValueFromList(listDate));
		Timestamp ts;
		if(ld != null) {
			ts = Timestamp.valueOf(ld.atStartOfDay());
		} else {
			ts = null;
		}
		
		String thumbnail = null;
		
		Integer accessId;
		if(Math.random() < 0.5) {
			accessId = 1;
		} else {
			accessId = 2;
		}
		
		try {
			qf.insert(document)
			.set(document.uuid, getValueFromList(listUuid))
			.set(document.mdTypeId, mdTypeId)
			.set(document.title, getValueFromList(listTitle))
			.set(document.date, ts)
			.set(document.creator, getValueFromList(listOrganisationCreator))
			.set(document.description, getValueFromList(listDescription))
			.set(document.thumbnail, thumbnail)
			.set(document.accessId, accessId)
			.execute();
		} catch(Exception e) {
			e.printStackTrace();
			throw new Exception(e.getCause() + " " + getValueFromList(listUuid));
		}
		
		Integer docId = qf.select(document.id)
				.from(document)
				.where(document.uuid.eq(getValueFromList(listUuid)))
				.fetchOne();
		
		for(String subjectOne : listSubject) {
			Integer subjectId = qf.select(subject.id)
					.from(subject)
					.where(subject.name.eq(subjectOne))
					.fetchOne();
			
			qf.insert(docSubject)
					.set(docSubject.documentId, docId)
					.set(docSubject.subjectId, subjectId)
					.execute();
		}
		
		setAnyText(qf, docId, listLocation);
		setAnyText(qf, docId, listNumber);
		setAnyText(qf, docId, listType);
		setAnyText(qf, docId, listPublisher);
		setAnyText(qf, docId, listContributor);
		setAnyText(qf, docId, listRights);
		setAnyText(qf, docId, listFormat);
		setAnyText(qf, docId, listSource);
		setAnyText(qf, docId, listSubject);
		setAnyText(qf, docId, listLanguage);
	}
	
	private static void setAnyText(SQLQueryFactory qf, Integer docId, List<String> listValues) {
		for(String value : listValues) {
			qf.insert(anyText)
			.set(anyText.documentId, docId)
			.set(anyText.content, value.trim())
			.execute();
		}
	}
	
	private static String getValueFromList(List<String> listValues) {
		if(listValues.size() == 0) {
			return null;
		} else {
			return listValues.get(0).trim();
		}
	}
	
	private static MetadataDocument parseDocument(Document d, Map<String, String> ns, Map<String, String> pf) throws Exception {
		// parse xml to DOM
		NamespaceContext nc = new NamespaceContext() {

			@Override
			public String getNamespaceURI(String pf) {
				return ns.get(pf);
			}

			@Override
			public String getPrefix(String ns) {
				return pf.get(ns);
			}
			
			@Override
			@SuppressWarnings("rawtypes")
			public Iterator getPrefixes(String ns) {
				return Arrays.asList(getPrefix(ns)).iterator();
			}
			
		};
		
		// construct XPath evaluator
		XPathFactory xf = XPathFactory.newInstance();
		XPath xp = xf.newXPath();
		xp.setNamespaceContext(nc);
		
		return new MetadataDocument(d, xp);
	}
	
	public static void setUrl(SQLQueryFactory qf, String url, String typeName, String typeLabelName, String language) {
		List<Integer> mdTypes = qf.select(mdType.id)
				.from(mdType)
				.where(mdType.url.eq(url))
				.fetch();
			
		Integer mdTypesCount = mdTypes.size();
		
		if(mdTypesCount.equals(0)) {
			qf.insert(mdType)
				.set(mdType.url, url)
				.set(mdType.name, typeName)
				.execute();
			
			Integer mdTypeId = qf.select(mdType.id)
					.from(mdType)
					.where(mdType.url.eq(url))
					.fetchOne();
			
			qf.insert(mdTypeLabel)
					.set(mdTypeLabel.mdTypeId, mdTypeId)
					.set(mdTypeLabel.language, language)
					.set(mdTypeLabel.title, typeLabelName)
					.execute();
		}
	}
	
	public static void executeStatement(DriverManagerDataSource dataSource, String statement) throws Exception {
		try(Statement stmt = DataSourceUtils.getConnection(dataSource).createStatement()) {
			stmt.execute(statement);
		}
	}
	
	public enum MetadataType {
		DATASET, SERVICE, DC;
	}
	
	public static class MetadataDocument {
		private final Document d;
		private final XPath xp;
		
		MetadataDocument(Document d, XPath xp) {
			this.d = d;
			this.xp = xp;
		}
		
		List<String> getStrings(String path) throws Exception {
			if(!path.endsWith("text()") && !path.endsWith("@uuidref")) {
				throw new RuntimeException("path should end with text() or @uuidref");
			}
			
			List<String> retval = new ArrayList<>();
			
			NodeList nl = (NodeList) xp.evaluate(path, d, XPathConstants.NODESET);
			
			for(int i = 0; i < nl.getLength(); i++) {
				retval.add(nl.item(i).getNodeValue());
			}
			
			return retval;
		}
	}
}
