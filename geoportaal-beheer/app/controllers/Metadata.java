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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
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

import models.DublinCore;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Http.Response;
import play.mvc.Result;
import util.QueryDSL;
import views.html.*;

public class Metadata extends Controller {
	@Inject QueryDSL q;
	
	public Result renderCreateForm(String textSearch, String supplierSearch, String statusSearch, 
			String mdFormatSearch, String dateStartSearch, String dateEndSearch) {
		Boolean create = true;
		String todayUS = new SimpleDateFormat("yyyy-MM-dd").format(new Date().getTime());
		String todayLocal = new SimpleDateFormat("dd-MM-yyyy").format(new Date().getTime());
		
		return q.withTransaction(tx -> {
			List<Tuple> typeInformationList = tx.select(typeInformation.id, typeInformation.name, typeInformationLabel.label)
			.from(typeInformation)
				.join(typeInformationLabel).on(typeInformation.id.eq(typeInformationLabel.typeInformationId))
				.fetch();
			
			List<Tuple> creatorsList = tx.select(creator.id, creator.name, creatorLabel.label)
				.from(creator)
				.join(creatorLabel).on(creator.id.eq(creatorLabel.creatorId))
				.fetch();
			
			List<Tuple> rightsList = tx.select(rights.id, rights.name, rightsLabel.label)
				.from(rights)
				.join(rightsLabel).on(rights.id.eq(rightsLabel.rightsId))
				.fetch();
			
			List<Tuple> useLimitationList = tx.select(useLimitation.id, useLimitation.name, useLimitationLabel.label)
				.from(useLimitation)
				.join(useLimitationLabel).on(useLimitation.id.eq(useLimitationLabel.useLimitationId))
				.fetch();
			
			List<Tuple> mdFormatList = tx.select(mdFormat.id, mdFormat.name, mdFormatLabel.label)
				.from(mdFormat)
				.join(mdFormatLabel).on(mdFormat.id.eq(mdFormatLabel.mdFormatId))
				.fetch();
			
			List<Tuple> subjectList = tx.select(subject.id, subject.name, subjectLabel.label)
				.from(subject)
				.join(subjectLabel).on(subject.id.eq(subjectLabel.subjectId))
				.fetch();
			
			return ok(views.html.form.render(create, todayUS, todayLocal, null, null, null, typeInformationList, creatorsList, rightsList, 
					useLimitationList, mdFormatList, null, null, subjectList, textSearch, supplierSearch, statusSearch, mdFormatSearch,
					dateStartSearch, dateEndSearch, false, null));
		});
	}
	
	public Result createSubmit(String textSearch, String supplierSearch, String statusSearch, String mdFormatSearch, 
			String dateStartSearch, String dateEndSearch) throws IOException {
		Form<DublinCore> dcForm = Form.form(DublinCore.class);
		DublinCore dc = dcForm.bindFromRequest().get();
		
		String uuid = UUID.randomUUID().toString();
		Timestamp dateToday = new Timestamp(new Date().getTime());
		
		return q.withTransaction(tx -> {
			Integer typeInformationKey = tx.select(typeInformation.id)
				.from(typeInformation)
				.where(typeInformation.name.eq(dc.getTypeInformation()))
				.fetchOne();
			
			Integer creatorKey = tx.select(creator.id)
				.from(creator)
				.where(creator.name.eq(dc.getCreator()))
				.fetchOne();
			
			Integer rightsKey = tx.select(rights.id)
				.from(rights)
				.where(rights.name.eq(dc.getRights()))
				.fetchOne();
			
			Integer useLimitationKey = tx.select(useLimitation.id)
				.from(useLimitation)
				.where(useLimitation.name.eq(dc.getUseLimitation()))
				.fetchOne();
			
			Integer formatKey = tx.select(mdFormat.id)
				.from(mdFormat)
				.where(mdFormat.name.eq(dc.getMdFormat()))
				.fetchOne();
			
			String fileIdValue;
			if("".equals(dc.getFileId())) {
				fileIdValue = null;
			} else {
				fileIdValue = dc.getFileId();
			}
			
			String creatorOtherValue;
			if(!"other".equals(dc.getCreator())) {
				creatorOtherValue = null;
			} else {
				creatorOtherValue = dc.getCreatorOther();
			}
			
			Timestamp dateSourceCreationValue = nullCheckDate(dc.getDateSourceCreation());
			Timestamp dateSourcePublicationValue = nullCheckDate(dc.getDateSourcePublication());
			Timestamp dateSourceRevisionValue = nullCheckDate(dc.getDateSourceRevision());
			Timestamp dateSourceValidFromValue = nullCheckDate(dc.getDateSourceValidFrom());
			Timestamp dateSourceValidUntilValue = nullCheckDate(dc.getDateSourceValidUntil());
			
			Integer supplierId = tx.select(user.id)
				.from(user)
				.where(user.username.eq(session("username")))
				.fetchOne();
			
			Boolean creatorOtherFailed = false;
			if(creatorKey != null) {
				if(creatorKey.equals(9) && "".equals(dc.getCreatorOther())) {
					creatorOtherFailed = true;
				} else {
					creatorOtherFailed = false;
				}
			}
			
			if("".equals(dc.getTitle()) || "".equals(dc.getDescription()) || "".equals(dc.getLocation()) || "".equals(dc.getFileId()) || 
				creatorKey == null || creatorOtherFailed || useLimitationKey == null || dateSourceCreationValue == null || 
				dc.getSubject() == null) {
					
					DublinCore previousDC = new DublinCore(dc.getLocation(), dc.getFileId(), dc.getTitle(), dc.getDescription(), dc.getTypeInformation(),
						dc.getCreator(), dc.getCreatorOther(), dc.getRights(), dc.getUseLimitation(), dc.getMdFormat(), dc.getSource(),
						dc.getDateSourceCreation(), dc.getDateSourcePublication(), dc.getDateSourceRevision(), dc.getDateSourceValidFrom(),
						dc.getDateSourceValidUntil(), dc.getSubject(), null);
					
					Map<String, DublinCore> previousValues = new HashMap<String, DublinCore>();
					previousValues.put("metadata", previousDC);
					
					return validateFormServer(true, null, null, textSearch, supplierSearch, statusSearch, mdFormatSearch, dateStartSearch, dateEndSearch, previousValues);
			}
			
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
				.set(metadata.dateSourceRevision, dateSourceRevisionValue)
				.set(metadata.dateSourceValidFrom, dateSourceValidFromValue)
				.set(metadata.dateSourceValidUntil, dateSourceValidUntilValue)
				.set(metadata.supplier, supplierId)
				.set(metadata.status, 2)
				.set(metadata.published, false)
				.set(metadata.lastRevisionUser, session("username"))
				.set(metadata.lastRevisionDate, dateToday)
				.execute();
			
			Integer metadataId = tx.select(metadata.id)
					.from(metadata)
					.where(metadata.uuid.eq(uuid))
					.fetchOne();
			
			play.mvc.Http.MultipartFormData body = request().body().asMultipartFormData();
			List<FilePart> allFiles = body.getFiles();
			
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
					
					tx.insert(mdAttachment)
						.set(mdAttachment.metadataId, metadataId)
						.set(mdAttachment.attachmentName, fp.getFilename())
						.set(mdAttachment.attachmentContent, input)
						.set(mdAttachment.attachmentMimetype, fp.getContentType())
						.execute();
				}
			}
			
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
			
			tx.refreshMaterializedViewConcurrently(metadataSearch);
			
			return redirect(controllers.routes.Index.index(textSearch, supplierSearch, statusSearch, mdFormatSearch, dateStartSearch, dateEndSearch, "dateDesc", ""));
		});
	}
	
	public Result renderEditForm(String metadataUuid, String textSearch, String supplierSearch, String statusSearch, 
			String mdFormatSearch, String dateStartSearch, String dateEndSearch) {
		Boolean create = false;
		
		return q.withTransaction(tx -> {
			Integer statusId = tx.from(metadata)
				.select(metadata.status)
				.where(metadata.uuid.eq(metadataUuid))
				.fetchOne();
			
			if(statusId.equals(4)) {
				return status(UNAUTHORIZED, "Geen toegang.");
			}
			
			Integer metadataId = tx.from(metadata)
				.select(metadata.id)
				.where(metadata.uuid.eq(metadataUuid))
				.fetchOne();
			
			Tuple datasetRow = tx.select(metadata.id, metadata.uuid, metadata.location, metadata.fileId, metadata.title, 
					metadata.description, metadata.typeInformation, metadata.creator, metadata.creatorOther, metadata.rights, metadata.useLimitation,
					metadata.mdFormat, metadata.source, metadata.dateSourceCreation, metadata.dateSourcePublication, metadata.dateSourceRevision,
					metadata.dateSourceValidFrom, metadata.dateSourceValidUntil, creator.name)
				.from(metadata)
				.join(creator).on(metadata.creator.eq(creator.id))
				.where(metadata.id.eq(metadataId))
				.fetchOne();
			
			List<Tuple> subjectsDataset = tx.select(mdSubject.all())
				.from(mdSubject)
				.where(mdSubject.metadataId.eq(metadataId))
				.fetch();
			
			List<Tuple> attachmentsDataset = tx.select(mdAttachment.all())
				.from(mdAttachment)
				.where(mdAttachment.metadataId.eq(metadataId))
				.orderBy(mdAttachment.attachmentName.asc())
				.fetch();
			
			List<Tuple> typeInformationList = tx.select(typeInformation.id, typeInformation.name, typeInformationLabel.label)
				.from(typeInformation)
				.join(typeInformationLabel).on(typeInformation.id.eq(typeInformationLabel.typeInformationId))
				.fetch();
				
			List<Tuple> creatorsList = tx.select(creator.id, creator.name, creatorLabel.label)
				.from(creator)
				.join(creatorLabel).on(creator.id.eq(creatorLabel.creatorId))
				.fetch();
			
			List<Tuple> rightsList = tx.select(rights.id, rights.name, rightsLabel.label)
				.from(rights)
				.join(rightsLabel).on(rights.id.eq(rightsLabel.rightsId))
				.fetch();
			
			List<Tuple> useLimitationList = tx.select(useLimitation.id, useLimitation.name, useLimitationLabel.label)
				.from(useLimitation)
				.join(useLimitationLabel).on(useLimitation.id.eq(useLimitationLabel.useLimitationId))
				.fetch();
			
			List<Tuple> mdFormatList = tx.select(mdFormat.id, mdFormat.name, mdFormatLabel.label)
				.from(mdFormat)
				.join(mdFormatLabel).on(mdFormat.id.eq(mdFormatLabel.mdFormatId))
				.fetch();
			
			List<Tuple> subjectList = tx.select(subject.id, subject.name, subjectLabel.label)
				.from(subject)
				.join(subjectLabel).on(subject.id.eq(subjectLabel.subjectId))
				.fetch();
			
			SimpleDateFormat sdfUS = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat sdfLocal = new SimpleDateFormat("dd-MM-yyyy");
			
			return ok(views.html.form.render(create, "", "", datasetRow, subjectsDataset, attachmentsDataset, typeInformationList, creatorsList, 
				rightsList, useLimitationList, mdFormatList, sdfUS, sdfLocal, subjectList, textSearch, supplierSearch, statusSearch, mdFormatSearch,
				dateStartSearch, dateEndSearch, false, null));
		});
	}
	
	public Result editSubmit(String metadataUuid, String textSearch, String supplierSearch, String statusSearch, String mdFormatSearch, 
			String dateStartSearch, String dateEndSearch) throws IOException {
		Form<DublinCore> dcForm = Form.form(DublinCore.class);
		DublinCore dc = dcForm.bindFromRequest().get();
		
		Timestamp dateToday = new Timestamp(new Date().getTime());
		
		return q.withTransaction(tx -> {
			Integer statusId = tx.from(metadata)
				.select(metadata.status)
				.where(metadata.uuid.eq(metadataUuid))
				.fetchOne();
			
			if(statusId.equals(4)) {
				return status(UNAUTHORIZED, "Geen toegang.");
			}
			
			Integer roleId = tx.select(user.roleId)
				.from(user)
				.where(user.username.eq(session("username")))
				.fetchOne();
			
			Integer userId = tx.select(user.id)
				.from(user)
				.where(user.username.eq(session("username")))
				.fetchOne();
			
			Integer supplierId = tx.select(metadata.supplier)
				.from(metadata)
				.where(metadata.uuid.eq(metadataUuid))
				.fetchOne();
			
			if(roleId.equals(2) && !userId.equals(supplierId)) {
				// do nothing
			} else {
				Integer metadataId = tx.select(metadata.id)
					.from(metadata)
					.where(metadata.uuid.eq(metadataUuid))
					.fetchOne();
				
				Integer typeInformationKey = tx.select(typeInformation.id)
					.from(typeInformation)
					.where(typeInformation.name.eq(dc.getTypeInformation()))
					.fetchOne();
				
				Integer creatorKey = tx.select(creator.id)
					.from(creator)
					.where(creator.name.eq(dc.getCreator()))
					.fetchOne();
				
				Integer rightsKey = tx.select(rights.id)
					.from(rights)
					.where(rights.name.eq(dc.getRights()))
					.fetchOne();
				
				Integer useLimitationKey = tx.select(useLimitation.id)
					.from(useLimitation)
					.where(useLimitation.name.eq(dc.getUseLimitation()))
					.fetchOne();
				
				Integer formatKey = tx.select(mdFormat.id)
					.from(mdFormat)
					.where(mdFormat.name.eq(dc.getMdFormat()))
					.fetchOne();
				
				String fileIdValue;
				if("".equals(dc.getFileId())) {
					fileIdValue = null;
				} else {
					fileIdValue = dc.getFileId();
				}
				
				String creatorOtherValue;
				if(!"other".equals(dc.getCreator())) {
					creatorOtherValue = null;
				} else {
					creatorOtherValue = dc.getCreatorOther();
				}
				
				Timestamp dateSourceCreationValue = nullCheckDate(dc.getDateSourceCreation());
				Timestamp dateSourcePublicationValue = nullCheckDate(dc.getDateSourcePublication());
				Timestamp dateSourceRevisionValue = nullCheckDate(dc.getDateSourceRevision());
				Timestamp dateSourceValidFromValue = nullCheckDate(dc.getDateSourceValidFrom());
				Timestamp dateSourceValidUntilValue = nullCheckDate(dc.getDateSourceValidUntil());
				
				List<String> subjects = dc.getSubject();
				
				Boolean creatorOtherFailed = false;
				if(creatorKey != null) {
					if(creatorKey.equals(9) && "".equals(dc.getCreatorOther())) {
						creatorOtherFailed = true;
					} else {
						creatorOtherFailed = false;
					}
				}
				
				if("".equals(dc.getTitle()) || "".equals(dc.getDescription()) || "".equals(dc.getLocation()) || "".equals(dc.getFileId()) || 
					creatorKey == null || creatorOtherFailed || useLimitationKey == null || dateSourceCreationValue == null || 
					dc.getSubject() == null) {
						
						Tuple datasetRow = tx.select(metadata.id, metadata.uuid, metadata.location, metadata.fileId, metadata.title, 
								metadata.description, metadata.typeInformation, metadata.creator, metadata.creatorOther, metadata.rights, metadata.useLimitation,
								metadata.mdFormat, metadata.source, metadata.dateSourceCreation, metadata.dateSourcePublication, metadata.dateSourceRevision,
								metadata.dateSourceValidFrom, metadata.dateSourceValidUntil, creator.name)
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
							dc.getDateSourceCreation(), dc.getDateSourcePublication(), dc.getDateSourceRevision(), dc.getDateSourceValidFrom(),
							dc.getDateSourceValidUntil(), dc.getSubject(), dc.getDeletedAttachment());
						
						Map<String, DublinCore> previousValues = new HashMap<String, DublinCore>();
						previousValues.put("metadata", previousDC);
						
						return validateFormServer(false, datasetRow, attachmentsDataset, textSearch, supplierSearch, statusSearch, mdFormatSearch, dateStartSearch, dateEndSearch, previousValues);
				}
				
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
					.set(metadata.dateSourceRevision, dateSourceRevisionValue)
					.set(metadata.dateSourceValidFrom, dateSourceValidFromValue)
					.set(metadata.dateSourceValidUntil, dateSourceValidUntilValue)
					.set(metadata.lastRevisionUser, session("username"))
					.set(metadata.lastRevisionDate, dateToday)
					.execute();
				
				Integer metadataFinalCount = metadataCount.intValue();
				if(!metadataFinalCount.equals(1)) {
					throw new Exception("Updating metadata: different amount of affected rows than expected");
				}
				
				List<String> attToDelete = dc.getDeletedAttachment();
				Integer attachmentsCount = 0;
				if(attToDelete != null) {
					for(String attachmentName : attToDelete) {
						tx.delete(mdAttachment)
							.where(mdAttachment.metadataId.eq(metadataId)
								.and(mdAttachment.attachmentName.eq(attachmentName)))
							.execute();
						
						attachmentsCount++;
					}
					
					if(!attachmentsCount.equals(attToDelete.size())) {
						throw new Exception("Deleting attachments: different amount of affected rows than expected");
					}
				}
				
				play.mvc.Http.MultipartFormData body = request().body().asMultipartFormData();
				List<FilePart> allFiles = body.getFiles();
				
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
						
						tx.insert(mdAttachment)
							.set(mdAttachment.metadataId, metadataId)
							.set(mdAttachment.attachmentName, fp.getFilename())
							.set(mdAttachment.attachmentContent, input)
							.set(mdAttachment.attachmentMimetype, fp.getContentType())
							.execute();
					}
				}
				
				if(subjects != null) {
					List<Integer> existingSubjects = tx.select(mdSubject.id)
						.from(mdSubject)
						.where(mdSubject.metadataId.eq(metadataId))
						.fetch();
					
					Long subjectsCount = tx.delete(mdSubject)
						.where(mdSubject.metadataId.eq(metadataId))
						.execute();
					
					Integer subjectsFinalCount = subjectsCount.intValue();
					if(!subjectsFinalCount.equals(existingSubjects.size())) {
						throw new Exception("Updating subjects: different amount of affected rows than expected");
					}
						
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
			
			tx.refreshMaterializedViewConcurrently(metadataSearch);
			
			return redirect(controllers.routes.Index.index(textSearch, supplierSearch, statusSearch, mdFormatSearch, dateStartSearch, dateEndSearch, "dateDesc", ""));
		});
	}
	
	public Result openAttachment(String attachmentName, String uuid) {
		
		return q.withTransaction(tx -> {
			Tuple attachment = tx.select(mdAttachment.attachmentContent, mdAttachment.attachmentMimetype)
				.from(mdAttachment)
				.join(metadata).on(mdAttachment.metadataId.eq(metadata.id))
				.where(metadata.uuid.eq(uuid))
				.where(mdAttachment.attachmentName.eq(attachmentName))
				.fetchOne();
			
			response().setContentType(attachment.get(mdAttachment.attachmentMimetype));
			
			byte[] content = attachment.get(mdAttachment.attachmentContent);
			ByteArrayInputStream bais = new ByteArrayInputStream(content);
			
			return ok(bais);
		});
	}
	
	public Result validateForm(String metadataUuid) {
		
		try {
			DynamicForm requestData = Form.form().bindFromRequest();
			String dateCreate = requestData.get("dateSourceCreation");
			String datePublication = requestData.get("dateSourcePublication");
			String dateRevision = requestData.get("dateSourceRevision");
			String dateValidFrom = requestData.get("dateSourceValidFrom");
			String dateValidUntil = requestData.get("dateSourceValidUntil");
			
			Boolean dateCreateReturn = validateDate(dateCreate);
			Boolean datePublicationReturn = validateDate(datePublication);
			Boolean dateRevisionReturn = validateDate(dateRevision);
			Boolean dateValidFromReturn = validateDate(dateValidFrom);
			Boolean dateValidUntilReturn = validateDate(dateValidUntil);
			
			if(!dateCreateReturn || !datePublicationReturn || !dateRevisionReturn || !dateValidFromReturn || !dateValidUntilReturn) {
				String dateCreateMsg = null;
				String datePublicationMsg = null;
				String dateRevisionMsg = null;
				String dateValidFromMsg = null;
				String dateValidUntilMsg = null;
				
				if(!dateCreateReturn) {
					dateCreateMsg = "De datum creatie is niet correct ingevuld.";
				} else {
					dateCreateMsg = null;
				}
				
				if(!datePublicationReturn) {
					datePublicationMsg = "De datum publicatie is niet correct ingevuld.";
				} else {
					datePublicationMsg = null;
				}
				
				if(!dateRevisionReturn) {
					dateRevisionMsg = "De datum mutatie is niet correct ingevuld.";
				} else {
					dateRevisionMsg = null;
				}
				
				if(!dateValidFromReturn) {
					dateValidFromMsg = "De datum geldig, van is niet correct ingevuld.";
				} else {
					dateValidFromMsg = null;
				}
				
				if(!dateValidUntilReturn) {
					dateValidUntilMsg = "De datum geldig, tot is niet correct ingevuld.";
				} else {
					dateValidUntilMsg = null;
				}
				
				return ok(bindingerror.render(null, dateCreateMsg, datePublicationMsg, dateRevisionMsg, dateValidFromMsg, dateValidUntilMsg, null, null));
				
			}
			
			Form<DublinCore> dcForm = Form.form(DublinCore.class);
			DublinCore dc = dcForm.bindFromRequest().get();
			
			String title = null;
			if("".equals(dc.getTitle().trim())) {
				title = null;
			} else {
				title = dc.getTitle();
			}
			
			String description = null;
			if("".equals(dc.getDescription().trim())) {
				description = null;
			} else {
				description = dc.getDescription();
			}
			
			String location = null;
			if("".equals(dc.getLocation().trim())) {
				location = null;
			} else {
				location = dc.getLocation();
			}
			
			String fileId = null;
			if("".equals(dc.getFileId().trim())) {
				fileId = null;
			} else {
				fileId = dc.getFileId();
			}
			
			String creator = null;
			String creatorOther = null;
			if("".equals(dc.getCreator().trim())) {
				creator = null;
			} else {
				creator = dc.getCreator();
			}
			
			if("other".equals(dc.getCreator().trim())) {
				if("".equals(dc.getCreatorOther().trim())) {
					creatorOther = null;
				} else {
					creatorOther = dc.getCreatorOther();
				}
			} else {
				creatorOther = "";
			}
			
			return ok(validateform.render(title, description, location, fileId, creator, creatorOther, dc.getDateSourceCreation(), dc.getSubject()));
		} catch(IllegalStateException ise) {
			return ok(bindingerror.render("Er is iets misgegaan. Controleer of de velden correct zijn ingevuld.", null, null, null, null, null, null, null));
		}
	}
	
	public Boolean validateDate(String date) {
		if("".equals(date)) {
			return true;
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		sdf.setLenient(false);
		try {
			sdf.parse(date);
			return true;
		} catch(ParseException pe) {
			return false;
		}
	}
	
	public Timestamp nullCheckDate(Date date) {
		Timestamp timestamp;
		
		if(date == null) {
			timestamp = null;
		} else {
			timestamp = new Timestamp(date.getTime());
		}
		
		return timestamp;
	}
	
	public Result validateFormServer(Boolean create, Tuple datasetRow, List<Tuple> attachmentsDataset, String textSearch, String supplierSearch, 
			String statusSearch, String mdFormatSearch, String dateStartSearch, String dateEndSearch, Map<String, DublinCore> previousValues) {
		String todayUS = new SimpleDateFormat("yyyy-MM-dd").format(new Date().getTime());
		String todayLocal = new SimpleDateFormat("dd-MM-yyyy").format(new Date().getTime());
		
		SimpleDateFormat sdfUS = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdfLocal = new SimpleDateFormat("dd-MM-yyyy");
		
		Boolean validate = true;
		
		return q.withTransaction(tx -> {
			List<Tuple> typeInformationList = tx.select(typeInformation.id, typeInformation.name, typeInformationLabel.label)
			.from(typeInformation)
				.join(typeInformationLabel).on(typeInformation.id.eq(typeInformationLabel.typeInformationId))
				.fetch();
			
			List<Tuple> creatorsList = tx.select(creator.id, creator.name, creatorLabel.label)
				.from(creator)
				.join(creatorLabel).on(creator.id.eq(creatorLabel.creatorId))
				.fetch();
			
			List<Tuple> rightsList = tx.select(rights.id, rights.name, rightsLabel.label)
				.from(rights)
				.join(rightsLabel).on(rights.id.eq(rightsLabel.rightsId))
				.fetch();
			
			List<Tuple> useLimitationList = tx.select(useLimitation.id, useLimitation.name, useLimitationLabel.label)
				.from(useLimitation)
				.join(useLimitationLabel).on(useLimitation.id.eq(useLimitationLabel.useLimitationId))
				.fetch();
			
			List<Tuple> mdFormatList = tx.select(mdFormat.id, mdFormat.name, mdFormatLabel.label)
				.from(mdFormat)
				.join(mdFormatLabel).on(mdFormat.id.eq(mdFormatLabel.mdFormatId))
				.fetch();
			
			List<Tuple> subjectList = tx.select(subject.id, subject.name, subjectLabel.label)
				.from(subject)
				.join(subjectLabel).on(subject.id.eq(subjectLabel.subjectId))
				.fetch();
			
			return ok(views.html.form.render(create, todayUS, todayLocal, datasetRow, null, attachmentsDataset, typeInformationList, creatorsList, rightsList, 
					useLimitationList, mdFormatList, sdfUS, sdfLocal, subjectList, textSearch, supplierSearch, statusSearch, mdFormatSearch,
					dateStartSearch, dateEndSearch, validate, previousValues));
		});
	}
	
	public Result cancel(String textSearch, String supplierSearch, String statusSearch, String mdFormatSearch, String dateStartSearch, 
			String dateEndSearch) {
		return redirect(controllers.routes.Index.index(textSearch, supplierSearch, statusSearch, mdFormatSearch, dateStartSearch, dateEndSearch, "dateDesc", ""));
	}
}