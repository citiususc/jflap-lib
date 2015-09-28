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





package edu.duke.cs.jflap.automata.pda;

import edu.duke.cs.jflap.automata.LambdaTransitionChecker;
import edu.duke.cs.jflap.automata.Transition;

/**
 * The pda lambda transition checker can be used to determine if a pushdown
 * automaton's transition is a lambda transition
 * 
 * @author Ryan Cavalcante
 */

public class PDALambdaTransitionChecker extends LambdaTransitionChecker {
	/**
	 * Creates a <CODE>PDALambdaTransitionChecker</CODE>
	 */
	public PDALambdaTransitionChecker() {
		super();
	}

	/**
	 * Returns true if <CODE>transition</CODE> is a lambda transition (i.e.
	 * all three of its fields are the lambda string).
	 * 
	 * @param transition
	 *            the transition
	 * @return true if <CODE>transition</CODE> is a lambda transition (i.e.
	 *         all three of its fields are the lambda string).
	 */
	public boolean isLambdaTransition(Transition transition) {
		PDATransition trans = (PDATransition) transition;
		String input = trans.getInputToRead();
		String toPop = trans.getStringToPop();
		String toPush = trans.getStringToPush();
		if (input.equals(LAMBDA) && toPop.equals(LAMBDA)
				&& toPush.equals(LAMBDA))
			return true;
		return false;
	}

}
