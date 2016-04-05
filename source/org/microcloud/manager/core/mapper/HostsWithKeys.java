package org.microcloud.manager.core.mapper;

import java.util.Set;

import org.microcloud.manager.core.model.datacenter.Host;

public class HostsWithKeys {

	final private Set<Host> hosts; 
	private Integer numberOfKeys;
	
	public HostsWithKeys(Set<Host> hosts) {
		this.hosts = hosts;
		this.numberOfKeys = 1;
	}	
	
	public Set<Host> getHosts() {
		return hosts;
	}
	public Integer getNumberOfKeys() {
		return numberOfKeys;
	}
	public void incrementNumberOfKeys() {
		this.numberOfKeys++;
	}
	
	public boolean equals(Object o) {
		return this.hosts.equals(((HostsWithKeys)o).getHosts());
	}
}
