package nl.idgis.portal.harvester.types;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;

import com.querydsl.sql.SQLQueryFactory;

import nl.idgis.portal.harvester.paths.DatasetPath;
import nl.idgis.portal.harvester.util.Database;
import nl.idgis.portal.harvester.util.DateConverter;
import nl.idgis.portal.harvester.util.GetMetadataDocument;
import nl.idgis.portal.harvester.util.MetadataDocument;

public class Dataset {
	
	private static final String VIEWER_URL_DATASET_PUBLIC_PREFIX = System.getenv("VIEWER_URL_DATASET_PUBLIC_PREFIX");
	private static final String VIEWER_URL_DATASET_SECURE_PREFIX = System.getenv("VIEWER_URL_DATASET_SECURE_PREFIX");
	private static final String VIEWER_URL_DATASET_WMSONLY_PREFIX = System.getenv("VIEWER_URL_DATASET_WMSONLY_PREFIX");
	private static final String URL_GEOSERVER_DATASET_PUBLIC_PREFIX = System.getenv("URL_GEOSERVER_DATASET_PUBLIC_PREFIX");
	
	public static void harvest(SQLQueryFactory qf, Document d, String url, boolean confidential, boolean published) {
		// construct namespace context
		Map<String, String> ns = new HashMap<>();
		ns.put("gmd", "http://www.isotc211.org/2005/gmd");
		ns.put("gco", "http://www.isotc211.org/2005/gco");
		
		Map<String, String> pf = new HashMap<>();
		for(Map.Entry<String, String> e : ns.entrySet()) {
			pf.put(e.getValue(), e.getKey());
		}
		
		MetadataDocument doc = GetMetadataDocument.parse(d, ns, pf);
		
		try {
			String thumbnail = doc.getString(DatasetPath.THUMBNAIL.path());
			if(thumbnail != null && thumbnail.trim().startsWith("http")) {
				thumbnail = thumbnail.trim().replaceAll("\\\\", "/");
			}
			
			Integer accessId = Database.getAccessId(qf, "intern");
			if(confidential == false) {
				accessId = Database.getAccessId(qf, "extern");
			}
			
			boolean downloadable = false;
			boolean wmsOnly = false;
			boolean archived = false;
			for(String ul : doc.getStrings(DatasetPath.USE_LIMITATION.path())) {
				if(ul == null) continue;
				
				if("Downloadable data".equals(ul.trim())) {
					for(String resource : doc.getStrings(DatasetPath.ONLINE_SOURCE.path())) {
						if(resource.startsWith(URL_GEOSERVER_DATASET_PUBLIC_PREFIX)) {
							downloadable = true;
							break;
						}
					}
				}
				
				if("Alleen WMS extern".equals(ul.trim())) {
					wmsOnly = true;
				}
				
				if("Alleen beschikbaar in historisch archief".equals(ul.trim())) {
					archived = true;
				}
			}
			
			String viewerUrl = null;
			for(String resource : doc.getStrings(DatasetPath.ONLINE_SOURCE.path())) {
				if(resource.startsWith(VIEWER_URL_DATASET_PUBLIC_PREFIX) ||
						resource.startsWith(VIEWER_URL_DATASET_SECURE_PREFIX) ||
						resource.startsWith(VIEWER_URL_DATASET_WMSONLY_PREFIX)) {
					viewerUrl = resource;
					break;
				}
			}
			
			Database.insertDataset(qf, 
					doc.getString(DatasetPath.UUID.path()),
					Database.getType(qf, url),
					doc.getString(DatasetPath.TITLE.path()),
					DateConverter.dateStringToTimestamp(doc.getString(DatasetPath.DATE_FILE.path())),
					DateConverter.dateStringToTimestamp(doc.getString(DatasetPath.DATE_DESCRIPTION_REVISION.path())),
					doc.getString(DatasetPath.ORGANISATION_CREATOR.path()),
					doc.getString(DatasetPath.ABSTRACT.path()),
					thumbnail,
					accessId,
					downloadable,
					doc.getString(DatasetPath.SPATIAL_SCHEMA.path()),
					published,
					viewerUrl,
					wmsOnly,
					archived);
			
			Long documentId =  Database.getDocumentId(qf, doc.getString(DatasetPath.UUID.path()));
			
			Database.insertDocumentSubjects(qf, doc.getStrings(DatasetPath.SUBJECT.path()), documentId);
			
			Database.insertAnyText(qf, documentId, doc.getStrings(DatasetPath.INDIVIDUAL_NAME_CREATOR.path()));
			Database.insertAnyText(qf, documentId, doc.getStrings(DatasetPath.ALT_TITLE.path()));
			Database.insertAnyText(qf, documentId, doc.getStrings(DatasetPath.MD_ID.path()));
			Database.insertAnyText(qf, documentId, doc.getStrings(DatasetPath.DATA_ID.path()));
			Database.insertAnyText(qf, documentId, doc.getStrings(DatasetPath.KEYWORD.path()));
			Database.insertAnyText(qf, documentId, doc.getStrings(DatasetPath.INDIVIDUAL_NAME_CONTACT.path()));
			Database.insertAnyText(qf, documentId, doc.getStrings(DatasetPath.ORGANISATION_CONTACT.path()));
			Database.insertAnyText(qf, documentId, doc.getStrings(DatasetPath.INDIVIDUAL_NAME_DISTRIBUTOR.path()));
			Database.insertAnyText(qf, documentId, doc.getStrings(DatasetPath.ORGSANISATION_DISTRIBUTOR.path()));
			Database.insertAnyText(qf, documentId, doc.getStrings(DatasetPath.GEO_AREA.path()));
			Database.insertAnyText(qf, documentId, doc.getStrings(DatasetPath.PURPOSE.path()));
			Database.insertAnyText(qf, documentId, doc.getStrings(DatasetPath.USE_LIMITATION.path()));
			Database.insertAnyText(qf, documentId, doc.getStrings(DatasetPath.USE_CONSTRAINT.path()));
			Database.insertAnyText(qf, documentId, doc.getStrings(DatasetPath.ACCESS_CONSTRAINT.path()));
			Database.insertAnyText(qf, documentId, doc.getStrings(DatasetPath.DESCRIPTION_SOURCE.path()));
			Database.insertAnyText(qf, documentId, doc.getStrings(DatasetPath.POTENTIAL_USE.path()));
			Database.insertAnyText(qf, documentId, doc.getStrings(DatasetPath.OTHER_CONSTRAINT.path()));
			Database.insertAnyText(qf, documentId, doc.getStrings(DatasetPath.RELATED_DATASET.path()));
			Database.insertAnyText(qf, documentId, doc.getStrings(DatasetPath.SPATIAL_SCHEMA.path()));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
