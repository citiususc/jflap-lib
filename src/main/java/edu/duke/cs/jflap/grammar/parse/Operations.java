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

import edu.duke.cs.jflap.grammar.*;
import edu.duke.cs.jflap.grammar.cfg.ContextFreeGrammar;
import java.util.*;

import javax.swing.JOptionPane;

/**
 * This is a utility class for finding out things about a grammar for purposes
 * of parsing.
 * 
 * @author Thomas Finley
 */

public class Operations {
	/**
	 * Dang class ain't for the instantiating!
	 */
	private Operations() {
	}

	/**
	 * Given a map to sets and a key, return the set.
	 */
	private static Set setForKey(Map map, Object key) {
		return (Set) map.get(key);
	}

	/**
	 * Calculate the first sets of a grammar.
	 * 
	 * @param grammar
	 *            the grammar to calculate first sets for
	 * @return a map of symbols in the grammar to the first sets of that symbol
	 *         for this grammar
	 */
	public static Map first(Grammar grammar) {
		if (CACHED_FIRST.containsKey(grammar))
			return (Map) CACHED_FIRST.get(grammar);
		Map first = new HashMap();
		// Put the terminals in the map.
		String[] terminals = grammar.getTerminals();
		for (int i = 0; i < terminals.length; i++) {
			Set termSet = new HashSet();
			termSet.add(terminals[i]);
			first.put(terminals[i], termSet);
		}
		// Put the variables in the map as empty sets.
		String[] variables = grammar.getVariables();
		for (int i = 0; i < variables.length; i++) {
			first.put(variables[i], new HashSet());
		}

		// Repeatedly go over the productions until there is no more
		// change.
		boolean hasChanged = true;
		Production[] productions = grammar.getProductions();
		while (hasChanged) {
			hasChanged = false;
			for (int i = 0; i < productions.length; i++) {
				String variable = productions[i].getLHS();
				String rhs = productions[i].getRHS();
				Set firstRhs = first(first, rhs);
				if (setForKey(first, variable).addAll(firstRhs))
					hasChanged = true;
			}
		}
		CACHED_FIRST.put(grammar, Collections.unmodifiableMap(first));
		return first(grammar);
	}

	/**
	 * Given a first map as returned by {@link #first(Grammar)} and a string
	 * containing some sequence of symbols, return the first for that sequence.
	 * 
	 * @param firstSets
	 *            the map of single symbols to a map
	 * @param sequence
	 *            a string of symbols
	 * @return the first set for that sequence of symbols
	 */
	public static Set first(Map firstSets, String sequence) {
		Set first = new HashSet();
		if (sequence.equals(""))
			first.add("");
		for (int j = 0; j < sequence.length(); j++) {
			Set s = setForKey(firstSets, sequence.substring(j, j + 1));
			if (!s.contains("")) {
				// Doesn't contain lambda. Add it and get the
				// hell out of dodge.
				first.addAll(s);
				break;
			}
			// Does contain lambda. Damn it.
			if (j != sequence.length() - 1)
				s.remove("");
			first.addAll(s);
			if (j != sequence.length() - 1)
				s.add("");
		}
		return first;
	}

	/**
	 * Given a grammar, this will return the follow mappings. This returns a map
	 * from the non-terminals in the grammar to the follow sets.
	 * 
	 * @param grammar
	 *            the grammar to calculate follow sets for
	 * @return the map of non-terminals to the follow sets
	 */
	public static Map follow(Grammar grammar) {
		if (CACHED_FOLLOW.containsKey(grammar))
			return (Map) CACHED_FOLLOW.get(grammar);
		Map follow = new HashMap();
		// Add the mapping from the initial variable to the end of
		// string character.
		Set initialSet = new HashSet();
		initialSet.add("$");
		follow.put(grammar.getStartVariable(), initialSet);
		// Make every follow mapping empty for now.
		String[] variables = grammar.getVariables();
		for (int i = 0; i < variables.length; i++)
			if (!variables[i].equals(grammar.getStartVariable()))
				follow.put(variables[i], new HashSet());
		// Get the first sets.
		Map firstSets = first(grammar);
		// Iterate repeatedly over the productions until we're
		// completely done.
		Production[] productions = grammar.getProductions();
		boolean hasChanged = true;
		while (hasChanged) {
			hasChanged = false;
			for (int i = 0; i < productions.length; i++) {
				String variable = productions[i].getLHS();
				String rhs = productions[i].getRHS();
				for (int j = 0; j < rhs.length(); j++) {
					String rhsVariable = rhs.substring(j, j + 1);
					if (!grammar.isVariable(rhsVariable))
						continue;
					Set firstFollowing = first(firstSets, rhs.substring(j + 1));
					// Is lambda in that following the variable? For
					// A->aBb where lambda is in FIRST(b), everything
					// in FOLLOW(A) is in FOLLOW(B).
					if (firstFollowing.remove("")) {
						if (setForKey(follow, rhsVariable).addAll(
								setForKey(follow, variable)))
							hasChanged = true;
					}
					// For A->aBb, everything in FIRST(b) except
					// lambda is put in FOLLOW(B).
					if (setForKey(follow, rhsVariable).addAll(firstFollowing))
						hasChanged = true;
				}
			}
		}
		CACHED_FOLLOW.put(grammar, Collections.unmodifiableMap(follow));
		return follow(grammar);
	}

	/**
	 * This returns if a grammar is LL(1).
	 * 
	 * @param grammar
	 *            the grammar to test
	 * @return if the grammar is LL(1)
	 */
	public static boolean isLL1(Grammar grammar) {
		Map first = first(grammar);
		Map follow = follow(grammar);
        if(follow == null){
            JOptionPane.showMessageDialog(null, "JFLAP failed to find a follow set.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
		Map varToProd = new HashMap();

		Production[] productions = grammar.getProductions();
		for (int i = 0; i < productions.length; i++) {
			String variable = productions[i].getLHS();
			if (!varToProd.containsKey(variable))
				varToProd.put(variable, new ArrayList());
			((List) varToProd.get(variable)).add(productions[i]);
		}
		String[] variables = grammar.getVariables();
		for (int i = 0; i < variables.length; i++) {
			Set followVar = (Set) follow.get(variables[i]);
            List varList = ((List) varToProd.get(variables[i]));
            if(varList == null){
                JOptionPane.showMessageDialog(null, "JFLAP failed to find a variable.  You may have used a variable on the right hand side without providing a derivation for it.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
			productions = (Production[]) varList.toArray(new Production[0]);
			for (int j = 0; j < productions.length; j++) {
				String alpha = productions[j].getRHS();
				Set alphaFirst = first(first, alpha);
				for (int k = j + 1; k < productions.length; k++) {
					String beta = productions[k].getRHS();
					Set betaFirst = first(first, beta);
					// Condition 1 & 2
					if (betaFirst.removeAll(alphaFirst))
						return false;
					// Condition 3
					if (betaFirst.contains(""))
						if (alphaFirst.removeAll(followVar))
							return false;
					if (alphaFirst.contains(""))
						if (betaFirst.removeAll(followVar))
							return false;
				}
			}
		}
		return true;
	}

	/**
	 * This will return an augmented grammar, given a grammar.
	 * 
	 * @param grammar
	 *            the grammar to augment
	 * @return the grammar augmented
	 */
	public static Grammar getAugmentedGrammar(Grammar grammar) {
		String start = grammar.getStartVariable();
		Grammar g = new ContextFreeGrammar();
		g.setStartVariable(start);
		Production[] prods = grammar.getProductions();
		Production startProduction = new Production(start, start);
		try{
            g.addProduction(startProduction);
        }
        catch(IllegalArgumentException e){
            return null;
            }
		startProduction.setLHS(start + "'");
		for (int i = 0; i < prods.length; i++)
			g.addProduction(prods[i]);
		return g;
	}

	/**
	 * Given a set of items, this produces the closure of that set.
	 * 
	 * @param grammar
	 *            the grammar for which this production is taking place
	 * @param items
	 *            the set of items
	 * @return a set containing the closure of those items
	 */
	public static Set closure(Grammar grammar, Set items) {
		items = new HashSet(items);
		Set closure = new HashSet(items);

		Map vp = getVariableProductionMap(grammar);

		while (true) {
			Set currentStep = new HashSet();
			Iterator it = closure.iterator();
			while (it.hasNext()) {
				Production item = (Production) it.next();
				// Find what's on this production.
				int p = item.getRHS().indexOf(ITEM_POSITION);
				p++; // We want what's after this.
				if (p == item.getRHS().length())
					continue;
				// We want all productions with this variable.
				String var = item.getRHS().substring(p, p + 1);
				Set ps = (Set) vp.get(var);
				if (ps == null) {
					continue;
				}
				Iterator pIt = ps.iterator();
				while (pIt.hasNext()) {
					Production cp = (Production) pIt.next();
					currentStep.add(new Production(var, ITEM_POSITION
							+ cp.getRHS()));
				}
			}
			if (!items.addAll(currentStep))
				return items;
			closure = currentStep;
		}
	}

	/**
	 * Given a grammar, a set of items, and a grammar symbol, return the goto of
	 * this set on that symbol for this grammar.
	 * 
	 * @param grammar
	 *            the grammar to calculate goto on
	 * @param items
	 *            the set of items (productions) for goto
	 * @param symbol
	 *            the symbol to use for goto
	 */
	public static Set goTo(Grammar grammar, Set items, String symbol) {
		Set more = new HashSet();
		Iterator it = items.iterator();
		while (it.hasNext()) {
			Production item = (Production) it.next();
			int p = item.getRHS().indexOf(ITEM_POSITION);
			p++; // We want what's after this.
			if (p == item.getRHS().length())
				continue;
			// We want all productions with this variable.
			String var = item.getRHS().substring(p, p + 1);
			if (!var.equals(symbol))
				continue;

			String newRhs = item.getRHS().substring(0, p - 1)
					+ item.getRHS().substring(p, p + 1) + ITEM_POSITION
					+ item.getRHS().substring(p + 1, item.getRHS().length());
			more.add(new Production(item.getLHS(), newRhs));
		}
		return closure(grammar, more);
	}

	/**
	 * Given a grammar, returns a mapping of variables in the grammar to a set
	 * of productions on that variable.
	 * 
	 * @param grammar
	 *            the grammar to get a map for
	 * @return the map of variables to productions
	 */
	public static Map getVariableProductionMap(Grammar grammar) {
		if (CACHED_VPMAP.containsKey(grammar))
			return Collections.unmodifiableMap((Map) CACHED_VPMAP.get(grammar));
		Map vp = new HashMap();
		CACHED_VPMAP.put(grammar, vp);
		Production[] p = grammar.getProductions();
		for (int i = 0; i < p.length; i++) {
			if (!vp.containsKey(p[i].getLHS()))
				vp.put(p[i].getLHS(), new HashSet());
			((Set) vp.get(p[i].getLHS())).add(p[i]);
		}
		return getVariableProductionMap(grammar);
	}

	/**
	 * Given a production, this returns the list of productions with the various
	 * permutations of items, with the item position indicator in every
	 * position.
	 * 
	 * @param production
	 *            the production
	 * @return an array of productions, each indicating an item
	 */
	public static Production[] getItems(Production production) {
		StringBuffer sb = new StringBuffer(production.getRHS());
		String rhs = production.getRHS();
		Production[] items = new Production[rhs.length() + 1];
		for (int i = 0; i <= rhs.length(); i++) {
			sb.insert(i, ITEM_POSITION);
			items[i] = new Production(production.getLHS(), sb.toString());
			sb.deleteCharAt(i);
		}
		return items;
	}

	/**
	 * Returns all the symbols possible to do a goto for on a particular set of
	 * items.
	 * 
	 * @param items
	 *            the set of items
	 * @return an array containing all the symbols one can do a goto on for this
	 *         item set
	 */
	public static String[] getCanGoto(Set items) {
		Iterator it = items.iterator();
		Set symbols = new HashSet();
		while (it.hasNext()) {
			Production item = (Production) it.next();
			int position = item.getRHS().indexOf(ITEM_POSITION) + 1;
			if (position == item.getRHS().length())
				continue;
			symbols.add(item.getRHS().substring(position, position + 1));
		}
		return (String[]) symbols.toArray(new String[0]);
	}

	/** The cached first sets, maps from grammars to first sets. */
	private static WeakHashMap CACHED_FIRST = new WeakHashMap();

	/** The cached follow sets, maps from grammars to follow sets. */
	private static WeakHashMap CACHED_FOLLOW = new WeakHashMap();

	/**
	 * The cached variables to productions maps, maps from grammars to maps from
	 * variables to productions on that variable.
	 */
	private static WeakHashMap CACHED_VPMAP = new WeakHashMap();

	/** The terminal used to indicate the position in an item. */
	public static final char ITEM_POSITION = '\u00B7';
}
