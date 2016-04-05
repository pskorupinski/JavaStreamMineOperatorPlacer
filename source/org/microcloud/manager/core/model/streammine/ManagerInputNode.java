package org.microcloud.manager.core.model.streammine;

import java.io.Serializable;
import java.util.List;

import org.microcloud.manager.proto.SMOperatorInitParam.SearchRequest;

public class ManagerInputNode implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -966407967261420459L;

	private String name = null;
	private List<String> wireWith = null;
	
	private String libraryPath = null;
	private String partitionerPath = null;
	
	private String parameters = null;
	private List<SliceHost> hosts = null;

	private String keyRangeSize = null;
	
	private int EOSSignalsToShutDownSlice = 0;
	
	/*
	 * To be set within Manager code
	 */
	private int operatorUid = -1;
	
///////////////////////////////////////////////////////////////
// GETTERS AND SETTERS
///////////////////////////////////////////////////////////////

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getWireWith() {
		return wireWith;
	}
	public void setWireWith(List<String> wireWith) {
		this.wireWith = wireWith;
	}
	public String getLibraryPath() {
		return libraryPath;
	}
	public void setLibraryPath(String libraryPath) {
		this.libraryPath = libraryPath;
	}
	public String getPartitionerPath() {
		return partitionerPath;
	}
	public void setPartitionerPath(String partitionerPath) {
		this.partitionerPath = partitionerPath;
	}
	public String getParameters() {
		return parameters;
	}
	public void setParameters(String parameters) {
		this.parameters = parameters;
	}
	public List<SliceHost> getHosts() {
		return hosts;
	}
	public void setHosts(List<SliceHost> hosts) {
		this.hosts = hosts;
	}
	public void setRoutingKeyRangeSize(Integer keyRangeSize) {
		this.keyRangeSize = keyRangeSize.toString();
	}
	public String getRoutingKeyRangeSize() {
		return this.keyRangeSize;
	}
	public int getEOSSignalsToShutDownSlice() {
		return EOSSignalsToShutDownSlice;
	}
	public void setEOSSignalsToShutDownSlice(int eOSSignalsToShutDownSlice) {
		EOSSignalsToShutDownSlice = eOSSignalsToShutDownSlice;
	}
	public int getOperatorUid() {
		return operatorUid;
	}
	public void setOperatorUid(int operatorUid) {
		this.operatorUid = operatorUid;
	}
	
	@Override
	public String toString() {
		String str = new String();
		
		if(name!=null) 						str += "name: " + name; 
		if(libraryPath!=null) 				str += "| libraryPath: " + libraryPath;
		if(partitionerPath!=null) 			str += "| partitionerPath: " + partitionerPath;
		if(parameters!=null) 				str += "| parameters: " + parameters;
		if(keyRangeSize!=null) 				str += "| keyRangeSize: " + keyRangeSize;
		if(EOSSignalsToShutDownSlice!=0) 	str += "| EOSSignalsToShutDownSlice: " + EOSSignalsToShutDownSlice;
		if(wireWith!=null) { 				str += "| wireWith: ";
			for(String ww : wireWith)		str += ww + ","; }
		if(hosts!=null) { 					str += "| hosts: ";
			for(SliceHost sh : hosts)		str += sh.getHost() + ","; }		
		
		return str;
	}
}