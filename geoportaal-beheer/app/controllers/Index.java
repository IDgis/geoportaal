package controllers;

import static models.QDataset.dataset;
import static models.QInfoFormats.infoFormats;
import static models.QStatuses.statuses;
import static models.QSuppliers.suppliers;

import java.sql.SQLException;
import java.util.List;

import javax.inject.Inject;
import javax.sql.DataSource;

import com.querydsl.core.Tuple;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.PostgreSQLTemplates;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.SQLTemplates;

import play.Routes;
import play.db.DB;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import actions.DefaultAuthenticator;

@Security.Authenticated(DefaultAuthenticator.class)
public class Index extends Controller {
	@Inject ZooKeeper zk;
	
	DataSource ds = DB.getDataSource();
	
	public Result index() throws SQLException {
    	SQLTemplates templates = new PostgreSQLTemplates();
    	Configuration configuration = new Configuration(templates);
    	SQLQueryFactory queryFactory = new SQLQueryFactory(configuration, ds);
    	
    	List<Tuple> datasetRows = queryFactory.select(dataset.id, dataset.title, dataset.supplier, dataset.status, 
    			dataset.lastRevisionDate.dayOfMonth(), dataset.lastRevisionDate.month(), dataset.lastRevisionDate.year())
    			.from(dataset)
    			.orderBy(dataset.lastRevisionDate.asc())
    			.fetch();
    	
    	List<String> supplierList = queryFactory.select(suppliers.name)
            	.from(suppliers)
            	.fetch();
    	
    	List<Tuple> statusList = queryFactory.select(statuses.identification, statuses.label)
            	.from(statuses)
            	.fetch();
    	
    	List<Tuple> infoFormatList = queryFactory.select(infoFormats.identification, infoFormats.label)
            	.from(infoFormats)
            	.fetch();
    	
    	return ok(views.html.index.render(datasetRows, supplierList, statusList, infoFormatList));
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