package org.microcloud.manager.core.placer.placement;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.microcloud.manager.core.model.clientquery.ClientQuery;
import org.microcloud.manager.core.model.datacenter.Host;
import org.microcloud.manager.core.model.datasource.DataSourceKeysDistribution;
import org.microcloud.manager.core.placer.PlacementProblem;
import org.microcloud.manager.core.placer.postplacement.PostPlacement;
import org.microcloud.manager.core.placer.postplacement.first.FirstPostPlacement;
import org.microcloud.manager.core.placer.postplacement.first.SecondPostPlacement;
import org.microcloud.manager.core.placer.preplacement.PrePlacement;
import org.microcloud.manager.core.placer.preplacement.first.FirstPrePlacement;
import org.microcloud.manager.core.placer.solution.SolutionGraph;
import org.microcloud.manager.core.placer.solution.SolutionGraphDoneHost;

public abstract class PlacementAlgorithm {
	
	private Set<DataSourceKeysDistribution> originalKeysHostsMapsSet;
	//protected Set<DataSourceKeysDistribution> keysHostsMapsSet = null;
	protected PlacementProblem placementProblem = null;
	protected ClientQuery clientQuery;
	
	protected final List<SolutionGraphDoneHost> solutionGraphs = new ArrayList<>();
	
	protected List<PostPlacement> postPlacementsForReasoning = new ArrayList<>();
	
////////////////////////////////////////////
////CONSTRUCTORS
////////////////////////////////////////////
	
	public PlacementAlgorithm(Set<DataSourceKeysDistribution> keysHostsMapsSet, ClientQuery clientQuery) {
		this.originalKeysHostsMapsSet = keysHostsMapsSet;
		this.clientQuery = clientQuery;
	}
	
////////////////////////////////////////////
////GETTERS
////////////////////////////////////////////

public List<SolutionGraphDoneHost> getSolutionGraphs() {
	return solutionGraphs;
}
	
////////////////////////////////////////////
////PUBLIC METHODS
////////////////////////////////////////////

	public final boolean runAlgorithm() {
		org.microcloud.manager.logger.MyLogger.getInstance().log(" >>> Calling preplacement... ");
		prePlacement();
		org.microcloud.manager.logger.MyLogger.getInstance().log(" >>> Calling placement... ");
		doRunAlgorithm();
		org.microcloud.manager.logger.MyLogger.getInstance().log(" >>> Calling postplacement... ");
		return postPlacement();
	}
	
////////////////////////////////////////////
////PROTECTED METHODS
////////////////////////////////////////////
	
	protected abstract void doRunAlgorithm();
	
	protected void prePlacement() {
		// 1. remove hosts that will be busy during the time
		PrePlacement prePlacement = new FirstPrePlacement(originalKeysHostsMapsSet, clientQuery);
		prePlacement.run();
		placementProblem = prePlacement.getPlacementProblem();
	}
	
	/**
	 * 
	 * @return whether any of found solutions is feasible
	 */
	protected boolean postPlacement() {
		PostPlacement postPlacement;
		
		// 1. Check if any of solutions found is still feasible
		for(SolutionGraphDoneHost solutionGraph : solutionGraphs) {
			org.microcloud.manager.logger.MyLogger.getInstance().log("\n\nFirst call of postplacement.\n");
			
			postPlacement = new SecondPostPlacement(solutionGraph, placementProblem);
			postPlacement.run();
			if(!postPlacement.isFeasible()) {
				solutionGraphs.remove(solutionGraph);
				postPlacementsForReasoning.add(postPlacement);
			}
		}
		
		return ! solutionGraphs.isEmpty();
	}

}
