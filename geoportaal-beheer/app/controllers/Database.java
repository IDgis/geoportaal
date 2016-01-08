package controllers;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sql.DataSource;

import com.querydsl.sql.Configuration;
import com.querydsl.sql.PostgreSQLTemplates;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.SQLTemplates;

import play.db.DB;

@Singleton
public class Database {
	final DataSource ds;
	final SQLQueryFactory queryFactory;
	
	@Inject public Database() {
		SQLTemplates templates = PostgreSQLTemplates.builder().printSchema().build();
		Configuration configuration = new Configuration(templates);
		ds = DB.getDataSource();
		queryFactory = new SQLQueryFactory(configuration, ds);
	}
}