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





package edu.duke.cs.jflap.automata.turing;

import edu.duke.cs.jflap.automata.LambdaTransitionChecker;
import edu.duke.cs.jflap.automata.Transition;

/**
 * The tm lambda transition checker can be used to check if a one-tape Turing
 * machine's transition is a lambda transition
 * 
 * @author Ryan Cavalcante
 */

public class TMLambdaTransitionChecker extends LambdaTransitionChecker {
	/**
	 * Returns true if <CODE>transition</CODE> is a lambda transition
	 * 
	 * @param transition
	 *            the transition
	 * @return true if <CODE>transition</CODE> is a lambda transition
	 */
	public boolean isLambdaTransition(Transition transition) {
		return false;
	}

}
