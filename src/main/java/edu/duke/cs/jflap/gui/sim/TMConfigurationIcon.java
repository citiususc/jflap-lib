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
import java.awt.Component;
import java.awt.Graphics2D;

import edu.duke.cs.jflap.automata.Configuration;
import edu.duke.cs.jflap.automata.turing.TMConfiguration;
import edu.duke.cs.jflap.automata.turing.Tape;

/**
 * This is a configuration icon for configurations related to finite state
 * automata. These sorts of configurations are defined only by the state that
 * the automata is current in, plus the input left.
 * 
 * @author Thomas Finley
 */

public class TMConfigurationIcon extends ConfigurationIcon implements
		TuringConstants {
	/**
	 * Instantiates a new <CODE>TMConfigurationIcon</CODE>.
	 * 
	 * @param configuration
	 *            the TM configuration that is represented
	 */
	public TMConfigurationIcon(Configuration configuration) {
		super(configuration);
		config = (TMConfiguration) configuration;
	}

	/**
	 * Returns the height of this icon.
	 * 
	 * @return the height of this icon
	 */
	public int getIconHeight() {
		// Why not...
		return super.getIconHeight() + 25 * config.getTapes().length;
	}

	/**
	 * This will paint a sort of "torn tape" object that shows the current
	 * contents and position of the tape.
	 * 
	 * @param c
	 *            the component this icon is drawn on
	 * @param g
	 *            the <CODE>Graphics2D</CODE> object to draw on
	 */
	public void paintConfiguration(Component c, Graphics2D g, int width,
			int height) {
		if (c != null)
			super.paintConfiguration(c, g, width, height);
		float position = BELOW_STATE.y + 5.0f;
		int headx = BELOW_STATE.x + width / 2;
		int heady = BELOW_STATE.y + 5;

		Tape[] tapes = config.getTapes();

		for (int i = 0; i < tapes.length; i++) {
			float tornHeight = Torn.paintString(g, FIX + tapes[i].getContents()
					+ FIX, BELOW_STATE.x, position, Torn.TOP, width, true,
					true, tapes[i].getTapeHead() + FIX.length());
			g.setColor(Color.black);
			g.drawLine(headx, heady, headx - SIZE_HEAD, heady - SIZE_HEAD);
			g.drawLine(headx, heady, headx + SIZE_HEAD, heady - SIZE_HEAD);
			position += tornHeight + 8f;
		}
		position -= 8f;
	}

	/** The turing machine configuration. */
	private TMConfiguration config;
}
