package org.microcloud.manager.core.placer.placement.Simplex.variations.workersnumberapprox;

import java.util.Map;

import org.microcloud.manager.core.model.datacenter.MicroCloud;
import org.microcloud.manager.core.model.datasource.DataSourceKeysDistribution;
import org.microcloud.manager.core.model.workeralgorithm.WorkerAlgorithm;
import org.microcloud.manager.core.placer.PlacementProblem;
import org.microcloud.manager.core.placer.placement.Simplex.variations.VariationImplCore;
import org.microcloud.manager.core.placer.solution.SolutionDestination;
import org.microcloud.manager.core.placer.solution.SolutionGraphFullMC;
import org.microcloud.manager.operations.UnitsConv;

public class QuaterMaxWorkersNumberApprox extends VariationImplCore implements WorkersNumberApprox {

	public QuaterMaxWorkersNumberApprox(PlacementProblem placementProblem) {
		super(placementProblem);
		// TODO Auto-generated constructor stub
	}

	public int count() {
		
		int approximateWorkersNumber = 0;
		
		/* 1. Count amount of data potentially produced per second.
		 *    It is MAX amount of data that might have been needed to be processed per second. */
		double totalBandwidthMBs = 0;
		for(DataSourceKeysDistribution dskd : placementProblem.getKeysHostsMapsSet()) {
			double bandwidthPerKeyMBs = UnitsConv.bitToByte(dskd.getDataSource().getSourceBandwidthKbMin()) / (double)(60 * 1024);
			int keysNumber = dskd.getHostKeysMapping().getColumnsNumber();
			
			totalBandwidthMBs += keysNumber * bandwidthPerKeyMBs;
		}
		
		approximateWorkersNumber = WorkerAlgorithm.doFindWorkersNumber(totalBandwidthMBs,placementProblem.getClientQuery().getWorkerAlgorighmType());
		
		org.microcloud.manager.logger.MyLogger.getInstance().log("Approximated workers number counted as " + approximateWorkersNumber);
		
		return (int) Math.round((double)approximateWorkersNumber/4.0*3.0);
	}

}
