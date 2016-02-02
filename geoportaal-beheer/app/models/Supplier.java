package models;

import java.util.List;

public class Supplier {
	private List<String> recordsChange;
	private String supplier;
	
	public Supplier() {
	}
	
	public Supplier(List<String> recordsChange, String supplier) {
		this.recordsChange = recordsChange;
		this.supplier = supplier;
	}

	public List<String> getRecordsChange() {
		return recordsChange;
	}

	public void setRecordsChange(List<String> recordsChange) {
		this.recordsChange = recordsChange;
	}

	public String getSupplier() {
		return supplier;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}
	
	
}
