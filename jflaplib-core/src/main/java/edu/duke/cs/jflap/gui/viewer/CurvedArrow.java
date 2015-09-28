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

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.*;
import java.awt.image.BufferedImage;

import edu.duke.cs.jflap.automata.Transition;

/**
 * This is a simple class for storing and drawing a curved line with possible
 * arrow heads on it.
 * 
 * @author Thomas Finley
 */

public class CurvedArrow {
	/**
	 * Instantiates a <CODE>CurvedArrow</CODE> object.
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
	public CurvedArrow(int x1, int y1, int x2, int y2, float curvy, Transition t) {
		curve = new QuadCurve2D.Float();
		start = new Point();
		end = new Point();
		control = new Point();
		setStart(x1, y1);
		setEnd(x2, y2);
		setCurvy(curvy);
        myTransition = t;
		refreshCurve();
	}

	/**
	 * Instantiates a <CODE>CurvedArrow</CODE> object.
	 * 
	 * @param start
	 *            the start point
	 * @param end
	 *            the end point
	 * @param curvy
	 *            the curvi-ness factor; 0 will create a straight line; 1 and -1
	 *            are rather curvy
	 */
	public CurvedArrow(Point start, Point end, float curvy, Transition t) {
		curve = new QuadCurve2D.Float();
		setStart(start);
		setEnd(end);
		control = new Point();
		setCurvy(curvy);
        myTransition = t;
		refreshCurve();
	}

	/**
	 * Sets the start point.
	 * 
	 * @param x1
	 *            the x coordinate of the start point
	 * @param y1
	 *            the y coordinate of the start point
	 */
	public void setStart(int x1, int y1) {
		start.x = x1;
		start.y = y1;
		needsRefresh = true;
	}

	/**
	 * Sets the start point.
	 * 
	 * @param start
	 *            the new start point
	 */
	public void setStart(Point start) {
		this.start = start;
		needsRefresh = true;
	}

	/**
	 * Sets the end point.
	 * 
	 * @param x2
	 *            the x coordinate of the end point
	 * @param y2
	 *            the y coordinate of the end point
	 */
	public void setEnd(int x2, int y2) {
		end.x = x2;
		end.y = y2;
		needsRefresh = true;
	}

	/**
	 * Sets the end point.
	 * 
	 * @param end
	 *            the new end point
	 */
	public void setEnd(Point end) {
		this.end = end;
		needsRefresh = true;
	}

	/**
	 * Sets the "curvy-ness" factor.
	 * 
	 * @param curvy
	 *            the new curvy factor
	 */
	public void setCurvy(float curvy) {
		this.curvy = curvy;
		needsRefresh = true;
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
		g.draw(curve); // Draws the main part of the arrow.
		drawArrow(g, end, control); // Draws the arrow head.
		drawText(g);
	}

    public void drawControlPoint(Graphics2D g){ //adjust later to center of circle = focus point
        g.drawOval((int)curve.getCtrlX() - 5, (int)curve.getCtrlY() - 5, 10,10);
    }

	/**
	 * Draws a highlight of the curve.
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
		g2.draw(curve);
		g2.transform(affineToText);
		g2.fill(bounds);
		g2.dispose();
	}

	/**
	 * Draws the text on the high point of the arc. The text drawn is none other
	 * than the label for this object, as retrieved from <CODE>getLabel</CODE>.
	 * 
	 * @param g
	 *            the graphics object to draw the text upon
	 */
	public void drawText(Graphics2D g) {
		// We don't want to corrupt the graphics environs with our
		// affine transforms!
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.transform(affineToText);

		// What about the text label?
		FontMetrics metrics = g2.getFontMetrics();
		bounds = metrics.getStringBounds(getLabel(), g2);
		// Will the label appear to be upside down?
		boolean upsideDown = end.x < start.x;
		float dx = (float) bounds.getWidth() / 2.0f;
		float dy = (curvy < 0.0f) ^ upsideDown ? metrics.getAscent() : -metrics
				.getDescent();
		bounds.setRect(bounds.getX() - dx, bounds.getY() + dy, bounds
				.getWidth(), bounds.getHeight());
		for (int i = 0; i < label.length(); i += CHARS_PER_STEP) {
			String sublabel = label.substring(i, Math.min(i + CHARS_PER_STEP,
					label.length()));
			g2.drawString(sublabel, -dx, dy);
			dx -= metrics.getStringBounds(sublabel, g2).getWidth();
		}
		// g2.drawString(label, -dx, dy);
		g2.dispose();
		/*
		 * if (GRAPHICS == null) { GRAPHICS = g.create(); METRICS =
		 * GRAPHICS.getFontMetrics(); }
		 */
	}

	/**
	 * Sets the label that will be drawn on the high arc point.
	 * 
	 * @param label
	 *            the new label for the arrow
	 */
	public void setLabel(String label) {
		this.label = label;
		// if (GRAPHICS == null) return;
		bounds = METRICS.getStringBounds(getLabel(), GRAPHICS);
		boolean upsideDown = end.x < start.x;
		float dx = (float) bounds.getWidth() / 2.0f;
		float dy = (curvy < 0.0f) ^ upsideDown ? METRICS.getAscent() : -METRICS
				.getDescent();
		bounds.setRect(bounds.getX() - dx, bounds.getY() + dy, bounds
				.getWidth(), bounds.getHeight());
		//System.out.println("Setting label" + label);
	}

	/**
	 * Returns the label for this arrow.
	 * 
	 * @return the label for this arrow
	 */
	public String getLabel() {
		return this.label;

	}

	/**
	 * Draws an arrow head on the graphics object. The arrow geometry is based
	 * on the point of its head as well as another point, which the arrow is
	 * defined as facing away from. This arrow head has no body.
	 * 
	 * @param g
	 *            the graphics object to draw upon
	 * @param head
	 *            the point that is the point of the head of the arrow
	 * @param away
	 *            the point opposite from where the arrow is pointing, a point
	 *            along the line segment extending from the head backwards from
	 *            the head if this were an arrow with a line trailing the head
	 */
	private void drawArrow(Graphics g, Point head, Point away) {
		int endX, endY;
		double angle = Math.atan2((double) (away.x - head.x),
				(double) (away.y - head.y));
		angle += ARROW_ANGLE;
		endX = ((int) (Math.sin(angle) * ARROW_LENGTH)) + head.x;
		endY = ((int) (Math.cos(angle) * ARROW_LENGTH)) + head.y;
		g.drawLine(head.x, head.y, endX, endY);
		angle -= 2 * ARROW_ANGLE;
		endX = ((int) (Math.sin(angle) * ARROW_LENGTH)) + head.x;
		endY = ((int) (Math.cos(angle) * ARROW_LENGTH)) + head.y;
		g.drawLine(head.x, head.y, endX, endY);
	}

	/**
	 * Refreshes the curve object.
	 */
	public void refreshCurve() {
//        System.out.println("Curve refreshing");
		needsRefresh = false;

        double lengthx = end.x - start.x;
        double lengthy = end.y - start.y;
        double centerx = ((double) (start.x + end.x)) / 2.0;
        double centery = ((double) (start.y + end.y)) / 2.0;

        double length = Math.sqrt(lengthx * lengthx + lengthy * lengthy);
        double factorx = length == 0.0 ? 0.0 : lengthx / length;
        double factory = length == 0.0 ? 0.0 : lengthy / length;

        if (myTransition.getControl() == null){

            control.x = (int) (centerx + curvy * HEIGHT * factory);
            control.y = (int) (centery - curvy * HEIGHT * factorx);
            high.x = (int) (centerx + curvy * HEIGHT * factory / 2.0);
            high.y = (int) (centery - curvy * HEIGHT * factorx / 2.0);

        }
        else{
            control.x = (int) myTransition.getControl().x;
            control.y = (int) myTransition.getControl().y;

            //take the vector from the center to the control, and take half of that
            double xt = control.x - centerx;
            double yt = centery - control.y;

            high.x = (int) (centerx + xt / 2); 
            high.y = (int) (centery - yt / 2);
        }

            curve.setCurve((float) start.x, (float) start.y, (float) control.x,
                    (float) control.y, (float) end.x, (float) end.y);

            affineToText = new AffineTransform();
            affineToText.translate(high.x, high.y);
            affineToText.rotate(Math.atan2(lengthy, lengthx));
            if (end.x < start.x)
                affineToText.rotate(Math.PI);
	}

	/**
	 * Returns the bounds.
	 * 
	 * @return the rectangular bounds for this curved arrow
	 */
	public Rectangle2D getBounds() {
		if (needsRefresh)
			refreshCurve();
		Rectangle2D b = curve.getBounds();
		Area area = new Area(bounds);
		area.transform(affineToText);
		b.add(area.getBounds());
		return b;
	}

	/**
	 * Determines if a point is on/near the curved arrow.
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

		} catch (NullPointerException e) {
			System.err.println(e + " : " + bounds + " : " + affineToText);
			return false;
		}
		return intersects(point, fudge, curve);
	}

	/**
	 * Checks if something is on the line. If it appears to be, then it
	 * subdivides the curve into halves and tries again recursively until the
	 * flatness of the curve is less than the fudge. Frankly, I am a fucking
	 * genius. I am one of two people in this department that could have
	 * possibly thought of this.
	 * 
	 * @param point
	 *            the point to check intersection
	 * @param fudge
	 *            the "fudge" factor
	 * @param c
	 *            the curve we're checking for intersection with
	 * @return <TT>true</TT> if the point is on the curve within a certain
	 *         fudge factor, <TT>false</TT> otherwise
	 */
	private boolean intersects(Point point, int fudge, QuadCurve2D.Float c) {
		if (!c.intersects(point.x - fudge, point.y - fudge, fudge << 1,
				fudge << 1))
			return false;
		if (c.getFlatness() < fudge)
			return true;
		QuadCurve2D.Float f1 = new QuadCurve2D.Float(), f2 = new QuadCurve2D.Float();
		c.subdivide(f1, f2);
		return intersects(point, fudge, f1) || intersects(point, fudge, f2);
	}
	
	public QuadCurve2D getCurve(){
		return curve;
	}


	/** The start, end, and single control points. */
	protected Point start, end, control;

	/** The high point of the arc. */
	private Point high = new Point();

	/** The "curvy-ness" factor. */
	protected float curvy;

	/**
	 * The quad-curve that controls the shape of the long part of the arrow.
	 */
	protected QuadCurve2D.Float curve;

	/**
	 * <CODE>true</CODE> if the curve needs to be refreshed, <CODE>false</CODE>
	 * otherwise.
	 */
	protected boolean needsRefresh = true;

	/** Arrow flags. */
	protected boolean startArrow = false, endArrow = false;

	/** The label for this arrow. */
	protected String label = "";

	/** The angle for the arrow heads. */
	private static double ARROW_ANGLE = Math.PI / 10;

	/** The length of the arrow head edges. */
	private static double ARROW_LENGTH = 15;

	/** The affine transform for "turning" text. */
	private static AffineTransform AFFINE_TURN_180;

	/** The stored bounds. */
	protected java.awt.geom.Rectangle2D bounds = new java.awt.Rectangle(0, 0);

	/** The affine transform for getting us to the text space. */
	protected AffineTransform affineToText;

	/** The number of characters to draw in each step. */
	private static final int CHARS_PER_STEP = 4;

	/** A graphics object. */
	protected static Graphics GRAPHICS = null;

	/** A font metrics object. */
	protected static FontMetrics METRICS;

	static {
		AFFINE_TURN_180 = new AffineTransform();
		AFFINE_TURN_180.rotate(Math.PI);

		BufferedImage image = new BufferedImage(1, 1,
				BufferedImage.TYPE_INT_RGB);
		GRAPHICS = image.getGraphics();
		METRICS = GRAPHICS.getFontMetrics();
	}

	/** The high factor of a control point. */
	private static double HEIGHT = 30.0;

	public static java.awt.Color HIGHLIGHT_COLOR = new java.awt.Color(255, 0,
			0, 128);

    public Transition myTransition;
}
