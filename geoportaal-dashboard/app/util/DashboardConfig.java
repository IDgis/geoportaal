package util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.inject.Inject;

import play.Configuration;

public class DashboardConfig {
	
	private final Boolean deploymentAcceptance;
	private final String logo;
	private final String dateTimeNow;
	
	@Inject
	public DashboardConfig(Configuration config) {
		this.deploymentAcceptance = config.getBoolean("dashboard.deployment.acceptance");
		this.logo = config.getString("dashboard.client.logo");
		this.dateTimeNow = 
				LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
	}
	
	public Boolean getDeploymentAcceptance() {
		return deploymentAcceptance;
	}

	public String getLogo() {
		return logo;
	}

	public String getDateTimeNow() {
		return dateTimeNow;
	}
}
