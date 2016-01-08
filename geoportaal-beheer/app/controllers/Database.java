package controllers;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sql.DataSource;

import com.querydsl.sql.Configuration;
import com.querydsl.sql.PostgreSQLTemplates;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.SQLTemplates;

@Singleton
public class Database {
	final SQLQueryFactory queryFactory;
	
	@Inject public Database(DataSource ds) {
		SQLTemplates templates = PostgreSQLTemplates.builder().printSchema().build();
		Configuration configuration = new Configuration(templates);
		queryFactory = new SQLQueryFactory(configuration, ds);
	}
}