package models;

import java.util.List;

public class Delete {
	
	private List<String> recordsToDel;
	private String permDel;
	private String textSearch;
	private String supplierSearch;
	private String statusSearch;
	private String mdFormatSearch;
	private String dateStartSearch;
	private String dateEndSearch;
	
	public Delete() {
	}
	
	public Delete(List<String> recordsToDel, String permDel, String textSearch, String supplierSearch, String statusSearch, 
			String mdFormatSearch, String dateStartSearch, String dateEndSearch) {
		this.recordsToDel = recordsToDel;
		this.permDel = permDel;
		this.textSearch = textSearch;
		this.supplierSearch = supplierSearch;
		this.statusSearch = statusSearch;
		this.mdFormatSearch = mdFormatSearch;
		this.dateStartSearch = dateStartSearch;
		this.dateEndSearch = dateEndSearch;
	}

	public List<String> getRecordsToDel() {
		return recordsToDel;
	}

	public void setRecordsToDel(List<String> recordsToDel) {
		this.recordsToDel = recordsToDel;
	}

	public String getPermDel() {
		return permDel;
	}

	public void setPermDel(String permDel) {
		this.permDel = permDel;
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
