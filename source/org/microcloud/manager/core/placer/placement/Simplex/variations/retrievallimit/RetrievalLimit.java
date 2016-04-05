package org.microcloud.manager.core.placer.placement.Simplex.variations.retrievallimit;

import org.microcloud.manager.core.placer.PlacementProblem;
import org.microcloud.manager.core.placer.solution.SolutionSourceHost;

public interface RetrievalLimit {

	public Double countFor(SolutionSourceHost solutionSourceHost);
	
}
