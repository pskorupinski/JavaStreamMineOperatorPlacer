package org.microcloud.manager.core.model.datasource;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name = "datasource")
public class DataSource {

	@Id
	@GeneratedValue
	@Column(name = "ds_id")
	private int id;
	
	@Embedded
	private DataSourceDefinition dataSourceDefinition;
	
	@Embedded
	private DataSourceHosts dataSourceHosts;

	@Column(name = "ds_host")
	private String hostName;

	@Column(name = "ds_port")
	private int port;
	
	@Column(name = "ds_collname")
	private String collName;
	
	// TODO: can we expect that on all the hosts in all microclouds the same port for every service
	// 			(if not, table between datacenter and datasource will have to be created)
	@Column(name = "ds_portonhosts")
	private int portOnHosts;
	
	public DataSource( DataSourceDefinition def, String hostName, int port, String collName ) {
		this.setDataSourceDefinition(def);
		this.setHostName(hostName);
		this.setPort(port);
		this.setCollName(collName);
	}
	
	public DataSource( DataSourceDefinition def, DataSourceHosts hosts) {
		this.setDataSourceDefinition(def);
		this.dataSourceHosts = hosts;
		this.hostName = null;
		this.port = -1;
		this.collName = null;
	}

	public DataSource() {
		
	}

	public String getHostName() {
		return hostName;
	}
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}

	public String getCollName() {
		return collName;
	}
	public void setCollName(String collName) {
		this.collName = collName;
	}

	public int getPortOnHosts() {
		return portOnHosts;
	}
	public void setPortOnHosts(int portOnHosts) {
		this.portOnHosts = portOnHosts;
	}
	
	public DataSourceHosts getDataSourceHosts() {
		return dataSourceHosts;
	}
	public void setDataSourceHosts(DataSourceHosts dataSourceHosts) {
		this.dataSourceHosts = dataSourceHosts;
	}

	public DataSourceDefinition getDataSourceDefinition() {
		return dataSourceDefinition;
	}

	public void setDataSourceDefinition(DataSourceDefinition dataSourceDefinition) {
		this.dataSourceDefinition = dataSourceDefinition;
	}
	
////////////////////////////////////////////////////////////////////////
// GETTERS FROM NESTED OBJECTS
////////////////////////////////////////////////////////////////////////
	
	public DataSourceType getDataSourceType() {
		return getDataSourceDefinition().getDataSourceTech().getDataSourceType();
	}
	public DataSourceTechType getDataSourceTechType() {
		return getDataSourceDefinition().getDataSourceTech().getDataSourceTechType();
	}
	public long getSourceBandwidthKbMin() {
		return getDataSourceDefinition().getDataSourceTech().getSourceBandwidthKbMin();
	}
	
	
	@Override
	public String toString() {
		return dataSourceDefinition.toString();
	}
}
