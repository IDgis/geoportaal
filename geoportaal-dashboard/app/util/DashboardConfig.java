package util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.inject.Inject;

import play.Configuration;

public class DashboardConfig {
	private final String logo;
	
	private final String dateTimeNow;
	
	@Inject
	public DashboardConfig(Configuration config) {
		this.logo = config.getString("dashboard.client.logo");
		this.dateTimeNow = 
				LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
	}
	
	public String getLogo() {
		return logo;
	}

	public String getDateTimeNow() {
		return dateTimeNow;
	}
}
