package nl.idgis.querydsl;

import java.lang.reflect.AnnotatedElement;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathImpl;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.PathMetadataFactory;
import com.querydsl.core.types.Visitor;

public class TsVectorPath<T extends TsVector> extends TsVectorExpression<T> implements Path<T> {

	private static final long serialVersionUID = 7609840397239044032L;
	
	private final PathImpl<T> pathMixin;
	
	protected TsVectorPath(PathImpl<T> mixin) {
        super(mixin);
        this.pathMixin = mixin;
    }
	
	@SuppressWarnings("unchecked")
	public TsVectorPath(Path<?> parent, String property) {
		this((Class<? extends T>)TsVector.class, parent, property);
	}
	
	public TsVectorPath(Class<? extends T> type, Path<?> parent, String property) {
		this(type, PathMetadataFactory.forProperty(parent, property));
	}
	
	@SuppressWarnings("unchecked")
	public TsVectorPath(PathMetadata metadata) {
		this((Class<? extends T>) TsVector.class, metadata);
	}
	
	public TsVectorPath(Class<? extends T> type, PathMetadata metadata) {
		super(ExpressionUtils.path(type, metadata));
        this.pathMixin = (PathImpl<T>) mixin;
	}
	
	@SuppressWarnings("unchecked")
	public TsVectorPath(String var) {
		this((Class<? extends T>) TsVector.class, PathMetadataFactory.forVariable(var));
	}
	
	public TsVectorPath(Class<? extends T> type, String var) {
		this(type, PathMetadataFactory.forVariable(var));
	}

	@Override
	public <R, C> R accept(Visitor<R, C> v, C context) {
		return v.visit(pathMixin, context);
	}

	@Override
	public PathMetadata getMetadata() {
		return pathMixin.getMetadata();
	}

	@Override
	public Path<?> getRoot() {
		return pathMixin.getRoot();
	}

	@Override
	public AnnotatedElement getAnnotatedElement() {
		return pathMixin.getAnnotatedElement();
	}

}
