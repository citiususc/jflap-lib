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





package edu.duke.cs.jflap.gui.grammar.automata;

/**
 * The <CODE>GrammarCreationException</CODE> is thrown if there is an error in
 * the creation of a grammar.
 * 
 * @author Thomas Finley
 */

public class GrammarCreationException extends RuntimeException {
	/**
	 * Instantiates a new <CODE>GrammarCreationException</CODE>.
	 */
	public GrammarCreationException() {
		super();
	}

	/**
	 * Instantiates a new <CODE>GrammarCreationException</CODE>.
	 * 
	 * @param message
	 *            the message to accompany this exception
	 */
	public GrammarCreationException(String message) {
		super(message);
	}
}
