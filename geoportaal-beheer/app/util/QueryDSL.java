package util;

import java.sql.Connection;
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
	
	@FunctionalInterface
	public interface TransactionCallable<A> {
		A call(SQLQueryFactory tx) throws Exception;
	}
	
	@FunctionalInterface
	public interface TransactionRunnable {
		void run(SQLQueryFactory tx) throws Exception;
	}
	
	private SQLQueryFactory factory(Connection c) {
		return new SQLQueryFactory(t, () -> c);
	}
	
	public void withTransaction(TransactionRunnable f) {
		d.withTransaction(c -> {
			try {
				f.run(factory(c));
			} catch(Exception e) {
				throw new SQLException(e);
			}
		});
	}
	
	public <T> T withTransaction(TransactionCallable<T> f) {
		return d.withTransaction(c -> {
			try {				
				return f.call(factory(c));
			} catch(Exception e) {
				throw new SQLException(e);
			}
		});
	}

}