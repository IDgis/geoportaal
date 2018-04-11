package models.portal;

public class MetadataInfo {
	private InfoLast infoLast;
	private InfoFromTime weekAgoInfo;
	private InfoFromTime dayAgoInfo;
	private InfoFromTime hourAgoInfo;
	
	public MetadataInfo(InfoLast infoLast, InfoFromTime weekAgoInfo, InfoFromTime dayAgoInfo,
			InfoFromTime hourAgoInfo) {
		this.infoLast = infoLast;
		this.weekAgoInfo = weekAgoInfo;
		this.dayAgoInfo = dayAgoInfo;
		this.hourAgoInfo = hourAgoInfo;
	}

	public InfoLast getInfoLast() {
		return infoLast;
	}

	public InfoFromTime getWeekAgoInfo() {
		return weekAgoInfo;
	}

	public InfoFromTime getDayAgoInfo() {
		return dayAgoInfo;
	}

	public InfoFromTime getHourAgoInfo() {
		return hourAgoInfo;
	}
}
