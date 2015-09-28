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

import edu.duke.cs.jflap.gui.environment.AutomatonEnvironment;
import edu.duke.cs.jflap.gui.environment.Universe;
import edu.duke.cs.jflap.gui.environment.tag.CriticalTag;
import edu.duke.cs.jflap.gui.regular.ConvertPane;

import java.awt.event.ActionEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * This action handles the conversion of an FSA to a regular expression.
 * 
 * @author Thomas Finley
 */

public class ConvertFSAToREAction extends FSAAction {
	/**
	 * Instantiates a new <CODE>ConvertFSAToREAction</CODE>.
	 * 
	 * @param environment
	 *            the environment
	 */
	public ConvertFSAToREAction(AutomatonEnvironment environment) {
		super("Convert FA to RE", null);
		this.environment = environment;
	}

	/**
	 * This method begins the process of converting an automaton to a regular
	 * expression.
	 * 
	 * @param event
	 *            the action event
	 */
	public void actionPerformed(ActionEvent event) {
		JFrame frame = Universe.frameForEnvironment(environment);
		if (environment.getAutomaton().getInitialState() == null) {
			JOptionPane.showMessageDialog(frame,
					"Conversion requires an automaton\nwith an initial state!",
					"No Initial State", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (environment.getAutomaton().getFinalStates().length == 0) {
			JOptionPane.showMessageDialog(frame,
					"Conversion requires at least\n" + "one final state!",
					"No Final States", JOptionPane.ERROR_MESSAGE);
			return;
		}
		ConvertPane pane = new ConvertPane(environment);
		environment.add(pane, "Convert FA to RE", new CriticalTag() {
		});
		environment.setActive(pane);
	}

	/** The automaton environment. */
	private AutomatonEnvironment environment;

}
