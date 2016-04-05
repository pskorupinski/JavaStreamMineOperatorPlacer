package org.microcloud.manager.core.placer.solution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.microcloud.manager.core.model.datacenter.MicroCloudProfileNode;
import org.microcloud.manager.core.model.datacenter.Host;
import org.microcloud.manager.core.model.datacenter.MicroCloud;
import org.microcloud.manager.core.model.key.HistoricalKey;
import org.microcloud.manager.core.model.key.RealTimeDataKey;
import org.microcloud.manager.core.model.workeralgorithm.WorkerAlgorithmProfileNode;
import org.microcloud.manager.core.model.workeralgorithm.WorkerAlgorithmType;
import org.microcloud.manager.core.placer.PlacementProblem;
import org.microcloud.manager.core.placer.solution.SolutionDestination;
import org.microcloud.manager.core.placer.solution.view.SimplexSolutionTransferringNode;
import org.microcloud.manager.operations.UnitsConv;
import org.microcloud.manager.persistence.EntityLoader;

public class SolutionConnection<DestinationType> implements SimplexSolutionTransferringNode {
	private SolutionSource source;
	private SolutionDestination<DestinationType> destination;
	
	private boolean removed = false;
	
	protected Double bandwidthUsageMBs = null;
	protected Double comprehensiveExecutionPrice = 0.0;
	protected Date connStartTime = null;
	protected Date connEndTime = null;
	
	protected Double transferOnConnectionKB = null;
	
	
	protected SolutionConnection(
			SolutionSource source, SolutionDestination<DestinationType> destination) {
		constructor(source, destination);
	}
	
	protected SolutionConnection(
			SolutionSource source, SolutionDestination<DestinationType> destination, double transfer) {
		this.transferOnConnectionKB = transfer;
		constructor(source,destination);
	}
	
	private void constructor(
			SolutionSource source, SolutionDestination<DestinationType> destination) {
		this.source = source;
		this.destination = destination;
		
		buildConnections();
	}
	
	
	public void setTransfer(double transfer) {
		this.transferOnConnectionKB = transfer;
	}
	public void nullTransfer() {
		this.transferOnConnectionKB = null;
	}
	public boolean isTransferSet() {
		return (this.transferOnConnectionKB != null);
	}
	public double getTransfer() {
		return this.transferOnConnectionKB;
	}
	
	protected void setTransferBySource() {
		this.transferOnConnectionKB = this.source.getTransferPerOneConnection();
	}
	
	
	public SolutionSource getSource() {
		return source;
	}
	public void setSource(SolutionSource source) {
		this.source = source;
	}
	public SolutionDestination<DestinationType> getDestination() {
		return destination;
	}
	public void setDestination(SolutionDestination<DestinationType> destination) {
		this.destination = destination;
	}
	
	
	public void remove() {
		if(!removed) {
			source.removeOutputConnection(this);
			destination.removeInputConnection(this);
			
			removed = true;
		}
	}
	
	/**
	 * Counts a price of transferring data on this connection 
	 * by counting an average price of transferring 1 MB during a given period of time
	 * and multiplying in with the size of data to be retrieved from source.
	 * 
	 * @param startTime
	 * @param endTime
	 */
	protected void countPrice(Date startTime, Date endTime) {
		/* DC ids */
		int dc1 = source.getHost().getHost().getRack().getMicroCloud().getId();
		int dc2 = destination.solutionMicroCloud.getDataCenter().getId();
		
		if(dc1 == dc2) {
			return;
		}
		else {
			/* data size */
			double datasizeMB = this.transferOnConnectionKB/1024.0;
			
			/* if transfer not yet set - take source data size */
			if(datasizeMB < 0.0)
				datasizeMB = this.source.getDataSizeMB();
			
			/* recount to GB */
			double datasizeGB = datasizeMB / 1024.0;
			
			/* algorithm for a source DC */
			double averagesourceprice = 
					source.getHost().solutionMicroCloud.getAverageTransferOutPricePerMB(startTime, endTime);
			double sourceprice = averagesourceprice * datasizeGB;
			
			/* algorithm for a destination DC */
			double averagedestprice = 
					destination.solutionMicroCloud.getAverageTransferInPricePerMB(startTime, endTime);
			double destprice = averagedestprice * datasizeGB;
			
			comprehensiveExecutionPrice = sourceprice + destprice;
		}
		
	}
	
	/**
	 * Counts a price guessing that it will be similar to the average price during an expected period of an execution.
	 * 
	 * @param placementProblem
	 */
	public void countPossiblePrice(PlacementProblem placementProblem) {
		Date startTime = placementProblem.getClientQuery().getStartTime();
		Date endTime = 
				new Date(startTime.getTime() + placementProblem.getExpectedExecutionTimeS()*1000);
		
		countPrice(startTime,endTime);
	}

	/**
	 * Counts price on connection in an exactly stated period of time (based on parameters counted in connection's source)
	 * 
	 * @param startTime
	 */
	public void countAccuratePrice(Date startTime) {
		/* period */
		int delay = this.source.startTimeDelayS * 1000;
		int retrievalTime = this.source.retrievalTimeS * 1000;
		
		this.connStartTime = new Date(startTime.getTime() + delay);
		this.connEndTime = new Date(this.connStartTime.getTime() + retrievalTime);
		
		countPrice(connStartTime, connEndTime);
	}
	
	/**
	 * Counts what will be the (maximum) bandwidth on this connection,
	 * by comparing bandwidths on a communication path and taking the minimum.
	 * 
	 * This assumes (incorrectly) that the connection will be able to use a whole
	 * in/out bandwidth of a MicroCloud when communicating between various of them.
	 * 
	 * @return
	 */
	public double countBandwidthOnConnection() {
		double bandwidthOnConnection;
		
		Host sourceHost = source.getHost().getHost();
		
		List<Double> valuesToCompare = new ArrayList<>();
		
		/* always retrieval speed on a source */
		valuesToCompare.add(source.retrievalSpeedMBs);
		
		boolean addMore = true;
		
		MicroCloud destDC;
		
		if(destination.getDestination() instanceof Host) {
			Host destHost = (Host) destination.getDestination();
			if(sourceHost.equals(destHost))
				addMore = false;
			
			destDC = destHost.getRack().getMicroCloud();
		}
		else {
			destDC = (MicroCloud) destination.getDestination();
		}
		
		if(addMore) {
			valuesToCompare.add(UnitsConv.bitToByte(sourceHost.getRack().getMicroCloud().getDataCenterAttributes().getConnectionBandInside()));
			
			if(sourceHost.getRack().getMicroCloud().equals(
					destDC));
			else {
				valuesToCompare.add(UnitsConv.bitToByte(sourceHost.getRack().getMicroCloud().getDataCenterAttributes().getOutputBandwidthMBitInt().doubleValue()));
				valuesToCompare.add(UnitsConv.bitToByte(destDC.getDataCenterAttributes().getInputBandwidthMBitInt().doubleValue()));
				valuesToCompare.add(UnitsConv.bitToByte(destDC.getDataCenterAttributes().getConnectionBandInside()));
			}
		}
		
		bandwidthOnConnection = Collections.min(valuesToCompare);
			
		this.bandwidthUsageMBs = bandwidthOnConnection;
		return bandwidthOnConnection;
	}
	
	/**
	 * Counts source price for connection, based on:
	 * <ul>
	 * 	<li> time (that is counted from connection bandwidth and data size to be transferred) </li>
	 * 	<li> average price of per hour in time of expected execution time 
	 * 		(we are not sure when would retrieval of key of this source exactly start) </li>
	 * </ul>
	 * 
	 * It is assumed that execution speed will not have an influence on retrieval speed (TODO WHY???)
	 * 
	 * @param placementProblem
	 * @return
	 */
	public double countSourcePriceForConnection(PlacementProblem placementProblem) {
		
		this.source.countDSRetrievalInfo();
		
		double retrievalSpeedMBsTemp = countBandwidthOnConnection();
		
		double dataSizeMB = this.source.getDataSizeMB();
		
		int retrievalTimeS = (int) Math.ceil(dataSizeMB / retrievalSpeedMBsTemp);
		
		Date startTime = placementProblem.getClientQuery().getStartTime();
		Date endTime = new Date(startTime.getTime() + placementProblem.getExpectedExecutionTimeS()*1000);
		
		double averagePricePerH = this.source.getHost().solutionMicroCloud.getAverageExecPricePerH(startTime,endTime);
		return retrievalTimeS / 3600.0 * averagePricePerH;
	}
	
	/**
	 * Counts price of an execution on a potential destination host,
	 * as if there was the only worker operator in the system deployed. 
	 * 
	 * @param placementProblem
	 * @param workerAlgorithmType
	 * @return
	 */
	public double countDestinationPriceForConnection(PlacementProblem placementProblem) {
		
		WorkerAlgorithmType workerAlgorithmType = placementProblem.getClientQuery().getWorkerAlgorighmType();
		
		List<WorkerAlgorithmProfileNode> workerAlgorithmProfile = 
				EntityLoader.getInstance().getWorkerAlgorithmProfile(workerAlgorithmType);
		double destinationComputationBandwidth = workerAlgorithmProfile.get(0).getVelocity();
		
		double processingSpeedMBs = 
				Math.min(this.bandwidthUsageMBs, destinationComputationBandwidth);

		int processingTimeS = (int) Math.ceil(this.source.getDataSizeMB() / processingSpeedMBs);

		Date startTime = placementProblem.getClientQuery().getStartTime();
		Date endTime = new Date(startTime.getTime() + placementProblem.getExpectedExecutionTimeS()*1000);
		
		return processingTimeS / 3600.0 * this.destination.solutionMicroCloud.getAverageExecPricePerH(startTime,endTime);
	}

	public Double getComprehensiveExecutionPrice() {
		return comprehensiveExecutionPrice;
	}

//////////////////////////////////////////////////////////////////////////
// OVERRIDEN METHODS
//////////////////////////////////////////////////////////////////////////	

	@Override
	public boolean equals(Object obj) {
		
		if(super.equals(obj))
			return true;
		
		if( !(obj instanceof SolutionConnection) )
			return false;
		
		SolutionConnection other = (SolutionConnection) obj;
		
		if(other.source.equals(this.source) && other.destination.equals(this.destination))
			return true;
		else
			return false;
	}
	
	@Override
	public String toString() {
		return "[" + this.source + "] ---> [" + this.destination + "]";
	}
	
//////////////////////////////////////////////////////////////////////////
// PROTECTED METHODS
//////////////////////////////////////////////////////////////////////////
	
	protected void setBandwidthUsageMBs(double retrievalSpeedMBs) {
		this.bandwidthUsageMBs = retrievalSpeedMBs;
	}
	
	@Override
	protected void finalize() throws Throwable {
		remove();
		super.finalize();
	}
	
//////////////////////////////////////////////////////////////////////////
// PRIVATE METHODS
//////////////////////////////////////////////////////////////////////////
	
	private void buildConnections() {
		source.addOutputConnection(this);
		destination.addInputConnection(this);
	}
	
	
	
}
