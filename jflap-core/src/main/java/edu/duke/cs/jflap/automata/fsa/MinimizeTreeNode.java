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





package edu.duke.cs.jflap.automata.fsa;

import javax.swing.tree.DefaultMutableTreeNode;

import edu.duke.cs.jflap.automata.State;

/**
 * The Minimize Tree Node object is merely a default mutable tree node with an
 * additional field of data. This additional field is a String. You can set and
 * get this field. Other than that addition, Minimize Tree Nodes function
 * identically to Default Mutable Tree Nodes. You can insert them into
 * DefaultTreeModels, etc. This particular node is used in the tree of
 * distinguishable groups created during the DFA minimize operation.
 * 
 * @author Ryan Cavalcante
 */

public class MinimizeTreeNode extends DefaultMutableTreeNode {
	/**
	 * Creates a new <CODE>MinimizeTreeNode</CODE> with <CODE>userObject</CODE>
	 * as its user object and the empty string as its terminal.
	 * 
	 * @param userObject
	 *            the node's user object
	 */
	public MinimizeTreeNode(Object userObject) {
		super(userObject);
		myTerminal = "";
	}

	/**
	 * Creates a new <CODE>MinimizeTreeNode</CODE> with <CODE>userObject</CODE>
	 * as its user object and <CODE>terminal</CODE> as its terminal.
	 * 
	 * @param userObject
	 *            the node's user object
	 * @param terminal
	 *            the node's terminal
	 */
	public MinimizeTreeNode(Object userObject, String terminal) {
		super(userObject);
		myTerminal = terminal;
	}

	/**
	 * Sets the node's terminal field to <CODE>terminal</CODE>.
	 * 
	 * @param terminal
	 *            the node's terminal
	 */
	public void setTerminal(String terminal) {
		myTerminal = terminal;
	}

	/**
	 * Returns the node's terminal field
	 * 
	 * @return the node's terminal field
	 */
	public String getTerminal() {
		return myTerminal;
	}

	/**
	 * Returns the states on this node.
	 * 
	 * @return the array of states for this group
	 */
	public State[] getStates() {
		return (State[]) getUserObject();
	}

	/** The node's terminal field. */
	protected String myTerminal = "";

}
