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
 * The nondeterminism detector object can be used to find all the
 * nondeterministic states in an automaton (i.e. all states with equal outward
 * transitions).
 * 
 * @author Ryan Cavalcante
 */

public abstract class NondeterminismDetector {
	/**
	 * Creates an instance of <CODE>NondeterminismDetector</CODE>
	 */
	public NondeterminismDetector() {
	}

	/**
	 * Returns true if the transitions are identical (i.e. all components of the
	 * label are equivalent) or if the transitions introduce nondeterminism
	 * 
	 * @param t1
	 *            a transition
	 * @param t2
	 *            a transition
	 * @return true if the transitions are nondeterministic
	 */
	public abstract boolean areNondeterministic(Transition t1, Transition t2);

	/**
	 * Returns an array of states that have nondeterminism.
	 * 
	 * @return an array of states that have nondeterminism.
	 */

	public State[] getNondeterministicStates(Automaton automaton) {
		LambdaTransitionChecker lc = LambdaCheckerFactory
				.getLambdaChecker(automaton);
		ArrayList list = new ArrayList();
		/* Get all states in automaton. */
		State[] states = automaton.getStates();
		/* Check each state for nondeterminism. */
		for (int k = 0; k < states.length; k++) {
			State state = states[k];
			/* Get all transitions from each state. */
			Transition[] transitions = automaton.getTransitionsFromState(state);

			for (int i = 0; i < transitions.length; i++) {
				Transition t1 = transitions[i];
				/* if is lambda transition. */
				if (lc.isLambdaTransition(t1)) {
					if (!list.contains(state))
						list.add(state);
				}
				/*
				 * Check all transitions against all other transitions to see if
				 * any are equal.
				 */
				else {
					for (int p = (i + 1); p < transitions.length; p++) {
						Transition t2 = transitions[p];
						if (areNondeterministic(t1, t2)) {
							if (!list.contains(state))
								list.add(state);
						}
					}
				}
			}
		}
		return (State[]) list.toArray(new State[0]);
	}

}
