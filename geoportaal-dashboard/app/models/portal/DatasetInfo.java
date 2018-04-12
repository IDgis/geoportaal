package models.portal;

public class DatasetInfo extends MetadataInfo {
	
	public DatasetInfo(InfoLast infoLast,
			InfoFromTime weekAgoInfo,
			InfoFromTime dayAgoInfo,
			InfoFromTime hourAgoInfo) {
		super(infoLast, weekAgoInfo, dayAgoInfo, hourAgoInfo);
	}
}
