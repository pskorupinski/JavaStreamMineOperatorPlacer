package org.microcloud.manager.core.placer.solution;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.microcloud.manager.core.model.datacenter.Host;
import org.microcloud.manager.core.model.datacenter.MicroCloudProfileNode;
import org.microcloud.manager.core.model.datacenter.MicroCloud;
import org.microcloud.manager.core.model.datacenter.Rack;
import org.microcloud.manager.core.placer.PlacementProblem;
import org.microcloud.manager.operations.UnitsConv;
import org.microcloud.manager.persistence.EntityLoader;

public class SolutionMicroCloud {
	
	private class DatesKey {
		Date startTime;
		Date endTime;
		DatesKey(Date startTime,Date endTime) { this.startTime = startTime; this.endTime = endTime; }
	}
	
	public static Double getBandwidthBetween(SolutionMicroCloud sourceMC, SolutionMicroCloud destMC) {
		Double bandBetween;
		
		if(sourceMC.equals(destMC)) {
			bandBetween = sourceMC.microCloud.getDataCenterAttributes().getConnectionBandInside();
		}
		else {
			bandBetween = (double) Math.max(sourceMC.microCloud.getDataCenterAttributes().getOutputBandwidthMBitInt(), 
					destMC.microCloud.getDataCenterAttributes().getInputBandwidthMBitInt());
		}
		
		return UnitsConv.bitToByte(bandBetween);
	}
	
	public Integer getFreeHostsNumber(PlacementProblem placementProblem) {
		Integer freeHostsNumber = null;
		
		Date startTime = placementProblem.getClientQuery().getStartTime();
		Date endTime = new Date(startTime.getTime() + placementProblem.getExpectedExecutionTimeS()*1000);
		
		Set<Integer> hostsIds = new HashSet<>();
		for(Rack r : microCloud.getRacks()) {
			for(Host h : r.getHosts()) {
				hostsIds.add(h.getId());
			}
		}
		List<Host> busyHosts = EntityLoader.getInstance().getBusyHostsInTime(hostsIds, startTime, endTime);
		
		freeHostsNumber = hostsIds.size() - busyHosts.size();
		
		return freeHostsNumber;
	}
	
	private MicroCloud microCloud;
	private HashMap<DatesKey, Double> averageExecPrices = new HashMap<>();
	private HashMap<DatesKey, Double> averageInPrices = new HashMap<>();
	private HashMap<DatesKey, Double> averageOutPrices = new HashMap<>();
	
	public SolutionMicroCloud(MicroCloud microCloud) {
		this.microCloud = microCloud;
	}
	
	public MicroCloud getDataCenter() {
		return this.microCloud;
	}

	public double getAverageExecPricePerH(Date startTime, Date endTime) {
		return getAveragePrice(startTime, endTime, true, false);
	}
	
	public double getAverageTransferOutPricePerMB(Date startTime, Date endTime) {
		return getAveragePrice(startTime, endTime, false, true);
	}
	
	public double getAverageTransferInPricePerMB(Date startTime, Date endTime) {
		return getAveragePrice(startTime, endTime, false, false);
	}
	
	
	/**
	 * 
	 * Counts average price (per hour or per MB) during the given period 
	 * by using an abstraction of a surface underneath a function (integral).
	 * 
	 * @param startTime
	 * @param endTime
	 * @param isExec
	 * @param isOut
	 * @return
	 */
	protected double getAveragePrice(Date startTime, Date endTime, boolean isExec, boolean isOut) {

		Double averagePrice;
		HashMap<DatesKey, Double> averagePrices =
				isExec ? averageExecPrices : (isOut ? averageOutPrices : averageInPrices);
		DatesKey datesKey = new DatesKey(startTime,endTime);
		
		Double val = averagePrices.get(datesKey);
		if( val != null ) {
			averagePrice = val;
		}
		else {
			List<MicroCloudProfileNode> microCloudProfileList = EntityLoader.getInstance().
					getDataCenterProfileNodes(microCloud.getDataCenterProfile().getId(), startTime, endTime);
			
			Date currentPointOfTime = startTime;
			
			/* surface counted with units of milliseconds */
			double surface = 0.0;
			
			for(int i=0; i<microCloudProfileList.size(); i++) {
				int priceInThisPeriod =
						isExec ? microCloudProfileList.get(i).getUsagePrice() :
						(isOut ? microCloudProfileList.get(i).getOutPrice() : microCloudProfileList.get(i).getInPrice());
				
				Date periodEnd;
				if(i==microCloudProfileList.size()-1)
					periodEnd = endTime;
				else
					periodEnd = microCloudProfileList.get(i+1).getTime();
				
				long millisWithinPeriod = periodEnd.getTime() - currentPointOfTime.getTime();
				
				surface += priceInThisPeriod * millisWithinPeriod;
				
				currentPointOfTime = periodEnd;
			}
			
			averagePrice = surface / (endTime.getTime() - startTime.getTime());
			
			averagePrices.put(datesKey, averagePrice);	
		}

		return averagePrice;
	}
	
	@Override
	public boolean equals(Object obj) {
		boolean retVal = false;
		
		if(obj instanceof SolutionMicroCloud) {
			SolutionMicroCloud other = (SolutionMicroCloud) obj;
			
			if(other.microCloud.equals(this.microCloud)) {
				retVal = true;
			}
		}
		
		return retVal;
	}
	
}
