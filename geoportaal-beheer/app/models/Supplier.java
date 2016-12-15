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
	private String dateStartSearch;
	private String dateEndSearch;
	
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
			String dateStartSearch, String dateEndSearch) {
		this.recordsChange = recordsChange;
		this.supplier = supplier;
		this.textSearch = textSearch;
		this.supplierSearch = supplierSearch;
		this.statusSearch = statusSearch;
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
