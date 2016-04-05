package org.microcloud.manager.core.model.datasource;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;


@Embeddable
public class DataSourceDefinition {
	
	@Column(name = "ds_name")
	private String dataSourceName;
	
	@ManyToOne
    @JoinColumn(name="ds_tech")
	private DataSourceTech dataSourceTech;
	
	public String getDataSourceName() {
		return dataSourceName;
	}
	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}
	public DataSourceTech getDataSourceTech() {
		return dataSourceTech;
	}
	public void setDataSourceTech(DataSourceTech dataSourceTech) {
		this.dataSourceTech = dataSourceTech;
	}
	
	@Override
	public String toString() {
		return dataSourceName;
	}
}
