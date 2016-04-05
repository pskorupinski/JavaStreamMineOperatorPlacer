package org.microcloud.manager.core.placer.placement.Simplex.variations.linearconstraintsetter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.optim.linear.LinearConstraint;
import org.apache.commons.math3.optim.linear.LinearConstraintSet;
import org.apache.commons.math3.optim.linear.Relationship;
import org.microcloud.manager.Factory;
import org.microcloud.manager.core.model.datacenter.MicroCloud;
import org.microcloud.manager.core.placer.PlacementProblem;
import org.microcloud.manager.core.placer.placement.Simplex.TotalConnectionExecution;
import org.microcloud.manager.core.placer.placement.Simplex.variations.VariationImplCore;
import org.microcloud.manager.core.placer.placement.Simplex.variations.retrievallimit.RetrievalLimit;
import org.microcloud.manager.core.placer.placement.Simplex.variations.workersnumberapprox.WorkersNumberApprox;

public class KeysDestsAwareLinearConstraintSetter extends VariationImplCore implements LinearContstraintSetter {
	
	WorkersNumberApprox workersNumberApprox;

	public KeysDestsAwareLinearConstraintSetter(PlacementProblem placementProblem) {
		super(placementProblem);
		
		Class<?>[] paramTypes = new Class<?>[]{PlacementProblem.class};
		Object[] params = new Object[]{placementProblem};		
		this.workersNumberApprox = (WorkersNumberApprox) Factory.getInstance().newInstance("workersNumberApprox",paramTypes,params);
	}

	@Override
	public LinearConstraintSet create(List<TotalConnectionExecution> totalConnExecList, int... args) {
		int keysNo = args[0];
		int destsNo = args[1];
		int srcHostsNo = args[2];
		int workersNum = workersNumberApprox.count();
		
		ArrayList<LinearConstraint> constraints = new ArrayList<LinearConstraint>();

		///////////////////////////////////////////////////			
		
		/* 
		 * KEYS SIZES LIMITS
		 *
		 * "sources of a key produce size of key data" constraint
		 */
		
		org.microcloud.manager.logger.MyLogger.getInstance().log("'sources of a key produce size of key data' constraint");		
				
		double [] keySizes = new double[keysNo];
		double [][] hostsForKey = new double[keysNo][totalConnExecList.size()];
		int connNum=0;
		for(TotalConnectionExecution conn : totalConnExecList) {
			int keyNo = conn.getK().getOrderNumber();
			
			/* if not already defined, define a size of that key */
			if(keySizes[keyNo] == 0.0d) keySizes[keyNo] = conn.getK().getKey().getSizeKB();
			
			/* this host will be in an equation for that key 
			 * - "sources of a key produce size of key data" constraint */
			hostsForKey[keyNo][connNum] = 1;			
			
			connNum++;
		}
		
		for(int i=0; i<keysNo; i++) {
			LinearConstraint linearContraint = 
					new LinearConstraint(hostsForKey[i], Relationship.EQ, keySizes[i]);
			org.microcloud.manager.logger.MyLogger.getInstance().log(
					Arrays.toString(linearContraint.getCoefficients().toArray()) + 
							" === " + 
							linearContraint.getValue());
			constraints.add(linearContraint);
		}
		
		///////////////////////////////////////////////////		
		
		/* 
		 * DESTINATIONS PROCESSING LIMITS
		 * 
		 *  "limit of microcloud data processing bandwidth" constraint
		 */
		
		org.microcloud.manager.logger.MyLogger.getInstance().log("'limit of microcloud data processing bandwidth' constraint");		

		double keySizesSum = 0.0;
		for(double d : keySizes) keySizesSum += d;		

		double [] destSizes = new double[destsNo];
		double [][] hostsForDest = new double[destsNo][totalConnExecList.size()];
		connNum=0;
		for(TotalConnectionExecution conn : totalConnExecList) {	
			/*
			 * 
			 */
			int destNo = conn.getD().getOrderNumber();
			
			/* if not already defined, define a max transfer to this microcloud */
			if(destSizes[destNo] == 0.0d) {

				int freeHosts = conn.getD().getSolutionMicroCloud().getFreeHostsNumber(placementProblem);
				
				double limit = keySizesSum * ((double)freeHosts / (double)workersNum);
				destSizes[destNo] = limit;	
			}
			
			/* this host will be in an equation for that destination 
			 * - "limit of microcloud data processing bandwidth" constraint */
			hostsForDest[destNo][connNum] = 1;	
			
			connNum++;
		}		
		
		for(int i=0; i<destsNo; i++) {
			LinearConstraint linearContraint = 
					new LinearConstraint(hostsForDest[i], Relationship.LEQ, destSizes[i]);
			org.microcloud.manager.logger.MyLogger.getInstance().log(
					Arrays.toString(linearContraint.getCoefficients().toArray()) + 
							" <== " + 
							linearContraint.getValue());
			constraints.add(linearContraint);
		}
		
		//////////////////////////////////////////////////
		
		/*
		 * TODO if transfer is set for a connection, define it here
		 */
		
		///////////////////////////////////////////////////		
		
		
		return new LinearConstraintSet(constraints);
	}

}
