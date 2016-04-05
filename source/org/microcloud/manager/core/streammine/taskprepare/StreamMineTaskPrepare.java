package org.microcloud.manager.core.streammine.taskprepare;


import org.microcloud.manager.core.model.clientquery.ClientQuery;
import org.microcloud.manager.core.placer.solution.SolutionGraphDoneHost;

public interface StreamMineTaskPrepare {

	public boolean storeTask(SolutionGraphDoneHost solutionGraph, ClientQuery clientQuery);
	
}
