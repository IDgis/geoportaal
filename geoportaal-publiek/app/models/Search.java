package models;

public class Search {
	private String elementsString;
	private String text;
	
	public String getElementsString() {
		return elementsString;
	}

	public void setElementsString(String elementsString) {
		this.elementsString = elementsString;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Search(String elementsString, String text) {
		super();
		this.elementsString = elementsString;
		this.text = text;
	}
}