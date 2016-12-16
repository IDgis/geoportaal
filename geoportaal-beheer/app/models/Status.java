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
	private String dateCreateStartSearch;
	private String dateCreateEndSearch;
	private String dateUpdateStartSearch;
	private String dateUpdateEndSearch;
	
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
			String dateCreateStartSearch, String dateCreateEndSearch, String dateUpdateStartSearch, 
			String dateUpdateEndSearch) {
		this.recordsChange = recordsChange;
		this.status = status;
		this.textSearch = textSearch;
		this.supplierSearch = supplierSearch;
		this.statusSearch = statusSearch;
		this.dateCreateStartSearch = dateCreateStartSearch;
		this.dateCreateEndSearch = dateCreateEndSearch;
		this.dateUpdateStartSearch = dateUpdateStartSearch;
		this.dateUpdateEndSearch = dateUpdateEndSearch;
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

	public String getDateCreateStartSearch() {
		return dateCreateStartSearch;
	}

	public void setDateCreateStartSearch(String dateCreateStartSearch) {
		this.dateCreateStartSearch = dateCreateStartSearch;
	}

	public String getDateCreateEndSearch() {
		return dateCreateEndSearch;
	}

	public void setDateCreateEndSearch(String dateCreateEndSearch) {
		this.dateCreateEndSearch = dateCreateEndSearch;
	}

	public String getDateUpdateStartSearch() {
		return dateUpdateStartSearch;
	}

	public void setDateUpdateStartSearch(String dateUpdateStartSearch) {
		this.dateUpdateStartSearch = dateUpdateStartSearch;
	}

	public String getDateUpdateEndSearch() {
		return dateUpdateEndSearch;
	}

	public void setDateUpdateEndSearch(String dateUpdateEndSearch) {
		this.dateUpdateEndSearch = dateUpdateEndSearch;
	}
}
