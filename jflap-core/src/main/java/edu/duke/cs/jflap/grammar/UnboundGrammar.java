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





package edu.duke.cs.jflap.grammar;

/**
 * An unbound grammar has no restrictions whatsoever in the way of what
 * productions can be added to it. Since we may no longer depend on the first
 * production being restricted, the start variable is assumed to be S until the
 * grammar is told otherwise.
 * 
 * @author Thomas Finley
 */

public class UnboundGrammar extends Grammar {
	/**
	 * Creates a new grammar.
	 */
	public UnboundGrammar() {
		setStartVariable("S");
	}

	/**
	 * Every production is all right except those with lambda in the left hand
	 * side of the production.
	 * 
	 * @param production
	 *            the production to check
	 * @throws IllegalArgumentException
	 *             if the production is lambda on the left hand side
	 */
	public void checkProduction(Production production) {
		/*
		 * if (production.getLHS().length() == 0) { throw new
		 * IllegalArgumentException ("The left hand side cannot be empty."); }
		 */
	}

	@Override
	public boolean isConverted() {
		// TODO Auto-generated method stub
		return false;
	}

}
