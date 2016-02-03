package controllers;

import static models.QCreator.creator;
import static models.QCreatorLabel.creatorLabel;
import static models.QMdAttachment.mdAttachment;
import static models.QMdFormat.mdFormat;
import static models.QMdFormatLabel.mdFormatLabel;
import static models.QMdSubject.mdSubject;
import static models.QMetadata.metadata;
import static models.QRights.rights;
import static models.QRightsLabel.rightsLabel;
import static models.QSubject.subject;
import static models.QSubjectLabel.subjectLabel;
import static models.QSupplier.supplier;
import static models.QTypeInformation.typeInformation;
import static models.QTypeInformationLabel.typeInformationLabel;
import static models.QUseLimitation.useLimitation;
import static models.QUseLimitationLabel.useLimitationLabel;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import com.querydsl.core.Tuple;

import models.DublinCore;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import util.QueryDSL;
import views.html.*;

public class Metadata extends Controller {
	@Inject QueryDSL q;
	
	public Result renderCreateForm() {
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
					useLimitationList, mdFormatList, null, null, subjectList));
		});
	}
	
	public Result createSubmit() throws IOException {
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
			
			String creatorOtherValue;
			if(!dc.getCreator().equals("other")) {
				creatorOtherValue = null;
			} else {
				creatorOtherValue = dc.getCreatorOther();
			}
			
			Timestamp dateSourceCreationValue = nullCheckDate(dc.getDateSourceCreation());
			Timestamp dateSourcePublicationValue = nullCheckDate(dc.getDateSourcePublication());
			Timestamp dateSourceRevisionValue = nullCheckDate(dc.getDateSourceRevision());
			Timestamp dateSourceValidFromValue = nullCheckDate(dc.getDateSourceValidFrom());
			Timestamp dateSourceValidUntilValue = nullCheckDate(dc.getDateSourceValidUntil());
			
			Integer supplierId = tx.select(supplier.id)
				.from(supplier)
				.where(supplier.name.eq(session("username")))
				.fetchOne();
			
			tx.insert(metadata)
	    		.set(metadata.uuid, uuid)
	    		.set(metadata.location, dc.getLocation())
	    		.set(metadata.fileId, dc.getFileId())
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
			
			return redirect(controllers.routes.Index.index());
		});
	}
	
	public Result renderEditForm(String metadataUuid) {
		Boolean create = false;
		
		return q.withTransaction(tx -> {
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
	    			rightsList, useLimitationList, mdFormatList, sdfUS, sdfLocal, subjectList));
		});
	}
	
	public Result editSubmit(String metadataUuid) throws IOException {
		Form<DublinCore> dcForm = Form.form(DublinCore.class);
		DublinCore dc = dcForm.bindFromRequest().get();
		
		Timestamp dateToday = new Timestamp(new Date().getTime());
		
		return q.withTransaction(tx -> {
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
			
			String creatorOtherValue;
			if(!dc.getCreator().equals("other")) {
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
			
			Long metadataCount = tx.update(metadata)
				.where(metadata.uuid.eq(metadataUuid))
				.set(metadata.location, dc.getLocation())
				.set(metadata.fileId, dc.getFileId())
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
			
			return redirect(controllers.routes.Index.index());
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
			if(dc.getTitle().equals("")) {
				title = null;
			} else {
				title = dc.getTitle();
			}
			
			String description = null;
			if(dc.getDescription().equals("")) {
				description = null;
			} else {
				description = dc.getDescription();
			}
			
			String location = null;
			if(dc.getLocation().equals("")) {
				location = null;
			} else {
				location = dc.getLocation();
			}
			
			String creatorOther = null;
			if(dc.getCreator() != null) {
				if(dc.getCreatorOther().equals("")) {
					creatorOther = null;
				} else {
					creatorOther = dc.getCreatorOther();
				}
			} else {
				creatorOther = "";
			}
			
			return ok(validateform.render(title, description, location, creatorOther, dc.getDateSourceCreation(), dc.getSubject()));
		} catch(IllegalStateException ise) {
			return ok(bindingerror.render("Er is iets misgegaan. Controleer of de velden correct zijn ingevuld.", null, null, null, null, null, null, null));
		}
	}
	
	public Boolean validateDate(String date) {
		if(date.equals("")) {
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
	
	public Result cancel() {

		return redirect(controllers.routes.Index.index());
	}
}