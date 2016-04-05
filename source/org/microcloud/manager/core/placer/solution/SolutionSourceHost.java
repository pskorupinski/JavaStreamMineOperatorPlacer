package org.microcloud.manager.core.placer.solution;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.microcloud.manager.core.model.datacenter.MicroCloudProfileNode;
import org.microcloud.manager.core.model.datacenter.Host;
import org.microcloud.manager.core.placer.solution.view.SimplexSolutionTransferringNode;
import org.microcloud.manager.persistence.EntityLoader;

public class SolutionSourceHost extends SolutionNode implements Comparable<SolutionSourceHost>, SimplexSolutionTransferringNode {

	private Host host;
	private Set<SolutionSource> sourcesSet = new HashSet<>();

	protected SolutionMicroCloud solutionMicroCloud;

	protected SolutionSourceHost(Host host) {
		this.host = host;
	}
	
	public Host getHost() {
		return host;
	}
	
	public SolutionMicroCloud getSolutionMicroCloud() {
		return solutionMicroCloud;
	}
	
	public void addSolutionSource(SolutionSource source) {
		this.sourcesSet.add(source);
	}
	public Set<SolutionSource> getSourcesSet() {
		return sourcesSet;
	}
	
	protected Double getHostDataSizeMB() {
		Double hostDataSizeMB = 0.0;
		for(SolutionSource ss : sourcesSet)
			hostDataSizeMB += ss.getDataSizeMB();
		return hostDataSizeMB;
	}
	
	/**
	 * Iterates sources to set the approximate retrieval start time for every of the keys
	 */
	public void setExecutionTimes() {
		
		int delayTime = 0;
		
		/* TODO easiest solution, should consider data arrangement on a hard-drive */
		for(SolutionSource source : sourcesSet) {
			source.startTimeDelayS = delayTime;
			delayTime += source.retrievalTimeS;
		}
		
		comprehensiveExecutionTimeS = delayTime;
		
	}
	
	public void countPrice(Date startTime) {
		
		this.endTime = new Date(startTime.getTime() + comprehensiveExecutionTimeS*1000);	

		double pricePerHour = solutionMicroCloud.getAverageExecPricePerH(startTime, endTime);
		
		comprehensiveExecutionPrice = (int) (pricePerHour * comprehensiveExecutionTimeS / 3600.0);	
	}
	
	
	@Override
	public boolean equals(Object obj) {
		
		if(super.equals(obj))
			return true;
		
		if( !(obj instanceof SolutionSourceHost) )
			return false;
		
		SolutionSourceHost other = (SolutionSourceHost) obj;
		
		if(other.host.equals(this.host))
			return true;
		else
			return false;
	}
	
	@Override
	public String toString() {
		return super.toString() + this.host;
	}

	@Override
	public int compareTo(SolutionSourceHost o) {
		double diff = o.getHostDataSizeMB() - this.getHostDataSizeMB();
		
		if(diff > 0.0)
			return 1;
		else if(diff == 0.0)
			return 0;
		else		
			return -1;
	}

	@Override
	public double getTransfer() {
		double sum = 0.0;
		for(SimplexSolutionTransferringNode n : sourcesSet) {
			sum += n.getTransfer();
		}
		return sum;
	}

	@Override
	public Object getNodeObject() {
		return getHost();
	}
	
	
}
