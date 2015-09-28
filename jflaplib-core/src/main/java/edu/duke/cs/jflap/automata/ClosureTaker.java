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
 * The closure taker object can be used to take the closure of states in an
 * automaton.
 * 
 * @author Ryan Cavalcante, Henry Qin
 */

public class ClosureTaker {
	/**
	 * There is no reason for this class to ever be constructed.
	 */
	private ClosureTaker() {

	}

	/**
	 * Returns the closure of <CODE>state</CODE>, that is, all states
	 * reachable from <CODE>state</CODE> without changing any internal state
	 * (e.g. stack, tape, input) via lambda transitions.
	 * 
	 * @param state
	 *            the state whose closure is being taken.
	 * @param automaton
	 *            the automaton
	 * @return the set of states that represent the closure of state.
	 */
	public static State[] getClosure(State state, Automaton automaton) {
		List list = new ArrayList();
		list.add(state);
		for (int i = 0; i < list.size(); i++) {
			state = (State) list.get(i);
			Transition transitions[] = automaton.getTransitionsFromState(state);
			for (int k = 0; k < transitions.length; k++) {
				Transition transition = transitions[k];
				LambdaTransitionChecker checker = LambdaCheckerFactory
						.getLambdaChecker(automaton);
				/** if lambda transition */
				if (checker.isLambdaTransition(transition)) {
					State toState = transition.getToState();
					if (!list.contains(toState)) {
						list.add(toState);
					}
				}
			}
		}
		return (State[]) list.toArray(new State[0]);
	}

}
