package models.portal;

public class InfoLast {
	private int countExtern;
	private int countIntern;
	
	public InfoLast(int countExtern, int countIntern) {
		this.countExtern = countExtern;
		this.countIntern = countIntern;
	}

	public int getCountExtern() {
		return countExtern;
	}

	public int getCountIntern() {
		return countIntern;
	}
}
