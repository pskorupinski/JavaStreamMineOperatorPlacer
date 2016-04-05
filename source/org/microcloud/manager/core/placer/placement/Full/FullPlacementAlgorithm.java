package org.microcloud.manager.core.placer.placement.Full;

import java.util.List;
import java.util.Set;

import org.microcloud.manager.core.model.clientquery.ClientQuery;
import org.microcloud.manager.core.model.datasourcedef.DataSourceKeysDistribution;
import org.microcloud.manager.core.placer.placement.PlacementAlgorithm;
import org.microcloud.manager.core.placer.placement.Full.steps.PlacementStep;
import org.microcloud.manager.core.placer.placement.Full.steps.PlacementStepZero;
import org.microcloud.manager.manager.core.model.solution.SolutionGraph;

public class FullPlacementAlgorithm extends PlacementAlgorithm {
	
	private boolean queryAnalysed = false;
	
////////////////////////////////////////////
//// CONSTRUCTORS
////////////////////////////////////////////
	
	public FullPlacementAlgorithm(Set<DataSourceKeysDistribution> keysHostsMapsSet, ClientQuery clientQuery) {
		super(keysHostsMapsSet,clientQuery);
	}
	
////////////////////////////////////////////
//// PUBLIC METHODS
////////////////////////////////////////////
	
	public boolean runAlgorithm() {
		
		if( !runStepZero() )
			runStepOne();
		
		runStepTwo();
		
		return true;
	}
	
	/**
	 * Implements the zero step of algorithm. It checks whether there are micro-clouds
	 * that contain all the keys. It is not obligatory step but recommended when there is some probability
	 * of such a situation.
	 * 
	 * @return true - if found micro-clouds with all the nodes, false - if not
	 */
	public boolean runStepZero() {
		
		PlacementStep step = new PlacementStepZero(keysHostsMapsSet);
		step.run();
		Object solution = step.getOutcome();
		
		if(solution == null) {
			return false;
		}
		else {
			solutionGraphs = (List<SolutionGraph>) solution;
			return true;
		}
	}
	
	public boolean runStepOne() {
		
		
		
		return true;
	}
	
	public boolean runStepTwo() {
		
		
		return true;
	}
	
////////////////////////////////////////////
//// PRIVATE METHODS
////////////////////////////////////////////
	
	private boolean analyzeQuery() {
		
		
		
		// analyze time, price, algorithm, is it real time data / historical data
		
		return false;
	}
	

}
