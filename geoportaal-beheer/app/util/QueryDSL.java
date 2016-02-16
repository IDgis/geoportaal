package util;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.sql.PostgreSQLTemplates;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLCommonQueryFactory;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.SQLTemplates;
import com.querydsl.sql.dml.SQLDeleteClause;
import com.querydsl.sql.dml.SQLInsertClause;
import com.querydsl.sql.dml.SQLMergeClause;
import com.querydsl.sql.dml.SQLUpdateClause;

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
	
	public class Transaction implements SQLCommonQueryFactory<SQLQuery<?>,
		SQLDeleteClause, SQLUpdateClause, SQLInsertClause, SQLMergeClause> {
		
		private final SQLQueryFactory factory;
		
		private final Connection c;
		
		Transaction(SQLQueryFactory factory, Connection c) {
			this.factory = factory;
			this.c = c;
		}
		
		public SQLQuery<?> query() {
			return factory.query();
		}
		
		public <T> SQLQuery<T> select(Expression<T> expr) {
			return factory.select(expr);
		}
		
		public SQLQuery<Tuple> select(Expression<?>... exprs) {
			return factory.select(exprs);
		}
		
		public <T> SQLQuery<T> selectDistinct(Expression<T> expr) {
			return factory.selectDistinct(expr);
		}
		
		public SQLQuery<Tuple> selectDistinct(Expression<?>... exprs) {
			return factory.select(exprs);
		}
		
		public SQLQuery<Integer> selectZero() {
			return factory.selectZero();
		}
		
		public SQLQuery<Integer> selectOne() {
			return factory.selectOne();
		}
		
		public <T> SQLQuery<T> selectFrom(RelationalPath<T> expr) {
			return factory.selectFrom(expr);
		}
		
		@Override
		public final SQLDeleteClause delete(RelationalPath<?> path) {
			return factory.delete(path);
		}
		
		@Override
		public final SQLQuery<?> from(Expression<?> from) {
			return factory.from(from);
		}
		
		@Override
		public final SQLQuery<?> from(Expression<?>... args) {
			return factory.from(args);
		}
		
		@Override
		public final SQLQuery<?> from(SubQueryExpression<?> subQuery, Path<?> alias) {
			return factory.from(subQuery, alias);
		}
		
		@Override
		public final SQLInsertClause insert(RelationalPath<?> path) {
			return factory.insert(path);
		}
		
		@Override
		public final SQLMergeClause merge(RelationalPath<?> path) {
			return factory.merge(path);
		}
		
		@Override
		public final SQLUpdateClause update(RelationalPath<?> path) {
			return factory.update(path);
		}
		
		public <T> void refreshMaterializedView(RelationalPath<T> path) throws SQLException {
			try(Statement stmt = c.createStatement()) {
				 stmt.execute("refresh materialized view \"" 
						+ path.getSchemaName() + "\".\"" 
						+ path.getTableName() + "\"");
			}
		}
		
		public <T> void refreshMaterializedViewConcurrently(RelationalPath<T> path) throws SQLException {
			try(Statement stmt = c.createStatement()) {
				stmt.execute("refresh materialized view concurrently \"" 
						+ path.getSchemaName() + "\".\"" 
						+ path.getTableName() + "\"");
			 }
		}
	}
	
	@FunctionalInterface
	public interface TransactionCallable<A> {
		A call(Transaction tx) throws Exception;
	}
	
	@FunctionalInterface
	public interface TransactionRunnable {
		void run(Transaction tx) throws Exception;
	}
	
	private Transaction transaction(Connection c) {
		return new Transaction(new SQLQueryFactory(t, () -> c), c);
	}
	
	public void withTransaction(TransactionRunnable f) {
		d.withTransaction(c -> {
			try {
				f.run(transaction(c));
			} catch(Exception e) {
				throw new SQLException(e);
			}
		});
	}
	
	public <T> T withTransaction(TransactionCallable<T> f) {
		return d.withTransaction(c -> {
			try {				
				return f.call(transaction(c));
			} catch(Exception e) {
				throw new SQLException(e);
			}
		});
	}

}