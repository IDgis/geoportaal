package controllers;

import java.io.UnsupportedEncodingException;

import play.mvc.Controller;
import play.mvc.Result;

public class WOO extends Controller {
	
	public Result robots() throws UnsupportedEncodingException {
		StringBuilder sb = new StringBuilder();
		sb.append("Sitemap: /sitemaps/sitemapindex-diwoo-infocat015.xml");
		
		return ok(sb.toString().getBytes("UTF-8")).as("text/plain");
	}
	
	public Result sitemapindex(String id) {
		if(!id.equals("015")) return notFound("404 NOT FOUND").as("text/plain");
		
		return ok(views.xml.sitemap.render()).as("application/xml");
	}
}
