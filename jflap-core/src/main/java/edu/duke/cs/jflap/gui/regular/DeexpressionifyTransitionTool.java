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





package edu.duke.cs.jflap.gui.regular;

import edu.duke.cs.jflap.gui.editor.Tool;
import edu.duke.cs.jflap.gui.viewer.AutomatonDrawer;
import edu.duke.cs.jflap.gui.viewer.AutomatonPane;

import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import edu.duke.cs.jflap.automata.Transition;
import edu.duke.cs.jflap.automata.fsa.FSATransition;

/**
 * A tool that handles the conversion of multiple transitions to one transition
 * for the FSA to regular expression conversion. This simply calls the
 * {@link FSAToREController#transitionCollapse} method.
 * 
 * @see edu.duke.cs.jflap.gui.regular.FSAToREController#transitionCreate
 * 
 * @author Thomas Finley
 */

public class DeexpressionifyTransitionTool extends Tool {
	/**
	 * Instantiates a new transition tool.
	 * 
	 * @param view
	 *            the view where the automaton is drawn
	 * @param drawer
	 *            the object that draws the automaton
	 * @param controller
	 *            the controller object for the transition from an FSA to an RE
	 */
	public DeexpressionifyTransitionTool(AutomatonPane view,
			AutomatonDrawer drawer, REToFSAController controller) {
		super(view, drawer);
		this.controller = controller;
	}

	/**
	 * Gets the tool tip for this tool.
	 * 
	 * @return the tool tip for this tool
	 */
	public String getToolTip() {
		return "De-expressionify Transition";
	}

	/**
	 * Returns the tool icon.
	 * 
	 * @return the state tool icon
	 */
	protected Icon getIcon() {
		java.net.URL url = getClass().getResource("/ICON/de-expressionify.gif");
		return new ImageIcon(url);
	}

	/**
	 * Returns the keystroke to switch to this tool, C.
	 * 
	 * @return the keystroke for this tool
	 */
	public KeyStroke getKey() {
		return KeyStroke.getKeyStroke('d');
	}

	/**
	 * When we press the mouse, the convert controller should be told that
	 * transitions are done.
	 * 
	 * @param event
	 *            the mouse event
	 */
	public void mousePressed(MouseEvent event) {
		Transition t = getDrawer().transitionAtPoint(event.getPoint());
		if (t != null)
			controller.transitionCheck((FSATransition) t);
	}

	/** The regular conversion controller. */
	private REToFSAController controller;
}
