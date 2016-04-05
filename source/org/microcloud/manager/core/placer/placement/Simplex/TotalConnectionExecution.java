package org.microcloud.manager.core.placer.placement.Simplex;

import org.microcloud.manager.core.model.datacenter.MicroCloud;
import org.microcloud.manager.core.placer.PlacementProblem;
import org.microcloud.manager.core.placer.solution.SolutionConnection;
import org.microcloud.manager.core.placer.solution.SolutionDestination;
import org.microcloud.manager.core.placer.solution.SolutionKey;
import org.microcloud.manager.core.placer.solution.SolutionSource;
import org.microcloud.manager.core.placer.solution.SolutionSourceHost;

public class TotalConnectionExecution {
	private SolutionConnection<MicroCloud> solutionConnection;
	private PlacementProblem placementProblem;
	private Double price = null;
	private int no;
	
	public TotalConnectionExecution(
			int no,
			SolutionConnection<MicroCloud> solutionConnection,
			PlacementProblem placementProblem) {
		this.solutionConnection = solutionConnection;
		this.placementProblem = placementProblem;
		this.no = no;
	}
	
	/**
	 * Template method.
	 * 
	 * Gets transfer cost C, as it is defined in transportation problem.
	 * 
	 * @return
	 */
	public final double getC() {
		if(this.price == null)
			countPrice();
		
		double keySizeKB = getK().getKey().getSizeKB();
		double c = this.price / keySizeKB;
		
		return c;
	}
	
	public SolutionKey getK() {
		return this.solutionConnection.getSource().getKey();
	}
	
	public SolutionSourceHost getH() {
		return this.solutionConnection.getSource().getHost();
	}
	
	public SolutionSource getS() {
		return this.solutionConnection.getSource();
	}
	
	public SolutionDestination<?> getD() {
		return this.solutionConnection.getDestination();
	}
	
	public void setTransfer(double transfer) {
		solutionConnection.setTransfer(transfer);
	}
	
	public double getTransfer() {
		return this.solutionConnection.getTransfer();
	}
	
	public int getNo() {
		return no;
	}
	
	/**
	 * Hook method
	 * 
	 */
	protected void countPrice() {
		
		this.price = 0.0;
		
		/*
		 * (I) SOLUTION SOURCE
		 */
		{
			this.price += solutionConnection.countSourcePriceForConnection(placementProblem);
		}
		
		/*
		 * (II) SOLUTION CONNECTION
		 */
		{
			solutionConnection.countPossiblePrice(placementProblem);
			this.price += solutionConnection.getComprehensiveExecutionPrice();
		}
		
		/*
		 * (III) SOLUTION DESTINATION
		 */
		{
			this.price += solutionConnection.countDestinationPriceForConnection(placementProblem);
		}
		
	}
	
	@Override
	public String toString() {
		return this.solutionConnection + 
				" ( P: " + this.price + 
				", T: " + this.solutionConnection.getTransfer() +
				"/" + this.solutionConnection.getSource().getKey().getKey().getSizeKB() + 
				" ) ";
}
}
