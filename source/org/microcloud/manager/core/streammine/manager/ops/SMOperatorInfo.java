package org.microcloud.manager.core.streammine.manager.ops;

import java.util.ArrayList;
import java.util.List;

public class SMOperatorInfo extends SMTaskChild {
	
	public enum OperatorStateType {
		NONEXISTENT,
		CREATED,
		DEPLOYED,
		WIRED, 
		LAUNCHED
	}

	private String operatorName;
	private int operatorUid;
	private int awaitedEOSSignalsPerSlice;
	
	private List<String> wiringSrcsList = new ArrayList<>();
	private String wiringDst = null;

	private List<Integer> sliceUidList = new ArrayList<>();
	
	private OperatorStateType operatorState = OperatorStateType.NONEXISTENT;
	
	public SMOperatorInfo(SMTaskInfo smTaskInfo, String name, int operatorUid, int eosSignalsToShutDownSlice) {
		super(smTaskInfo);
		this.operatorName = name;
		this.operatorUid = operatorUid;
		this.awaitedEOSSignalsPerSlice = eosSignalsToShutDownSlice;
	}

	public void addSlice(Integer sliceUid) {
		sliceUidList.add(sliceUid);
	}
	public int removeSlice(Integer sliceUid) {
		sliceUidList.remove(sliceUid);
		return sliceUidList.size();
	}
	
	public int getAwaitedEOSSignalsPerSlice() {
		return this.awaitedEOSSignalsPerSlice;
	}
	public String getOperatorName() {
		return this.operatorName;
	}
	public int getOperatorUid() {
		return this.operatorUid;
	}
	
	public void addWiringSrc(String operatorName) {
		wiringSrcsList.add(operatorName);
	}
	public void removeWiringSrc(String operatorName) {
		wiringSrcsList.remove(operatorName);
	}
	public void clearWiringSrcsList() {
		wiringSrcsList.clear();
	}	
	
	public void setWiringDst(String operatorName) {
		wiringDst = operatorName;
	}
	public void unsetWiringDst() {
		wiringDst = null;
	}
	public String getWiringDst() {
		return wiringDst;
	}
	
	public List<String> getWiringSrcsList() {
		return wiringSrcsList;
	}

	public OperatorStateType getOperatorState() {
		return operatorState;
	}
	public void setOperatorState(OperatorStateType operatorState) {
		this.operatorState = operatorState;
	}
	
}
