package org.microcloud.manager.core.model.datacenter;

import java.net.InetAddress;
import java.net.UnknownHostException;
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

@Entity
@Table (name="Host")
public class Host {
	
	private int id;
	private InetAddress inetAddress;
	private String hostName;
	private double computationPowerFactor = 1.0;
	private double diskReadSpeed;

	private Rack rack;
	private Set<HostBusyTimes> hostBusyTimes;

////////////////////////////////////////////////////////
// constructors
////////////////////////////////////////////////////////
	
	public Host() {
		
	}
	
//	public Host(InetAddress inetAddress, Integer portInt) {
//		this.inetAddress = inetAddress;
//		
//		findRack();
//	}
	
	public Host(String hostPortString) {
		this.hostName = hostPortString;
		
//		String [] hostPortArray = hostPortString.split(":");
//		
//		try {
//			this.inetAddress = InetAddress.getByName(hostPortArray[0]);
//		} catch (UnknownHostException | NumberFormatException e) {
//			throw new IllegalArgumentException("Argument string should contain a valid host name and a valid port number given as host:port\n" +
//					"Given string was" +  hostPortString);
//		}
//		
//		findRack();
	}

////////////////////////////////////////////////////////
// getters and setters
////////////////////////////////////////////////////////
	
	@Id
	@GeneratedValue
	@Column(name = "h_id")
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	@Column(name = "h_name", nullable = false)
	public String getName() {
		return hostName;
	}
	public void setName(String host) {
		this.hostName = host;
	}
	
//	public String getName() {
//		return inetAddress.getCanonicalHostName();
//	}
//	public void setName(String host) {
//		try {
//			this.inetAddress = InetAddress.getByName(host);
//		} catch (UnknownHostException e) {
//			this.inetAddress = null;
//			System.err.println("Host name stored in DB: " + host + " doesn't exist anymore!");
//		}
//	}

	@Column(name = "h_compfactor", nullable = false)
	public double getComputationPowerFactor() {
		return computationPowerFactor;
	}
	public void setComputationPowerFactor(double computationPowerFactor) {
		this.computationPowerFactor = computationPowerFactor;
	}
	
	@Column(name = "h_diskreadspeed")
	public double getDiskReadSpeed() {
		return diskReadSpeed;
	}
	public void setDiskReadSpeed(double diskReadSpeed) {
		this.diskReadSpeed = diskReadSpeed;
	}
	
	@ManyToOne( cascade = {CascadeType.PERSIST, CascadeType.MERGE}, targetEntity = org.microcloud.manager.core.model.datacenter.Rack.class )
    @JoinColumn(name="r_id")
	public Rack getRack() {
		return rack;
	}
	public void setRack(Rack rack) {
		this.rack = rack;
	}
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
    @JoinColumn(name="h_id")	
	public Set<HostBusyTimes> getHostBusyTimes() {
		return hostBusyTimes;
	}
	public void setHostBusyTimes(Set<HostBusyTimes> hostBusyTimes) {
		this.hostBusyTimes = hostBusyTimes;
	}
	
/////////////////////////////////////////////////////////////////
// Object overrides
/////////////////////////////////////////////////////////////////

	@Override
	public boolean equals(Object o) {
		if(! (o instanceof Host))
			return false;
		
		Host h = (Host) o;
		if(this.hostName.equals(h.hostName) /*&& getPortInt().equals(h.getPortInt())*/)
			return true;
		else
			return false;		
	}
	
	@Override
	public String toString() {
		return "Host: { id: " + this.id + ", name: " + this.hostName + ", " + this.rack + " }";
	}

}
