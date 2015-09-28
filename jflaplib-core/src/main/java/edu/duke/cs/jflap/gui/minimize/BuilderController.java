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





package edu.duke.cs.jflap.gui.minimize;

import edu.duke.cs.jflap.automata.*;
import edu.duke.cs.jflap.automata.fsa.*;
import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import java.awt.Component;
import java.util.*;
import edu.duke.cs.jflap.automata.event.AutomataTransitionListener;
import edu.duke.cs.jflap.automata.event.AutomataTransitionEvent;
import edu.duke.cs.jflap.gui.viewer.SelectionDrawer;
import edu.duke.cs.jflap.gui.environment.FrameFactory;

/**
 * This is the class for controlling the building of the minimized automaton,
 * after the tree has been built.
 * 
 * @author Thomas Finley
 */

class BuilderController {
	/**
	 * Instantiates a new <CODE>BuilderController</CODE>.
	 * 
	 * @param dfa
	 *            the original DFA being minimized
	 * @param minDfa
	 *            the DFA being built; this should initially have just the
	 *            states and nothing else
	 * @param drawer
	 *            the selection drawer for the DFA being built
	 * @param minimizer
	 *            the minimizer object handling the grit of the minimization
	 *            process
	 * @param tree
	 *            the completed minimizer tree
	 * @param view
	 *            the view that the automaton is minimized in
	 */
	public BuilderController(FiniteStateAutomaton dfa,
			FiniteStateAutomaton minDfa, SelectionDrawer drawer,
			Minimizer minimizer, DefaultTreeModel tree, Component view) {
		this.dfa = dfa;
		this.minDfa = minDfa;
		this.drawer = drawer;
		this.minimizer = minimizer;
		this.tree = tree;
		this.view = view;

		determineTransitions();
		initTransitionListener();
	}

	/**
	 * Sets up the remaining transitions set with all the transitions that need
	 * to be created.
	 */
	private void determineTransitions() {
		remainingTransitions = new HashSet();
		State[] states = minDfa.getStates();
		for (int i = 0; i < states.length; i++)
			remainingTransitions.addAll(minimizer.getTransitionsForState(
					states[i], minDfa, dfa, tree));
	}

	/**
	 * Sets up the listener of transitions that will monitor the automaton begin
	 * created for newly created transitions.
	 */
	private void initTransitionListener() {
		minDfa.addTransitionListener(new AutomataTransitionListener() {
			public void automataTransitionChange(AutomataTransitionEvent e) {
				if (!e.isAdd())
					return;
				Transition transition = e.getTransition();
				if (!remainingTransitions.contains(transition)) {
					JOptionPane.showMessageDialog(view,
							"That transition is not correct!");
					minDfa.removeTransition(transition);
				} else {
					remainingTransitions.remove(transition);
				}
			}
		});
	}

	/**
	 * This will add one transition (picked quasi-randomly, or at least
	 * unpredictably, as the first off the map iterator) to the automaton being
	 * built.
	 */
	public void hint() {
		if (remainingTransitions.size() == 0) {
			JOptionPane.showMessageDialog(view,
					"All transitions are in place already!");
			return;
		}
		Iterator it = new HashSet(remainingTransitions).iterator();
		Transition t = (Transition) it.next();
		minDfa.addTransition(t);
	}

	/**
	 * This will add all remaining transitions to the automaton.
	 */
	public void complete() {
		if (remainingTransitions.size() == 0) {
			JOptionPane.showMessageDialog(view,
					"All transitions are in place already!");
			return;
		}
		Iterator it = new HashSet(remainingTransitions).iterator();
		while (it.hasNext()) {
			Transition t = (Transition) it.next();
			minDfa.addTransition(t);
		}
	}

	/**
	 * This will check if the automaton is done. If it is, it will bring it up
	 * in its own window.
	 */
	public void done() {
		int remain = remainingTransitions.size();
		if (remain != 0) {
			Transition t = (Transition) remainingTransitions.iterator().next();
			drawer.addSelected(t.getFromState());
			JOptionPane.showMessageDialog(view, remain + " transition"
					+ (remain == 1 ? "" : "s") + " remain " + "to be placed.\n"
					+ "One comes from the state highlighted.");
			drawer.clearSelected();
			return;
		}
		JOptionPane.showMessageDialog(view,
				"The minimized automaton is fully built!\n"
						+ "It will now be placed in a new window.");
		FrameFactory.createFrame((FiniteStateAutomaton) minDfa.clone());
	}

	/** The original dfa. */
	private FiniteStateAutomaton dfa;

	/** The dfa being built. */
	private FiniteStateAutomaton minDfa;

	/** The selection drawer for the DFA being built. */
	private SelectionDrawer drawer;

	/** The minimizer object. */
	private Minimizer minimizer;

	/** The built minimize tree. */
	private DefaultTreeModel tree;

	/** The component. */
	private Component view;

	/** The set of transitions that must be created. */
	Set remainingTransitions;
}
