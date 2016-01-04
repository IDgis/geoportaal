package controllers;

import static models.QCreator.creator;
import static models.QDataAttachment.dataAttachment;
import static models.QDataSubject.dataSubject;
import static models.QDataset.dataset;
import static models.QInfoFormat.infoFormat;
import static models.QRights.rights;
import static models.QSubject.subject;
import static models.QTypeInformation.typeInformation;
import static models.QUseLimitation.useLimitation;

import java.text.SimpleDateFormat;
import java.util.List;

import javax.inject.Inject;

import com.querydsl.core.Tuple;

import play.mvc.Controller;
import play.mvc.Result;

public class Edit extends Controller {
	@Inject Database db;
	
	public Result edit(String datasetUuid) {
		Boolean create = false;
		
		Integer datasetId = db.queryFactory
				.from(dataset)
				.select(dataset.id)
				.where(dataset.uuid.eq(datasetUuid))
				.fetchFirst();
		
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
    	
    	List<Tuple> typeInformationList = db.queryFactory.select(typeInformation.all())
        		.from(typeInformation)
        		.fetch();
        	
    	List<Tuple> creatorsList = db.queryFactory.select(creator.all())
            	.from(creator)
            	.fetch();
        	
       	List<Tuple> rightsList = db.queryFactory.select(rights.all())
               	.from(rights)
               	.fetch();
        	
       	List<Tuple> useLimitationList = db.queryFactory.select(useLimitation.all())
               	.from(useLimitation)
               	.fetch();
        	
        List<Tuple> infoFormatList = db.queryFactory.select(infoFormat.all())
               	.from(infoFormat)
               	.fetch();
        
        SimpleDateFormat sdfUS = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdfLocal = new SimpleDateFormat("dd-MM-yyyy");
        
        List<Tuple> subjectList = db.queryFactory.select(subject.all())
               	.from(subject)
               	.fetch();
    	
    	return ok(views.html.form.render(create, "", "", "", datasetRow, subjectsDataset, attachmentsDataset, typeInformationList, creatorsList, 
    			rightsList, useLimitationList, infoFormatList, sdfUS, sdfLocal, subjectList));
	}
	
	public Result changeStatus(Integer datasetId, String status) {
		db.queryFactory.update(dataset)
    		.where(dataset.id.eq(datasetId))
    		.set(dataset.status, status)
    		.execute();
    	
    	return redirect(controllers.routes.Index.index());
	}
	
	public Result changeSupplier(Integer datasetId, String supplier) {
		db.queryFactory.update(dataset)
    		.where(dataset.id.eq(datasetId))
    		.set(dataset.supplier, supplier)
    		.execute();
    	
    	return redirect(controllers.routes.Index.index());
	}
}
