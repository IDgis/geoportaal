package models.portal;

public class CountDifference {
	private CountDifferenceType type;
	private int value;
	
	public CountDifference(CountDifferenceType type, int value) {
		this.type = type;
		this.value = value;
	}

	public CountDifferenceType getType() {
		return type;
	}

	public int getValue() {
		return value;
	}
}
