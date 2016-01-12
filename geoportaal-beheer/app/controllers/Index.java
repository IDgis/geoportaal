package controllers;

import static models.QMdAttachment.mdAttachment;
import static models.QMdFormat.mdFormat;
import static models.QMetadata.metadata;
import static models.QStatus.status;
import static models.QSupplier.supplier;
import static models.QStatusLabel.statusLabel;
import static models.QMdFormatLabel.mdFormatLabel;
import static models.QMdSubject.mdSubject;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.inject.Inject;

import com.querydsl.core.Tuple;

import actions.DefaultAuthenticator;
import play.Routes;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

@Security.Authenticated(DefaultAuthenticator.class)
public class Index extends Controller {
	@Inject ZooKeeper zk;
	@Inject Database db;
	
	public Result index() throws SQLException {
		List<Tuple> datasetRows = db.queryFactory
    			.select(metadata.id, metadata.uuid, metadata.title, metadata.lastRevisionDate, statusLabel.label, supplier.name)
    			.from(metadata)
    			.join(status).on(metadata.status.eq(status.id))
    			.join(supplier).on(metadata.supplier.eq(supplier.id))
    			.join(statusLabel).on(status.id.eq(statusLabel.statusId))
    			.orderBy(metadata.lastRevisionDate.asc())
    			.fetch();
    	
    	List<Tuple> supplierList = db.queryFactory.select(supplier.all())
            	.from(supplier)
            	.fetch();
    	
    	List<Tuple> statusList = db.queryFactory
    			.select(status.name, statusLabel.label)
            	.from(status)
            	.join(statusLabel).on(status.id.eq(statusLabel.statusId))
            	.fetch();
    	
    	List<Tuple> mdFormatList = db.queryFactory
    			.select(mdFormat.name, mdFormatLabel.label)
            	.from(mdFormat)
            	.join(mdFormatLabel).on(mdFormat.id.eq(mdFormatLabel.mdFormatId))
            	.fetch();
    	
    	SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    	
    	return ok(views.html.index.render(datasetRows, supplierList, statusList, mdFormatList, sdf));
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
	
	public Result deleteMetadata(Integer datasetId) {
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
	
	public Result jsRoutes() {
		return ok (Routes.javascriptRouter ("jsRoutes",
            controllers.routes.javascript.Assets.versioned(),
			controllers.routes.javascript.Index.deleteMetadata(),
			controllers.routes.javascript.Index.changeStatus(),
			controllers.routes.javascript.Index.changeSupplier()
        )).as ("text/javascript");
    }
}