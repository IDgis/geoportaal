package controllers;

import static models.QCreator.creator;
import static models.QCreatorLabel.creatorLabel;
import static models.QMdAttachment.mdAttachment;
import static models.QMdFormat.mdFormat;
import static models.QMdFormatLabel.mdFormatLabel;
import static models.QMdSubject.mdSubject;
import static models.QMetadata.metadata;
import static models.QMetadataSearch.metadataSearch;
import static models.QRights.rights;
import static models.QRightsLabel.rightsLabel;
import static models.QSubject.subject;
import static models.QSubjectLabel.subjectLabel;
import static models.QTypeInformation.typeInformation;
import static models.QTypeInformationLabel.typeInformationLabel;
import static models.QUseLimitation.useLimitation;
import static models.QUseLimitationLabel.useLimitationLabel;
import static models.QUser.user;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import com.querydsl.core.Tuple;
import com.querydsl.sql.SQLQuery;

import actions.DefaultAuthenticator;
import models.DublinCore;
import models.Search;
import play.data.DynamicForm;
import play.data.Form;
import play.i18n.Messages;
import play.libs.ws.WSClient;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import play.mvc.Security;
import play.twirl.api.Html;
import util.QueryDSL;
import views.html.*;

/**
 * The class for the metadata entity
 * 
 * @author Sandro
 *
 */
@Security.Authenticated(DefaultAuthenticator.class)
public class Metadata extends Controller {
	@Inject QueryDSL q;
	@Inject WSClient ws;
	
	/**
	 * Render the form of a new metadata record
	 * 
	 * @param textSearch the search value of the text field
	 * @param supplierSearch the search value of the supplier field
	 * @param statusSearch the search value of the status field
	 * @param mdFormatSearch the search value of the format field
	 * @param dateStartSearch the search value of the date start field
	 * @param dateEndSearch the search value of the date end field
	 * @return the {@link Result} of the form page
	 */
	public Result renderCreateForm(String textSearch, String supplierSearch, String statusSearch, 
			String mdFormatSearch, String dateStartSearch, String dateEndSearch) {
		// Create boolean that determines if the form is for a new record
		Boolean create = true;
		
		// Create strings according to yyyy-MM-dd and dd-MM-yyyy formats
		String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date().getTime());
		
		// Create search object
		Search search = new Search(textSearch, supplierSearch, statusSearch, mdFormatSearch, dateStartSearch, dateEndSearch);
		
		return q.withTransaction(tx -> {
			// Fetch type information list
			List<Tuple> typeInformationList = tx.select(typeInformation.id, typeInformation.name, typeInformationLabel.label)
			.from(typeInformation)
				.join(typeInformationLabel).on(typeInformation.id.eq(typeInformationLabel.typeInformationId))
				.orderBy(typeInformationLabel.label.asc())
				.fetch();
			
			// Fetch creator list
			List<Tuple> creatorsList = tx.select(creator.id, creator.name, creatorLabel.label)
				.from(creator)
				.join(creatorLabel).on(creator.id.eq(creatorLabel.creatorId))
				.orderBy(creator.id.asc())
				.fetch();
			
			// Fetch rights list
			List<Tuple> rightsList = tx.select(rights.id, rights.name, rightsLabel.label)
				.from(rights)
				.join(rightsLabel).on(rights.id.eq(rightsLabel.rightsId))
				.orderBy(rightsLabel.label.asc())
				.fetch();
			
			// Fetch use limitation list
			List<Tuple> useLimitationList = tx.select(useLimitation.id, useLimitation.name, useLimitationLabel.label)
				.from(useLimitation)
				.join(useLimitationLabel).on(useLimitation.id.eq(useLimitationLabel.useLimitationId))
				.orderBy(useLimitationLabel.label.asc())
				.fetch();
			
			// Fetch format list
			List<Tuple> mdFormatList = tx.select(mdFormat.id, mdFormat.name, mdFormatLabel.label)
				.from(mdFormat)
				.join(mdFormatLabel).on(mdFormat.id.eq(mdFormatLabel.mdFormatId))
				.orderBy(mdFormatLabel.label.asc())
				.fetch();
			
			// Fetch subject list
			List<Tuple> subjectList = tx.select(subject.id, subject.name, subjectLabel.label)
				.from(subject)
				.join(subjectLabel).on(subject.id.eq(subjectLabel.subjectId))
				.fetch();
			
			// Fetches the role of the logged in user
			Integer roleId = tx.select(user.roleId)
					.from(user)
					.where(user.username.equalsIgnoreCase(session("username")))
					.fetchOne();
			
			// Return form page
			return ok(views.html.form.render(create, today, null, null, null, typeInformationList, creatorsList, rightsList, 
					useLimitationList, mdFormatList, null, subjectList, roleId, search, false, null, null));
		});
	}
	
	/**
	 * Handles the insertion of a new metadata record
	 * 
	 * @param textSearch the search value of the text field
	 * @param supplierSearch the search value of the supplier field
	 * @param statusSearch the search value of the status field
	 * @param mdFormatSearch the search value of the format field
	 * @param dateStartSearch the search value of the date start field
	 * @param dateEndSearch the search value of the date end field
	 * @return the {@link Result} of the index page
	 * @throws IOException
	 */
	public Result createSubmit(String textSearch, String supplierSearch, String statusSearch, String mdFormatSearch, 
			String dateStartSearch, String dateEndSearch) throws IOException {
		// Fetches the form
		Form<DublinCore> dcForm = Form.form(DublinCore.class);
		DublinCore dc = dcForm.bindFromRequest().get();
		
		// Generate an UUID
		String uuid = UUID.randomUUID().toString();
		
		return q.withTransaction(tx -> {
			// Fetches the type information key according to form value
			Integer typeInformationKey = tx.select(typeInformation.id)
				.from(typeInformation)
				.where(typeInformation.name.eq(dc.getTypeInformation()))
				.fetchOne();
			
			// Fetches the creator key according to form value
			Integer creatorKey = tx.select(creator.id)
				.from(creator)
				.where(creator.name.eq(dc.getCreator()))
				.fetchOne();
			
			// Fetches the rights key according to form value
			Integer rightsKey = tx.select(rights.id)
				.from(rights)
				.where(rights.name.eq(dc.getRights()))
				.fetchOne();
			
			// Fetches the use limitation key according to form value
			Integer useLimitationKey = tx.select(useLimitation.id)
				.from(useLimitation)
				.where(useLimitation.name.eq(dc.getUseLimitation()))
				.fetchOne();
			
			// Fetches the format key according to form value
			Integer formatKey = tx.select(mdFormat.id)
				.from(mdFormat)
				.where(mdFormat.name.eq(dc.getMdFormat()))
				.fetchOne();
			
			// Sets file id value to null if string is empty
			String fileIdValue;
			if("".equals(dc.getFileId().trim())) {
				fileIdValue = null;
			} else {
				fileIdValue = dc.getFileId();
			}
			
			// If creator value is other set creator other field
			String creatorOtherValue;
			if(!"other".equals(dc.getCreator())) {
				creatorOtherValue = null;
			} else {
				creatorOtherValue = dc.getCreatorOther();
			}
			
			// Check if dates are null
			Timestamp dateSourceCreationValue = nullCheckDate(dc.getDateSourceCreation());
			Timestamp dateSourcePublicationValue = nullCheckDate(dc.getDateSourcePublication());
			Timestamp dateSourceValidFromValue = nullCheckDate(dc.getDateSourceValidFrom());
			Timestamp dateSourceValidUntilValue = nullCheckDate(dc.getDateSourceValidUntil());
			
			// Fetches the supplier id of logged in user
			Integer supplierId = tx.select(user.id)
				.from(user)
				.where(user.username.equalsIgnoreCase(session("username")))
				.fetchOne();
			
			// Check if creator other isn't empty if creator is other
			Boolean creatorOtherFailed = false;
			if(creatorKey != null) {
				if(creatorKey.equals(9) && "".equals(dc.getCreatorOther().trim())) {
					creatorOtherFailed = true;
				} else {
					creatorOtherFailed = false;
				}
			}
			
			Boolean dateCreatePublicationCheck = logicCheckDate(dc.getDateSourceCreation(), dc.getDateSourcePublication());
			Boolean dateValidCheck = logicCheckDate(dc.getDateSourceValidFrom(), dc.getDateSourceValidUntil());
			
			// Checks if every mandatory field has been completed, if not return the form with previous state
			if("".equals(dc.getTitle().trim()) || "".equals(dc.getDescription().trim()) || "".equals(dc.getLocation().trim()) || 
				"".equals(dc.getFileId().trim()) || creatorKey == null || creatorOtherFailed || useLimitationKey == null || 
				dateSourceCreationValue == null || dc.getSubject() == null || !dateCreatePublicationCheck || !dateValidCheck) {
					
					DublinCore previousDC = new DublinCore(dc.getLocation(), dc.getFileId(), dc.getTitle(), dc.getDescription(), dc.getTypeInformation(),
						dc.getCreator(), dc.getCreatorOther(), dc.getRights(), dc.getUseLimitation(), dc.getMdFormat(), dc.getSource(),
						dc.getDateSourceCreation(), dc.getDateSourcePublication(), dc.getDateSourceValidFrom(), dc.getDateSourceValidUntil(), 
						dc.getSubject(), null);
					
					Map<String, DublinCore> previousValues = new HashMap<String, DublinCore>();
					previousValues.put("metadata", previousDC);
					
					return validateFormServer(true, null, null, textSearch, supplierSearch, statusSearch, mdFormatSearch, dateStartSearch, dateEndSearch, previousValues);
			}
			
			// Insert the form value in a new metadata record
			tx.insert(metadata)
				.set(metadata.uuid, uuid)
				.set(metadata.location, dc.getLocation())
				.set(metadata.fileId, fileIdValue)
				.set(metadata.title, dc.getTitle())
				.set(metadata.description, dc.getDescription())
				.set(metadata.typeInformation, typeInformationKey)
				.set(metadata.creator, creatorKey)
				.set(metadata.creatorOther, creatorOtherValue)
				.set(metadata.rights, rightsKey)
				.set(metadata.useLimitation, useLimitationKey)
				.set(metadata.mdFormat, formatKey)
				.set(metadata.source, dc.getSource())
				.set(metadata.dateSourceCreation, dateSourceCreationValue)
				.set(metadata.dateSourcePublication, dateSourcePublicationValue)
				.set(metadata.dateSourceValidFrom, dateSourceValidFromValue)
				.set(metadata.dateSourceValidUntil, dateSourceValidUntilValue)
				.set(metadata.supplier, supplierId)
				.set(metadata.status, 2)
				.set(metadata.lastRevisionUser, session("username"))
				.set(metadata.lastRevisionDate, new Timestamp(new Date().getTime()))
				.execute();
			
			// Fetches the metadata id of the new metadata record
			Integer metadataId = tx.select(metadata.id)
					.from(metadata)
					.where(metadata.uuid.eq(uuid))
					.fetchOne();
			
			// Get attachments
			play.mvc.Http.MultipartFormData body = request().body().asMultipartFormData();
			List<FilePart> allFiles = body.getFiles();
			
			// Insert every attachment individually
			for(FilePart fp: allFiles) {
				if(fp != null) {
					java.io.File file = fp.getFile();
					InputStream inputStream = new FileInputStream(file);
					
					byte[] buffer = new byte[8192];
					int bytesRead;
					ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
					while((bytesRead = inputStream.read(buffer)) != -1) {
						byteOutput.write(buffer, 0, bytesRead);
					}
					byte[] input = byteOutput.toByteArray();
					
					inputStream.close();
					
					// Check if attachmentname already exists in the database
					Long countDuplicate = tx.select(mdAttachment.attachmentName)
						.from(mdAttachment)
						.where(mdAttachment.attachmentName.eq(fp.getFilename()))
						.where(mdAttachment.metadataId.eq(metadataId))
						.fetchCount();
					
					Integer countDuplicateInt = countDuplicate.intValue();
					
					// Only insert attachment if attachmentname doesn't exist yet in the database
					if(countDuplicateInt.equals(0)) {
						tx.insert(mdAttachment)
							.set(mdAttachment.metadataId, metadataId)
							.set(mdAttachment.attachmentName, fp.getFilename())
							.set(mdAttachment.attachmentContent, input)
							.set(mdAttachment.attachmentMimetype, fp.getContentType())
							.set(mdAttachment.attachmentLength, input.length)
							.execute();
					} else {
						flash("attachmentSkipped", Messages.get("index.warning.attachment.skipped"));
					}
				}
			}
			
			// Insert every subject individually
			if(dc.getSubject() != null) {
				for(String subjectStr : dc.getSubject()) {
					Integer subjectKey = tx.select(subject.id)
						.from(subject)
						.where(subject.name.eq(subjectStr))
						.fetchOne();
					
					tx.insert(mdSubject)
						.set(mdSubject.metadataId, metadataId)
						.set(mdSubject.subject, subjectKey)
						.execute();
				}
			}
			
			// Refresh materialized view
			tx.refreshMaterializedViewConcurrently(metadataSearch);
			
			// Return index page
			return redirect(controllers.routes.Index.index(textSearch, supplierSearch, statusSearch, mdFormatSearch, dateStartSearch, dateEndSearch, "dateDesc", ""));
		});
	}
	
	/**
	 * Renders the form of an existing metadata record
	 * 
	 * @param metadataUuid the UUID of the metadata
	 * @param textSearch the search value of the text field
	 * @param supplierSearch the search value of the supplier field
	 * @param statusSearch the search value of the status field
	 * @param mdFormatSearch the search value of the format field
	 * @param dateStartSearch the search value of the date start field
	 * @param dateEndSearch the search value of the date end field
	 * @return the {@link Result} of the index page
	 */
	public Result renderEditForm(String metadataUuid, String textSearch, String supplierSearch, String statusSearch, 
			String mdFormatSearch, String dateStartSearch, String dateEndSearch) {
		// Create boolean that determines if the form is for an existing record
		Boolean create = false;
		
		// Create search object
		Search search = new Search(textSearch, supplierSearch, statusSearch, mdFormatSearch, dateStartSearch, dateEndSearch);
		
		return q.withTransaction(tx -> {
			// Fetches status id of metadata
			Integer statusId = tx.from(metadata)
				.select(metadata.status)
				.where(metadata.uuid.eq(metadataUuid))
				.fetchOne();
			
			// If status is published return unauthorized
			if(statusId.equals(4)) {
				return status(UNAUTHORIZED, Messages.get("unauthorized"));
			}
			
			// Fetches the metadata id
			Integer metadataId = tx.from(metadata)
				.select(metadata.id)
				.where(metadata.uuid.eq(metadataUuid))
				.fetchOne();
			
			// Fetches the metadata record of the form
			Tuple datasetRow = tx.select(metadata.id, metadata.uuid, metadata.location, metadata.fileId, metadata.title, 
					metadata.description, metadata.typeInformation, metadata.creator, metadata.creatorOther, metadata.rights, metadata.useLimitation,
					metadata.mdFormat, metadata.source, metadata.dateSourceCreation, metadata.dateSourcePublication, metadata.dateSourceValidFrom, 
					metadata.dateSourceValidUntil, creator.name)
				.from(metadata)
				.join(creator).on(metadata.creator.eq(creator.id))
				.where(metadata.id.eq(metadataId))
				.fetchOne();
			
			// Fetches the subjects of the form
			List<Tuple> subjectsDataset = tx.select(mdSubject.all())
				.from(mdSubject)
				.where(mdSubject.metadataId.eq(metadataId))
				.fetch();
			
			// Fetches the attachments of the form
			List<Tuple> attachmentsDataset = tx.select(mdAttachment.id, mdAttachment.metadataId, mdAttachment.attachmentName, 
					mdAttachment.attachmentMimetype, mdAttachment.attachmentLength)
				.from(mdAttachment)
				.where(mdAttachment.metadataId.eq(metadataId))
				.orderBy(mdAttachment.attachmentName.asc())
				.fetch();
			
			// Fetches type information list
			List<Tuple> typeInformationList = tx.select(typeInformation.id, typeInformation.name, typeInformationLabel.label)
				.from(typeInformation)
				.join(typeInformationLabel).on(typeInformation.id.eq(typeInformationLabel.typeInformationId))
				.orderBy(typeInformationLabel.label.asc())
				.fetch();
			
			// Fetches creator list
			List<Tuple> creatorsList = tx.select(creator.id, creator.name, creatorLabel.label)
				.from(creator)
				.join(creatorLabel).on(creator.id.eq(creatorLabel.creatorId))
				.orderBy(creator.id.asc())
				.fetch();
			
			// Fetches rights list
			List<Tuple> rightsList = tx.select(rights.id, rights.name, rightsLabel.label)
				.from(rights)
				.join(rightsLabel).on(rights.id.eq(rightsLabel.rightsId))
				.orderBy(rightsLabel.label.asc())
				.fetch();
			
			// Fetches use limitation list
			List<Tuple> useLimitationList = tx.select(useLimitation.id, useLimitation.name, useLimitationLabel.label)
				.from(useLimitation)
				.join(useLimitationLabel).on(useLimitation.id.eq(useLimitationLabel.useLimitationId))
				.orderBy(useLimitationLabel.label.asc())
				.fetch();
			
			// Fetches format list
			List<Tuple> mdFormatList = tx.select(mdFormat.id, mdFormat.name, mdFormatLabel.label)
				.from(mdFormat)
				.join(mdFormatLabel).on(mdFormat.id.eq(mdFormatLabel.mdFormatId))
				.orderBy(mdFormatLabel.label.asc())
				.fetch();
			
			// Fetches subject list
			List<Tuple> subjectList = tx.select(subject.id, subject.name, subjectLabel.label)
				.from(subject)
				.join(subjectLabel).on(subject.id.eq(subjectLabel.subjectId))
				.fetch();
			
			// Create SimpleDateFormat in yyyy-MM-dd and dd-MM-yyyy format
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			
			// Create DecimalFormat with two decimals
			DecimalFormat df = new DecimalFormat("0.##");
			
			// Fetches the role of the logged in user
			Integer roleId = tx.select(user.roleId)
					.from(user)
					.where(user.username.equalsIgnoreCase(session("username")))
					.fetchOne();
			
			// Return form page
			return ok(views.html.form.render(create, "", datasetRow, subjectsDataset, attachmentsDataset, typeInformationList, creatorsList, 
				rightsList, useLimitationList, mdFormatList, sdf, subjectList, roleId, search, false, null, df));
		});
	}
	
	/**
	 * Handles the updating of an existing metadata record
	 * 
	 * @param metadataUuid the UUID of the metadata
	 * @param textSearch the search value of the text field
	 * @param supplierSearch the search value of the supplier field
	 * @param statusSearch the search value of the status field
	 * @param mdFormatSearch the search value of the format field
	 * @param dateStartSearch the search value of the date start field
	 * @param dateEndSearch the search value of the date end field
	 * @return the {@link Result} of the index page
	 * @throws IOException
	 */
	public Result editSubmit(String metadataUuid, String textSearch, String supplierSearch, String statusSearch, String mdFormatSearch, 
			String dateStartSearch, String dateEndSearch) throws IOException {
		// Fetches the form
		Form<DublinCore> dcForm = Form.form(DublinCore.class);
		DublinCore dc = dcForm.bindFromRequest().get();
		
		return q.withTransaction(tx -> {
			// Fetches the status id of the metadata
			Integer statusId = tx.from(metadata)
				.select(metadata.status)
				.where(metadata.uuid.eq(metadataUuid))
				.fetchOne();
			
			// If metadata is published return unauthorized
			if(statusId.equals(4)) {
				return status(UNAUTHORIZED, Messages.get("unauthorized"));
			}
			
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
			
			// Fetches the supplier id of the metadata
			Integer supplierId = tx.select(metadata.supplier)
				.from(metadata)
				.where(metadata.uuid.eq(metadataUuid))
				.fetchOne();
			
			// If user is a supplier and the metadata doesn't match the user id don't do anything
			if(roleId.equals(2) && !userId.equals(supplierId)) {
				// do nothing
			} else {
				// Fetches the metadata id
				Integer metadataId = tx.select(metadata.id)
					.from(metadata)
					.where(metadata.uuid.eq(metadataUuid))
					.fetchOne();
				
				// Fetches the type information key according to form value
				Integer typeInformationKey = tx.select(typeInformation.id)
					.from(typeInformation)
					.where(typeInformation.name.eq(dc.getTypeInformation()))
					.fetchOne();
				
				// Fetches the creator key according to form value
				Integer creatorKey = tx.select(creator.id)
					.from(creator)
					.where(creator.name.eq(dc.getCreator()))
					.fetchOne();
				
				// Fetches the rights key according to form value
				Integer rightsKey = tx.select(rights.id)
					.from(rights)
					.where(rights.name.eq(dc.getRights()))
					.fetchOne();
				
				// Fetches the use limitation key according to form value
				Integer useLimitationKey = tx.select(useLimitation.id)
					.from(useLimitation)
					.where(useLimitation.name.eq(dc.getUseLimitation()))
					.fetchOne();
				
				// Fetches the format key according to form valuew
				Integer formatKey = tx.select(mdFormat.id)
					.from(mdFormat)
					.where(mdFormat.name.eq(dc.getMdFormat()))
					.fetchOne();
				
				// Sets file id value to null if string is empty
				String fileIdValue;
				if("".equals(dc.getFileId().trim())) {
					fileIdValue = null;
				} else {
					fileIdValue = dc.getFileId();
				}
				
				// If creator value is other set creator other field
				String creatorOtherValue;
				if(!"other".equals(dc.getCreator())) {
					creatorOtherValue = null;
				} else {
					creatorOtherValue = dc.getCreatorOther();
				}
				
				// Check if dates are null
				Timestamp dateSourceCreationValue = nullCheckDate(dc.getDateSourceCreation());
				Timestamp dateSourcePublicationValue = nullCheckDate(dc.getDateSourcePublication());
				Timestamp dateSourceValidFromValue = nullCheckDate(dc.getDateSourceValidFrom());
				Timestamp dateSourceValidUntilValue = nullCheckDate(dc.getDateSourceValidUntil());
				
				// Fetch the submitted subjects of the form
				List<String> subjects = dc.getSubject();
				
				// Check if creator other isn't empty if creator is other
				Boolean creatorOtherFailed = false;
				if(creatorKey != null) {
					if(creatorKey.equals(9) && "".equals(dc.getCreatorOther().trim())) {
						creatorOtherFailed = true;
					} else {
						creatorOtherFailed = false;
					}
				}
				
				Boolean dateCreatePublicationCheck = logicCheckDate(dc.getDateSourceCreation(), dc.getDateSourcePublication());
				Boolean dateValidCheck = logicCheckDate(dc.getDateSourceValidFrom(), dc.getDateSourceValidUntil());
				
				// Checks if every mandatory field has been completed, if not return the form with previous state
				if("".equals(dc.getTitle().trim()) || "".equals(dc.getDescription().trim()) || "".equals(dc.getLocation().trim()) || 
					"".equals(dc.getFileId().trim()) || creatorKey == null || creatorOtherFailed || useLimitationKey == null || 
					dateSourceCreationValue == null || dc.getSubject() == null || !dateCreatePublicationCheck || !dateValidCheck) {
						
						Tuple datasetRow = tx.select(metadata.id, metadata.uuid, metadata.location, metadata.fileId, metadata.title, 
								metadata.description, metadata.typeInformation, metadata.creator, metadata.creatorOther, metadata.rights, 
								metadata.useLimitation, metadata.mdFormat, metadata.source, metadata.dateSourceCreation, 
								metadata.dateSourcePublication, metadata.dateSourceValidFrom, metadata.dateSourceValidUntil, creator.name)
							.from(metadata)
							.join(creator).on(metadata.creator.eq(creator.id))
							.where(metadata.id.eq(metadataId))
							.fetchOne();
						
						SQLQuery<Tuple> attachmentQuery = tx.select(mdAttachment.all())
							.from(mdAttachment)
							.where(mdAttachment.metadataId.eq(metadataId));
						
						if(dc.getDeletedAttachment() != null) {
							attachmentQuery
								.where(mdAttachment.attachmentName.notIn(dc.getDeletedAttachment()));
						}
							
						List<Tuple> attachmentsDataset = attachmentQuery
							.orderBy(mdAttachment.attachmentName.asc())
							.fetch();
						
						DublinCore previousDC = new DublinCore(dc.getLocation(), dc.getFileId(), dc.getTitle(), dc.getDescription(), dc.getTypeInformation(),
							dc.getCreator(), dc.getCreatorOther(), dc.getRights(), dc.getUseLimitation(), dc.getMdFormat(), dc.getSource(),
							dc.getDateSourceCreation(), dc.getDateSourcePublication(), dc.getDateSourceValidFrom(), dc.getDateSourceValidUntil(), 
							dc.getSubject(), dc.getDeletedAttachment());
						
						Map<String, DublinCore> previousValues = new HashMap<String, DublinCore>();
						previousValues.put("metadata", previousDC);
						
						return validateFormServer(false, datasetRow, attachmentsDataset, textSearch, supplierSearch, statusSearch, mdFormatSearch, 
							dateStartSearch, dateEndSearch, previousValues);
				}
				
				// Update metadata record
				Long metadataCount = tx.update(metadata)
					.where(metadata.uuid.eq(metadataUuid))
					.set(metadata.location, dc.getLocation())
					.set(metadata.fileId, fileIdValue)
					.set(metadata.title, dc.getTitle())
					.set(metadata.description, dc.getDescription())
					.set(metadata.typeInformation, typeInformationKey)
					.set(metadata.creator, creatorKey)
					.set(metadata.creatorOther, creatorOtherValue)
					.set(metadata.rights, rightsKey)
					.set(metadata.useLimitation, useLimitationKey)
					.set(metadata.mdFormat, formatKey)
					.set(metadata.source, dc.getSource())
					.set(metadata.dateSourceCreation, dateSourceCreationValue)
					.set(metadata.dateSourcePublication, dateSourcePublicationValue)
					.set(metadata.dateSourceValidFrom, dateSourceValidFromValue)
					.set(metadata.dateSourceValidUntil, dateSourceValidUntilValue)
					.set(metadata.lastRevisionUser, session("username"))
					.set(metadata.lastRevisionDate, new Timestamp(new Date().getTime()))
					.execute();
				
				// Check if the count of the updated record is what is expected
				Integer metadataFinalCount = metadataCount.intValue();
				if(!metadataFinalCount.equals(1)) {
					throw new Exception("Updating metadata: different amount of affected rows than expected");
				}
				
				List<String> attToDelete = dc.getDeletedAttachment();
				Integer attachmentsCount = 0;
				if(attToDelete != null) {
					for(String attachmentName : attToDelete) {
						// Delete attachment
						tx.delete(mdAttachment)
							.where(mdAttachment.metadataId.eq(metadataId)
								.and(mdAttachment.attachmentName.eq(attachmentName)))
							.execute();
						
						// Remember how many attachments have been deleted
						attachmentsCount++;
					}
					
					// Check if the count of the deleted attachments is what is expected
					if(!attachmentsCount.equals(attToDelete.size())) {
						throw new Exception("Deleting attachments: different amount of affected rows than expected");
					}
				}
				
				// Get new attachments
				play.mvc.Http.MultipartFormData body = request().body().asMultipartFormData();
				List<FilePart> allFiles = body.getFiles();
				
				// Insert every new attachment individually
				for(FilePart fp: allFiles) {
					if(fp != null) {
						java.io.File file = fp.getFile();
						InputStream inputStream = new FileInputStream(file);
						
						byte[] buffer = new byte[8192];
						int bytesRead;
						ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
						while((bytesRead = inputStream.read(buffer)) != -1)
						{
							byteOutput.write(buffer, 0, bytesRead);
						}
						byte[] input = byteOutput.toByteArray();
						
						
						inputStream.close();
						
						// Check if attachmentname already exists in the database
						Long countDuplicate = tx.select(mdAttachment.attachmentName)
							.from(mdAttachment)
							.where(mdAttachment.attachmentName.eq(fp.getFilename()))
							.where(mdAttachment.metadataId.eq(metadataId))
							.fetchCount();
						
						Integer countDuplicateInt = countDuplicate.intValue();
						
						// Only insert attachment if attachmentname doesn't exist yet in the database
						if(countDuplicateInt.equals(0)) {
							tx.insert(mdAttachment)
							.set(mdAttachment.metadataId, metadataId)
							.set(mdAttachment.attachmentName, fp.getFilename())
							.set(mdAttachment.attachmentContent, input)
							.set(mdAttachment.attachmentMimetype, fp.getContentType())
							.set(mdAttachment.attachmentLength, input.length)
							.execute();
						} else {
							flash("attachmentSkipped", Messages.get("index.warning.attachment.skipped"));
						}
					}
				}
				
				// Delete old subjects and insert new subjects
				if(subjects != null) {
					// Fetch the old subjects
					List<Integer> existingSubjects = tx.select(mdSubject.id)
						.from(mdSubject)
						.where(mdSubject.metadataId.eq(metadataId))
						.fetch();
					
					// Delete all old subjects
					Long subjectsCount = tx.delete(mdSubject)
						.where(mdSubject.metadataId.eq(metadataId))
						.execute();
					
					// Check if the count of deleted subjects is what is expected
					Integer subjectsFinalCount = subjectsCount.intValue();
					if(!subjectsFinalCount.equals(existingSubjects.size())) {
						throw new Exception("Updating subjects: different amount of affected rows than expected");
					}
					
					// Insert the new subjects
					for(String subjectStr : subjects) {
						Integer subjectKey = tx.select(subject.id)
							.from(subject)
							.where(subject.name.eq(subjectStr))
							.fetchOne();
						
						tx.insert(mdSubject)
							.set(mdSubject.metadataId, metadataId)
							.set(mdSubject.subject, subjectKey)
							.execute();
					}
				}
			}
			
			// Refresh the materialized view
			tx.refreshMaterializedViewConcurrently(metadataSearch);
			
			// Return the index page
			return redirect(controllers.routes.Index.index(textSearch, supplierSearch, statusSearch, mdFormatSearch, dateStartSearch, dateEndSearch, "dateDesc", ""));
		});
	}
	
	public Result getMetadata(String uuid) throws MalformedURLException, IOException {
		Html h = new DublinCoreMetadata(q).getMetadataInternal(uuid + ".xml");
		
		return ok(h).as("application/xml");
	}
	
	/**
	 * Validate the form on the client side
	 * 
	 * @param metadataUuid the UUID of the metadata
	 * @return the {@link Result} of the error messages
	 */
	public Result validateForm(String metadataUuid) {
		System.out.println("VALIDATE client");
		try {
			// Fetches the form fields
			DynamicForm requestData = Form.form().bindFromRequest();
			String dateCreate = requestData.get("dateSourceCreation");
			String datePublication = requestData.get("dateSourcePublication");
			String dateValidFrom = requestData.get("dateSourceValidFrom");
			String dateValidUntil = requestData.get("dateSourceValidUntil");
			
			// Validate the dates
			Boolean dateCreateReturn = validateDate(dateCreate);
			Boolean datePublicationReturn = validateDate(datePublication);
			Boolean dateValidFromReturn = validateDate(dateValidFrom);
			Boolean dateValidUntilReturn = validateDate(dateValidUntil);
			
			// Check if one or more dates couldn't be parsed, if so return an error message 
			if(!dateCreateReturn || !datePublicationReturn || !dateValidFromReturn || !dateValidUntilReturn) {
				String dateCreateMsg = null;
				String datePublicationMsg = null;
				String dateValidFromMsg = null;
				String dateValidUntilMsg = null;
				
				if(!dateCreateReturn) {
					dateCreateMsg = Messages.get("validate.form.parse.date.create");
				} else {
					dateCreateMsg = null;
				}
				
				if(!datePublicationReturn) {
					datePublicationMsg = Messages.get("validate.form.parse.date.publication");
				} else {
					datePublicationMsg = null;
				}
				
				if(!dateValidFromReturn) {
					dateValidFromMsg = Messages.get("validate.form.parse.date.valid.start");
				} else {
					dateValidFromMsg = null;
				}
				
				if(!dateValidUntilReturn) {
					dateValidUntilMsg = Messages.get("validate.form.parse.date.valid.end");
				} else {
					dateValidUntilMsg = null;
				}
				
				return ok(bindingerror.render(null, dateCreateMsg, datePublicationMsg, dateValidFromMsg, dateValidUntilMsg, null, null));
			}
			
			// Fetches the form
			Form<DublinCore> dcForm = Form.form(DublinCore.class);
			DublinCore dc = dcForm.bindFromRequest().get();
			
			// If title is empty set to null (which will generate an error message)
			String title = null;
			if("".equals(dc.getTitle().trim())) {
				title = null;
			} else {
				title = dc.getTitle();
			}
			
			// If description is empty set to null (which will generate an error message)
			String description = null;
			if("".equals(dc.getDescription().trim())) {
				description = null;
			} else {
				description = dc.getDescription();
			}
			
			// If location is empty set to null (which will generate an error message)
			String location = null;
			if("".equals(dc.getLocation().trim())) {
				location = null;
			} else {
				location = dc.getLocation();
			}
			
			Boolean fileIdDuplicate = false;
			Boolean fileIdCharacter = false;
			Boolean fileIdLength = false;
			
			// If file id is empty set to null (which will generate an error message)
			String fileId = null;
			if("".equals(dc.getFileId().trim())) {
				fileId = null;
			} else {
				fileId = dc.getFileId();
			}
			
			if (fileId != null){
				/*
				 * These checks will result in a warning, but should not block the validation
				 */
				System.out.println("File id: " + fileId);
				
				// check for multiple occurences numbers
				Long fileIdCount = nrOfOccurencesFileId(fileId);
				System.out.println(fileId + " found " + fileIdCount + " times");
				if (fileIdCount > 0){
					fileIdDuplicate = true;
				}
				
				// check for other character than 0123456789
				if (!fileId.matches("\\d+")){
					System.out.println(fileId + " does not contain only 0123456789");
					fileIdCharacter = true;
				}
				
				// check for length < 6
				if (fileId.length() < 6){
					System.out.println(fileId + " length() < 6");
					fileIdLength = true;
				}
			}
			
			// If creator is empty set to null (which will generate an error message)
			String creator = null;
			String creatorOther = null;
			if("".equals(dc.getCreator().trim())) {
				creator = null;
			} else {
				creator = dc.getCreator();
			}
			
			// If creator value is other and creator other value is empty set creator other to null (which will generate an error message)
			if("other".equals(dc.getCreator().trim())) {
				if("".equals(dc.getCreatorOther().trim())) {
					creatorOther = null;
				} else {
					creatorOther = dc.getCreatorOther();
				}
			} else {
				creatorOther = "";
			}
			
			Boolean dateCreatePublicationCheck = logicCheckDate(dc.getDateSourceCreation(), dc.getDateSourcePublication());
			Boolean dateValidCheck = logicCheckDate(dc.getDateSourceValidFrom(), dc.getDateSourceValidUntil());
			
			// Return specific error message view
			return ok(validateform.render(title, description, location, fileId, fileIdDuplicate, fileIdCharacter, fileIdLength, creator, creatorOther, 
					dc.getDateSourceCreation(), dc.getSubject(), dateCreatePublicationCheck, dateValidCheck));
		} catch(IllegalStateException ise) {
			// Return generic error message view
			return ok(bindingerror.render(Messages.get("validate.search.generic"), null, null, null, null, null, null));
		}
	}
	
	/**
	 * Find the nr of occurences of fileId in the metadata table
	 * @param currentFileId field to check
	 * @return the nr of occurences of currentFileId 
	 */
	public Long nrOfOccurencesFileId(final String currentFileId){
		return q.withTransaction(tx -> {
			// count nr of times a certain fileId is found in the whole dataset
			Long fileIdCount = tx.select(metadata.fileId.count())
				.from(metadata)
				.where(metadata.fileId.eq(currentFileId))
				.groupBy(metadata.fileId)
				.fetchOne();
			if (fileIdCount == null){
				fileIdCount = 0L;
			}
			return fileIdCount;
		});
	}
	
	/**
	 * Validation of dates
	 * 
	 * @param date the date as a {@link String} to be validated
	 * @return a {@link Boolean} that signals if validation was successful
	 */
	public Boolean validateDate(String date) {
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
			// If parsing was unsuccessful return false
			return false;
		}
	}
	
	/**
	 * Check if date is null
	 * 
	 * @param date the {@link Date} to be checked
	 * @return the {@link Timestamp} of the date
	 */
	public Timestamp nullCheckDate(Date date) {
		Timestamp timestamp;
		
		if(date == null) {
			timestamp = null;
		} else {
			timestamp = new Timestamp(date.getTime());
		}
		
		return timestamp;
	}
	
	public Boolean logicCheckDate(Date dateFirst, Date dateSecond) {
		Boolean b = true;
		if(dateFirst != null && dateSecond != null) {
			if(dateFirst.after(dateSecond)) {
				b = false;
			}
		}
		
		return b;
	}
	
	/**
	 * Validation on the server side
	 * 
	 * @param create indicates if the form is for a new or existing record
	 * @param datasetRow the record if an existing record is being validated
	 * @param attachmentsDataset the attachments of the record if an existing record is being validated
	 * @param textSearch the search value of the text field
	 * @param supplierSearch the search value of the supplier field
	 * @param statusSearch the search value of the status field
	 * @param mdFormatSearch the search value of the format field
	 * @param dateStartSearch the search value of the date start field
	 * @param dateEndSearch the search value of the date end field
	 * @param previousValues the previous values of the form
	 * @return the {@link Result} of the form page
	 */
	public Result validateFormServer(Boolean create, Tuple datasetRow, List<Tuple> attachmentsDataset, String textSearch, String supplierSearch, 
			String statusSearch, String mdFormatSearch, String dateStartSearch, String dateEndSearch, Map<String, DublinCore> previousValues) {
		// Create strings according to yyyy-MM-dd and dd-MM-yyyy formats
		String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date().getTime());
		
		// Create SimpleDateFormat objects according to yyyy-MM-dd and dd-MM-yyyy formats
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		// Create boolean indicating that the resulting values originates from a validation
		Boolean validate = true;
		
		// Create a search object
		Search search = new Search(textSearch, supplierSearch, statusSearch, mdFormatSearch, dateStartSearch, dateEndSearch);
		
		return q.withTransaction(tx -> {
			// Fetches the type information list
			List<Tuple> typeInformationList = tx.select(typeInformation.id, typeInformation.name, typeInformationLabel.label)
				.from(typeInformation)
				.join(typeInformationLabel).on(typeInformation.id.eq(typeInformationLabel.typeInformationId))
				.orderBy(typeInformationLabel.label.asc())
				.fetch();
			
			// Fetches the creator list
			List<Tuple> creatorsList = tx.select(creator.id, creator.name, creatorLabel.label)
				.from(creator)
				.join(creatorLabel).on(creator.id.eq(creatorLabel.creatorId))
				.orderBy(creator.id.asc())
				.fetch();
			
			// Fetches the rights list
			List<Tuple> rightsList = tx.select(rights.id, rights.name, rightsLabel.label)
				.from(rights)
				.join(rightsLabel).on(rights.id.eq(rightsLabel.rightsId))
				.orderBy(rightsLabel.label.asc())
				.fetch();
			
			// Fetches the use limitation list
			List<Tuple> useLimitationList = tx.select(useLimitation.id, useLimitation.name, useLimitationLabel.label)
				.from(useLimitation)
				.join(useLimitationLabel).on(useLimitation.id.eq(useLimitationLabel.useLimitationId))
				.orderBy(useLimitationLabel.label.asc())
				.fetch();
			
			// Fetches the format list
			List<Tuple> mdFormatList = tx.select(mdFormat.id, mdFormat.name, mdFormatLabel.label)
				.from(mdFormat)
				.join(mdFormatLabel).on(mdFormat.id.eq(mdFormatLabel.mdFormatId))
				.orderBy(mdFormatLabel.label.asc())
				.fetch();
			
			// Fetches the subject list
			List<Tuple> subjectList = tx.select(subject.id, subject.name, subjectLabel.label)
				.from(subject)
				.join(subjectLabel).on(subject.id.eq(subjectLabel.subjectId))
				.fetch();
			
			// Fetches the role of the logged in user
			Integer roleId = tx.select(user.roleId)
					.from(user)
					.where(user.username.equalsIgnoreCase(session("username")))
					.fetchOne();
			
			// Create DecimalFormat with two decimals
			DecimalFormat df = new DecimalFormat("0.##");
			
			// Return form page
			return ok(views.html.form.render(create, today, datasetRow, null, attachmentsDataset, typeInformationList, creatorsList, rightsList, 
					useLimitationList, mdFormatList, sdf, subjectList, roleId, search, validate, previousValues, df));
		});
	}
	
	/**
	 * Cancels the creation or updating of a form
	 * 
	 * @param textSearch the search value of the text field
	 * @param supplierSearch the search value of the supplier field
	 * @param statusSearch the search value of the status field
	 * @param mdFormatSearch the search value of the format field
	 * @param dateStartSearch the search value of the date start field
	 * @param dateEndSearch the search value of the date end field
	 * @return the {@link Result} of the index page
	 */
	public Result cancel(String textSearch, String supplierSearch, String statusSearch, String mdFormatSearch, String dateStartSearch, 
			String dateEndSearch) {
		return redirect(controllers.routes.Index.index(textSearch, supplierSearch, statusSearch, mdFormatSearch, dateStartSearch, dateEndSearch, "dateDesc", ""));
	}
}