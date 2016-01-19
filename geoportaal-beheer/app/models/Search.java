package models;

import java.util.Date;

public class Search {
	
	private String text;
	private String supplier;
	private String status;
	private String format;
	private Date dateUpdateStart;
	private Date dateUpdateEnd;
	
	public Search() {
	}
	
	public Search(String text, String supplier, String status, String format, Date dateUpdateStart, Date dateUpdateEnd) {
		this.text = text;
		this.supplier = supplier;
		this.status = status;
		this.format = format;
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

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public Date getDateUpdateStart() {
		return dateUpdateStart;
	}

	public void setDateUpdateStart(Date dateUpdateStart) {
		this.dateUpdateStart = dateUpdateStart;
	}

	public Date getDateUpdateEnd() {
		return dateUpdateEnd;
	}

	public void setDateUpdateEnd(Date dateUpdateEnd) {
		this.dateUpdateEnd = dateUpdateEnd;
	}
	
	
}
