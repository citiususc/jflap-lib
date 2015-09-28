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





package edu.duke.cs.jflap.file.xml;

import org.w3c.dom.*;

/**
 * This prettier converts DOMs to a prettier form. When creating a new raw DOM,
 * all the tags run together. However, this is not very pretty. To correct this,
 * text tags are inserted every so often to create at most one element per line,
 * where everything is properly indented, just as one would see in an XML
 * document formated by hand.
 * 
 * @author Thomas Finley
 */

public class DOMPrettier {
	/**
	 * Recursive private helper method that inserts indenting text nodes.
	 * 
	 * @param dom
	 *            the DOM document
	 * @param indent
	 *            the indent string sofar
	 * @param node
	 *            the node that we are recursing on
	 * @return if the last node encountered was a text node
	 */
	private static boolean makePretty(Document dom, String indent, Node node) {
		if (node.getNodeType() == Node.TEXT_NODE)
			return true;
		try {
			node.getParentNode().insertBefore(dom.createTextNode(indent), node);
		} catch (DOMException e) {
			// This occurs when the parent is the document, in which
			// case we don't want to insert the indentation anyway, so
			// this is perfectly fine.
		}
		NodeList list = node.getChildNodes();
		// Not good to insert nodes while accessing the list. :)
		Node[] nodes = new Node[list.getLength()];
		for (int i = 0; i < list.getLength(); i++)
			nodes[i] = list.item(i);

		boolean lastChild = true; // If no children, don't want text inside.
		for (int i = 0; i < nodes.length; i++)
			lastChild = makePretty(dom, indent + INDENT, nodes[i]);
		if (!lastChild)
			node.appendChild(dom.createTextNode(indent));
		return false;
	}

	/**
	 * Pretty-fies a DOM by inserting whitespace text nodes at appropriate
	 * places. This modifies the DOM itself.
	 * 
	 * @param dom
	 *            the DOM document to make pretty
	 */
	public static void makePretty(Document dom) {
		String newline = System.getProperty("line.separator");
		makePretty(dom, newline, dom.getDocumentElement());
	}

	/**
	 * The changing indentation string. Whenever a new level of indent is
	 * reached, this is prepended.
	 */
	public static final String INDENT = "\t";
}
