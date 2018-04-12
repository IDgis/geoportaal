package models;

public class DataSource {
	private String name;
	private DataSourceStatus status;
	
	public String getName() {
		return name;
	}

	public DataSourceStatus getStatus() {
		return status;
	}

	@Override
	public String toString() {
		return name + " - (" + status + ")";
	}
}
