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

import javax.swing.Icon;
import javax.swing.KeyStroke;

import edu.duke.cs.jflap.automata.State;
/**
 * The Block Transition tool works the same way as a normal transition, except it is between
 * two blocks.
 * @author Bart Bressler and Stephen Reading
 *
 */
public class BlockTransitionTool extends TransitionTool {
	/**
	 * The constructor to create a Block Transition Tool
	 * 
	 * @param view The view we are drawing the automaton in
	 * @param drawer The automaton drawer itself
	 * @param creator the creator we are using to draw the transitions.
	 */
	public BlockTransitionTool(AutomatonPane view, AutomatonDrawer drawer,
		TransitionCreator creator) {
		super(view, drawer);
		this.creator = creator;
	}

	/**
	 * Returns the keystroke to switch to this tool, the T key.
	 * 
	 * @return the keystroke to switch to this tool
	 */
	public KeyStroke getKey() {
		return KeyStroke.getKeyStroke('T');
	}
	
	/**
	 * Instantiates a new BlockTransition tool. The transition creator is
	 * obtained from whatever is returned from the transition creator factory.
	 * 
	 * @see edu.duke.cs.jflap.gui.editor.TransitionCreator#creatorForAutomaton
	 */
	public BlockTransitionTool(AutomatonPane view, AutomatonDrawer drawer) {
		super(view, drawer);
		this.creator = TransitionCreator.creatorForAutomaton(getAutomaton(),
				getView());
	}

	/**
	 * When we release the mouse, a transition from the start state to this
	 * released state must be formed.
	 * 
	 */
	public void mouseReleased(MouseEvent event) {
		// Did we even start at a state?
		if (first == null)
			return;
		State state = getDrawer().stateAtPoint(event.getPoint());
		if (state != null) {
			if (creator instanceof TMTransitionCreator) {
				TMTransitionCreator tmCreator = (TMTransitionCreator) creator;
				tmCreator.setBlockTransition(true);
				tmCreator.createTransition(first, state);
			}
		}
		first = null;
		getView().repaint();
	}

	/**
	 * Gets the tool tip.
	 * 
	 * @return the tool tip for this tool
	 */
	public String getToolTip() {
		return "BlockTransition Creator";
	}

	/**
	 * Returns the icon - used to draw the button in the toolbar.
	 * 
	 * @return the transition tool icon
	 */
	protected Icon getIcon() {
		java.net.URL url = getClass().getResource("/ICON/blockTransition.gif");
		return new javax.swing.ImageIcon(url);
	}

}
