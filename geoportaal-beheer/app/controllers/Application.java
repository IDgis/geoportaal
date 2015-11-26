package controllers;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.sql.DataSource;

import com.querydsl.core.QueryFactory;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.PostgreSQLTemplates;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.SQLTemplates;

import models.*;
import play.db.DB;
import play.mvc.Controller;
import play.mvc.Result;

public class Application extends Controller {
	DataSource ds = DB.getDataSource();
	
    public Result index() throws SQLException {
    	Calendar cal = Calendar.getInstance();
    	
    	QDataset dataset = new QDataset("d");
    	QDefaults def = new QDefaults("def");
    	QSubject subj = new QSubject("s");
    	QAttachment att = new QAttachment("a");
    	
    	Connection connection = DB.getConnection(false);
    	SQLTemplates templates = new PostgreSQLTemplates();
    	Configuration configuration = new Configuration(templates);
    	SQLQueryFactory queryFactory = new SQLQueryFactory(configuration, ds);
    	
    	List<String> publishers = queryFactory.select(def.publisher)
    			.from(def)
    			.fetch();
    	
    	List<String> languages = queryFactory.select(def.language)
    			.from(def)
    			.fetch();
    	
    	List<BigDecimal> westCrd = queryFactory.select(def.westBoundLongitude)
    			.from(def)
    			.fetch();
    	
    	List<Timestamp> dateCovUnt = queryFactory.select(def.temporalCoverageUntil)
    			.from(def)
    			.fetch();
    	
    	String formattedDate = "";
    	for(Timestamp date : dateCovUnt) {
    		formattedDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date(date.getTime()));
    	}
    			
    	return ok(views.html.index.render(publishers, languages, westCrd, formattedDate));
    }
}