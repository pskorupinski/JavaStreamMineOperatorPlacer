package org.microcloud.manager.core.model.key;

import org.microcloud.manager.core.model.datasource.DataSource;

public abstract class Key {
	private DataSource dataSource;
	private long sizeKB;
	
	public Key(DataSource dataSource, long sizeKB) {
		this.setDataSource(dataSource);
		this.sizeKB = sizeKB;
	}

	public DataSource getDataSource() {
		return dataSource;
	}
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public long getSizeKB() {
		return sizeKB;
	}
	public void setSizeKB(long sizeKB) {
		this.sizeKB = sizeKB;
	}
	
	@Override
	public boolean equals(Object obj) {
		boolean isEqual = false;
		
		if(this.getClass().equals(obj.getClass())) {
			Key other = (Key) obj;
			
			if(this.dataSource.equals(other.dataSource)) {
				isEqual = true;
			}
		}
		
		return isEqual;
	}
	
	@Override
	public String toString() {
		return "Key: { size: " + this.sizeKB + ", ";
	}
	
}
