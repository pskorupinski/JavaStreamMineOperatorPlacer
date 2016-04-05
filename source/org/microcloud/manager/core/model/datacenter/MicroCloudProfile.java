package org.microcloud.manager.core.model.datacenter;

import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

@Entity(name="microcloud_profile")
public class MicroCloudProfile {

	@Id
	@GeneratedValue
	@Column(name = "mcp_id")
	private int id;

	@Column(name = "mcp_name")
	private String name;
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
    @JoinColumn(name="mcp_id")
	Set<MicroCloudProfileNode> dataCenterProfileNodes;

	public Set<MicroCloudProfileNode> getDataCenterProfileNodes() {
		return dataCenterProfileNodes;
	}
	public void setDataCenterProfileNodes(Set<MicroCloudProfileNode> dataCenterProfileNodes) {
		this.dataCenterProfileNodes = dataCenterProfileNodes;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getId() {
		return id;
	}
	
}
