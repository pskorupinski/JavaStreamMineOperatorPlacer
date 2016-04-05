package org.microcloud.manager.core.placer.postplacement;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.microcloud.manager.core.model.clientquery.ClientQuery;
import org.microcloud.manager.core.model.datacenter.Host;
import org.microcloud.manager.core.model.datasource.DataSourceKeysDistribution;
import org.microcloud.manager.core.placer.PlacementProblem;
import org.microcloud.manager.core.placer.solution.SolutionDestination;
import org.microcloud.manager.core.placer.solution.SolutionGraph;
import org.microcloud.manager.core.placer.solution.SolutionGraphDoneHost;
import org.microcloud.manager.core.placer.solution.SolutionSource;

public abstract class PostPlacement {
	
	protected SolutionGraphDoneHost solutionGraph;
	protected PlacementProblem placementProblem;
	
	protected boolean isFeasible;
	
	protected List<SolutionSource> sourceHostsProblematic = new ArrayList<>();
	protected List<SolutionDestination<Host>> destinationHostsProblematic = new ArrayList<>();
	

	public PostPlacement(SolutionGraphDoneHost solutionGraph, PlacementProblem placementProblem) {
		this.solutionGraph = solutionGraph;
		this.placementProblem = placementProblem;
		this.isFeasible = false;
	}
	
	public void run() {
		preRun();
		doRun();
	}
	
	protected abstract void doRun();
	
	public boolean isFeasible() {
		return this.isFeasible;
	}

	public List<SolutionSource> getSourceHostsProblematic() {
		return sourceHostsProblematic;
	}
	public List<SolutionDestination<Host>> getDestinationHostsProblematic() {
		return destinationHostsProblematic;
	}
	
	protected void preRun() {
		solutionGraph.confirmAStructure(placementProblem);
	}
	
}
