package org.microcloud.manager.core.placer.placement.Simplex.variations.workersnumber;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.microcloud.manager.core.model.datacenter.MicroCloud;
import org.microcloud.manager.core.model.datasource.DataSource;
import org.microcloud.manager.core.model.workeralgorithm.WorkerAlgorithm;
import org.microcloud.manager.core.model.workeralgorithm.WorkerAlgorithmProfileNode;
import org.microcloud.manager.core.model.workeralgorithm.WorkerAlgorithmType;
import org.microcloud.manager.core.placer.solution.SolutionDestination;
import org.microcloud.manager.core.placer.solution.SolutionGraphFullMC;
import org.microcloud.manager.core.placer.solution.SolutionMicroCloud;
import org.microcloud.manager.core.placer.solution.SolutionSource;
import org.microcloud.manager.persistence.objectsloader.WorkerAlgorithmDao;

public class NeededWorkersCounter {
	
	SolutionGraphFullMC fullInitGraph;
	WorkerAlgorithmType workerAlgorithmType;

	public NeededWorkersCounter(SolutionGraphFullMC fullInitGraph, WorkerAlgorithmType workerAlgorithmType) {
		this.fullInitGraph = fullInitGraph;
		this.workerAlgorithmType = workerAlgorithmType;
	}
	
	/**
	 *  Here it is assumed that outgoing bandwidths are independent of each other
	 * and average bandwidth produced by every of sources will be average 
	 * of all the potential bandwidths speeds of out-connections.
	 * A sum of all of this average bandwidths will be an approximate bandwidth 
	 * within the system (). 
	 */
	public int count() {		
		Map<SolutionMicroCloud,Double> fractionsMap = new HashMap<>();
		
		/* 
		 * 1. Make a map with fractions of transfer from any source to every of destination MicroClouds
		 */
		doFractionsMap(fractionsMap);
		
		/*
		 * 2. Count the sum of approximate outgoing bandwidths of sources
		 */
		double bandwidth = doCountBandwidth(fractionsMap);
		
		/*
		 * 3. Find a number of workers appropriate for this bandwidth
		 */
		return WorkerAlgorithm.doFindWorkersNumber(bandwidth,workerAlgorithmType);
	}
	
	/**
	 * Fills a map given as an argument with pairs: MicroCloud (representation of MicroCloud in the system)
	 * + fraction of system's inside transfer that is going to come into this MicroCloud
	 * 
	 * @param fractionsMap
	 */
	protected void doFractionsMap(Map<SolutionMicroCloud,Double> fractionsMap) {
		/* 1a. Get transfer to every of MicroClouds and count a sum of them */
		Map<SolutionMicroCloud,Double> transfersMap = new HashMap<>();
		Double transfersSum = 0.0;
		
		for(SolutionDestination<MicroCloud> sd : fullInitGraph.getDestinations()) {
			Double transferTo = sd.getTransfer();
			if(transferTo > 0.0) {
				transfersMap.put(sd.getSolutionMicroCloud(), transferTo);
				transfersSum += transferTo;
			}
		}
		
		/* 1b. For every MicroCloud, count a fraction */
		for(Map.Entry<SolutionMicroCloud, Double> e : transfersMap.entrySet()) {
			fractionsMap.put(e.getKey(), e.getValue()/transfersSum);
		}
	}
	
	/**
	 * This method approximates a total bandwidth by taking bandwidths between every of the active sources and every of active destinations
	 * and multiplying them by fraction of a transfer that will be transferred from this source to that destination.
	 * 
	 * @param fractionsMap
	 * @return
	 */
	protected Double doCountBandwidth(Map<SolutionMicroCloud, Double> fractionsMap) {
		double sum = 0.0;
		
		for(Map.Entry<SolutionMicroCloud, Double> e : fractionsMap.entrySet()) {
			for(SolutionSource ss : fullInitGraph.getSources()) {
				Double transferFrom = ss.getTransfer();
				if(transferFrom > 0.0) {
					/* TODO solution source hosts should be counted once */
				
					Double bandwidthMB = SolutionMicroCloud.getBandwidthBetween(
							ss.getHost().getSolutionMicroCloud(), e.getKey());
					
					double sourceRetrievalMBs = ss.getRetrievalSpeedMBs();
					double fractionOfRetrievalSpeed = sourceRetrievalMBs * e.getValue();
					
					/*
					 * The sending speed will be a fraction of data retrieved from the disk - 
					 * - if it is possible to send it at that average speed through this connection.
					 */
					sum += Math.min(fractionOfRetrievalSpeed, bandwidthMB);
				}
			}
		}
		
		org.microcloud.manager.logger.MyLogger.getInstance().log("Total bandwidth in the system counted as " + sum);
		
		return sum;
	}

}
