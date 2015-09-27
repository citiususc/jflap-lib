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





package edu.duke.cs.jflap.gui.grammar.parse;

import javax.swing.tree.*;

class UnrestrictedTreeNode extends DefaultMutableTreeNode {
	/**
	 * Creates a new unrestricted tree node.
	 * 
	 * @param text
	 *            the label for this unrestricted tree node
	 */
	public UnrestrictedTreeNode(String text) {
		super(text);
		this.text = text;
	}

	/**
	 * Returns the length of this node, which is the length of the text.
	 */
	public int length() {
		return text.length();
	}

	/**
	 * Returns the text.
	 * 
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * Returns a string representation of the node.
	 * 
	 * @return a string representation of the node
	 */
	public String toString() {
		return super.toString();
		// return "("+text+", "+weight+")";
	}

	/** The text! */
	private String text;

	/** The weight. */
	public double weight = 1.0;

	/** The highest row. */
	public int highest = 0;

	/** The lowest row. */
	public int lowest = 0;
}
