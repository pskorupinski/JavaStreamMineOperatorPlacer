package org.microcloud.manager.core.placer.placement.Simplex.variations.retrievallimit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.microcloud.manager.Factory;
import org.microcloud.manager.core.model.datacenter.MicroCloud;
import org.microcloud.manager.core.model.datasource.DataSourceKeysDistribution;
import org.microcloud.manager.core.model.datasource.DataSourceType;
import org.microcloud.manager.core.model.key.Key;
import org.microcloud.manager.core.model.workeralgorithm.WorkerAlgorithm;
import org.microcloud.manager.core.placer.PlacementProblem;
import org.microcloud.manager.core.placer.placement.Simplex.variations.VariationImplCore;
import org.microcloud.manager.core.placer.solution.SolutionDestination;
import org.microcloud.manager.core.placer.solution.SolutionGraphFullMC;
import org.microcloud.manager.core.placer.solution.SolutionSource;
import org.microcloud.manager.core.placer.solution.SolutionSourceHost;
import org.microcloud.manager.operations.UnitsConv;

public class DefaultRetrievalLimit extends VariationImplCore implements RetrievalLimit {
	
	Double keyLimitKB;

	public DefaultRetrievalLimit(PlacementProblem placementProblem) {
		super(placementProblem);
		
		keyLimitKB = Double.parseDouble(Factory.getInstance().getConstant("retrievalSizeLimit"));
	}

	/**
	 * This implementation is based on a current system, where
	 * real-time sources are on different nodes than historical sources.
	 * If this is the host of a historical data source, the limit will be defined key limit or 1/3 of key sizes (to avoid infeasibilities)
	 */
	public Double countFor(SolutionSourceHost solutionSourceHost) {
		Double limit = null;
		Double keyOfBiggestSize = 0.0;
		Double sumOfKeySizes = 0.0;
		
		for(SolutionSource ss : solutionSourceHost.getSourcesSet()) {
			Key key = ss.getKey().getKey();
			
			if(key.getDataSource().getDataSourceType() == DataSourceType.HISTORICAL) {
				keyOfBiggestSize = Math.max(keyOfBiggestSize, key.getSizeKB());
				sumOfKeySizes += key.getSizeKB();
			}
			/* is is real-time, leave list empty and consequently return null */
		}
		
		if(sumOfKeySizes > 0.0d) {
			limit = Math.max(keyLimitKB, Math.ceil(sumOfKeySizes/3.0));
		}
		
		return limit;
	}

}
