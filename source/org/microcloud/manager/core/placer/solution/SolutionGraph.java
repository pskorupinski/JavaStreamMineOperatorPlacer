package org.microcloud.manager.core.placer.solution;

import java.util.ArrayList;
import java.util.List;

import org.microcloud.manager.core.model.datacenter.Host;
import org.microcloud.manager.core.placer.PlacementProblem;
import org.microcloud.manager.core.placer.solution.SolutionDestination;
import org.microcloud.manager.core.pricetimeapprox.SolutionPriceTimeApproximator;

public abstract class SolutionGraph<DestinationType> {

	protected List<SolutionSource> sources;
	protected List<SolutionConnection<DestinationType>> connections;
	protected List<SolutionDestination<DestinationType>> destinations;
	protected int sourceHostsNo;
	protected int keysNo;
	
	protected boolean structureConfirmed = false;

////////////////////////////////////////////
//// CONSTRUCTORS
////////////////////////////////////////////	
	
	protected SolutionGraph(List<SolutionSource> sources,
			List<SolutionDestination<DestinationType>> destinations,
			List<SolutionConnection<DestinationType>> connections,
			int sourceHostsNo, int keysNo) {
		this.sources = sources;
		this.connections = connections;
		this.destinations = destinations;
		this.sourceHostsNo = sourceHostsNo;
		this.keysNo = keysNo;
	}
	
	protected SolutionGraph(List<SolutionSource> sources,
			List<SolutionDestination<DestinationType>> destinations,
			boolean connectAll,
			int sourceHostsNo, int keysNo) {
		this.sources = sources;
		this.destinations = destinations;
		if(connectAll == true) {
			connectAll();
		}
		this.sourceHostsNo = sourceHostsNo;
		this.keysNo = keysNo;
	}
	
	protected SolutionGraph() {
		this.sources = new ArrayList<>();
		this.connections = new ArrayList<>();
		this.destinations = new ArrayList<>();
	}
	
////////////////////////////////////////////
//// PUBLIC METHODS
////////////////////////////////////////////
	
	/**
	 * Counts a solution properties for the case of specific client's query
	 * 
	 * @param pracerProblem
	 */
	public abstract void confirmAStructure(PlacementProblem placementProblem);
	
////////////////////////////////////////////
//// GETTERS AND SETTERS
////////////////////////////////////////////

	public List<SolutionSource> getSources() {
		return sources;
	}
	public void setSources(List<SolutionSource> sources) {
		if(!structureConfirmed) this.sources = sources;
	}
	public List<SolutionConnection<DestinationType>> getConnections() {
		return connections;
	}
	public void setConnections(List<SolutionConnection<DestinationType>> connections) {
		if(!structureConfirmed) this.connections = connections;
	}
	public List<SolutionDestination<DestinationType>> getDestinations() {
		return destinations;
	}
	public void setDestinations(List<SolutionDestination<DestinationType>> destinations) {
		if(!structureConfirmed) this.destinations = destinations;
	}
	
	public int getSourceHostsNo() {
		return sourceHostsNo;
	}

	public int getKeysNo() {
		return keysNo;
	}
	
////////////////////////////////////////////
//// "ADDERS"
////////////////////////////////////////////
	
	public void addASource(SolutionSource source) {
		if(!structureConfirmed) this.sources.add(source);
	}
	public void addAConnection(SolutionConnection<DestinationType> solutionConnection) {
		if(!structureConfirmed) this.connections.add(solutionConnection);
	}
	public void addADestination(SolutionDestination<DestinationType> solutionDestination) {
		if(!structureConfirmed) this.destinations.add(solutionDestination);
	}
	
////////////////////////////////////////////
//// OVERRIDEN METHODS
////////////////////////////////////////////
	
	@Override
	public String toString() {
		String ret = "";
		
		for(SolutionConnection<DestinationType> conn : this.connections) {
			ret += conn + " ( P: " + conn.getComprehensiveExecutionPrice() + 
					", T: " + conn.getTransfer() + "/" + conn.getSource().getKey().getKey().getSizeKB() + " ) " + "\n";
		}
		
		return ret;
	}
	
////////////////////////////////////////////
//// PRIVATE METHODS
////////////////////////////////////////////
	
	private void connectAll() {
		
		
		
	}
}
