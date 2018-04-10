import static java.util.concurrent.TimeUnit.*;

import static models.QAccess.access;
import static models.QAnyText.anyText;
import static models.QDocSubject.docSubject;
import static models.QDocument.document;
import static models.QDocumentSearch.documentSearch;
import static models.QHarvestSession.harvestSession;
import static models.QMdType.mdType;
import static models.QSubject.subject;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

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
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.property.DavPropertySet;
import org.apache.jackrabbit.webdav.xml.Namespace;
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
	
	private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
	
	private static final Path directory = Paths.get("/var/lib/harvest/");
	
	private static final Path file = Paths.get("/var/lib/harvest/last_harvest_errors.txt");
	
	private static final ScheduledExecutorService scheduler =
			Executors.newScheduledThreadPool(1);
	
	public static void main(String[] args) throws Exception {
		
		if(!Files.exists(directory)) {
			Files.createDirectory(directory);
		}
		
		Files.deleteIfExists(file);
		Files.createFile(file);
		
		final Runnable harvest = () -> {
			try {
				doHarvest();
			} catch(Exception e) {
				writeToMonitorFile("ERROR general error in main");
				e.printStackTrace();
			}
		};
		
		Integer interval = Integer.parseInt(System.getenv("HARVEST_INTERVAL"));
		System.out.println("Harvesting scheduled with an interval of " + interval + " minutes");
		
		final ScheduledFuture<?> harvestHandle =
				scheduler.scheduleAtFixedRate(harvest, 1, interval, MINUTES);
	}
	
	public static void doHarvest() throws Exception {
		LOGGER.setLevel(Level.INFO);
		
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(System.getenv("DB_DRIVER"));
		dataSource.setUrl(System.getenv("DB_URL"));
		dataSource.setUsername(System.getenv("DB_USER"));
		dataSource.setPassword(System.getenv("DB_PASSWORD"));
		
		PlatformTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
		TransactionTemplate tt = new TransactionTemplate(transactionManager);
		
		HostConfiguration hostConfig = new HostConfiguration();
		HttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
		HttpConnectionManagerParams params = new HttpConnectionManagerParams();
		connectionManager.setParams(params);
		HttpClient client = new HttpClient(connectionManager);
		client.setHostConfiguration(hostConfig);
		
		SQLTemplates templates = PostgreSQLTemplates.builder().printSchema().build();
		Configuration configuration = new Configuration(templates);
		SQLQueryFactory qf = new SQLQueryFactory(configuration, () -> DataSourceUtils.getConnection(dataSource));
		
		LOGGER.info("starting...");
		
		tt.execute((status) -> {
			try {			
				// Delete values from previous harvest
				qf.delete(mdType)
					.where(mdType.url.eq(System.getenv("DATA_URL")))
					.execute();
				
				qf.insert(mdType)
					.set(mdType.url, System.getenv("DATA_URL"))
					.set(mdType.name, System.getenv("DATA_NAME"))
					.execute();
				
				DavMethod pFind = new PropFindMethod(System.getenv("DATA_URL"), DavConstants.PROPFIND_ALL_PROP, DavConstants.DEPTH_1);
				String trustedHeader = System.getenv("TRUSTED_HEADER");
				if(trustedHeader != null) {
					pFind.addRequestHeader(trustedHeader, "1");
				}
				
				// Populate database
				if("dataset".equals(System.getenv("DATA_NAME"))) {
					executeWebDav(qf, client, pFind, System.getenv("DATA_URL"), MetadataType.DATASET);
				} else if("service".equals(System.getenv("DATA_NAME"))) {
					executeWebDav(qf, client, pFind, System.getenv("DATA_URL"), MetadataType.SERVICE);
				} else if("dc".equals(System.getenv("DATA_NAME"))) {
					executeWebDav(qf, client, pFind, System.getenv("DATA_URL"), MetadataType.DC);
				}
				
				// Insert harvest session information
				Integer internCount = qf.select(document.uuid)
					.from(document)
					.join(mdType).on(mdType.id.eq(document.mdTypeId))
					.where(mdType.name.eq(System.getenv("DATA_NAME")))
					.fetch()
					.size();
				
				Integer externCount = qf.select(document.uuid)
					.from(document)
					.join(mdType).on(mdType.id.eq(document.mdTypeId))
					.join(access).on(access.id.eq(document.accessId))
					.where(mdType.name.eq(System.getenv("DATA_NAME"))
							.and(access.name.eq("extern")))
					.fetch()
					.size();
				
				qf.insert(harvestSession)
					.set(harvestSession.type, System.getenv("DATA_NAME"))
					.set(harvestSession.internCount, internCount)
					.set(harvestSession.externCount, externCount)
					.set(harvestSession.createTime, Timestamp.valueOf(LocalDateTime.now()))
					.execute();
				
				// Refresh materialized view
				executeStatement(dataSource, "refresh materialized view concurrently \"" 
						+ documentSearch.getSchemaName() + "\".\"" 
						+ documentSearch.getTableName() + "\"");
				
				return true;
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
		});
		
		LOGGER.info("done...");
	}
	
	public static void executeWebDav(SQLQueryFactory qf, HttpClient client, DavMethod pFind, String url, MetadataType metadataType) throws Exception {
		client.executeMethod(pFind);
		MultiStatus multiStatus = pFind.getResponseBodyAsMultiStatus();
		MultiStatusResponse[] responses = multiStatus.getResponses();
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder db = dbf.newDocumentBuilder();
		
		for (MultiStatusResponse response : responses) {
			String href = response.getHref();
			
			if(href != null && href.endsWith(".xml")) {
				String filename = href.substring(href.lastIndexOf("/") + 1);
				LOGGER.info(filename);
				
				HttpURLConnection connection = (HttpURLConnection)new URL(url + filename).openConnection();
				String trustedHeader = System.getenv("TRUSTED_HEADER");
				if(trustedHeader != null) {
					connection.setRequestProperty(trustedHeader, "1");
				}
				
				try(InputStream input = connection.getInputStream()) {
					Document doc = db.parse(input);
					switch(metadataType) {
						case DATASET:
							DavPropertySet propertiesDataset = response.getProperties(200);
							DavProperty<?> confidentialDataset = propertiesDataset.get("confidential", Namespace.getNamespace("http://idgis.nl/geopublisher"));
							DavProperty<?> publishedDataset = propertiesDataset.get("published", Namespace.getNamespace("http://idgis.nl/geopublisher"));
							
							boolean securedDataset;
							if(confidentialDataset == null) {
								securedDataset = false;
							} else {
								securedDataset = Boolean.parseBoolean(confidentialDataset.getValue().toString());
							}
							
							boolean externDataset;
							if(publishedDataset == null) {
								externDataset = false;
							} else {
								externDataset = Boolean.parseBoolean(publishedDataset.getValue().toString());
							}
							
							convertDatasetValues(qf, doc, securedDataset, externDataset);
							break;
						case SERVICE:
							DavPropertySet propertiesService = response.getProperties(200);
							DavProperty<?> confidentialService = propertiesService.get("confidential", Namespace.getNamespace("http://idgis.nl/geopublisher"));
							
							boolean securedService;
							if(confidentialService == null) {
								securedService = false;
							} else {
								securedService = Boolean.parseBoolean(confidentialService.getValue().toString());
							}
							
							convertServiceValues(qf, doc, securedService);
							break;
						case DC:
							convertDcValues(qf, doc);
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void convertDatasetValues(SQLQueryFactory qf, Document d, boolean secured, boolean published) {
		// construct namespace context
		Map<String, String> ns = new HashMap<>();
		ns.put("gmd", "http://www.isotc211.org/2005/gmd");
		ns.put("gco", "http://www.isotc211.org/2005/gco");
		
		Map<String, String> pf = new HashMap<>();
		for(Map.Entry<String, String> e : ns.entrySet()) {
			pf.put(e.getValue(), e.getKey());
		}
		
		MetadataDocument metaDoc = parseDocument(d, ns, pf);
		
		try {
			List<String> listUuid = metaDoc.getStrings(DatasetPath.UUID.path());
			List<String> listTitle = metaDoc.getStrings(DatasetPath.TITLE.path());
			List<String> listDate = metaDoc.getStrings(DatasetPath.DATE.path());
			List<String> listOrganisationCreator = metaDoc.getStrings(DatasetPath.ORGANISATION_CREATOR.path());
			List<String> listDescription = metaDoc.getStrings(DatasetPath.ABSTRACT.path());
			List<String> listThumbnail = metaDoc.getStrings(DatasetPath.THUMBNAIL.path());
			
			List<String> listSubject = metaDoc.getStrings(DatasetPath.SUBJECT.path());
			
			List<String> listSpatialSchema = metaDoc.getStrings(DatasetPath.SPATIAL_SCHEMA.path());
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
			List<String> listUseConstraint = metaDoc.getStrings(DatasetPath.USE_CONSTRAINT.path());
			List<String> listAccessConstraint = metaDoc.getStrings(DatasetPath.ACCESS_CONSTRAINT.path());
			List<String> listDescriptionSource = metaDoc.getStrings(DatasetPath.DESCRIPTION_SOURCE.path());
			List<String> listPotentialUse = metaDoc.getStrings(DatasetPath.POTENTIAL_USE.path());
			List<String> listOtherConstraint = metaDoc.getStrings(DatasetPath.OTHER_CONSTRAINT.path());
			List<String> listRelatedDataset = metaDoc.getStrings(DatasetPath.RELATED_DATASET.path());
			
			List<String> listViewerUrls = metaDoc.getStrings(DatasetPath.VIEWER_URL.path());
			
			Integer mdTypeId = qf.select(mdType.id)
					.from(mdType)
					.where(mdType.url.eq(System.getenv("DATA_URL")))
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
			
			Integer internId = qf.select(access.id)
					.from(access)
					.where(access.name.eq("intern"))
					.fetchOne();
			
			Integer externId = qf.select(access.id)
					.from(access)
					.where(access.name.eq("extern"))
					.fetchOne();
			
			Integer accessId = internId;
			if(secured == false) {
				accessId = externId;
			}
			
			Boolean downloadable = false;
			Boolean wmsOnly = false;
			for(String ul : listUseLimitation) {
				if(ul.equals("Downloadable data")) {
					downloadable = true;
				}
				
				if(ul.equals("Alleen WMS extern")) {
					wmsOnly = true;
				}
			}
			
			String viewerUrl = null;
			for(String url : listViewerUrls) {
				if(url.startsWith(System.getenv("VIEWER_URL_DATASET_PUBLIC_PREFIX")) ||
						url.startsWith(System.getenv("VIEWER_URL_DATASET_SECURE_PREFIX")) ||
						url.startsWith(System.getenv("VIEWER_URL_DATASET_WMSONLY_PREFIX"))) {
					viewerUrl = url;
					break;
				}
			}
			
			qf.insert(document)
				.set(document.uuid, getValueFromList(listUuid))
				.set(document.mdTypeId, mdTypeId)
				.set(document.title, getValueFromList(listTitle))
				.set(document.date, ts)
				.set(document.creator, getValueFromList(listOrganisationCreator))
				.set(document.description, getValueFromList(listDescription))
				.set(document.thumbnail, getValueFromList(listFinalThumbnail))
				.set(document.accessId, accessId)
				.set(document.downloadable, downloadable)
				.set(document.spatialSchema, getValueFromList(listSpatialSchema))
				.set(document.published, published)
				.set(document.viewerUrl, viewerUrl)
				.set(document.wmsOnly, wmsOnly)
				.execute();
			
			Long docId = qf.select(document.id)
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
			setAnyText(qf, docId, listUseConstraint);
			setAnyText(qf, docId, listAccessConstraint);
			setAnyText(qf, docId, listDescriptionSource);
			setAnyText(qf, docId, listPotentialUse);
			setAnyText(qf, docId, listOtherConstraint);
			setAnyText(qf, docId, listRelatedDataset);
			setAnyText(qf, docId, listSpatialSchema);
		} catch(Exception e) {
			writeToMonitorFile("ERROR error inserting in database");
			e.printStackTrace();
		}
	}
	
	public static void convertServiceValues(SQLQueryFactory qf, Document d, boolean secured) {
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
		
		try {
			List<String> listUuid = metaDoc.getStrings(ServicePath.UUID.path());
			List<String> listTitle = metaDoc.getStrings(ServicePath.TITLE.path());
			List<String> listDate = metaDoc.getStrings(ServicePath.DATE.path());
			List<String> listOrganisationCreator = metaDoc.getStrings(ServicePath.ORGANISATION_CREATOR.path());
			List<String> listDescription = metaDoc.getStrings(ServicePath.ABSTRACT.path());
			List<String> listTypeService = metaDoc.getStrings(ServicePath.TYPE_SERVICE.path());
			
			List<String> listUseLimitation = metaDoc.getStrings(ServicePath.USE_LIMITATION.path());
			List<String> listAccessConstraint = metaDoc.getStrings(ServicePath.ACCESS_CONSTRAINT.path());
			List<String> listOtherConstraint = metaDoc.getStrings(ServicePath.OTHER_CONSTRAINT.path());
			List<String> listIndividualNameCreator = metaDoc.getStrings(ServicePath.INDIVIDUAL_NAME_CREATOR.path());
			List<String> listOrganisationContact = metaDoc.getStrings(ServicePath.ORGANISATION_CONTACT.path());
			List<String> listIndividualNameContact = metaDoc.getStrings(ServicePath.INDIVIDUAL_NAME_CONTACT.path());
			List<String> listGeoArea = metaDoc.getStrings(ServicePath.GEO_AREA.path());
			List<String> listLayer = metaDoc.getStrings(ServicePath.LAYER.path());
			List<String> listAttachedFile = metaDoc.getStrings(ServicePath.ATTACHED_FILE.path());
			List<String> listKeyword = metaDoc.getStrings(ServicePath.KEYWORD.path());
			
			List<String> listViewerUrls = metaDoc.getStrings(ServicePath.VIEWER_URL.path());
			
			Integer mdTypeId = qf.select(mdType.id)
					.from(mdType)
					.where(mdType.url.eq(System.getenv("DATA_URL")))
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
			
			Integer internId = qf.select(access.id)
					.from(access)
					.where(access.name.eq("intern"))
					.fetchOne();
			
			Integer externId = qf.select(access.id)
					.from(access)
					.where(access.name.eq("extern"))
					.fetchOne();
			
			Integer accessId;
			if(secured) {
				accessId = internId;
			} else {
				accessId = externId;
			}
			
			String viewerUrl = null;
			for(String url : listViewerUrls) {
				if(url.startsWith(System.getenv("VIEWER_URL_SERVICE_PUBLIC_PREFIX")) ||
						url.startsWith(System.getenv("VIEWER_URL_SERVICE_SECURE_PREFIX")) ||
						url.startsWith(System.getenv("VIEWER_URL_SERVICE_WMSONLY_PREFIX"))) {
					viewerUrl = url;
					break;
				}
			}
			
			qf.insert(document)
				.set(document.uuid, getValueFromList(listUuid))
				.set(document.mdTypeId, mdTypeId)
				.set(document.title, getValueFromList(listTitle))
				.set(document.date, ts)
				.set(document.creator, getValueFromList(listOrganisationCreator))
				.set(document.description, getValueFromList(listDescription))
				.set(document.thumbnail, thumbnail)
				.set(document.accessId, accessId)
				.set(document.typeService, getValueFromList(listTypeService))
				.set(document.viewerUrl, viewerUrl)
				.execute();
			
			Long docId = qf.select(document.id)
					.from(document)
					.where(document.uuid.eq(getValueFromList(listUuid)))
					.fetchOne();
			
			setAnyText(qf, docId, listUseLimitation);
			setAnyText(qf, docId, listAccessConstraint);
			setAnyText(qf, docId, listOtherConstraint);
			setAnyText(qf, docId, listIndividualNameCreator);
			setAnyText(qf, docId, listOrganisationContact);
			setAnyText(qf, docId, listIndividualNameContact);
			setAnyText(qf, docId, listGeoArea);
			setAnyText(qf, docId, listLayer);
			setAnyText(qf, docId, listAttachedFile);
			setAnyText(qf, docId, listKeyword);
		} catch(Exception e) {
			writeToMonitorFile("ERROR error inserting in database");
			e.printStackTrace();
		}
	}
	
	public static void convertDcValues(SQLQueryFactory qf, Document d) {
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
		
		try {
			List<String> listUuid = metaDoc.getStrings(DcPath.UUID.path());
			List<String> listTitle = metaDoc.getStrings(DcPath.TITLE.path());
			List<String> listDateCreate = metaDoc.getStrings(DcPath.DATE_CREATE.path());
			List<String> listDateIssued = metaDoc.getStrings(DcPath.DATE_ISSUED.path());
			List<String> listOrganisationCreator = metaDoc.getStrings(DcPath.ORGANISATION_CREATOR.path());
			List<String> listDescription = metaDoc.getStrings(DcPath.ABSTRACT.path());
			
			List<String> listLocation = metaDoc.getStrings(DcPath.LOCATION.path());
			List<String> listNumber = metaDoc.getStrings(DcPath.NUMBER.path());
			List<String> listType = metaDoc.getStrings(DcPath.TYPE.path());
			List<String> listPublisher = metaDoc.getStrings(DcPath.PUBLISHER.path());
			List<String> listContributor = metaDoc.getStrings(DcPath.CONTRIBUTOR.path());
			List<String> listRights = metaDoc.getStrings(DcPath.RIGHTS.path());
			List<String> listUseLimitations = metaDoc.getStrings(DcPath.USE_LIMITATION.path());
			List<String> listFormat = metaDoc.getStrings(DcPath.FORMAT.path());
			List<String> listSource = metaDoc.getStrings(DcPath.SOURCE.path());
			List<String> listSubject = metaDoc.getStrings(DcPath.SUBJECT.path());
			List<String> listLanguage = metaDoc.getStrings(DcPath.LANGUAGE.path());
			
			Integer mdTypeId = qf.select(mdType.id)
					.from(mdType)
					.where(mdType.url.eq(System.getenv("DATA_URL")))
					.fetchOne();
			
			String dateCreate = getValueFromList(listDateCreate);
			String dateIssued = getValueFromList(listDateIssued);
			
			LocalDate ld = null;
			if(dateIssued != null) {
				ld = LocalDate.parse(getValueFromList(listDateIssued));
			} else if(dateCreate != null) {
				ld = LocalDate.parse(getValueFromList(listDateCreate));
			}
			
			Timestamp ts;
			if(ld != null) {
				ts = Timestamp.valueOf(ld.atStartOfDay());
			} else {
				ts = null;
			}
			
			String thumbnail = null;
			
			Integer internId = qf.select(access.id)
					.from(access)
					.where(access.name.eq("intern"))
					.fetchOne();
			
			Integer externId = qf.select(access.id)
					.from(access)
					.where(access.name.eq("extern"))
					.fetchOne();
			
			Integer accessId = internId;
			if("De bron mag ook voor externe partijen vindbaar zijn".equals(getValueFromList(listUseLimitations))) {
				accessId = externId;
			}
			
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
			
			Long docId = qf.select(document.id)
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
		} catch(Exception e) {
			writeToMonitorFile("ERROR error inserting in database");
			e.printStackTrace();
		}
	}
	
	private static void setAnyText(SQLQueryFactory qf, Long docId, List<String> listValues) {
		for(String value : listValues) {
			qf.insert(anyText)
				.set(anyText.documentId, docId)
				.set(anyText.content, value.trim())
				.execute();
		}
	}
	
	private static String getValueFromList(List<String> listValues) {
		if(listValues.isEmpty()) {
			return null;
		} else {
			return listValues.get(0).trim();
		}
	}
	
	private static void writeToMonitorFile(String message) {
		try(BufferedWriter writer = Files.newBufferedWriter(file, StandardOpenOption.APPEND)) {
			LocalDateTime now = LocalDateTime.now();
			String dateStr = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm"));
			
			writer.write(dateStr + " " + message);
			writer.newLine();
			writer.flush();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private static MetadataDocument parseDocument(Document d, Map<String, String> ns, Map<String, String> pf) {
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
	
	public static void executeStatement(DriverManagerDataSource dataSource, String statement) throws SQLException {
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
			if(!path.endsWith("text()") && !path.endsWith("@uuidref") &&!path.endsWith("codeListValue")) {
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
