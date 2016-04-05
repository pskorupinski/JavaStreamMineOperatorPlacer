package org.microcloud.manager.core.datainput;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.microcloud.manager.core.model.clientquery.ClientQuery;
import org.microcloud.manager.core.model.datasource.DataSource;
import org.microcloud.manager.core.model.datasource.DataSourceDefinition;
import org.microcloud.manager.core.model.datasource.DataSourceTech;
import org.microcloud.manager.core.model.datasource.DataSourceTechType;
import org.microcloud.manager.core.model.datasource.DataSourceType;
import org.microcloud.manager.core.model.workeralgorithm.WorkerAlgorithmType;
import org.microcloud.manager.core.schedulerinput.ConcreteSourceManager;

public class ConcreteSourceManagerTest {
	
	ConcreteSourceManager concreteSourceManager;

	@Before
	public void setUp() throws Exception {
		concreteSourceManager = new ConcreteSourceManager();
	}

//	@Test
//	public void test() {
//		DataSourceDefinition dataSourceDef;
//		List<Object> keysList;
//		
//		ExemplarDataGenerator.getInstance().generate();
//		
//		dataSourceDef = new DataSourceDefinition();
//		dataSourceDef.setDataSourceName("");
//		dataSourceDef.setDataSourceType(DataSourceType.HISTORICAL);
//		dataSourceDef.setDataSourceTechType(DataSourceTechType.MONGODB_CLUSTER);
//		
//		keysList = new ArrayList<>();
//		keysList.add(new Double(4.0));
//		keysList.add(new Double(40.0));
//		keysList.add(new Double(400.0));
//		keysList.add(new Double(4000.0));
//		keysList.add(new Double(40000.0));
//		
//		concreteSourceManager.addUndifinedResource(dataSourceDef, keysList, null);
//	}
	
//	@Test
//	public void getDSAddressFromNameTest() {
//		
//		String dataSourceName = "mongo";
//		
//		DataSource ds = concreteSourceManager.getDSAddressFromName(dataSourceName);
//		
//		assertEquals("fs", ds.getCollName());
//		assertEquals("localhost", ds.getHostName());
//		
//	}
	
	@Test
	public void adjustKeysTest() {
		
		ClientQuery clientQuery = 
				new ClientQuery(
						300, null, WorkerAlgorithmType.WORD_COUNT);
		
		concreteSourceManager.useRealTimeDataSource("realtime");
		
		concreteSourceManager.adjustKeys(clientQuery);
		
		return;		
	}

}
