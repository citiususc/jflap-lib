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





package edu.duke.cs.jflap.gui.sim;

import edu.duke.cs.jflap.gui.viewer.StateDrawer;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

import javax.swing.Icon;

import edu.duke.cs.jflap.automata.Automaton;
import edu.duke.cs.jflap.automata.Configuration;

/**
 * The <CODE>ConfigurationIcon</CODE> is a general sort of icon that can be
 * used to draw transitions. The <CODE>Icon</CODE> can then be added as the
 * part of any sort of object.
 * 
 * @author Thomas Finley
 */

public abstract class ConfigurationIcon implements Icon {
	/**
	 * Instantiates a new <CODE>ConfigurationIcon</CODE>.
	 * 
	 * @param configuration
	 *            the configuration that is represented
	 */
	public ConfigurationIcon(Configuration configuration) {
		this.configuration = configuration;
		this.automaton = configuration.getCurrentState().getAutomaton();
	}

	/**
	 * Returns the preferred width of the icon. Subclasses should attempt to
	 * draw within these bounds, and can override if they'd like a bit more
	 * space to play with.
	 * 
	 * @return the default preferred width is 150 pixels
	 */
	public int getIconWidth() {
		return 400;
	}

	/**
	 * Returns the preferred height of the icon, which is just enought to draw
	 * the state.
	 */
	public int getIconHeight() {
		return STATE_RADIUS * 2;
	}

	/**
	 * Returns the <CODE>Configuration</CODE> drawn by this icon.
	 * 
	 * @return the <CODE>Configuration</CODE> drawn by this icon
	 */
	public Configuration getConfiguration() {
		return configuration;
	}

	/**
	 * Paints the graphical representation of a configuration on this icon
	 * 
	 * @param c
	 *            the component this icon is drawn on
	 * @param g
	 *            the graphics object to draw to
	 * @param x
	 *            the start <CODE>x</CODE> coordinate to draw at
	 * @param y
	 *            the start <CODE>y</CODE> coordinate to draw at
	 */
	public void paintIcon(Component c, Graphics g, int x, int y) {
		Graphics2D g2 = (Graphics2D) g.create();
		g2.translate(x, y);
		// Draws the state.
		paintConfiguration(c, g2, getIconWidth(), getIconHeight());
		g2.translate(-x, -y);
	}

	/**
	 * The general method for painting the rest of the configuration. The
	 * subclasses should override to do whatever it is they do. At this point
	 * the state has already been drawn. The areas where the state has NOT
	 * already been drawn is defined by the static variables <CODE>RIGHT_STATE</CODE>
	 * and <CODE>BELOW_STATE</CODE>. By default this method paints the state.
	 * 
	 * @param c
	 *            the component this icon is drawn on
	 * @param g
	 *            the <CODE>Graphics2D</CODE> object to draw on
	 * @param width
	 *            the width to paint the configuration in
	 * @param height
	 *            the height to paint the configuration in
	 */
	public void paintConfiguration(Component c, Graphics2D g, int width,
			int height) {

//		if (configuration.getCurrentState().getParentBlock() != null) {
//			// Draw the building block where the state would normally be
//			STATE_DRAWER.drawState(g, automaton, configuration
//					.getCurrentState().getParentBlock(), STATE_POINT);
//			// Draw the current state
//			STATE_DRAWER.drawState(g, automaton, configuration
//					.getCurrentState(), BB_POINT);
//			// Draw the arrow to "connect" the two
//			// ARROW_DRAWER.draw(g);
//		} else {
			STATE_DRAWER.drawState(g, automaton, configuration
					.getCurrentState(), STATE_POINT);
//		}
	}

	/** The configuration that is being drawn. */
	private Configuration configuration;

	/** The automaton that this configuration belongs to. */
	private Automaton automaton;

	/** The radius of states drawn here. */
	protected static final int STATE_RADIUS = 13;

	/** The state drawer. */
	private static final StateDrawer STATE_DRAWER = new StateDrawer(
			STATE_RADIUS);

	/** The point where states are drawn. */
	private static final Point STATE_POINT = new Point(STATE_RADIUS * 2,
			STATE_RADIUS);

	/** If there is a building block, this is where you draw the current state */
	private static final Point BB_POINT = new Point(
			(int) (STATE_POINT.getX() * 3.5), (int) STATE_POINT.getY());

	/** Draws the arrow from the building block to the current state */
	// private static final CurvedArrow ARROW_DRAWER =
	// new CurvedArrow(new Point((int) (STATE_POINT.getX() + STATE_RADIUS),
	// (int) STATE_POINT.getY()),
	// new Point ((int) (BB_POINT.getX() - STATE_RADIUS), (int)
	// BB_POINT.getY()), 0);
	/**
	 * For the reference of subclasses, the point on the upper left of the
	 * region to the right of the state.
	 */
	protected static final Point RIGHT_STATE = new Point(STATE_RADIUS * 3, 0);

	/** The point on the upper left of the region below the state. */
	protected static final Point BELOW_STATE = new Point(0, STATE_RADIUS * 2);
}
