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

import edu.duke.cs.jflap.automata.State;
import edu.duke.cs.jflap.automata.Transition;
import edu.duke.cs.jflap.automata.pda.PDAToCFGConverter;
import edu.duke.cs.jflap.automata.pda.PushdownAutomaton;
import edu.duke.cs.jflap.grammar.Grammar;
import edu.duke.cs.jflap.grammar.Production;
import edu.duke.cs.jflap.grammar.cfg.ContextFreeGrammar;
import edu.duke.cs.jflap.gui.viewer.SelectionDrawer;
import java.util.*;

/**
 * This controls the conversion of a push down automaton to a context free
 * grammar.
 * 
 * @author Thomas Finley
 */

public class PDAConvertController extends ConvertController {
	/**
	 * Instantiates a <CODE>PDAConvertController</CODE> for an automaton.
	 * 
	 * @param pane
	 *            the convert pane that holds the automaton pane and the grammar
	 *            table
	 * @param drawer
	 *            the selection drawer where the automaton is made
	 * @param automaton
	 *            the automaton to build the <CODE>PDAConvertController</CODE>
	 *            for
	 */
	public PDAConvertController(ConvertPane pane, SelectionDrawer drawer,
			PushdownAutomaton automaton) {
		super(pane, drawer, automaton);
		converter = new PDAToCFGConverter();
		converter.initializeConverter();
		fillMap();
	}

	/**
	 * Returns the productions for a particular state. This method will only be
	 * called once.
	 * 
	 * @param state
	 *            the state to get the productions for
	 * @return an array containing the productions that correspond to a
	 *         particular state
	 */
	protected Production[] getProductions(State state) {
		
		return new Production[0];
	}

	/**
	 * Returns the productions for a particular transition. This method will
	 * only be called once.
	 * 
	 * @param transition
	 *            the transition to get the productions for
	 * @return an array containing the productions that correspond to a
	 *         particular transition
	 */
	protected Production[] getProductions(Transition transition) {
		return (Production[]) converter.createProductionsForTransition(
				transition, getAutomaton()).toArray(new Production[0]);
	}

	/**
	 * Returns the grammar that's the result of this conversion.
	 * 
	 * @return the grammar that's the result of this conversion
	 * @throws GrammarCreationException
	 *             if there are not enough variables to uniquely identify every
	 *             variable here
	 */
	protected Grammar getGrammar() {
		int oldNumProductions = getModel().getProductions().length;
		converter.purgeProductions(getAutomaton(), getModel());		
		if (oldNumProductions != getModel().getProductions().length && converter.numberVariables() > 26)			
			throw new GrammarCreationException(
					"Your list of rules has been trimmed, but there are still more variables than " +
					"can be uniquely represented.");
		else if (converter.numberVariables() > 26)
			throw new GrammarCreationException("There are more variables than can be uniquely represented.");
		else if (oldNumProductions != getModel().getProductions().length)
			javax.swing.JOptionPane.showMessageDialog(null, "Your list of rules has been trimmed.");
		
		int rows = getModel().getRowCount();
		ContextFreeGrammar grammar = new ContextFreeGrammar();
		grammar.setStartVariable("S");
		ArrayList productions = new ArrayList();
		for (int i = 0; i < rows; i++) {
			Production production = getModel().getProduction(i);
			if (production == null)
				continue;
			production = converter.getSimplifiedProduction(production);
			productions.add(production);
		}

		Collections.sort(productions, new Comparator() {
			public int compare(Object o1, Object o2) {
				Production p1 = (Production) o1, p2 = (Production) o2;
				if ("S".equals(p1.getLHS())) {
					if (p1.getLHS().equals(p2.getLHS()))
						return 0;
					else
						return -1;
				}
				if ("S".equals(p2.getLHS()))
					return 1;
				return p2.getLHS().compareTo(p1.getRHS());
			}

			public boolean equals(Object o) {
				return false;
			}
		});
		Iterator it = productions.iterator();
		while (it.hasNext())
			grammar.addProduction((Production) it.next());
		return grammar;
	}

	/** The converter object from which we get the productions. */
	private PDAToCFGConverter converter;
}
