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





package edu.duke.cs.jflap.gui.viewer;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Area;
import edu.duke.cs.jflap.automata.Transition;

/**
 * An invisible curved arrow is a curved arrow where the actual line and arrow
 * are not displayed, only the label. This is used for transitions where there
 * are multiple transitions from one state to another.
 * 
 * @author Thomas Finley
 */

public class InvisibleCurvedArrow extends CurvedArrow {
	/**
	 * Instantiates an <CODE>InvisibleCurvedArrow</CODE> object.
	 * 
	 * @param x1
	 *            the x coordinate of the start point
	 * @param y1
	 *            the y coordinate of the start point
	 * @param x2
	 *            the x coordinate of the end point
	 * @param y2
	 *            the y coordinate of the end point
	 * @param curvy
	 *            the curvi-ness factor; 0 will create a straight line; 1 and -1
	 *            are rather curvy
	 */
	public InvisibleCurvedArrow(int x1, int y1, int x2, int y2, float curvy, Transition t) {
		super(x1, y1, x2, y2, curvy, t);
	}

	/**
	 * Instantiates an <CODE>InvisibleCurvedArrow</CODE> object.
	 * 
	 * @param start
	 *            the start point
	 * @param end
	 *            the end point
	 * @param curvy
	 *            the curvi-ness factor; 0 will create a straight line; 1 and -1
	 *            are rather curvy
	 */
	public InvisibleCurvedArrow(Point start, Point end, float curvy, Transition t) {
		super(start, end, curvy, t);
	}

	/**
	 * Draws the arrow on the indicated graphics environment.
	 * 
	 * @param g
	 *            the graphics to draw this arrow upon
	 */
	public void draw(Graphics2D g) {
		if (needsRefresh)
			refreshCurve();
		drawText(g);
	}

	/**
	 * Draws a highlight of the curve. This will only highlight the label.
	 * 
	 * @param g
	 *            the graphics to draw the highlight of the curve upon
	 */
	public void drawHighlight(Graphics2D g) {
		if (needsRefresh)
			refreshCurve();
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setStroke(new java.awt.BasicStroke(6.0f));
		g2.setColor(HIGHLIGHT_COLOR);
		g2.transform(affineToText);
		g2.fill(bounds);
		g2.dispose();
	}

	/**
	 * Returns the bounds.
	 * 
	 * @return the rectangular bounds for this curved arrow
	 */
	public Rectangle2D getBounds() {
		Area area = new Area(bounds);
		area.transform(affineToText);
		return area.getBounds();
	}

	/**
	 * Determines if a point is on/near the curved arrow. Since here the arrow
	 * is not displayed, only points on the label are identified.
	 * 
	 * @param point
	 *            the point to check
	 * @param fudge
	 *            the radius around the point that should be checked for the
	 *            presence of the curve
	 * @return <TT>true</TT> if the point is on the curve within a certain
	 *         fudge factor, <TT>false</TT> otherwise
	 */
	public boolean isNear(Point point, int fudge) {
		if (needsRefresh)
			refreshCurve();
		try {
			if (bounds.contains(affineToText.inverseTransform(point, null)))
				return true;
		} catch (java.awt.geom.NoninvertibleTransformException e) {

		}
		return false;
	}
}
