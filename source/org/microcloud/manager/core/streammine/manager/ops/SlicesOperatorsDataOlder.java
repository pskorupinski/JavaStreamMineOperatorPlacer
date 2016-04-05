package org.microcloud.manager.core.streammine.manager.ops;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.microcloud.manager.core.model.streammine.ManagerInput;
import org.microcloud.manager.core.model.streammine.ManagerInputNode;
import org.microcloud.manager.core.model.streammine.SliceHost;
import org.microcloud.manager.core.streammine.manager.model.LaunchableSlice;
import org.microcloud.manager.core.systemstate.BusyHostsController;
import org.microcloud.manager.structures.BooleansTable;

import streammine3G.CloudControl;
import streammine3G.OperatorConfig;

public class SlicesOperatorsDataOlder {

	private Map<Integer, SMSliceInfo> deployedSlicesMap = new HashMap<>();
	private Map<String, SMOperatorInfo> createdOperatorsMap = new HashMap<>();
	private List<LaunchableSlice> slicesToBeLaunched = new ArrayList<>();
	
	private Map<SMTaskChild, SMTaskInfo> mapToTasks = new HashMap<>();
	
	private Map<Integer, Set<Integer>> scheduledTeardownsMap = new HashMap<>();
	
	private CloudControl cloudControl;
	private String addressBase;
	private BooleansTable freeOperatorUids;
	private BooleansTable freeSlicesUids;
	
	public SlicesOperatorsDataOlder(
			CloudControl cloudControl, 
			String addressBase, 
			BooleansTable freeOperatorUids,
			BooleansTable freeSlicesUids) {
		this.cloudControl = cloudControl;
		this.addressBase = addressBase;
		this.freeOperatorUids = freeOperatorUids;
		this.freeSlicesUids = freeSlicesUids;
	}
	
	/**
	 * 
	 * @param n
	 * @param taskInfo
	 */
	public synchronized void createOperator(ManagerInputNode n, SMTaskInfo taskInfo) {
		
		n.setOperatorUid(this.freeOperatorUids.getFirstFree());
		
		/* PHYSICALLY CREATE */
		
        OperatorConfig operatorConfig = cloudControl.createOperator(n.getName(), n.getOperatorUid());
        System.out.println("createOperator("+n.getName()+","+n.getOperatorUid()+");");
        
        operatorConfig.setParameter("libraryPath", addressBase + n.getLibraryPath());
        System.out.println("setParameter(libraryPath,"+ addressBase + n.getLibraryPath() +");");
        
        operatorConfig.setParameter("slices", new Integer(n.getHosts().size()).toString());
        System.out.println("setParameter(slices,"+ new Integer(n.getHosts().size()).toString() +");");
        
        if(n.getParameters() != null) { 
        	operatorConfig.setParameter("parameters", n.getParameters());
            System.out.println("setParameter(parameters,"+ n.getParameters() +");");
        }
        
        if(n.getPartitionerPath() != null) {
        	operatorConfig.setParameter("partitionerLibrary", addressBase + n.getPartitionerPath());
            System.out.println("setParameter(partitionerLibrary,"+ addressBase + n.getPartitionerPath() +");");        	
        }
        if(n.getRoutingKeyRangeSize() != null) {
        	operatorConfig.setParameter("routingKeyRangeSize", n.getRoutingKeyRangeSize());
            System.out.println("setParameter(routingKeyRangeSize,"+ n.getRoutingKeyRangeSize() +");");  
		}
        operatorConfig.setParameter("timerInterval" , "1000000");
        operatorConfig.setParameter("checkPointEpochLength", "10");
        
        /* LOGICALLY CREATE */
        
        String operatorName = n.getName();
		int operatorUid = n.getOperatorUid();
		int EOSSignalsToShutDownSlice = n.getEOSSignalsToShutDownSlice();
		
		/* Add to created operators map */
		SMOperatorInfo smOperatorInfo = new SMOperatorInfo(taskInfo, operatorName, operatorUid, EOSSignalsToShutDownSlice);
		createdOperatorsMap.put(operatorName, smOperatorInfo);
        
	}
	
//	/**
//	 * 
//	 * @param operatorName
//	 */
//	public synchronized void operatorCreated(String operatorName) {
//		
//		SMOperatorInfo operatorInfo = createdOperatorsMap.get(operatorName);
//		SMTaskInfo taskInfo = mapToTasks.get(operatorInfo);
//		
//		if(taskInfo.incrementCounterIsMax()) {
//			taskInfo.setOperationMax(taskInfo.getManagerInput().getManagerInputNodes().size());
//			deployOperators(taskInfo.getManagerInput());
//		}
//		
//	}
	
	/**
	 * 
	 * @param input
	 */
	public synchronized void deployOperators(ManagerInput input) {
		
		List<ManagerInputNode> managerInputNodeList = input.getManagerInputNodes();
		
		/* Deploy operators */
    	for(ManagerInputNode n : managerInputNodeList)
    		deployOperator(n);
		
	}
	
	/**
	 * 
	 * @param operatorName
	 */
	public synchronized void deployOperator(ManagerInputNode n) {

		/* Physically deploy */
		cloudControl.deployOperator(n.getName());
        System.out.println("deployOperator("+ n.getName() +");");
        
	}
	
	/**
	 * 
	 * @param input
	 */
	public synchronized void wireOperators(ManagerInput input) {
		
		List<ManagerInputNode> managerInputNodeList = input.getManagerInputNodes();
		
		/* Wire operators */
    	for(ManagerInputNode n : managerInputNodeList) {
    		String wireFrom = n.getName();
    		if(n.getWireWith() != null) {
    			for(String wireTo : n.getWireWith()) {
    				wireOperator(wireFrom, wireTo);
    			}
    		}
    	}
		
	}
	
	/**
	 * 
	 * @param wireFrom
	 * @param wireTo
	 * @return
	 */
	public synchronized boolean wireOperator(String wireFrom, String wireTo) {
		
		SMOperatorInfo operatorFrom = createdOperatorsMap.get(wireFrom);
		SMOperatorInfo operatorTo = createdOperatorsMap.get(wireTo);
		
		if(operatorFrom==null || operatorTo==null ) {
			return false;
		}

		/* Logically wire */
		operatorFrom.setWiringDst(wireTo);
		operatorTo.addWiringSrc(wireFrom);
		
		/* Physically wire */
		cloudControl.wireOperator(wireFrom, wireTo);
        System.out.println("wireOperator("+wireFrom+","+wireTo+");"); 
        
        return true;
	}
	
	/**
	 * 
	 * @param input
	 */
	public synchronized void deploySlices(ManagerInput input) {
		
		List<ManagerInputNode> managerInputNodeList = input.getManagerInputNodes();
		
		/* Deploy slices */
    	for(ManagerInputNode n : managerInputNodeList) {
    		for(int i=0; i < n.getHosts().size(); i++) {
    			deploySlice(
    					n, 
    					n.getHosts().get(i)
    					);
    		}
    	}
		
	}
	
	/**
	 * 
	 * @param n
	 * @param host
	 * @param sliceId
	 */
	public synchronized void deploySlice(ManagerInputNode n, SliceHost sh) {
		
		Integer sliceUid = this.freeSlicesUids.getFirstFree();
		
		sh.setSliceUid(sliceUid);
		
		SMOperatorInfo smOperatorInfo = createdOperatorsMap.get(n.getName());
		smOperatorInfo.addSlice(sliceUid);
		
		SMSliceInfo smSliceInfo = new SMSliceInfo(smOperatorInfo.getSMTaskInfo(), smOperatorInfo, sliceUid, sh.getHostBusyTimesId());
		deployedSlicesMap.put(sliceUid, smSliceInfo);
	
		/* physically deploy */
        cloudControl.deployOperatorSlice(
        		sh.getHost(),	/* host address */
        		n.getName(), 	/* operator name */
        		sh.getSliceId(),/* slice id */
        		sliceUid		/* slice unique id */
        		);
        System.out.println("deployOperatorSlice("+sh.getHost()+","+n.getName()+","+sh.getSliceId()+","+sliceUid+");"); 
        
        /* add to the list of unlaunched slices */
        slicesToBeLaunched.add(new LaunchableSlice(sliceUid, null));		
		
	}

	/**
	 * 
	 */
	public synchronized void launchNewSlices() {	
    	for(Iterator<LaunchableSlice> it = slicesToBeLaunched.iterator(); it.hasNext(); ) {
    		LaunchableSlice slice = it.next();
    		cloudControl.launchOperatorSlice(slice.getSliceUid());
            System.out.println("launchOperatorSlice("+slice.getSliceUid()+");"); 
    		it.remove();
    	}
	}
	
	/**
	 * Method responsible for firing slices and operators removal, if needed
	 * 
	 * @param sliceUid
	 */
	public synchronized void eosSignalHandlerMethod(Integer sliceUid) {
		
		SMSliceInfo smSliceInfo = deployedSlicesMap.get(sliceUid);

		/* decrement slice counter */
		if(smSliceInfo.decrementCounterGetIsZero()) {
			
			/* slice teardown */
			if(removeSlice(smSliceInfo)) {
				/* operator removal */
				removeOperator(smSliceInfo.getOperator());
			}
		}		
		
	}
	
	private boolean removeSlice(SMSliceInfo smSliceInfo) {
		/* Physically remove */
		cloudControl.tearDownOperatorSlice(smSliceInfo.getSliceUid());
		cloudControl.removeOperatorSlice(smSliceInfo.getSliceUid());
		
		/* Logically remove */
		deployedSlicesMap.remove(smSliceInfo.getSliceUid());
		boolean operatorSlicesRemoved = smSliceInfo.remove();
		freeSlicesUids.clear(smSliceInfo.getSliceUid());
		/* Host not busy anymore */
		
		BusyHostsController.getInstance().freeHostTime(smSliceInfo.getHostBusyTimesId());
		
		return operatorSlicesRemoved;
	}
	
	/**
	 * 
	 * @param smOperatorInfo
	 */
	private void removeOperator(SMOperatorInfo smOperatorInfo) {
		
		String operatorName = smOperatorInfo.getOperatorName();
		
		/* UNWIRING */

		List<String> wiringSrcsList = smOperatorInfo.getWiringSrcsList();
		String wiringDst = smOperatorInfo.getWiringDst();
		
		for(String wiringSrc : wiringSrcsList) {
			/* Physically unwire */
			cloudControl.unwireOperator(wiringSrc, operatorName);
			
			/* Logically unwire */
			SMOperatorInfo sourceOperator = createdOperatorsMap.get(wiringSrc);
			if(sourceOperator != null) sourceOperator.unsetWiringDst();
		}
		
		{
			/* Physically unwire */
			cloudControl.unwireOperator(operatorName, wiringDst);
			
			/* Logically unwire */
			SMOperatorInfo dstOperator = createdOperatorsMap.get(wiringDst);
			if(dstOperator != null) dstOperator.removeWiringSrc(operatorName);
		}
		
		/* Logically unwire */
		smOperatorInfo.clearWiringSrcsList();
		smOperatorInfo.unsetWiringDst();
		
		/* Physically remove */
		cloudControl.removeOperator(smOperatorInfo.getOperatorName());
		
		/* Logically remove */	
		createdOperatorsMap.remove(smOperatorInfo.getOperatorName());
		freeOperatorUids.clear(smOperatorInfo.getOperatorUid());
	}
	
	
	
}
