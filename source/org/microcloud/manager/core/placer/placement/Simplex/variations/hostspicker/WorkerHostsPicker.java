package org.microcloud.manager.core.placer.placement.Simplex.variations.hostspicker;

import java.util.List;
import java.util.Map;

import org.microcloud.manager.core.model.datacenter.Host;
import org.microcloud.manager.core.model.datacenter.MicroCloud;
import org.microcloud.manager.core.placer.placement.Simplex.variations.hostspicker.DefaultWorkerHostsPicker.RackOfSources;
import org.microcloud.manager.core.placer.solution.SolutionDestination;
import org.microcloud.manager.core.placer.solution.SolutionTransformer;

public interface WorkerHostsPicker {

	public List<RackOfSources> pickHosts(
			SolutionDestination<MicroCloud> destDataCenter, Integer hostsNumber);

	public void setSolutionTransformer(SolutionTransformer solutionTransformer);
		
}
