package teamwork.linkpred_matrix;

import java.util.Date;

import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.SimplePointChecker;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.MultiStartMultivariateOptimizer;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunctionGradient;
import org.apache.commons.math3.optim.nonlinear.scalar.gradient.NonLinearConjugateGradientOptimizer;
import org.apache.commons.math3.random.GaussianRandomGenerator;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomVectorGenerator;
import org.apache.commons.math3.random.UncorrelatedRandomVectorGenerator;

public class LinkpredProblem {
	private LinkPrediction lp;
	private PointValuePair optimum;
	
	public LinkpredProblem (Graph [] graphs, int f, double alpha, double lambda, double b) {
		this.lp = new LinkPrediction(graphs, f, alpha, lambda, b);
	}

	public void optimize () {
		NonLinearConjugateGradientOptimizer opt = new NonLinearConjugateGradientOptimizer(
				NonLinearConjugateGradientOptimizer.Formula.POLAK_RIBIERE, 
				new SimplePointChecker<PointValuePair>(1.0e-5, 1.0e-4, 100));                           // create optimizer using Polak-Ribiere formula, and convergence 
		                                                                                                // after 10^-5 difference between iterations, or 10^-4 cost function, 
		                                                                                                // or maximum 100 iterations
		
		ObjectiveFunction func = new ObjectiveFunction(new LinkPredictionCost(lp));                     // Selecting our function as objective
		ObjectiveFunctionGradient grad = new ObjectiveFunctionGradient(new LinkPredictionGradient(lp)); // Selecting self-defined gradient as objective gradient
		
		JDKRandomGenerator rand = new JDKRandomGenerator();
		rand.setSeed(new Date().getTime());                                                              // creates random generator
		RandomVectorGenerator rvg = new UncorrelatedRandomVectorGenerator(
				lp.getParametersNumber(), new GaussianRandomGenerator(rand));                            // generates random vector of initial parameters
		
		MultiStartMultivariateOptimizer optimizer = 
				new MultiStartMultivariateOptimizer(opt, 50, rvg);                                      // creates multistart optimizer with 500 starting points
		
		
		System.out.println("And we are running ...");
		
		optimum = optimizer.optimize(
				func, GoalType.MINIMIZE, grad, new InitialGuess(rvg.nextVector()), new MaxEval(100));   // runs the optimization    
	}
	
	public PointValuePair getOptimum() {
		return optimum;
	}
	
}
