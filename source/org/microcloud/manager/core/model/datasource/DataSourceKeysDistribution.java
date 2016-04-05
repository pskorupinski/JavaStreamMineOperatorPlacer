package org.microcloud.manager.core.model.datasource;

import org.microcloud.manager.core.model.datacenter.Host;
import org.microcloud.manager.core.model.key.Key;
import org.microcloud.manager.core.model.key.MongoKey;
import org.microcloud.manager.structures.UniqueBiMapping;


public class DataSourceKeysDistribution {
	private DataSource dataSourceAddress;
	private UniqueBiMapping<Host, Key> hostKeysMapping;

////////////////////////////////////////////////////////////////////////
// CONSTRUCTORS
////////////////////////////////////////////////////////////////////////
	
	public DataSourceKeysDistribution(DataSource dataSourceAddress, UniqueBiMapping<Host, Key> hostKeysMapping) {
		this.dataSourceAddress = dataSourceAddress;
		this.hostKeysMapping = hostKeysMapping;
	}
	
////////////////////////////////////////////////////////////////////////
// PUBLIC METHODS
////////////////////////////////////////////////////////////////////////
	
	
//	/**
//	 * @return how many MB/s will be produced by 
//	 */
//	public int getRetrievalSizePerSecond() {
//		
//		int oneSrcPerSecond = dataSourceAddress.getDataSourceDefinition().getDataSourceTech().getSourceVelocity();
//		int srcNumber = hostKeysMapping.getColumnsNumber();
//		
//		return oneSrcPerSecond * srcNumber;
//	}
	
////////////////////////////////////////////////////////////////////////
// GETTERS AND SETTERS
////////////////////////////////////////////////////////////////////////
	
	public DataSource getDataSource() {
		return dataSourceAddress;
	}
	public void setDataSource(DataSource dataSource) {
		this.dataSourceAddress = dataSource;
	}
	public UniqueBiMapping<Host, Key> getHostKeysMapping() {
		return hostKeysMapping;
	}
	public void setHostKeysMapping(UniqueBiMapping<Host, Key> hostKeysMapping) {
		this.hostKeysMapping = hostKeysMapping;
	}
	

}
