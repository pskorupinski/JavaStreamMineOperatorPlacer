package org.microcloud.manager.core.streammine.manager.model;

public class LaunchableSlice {
	private Integer sliceUid;
	private Integer timeLimitMin;
	
	public LaunchableSlice(Integer sliceUid, Integer timeLimitMin) {
		this.sliceUid = sliceUid;
		this.timeLimitMin = timeLimitMin;
	}
	
	public Integer getSliceUid() {
		return this.sliceUid;
	}
	public Integer getTimeLimitMin() {
		return this.timeLimitMin;
	}
}
