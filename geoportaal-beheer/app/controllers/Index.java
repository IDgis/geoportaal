package controllers;

import static models.QDataset.dataset;
import static models.QInfoFormats.infoFormats;
import static models.QStatuses.statuses;
import static models.QSuppliers.suppliers;

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
    	
    	List<Tuple> supplierList = db.queryFactory.select(suppliers.all())
            	.from(suppliers)
            	.fetch();
    	
    	List<Tuple> statusList = db.queryFactory.select(statuses.all())
            	.from(statuses)
            	.fetch();
    	
    	List<Tuple> infoFormatList = db.queryFactory.select(infoFormats.all())
            	.from(infoFormats)
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