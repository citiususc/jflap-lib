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

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.Serializable;

import javax.swing.KeyStroke;

import edu.duke.cs.jflap.automata.Automaton;
import edu.duke.cs.jflap.automata.AutomatonSimulator;
import edu.duke.cs.jflap.automata.fsa.FiniteStateAutomaton;
import edu.duke.cs.jflap.automata.pda.PushdownAutomaton;

/**
 * This is the action used for the stepwise simulation of data without closure,
 * that is, without lambda transitions being automatically traversed.
 * 
 * @author Thomas Finley
 */

public class SimulateNoClosureAction extends SimulateAction {
	/**
	 * Instantiates a new <CODE>SimulateNoClosureAction</CODE>.
	 * 
	 * @param automaton
	 *            the automaton that input will be simulated on
	 * @param environment
	 *            the environment object that we shall add our simulator pane to
	 */
	public SimulateNoClosureAction(Automaton automaton, Environment environment) {
		super(automaton, environment);
		putValue(NAME, "Step by State...");
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_R,
				MAIN_MENU_MASK + InputEvent.SHIFT_MASK));
	}

	/**
	 * Returns the simulator for this automaton.
	 * 
	 * @param automaton
	 *            the automaton to get the simulator for
	 * @return a simulator for this automaton
	 */
	protected AutomatonSimulator getSimulator(Automaton automaton) {
		if (automaton instanceof edu.duke.cs.jflap.automata.fsa.FiniteStateAutomaton)
			return new edu.duke.cs.jflap.automata.fsa.FSAStepByStateSimulator(automaton);
		else
			return new edu.duke.cs.jflap.automata.pda.PDAStepByStateSimulator(automaton);
	}

	/**
	 * This particular action may only be applied to finite state automata.
	 * 
	 * @param object
	 *            the object to test for applicability
	 * @return <CODE>true</CODE> if the passed in object is a finite state
	 *         automaton, <CODE>false</CODE> otherwise
	 */
	public static boolean isApplicable(Serializable object) {
		return object instanceof FiniteStateAutomaton
				|| object instanceof PushdownAutomaton;
	}
}
