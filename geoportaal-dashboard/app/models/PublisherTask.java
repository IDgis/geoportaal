package models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PublisherTask {
	private String type;
	private String name;
	private LocalDateTime time;
	private boolean success;
	
	public PublisherTask(String type, String name, LocalDateTime time, boolean success) {
		this.type = type;
		this.name = name;
		this.time = time;
		this.success = success;
	}

	public String getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public LocalDateTime getTime() {
		return time;
	}

	public String getTimeFormatted() {
		return time.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
	}

	public boolean isSuccess() {
		return success;
	}
}
