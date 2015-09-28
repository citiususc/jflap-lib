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




package edu.duke.cs.jflap.grammar.parse;

import edu.duke.cs.jflap.grammar.Grammar;

/**
 * UnrestrictedUser Parser that is going to be used for parsing performed by user.
 * 
 * @author Kyung Min (Jason) Lee
 */
public class UnrestrictedUserParser extends UserParser{

	/**
	 * Creates a new unrestricted user parser.
	 * 
	 * @param grammar
	 *            the unrestricted grammar to parse
	 * @param target
	 *            the target string
	 */
	public UnrestrictedUserParser(Grammar grammar, String target) {
		super(grammar, target);
	}
}
