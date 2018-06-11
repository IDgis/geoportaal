package nl.idgis.portal.harvester.types;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;

import com.querydsl.sql.SQLQueryFactory;

import nl.idgis.portal.harvester.paths.DublinCorePath;
import nl.idgis.portal.harvester.util.Database;
import nl.idgis.portal.harvester.util.DateConverter;
import nl.idgis.portal.harvester.util.GetMetadataDocument;
import nl.idgis.portal.harvester.util.MetadataDocument;

public class DublinCore {
	public static void harvest(SQLQueryFactory qf, Document d, String url) {
		// construct namespace context
		Map<String, String> ns = new HashMap<>();
		ns.put("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		ns.put("dc", "http://purl.org/dc/elements/1.1/");
		ns.put("dcterms", "http://purl.org/dc/terms/");
		
		Map<String, String> pf = new HashMap<>();
		for(Map.Entry<String, String> e : ns.entrySet()) {
			pf.put(e.getValue(), e.getKey());
		}
		
		MetadataDocument doc = GetMetadataDocument.parse(d, ns, pf);
		
		try {
			Integer accessId = Database.getAccessId(qf, "intern");
			if("De bron mag ook voor externe partijen vindbaar zijn".equals(doc.getString(DublinCorePath.USE_LIMITATION.path()))) {
				accessId = Database.getAccessId(qf, "extern");
			}
			
			Database.insertDublinCore(qf,
					doc.getString(DublinCorePath.UUID.path()),
					Database.getType(qf, url),
					doc.getString(DublinCorePath.TITLE.path()),
					DateConverter.dateStringToTimestamp(doc.getString(DublinCorePath.DATE_CREATED.path())),
					DateConverter.dateStringToTimestamp(doc.getString(DublinCorePath.DATE_PUBLISHED.path())),
					doc.getString(DublinCorePath.ORGANISATION_CREATOR.path()),
					doc.getString(DublinCorePath.ABSTRACT.path()),
					null,
					accessId);
			
			Long documentId =  Database.getDocumentId(qf, doc.getString(DublinCorePath.UUID.path()));
			
			Database.insertDocumentSubjects(qf, doc.getStrings(DublinCorePath.SUBJECT.path()), documentId);
			
			Database.insertAnyText(qf, documentId, doc.getStrings(DublinCorePath.LOCATION.path()));
			Database.insertAnyText(qf, documentId, doc.getStrings(DublinCorePath.NUMBER.path()));
			Database.insertAnyText(qf, documentId, doc.getStrings(DublinCorePath.TYPE.path()));
			Database.insertAnyText(qf, documentId, doc.getStrings(DublinCorePath.PUBLISHER.path()));
			Database.insertAnyText(qf, documentId, doc.getStrings(DublinCorePath.CONTRIBUTOR.path()));
			Database.insertAnyText(qf, documentId, doc.getStrings(DublinCorePath.RIGHTS.path()));
			Database.insertAnyText(qf, documentId, doc.getStrings(DublinCorePath.FORMAT.path()));
			Database.insertAnyText(qf, documentId, doc.getStrings(DublinCorePath.SOURCE.path()));
			Database.insertAnyText(qf, documentId, doc.getStrings(DublinCorePath.SUBJECT.path()));
			Database.insertAnyText(qf, documentId, doc.getStrings(DublinCorePath.LANGUAGE.path()));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
