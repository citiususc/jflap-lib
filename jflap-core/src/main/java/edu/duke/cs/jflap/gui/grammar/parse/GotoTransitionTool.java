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





package edu.duke.cs.jflap.gui.grammar.parse;

import edu.duke.cs.jflap.automata.State;
import edu.duke.cs.jflap.gui.editor.TransitionTool;
import edu.duke.cs.jflap.gui.viewer.AutomatonDrawer;
import edu.duke.cs.jflap.gui.viewer.AutomatonPane;
import java.awt.event.MouseEvent;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * This is a specialized transition tool that handles the movement of gotos from
 * one set to another set.
 * 
 * @author Thomas Finley
 */

public class GotoTransitionTool extends TransitionTool {
	/**
	 * Instantiates a new <CODE>TransitionExpanderTool</CODE>.
	 * 
	 * @param view
	 *            the view where the automaton is drawn
	 * @param drawer
	 *            the object that draws the automaton
	 * @param controller
	 *            the NFA to DFA controller object
	 */
	public GotoTransitionTool(AutomatonPane view, AutomatonDrawer drawer,
			LRParseDerivationController controller) {
		super(view, drawer);
		this.controller = controller;
	}

	/**
	 * When we release the mouse, a transition from the start state to this
	 * released state must be formed.
	 * 
	 * @param event
	 *            the mouse event
	 */
	public void mouseReleased(MouseEvent event) {
		// Did we even start at a state?
		if (first == null)
			return;
		State state = getDrawer().stateAtPoint(event.getPoint());
		controller.gotoGroup(first, event.getPoint(), state);
		first = null;
		getView().repaint();
	}

	/**
	 * Returns the tool icon.
	 * 
	 * @return the group expander tool icon
	 */
	protected Icon getIcon() {
		java.net.URL url = getClass().getResource("/ICON/expand_group.gif");
		return new ImageIcon(url);
	}

	/**
	 * Gets the tool tip for this tool.
	 * 
	 * @return the tool tip for this tool
	 */
	public String getToolTip() {
		return "Goto Set on Symbol";
	}

	/** The conversion to DFA controller. */
	private LRParseDerivationController controller;
}
