package models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PublisherTask {
	private String type;
	private String name;
	private LocalDateTime time;
	private boolean success;
	
	private static final String HARVESTSQL = "select ds.name as name from publisher.harvest_job hj "
			+ "join publisher.data_source ds on ds.id = hj.data_source_id "
			+ "where hj.job_id = ?";
	
	private static final String IMPORTSQL = "select ij.job_id, d.name as name from publisher.import_job ij "
			+ "join publisher.dataset d on d.id = ij.dataset_id "
			+ "where ij.job_id = ?";
	
	private static final String SERVICESQL = "select gl.name as name, sj.published published "
			+ "from publisher.service_job sj "
			+ "join publisher.service s on s.id = sj.service_id "
			+ "join publisher.generic_layer gl on gl.id = s.generic_layer_id "
			+ "where sj.job_id = ?";
	
	private static final String UNKNOWN_NAME = "unknown";
	
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
	
	public static String getSQL(String type) {
		if("HARVEST".equals(type)) {
			return HARVESTSQL;
		} else if("IMPORT".equals(type)) {
			return IMPORTSQL;
		} else if("SERVICE".equals(type)) {
			return SERVICESQL;
		} else {
			return null;
		}
	}
	
	public static String getUnknownName() {
		return UNKNOWN_NAME;
	}
}
