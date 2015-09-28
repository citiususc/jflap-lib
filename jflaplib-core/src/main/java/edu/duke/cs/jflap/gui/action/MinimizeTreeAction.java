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





package edu.duke.cs.jflap.gui.action;

import edu.duke.cs.jflap.gui.environment.Environment;
import edu.duke.cs.jflap.gui.environment.Universe;
import edu.duke.cs.jflap.gui.environment.tag.CriticalTag;
import edu.duke.cs.jflap.gui.minimize.MinimizePane;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import edu.duke.cs.jflap.automata.AutomatonChecker;
import edu.duke.cs.jflap.automata.fsa.FiniteStateAutomaton;
import edu.duke.cs.jflap.automata.fsa.Minimizer;

/**
 * This action allows the user to manually minimize a DFA using a minimization
 * tree.
 * 
 * @author Thomas Finley
 */

public class MinimizeTreeAction extends FSAAction {
	/**
	 * Instantiates a new <CODE>MinimizeTreeAction</CODE>.
	 * 
	 * @param automaton
	 *            the automaton that the tree will be shown for
	 * @param environment
	 *            the environment object that we shall add our simulator pane to
	 */
	public MinimizeTreeAction(FiniteStateAutomaton automaton,
			Environment environment) {
		super("Miniminize DFA", null);
		this.automaton = automaton;
		this.environment = environment;
		/*
		 * putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke (KeyEvent.VK_R,
		 * MAIN_MENU_MASK+InputEvent.SHIFT_MASK));
		 */
	}

	/**
	 * Puts the DFA form in another window.
	 * 
	 * @param e
	 *            the action event
	 */
	public void actionPerformed(ActionEvent e) {
		if (automaton.getInitialState() == null) {
			JOptionPane.showMessageDialog(Universe
					.frameForEnvironment(environment),
					"The automaton should have " + "an initial state.");
			return;
		}
		AutomatonChecker ac = new AutomatonChecker();
		if (ac.isNFA(automaton)) {
			JOptionPane.showMessageDialog(Universe
					.frameForEnvironment(environment), "This isn't a DFA!");
			return;
		}
		// Show the new environs pane.
		FiniteStateAutomaton minimized = (FiniteStateAutomaton) automaton
				.clone();
		MinimizePane minPane = new MinimizePane(minimized, environment);
		environment.add(minPane, "Minimization", new CriticalTag() {
		});
		environment.setActive(minPane);
	}

	/** The automaton. */
	private FiniteStateAutomaton automaton;

	/** The environment. */
	private Environment environment;

	/** That which minimizes a DFA. */
	private static Minimizer minimizer = new Minimizer();

}
