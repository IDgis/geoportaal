package controllers;

import java.util.UUID;

import models.DublinCore;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

public class Add extends Controller {
	
	public Result add() {
		String uuid = UUID.randomUUID().toString();

		return ok(views.html.addRecord.render(uuid));
	}
	
	public Result submit() {
		return ok(views.html.index.render());
	}
}
