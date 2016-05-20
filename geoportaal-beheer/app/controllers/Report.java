package controllers;

import actions.DefaultAuthenticator;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

@Security.Authenticated(DefaultAuthenticator.class)
public class Report extends Controller {
	
	/**
	 * The rendering of the report page
	 * 
	 * @return the {@link Result} of the report page
	 */
	public Result renderReport() {
		return ok(views.html.report.render());
	}
}
