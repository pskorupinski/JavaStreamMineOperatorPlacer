package org.microcloud.manager.core.pricetimeapprox;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.microcloud.manager.core.model.datacenter.Host;
import org.microcloud.manager.core.model.workeralgorithm.WorkerAlgorithmType;
import org.microcloud.manager.core.placer.solution.SolutionConnection;
import org.microcloud.manager.core.placer.solution.SolutionDestination;
import org.microcloud.manager.core.placer.solution.SolutionGraph;
import org.microcloud.manager.core.placer.solution.SolutionSource;
import org.microcloud.manager.core.placer.solution.SolutionSourceHost;

public class SolutionPriceTimeApproximator {
	
	protected SolutionGraph<Host> solutionGraph;
	protected Date startTime;
	protected WorkerAlgorithmType workerAlgorithmType;
	
	protected int price;
	protected int time;
	

	public SolutionPriceTimeApproximator(SolutionGraph<Host> solutionGraph, Date startTime, WorkerAlgorithmType workerAlgorithmType) {
		this.solutionGraph = solutionGraph;
		this.startTime = startTime;
		this.workerAlgorithmType = workerAlgorithmType;
	}
	
	public void run() {
		org.microcloud.manager.logger.MyLogger.getInstance().log("SolutionPriceTimeApproximator");	
		
		workOnSources();
		workOnConnections();
		workOnDestinations();
		
		countGraphTime();
		countGraphPrice();
	}

	

	public int getPrice() {
		return this.price;
	}
	public int getTime() {
		return this.time;
	}
	
	/**
	 * Counts sources executions times and prices.
	 * 
	 * @Tested
	 */
	private void workOnSources() {
		
		List<SolutionSource> sourcesList = solutionGraph.getSources();
		
		for(SolutionSource s : sourcesList) {
			/* 1. count the execution time */
			s.countTime();
			/* 2. store effective bandwidth usage for connections */
			s.setConnectionsBandwidth();
			
			org.microcloud.manager.logger.MyLogger.getInstance().log("Counted time of " + s + " as " + s.getRetrievalTimeS()); 
			org.microcloud.manager.logger.MyLogger.getInstance().log("Transfer per output connection of " + s + " set as " + s.getTransferPerOneConnection()); 
			
		}

		Set<SolutionSourceHost> hSet = new HashSet<>();
		
		for(SolutionSource s : sourcesList) {
			SolutionSourceHost h = s.getHost();
			if( hSet.add(h) ) {
				/* 3. define delays on reading of keys */
				h.setExecutionTimes();
				/* 4. count price */
				h.countPrice(startTime);
				
				org.microcloud.manager.logger.MyLogger.getInstance().log("Counted comprehensive execution time of source host " + h.getHost() + " as " + h.getComprehensiveExecutionTimeS()); 
				org.microcloud.manager.logger.MyLogger.getInstance().log("Counted comprehensive execution price of source host " + h.getHost() + " as " + h.getComprehensiveExecutionPrice()); 
			}
		}
		
	}
	
	private void workOnConnections() {
		
		List<SolutionConnection<Host>> connectionsList = solutionGraph.getConnections();
		
		for(SolutionConnection<?> c : connectionsList) {
			c.countAccuratePrice(startTime);
			
			org.microcloud.manager.logger.MyLogger.getInstance().log("Counted price of connection " + c + " as " + c.getComprehensiveExecutionPrice()); 
		}
		
	}
	
	private void workOnDestinations() {
		
		List<SolutionDestination<Host>> destinationsList = solutionGraph.getDestinations();
		
		for(SolutionDestination<Host> d : destinationsList) {
			d.analyzeIncomingBandwidth();
			d.countTime(workerAlgorithmType);
			d.countPrice();
			org.microcloud.manager.logger.MyLogger.getInstance().log("Counted comprehensive execution time of destination " + d.getDestination() + " as " + d.getComprehensiveExecutionTimeS()); 
			org.microcloud.manager.logger.MyLogger.getInstance().log("Counted comprehensive execution price of destination " + d.getDestination() + " as " + d.getComprehensiveExecutionPrice()); 
		}
		
	}

	private void countGraphTime() {
		
		Date endTime = new Date(0);
		
		List<SolutionDestination<Host>> destinationsList = solutionGraph.getDestinations();
		for(SolutionDestination<Host> d : destinationsList) {
			if(d.getEndTime().after(endTime))
				endTime = d.getEndTime();
		}
		
		this.time = (int) ( (endTime.getTime() - startTime.getTime()) / 1000 );
		
	}

	private void countGraphPrice() {
		int comprehensivePrice = 0;
		
		Set<Host> hostsOfDestinationsSet = new HashSet<>();
		
		/* add destination prices */
		List<SolutionDestination<Host>> destinationsList = solutionGraph.getDestinations();
		for(SolutionDestination<Host> d : destinationsList) {
			hostsOfDestinationsSet.add(d.getDestination());
			comprehensivePrice += d.getComprehensiveExecutionPrice();
		}

		/* add source prices (if sources on other hosts than destinations) */		
		List<SolutionSource> sourcesList = solutionGraph.getSources();
		Set<SolutionSourceHost> hSet = new HashSet<>();
		for(SolutionSource s : sourcesList) {
			SolutionSourceHost h = s.getHost();
			if( hSet.contains(h) )
				continue;
			else {
				hSet.add(h);
				if(hostsOfDestinationsSet.contains(h.getHost()));
				else
					comprehensivePrice += h.getComprehensiveExecutionPrice();
			}
		}
		
		/* add connection prices */		
		List<SolutionConnection<Host>> connectionsList = solutionGraph.getConnections();
		for(SolutionConnection<?> c : connectionsList) {
			comprehensivePrice += c.getComprehensiveExecutionPrice();
		}
		
		this.price = comprehensivePrice;
		
	}
}
