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

import java.util.List;

import javax.inject.Inject;

import com.querydsl.core.Tuple;

import play.mvc.Controller;
import play.mvc.Result;

public class Edit extends Controller {
	@Inject Database db;
	
	public Result edit(String datasetId) {
		Boolean create = false;
		
		Tuple datasetRow = db.queryFactory.select(dataset.id, dataset.location, dataset.fileId, dataset.title, dataset.description,
    			dataset.typeInfo, dataset.creator, dataset.rights, dataset.useLimitation, dataset.format, dataset.source, 
    			dataset.dateSourceCreation.dayOfMonth(), dataset.dateSourceCreation.month(), dataset.dateSourceCreation.year(),
    			dataset.dateSourcePublication.dayOfMonth(), dataset.dateSourcePublication.month(), dataset.dateSourcePublication.year(),
    			dataset.dateSourceRevision.dayOfMonth(), dataset.dateSourceRevision.month(), dataset.dateSourceRevision.year(),
    			dataset.dateSourceValidFrom.dayOfMonth(), dataset.dateSourceValidFrom.month(), dataset.dateSourceValidFrom.year(),
    			dataset.dateSourceValidUntil.dayOfMonth(), dataset.dateSourceValidUntil.month(), dataset.dateSourceValidUntil.year())
    			.from(dataset)
    			.where(dataset.id.eq(datasetId))
    			.fetchFirst();
    	
    	List<String> subjectsDataset = db.queryFactory.select(dataSubject.subject)
    			.from(dataSubject)
    			.where(dataSubject.datasetId.eq(datasetId))
    			.fetch();
    	
    	List<Tuple> attachmentsDataset = db.queryFactory.select(dataAttachment.attachmentName, dataAttachment.attachmentContent)
    			.from(dataAttachment)
    			.where(dataAttachment.datasetId.eq(datasetId))
    			.fetch();
    	
    	List<Tuple> typeInformationList = db.queryFactory.select(typeInformations.identification, typeInformations.label)
        		.from(typeInformations)
        		.fetch();
        	
    	List<Tuple> creatorsList = db.queryFactory.select(creators.identification, creators.label)
            	.from(creators)
            	.fetch();
        	
       	List<Tuple> rightsList = db.queryFactory.select(rights.identification, rights.label)
               	.from(rights)
               	.fetch();
        	
       	List<Tuple> useLimitationList = db.queryFactory.select(useLimitations.identification, useLimitations.label)
               	.from(useLimitations)
               	.fetch();
        	
        List<Tuple> infoFormatList = db.queryFactory.select(infoFormats.identification, infoFormats.label)
               	.from(infoFormats)
               	.fetch();
        	
        List<Tuple> subjectList = db.queryFactory.select(subjects.identification, subjects.label)
               	.from(subjects)
               	.fetch();
    	
    	return ok(views.html.form.render(create, "", "", datasetRow, subjectsDataset, attachmentsDataset, typeInformationList, creatorsList, 
    			rightsList, useLimitationList, infoFormatList, subjectList));
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
