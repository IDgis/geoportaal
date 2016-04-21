import static models.QMdType.mdType;
import static models.QMdTypeLabel.mdTypeLabel;

import java.io.InputStream;
import java.net.URL;
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
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.querydsl.sql.Configuration;
import com.querydsl.sql.PostgreSQLTemplates;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.SQLTemplates;

public class Main {
	public static void main(String[] args) throws Exception {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(System.getenv("db.driver"));
		dataSource.setUrl(System.getenv("db.url"));
		dataSource.setUsername(System.getenv("db.user"));
		dataSource.setPassword(System.getenv("db.password"));
		
		HostConfiguration hostConfig = new HostConfiguration();
		HttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
		HttpConnectionManagerParams params = new HttpConnectionManagerParams();
		connectionManager.setParams(params);
		HttpClient client = new HttpClient(connectionManager);
		client.setHostConfiguration(hostConfig);
		
		SQLTemplates templates = PostgreSQLTemplates.builder().printSchema().build();
		Configuration configuration = new Configuration(templates);
		SQLQueryFactory qf = new SQLQueryFactory(configuration, dataSource);
		
		setUrl(qf, System.getenv("dataset.url"), System.getenv("dataset.name"), System.getenv("dataset.label"), System.getenv("language"));
		setUrl(qf, System.getenv("service.url"), System.getenv("service.name"), System.getenv("service.label"), System.getenv("language"));
		
		DavMethod pFind = new PropFindMethod(System.getenv("dataset.url"), DavConstants.PROPFIND_ALL_PROP, DavConstants.DEPTH_1);
		
		executeWebDav(client, pFind, System.getenv("dataset.url"));
	}
	
	public static void executeWebDav(HttpClient client, DavMethod pFind, String url) throws Exception {
		client.executeMethod(pFind);
		MultiStatus multiStatus = pFind.getResponseBodyAsMultiStatus();
		MultiStatusResponse[] responses = multiStatus.getResponses();
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder db = dbf.newDocumentBuilder();
		
		for (int i = 0; i < 6; i++) {
			String href = responses[i].getHref();
			if(href.endsWith(".xml")) {
				String filename = href.substring(href.lastIndexOf("/") + 1);
				
				try(InputStream input = new URL(url + filename).openStream();) {
					Document doc = db.parse(input);
					convertDatasetValues(doc);
				}
			}
		}
	}
	
	public static void convertDatasetValues(Document d) throws Exception {
		// construct namespace context
		Map<String, String> ns = new HashMap<>();
		ns.put("gmd", "http://www.isotc211.org/2005/gmd");
		ns.put("gco", "http://www.isotc211.org/2005/gco");
		ns.put("gml", "http://www.opengis.net/gml");
		
		Map<String, String> pf = new HashMap<>();
		for(Map.Entry<String, String> e : ns.entrySet()) {
			pf.put(e.getValue(), e.getKey());
		}
		
		MetadataDocument metaDoc = parseDocument(d, ns, pf);
		
		List<String> uuids = metaDoc.getStrings(DatasetPath.UUID.path());
		List<String> titles = metaDoc.getStrings(DatasetPath.TITLE.path());
		List<String> dates = metaDoc.getStrings(DatasetPath.DATE.path());
		List<String> creators = metaDoc.getStrings(DatasetPath.CREATOR.path());
		List<String> abstracts = metaDoc.getStrings(DatasetPath.ABSTRACT.path());
		List<String> thumbnails = metaDoc.getStrings(DatasetPath.THUMBNAIL.path());
		
		List<String> subjects = metaDoc.getStrings(DatasetPath.SUBJECT.path());
		
		List<String> altTitles = metaDoc.getStrings(DatasetPath.ALT_TITLE.path());
		List<String> mdIds = metaDoc.getStrings(DatasetPath.MD_ID.path());
		List<String> dataIds = metaDoc.getStrings(DatasetPath.DATA_ID.path());
		List<String> keywords = metaDoc.getStrings(DatasetPath.KEYWORD.path());
		List<String> orgContacts = metaDoc.getStrings(DatasetPath.ORGANISATION_CONTACT.path());
		List<String> orgNames = metaDoc.getStrings(DatasetPath.ORGANISATION_NAME.path());
		List<String> distribNames = metaDoc.getStrings(DatasetPath.DISTRIBUTOR_NAME.path());
		List<String> geoAreas = metaDoc.getStrings(DatasetPath.GEO_AREA.path());
		
		LocalDate finalLD = null;
		for(String date : dates) {
			if(finalLD == null) {
				finalLD = LocalDate.parse(date);
			} else {
				LocalDate tempLD = LocalDate.parse(date);
				if(tempLD.isAfter(finalLD)) {
					finalLD = LocalDate.parse(date);
				}
			}
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
	
	public static class MetadataDocument {
		private final Document d;
		private final XPath xp;
		
		MetadataDocument(Document d, XPath xp) {
			this.d = d;
			this.xp = xp;
		}
		
		List<String> getStrings(String path) throws Exception {
			if(!path.endsWith("text()")) {
				throw new RuntimeException("path should end with text()");
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
