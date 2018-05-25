package nl.idgis.portal.harvester.util;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.support.TransactionTemplate;

import com.querydsl.sql.Configuration;
import com.querydsl.sql.PostgreSQLTemplates;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.SQLTemplates;

public class Config {
	
	private static final String databaseDriver = System.getenv("DB_DRIVER");
	private static final String databaseUrl = System.getenv("DB_URL");
	private static final String databaseUser = System.getenv("DB_USER");
	private static final String databasePassword = System.getenv("DB_PASSWORD");
	
	private DriverManagerDataSource dataSource;
	private TransactionTemplate transaction;
	private SQLQueryFactory queryFactory;
	
	public Config() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(databaseDriver);
		dataSource.setUrl(databaseUrl);
		dataSource.setUsername(databaseUser);
		dataSource.setPassword(databasePassword);
		
		SQLTemplates templates = PostgreSQLTemplates.builder().printSchema().build();
		Configuration configuration = new Configuration(templates);
		SQLQueryFactory queryFactory = new SQLQueryFactory(configuration, () -> DataSourceUtils.getConnection(dataSource));
		
		TransactionTemplate transaction = 
				new TransactionTemplate(new DataSourceTransactionManager(dataSource));
		
		this.dataSource = dataSource;
		this.transaction = transaction;
		this.queryFactory = queryFactory;
	}

	public DriverManagerDataSource getDataSource() {
		return dataSource;
	}

	public TransactionTemplate getTransaction() {
		return transaction;
	}

	public SQLQueryFactory getQueryFactory() {
		return queryFactory;
	}
}
