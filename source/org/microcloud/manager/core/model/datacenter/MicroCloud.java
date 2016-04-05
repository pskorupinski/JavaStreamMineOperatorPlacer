package org.microcloud.manager.core.model.datacenter;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDefs;
import org.hibernate.annotations.TypeDef;
import org.microcloud.manager.network.RestService;


@Entity
@Table (name="microcloud")
public class MicroCloud {
	
	private int id;
	private String name;
	private InetAddress inetAddress;
	private String host;
    private LocPoint location = null;
    private Integer timeZoneInt = null;
	
    private MicroCloudProfile dataCenterProfile;
    private MicroCloudAttributes dataCenterAttributes;
    
    private Set<Rack> racks;
    
    private void countTimeZoneFromLocation() {
    	String url = "http://www.earthtools.org/timezone/" + location.getX() + "/" + location.getY();
    	RestService rs = new RestService();
    	String xmlString = null;
    	
    	final int reconnect = 5;
    	final int backoffMs = 200;
    	int connectionNo = 0;
    	boolean connected = false;
    	
    	do {
	    	try {
				xmlString = rs.getStringFromAddress(url);
				connected = true;
			} catch (IOException e) {
				System.err.println("Connection to " + url + " failed. Try " + connectionNo+1);
				e.printStackTrace();
			}
	    	
	    	if(!connected) {
	    		connectionNo++;
	    		
				try {
					Thread.sleep(backoffMs);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	}
	    	
    	} while (!connected && connectionNo < reconnect);
		
    	if(xmlString != null) {
    		
    		int beginIndex = xmlString.indexOf("<offset>") + "<offset>".length();
    		int endIndex = xmlString.indexOf("</offset>");
    		
    		String timeZoneString = xmlString.substring(beginIndex, endIndex);
    		timeZoneInt = Integer.parseInt(timeZoneString);
    	}
    	
    }
	
	////////////////////////////////////////////////////////
	// getters and setters
	////////////////////////////////////////////////////////

	@Id
	@GeneratedValue
	@Column(name = "mc_id")
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "mc_name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "mc_host", nullable = false)
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
//	public String getHost() {
//		return inetAddress.getCanonicalHostName();
//	}
//	public void setHost(String host) {
//		try {
//			this.inetAddress = InetAddress.getByName(host);
//		} catch (UnknownHostException e) {
//			this.inetAddress = null;
//			System.err.println("Host name stored in DB: " + host + " doesn't exist anymore!");
//		}
//	}
	
	@Embedded//(name="dc_location", columnDefinition="geometry")
	public LocPoint getLocation() {
		return location;
	}
	public void setLocation(LocPoint location) {
		this.location = location;
	}

	@ManyToOne( cascade = {CascadeType.ALL} )
    @JoinColumn(name="mc_profile")
	public MicroCloudProfile getDataCenterProfile() {
		return dataCenterProfile;
	}
	public void setDataCenterProfile(MicroCloudProfile dataCenterProfile) {
		this.dataCenterProfile = dataCenterProfile;
	}

	@ManyToOne( cascade = {CascadeType.ALL} )
    @JoinColumn(name="mc_attributes")
	public MicroCloudAttributes getDataCenterAttributes() {
		return dataCenterAttributes;
	}
	public void setDataCenterAttributes(MicroCloudAttributes dataCenterAttributes) {
		this.dataCenterAttributes = dataCenterAttributes;
	}
	
	@Transient
	public Integer getTimeZoneInt() {
		if(location == null)
			return null;

		if(timeZoneInt == null)
			countTimeZoneFromLocation();
		
		return timeZoneInt;
	}

	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
    @JoinColumn(name="mc_id")
	public Set<Rack> getRacks() {
		return racks;
	}
	public void setRacks(Set<Rack> racks) {
		this.racks = racks;
	}



////////////////////////////////////////////////////////
// OVERRIDE
////////////////////////////////////////////////////////
	
	@Override
	public boolean equals(Object obj) {
		if(super.equals(obj))
			return true;
		
		if(obj instanceof MicroCloud) {
			MicroCloud other = (MicroCloud) obj;
			if(other.name.equals(this.name))
				return true;
			else
				return false;
		}
		else
			return false;
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	@Override
	public String toString() {
		return "DC: { id: " + this.id + ", name: " + this.name + " }";
	}
	
}
