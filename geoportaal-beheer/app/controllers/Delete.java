package controllers;

import static models.QDataAttachment.dataAttachment;
import static models.QDataSubject.dataSubject;
import static models.QDataset.dataset;

import javax.inject.Inject;

import play.mvc.Controller;
import play.mvc.Result;

public class Delete extends Controller {
	@Inject Database db;
	
	public Result delete(String datasetId) {
		db.queryFactory.delete(dataset)
    		.where(dataset.id.eq(datasetId))
    		.execute();
    	
    	db.queryFactory.delete(dataAttachment)
    		.where(dataAttachment.datasetId.eq(datasetId))
    		.execute();
    	
    	db.queryFactory.delete(dataSubject)
    		.where(dataSubject.datasetId.eq(datasetId))
    		.execute();
		
		return redirect(controllers.routes.Index.index());
	}
}
