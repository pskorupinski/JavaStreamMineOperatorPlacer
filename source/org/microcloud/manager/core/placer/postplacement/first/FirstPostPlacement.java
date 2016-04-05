package org.microcloud.manager.core.placer.postplacement.first;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.microcloud.manager.core.model.clientquery.ClientQuery;
import org.microcloud.manager.core.model.datacenter.Host;
import org.microcloud.manager.core.model.datasource.DataSourceKeysDistribution;
import org.microcloud.manager.core.model.key.Key;
import org.microcloud.manager.core.placer.PlacementProblem;
import org.microcloud.manager.core.placer.postplacement.PostPlacement;
import org.microcloud.manager.core.placer.preplacement.PrePlacement;
import org.microcloud.manager.core.placer.solution.SolutionDestination;
import org.microcloud.manager.core.placer.solution.SolutionGraph;
import org.microcloud.manager.core.placer.solution.SolutionGraphDoneHost;
import org.microcloud.manager.core.placer.solution.SolutionSource;
import org.microcloud.manager.persistence.EntityLoader;
import org.microcloud.manager.structures.UniqueArrayList;
import org.microcloud.manager.structures.UniqueBiMapping;

public class FirstPostPlacement extends PostPlacement {

	public FirstPostPlacement(SolutionGraphDoneHost solutionGraph, PlacementProblem placementProblem) {
		super(solutionGraph, placementProblem);
	}

	@Override
	public void doRun() {
		
		/* 1. Get a time when the execution should start */
		Date startTime = placementProblem.getClientQuery().getStartTime();
		if(startTime == null)
			startTime = new Date();
		
		/* 2. Get an approximation of how long will the execution last */
		int executionPeriodS = solutionGraph.getApproximateTime();
		Date endTime = new Date( startTime.getTime() + 1000 * executionPeriodS );
		
		/* 3. make a list of all hosts ids */
		Set<Integer> hostsIds = new HashSet<>();
		
		for(SolutionSource solutionSource : solutionGraph.getSources()) {
			hostsIds.add( solutionSource.getHost().getHost().getId() );
		}
		for(SolutionDestination<Host> solutionDestination : solutionGraph.getDestinations()) {
			hostsIds.add( solutionDestination.getDestination().getId() );
		}
		
		/* 4. Query for information about all of the hosts in the system */
		List<Host> occupiedHostsList = EntityLoader.getInstance().getBusyHostsInTime(hostsIds, startTime, endTime);
		
		/* 5.   */
		for(SolutionSource solutionSource : solutionGraph.getSources()) {
			if(occupiedHostsList.contains(solutionSource.getHost())) {
				sourceHostsProblematic.add(solutionSource);
			}
		}
		for(SolutionDestination<Host> solutionDestination : solutionGraph.getDestinations()) {
			if(occupiedHostsList.contains(solutionDestination.getDestination())) {
				destinationHostsProblematic.add(solutionDestination);
			}
		}
		
		/* 6. */
		if( destinationHostsProblematic.isEmpty() && sourceHostsProblematic.isEmpty() ) {
			isFeasible = true;
		}
		
	}

}
