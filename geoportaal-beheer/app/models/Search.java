package models;

/**
 * The model class to store information about the search values
 * 
 * @author Sandro
 *
 */
public class Search {
	
	private String text;
	private String supplier;
	private String status;
	private String dateCreateStart;
	private String dateCreateEnd;
	private String dateUpdateStart;
	private String dateUpdateEnd;
	
	public Search() {
	}
	
	/**
	 * The constructor of the search class
	 * 
	 * @param text the text search value
	 * @param supplier the supplier search value
	 * @param status the status search value
	 * @param format the format search value
	 * @param dateUpdateStart the date start search value
	 * @param dateUpdateEnd the date end search value
	 */
	public Search(String text, String supplier, String status, String dateCreateStart, String dateCreateEnd,
			String dateUpdateStart, String dateUpdateEnd) {
		this.text = text;
		this.supplier = supplier;
		this.status = status;
		this.dateCreateStart = dateCreateStart;
		this.dateCreateEnd = dateCreateEnd;
		this.dateUpdateStart = dateUpdateStart;
		this.dateUpdateEnd = dateUpdateEnd;
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

	public String getDateCreateStart() {
		return dateCreateStart;
	}

	public void setDateCreateStart(String dateCreateStart) {
		this.dateCreateStart = dateCreateStart;
	}

	public String getDateCreateEnd() {
		return dateCreateEnd;
	}

	public void setDateCreateEnd(String dateCreateEnd) {
		this.dateCreateEnd = dateCreateEnd;
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
	
	
}