package org.microcloud.manager.core.placer.solution;

import java.util.Date;

public abstract class SolutionNode extends OrderableSolutionElement {
	
	protected int comprehensiveExecutionTimeS = 0;
	protected int comprehensiveExecutionPrice = 0;

	protected Date endTime;

	public int getComprehensiveExecutionTimeS() {
		return comprehensiveExecutionTimeS;
	}

	public int getComprehensiveExecutionPrice() {
		return comprehensiveExecutionPrice;
	}

	public Date getEndTime() {
		return endTime;
	}
	
	public abstract Object getNodeObject();
	
}
