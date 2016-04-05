package org.microcloud.manager.core.placer.solution;

import java.util.List;

import org.microcloud.manager.core.model.datacenter.Host;
import org.microcloud.manager.core.model.datacenter.MicroCloud;
import org.microcloud.manager.core.placer.PlacementProblem;
import org.microcloud.manager.core.pricetimeapprox.SolutionPriceTimeApproximator;

public class SolutionGraphDoneHost extends SolutionGraph<Host> {
	
	private Integer approximateTime = null;
	private Integer approximatePrice = null;

	protected SolutionGraphDoneHost(List<SolutionSource> sources,
			List<SolutionDestination<Host>> destinations,
			List<SolutionConnection<Host>> connections,
			int sourceHostsNo, int keysNo) {
		super(sources,destinations,connections,sourceHostsNo,keysNo);
	}
	
	protected SolutionGraphDoneHost(List<SolutionSource> sources,
			List<SolutionDestination<Host>> destinations,
			boolean connectAll,
			int sourceHostsNo, int keysNo) {
		super(sources,destinations,connectAll,sourceHostsNo,keysNo);
	}
	
	protected SolutionGraphDoneHost() {
		super();
	}

	@Override
	public void confirmAStructure(PlacementProblem placementProblem) {
		structureConfirmed = true;
		
		/* Every connection will transfer the same amount of data
		 * from a specific host */
		for(SolutionConnection<Host> sc : connections)
			sc.setTransferBySource();
		
		SolutionPriceTimeApproximator solutionPriceTimeApproximator = 
				new SolutionPriceTimeApproximator(
						(SolutionGraph<Host>) this, 
						placementProblem.getClientQuery().getStartTime(), 
						placementProblem.getClientQuery().getWorkerAlgorighmType());
		solutionPriceTimeApproximator.run();
		approximateTime = solutionPriceTimeApproximator.getTime();
		approximatePrice= solutionPriceTimeApproximator.getPrice();
	}
	
	public Integer getApproximateTime() {
		return approximateTime;
	}
	public Integer getApproximatePrice() {
		return approximatePrice;
	}
	
	@Override
	public String toString() {
		String ret = "";
		
		ret += "Approximate price: " + approximatePrice + "\n";
		ret += "Approximate time:  " + approximateTime  + "\n";
		
		int i = 1;
		ret += "Solution sources: \n";
		for(SolutionSource src : this.sources) {
			ret += i++ + ". " + src + "\n";
		}
		i = 1;
		ret += "Solution destinations: \n";
		for(SolutionDestination<Host> dst : this.destinations) {
			ret += i++ + ". " + dst + "\n";
		}
		
		return ret;
	}

}
