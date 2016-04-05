package org.microcloud.manager.core.retriever.mongodb;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.microcloud.manager.core.mapper.mongodb.MongoDbDataSource;
import org.microcloud.manager.core.model.datacenter.Host;
import org.microcloud.manager.core.model.datasource.DataSource;
import org.microcloud.manager.core.model.key.Key;
import org.microcloud.manager.core.model.key.MongoKey;
import org.microcloud.manager.structures.UniqueBiMapping;

public class MongoDbDataSourceTest {
	
	MongoDbDataSource mongoDbDS;

	@Before
	public void setUp() throws Exception {
		DataSource dataSource = new DataSource(null, "localhost", 27017, "filesystem.fs.chunks");
		
		mongoDbDS = new MongoDbDataSource(dataSource);
	}

	@Test
	public void testGetKeysHostsMap() {
		
		List<Object> keysList = new ArrayList<>();
		keysList.add(new String("0eebec843bbd8b441cab6c76893d1d31"));
		
		UniqueBiMapping<Host, Key> keysHostsMap = mongoDbDS.getKeysHostsMap(keysList);
//		for( Map.Entry<Set<Host>, IncrementableInteger> hostsSet : keysHostsMap.entrySet() ) {
//			org.microcloud.manager.logger.Logger.getInstance().log("HOSTS:");
//			for(Host h : hostsSet.getKey()) {
//				org.microcloud.manager.logger.Logger.getInstance().log(" - " + h);
//			}
//			org.microcloud.manager.logger.Logger.getInstance().log("STORE " + hostsSet.getValue().get() + " KEYS.");
//		}
		return;
		
	}

	@Test
	public void testGetChunksMap() {
		mongoDbDS.getChunksMap();
	}

}
