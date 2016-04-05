package org.microcloud.manager.core.placer.solution;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.HashSet;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

import org.microcloud.manager.core.model.datacenter.MicroCloudProfileNode;
import org.microcloud.manager.core.model.datacenter.Host;
import org.microcloud.manager.core.model.workeralgorithm.WorkerAlgorithmProfileNode;
import org.microcloud.manager.core.model.workeralgorithm.WorkerAlgorithmType;
import org.microcloud.manager.core.placer.solution.view.SimplexSolutionTransferringNode;
import org.microcloud.manager.persistence.EntityLoader;

public class SolutionDestination<DestinationType> extends SolutionNode implements SimplexSolutionTransferringNode {
	private DestinationType destination;
	
	private Set<SolutionConnection<DestinationType>> inputConnections = new HashSet<>();
	
	private NavigableMap<Date, Double> incomingBandwidthOverTime = new TreeMap<>();

	protected SolutionMicroCloud solutionMicroCloud;
	
	protected SolutionDestination(DestinationType destination) {
		this.setDestination(destination);
	}

	
	public DestinationType getDestination() {
		return destination;
	}
	public void setDestination(DestinationType destination) {
		this.destination = destination;
	}
	public Set<SolutionConnection<DestinationType>> getInputConnections() {
		return inputConnections;
	}
	
	
	protected void addInputConnection(SolutionConnection<DestinationType> inputConnection) {
		inputConnections.add(inputConnection);
	}
	protected void removeInputConnection(SolutionConnection<DestinationType> inputConnection) {
		inputConnections.remove(inputConnection);
	}


	public SolutionMicroCloud getSolutionMicroCloud() {
		return solutionMicroCloud;
	}


	public void analyzeIncomingBandwidth() {
		
		incomingBandwidthOverTime.put(new Date(0), 0.0);
		
		for(SolutionConnection<DestinationType> conn : inputConnections) {
			
			/* preconditions */
			if ( conn.bandwidthUsageMBs == null ||
					conn.connStartTime == null ||
					conn.connEndTime == null )
				throw new NullPointerException("Parameters that should be initialized in previous" +
						"steps of an algorithm are null.");
			/* preconditions end */
			
			double bandwidthOnConn = conn.bandwidthUsageMBs;
			Date connStartTime = conn.connStartTime;
			Date connEndTime = conn.connEndTime;
			
			Entry<Date, Double> pointA = incomingBandwidthOverTime.floorEntry(connStartTime);
			Entry<Date, Double> pointD = incomingBandwidthOverTime.floorEntry(connEndTime);
			
			/* 1. Create a start point */
			incomingBandwidthOverTime.put(connStartTime, pointA.getValue() + bandwidthOnConn);
			/* 2. Create an end point */
			incomingBandwidthOverTime.put(connEndTime, pointD.getValue());
			
			Entry<Date, Double> newStart = incomingBandwidthOverTime.floorEntry(connStartTime);
			Entry<Date, Double> newEnd = incomingBandwidthOverTime.floorEntry(connEndTime);
			
				
			boolean ourpoints = false;
			
			/* 3. Change value for other points */
			for(Entry<Date, Double> pointBCD : incomingBandwidthOverTime.entrySet()) {
				
				if(pointBCD.equals(newEnd))
					break;
				else if(ourpoints == true)
					pointBCD.setValue( pointBCD.getValue() + bandwidthOnConn );
				else if(pointBCD.equals(newStart))
					ourpoints = true;
				
			}
		}
		
		incomingBandwidthOverTime.pollFirstEntry();
		
	}
	public void countTime(WorkerAlgorithmType workerAlgorithmType) {
		
		List<WorkerAlgorithmProfileNode> workerAlgorithmProfile = 
				EntityLoader.getInstance().getWorkerAlgorithmProfile(workerAlgorithmType);
		
		// will be processed with a normal tempo for this algorithm
		double computationBandwidth = workerAlgorithmProfile.get(0).getVelocity();
		
		if(destination instanceof Host) {
			Host host = (Host) destination;
			double hostComputationBandwidth = host.getComputationPowerFactor() * computationBandwidth;
			
			double leftFromTheLast = 0.0;
			boolean theFirstOne = true;
			Entry<Date, Double> lastPointInTime = null;
			
			for(Entry<Date, Double> pointInTime : incomingBandwidthOverTime.entrySet()) {
				
				if(theFirstOne) {
					theFirstOne = false;
				}
				else {
					long timePeriod = pointInTime.getKey().getTime() - lastPointInTime.getKey().getTime();
					/* surface / integral of a function represented by the line between two points of time */
					double surfaceNeededForTheNew = timePeriod * lastPointInTime.getValue();
					double surfaceNeeded = surfaceNeededForTheNew + leftFromTheLast;
					
					double newSurfaceProvided = timePeriod * hostComputationBandwidth;
					
					double surfacesDifference = surfaceNeeded - newSurfaceProvided;
					
					leftFromTheLast = Math.max(surfacesDifference, 0.0);
				}
				
				lastPointInTime = pointInTime;
			}
			
			/* x = xy / y */
			long moreTime = (long) (leftFromTheLast / hostComputationBandwidth);
			
			this.comprehensiveExecutionTimeS = (int) 
					((incomingBandwidthOverTime.lastKey().getTime() - incomingBandwidthOverTime.firstKey().getTime()) + moreTime) / 1000;
		}
		else {
			// TODO
		}
		
	}
	public void countPrice() {
		
		Date startTime = incomingBandwidthOverTime.firstKey();
		this.endTime = new Date(startTime.getTime() + comprehensiveExecutionTimeS*1000);	
		
		if(destination instanceof Host) {
			double pricePerHour = solutionMicroCloud.getAverageExecPricePerH(startTime, endTime);
			
			comprehensiveExecutionPrice = (int) (pricePerHour * comprehensiveExecutionTimeS / 3600.0);	
		}
	}
	
	@Override
	public int hashCode() {
		return destination.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(super.equals(obj))
			return true;
		
		if( !(obj instanceof SolutionDestination) )
			return false;
		
		SolutionDestination<?> other = (SolutionDestination<?>) obj;
		
		if(other.destination.equals(this.destination))
			return true;
		else
			return false;
	}
	
	@Override
	public String toString() {
		return super.toString() + this.destination;
	}
	
	@Override
	public double getTransfer() {
		double sum = 0.0;
		for(SimplexSolutionTransferringNode n : inputConnections) {
			sum += n.getTransfer();
		}
		return sum;
	}
	
	public static Comparator<SolutionDestination<?>> DestinationTransferToComparator = 
			new Comparator<SolutionDestination<?>>() {

		@Override
		public int compare(SolutionDestination<?> sd1, SolutionDestination<?> sd2) {
			
			Double destinationTransferTo1 = sd1.getTransfer();
			Double destinationTransferTo2 = sd2.getTransfer();
			
			//descending order
			return destinationTransferTo2.compareTo(destinationTransferTo1);
		}
		
	};

	@Override
	public Object getNodeObject() {
		return getDestination();
	}
	
}
