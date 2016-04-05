package org.microcloud.manager.core.placer.placement.Simplex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.optim.MaxIter;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.linear.LinearConstraint;
import org.apache.commons.math3.optim.linear.LinearConstraintSet;
import org.apache.commons.math3.optim.linear.LinearObjectiveFunction;
import org.apache.commons.math3.optim.linear.NonNegativeConstraint;
import org.apache.commons.math3.optim.linear.Relationship;
import org.apache.commons.math3.optim.linear.SimplexSolver;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.microcloud.manager.Factory;
import org.microcloud.manager.core.model.clientquery.ClientQuery;
import org.microcloud.manager.core.model.datacenter.MicroCloud;
import org.microcloud.manager.core.model.datasource.DataSourceKeysDistribution;
import org.microcloud.manager.core.placer.PlacementProblem;
import org.microcloud.manager.core.placer.placement.PlacementAlgorithm;
import org.microcloud.manager.core.placer.placement.Simplex.variations.hostspicker.DefaultWorkerHostsPicker;
import org.microcloud.manager.core.placer.placement.Simplex.variations.hostspicker.WorkerHostsPicker;
import org.microcloud.manager.core.placer.placement.Simplex.variations.linearconstraintsetter.KeysAwareLinearConstraintSetter;
import org.microcloud.manager.core.placer.placement.Simplex.variations.linearconstraintsetter.LinearContstraintSetter;
import org.microcloud.manager.core.placer.placement.Simplex.variations.workersnumber.DefaultWorkersNumber;
import org.microcloud.manager.core.placer.placement.Simplex.variations.workersnumber.WorkersNumber;
import org.microcloud.manager.core.placer.placement.Simplex.variations.workersnumberapprox.WorkersNumberApprox;
import org.microcloud.manager.core.placer.solution.SolutionBuilder;
import org.microcloud.manager.core.placer.solution.SolutionGraph;
import org.microcloud.manager.core.placer.solution.SolutionGraphDoneHost;
import org.microcloud.manager.core.placer.solution.SolutionGraphFullMC;
import org.microcloud.manager.core.placer.tools.StructuresBuilder;

public class SimplexPlacementAlgorithm extends PlacementAlgorithm {
	
	/* Variation points */
	LinearContstraintSetter constraintSetter;
	NewAnalyseConvertSimplexSolution analConv = null;
	
	
////////////////////////////////////////////
////CONSTRUCTORS
////////////////////////////////////////////

	public SimplexPlacementAlgorithm(Set<DataSourceKeysDistribution> keysHostsMapsSet,	ClientQuery clientQuery) {
		super(keysHostsMapsSet, clientQuery);
		
	}
	
	public void varySlots(
			LinearContstraintSetter constraintSetter) {
		this.constraintSetter = constraintSetter;
	}
	
////////////////////////////////////////////
////PUBLIC METHODS
////////////////////////////////////////////

	@Override
	protected void doRunAlgorithm() {
		
		Class<?>[] paramTypes = new Class<?>[]{PlacementProblem.class};
		Object[] params = new Object[]{placementProblem};
		this.constraintSetter = (LinearContstraintSetter) Factory.getInstance().newInstance("linearConstraintSetter",paramTypes,params);
		
		boolean runStepZero = false;
		boolean runStepOne = false;
		
		String simplexTestConfig = Factory.getInstance().getConstant("simplexTest");
		if(simplexTestConfig == null) {
			runStepZero = true;
			runStepOne = true;
		}
		else {
			if(simplexTestConfig.contains("StepZero"))
				runStepZero = true;
			if(simplexTestConfig.contains("StepOne"))
				runStepOne = true;
		}
		
		/* objects */
		List<SolutionGraphFullMC> fullInitGraphList = new ArrayList<>();
		SolutionGraphFullMC graphForSimplex;
		
		if(runStepZero) {
		
			/* 
			 * A. STEP ZERO
			 */
			org.microcloud.manager.logger.MyLogger.getInstance().log("Step Zero");	
			
			/* 0. Find possibilities of solving a problem in one MicroCloud */
			AllInOneMCAlgorithm stepZero = new AllInOneMCAlgorithm(placementProblem.getKeysHostsMapsSet());
			stepZero.run();
			fullInitGraphList.addAll(stepZero.getOutcome());
			
		}
		
		if(runStepOne) {
		
			/*
			 * B. STEP ONE
			 */
			org.microcloud.manager.logger.MyLogger.getInstance().log("Step One");			
			
			/* 1. Build a graph */
			graphForSimplex = StructuresBuilder.buildFullGraphForProblem(placementProblem);
			
			/* 2. Count for structure */
			graphForSimplex.confirmAStructure(placementProblem);
			
			/* 3. Prepare data for simplex */
			List<TotalConnectionExecution> totalConnExecList =
					graphForSimplex.getTotalConnectionExecutionList();
			int sourceHostsNo = graphForSimplex.getSourceHostsNo();
			int keysNo = graphForSimplex.getKeysNo();
			int sourcesNo = graphForSimplex.getSources().size();
			int destsNo = graphForSimplex.getDestinations().size();
			
			/* 4. SIMPLEX */
			boolean isSolutionFound = runSimplex(totalConnExecList,sourceHostsNo, keysNo, sourcesNo, destsNo);
			
			
			if(isSolutionFound) {
				fullInitGraphList.add(graphForSimplex);
				org.microcloud.manager.logger.MyLogger.getInstance().log(graphForSimplex);
			}
		
		}
		
		/*
		 * C. STEP TWO
		 */
		org.microcloud.manager.logger.MyLogger.getInstance().log("Step Two");	
		
		/* 5. ANALYSIS, <datacenter -> hosts> conversion, keys redefinition */
//		for(SolutionGraphFullMC fullInitGraph : fullInitGraphList) {
//			analConv = new AnalyseConvertSimplexSolution(placementProblem,fullInitGraph);
//			analConv.analyze();
//			
//			final SolutionGraphDoneHost doneHostsGraph = analConv.getConverted();
//			if(doneHostsGraph != null) {
//				/* 6. add to solution graphs and finish */
//				solutionGraphs.add(doneHostsGraph);
//			}
//		}
		
		for(SolutionGraphFullMC fullInitGraph : fullInitGraphList) {
			analConv = new NewAnalyseConvertSimplexSolution(placementProblem,fullInitGraph);
			analConv.analyze();
			
			final List<SolutionGraphDoneHost> doneHostsGraph = analConv.getConverted();
			if(doneHostsGraph != null) {
				/* 6. add to solution graphs and finish */
				solutionGraphs.addAll(doneHostsGraph);
			}
		}
		
	}
	
////////////////////////////////////////////
////PRIVATE METHODS
////////////////////////////////////////////	

	private boolean runSimplex(List<TotalConnectionExecution> totalConnExecList, int sourceHostsNo, int keysNo, int sourcesNo, int destsNo) {
		LinearObjectiveFunction f;
		LinearConstraintSet s;
		NonNegativeConstraint n;
		GoalType g;
		MaxIter x;
		
		/* LINEAR OBJECTIVE FUNCTION */
		List<Double> list = new ArrayList<Double>();
		for(TotalConnectionExecution t : totalConnExecList) {
			Double c = t.getC();
			list.add(c);
		}
		double[] doubleArray = ArrayUtils.toPrimitive(list.toArray(new Double[0]));
		org.microcloud.manager.logger.MyLogger.getInstance().log(Arrays.toString(doubleArray));
		f = new LinearObjectiveFunction(doubleArray, 0.0d);
		
		/* LINEAR CONSTRAINT SET */
		s = constraintSetter.create(totalConnExecList, keysNo, destsNo, sourceHostsNo);
		
		/* NON NEGATIVE CONSTRAINT */
		n = new NonNegativeConstraint(true);
		
		/* GOAL TYPE */
		g = GoalType.MINIMIZE;
		
		/* MAXIMUM NUMBER OF ITERATIONS */
		x = MaxIter.unlimited();
		
		//double epsilon = 1e-6;
		/* ALGORITHM RUN */
		SimplexSolver solver = new SimplexSolver();
		
		PointValuePair solution;
		
		try {
			solution = solver.optimize(f, s, n, g, x);
		} catch(org.apache.commons.math3.exception.MathIllegalStateException e) {
			org.microcloud.manager.logger.MyLogger.getInstance().log("\nThere is no feasible solution.");
			return false;
		}
		
		org.microcloud.manager.logger.MyLogger.getInstance().log(solution.getValue());
		
		/* SET TRANSFERS IN GRAPH BASED ON A FOUND SOLUTION */
		for(int i=0; i<totalConnExecList.size(); i++) {
			double transfer = solution.getPoint()[i];
			totalConnExecList.get(i).setTransfer(transfer);
		}
		
		return true;
			
	}

}
