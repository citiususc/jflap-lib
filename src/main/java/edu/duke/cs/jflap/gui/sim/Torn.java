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

import java.awt.geom.GeneralPath;
import java.awt.Graphics2D;
import java.awt.FontMetrics;
import java.awt.Color;

/**
 * This is a rather silly factory class that returns shape objects that look
 * like "torn tape," for objects that need to express a potentially very large
 * structure in a very small amount of space. These objects are here referred to
 * often as "torn". I've nouned torn, like I just verbed noun.
 * 
 * @author Thomas Finley
 */

public class Torn {
	/**
	 * Returns a shape whose stroke and fill will give the impression of a "torn
	 * tape" sort of structure. The point <CODE>(x,y)</CODE> will be the upper
	 * left corner of the torn tape, the structure will be completely enclosed
	 * in the bounds <CODE>(x,y,x+width,x+height)</CODE>.
	 * 
	 * @param width
	 *            the width of the torn tape
	 * @param x
	 *            the <I>x</I> coordinate of the torn
	 * @param y
	 *            the <I>y</I> coordinate of the torn
	 * @param height
	 *            the height of the torn tape
	 * @param left
	 *            <CODE>true</CODE> if this tape is "torn" on the left end
	 * @param right
	 *            <CODE>true</CODE> if this tape is "torn" on the right end
	 */
	public static GeneralPath getTorn(float x, float y, float width,
			float height, boolean left, boolean right) {
		GeneralPath path = new GeneralPath();
		path.moveTo(x, y);
		path.lineTo(x + width, y);
		if (right) {
			path.quadTo(x + width - height / 4.0f, y + height / 4.0f,
					x + width, y + height / 2.0f);
			path.quadTo(x + width - height / 4.0f, y + 3.0f * height / 4.0f, x
					+ width, y + height);
		} else
			path.lineTo(x + width, y + height);
		path.lineTo(x, y + height);
		if (left) {
			path.quadTo(x + height / 4.0f, y + 3.0f * height / 4.0f, x, y
					+ height / 2.0f);
			path.quadTo(x + height / 4.0f, y + height / 4.0f, x, y);
		}
		path.closePath();
		return path;
	}

	/**
	 * A given string is painted inside of a "Torn". The string is aligned with
	 * whatever part of the tape is NOT torn. If both ends are torn, the string
	 * is aligned in the center. If neither end is torn, the string is left
	 * aligned.
	 * 
	 * @param g
	 *            the graphics context to paint in
	 * @param string
	 *            the string to paint inside of a torn
	 * @param x
	 *            the x coordinate of the torn
	 * @param y
	 *            the y coordinate of the torn
	 * @param align
	 *            the alignment of the torn, as specified by either <CODE>Torn.TOP</CODE>
	 *            (i.e. <CODE>(x,y)</CODE> is the upper left), <CODE>Torn.MIDDLE</CODE>,
	 *            or <CODE>Torn.BOTTOM</CODE>.
	 * @param width
	 *            the width of the torn
	 * @param left
	 *            is the left end torn?
	 * @param right
	 *            is the right end torn?
	 * @param select
	 *            the character to draw as centered and selected, or -1 if no
	 *            character should be drawn as selected
	 * @return the height of the resulting drawn "torn"
	 */
	public static float paintString(Graphics2D g, String string, float x,
			float y, int align, float width, boolean left, boolean right,
			int select) {
		// Convert the y coordinate into the alignment into the upper
		// left corner.
		FontMetrics metrics = g.getFontMetrics();
		float toBaseline = PADDING + metrics.getAscent();
		float height = toBaseline + PADDING + metrics.getDescent();
		if (align == MIDDLE)
			y -= height * 0.5f;
		if (align == BOTTOM)
			y -= height;
		if (select > string.length())
			select = string.length();

		// Create the torn object.
		GeneralPath torn = getTorn(x, y, width, height, left, right);

		g.setColor(Color.white);
		g.fill(torn);

		// Prepare some measurements for putting the text in the right
		// place.
		float horizontalPadding = metrics.charWidth(' ');
		Graphics2D g2 = (Graphics2D) g.create();
		g2.clip(torn);
		float dx = 0.0f;
		if (left)
			dx = (float) metrics.getStringBounds(string, g2).getWidth() - width
					+ 2.0f * horizontalPadding;
		if (right)
			dx *= 0.5f; // Easy.
		if (select >= 0 && !left && right) {
			// Do that greying out and crap.
			String before = string.substring(0, select), after = string
					.substring(select);
			double bLength = metrics.getStringBounds(before, g2).getWidth();
			double aLength = metrics.getStringBounds(after, g2).getWidth();
			float aStart = bLength > width / 2.0f ? width / 2.0f
					: horizontalPadding + (float) bLength;
			float bStart = aStart - (float) bLength;
			g2.setColor(Color.gray);
			g2.drawString(before, x + bStart, y + toBaseline);
			g2.setColor(Color.black);
			g2.drawString(after, x + aStart, y + toBaseline);
		} else {
			if (select >= 0) {
				double l = metrics.getStringBounds(string.substring(0, select),
						g2).getWidth();
				double c = metrics.getStringBounds(
						string.substring(select, select + 1), g2).getWidth();
				g2.setColor(HIGHLIGHT_COLOR);
				g2.fillRect((int) (x + 0.5f * (width - c)), (int) y, (int) c,
						100);
				dx = (float) (l + 0.5 * (c - width) + horizontalPadding);
			}
			g2.setColor(Color.black);
			// We finally get to draw the string.
			g2.drawString(string, x + horizontalPadding - dx, y + toBaseline);
		}
		g2.dispose();
		// Finish by drawing the outline of the torn.
		g.setColor(Color.black);
		g.draw(torn);
		return height;
	}

	/** Alignment constants. */
	public static final int TOP = 0;

	public static final int MIDDLE = 1;

	public static final int BOTTOM = 2;

	/** What is the padding of the torn? */
	public static final float PADDING = 2.0f;

	/** What is the highlight color? */
	private static final Color HIGHLIGHT_COLOR = new Color(255, 0, 0, 128);
}
