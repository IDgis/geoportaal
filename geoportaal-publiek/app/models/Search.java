package models;

public class Search {
	private String text;
	private String elementsString;
	private String page;
	
	public Search() {
	}
	
	public Search(String text, String elementsString, String page) {
		super();
		this.text = text;
		this.elementsString = elementsString;
		this.page = page;
	}
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	public String getElementsString() {
		return elementsString;
	}

	public void setElementsString(String elementsString) {
		this.elementsString = elementsString;
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}
}