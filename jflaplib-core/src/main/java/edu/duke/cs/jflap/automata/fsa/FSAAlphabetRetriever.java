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

import edu.duke.cs.jflap.automata.*;
import java.util.*;

/**
 * The FSA alphabet retriever object can be used to find the alphabet for a
 * given finite state automaton. The method of determining the alphabet for
 * automaton involves examining all transitions in the automaton and adding each
 * new character on a transition label to the alphabet.
 * 
 * @author Ryan Cavalcante
 */

public class FSAAlphabetRetriever extends AlphabetRetriever {
	/**
	 * Creates an instance of <CODE>FSAAlphabetRetriever</CODE>.
	 */
	public FSAAlphabetRetriever() {

	}

	/**
	 * Returns the alphabet of <CODE>automaton</CODE> by analyzing all
	 * transitions and their labels.
	 * 
	 * @param automaton
	 *            the automaton
	 * @return the alphabet, in a string[].
	 */
	public String[] getAlphabet(Automaton automaton) {
		ArrayList list = new ArrayList();
		Transition[] transitions = automaton.getTransitions();
		for (int k = 0; k < transitions.length; k++) {
			FSATransition transition = (FSATransition) transitions[k];
			String label = transition.getLabel();
			if (!label.equals("") && !list.contains(label)) {
				list.add(label);
			}
		}
		return (String[]) list.toArray(new String[0]);
	}
}
