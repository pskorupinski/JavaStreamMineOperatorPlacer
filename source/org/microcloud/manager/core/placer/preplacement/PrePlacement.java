package org.microcloud.manager.core.placer.preplacement;

import java.util.Set;

import org.microcloud.manager.core.model.clientquery.ClientQuery;
import org.microcloud.manager.core.model.datasource.DataSourceKeysDistribution;
import org.microcloud.manager.core.placer.PlacementProblem;

public abstract class PrePlacement {
	
	//protected Set<DataSourceKeysDistribution> keysHostsMapsSet;
	//protected ClientQuery clientQuery;
	
	protected PlacementProblem placementProblem;

	public PrePlacement(Set<DataSourceKeysDistribution> keysHostsMapsSet, ClientQuery clientQuery) {
		this.placementProblem = new PlacementProblem(keysHostsMapsSet, clientQuery);
	}
	
	/**
	 * Remove machines that cannot be used during the time that is needed for execution.
	 * To count it well, come PrePlacement algorithms should be able to approximate the time of execution.
	 */
	public abstract void run();
	
	public PlacementProblem getPlacementProblem() {
		return this.placementProblem;
	}
	
}
