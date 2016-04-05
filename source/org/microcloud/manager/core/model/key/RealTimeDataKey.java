package org.microcloud.manager.core.model.key;

import org.microcloud.manager.core.model.datasource.DataSource;
import org.microcloud.manager.operations.UnitsConv;

public class RealTimeDataKey extends Key {

	//private long sizePerMinKB;

	public RealTimeDataKey(DataSource dataSource, int timeMin) {
		super(dataSource, countSizeKB(timeMin, dataSource.getSourceBandwidthKbMin()));
		//this.sizePerMinKB = sizePerMinKB;
	}
	
	public RealTimeDataKey(DataSource dataSource) {
		super(dataSource, -1);
	}
	public void setTime(int timeMin) {
		setSizeKB(countSizeKB(timeMin, getDataSource().getSourceBandwidthKbMin()));
	}

	public long getSizePerMinKb() {
		return getDataSource().getSourceBandwidthKbMin();
	}
	
	@Override
	public String toString() {
		return super.toString() + "type: RealTimeDataKey }";
	}
	
	private static long countSizeKB(int timeMin, long sourceBandwidthKbMin) {
		return (long) (timeMin * UnitsConv.bitToByte(sourceBandwidthKbMin));
	}
	
}
