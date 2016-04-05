package org.microcloud.generator.datagenerator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.hibernate.mapping.Set;
import org.microcloud.manager.core.model.datacenter.Host;
import org.microcloud.manager.core.model.datasource.DataSource;
import org.microcloud.manager.core.model.datasource.DataSourceKeysDistribution;
import org.microcloud.manager.core.model.key.Key;
import org.microcloud.manager.core.model.key.MongoKey;
import org.microcloud.manager.persistence.objectsloader.DataSourceDao;
import org.microcloud.manager.persistence.objectsloader.HostDao;
import org.microcloud.manager.structures.UniqueBiMapping;

public class FakeData {

	/**
	 * Mapping for MongoDB, 3 keys.
	 * 
	 * Key1 - Hosts: 1,2,4
	 * Key2 - Hosts: 1,3,5
	 * Key3 - Hosts: 2,5,6
	 * 
	 * @return
	 */
	public static HashSet<DataSourceKeysDistribution> getFakeDataSourceKeysDistribution1() {
		HostDao hostDao = new HostDao();
		List<Host> hostList = hostDao.findAll();
		
		DataSourceDao dataSourceDao = new DataSourceDao();
		DataSource dataSource = dataSourceDao.find(1);
		int chunkSizeKB = 256;
		String key = "key";
		
		Key [] mongoKeyArray = new MongoKey[3];
		for(int i=0; i<mongoKeyArray.length; i++) {
			mongoKeyArray[i] = new MongoKey(dataSource, key+i, 0, 10*(i+1), chunkSizeKB);
		}

		UniqueBiMapping<Host, Key> mapping = new UniqueBiMapping<>(hostList, Arrays.asList(mongoKeyArray));
		
		mapping.addColumnToRowMapping(hostList.get(0), mongoKeyArray[0]);
		mapping.addColumnToRowMapping(hostList.get(1), mongoKeyArray[0]);
		mapping.addColumnToRowMapping(hostList.get(3), mongoKeyArray[0]);
		
		mapping.addColumnToRowMapping(hostList.get(0), mongoKeyArray[1]);
		mapping.addColumnToRowMapping(hostList.get(2), mongoKeyArray[1]);
		mapping.addColumnToRowMapping(hostList.get(4), mongoKeyArray[1]);

		mapping.addColumnToRowMapping(hostList.get(1), mongoKeyArray[2]);
		mapping.addColumnToRowMapping(hostList.get(4), mongoKeyArray[2]);
		mapping.addColumnToRowMapping(hostList.get(5), mongoKeyArray[2]);
		
		DataSourceKeysDistribution dskd = new DataSourceKeysDistribution(dataSource, mapping);
		
		HashSet<DataSourceKeysDistribution> set = new HashSet<>();
		set.add(dskd);
		
		return set;
	}
	
}
