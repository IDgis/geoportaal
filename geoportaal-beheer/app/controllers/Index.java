package controllers;

import static models.QMdFormat.mdFormat;
import static models.QMetadata.metadata;
import static models.QStatus.status;
import static models.QSupplier.supplier;
import static models.QStatusLabel.statusLabel;
import static models.QMdFormatLabel.mdFormatLabel;

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
	
	public Result jsRoutes() {
		return ok (Routes.javascriptRouter ("jsRoutes",
            controllers.routes.javascript.Assets.versioned(),
			controllers.routes.javascript.Delete.delete(),
			controllers.routes.javascript.Edit.changeStatus(),
			controllers.routes.javascript.Edit.changeSupplier()
        )).as ("text/javascript");
    }
}