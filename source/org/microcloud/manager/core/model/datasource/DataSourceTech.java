package org.microcloud.manager.core.model.datasource;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name = "datasourcetech")
public class DataSourceTech {

	@Id
	@GeneratedValue
	@Column(name = "dst_id")
	private int id;
	
	@Column(name = "dst_techtype")
	@Enumerated(EnumType.STRING)
	private DataSourceTechType dataSourceTechType;
	
	@Column(name = "dst_version")
	private String dataSourceVersion;
	
	@Column(name = "dst_type")
	@Enumerated(EnumType.STRING)
	private DataSourceType dataSourceType;
	
//	/**
//	 * how many MB/s can be retrieved locally from this type of DataSource
//	 * on an average VM 
//	 */
//	@Column(name = "dst_velocity")
//	private int sourceVelocity;
	
	/**
	 * should be used if a source technology changes a bandwidth of data retrieval
	 */
	@Column(name = "dst_bandfactor")
	private double sourceBandwidthFactor = 1.0;
	
	/**
	 * should be used for Real-Time sources
	 */
	@Column(name = "dst_bandwidth")
	private long sourceBandwidthKbMin;
	
	public DataSourceTech() {
		// TODO Auto-generated constructor stub
	}
	
	public DataSourceTech(DataSourceTechType dataSourceTechType,
			String dataSourceVersion,
			DataSourceType dataSourceType) {
		this.dataSourceTechType = dataSourceTechType;
		this.dataSourceType = dataSourceType;
		this.dataSourceVersion = dataSourceVersion;
//		this.sourceVelocity = sourceVelocity;
	}
	
	public DataSourceTech(DataSourceTechType dataSourceTechType,
			String dataSourceVersion,
			DataSourceType dataSourceType,
			double sourceBandwidthFactor) {
		this.dataSourceTechType = dataSourceTechType;
		this.dataSourceType = dataSourceType;
		this.dataSourceVersion = dataSourceVersion;
		this.sourceBandwidthFactor = sourceBandwidthFactor;
	}
	
	public DataSourceTech(DataSourceTechType dataSourceTechType,
			String dataSourceVersion,
			DataSourceType dataSourceType,
			long sourceBandwidthKbMin) {
		this.dataSourceTechType = dataSourceTechType;
		this.dataSourceType = dataSourceType;
		this.dataSourceVersion = dataSourceVersion;
		this.sourceBandwidthKbMin = sourceBandwidthKbMin;
	}

	public DataSourceTechType getDataSourceTechType() {
		return dataSourceTechType;
	}

	public void setDataSourceTechType(DataSourceTechType dataSourceTechType) {
		this.dataSourceTechType = dataSourceTechType;
	}

	public String getDataSourceVersion() {
		return dataSourceVersion;
	}

	public void setDataSourceVersion(String dataSourceVersion) {
		this.dataSourceVersion = dataSourceVersion;
	}

	public DataSourceType getDataSourceType() {
		return dataSourceType;
	}

	public void setDataSourceType(DataSourceType dataSourceType) {
		this.dataSourceType = dataSourceType;
	}

	public double getSourceBandwidthFactor() {
		return sourceBandwidthFactor;
	}

	public void setSourceBandwidthFactor(double sourceBandwidthFactor) {
		this.sourceBandwidthFactor = sourceBandwidthFactor;
	}

	public long getSourceBandwidthKbMin() {
		return sourceBandwidthKbMin;
	}

	public void setSourceBandwidthKbMin(long sourceBandwidthKbMin) {
		this.sourceBandwidthKbMin = sourceBandwidthKbMin;
	}

//	public int getSourceVelocity() {
//		return sourceVelocity;
//	}
//
//	public void setSourceVelocity(int sourceVelocity) {
//		this.sourceVelocity = sourceVelocity;
//	}
	
}
