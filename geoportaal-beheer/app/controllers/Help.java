package controllers;

import actions.DefaultAuthenticator;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.*;

/**
 * The class for the help entity
 * 
 * @author Sandro
 *
 */
@Security.Authenticated(DefaultAuthenticator.class)
public class Help extends Controller{
	
	/**
	 * The rendering of the help page
	 * 
	 * @return the {@link Result} of the help page
	 */
	public Result renderHelp() {
		return ok(views.html.help.render());
	}
}
