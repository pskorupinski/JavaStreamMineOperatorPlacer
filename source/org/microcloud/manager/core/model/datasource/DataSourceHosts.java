package org.microcloud.manager.core.model.datasource;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;

import org.microcloud.manager.core.model.datacenter.Host;

@Embeddable
public class DataSourceHosts {
	
    @ManyToMany(
        targetEntity=org.microcloud.manager.core.model.datacenter.Host.class,
        cascade={CascadeType.ALL},
        fetch=FetchType.EAGER
    )
    @JoinTable(
        name="datasource_hosts",
        joinColumns=@JoinColumn(name="ds_id"),
        inverseJoinColumns=@JoinColumn(name="h_id")
    )	
	private Set<Host> hosts;

	public Set<Host> getHosts() {
		return hosts;
	}
	public void setHosts(Set<Host> hosts) {
		this.hosts = hosts;
	}
}
