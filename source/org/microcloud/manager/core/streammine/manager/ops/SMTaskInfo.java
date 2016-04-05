package org.microcloud.manager.core.streammine.manager.ops;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.microcloud.manager.core.model.streammine.ManagerInput;

public class SMTaskInfo {
	private ManagerInput managerInput;
	private AtomicInteger currentOperationCounter = new AtomicInteger();
	private int currentOperationMax;
	
	private List<SMOperatorInfo> operatorChildren = new ArrayList<>();
	private List<SMSliceInfo> sliceChildren = new ArrayList<>();	
	
	public SMTaskInfo(ManagerInput managerInput) {
		this.managerInput = managerInput;
	}

	public synchronized boolean incrementCounterIsMax() {
		if(currentOperationCounter.incrementAndGet()>=currentOperationMax) {
			currentOperationMax = 0;
			currentOperationCounter.set(0);
			return true;
		}
		return false;
	}
	
	public synchronized void setOperationMax(int operationMax) {
		this.currentOperationMax = operationMax;
		currentOperationCounter.set(0);
	}
	
	public ManagerInput getManagerInput() {
		return this.managerInput;
	}

	public void addChild(SMTaskChild smTaskChild) {
		if(smTaskChild instanceof SMOperatorInfo)
			operatorChildren.add((SMOperatorInfo) smTaskChild);
		else if(smTaskChild instanceof SMSliceInfo)
			sliceChildren.add((SMSliceInfo) smTaskChild);
	}
	
	public List<SMOperatorInfo> getOperatorChildren() {
		return this.operatorChildren;
	}
	
	public List<SMSliceInfo> getSliceChildren() {
		return this.sliceChildren;
	}
}
