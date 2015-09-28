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





package edu.duke.cs.jflap.automata;

import java.util.*;

/**
 * The unreachable states detector object can be used to find all unreachable
 * states in an automaton (i.e. all states for which there exists no path from
 * the initial state to that state).
 * 
 * @author Ryan Cavalcante
 */

public class UnreachableStatesDetector {
	/**
	 * Creates an instance of <CODE>UnreachableStatesDetector</CODE>
	 * 
	 * @param automaton
	 *            the automaton to search for unreachable states.
	 */
	public UnreachableStatesDetector(Automaton automaton) {
		myAutomaton = automaton;
	}

	/**
	 * Initializes all nodes for DFS by creating a Node object for each State in
	 * <CODE>states</CODE> and coloring them all white.
	 * 
	 * @param states
	 *            the set of states to create Nodes for.
	 */
	public void initializeNodes(State[] states) {
		myNodes = new Node[states.length];
		/** Color all vertices white. */
		for (int k = 0; k < states.length; k++) {
			Node node = new Node(states[k]);
			node.colorWhite();
			myNodes[k] = node;
		}
	}

	/**
	 * Returns all states in automaton that are unreachable from the initial
	 * state. This method implements the standard depth first search algorithm
	 * for directed graphs.
	 * 
	 * @return all states in the automaton that are unreachable from the initial
	 *         state.
	 */
	public State[] getUnreachableStates() {
		ArrayList list = new ArrayList();
		State[] states = myAutomaton.getStates();
		/** Create nodes for DFS. */
		initializeNodes(states);

		Node initialNode = getNodeForState(myAutomaton.getInitialState());
		/** Start DFS at node representing initial state. */
		visit(initialNode);

		/**
		 * DFS has completed. Add all non-visited (white) nodes to list of
		 * unreachable states.
		 */
		for (int k = 0; k < myNodes.length; k++) {
			if (myNodes[k].isWhite()) {
				list.add(myNodes[k].getState());
			}
		}
		return (State[]) list.toArray(new State[0]);
	}

	/**
	 * Returns Node object that contains <CODE>state</CODE>.
	 * 
	 * @param state
	 *            the state
	 * @return Node object that contains <CODE>state</CODE>.
	 */
	public Node getNodeForState(State state) {
		/** Search through all nodes for state. */
		for (int k = 0; k < myNodes.length; k++) {
			Node node = myNodes[k];
			if (node.getState() == state)
				return node;
		}
		return null;
	}

	/**
	 * The visit method from the standard DFS algorithm for directed graphs. A
	 * recursive function which visits all neighbors of <CODE>node</CODE>
	 * (i.e. all states reachable by transitions out of <CODE>node</CODE>),
	 * and then visits the neighbors of all those nodes and so on. In the end,
	 * all nodes reachable from <CODE>node</CODE> will have been visited and
	 * colored black.
	 */
	public void visit(Node node) {
		node.colorGrey();
		Transition[] transitions = myAutomaton.getTransitionsFromState(node
				.getState());
		for (int k = 0; k < transitions.length; k++) {
			Transition transition = transitions[k];
			State toState = transition.getToState();
			Node v = getNodeForState(toState);
			if (v.isWhite()) {
				visit(v);
			}
		}
		node.colorBlack();
	}

	/** The automaton. */
	protected Automaton myAutomaton;

	/** Set of nodes for dfs. */
	protected Node[] myNodes;
}
