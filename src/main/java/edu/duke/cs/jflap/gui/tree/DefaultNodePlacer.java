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

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

/**
 * A <CODE>DefaultNodePlacer</CODE> places the nodes in a tree in a rather
 * simple "top down" pattern.
 * 
 * @author Thomas Finley
 */

public class DefaultNodePlacer implements NodePlacer {
	/**
	 * Given a <CODE>TreeModel</CODE> that contains <CODE>TreeNode</CODE>
	 * objects, this method returns a map from all <CODE>TreeNode</CODE>
	 * objects to <CODE>Dimension2D</CODE> points. This placer works according
	 * to a rather simple algorithm that places all nodes at a particular depth
	 * in a tree at regular intervals.
	 * 
	 * @param tree
	 *            the tree model
	 * @param drawer
	 *            the object that draws the nodes in the tree
	 * @return a map from the nodes of the tree to points where those nodes
	 *         should be drawn
	 */
	public Map placeNodes(TreeModel tree, NodeDrawer drawer) {
		HashMap nodeToPoint = new HashMap();
		int[] width = Trees.width(tree), sofar = new int[width.length];
		Arrays.fill(sofar, 0);
		setPoints((TreeNode) tree.getRoot(), width.length - 1, 0, width, sofar,
				nodeToPoint);
		return nodeToPoint;
	}

	/**
	 * Recursively sets the points of the tree going the <CODE>nodeToPoint</CODE>
	 * structure as it goes.
	 * 
	 * @param node
	 *            the current node in the tree
	 * @param depth
	 *            the total depth of the tree
	 * @param thisDepth
	 *            the depth of this particular node
	 * @param width
	 *            the array of all widths
	 * @param widthSofar
	 *            the widths sofar
	 * @param nodeToPoint
	 *            the mapping of nodes to points built
	 */
	private void setPoints(TreeNode node, int depth, int thisDepth,
			int[] width, int[] widthSofar, Map nodeToPoint) {
		// Scale points along ([0,1], [0,1]).
		float x = (float) (widthSofar[thisDepth] + 1)
				/ (float) (width[thisDepth] + 1);
		float y = (float) (thisDepth + 1) / (float) (depth + 2);
		nodeToPoint.put(node, new Point2D.Float(x, y));
		// Check the maximum width.
		// max_width = Math.max(max_width, width[thisDepth]);
		// Update the depth and width figures.
		widthSofar[thisDepth++]++;
		// Recurse on children.
		TreeNode[] children = Trees.children(node);
		for (int i = 0; i < children.length; i++)
			setPoints(children[i], depth, thisDepth, width, widthSofar,
					nodeToPoint);
	}

}
