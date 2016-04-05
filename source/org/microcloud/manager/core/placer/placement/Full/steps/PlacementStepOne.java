package org.microcloud.manager.core.placer.placement.Full.steps;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.microcloud.manager.core.model.clientquery.ClientQuery;
import org.microcloud.manager.core.model.datacenter.Host;
import org.microcloud.manager.core.model.datasourcedef.DataSourceKeysDistribution;
import org.microcloud.manager.core.model.datasourcedef.DataSourceType;
import org.microcloud.manager.core.model.key.Key;
import org.microcloud.manager.core.model.workeralgorithm.WorkerAlgorithm;
import org.microcloud.manager.core.model.workeralgorithm.WorkerAlgorithmProfileNode;
import org.microcloud.manager.manager.core.model.datacenter.DataCenter;
import org.microcloud.manager.manager.core.model.solution.SolutionConnection;
import org.microcloud.manager.manager.core.model.solution.SolutionGraph;
import org.microcloud.manager.manager.core.model.solution.SolutionSource;
import org.microcloud.manager.manager.core.model.solution.destination.GeneralSolutionDestination;
import org.microcloud.manager.persistence.EntityLoader;
import org.microcloud.manager.structures.UniqueBiMapping;

public class PlacementStepOne implements PlacementStep {
	
	Set<DataSourceKeysDistribution> keysHostsMapsSet;
	ClientQuery clientQuery;

	public PlacementStepOne(Set<DataSourceKeysDistribution> keysHostsMapsSet, ClientQuery clientQuery) {
		this.keysHostsMapsSet = keysHostsMapsSet;
		this.clientQuery = clientQuery;
	}
	
////////////////////////////////////////////
////PUBLIC METHODS
////////////////////////////////////////////
	
	@Override
	public void run() {
		
		SolutionGraph solutionGraph;
		
		/*
		 * 1. Prepare the full graph based on data received in Set<DataSourceKeysDistribution>.
		 * 	It will contain all possible SolutionSources, SolutionConnections, SolutionDestinations 
		 */
		solutionGraph = runPart1();
		
		/* 
		 * 2. Analyze client query to get max time.
		 * 	This will include a size of all the data per data source type.
		 *  
		 * 	Scenarios:
		 * 	I. historical + time given:
		 * 		-> tmax 			- given by user
		 * 		-> worker slices 	- counted from tmax & data size
		 * 	II. historical + price given:
		 * 		-> worker slices 	- data sizes & guessed few times 
		 * 		-> tmax 			- counted from worker slices no.
		 * 	III. real-time:
		 * 		-> tmax 			- time of execution of real-time
		 * 		-> worker slices 	- counted from tmax & data size (to process data real-time)
		 * 	IV. hybrid:
		 * 		-> tmax				- bounded by real-time execution
		 * 		-> worker slices	- counted from tmax & data size + those needed to process real-time
		 * */
		runPart2();
		
		/*
		 * 3. Copy a graph and delete in a new version those nodes (and their connections) 
		 * 	which will not be available during the time needed. 
		 */
		
		
		/*
		 * 4. Count PRICEi, PRICEj, PRICEi->j, PRICEij, ROUTE_COST c, capacity C
		 */
		
		
		/*
		 * 5. Present the data as an input do simplex algorighm, run it.
		 */
		
		
		/*
		 * 6. In some cases, rerun algorithm
		 */
		

	}

	@Override
	public Object getOutcome() {
		// TODO Auto-generated method stub
		return null;
	}
	
////////////////////////////////////////////
////PRIVATE METHODS
////////////////////////////////////////////

	private SolutionGraph runPart1() {
		
		// solution sources list
		List<SolutionSource> solutionSources = new ArrayList<>();
		
		for( DataSourceKeysDistribution distr : keysHostsMapsSet ) {
			
			UniqueBiMapping<Host, Key> keysHostsMap = distr.getHostKeysMapping();
			
			int numberCols = keysHostsMap.getColumnsNumber();
			
			for(int i=0; i<numberCols; i++) {
				Set<Host> hostsForKey = keysHostsMap.getRowsOfColIndex(i);
				Key key = keysHostsMap.getColumn(i);
				
				for(Host host : hostsForKey) {
					SolutionSource solutionSource = new SolutionSource(host, key);
					solutionSources.add(solutionSource);
				}
			}
		}
		
		// solution destinations list
		List<GeneralSolutionDestination> solutionDestinations = new ArrayList<>();
		
		@SuppressWarnings("unchecked")
		List<DataCenter> dataCentersList = EntityLoader.getInstance().getDataCenters();
		
		for(DataCenter dc : dataCentersList) {
			GeneralSolutionDestination sd = new GeneralSolutionDestination(dc);
			solutionDestinations.add(sd);
		}
		
		// solution connections list
		List<SolutionConnection> solutionConnections = new ArrayList<>();
		
		for(SolutionSource ss : solutionSources) {
			for(GeneralSolutionDestination sd : solutionDestinations) {
				SolutionConnection sc = new SolutionConnection(ss, sd);
				solutionConnections.add(sc);
			}
		}
		
		return new SolutionGraph(solutionSources, solutionDestinations, solutionConnections);		
	}
	
	private void runPart2() {
		
		int aproxTime;
		int workeropSlicesProp;
		
		/*
		 * 2.1 Decide on which kind of scenario do we have
		 */
		
		/* 2.1.1 Check whether are historical and/or real-time */
		boolean historical = false;
		boolean realtime = false;
		
		for(DataSourceKeysDistribution dskd : keysHostsMapsSet) {
			DataSourceType dst = dskd.getDataSource().getDataSourceDefinition().getDataSourceTech().getDataSourceType();
			if(dst == DataSourceType.HISTORICAL) {
				historical = true;
			}
			else if(dst == DataSourceType.REAL_TIME) {
				realtime = true;
			}			
		}

		/* 2.1.2 Check what is defined in client query */
		boolean priceNotTime = false;
		
		if(clientQuery.getPrice() != null)
			priceNotTime = true;
		
		/*
		 * 2.2 MB/s of data retrieved from sources
		 */
		
		int retrievedPerSecond = 0;
		
		for(DataSourceKeysDistribution dskd : keysHostsMapsSet) {
			retrievedPerSecond += dskd.getRetrievalSizePerSecond();
		}
		
		/*
		 * 2.3 Worker algorithm
		 */
		
		WorkerAlgorithm workerAlgorithm = EntityLoader.getInstance().getWorkerAlgorithm(clientQuery.getWorkerAlgorighmType());
		Set<WorkerAlgorithmProfileNode> workerProfileSet = workerAlgorithm.getWorkerAlgorithmProfileNodes();
		
		List<WorkerAlgorithmProfileNode> workerProfileList = new ArrayList<>(workerProfileSet);
		java.util.Collections.sort(workerProfileList);
		
		WorkerAlgorithmProfileNode n0 = null;
		
		for(WorkerAlgorithmProfileNode wapn : workerProfileList) {
			// TODO some parameter so that we set that velocity of workerops will be surely enough
			if( wapn.getVelocity() > retrievedPerSecond /* * costam */ ) {
				n0 = wapn;
			}
		}
		
		if( n0 == null )
			n0 = workerProfileList.get(workerProfileList.size()-1);
		
		/*
		 * 2.4 Various scenarios
		 */
		
		/* 2.4.1 historical + time given */
		if		( historical && !realtime && !priceNotTime ) {
			
		}
		/* 2.4.2 historical + price given */
		else if	( historical && !realtime && priceNotTime ) {
			
		}
		/* 2.4.3 realtime */
		else if	( !historical && realtime ) {
			aproxTime = clientQuery.getTime();
			workeropSlicesProp = n0.getSlicesNo();
			
		}
		/* 2.4.4 hybrid */
		else if	( historical && realtime ) {
			
		}		
		
	}

}
