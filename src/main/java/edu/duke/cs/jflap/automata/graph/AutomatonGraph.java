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

import edu.duke.cs.jflap.automata.Automaton;
import edu.duke.cs.jflap.automata.State;
import edu.duke.cs.jflap.automata.Transition;
import java.awt.Point;
import java.awt.geom.Point2D;

/**
 * This extension of the graph makes it easier for a graph to be built from an
 * automaton. The vertex objects used are the states themselves. The analogy to
 * an edge is, naturally, transitions. This class of graph has a method to, once
 * graph vertices are moved around, to synchronize the locations of the
 * automaton states to the positions of the graph nodes, thus making graph
 * layout algorithms simpler to apply.
 * 
 * @author Thomas Finley
 */

public class AutomatonGraph extends Graph {
	/**
	 * Constructures a graph using an automaton.
	 * 
	 * @param automaton
	 *            the automaton to build the graph from
	 */
	public AutomatonGraph(Automaton automaton) {
		super();
		State[] states = automaton.getStates();
		Transition[] transitions = automaton.getTransitions();
		for (int i = 0; i < states.length; i++)
			addVertex(states[i], states[i].getPoint());
		for (int i = 0; i < transitions.length; i++)
			addEdge(transitions[i].getFromState(), transitions[i].getToState());
	}

	/**
	 * Moves the states of the underlying automaton to synchronize with the
	 * positions of the corresponding vertices in the graph.
	 */
	public void moveAutomatonStates() {
		Object[] vertices = vertices();
		for (int i = 0; i < vertices.length; i++) {
			State state = (State) vertices[i];
			Point2D point = pointForVertex(state);
			state.setPoint(new Point((int) point.getX(), (int) point.getY()));
		}
	}
}
