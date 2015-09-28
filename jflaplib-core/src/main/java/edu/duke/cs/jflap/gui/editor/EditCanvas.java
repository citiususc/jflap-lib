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

import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 * An <CODE>EditCanvas</CODE> is an extension of <CODE>AutomatonPane</CODE>
 * more suitable for use as a place where automatons may be edited.
 * 
 * @author Thomas Finley
 */

public class EditCanvas extends AutomatonPane {
	/**
	 * Instantiates a new <CODE>EditCanvas</CODE>.
	 * 
	 * @param drawer
	 *            the automaton drawer
	 */
	public EditCanvas(AutomatonDrawer drawer) {
		this(drawer, false);
	}

	/**
	 * Instantiates a new <CODE>EditCanvas</CODE>.
	 * 
	 * @param drawer
	 *            the automaton drawer
	 * @param fit
	 *            <CODE>true</CODE> if the automaton should change its size to
	 *            fit in the automaton; this can be very annoying
	 */
	public EditCanvas(AutomatonDrawer drawer, boolean fit) {
		super(drawer, fit);
	}

	/**
	 * Sets the toolbar for this edit canvas.
	 * 
	 * @param toolbar
	 *            the toolbar for this edit canvas
	 */
	public void setToolBar(ToolBar toolbar) {
		this.toolbar = toolbar;
	}
	

	

	/**
	 * Paints the component. In addition to what the automaton pane does, this
	 * also calls the current tool's draw method.
	 * 
	 * @param g
	 *            the graphics object to draw upon
	 */
	public void paintComponent(Graphics g) {
		if (getCreator().automaton.getEnvironmentFrame() !=null)
		if (!((AutomatonEnvironment)(getCreator().automaton.getEnvironmentFrame().getEnvironment())).shouldPaint()) 
			return;
//		EDebug.print(Thread.currentThread().getName());
		super.paintComponent(g);
		toolbar.drawTool(g);
		Graphics2D g2 = (Graphics2D) g;
		double newXScale = 1.0/transform.getScaleX();
		double newYScale = 1.0/transform.getScaleY();
		g2.scale(newXScale, newYScale);
		g2.translate(-transform.getTranslateX(), -transform.getTranslateY());
	}

	/** The toolbar that is used for this edit canvas. */
	private ToolBar toolbar;
	
}
