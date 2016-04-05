package org.microcloud.manager.core.model.streammine;

import java.io.Serializable;

public class SliceHost implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String host;
	private Integer sliceId;
	
	private Integer hostBusyTimesId;
	
	/*
	 * To be filled in Manager
	 */
	
	private Integer sliceUid = 0;
	
	public SliceHost(String host, Integer sliceId, Integer hostBusyTimesId) {
		this.host = host;
		this.sliceId = sliceId;
		this.hostBusyTimesId = hostBusyTimesId;
	}

	public String getHost() {
		return host;
	}
	public Integer getSliceId() {
		return sliceId;
	}
	public Integer getHostBusyTimesId() {
		return hostBusyTimesId;
	}
	
	public void setSliceUid(Integer sliceUid) {
		this.sliceUid = sliceUid;
	}
	public Integer getSliceUid() {
		return this.sliceUid;
	}
	
}
