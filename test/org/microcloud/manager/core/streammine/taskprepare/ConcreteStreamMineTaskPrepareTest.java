package org.microcloud.manager.core.streammine.taskprepare;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.microcloud.manager.core.model.clientquery.ClientQuery;
import org.microcloud.manager.core.model.datacenter.Host;
import org.microcloud.manager.core.model.datasource.DataSource;
import org.microcloud.manager.core.model.datasource.DataSourceDefinition;
import org.microcloud.manager.core.model.datasource.DataSourceTech;
import org.microcloud.manager.core.model.datasource.DataSourceTechType;
import org.microcloud.manager.core.model.datasource.DataSourceType;
import org.microcloud.manager.core.model.key.MongoKey;
import org.microcloud.manager.core.model.key.RealTimeDataKey;
import org.microcloud.manager.core.model.workeralgorithm.WorkerAlgorithmType;
import org.microcloud.manager.core.placer.solution.GraphConfirmationType;
import org.microcloud.manager.core.placer.solution.SolutionBuilder;
import org.microcloud.manager.core.placer.solution.SolutionBuilderTransfersSetter;
import org.microcloud.manager.core.placer.solution.SolutionGraphDoneHost;
import org.microcloud.manager.persistence.objectsloader.DataSourceDao;
import org.microcloud.manager.persistence.objectsloader.HostDao;

public class ConcreteStreamMineTaskPrepareTest {
	
	ConcreteStreamMineTaskPrepare concreteStreamMineTaskPrepare;

	@Before
	public void setUp() throws Exception {
		concreteStreamMineTaskPrepare = new ConcreteStreamMineTaskPrepare();
		

		org.microcloud.manager.logger.MyLogger.newInstance("junittest");

	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void storeTaskTest() {		
		SolutionGraphDoneHost solutionGraph;
		ClientQuery clientQuery;
		
		SolutionBuilder<Host> solutionBuilder = new SolutionBuilder<>();
		Host node1 = new HostDao().find(19);
		Host node2 = new HostDao().find(20);
		DataSource dataSource = new DataSourceDao().find(1);
		MongoKey mongoKey = new MongoKey(dataSource, 
				"72425f1c3df63e7bbc3a802458aeaf8a", 
				3, 3, 
				65536);
		solutionBuilder.createSource(node1, mongoKey);
		solutionBuilder.createDestination(node1);
		solutionBuilder.createDestination(node2);
		solutionBuilder.connectAll();
		solutionGraph = (SolutionGraphDoneHost) solutionBuilder.getSolutionGraph(GraphConfirmationType.DONE_DESTHOST);
		
		clientQuery = new ClientQuery(60, null, WorkerAlgorithmType.WORD_COUNT);
		clientQuery.setStartTime(new Date());
		
		concreteStreamMineTaskPrepare.storeTask(solutionGraph, clientQuery);
	}
	
	@Test
	public void storeTaskTest2() {		
		SolutionGraphDoneHost solutionGraph;
		ClientQuery clientQuery;
		
		SolutionBuilder<Host> solutionBuilder = new SolutionBuilder<>();
		Host node1 = new HostDao().find(3);
		Host node2 = new HostDao().find(2);
		DataSource dataSource = new DataSourceDao().find(2);
		RealTimeDataKey rtKey = new RealTimeDataKey(dataSource, 1);
		solutionBuilder.createSource(node1, rtKey);
		solutionBuilder.createDestination(node2);
		solutionBuilder.connectAll();
		solutionGraph = (SolutionGraphDoneHost) solutionBuilder.getSolutionGraph(GraphConfirmationType.DONE_DESTHOST);
		
		clientQuery = new ClientQuery(1, null, WorkerAlgorithmType.WORD_COUNT);
		clientQuery.setStartTime(new Date());
		
		concreteStreamMineTaskPrepare.storeTask(solutionGraph, clientQuery);
	}	

}
