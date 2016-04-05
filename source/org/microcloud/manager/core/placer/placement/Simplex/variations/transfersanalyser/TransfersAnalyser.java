package org.microcloud.manager.core.placer.placement.Simplex.variations.transfersanalyser;

import org.microcloud.manager.core.placer.PlacementProblem;
import org.microcloud.manager.core.placer.solution.SolutionGraphFullDC;

public interface TransfersAnalyser {

	/**
	 * Method responsible for: 
	 * <ul>
	 * 	<li>rounding the transfers, so that they can be adapted more easily to the actual execution model
	 * 		(eg. rounding them so they can be divided by number of chunks of key.) </li>
	 * </ul>
	 * 
	 * @param fullInitGraph
	 * @param placementProblem
	 * @return
	 */
	public abstract boolean conformTransfers(SolutionGraphFullDC fullInitGraph, PlacementProblem placementProblem);
	
}
