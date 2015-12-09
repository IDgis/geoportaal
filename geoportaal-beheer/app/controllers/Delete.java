package controllers;

import static models.QAttachment.attachment1;
import static models.QDataset.dataset;
import static models.QSubject.subject1;

import javax.sql.DataSource;

import com.querydsl.sql.Configuration;
import com.querydsl.sql.PostgreSQLTemplates;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.SQLTemplates;

import play.db.DB;
import play.mvc.Controller;
import play.mvc.Result;

public class Delete extends Controller {
	DataSource ds = DB.getDataSource();
	
	public Result delete(String datasetId) {
		SQLTemplates templates = new PostgreSQLTemplates();
    	Configuration configuration = new Configuration(templates);
    	SQLQueryFactory queryFactory = new SQLQueryFactory(configuration, ds);
    	
    	queryFactory.delete(dataset)
    		.where(dataset.id.eq(datasetId))
    		.execute();
    	
    	queryFactory.delete(attachment1)
    		.where(attachment1.datasetId.eq(datasetId))
    		.execute();
    	
    	queryFactory.delete(subject1)
    		.where(subject1.datasetId.eq(datasetId))
    		.execute();
		
		return redirect(controllers.routes.Index.index());
	}
}
