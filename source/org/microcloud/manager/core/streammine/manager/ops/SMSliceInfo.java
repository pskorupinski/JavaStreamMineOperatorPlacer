package org.microcloud.manager.core.streammine.manager.ops;

import java.util.concurrent.atomic.AtomicInteger;

public class SMSliceInfo extends SMTaskChild {
	
	public enum SliceStateType {
		NONEXISTENT,
		DEPLOYED,
		LAUNCHED
	}

	private Integer sliceUid;
	private SMOperatorInfo operator;
	private AtomicInteger awaitedEOSSignalsCounter;
	private int hostBusyTimesId;
	
	private SliceStateType sliceState = SliceStateType.NONEXISTENT;
	
	public SMSliceInfo(SMTaskInfo smTaskInfo, SMOperatorInfo smOperatorInfo, Integer sliceUid, Integer hostBusyTimesId) {
		super(smTaskInfo);
		this.operator = smOperatorInfo;
		this.awaitedEOSSignalsCounter = new AtomicInteger(smOperatorInfo.getAwaitedEOSSignalsPerSlice());
		this.sliceUid = sliceUid;
		this.hostBusyTimesId = hostBusyTimesId;
	}
	
	public boolean decrementCounterGetIsZero() {
		return (awaitedEOSSignalsCounter.decrementAndGet() == 0);
	}
	
	public Integer getSliceUid() {
		return this.sliceUid;
	}
	
	public Integer getHostBusyTimesId() {
		return this.hostBusyTimesId;
	}
	
	public boolean remove() {
		return (operator.removeSlice(sliceUid) == 0);
	}
	
	public SMOperatorInfo getOperator() {
		return operator;
	}

	public SliceStateType getSliceState() {
		return sliceState;
	}
	public void setSliceState(SliceStateType sliceState) {
		this.sliceState = sliceState;
	}
	
}
