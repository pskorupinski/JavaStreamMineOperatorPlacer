package org.microcloud.manager.core.placer.postplacement.first;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.microcloud.manager.core.model.datacenter.Host;
import org.microcloud.manager.core.placer.PlacementProblem;
import org.microcloud.manager.core.placer.postplacement.PostPlacement;
import org.microcloud.manager.core.placer.solution.SolutionDestination;
import org.microcloud.manager.core.placer.solution.SolutionGraphDoneHost;
import org.microcloud.manager.core.placer.solution.SolutionSource;
import org.microcloud.manager.core.placer.solution.SolutionSourceHost;
import org.microcloud.manager.persistence.EntityLoader;

public class SecondPostPlacement extends PostPlacement {

	public SecondPostPlacement(SolutionGraphDoneHost solutionGraph, PlacementProblem placementProblem) {
		super(solutionGraph, placementProblem);
	}

	@Override
	public void doRun() {
		
		/* 1. Get a time when the execution should start */
		Date startTime = placementProblem.getClientQuery().getStartTime();
		if(startTime == null)
			startTime = new Date();
		
		HashSet<SolutionSourceHost> sshSet = new HashSet<>();
		for(SolutionSource ss : solutionGraph.getSources()) {
			if(sshSet.add(ss.getHost())) {
				SolutionSourceHost ssh = ss.getHost();
				Date endTime = ssh.getEndTime();
				Set<Integer> hostIds = new HashSet<>();
				hostIds.add(ssh.getHost().getId());
				
				/* TODO use more efficient way ... */
				List<Host> occupiedHostsList = EntityLoader.getInstance().getBusyHostsInTime(hostIds, startTime, endTime);
				
				if(!occupiedHostsList.isEmpty())
					sourceHostsProblematic.addAll(ssh.getSourcesSet());
			}
		}
		
		for(SolutionDestination<Host> sd : solutionGraph.getDestinations()) {
			Date endTime = sd.getEndTime();
			Set<Integer> hostIds = new HashSet<>();
			hostIds.add(sd.getDestination().getId());
			
			/* TODO use more efficient way ... */
			List<Host> occupiedHostsList = EntityLoader.getInstance().getBusyHostsInTime(hostIds, startTime, endTime);
			
			if(!occupiedHostsList.isEmpty())
				destinationHostsProblematic.add(sd);
		}
		
		/* 6. */
		if( destinationHostsProblematic.isEmpty() && sourceHostsProblematic.isEmpty() ) {
			isFeasible = true;
		}
		
	}

}
