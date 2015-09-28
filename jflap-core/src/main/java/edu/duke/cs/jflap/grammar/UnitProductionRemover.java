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

import edu.duke.cs.jflap.automata.State;
import edu.duke.cs.jflap.automata.Transition;
import edu.duke.cs.jflap.automata.UnreachableStatesDetector;
import edu.duke.cs.jflap.automata.vdg.VDGTransition;
import edu.duke.cs.jflap.automata.vdg.VariableDependencyGraph;

/**
 * The Unit Production remover can be used to convert a grammar to an equivalent
 * grammar that doesn't have any unit productions (i.e productions of the form
 * A->B). This conversion consists of 3 steps: 1. drawing a variable dependency
 * graph of all variables that have unit productions. 2. putting all non-unit
 * productions from the original grammar into the new gramamr. 3. adding
 * productions to the new grammar to replace the unit productions in the
 * original grammar. Each step can be performed immediately or step by step. You
 * can perform step 1 immediately by calling getVariableDependencyGraph, or you
 * can build the dependency graph step by step by first calling
 * initializeDependencyGraph which will add nodes for each variable in the
 * grammar to your dependency graph. Then, you actually represent the
 * dependencies of the variables by examining only the unit productions in the
 * grammar. To get the transition (for the graph) that represents the dependency
 * of a specific unit production, call getTransitionForUnitProduction. Then you
 * can add the returned transition to your variable dependency graph. After you
 * do this for every unit production in the grammar, the dependency graph will
 * be complete. You can perform step 2 immediately by calling
 * addAllNonUnitProductionsToGrammar. Or you can do this step by step by
 * creating a ProductionChecker object to check each production in the original
 * grammar to see if it is a unit production. So, if the user selects a
 * production from the original grammar and tries to add it to the new grammar,
 * you must check (using the ProductionChecker) if the production is a unit
 * production. If not, you can add it to the new grammar by simply calling
 * addProduction on the grammar. Once you've added all the Non-unit productions
 * from the original grammar to the new grammar, you can finish the conversion
 * by performing step 3. You can perform step 3 immediately by calling
 * addAllProductionsToGrammar, or you can perform it step by step by getting the
 * dependencies for each variable in the grammar by calling getDependencies.
 * Then, for each variable that said variable is dependent on, you need to get
 * all the non-unit productions in the grammar on that variable (by using a
 * GrammarChecker and calling getNonUnitProductionsOnVariable, and then call
 * getNewProductions to get the productions necessary to replace the unit
 * productions on said variable. This will return a list of new productions on
 * said variable that accounts for the removal of the unit production to its
 * dependent variable.
 * 
 * @author Ryan Cavalcante
 */

public class UnitProductionRemover {
	/**
	 * Creates instance of <CODE>UnitProductionRemover</CODE>.
	 */
	public UnitProductionRemover() {

	}

	/**
	 * Returns the variable dependency graph for the variables in <CODE>grammar</CODE>.
	 * 
	 * @param grammar
	 *            the grammar.
	 * @return the variable dependency graph for the variables in <CODE>grammar</CODE>.
	 */
	public VariableDependencyGraph getVariableDependencyGraph(Grammar grammar) {
		VariableDependencyGraph graph = new VariableDependencyGraph();
		initializeDependencyGraph(graph, grammar);
		Production[] uprods = getUnitProductions(grammar);
		for (int k = 0; k < uprods.length; k++) {
			graph
					.addTransition(getTransitionForUnitProduction(uprods[k],
							graph));
		}
		return graph;
	}

	/**
	 * Returns all unit productions in <CODE>grammar</CODE>.
	 * 
	 * @param grammar
	 *            the grammar.
	 * @return all unit productions in <CODE>grammar</CODE>.
	 */
	public Production[] getUnitProductions(Grammar grammar) {
		ArrayList list = new ArrayList();
		ProductionChecker pc = new ProductionChecker();

		Production[] productions = grammar.getProductions();
		for (int k = 0; k < productions.length; k++) {
			if (ProductionChecker.isUnitProduction(productions[k])) {
				list.add(productions[k]);
			}
		}
		return (Production[]) list.toArray(new Production[0]);
	}

	/**
	 * Returns all non-unit productions in <CODE>grammar</CODE>.
	 * 
	 * @param grammar
	 *            the grammar
	 * @return all non-unit productions in <CODE>grammar</CODE>.
	 */
	public Production[] getNonUnitProductions(Grammar grammar) {
		ArrayList list = new ArrayList();
		ProductionChecker pc = new ProductionChecker();

		Production[] productions = grammar.getProductions();
		for (int k = 0; k < productions.length; k++) {
			if (!ProductionChecker.isUnitProduction(productions[k])) {
				list.add(productions[k]);
			}
		}
		return (Production[]) list.toArray(new Production[0]);
	}

	/**
	 * Adds a state for each variable in <CODE>grammar</CODE> to <CODE>graph</CODE>.
	 * 
	 * @param graph
	 *            the graph to add the states to.
	 * @param grammar
	 *            the grammar
	 */
	public void initializeDependencyGraph(VariableDependencyGraph graph,
			Grammar grammar) {
		// StatePlacer sp = new StatePlacer();
		String[] variables = grammar.getVariables();
		for (int k = 0; k < variables.length; k++) {
			// Point point = sp.getPointForState(graph);
			double theta = 2.0 * Math.PI * (double) k
					/ (double) variables.length;
			Point point = new Point(200 + (int) (180.0 * Math.cos(theta)),
					200 + (int) (180.0 * Math.sin(theta)));
			State state = graph.createState(point);
			state.setName(variables[k]);
		}
	}

	/**
	 * Returns the state in <CODE>graph</CODE> that represents <CODE>variable</CODE>
	 * (i.e. the state whose label is <CODE>variable</CODE>).
	 * 
	 * @param variable
	 *            the variable
	 * @param graph
	 *            the graph
	 * @return the state in <CODE>graph</CODE> that represents <CODE>variable</CODE>
	 *         (i.e. the state whose label is <CODE>variable</CODE>).
	 */
	public State getStateForVariable(String variable,
			VariableDependencyGraph graph) {
		State[] states = graph.getStates();
		for (int k = 0; k < states.length; k++) {
			if (states[k].getName().equals(variable))
				return states[k];
		}
		return null;
	}

	/**
	 * Returns the transition for <CODE>graph</CODE> that represents the
	 * dependency of the variables in the unit production <CODE>production</CODE>.
	 * 
	 * @param production
	 *            the unit production
	 * @param graph
	 *            the graph
	 * @return the transition for <CODE>graph</CODE> that represents the
	 *         dependency of the variables in the unit production <CODE>production</CODE>.
	 */
	public Transition getTransitionForUnitProduction(Production production,
			VariableDependencyGraph graph) {
		ProductionChecker pc = new ProductionChecker();
		if (!ProductionChecker.isUnitProduction(production))
			return null;
		String lhs = production.getLHS();
		String rhs = production.getRHS();
		State from = getStateForVariable(lhs, graph);
		State to = getStateForVariable(rhs, graph);
		return new VDGTransition(from, to);
	}

	/**
	 * Adds all non-unit productions in <CODE>oldGrammar</CODE> to <CODE>newGrammar</CODE>.
	 * 
	 * @param oldGrammar
	 *            a grammar
	 * @param newGrammar
	 *            a grammar
	 */
	public void addAllNonUnitProductionsToGrammar(Grammar oldGrammar,
			Grammar newGrammar) {
		Production[] productions = getNonUnitProductions(oldGrammar);
		for (int k = 0; k < productions.length; k++) {
			newGrammar.addProduction(productions[k]);
		}
	}

	/**
	 * Returns true if <CODE>variable1</CODE> is dependent on <CODE>variable2</CODE>.
	 * (i.e. there is a path in <CODE>graph</CODE> from <CODE>variable1</CODE>
	 * to <CODE>variable2</CODE>).
	 * 
	 * @param variable1
	 *            the first variable; the start of the path.
	 * @param variable2
	 *            the second variable; the destination of the path.
	 * @param graph
	 *            the variable dependency graph.
	 * @return true if <CODE>variable1</CODE> is dependent on <CODE>variable2</CODE>.
	 *         (i.e. there is a path in <CODE>graph</CODE> from <CODE>variable1</CODE>
	 *         to <CODE>variable2</CODE>).
	 */
	public boolean isDependentOn(String variable1, String variable2,
			VariableDependencyGraph graph) {
		State v1 = getStateForVariable(variable1, graph);
		State v2 = getStateForVariable(variable2, graph);
		graph.setInitialState(v1);
		UnreachableStatesDetector usd = new UnreachableStatesDetector(graph);
		State[] states = usd.getUnreachableStates();
		graph.setInitialState(null);
		for (int k = 0; k < states.length; k++) {
			if (v2 == states[k])
				return false;
		}
		return true;
	}

	/**
	 * Returns all variables that <CODE>variable</CODE> is dependent on (i.e.
	 * all variables whose states can be reached from the state that represents
	 * <CODE>variable</CODE> in <CODE>graph</CODE>.
	 * 
	 * @param variable
	 *            the variable whose dependencies are being found
	 * @param grammar
	 *            the grammar
	 * @param graph
	 *            the dependency graph
	 * @return all variables that <CODE>variable</CODE> is dependent on (i.e.
	 *         all variables whose states can be reached from the state that
	 *         represents <CODE>variable</CODE> in <CODE>graph</CODE>.
	 */
	public String[] getDependencies(String variable, Grammar grammar,
			VariableDependencyGraph graph) {
		ArrayList list = new ArrayList();
		String[] variables = grammar.getVariables();
		for (int k = 0; k < variables.length; k++) {
			if (!variable.equals(variables[k])) {
				if (isDependentOn(variable, variables[k], graph)) {
					list.add(variables[k]);
				}
			}
		}
		return (String[]) list.toArray(new String[0]);
	}

	/**
	 * Returns a list of productions created by taking <CODE>variable</CODE>
	 * as their left hand side, and the right hand side of a production in
	 * <CODE>oldProductions</CODE> as their right hand sides.
	 * 
	 * @param variable
	 *            the left hand side of the created productions
	 * @param oldProductions
	 *            the set of productions whose right hand sides are used as the
	 *            right hand sides of the created productions
	 * @return a list of productions created by taking <CODE>variable</CODE>
	 *         as their left hand side, and the right hand side of a production
	 *         in <CODE>oldProductions</CODE> as their right hand sides.
	 */
	public Production[] getNewProductions(String variable,
			Production[] oldProductions) {
		ArrayList list = new ArrayList();
		for (int k = 0; k < oldProductions.length; k++) {
			list.add(new Production(variable, oldProductions[k].getRHS()));
		}
		return (Production[]) list.toArray(new Production[0]);
	}

	/**
	 * Adds all productions to <CODE>newGrammar</CODE> required to account for
	 * removing all unit productions from <CODE>oldGrammar</CODE>.
	 * 
	 * @param oldGrammar
	 *            the original grammar
	 * @param newGrammar
	 *            the new grammar
	 * @param graph
	 *            the variable dependency graph of the original grammar
	 */
	public void addAllNewProductionsToGrammar(Grammar oldGrammar,
			Grammar newGrammar, VariableDependencyGraph graph) {
		GrammarChecker gc = new GrammarChecker();
		String[] variables = oldGrammar.getVariables();
		for (int k = 0; k < variables.length; k++) {
			String v1 = variables[k];
			String[] dep = getDependencies(v1, oldGrammar, graph);
			for (int i = 0; i < dep.length; i++) {
				Production[] prods = GrammarChecker
						.getNonUnitProductionsOnVariable(dep[i], oldGrammar);
				newGrammar.addProductions(getNewProductions(v1, prods));
			}
		}
	}

	/**
	 * Returns a unit production-less grammar equivalent to <CODE>grammar</CODE>.
	 * 
	 * @param grammar
	 *            the grammar
	 * @param graph
	 *            the variable dependency graph of <CODE>grammar</CODE>.
	 * @return a unit production-less grammar equivalent to <CODE>grammar</CODE>.
	 */
	public Grammar getUnitProductionlessGrammar(Grammar grammar,
			VariableDependencyGraph graph) {
		Grammar uplgrammar = new ContextFreeGrammar();
		addAllNonUnitProductionsToGrammar(grammar, uplgrammar);
		addAllNewProductionsToGrammar(grammar, uplgrammar, graph);
		return uplgrammar;
	}
}
