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

public class KeysTransfersAwareLinearConstraintSetter extends VariationImplCore implements LinearContstraintSetter {

	public KeysTransfersAwareLinearConstraintSetter(PlacementProblem placementProblem) {
		super(placementProblem);
		// TODO Auto-generated constructor stub
	}

	@Override
	public LinearConstraintSet create(List<TotalConnectionExecution> totalConnExecList, int... args) {
		int keysNo = args[0];
		int destsNo = args[1];
		
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
			
			hostsForKey[keyNo][connNum] = 1;
	
			connNum++;
		}		
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
		
		/* 
		 * EQUAL TRANSFERS
		 *
		 * "equal transfers of every key between destinations" constraint
		 */				
		
		org.microcloud.manager.logger.MyLogger.getInstance().log("'equal transfers of every key between destinations' constraint");

		double [][][] equalTransfersConstraintsArray = new double[keysNo-1][destsNo][totalConnExecList.size()];
		connNum = 0;
		for(TotalConnectionExecution conn : totalConnExecList) {
			int keyNo = conn.getK().getOrderNumber();
			int destNo = conn.getD().getOrderNumber();
			
			if(keyNo == 0) {
				for(double [][] daa : equalTransfersConstraintsArray) {
					daa[destNo][connNum] = 1;
				}
			}
			else {
				equalTransfersConstraintsArray[keyNo-1][destNo][connNum] = - (keySizes[0]/keySizes[keyNo]);
			}
	
			connNum++;
		}		
		for(int i=0; i<keysNo-1; i++) {
			for(int j=0; j<destsNo; j++) {
				LinearConstraint linearContraint = 
						new LinearConstraint(equalTransfersConstraintsArray[i][j], Relationship.EQ, 0);
				org.microcloud.manager.logger.MyLogger.getInstance().log(
						Arrays.toString(linearContraint.getCoefficients().toArray()) + 
								" --- " + 
								linearContraint.getValue());
				constraints.add(linearContraint);
			}
		}

		///////////////////////////////////////////////////	
		
		return new LinearConstraintSet(constraints);
	}

}
