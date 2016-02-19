package models;

import java.util.List;

public class Supplier {
	private List<String> recordsChange;
	private String supplier;
	private String textSearch;
	private String supplierSearch;
	private String statusSearch;
	private String mdFormatSearch;
	private String dateStartSearch;
	private String dateEndSearch;
	
	public Supplier() {
	}
	
	public Supplier(List<String> recordsChange, String supplier, String textSearch, String supplierSearch, String statusSearch, 
			String mdFormatSearch, String dateStartSearch, String dateEndSearch) {
		this.recordsChange = recordsChange;
		this.supplier = supplier;
		this.textSearch = textSearch;
		this.supplierSearch = supplierSearch;
		this.statusSearch = statusSearch;
		this.mdFormatSearch = mdFormatSearch;
		this.dateStartSearch = dateStartSearch;
		this.dateEndSearch = dateEndSearch;
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

	public String getTextSearch() {
		return textSearch;
	}

	public void setTextSearch(String textSearch) {
		this.textSearch = textSearch;
	}

	public String getSupplierSearch() {
		return supplierSearch;
	}

	public void setSupplierSearch(String supplierSearch) {
		this.supplierSearch = supplierSearch;
	}

	public String getStatusSearch() {
		return statusSearch;
	}

	public void setStatusSearch(String statusSearch) {
		this.statusSearch = statusSearch;
	}

	public String getMdFormatSearch() {
		return mdFormatSearch;
	}

	public void setMdFormatSearch(String mdFormatSearch) {
		this.mdFormatSearch = mdFormatSearch;
	}

	public String getDateStartSearch() {
		return dateStartSearch;
	}

	public void setDateStartSearch(String dateStartSearch) {
		this.dateStartSearch = dateStartSearch;
	}

	public String getDateEndSearch() {
		return dateEndSearch;
	}

	public void setDateEndSearch(String dateEndSearch) {
		this.dateEndSearch = dateEndSearch;
	}
	
	
}
