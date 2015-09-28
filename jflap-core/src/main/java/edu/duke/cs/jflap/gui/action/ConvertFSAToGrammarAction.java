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
import edu.duke.cs.jflap.gui.environment.EnvironmentFrame;
import edu.duke.cs.jflap.gui.environment.Universe;
import edu.duke.cs.jflap.gui.grammar.automata.ConvertController;
import edu.duke.cs.jflap.gui.grammar.automata.ConvertPane;
import edu.duke.cs.jflap.gui.grammar.automata.FSAConvertController;
import edu.duke.cs.jflap.gui.viewer.SelectionDrawer;
import edu.duke.cs.jflap.gui.viewer.ZoomPane;

import java.awt.BorderLayout;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import edu.duke.cs.jflap.automata.Automaton;
import edu.duke.cs.jflap.automata.Transition;
import edu.duke.cs.jflap.automata.fsa.FSATransition;
import edu.duke.cs.jflap.automata.fsa.FiniteStateAutomaton;

/**
 * This action handles the conversion of an FSA to a regular grammar.
 * 
 * @author Thomas Finley
 */

public class ConvertFSAToGrammarAction extends ConvertAutomatonToGrammarAction {
	/**
	 * Instantiates a new <CODE>ConvertFSAToGrammarAction</CODE>.
	 * 
	 * @param environment
	 *            the environment
	 */
	public ConvertFSAToGrammarAction(AutomatonEnvironment environment) {
		super(environment);
	}

	/**
	 * Checks the FSA to make sure it's ready to be converted.
	 */
	protected boolean checkAutomaton() {
		// If we have more than 26 states, we can't have a single
		// letter for all states.
		if (getAutomaton().getStates().length > 26) {
			JOptionPane.showMessageDialog(Universe
					.frameForEnvironment(getEnvironment()),
					"There may be at most 26 states for conversion.",
					"Number of States Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		// Check for transitions with capital letters.
		Set bad = new HashSet();
		Transition[] t = getAutomaton().getTransitions();
		for (int i = 0; i < t.length; i++) {
			if (((FSATransition) t[i]).getLabel().matches(".*[A-Z].*")) {
				bad.add(t[i]);
			}
		}
		if (bad.size() != 0) {
			// Initialize the structure for displaying a problem.
			EnvironmentFrame frame = Universe
					.frameForEnvironment(getEnvironment());
			JPanel messagePanel = new JPanel(new BorderLayout());
			SelectionDrawer drawer = new SelectionDrawer(getAutomaton());
			JLabel messageLabel = new JLabel();
			ZoomPane zoom = new ZoomPane(drawer);
			JPanel tempPanel = new JPanel(new BorderLayout());
			tempPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
			zoom.setPreferredSize(new java.awt.Dimension(300, 200));
			tempPanel.add(zoom, BorderLayout.CENTER);
			messagePanel.add(tempPanel, BorderLayout.CENTER);
			messagePanel.add(messageLabel, BorderLayout.SOUTH);
			// Display the message.
			drawer.clearSelected();
			Iterator it = bad.iterator();
			while (it.hasNext())
				drawer.addSelected((Transition) it.next());
			messageLabel
					.setText("Capital letters are reserved for grammar variables.");
			JOptionPane.showMessageDialog(frame, messagePanel,
					"Transitions With Capitals Error",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}

		return true;
	}

	/**
	 * Initializes the convert controller.
	 * 
	 * @param pane
	 *            the convert pane that holds the automaton pane and the grammar
	 *            table
	 * @param drawer
	 *            the selection drawer of the new view
	 * @param automaton
	 *            the automaton that's being converted; note that this will not
	 *            be the exact object returned by <CODE>getAutomaton</CODE>
	 *            since a clone is made
	 * @return the convert controller to handle the conversion of the automaton
	 *         to a grammar
	 */
	protected ConvertController initializeController(ConvertPane pane,
			SelectionDrawer drawer, Automaton automaton) {
		return new FSAConvertController(pane, drawer,
				(FiniteStateAutomaton) automaton);
	}

	/**
	 * This action is applicable only to <CODE>FiniteStateAutomaton</CODE>s.
	 * 
	 * @param object
	 *            the object to check for applicability
	 * @return <CODE>true</CODE> if the object is an FSA, <CODE>false</CODE>
	 *         otherwise
	 */
	public static boolean isApplicable(Object object) {
		return object instanceof FiniteStateAutomaton;
	}
}
