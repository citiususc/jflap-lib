/*
 *  JFLAP - Formal Languages and Automata Package
 * 
 * 
 *  Susan H. Rodger
 *  Computer Science Department
 *  Duke University
 *  August 27, 2009

 *  Copyright (c) 2002-2009
 *  All rights reserved.

 *  JFLAP is open source software. Please see the LICENSE for terms.
 *
 */




package edu.duke.cs.jflap.automata.graph;

import java.awt.Dimension;
import java.util.Random;
import edu.duke.cs.jflap.automata.Automaton;
import edu.duke.cs.jflap.automata.graph.layout.*;

/**
 * This class allows the user to fetch a new <code>LayoutAlgorithm</code>, either a random one or a 
 * specific one.  It also allows the user to fetch a particular <code>AutomatonGraph</code> that
 * corresponds to a given <code>LayoutAlgorithm</code>.
 * 
 * @author Chris Morgan
 */
public class LayoutAlgorithmFactory {
	/**
	 * Value that represents the number of <code>LayoutAlgorithms</code> of which this factory currently has 
	 * knowledge.  Used to generate a random <code>LayoutAlgorithm</code>, so if changed, one should make
	 * sure all integers from 0 to <i>NUM_ALGORITHMS</i>-1 are numerical identifiers for 
	 * <code>LayoutAlgorithms</code>. 
	 */
	private static final int NUM_ALGORITHMS = 7;
	/**
	 * Numerical identifier for choosing a random </code>LayoutAlgorithm</code> instance.
	 */
	public static final int RANDOM_CHOICE = -1;
	/**
	 * Numerical identifier for a <code>CircleLayoutAlgorithm</code> instance.
	 */
	public static final int CIRCLE = 0;
	/**
	 * Numerical identifier for a <code>GEMLayoutAlgorithm</code> instance.
	 */
	public static final int GEM = 1;
	/**
	 * Numerical identifier for a <code>RandomLayoutAlgorithm</code> instance.
	 */
	public static final int RANDOM = 2;
	/**
	 * Numerical identifier for a <code>SpiralLayoutAlgorithm</code> instance.
	 */
	public static final int SPIRAL = 3;
	/**
	 * Numerical identifier for a <code>TreeLayoutAlgorithm</code> instance with a hierarchical tree.
	 */
	public static final int TREE_HIERARCHY = 4;
	/**
	 * Numerical identifier for a <code>TreeLayoutAlgorithm</code> instance with a degree tree.
	 */
	public static final int TREE_DEGREE = 5;	
	/**
	 * Numerical identifier for a <code>TwoCircleLayoutAlgorithm</code> instance.
	 */
	public static final int TWO_CIRCLE = 6;

	/**
	 * Returns a random <code>LayoutAlgorithm</code> among those defined.  Should not be mistaken for an
	 * algorithm always creating a <code>RandomLayoutAlgorithm</code> instance.
	 * 
	 * @return A random <code>LayoutAlgorithm</code> among those defined.
	 */
	public static LayoutAlgorithm getRandomLayoutAlgorithm() {
		Random random = new Random();
		return getLayoutAlgorithm(Math.abs(random.nextInt() % NUM_ALGORITHMS));
	}
	
	/**
	 * Returns a random <code>LayoutAlgorithm</code> among those defined.  Should not be mistaken for an 
	 * algorithm always creating a <code>RandomLayoutAlgorithm</code> instance.
	 * 
	 * @param pSize value for <code>size</code>.
	 * @param vDim value for <code>vertexDim</code>.
	 * @param vBuffer value for <code>vertexBuffer</code>.
	 * @return A random <code>LayoutAlgorithm</code> among those defined.
	 */
	public static LayoutAlgorithm getRandomLayoutAlgorithm(Dimension pSize, Dimension vDim, double vBuffer) {
		Random random = new Random();
		return getLayoutAlgorithm(Math.abs(random.nextInt() % NUM_ALGORITHMS), pSize, vDim, vBuffer);
	}
	
	/**
	 * Returns a <code>LayoutAlgorithm</code> corresponding to the numerical identifier given.
	 * 
	 * @param algorithm a numerical identifier for the specific layout algorithm that should be generated.
	 * @return A layout algorithm corresponding to the <code>algorithm</code> value.
	 */
	public static LayoutAlgorithm getLayoutAlgorithm(int algorithm) {	
		switch (algorithm) {
		    case RANDOM_CHOICE: return getRandomLayoutAlgorithm();
		    case CIRCLE: return new CircleLayoutAlgorithm();
			case GEM: return new GEMLayoutAlgorithm();
			case RANDOM: return new RandomLayoutAlgorithm();
			case SPIRAL: return new SpiralLayoutAlgorithm();			
			case TREE_DEGREE: return new TreeLayoutAlgorithm(false);
			case TREE_HIERARCHY: return new TreeLayoutAlgorithm(true);
			case TWO_CIRCLE: return new TwoCircleLayoutAlgorithm();
			case VertexMover.NEGATIVE_SLOPE_DIAGONAL: return new VertexMover(VertexMover.NEGATIVE_SLOPE_DIAGONAL);
			case VertexMover.POSITIVE_SLOPE_DIAGONAL: return new VertexMover(VertexMover.POSITIVE_SLOPE_DIAGONAL);
			case VertexMover.ROTATE: return new VertexMover(VertexMover.ROTATE);
			case VertexMover.HORIZONTAL_CENTER: return new VertexMover(VertexMover.HORIZONTAL_CENTER);
			case VertexMover.VERTICAL_CENTER: return new VertexMover(VertexMover.VERTICAL_CENTER);
			case VertexMover.FILL: return new VertexMover(VertexMover.FILL);
		}
		return null;
	}
	
	/**
	 * Returns a <code>LayoutAlgorithm</code> corresponding to the numerical identifier given.
	 * 
	 * @param algorithm a numerical identifier for the specific layout algorithm that should be generated.
	 * @param pSize value for <code>size</code>.
	 * @param vDim value for <code>vertexDim</code>.
	 * @param vBuffer value for <code>vertexBuffer</code>.
	 * @return A layout algorithm corresponding to the <code>algorithm</code> value.
	 */
	public static LayoutAlgorithm getLayoutAlgorithm(int algorithm, Dimension pSize, Dimension vDim, double vBuffer) {
		switch (algorithm) {
			case RANDOM_CHOICE: return getRandomLayoutAlgorithm(pSize, vDim, vBuffer);
			case CIRCLE: return new CircleLayoutAlgorithm(pSize, vDim, vBuffer);
			case GEM: return new GEMLayoutAlgorithm(pSize, vDim, vBuffer);
			case RANDOM: return new RandomLayoutAlgorithm(pSize, vDim, vBuffer);
			case SPIRAL: return new SpiralLayoutAlgorithm(pSize, vDim, vBuffer);
			case TREE_DEGREE: return new TreeLayoutAlgorithm(pSize, vDim, vBuffer, false);
			case TREE_HIERARCHY: return new TreeLayoutAlgorithm(pSize, vDim, vBuffer, true);
			case TWO_CIRCLE: return new TwoCircleLayoutAlgorithm(pSize, vDim, vBuffer);
			case VertexMover.NEGATIVE_SLOPE_DIAGONAL: 
					return new VertexMover(pSize, vDim, vBuffer, VertexMover.NEGATIVE_SLOPE_DIAGONAL);
			case VertexMover.POSITIVE_SLOPE_DIAGONAL: 
					return new VertexMover(pSize, vDim, vBuffer, VertexMover.POSITIVE_SLOPE_DIAGONAL);
			case VertexMover.ROTATE: return new VertexMover(pSize, vDim, vBuffer, VertexMover.ROTATE);
			case VertexMover.HORIZONTAL_CENTER: 
					return new VertexMover(pSize, vDim, vBuffer, VertexMover.HORIZONTAL_CENTER);
			case VertexMover.VERTICAL_CENTER: 
					return new VertexMover(pSize, vDim, vBuffer, VertexMover.VERTICAL_CENTER);
			case VertexMover.FILL: return new VertexMover(pSize, vDim, vBuffer, VertexMover.FILL);
		}
		return null;		 
	}
	
	/**
	 * Returns the correct <code>AutomatonGraph</code> that corresponds with this layout algorithm.
	 * 
	 * @param algorithm a numerical identifier for the specific layout algorithm which will use
	 * the graph.
	 * @param automaton the automaton that will be used to generate the graph.
	 * @return the correct <code>AutomatonGraph</code>.
	 */
	public static AutomatonGraph getAutomatonGraph(int algorithm, Automaton automaton) {
		if (algorithm == TREE_HIERARCHY)
			return new AutomatonDirectedGraph(automaton);
		else
			return new AutomatonGraph(automaton);
	}
}
