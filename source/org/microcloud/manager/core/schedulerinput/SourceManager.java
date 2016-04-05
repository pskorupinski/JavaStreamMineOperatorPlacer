package org.microcloud.manager.core.schedulerinput;

import java.util.List;
import java.util.Set;

import org.microcloud.manager.core.model.clientquery.ClientQuery;
import org.microcloud.manager.core.model.datacenter.Host;
import org.microcloud.manager.core.placer.solution.SolutionGraphDoneHost;

public interface SourceManager {
	
	boolean useHistoricalDataSource(String dataSourceName, List<Object> keysList);
	
	boolean useRealTimeDataSource(String dataSourceName);
	
	List<SolutionGraphDoneHost> runPlacement( ClientQuery clientQuery );
	
	boolean confirmExecution( int solutionId );
}
