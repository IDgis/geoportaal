package models;

import java.util.List;

public class Status {
	private List<String> recordsChange;
	private String status;
	
	public Status() {
	}
	
	public Status(List<String> recordsChange, String status) {
		this.recordsChange = recordsChange;
		this.status = status;
	}

	public List<String> getRecordsChange() {
		return recordsChange;
	}

	public void setRecordsChange(List<String> recordsChange) {
		this.recordsChange = recordsChange;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
