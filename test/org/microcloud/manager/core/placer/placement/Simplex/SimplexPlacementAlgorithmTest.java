package org.microcloud.manager.core.placer.placement.Simplex;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.HashSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.microcloud.manager.core.model.clientquery.ClientQuery;
import org.microcloud.manager.core.model.datasource.DataSourceKeysDistribution;
import org.microcloud.manager.core.model.workeralgorithm.WorkerAlgorithmType;
import org.microcloud.manager.core.placer.placement.Greedy.GreedyPlacementAlgorithm;
import org.microcloud.manager.core.placer.placement.Simplex.SimplexPlacementAlgorithm;
import org.microcloud.manager.datagenerator.FakeData;

public class SimplexPlacementAlgorithmTest {

	SimplexPlacementAlgorithm algorithm;

	@Before
	public void setUp() throws Exception {
		Date startTime = new Date(new Date().getTime() + 1000*60*60);
		
		HashSet<DataSourceKeysDistribution> keysHostsMapsSet = FakeData.getFakeDataSourceKeysDistribution1();
		ClientQuery clientQuery = new ClientQuery(0,0,WorkerAlgorithmType.WORD_COUNT,startTime);
		
		algorithm = new SimplexPlacementAlgorithm(keysHostsMapsSet, clientQuery);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		
		if( algorithm.runAlgorithm() ) {
			
			algorithm.getSolutionGraphs();
			
		}
		
	}

}
