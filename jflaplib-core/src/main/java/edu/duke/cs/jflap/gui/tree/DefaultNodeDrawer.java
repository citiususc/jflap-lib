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





package edu.duke.cs.jflap.gui.tree;

import edu.duke.cs.jflap.gui.environment.Universe;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.*;
import javax.swing.tree.TreeNode;

/**
 * A very simple <CODE>DefaultNodeDrawer</CODE> object.
 * 
 * @author Thomas Finley
 */

public class DefaultNodeDrawer implements NodeDrawer {
	/**
	 * This draws a node. The fill color is the color of the graphics object
	 * before this method was called.
	 * 
	 * @param g
	 *            the graphics object to draw the node on
	 * @param node
	 *            the node to draw
	 */
	public void draw(Graphics2D g, TreeNode node) {
		g.fill(NODE_SHAPE);
		Color c = g.getColor();
		g.setColor(Color.black);
		g.draw(NODE_SHAPE);

		String s = node.toString();
		if (s == null)
			s = "null";
		if (s.length() == 0)
			s = Universe.curProfile.getEmptyString();
		Rectangle2D bounds = getBounds(g, s);
		// Draw the label for the node.
		int dx = ((int) bounds.getWidth()) >> 1;
		int dy = ((int) -bounds.getY()) >> 1;
		g.drawString(s, -dx, dy);
		// Restore the color.
		g.setColor(c);
	}
	
	public void draw(Graphics2D g, TreeNode node, boolean dummy)
	{
		g.fill(new Ellipse2D.Float(-NODE_RADIUS,
				-NODE_RADIUS, NODE_RADIUS * 4.0f, NODE_RADIUS * 2.0f));
		Color c = g.getColor();
		g.setColor(Color.black);
		g.draw(new Ellipse2D.Float(-NODE_RADIUS,
				-NODE_RADIUS, NODE_RADIUS * 4.0f, NODE_RADIUS * 2.0f));

		String s = node.toString();
		if (s == null)
			s = "null";
		if (s.length() == 0)
			s = Universe.curProfile.getEmptyString();
		Rectangle2D bounds = getBounds(g, s);
		// Draw the label for the node.
		int dx = ((int) bounds.getWidth()) >> 2;
		int dy = ((int) -bounds.getY()) >> 1;
		g.drawString(s, -dx, dy);
		// Restore the color.
		g.setColor(c);
	}

	/**
	 * Returns the bounds for a string.
	 * 
	 * @param g
	 *            the graphics object to use for the string bounds
	 * @param string
	 *            the string to get the bounds for
	 * @return a rectangle containing the bounds of the string
	 */
	protected Rectangle2D getBounds(Graphics2D g, String string) {
		return g.getFontMetrics().getStringBounds(string, g);
	}

	/**
	 * Returns the size of a node.
	 * 
	 * @return the space any node takes up
	 */
	public Rectangle2D nodeSize() {
		return NODE_SIZE;
	}

	/**
	 * Returns the size of a node. This method returns the same as the nodeless
	 * size method.
	 * 
	 * @param node
	 *            the node to get the size for
	 * @return the space any node takes up
	 */
	public Rectangle2D nodeSize(TreeNode node) {
		return nodeSize();
	}

	/**
	 * Returns if a given point is "on" a given node, assuming that the node is
	 * in the same position as the <CODE>draw</CODE> function would draw it.
	 * 
	 * @param node
	 *            the node to test for containment of a point
	 * @param x
	 *            the x coordinate
	 * @param y
	 *            the y coordinate
	 * @return <CODE>true</CODE> if the point (x,y) is on the node, <CODE>false</CODE>
	 *         otherwise
	 * @see #draw
	 */
	public boolean onNode(TreeNode node, double x, double y) {
		return Math.sqrt(x * x + y * y) <= NODE_RADIUS;
	}

	/** The shape of a node. */
	public static final float NODE_RADIUS = 13.0f;

	public static final Shape NODE_SHAPE = new Ellipse2D.Float(-NODE_RADIUS,
			-NODE_RADIUS, NODE_RADIUS * 2.0f, NODE_RADIUS * 2.0f);

	/** A constant node size. */
	public static final Rectangle2D NODE_SIZE = new Rectangle(
			(int) NODE_RADIUS, (int) NODE_RADIUS, (int) (NODE_RADIUS * 2.0f),
			(int) (NODE_RADIUS * 2.0f));
}
