package org.microcloud.manager.core.placer.solution;

import java.util.Map;

import org.apache.commons.collections.iterators.EntrySetMapIterator;
import org.microcloud.manager.core.model.datacenter.Host;
import org.microcloud.manager.core.model.datacenter.MicroCloud;
import org.microcloud.manager.core.model.key.Key;

public class SolutionTransformer extends SolutionBuilder<Host> {

	private SolutionGraphFullMC inputGraph;

	public SolutionTransformer(SolutionGraphFullMC inputGraph) {
		this.inputGraph = inputGraph;
		copySources();
	}
	
	public void setConnectionsWithTransfers() {
		connectAll();
		
		for(SolutionConnection<Host> sc : solutionConnectionList)
			sc.setTransferBySource();
	}

	public SolutionGraphDoneHost getSolutionGraph() {
		return (SolutionGraphDoneHost) getSolutionGraph(GraphConfirmationType.DONE_DESTHOST);
	}
	
	
	private void copySources() {
		/* TODO instead of this, lists copying could be done, but might be problematic? */
		for(SolutionSource ss : inputGraph.sources) {
			this.createSource(ss.getHost().getHost(), ss.getKey().getKey());
		}	
	}
		
	
}
