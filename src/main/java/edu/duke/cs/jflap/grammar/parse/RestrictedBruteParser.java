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
import java.util.*;

/**
 * This is the brute parser for an restricted grammar. It has additional tests
 * for whether a derivation is possible.
 * 
 * @author Thomas Finley
 */

public class RestrictedBruteParser extends BruteParser {
	/**
	 * Creates a new unrestricted brute parser.
	 * 
	 * @param grammar
	 *            the unrestricted grammar to parse
	 * @param target
	 *            the target string
	 */
	public RestrictedBruteParser(Grammar grammar, String target) {
		super(grammar, target);
	}

	public boolean isPossibleDerivation(String derivation) {
		if (Unrestricted.minimumLength(derivation, smaller) > target.length())
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
			startBookend = !grammar.isVariable(derivation.substring(0, 1));
			endBookend = !grammar.isVariable(derivation.substring(derivation
					.length() - 1, derivation.length()));
		}

		/* Break up groups of terminals into the "discrete" array. */
		for (int i = 0; i <= derivation.length(); i++) {
			String symbol = i == derivation.length() ? null : derivation
					.substring(i, i + 1);
			if (symbol == null || grammar.isVariable(symbol)) {
				// if (symbol == null) endBookend = true;
				if (sb.length() == 0)
					continue;
				if (start == -1)
					continue;
				discrete.add(derivation.substring(start, i));
				start = -1;
			} else if (grammar.isTerminal(symbol)) {
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
				if (!target.startsWith(e))
					return false;
				cp = e.length();
			} else if (endBookend && i == discrete.size() - 1) {
				if (!target.endsWith(e))
					return false;
			} else {
				cp = target.indexOf(e, cp);
				if (cp == -1)
					return false;
				cp += e.length();
			}
		}
		return true;
	}
}
