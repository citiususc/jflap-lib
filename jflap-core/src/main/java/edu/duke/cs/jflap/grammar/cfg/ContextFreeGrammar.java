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





package edu.duke.cs.jflap.grammar.cfg;

import edu.duke.cs.jflap.grammar.*;

/**
 * The context free grammar object is a representation of a context free
 * grammar. This object is a data structure of sorts, maintaining the data
 * pertinent to the definition of a context free grammar.
 * 
 * @author Ryan Cavalcante
 */

public class ContextFreeGrammar extends Grammar {
	/**
	 * Creates an instance of <CODE>ContextFreeGrammar</CODE>. The created
	 * instance has no productions, no terminals, no variables, and specifically
	 * no start variable.
	 */
	public ContextFreeGrammar() {
		super();
	}

	/**
	 * Throws an exception if the production is unrestricted on the left hand
	 * side.
	 * 
	 * @param production
	 *            the production to check
	 * @throws IllegalArgumentException
	 *             if the production is unrestricted on the left hand side
	 */
	public void checkProduction(Production production) {
		if (!ProductionChecker.isRestrictedOnLHS(production)){
            javax.swing.JOptionPane.showMessageDialog(null,
                    "Your production is unrestricted on the left hand side.");
			throw new IllegalArgumentException(
					"The production is unrestricted on the left hand side.");
        }
	}

	@Override
	public boolean isConverted() {
		// TODO Auto-generated method stub
		return false;
	}

}
