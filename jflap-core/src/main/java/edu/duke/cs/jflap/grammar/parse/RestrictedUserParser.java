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

import java.util.ArrayList;

/**
 * RestrictedUser Parser 
 * (This class is very similar to RestrictedBruteParser except for that this class extends UserParser) 
 * May be important in the future 
 * 
 * @author Kyung Min (Jason) Lee 
 */
public class RestrictedUserParser extends UserParser{
	
	/**
	 * Creates a new unrestricted brute parser.
	 * 
	 * @param grammar
	 *            the unrestricted grammar to parse
	 * @param target
	 *            the target string
	 */
	public RestrictedUserParser(Grammar grammar, String target) {
		super(grammar, target);
	}

	public boolean isPossibleDerivation(String derivation) {
		if (Unrestricted.minimumLength(derivation, mySmallerSet) > myTarget.length())
			return false;
		int targetSearched = 0;
		boolean startBookend = false, endBookend = false;
		ArrayList discrete = new ArrayList();
		StringBuffer sb = new StringBuffer();
		int start = -1;

		/*
		 * Set the start and end "bookeneds", that is, the derivation is padded
		 * with terminals on either it's left or right sides.
		 */
		if (derivation.length() == 0) {
			startBookend = endBookend = false;
		} else {
			startBookend = !myGrammar.isVariable(derivation.substring(0, 1));
			endBookend = !myGrammar.isVariable(derivation.substring(derivation
					.length() - 1, derivation.length()));
		}

		/* Break up groups of terminals into the "discrete" array. */
		for (int i = 0; i <= derivation.length(); i++) {
			String symbol = i == derivation.length() ? null : derivation
					.substring(i, i + 1);
			if (symbol == null || myGrammar.isVariable(symbol)) {
				// if (symbol == null) endBookend = true;
				if (sb.length() == 0)
					continue;
				if (start == -1)
					continue;
				discrete.add(derivation.substring(start, i));
				start = -1;
			} else if (myGrammar.isTerminal(symbol)) {
				if (start == -1)
					start = i;
				sb.append(symbol);
				// if (i==0) startBookend = true;
			}
		}
		int cp = 0;
		for (int i = 0; i < discrete.size(); i++) {
			String e = (String) discrete.get(i);
			if (startBookend && i == 0) {
				if (!myTarget.startsWith(e))
					return false;
				cp = e.length();
			} else if (endBookend && i == discrete.size() - 1) {
				if (!myTarget.endsWith(e))
					return false;
			} else {
				cp = myTarget.indexOf(e, cp);
				if (cp == -1)
					return false;
				cp += e.length();
			}
		}
		return true;
	}
}
