package org.microcloud.manager.core.placer.placement.Simplex;

import java.util.List;
import java.util.Map;

import org.microcloud.manager.core.model.datacenter.Host;
import org.microcloud.manager.core.model.datacenter.MicroCloud;
import org.microcloud.manager.core.placer.PlacementProblem;
import org.microcloud.manager.core.placer.placement.Simplex.variations.hostspicker.WorkerHostsPicker;
import org.microcloud.manager.core.placer.placement.Simplex.variations.transfersanalyser.TransfersAnalyser;
import org.microcloud.manager.core.placer.placement.Simplex.variations.workersnumber.WorkersNumber;
import org.microcloud.manager.core.placer.solution.SolutionDestination;
import org.microcloud.manager.core.placer.solution.SolutionGraphDoneHost;
import org.microcloud.manager.core.placer.solution.SolutionGraphFullDC;
import org.microcloud.manager.core.placer.solution.SolutionTransformer;

public class AnalyseConvertSimplexSolution {

	private TransfersAnalyser transferAnalyser;
	private WorkersNumber workersNumber;
	private WorkerHostsPicker workerHostsPicker;
	
	private PlacementProblem placementProblem;
	private SolutionGraphFullDC fullInitGraph;
	
	private Map<SolutionDestination<MicroCloud>, List<Host>> hostListsPerDataCenterMap = null;
	private SolutionGraphDoneHost hostDoneGraph = null;
	
	protected AnalyseConvertSimplexSolution(
			TransfersAnalyser transferAnalyser,
			WorkersNumber workersNumber,
			WorkerHostsPicker hostsChooser) {
		this.transferAnalyser = transferAnalyser;
		this.workersNumber = workersNumber;
		this.workerHostsPicker = hostsChooser;
	}
	
////////////////////////////////////////////
//// PROTECTED METHODS
////////////////////////////////////////////
	
	protected void init(PlacementProblem placementProblem, SolutionGraphFullDC fullInitGraph) {
		this.hostDoneGraph = null;
		this.placementProblem = placementProblem;
		this.fullInitGraph = fullInitGraph;
	}

	protected boolean analyze() {
		
		boolean nextIterationNeeded = false;
		
		/* Conforming transfers */
		
		nextIterationNeeded = transferAnalyser.conformTransfers(fullInitGraph, placementProblem);
		
		/* Conforming destinations */
		
		Map<SolutionDestination<MicroCloud>, Integer> numbersMap;
		numbersMap = workersNumber.count(fullInitGraph);
		
		SolutionTransformer solutionTransformer;
		solutionTransformer = new SolutionTransformer(fullInitGraph);
		workerHostsPicker.setSolutionTransformer(solutionTransformer);
		
		for(Map.Entry<SolutionDestination<MicroCloud>, Integer> dataCenterCase : numbersMap.entrySet()) {
			workerHostsPicker.pickHosts(dataCenterCase.getKey(), dataCenterCase.getValue()); 
		}
		
		/* Conforming sources (keys) */
		
		solutionTransformer.redefineKeys();
		
		/* Process confirmation */
		
		hostDoneGraph = solutionTransformer.getSolutionGraph();
		
		return nextIterationNeeded;
	}

	protected SolutionGraphDoneHost getConverted() {
		return hostDoneGraph;
	}
	
}
