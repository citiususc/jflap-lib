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

import edu.duke.cs.jflap.gui.viewer.AutomatonDrawer;
import edu.duke.cs.jflap.gui.viewer.AutomatonPane;

import java.awt.event.MouseEvent;

/**
 * This extension of the arrow tool does not allow the editing of an automaton,
 * even for moving states, but does allow for the changing of certain view
 * options (displaying of labels and things of that nature).
 * 
 * @author Thomas Finley
 */

public class ArrowDisplayOnlyTool extends ArrowNontransitionTool {
	/**
	 * Instantiates a new <CODE>ArrowDisplayOnlyTool</CODE>.
	 * 
	 * @param view
	 *            the view the automaton is drawn in
	 * @param drawer
	 *            the automaton drawer
	 */
	public ArrowDisplayOnlyTool(AutomatonPane view, AutomatonDrawer drawer) {
		super(view, drawer);
	}

	/**
	 * We don't want anything happening when the mouse is dragged. This method
	 * simply returns without doing anything.
	 * 
	 * @param event
	 *            the dragging mouse event
	 */
	public void mouseDragged(MouseEvent event) {
		return;
	}
}
