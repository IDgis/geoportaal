package controllers;

import static models.QCreator.creator;
import static models.QCreatorLabel.creatorLabel;
import static models.QMdAttachment.mdAttachment;
import static models.QMdTheme.mdTheme;
import static models.QMdWooTheme.mdWooTheme;
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
import static models.QWooTheme.wooTheme;
import static models.QWooThemeLabel.wooThemeLabel;

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

import enums.TypeApp;
import play.i18n.Messages;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import util.QueryDSL;
import util.QueryDSL.Transaction;

/**
 * The class to query report data
 * 
 * @author Kevin
 *
 */
public class DocumentApi extends Controller {
	
	@Inject
	private QueryDSL q;
	
	/**
	 * Returns a {@link JSON} Object with all document data
	 * 
	 * @param textSearch - the search value of the text field
	 * @param offset - The offset to start the search
	 * @param limit - The number of results to return
	 * @param sort - the sort type value
	 * @param typeFilter - The type of research to filter
	 * @param themeFilter - The theme to filter
	 * @param creationYear - The year of creation to filter
	 * @return a {@link JSON} Object with the document data
	 */
	public Result search(String typeApp, String textSearch, long offset, long limit, String sort, String typeFilter, String themeFilter, long creationYear) {
		TypeApp ta = getTypeApp(typeApp);
		if(ta == null) return notFound();
		
		return q.withTransaction(tx -> {
			Map<String, Object> root = new HashMap<>();
			
			SQLQuery<Tuple> datasetQuery = tx.select(metadata.id, metadata.uuid, metadata.title, metadata.description, creatorLabel.label,
					rightsLabel.label, typeInformationLabel.label, typeResearchLabel.label, useLimitationLabel.label, metadata.dateSourcePublication,
					metadata.dateSourceCreation)
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
					.and(status.name.equalsIgnoreCase("published")));
			
			filterTypeApp(ta, datasetQuery);
			
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
						.where(typeResearch.id.eq(metadata.typeResearch))
						.where(typeResearch.name.equalsIgnoreCase(typeFilter))
						.exists());
			}
			
			// Filter on theme
			if (!themeFilter.isEmpty()) {
				if(ta.equals(TypeApp.ONDERZOEKS_BIBLIOTHEEK)) {
					datasetQuery.where(
						tx.selectOne()
							.from(theme)
							.join(mdTheme).on(mdTheme.theme.eq(theme.id))
							.where(mdTheme.metadataId.eq(metadata.id))
							.where(theme.name.equalsIgnoreCase(themeFilter))
							.exists());
				} else if(ta.equals(TypeApp.WOO_PORTAAL)) {
					datasetQuery.where(
						tx.selectOne()
							.from(wooTheme)
							.join(mdWooTheme).on(mdWooTheme.wooTheme.eq(wooTheme.id))
							.where(mdWooTheme.metadataId.eq(metadata.id))
							.where(wooTheme.name.equalsIgnoreCase(themeFilter))
							.exists());
				}
			}
			
			// Filter on creationYear
			if (creationYear > 0) {
				datasetQuery.where(metadata.dateSourceCreation.year().eq((int) creationYear));
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
			for(Tuple t : md) {
				Map<String, Object> r = new HashMap<>();
				createJsonObject(tx, t, ta, r);
				result.add(r);
			}
			root.put("records", result);
			
			return ok(Json.toJson(root));
		});
	}
	
	/**
	 * Returns a {@link JSON} Object with the document data
	 * 
	 * @param metadataUuid the UUID of the metadata
	 * @return a {@link JSON} Object with the document data
	 */
	public Result findDocument(String typeApp, String metadataUuid) {
		TypeApp ta = getTypeApp(typeApp);
		if(ta == null) return notFound();
		
		return q.withTransaction(tx -> {
			SQLQuery<Tuple> datasetQuery = tx.select(metadata.id, metadata.uuid, metadata.title, metadata.description, creatorLabel.label,
					rightsLabel.label, typeInformationLabel.label, typeResearchLabel.label, useLimitationLabel.label, metadata.dateSourcePublication,
					metadata.dateSourceCreation)
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
					.and(metadata.uuid.eq(metadataUuid)));
			
			filterTypeApp(ta, datasetQuery);
			
			Map<String, Object> r = new HashMap<>();
			Optional<Tuple> mdResult = Optional.ofNullable(datasetQuery.fetchOne());
			mdResult.ifPresent(t -> createJsonObject(tx, t, ta, r));
			
			return ok(Json.toJson(r));
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
				.where(typeResearch.name.notEqualsIgnoreCase("none")
					.and(typeResearch.name.notEqualsIgnoreCase("wooDocument")))
				.orderBy(typeResearchLabel.label.asc())
				.fetch()
				.stream()
				.map(row -> {
					Map<String, Object> r = new HashMap<>();
					r.put("id", row.get(typeResearch.name));
					r.put("label", row.get(typeResearchLabel.label));
					return r;
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
					Map<String, Object> r = new HashMap<>();
					r.put("id", row.get(theme.name));
					r.put("label", row.get(themeLabel.label));
					return r;
				})
				.collect(Collectors.toList());
			
			return ok(Json.toJson(result));
		});
	}
	
	/**
	 * Returns a {@link JSON} array with all available WOO themes
	 * 
	 * @return a {@link JSON} array with all available WOO themes
	 */
	public Result getWooThemes() {
		return q.withTransaction(tx -> {
			List<Map<String, Object>> result = tx.select(wooTheme.name, wooThemeLabel.label)
				.from(wooTheme)
				.join(wooThemeLabel).on(wooThemeLabel.wooThemeId.eq(wooTheme.id))
				.orderBy(wooThemeLabel.label.asc())
				.fetch()
				.stream()
				.map(row -> {
					Map<String, Object> r = new HashMap<>();
					r.put("id", row.get(wooTheme.name));
					r.put("label", row.get(wooThemeLabel.label));
					return r;
				})
				.collect(Collectors.toList());
			
			return ok(Json.toJson(result));
		});
	}
	
	private TypeApp getTypeApp(String typeApp) {
		switch(typeApp) {
			case "woo":
				return TypeApp.WOO_PORTAAL;
			case "ob":
				return TypeApp.ONDERZOEKS_BIBLIOTHEEK;
			default:
				return null;
		}
	}
	
	private void filterTypeApp(TypeApp ta, SQLQuery<Tuple> datasetQuery) {
		if(ta.equals(TypeApp.ONDERZOEKS_BIBLIOTHEEK)) {
			datasetQuery
				.where(typeResearch.name.notEqualsIgnoreCase("none")
					.and(typeResearch.name.notEqualsIgnoreCase("wooDocument")));
		} else if(ta.equals(TypeApp.WOO_PORTAAL)) {
			datasetQuery.where(typeResearch.name.equalsIgnoreCase("wooDocument"));
		}
	}
	
	private void createJsonObject(Transaction tx, Tuple t, TypeApp ta, Map<String, Object> r) {
		r.put("uuid", t.get(metadata.uuid));
		r.put("titel", t.get(metadata.title));
		r.put("omschrijving", t.get(metadata.description));
		r.put("eindverantwoordelijke", t.get(creatorLabel.label));
		r.put("eigendomsrechten", t.get(rightsLabel.label));
		r.put("typeInformatie", t.get(typeInformationLabel.label));
		if(ta.equals(TypeApp.ONDERZOEKS_BIBLIOTHEEK)) {
			r.put("typeOnderzoek", t.get(typeResearchLabel.label));
		}
		r.put("gebruiksrestricties", t.get(useLimitationLabel.label));
		r.put("datumPublicatie", t.get(metadata.dateSourcePublication).toLocalDateTime().toString());
		r.put("datumCreatie", t.get(metadata.dateSourceCreation).toLocalDateTime().toString());
		
		// Bijlagen
		List<Map<String, Object>> attachmentsList = tx.select(mdAttachment.id, mdAttachment.attachmentName)
			.from(mdAttachment)
			.where(mdAttachment.metadataId.eq(t.get(metadata.id)))
			.fetch()
			.stream()
			.map(row -> {
				Map<String, Object> attachment = new HashMap<>();
				attachment.put("naam", row.get(mdAttachment.attachmentName));
				return attachment;
			})
			.collect(Collectors.toList());
		r.put("bijlagen", attachmentsList);
		
		// Themas
		if(ta.equals(TypeApp.ONDERZOEKS_BIBLIOTHEEK)) {
			List<String> themes = tx.select(themeLabel.label)
				.from(mdTheme)
				.join(theme).on(theme.id.eq(mdTheme.theme))
				.join(themeLabel).on(themeLabel.themeId.eq(theme.id))
				.where(mdTheme.metadataId.eq(t.get(metadata.id)))
				.fetch();
			r.put("themas", themes);
		}
		
		// WOO themas
		if(ta.equals(TypeApp.WOO_PORTAAL)) {
			List<String> wooThemes = tx.select(wooThemeLabel.label)
				.from(mdWooTheme)
				.join(wooTheme).on(wooTheme.id.eq(mdWooTheme.wooTheme))
				.join(wooThemeLabel).on(wooThemeLabel.wooThemeId.eq(wooTheme.id))
				.where(mdWooTheme.metadataId.eq(t.get(metadata.id)))
				.fetch();
			r.put("wooThemas", wooThemes);
		}
	}
}
