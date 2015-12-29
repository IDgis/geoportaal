package controllers;

import static models.QCreators.creators;
import static models.QDataAttachment.dataAttachment;
import static models.QDataSubject.dataSubject;
import static models.QDataset.dataset;
import static models.QInfoFormats.infoFormats;
import static models.QRights.rights;
import static models.QSubjects.subjects;
import static models.QTypeInformations.typeInformations;
import static models.QUseLimitations.useLimitations;

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
		
		List<Tuple> typeInformationList = db.queryFactory.select(typeInformations.all())
    		.from(typeInformations)
    		.fetch();
    	
    	List<Tuple> creatorsList = db.queryFactory.select(creators.all())
        	.from(creators)
        	.fetch();
    	
    	List<Tuple> rightsList = db.queryFactory.select(rights.all())
            	.from(rights)
            	.fetch();
    	
    	List<Tuple> useLimitationList = db.queryFactory.select(useLimitations.all())
            	.from(useLimitations)
            	.fetch();
    	
    	List<Tuple> infoFormatList = db.queryFactory.select(infoFormats.all())
            	.from(infoFormats)
            	.fetch();
    	
    	List<Tuple> subjectList = db.queryFactory.select(subjects.all())
            	.from(subjects)
            	.fetch();
		
		return ok(views.html.form.render(create, uuid, todayUS, todayLocal, null, null, null, typeInformationList, creatorsList, rightsList, 
				useLimitationList, infoFormatList, null, null, subjectList));
	}
	
	public Result submit() throws ParseException, IOException {
		Form<DublinCore> dcForm = Form.form(DublinCore.class);
		DublinCore dc = dcForm.bindFromRequest().get();
		
		Timestamp dateToday = new Timestamp(new Date().getTime());
		
		db.queryFactory.insert(dataset)
    		.set(dataset.id, dc.getId())
    		.set(dataset.location, dc.getLocation())
    		.set(dataset.fileId, dc.getFileId())
    		.set(dataset.title, dc.getTitle())
    		.set(dataset.description, dc.getDescription())
    		.set(dataset.typeInfo, dc.getTypeInfo())
    		.set(dataset.creator, dc.getCreator())
    		.set(dataset.rights, dc.getRights())
    		.set(dataset.useLimitation, dc.getUseLimitation())
    		.set(dataset.format, dc.getFormat())
    		.set(dataset.source, dc.getSource())
    		.set(dataset.dateSourceCreation, new Timestamp(dc.getDateSourceCreation().getTime()))
    		.set(dataset.dateSourcePublication, new Timestamp(dc.getDateSourcePublication().getTime()))
    		.set(dataset.dateSourceRevision, new Timestamp(dc.getDateSourceRevision().getTime()))
    		.set(dataset.dateSourceValidFrom, new Timestamp(dc.getDateSourceValidFrom().getTime()))
    		.set(dataset.dateSourceValidUntil, new Timestamp(dc.getDateSourceValidUntil().getTime()))
    		.set(dataset.supplier, session("username"))
    		.set(dataset.status, "concept")
    		.set(dataset.published, false)
    		.set(dataset.lastRevisionUser, session("username"))
    		.set(dataset.lastRevisionDate, dateToday)
    		.execute();
    	
		play.mvc.Http.MultipartFormData body = request().body().asMultipartFormData();
		List<FilePart> allFiles = body.getFiles();
		for(FilePart fp: allFiles) {
			if(fp != null) {
				java.io.File file = fp.getFile();
				InputStream inputStream = new FileInputStream(file);
				String input = inputStream.toString();
				
				inputStream.close();
				
				db.queryFactory.insert(dataAttachment)
					.set(dataAttachment.datasetId, dc.getId())
					.set(dataAttachment.attachmentName, fp.getFilename())
					.set(dataAttachment.attachmentContent, input)
					.execute();
			}
		}
		
		if(dc.getSubject() != null) {
			for(String subject : dc.getSubject()) {
				db.queryFactory.insert(dataSubject)
					.set(dataSubject.datasetId, dc.getId())
					.set(dataSubject.subject, subject)
					.execute();
			}
		}
		
		return redirect(controllers.routes.Index.index());
	}
	
	public Result cancel() {

		return redirect(controllers.routes.Index.index());
	}
}