package controllers;

import static models.QUser.user;

import javax.inject.Inject;

import actions.DefaultAuthenticator;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import util.QueryDSL;
import views.html.*;

/**
 * The class for the help entity
 * 
 * @author Sandro
 *
 */
@Security.Authenticated(DefaultAuthenticator.class)
public class Help extends Controller{
	@Inject QueryDSL q;
	
	/**
	 * The rendering of the help page
	 * 
	 * @return the {@link Result} of the help page
	 */
	public Result renderHelp() {
		return q.withTransaction( tx -> {
			// Fetches the role of the logged in user
			Integer roleId = tx.select(user.roleId)
				.from(user)
				.where(user.username.equalsIgnoreCase(session("username")))
				.fetchOne();
			
			return ok(views.html.help.render(roleId));
		});
	}
}
