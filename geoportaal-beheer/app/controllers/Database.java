package controllers;

import javax.inject.Singleton;
import javax.sql.DataSource;

import com.querydsl.sql.Configuration;
import com.querydsl.sql.PostgreSQLTemplates;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.SQLTemplates;

import play.db.DB;

@Singleton
public class Database {
	DataSource ds = DB.getDataSource();
	SQLTemplates templates = new PostgreSQLTemplates();
	Configuration configuration = new Configuration(templates);
	SQLQueryFactory queryFactory = new SQLQueryFactory(configuration, ds);
}
