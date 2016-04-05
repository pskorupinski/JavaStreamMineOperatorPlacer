package org.microcloud.manager.core.placer.tools;

import java.util.HashSet;
import java.util.Set;

import org.microcloud.manager.core.model.datacenter.Host;
import org.microcloud.manager.core.model.datacenter.MicroCloud;
import org.microcloud.manager.core.model.datasource.DataSourceKeysDistribution;
import org.microcloud.manager.core.model.key.Key;
import org.microcloud.manager.core.placer.PlacementProblem;
import org.microcloud.manager.core.placer.solution.GraphConfirmationType;
import org.microcloud.manager.core.placer.solution.SolutionBuilder;
import org.microcloud.manager.core.placer.solution.SolutionGraph;
import org.microcloud.manager.core.placer.solution.SolutionGraphFullMC;
import org.microcloud.manager.persistence.objectsloader.MicroCloudDao;
import org.microcloud.manager.structures.UniqueBiMapping;

public class StructuresBuilder {

	public static SolutionGraphFullMC buildFullGraphForProblem(PlacementProblem placementProblem) {
		
		SolutionBuilder<MicroCloud> solutionBuilder = new SolutionBuilder<>();
		
		/* 1. All key-host pairs to solution sources */		
		for(DataSourceKeysDistribution dskd : placementProblem.getKeysHostsMapsSet()) {	
			UniqueBiMapping<Host, Key> hostsKeysMapping = dskd.getHostKeysMapping();
			int keysNumber = hostsKeysMapping.getColumnsNumber();
			
			for(int i=0; i<keysNumber; i++) {
				Key key = hostsKeysMapping.getColumn(i);
				Set<Host> hostsOfKey = hostsKeysMapping.getRowsOfColIndex(i);
				
				for(Host host : hostsOfKey) {
					/* a new solution source */
					solutionBuilder.createSource(host, key);					
				}
			}
		}
		
		/* 2. All data centers as solution destinations */
		MicroCloudDao dataCenterDao = new MicroCloudDao();
		for(Object o : dataCenterDao.findAll()) {
			MicroCloud dc = (MicroCloud) o;
			/* a new solution destination */
			solutionBuilder.createDestination(dc);
		}
		
		/* 3. Connect all */
		solutionBuilder.connectAll();
		
		return (SolutionGraphFullMC) solutionBuilder.getSolutionGraph(GraphConfirmationType.FULL_DESTDC);
	}
	
}
