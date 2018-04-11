package models.portal;

public class HarvestInfo {
	private DatasetInfo datasetInfo;
	private ServiceInfo serviceInfo;
	private DCInfo dcInfo;
	
	public HarvestInfo(DatasetInfo datasetInfo, ServiceInfo serviceInfo, DCInfo dcInfo) {
		this.datasetInfo = datasetInfo;
		this.serviceInfo = serviceInfo;
		this.dcInfo = dcInfo;
	}

	public DatasetInfo getDatasetInfo() {
		return datasetInfo;
	}

	public ServiceInfo getServiceInfo() {
		return serviceInfo;
	}

	public DCInfo getDcInfo() {
		return dcInfo;
	}
}
