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





package edu.duke.cs.jflap.grammar.cfg;

import edu.duke.cs.jflap.grammar.Grammar;
import edu.duke.cs.jflap.grammar.GrammarToAutomatonConverter;
import edu.duke.cs.jflap.grammar.Production;
import edu.duke.cs.jflap.automata.Automaton;
import edu.duke.cs.jflap.automata.State;
import edu.duke.cs.jflap.automata.StatePlacer;
import edu.duke.cs.jflap.automata.Transition;
import edu.duke.cs.jflap.automata.pda.PDATransition;

/**
 * The CFG to PDA (LR parsing) converter can be used to convert a context free
 * grammar to an equivalent pushdown automaton that can be used for LR parsing.
 * You can perform this conversion simply by calling convertToAutomaton, or you
 * can do it step by step by first calling createStatesForConversion, which will
 * add the necessary states for the conversion to your pushdown automaton, and
 * then calling getTransitionForProduction for each production in the grammar.
 * Of course you must then add the returned transition to your pushdown
 * automaton for each call to getTransitionForProduction. When you have done
 * this for every production in your grammar, you will have an equivalent
 * pushdown automaton.
 * 
 * @author Ryan Cavalcante
 */

public class CFGToPDALRConverter extends GrammarToAutomatonConverter {
	/**
	 * Creates an instance of <CODE>CFGToPDALRConverter</CODE>.
	 */
	public CFGToPDALRConverter() {

	}

	/**
	 * Returns the reverse of <CODE>string</CODE> e.g. it would return "cba"
	 * for "abc".
	 * 
	 * @param string
	 *            the string
	 * @return the reverse of <CODE>string</CODE>
	 */
	private String getReverse(String string) {
		StringBuffer buffer = new StringBuffer();
		for (int k = string.length() - 1; k >= 0; k--) {
			buffer.append(string.charAt(k));
		}
		return buffer.toString();
	}

	/**
	 * Returns the transition created by converting <CODE>production</CODE> to
	 * its equivalent transition.
	 * 
	 * @param production
	 *            the production
	 * @return the equivalent transition.
	 */
	public Transition getTransitionForProduction(Production production) {
		String lhs = production.getLHS();
		String rhs = production.getRHS();
		String rrhs = getReverse(rhs);
		Transition transition = new PDATransition(START_STATE, START_STATE, "",
				rrhs, lhs);
		return transition;
	}

	/**
	 * Adds all states to <CODE>automaton</CODE> necessary for the conversion
	 * of <CODE>grammar</CODE> to its equivalent automaton. This creates three
	 * states--an initial state, an intermediate state, and a final state. It
	 * also adds transitions connecting the three states, and transitions for
	 * each terminal in <CODE>grammar</CODE>
	 * 
	 * @param grammar
	 *            the grammar being converted.
	 * @param automaton
	 *            the automaton being created.
	 */
	public void createStatesForConversion(Grammar grammar, Automaton automaton) {
		initialize();
		StatePlacer sp = new StatePlacer();

		State initialState = automaton.createState(sp
				.getPointForState(automaton));
		automaton.setInitialState(initialState);
		START_STATE = initialState;

		State intermediateState = automaton.createState(sp
				.getPointForState(automaton));

		State finalState = automaton
				.createState(sp.getPointForState(automaton));
		automaton.addFinalState(finalState);

		String startVariable = grammar.getStartVariable();
		PDATransition trans1 = new PDATransition(initialState,
				intermediateState, "", startVariable, "");
		automaton.addTransition(trans1);
		PDATransition trans2 = new PDATransition(intermediateState, finalState,
				"", BOTTOM_OF_STACK, "");
		automaton.addTransition(trans2);

		String[] terminals = grammar.getTerminals();
		for (int k = 0; k < terminals.length; k++) {
			PDATransition trans = new PDATransition(initialState, initialState,
					terminals[k], "", terminals[k]);
			automaton.addTransition(trans);
		}
	}

	/** The start state. */
	protected State START_STATE;

}
