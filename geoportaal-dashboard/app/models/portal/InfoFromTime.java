package models.portal;

public class InfoFromTime {
	private CountDifference countDifferenceExtern;
	private CountDifference countDifferenceIntern;
	
	public InfoFromTime(CountDifference countDifferenceExtern, CountDifference countDifferenceIntern) {
		this.countDifferenceExtern = countDifferenceExtern;
		this.countDifferenceIntern = countDifferenceIntern;
	}

	public CountDifference getCountDifferenceExtern() {
		return countDifferenceExtern;
	}

	public CountDifference getCountDifferenceIntern() {
		return countDifferenceIntern;
	}
}
