package models.portal;

public class ServiceInfo extends MetadataInfo {
	
	public ServiceInfo(InfoLast infoLast,
			InfoFromTime weekAgoInfo,
			InfoFromTime dayAgoInfo,
			InfoFromTime hourAgoInfo) {
		super(infoLast, weekAgoInfo, dayAgoInfo, hourAgoInfo);
	}
}
