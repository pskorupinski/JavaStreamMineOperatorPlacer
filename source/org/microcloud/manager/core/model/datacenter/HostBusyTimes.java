package org.microcloud.manager.core.model.datacenter;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.microcloud.manager.core.model.streammine.ManagerTask;

@Entity
@Table (name="hostbusytimes")
public class HostBusyTimes {

	@Id
	@GeneratedValue
	@Column(name = "hbt_id")
	private int id;
	
	@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, targetEntity = org.microcloud.manager.core.model.streammine.ManagerTask.class)
	@JoinColumn(name="mt_id")	
	private ManagerTask managerTask;
	
	@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, targetEntity = org.microcloud.manager.core.model.datacenter.Host.class)
	@JoinColumn(name = "h_id")
	private Host host;	
	
	@Column(name = "hbt_starttime", columnDefinition = "DATETIME")
	@Temporal(TemporalType.TIMESTAMP)
	private Date expStartTime;
	
	@Column(name = "hbt_endtime", columnDefinition = "DATETIME")
	@Temporal(TemporalType.TIMESTAMP)
	private Date expEndTime;

	
////////////////////////////////////////////////////////
//getters and setters
////////////////////////////////////////////////////////

	public int getId() {
		return this.id;
	}
	
	public Host getHost() {
		return host;
	}
	public void setHost(Host host) {
		this.host = host;
	}

	public Date getExpStartTime() {
		return expStartTime;
	}
	public void setExpStartTime(Date expStartTime) {
		this.expStartTime = expStartTime;
	}

	public Date getExpEndTime() {
		return expEndTime;
	}
	public void setExpEndTime(Date expEndTime) {
		this.expEndTime = expEndTime;
	}
	
	public ManagerTask getManagerTask() {
		return managerTask;
	}
	public void setManagerTask(ManagerTask managerTask) {
		this.managerTask = managerTask;
	}
	
}
