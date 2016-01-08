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
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;

public class Add extends Controller {
	@Inject Database db;
	
	public Result add() {
		Boolean create = true;
		String uuid = UUID.randomUUID().toString();
		String todayUS = new SimpleDateFormat("yyyy-MM-dd").format(new Date().getTime());
		String todayLocal = new SimpleDateFormat("dd-MM-yyyy").format(new Date().getTime());
		
		List<Tuple> typeInformationList = db.queryFactory
			.select(typeInformation.id, typeInformationLabel.label)
    		.from(typeInformation)
    		.join(typeInformationLabel).on(typeInformation.id.eq(typeInformationLabel.typeInformationId))
    		.fetch();
    	
    	List<Tuple> creatorsList = db.queryFactory
    		.select(creator.id, creatorLabel.label)
        	.from(creator)
        	.join(creatorLabel).on(creator.id.eq(creatorLabel.creatorId))
        	.fetch();
    	
    	List<Tuple> rightsList = db.queryFactory
    			.select(rights.id, rightsLabel.label)
            	.from(rights)
            	.join(rightsLabel).on(rights.id.eq(rightsLabel.rightsId))
            	.fetch();
    	
    	List<Tuple> useLimitationList = db.queryFactory
    			.select(useLimitation.id, useLimitationLabel.label)
            	.from(useLimitation)
            	.join(useLimitationLabel).on(useLimitation.id.eq(useLimitationLabel.useLimitationId))
            	.fetch();
    	
    	List<Tuple> mdFormatList = db.queryFactory
    			.select(mdFormat.id, mdFormatLabel.label)
            	.from(mdFormat)
            	.join(mdFormatLabel).on(mdFormat.id.eq(mdFormatLabel.mdFormatId))
            	.fetch();
    	
    	List<Tuple> subjectList = db.queryFactory
    			.select(subject.id, subjectLabel.label)
            	.from(subject)
            	.join(subjectLabel).on(subject.id.eq(subjectLabel.subjectId))
            	.fetch();
		
		return ok(views.html.form.render(create, uuid, todayUS, todayLocal, null, null, null, typeInformationList, creatorsList, rightsList, 
				useLimitationList, mdFormatList, null, null, subjectList));
	}
	
	public Result submit() throws ParseException, IOException {
		Form<DublinCore> dcForm = Form.form(DublinCore.class);
		DublinCore dc = dcForm.bindFromRequest().get();
		
		Timestamp dateToday = new Timestamp(new Date().getTime());
		
		Integer typeInformationKey = db.queryFactory.select(typeInformation.id)
			.from(typeInformation)
			.where(typeInformation.name.eq(dc.getTypeInfo()))
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
			.where(mdFormat.name.eq(dc.getFormat()))
			.fetchFirst();
		
		db.queryFactory.insert(metadata)
    		.set(metadata.uuid, dc.getUuid())
    		.set(metadata.location, dc.getLocation())
    		.set(metadata.fileId, dc.getFileId())
    		.set(metadata.title, dc.getTitle())
    		.set(metadata.description, dc.getDescription())
    		.set(metadata.typeInformation, typeInformationKey)
    		.set(metadata.creator, creatorKey)
    		.set(metadata.rights, rightsKey)
    		.set(metadata.useLimitation, useLimitationKey)
    		.set(metadata.mdFormat, formatKey)
    		.set(metadata.source, dc.getSource())
    		.set(metadata.dateSourceCreation, new Timestamp(dc.getDateSourceCreation().getTime()))
    		.set(metadata.dateSourcePublication, new Timestamp(dc.getDateSourcePublication().getTime()))
    		.set(metadata.dateSourceRevision, new Timestamp(dc.getDateSourceRevision().getTime()))
    		.set(metadata.dateSourceValidFrom, new Timestamp(dc.getDateSourceValidFrom().getTime()))
    		.set(metadata.dateSourceValidUntil, new Timestamp(dc.getDateSourceValidUntil().getTime()))
    		.set(metadata.supplier, 1)
    		.set(metadata.status, 2)
    		.set(metadata.published, false)
    		.set(metadata.lastRevisionUser, session("username"))
    		.set(metadata.lastRevisionDate, dateToday)
    		.execute();
    	
		Integer datasetId = db.queryFactory.select(metadata.id)
				.from(metadata)
				.where(metadata.uuid.eq(dc.getUuid()))
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
					.set(mdAttachment.metadataId, datasetId)
					.set(mdAttachment.attachmentName, fp.getFilename())
					.set(mdAttachment.attachmentContent, input)
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
					.set(mdSubject.metadataId, datasetId)
					.set(mdSubject.subject, subjectKey)
					.execute();
			}
		}
		
		return redirect(controllers.routes.Index.index());
	}
	
	public Result cancel() {

		return redirect(controllers.routes.Index.index());
	}
}