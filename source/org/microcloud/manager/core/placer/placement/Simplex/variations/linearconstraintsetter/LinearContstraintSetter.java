package org.microcloud.manager.core.placer.placement.Simplex.variations.linearconstraintsetter;

import java.util.List;

import org.apache.commons.math3.optim.linear.LinearConstraintSet;
import org.microcloud.manager.core.placer.placement.Simplex.TotalConnectionExecution;

public interface LinearContstraintSetter {

	LinearConstraintSet create(List<TotalConnectionExecution> totalConnExecList, int... args);
	
}
