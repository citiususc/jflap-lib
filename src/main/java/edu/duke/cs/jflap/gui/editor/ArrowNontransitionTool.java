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
 * This extension of the arrow tool does not allow the editing of an automaton
 * aside from the moving about of states.
 * 
 * @author Thomas Finley
 */

public class ArrowNontransitionTool extends ArrowTool {
	/**
	 * Instantiates a new <CODE>ArrowNontransitionTool</CODE>.
	 * 
	 * @param view
	 *            the view the automaton is drawn in
	 * @param drawer
	 *            the automaton drawer
	 */
	public ArrowNontransitionTool(AutomatonPane view, AutomatonDrawer drawer) {
		super(view, drawer);
	}

	/**
	 * On a mouse click, this simply returns,
	 * 
	 * @param event
	 *            the mouse event
	 */
	public void mouseClicked(MouseEvent event) {
		return;
		/*
		 * if (event.getClickCount() == 2) return; super.mouseClicked(event);
		 */
	}

	protected boolean shouldShowStatePopup() {
		return false;
	}
}
