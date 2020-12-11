package controllers;

import static models.QCreator.creator;
import static models.QCreatorLabel.creatorLabel;
import static models.QMdAttachment.mdAttachment;
import static models.QMdSubject.mdSubject;
import static models.QMetadata.metadata;
import static models.QRights.rights;
import static models.QRightsLabel.rightsLabel;
import static models.QStatus.status;
import static models.QSubject.subject;
import static models.QSubjectLabel.subjectLabel;
import static models.QTypeInformation.typeInformation;
import static models.QTypeInformationLabel.typeInformationLabel;
import static models.QUseLimitation.useLimitation;
import static models.QUseLimitationLabel.useLimitationLabel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.querydsl.core.Tuple;
import com.querydsl.sql.SQLQuery;

import actions.DefaultAuthenticator;
import play.Configuration;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import util.QueryDSL;

/**
 * The class to query report data
 * 
 * @author Kevin
 *
 */
@Security.Authenticated(DefaultAuthenticator.class)
public class ReportApi extends Controller {
	
	@Inject
	private QueryDSL q;
	
	private final Configuration configuration = play.Play.application().configuration();
	
	/**
	 * Returns a {@link JSON} Object with all report data
	 * 
	 * @param offset - The offset to start the search
	 * @param limit - The number of results to return
	 * @return a {@link JSON} Object with the report data
	 */
	public Result search(long offset, long limit) {
		return q.withTransaction(tx -> {
			Map<String, Object> root = new HashMap<>();
			
			SQLQuery<Tuple> datasetQuery = tx.select(metadata.id, metadata.uuid, metadata.title, metadata.description, creatorLabel.label,
					rightsLabel.label, typeInformationLabel.label, useLimitationLabel.label, metadata.dateSourcePublication)
				.from(metadata)
				.join(typeInformation).on(typeInformation.id.eq(metadata.typeInformation))
				.join(typeInformationLabel).on(typeInformationLabel.typeInformationId.eq(typeInformation.id))
				.join(creator).on(creator.id.eq(metadata.creator))
				.join(creatorLabel).on(creatorLabel.creatorId.eq(creator.id))
				.join(useLimitation).on(useLimitation.id.eq(metadata.useLimitation))
				.join(useLimitationLabel).on(useLimitationLabel.useLimitationId.eq(useLimitation.id))
				.join(rights).on(rights.id.eq(metadata.rights))
				.join(rightsLabel).on(rightsLabel.rightsId.eq(rights.id))
				.join(status).on(status.id.eq(metadata.status))
				.where(typeInformation.name.equalsIgnoreCase("report")
					.and(useLimitation.name.equalsIgnoreCase("extern"))
					.and(status.name.equalsIgnoreCase("published")));
			
			root.put("count", datasetQuery.fetchCount());
			
			List<Map<String, Object>> result = new ArrayList<>();
			List<Tuple> md = datasetQuery.offset(offset).limit(limit).fetch();
			for (Tuple mdRow : md) {
				Map<String, Object> record = new HashMap<>();
				record.put("titel", mdRow.get(metadata.title));
				record.put("omschrijving", mdRow.get(metadata.description));
				record.put("eindverantwoordelijke", mdRow.get(creatorLabel.label));
				record.put("eigendomsrechten", mdRow.get(rightsLabel.label));
				record.put("typeInformatie", mdRow.get(typeInformationLabel.label));
				record.put("gebruiksrestricties", mdRow.get(useLimitationLabel.label));
				record.put("datumPublicatie", mdRow.get(metadata.dateSourcePublication).toLocalDateTime().toString());
				
				// Bijlagen
				List<Map<String, Object>> attachmentsList = new ArrayList<>();
				List<Tuple> attachments = tx.select(mdAttachment.id, mdAttachment.attachmentName)
					.from(mdAttachment)
					.where(mdAttachment.metadataId.eq(mdRow.get(metadata.id)))
					.fetch();
				for (Tuple attRow : attachments) {
					Map<String, Object> att = new HashMap<>();
					att.put("naam", attRow.get(mdAttachment.attachmentName));
					att.put("url", configuration.getString("geoportaal.attachmentPrefixExternal") + mdRow.get(metadata.uuid) + "/" + attRow.get(mdAttachment.attachmentName));
					attachmentsList.add(att);
				}
				record.put("bijlagen", attachmentsList);
				
				// ISO Onderwerpen
				List<String> subjects = tx.select(subjectLabel.label)
					.from(mdSubject)
					.join(subject).on(subject.id.eq(mdSubject.subject))
					.join(subjectLabel).on(subjectLabel.subjectId.eq(subject.id))
					.where(mdSubject.metadataId.eq(mdRow.get(metadata.id)))
					.fetch();
				record.put("isoOnderwerpen", subjects);
				
				result.add(record);
			}
			root.put("records", result);
			
			return ok(Json.toJson(root));
		});
	}
}
