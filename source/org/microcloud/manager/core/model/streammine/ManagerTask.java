package org.microcloud.manager.core.model.streammine;

import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.microcloud.manager.core.model.datacenter.Host;
import org.microcloud.manager.core.model.datacenter.HostBusyTimes;

@Entity(name="manager_task")
public class ManagerTask {
	
	@Id
	@GeneratedValue
	@Column(name = "mt_id")
	private int id;

	@Column(name = "mt_start", columnDefinition = "DATETIME")
	@Temporal(TemporalType.TIMESTAMP)
	private Date startTime;
	
	@Column(name="mt_input")
	@Lob
	private ManagerInput managerInput;
	
	@OneToMany
    @JoinColumn(name="mt_id")
	private Set<HostBusyTimes> hostBusyTimes;
	
//////////////////////////////////////////////////////
// CONSTRUCTORS
//////////////////////////////////////////////////////
	
	public ManagerTask() { }
	
	public ManagerTask(ManagerInput managerInput, Date startTime) {
		this.managerInput = managerInput;
		this.startTime = startTime;
	}
	public ManagerTask(Date startTime) {
		this.startTime = startTime;
	}
	
///////////////////////////////////////////////////////
// GETTERS
///////////////////////////////////////////////////////
	
	public int getId() {
		return this.id;
	}
	
	public ManagerInput getManagerInput() {
		return this.managerInput;
	}
	public void setManagerInput(ManagerInput managerInput) {
		this.managerInput = managerInput;
	}

	public Date getStartTime() {
		return startTime;
	}

}
