package controllers;

import static models.QCreator.creator;
import static models.QCreatorLabel.creatorLabel;
import static models.QMdAttachment.mdAttachment;
import static models.QMdSubject.mdSubject;
import static models.QMetadata.metadata;
import static models.QMdFormat.mdFormat;
import static models.QMdFormatLabel.mdFormatLabel;
import static models.QRights.rights;
import static models.QRightsLabel.rightsLabel;
import static models.QSubject.subject;
import static models.QSubjectLabel.subjectLabel;
import static models.QTypeInformation.typeInformation;
import static models.QTypeInformationLabel.typeInformationLabel;
import static models.QUseLimitation.useLimitation;
import static models.QUseLimitationLabel.useLimitationLabel;
import static models.QStatus.status;
import static models.QSupplier.supplier;

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
				.from(metadata)
				.select(metadata.id)
				.where(metadata.uuid.eq(datasetUuid))
				.fetchFirst();
		
		Tuple datasetRow = db.queryFactory.select(metadata.all())
    			.from(metadata)
    			.where(metadata.id.eq(datasetId))
    			.fetchFirst();
    	
    	List<Tuple> subjectsDataset = db.queryFactory.select(mdSubject.all())
    			.from(mdSubject)
    			.where(mdSubject.metadataId.eq(datasetId))
    			.fetch();
    	
    	List<Tuple> attachmentsDataset = db.queryFactory.select(mdAttachment.all())
    			.from(mdAttachment)
    			.where(mdAttachment.metadataId.eq(datasetId))
    			.fetch();
    	
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
        
        SimpleDateFormat sdfUS = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdfLocal = new SimpleDateFormat("dd-MM-yyyy");
        
        return ok(views.html.form.render(create, "", "", "", datasetRow, subjectsDataset, attachmentsDataset, typeInformationList, creatorsList, 
    			rightsList, useLimitationList, mdFormatList, sdfUS, sdfLocal, subjectList));
	}
	
	public Result changeStatus(Integer datasetId, String statusStr) {
		Integer statusKey = db.queryFactory.select(status.id)
			.from(status)
			.where(status.name.eq(statusStr))
			.fetchFirst();
		
		db.queryFactory.update(metadata)
    		.where(metadata.id.eq(datasetId))
    		.set(metadata.status, statusKey)
    		.execute();
    	
    	return redirect(controllers.routes.Index.index());
	}
	
	public Result changeSupplier(Integer datasetId, String supplierStr) {
		Integer supplierKey = db.queryFactory.select(supplier.id)
			.from(supplier)
			.where(supplier.name.eq(supplierStr))
			.fetchFirst();
		
		db.queryFactory.update(metadata)
    		.where(metadata.id.eq(datasetId))
    		.set(metadata.supplier, supplierKey)
    		.execute();
    	
    	return redirect(controllers.routes.Index.index());
	}
}
