package util;

import java.sql.SQLException;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.querydsl.sql.PostgreSQLTemplates;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.SQLTemplates;

import play.db.Database;

@Singleton
public class QueryDSL {
	
	private final Database d;
	
	private final SQLTemplates t;
	
	@Inject
	public QueryDSL(Database d) {
		this.d = d;
		
		t = PostgreSQLTemplates.builder().printSchema().build();
	}
	
	public interface TransactionCallable<A> {
		A call(SQLQueryFactory tx) throws Exception;
	}
	
	public <T> T withTransaction(TransactionCallable<T> f) {
		return d.withTransaction(c -> {
			try {				
				return f.call(new SQLQueryFactory(t, () -> c));
			} catch(SQLException e) {
				throw e;
			} catch(Exception e) {
				throw new SQLException(e);
			}
		});
	}
}