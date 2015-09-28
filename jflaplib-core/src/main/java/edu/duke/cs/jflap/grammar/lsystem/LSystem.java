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





package edu.duke.cs.jflap.grammar.lsystem;

import edu.duke.cs.jflap.grammar.*;
import java.io.Serializable;
import java.util.*;

/**
 * The <CODE>LSystem</CODE> class represents L-systems. This does not do any
 * simulation of L-systems, but rather has the minimal mathematical definitions
 * required, i.e., the axiom, replacement rules, with some concession given to
 * define parameters for drawing.
 * 
 * @author Thomas Finley
 */

// Oh, I'm just doing fine. Thank you very much. Just very well.
// Oh, just fine! Thank you. Very well. Mmm-hmm! I'm just...
public class LSystem implements Serializable {
	/**
	 * Constructs an empty L-System.
	 */
	public LSystem() {
		this("", new UnrestrictedGrammar(), new HashMap());
	}

	/**
	 * Constructs a new L-System.
	 * 
	 * @param replacements
	 *            the grammar holding the replacement rules, where each
	 *            production has (on the left hand side) the symbol to replace,
	 *            while on the right hand side is a string containing space
	 *            delimited symbols
	 * @param values
	 *            various parameters controlling drawing in the lsystem
	 * @param axiom
	 *            the start symbols as a space delimited string
	 */
	public LSystem(String axiom, Grammar replacements, Map values) {
		this.values = Collections.unmodifiableMap(values);
		initReplacements(replacements);
		this.axiom = tokenify(axiom);
	}

	/**
	 * Given a space delimited string, returns a list of the non-whitespace
	 * tokens.
	 * 
	 * @param string
	 *            the string to take tokens from
	 * @return a list containing all tokens of the string
	 */
	public static List tokenify(String string) {
		StringTokenizer st = new StringTokenizer(string);
		ArrayList list = new ArrayList();
		while (st.hasMoreTokens())
			list.add(st.nextToken());
		return list;
	}

	/**
	 * Initializes the list of rewriting rules.
	 * 
	 * @param replacements
	 *            the grammar holding the replacement rules
	 */
	private void initReplacements(Grammar replacements) {
		Map reps = new HashMap();
		Production[] p = replacements.getProductions();
		for (int i = 0; i < p.length; i++) {
			String replace = p[i].getLHS();
			ArrayList currentReplacements = null;
			if (!reps.containsKey(replace))
				reps.put(replace, currentReplacements = new ArrayList());
			else
				currentReplacements = (ArrayList) reps.get(replace);
			List currentSubstitution = tokenify(p[i].getRHS());
			try {
				List lastSubstitution = (List) currentReplacements
						.get(currentReplacements.size() - 1);
				if (!currentSubstitution.equals(lastSubstitution))
					nondeterministic = true;
			} catch (IndexOutOfBoundsException e) {

			}
			currentReplacements.add(currentSubstitution);
		}
		Iterator it = reps.entrySet().iterator();
		symbolToReplacements = new TreeMap();
		List[] emptyListArray = new List[0];
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			List l = (List) entry.getValue();
			List[] replacementArray = (List[]) l.toArray(emptyListArray);
			symbolToReplacements.put(entry.getKey(), replacementArray);
		}
	}

	/**
	 * Returns the list of symbols for the axiom.
	 * 
	 * @return the list of symbols for the axiom
	 */
	public List getAxiom() {
		return axiom;
	}

	/**
	 * Returns the array of replacements for a symbol.
	 * 
	 * @param symbol
	 *            the symbol to get the replacements for
	 * @return an array of lists, where each list is a list of the strings; the
	 *         array will be empty if there are no replacements
	 */
	public List[] getReplacements(String symbol) {
		List[] toReturn = (List[]) symbolToReplacements.get(symbol);
		return toReturn == null ? EMPTY_LIST : toReturn;
	}

	/**
	 * Returns the symbols for which there are replacements.
	 * 
	 * @return the set of symbols that have replacements in this L-system
	 */
	public Set getSymbolsWithReplacements() {
		return symbolToReplacements.keySet();
	}

	/**
	 * Returns a mapping of names of parameters for the L-system to their
	 * respective values
	 * 
	 * @return the map of names of parameters to the parameters themselves
	 */
	public Map getValues() {
		return values;
	}

	/**
	 * Returns whether the l-system is nondeterministic, i.e., if there are any
	 * symbols that could result in an ambiguous outcome (a sort of stochiastic
	 * thing).
	 * 
	 * @return if the l-system is nondeterministic
	 */
	public boolean nondeterministic() {
		return nondeterministic;
	}

	/** The grammar holding the replacement rules. */
	private Map symbolToReplacements;

	/** The mapping of keys to values. */
	private Map values;

	/** The axiom. */
	private List axiom;

	/** Whether or not the L-system has stochiastic properties. */
	private boolean nondeterministic = false;

	/** An empty list array. */
	private static final List[] EMPTY_LIST = new List[0];
}
