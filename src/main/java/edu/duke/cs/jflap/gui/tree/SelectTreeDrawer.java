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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.WeakHashMap;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

/**
 * This tree drawer allows the selection of nodes.
 * 
 * @author Thomas Finley
 */

public class SelectTreeDrawer extends DefaultTreeDrawer {
	/**
	 * Instantiates a new <CODE>SelectTreeDrawer</CODE> with the default
	 * colors.
	 * 
	 * @param tree
	 *            the model to draw
	 */
	public SelectTreeDrawer(TreeModel tree) {
		this(tree, NODE_COLOR, SELECTED_NODE_COLOR);
	}

	/**
	 * Instantiates a new <CODE>SelectTreeDrawer</CODE> with some deselected
	 * and selected colors.
	 * 
	 * @param tree
	 *            the model to draw
	 * @param deselected
	 *            the deselected color
	 * @param selected
	 *            the selected color
	 */
	public SelectTreeDrawer(TreeModel tree, Color deselected, Color selected) {
		super(tree);
		deselectedColor = deselected;
		selectedColor = selected;
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
		Set s = new HashSet(selectedNodes.keySet());
		Iterator it = s.iterator();
		while (it.hasNext()) {
			TreeNode n = (TreeNode) it.next();
			if (n.getParent() == null && n != getModel().getRoot())
				selectedNodes.remove(n);
		}
		return (TreeNode[]) selectedNodes.keySet().toArray(new TreeNode[0]);
	}

	/**
	 * Sets all nodes as deselected.
	 */
	public void clearSelected() {
		selectedNodes.clear();
	}

	/**
	 * This method returns the color for a particular node.
	 * 
	 * @param node
	 *            the node to color
	 * @return the color for this node, which will be either the selected color
	 *         if the node is selected, otherwise the deselected color
	 */
	protected Color getNodeColor(TreeNode node) {
		return isSelected(node) ? selectedColor : deselectedColor;
		// Color color = super.getNodeColor(node);
		// return isSelected(node) ? color.darker() : color;
	}

	/** The selected nodes, with keys as nodes. */
	protected WeakHashMap selectedNodes = new WeakHashMap();

	/** The deselected node color. */
	protected Color deselectedColor = NODE_COLOR;

	/** The selected node color. */
	protected Color selectedColor = SELECTED_NODE_COLOR;

	/** The default deselected node color. */
	public static final Color NODE_COLOR = Color.yellow;

	/** The default selected node color. */
	public static final Color SELECTED_NODE_COLOR = NODE_COLOR.darker();
}
