package controllers;

import static models.QDataset.dataset;
import static models.QInfoFormat.infoFormat;
import static models.QStatus.status;
import static models.QSupplier.supplier;

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
    	List<Tuple> datasetRows = db.queryFactory.select(dataset.all())
    			.from(dataset)
    			.orderBy(dataset.lastRevisionDate.asc())
    			.fetch();
    	
    	List<Tuple> supplierList = db.queryFactory.select(supplier.all())
            	.from(supplier)
            	.fetch();
    	
    	List<Tuple> statusList = db.queryFactory.select(status.all())
            	.from(status)
            	.fetch();
    	
    	List<Tuple> infoFormatList = db.queryFactory.select(infoFormat.all())
            	.from(infoFormat)
            	.fetch();
    	
    	SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    	
    	return ok(views.html.index.render(datasetRows, supplierList, statusList, infoFormatList, sdf));
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