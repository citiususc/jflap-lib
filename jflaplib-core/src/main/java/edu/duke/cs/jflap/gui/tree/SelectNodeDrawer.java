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

import java.awt.Color;
import java.awt.Graphics2D;
import javax.swing.tree.TreeNode;
import java.util.WeakHashMap;

/**
 * Adding this to a drawer allows one to select nodes, and have them appear to
 * be selected.
 * 
 * @author Thomas Finley
 */

public class SelectNodeDrawer extends DefaultNodeDrawer {
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
		Color c = g.getColor();
		if (isSelected(node))
			g.setColor(c.darker());
		super.draw(g, node);
		g.setColor(c);
	}

	/**
	 * Determines if a node is selected.
	 * 
	 * @param node
	 *            the node to check for selectedness
	 * @return <CODE>true</CODE> if the node is selected, <CODE>false</CODE>
	 *         otehrwise
	 */
	public boolean isSelected(TreeNode node) {
		return selectedNodes.containsKey(node);
	}

	/**
	 * Sets the selectedness of a node.
	 * 
	 * @param node
	 *            the node to select or deselect
	 * @param select
	 *            if true, then select the node, otherwise deselect
	 */
	public void setSelected(TreeNode node, boolean select) {
		if (select)
			selectedNodes.put(node, null);
		else
			selectedNodes.remove(node);
	}

	/**
	 * Returns an array containing the list of all selected nodes.
	 * 
	 * @return an array containing the list of all selected nodes
	 */
	public TreeNode[] getSelected() {
		return (TreeNode[]) selectedNodes.keySet().toArray(new TreeNode[0]);
	}

	/**
	 * Sets all nodes as deselected.
	 */
	public void clearSelected() {
		selectedNodes.clear();
	}

	/** The selected nodes, with keys as nodes. */
	protected WeakHashMap selectedNodes = new WeakHashMap();
}
