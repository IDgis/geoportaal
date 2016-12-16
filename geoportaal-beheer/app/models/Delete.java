package models;

import java.util.List;

/**
 * The model class to store information for deleting records
 * 
 * @author Sandro
 *
 */
public class Delete {
	
	private List<String> recordsToDel;
	private String permDel;
	private String textSearch;
	private String supplierSearch;
	private String statusSearch;
	private String dateCreateStartSearch;
	private String dateCreateEndSearch;
	private String dateUpdateStartSearch;
	private String dateUpdateEndSearch;
	
	public Delete() {
	}
	
	/**
	 * The constructor of the delete class
	 * 
	 * @param recordsToDel the UUID's of the records to be deleted
	 * @param permDel flag if the records should be permanently deleted
	 * @param textSearch the search value of the text field
	 * @param supplierSearch the search value of the supplier field
	 * @param statusSearch the search value of the status field
	 * @param mdFormatSearch the search value of the format field
	 * @param dateStartSearch the search value of the date start field
	 * @param dateEndSearch the search value of the date end field
	 */
	public Delete(List<String> recordsToDel, String permDel, String textSearch, String supplierSearch, String statusSearch, 
			String dateCreateStartSearch, String dateCreateEndSearch, String dateUpdateStartSearch, 
			String dateUpdateEndSearch) {
		this.recordsToDel = recordsToDel;
		this.permDel = permDel;
		this.textSearch = textSearch;
		this.supplierSearch = supplierSearch;
		this.statusSearch = statusSearch;
		this.dateCreateStartSearch = dateCreateStartSearch;
		this.dateCreateEndSearch = dateCreateEndSearch;
		this.dateUpdateStartSearch = dateUpdateStartSearch;
		this.dateUpdateEndSearch = dateUpdateEndSearch;
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
