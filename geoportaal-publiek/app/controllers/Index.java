package controllers;

import static models.QMdType.mdType;
import static models.QMdTypeLabel.mdTypeLabel;

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
		return q.withTransaction(tx -> {
			Lang curLang = Http.Context.current().lang();
			
			List<Tuple> mdTypes = tx.select(mdType.name, mdTypeLabel.title)
				.from(mdType)
				.join(mdTypeLabel).on(mdType.id.eq(mdTypeLabel.mdTypeId))
				.where(mdTypeLabel.language.eq(curLang.code()))
				.fetch();
			
			return ok(search.render(mdTypes));
		});
	}
	
	public Result browse() {
		return ok(browse.render());
	}
}