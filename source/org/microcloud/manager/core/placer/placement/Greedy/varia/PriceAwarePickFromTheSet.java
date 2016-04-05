package org.microcloud.manager.core.placer.placement.Greedy.varia;

import java.util.Date;

import org.microcloud.manager.core.model.datacenter.Host;
import org.microcloud.manager.core.placer.PlacementProblem;
import org.microcloud.manager.core.placer.placement.Simplex.variations.VariationImplCore;
import org.microcloud.manager.core.placer.solution.SolutionMicroCloud;

public class PriceAwarePickFromTheSet extends VariationImplCore implements PickFromTheSet {

	public PriceAwarePickFromTheSet(PlacementProblem placementProblem) {
		super(placementProblem);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Host pickFromTheSet(Object[] domainOfHosts) {
		
		Date startTime = placementProblem.getClientQuery().getStartTime();
		int expectedExecutionTimeS = placementProblem.getExpectedExecutionTimeS();
		Date endTime = new Date(startTime.getTime() + 1000*expectedExecutionTimeS); 
		
		Host cheapestHost = (Host)domainOfHosts[0]; // just in case
		double lowestPrice = Double.MAX_VALUE;
		
		for(Object o : domainOfHosts) {
			Host h = (Host)o;
			SolutionMicroCloud smc = new SolutionMicroCloud(h.getRack().getMicroCloud());

			double avPriceOfHost = smc.getAverageExecPricePerH(startTime, endTime);
			
			if(avPriceOfHost < lowestPrice) {
				lowestPrice = avPriceOfHost;
				cheapestHost = h;
			}
				
		}
		
		return cheapestHost;
	}

}
