package org.microcloud.manager.core.mapper;

import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;

import org.microcloud.manager.core.model.datacenter.Host;
import org.microcloud.manager.core.model.datasource.DataSource;
import org.microcloud.manager.core.model.key.Key;
import org.microcloud.manager.core.model.key.MongoKey;
import org.microcloud.manager.structures.UniqueBiMapping;

import com.mongodb.DB;

public abstract class RetrievableDataSource {

	protected DataSource dataSource;

	public RetrievableDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	/**
	 * 
	 * @param keysList
	 * @return resources grouped by replicated hosts.
	 * 		Set<Host> - set of replicated hosts
	 * 		Integer - how many keys are in this group
	 */
	public abstract UniqueBiMapping<Host, Key> getKeysHostsMap (
			final List<Object> keysList);
}
