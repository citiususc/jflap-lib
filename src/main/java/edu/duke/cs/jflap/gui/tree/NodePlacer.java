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

import javax.swing.tree.TreeModel;
import java.util.Map;

/**
 * A <CODE>NodePlacer</CODE> object is used to assign locations to nodes in a
 * tree.
 * 
 * @author Thomas Finley
 */

public interface NodePlacer {
	/**
	 * Given a <CODE>TreeModel</CODE> that contains <CODE>TreeNode</CODE>
	 * objects, this method returns a map from <CODE>TreeNode</CODE> objects
	 * to <CODE>Dimension2D</CODE> points. The points should be in the domain
	 * ([0,1],[0,1]), where (0,0) is the upper left corner and (1,0) the upper
	 * right. A node placer may optionally not place an entry for each node if a
	 * particular node should not be drawn.
	 * 
	 * @param tree
	 *            the tree model
	 * @param drawer
	 *            the object that draws the nodes in the tree
	 * @return a map from the nodes of the tree to points where those nodes
	 *         should be drawn
	 */
	public Map placeNodes(TreeModel tree, NodeDrawer drawer);
}
