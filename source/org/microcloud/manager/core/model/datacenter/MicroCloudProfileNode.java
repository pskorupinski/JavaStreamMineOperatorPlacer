package org.microcloud.manager.core.model.datacenter;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table (name="microcloud_profile_node")
public class MicroCloudProfileNode {
	
	@Embeddable
	public static class MicroCloudProfilePk extends Object implements Serializable {
		public MicroCloudProfilePk() {};
		private static final long serialVersionUID = 1062833372676492281L;
		@ManyToOne
		@JoinColumn(name = "mcp_id")
		private MicroCloudProfile dataCenterProfile;
		@Column(name = "mcpn_time", columnDefinition = "DATETIME")
		@Temporal(TemporalType.TIMESTAMP)
		private Date time;
	}
	
	@Id
	private MicroCloudProfilePk pk = new MicroCloudProfilePk();
	
	/**
	 * price in $0.001
	 */
	@Column(name = "mcpn_usagePrice")		
	private Integer usagePrice;
	
	/**
	 * price in $0.001
	 */
	@Column(name = "mcpn_inPrice")		
	private Integer inPrice;
	
	/**
	 * price in $0.001
	 */
	@Column(name = "mcpn_outPrice")		
	private Integer outPrice;
	
////////////////////////////////////////////////////////
// getters and setters
////////////////////////////////////////////////////////
	
	public MicroCloudProfilePk getPk() {
		return pk;
	}
	public void setPk(MicroCloudProfilePk pk) {
		this.pk = pk;
	}
	
	public MicroCloudProfile getDataCenterProfile() {
		return pk.dataCenterProfile;
	}
	public void setDataCenterProfile(MicroCloudProfile dataCenterProfile) {
		this.pk.dataCenterProfile = dataCenterProfile;
	}
	
	public Date getTime() {
		return pk.time;
	}
	public void setTime(Date time) {
		this.pk.time = time;
	}

	public Integer getUsagePrice() {
		return usagePrice;
	}
	public void setUsagePrice(Integer usagePrice) {
		this.usagePrice = usagePrice;
	}
	
	public Integer getInPrice() {
		return inPrice;	
	}
	public void setInPrice(Integer inPrice) {
		this.inPrice = inPrice;
	}
	
	public Integer getOutPrice() {
		return outPrice;	
	}
	public void setOutPrice(Integer outPrice) {
		this.outPrice = outPrice;
	}
	
}
