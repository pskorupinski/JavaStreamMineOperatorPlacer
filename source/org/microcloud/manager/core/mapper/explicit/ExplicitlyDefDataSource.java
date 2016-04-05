package org.microcloud.manager.core.mapper.explicit;

import java.util.List;
import java.util.Set;

import org.microcloud.manager.core.mapper.RetrievableDataSource;
import org.microcloud.manager.core.model.datacenter.Host;
import org.microcloud.manager.core.model.datasource.DataSource;
import org.microcloud.manager.core.model.key.Key;
import org.microcloud.manager.core.model.key.RealTimeDataKey;
import org.microcloud.manager.structures.UniqueBiMapping;

public class ExplicitlyDefDataSource extends RetrievableDataSource {

	public ExplicitlyDefDataSource(DataSource dataSource) {
		super(dataSource);
	}

	@Override
	public UniqueBiMapping<Host, Key> getKeysHostsMap(List<Object> keysList) {
		Key pseudoColumn = new RealTimeDataKey(dataSource);
		
		UniqueBiMapping<Host,Key> hostsKeysMapping = new UniqueBiMapping<>();
		
		Set<Host> hosts = dataSource.getDataSourceHosts().getHosts();
		
		if(hosts.isEmpty())
			hostsKeysMapping = null;
		else
			hostsKeysMapping.addColumnToRowsMapping(dataSource.getDataSourceHosts().getHosts(), pseudoColumn);

		return hostsKeysMapping;
	}

}
