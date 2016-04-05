package org.microcloud.manager.core.placer.placement.Simplex;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.microcloud.manager.Factory;
import org.microcloud.manager.core.model.datacenter.Host;
import org.microcloud.manager.core.model.datacenter.MicroCloud;
import org.microcloud.manager.core.model.key.DivisibleKey;
import org.microcloud.manager.core.model.key.DivisibleKeyInterface;
import org.microcloud.manager.core.placer.PlacementProblem;
import org.microcloud.manager.core.placer.placement.Simplex.variations.hostspicker.WorkerHostsPicker;
import org.microcloud.manager.core.placer.placement.Simplex.variations.workersnumber.WorkersNumber;
import org.microcloud.manager.core.placer.solution.GraphConfirmationType;
import org.microcloud.manager.core.placer.solution.SolutionConnection;
import org.microcloud.manager.core.placer.solution.SolutionDestination;
import org.microcloud.manager.core.placer.solution.SolutionGraphDoneHost;
import org.microcloud.manager.core.placer.solution.SolutionGraphFullMC;
import org.microcloud.manager.core.placer.solution.SolutionKey;
import org.microcloud.manager.core.placer.solution.SolutionNormalizer;
import org.microcloud.manager.core.placer.solution.SolutionSource;
import org.microcloud.manager.core.placer.solution.SolutionTransformer;

public class NewAnalyseConvertSimplexSolution  {

	private List<Object> workersNumberImpls;
	private WorkerHostsPicker workerHostsPicker;
	
	private PlacementProblem placementProblem;
	private SolutionGraphFullMC fullInitGraph;
	
	private Map<SolutionDestination<MicroCloud>, List<Host>> hostListsPerDataCenterMap = null;
	private List<SolutionGraphDoneHost> hostDoneGraphsList;

	public NewAnalyseConvertSimplexSolution(PlacementProblem placementProblem, SolutionGraphFullMC fullInitGraph) {
		this.hostDoneGraphsList = new ArrayList<>();
		this.placementProblem = placementProblem;
		this.fullInitGraph = fullInitGraph;
		
		Class<?>[] paramTypes = new Class<?>[]{PlacementProblem.class};
		Object[] params = new Object[]{placementProblem};
		this.workersNumberImpls = Factory.getInstance().newInstances("workersNumber",paramTypes,params);
		this.workerHostsPicker = (WorkerHostsPicker) Factory.getInstance().newInstance("hostsPicker",paramTypes,params);
	}
	
////////////////////////////////////////////
////PROTECTED METHODS
////////////////////////////////////////////

	protected boolean analyze() {
		
		/* 0. */
		normalizeIndivisibleKeysSources();
		
		for(Object obj : workersNumberImpls) {
			
			org.microcloud.manager.logger.MyLogger.getInstance().log("\n New Analyze/Convert run");
			
			WorkersNumber workersNumber = (WorkersNumber) obj;
		
			/* 1. Count number of workers needed in the system */
			/* 2. Count number of workers per MicroCloud */
			Map<SolutionDestination<MicroCloud>, Integer> numbersMap;
			numbersMap = workersNumber.count(fullInitGraph);
			
			/* 3. Conform sources, keys, transfers to how will they look in practice
			 * (equally to every worker) */
			SolutionNormalizer equalingTransformer = 
					new SolutionNormalizer(fullInitGraph, numbersMap);
			SolutionGraphFullMC actualMCGraph = equalingTransformer.getSolutionGraph();
			
			/* 4. Pick hosts in every MicroCloud */
			SolutionTransformer solutionTransformer;
			solutionTransformer = new SolutionTransformer(actualMCGraph);
			workerHostsPicker.setSolutionTransformer(solutionTransformer);
		
			for(Map.Entry<SolutionDestination<MicroCloud>, Integer> dataCenterCase : numbersMap.entrySet()) {
				if(dataCenterCase.getValue()>0) {
					int index = actualMCGraph.getDestinations().indexOf(dataCenterCase.getKey());
					workerHostsPicker.pickHosts(actualMCGraph.getDestinations().get(index), dataCenterCase.getValue()); 
				}
			}
			
			/* 5. Connect sources with destinations */
			solutionTransformer.setConnectionsWithTransfers();
			
			/* 6. Process confirmation */
			hostDoneGraphsList.add(solutionTransformer.getSolutionGraph());
			
		}
		

		return true;
	}

	protected List<SolutionGraphDoneHost> getConverted() {
		return this.hostDoneGraphsList;
	}
	
	
	
	private void normalizeIndivisibleKeysSources() {
		
		Set<SolutionKey> solutionKeys = new HashSet<>();
		
		for(SolutionSource solutionSource : fullInitGraph.getSources()) {
			
			SolutionKey sk = solutionSource.getKey();
			if(solutionKeys.add(sk)) {				
				
				boolean isDivisible = false;
			
				if(sk.getKey() instanceof DivisibleKeyInterface) {
					if(((DivisibleKeyInterface)sk.getKey()).getNumberOfParts() != 1)
						isDivisible = true;
				}
				
				
				if(!isDivisible) {

					List<SolutionSource> keySources = new ArrayList<>(sk.getSourcesSet());
					
					/* Find the one with the maximum out-transfer  */
					int maxSSNo = 0;
					for(int i=1; i<keySources.size(); i++) {
						if(keySources.get(i).getTransfer() > keySources.get(maxSSNo).getTransfer()) {
							maxSSNo = i;
						}
					}					
					
					/* Change transfers depending on whether maximum out-transfer or not */
					for(int i=0; i<keySources.size(); i++) {
						SolutionSource ss = keySources.get(i);
						Set<SolutionConnection<?>> sourceConns = ss.getOutputConnections();
						if(i==maxSSNo) {
							/* Count with what proportion to rise the data transfer on connections */
							double currentTransfer = ss.getTransfer();
							double neededTransfer = ss.getKey().getKey().getSizeKB();
							double proportion = neededTransfer / currentTransfer;
							
							for(SolutionConnection<?> sc : sourceConns) {
								sc.setTransfer(proportion*sc.getTransfer());
							}
						}
						else {
							for(SolutionConnection<?> sc : sourceConns) {
								sc.setTransfer(0.0);
							}							
						}
					}
					
				}
			
			}
			
		}
		
	}
	
}
