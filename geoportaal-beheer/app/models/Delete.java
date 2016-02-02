package models;

import java.util.List;

public class Delete {
	
	private List<String> recordsToDel;
	private String permDel;
	
	public Delete() {
	}
	
	public Delete(List<String> recordsToDel, String permDel) {
		this.recordsToDel = recordsToDel;
		this.permDel = permDel;
	}

	public List<String> getRecordsToDel() {
		return recordsToDel;
	}

	public void setRecordsToDel(List<String> recordsToDel) {
		this.recordsToDel = recordsToDel;
	}

	public String getPermDel() {
		return permDel;
	}

	public void setPermDel(String permDel) {
		this.permDel = permDel;
	}
	
	
}
