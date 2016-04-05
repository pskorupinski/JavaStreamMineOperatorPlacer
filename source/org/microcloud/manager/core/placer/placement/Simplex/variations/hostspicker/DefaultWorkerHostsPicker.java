package org.microcloud.manager.core.placer.placement.Simplex.variations.hostspicker;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.hibernate.cfg.NotYetImplementedException;
import org.microcloud.manager.core.model.datacenter.Host;
import org.microcloud.manager.core.model.datacenter.MicroCloud;
import org.microcloud.manager.core.model.datacenter.Rack;
import org.microcloud.manager.core.placer.PlacementProblem;
import org.microcloud.manager.core.placer.PlacerParameters;
import org.microcloud.manager.core.placer.parameterenums.StrategyHostChoice;
import org.microcloud.manager.core.placer.placement.Simplex.variations.VariationImplCore;
import org.microcloud.manager.core.placer.placement.Simplex.variations.hostspicker.DefaultWorkerHostsPicker.RackOfSources;
import org.microcloud.manager.core.placer.solution.SolutionConnection;
import org.microcloud.manager.core.placer.solution.SolutionDestination;
import org.microcloud.manager.core.placer.solution.SolutionSourceHost;
import org.microcloud.manager.core.placer.solution.SolutionTransformer;
import org.microcloud.manager.operations.OperationsOnNumbers;
import org.microcloud.manager.operations.SetOperations;

public class DefaultWorkerHostsPicker extends VariationImplCore implements WorkerHostsPicker {
	
	public DefaultWorkerHostsPicker(PlacementProblem placementProblem) {
		super(placementProblem);
	}

	private SolutionTransformer solutionTransformer = null;

	@Override
	public List<RackOfSources> pickHosts(
			SolutionDestination<MicroCloud> destMicroCloud, Integer hostsNumber) {
		
		org.microcloud.manager.logger.MyLogger.getInstance().log("Destination hosts picker for " + destMicroCloud.getDestination());
		
		Set<SolutionConnection<MicroCloud>> inputConnections = destMicroCloud.getInputConnections();
		
		RacksOfSources racksOfSources = new RacksOfSources(destMicroCloud.getDestination());
		Set<SolutionConnection<MicroCloud>> connectionsWithoutMC = new HashSet<>();
		
		/* 
		 * 1. Look at sources within the data center - 
		 * put workers according to their positions 
		 */
		
		/* 1a. Make sets for connections from inside and from outside */
		/* 1b. Count how many data will come from every of the racks */
		MicroCloud dstMC = destMicroCloud.getDestination();
		for(SolutionConnection<MicroCloud> sc : inputConnections) {
			Rack srcRack = sc.getSource().getHost().getHost().getRack();
			MicroCloud srcMC = srcRack.getMicroCloud();
			
			if(dstMC.equals(srcMC)) {
				racksOfSources.add(sc);
			}
			else {
				connectionsWithoutMC.add(sc);
			}
		}
		
		List<RackOfSources> rackOfSourcesList = racksOfSources.getSortedSourcesCollection(); 	/* 0 */ 
		
		/* NO rack awareness needed - all workers in one rack */
		if(destMicroCloud.getDestination().getRacks().size() == 1) {
			RackOfSources ros = rackOfSourcesList.get(0);
			ros.setWorkersNumber(hostsNumber);
			
			for(SolutionConnection<MicroCloud> sc : connectionsWithoutMC) {
				ros.addOuterSource(sc);
			}
		}
		/* rack awareness needed - TODO (easy stuff) */
		else {		
			throw new NotYetImplementedException("Not yet implemented picking hosts when there are many racks in destination MicroCloud");
			
			/* 1c. Decide on how many workers should be in every of the racks */
//			List<Double> sourceDataSizes = new ArrayList<Double>();
//			for(RackOfSources ros : rackOfSourcesList) { 
//				sourceDataSizes.add(ros.getSourceDataSize());
//			}
//			List<Integer> workersNumbers = OperationsOnNumbers.doublesListToIntegersList(sourceDataSizes, hostsNumber);
//			int i=0;
//			for(RackOfSources ros : rackOfSourcesList) { 
//				ros.setWorkersNumber(workersNumbers.get(i));
//				i++;
//			}			
//			
//			double hostsInHowManyRacks = racksOfSources.getRacksOfSourcesNumber() / destMicroCloud.getDestination().getRacks().size();
//			if(hostsInHowManyRacks > PlacerParameters.WorkerHostsPickerParam_SourceRacksPercent) /* which is always the case for one rack */ {
//				
//			}
			
			/* 
			 * Questions 
			 */
			
			/* Q1 */ 
//			destMicroCloud.getDestination().getRacks().size();
//			
			/* 
			 * 2. Decide to which workers other sources should transfer their data 
			 */
		}
			
		/* 
		 * 3. Decide on which hosts exactly to put workers
		 */
		for(RackOfSources ros : rackOfSourcesList) {
			Rack rack = ros.getRack();
			Set<Host> freeHosts = rack.getFreeHosts(placementProblem.getClientQuery().getStartTime(), placementProblem.getExpectedExecutionTimeS());
			
			/* 0. (Easy solution) If not enough free hosts, lower amount of hosts to be deployed (and do nothing more) */
			if(freeHosts.size() < ros.getWorkersNumber()) {
				ros.setWorkersNumber(freeHosts.size());
				org.microcloud.manager.logger.MyLogger.getInstance().log("Not enough free hosts, changing worker's number to " + freeHosts.size());			
			}
			
			/* 1. Get a list of solution hosts of this rack, sorted by the size of data retrieved on them */
			Set<SolutionSourceHost> solutionSourceHostSet = new HashSet<>();
			for(SolutionConnection<MicroCloud> sc : ros.solutionConnections) {
				solutionSourceHostSet.add(sc.getSource().getHost());
			}
			List<SolutionSourceHost> solutionSourceHostList = new ArrayList<>(solutionSourceHostSet);
			Collections.sort(solutionSourceHostList);
			
			/* 2. Put workers on the hosts with the greatest amount of data */
			int workersOverSources = ros.getWorkersNumber() - solutionSourceHostList.size();
			
			if(workersOverSources <= 0) {
				for(int i=0; i<ros.getWorkersNumber(); i++) {
					Host chosenHost = solutionSourceHostList.get(i).getHost();
					org.microcloud.manager.logger.MyLogger.getInstance().log("Picking host with source as destination " + chosenHost);			
					solutionTransformer.createDestination(chosenHost);
				}
			}
			else {
				Set<Host> usedHosts = new HashSet<>();
				for(int i=0; i<solutionSourceHostList.size(); i++) {
					Host chosenHost = solutionSourceHostList.get(i).getHost();
					org.microcloud.manager.logger.MyLogger.getInstance().log("Picking host with source as destination " + chosenHost);
					solutionTransformer.createDestination(chosenHost);
					usedHosts.add(chosenHost);
				}
				Set<Host> leftHosts = SetOperations.difference(freeHosts, usedHosts);
				List<Host> leftHostsList = new ArrayList<>(leftHosts);
				Random random = new Random();
				for(int i=0; i<workersOverSources; i++) {
					int hostNo = random.nextInt(leftHostsList.size());
					Host chosenHost = leftHostsList.get(hostNo);
					org.microcloud.manager.logger.MyLogger.getInstance().log("Picking host without source as destination " + chosenHost);
					solutionTransformer.createDestination(chosenHost);
					leftHostsList.remove(hostNo);
				}
														
			}
			
//			if(strategy == StrategyHostChoice.SourcesAware_Avoid) {
//				// remove some
//			}
//			else if(strategy == StrategyHostChoice.SourcesAware_Pick) {
//				// remove some
//			}
//			
//			/* VARIATION POINT */
//			/* 3a. Choose random hosts from those free... */
//			Iterator<Host> iterator = freeHosts.iterator();
//			Integer chosenHosts = 0;
//			Host host;
//			Random random = new Random();
//			while(chosenHosts  < hostsNumber) {
//				if(! iterator.hasNext())
//					iterator = freeHosts.iterator();
//				
//				host = iterator.next();
//				
//				if(random.nextBoolean() == true) {
//					destinationHosts.addDestinationHost(host);
//					iterator.remove();
//					chosenHosts++;
//				}		
//			}
			
		}
		
		return rackOfSourcesList;
	}

	@Override
	public void setSolutionTransformer(SolutionTransformer solutionTransformer) {
		this.solutionTransformer = solutionTransformer;
	}

/////////////////////////////////////////////////
//	NESTED CLASSES
/////////////////////////////////////////////////
	
	class RacksOfSources {
		private Map<Rack, RackOfSources> racksMap = new HashMap<Rack, DefaultWorkerHostsPicker.RackOfSources>();
		private double dataSizeSum = 0;
		
		public RacksOfSources(MicroCloud microCloud) {
			for(Rack rack : microCloud.getRacks()) {
				racksMap.put(rack, new RackOfSources(rack));
			}
		}
		
		protected synchronized void add(SolutionConnection<MicroCloud> sc) {
			Rack srcRack = sc.getSource().getHost().getHost().getRack();
			RackOfSources ros = racksMap.get(srcRack);
			
			if(ros == null) {
				racksMap.put(srcRack, new RackOfSources(srcRack));
				dataSizeSum += sc.getTransfer();
				ros = racksMap.get(srcRack);
			}
			
			ros.addInnerSource(sc);
		}
		
		/**
		 * Returns data sources sorted by data size
		 * 
		 * @return
		 */
		protected List<RackOfSources> getSortedSourcesCollection() {
			List<RackOfSources> list = new ArrayList<RackOfSources>(racksMap.values());
			Collections.sort(list);
			return list;
		}
		
		protected double getSumTransfersInMC() {
			return this.dataSizeSum;
		}
		
		protected int getRacksOfSourcesNumber() {
			return racksMap.size();
		}
	}
	
	class RackOfSources implements Comparable<RackOfSources> {
		private Rack rack;
		
		private List<SolutionConnection<MicroCloud>> solutionConnections = new ArrayList<>();
		private List<SolutionConnection<MicroCloud>> outerConnections = new ArrayList<>();
		private double sourceDataSize = 0;
		private int workersNumber = 0;
		
		protected RackOfSources(Rack rack) {
			this.rack = rack;
		}
		
		protected Rack getRack() {
			return this.rack;
		}

		protected void addInnerSource(SolutionConnection<MicroCloud> solutionConnection) {
			this.solutionConnections.add(solutionConnection);
			this.sourceDataSize += solutionConnection.getTransfer();
		}
		
		protected void addOuterSource(SolutionConnection<MicroCloud> solutionConnection) {
			this.outerConnections.add(solutionConnection);
		}
		
		protected void setWorkersNumber(int workersNumber) {
			this.workersNumber = workersNumber;
		}
		protected void incrementWorkersNumber() {
			this.workersNumber++;
		}
		protected int getWorkersNumber() {
			return this.workersNumber;
		}
		
		protected List<SolutionConnection<MicroCloud>> getConnections() {
			return this.solutionConnections;
		}
		
		protected double getSourceDataSize() {
			return this.sourceDataSize;
		}

		@Override
		public int compareTo(RackOfSources other) {
			return (int) (other.sourceDataSize - this.sourceDataSize);
		}
		
	}
	
//	class DestinationHosts {
//		Set<Host> hostsSet = new HashSet<>();
//		List<DestinationHost> destinationHosts = new ArrayList<>();
//		
//		protected void addDestinationHost(Host host) {
//			this.hostsSet.add(host);
//			this.destinationHosts.add(new DestinationHost(host));
//		}
//		
//		protected boolean contains(Host host) {
//			return hostsSet.contains(host);
//		}
//		
//		protected DestinationHost get(Host host) {
//			DestinationHost tmp = new DestinationHost(host);
//			int index = destinationHosts.indexOf(tmp);
//			if(index == -1) return null;
//			else return destinationHosts.get(index);
//		}
//		
//	}
//	
//	class DestinationHost {
//		
//		private Host host;
//		private double incomingTransfer = 0;
//		
//		protected DestinationHost(Host host) {
//			this.host = host;
//		}
//		
//		protected void increaseIncomingTransfer(double transfer) {
//			this.incomingTransfer += transfer;
//		}
//		
//		protected double getIncomingTransfer() {
//			return this.incomingTransfer;
//		}
//		
//		protected void createTransferRule(SolutionConnection<MicroCloud> sc) {
//			solutionTransformer.createTransformRuleSimpl(sc,host);
//			incomingTransfer += sc.getTransfer();
//		}
//		
//		@Override
//		public boolean equals(Object obj) {
//			boolean retVal = false;
//			
//			if(obj instanceof DestinationHost) {
//				DestinationHost other = (DestinationHost) obj;
//				if(this.host.equals(other.host)) {
//					retVal = true;
//				}
//			}
//			
//			return retVal;
//		}
//		
//	}


}
