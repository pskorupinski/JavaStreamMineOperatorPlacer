package org.microcloud.manager.core.placer;

import java.util.Set;

import org.microcloud.manager.core.model.clientquery.ClientQuery;
import org.microcloud.manager.core.model.datasource.DataSourceKeysDistribution;

public class PlacementProblem {
	
	public PlacementProblem(Set<DataSourceKeysDistribution> keysHostsMapsSet,	
							ClientQuery clientQuery) {
		this.keysHostsMapsSet = keysHostsMapsSet;
		this.clientQuery = clientQuery;
	}
	
	private Set<DataSourceKeysDistribution> keysHostsMapsSet;
	private ClientQuery clientQuery;
	
	private int expectedExecutionTimeS = 7200;
	
	public Set<DataSourceKeysDistribution> getKeysHostsMapsSet() {
		return keysHostsMapsSet;
	}
	public ClientQuery getClientQuery() {
		return clientQuery;
	}
	
	public int getExpectedExecutionTimeS() {
		return expectedExecutionTimeS;
	}
	public void setExpectedExecutionTimeS(int expectedExecutionTimeS) {
		this.expectedExecutionTimeS = expectedExecutionTimeS;
	}
	
}
