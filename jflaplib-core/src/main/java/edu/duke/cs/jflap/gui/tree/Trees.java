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

import javax.swing.tree.*;
import java.util.*;

/**
 * The <CODE>Trees</CODE> class contains method for doing basic calculations
 * with trees. Many of the operations assume that the <CODE>TreeModel</CODE>
 * holds <CODE>TreeNode</CODE> objects.
 * 
 * @author Thomas Finley
 */

public class Trees {
	/**
	 * We don't need no stinkin' instances!
	 */
	private Trees() {
	}

	/**
	 * Returns an array containing the children of a treenode.
	 * 
	 * @param node
	 *            the treenode to return the children for
	 * @return an array containing the children in the order retrieved from the
	 *         enumerator, or an empty array if this is a leaf not
	 */
	public static TreeNode[] children(TreeNode node) {
		TreeNode[] children = new TreeNode[node.getChildCount()];
		if (!node.isLeaf()) {
			int i = 0;
			Enumeration enumer = node.children();
			while (i < children.length)
				children[i++] = (TreeNode) enumer.nextElement();
		}
		return children;
	}

	/**
	 * Exhaustively calculates the "widths" of levels of the tree. The width of
	 * the level is defined as the number of nodes with that particular depth.
	 * 
	 * @return the widths of the tree, where the array returned is of length
	 *         <CODE>depth()+1</CODE>, and index 0 is always 1 (since only
	 *         the root can be at level 0).
	 */
	public static int[] width(TreeModel tree) {
		int[] width = new int[depth(tree) + 1];
		Arrays.fill(width, 0);
		Trees.width((TreeNode) tree.getRoot(), 0, width);
		return width;
	}

	/**
	 * Helper function for width. This visits each node, and for its depth adds
	 * one to its depth field.
	 * 
	 * @param node
	 *            the node that we are currently visiting
	 * @param depth
	 *            the current depth of the node
	 * @param array
	 *            the width array being filled
	 */
	private static void width(TreeNode node, int depth, int[] array) {
		array[depth++]++;
		if (node.isLeaf())
			return;
		TreeNode[] children = Trees.children(node);
		for (int i = 0; i < children.length; i++)
			width(children[i], depth, array);
	}

	/**
	 * Exhaustively calculates the depth of the tree.
	 * 
	 * @param tree
	 *            the tree to get the depth of
	 * @return the maximum distance from a leaf to the root
	 */
	public static int depth(TreeModel tree) {
		return depth((TreeNode) tree.getRoot());
	}

	/**
	 * Returns the depth of a subtree rooted at the indicated node.
	 * 
	 * @param node
	 *            a node in the tree
	 * @return the depth of the subtree rooted at this node, e.g. 0 if this node
	 *         is a left
	 */
	public static int depth(TreeNode node) {
		TreeNode[] children = Trees.children(node);
		int max = -1;
		for (int i = 0; i < children.length; i++)
			max = Math.max(max, depth(children[i]));
		return max + 1;
	}

	/**
	 * Returns an array containing all the leaves of a tree.
	 * 
	 * @param tree
	 *            the tree to get leaves of
	 * @return an array with the leaves of the tree
	 */
	public static TreeNode[] leaves(TreeModel tree) {
		return leaves((TreeNode) tree.getRoot());
	}

	/**
	 * Returns an array containing all the leaves of a subtree rooted at the
	 * indicated node.
	 * 
	 * @param node
	 *            the node in the tree
	 * @return an array of all children in the tree
	 */
	public static TreeNode[] leaves(TreeNode node) {
		TreeNode[] children = Trees.children(node);
		if (children.length == 0)
			return new TreeNode[] { node };
		ArrayList leaves = new ArrayList();
		for (int i = 0; i < children.length; i++) {
			TreeNode[] subleaves = leaves(children[i]);
			for (int j = 0; j < subleaves.length; j++)
				leaves.add(subleaves[j]);
		}
		return (TreeNode[]) leaves.toArray(new TreeNode[0]);
	}
}
