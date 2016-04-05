package org.microcloud.manager.core.schedulerinput;

import java.awt.Point;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.NotImplementedException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.microcloud.manager.Factory;
import org.microcloud.manager.core.mapper.RetrievableDataSource;
import org.microcloud.manager.core.mapper.explicit.ExplicitlyDefDataSource;
import org.microcloud.manager.core.mapper.mongodb.MongoDbDataSource;
import org.microcloud.manager.core.model.clientquery.ClientQuery;
import org.microcloud.manager.core.model.datacenter.Host;
import org.microcloud.manager.core.model.datacenter.LocPoint;
import org.microcloud.manager.core.model.datacenter.MicroCloud;
import org.microcloud.manager.core.model.datasource.DataSource;
import org.microcloud.manager.core.model.datasource.DataSourceDefinition;
import org.microcloud.manager.core.model.datasource.DataSourceKeysDistribution;
import org.microcloud.manager.core.model.datasource.DataSourceTechType;
import org.microcloud.manager.core.model.datasource.DataSourceType;
import org.microcloud.manager.core.model.key.Key;
import org.microcloud.manager.core.model.key.MongoKey;
import org.microcloud.manager.core.model.key.RealTimeDataKey;
import org.microcloud.manager.core.model.key.UnknownSizeKey;
import org.microcloud.manager.core.placer.placement.PlacementAlgorithm;
import org.microcloud.manager.core.placer.solution.SolutionGraphDoneHost;
import org.microcloud.manager.core.streammine.taskprepare.ConcreteStreamMineTaskPrepare;
import org.microcloud.manager.core.streammine.taskprepare.StreamMineTaskPrepare;
import org.microcloud.manager.persistence.PersistenceFactory;
import org.microcloud.manager.persistence.objectsloader.DataSourceDao;
import org.microcloud.manager.structures.UniqueBiMapping;

public class ConcreteSourceManager implements SourceManager {
	
	private Set<DataSourceKeysDistribution> keysHostsMapsSet = new HashSet<>();
	private ClientQuery clientQuery = null;
	private List<SolutionGraphDoneHost> solutionGraphs = null;
	
////////////////////////////////////////////
//// INTERFACE OVERRIDES
////////////////////////////////////////////	
	
	@Override
	public boolean useHistoricalDataSource(String dataSourceName, List<Object> keysList) {
		
		/* 1. Get data from database about host of system of data source */
		DataSource dataSource = getDSAddressFromName(dataSourceName);
		DataSourceTechType techType = dataSource.getDataSourceDefinition().getDataSourceTech().getDataSourceTechType();
		
		/* 2. Init correct source object */
		RetrievableDataSource retrievableDataSource;
		
		if(techType == DataSourceTechType.MONGODB_CLUSTER) {
			retrievableDataSource = new MongoDbDataSource(dataSource);
		}
		else {
			throw new NotImplementedException("Datasource not yet implemented");
		}
		
		return addDataSource(retrievableDataSource,dataSource,keysList);
	}

	@Override
	public boolean useRealTimeDataSource(String dataSourceName) {
		
		/* 1. Get data from database about hosts of a system of the data source */
		DataSource dataSource = getDSAddressFromName(dataSourceName);		
		
		/* 2. Init correct source object */
		RetrievableDataSource retrievableDataSource = new ExplicitlyDefDataSource(dataSource);
		
		return addDataSource(retrievableDataSource,dataSource,null);
	}

	@Override
	public List<SolutionGraphDoneHost> runPlacement(ClientQuery clientQuery) {
		
		this.clientQuery = clientQuery;
		
		final Set<DataSourceKeysDistribution> readonlyKeysHostsMapsSet = keysHostsMapsSet;
		
		/* 1. Find keys of unknown size and count their size (e.g. how much data will
		 *  be received from WebCrawler in a time of working) */
		adjustKeys(clientQuery);		
		
		/* 2. Run placement algorithm */
		if(readonlyKeysHostsMapsSet.size() > 0) {
			PlacementAlgorithm placementAlgorithm =
					(PlacementAlgorithm) Factory.getInstance().
					newInstance("placer",new Class<?>[]{Set.class,ClientQuery.class},new Object[]{readonlyKeysHostsMapsSet,clientQuery});
			
			placementAlgorithm.runAlgorithm();
			
			solutionGraphs = placementAlgorithm.getSolutionGraphs();
			
			return solutionGraphs;	
		}
		
		return null;
	}

	@Override
	public boolean confirmExecution(int solutionId) {
		
		/* Preconditions */
		if(solutionGraphs == null || solutionGraphs.size() <= solutionId || solutionId < 0)
			return false;
		
		/* Core */
		StreamMineTaskPrepare taskPrepare = new ConcreteStreamMineTaskPrepare();
		boolean isTaskStored = taskPrepare.storeTask(solutionGraphs.get(solutionId), clientQuery);
		
		return isTaskStored;
	}
	
////////////////////////////////////////////
//// PRIVATE METHODS
////////////////////////////////////////////
	
	protected void adjustKeys(ClientQuery clientQuery) {
		
		int retrievingTimeMinutes = clientQuery.getTime();
		
		for(DataSourceKeysDistribution keysDistr : keysHostsMapsSet) {
			if(keysDistr.getDataSource().getDataSourceType() == DataSourceType.REAL_TIME) {
				for(Key key : keysDistr.getHostKeysMapping().getColumns()) {
					((RealTimeDataKey)key).setTime(retrievingTimeMinutes);
				}
			}
		}
		
	}

	protected boolean addDataSource(RetrievableDataSource dataSource, DataSource dataSourceAddress, List<Object> keysList) {
		UniqueBiMapping<Host, Key> hostsKeysMapping = dataSource.getKeysHostsMap(keysList);

		org.microcloud.manager.logger.MyLogger.getInstance().log("Data source " + dataSourceAddress + " added.");
		org.microcloud.manager.logger.MyLogger.getInstance().log(hostsKeysMapping);
		
		if(hostsKeysMapping == null) {
			return false;
		}
		else {			
			DataSourceKeysDistribution dsKeysDist = new DataSourceKeysDistribution(dataSourceAddress, hostsKeysMapping);
			keysHostsMapsSet.add(dsKeysDist);
			return true;
		}
	}
	
	protected DataSource getDSAddressFromName(String dataSourceName) {

		DataSourceDao dataSourceDao = new DataSourceDao();
		DataSource dataSource = dataSourceDao.getByName(dataSourceName);
		
		if(dataSource == null)
			throw new IllegalArgumentException("Given DataSource definition is unknown!");
		
		return dataSource;	
	}

}
