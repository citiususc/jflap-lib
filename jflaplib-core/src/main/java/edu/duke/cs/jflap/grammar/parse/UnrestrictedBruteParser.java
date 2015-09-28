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
 * This is the brute parser for an unrestricted grammar.
 * 
 * @author Thomas Finley
 */

public class UnrestrictedBruteParser extends BruteParser {
	/**
	 * Creates a new unrestricted brute parser.
	 * 
	 * @param grammar
	 *            the unrestricted grammar to parse
	 * @param target
	 *            the target string
	 */
	public UnrestrictedBruteParser(Grammar grammar, String target) {
		super(grammar, target);
	}
}
