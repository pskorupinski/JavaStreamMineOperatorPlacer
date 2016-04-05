package org.microcloud.manager.core.placer.placement.Simplex.variations.linearconstraintsetter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.optim.linear.LinearConstraint;
import org.apache.commons.math3.optim.linear.LinearConstraintSet;
import org.apache.commons.math3.optim.linear.Relationship;
import org.microcloud.manager.core.placer.PlacementProblem;
import org.microcloud.manager.core.placer.placement.Simplex.TotalConnectionExecution;
import org.microcloud.manager.core.placer.placement.Simplex.variations.VariationImplCore;

public class KeysAwareLinearConstraintSetter extends VariationImplCore implements LinearContstraintSetter {

	public KeysAwareLinearConstraintSetter(PlacementProblem placementProblem) {
		super(placementProblem);
		// TODO Auto-generated constructor stub
	}

	@Override
	public LinearConstraintSet create(List<TotalConnectionExecution> totalConnExecList, int... args) {
		int keysNo = args[0];

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
			 * - "sources of key produce size of key data" constraint 
			 */
			hostsForKey[keyNo][connNum] = 1;
			
			/*
			 * TODO if transfer is set for a connection, define it here
			 */
			
			
			connNum++;
		}		
		ArrayList<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
		for(int i=0; i<keysNo; i++) {
			LinearConstraint linearContraint = 
					new LinearConstraint(hostsForKey[i], Relationship.EQ, keySizes[i]);
			org.microcloud.manager.logger.MyLogger.getInstance().log(
					Arrays.toString(linearContraint.getCoefficients().toArray()) + 
							" --- " + 
							linearContraint.getValue());
			constraints.add(linearContraint);
		}

		///////////////////////////////////////////////////	
		
		return new LinearConstraintSet(constraints);
	}

}
