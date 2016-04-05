package org.microcloud.manager.core.model.datacenter;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.microcloud.manager.persistence.EntityLoader;

@Entity
@Table (name="Rack")
public class Rack {
	
	private int id;
	private String name;
	
	MicroCloud microCloud;
	
	private Set<Host> hosts;
	
////////////////////////////////////////////////////////
// PUBLIC METHODS
////////////////////////////////////////////////////////
	
	public Set<Host> getFreeHosts(Date startTime, int expectedExecutionTimeS) {
		Date endTime = new Date(startTime.getTime() + expectedExecutionTimeS*1000);
		Set<Integer> hostsIds = new HashSet<>();
		for(Host h : hosts) {
			hostsIds.add(h.getId());
		}
		List<Host> busyHosts = EntityLoader.getInstance().getBusyHostsInTime(hostsIds, startTime, endTime);
		Set<Host> tmp = new HashSet<>(hosts);
		tmp.removeAll(busyHosts);
		
		return tmp;
	}
	
////////////////////////////////////////////////////////
// getters and setters
////////////////////////////////////////////////////////
	
	@Id
	@GeneratedValue
	@Column(name = "r_id")
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}	
	
	@Column(name = "r_name")	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@ManyToOne( cascade = {CascadeType.PERSIST, CascadeType.MERGE}, targetEntity = org.microcloud.manager.core.model.datacenter.MicroCloud.class )
    @JoinColumn(name="mc_id")
	public MicroCloud getMicroCloud() {
		return microCloud;
	}
	public void setMicroCloud(MicroCloud microCloud) {
		this.microCloud = microCloud;
	}
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
    @JoinColumn(name="r_id")
	public Set<Host> getHosts() {
		return hosts;
	}
	public void setHosts(Set<Host> hosts) {
		this.hosts = hosts;
	}
	
////////////////////////////////////////////////////////
//OVERRIDE
////////////////////////////////////////////////////////

	@Override
	public String toString() {
		return "Rack: { id: " + this.id + ", name: " + this.name + ", " + this.microCloud + " }";
	}
	
}
