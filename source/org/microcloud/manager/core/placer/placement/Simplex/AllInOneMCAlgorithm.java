package org.microcloud.manager.core.placer.placement.Simplex;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.microcloud.manager.core.model.datacenter.Host;
import org.microcloud.manager.core.model.datacenter.MicroCloud;
import org.microcloud.manager.core.model.datacenter.Rack;
import org.microcloud.manager.core.model.datasource.DataSourceKeysDistribution;
import org.microcloud.manager.core.model.key.Key;
import org.microcloud.manager.core.placer.solution.GraphConfirmationType;
import org.microcloud.manager.core.placer.solution.SolutionBuilder;
import org.microcloud.manager.core.placer.solution.SolutionBuilderTransfersSetter;
import org.microcloud.manager.core.placer.solution.SolutionGraphFullMC;
import org.microcloud.manager.operations.SetOperations;
import org.microcloud.manager.structures.UniqueBiMapping;

/**
 * 
 * 
 * @author PSkorupinski
 *
 */
public class AllInOneMCAlgorithm {

	private Set<DataSourceKeysDistribution> keysHostsMapsSet;
	private UniqueBiMapping<MicroCloud, Key> keysCloudsMap;
	private Boolean outcome = false;
	
	private List<SolutionGraphFullMC> generalSolutionsList;

////////////////////////////////////////////
//// CONSTRUCTORS
////////////////////////////////////////////	
	
	public AllInOneMCAlgorithm(Set<DataSourceKeysDistribution> keysHostsMapsSet) {
		this.keysHostsMapsSet = keysHostsMapsSet;
		createKeysCloudsMap();
		this.generalSolutionsList = new ArrayList<>();
	}
	
////////////////////////////////////////////
//// PUBLIC METHODS
////////////////////////////////////////////
	
	public void run() {
		
		/* 1. Create a map of keys for every MicroCloud */
		for( DataSourceKeysDistribution distr : keysHostsMapsSet ) {
			
			UniqueBiMapping<Host, Key> keysHostsMap = distr.getHostKeysMapping();
			
			int number = keysHostsMap.getRowsNumber();
			
			for(int i=0; i<number; i++) {
				MicroCloud dc = keysHostsMap.getRow(i).getRack().getMicroCloud();
				Set<Key> keys = keysHostsMap.getColumnsOfRowIndex(i);
				
				keysCloudsMap.addRowToColumnsMapping(dc, keys);
			}
		}
		
		/* 2. */
		for(int i=0; i<keysCloudsMap.getRowsNumber(); i++) {
			
			Set<Host> dataCenterHosts = new HashSet<>();
			MicroCloud dc = keysCloudsMap.getRow(i);
			for(Rack r : dc.getRacks())
				dataCenterHosts.addAll(r.getHosts());
			
			Set<Integer> keysIndices = keysCloudsMap.getColIndexesOfRowIndex(i);
			
			org.microcloud.manager.logger.MyLogger.getInstance().log("Keys <--> MicroClouds mapping");
			org.microcloud.manager.logger.MyLogger.getInstance().log(keysCloudsMap);
			
			/* That means all keys are present */
			if(keysIndices.size() == keysCloudsMap.getColumnsNumber()) {

				/* Create solution */
				SolutionGraphFullMC generalSolution = createSolution(dc, dataCenterHosts);
				
				/* Add to the set */
				generalSolutionsList.add(generalSolution);
			}
			
		}
		
	}
	
	public List<SolutionGraphFullMC> getOutcome() {
		return generalSolutionsList;
	}
	
////////////////////////////////////////////
//// PRIVATE METHODS
////////////////////////////////////////////	
	
	/**
	 * 
	 */
	private void createKeysCloudsMap() {
		keysCloudsMap = new UniqueBiMapping<MicroCloud, Key>();
		
		for ( DataSourceKeysDistribution keyHostMap : keysHostsMapsSet ) {
			List<Key> keys = new ArrayList<Key>(keyHostMap.getHostKeysMapping().getColumns());
			keysCloudsMap.defineColumns(keys);
		}
	}
	
	/**
	 * 
	 * @param dataCenterHosts
	 * @return
	 */
	private SolutionGraphFullMC createSolution(MicroCloud dc, Set<Host> dataCenterHosts) {
		
		SolutionBuilderTransfersSetter solutionBuilder = new SolutionBuilderTransfersSetter();
		
		/* Create solution sources */
		createSolutionSources(solutionBuilder, dataCenterHosts);
		
		/* Create solution destinations */
		solutionBuilder.createDestination(dc);
		
		/* Create solution connections */
		solutionBuilder.connectAll();
		
		/* Set transfers */
		solutionBuilder.setTranfersEqually();
		
		/* Create solution */
		SolutionGraphFullMC generalSolution = (SolutionGraphFullMC) solutionBuilder.getSolutionGraph(GraphConfirmationType.FULL_DESTDC);
		
		return generalSolution;
	}

	/**
	 * 
	 * @param dataCenterHosts
	 * @return
	 */
	private void createSolutionSources(SolutionBuilder<MicroCloud> solutionBuilder, Set<Host> dataCenterHosts) {
		
		for( DataSourceKeysDistribution distr : keysHostsMapsSet ) {
			UniqueBiMapping<Host, Key> keysHostsMap = distr.getHostKeysMapping();
			Set<Host> hostsWithKeys  = keysHostsMap.getRows();
			
			/* Set of hosts in this MC that have any of the keys */
			Set<Host> sourceHosts = SetOperations.intersection(dataCenterHosts, hostsWithKeys);
			
			for(Host sourceHost : sourceHosts) {
				Set<Key> keysOfHost = keysHostsMap.getColumnsOfRow(sourceHost);
				
				for(Key key : keysOfHost) {
					solutionBuilder.createSource(sourceHost, key);
				}
			}
		}
	}
		
}
