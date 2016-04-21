package controllers;

import static models.QMdType.mdType;
import static models.QMdTypeLabel.mdTypeLabel;
import static models.QDocument.document;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.inject.Inject;

import com.querydsl.core.Tuple;

import play.i18n.Lang;
import play.mvc.*;
import util.QueryDSL;
import views.html.*;

public class Index extends Controller {
	@Inject QueryDSL q;
	
	public Result index() {
		return ok(index.render());
	}
	
	public Result contact() {
		return ok(contact.render());
	}
	
	public Result about() {
		return ok(about.render());
	}
	
	public Result search() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		
		return q.withTransaction(tx -> {
			Lang curLang = Http.Context.current().lang();
			
			List<Tuple> documents = tx.select(document.title, document.date, document.creator, document.description, document.thumbnail, mdType.name)
					.from(document)
					.join(mdType).on(document.mdTypeId.eq(mdType.id))
					.where(document.date.isNotNull())
					.orderBy(document.date.desc())
					.limit(10)
					.fetch();
			
			List<Tuple> mdTypes = tx.select(mdType.name, mdTypeLabel.title)
					.from(mdType)
					.join(mdTypeLabel).on(mdType.id.eq(mdTypeLabel.mdTypeId))
					.where(mdTypeLabel.language.eq(curLang.code()))
					.fetch();
			
			return ok(search.render(mdTypes, documents, sdf));
		});
	}
	
	public Result browse() {
		return ok(browse.render());
	}
}