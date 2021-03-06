package nl.idgis.portal.harvester.types;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;

import com.querydsl.sql.SQLQueryFactory;

import nl.idgis.portal.harvester.paths.ServicePath;
import nl.idgis.portal.harvester.util.Database;
import nl.idgis.portal.harvester.util.DateConverter;
import nl.idgis.portal.harvester.util.GetMetadataDocument;
import nl.idgis.portal.harvester.util.MetadataDocument;

public class Service {
	private static final String VIEWER_URL_SERVICE_PUBLIC_PREFIX = System.getenv("VIEWER_URL_SERVICE_PUBLIC_PREFIX");
	private static final String VIEWER_URL_SERVICE_SECURE_PREFIX = System.getenv("VIEWER_URL_SERVICE_SECURE_PREFIX");
	private static final String VIEWER_URL_SERVICE_WMSONLY_PREFIX = System.getenv("VIEWER_URL_SERVICE_WMSONLY_PREFIX");
	
	public static void harvest(SQLQueryFactory qf, Document d, String url, boolean confidential) {
		// construct namespace context
		Map<String, String> ns = new HashMap<>();
		ns.put("gmd", "http://www.isotc211.org/2005/gmd");
		ns.put("gco", "http://www.isotc211.org/2005/gco");
		ns.put("srv", "http://www.isotc211.org/2005/srv");
		
		Map<String, String> pf = new HashMap<>();
		for(Map.Entry<String, String> e : ns.entrySet()) {
			pf.put(e.getValue(), e.getKey());
		}
		
		MetadataDocument doc = GetMetadataDocument.parse(d, ns, pf);
		
		try {
			Integer accessId = Database.getAccessId(qf, "intern");
			if(confidential == false) {
				accessId = Database.getAccessId(qf, "extern");
			}
			
			String viewerUrl = null;
			for(String item : doc.getStrings(ServicePath.VIEWER_URL.path())) {
				if(item.startsWith(VIEWER_URL_SERVICE_PUBLIC_PREFIX) ||
						item.startsWith(VIEWER_URL_SERVICE_SECURE_PREFIX) ||
						item.startsWith(VIEWER_URL_SERVICE_WMSONLY_PREFIX)) {
					viewerUrl = item;
					break;
				}
			}
			
			Database.insertService(qf, 
					doc.getString(ServicePath.UUID.path()),
					Database.getType(qf, url),
					doc.getString(ServicePath.TITLE.path()),
					DateConverter.dateStringToTimestamp(doc.getString(ServicePath.DATE_PUBLISHED.path())),
					DateConverter.dateStringToTimestamp(doc.getString(ServicePath.DATE_DESCRIPTION_REVISION.path())),
					doc.getString(ServicePath.ORGANISATION_CREATOR.path()),
					doc.getString(ServicePath.ABSTRACT.path()),
					null,
					accessId,
					doc.getString(ServicePath.TYPE_SERVICE.path()),
					viewerUrl);
			
			Long documentId =  Database.getDocumentId(qf, doc.getString(ServicePath.UUID.path()));
			
			Database.insertAnyText(qf, documentId, doc.getStrings(ServicePath.TITLE.path()));
			Database.insertAnyText(qf, documentId, doc.getStrings(ServicePath.ABSTRACT.path()));
			Database.insertAnyText(qf, documentId, Arrays.asList(viewerUrl));
			Database.insertAnyText(qf, documentId, doc.getStrings(ServicePath.LAYER.path()));
			Database.insertAnyText(qf, documentId, doc.getStrings(ServicePath.USE_LIMITATION.path()));
			
			Database.insertAnyText(qf, documentId, doc.getStrings(ServicePath.INDIVIDUAL_NAME_CONTACT.path()));
			Database.insertAnyText(qf, documentId, doc.getStrings(ServicePath.ORGANISATION_CONTACT.path()));
			Database.insertAnyText(qf, documentId, doc.getStrings(ServicePath.INDIVIDUAL_NAME_CREATOR.path()));
			Database.insertAnyText(qf, documentId, doc.getStrings(ServicePath.ORGANISATION_CREATOR.path()));
			
			Database.insertAnyText(qf, documentId, doc.getStrings(ServicePath.GEO_AREA.path()));
			
			Database.insertAnyText(qf, documentId, doc.getStrings(ServicePath.ATTACHED_FILE.path()));
			Database.insertAnyText(qf, documentId, doc.getStrings(ServicePath.KEYWORD.path()));
			Database.insertAnyText(qf, documentId, doc.getStrings(ServicePath.UUID.path()));
			Database.insertAnyText(qf, documentId, doc.getStrings(ServicePath.ACCESS_CONSTRAINT.path()));
			Database.insertAnyText(qf, documentId, doc.getStrings(ServicePath.OTHER_CONSTRAINT.path()));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
