package nl.idgis.portal.harvester;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

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
import nl.idgis.portal.harvester.types.Dataset;
import nl.idgis.portal.harvester.types.DublinCore;
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
		
		LOGGER.info("harvesting has been delayed with " + getInitialDelay() + " seconds");
		
		final ScheduledFuture<?> harvestHandle =
				Executors.newScheduledThreadPool(1)
					.scheduleAtFixedRate(
						harvest, 
						getInitialDelay(), 
						Integer.parseInt(HARVEST_INTERVAL) * 60, 
						TimeUnit.SECONDS
					);
	}
	
	private static long getInitialDelay() {
		ZonedDateTime zonedNow = ZonedDateTime.of(LocalDateTime.now(), ZoneId.of("Europe/Amsterdam"));
		
		int nowMinute = zonedNow.getMinute();
		
		ZonedDateTime zonedNext;
		if(nowMinute < 15 || nowMinute >= 45) zonedNext = zonedNow.withMinute(15).withSecond(0);
		else zonedNext = zonedNow.withMinute(45).withSecond(0);
		
		if(zonedNow.compareTo(zonedNext) > 0) zonedNext = zonedNext.plusHours(1);
		return Duration.between(zonedNow, zonedNext).getSeconds();
	}
	
	public static void doHarvest() throws Exception {
		LOGGER.info("harvesting has started");
		LocalDateTime start = LocalDateTime.now();
		
		Config config = new Config();
		SQLQueryFactory qf = config.getQueryFactory();
		DriverManagerDataSource dataSource = config.getDataSource();
		
		config.getTransaction().execute((status) -> {
			try {
				// Populate database
				
				LOGGER.info("harvesting of datasets has started");
				LocalDateTime beforeDataset = LocalDateTime.now();
				int lengthDatasets = executeHarvest(qf, DATASET_URL, DATASET_KEY, MetadataType.DATASET);
				
				LOGGER.info("harvesting of services has started");
				LocalDateTime beforeService = LocalDateTime.now();
				int lengthServices = executeHarvest(qf, SERVICE_URL, SERVICE_KEY, MetadataType.SERVICE);
				
				LOGGER.info("harvesting of dublin core has started");
				LocalDateTime beforeDc = LocalDateTime.now();
				int lengthDcs = executeHarvest(qf, DUBLINCORE_URL, DUBLINCORE_KEY, MetadataType.DUBLINCORE);
				
				// Refresh materialized view
				LOGGER.info("refreshing materialized view");
				LocalDateTime beforeRefreshView = LocalDateTime.now();
				Database.refreshMaterializedView(dataSource);
				LocalDateTime afterRefreshView = LocalDateTime.now();
				
				long durationBeforeBeginHarvesting = ChronoUnit.MILLIS.between(start, beforeDataset);
				long durationDataset = ChronoUnit.MILLIS.between(beforeDataset, beforeService);
				long durationService = ChronoUnit.MILLIS.between(beforeService, beforeDc);
				long durationDc = ChronoUnit.MILLIS.between(beforeDc, beforeRefreshView);
				long durationRefreshView = ChronoUnit.MILLIS.between(beforeRefreshView, afterRefreshView);
				
				LOGGER.info("milli seconds between start and start datasets: " + durationBeforeBeginHarvesting);
				
				LOGGER.info("milli seconds duration of harvesting of datasets: " + durationDataset);
				LOGGER.info("length of datasets: " + lengthDatasets);
				if(lengthDatasets > 0) LOGGER.info("average milli seconds duration of dataset: " + (durationDataset / lengthDatasets));
				
				LOGGER.info("milli seconds duration of harvesting of services: " + durationService);
				LOGGER.info("length of services: " + lengthServices);
				if(lengthServices > 0) LOGGER.info("average milli seconds duration of service: " + (durationService / lengthServices));
				
				LOGGER.info("milli seconds duration of harvesting of dcs: " + durationDc);
				LOGGER.info("length of dcs: " + lengthDcs);
				if(lengthDcs > 0) LOGGER.info("average milli seconds duration of dc: " + (durationDc / lengthDcs));
				
				LOGGER.info("milli seconds between start refresh view and end refresh view: " + durationRefreshView);
				
				return true;
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
		});
		
		LocalDateTime end = LocalDateTime.now();
		long durationTotal = ChronoUnit.MILLIS.between(start, end);
		LOGGER.info("milli seconds of harvesting: " + durationTotal);
		
		LOGGER.info("harvesting is done");
	}
	
	public static int executeHarvest(SQLQueryFactory qf, String url, String dataKey, MetadataType metadataType) throws Exception {
		
		HarvestResponses harvestResponse = new HarvestResponses(url);
		List<MultiStatusResponse> responses = Arrays.asList(harvestResponse.getResponses());
		DocumentBuilder db = GetDocumentBuilder.getDocumentBuilder();
		
		// Delete values from previous harvest
		Database.removeDocumentsFromType(qf, dataKey);
		Database.insertType(qf, url, dataKey);
		
		responses
			.stream()
			.forEach(response -> {
				String href = response.getHref();
				
				if(href != null && href.endsWith(".xml")) {
					String filename = href.substring(href.lastIndexOf("/") + 1);
					LOGGER.info("name of metadata file: " + filename);
					
					HttpURLConnection connection;
					try {
						connection = (HttpURLConnection) new URL(url + filename).openConnection();
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
									Dataset.harvest(
											qf,
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
									break;
							}
						}
					} catch (MalformedURLException mue) {
						mue.printStackTrace();
					} catch (IOException ioe) {
						ioe.printStackTrace();
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			});
		
		// Insert harvest session information
		Database.insertHarvestSession(qf, dataKey);
		
		return (responses.size() -1);
	}
}
