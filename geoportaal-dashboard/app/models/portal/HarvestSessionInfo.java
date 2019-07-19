package models.portal;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HarvestSessionInfo {
	private String type;
	private LocalDateTime time;
	
	public HarvestSessionInfo(String type, LocalDateTime time) {
		this.type = type;
		this.time = time;
	}

	public String getType() {
		return type;
	}

	public LocalDateTime getTime() {
		return time;
	}
	
	public String getTimeFormatted() {
		try {
			return time.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
		} catch (IllegalArgumentException iae) {
			return "error";
		}
	}
}
