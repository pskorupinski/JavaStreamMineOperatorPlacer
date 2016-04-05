package org.microcloud.manager.core.streammine.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.microcloud.manager.core.model.streammine.ManagerInput;
import org.microcloud.manager.core.model.streammine.ManagerInputNode;
import org.microcloud.manager.core.model.streammine.ManagerTask;
import org.microcloud.manager.core.streammine.manager.ops.SMTaskInfo;
import org.microcloud.manager.core.streammine.manager.ops.SlicesOperatorsData;
import org.microcloud.manager.persistence.objectsloader.ManagerTaskDao;
import org.microcloud.manager.structures.BooleansTable;

import com.google.protobuf.TextFormat;

import streammine3G.Action;
import streammine3G.CloudControl;
import streammine3G.Manager;
import streammine3G.OperatorConfig;
import streammine3G.PerformanceProbe;

public class AsyncMicroCloudSMManager implements Manager
{
	private CloudControl cloudControl;

    private int nodeCounter = 0;
    private int timeCounter = 0;
    
    private BooleansTable freeOperatorUids = new BooleansTable(10000);
    private BooleansTable freeSlicesUids = new BooleansTable(100000);
    
    private SlicesOperatorsData slicesOperatorsData;
    
	@Override
	public void init(CloudControl cloudControl, String nodeName, int nodeId)
	{
		this.cloudControl = cloudControl;
		String base = "/local/mt1/workspace/example-third/build/";
		
		this.slicesOperatorsData = new SlicesOperatorsData(cloudControl, base, freeOperatorUids, freeSlicesUids);
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
		
		String str = "Action completed: " + action + " from ";
		if(payload1 != null && !payload1.isEmpty()) str += "operator " + payload1;
		else str += "slice " + payload2;
		
		System.out.println(str);
		
		if( action == Action.CreateOperator_SUCCESS ) {
			slicesOperatorsData.operatorCreated(payload1);
		}
		else if( action == Action.DeployOperator_SUCCESS ) {
			slicesOperatorsData.operatorDeployed(payload1);
		}
		else if( action == Action.WireOperator_SUCCESS ) {
			slicesOperatorsData.operatorWired(payload1);
		}
		else if( action == Action.DeployOperatorSlice_SUCCESS ) {
			slicesOperatorsData.sliceDeployed(payload2);
		}
		else if( action == Action.LaunchOperatorSlice_SUCCESS ) {
			slicesOperatorsData.sliceLauched(payload2);
		}
		
		else if( action == Action.TeardownOperatorSlice_SUCCESS ) {
			slicesOperatorsData.sliceTorndown(payload2);
		}
		else if( action == Action.RemoveOperatorSlice_SUCCESS ) {
			slicesOperatorsData.sliceRemoved(payload2);
		}
		else if( action == Action.UnwireOperator_SUCCESS) {
			slicesOperatorsData.operatorUnwired(payload1);
		}
		else if( action == Action.RemoveOperator_SUCCESS ) {
			slicesOperatorsData.operatorRemoved(payload1);
		}
		
//		else if( action == Action.CreateOperator_FAILED ) {
//			slicesOperatorsData.removeOperators(payload1);
//		}
//		else if( action == Action.DeployOperator_FAILED ) {
//			slicesOperatorsData.removeOperators(payload1);
//		}
//		else if( action == Action.WireOperator_FAILED ) {
//			slicesOperatorsData.removeOperators(payload1);
//		}
//		else if( action == Action.DeployOperatorSlice_FAILED ) {
//			slicesOperatorsData.removeSlices(payload2);
//		}
//		else if( action == Action.LaunchOperatorSlice_FAILED ) {
//			slicesOperatorsData.removeSlices(payload2);
//		}
		
	}

	@Override
	public void onTimer()
	{
	    System.out.println("onTimer: "+ (timeCounter + 1));
	    System.out.println("nodeCounter: "+ nodeCounter);

	    timeCounter++;
	    
	    if(timeCounter == 1) {

		/* Get tasks from within the next 10 seconds */
	    ManagerTaskDao managerTaskDao = new ManagerTaskDao();
	    List<ManagerTask> tasks = managerTaskDao.getByTime(10);
		
		/* DEPLOY */
	    for(ManagerTask task : tasks) {
	    	ManagerInput managerInput = task.getManagerInput();
	    	
	    	List<ManagerInputNode> managerInputNodeList = managerInput.getManagerInputNodes();
	    	
	    	SMTaskInfo smTaskInfo = new SMTaskInfo(managerInput);
	    	smTaskInfo.setOperationMax(managerInputNodeList.size());
	    	
	    	/* Create operators */
	    	for(ManagerInputNode n : managerInputNodeList)
		        slicesOperatorsData.createOperator(n, smTaskInfo);
	    		    	
	    	//
	    	
		    /* Remove the deployed tasks */
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
