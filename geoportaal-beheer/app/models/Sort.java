package models;

import java.util.List;

/**
 * The model class to store the sorting information
 * 
 * @author Sandro
 *
 */
public class Sort {
	private String text;
	private String supplier;
	private String status;
	private String format;
	private String dateUpdateStart;
	private String dateUpdateEnd;
	private String sort;
	private List<String> recordsChecked;
	
	public Sort() {
	}
	
	/**
	 * The constructor of the sort class
	 * 
	 * @param text the search value of the text field
	 * @param supplier the search value of the supplier field
	 * @param status the search value of the status field
	 * @param format the search value of the format field
	 * @param dateUpdateStart the search value of the date start field
	 * @param dateUpdateEnd the search value of the date end field
	 * @param sort the sort type value
	 * @param recordsChecked the UUID's of the records selected
	 */
	public Sort(String text, String supplier, String status, String format, String dateUpdateStart, String dateUpdateEnd, String sort, 
		List<String> recordsChecked) {
		
		this.text = text;
		this.supplier = supplier;
		this.status = status;
		this.format = format;
		this.dateUpdateStart = dateUpdateStart;
		this.dateUpdateEnd = dateUpdateEnd;
		this.sort = sort;
		this.recordsChecked = recordsChecked;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getSupplier() {
		return supplier;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getDateUpdateStart() {
		return dateUpdateStart;
	}

	public void setDateUpdateStart(String dateUpdateStart) {
		this.dateUpdateStart = dateUpdateStart;
	}

	public String getDateUpdateEnd() {
		return dateUpdateEnd;
	}

	public void setDateUpdateEnd(String dateUpdateEnd) {
		this.dateUpdateEnd = dateUpdateEnd;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public List<String> getRecordsChecked() {
		return recordsChecked;
	}

	public void setRecordsChecked(List<String> recordsChecked) {
		this.recordsChecked = recordsChecked;
	}
}
