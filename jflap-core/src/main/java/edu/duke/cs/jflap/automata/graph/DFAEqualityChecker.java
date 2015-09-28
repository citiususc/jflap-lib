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

import java.util.*;
import edu.duke.cs.jflap.automata.*;
import edu.duke.cs.jflap.automata.fsa.*;

/**
 * This is an object that checks if two deterministic finite state automatons
 * are equal, that is, you could rearrange the states in one and (except for
 * state names) have exactly the same automaton looking back at you from the
 * screen. This does not compare two DFAs to see if they accept the same
 * language!
 * 
 * @author Thomas Finley
 */

public class DFAEqualityChecker {
	/**
	 * "Hypothesize" that two states are equal in the graph isomorphism for this
	 * automaton. This function tests to see if that assumption is justifiable.
	 * 
	 * @param state1
	 *            the state in the first automaton
	 * @param state2
	 *            the state in the second automaton
	 * @param matching
	 *            the matchings of states in the first automaton to states in
	 *            the second automaton
	 */
	private boolean hypothesize(State state1, State state2, Map matching) {
		{
			// Does state one already have a counterpart?
			State counterpart = (State) matching.get(state1);
			// If it does, is it the same?
			if (counterpart != null)
				return counterpart == state2;
			// We haven't visited this node yet.
			// Does "finality" match up?
			if (state1.getAutomaton().isFinalState(state1)
					^ state2.getAutomaton().isFinalState(state2))
				return false;
		}

		Map labelToTrans1 = new HashMap(), labelToTrans2 = new HashMap();
		Transition[] t1 = state1.getAutomaton().getTransitionsFromState(state1);
		Transition[] t2 = state2.getAutomaton().getTransitionsFromState(state2);
		// If they're not even the same length...
		if (t1.length != t2.length)
			return false;
		for (int i = 0; i < t1.length; i++) {
			labelToTrans1.put(((FSATransition) t1[i]).getLabel(), t1[i]);
			labelToTrans2.put(((FSATransition) t2[i]).getLabel(), t2[i]);
		}
		// Now, for each transition from state1, we can find the
		// corresponding transition in state2, if it exists.
		for (int i = 0; i < t1.length; i++) {
			String label = ((FSATransition) t1[i]).getLabel();
			Transition counterpart = (Transition) labelToTrans2.get(label);
			// Does the same transition exist in the other automaton?
			if (counterpart == null)
				return false;
			matching.put(state1, state2);
			boolean equal = hypothesize(t1[i].getToState(), counterpart
					.getToState(), matching);
			if (!equal) {
				matching.remove(state1);
				return false;
			}
		}
		return true;
	}

	/**
	 * Compares two DFAs for equality. The precondition is that these objects be
	 * instances of <CODE>FiniteStateAutomaton</CODE>, both are
	 * deterministic, and both have an initial state. Results are undefined
	 * otherwise.
	 * 
	 * @param one
	 *            the first dfa
	 * @param two
	 *            the second dfa
	 * @return <CODE>true</CODE> if the two DFAs are equal, <CODE>false</CODE>
	 *         if they are not
	 */
	public boolean equals(FiniteStateAutomaton one, FiniteStateAutomaton two) {
		// Make sure they have the same number of states.
		if (one.getStates().length != two.getStates().length)
			return false;
		return hypothesize(one.getInitialState(), two.getInitialState(),
				new HashMap());
	}
}
