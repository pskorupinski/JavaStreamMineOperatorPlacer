package org.microcloud.manager.core.placer.placement.Simplex.variations.transfersanalyser;


import org.microcloud.manager.core.placer.PlacementProblem;
import org.microcloud.manager.core.placer.solution.SolutionGraphFullDC;

public class DefaultTrasfersAnalyser implements TransfersAnalyser {

	@Override
	public boolean conformTransfers(SolutionGraphFullDC fullInitGraph,
			PlacementProblem placementProblem) {

		boolean nextIterationNeeded = false;
		
		/* DO NOTHING */
		
		return nextIterationNeeded;
	}

}
