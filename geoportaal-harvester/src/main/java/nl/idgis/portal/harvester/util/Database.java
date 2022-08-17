package nl.idgis.portal.harvester.util;

import static models.QAccess.access;
import static models.QAnyText.anyText;
import static models.QDocSubject.docSubject;
import static models.QDocument.document;
import static models.QDocumentSearch.documentSearch;
import static models.QHarvestSession.harvestSession;
import static models.QMdType.mdType;
import static models.QSubject.subject;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.querydsl.sql.SQLQueryFactory;

public class Database {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Database.class);
	
	public static int getAccessId(SQLQueryFactory qf, String type) {
		return qf.select(access.id)
			.from(access)
			.where(access.name.eq(type))
			.fetchOne();
	}
	
	public static Long getDocumentId(SQLQueryFactory qf, String uuid) {
		return qf.select(document.id)
			.from(document)
			.where(document.uuid.eq(uuid))
			.fetchOne();
	}
	
	public static void removeDocumentsFromType(SQLQueryFactory qf, String dataKey) {
		qf.delete(mdType)
			.where(mdType.name.eq(dataKey))
			.execute();
	}
	
	public static void insertType(SQLQueryFactory qf, String url, String dataKey) {
		qf.insert(mdType)
			.set(mdType.url, url)
			.set(mdType.name, dataKey)
			.execute();
	}
	
	public static int getType(SQLQueryFactory qf, String url) {
		return qf.select(mdType.id)
			.from(mdType)
			.where(mdType.url.eq(url))
			.fetchOne();
	}
	
	public static Integer getInternCount(SQLQueryFactory qf, String dataKey) {
		return qf.select(document.uuid)
			.from(document)
			.join(mdType).on(mdType.id.eq(document.mdTypeId))
			.where(mdType.name.eq(dataKey)
				.and(document.archived.isNull().or(document.archived.isFalse())))
			.fetch()
			.size();
	}
	
	public static Integer getExternCount(SQLQueryFactory qf, String dataKey) {
		return qf.select(document.uuid)
			.from(document)
			.join(mdType).on(mdType.id.eq(document.mdTypeId))
			.join(access).on(access.id.eq(document.accessId))
			.where(mdType.name.eq(dataKey)
				.and(access.name.eq("extern"))
				.and(document.archived.isNull().or(document.archived.isFalse())))
			.fetch()
			.size();
	}
	
	public static Integer getDatasetArchivedCount(SQLQueryFactory qf, String dataKey) {
		if(!"dataset".equals(dataKey)) return null;
		
		return qf.select(document.uuid)
			.from(document)
			.join(mdType).on(mdType.id.eq(document.mdTypeId))
			.where(mdType.name.eq("dataset")
				.and(document.archived.isTrue()))
			.fetch()
			.size();
	}
	
	public static void insertHarvestSession(SQLQueryFactory qf, String dataKey) {
		qf.insert(harvestSession)
			.set(harvestSession.type, dataKey)
			.set(harvestSession.internCount, Database.getInternCount(qf, dataKey))
			.set(harvestSession.externCount, Database.getExternCount(qf, dataKey))
			.set(harvestSession.archivedCount, Database.getDatasetArchivedCount(qf, dataKey))
			.set(harvestSession.createTime, Timestamp.valueOf(LocalDateTime.now()))
			.execute();
	}
	
	public static void insertDocumentSubjects(SQLQueryFactory qf, List<String> list, Long documentId) {
		for(String subjectItem : list) {
			String subjectItemName = null;
			
			if(subjectItem.startsWith("subject: ")) {
				subjectItemName = subjectItem.substring("subject: ".length());
			} else if(subjectItem.startsWith("theme: ")) {
				// do nothing
			} else {
				subjectItemName = subjectItem;
			}
			
			if(subjectItemName != null) {
				Integer subjectId = qf.select(subject.id)
						.from(subject)
						.where(subject.name.eq(subjectItemName))
						.fetchOne();
				
				if(subjectId != null) {
					qf.insert(docSubject)
						.set(docSubject.documentId, documentId)
						.set(docSubject.subjectId, subjectId)
						.execute();
				} else {
					LOGGER.warn("subject id for subject item {} is null", subjectItem);
				}
			}
		}
	}
	
	public static void insertDataset(SQLQueryFactory qf,
			String uuid,
			Integer mdTypeId,
			String title,
			Timestamp dateDataset,
			Timestamp dateDescription,
			String creator,
			String description,
			String thumbnail,
			Integer accessId,
			boolean downloadable,
			String spatialSchema,
			boolean published,
			String viewerUrl,
			boolean wmsOnly,
			boolean archived,
			String maintenanceFrequency) {
		qf.insert(document)
			.set(document.uuid, uuid)
			.set(document.mdTypeId, mdTypeId)
			.set(document.title, title)
			.set(document.dateDataset, dateDataset)
			.set(document.dateDescription, dateDescription)
			.set(document.creator, creator)
			.set(document.description, description)
			.set(document.thumbnail, thumbnail)
			.set(document.accessId, accessId)
			.set(document.downloadable, downloadable)
			.set(document.spatialSchema, spatialSchema)
			.set(document.published, published)
			.set(document.viewerUrl, viewerUrl)
			.set(document.wmsOnly, wmsOnly)
			.set(document.archived, archived)
			.set(document.maintenanceFrequency, maintenanceFrequency)
			.execute();
	}
	
	public static void insertService(SQLQueryFactory qf,
			String uuid,
			Integer mdTypeId,
			String title,
			Timestamp dateDataset,
			Timestamp dateDescription,
			String creator,
			String description,
			String thumbnail,
			Integer accessId,
			String typeService,
			String viewerUrl) {
		qf.insert(document)
			.set(document.uuid, uuid)
			.set(document.mdTypeId, mdTypeId)
			.set(document.title, title)
			.set(document.dateDataset, dateDataset)
			.set(document.dateDescription, dateDescription)
			.set(document.creator, creator)
			.set(document.description, description)
			.set(document.thumbnail, thumbnail)
			.set(document.accessId, accessId)
			.set(document.typeService, typeService)
			.set(document.viewerUrl, viewerUrl)
			.execute();
	}
	
	public static void insertDublinCore(SQLQueryFactory qf,
			String uuid,
			Integer mdTypeId,
			String title,
			Timestamp dateDataset,
			Timestamp dateDescription,
			String creator,
			String description,
			String thumbnail,
			Integer accessId) {
		qf.insert(document)
			.set(document.uuid, uuid)
			.set(document.mdTypeId, mdTypeId)
			.set(document.title, title)
			.set(document.dateDataset, dateDataset)
			.set(document.dateDescription, dateDescription)
			.set(document.creator, creator)
			.set(document.description, description)
			.set(document.thumbnail, thumbnail)
			.set(document.accessId, accessId)
			.execute();
	}
	
	public static void insertAnyText(SQLQueryFactory qf, Long documentId, List<String> listValues) {
		for(String value : listValues) {
			if(value != null && !value.trim().isEmpty()) {
				qf.insert(anyText)
					.set(anyText.documentId, documentId)
					.set(anyText.content, value.trim())
					.execute();
			}
		}
	}
	
	public static void refreshMaterializedView(DriverManagerDataSource dataSource) throws SQLException {
		String statement = "refresh materialized view concurrently \"" 
				+ documentSearch.getSchemaName() + "\".\"" 
				+ documentSearch.getTableName() + "\"";
		
		try(Statement stmt = DataSourceUtils.getConnection(dataSource).createStatement()) {
			stmt.execute(statement);
		}
	}
}
