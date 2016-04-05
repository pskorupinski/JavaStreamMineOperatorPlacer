package org.microcloud.manager.core.streammine.manager.ops;

public class SMTaskChild {
	private SMTaskInfo smTaskInfo;
	
	public SMTaskChild(SMTaskInfo smTaskInfo) {
		this.smTaskInfo = smTaskInfo;
		this.smTaskInfo.addChild(this);
	}
	
	public SMTaskInfo getSMTaskInfo() {
		return this.smTaskInfo;
	}
	
}
