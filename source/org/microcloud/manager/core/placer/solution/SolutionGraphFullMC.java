package org.microcloud.manager.core.placer.solution;

import java.util.ArrayList;
import java.util.List;

import org.microcloud.manager.core.model.datacenter.MicroCloud;
import org.microcloud.manager.core.placer.PlacementProblem;
import org.microcloud.manager.core.placer.placement.Simplex.TotalConnectionExecution;

public class SolutionGraphFullMC extends SolutionGraph<MicroCloud> {
	
	private List<TotalConnectionExecution> totalConnectionExecutionList = new ArrayList<>();

	protected SolutionGraphFullMC(List<SolutionSource> sources,
			List<SolutionDestination<MicroCloud>> destinations,
			List<SolutionConnection<MicroCloud>> connections,
			int sourceHostsNo, int keysNo) {
		super(sources,destinations,connections, sourceHostsNo, keysNo);
	}
	
	protected SolutionGraphFullMC(List<SolutionSource> sources,
			List<SolutionDestination<MicroCloud>> destinations,
			boolean connectAll, int sourceHostsNo, int keysNo) {
		super(sources,destinations,connectAll, sourceHostsNo, keysNo);
	}
	
	protected SolutionGraphFullMC() {
		super();
	}

	@Override
	public void confirmAStructure(PlacementProblem placementProblem) {
		int i=0;
		for(SolutionConnection<MicroCloud> con : connections) {
			TotalConnectionExecution totalConnectionExecution = 
					new TotalConnectionExecution(i, con, placementProblem);
			totalConnectionExecutionList.add(totalConnectionExecution);
			i++;
		}
		
	}

	public List<TotalConnectionExecution> getTotalConnectionExecutionList() {
		return totalConnectionExecutionList;
	}
	
////////////////////////////////////////////
//// OVERRIDEN METHODS
////////////////////////////////////////////

	@Override
	public String toString() {
		String ret = "";
		
		for(TotalConnectionExecution connEx : this.totalConnectionExecutionList) {
			ret += connEx.toString() + "\n";
		}
		
		return ret;
	}
	
}
	