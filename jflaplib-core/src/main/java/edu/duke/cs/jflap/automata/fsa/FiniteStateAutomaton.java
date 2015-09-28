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

import edu.duke.cs.jflap.automata.Automaton;

/**
 * This subclass of <CODE>Automaton</CODE> is specifically for a definition of
 * a regular Finite State Automaton.
 * 
 * @author Thomas Finley
 */

public class FiniteStateAutomaton extends Automaton {
	/**
	 * Creates a finite state automaton with no states and no transitions.
	 */
	public FiniteStateAutomaton() {
		super();
	}

	/**
	 * Returns the class of <CODE>Transition</CODE> this automaton must
	 * accept.
	 * 
	 * @return the <CODE>Class</CODE> object for <CODE>automata.fsa.FSATransition</CODE>
	 */
	protected Class getTransitionClass() {
		return edu.duke.cs.jflap.automata.fsa.FSATransition.class;
	}
}
