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
 * The alphabet retriever object can be used to find the alphabet for a given
 * automaton.
 * 
 * @author Ryan Cavalcante
 */

public abstract class AlphabetRetriever {
	/**
	 * Instantiates an <CODE>AlphabetRetriever</CODE> object.
	 */
	public AlphabetRetriever() {

	}

	/**
	 * Returns the alphabet for <CODE>automaton</CODE> in an array of strings.
	 * 
	 * @param automaton
	 *            the automaton.
	 * @return the alphabet.
	 */
	public abstract String[] getAlphabet(Automaton automaton);
}
