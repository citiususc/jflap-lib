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

/**
 * The lambda transition checker can be used to determine if a given transition
 * is a lambda transition.
 * 
 * @author Ryan Cavalcante
 */

public abstract class LambdaTransitionChecker {
	/**
	 * Creates a <CODE>LambdaTransitionChecker</CODE>.
	 */
	public LambdaTransitionChecker() {

	}

	/**
	 * Returns true if <CODE>transition</CODE> is a lambda transition.
	 * 
	 * @param transition
	 *            the transition.
	 * @return true if <CODE>transition</CODE> is a lambda transition.
	 */
	public abstract boolean isLambdaTransition(Transition transition);

	/** The lambda string. */
	protected String LAMBDA = "";

	/** The stay string. */
	protected String STAY = "S";

}
