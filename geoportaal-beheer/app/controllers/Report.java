package controllers;

import static models.QUser.user;

import javax.inject.Inject;

import actions.DefaultAuthenticator;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import util.QueryDSL;

@Security.Authenticated(DefaultAuthenticator.class)
public class Report extends Controller {
	@Inject QueryDSL q;
	
	/**
	 * The rendering of the report page
	 * 
	 * @return the {@link Result} of the report page
	 */
	public Result renderReport() {
		return q.withTransaction(tx -> {
			// Fetches the role of the logged in user
			Integer roleId = tx.select(user.roleId)
				.from(user)
				.where(user.username.eq(session("username")))
				.fetchOne();
			
			return ok(views.html.report.render(roleId));
		});
	}
}
