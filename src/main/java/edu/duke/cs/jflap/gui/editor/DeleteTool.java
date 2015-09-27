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

import edu.duke.cs.jflap.gui.environment.AutomatonEnvironment;
import edu.duke.cs.jflap.gui.viewer.AutomatonDrawer;
import edu.duke.cs.jflap.gui.viewer.AutomatonPane;

import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.KeyStroke;

import edu.duke.cs.jflap.automata.State;
import edu.duke.cs.jflap.automata.Transition;

/**
 * A tool that handles the deletion of states and transitions.
 * 
 * @author Thomas Finley
 */

public class DeleteTool extends Tool {
	/**
	 * Instantiates a new delete tool.
	 */
	public DeleteTool(AutomatonPane view, AutomatonDrawer drawer) {
		super(view, drawer);
	}

	/**
	 * Gets the tool tip for this tool.
	 * 
	 * @return the tool tip for this tool
	 */
	public String getToolTip() {
		return "Deleter";
	}

	/**
	 * Returns the tool icon.
	 * 
	 * @return the delete tool icon
	 */
	protected Icon getIcon() {
		java.net.URL url = getClass().getResource("/ICON/delete.gif");
		return new javax.swing.ImageIcon(url);
	}

	/**
	 * Returns the key stroke to switch to this tool, the D key.
	 * 
	 * @return the key stroke to switch to this tool
	 */
	public KeyStroke getKey() {
		return KeyStroke.getKeyStroke('d');
	}

	/**
	 * When the user clicks, we delete either the state or, if no state, the
	 * transition found at this point. If there's nothing at this point, nothing
	 * happens.
	 * 
	 * @param event
	 *            the mouse event
	 */
	public void mouseClicked(MouseEvent event) {
		if (getDrawer().getAutomaton().getEnvironmentFrame() !=null)
    		((AutomatonEnvironment)getDrawer().getAutomaton().getEnvironmentFrame().getEnvironment()).saveStatus();
		State state = getDrawer().stateAtPoint(event.getPoint());
		if (state != null) {
			getAutomaton().removeState(state);
			getView().repaint();
			return;
		}
		Transition trans = getDrawer().transitionAtPoint(event.getPoint());
		if (trans != null) {
			getAutomaton().removeTransition(trans);
			getView().repaint();
		}
	}
}
