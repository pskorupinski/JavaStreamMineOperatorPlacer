package org.microcloud.manager.core.model.datacenter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
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

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDefs;
import org.hibernate.annotations.TypeDef;
import org.microcloud.manager.network.RestService;

import com.vividsolutions.jts.geom.Point;


@TypeDefs({
    @TypeDef(name="point", typeClass=org.mc.persistence.mytypes.GeometryUserType.class)
})

@Entity
@Table (name="DataCenter")
public class DataCenterOld {
	
	private int id;
	private InetAddress inetAddress;
    private Point location = null;
    private Integer timeZoneInt = null;
	
    private DataCenterProfile dataCenterProfile;
    private DataCenterAttributes dataCenterAttributes;
    
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
	@Column(name = "dc_id")
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	@Column(name = "dc_host", nullable = false)
	public String getHost() {
		return inetAddress.getCanonicalHostName();
	}
	public void setHost(String host) {
		try {
			this.inetAddress = InetAddress.getByName(host);
		} catch (UnknownHostException e) {
			this.inetAddress = null;
			System.err.println("Host name stored in DB: " + host + " doesn't exist anymore!");
		}
	}
	
	@Column(name="dc_location", columnDefinition="geometry")
	@Type(type="point")
	public Point getLocation() {
		return location;
	}
	public void setLocation(Point location) {
		this.location = location;
		org.microcloud.manager.logger.Logger.getInstance().log();
		org.microcloud.manager.logger.Logger.getInstance().log(this.location);
		org.microcloud.manager.logger.Logger.getInstance().log();
	}

	@ManyToOne
    @JoinColumn(name="dc_profile")
	public DataCenterProfile getDataCenterProfile() {
		return dataCenterProfile;
	}
	public void setDataCenterProfile(DataCenterProfile dataCenterProfile) {
		this.dataCenterProfile = dataCenterProfile;
	}

	@ManyToOne
    @JoinColumn(name="dc_attributes")
	public DataCenterAttributes getDataCenterAttributes() {
		return dataCenterAttributes;
	}
	public void setDataCenterAttributes(DataCenterAttributes dataCenterAttributes) {
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
}
