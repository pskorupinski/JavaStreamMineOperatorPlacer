package org.microcloud.manager.core.placer.placement.Full.steps;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.microcloud.manager.core.model.datacenter.Host;
import org.microcloud.manager.core.model.datacenter.Rack;
import org.microcloud.manager.core.model.datasourcedef.DataSourceKeysDistribution;
import org.microcloud.manager.core.model.key.Key;
import org.microcloud.manager.manager.core.model.datacenter.DataCenter;
import org.microcloud.manager.manager.core.model.solution.SolutionConnection;
import org.microcloud.manager.manager.core.model.solution.SolutionGraph;
import org.microcloud.manager.manager.core.model.solution.SolutionSource;
import org.microcloud.manager.manager.core.model.solution.destination.GeneralSolutionDestination;
import org.microcloud.manager.structures.UniqueBiMapping;

public class PlacementStepZero implements PlacementStep {

	private Set<DataSourceKeysDistribution> keysHostsMapsSet;
	private UniqueBiMapping<DataCenter, Key> keysCloudsMap;
	private Boolean outcome = false;
	
	private List<SolutionGraph> generalSolutionsList;

////////////////////////////////////////////
//// CONSTRUCTORS
////////////////////////////////////////////	
	
	public PlacementStepZero(Set<DataSourceKeysDistribution> keysHostsMapsSet) {
		this.keysHostsMapsSet = keysHostsMapsSet;
		createKeysCloudsMap();
		this.generalSolutionsList = new ArrayList<>();
	}
	
////////////////////////////////////////////
//// PUBLIC METHODS
////////////////////////////////////////////
	
	public void run() {
		
		for( DataSourceKeysDistribution distr : keysHostsMapsSet ) {
			
			UniqueBiMapping<Host, Key> keysHostsMap = distr.getHostKeysMapping();
			
			int number = keysHostsMap.getRowsNumber();
			
			for(int i=0; i<number; i++) {
				DataCenter dc = keysHostsMap.getRow(i).getRack().getDataCenter();
				Set<Key> keys = keysHostsMap.getColumnsOfRowIndex(i);
				
				keysCloudsMap.addRowToColumnsMapping(dc, keys);
			}
		}
		
		for(int i=0; i<keysCloudsMap.getRowsNumber(); i++) {
			
			Set<Host> dataCenterHosts = new HashSet<>();
			DataCenter dc = keysCloudsMap.getRow(i);
			for(Rack r : dc.getRacks())
				dataCenterHosts.addAll(r.getHosts());
			
			Set<Integer> keysIndices = keysCloudsMap.getColIndexesOfRowIndex(i);
			
			/* That means all keys are present */
			if(keysIndices.size() == keysCloudsMap.getColumnsNumber()) {

				/* Create solution */
				SolutionGraph generalSolution = createSolution(dc, dataCenterHosts);
				
				/* Add to the set */
				generalSolutionsList.add(generalSolution);
			}
			
		}
		
	}
	
	public Object getOutcome() {
		return generalSolutionsList;
	}
	
////////////////////////////////////////////
//// PRIVATE METHODS
////////////////////////////////////////////	
	
	/**
	 * 
	 */
	private void createKeysCloudsMap() {
		keysCloudsMap = new UniqueBiMapping<DataCenter, Key>();
		
		for ( DataSourceKeysDistribution keyHostMap : keysHostsMapsSet ) {
			Set<Key> keys = keyHostMap.getHostKeysMapping().getColumns();
			keysCloudsMap.defineColumns(keys);
		}
	}
	
	/**
	 * 
	 * @param set1
	 * @param set2
	 * @return
	 */
	private <T> Set<T> setsIntersection(Set<T> set1, Set<T> set2) {
		
		Set<T> outcomeSet = new HashSet<>();
		
		for( T t : set1 ) {
			if(set2.contains(t))
				outcomeSet.add(t);
		}
		
		return outcomeSet;
	}
	
	/**
	 * 
	 * @param dataCenterHosts
	 * @return
	 */
	private SolutionGraph createSolution(DataCenter dc, Set<Host> dataCenterHosts) {
		
		/* Create solution sources */
		List<SolutionSource> solutionSources = createSolutionSources(dataCenterHosts);
		
		/* Create solution destinations */
		List<GeneralSolutionDestination> solutionDestinations = createSolutionDestinations(dc);
		
		/* Create solution connections */
		List<SolutionConnection> solutionConnections = createSolutionConnections(solutionSources, solutionDestinations);
		
		/* Create solution */
		SolutionGraph generalSolution = new SolutionGraph(solutionSources, solutionDestinations, solutionConnections);
		
		analyzeAndStandardize(generalSolution);
		
		return generalSolution;
	}

	/**
	 * 
	 * @param dataCenterHosts
	 * @return
	 */
	private List<SolutionSource> createSolutionSources(Set<Host> dataCenterHosts) {
		List<SolutionSource> solutionSources = new ArrayList<>();
		
		for( DataSourceKeysDistribution distr : keysHostsMapsSet ) {
			UniqueBiMapping<Host, Key> keysHostsMap = distr.getHostKeysMapping();
			Set<Host> hostsWithKeys  = keysHostsMap.getRows();
			
			Set<Host> sourceHosts = setsIntersection(dataCenterHosts, hostsWithKeys);
			
			for(Host sourceHost : sourceHosts) {
				Set<Key> keysOfHost = keysHostsMap.getColumnsOfRow(sourceHost);
				
				for(Key key : keysOfHost) {
					SolutionSource solutionSource = new SolutionSource(sourceHost, key);
					solutionSources.add(solutionSource);
				}
			}
		}
		
		return solutionSources;
	}
	
	/**
	 * 
	 * @param dc
	 * @return
	 */
	private List<GeneralSolutionDestination> createSolutionDestinations(DataCenter dc) {
		List<GeneralSolutionDestination> solutionDestinations = new ArrayList<>();
		
		GeneralSolutionDestination solutionDestination = new GeneralSolutionDestination(dc);
		solutionDestinations.add(solutionDestination);
		
		return solutionDestinations;
	}

	/**
	 * 
	 * @param solutionSources
	 * @param solutionDestinations
	 * @return
	 */
	private List<SolutionConnection> createSolutionConnections(List<SolutionSource> solutionSources, List<GeneralSolutionDestination> solutionDestinations) {
		List<SolutionConnection> solutionConnections = new ArrayList<>();
		
		GeneralSolutionDestination dst = solutionDestinations.get(0);
		
		for(SolutionSource src : solutionSources) {
			SolutionConnection conn = new SolutionConnection(src, dst);
			
			solutionConnections.add(conn);
		}
		
		return solutionConnections;
	}
	
	/**
	 * 
	 * @param generalSolution
	 * @return
	 */
	private SolutionGraph analyzeAndStandardize(SolutionGraph generalSolution) {
		
		
		return generalSolution;
	}
	
		
	
}
