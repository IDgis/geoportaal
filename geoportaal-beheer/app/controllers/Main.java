package controllers;

import play.mvc.Controller;
import play.mvc.Result;

import views.html.*;

public class Main extends Controller{
	
	public Result renderHelp() {
		return ok(views.html.help.render());
	}
}
