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

import edu.duke.cs.jflap.automata.NondeterminismDetector;
import edu.duke.cs.jflap.automata.Transition;

/**
 * The FSA nondeterminism detector object can be used to find all the
 * nondeterministic states in a finite state automaton (i.e. all states with
 * equal outward transitions).
 * 
 * @author Ryan Cavalcante
 */

public class FSANondeterminismDetector extends NondeterminismDetector {
	/**
	 * Creates an instance of <CODE>FSANondeterminismDetector</CODE>
	 */
	public FSANondeterminismDetector() {
	}

	/**
	 * Returns true if the transitions are identical (i.e. the labels are
	 * equivalent), or if they introduce nondeterminism (e.g. the label of one
	 * could be a prefix of the label of the other).
	 * 
	 * @param t1
	 *            a transition
	 * @param t2
	 *            a transition
	 * @return true if the transitions are nondeterministic.
	 */
	public boolean areNondeterministic(Transition t1, Transition t2) {
		FSATransition transition1 = (FSATransition) t1;
		FSATransition transition2 = (FSATransition) t2;
		if (transition1.getLabel().equals(transition2.getLabel()))
			return true;
		else if (transition1.getLabel().startsWith(transition2.getLabel()))
			return true;
		else if (transition2.getLabel().startsWith(transition1.getLabel()))
			return true;
		else
			return false;
	}

}
