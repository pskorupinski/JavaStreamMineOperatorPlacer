package org.microcloud.manager.core.streammine.manager;


import streammine3G.Action;
import streammine3G.CloudControl;
import streammine3G.Manager;
import streammine3G.OperatorConfig;
import streammine3G.PerformanceProbe;

public class AndreSampleManager implements Manager
{
	private CloudControl cloudControl;

    int nodeCounter = 0;
    int timeCounter = 0;
    
	@Override
	public void init(CloudControl cloudControl, String nodeName, int nodeId)
	{
		this.cloudControl = cloudControl;
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
	public void onCustomProbe(int sliceUId, String buf)
	{
	}

	@Override
	public void onActionCompleted(Action action, String payload1, int payload2, String payload3)
	{
	}

	@Override
	public void onTimer()
	{
		String base = "/home/andre/workspace-cpp/StreamMine3G/build/src/example/";
	    OperatorConfig operatorConfig = null;
	    
	    System.out.println("onTimer: "+ (timeCounter + 1));
	    System.out.println("nodeCounter: "+ nodeCounter);

	    if (nodeCounter != 2 && timeCounter == 0) return;
	    timeCounter++;

	    switch(timeCounter)
	    {
	    case 3:
	        // configure source operator
	        operatorConfig = cloudControl.createOperator("source", 0);
	        operatorConfig.setParameter("libraryPath", base+"libSourceOperator.so");
	        operatorConfig.setParameter("parameters", "testParameter0");
	        operatorConfig.setParameter("timerInterval" , "1000000");
	        operatorConfig.setParameter("checkPointEpochLength", "10");
	        //operatorConfig.setParameter("layerType", "noorderdelegate");

	        // configure worker
	        operatorConfig = cloudControl.createOperator("worker", 1 /* operatorId */);
	        operatorConfig.setParameter("libraryPath", base+"libWorkerOperator.so");
	        operatorConfig.setParameter("parameters", "testParameter1");
	        operatorConfig.setParameter("slices", "2");
	        operatorConfig.setParameter("checkPointEpochLength", "10");
	        //operatorConfig.setParameter("layerType", "noorderdelegate");

	        // configure sink
	        operatorConfig = cloudControl.createOperator("sink", 2 /* operatorId */);
	        operatorConfig.setParameter("libraryPath", base+"libSinkOperator.so");
	        operatorConfig.setParameter("parameters", "testParameter2");
	        operatorConfig.setParameter("slices", "2");
	        operatorConfig.setParameter("checkPointEpochLength", "10");
	        operatorConfig.setParameter("partitionerLibrary", base+"libSamplePartitioner.so");
	        //operatorConfig.setParameter("layerType", "noorderdelegate");

	        // deploy previously configured operators - deployment means registering the nodes in the ZK config
	        cloudControl.deployOperator("source");
	        cloudControl.deployOperator("worker");
	        cloudControl.deployOperator("sink");

	        // connect operators
	        cloudControl.wireOperator("source", "worker");
	        cloudControl.wireOperator("worker", "sink");
	        break;

	     case 5:
	        // sliceId in this case (see config above) we have 2 slices per op
	        // sliceUId unique number (has to be different for each slice)
	        cloudControl.deployOperatorSlice("node1.mycloud.com", "source", 0 /* sliceId */, 0 /* sliceUId */);
	        cloudControl.deployOperatorSlice("node2.mycloud.com", "worker", 0 /* sliceId */, 1 /* sliceUId */);
	        cloudControl.deployOperatorSlice("node2.mycloud.com", "worker", 1 /* sliceId */, 2 /* sliceUId */);
	        cloudControl.deployOperatorSlice("node2.mycloud.com", "sink", 0 /* sliceId */, 3 /* sliceUId */);
	        cloudControl.deployOperatorSlice("node2.mycloud.com", "sink", 1 /* sliceId */, 4 /* sliceUId */);

	        // active replica for slice 2 on different SM3G process
	        // note that it has a different sliceUId than its original
	        cloudControl.deployOperatorSlice("node1.mycloud.com", "worker", 0 /* sliceId */, 5 /* sliceUId */);
	        break;

	     case 7:
	        cloudControl.launchOperatorSlice(0);
	        cloudControl.launchOperatorSlice(1);
	        cloudControl.launchOperatorSlice(2);
	        cloudControl.launchOperatorSlice(3);
	        cloudControl.launchOperatorSlice(4);
	        cloudControl.launchOperatorSlice(5);
	        break;

	     case 10:
	         cloudControl.migrateOperatorSlice(5, 8, "node2.mycloud.com");
	         //cloudControl.syncOperatorSlice(5, 3);

	         //cloudControl.tearDownOperatorSlice(5);
	         //cloudControl.removeOperatorSlice(5);
	         break;
	     case 24:
	         cloudControl.tearDownOperatorSlice(0);
	         cloudControl.tearDownOperatorSlice(1);
	         cloudControl.tearDownOperatorSlice(2);
	         cloudControl.tearDownOperatorSlice(3);
	         cloudControl.tearDownOperatorSlice(4);

	         cloudControl.tearDownOperatorSlice(5);

	         cloudControl.tearDownOperatorSlice(8);
	         break;

	     case 26:
	         cloudControl.removeOperatorSlice(0);
	         cloudControl.removeOperatorSlice(1);
	         cloudControl.removeOperatorSlice(2);
	         cloudControl.removeOperatorSlice(3);
	         cloudControl.removeOperatorSlice(4);

	         //cloudControl.removeOperatorSlice(5);

	         cloudControl.removeOperatorSlice(8);
	         break;

	     case 27:
	         cloudControl.unwireOperator("source", "worker");
	         cloudControl.unwireOperator("worker", "sink");
	         break;

	     case 28:
	         cloudControl.removeOperator("source");
	         cloudControl.removeOperator("worker");
	         cloudControl.removeOperator("sink");

	         cloudControl.shutDownNode("node1.mycloud.com");
	         cloudControl.shutDownNode("node2.mycloud.com");
	         break;

	     case 29:
	         cloudControl.shutDownNode("node0.mycloud.com");
	         break;

	    } 
	 }

	@Override
	public String version() {
		// TODO Auto-generated method stub
		return null;
	}
}
