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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import com.querydsl.core.Tuple;

import models.DublinCore;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;

import views.html.*;

public class MetadataDC extends Controller {
	@Inject Database db;
	
	public Result renderCreateForm() {
		Boolean create = true;
		String todayUS = new SimpleDateFormat("yyyy-MM-dd").format(new Date().getTime());
		String todayLocal = new SimpleDateFormat("dd-MM-yyyy").format(new Date().getTime());
		
		List<Tuple> typeInformationList = db.queryFactory
			.select(typeInformation.id, typeInformation.name, typeInformationLabel.label)
    		.from(typeInformation)
    		.join(typeInformationLabel).on(typeInformation.id.eq(typeInformationLabel.typeInformationId))
    		.fetch();
    	
    	List<Tuple> creatorsList = db.queryFactory
    		.select(creator.id, creator.name, creatorLabel.label)
        	.from(creator)
        	.join(creatorLabel).on(creator.id.eq(creatorLabel.creatorId))
        	.fetch();
    	
    	List<Tuple> rightsList = db.queryFactory
    			.select(rights.id, rights.name, rightsLabel.label)
            	.from(rights)
            	.join(rightsLabel).on(rights.id.eq(rightsLabel.rightsId))
            	.fetch();
    	
    	List<Tuple> useLimitationList = db.queryFactory
    			.select(useLimitation.id, useLimitation.name, useLimitationLabel.label)
            	.from(useLimitation)
            	.join(useLimitationLabel).on(useLimitation.id.eq(useLimitationLabel.useLimitationId))
            	.fetch();
    	
    	List<Tuple> mdFormatList = db.queryFactory
    			.select(mdFormat.id, mdFormat.name, mdFormatLabel.label)
            	.from(mdFormat)
            	.join(mdFormatLabel).on(mdFormat.id.eq(mdFormatLabel.mdFormatId))
            	.fetch();
    	
    	List<Tuple> subjectList = db.queryFactory
    			.select(subject.id, subject.name, subjectLabel.label)
            	.from(subject)
            	.join(subjectLabel).on(subject.id.eq(subjectLabel.subjectId))
            	.fetch();
		
    	return ok(views.html.form.render(create, todayUS, todayLocal, null, null, null, typeInformationList, creatorsList, rightsList, 
				useLimitationList, mdFormatList, null, null, subjectList));
	}
	
	public Result createSubmit() throws IOException {
		Form<DublinCore> dcForm = Form.form(DublinCore.class);
		DublinCore dc = dcForm.bindFromRequest().get();
		
		String uuid = UUID.randomUUID().toString();
		Timestamp dateToday = new Timestamp(new Date().getTime());
		
		Integer typeInformationKey = db.queryFactory.select(typeInformation.id)
			.from(typeInformation)
			.where(typeInformation.name.eq(dc.getTypeInformation()))
			.fetchFirst();
		
		Integer creatorKey = db.queryFactory.select(creator.id)
			.from(creator)
			.where(creator.name.eq(dc.getCreator()))
			.fetchFirst();
		
		Integer rightsKey = db.queryFactory.select(rights.id)
			.from(rights)
			.where(rights.name.eq(dc.getRights()))
			.fetchFirst();
		
		Integer useLimitationKey = db.queryFactory.select(useLimitation.id)
			.from(useLimitation)
			.where(useLimitation.name.eq(dc.getUseLimitation()))
			.fetchFirst();
		
		Integer formatKey = db.queryFactory.select(mdFormat.id)
			.from(mdFormat)
			.where(mdFormat.name.eq(dc.getMdFormat()))
			.fetchFirst();
		
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
		
		Integer supplierId = db.queryFactory.select(supplier.id)
			.from(supplier)
			.where(supplier.name.eq(session("username")))
			.fetchFirst();
		
		db.queryFactory.insert(metadata)
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
    	
		Integer metadataId = db.queryFactory.select(metadata.id)
				.from(metadata)
				.where(metadata.uuid.eq(uuid))
				.fetchFirst();
		
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
				
				db.queryFactory.insert(mdAttachment)
					.set(mdAttachment.metadataId, metadataId)
					.set(mdAttachment.attachmentName, fp.getFilename())
					.set(mdAttachment.attachmentContent, input)
					.set(mdAttachment.attachmentMimetype, fp.getContentType())
					.execute();
			}
		}
		
		if(dc.getSubject() != null) {
			for(String subjectStr : dc.getSubject()) {
				Integer subjectKey = db.queryFactory.select(subject.id)
					.from(subject)
					.where(subject.name.eq(subjectStr))
					.fetchFirst();
				
				db.queryFactory.insert(mdSubject)
					.set(mdSubject.metadataId, metadataId)
					.set(mdSubject.subject, subjectKey)
					.execute();
			}
		}
		
		return redirect(controllers.routes.Index.index());
	}
	
	public Result renderEditForm(String metadataUuid) {
		Boolean create = false;
		
		Integer metadataId = db.queryFactory
				.from(metadata)
				.select(metadata.id)
				.where(metadata.uuid.eq(metadataUuid))
				.fetchFirst();
		
		Tuple datasetRow = db.queryFactory.select(metadata.id, metadata.uuid, metadata.location, metadata.fileId, metadata.title, 
				metadata.description, metadata.typeInformation, metadata.creator, metadata.creatorOther, metadata.rights, metadata.useLimitation,
				metadata.mdFormat, metadata.source, metadata.dateSourceCreation, metadata.dateSourcePublication, metadata.dateSourceRevision,
				metadata.dateSourceValidFrom, metadata.dateSourceValidUntil, creator.name)
    			.from(metadata)
    			.join(creator).on(metadata.creator.eq(creator.id))
    			.where(metadata.id.eq(metadataId))
    			.fetchFirst();
    	
    	List<Tuple> subjectsDataset = db.queryFactory.select(mdSubject.all())
    			.from(mdSubject)
    			.where(mdSubject.metadataId.eq(metadataId))
    			.fetch();
    	
    	List<Tuple> attachmentsDataset = db.queryFactory.select(mdAttachment.all())
    			.from(mdAttachment)
    			.where(mdAttachment.metadataId.eq(metadataId))
    			.fetch();
    	
    	List<Tuple> typeInformationList = db.queryFactory
    			.select(typeInformation.id, typeInformation.name, typeInformationLabel.label)
        		.from(typeInformation)
        		.join(typeInformationLabel).on(typeInformation.id.eq(typeInformationLabel.typeInformationId))
        		.fetch();
        	
    	List<Tuple> creatorsList = db.queryFactory
	    		.select(creator.id, creator.name, creatorLabel.label)
	        	.from(creator)
	        	.join(creatorLabel).on(creator.id.eq(creatorLabel.creatorId))
	        	.fetch();
    	
    	List<Tuple> rightsList = db.queryFactory
    			.select(rights.id, rights.name, rightsLabel.label)
            	.from(rights)
            	.join(rightsLabel).on(rights.id.eq(rightsLabel.rightsId))
            	.fetch();
    	
    	List<Tuple> useLimitationList = db.queryFactory
    			.select(useLimitation.id, useLimitation.name, useLimitationLabel.label)
            	.from(useLimitation)
            	.join(useLimitationLabel).on(useLimitation.id.eq(useLimitationLabel.useLimitationId))
            	.fetch();
    	
    	List<Tuple> mdFormatList = db.queryFactory
    			.select(mdFormat.id, mdFormat.name, mdFormatLabel.label)
            	.from(mdFormat)
            	.join(mdFormatLabel).on(mdFormat.id.eq(mdFormatLabel.mdFormatId))
            	.fetch();
    	
    	List<Tuple> subjectList = db.queryFactory
    			.select(subject.id, subject.name, subjectLabel.label)
            	.from(subject)
            	.join(subjectLabel).on(subject.id.eq(subjectLabel.subjectId))
            	.fetch();
        
        SimpleDateFormat sdfUS = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdfLocal = new SimpleDateFormat("dd-MM-yyyy");
        
        return ok(views.html.form.render(create, "", "", datasetRow, subjectsDataset, attachmentsDataset, typeInformationList, creatorsList, 
    			rightsList, useLimitationList, mdFormatList, sdfUS, sdfLocal, subjectList));
	}
	
	public Result editSubmit(String metadataUuid) throws IOException {
		Form<DublinCore> dcForm = Form.form(DublinCore.class);
		DublinCore dc = dcForm.bindFromRequest().get();
		
		Timestamp dateToday = new Timestamp(new Date().getTime());
		
		Integer metadataId = db.queryFactory.select(metadata.id)
				.from(metadata)
				.where(metadata.uuid.eq(metadataUuid))
				.fetchFirst();
		
		Integer typeInformationKey = db.queryFactory.select(typeInformation.id)
			.from(typeInformation)
			.where(typeInformation.name.eq(dc.getTypeInformation()))
			.fetchFirst();
		
		Integer creatorKey = db.queryFactory.select(creator.id)
			.from(creator)
			.where(creator.name.eq(dc.getCreator()))
			.fetchFirst();
		
		Integer rightsKey = db.queryFactory.select(rights.id)
			.from(rights)
			.where(rights.name.eq(dc.getRights()))
			.fetchFirst();
		
		Integer useLimitationKey = db.queryFactory.select(useLimitation.id)
			.from(useLimitation)
			.where(useLimitation.name.eq(dc.getUseLimitation()))
			.fetchFirst();
		
		Integer formatKey = db.queryFactory.select(mdFormat.id)
			.from(mdFormat)
			.where(mdFormat.name.eq(dc.getMdFormat()))
			.fetchFirst();
		
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
		
		db.queryFactory.update(metadata)
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
	
		List<String> attToDelete = dc.getDeletedAttachment();
		if(attToDelete != null) {
			for(String attachmentName : attToDelete) {
				db.queryFactory.delete(mdAttachment)
					.where(mdAttachment.metadataId.eq(metadataId)
						.and(mdAttachment.attachmentName.eq(attachmentName)))
					.execute();
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
				
				db.queryFactory.insert(mdAttachment)
					.set(mdAttachment.metadataId, metadataId)
					.set(mdAttachment.attachmentName, fp.getFilename())
					.set(mdAttachment.attachmentContent, input)
					.set(mdAttachment.attachmentMimetype, fp.getContentType())
					.execute();
			}
		}
		
		if(subjects != null) {
			db.queryFactory.delete(mdSubject)
				.where(mdSubject.metadataId.eq(metadataId))
				.execute();
				
			for(String subjectStr : subjects) {
				Integer subjectKey = db.queryFactory.select(subject.id)
					.from(subject)
					.where(subject.name.eq(subjectStr))
					.fetchFirst();
				
				db.queryFactory.insert(mdSubject)
					.set(mdSubject.metadataId, metadataId)
					.set(mdSubject.subject, subjectKey)
					.execute();
			}
		}
		
		return redirect(controllers.routes.Index.index());
	}
	
	public Result validateForm(String metadataUuid) {
		Form<DublinCore> dcForm = Form.form(DublinCore.class);
		DublinCore dc = dcForm.bindFromRequest().get();
		
		String title = "";
		if(dc.getTitle().equals("")) {
			title = null;
		} else {
			title = dc.getTitle();
		}
		
		String description = "";
		if(dc.getDescription().equals("")) {
			description = null;
		} else {
			description = dc.getDescription();
		}
		
		String location = "";
		if(dc.getLocation().equals("")) {
			location = null;
		} else {
			location = dc.getLocation();
		}
		
		return ok(validateform.render(title, description, location, dc.getDateSourceCreation(), dc.getSubject()));
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