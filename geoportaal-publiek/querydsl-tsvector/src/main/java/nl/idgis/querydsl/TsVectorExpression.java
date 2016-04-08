package nl.idgis.querydsl;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.SimpleExpression;

public abstract class TsVectorExpression<T extends TsVector> extends SimpleExpression<T> {
	
	private static final long serialVersionUID = 2302357736274141901L;

	public TsVectorExpression(Expression<T> mixin) {
		super(mixin);
	}
	
	public BooleanExpression query(String language, String query) {
		return Expressions.booleanTemplate("{0} @@ to_tsquery('" + language + "', {1})", mixin, query);
	}
}
