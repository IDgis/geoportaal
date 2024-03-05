package controllers;

import static models.QCreator.creator;
import static models.QCreatorLabel.creatorLabel;
import static models.QMdAttachment.mdAttachment;
import static models.QMdFormat.mdFormat;
import static models.QMdFormatLabel.mdFormatLabel;
import static models.QMdSubject.mdSubject;
import static models.QMdTheme.mdTheme;
import static models.QMetadata.metadata;
import static models.QMetadataSearch.metadataSearch;
import static models.QRights.rights;
import static models.QRightsLabel.rightsLabel;
import static models.QStatus.status;
import static models.QStatusLabel.statusLabel;
import static models.QSubject.subject;
import static models.QSubjectLabel.subjectLabel;
import static models.QTheme.theme;
import static models.QThemeLabel.themeLabel;
import static models.QTypeInformation.typeInformation;
import static models.QTypeInformationLabel.typeInformationLabel;
import static models.QTypeResearch.typeResearch;
import static models.QTypeResearchLabel.typeResearchLabel;
import static models.QUseLimitation.useLimitation;
import static models.QUseLimitationLabel.useLimitationLabel;
import static models.QUser.user;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.sql.SQLQuery;

import actions.DefaultAuthenticator;
import exceptions.GeoportaalBeheerException;
import models.Delete;
import models.Search;
import models.Sort;
import models.Supplier;
import play.Logger;
import play.Routes;
import play.data.DynamicForm;
import play.data.Form;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import util.QueryDSL;
import views.html.*;

/**
 * The class for the index entity
 * 
 * @author Sandro
 *
 */
@Security.Authenticated(DefaultAuthenticator.class)
public class Index extends Controller {
	@Inject QueryDSL q;
	
	/**
	 * Renders the index page
	 * 
	 * @param textSearch the search value of the text field
	 * @param supplierSearch the search value of the supplier field
	 * @param statusSearch the search value of the status field
	 * @param dateStartSearch the search value of the date start field
	 * @param dateEndSearch the search value of the date end field
	 * @param sort the sort type value
	 * @param checked the UUID's of the records who are checked
	 * @return the {@link Result} of the index page
	 * @throws SQLException
	 */
	public Result index(String textSearch, String supplierSearch, String statusSearch, String dateCreateStartSearch, 
			String dateCreateEndSearch, String dateUpdateStartSearch, String dateUpdateEndSearch, 
			String sort, String checked) throws SQLException {
		return q.withTransaction(tx -> {
			// Fetches the supplier list
			List<Tuple> supplierList = tx.select(user.all())
				.from(user)
				.where(user.archived.isFalse())
				.orderBy(user.label.asc())
				.fetch();
			
			// Fetches the status list
			List<Tuple> statusList = tx.select(status.name, statusLabel.label)
				.from(status)
				.join(statusLabel).on(status.id.eq(statusLabel.statusId))
				.orderBy(status.id.asc())
				.fetch();
			
			// Create a SimpleDateFormat object for the yyyy-MM-dd format
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat sdfLocal = new SimpleDateFormat("dd-MM-yyyy");
			
			// Fetches the role of the logged in user
			Integer roleId = tx.select(user.roleId)
				.from(user)
				.where(user.username.equalsIgnoreCase(session("username")))
				.fetchOne();
			
			// Fetches the supplier id of the logged in user
			Integer supplierId = tx.select(user.id)
				.from(user)
				.where(user.username.equalsIgnoreCase(session("username")))
				.fetchOne();
			
			// Start of query to fetch the applicable records
			SQLQuery<Tuple> datasetQuery = tx.select(metadata.id, metadata.uuid, metadata.fileId, metadata.title, metadata.status, metadata.lastRevisionDate, 
					statusLabel.label, user.label, status.name, mdFormat.name)
				.from(metadata)
				.join(status).on(metadata.status.eq(status.id))
				.join(user).on(metadata.supplier.eq(user.id))
				.join(statusLabel).on(status.id.eq(statusLabel.statusId))
				.join(mdFormat).on(metadata.mdFormat.eq(mdFormat.id));
			
			// Strip characters from text search string that conflict with Postgres full-text search
			String textSearchFirstStrip = textSearch.replace("&", "");
			String textSearchSecondStrip = textSearchFirstStrip.replace("(", "");
			String textSearchThirdStrip = textSearchSecondStrip.replace(")", "");
			String textSearchFinalStrip = textSearchThirdStrip.replace(":", "");
			
			// Convert text search string to an array
			String[] textSearchTerms = textSearchFinalStrip.split("\\s+");
			
			// Convert array of text search words to list
			List<String> finalListTextSearch = new ArrayList<String>();
			List<String> textListTermsSearch = Arrays.asList(textSearchTerms);
			for(String word : textListTermsSearch) {
				if(word.length() > 0) {
					finalListTextSearch.add(word + ":*");
				}
			}
			
			// Create a string of all the words in the text search list with a '&' between them
			String tsQuery = 
				finalListTextSearch.stream()
					.filter(str -> !str.isEmpty())
					.collect(Collectors.joining(" & "));
			
			// Filter records on text search words
			if(!tsQuery.isEmpty()) {
				// Get local language for query
				String language = Messages.get("tsv.language");
				
				datasetQuery.where(
					tx.selectOne()
						.from(metadataSearch)
						.where(metadataSearch.metadataId.eq(metadata.id))
						.where(metadataSearch.tsv.query(language, tsQuery))
						.exists());
			}
			
			// Filter records on supplier selected
			if(!"none".equals(supplierSearch)) {
				datasetQuery
					.where(user.label.eq(supplierSearch));
			}
			
			// Filter records on status selected
			if(!"none".equals(statusSearch)) {
				datasetQuery
					.where(status.name.eq(statusSearch));
			}
			
			// If no status is selected exclude the records in the trash bin and archive
			if("none".equals(statusSearch)) {
				datasetQuery.where(metadata.status.notIn(5).and(metadata.status.notIn(6)));
			}
			
			// If user is a supplier never display the records in the trash bin 
			if(roleId.equals(2) && "deleted".equals(statusSearch)) {
				datasetQuery.where(metadata.status.notIn(5));
			}
			
			// If user is a supplier never display the records in the archive
			if(roleId.equals(2) && "archived".equals(statusSearch)) {
				datasetQuery.where(metadata.status.notIn(6));
			}
			
			// If search value of date start isn't null or empty convert it to a date
			final Date finalCreateDateStartSearch;
			if(dateCreateStartSearch != null && !dateCreateStartSearch.trim().isEmpty()) {
				try {
					finalCreateDateStartSearch = sdf.parse(dateCreateStartSearch);
				} catch(ParseException e) {
					throw new IllegalArgumentException("wrong dateStartSearch", e);
				}
			} else {
				finalCreateDateStartSearch = null;
			}
			
			// If search value of date end isn't null or empty convert it to a date
			final Date finalCreateDateEndSearch;
			if(dateCreateEndSearch != null && !dateCreateEndSearch.trim().isEmpty()) {
				try {
					finalCreateDateEndSearch = sdf.parse(dateCreateEndSearch);
				} catch(ParseException e) {
					throw new IllegalArgumentException("wrong dateEndSearch", e);
				}
			} else {
				finalCreateDateEndSearch = null;
			}
			
			// If date start and date end aren't null convert it to a timestamp and filter records on the dates. Add one day to the end date.
			Timestamp timestampCreateStartSearch = null;
			Timestamp timestampCreateEndSearch = null;
			if(finalCreateDateStartSearch != null && finalCreateDateEndSearch != null) {
				timestampCreateStartSearch = new Timestamp(finalCreateDateStartSearch.getTime());
				timestampCreateEndSearch = new Timestamp(finalCreateDateEndSearch.getTime() + 86400000);
				
				datasetQuery
					.where(metadata.dateSourceCreation.after(timestampCreateStartSearch))
					.where(metadata.dateSourceCreation.before(timestampCreateEndSearch));
			}
			
			// If search value of date start isn't null or empty convert it to a date
			final Date finalUpdateDateStartSearch;
			if(dateUpdateStartSearch != null && !dateUpdateStartSearch.trim().isEmpty()) {
				try {
					finalUpdateDateStartSearch = sdf.parse(dateUpdateStartSearch);
				} catch(ParseException e) {
					throw new IllegalArgumentException("wrong dateStartSearch", e);
				}
			} else {
				finalUpdateDateStartSearch = null;
			}
			
			// If search value of date end isn't null or empty convert it to a date
			final Date finalUpdateDateEndSearch;
			if(dateUpdateEndSearch != null && !dateUpdateEndSearch.trim().isEmpty()) {
				try {
					finalUpdateDateEndSearch = sdf.parse(dateUpdateEndSearch);
				} catch(ParseException e) {
					throw new IllegalArgumentException("wrong dateEndSearch", e);
				}
			} else {
				finalUpdateDateEndSearch = null;
			}
			
			// If date start and date end aren't null convert it to a timestamp and filter records on the dates. Add one day to the end date.
			Timestamp timestampUpdateStartSearch = null;
			Timestamp timestampUpdateEndSearch = null;
			if(finalUpdateDateStartSearch != null && finalUpdateDateEndSearch != null) {
				timestampUpdateStartSearch = new Timestamp(finalUpdateDateStartSearch.getTime());
				timestampUpdateEndSearch = new Timestamp(finalUpdateDateEndSearch.getTime() + 86400000);
				
				datasetQuery
					.where(metadata.lastRevisionDate.after(timestampUpdateStartSearch))
					.where(metadata.lastRevisionDate.before(timestampUpdateEndSearch));
			}
			
			// If user is a supplier only display their own records
			if(roleId.equals(2)) {
				datasetQuery.where(metadata.supplier.eq(supplierId));
			}
			
			// Limit the records to a maximum of 200
			datasetQuery.limit(200);
			
			// Sort the records according to sort value
			if("numberDesc".equals(sort)) {
				datasetQuery.orderBy(metadata.fileId.desc());
			}
			
			if("numberAsc".equals(sort)) {
				datasetQuery.orderBy(metadata.fileId.asc());
			}
			
			if("titleDesc".equals(sort)) {
				datasetQuery.orderBy(metadata.title.desc());
			}
			
			if("titleAsc".equals(sort)) {
				datasetQuery.orderBy(metadata.title.asc());
			}
			
			if("supplierDesc".equals(sort)) {
				datasetQuery.orderBy(user.label.desc());
			}
			
			if("supplierAsc".equals(sort)) {
				datasetQuery.orderBy(user.label.asc());
			}
			
			if("statusDesc".equals(sort)) {
				datasetQuery.orderBy(metadata.status.desc());
			}
			
			if("statusAsc".equals(sort)) {
				datasetQuery.orderBy(metadata.status.asc());
			}
			
			if("dateAsc".equals(sort)) {
				datasetQuery.orderBy(metadata.lastRevisionDate.asc());
			}
			
			if("dateDesc".equals(sort)) {
				datasetQuery.orderBy(metadata.lastRevisionDate.desc());
			}
			
			// Fetch the records
			List<Tuple> datasetRows = datasetQuery.fetch();
			
			// Create a timestamp without the extra day
			Timestamp resetTimestampCreateEndSearch = null;
			if(finalCreateDateEndSearch != null) {
				resetTimestampCreateEndSearch = new Timestamp(finalCreateDateEndSearch.getTime());
			}
			
			// Create a timestamp without the extra day
			Timestamp resetTimestampUpdateEndSearch = null;
			if(finalUpdateDateEndSearch != null) {
				resetTimestampUpdateEndSearch = new Timestamp(finalUpdateDateEndSearch.getTime());
			}
			
			// Convert string of UUID's to list
			List<String> checkedList = new ArrayList<String>();
			String[] checkedArray = checked.split(" ");
			if(!"".equals(checked.trim())) {
				for(String checkedString : checkedArray) {
					checkedList.add(checkedString);
				}
			}
			
			Long datasetTotalCount = datasetQuery.fetchCount();
			Long datasetDisplayedCount;
			
			if(datasetTotalCount > 200) {
				datasetDisplayedCount = 200L;
			} else {
				datasetDisplayedCount = datasetTotalCount;
			}
			
			// Return index page
			return ok(views.html.index.render(datasetRows, supplierList, statusList, sdf, sdfLocal, roleId, textSearch, 
				supplierSearch, statusSearch, dateCreateStartSearch, dateCreateEndSearch, timestampCreateStartSearch, 
				resetTimestampCreateEndSearch, dateUpdateStartSearch, dateUpdateEndSearch, timestampUpdateStartSearch, 
				resetTimestampUpdateEndSearch, sort, checkedList, datasetTotalCount, datasetDisplayedCount));
		});
	}
	
	/**
	 * Handles the search form
	 * 
	 * @return the {@link Result} of the index page
	 */
	public Result search() {
		// Fetches the form fields
		Form<Search> searchForm = Form.form(Search.class);
		Search s = searchForm.bindFromRequest().get();
		String textSearch = s.getText();
		String supplierSearch = s.getSupplier();
		String statusSearch = s.getStatus();
		String dateCreateStartSearch = s.getDateCreateStart();
		String dateCreateEndSearch = s.getDateCreateEnd();
		String dateUpdateStartSearch = s.getDateUpdateStart();
		String dateUpdateEndSearch = s.getDateUpdateEnd();
		
		// Empties both date search fields if one of them is empty
		if("".equals(dateCreateStartSearch.trim()) || "".equals(dateCreateEndSearch.trim())) {
			dateCreateStartSearch = "";
			dateCreateEndSearch = "";
		}
		
		// Empties both date search fields if one of them is empty
		if("".equals(dateUpdateStartSearch.trim()) || "".equals(dateUpdateEndSearch.trim())) {
			dateUpdateStartSearch = "";
			dateUpdateEndSearch = "";
		}
		
		// Return index page
		return redirect(controllers.routes.Index.index(textSearch, supplierSearch, statusSearch, 
				dateCreateStartSearch, dateCreateEndSearch, dateUpdateStartSearch, dateUpdateEndSearch, "dateDesc", ""));
	}
	
	/**
	 * Handles the sort action
	 * 
	 * @return the {@link Result} of the index page
	 */
	public Result sort() {
		// Fetches the form fields
		Form<Sort> sortForm = Form.form(Sort.class);
		Sort s = sortForm.bindFromRequest().get();
		String textSearch = s.getText();
		String supplierSearch = s.getSupplier();
		String statusSearch = s.getStatus();
		String dateCreateStartSearch = s.getDateCreateStart();
		String dateCreateEndSearch = s.getDateCreateEnd();
		String dateUpdateStartSearch = s.getDateUpdateStart();
		String dateUpdateEndSearch = s.getDateUpdateEnd();
		
		// Fetch the sort value
		String sort = s.getSort();
		
		// Fetch the UUID's of the records checked
		List<String> recordsChecked = s.getRecordsChecked();
		
		// Empties both date search fields if one of them is empty
		if("".equals(dateCreateStartSearch.trim()) || "".equals(dateCreateEndSearch.trim())) {
			dateCreateStartSearch = "";
			dateCreateEndSearch = "";
		}
		
		// Empties both date search fields if one of them is empty
		if("".equals(dateUpdateStartSearch.trim()) || "".equals(dateUpdateEndSearch.trim())) {
			dateUpdateStartSearch = "";
			dateUpdateEndSearch = "";
		}
		
		// Make one long string of all UUID's of the records checked
		String checked = "";
		if(recordsChecked != null) {
			checked = recordsChecked.stream().collect(Collectors.joining(" "));
		}
		
		// Return the index page
		return redirect(controllers.routes.Index.index(textSearch, supplierSearch, statusSearch, 
				dateCreateStartSearch, dateCreateEndSearch, dateUpdateStartSearch, dateUpdateEndSearch, 
			sort, checked) + "#list");
	}
	
	/**
	 * Change the status of selected records
	 * 
	 * @return the {@link Result} of the index page
	 */
	public Result changeStatus() {
		// Fetches the form fields
		Form<models.Status> statusForm = Form.form(models.Status.class);
		models.Status s = statusForm.bindFromRequest().get();
		String textSearch = s.getTextSearch();
		String supplierSearch = s.getSupplierSearch();
		String statusSearch = s.getStatusSearch();
		String dateCreateStartSearch = s.getDateCreateStartSearch();
		String dateCreateEndSearch = s.getDateCreateEndSearch();
		String dateUpdateStartSearch = s.getDateUpdateStartSearch();
		String dateUpdateEndSearch = s.getDateUpdateEndSearch();
		
		// Fetches the name of the destination status
		String statusName = s.getStatus();
		
		// Fetch the UUID's of the records checked
		List<String> changeRecords = s.getRecordsChange();
		
		return q.withTransaction(tx -> {
			// Fetch the role id of the logged in user
			Integer roleId = tx.select(user.roleId)
				.from(user)
				.where(user.username.equalsIgnoreCase(session("username")))
				.fetchOne();
			
			// Fetch the user id of the logged in user
			Integer userId = tx.select(user.id)
				.from(user)
				.where(user.username.equalsIgnoreCase(session("username")))
				.fetchOne();
			
			// Fill list of records according to type of user
			List<String> finalChangeRecords = new ArrayList<String>();
			if(roleId.equals(2)) {
				if(changeRecords != null) {
					for(String record : changeRecords) {
						// Fetch status id of record about to be changed
						Integer statusId = tx.select(metadata.status)
							.from(metadata)
							.where(metadata.uuid.eq(record))
							.fetchOne();
						
						// Fetch supplier id of record about to be changed
						Integer supplierId = tx.select(metadata.supplier)
							.from(metadata)
							.where(metadata.uuid.eq(record))
							.fetchOne();
						
						// Only add to list if status isn't published, deleted or archived and logged in user matches the supplier id
						if(!statusId.equals(4) && !statusId.equals(5) 
								&& !statusId.equals(6) && userId.equals(supplierId)) {
							finalChangeRecords.add(record);
						}
					}
				}
			} else {
				if(changeRecords != null) {
					for(String record : changeRecords) {
						finalChangeRecords.add(record);
					}
				}
			}
			
			if(finalChangeRecords != null && statusName != null) {
				// Fetch status key of the status the records should receive
				Integer statusKey = tx.select(status.id)
					.from(status)
					.where(status.name.eq(statusName))
					.fetchOne();
				
				if(statusKey != null) {
					// Don't do anything if user is a supplier and the destination status is published 
					// or archived
					if(roleId.equals(2) && 
							("published".equals(statusName) || "archived".equals(statusName))) {
						// do nothing
					} else {
						// Change the records
						Long count = tx.update(metadata)
							.where(metadata.uuid.in(finalChangeRecords))
							.set(metadata.status, statusKey)
							.set(metadata.lastRevisionDate, new Timestamp(new Date().getTime()))
							.execute();
						
						// Check if the count of the changed records is what is expected
						Integer finalCount = count.intValue();
						if(!finalCount.equals(finalChangeRecords.size())) {
							throw new GeoportaalBeheerException("Changing status: different amount of affected rows than expected");
						}
						
						if("published".equals(statusName)) {
							Long publishedCount = tx.update(metadata)
								.where(metadata.uuid.in(finalChangeRecords))
								.set(metadata.dateSourcePublication, new Timestamp(new Date().getTime()))
								.execute();
							
							// Check if the count of the changed records is what is expected
							Integer finalPublishedCount = publishedCount.intValue();
							if(!finalPublishedCount.equals(finalChangeRecords.size())) {
								throw new GeoportaalBeheerException("Updating date published: different amount of affected rows than expected");
							}
						}
					}
				}
			}
			
			// Return the index page
			return redirect(controllers.routes.Index.index(textSearch, supplierSearch, statusSearch, 
					dateCreateStartSearch, dateCreateEndSearch, dateUpdateStartSearch, dateUpdateEndSearch, 
					"dateDesc", ""));
		});
		
	}
	
	/**
	 * Change the supplier of selected records
	 * 
	 * @return the {@link Result} of the index page
	 */
	public Result changeSupplier() {
		// Fetches the form fields
		Form<Supplier> supplierForm = Form.form(Supplier.class);
		Supplier s = supplierForm.bindFromRequest().get();
		String textSearch = s.getTextSearch();
		String supplierSearch = s.getSupplierSearch();
		String statusSearch = s.getStatusSearch();
		String dateCreateStartSearch = s.getDateCreateStartSearch();
		String dateCreateEndSearch = s.getDateCreateEndSearch();
		String dateUpdateStartSearch = s.getDateUpdateStartSearch();
		String dateUpdateEndSearch = s.getDateUpdateEndSearch();
		
		// Fetches the records about to be changed
		List<String> changeRecords = s.getRecordsChange();
		
		// Fetches the name of the destination supplier
		String supplierName = s.getSupplier();
		
		return q.withTransaction(tx -> {
			// Fetches the role id of the logged in user
			Integer roleId = tx.select(user.roleId)
				.from(user)
				.where(user.username.equalsIgnoreCase(session("username")))
				.fetchOne();
			
			
			if(!roleId.equals(2) && changeRecords != null && supplierName != null) {
				// Fetches the supplier key of the destination supplier
				Integer supplierKey = tx.select(user.id)
					.from(user)
					.where(user.label.eq(supplierName))
					.fetchOne();
				
				if(supplierKey != null) {
					// Change the selected records to new supplier
					Long count = tx.update(metadata)
						.where(metadata.uuid.in(changeRecords))
						.set(metadata.supplier, supplierKey)
						.execute();
					
					// Check if the count of the changed records is what is expected
					Integer finalCount = count.intValue();
					if(!finalCount.equals(changeRecords.size())) {
						throw new GeoportaalBeheerException("Changing supplier: different amount of affected rows than expected");
					}
				}
				
				tx.refreshMaterializedViewConcurrently(metadataSearch);
			}
			
			// Return the index page
			return redirect(controllers.routes.Index.index(textSearch, supplierSearch, statusSearch, 
					dateCreateStartSearch, dateCreateEndSearch, dateUpdateStartSearch, dateUpdateEndSearch, 
					"dateDesc", ""));
		});
	}
	
	/**
	 * Delete the metadata of selected records
	 * 
	 * @return the {@link Result} of the index page
	 */
	public Result deleteMetadata() {
		// Fetches the form fields
		Form<Delete> deleteForm = Form.form(Delete.class);
		Delete d = deleteForm.bindFromRequest().get();
		String textSearch = d.getTextSearch();
		String supplierSearch = d.getSupplierSearch();
		String statusSearch = d.getStatusSearch();
		String dateCreateStartSearch = d.getDateCreateStartSearch();
		String dateCreateEndSearch = d.getDateCreateEndSearch();
		String dateUpdateStartSearch = d.getDateUpdateStartSearch();
		String dateUpdateEndSearch = d.getDateUpdateEndSearch();
		
		// Fetches the records about to be deleted
		List<String> deleteRecords = d.getRecordsToDel();
		
		// Value to determine if the records should be permanently deleted
		String permDel = d.getPermDel();
		
		return q.withTransaction(tx -> {
			// Fetches the role id of the logged in user
			Integer roleId = tx.select(user.roleId)
				.from(user)
				.where(user.username.equalsIgnoreCase(session("username")))
				.fetchOne();
			
			// Fetches the user id of the logged in user
			Integer userId = tx.select(user.id)
				.from(user)
				.where(user.username.equalsIgnoreCase(session("username")))
				.fetchOne();
			
			// Fill list according to type of user
			List<String> finalDeleteRecords = new ArrayList<String>();
			if(roleId.equals(2)) {
				if(deleteRecords != null) {
					for(String record : deleteRecords) {
						// Fetch the status id of record about to be changed
						Integer statusId = tx.select(metadata.status)
							.from(metadata)
							.where(metadata.uuid.eq(record))
							.fetchOne();
						
						// Fetch supplier id of record about to be changed
						Integer supplierId = tx.select(metadata.supplier)
							.from(metadata)
							.where(metadata.uuid.eq(record))
							.fetchOne();
						
						// Add record to list if status isn't published or deleted and when there is a match between logged in
						// user and supplier id
						if(!statusId.equals(4) && !statusId.equals(5) && userId.equals(supplierId)) {
							finalDeleteRecords.add(record);
						}
					}
				}
			} else {
				if(deleteRecords != null) {
					for(String record : deleteRecords) {
						finalDeleteRecords.add(record);
					}
				}
			}
			
			// Different actions according to permDel value
			if(finalDeleteRecords != null) {
				if(permDel != null) {
					// Delete records
					Long count = tx.delete(metadata)
						.where(metadata.uuid.in(finalDeleteRecords))
						.execute();
					
					// Check if the count of the deleted records is what is expected
					Integer finalCount = count.intValue();
					if(!finalCount.equals(finalDeleteRecords.size())) {
						throw new GeoportaalBeheerException("Deleting records: different amount of affected rows than expected");
					}
				} else {
					// Change status to deleted
					Long count = tx.update(metadata)
						.where(metadata.uuid.in(finalDeleteRecords))
						.set(metadata.status, 5)
						.set(metadata.lastRevisionDate, new Timestamp(new Date().getTime()))
						.execute();
					
					// Check if the count of the changed records is what is expected
					Integer finalCount = count.intValue();
					if(!finalCount.equals(finalDeleteRecords.size())) {
						throw new GeoportaalBeheerException("Change status to deleted: different amount of affected rows than expected");
					}
				}
			}
			
			// Refresh materialized view
			tx.refreshMaterializedViewConcurrently(metadataSearch);
			
			// Return index page
			return redirect(controllers.routes.Index.index(textSearch, supplierSearch, statusSearch, 
					dateCreateStartSearch, dateCreateEndSearch, dateUpdateStartSearch, dateUpdateEndSearch, 
					"dateDesc", ""));
		});
	}
	
	/**
	 * The validation of the search form
	 * 
	 * @return the {@link Result} of the validation
	 */
	public Result validateForm() {
		try {
			// Fetches the form fields
			DynamicForm requestData = Form.form().bindFromRequest();
			String dateCreateStart = requestData.get("dateCreateStart");
			String dateCreateEnd = requestData.get("dateCreateEnd");
			String dateUpdateStart = requestData.get("dateUpdateStart");
			String dateUpdateEnd = requestData.get("dateUpdateEnd");
			
			// Check if the date is valid
			Boolean dateCreateSearchStartReturn = validateDate(dateCreateStart);
			Boolean dateCreateSearchEndReturn = validateDate(dateCreateEnd);
			
			// Check if the date is valid
			Boolean dateUpdateSearchStartReturn = validateDate(dateUpdateStart);
			Boolean dateUpdateSearchEndReturn = validateDate(dateUpdateEnd);
			
			// Create string for error message
			String dateCreateSearchStartMsg = null;
			String dateCreateSearchEndMsg = null;
			
			// Create string for error message
			String dateUpdateSearchStartMsg = null;
			String dateUpdateSearchEndMsg = null;
			
			// Assign error message if validation of a date returned false
			if(!dateCreateSearchStartReturn || !dateCreateSearchEndReturn) {
				if(!dateCreateSearchStartReturn) {
					dateCreateSearchStartMsg = Messages.get("validate.create.date.start");
				} else {
					dateCreateSearchStartMsg = null;
				}
				
				if(!dateCreateSearchEndReturn) {
					dateCreateSearchEndMsg = Messages.get("validate.create.date.end");
				} else {
					dateCreateSearchEndMsg = null;
				}
			}
			
			// Assign error message if validation of a date returned false
			if(!dateUpdateSearchStartReturn || !dateUpdateSearchEndReturn) {
				if(!dateUpdateSearchStartReturn) {
					dateUpdateSearchStartMsg = Messages.get("validate.update.date.start");
				} else {
					dateUpdateSearchStartMsg = null;
				}
				
				if(!dateUpdateSearchEndReturn) {
					dateUpdateSearchEndMsg = Messages.get("validate.update.date.end");
				} else {
					dateUpdateSearchEndMsg = null;
				}
			}
			
			// Return specific error message view
			return ok(bindingerror.render(null, null, null, null, dateCreateSearchStartMsg, 
					dateCreateSearchEndMsg, dateUpdateSearchStartMsg, dateUpdateSearchEndMsg));
		} catch(IllegalStateException ise) {
			Logger.error(ise.getMessage(), ise);
			
			// Return generic error message view
			return ok(bindingerror.render(Messages.get("validate.search.generic"), null, null, null, null, 
					null, null, null));
		}
	}
	
	/**
	 * Validation of dates
	 * 
	 * @param date the date as a {@link String} to be validated
	 * @return a {@link Boolean} that signals if validation was successful
	 */
	public Boolean validateDate(String date) {
		if(date != null) {
			// If string of date is empty return true
			if("".equals(date.trim())) {
				return true;
			}
			
			// Create SimpleDateFormat and use strict parsing
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			sdf.setLenient(false);
			try {
				// Try to parse date, if successful return true
				sdf.parse(date);
				return true;
			} catch(ParseException pe) {
				Logger.error(pe.getMessage(), pe);
				
				// If parsing was unsuccessful return false
				return false;
			}
		} else {
			return true;
		}
	}
	
	/**
	 * Make controller methods available in JavaScript
	 * 
	 * @return the {@link Result} of the controller methods as JavaScript script
	 */
	public Result jsRoutes() {
		return ok(Routes.javascriptRouter("jsRoutes",
			controllers.routes.javascript.Assets.versioned(),
			controllers.routes.javascript.Index.deleteMetadata(),
			controllers.routes.javascript.Index.changeStatus(),
			controllers.routes.javascript.Index.changeSupplier(),
			controllers.routes.javascript.Metadata.validateForm(),
			controllers.routes.javascript.Index.validateForm()
		)).as("text/javascript");
	}
}