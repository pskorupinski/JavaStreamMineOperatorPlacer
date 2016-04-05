package org.microcloud.manager.core.placer.solution;


import java.util.HashSet;
import java.util.Set;

import org.microcloud.manager.core.model.key.RealTimeDataKey;
import org.microcloud.manager.core.placer.solution.view.SimplexSolutionTransferringNode;
import org.microcloud.manager.operations.UnitsConv;

public class SolutionSource extends OrderableSolutionElement implements SimplexSolutionTransferringNode {
	private SolutionSourceHost host;
	private SolutionKey key;
	
	private Set<SolutionConnection<?>> outputConnections = new HashSet<>();
	
	protected int retrievalTimeS;
	protected int startTimeDelayS;
	private double actualRetrievalSpeedMBs;
	protected Double retrievalSpeedMBs  = null;
	

	protected SolutionSource(SolutionSourceHost host, SolutionKey key) {
		this.host = host;
		this.key = key;
		
		this.host.addSolutionSource(this);
		this.key.addSolutionSource(this);
	}
	
	protected void changeKey(SolutionKey key) {
		this.key = key;
		this.key.addSolutionSource(this);
	}
	
	protected Double getDataSizeMB() {
		return key.getKey().getSizeKB() / 1024.0;
	}
	
	public SolutionSourceHost getHost() {
		return host;
	}
	public void setHost(SolutionSourceHost host) {
		this.host = host;
	}
	public SolutionKey getKey() {
		return key;
	}
	public void setKey(SolutionKey key) {
		this.key = key;
	}
	
	public Set<SolutionConnection<?>> getOutputConnections() {
		return outputConnections;
	}
	protected void addOutputConnection(SolutionConnection<?> outputConnection) {
		outputConnections.add(outputConnection);
	}
	protected void removeOutputConnection(SolutionConnection<?> outputConnection) {
		outputConnections.remove(outputConnection);
	}
	
//	/**
//	 * 
//	 * @return sum of transfers on all connections going out of this source
//	 */
//	protected double getOutputTransfer() {
//		double outputTransfer = 0.0;
//		
//		for(SolutionConnection<?> sc : outputConnections) {
//			outputTransfer += sc.getTransfer();
//		}
//		
//		return outputTransfer;
//	}
	
	/**
	 * Defines an information about a retrieval speed of this source
	 * based on a data from key
	 */
	protected void countDSRetrievalInfo() {
		if( this.retrievalSpeedMBs == null ) {
			
			double retrievalSpeedMbs = 0.0;
			
			if(this.key.getKey() instanceof RealTimeDataKey)
				retrievalSpeedMbs = ((RealTimeDataKey)this.key.getKey()).getSizePerMinKb() / (60.0*1024.0);
			else
				retrievalSpeedMbs = 8 * host.getHost().getDiskReadSpeed();
			
			this.retrievalSpeedMBs = UnitsConv.bitToByte(retrievalSpeedMbs);
		}
	}

	public int getRetrievalTimeS() {
		return retrievalTimeS;
	}

	public void countTime() {
		
		countDSRetrievalInfo();
		
		int outputConns = this.outputConnections.size();
		
		/* average retrieval speed for every of output connections */
		double avRetrievalSpeedMBsTemp = this.retrievalSpeedMBs / outputConns;
		
		for( SolutionConnection<?> conn : this.outputConnections) {
			/* Pessimistic assumption: total speed will be exactly the lowest speed */
			avRetrievalSpeedMBsTemp = Math.min(avRetrievalSpeedMBsTemp, conn.countBandwidthOnConnection());
		}
		
		this.actualRetrievalSpeedMBs = avRetrievalSpeedMBsTemp;
		this.retrievalTimeS = (int) Math.ceil(this.getDataSizeMB() / (avRetrievalSpeedMBsTemp * outputConns));
	}

	public void setConnectionsBandwidth() {
		for( SolutionConnection<?> conn : outputConnections) {
			/* Pessimistic assumption: bandwidth usage will be exactly the lowest speed */
			conn.setBandwidthUsageMBs(actualRetrievalSpeedMBs);
		}				
	}

	public int getStartTimeDelayS() {
		return startTimeDelayS;
	}
	protected void setStartTimeDelayS(int startTimeDelayS) {
		this.startTimeDelayS = startTimeDelayS;
	}	
	
	public double getTransferPerOneConnection() {
		return this.key.getKey().getSizeKB()/(double)outputConnections.size();
	}
	
	public Double getRetrievalSpeedMBs() {
		if(retrievalSpeedMBs == null)
			countDSRetrievalInfo();
		return retrievalSpeedMBs;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(super.equals(obj))
			return true;
		
		if( !(obj instanceof SolutionSource) )
			return false;
		
		SolutionSource other = (SolutionSource) obj;
		
		if(other.host.equals(this.host) && other.key.equals(this.key))
			return true;
		else
			return false;
	}
	
	@Override
	public String toString() {
		return super.toString() + " (" + this.host + ", " + this.key + ")";
	}

	@Override
	public double getTransfer() {
		double sum = 0.0;
		for(SimplexSolutionTransferringNode n : this.outputConnections) {
			sum += n.getTransfer();
		}
		return sum;
	}


}
