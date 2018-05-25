package nl.idgis.portal.harvester;

import static java.util.concurrent.TimeUnit.MINUTES;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import javax.xml.parsers.DocumentBuilder;

import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.property.DavPropertySet;
import org.apache.jackrabbit.webdav.xml.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.w3c.dom.Document;

import com.querydsl.sql.SQLQueryFactory;

import nl.idgis.portal.harvester.data.HarvestResponses;
import nl.idgis.portal.harvester.types.DublinCore;
import nl.idgis.portal.harvester.types.Dataset;
import nl.idgis.portal.harvester.types.Service;
import nl.idgis.portal.harvester.util.Config;
import nl.idgis.portal.harvester.util.Database;
import nl.idgis.portal.harvester.util.GetDocumentBuilder;
import nl.idgis.portal.harvester.util.MetadataType;
import nl.idgis.portal.harvester.util.WebDavBooleanParser;

public class App {
	private static final Logger LOGGER = LoggerFactory.getLogger(App.class);
	
	private static final String HARVEST_INTERVAL = System.getenv("HARVEST_INTERVAL");
	private static final String TRUSTED_HEADER = System.getenv("TRUSTED_HEADER");
	
	private static final String DATASET_KEY = "dataset";
	private static final String SERVICE_KEY = "service";
	private static final String DUBLINCORE_KEY = "dc";
	
	private static final String DATASET_URL = System.getenv("DATASET_URL");
	private static final String SERVICE_URL = System.getenv("SERVICE_URL");
	private static final String DUBLINCORE_URL = System.getenv("DUBLINCORE_URL");
	
	public static void main(String[] args) throws Exception {
		final Runnable harvest = () -> {
			try {
				doHarvest();
			} catch(Exception e) {
				e.printStackTrace();
			}
		};
		
		final ScheduledFuture<?> harvestHandle =
				Executors.newScheduledThreadPool(1)
					.scheduleAtFixedRate(harvest, 0, Integer.parseInt(HARVEST_INTERVAL), MINUTES);
	}
	
	public static void doHarvest() throws Exception {
		LOGGER.info("harvesting has started");
		
		Config config = new Config();
		SQLQueryFactory qf = config.getQueryFactory();
		DriverManagerDataSource dataSource = config.getDataSource();
		
		config.getTransaction().execute((status) -> {
			try {
				// Populate database
				LOGGER.info("harvesting of datasets has started");
				executeHarvest(qf, DATASET_URL, DATASET_KEY, MetadataType.DATASET);
				
				LOGGER.info("harvesting of services has started");
				executeHarvest(qf, SERVICE_URL, SERVICE_KEY, MetadataType.SERVICE);
				
				LOGGER.info("harvesting of dublin core has started");
				executeHarvest(qf, DUBLINCORE_URL, DUBLINCORE_KEY, MetadataType.DUBLINCORE);
				
				// Refresh materialized view
				LOGGER.info("refreshing materialized view");
				Database.refreshMaterializedView(dataSource);
				
				return true;
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
		});
		
		LOGGER.info("harvesting is done");
	}
	
	public static void executeHarvest(SQLQueryFactory qf, String url, String dataKey, MetadataType metadataType) throws Exception {
		
		HarvestResponses harvestResponse = new HarvestResponses(url);
		MultiStatusResponse[] responses = harvestResponse.getResponses();
		DocumentBuilder db = GetDocumentBuilder.getDocumentBuilder();
		
		// Delete values from previous harvest
		Database.removeDocumentsFromType(qf, url);
		Database.insertType(qf, url, dataKey);
		
		for (MultiStatusResponse response : responses) {
			String href = response.getHref();
			
			if(href != null && href.endsWith(".xml")) {
				String filename = href.substring(href.lastIndexOf("/") + 1);
				LOGGER.info("name of metadata file: " + filename);
				
				HttpURLConnection connection = (HttpURLConnection)new URL(url + filename).openConnection();
				if(TRUSTED_HEADER != null) {
					connection.setRequestProperty(TRUSTED_HEADER, "1");
				}
				
				DavPropertySet properties = response.getProperties(200);
				DavProperty<?> confidential = 
						properties.get("confidential", Namespace.getNamespace("http://idgis.nl/geopublisher"));
				DavProperty<?> published = 
						properties.get("published", Namespace.getNamespace("http://idgis.nl/geopublisher"));
				
				try(InputStream input = connection.getInputStream()) {
					Document doc = db.parse(input);
					switch(metadataType) {
						case DATASET:
							Dataset.harvest(qf,
									doc,
									url,
									WebDavBooleanParser.parse(confidential),
									WebDavBooleanParser.parse(published));
							break;
						case SERVICE:
							Service.harvest(qf, doc, url, WebDavBooleanParser.parse(confidential));
							break;
						case DUBLINCORE:
							DublinCore.harvest(qf, doc, url);
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		// Insert harvest session information
		Database.insertHarvestSession(qf, dataKey);
	}
}
