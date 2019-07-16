package models.portal;

public class InfoFromTime {
	private CountDifference countDifferenceExtern;
	private CountDifference countDifferenceIntern;
	private CountDifference countDifferenceArchived;
	
	public InfoFromTime(
			CountDifference countDifferenceExtern, 
			CountDifference countDifferenceIntern,
			CountDifference countDifferenceArchived) {
		this.countDifferenceExtern = countDifferenceExtern;
		this.countDifferenceIntern = countDifferenceIntern;
		this.countDifferenceArchived = countDifferenceArchived;
	}

	public CountDifference getCountDifferenceExtern() {
		return countDifferenceExtern;
	}

	public CountDifference getCountDifferenceIntern() {
		return countDifferenceIntern;
	}

	public CountDifference getCountDifferenceArchived() {
		return countDifferenceArchived;
	}
}
