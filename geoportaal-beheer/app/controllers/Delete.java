package controllers;

import static models.QMdAttachment.mdAttachment;
import static models.QMdSubject.mdSubject;
import static models.QMetadata.metadata;

import javax.inject.Inject;

import play.mvc.Controller;
import play.mvc.Result;

public class Delete extends Controller {
	@Inject Database db;
	
	public Result delete(Integer datasetId) {
		db.queryFactory.delete(metadata)
    		.where(metadata.id.eq(datasetId))
    		.execute();
    	
    	db.queryFactory.delete(mdAttachment)
    		.where(mdAttachment.metadataId.eq(datasetId))
    		.execute();
    	
    	db.queryFactory.delete(mdSubject)
    		.where(mdSubject.metadataId.eq(datasetId))
    		.execute();
		
		return redirect(controllers.routes.Index.index());
	}
}
