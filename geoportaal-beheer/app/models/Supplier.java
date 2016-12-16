package models;

import java.util.List;

/**
 * The model class to store information for changing the supplier of records
 * 
 * @author Sandro
 *
 */
public class Supplier {
	private List<String> recordsChange;
	private String supplier;
	private String textSearch;
	private String supplierSearch;
	private String statusSearch;
	private String dateCreateStartSearch;
	private String dateCreateEndSearch;
	private String dateUpdateStartSearch;
	private String dateUpdateEndSearch;
	
	public Supplier() {
	}
	
	/**
	 * The constructor of the supplier class
	 * 
	 * @param recordsChange the UUID's of the records about to be changed
	 * @param supplier the new supplier of the records
	 * @param textSearch the search value of the text field
	 * @param supplierSearch the search value of the supplier field
	 * @param statusSearch the search value of the status field
	 * @param mdFormatSearch the search value of the format field
	 * @param dateStartSearch the search value of the date start field
	 * @param dateEndSearch the search value of the date end field
	 */
	public Supplier(List<String> recordsChange, String supplier, String textSearch, String supplierSearch, String statusSearch, 
			String dateCreateStartSearch, String dateCreateEndSearch, String dateUpdateStartSearch, 
			String dateUpdateEndSearch) {
		this.recordsChange = recordsChange;
		this.supplier = supplier;
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
