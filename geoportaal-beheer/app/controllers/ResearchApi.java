package controllers;

import static models.QCreator.creator;
import static models.QCreatorLabel.creatorLabel;
import static models.QMdAttachment.mdAttachment;
import static models.QMdTheme.mdTheme;
import static models.QMetadata.metadata;
import static models.QMetadataSearch.metadataSearch;
import static models.QRights.rights;
import static models.QRightsLabel.rightsLabel;
import static models.QStatus.status;
import static models.QTheme.theme;
import static models.QThemeLabel.themeLabel;
import static models.QTypeInformation.typeInformation;
import static models.QTypeInformationLabel.typeInformationLabel;
import static models.QTypeResearch.typeResearch;
import static models.QTypeResearchLabel.typeResearchLabel;
import static models.QUseLimitation.useLimitation;
import static models.QUseLimitationLabel.useLimitationLabel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.querydsl.core.Tuple;
import com.querydsl.sql.SQLQuery;

import play.i18n.Messages;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import util.QueryDSL;

/**
 * The class to query report data
 * 
 * @author Kevin
 *
 */
public class ResearchApi extends Controller {
	
	@Inject
	private QueryDSL q;
	
	/**
	 * Returns a {@link JSON} Object with all research data
	 * 
	 * @param textSearch - the search value of the text field
	 * @param offset - The offset to start the search
	 * @param limit - The number of results to return
	 * @param sort - the sort type value
	 * @param typeFilter - The type of research to filter
	 * @Param themeFilter - The theme to filter
	 * @return a {@link JSON} Object with the research data
	 */
	public Result search(String textSearch, long offset, long limit, String sort, String typeFilter, String themeFilter) {
		return q.withTransaction(tx -> {
			Map<String, Object> root = new HashMap<>();
			
			SQLQuery<Tuple> datasetQuery = tx.select(metadata.id, metadata.uuid, metadata.title, metadata.description, creatorLabel.label,
					rightsLabel.label, typeInformationLabel.label, typeResearchLabel.label, useLimitationLabel.label, metadata.dateSourcePublication)
				.from(metadata)
				.join(typeInformation).on(typeInformation.id.eq(metadata.typeInformation))
				.join(typeInformationLabel).on(typeInformationLabel.typeInformationId.eq(typeInformation.id))
				.join(typeResearch).on(typeResearch.id.eq(metadata.typeResearch))
				.join(typeResearchLabel).on(typeResearchLabel.typeResearchId.eq(typeResearch.id))
				.join(creator).on(creator.id.eq(metadata.creator))
				.join(creatorLabel).on(creatorLabel.creatorId.eq(creator.id))
				.join(useLimitation).on(useLimitation.id.eq(metadata.useLimitation))
				.join(useLimitationLabel).on(useLimitationLabel.useLimitationId.eq(useLimitation.id))
				.join(rights).on(rights.id.eq(metadata.rights))
				.join(rightsLabel).on(rightsLabel.rightsId.eq(rights.id))
				.join(status).on(status.id.eq(metadata.status))
				.where(useLimitation.name.equalsIgnoreCase("extern")
					.and(status.name.equalsIgnoreCase("published"))
					.and(typeResearch.name.notEqualsIgnoreCase("none")));
			
			// Strip characters from text search string that conflict with Postgres full-text search
			String textSearchFirstStrip = textSearch.replace("&", "");
			String textSearchSecondStrip = textSearchFirstStrip.replace("(", "");
			String textSearchThirdStrip = textSearchSecondStrip.replace(")", "");
			String textSearchFinalStrip = textSearchThirdStrip.replace(":", "");
			
			// Convert text search string to an array
			String[] textSearchTerms = textSearchFinalStrip.split("\\s+");
			
			// Convert array of text search words to list
			// Create a string of all the words in the text search list with a '&' between them
			String tsQuery = Arrays.asList(textSearchTerms).stream()
				.filter(word -> !word.isEmpty())
				.map(word -> word + ":*")
				.collect(Collectors.joining(" & "));
			
			// Filter records on text search words
			if (!tsQuery.isEmpty()) {
				// Get local language for query
				String language = Messages.get("tsv.language");
				
				datasetQuery.where(
					tx.selectOne()
						.from(metadataSearch)
						.where(metadataSearch.metadataId.eq(metadata.id))
						.where(metadataSearch.tsv.query(language, tsQuery))
						.exists());
			}
			
			// Filter on type of research
			if (!typeFilter.isEmpty()) {
				datasetQuery.where(
					tx.selectOne()
						.from(typeResearch)
						.join(metadata).on(metadata.typeResearch.eq(typeResearch.id))
						.where(typeResearch.name.equalsIgnoreCase(typeFilter))
						.exists());
			}
			
			// Filter on theme
			if (!themeFilter.isEmpty()) {
				datasetQuery.where(
					tx.selectOne()
						.from(theme)
						.join(mdTheme).on(mdTheme.theme.eq(theme.id))
						.where(mdTheme.metadataId.eq(metadata.id))
						.where(theme.name.equalsIgnoreCase(themeFilter))
						.exists());
			}
			
			if ("dateAsc".equals(sort)) {
				datasetQuery.orderBy(metadata.dateSourcePublication.asc());
			}
			if ("dateDesc".equals(sort)) {
				datasetQuery.orderBy(metadata.dateSourcePublication.desc());
			}
			if ("titleAsc".equals(sort)) {
				datasetQuery.orderBy(metadata.title.asc());
			}
			if ("titleDesc".equals(sort)) {
				datasetQuery.orderBy(metadata.title.desc());
			}
			
			root.put("count", datasetQuery.fetchCount());
			
			List<Map<String, Object>> result = new ArrayList<>();
			List<Tuple> md = datasetQuery.offset(offset).limit(limit).fetch();
			for (Tuple mdRow : md) {
				Map<String, Object> record = new HashMap<>();
				record.put("uuid", mdRow.get(metadata.uuid));
				record.put("titel", mdRow.get(metadata.title));
				record.put("omschrijving", mdRow.get(metadata.description));
				record.put("eindverantwoordelijke", mdRow.get(creatorLabel.label));
				record.put("eigendomsrechten", mdRow.get(rightsLabel.label));
				record.put("typeInformatie", mdRow.get(typeInformationLabel.label));
				record.put("typeOnderzoek", mdRow.get(typeResearchLabel.label));
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
					attachmentsList.add(att);
				}
				record.put("bijlagen", attachmentsList);
				
				// Themas
				List<String> themes = tx.select(themeLabel.label)
					.from(mdTheme)
					.join(theme).on(theme.id.eq(mdTheme.theme))
					.join(themeLabel).on(themeLabel.themeId.eq(theme.id))
					.where(mdTheme.metadataId.eq(mdRow.get(metadata.id)))
					.fetch();
				record.put("themas", themes);
				
				result.add(record);
			}
			root.put("records", result);
			
			return ok(Json.toJson(root));
		});
	}
	
	/**
	 * Returns a {@link JSON} Object with the research data
	 * 
	 * @param metadataUuid the UUID of the metadata
	 * @return a {@link JSON} Object with the research data
	 */
	public Result findResearch(String metadataUuid) {
		return q.withTransaction(tx -> {
			SQLQuery<Tuple> datasetQuery = tx.select(metadata.id, metadata.uuid, metadata.title, metadata.description, creatorLabel.label,
					rightsLabel.label, typeInformationLabel.label, typeResearchLabel.label, useLimitationLabel.label, metadata.dateSourcePublication)
				.from(metadata)
				.join(typeInformation).on(typeInformation.id.eq(metadata.typeInformation))
				.join(typeInformationLabel).on(typeInformationLabel.typeInformationId.eq(typeInformation.id))
				.join(typeResearch).on(typeResearch.id.eq(metadata.typeResearch))
				.join(typeResearchLabel).on(typeResearchLabel.typeResearchId.eq(typeResearch.id))
				.join(creator).on(creator.id.eq(metadata.creator))
				.join(creatorLabel).on(creatorLabel.creatorId.eq(creator.id))
				.join(useLimitation).on(useLimitation.id.eq(metadata.useLimitation))
				.join(useLimitationLabel).on(useLimitationLabel.useLimitationId.eq(useLimitation.id))
				.join(rights).on(rights.id.eq(metadata.rights))
				.join(rightsLabel).on(rightsLabel.rightsId.eq(rights.id))
				.join(status).on(status.id.eq(metadata.status))
				.where(useLimitation.name.equalsIgnoreCase("extern")
					.and(status.name.equalsIgnoreCase("published"))
					.and(typeResearch.name.notEqualsIgnoreCase("none"))
					.and(metadata.uuid.eq(metadataUuid)));
			
			Map<String, Object> result = new HashMap<>();
			
			Optional<Tuple> mdResult = Optional.ofNullable(datasetQuery.fetchOne());
			mdResult.ifPresent(record -> {
				result.put("uuid", record.get(metadata.uuid));
				result.put("titel", record.get(metadata.title));
				result.put("omschrijving", record.get(metadata.description));
				result.put("eindverantwoordelijke", record.get(creatorLabel.label));
				result.put("eigendomsrechten", record.get(rightsLabel.label));
				result.put("typeInformatie", record.get(typeInformationLabel.label));
				result.put("typeOnderzoek", record.get(typeResearchLabel.label));
				result.put("gebruiksrestricties", record.get(useLimitationLabel.label));
				result.put("datumPublicatie", record.get(metadata.dateSourcePublication).toLocalDateTime().toString());
				
				// Bijlagen
				List<Map<String, Object>> attachmentsList = tx.select(mdAttachment.id, mdAttachment.attachmentName)
					.from(mdAttachment)
					.where(mdAttachment.metadataId.eq(record.get(metadata.id)))
					.fetch()
					.stream()
					.map(row -> {
						Map<String, Object> attachment = new HashMap<>();
						attachment.put("naam", row.get(mdAttachment.attachmentName));
						return attachment;
					})
					.collect(Collectors.toList());
				result.put("bijlagen", attachmentsList);
				
				// Themas
				List<String> themes = tx.select(themeLabel.label)
					.from(mdTheme)
					.join(theme).on(theme.id.eq(mdTheme.theme))
					.join(themeLabel).on(themeLabel.themeId.eq(theme.id))
					.where(mdTheme.metadataId.eq(record.get(metadata.id)))
					.fetch();
				result.put("themas", themes);
			});
			
			return ok(Json.toJson(result));
		});
	}

	/**
	 * Returns a {@link JSON} array with all available types of researches
	 * 
	 * @return a {@link JSON} array with all available types of researches 
	 */
	public Result getTypesResearch() {
		return q.withTransaction(tx -> {
			List<Map<String, Object>> result = tx.select(typeResearch.name, typeResearchLabel.label)
				.from(typeResearch)
				.join(typeResearchLabel).on(typeResearchLabel.typeResearchId.eq(typeResearch.id))
				.where(typeResearch.name.notEqualsIgnoreCase("none"))
				.orderBy(typeResearchLabel.label.asc())
				.fetch()
				.stream()
				.map(row -> {
					Map<String, Object> record = new HashMap<>();
					record.put("id", row.get(typeResearch.name));
					record.put("label", row.get(typeResearchLabel.label));
					return record;
				})
				.collect(Collectors.toList());
			
			return ok(Json.toJson(result));
		});
	}
	
	/**
	 * Returns a {@link JSON} array with all available themes
	 * 
	 * @return a {@link JSON} array with all available themes
	 */
	public Result getThemes() {
		return q.withTransaction(tx -> {
			List<Map<String, Object>> result = tx.select(theme.name, themeLabel.label)
				.from(theme)
				.join(themeLabel).on(themeLabel.themeId.eq(theme.id))
				.orderBy(themeLabel.label.asc())
				.fetch()
				.stream()
				.map(row -> {
					Map<String, Object> record = new HashMap<>();
					record.put("id", row.get(theme.name));
					record.put("label", row.get(themeLabel.label));
					return record;
				})
				.collect(Collectors.toList());
			
			return ok(Json.toJson(result));
		});
	}
}
