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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JToggleButton;
import edu.duke.cs.jflap.automata.Configuration;

/**
 * This is a toggle button that displays a configuration inside of it.
 * 
 * @author Thomas Finley
 */

public class ConfigurationButton extends JToggleButton {
	/**
	 * Instantiates a configuration button with a configuration with the default
	 * setting of the state being labeled as NORMAL.
	 * 
	 * @param config
	 *            the configuration to set the configuration button for
	 */
	public ConfigurationButton(Configuration config) {
		this(config, NORMAL);
	}

	/**
	 * Instantiates a configuration button with a configuration and its state.
	 * 
	 * @param config
	 *            the configuration to set the configuration button for
	 * @param state
	 *            the state to create the configuration button with, with states
	 *            as defined in the class <CODE>ConfigurationPane</CODE>
	 */
	public ConfigurationButton(Configuration config, int state) {
		super();
		this.config = config;
		icon = ConfigurationIconFactory.iconForConfiguration(config);
		// this.setIcon(icon);

		this.setHorizontalTextPosition(CENTER);
		this.setVerticalTextPosition(TOP);

		this.setState(state);

		// Set the preferred size of this button to be the size of the
		// icon plus whatever padding is the default for this button.
		this.setPreferredSize(new Dimension(0, PADDING * 2
				+ icon.getIconHeight()));
		// this.setMinimumSize(new Dimension(0,PADDING*2+icon.getIconHeight()));
		/*
		 * this.setMaximumSize(new Dimension(PADDING*2+icon.getIconWidth(),
		 * PADDING*2+icon.getIconHeight()));
		 */
	}

	/**
	 * Sets the state of this configuration button.
	 * 
	 * @param state
	 *            the new state, for example, <CODE>NORMAL</CODE>
	 */
	public void setState(int state) {
		// Is the correct state?
		if (config.isAccept())
			state = ACCEPT;
		if (state < 0 || state >= TEXT.length)
			state = NORMAL;
		// setText(TEXT[state]);
		// setForeground(STATE_COLOR[state]);
		// setBackground(STATE_COLOR[state]);
		this.state = state;
	}

	/**
	 * Returns the configuration represented by this configuration button.
	 * 
	 * @return the configuration represented by this configuration button
	 */
	public Configuration getConfiguration() {
		return config;
	}

	/**
	 * Paints this component.
	 * 
	 * @param g
	 *            the graphics object to paint this component to
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(STATE_COLOR[state]);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.translate(PADDING, PADDING);

		icon.paintConfiguration(this, (Graphics2D) g, getWidth() - PADDING * 2,
				getHeight() - PADDING * 2);
		g.translate(-PADDING, -PADDING);
	}

	/** The state of this object. */
	protected int state = NORMAL;

	/** The configuration represented by this object. */
	private Configuration config = null;

	/** The drawer of the configuration icon. */
	private ConfigurationIcon icon = null;

	/** States one can set on a configuration button. */
	public static final int NORMAL = 0, ACCEPT = 1, REJECT = 2, FREEZE = 3,
			FOCUSED = 4;

	/** The texts that correspond to the states of an object. */
	private static final String[] TEXT = { "Normal", "Accept", "Reject",
			"Freeze", "Focused" };

	/**
	 * State colors. For example, STATE_COLOR[ACCEPT] is the state color for a
	 * configuration in the accept state.
	 */
	public static final Color[] STATE_COLOR = { new Color(0, 0, 0, 0),
			new Color(0, 150, 0, 80), new Color(255, 0, 0, 80),
			new Color(100, 100, 255, 80), new Color(255, 255, 0, 80) };

	/*
	 * public static final Color[] STATE_COLOR = {Color.black, new
	 * Color(0,150,0), Color.red, new Color(100,100,255)};
	 */
	/** The padding for the icon drawing. */
	protected static final int PADDING = 5;

}
