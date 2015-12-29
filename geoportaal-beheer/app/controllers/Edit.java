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

import java.text.SimpleDateFormat;
import java.util.List;

import javax.inject.Inject;

import com.querydsl.core.Tuple;

import play.mvc.Controller;
import play.mvc.Result;

public class Edit extends Controller {
	@Inject Database db;
	
	public Result edit(String datasetId) {
		Boolean create = false;
		
		Tuple datasetRow = db.queryFactory.select(dataset.all())
    			.from(dataset)
    			.where(dataset.id.eq(datasetId))
    			.fetchFirst();
    	
    	List<Tuple> subjectsDataset = db.queryFactory.select(dataSubject.all())
    			.from(dataSubject)
    			.where(dataSubject.datasetId.eq(datasetId))
    			.fetch();
    	
    	List<Tuple> attachmentsDataset = db.queryFactory.select(dataAttachment.all())
    			.from(dataAttachment)
    			.where(dataAttachment.datasetId.eq(datasetId))
    			.fetch();
    	
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
        
        SimpleDateFormat sdfUS = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdfLocal = new SimpleDateFormat("dd-MM-yyyy");
        
        List<Tuple> subjectList = db.queryFactory.select(subjects.all())
               	.from(subjects)
               	.fetch();
    	
    	return ok(views.html.form.render(create, "", "", "", datasetRow, subjectsDataset, attachmentsDataset, typeInformationList, creatorsList, 
    			rightsList, useLimitationList, infoFormatList, sdfUS, sdfLocal, subjectList));
	}
	
	public Result changeStatus(String datasetId, String status) {
		db.queryFactory.update(dataset)
    		.where(dataset.id.eq(datasetId))
    		.set(dataset.status, status)
    		.execute();
    	
    	return redirect(controllers.routes.Index.index());
	}
	
	public Result changeSupplier(String datasetId, String supplier) {
		db.queryFactory.update(dataset)
    		.where(dataset.id.eq(datasetId))
    		.set(dataset.supplier, supplier)
    		.execute();
    	
    	return redirect(controllers.routes.Index.index());
	}
}
