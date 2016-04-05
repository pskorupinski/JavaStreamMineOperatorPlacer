package org.microcloud.manager.core.placer.solution;

import java.util.Map;

import org.apache.commons.collections.iterators.EntrySetMapIterator;
import org.microcloud.manager.core.model.datacenter.Host;
import org.microcloud.manager.core.model.datacenter.MicroCloud;
import org.microcloud.manager.core.model.key.Key;

public class SolutionTransformer extends SolutionBuilder<Host> {

	private SolutionGraphFullDC inputGraph;

	public SolutionTransformer(SolutionGraphFullDC inputGraph) {
		this.inputGraph = inputGraph;
	}
	
	public void createTransformRule(SolutionSource ss, SolutionDestination<MicroCloud> sd, Map<Host, Integer> dstHosts) {
		
		/*
		 *  PRECONDITIONS
		 */
		/* 1. Whether source & destination exist */
		int index = inputGraph.getConnections().indexOf(new SolutionConnection<>(ss, sd));
		if(index < 0) return;
		/* 2. Whether the transfer on a connection between stays the same */
		double transferToDestMC = inputGraph.getConnections().get(index).getTransfer();
		double transferToDestHosts = 0;
		for( Map.Entry<Host, Integer> hostTransfer : dstHosts.entrySet() ) {
			transferToDestHosts += hostTransfer.getValue();
		}
		if(transferToDestHosts != transferToDestMC)
			return;
		
		/*
		 * METHOD BODY
		 */
		ss = checkAddToList(ss, solutionSourceList);
		for( Map.Entry<Host, Integer> hostTransfer : dstHosts.entrySet() ) {
			SolutionDestination<Host> sdh = 
					createDestination(hostTransfer.getKey());
			sdh = checkAddToList(sdh, solutionDestinationList);
			createConnectionCore(ss, sdh, hostTransfer.getValue());
		}
	
	}
	
	public void createTransformRuleSimpl(SolutionConnection<MicroCloud> sc, Host dstHost) {
		
		/*
		 *  PRECONDITION
		 */
		int index = inputGraph.getConnections().indexOf(sc);
		if(index < 0) return;
		
		/*
		 * METHOD BODY
		 */
		SolutionSource ss = checkAddToList(sc.getSource(), solutionSourceList);
		SolutionDestination<Host> sdh = createDestination(dstHost);
		sdh = checkAddToList(sdh, solutionDestinationList);
		createConnectionCore(ss, sdh, sc.getTransfer());
		
	}
	
	
	public SolutionGraphDoneHost getSolutionGraph() {
		return (SolutionGraphDoneHost) getSolutionGraph(GraphConfirmationType.DONE_DESTHOST);
	}
		
	
}
