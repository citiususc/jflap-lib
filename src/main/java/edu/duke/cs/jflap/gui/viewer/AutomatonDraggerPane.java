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





package edu.duke.cs.jflap.gui.viewer;

import edu.duke.cs.jflap.gui.editor.ArrowNontransitionTool;
import edu.duke.cs.jflap.automata.Automaton;

/**
 * This is the same as an automaton pane, except that it allows the user to drag
 * states around. This is used particularly in situations where the placement of
 * states may not be to a users liking, i.e. displaying of DFAs with a random
 * placement of some states.
 * 
 * @author Thomas Finley
 */

public class AutomatonDraggerPane extends AutomatonPane {
	/**
	 * Instantiates the automaton dragger pane.
	 * 
	 * @param drawer
	 *            the automaton drawer
	 */
	public AutomatonDraggerPane(AutomatonDrawer drawer) {
		super(drawer);
		init();
	}

	/**
	 * Instantiates the automaton dragger pane.
	 * 
	 * @param drawer
	 *            the automaton drawer
	 * @param adapt
	 *            whether or not to adapt the size of the view
	 */
	public AutomatonDraggerPane(AutomatonDrawer drawer, boolean adapt) {
		super(drawer, adapt);
		init();
	}

	/**
	 * Instantiates the automaton dragger pane.
	 * 
	 * @param automaton
	 *            the automaton to draw
	 */
	public AutomatonDraggerPane(Automaton automaton) {
		super(automaton);
		init();
	}

	/**
	 * Adds what allows dragging.
	 */
	private void init() {
		ArrowNontransitionTool t = new ArrowNontransitionTool(this, getDrawer());
		addMouseListener(t);
		addMouseMotionListener(t);
	}
}
