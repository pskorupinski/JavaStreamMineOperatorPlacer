package org.microcloud.manager.core.streammine.manager.ops;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.microcloud.manager.core.model.streammine.ManagerInput;
import org.microcloud.manager.core.model.streammine.ManagerInputNode;
import org.microcloud.manager.core.model.streammine.SliceHost;
import org.microcloud.manager.core.streammine.manager.model.LaunchableSlice;
import org.microcloud.manager.core.streammine.manager.ops.SMOperatorInfo.OperatorStateType;
import org.microcloud.manager.core.streammine.manager.ops.SMSliceInfo.SliceStateType;
import org.microcloud.manager.core.systemstate.BusyHostsController;
import org.microcloud.manager.structures.BooleansTable;
import org.omg.PortableInterceptor.NON_EXISTENT;

import streammine3G.CloudControl;
import streammine3G.OperatorConfig;

public class SlicesOperatorsData {

	private Map<Integer, SMSliceInfo> deployedSlicesMap = new HashMap<>();
	private Map<String, SMOperatorInfo> createdOperatorsMap = new HashMap<>();
	private List<LaunchableSlice> slicesToBeLaunched = new ArrayList<>();
	
	private Map<SMTaskChild, SMTaskInfo> mapToTasks = new HashMap<>();
	
	private Map<Integer, Set<Integer>> scheduledTeardownsMap = new HashMap<>();
	
	private CloudControl cloudControl;
	private String addressBase;
	private BooleansTable freeOperatorUids;
	private BooleansTable freeSlicesUids;
	
	public SlicesOperatorsData(
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
		mapToTasks.put(smOperatorInfo, taskInfo);
        
	}
	
	/**
	 * 
	 * @param operatorName
	 */
	public synchronized void operatorCreated(String operatorName) {
		
		SMOperatorInfo operatorInfo = createdOperatorsMap.get(operatorName);
		SMTaskInfo taskInfo = mapToTasks.get(operatorInfo);
		
		operatorInfo.setOperatorState(OperatorStateType.CREATED);
		
		if(taskInfo.incrementCounterIsMax()) {
			taskInfo.setOperationMax(taskInfo.getManagerInput().getManagerInputNodes().size());
			deployOperators(taskInfo.getManagerInput());
		}
		
	}
	
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
	 * @param operatorName
	 */
	public synchronized void operatorDeployed(String operatorName) {
		SMOperatorInfo operatorInfo = createdOperatorsMap.get(operatorName);
		SMTaskInfo taskInfo = mapToTasks.get(operatorInfo);
		
		operatorInfo.setOperatorState(OperatorStateType.DEPLOYED);
		
		if(taskInfo.incrementCounterIsMax()) {
			int operationMaxCounter = 0;
			
			for(ManagerInputNode n : taskInfo.getManagerInput().getManagerInputNodes()) {
	    		if(n.getWireWith() != null) {
	    			operationMaxCounter += n.getWireWith().size();
	    		}
	    	}
			
			taskInfo.setOperationMax(operationMaxCounter);
			wireOperators(taskInfo.getManagerInput());
		}
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
	 * @param operatorName
	 */
	public synchronized void operatorWired(String operatorName) {
		SMTaskChild taskChild = createdOperatorsMap.get(operatorName);
		SMTaskInfo taskInfo = mapToTasks.get(taskChild);
		
		if(taskInfo.incrementCounterIsMax()) {
			int operationMaxCounter = 0;
			
			/* When all are wired, we're sure that every one is wired */
			for(SMOperatorInfo opInfo : taskInfo.getOperatorChildren()) {
				opInfo.setOperatorState(OperatorStateType.WIRED);
			}
			
			/* Deploy slices */
	    	for(ManagerInputNode n : taskInfo.getManagerInput().getManagerInputNodes()) {
	    		operationMaxCounter += n.getHosts().size();
	    	}
			
			taskInfo.setOperationMax(operationMaxCounter);
			deploySlices(taskInfo.getManagerInput());
		}
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
		mapToTasks.put(smSliceInfo, mapToTasks.get(smOperatorInfo));
	
		/* physically deploy */
        cloudControl.deployOperatorSlice(
        		sh.getHost(),	/* host address */
        		n.getName(), 	/* operator name */
        		sh.getSliceId(),/* slice id */
        		sliceUid		/* slice unique id */
        		);
        System.out.println("deployOperatorSlice("+sh.getHost()+","+n.getName()+","+sh.getSliceId()+","+sliceUid+");"); 
        
        /* add to the list of unlaunched slices */
        //slicesToBeLaunched.add(new LaunchableSlice(sliceUid, null));		
		
	}
	
	/**
	 * 
	 * @param operatorName
	 */
	public synchronized void sliceDeployed(int sliceUid) {
		SMSliceInfo smSliceInfo = deployedSlicesMap.get(sliceUid);
		SMTaskInfo taskInfo = mapToTasks.get(smSliceInfo);
		
		smSliceInfo.setSliceState(SliceStateType.DEPLOYED);
		
		if(taskInfo.incrementCounterIsMax()) {
			int operationMaxCounter = 0;
			
			/* Deploy slices */
	    	for(ManagerInputNode n : taskInfo.getManagerInput().getManagerInputNodes()) {
	    		operationMaxCounter += n.getHosts().size();
	    	}
			
			taskInfo.setOperationMax(operationMaxCounter);
			launchSlices(taskInfo.getManagerInput());
		}
	}
	
	/**
	 * 
	 * @param input
	 */
	public synchronized void launchSlices(ManagerInput input) {
		
		List<ManagerInputNode> managerInputNodeList = input.getManagerInputNodes();
		
		/* Deploy slices */
    	for(ManagerInputNode n : managerInputNodeList) {
    		for(int i=0; i < n.getHosts().size(); i++) {
    			launchSlice(n.getHosts().get(i));
    		}
    	}
		
	}
	
	/**
	 * 
	 * @param sh
	 */
	public synchronized void launchSlice(SliceHost sh) {
		cloudControl.launchOperatorSlice(sh.getSliceUid());
        System.out.println("launchOperatorSlice("+sh.getSliceUid()+");");
	}
	
	/**
	 * 
	 * @param sliceUid
	 */
	public synchronized void sliceLauched(Integer sliceUid) {
		
		System.out.println("Called: sliceLauched("+sliceUid+")");
		
		SMSliceInfo smSliceInfo = deployedSlicesMap.get(sliceUid);
		
		smSliceInfo.setSliceState(SliceStateType.LAUNCHED);
		smSliceInfo.getOperator().setOperatorState(OperatorStateType.LAUNCHED);
		
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
			teardownSlice(smSliceInfo);
		}		
		
	}
	
	/**
	 * 
	 * @param smSliceInfo
	 */
	private void teardownSlice(SMSliceInfo smSliceInfo) {
		/* Physically remove */
		cloudControl.tearDownOperatorSlice(smSliceInfo.getSliceUid());
		System.out.println("tearDownOperatorSlice("+smSliceInfo.getSliceUid()+")");
	}

	/**
	 * 
	 * @param sliceUid
	 */
	public synchronized void sliceTorndown(int sliceUid) {
		
		System.out.println("Called: sliceTorndown("+sliceUid+")");
				
		SMSliceInfo smSliceInfo = deployedSlicesMap.get(sliceUid);
		
		smSliceInfo.setSliceState(SliceStateType.DEPLOYED);
		
		removeSlice(smSliceInfo);

	}
	
	private void removeSlice(SMSliceInfo smSliceInfo) {
		/* Physically remove */
		cloudControl.removeOperatorSlice(smSliceInfo.getSliceUid());	
		System.out.println("removeOperatorSlice("+smSliceInfo.getSliceUid()+")");
	}

	/**
	 * 
	 * @param sliceUid
	 */
	public synchronized void sliceRemoved(int sliceUid) {
		
		System.out.println("Called: sliceRemoved("+sliceUid+")");

		SMSliceInfo smSliceInfo = deployedSlicesMap.get(sliceUid);
		
		/* Logically remove */
		deployedSlicesMap.remove(smSliceInfo.getSliceUid());
		boolean operatorSlicesRemoved = smSliceInfo.remove();
		freeSlicesUids.clear(smSliceInfo.getSliceUid());
		
		smSliceInfo.setSliceState(SliceStateType.NONEXISTENT);
		
		/* Host not busy anymore */
		BusyHostsController.getInstance().freeHostTime(smSliceInfo.getHostBusyTimesId());
		
		if(operatorSlicesRemoved) {
			unwireOperator(smSliceInfo.getOperator());
		}
	}
	
	/**
	 * 
	 * @param smOperatorInfo
	 */
	private void unwireOperator(SMOperatorInfo smOperatorInfo) {
		
		String operatorName = smOperatorInfo.getOperatorName();
		
		/* UNWIRING */
		String wiringDst = smOperatorInfo.getWiringDst();
		
		if(wiringDst != null) {	
			/* Physically unwire */
			cloudControl.unwireOperator(operatorName, wiringDst);
			System.out.println("unwireOperator("+operatorName+", "+wiringDst+")");
		}
		else {
			operatorUnwired(operatorName);
		}
		
	}
	
	/**
	 * In an assumed situation when there is always one output wiring from operators,
	 * it is possible to define which operators were exactly defined
	 * 
	 * @param srcOperatorName
	 */
	public synchronized void operatorUnwired(String srcOperatorName) {
		
		System.out.println("Called: operatorUnwired("+srcOperatorName+")");
		
		SMOperatorInfo smSourceOperatorInfo = createdOperatorsMap.get(srcOperatorName);
		SMOperatorInfo smDestinationOperatorInfo = null;
		if(smSourceOperatorInfo.getWiringDst() != null)
			smDestinationOperatorInfo = createdOperatorsMap.get(smSourceOperatorInfo.getWiringDst());
		
		smSourceOperatorInfo.setOperatorState(OperatorStateType.DEPLOYED);
		
		/* Check if fully unwired - then remove */
		if(smSourceOperatorInfo.getWiringSrcsList().size() == 0) {
			smSourceOperatorInfo.setOperatorState(OperatorStateType.CREATED);
			
			/* Unwire logically only if fully unwired */
			smSourceOperatorInfo.unsetWiringDst();
			
			if(smDestinationOperatorInfo != null)
				smDestinationOperatorInfo.removeWiringSrc(srcOperatorName);
			
			removeOperator(smSourceOperatorInfo);
		}

		if(smDestinationOperatorInfo != null) {
	
			if(smDestinationOperatorInfo.getWiringSrcsList().size() == 0 
					&& smDestinationOperatorInfo.getOperatorState().ordinal() < OperatorStateType.WIRED.ordinal()) {			
				operatorUnwired(smDestinationOperatorInfo.getOperatorName());
			}
		}
		
	}
	
	/**
	 * 
	 * @param smOperatorInfo
	 */
	private void removeOperator(SMOperatorInfo smOperatorInfo) {
		
		/* Physically remove */
		cloudControl.removeOperator(smOperatorInfo.getOperatorName());
		System.out.println("removeOperator("+smOperatorInfo.getOperatorName()+")");
	}

	/**
	 * 
	 * @param operatorName
	 */
	public synchronized void operatorRemoved(String operatorName) {
		
		SMOperatorInfo smOperatorInfo = createdOperatorsMap.get(operatorName);
		
		/* Logically remove */	
		smOperatorInfo.setOperatorState(OperatorStateType.NONEXISTENT);
		createdOperatorsMap.remove(smOperatorInfo.getOperatorName());
		freeOperatorUids.clear(smOperatorInfo.getOperatorUid());
		
		boolean allRemoved = true;
		List<SMOperatorInfo> taskOperatorsList = smOperatorInfo.getSMTaskInfo().getOperatorChildren();
		for(SMOperatorInfo opInfo : taskOperatorsList) {
			if(opInfo.getOperatorState()!=OperatorStateType.NONEXISTENT) {
				allRemoved = false;
				break;
			}
		}
		
		if(allRemoved) {
			System.out.println("All operators removed - removing task");
			removeTask(smOperatorInfo.getSMTaskInfo());
		}
		
	}

	/**
	 * @param sliceUid 
	 * 
	 */
	public synchronized void removeSlices(int sliceUid) {
		
		SMSliceInfo sliceInfo = deployedSlicesMap.get(sliceUid);
		
		List<SMSliceInfo> slicesList = sliceInfo.getSMTaskInfo().getSliceChildren();

		boolean isActionTaken = false;
		
		for(SMSliceInfo smSliceInfo : slicesList) {
			
			if(smSliceInfo.getSliceState() == SliceStateType.LAUNCHED) {
				teardownSlice(smSliceInfo);
			}
			else if(smSliceInfo.getSliceState() == SliceStateType.DEPLOYED) {
				removeSlice(smSliceInfo);
			}
			
		}
		
		if(!isActionTaken) removeOperators(sliceInfo.getOperator().getOperatorName());
				
	}

	/**
	 * Starts a procedure of removal of all the operators that belong to the same task as given operator
	 * 
	 * @param operatorName 
	 * 
	 */
	public synchronized void removeOperators(String operatorName) {
		
		/* 1. Get the task for this operator name */
		SMTaskChild smTaskChild = createdOperatorsMap.get(operatorName);
		
		List<SMOperatorInfo> operatorsList = smTaskChild.getSMTaskInfo().getOperatorChildren();
		
		boolean isActionTaken = false;
		
		/* For every operator, start a procedure from an appropriate point */
		for(SMOperatorInfo smOperatorInfo : operatorsList) {
			
			if(smOperatorInfo.getWiringDst() != null || 
					smOperatorInfo.getWiringSrcsList().size() > 0) {
				unwireOperator(smOperatorInfo);
				isActionTaken = true;
			}
			else if(smOperatorInfo.getOperatorState() != OperatorStateType.NONEXISTENT) {
				removeOperator(smOperatorInfo);
				isActionTaken = true;
			}
			
		}
		
		if(!isActionTaken) {
			removeTask(smTaskChild.getSMTaskInfo());
		}
		
	}

	/**
	 * 
	 * @param smTaskInfo
	 */
	private void removeTask(SMTaskInfo smTaskInfo) {
		for(Entry<SMTaskChild, SMTaskInfo> e : mapToTasks.entrySet()) {
			if(e.getValue().equals(smTaskInfo))
				mapToTasks.remove(e.getKey());
		}
	}	
	
}
