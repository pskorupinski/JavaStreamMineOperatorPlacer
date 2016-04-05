package org.microcloud.manager.core.placer.preplacement.first;

import java.security.InvalidParameterException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.microcloud.manager.Factory;
import org.microcloud.manager.core.model.clientquery.ClientQuery;
import org.microcloud.manager.core.model.datacenter.Host;
import org.microcloud.manager.core.model.datasource.DataSourceKeysDistribution;
import org.microcloud.manager.core.model.datasource.DataSourceType;
import org.microcloud.manager.core.model.key.Key;
import org.microcloud.manager.core.model.key.RealTimeDataKey;
import org.microcloud.manager.core.placer.preplacement.PrePlacement;
import org.microcloud.manager.persistence.EntityLoader;
import org.microcloud.manager.structures.UniqueArrayList;
import org.microcloud.manager.structures.UniqueBiMapping;


/**
 * 
 * This implementation of PrePlacement: <br/>
 * - removes from hosts-to-keys mapping those hosts that are planned to be occupied 
 *   within the next 2 hours from the planned start of an execution of the query
 * 
 * @author PSkorupinski
 *
 */
public class FirstPrePlacement extends PrePlacement {

	public FirstPrePlacement(Set<DataSourceKeysDistribution> keysHostsMapsSet, ClientQuery clientQuery) {
		super(keysHostsMapsSet, clientQuery);
	}

	@Override
	public void run() {
		
		// 1. Get a time when the execution should start
		if(placementProblem.getClientQuery().getStartTime() == null)
			placementProblem.getClientQuery().setStartTime(new Date());
		Date startTime = placementProblem.getClientQuery().getStartTime();
		
		// 2. Get an approximation of how long will the execution last
		int executionPeriodMin = countApproximateExecutionTimeMin(startTime);
		Date endTime = new Date( startTime.getTime() + 1000 * 60 * executionPeriodMin );
		
		org.microcloud.manager.logger.MyLogger.getInstance().log("Execution period: " + executionPeriodMin + " min (" +
				startTime + "-" + endTime + ")");
		
		placementProblem.setExpectedExecutionTimeS(60*executionPeriodMin);
		
		// 3. For placement needs, change real-time keys size by updating their retrieval time
		for(DataSourceKeysDistribution keysDistr : placementProblem.getKeysHostsMapsSet()) {
			if(keysDistr.getDataSource().getDataSourceType() == DataSourceType.REAL_TIME) {
				RealTimeDataKey key = (RealTimeDataKey) keysDistr.getHostKeysMapping().getColumn(0);
				key.setTime(executionPeriodMin);
			}
		}		
		
		// 4. make a list of all hosts ids
		Set<Integer> hostsIds = new HashSet<>();
		
		for(DataSourceKeysDistribution keysDistr : placementProblem.getKeysHostsMapsSet()) {
			UniqueBiMapping<Host, Key> keysHostsMapping = keysDistr.getHostKeysMapping();
			for (Host host : keysHostsMapping.getRows()) {
				hostsIds.add( host.getId() );
			}
		}
		
		// 5. Query for information about all of the busy hosts in the system
		List<Host> occupiedHostsList = EntityLoader.getInstance().getBusyHostsInTime(hostsIds, startTime, endTime);
		
		// 6. Remove those hosts, which, during the approximated execution time, will be occupied 
		for(DataSourceKeysDistribution keysDistr : placementProblem.getKeysHostsMapsSet()) {
			UniqueBiMapping<Host, Key> keysHostsMapping = keysDistr.getHostKeysMapping();
			for (int i=0; i< keysHostsMapping.getRowsNumber(); i++) {
				Host h = keysHostsMapping.getRow(i);
				if(occupiedHostsList.contains(h)) {
					org.microcloud.manager.logger.MyLogger.getInstance().log("Busy host removed --- " + h);
					keysHostsMapping.removeRow(i);
				}
			}
		}		
		
	}
	
	private int countApproximateExecutionTimeMin(Date startTime) {

		int maximumTimeForAnalysis;
		
		if(Factory.getInstance().getBoolean("test")) {
			/* max. 48 h */
			maximumTimeForAnalysis = 1000*60*60*48;
		}
		else {
			/* max. 48 h from now */
			maximumTimeForAnalysis = (int) Math.floor(((new Date().getTime() + 1000*60*60*48) - startTime.getTime()) / 1000.0*60.0);
			if(maximumTimeForAnalysis <= 0)
				throw new InvalidParameterException("Cannot find solution for tasks to be started in more than 48 hours!");
		}
		
		int approximateExecutionTimeMin;
		
		int numberOfKeys = 0;
		long sizeOfKeysKB = 0L;
		
		boolean historicalExist = false;
		
		for(DataSourceKeysDistribution dskd : placementProblem.getKeysHostsMapsSet()) {
			
			if(dskd.getDataSource().getDataSourceType() == DataSourceType.HISTORICAL) {
				historicalExist = true;
				
				UniqueBiMapping<Host, Key> hostKeysMapping = dskd.getHostKeysMapping();
				
				int keysNumber = hostKeysMapping.getColumnsNumber();
				
				numberOfKeys += keysNumber;
				
				for(int i=0; i<keysNumber; i++) {
					Key key = hostKeysMapping.getColumn(i);
					sizeOfKeysKB += key.getSizeKB();
				}
			}
		}
		
		if(historicalExist) {
		
			/* Assuming on average every source will retrieve two keys */
			/* Assuming average disk output bandwidth 100 MB/s */
			long retrievalSpeedOnOneKBMin = 1024 * 100 * 60;
			long retrievalSpeedOnAllKBMin = (long)(Math.ceil(numberOfKeys/2.0)) * retrievalSpeedOnOneKBMin;
			
			/* No more than 2 days should be taken into considerations */
			approximateExecutionTimeMin = 
					Math.min( 4*(int)(sizeOfKeysKB/retrievalSpeedOnAllKBMin) , maximumTimeForAnalysis ) + 1;
		
		}
		else {
			approximateExecutionTimeMin = 
					Math.min( placementProblem.getClientQuery().getTime() , maximumTimeForAnalysis );
		}
		
		return approximateExecutionTimeMin;
		
	}

}
