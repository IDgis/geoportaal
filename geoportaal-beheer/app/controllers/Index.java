package controllers;

import static models.QDataset.dataset;

import java.sql.SQLException;
import java.util.List;

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

public class Index extends Controller {
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
    	
    	return ok(views.html.index.render(datasetRows));
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