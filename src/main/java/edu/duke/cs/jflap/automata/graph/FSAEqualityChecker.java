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

import edu.duke.cs.jflap.automata.fsa.NFAToDFA;
import edu.duke.cs.jflap.automata.fsa.Minimizer;
import edu.duke.cs.jflap.automata.fsa.FiniteStateAutomaton;

/**
 * This determines if two FSAs accept the same language.
 * 
 * @author Thomas Finley
 */

public class FSAEqualityChecker {
	/**
	 * Checks if two FSAs accept the same language.
	 * 
	 * @param fsa1
	 *            the first finite state automaton
	 * @param fsa2
	 *            the second finite state automaton
	 * @return <CODE>true</CODE> if <CODE>fsa1</CODE> and <CODE>fsa2</CODE>
	 *         accept the same language, <CODE>false</CODE> if they they do
	 *         not
	 */
	public boolean equals(FiniteStateAutomaton fsa1, FiniteStateAutomaton fsa2) {
		// Clone for safety.
		fsa1 = (FiniteStateAutomaton) fsa1.clone();
		fsa2 = (FiniteStateAutomaton) fsa2.clone();

		// Make sure they're DFAs.
		fsa1 = nfaConverter.convertToDFA(fsa1);
		fsa2 = nfaConverter.convertToDFA(fsa2);
		// Minimize the DFAs.
		minimizer.initializeMinimizer();
		fsa1 = (FiniteStateAutomaton) minimizer.getMinimizeableAutomaton(fsa1);
		javax.swing.tree.DefaultTreeModel tree = minimizer
				.getDistinguishableGroupsTree(fsa1);
		fsa1 = minimizer.getMinimumDfa(fsa1, tree);

		minimizer.initializeMinimizer();
		fsa2 = (FiniteStateAutomaton) minimizer.getMinimizeableAutomaton(fsa2);
		tree = minimizer.getDistinguishableGroupsTree(fsa2);
		fsa2 = minimizer.getMinimumDfa(fsa2, tree);

		// Check the minimized DFAs to see if they are the same.
		return checker.equals(fsa1, fsa2);
	}

	/** The equality checker. */
	private static DFAEqualityChecker checker = new DFAEqualityChecker();

	/** The converter for an NFA to a DFA. */
	private static NFAToDFA nfaConverter = new NFAToDFA();

	/** That which minimizes a DFA. */
	private static Minimizer minimizer = new Minimizer();
}
