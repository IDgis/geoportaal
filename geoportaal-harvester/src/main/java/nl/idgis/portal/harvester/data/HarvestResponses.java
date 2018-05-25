package nl.idgis.portal.harvester.data;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.MultiStatus;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.client.methods.DavMethod;
import org.apache.jackrabbit.webdav.client.methods.PropFindMethod;

public class HarvestResponses {
	
	private static final String trustedHeader = System.getenv("TRUSTED_HEADER");
	
	private MultiStatusResponse[] responses;
	
	public HarvestResponses(String url) throws Exception {
		HostConfiguration hostConfig = new HostConfiguration();
		HttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
		HttpConnectionManagerParams params = new HttpConnectionManagerParams();
		connectionManager.setParams(params);
		HttpClient client = new HttpClient(connectionManager);
		client.setHostConfiguration(hostConfig);
		
		DavMethod pFind = new PropFindMethod(url, DavConstants.PROPFIND_ALL_PROP, DavConstants.DEPTH_1);
		
		if(trustedHeader != null) {
			pFind.addRequestHeader(trustedHeader, "1");
		}
		
		client.executeMethod(pFind);
		MultiStatus multiStatus = pFind.getResponseBodyAsMultiStatus();
		this.responses = multiStatus.getResponses();
	}

	public MultiStatusResponse[] getResponses() {
		return responses;
	}
}
