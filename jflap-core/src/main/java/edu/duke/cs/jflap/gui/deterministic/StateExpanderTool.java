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





package edu.duke.cs.jflap.gui.deterministic;

import edu.duke.cs.jflap.gui.editor.Tool;
import edu.duke.cs.jflap.gui.viewer.AutomatonDrawer;
import edu.duke.cs.jflap.gui.viewer.AutomatonPane;

import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import edu.duke.cs.jflap.automata.State;

/**
 * This is a tool that expands a state completely.
 * 
 * @author Thomas Finley
 */

public class StateExpanderTool extends Tool {
	/**
	 * Instantiates a new state tool.
	 */
	public StateExpanderTool(AutomatonPane view, AutomatonDrawer drawer,
			ConversionController controller) {
		super(view, drawer);
		this.controller = controller;
	}

	/**
	 * Gets the tool tip for this tool.
	 * 
	 * @return the tool tip for this tool
	 */
	public String getToolTip() {
		return "State Expander";
	}

	/**
	 * Returns the tool icon.
	 * 
	 * @return the state tool icon
	 */
	protected Icon getIcon() {
		java.net.URL url = getClass().getResource("/ICON/state_expander.gif");
		return new ImageIcon(url);
	}

	/**
	 * When the user clicks, one creates a state.
	 * 
	 * @param event
	 *            the mouse event
	 */
	public void mousePressed(MouseEvent event) {
		State state = getDrawer().stateAtPoint(event.getPoint());
		if (state == null)
			return;
		controller.expandState(state);
	}

	/**
	 * Returns the keystroke to switch to this tool, S.
	 * 
	 * @return the keystroke for this tool
	 */
	public KeyStroke getKey() {
		return KeyStroke.getKeyStroke('s');
	}

	/** The deterministic NFA to DFA controller. */
	private ConversionController controller;
}
