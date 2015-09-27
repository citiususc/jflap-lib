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





package edu.duke.cs.jflap.grammar;

import edu.duke.cs.jflap.grammar.cfg.ContextFreeGrammar;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import edu.duke.cs.jflap.automata.State;
import edu.duke.cs.jflap.automata.Transition;
import edu.duke.cs.jflap.automata.UnreachableStatesDetector;
import edu.duke.cs.jflap.automata.vdg.VDGTransition;
import edu.duke.cs.jflap.automata.vdg.VariableDependencyGraph;

/**
 * As it stands now, the code in here is almost completely useless. Through
 * conjunction with <CODE>gui.grammar.transform.UselessController</CODE> it
 * manages to do the correct thing (I hope), but in the interest of correctness
 * this code should be reformed; I have too much to do right now to figure out
 * where the hell he was going with some of this garbage... TWF
 */

public class UselessProductionRemover {
	/**
	 * Creates instance of <CODE>UselessProductionRemover</CODE>.
	 */
	public UselessProductionRemover() {

	}

	/**
	 * Returns set of all useful variables in <CODE>grammar</CODE>. A grammar
	 * is considered useful if it can derive a string.
	 * 
	 * @param grammar
	 *            the grammar
	 * @return set of all useful variables in <CODE>grammar</CODE>. A grammar
	 *         is considered useful if it can derive a string.
	 */
	public static Set getCompleteUsefulVariableSet(Grammar grammar) {
		Set set = getNewUsefulVariableSet();
		while (areMoreVariablesThatBelongInUsefulVariableSet(grammar, set)) {
			String variable = getVariableThatBelongsInUsefulVariableSet(
					grammar, set);
			addToUsefulVariableSet(variable, set);
		}
		return set;
	}

	/**
	 * Returns empty set.
	 * 
	 * @return empty set.
	 */
	private static Set getNewUsefulVariableSet() {
		return new HashSet();
	}

	/**
	 * Adds <CODE>variable</CODE> to <CODE>set</CODE>.
	 * 
	 * @param variable
	 *            the variable
	 * @param set
	 *            the set
	 */
	public static void addToUsefulVariableSet(String variable, Set set) {
		set.add(variable);
	}

	/**
	 * Returns the set of variables that are the predicate of rules that are
	 * only terminal strings.
	 */
	public static Set getTerminalProductions(Grammar grammar) {
		Set terminalDerivers = new TreeSet();
		Production[] p = grammar.getProductions();
		for (int i = 0; i < p.length; i++) {
			String lhs = p[i].getLHS();
			if (terminalDerivers.contains(lhs))
				continue;
			String rhs = p[i].getRHS();
			for (int k = 0; k < rhs.length(); k++) {
				char ch = rhs.charAt(k);
				if (ProductionChecker.isVariable(ch)) {
					lhs = null;
					break;
				}
			}
			if (lhs == null)
				continue;
			terminalDerivers.add(lhs);
		}
		return terminalDerivers;
	}

	/**
	 * Get a grammar with only those variables that derive terminals, directly
	 * or indirectly. This is not the same as a useless production-less grammar.
	 * 
	 * @param grammar
	 *            the grammar to get the reformed grammar for
	 */
	public static Grammar getTerminalGrammar(Grammar grammar) {
		Grammar g = new ContextFreeGrammar();
		Set terminalVars = getCompleteUsefulVariableSet(grammar);
		Production[] prods = grammar.getProductions();
		for (int i = 0; i < prods.length; i++) {
			Set v = new HashSet(Arrays.asList(prods[i].getVariables()));
			v.removeAll(terminalVars);
			if (v.size() > 0)
				continue;
			// Production has no variables that aren't terminal derivers!
			g.addProduction(prods[i]);
		}
		g.setStartVariable(grammar.getStartVariable());
		return g;
	}

	/**
	 * Returns a variable that belongs in the set of useful variables for <CODE>grammar</CODE>
	 * that is not already in <CODE>set</CODE>.
	 * 
	 * @param grammar
	 *            the grammar
	 * @param set
	 *            the set of useful variables in <CODE>grammar</CODE>.
	 * @return a variable that belongs in the set of useful variables for <CODE>grammar</CODE>
	 *         that is not already in <CODE>set</CODE>.
	 */
	public static String getVariableThatBelongsInUsefulVariableSet(
			Grammar grammar, Set set) {
		String[] variables = grammar.getVariables();
		for (int k = 0; k < variables.length; k++) {
			if (belongsInUsefulVariableSet(variables[k], grammar, set)
					&& !set.contains(variables[k]))
				return variables[k];
		}
		return null;
	}

	/**
	 * Returns true if <CODE>set</CODE> contains a variable equivalent to
	 * <CODE>ch</CODE>.
	 * 
	 * @param ch
	 *            the character
	 * @param set
	 *            the set of useful variables
	 * @return true if <CODE>set</CODE> contains a variable equivalent to
	 *         <CODE>ch</CODE>.
	 */
	private static boolean isInUsefulVariableSet(char ch, Set set) {
		Iterator it = set.iterator();
		while (it.hasNext()) {
			String variable = (String) it.next();
			char var = variable.charAt(0);
			if (ch == var) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns true if <CODE>production</CODE> can derive a string. (i.e. if
	 * all letters on the right hand side of the production are either terminals
	 * or useful variables (variables in <CODE>set</CODE>).
	 * 
	 * @param production
	 *            the production
	 * @param set
	 *            the set of useful variables
	 * @return true if <CODE>production</CODE> can derive a string. (i.e. if
	 *         all letters on the right hand side of the production are either
	 *         terminals or useful variables (variables in <CODE>set</CODE>).
	 */
	private static boolean isUsefulProduction(Production production, Set set) {
		ProductionChecker pc = new ProductionChecker();
		String rhs = production.getRHS();
		for (int k = 0; k < rhs.length(); k++) {
			char ch = rhs.charAt(k);
			if (!ProductionChecker.isTerminal(ch)
					&& !isInUsefulVariableSet(ch, set)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns true if <CODE>production</CODE> contains only terminals and
	 * variables in <CODE>set</CODE>, the set of useful variables. This
	 * includes both the left and right hand side of the production.
	 * 
	 * @param production
	 *            the production
	 * @param set
	 *            the set of useful variables
	 * @return true if <CODE>production</CODE> contains only terminals and
	 *         variables in <CODE>set</CODE>, the set of useful variables.
	 *         This includes both the left and right hand side of the
	 *         production.
	 */
	public static boolean isValidProduction(Production production, Set set) {
		String lhs = production.getLHS();
		for (int k = 0; k < lhs.length(); k++) {
			if (!isInUsefulVariableSet(lhs.charAt(k), set))
				return false;
		}
		return isUsefulProduction(production, set);
	}

	/**
	 * Returns true if <CODE>variable</CODE> belongs in the set of useful
	 * variables, even if it is already in <CODE>set</CODE>. This function
	 * examines all productions in <CODE>grammar</CODE> with variable on the
	 * left hand side, and determines if any of those productions are useful.
	 * 
	 * @param variable
	 *            the variable
	 * @param grammar
	 *            the grammar
	 * @param set
	 *            the set of useful variables
	 * @return true if <CODE>variable</CODE> belongs in the set of useful
	 *         variables, even if it is already in <CODE>set</CODE>.
	 */
	public static boolean belongsInUsefulVariableSet(String variable,
			Grammar grammar, Set set) {
		GrammarChecker gc = new GrammarChecker();
		Production[] productions = GrammarChecker.getProductionsOnVariable(
				variable, grammar);
		for (int k = 0; k < productions.length; k++) {
			if (isUsefulProduction(productions[k], set))
				return true;
		}
		return false;
	}

	/**
	 * Returns true if there are more variables (i.e. other than the ones
	 * already in the set) that belong in the set of useful variables <CODE>set</CODE>.
	 * 
	 * @param grammar
	 *            the grammar
	 * @param set
	 *            the set of useful variables.
	 * @return true if there are more variables (i.e. other than the ones
	 *         already in the set) that belong in the set of useful variables
	 *         <CODE>set</CODE>.
	 */
	public static boolean areMoreVariablesThatBelongInUsefulVariableSet(
			Grammar grammar, Set set) {
		if (getVariableThatBelongsInUsefulVariableSet(grammar, set) == null)
			return false;
		return true;
	}

	/**
	 * Returns the set of all useful productions (i.e. productions that can
	 * derive strings) in <CODE>grammar</CODE> based on the useful variables,
	 * contained in <CODE>usefulVariableSet</CODE>.
	 * 
	 * @param grammar
	 *            the grammar
	 * @param usefulVariableSet
	 *            the set of useful variables
	 * @return the set of all useful productions (i.e. productions that can
	 *         derive strings) in <CODE>grammar</CODE> based on the useful
	 *         variables, contained in <CODE>usefulVariableSet</CODE>.
	 */
	public static Set getCompleteProductionWithUsefulVariableSet(
			Grammar grammar, Set usefulVariableSet) {
		Set set = getNewProductionWithUsefulVariableSet();
		Production[] productions = grammar.getProductions();
		for (int k = 0; k < productions.length; k++) {
			if (belongsInProductionWithUsefulVariableSet(productions[k],
					usefulVariableSet)) {
				set.add(productions[k]);
			}
		}
		return set;
	}

	/**
	 * Returns an empty set.
	 * 
	 * @return an empty set.
	 */
	public static Set getNewProductionWithUsefulVariableSet() {
		return new HashSet();
	}

	/**
	 * Returns true if <CODE>production</CODE> belongs in set of useful
	 * productions (i.e. if <CODE>production</CODE> contains only terminals
	 * and variables in <CODE>usefulVariableSet</CODE>.
	 * 
	 * @param production
	 *            the production
	 * @param usefulVariableSet
	 *            the set of useful variables
	 * @return true if <CODE>production</CODE> belongs in set of useful
	 *         productions (i.e. if <CODE>production</CODE> contains only
	 *         terminals and variables in <CODE>usefulVariableSet</CODE>.
	 */
	public static boolean belongsInProductionWithUsefulVariableSet(
			Production production, Set usefulVariableSet) {
		if (isValidProduction(production, usefulVariableSet)) {
			return true;
		}
		return false;
	}

	/**
	 * Adds <CODE>production</CODE> to <CODE>set</CODE>.
	 */
	public static void addToProductionWithUsefulVariableSet(
			Production production, Set set) {
		set.add(production);
	}

	/**
	 * Adds a state for every variable in <CODE>grammar</CODE> to <CODE>graph</CODE>,
	 * and sets the state that represents the start variable ("S") to the
	 * initial state.
	 * 
	 * @param graph
	 *            the variable dependency graph
	 * @param grammar
	 *            the grammar.
	 */
	public static void initializeVariableDependencyGraph(
			VariableDependencyGraph graph, Grammar grammar) {
		String[] variables = (String[]) getCompleteUsefulVariableSet(grammar)
				.toArray(new String[0]);
		for (int k = 0; k < variables.length; k++) {
			double theta = 2.0 * Math.PI * (double) k
					/ (double) variables.length;
			Point point = new Point(200 + (int) (180.0 * Math.cos(theta)),
					200 + (int) (180.0 * Math.sin(theta)));
			State state = graph.createState(point);
			state.setName(variables[k]);
			if (variables[k].equals(grammar.getStartVariable()))
				graph.setInitialState(state);
		}
	}

	/**
	 * Returns true if <CODE>v1</CODE> is dependent on <CODE>v2</CODE>.
	 * (i.e. if <CODE>v2</CODE> is on the right hand side of any production in
	 * <CODE>grammar</CODE> that has <CODE>v1</CODE> on the left hand side).
	 * 
	 * @param v1
	 *            the variable on the left hand side
	 * @param v2
	 *            the variable on the right hand side
	 * @param grammar
	 *            the grammar
	 * @return true if <CODE>v1</CODE> is dependent on <CODE>v2</CODE>.
	 *         (i.e. if <CODE>v2</CODE> is on the right hand side of any
	 *         production in <CODE>grammar</CODE> that has <CODE>v1</CODE>
	 *         on the left hand side).
	 */
	public static boolean isDependentOn(String v1, String v2, Grammar grammar) {
		GrammarChecker gc = new GrammarChecker();
		ProductionChecker pc = new ProductionChecker();
		Production[] productions = GrammarChecker.getProductionsOnVariable(v1,
				grammar);
		for (int k = 0; k < productions.length; k++) {
			if (ProductionChecker.isVariableInProduction(v2, productions[k])) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns a transition between the states that represent <CODE>v1</CODE>
	 * and <CODE>v2</CODE> in <CODE>graph</CODE>.
	 * 
	 * @param v1
	 *            a variable
	 * @param v2
	 *            a variable
	 * @param graph
	 *            the variable dependency graph
	 * @return a transition between the states that represent <CODE>v1</CODE>
	 *         and <CODE>v2</CODE> in <CODE>graph</CODE>.
	 */
	public static Transition getTransition(String v1, String v2,
			VariableDependencyGraph graph) {
		State from = getStateForVariable(v1, graph);
		State to = getStateForVariable(v2, graph);
		return new VDGTransition(from, to);
	}

	/**
	 * Returns the state in <CODE>graph</CODE> that represents <CODE>variable</CODE>
	 * (i.e the state whose name is <CODE>variable</CODE>).
	 * 
	 * @param variable
	 *            the variable
	 * @param graph
	 *            the variable dependency graph.
	 * @return the state in <CODE>graph</CODE> that represents <CODE>variable</CODE>
	 *         (i.e the state whose name is <CODE>variable</CODE>).
	 */
	public static State getStateForVariable(String variable,
			VariableDependencyGraph graph) {
		State[] states = graph.getStates();
		for (int k = 0; k < states.length; k++) {
			State state = states[k];
			if (state.getName().equals(variable))
				return state;
		}
		return null;
	}

	/**
	 * Returns the variable dependency graph for <CODE>grammar</CODE>.
	 * 
	 * @param grammar
	 *            the grammar
	 * @return the variable dependency graph for <CODE>grammar</CODE>.
	 */
	public static VariableDependencyGraph getVariableDependencyGraph(
			Grammar grammar) {
		VariableDependencyGraph graph = new VariableDependencyGraph();
		initializeVariableDependencyGraph(graph, grammar);
		String[] variables = (String[]) getCompleteUsefulVariableSet(grammar)
				.toArray(new String[0]);
		for (int k = 0; k < variables.length; k++) {
			String v1 = variables[k];
			for (int i = 0; i < variables.length; i++) {
				String v2 = variables[i];
				if (i != k && isDependentOn(v1, v2, grammar)) {
					Transition trans = getTransition(v1, v2, graph);
					graph.addTransition(trans);
				}
			}
		}
		return graph;
	}

	/**
	 * Returns a set of transitions that represent all the dependencies
	 * determined by <CODE>production</CODE>.
	 * 
	 * @param production
	 *            the production
	 * @param graph
	 *            the variable dependency graph
	 * @return a set of transitions that represent all the dependencies
	 *         determined by <CODE>production</CODE>.
	 */
	public static Transition[] getTransitionsForProduction(
			Production production, VariableDependencyGraph graph) {
		ArrayList list = new ArrayList();
		String v1 = production.getLHS();
		ProductionChecker pc = new ProductionChecker();
		String rhs = production.getRHS();
		for (int k = 0; k < rhs.length(); k++) {
			char ch = rhs.charAt(k);
			if (ProductionChecker.isVariable(ch)) {
				StringBuffer buffer = new StringBuffer();
				buffer.append(ch);
				list.add(getTransition(v1, buffer.toString(), graph));
			}
		}
		return (Transition[]) list.toArray(new Transition[0]);
	}

	/**
	 * Returns the set of variables in <CODE>grammar</CODE> whose productions
	 * can never be reached from the start symbol. This is determined by the
	 * variable dependency graph <CODE>graph</CODE>.
	 * 
	 * @param grammar
	 *            the grammar
	 * @param graph
	 *            the variable dependency graph
	 * @return the set of variables in <CODE>grammar</CODE> whose productions
	 *         can never be reached from the start symbol. This is determined by
	 *         the variable dependency graph <CODE>graph</CODE>.
	 */
	public static String[] getUselessVariables(Grammar grammar,
			VariableDependencyGraph graph) {
		ArrayList list = new ArrayList();
		UnreachableStatesDetector usd = new UnreachableStatesDetector(graph);
		State[] states = usd.getUnreachableStates();
		for (int k = 0; k < states.length; k++) {
			list.add(states[k].getName());
		}
		return (String[]) list.toArray(new String[0]);
	}

	/**
	 * Removes all productions from <CODE>grammar</CODE> that contain <CODE>variable</CODE>,
	 * either on the left or right hand sides.
	 * 
	 * @param variable
	 *            the variable
	 * @param grammar
	 *            the grammar
	 */
	public static void removeProductionsForVariable(String variable,
			Grammar grammar) {
		GrammarChecker gc = new GrammarChecker();
		Production[] productions = GrammarChecker.getProductionsWithVariable(
				variable, grammar);
		for (int k = 0; k < productions.length; k++) {
			grammar.removeProduction(productions[k]);
		}
	}

	/**
	 * Returns a grammar with no variables that can not derive strings, by
	 * simply creating a new grammar and adding all productions in <CODE>usefulProductionSet</CODE>
	 * to that grammar.
	 * 
	 * @param usefulProductionSet
	 *            the set of useful productions
	 * @return a grammar with no variables that can not derive strings, by
	 *         simply creating a new grammar and adding all productions in
	 *         <CODE>usefulProductionSet</CODE> to that grammar.
	 */
	private static Grammar getGrammarWithNoVariablesThatCantDeriveStrings(
			Set usefulProductionSet) {
		Grammar g = new ContextFreeGrammar();
		Iterator it = usefulProductionSet.iterator();
		while (it.hasNext()) {
			Production p = (Production) it.next();
			g.addProduction(p);
		}
		return g;
	}

	/**
	 * Returns a grammar, equivalent to <CODE>grammar</CODE> that contains no
	 * useless productions.
	 * 
	 * @param grammar
	 *            the grammar
	 * @return a grammar, equivalent to <CODE>grammar</CODE> that contains no
	 *         useless productions.
	 */
	public static Grammar getUselessProductionlessGrammar(Grammar grammar) {
		Grammar g = new ContextFreeGrammar();
		g.setStartVariable(grammar.getStartVariable());
		if (!getCompleteUsefulVariableSet(grammar).contains(
				grammar.getStartVariable()))
			return g;
		grammar = getTerminalGrammar(grammar);
		VariableDependencyGraph graph = getVariableDependencyGraph(grammar);
		Set useless = new HashSet(Arrays.asList(getUselessVariables(g, graph)));
		Production[] p = grammar.getProductions();
		for (int i = 0; i < p.length; i++) {
			Set variables = new HashSet(Arrays.asList(p[i].getVariables()));
			variables.retainAll(useless);
			if (variables.size() > 0)
				continue;
			g.addProduction(p[i]);
		}
		return g;

	}

	/** the start symbol. */
	protected static String START_SYMBOL = "S";
}
