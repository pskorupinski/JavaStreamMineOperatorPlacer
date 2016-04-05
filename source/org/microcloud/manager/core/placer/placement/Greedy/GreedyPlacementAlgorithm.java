package org.microcloud.manager.core.placer.placement.Greedy;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.microcloud.manager.Factory;
import org.microcloud.manager.core.model.clientquery.ClientQuery;
import org.microcloud.manager.core.model.datacenter.Host;
import org.microcloud.manager.core.model.datacenter.MicroCloud;
import org.microcloud.manager.core.model.datasource.DataSourceKeysDistribution;
import org.microcloud.manager.core.model.datasource.DataSourceType;
import org.microcloud.manager.core.model.key.Key;
import org.microcloud.manager.core.placer.PlacementProblem;
import org.microcloud.manager.core.placer.placement.PlacementAlgorithm;
import org.microcloud.manager.core.placer.placement.Greedy.varia.PickFromTheSet;
import org.microcloud.manager.core.placer.solution.GraphConfirmationType;
import org.microcloud.manager.core.placer.solution.SolutionBuilder;
import org.microcloud.manager.core.placer.solution.SolutionGraphDoneHost;
import org.microcloud.manager.structures.UniqueBiMapping;

/**
 * This is a very basic placement approach. <br/>
 * <br/>
 * Its characteristics are: <br/>
 * <br/>
 * 1. Always will only one data source for every key be chosen <br/>
 * <br/>
 * 2. Workerop slices will be put on the same nodes as Accessops <br/>
 * <br/>
 * Algorithm is cost-oblivious. <br/>
 * <br/>
 * The only strategy is that the algorithm tries to put operators in a low number of data-centers but on various hosts
 * (to avoid too long reading from one disk). Consequently, it is somehow time-aware.
 * 
 * @author PSkorupinski
 *
 */
public class GreedyPlacementAlgorithm extends PlacementAlgorithm {
	
////////////////////////////////////////////
////PRIVATE VARIABLES
////////////////////////////////////////////
	
	private Set<MicroCloud> pickedDataCenters = new HashSet<>();
	private PickFromTheSet pickFromTheSet;
	
////////////////////////////////////////////
////CONSTRUCTORS
////////////////////////////////////////////

	public GreedyPlacementAlgorithm(Set<DataSourceKeysDistribution> keysHostsMapsSet, ClientQuery clientQuery) {
		super(keysHostsMapsSet, clientQuery);
	
	}
	
////////////////////////////////////////////
////PUBLIC METHODS
////////////////////////////////////////////

	@Override
	protected void doRunAlgorithm() {
		
		Class<?>[] paramTypes = new Class<?>[]{PlacementProblem.class};
		Object[] params = new Object[]{placementProblem};
		pickFromTheSet = (PickFromTheSet) Factory.getInstance().newInstance("pickFromTheSet", paramTypes, params);
		
		SolutionBuilder<Host> solutionBuilder = new SolutionBuilder<>();
		
		/* 1. look for real-time sources */
		for( DataSourceKeysDistribution keysHostsMap : placementProblem.getKeysHostsMapsSet() ) {
			if( keysHostsMap.getDataSource().getDataSourceType() == DataSourceType.REAL_TIME ) {
				pickHosts(keysHostsMap, solutionBuilder);
			}
		}
		/* 2. look for historical sources */
		for( DataSourceKeysDistribution keysHostsMap : placementProblem.getKeysHostsMapsSet() ) {
			if( keysHostsMap.getDataSource().getDataSourceType() == DataSourceType.HISTORICAL ) {
				pickHosts(keysHostsMap, solutionBuilder);
			}
		}
		
		solutionBuilder.connectAll();
		
		solutionGraphs.add((SolutionGraphDoneHost) solutionBuilder.getSolutionGraph(GraphConfirmationType.DONE_DESTHOST));
	}
	
//////////////////////////////////////////////////
////PRIVATE METHODS
//////////////////////////////////////////////////
	
	private SolutionBuilder<Host> pickHosts(DataSourceKeysDistribution keysDistribution, SolutionBuilder<Host> solutionBuilder) {
		
		UniqueBiMapping<Host, Key> hostsKeysMapping = keysDistribution.getHostKeysMapping();
		int keysNumber = hostsKeysMapping.getColumnsNumber();
		
		for(int i=0; i<keysNumber; i++) {
			
			Key key = hostsKeysMapping.getColumn(i);
			
			Set<Host> hostsOfKeyInPickedDCs = new HashSet<>();
			Set<Host> freeHostsOfKey = new HashSet<>();
			Set<Host> hostsOfKey = hostsKeysMapping.getRowsOfColIndex(i);
			
			Object[] domainOfHosts;
			
			/* Check which hosts that store data of this key
			 * are in one of micro-clouds of already picked hosts */
			for(Host host : hostsOfKey) {
				if( ! solutionBuilder.sourceHostExists(host) ) {
					freeHostsOfKey.add(host);
					if(pickedDataCenters.contains(host.getRack().getMicroCloud())) {
						hostsOfKeyInPickedDCs.add(host);
					}
				}
			}
			
			/* If there are some, choose from hosts that are in micro-cloud with already picked hosts,
			 * if not choose from all */
			if(hostsOfKeyInPickedDCs.size() > 0)
				domainOfHosts = hostsOfKeyInPickedDCs.toArray();
			else if(freeHostsOfKey.size() > 0)
				domainOfHosts = freeHostsOfKey.toArray();				
			else
				domainOfHosts = hostsOfKey.toArray();
			
			/* Pick the random from a set */
			Host chosenHost = pickFromTheSet.pickFromTheSet(domainOfHosts);
			
			pickedDataCenters.add( chosenHost.getRack().getMicroCloud()	);
			
			org.microcloud.manager.logger.MyLogger.getInstance().log("Adding host as source / destination --- " + chosenHost + 
					"(key ---" + key + ")");
			
			solutionBuilder.createSource(chosenHost, key);
			solutionBuilder.createDestination(chosenHost);
		}
		
		return solutionBuilder;
	}

}
