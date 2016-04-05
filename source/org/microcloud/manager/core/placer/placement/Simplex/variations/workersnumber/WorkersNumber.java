package org.microcloud.manager.core.placer.placement.Simplex.variations.workersnumber;

import java.util.List;
import java.util.Map;

import org.apache.commons.math3.optim.linear.LinearConstraintSet;
import org.microcloud.manager.core.model.datacenter.MicroCloud;
import org.microcloud.manager.core.placer.PlacementProblem;
import org.microcloud.manager.core.placer.placement.Simplex.TotalConnectionExecution;
import org.microcloud.manager.core.placer.solution.SolutionDestination;
import org.microcloud.manager.core.placer.solution.SolutionGraphFullMC;

public interface WorkersNumber {

	Map<SolutionDestination<MicroCloud>, Integer> count(
			SolutionGraphFullMC fullInitGraph);

}
