package controllers;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import models.DataSource;
import play.Configuration;
import play.libs.F.Promise;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

public class Application extends Controller {
	@Inject Configuration config;
	@Inject WSClient ws;
	
	public Promise<Result> index() {
		final String logo = config.getString("dashboard.client.logo");
		final String url = config.getString("dashboard.provider.connection.url");
		
		Promise<List<DataSource>> dataSources = getDataSources(url);
		
		return dataSources.map(listDataSources -> {
			return ok(index.render(logo, listDataSources));
		});
	}
	
	private Promise<List<DataSource>> getDataSources(String url) {
		WSRequest request = ws.url(url).setFollowRedirects(true);
		return request.get().map(response -> {
			Gson gson = new GsonBuilder().create();
			String json = new String(response.asByteArray());
			
			try {
				DataSource[] dataSources = gson.fromJson(json, DataSource[].class);
				
				if(dataSources != null) {
					List<DataSource> listDataSources = Arrays.asList(dataSources);
					
					return listDataSources;
				}
			} catch(JsonSyntaxException jse) {
				jse.printStackTrace();
			}
			
			return Collections.emptyList();
		});
	}
}
