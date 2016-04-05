package org.microcloud.manager.core.streammine.manager;


import org.microcloud.manager.proto.SMOperatorInitParam.GeneralKey;
import org.microcloud.manager.proto.SMOperatorInitParam.SearchRequest;

import com.google.protobuf.TextFormat;

import streammine3G.Action;
import streammine3G.CloudControl;
import streammine3G.Manager;
import streammine3G.OperatorConfig;
import streammine3G.PerformanceProbe;

public class SampleManager implements Manager
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
		System.out.println("From " + sliceUId + ": " + buf);
	}

	@Override
	public void onActionCompleted(Action action, String payload1, int payload2, String payload3)
	{
	}

	@Override
	public void onTimer()
	{
		String base = "/local/mt1/workspace/example-third/build/";
	    OperatorConfig operatorConfig = null;

	    System.out.println("onTimer: "+ (timeCounter + 1));
	    System.out.println("nodeCounter: "+ nodeCounter);
	    
	    //if (nodeCounter != 2 && timeCounter == 0) return;
	    timeCounter++;

	    /////////////////////////////////////////////////////////////////////////////////
	     /////////////////////////////////////////////////////////////////////////////////
	 	/* Mongo DB Reading operator */
	 	SearchRequest.Builder builder = SearchRequest.newBuilder();
		
		GeneralKey.Builder keyBuilder = GeneralKey.newBuilder();
		keyBuilder.setName("ff50012e928c630eee16c2f6746ef965");
		keyBuilder.setFirst(0);
		keyBuilder.setLast(0);
		builder.addGeneralKeys(keyBuilder.build());
		
		builder.setHost("h2.r1.mc1.microcloud.com");
		builder.setPort(10001);
		builder.setDataSourceImplType(SearchRequest.DataSourceImplType.MONGO_GRIDFS);
		builder.setReadPreferenceType(SearchRequest.ReadPreferenceType.LIST_OF_KEYS);
		builder.setSourceName("filesystem");

	    /////////////////////////////////////////////////////////////////////////////////
	    /////////////////////////////////////////////////////////////////////////////////


	    switch(timeCounter)
	    {
	        case 3:
	        	
	        	String parameters = TextFormat.printToString(builder.build());
	        	String parameters2 = builder.build().toByteString().toStringUtf8();
	        	System.out.println(parameters);
	        	
	            operatorConfig = cloudControl.createOperator("source", 0);
		        operatorConfig.setParameter("libraryPath", base + "libAccessop.so");
		        operatorConfig.setParameter("parameters", parameters);
		        operatorConfig.setParameter("timerInterval" , "1000000");
		        operatorConfig.setParameter("checkPointEpochLength", "10");
		        
		        operatorConfig = cloudControl.createOperator("mapper", 1);
		        operatorConfig.setParameter("libraryPath", base + "libMapper.so");
		        operatorConfig.setParameter("slices", "1");
		        operatorConfig.setParameter("routingKeyRangeSize", "1");
		        operatorConfig.setParameter("checkPointEpochLength", "10");
		        
		        operatorConfig = cloudControl.createOperator("worker", 2);
		        operatorConfig.setParameter("libraryPath", base + "libWorkerop.so");
		        operatorConfig.setParameter("slices", "2");
		        operatorConfig.setParameter("partitionerLibrary", base + "libSamplePartitioner.so");
			    operatorConfig.setParameter("checkPointEpochLength", "10");

	            // deploy previously configured operators - deployment means registering the nodes in the ZK config
	            cloudControl.deployOperator("source");
	            cloudControl.deployOperator("mapper");
	            cloudControl.deployOperator("worker");

	            // connect operators
	            cloudControl.wireOperator("source", "mapper");
	            cloudControl.wireOperator("mapper", "worker");
	            break;

	        case 5:
	            // sliceId in this case (see config above) we have 2 slices per op
	            // sliceUId unique number (has to be different for each slice)
	            cloudControl.deployOperatorSlice("h2.r1.mc1.microcloud.com", "source", 0 /* sliceId */, 0 /* sliceUId */);
	            cloudControl.deployOperatorSlice("h2.r1.mc1.microcloud.com", "mapper", 0 /* sliceId */, 1 /* sliceUId */);
	            cloudControl.deployOperatorSlice("h2.r1.mc1.microcloud.com", "worker", 0 /* sliceId */, 2 /* sliceUId */);
	            break;

	        case 7:
	            cloudControl.launchOperatorSlice(0);
	            cloudControl.launchOperatorSlice(1);
	            cloudControl.launchOperatorSlice(2);
//	            break;
//
//	        case 54:
//	            cloudControl.tearDownOperatorSlice(0);
//	            cloudControl.tearDownOperatorSlice(1);
//	            cloudControl.tearDownOperatorSlice(2);
//	            cloudControl.tearDownOperatorSlice(3);
//	            break;
//
//	        case 56:
//	            cloudControl.removeOperatorSlice(0);
//	            cloudControl.removeOperatorSlice(1);
//	            cloudControl.removeOperatorSlice(2);
//	            cloudControl.removeOperatorSlice(3);
//	            break;
//
//	        case 57:
//	            cloudControl.unwireOperator("source", "mapper");
//	            cloudControl.unwireOperator("mapper", "worker");
//	            break;
//
//	        case 58:
//	            cloudControl.removeOperator("source");
//	            cloudControl.removeOperator("mapper");
//	            cloudControl.removeOperator("worker");

//	            cloudControl.shutDownNode("node1.mycloud.com");
//	            cloudControl.shutDownNode("node2.mycloud.com");
//	            break;
//
//	        case 29:
//	            cloudControl.shutDownNode("node0.mycloud.com");
	            break;
	    }
 
	 }

	@Override
	public String version() {
		// TODO Auto-generated method stub
		return "1.4.11";
	}
}
