package controllers;

import javax.inject.Inject;

import play.Configuration;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

public class Application extends Controller {
	@Inject Configuration config;
	
	public Result index() {
		final String logo = config.getString("dashboard.client.logo");
		
		return ok(index.render(logo));
	}
}
