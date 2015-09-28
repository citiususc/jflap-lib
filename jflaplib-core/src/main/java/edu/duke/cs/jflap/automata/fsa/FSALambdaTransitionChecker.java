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





package edu.duke.cs.jflap.automata.fsa;

import edu.duke.cs.jflap.automata.LambdaTransitionChecker;
import edu.duke.cs.jflap.automata.Transition;

/**
 * The fsa lambda transition checker can be used to determine if a finite state
 * automaton's transition is a lambda transition.
 * 
 * @author Ryan Cavalcante
 */

public class FSALambdaTransitionChecker extends LambdaTransitionChecker {
	/**
	 * Creates instance of <CODE>FSALambdaTransitionChecker</CODE>.
	 */
	public FSALambdaTransitionChecker() {
		super();
	}

	/**
	 * Returns true if <CODE>transition</CODE> is a lambda transition (i.e.
	 * it's label is the lambda string).
	 * 
	 * @param transition
	 *            the transition
	 * @return true if <CODE>transition</CODE> is a lambda transition (i.e.
	 *         it's label is the lambda string).
	 */
	public boolean isLambdaTransition(Transition transition) {
		FSATransition trans = (FSATransition) transition;
		if (trans.getLabel().equals(LAMBDA))
			return true;
		return false;
	}

}
