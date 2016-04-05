package org.microcloud.manager.core.model.datacenter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table (name="microcloud_attributes")	
public class MicroCloudAttributes {
	
	private Integer inputBandwidthMBitInt;
	private Integer outputBandwidthMBitInt;
	private int id;
	private double connectionBandInside;

	
	////////////////////////////////////////////////////////
	// getters and setters
	////////////////////////////////////////////////////////

	@Id
	@GeneratedValue
	@Column(name = "mca_id")
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "mca_inputbandwidth_mbit")
	public Integer getInputBandwidthMBitInt() {
		return inputBandwidthMBitInt;
	}
	public void setInputBandwidthMBitInt(Integer inputBandwidthMBitInt) {
		this.inputBandwidthMBitInt = inputBandwidthMBitInt;
	}

	@Column(name = "mca_outputbandwidth_mbit")
	public Integer getOutputBandwidthMBitInt() {
		return outputBandwidthMBitInt;
	}
	public void setOutputBandwidthMBitInt(Integer outputBandwidthMBitInt) {
		this.outputBandwidthMBitInt = outputBandwidthMBitInt;
	}
	
	@Column(name = "mca_connband", nullable = false)
	public double getConnectionBandInside() {
		return connectionBandInside;
	}
	public void setConnectionBandInside(double connectionBandInside) {
		this.connectionBandInside = connectionBandInside;
	}
}
