package controllers;

import actions.DefaultAuthenticator;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.*;

@Security.Authenticated(DefaultAuthenticator.class)
public class Dashboard extends Controller{
	
	public Result renderDashboard() {
		return ok(views.html.dashboard.render());
	}
}
