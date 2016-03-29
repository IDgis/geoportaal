package models;

import java.util.List;

/**
 * The model class to store information for changing the status of records
 * 
 * @author Sandro
 *
 */
public class Status {
	private List<String> recordsChange;
	private String status;
	private String textSearch;
	private String supplierSearch;
	private String statusSearch;
	private String mdFormatSearch;
	private String dateStartSearch;
	private String dateEndSearch;
	
	public Status() {
	}
	
	/**
	 * The constructor of the status class
	 * 
	 * @param recordsChange the UUID's of the records about to be changed
	 * @param status the new status of the records
	 * @param textSearch the search value of the text field
	 * @param supplierSearch the search value of the supplier field
	 * @param statusSearch the search value of the status field
	 * @param mdFormatSearch the search value of the format field
	 * @param dateStartSearch the search value of the date start field
	 * @param dateEndSearch the search value of the date end field
	 */
	public Status(List<String> recordsChange, String status, String textSearch, String supplierSearch, String statusSearch, 
			String mdFormatSearch, String dateStartSearch, String dateEndSearch) {
		this.recordsChange = recordsChange;
		this.status = status;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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
