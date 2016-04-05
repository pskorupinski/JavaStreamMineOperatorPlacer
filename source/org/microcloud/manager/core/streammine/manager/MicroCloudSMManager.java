package org.microcloud.manager.core.streammine.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.microcloud.manager.core.model.streammine.ManagerInput;
import org.microcloud.manager.core.model.streammine.ManagerInputNode;
import org.microcloud.manager.core.model.streammine.ManagerTask;
import org.microcloud.manager.core.streammine.manager.ops.SMTaskInfo;
import org.microcloud.manager.core.streammine.manager.ops.SlicesOperatorsData;
import org.microcloud.manager.core.streammine.manager.ops.SlicesOperatorsDataOlder;
import org.microcloud.manager.persistence.objectsloader.ManagerTaskDao;
import org.microcloud.manager.structures.BooleansTable;

import com.google.protobuf.TextFormat;

import streammine3G.Action;
import streammine3G.CloudControl;
import streammine3G.Manager;
import streammine3G.OperatorConfig;
import streammine3G.PerformanceProbe;

public class MicroCloudSMManager implements Manager
{
	private CloudControl cloudControl;

    private int nodeCounter = 0;
    private int timeCounter = 0;
    
    private BooleansTable freeOperatorUids = new BooleansTable(10000);
    private BooleansTable freeSlicesUids = new BooleansTable(100000);
    
    private SlicesOperatorsDataOlder slicesOperatorsData;
    
	@Override
	public void init(CloudControl cloudControl, String nodeName, int nodeId)
	{
		this.cloudControl = cloudControl;
		String base = "/local/mt1/workspace/example-third/build/";
		
		this.slicesOperatorsData = new SlicesOperatorsDataOlder(cloudControl, base, freeOperatorUids, freeSlicesUids);
	}

	@Override
	public void onNodeJoin(String nodeName, int nodeId)
	{
	    nodeCounter++;
	    System.out.println("Node joined: " + nodeName); 
	}

	@Override
	public void onNodeLeave(String nodeName, int nodeId)
	{
	    nodeCounter--;
	    System.out.println("Node left: " + nodeName); 
	}

	@Override
	public void onPerformanceProbe(PerformanceProbe performanceProbe)
	{
	}

	@Override
	public void onCustomProbe(int sliceUId, String buf) {

		System.out.println("Slice " + sliceUId + " has sent a custom probe: [" + buf + "]");
		
		if(buf.toUpperCase().contains("EOS"))			
			slicesOperatorsData.eosSignalHandlerMethod(sliceUId);
	}

	@Override
	public void onActionCompleted(Action payload2Enum, String payload1, int actionInt, String payload3)
	{
		int payload2 = payload2Enum.ordinal();
		Action action = Action.NOOP;
		for(Action type : Action.values()) {
			if(type.ordinal() == actionInt) {
				action = type;
				break;
			}
		}
		
		System.out.print("Called: onActionCompleted(");
		System.out.print(action.toString()+", ");
		if(payload1 != null) System.out.print(payload1+", ");
		System.out.print(payload2+", ");
		if(payload3 != null) System.out.print(payload3+")");
		System.out.println();
	}

	@Override
	public void onTimer()
	{
	    System.out.println("onTimer: "+ (timeCounter + 1));
	    //System.out.println("nodeCounter: "+ nodeCounter);

	    timeCounter++;

		/* Get tasks from within the next 10 seconds */
	    ManagerTaskDao managerTaskDao = new ManagerTaskDao();
	    if(timeCounter == 1) {
	    List<ManagerTask> tasks = managerTaskDao.getByTime(10);
		
		/* DEPLOY */
	    for(ManagerTask task : tasks) {
	    	
	    	ManagerInput managerInput = task.getManagerInput();
	    	
	    	SMTaskInfo smTaskInfo = new SMTaskInfo(managerInput);
	    	
	    	List<ManagerInputNode> managerInputNodeList = managerInput.getManagerInputNodes();
	    	
	    	/* Create operators */
	    	for(ManagerInputNode n : managerInputNodeList)
		        slicesOperatorsData.createOperator(n, smTaskInfo);
	    	
	    	/* Deploy operators */
	    	for(ManagerInputNode n : managerInputNodeList)
	    		slicesOperatorsData.deployOperator(n);
	    	
	    	/* Wire operators */
	    	for(ManagerInputNode n : managerInputNodeList) {
	    		String wireFrom = n.getName();
	    		if(n.getWireWith() != null) {
	    			for(String wireTo : n.getWireWith()) {
	    				slicesOperatorsData.wireOperator(wireFrom, wireTo);
	    			}
	    		}
	    	}
	    	
	    	/* Deploy slices */
	    	for(ManagerInputNode n : managerInputNodeList) {
	    		for(int i=0; i < n.getHosts().size(); i++) {
	    			slicesOperatorsData.deploySlice(
	    					n, 
	    					n.getHosts().get(i)
	    					);
	    		}
	    	}
	    	
	    	/* Launch slices */
	    	slicesOperatorsData.launchNewSlices();
	    	
	    	//
	    	
		    /* Remove the task */
	    	//managerTaskDao.delete(task);
	    }
	    }
	 }

	@Override
	public String version() {
		// TODO Auto-generated method stub
		return "1.4.12";
	}
}
