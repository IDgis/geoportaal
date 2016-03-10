package nl.idgis.geoportaal.conversie;

public class Label {

	private String value;
	private String table;
	private String labelTable;

	/**
	 * Puntkomma's in {@code value} worden vervangen door dubbele punten. Dit gebeurt niet bij {@link Label#setValue}.
	 * @param value
	 * @param table
	 */
	public Label(String value, String table) {
		this.value = value;
		this.table = table;
		this.labelTable = table + "_label";

		if (this.value != null)
			this.value = value.replace(';', ':');
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String getLabelTable() {
		return labelTable;
	}

	public void setLabelTable(String labelTable) {
		this.labelTable = labelTable;
	}

	@Override
	public String toString() {
		return "Label [value=" + value + ", table=" + table + ", labelTable=" + labelTable + "]";
	}

}
