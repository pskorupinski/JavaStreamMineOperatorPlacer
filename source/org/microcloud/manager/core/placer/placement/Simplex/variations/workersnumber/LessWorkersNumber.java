package org.microcloud.manager.core.placer.placement.Simplex.variations.workersnumber;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.microcloud.manager.core.model.datacenter.MicroCloud;
import org.microcloud.manager.core.placer.PlacementProblem;
import org.microcloud.manager.core.placer.placement.Simplex.variations.VariationImplCore;
import org.microcloud.manager.core.placer.solution.SolutionDestination;
import org.microcloud.manager.core.placer.solution.SolutionGraphFullMC;
import org.microcloud.manager.operations.OperationsOnNumbers;

public class LessWorkersNumber extends VariationImplCore implements WorkersNumber {

	public LessWorkersNumber(PlacementProblem placementProblem) {
		super(placementProblem);
	}

	@Override
	public Map<SolutionDestination<MicroCloud>, Integer> count(SolutionGraphFullMC fullInitGraph) {
		
		Map<SolutionDestination<MicroCloud>,Integer> numbersMap = new HashMap<SolutionDestination<MicroCloud>, Integer>();
		
		/* 1. Decide how many workers should be in a whole system */
		int workersNumberInSystem = 0;
		
		NeededWorkersCounter workersCounter = 
				new NeededWorkersCounter(fullInitGraph, placementProblem.getClientQuery().getWorkerAlgorighmType());
		workersNumberInSystem = workersCounter.count();
		
		workersNumberInSystem = (int) Math.round(0.75*(double)(workersNumberInSystem));
		
		org.microcloud.manager.logger.MyLogger.getInstance().log("Needed workers number counted as " + workersNumberInSystem);
		
		/* 2. Count how much per MicroCloud would that be */
		/* 2a. Sorted destinations collections (greatest -> smallest transferTo) */
		List<SolutionDestination<MicroCloud>> solutionDestList = new ArrayList<>(fullInitGraph.getDestinations());
		Collections.sort(solutionDestList, SolutionDestination.DestinationTransferToComparator);
		/* 2b. Translate to doubles list */
		List<Double> sourceDataSizes = new ArrayList<Double>();
		for(SolutionDestination<MicroCloud> sd : solutionDestList) { 
			sourceDataSizes.add(sd.getTransfer());
		}
		/* 2c. Translate to integers list */
		List<Integer> workersNumbers = 
				OperationsOnNumbers.doublesListToIntegersList(sourceDataSizes, workersNumberInSystem);
		/* 2d. Translate to [destination -> integer] map */
		int i=0;
		for(SolutionDestination<MicroCloud> sd : solutionDestList) { 
			numbersMap.put(sd, workersNumbers.get(i));
			i++;
		}	
		
		org.microcloud.manager.logger.MyLogger.getInstance().log("Workers number by MicroCloud");
		org.microcloud.manager.logger.MyLogger.getInstance().log(numbersMap);
		
		return numbersMap;
	}

	
}
