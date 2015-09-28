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





package edu.duke.cs.jflap.gui.grammar;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.Icon;

/**
 * This is an icon that draws a simple arrow pointing to the right.
 * 
 * @author Thomas Finley
 */

public class ArrowIcon implements Icon {
	/**
	 * Instantiates a new <CODE>ArrowIcon</CODE>.
	 * 
	 * @param width
	 *            the width of this icon
	 * @param height
	 *            the height of this icon
	 */
	public ArrowIcon(int width, int height) {
		this.width = width;
		this.height = height;
	}

	/**
	 * Returns the icon's height.
	 * 
	 * @return the icon's height
	 */
	public int getIconHeight() {
		return height;
	}

	/**
	 * Returns the icon's width.
	 * 
	 * @return the icon's width
	 */
	public int getIconWidth() {
		return width;
	}

	/**
	 * Paints the arrow.
	 */
	public void paintIcon(Component c, Graphics g, int x, int y) {
		g.setColor(Color.black);
		g.drawLine(x, y + height / 2, x + width, y + height / 2);
		g.drawLine(x + width - height / 2, y, x + width, y + height / 2);
		g.drawLine(x + width - height / 2, y + height, x + width, y + height
				/ 2);
	}

	/** The width of the icon. */
	private int width;

	/** The height of the icon. */
	private int height;
}
