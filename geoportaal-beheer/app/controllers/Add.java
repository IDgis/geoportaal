package controllers;

import static models.QDataAttachment.dataAttachment;
import static models.QDataset.dataset;
import static models.QDataSubject.dataSubject;
import static models.QTypeInformations.typeInformations;
import static models.QCreators.creators;
import static models.QRights.rights;
import static models.QUseLimitations.useLimitations;
import static models.QInfoFormats.infoFormats;
import static models.QSubjects.subjects;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.sql.DataSource;

import com.querydsl.core.Tuple;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.PostgreSQLTemplates;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.SQLTemplates;

import models.DublinCore;
import play.data.Form;
import play.db.DB;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;

public class Add extends Controller {
	DataSource ds = DB.getDataSource();
	
	public Result add() {
		Boolean create = true;
		
		SQLTemplates templates = new PostgreSQLTemplates();
    	Configuration configuration = new Configuration(templates);
    	SQLQueryFactory queryFactory = new SQLQueryFactory(configuration, ds);
    	
    	List<Tuple> typeInformationList = queryFactory.select(typeInformations.identification, typeInformations.label)
    		.from(typeInformations)
    		.fetch();
    	
    	List<Tuple> creatorsList = queryFactory.select(creators.identification, creators.label)
        	.from(creators)
        	.fetch();
    	
    	List<Tuple> rightsList = queryFactory.select(rights.identification, rights.label)
            	.from(rights)
            	.fetch();
    	
    	List<Tuple> useLimitationList = queryFactory.select(useLimitations.identification, useLimitations.label)
            	.from(useLimitations)
            	.fetch();
    	
    	List<Tuple> infoFormatList = queryFactory.select(infoFormats.identification, infoFormats.label)
            	.from(infoFormats)
            	.fetch();
    	
    	List<Tuple> subjectList = queryFactory.select(subjects.identification, subjects.label)
            	.from(subjects)
            	.fetch();
		
		String uuid = UUID.randomUUID().toString();
		Date dateToday = new Date();
		String today = new SimpleDateFormat("dd-MM-yyyy").format(new Date(dateToday.getTime()));
		
		return ok(views.html.form.render(create, uuid, today, null, null, null, typeInformationList, creatorsList, rightsList, 
				useLimitationList, infoFormatList, subjectList));
	}
	
	public Result submit() throws ParseException, FileNotFoundException {
		SQLTemplates templates = new PostgreSQLTemplates();
    	Configuration configuration = new Configuration(templates);
    	SQLQueryFactory queryFactory = new SQLQueryFactory(configuration, ds);
		
		Form<DublinCore> dcForm = Form.form(DublinCore.class);
		DublinCore dc = dcForm.bindFromRequest().get();
		
		Date dateToday = new Date();
    	
		Date dSC = new SimpleDateFormat("dd-MM-yyyy").parse(dc.getDateSourceCreation());
		Date dSP = new SimpleDateFormat("dd-MM-yyyy").parse(dc.getDateSourcePublication());
		Date dSR = new SimpleDateFormat("dd-MM-yyyy").parse(dc.getDateSourceRevision());
		Date dSVF = new SimpleDateFormat("dd-MM-yyyy").parse(dc.getDateSourceValidFrom());
		Date dSVU = new SimpleDateFormat("dd-MM-yyyy").parse(dc.getDateSourceValidUntil());
		
		queryFactory.insert(dataset)
    		.columns(dataset.id, dataset.location, dataset.fileId, dataset.title, dataset.description, dataset.typeInfo,
    			dataset.creator, dataset.rights, dataset.useLimitation, dataset.format, dataset.source, dataset.dateSourceCreation,
    			dataset.dateSourcePublication, dataset.dateSourceRevision, dataset.dateSourceValidFrom, dataset.dateSourceValidUntil,
    			dataset.supplier, dataset.status, dataset.published, dataset.lastRevisionUser, dataset.lastRevisionDate)
    		.values(dc.getId(), dc.getLocation(), dc.getFileId(), dc.getTitle(), dc.getDescription(), dc.getTypeInfo(), dc.getCreator(),
    				dc.getRights(), dc.getUseLimitation(), dc.getFormat(), dc.getSource(), dSC, dSP, dSR, dSVF, dSVU, 
    				"Nienhuis", "concept", false, "Nienhuis", dateToday)
    		.execute();
    	
		play.mvc.Http.MultipartFormData body = request().body().asMultipartFormData();
		List<FilePart> allFiles = body.getFiles();
		for(FilePart fp: allFiles) {
			if(fp != null) {
				java.io.File file = fp.getFile();
				InputStream inpSt = new FileInputStream(file);
				
				queryFactory.insert(dataAttachment)
					.columns(dataAttachment.datasetId, dataAttachment.attachmentName, dataAttachment.attachmentContent)
					.values(dc.getId(), fp.getFilename(), inpSt)
					.execute();
			}
		}
		
		if(dc.getSubject() != null) {
			for(String subject : dc.getSubject()) {
				queryFactory.insert(dataSubject)
					.columns(dataSubject.datasetId, dataSubject.subject)
					.values(dc.getId(), subject)
					.execute();
			}
		}
		
		return redirect(controllers.routes.Index.index());
	}
	
	public Result cancel() {

		return redirect(controllers.routes.Index.index());
	}
}