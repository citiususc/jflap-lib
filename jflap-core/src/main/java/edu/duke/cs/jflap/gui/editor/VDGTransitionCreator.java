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





package edu.duke.cs.jflap.gui.editor;

import edu.duke.cs.jflap.gui.viewer.AutomatonPane;
import edu.duke.cs.jflap.automata.State;
import edu.duke.cs.jflap.automata.Transition;
import edu.duke.cs.jflap.automata.vdg.VDGTransition;

/**
 * This is a transition creator for variable dependency graphs.
 * 
 * @author Thomas Finley
 */

public class VDGTransitionCreator extends TransitionCreator {
	/**
	 * Instantiates a transition creator.
	 * 
	 * @param parent
	 *            the parent object that any dialogs or windows brought up by
	 *            this creator should be the child of
	 */
	public VDGTransitionCreator(AutomatonPane parent) {
		super(parent);
	}

	/**
	 * Creates a transition with user interaction and returns it.
	 * 
	 * @return returns the variable dependency transition
	 */
	public Transition createTransition(State from, State to) {
		VDGTransition t = new VDGTransition(from, to);
		getParent().getDrawer().getAutomaton().addTransition(t);
		return null;
	}

	/**
	 * Edits a given transition. Ideally this should use the same interface as
	 * that given by <CODE>createTransition</CODE>.
	 * 
	 * @param transition
	 *            the transition to edit
	 * @return <CODE>false</CODE> if the user decided to not edit a
	 *         transition, <CODE>true</CODE> if the edit was "approved"
	 */
	public boolean editTransition(Transition transition) {
		return false;
	}
}
