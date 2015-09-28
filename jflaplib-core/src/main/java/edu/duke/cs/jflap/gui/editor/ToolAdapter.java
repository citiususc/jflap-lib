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

import edu.duke.cs.jflap.gui.SuperMouseAdapter;
import java.awt.event.*;
import edu.duke.cs.jflap.gui.viewer.AutomatonPane;

/**
 * This is a simple mouse adapter that simply forwards all mouse actions along
 * to another adapter set by the <CODE>setAdapter</CODE> method.
 * 
 * @author Thomas Finley
 */

public class ToolAdapter extends SuperMouseAdapter {
	/**
	 * Instantiates a tool adapter.
	 * 
	 * @param pane
	 *            the automaton pane this tool adapter is listening to
	 */
	public ToolAdapter(AutomatonPane pane) {
		this.pane = pane;
	}

	/**
	 * Sets a new adapter to be the adapter.
	 * 
	 * @param adapter
	 *            the new adapter
	 */
	public void setAdapter(SuperMouseAdapter adapter) {
		this.adapter = adapter;
	}

	/**
	 * Invoked when a mouse button is clicked on a component.
	 * 
	 * @param event
	 *            the MouseEvent to process
	 */
	public void mouseClicked(MouseEvent event) {
		adapter.mouseClicked(event);
	}

	/**
	 * Invoked when the mouse enters a component.
	 * 
	 * @param event
	 *            the MouseEvent to process
	 */
	public void mouseEntered(MouseEvent event) {
		adapter.mouseEntered(event);
	}

	/**
	 * Invoked when the mouse exits a component.
	 * 
	 * @param event
	 *            the MouseEvent to process
	 */
	public void mouseExited(MouseEvent event) {
		adapter.mouseExited(event);
	}

	/**
	 * Invoked when a mouse button is held down on a component.
	 * 
	 * @param event
	 *            the MouseEvent to process
	 */
	public void mousePressed(MouseEvent event) {
		adapter.mousePressed(event);
	}

	/**
	 * Invoked when a mouse button is released on a component.
	 * 
	 * @param event
	 *            the MouseEvent to process
	 */
	public void mouseReleased(MouseEvent event) {
		adapter.mouseReleased(event);
	}

	/**
	 * Invoked when a mouse is dragged over this component with a button down.
	 * 
	 * @param event
	 *            the MouseEvent to process
	 */
	public void mouseDragged(MouseEvent event) {
		adapter.mouseDragged(event);
	}

	/**
	 * Invoked when a mouse is moved over this component with no buttons down.
	 * 
	 * @param event
	 *            the MouseEvent to process
	 */
	public void mouseMoved(MouseEvent event) {
		adapter.mouseMoved(event);
	}

	/** The current adapter */
	private SuperMouseAdapter adapter = new SuperMouseAdapter() {
	};

	/** The automaton pane that this tool adapter is part of. */
	private AutomatonPane pane = null;
}
