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





package edu.duke.cs.jflap.grammar.reg;

import edu.duke.cs.jflap.grammar.Production;
import edu.duke.cs.jflap.grammar.ProductionChecker;

/**
 * This <CODE>RightLinearGrammar</CODE> is a regular grammar with the
 * additional restriction that the grammar cannot be a left linear grammar.
 * 
 * @author Thomas Finley
 */

public class RightLinearGrammar extends RegularGrammar {
	/**
	 * The production checker makes sure that the production added is a proper
	 * right linear production.
	 * 
	 * @param production
	 *            the production to check
	 * @throws IllegalArgumentException
	 *             if the production is not a right linear production
	 */
	public void checkProduction(Production production) {
		if (!ProductionChecker.isRightLinear(production))
			throw new IllegalArgumentException(
					"The production is not right linear.");
	}

	/** The production checker. */
	private static ProductionChecker PC = new ProductionChecker();
}
