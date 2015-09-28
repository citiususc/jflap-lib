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

import java.util.Iterator;
import java.util.Map;
import javax.xml.parsers.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.*;

/**
 * This is an abstract implementation of a transducer that implements very
 * common methods that will be used by many documents.
 * 
 * @author Thomas Finley
 */

public abstract class AbstractTransducer implements Transducer {

	private static final Logger logger = LoggerFactory.getLogger(AbstractTransducer.class);
	/**
	 * Returns a new DOM document instance. This will have the structure tags
	 * with the type instantiated, and the processing instruction signifying
	 * that this is an XML document, but nothing else.
	 * 
	 * @return a new document
	 */
	protected Document newEmptyDocument() {
		Document doc = docBuilder.newDocument();
		// Add the processing instruction.
		/*
		 * doc.appendChild(doc.createProcessingInstruction ("xml",
		 * "version=\"1.0\""));
		 */
		// Add the credit string for JFLAP.
		doc.appendChild(createComment(doc, "Created with JFLAP "
				+ edu.duke.cs.jflap.gui.AboutBox.VERSION + "."));
		// Create and add the <structure> element.
		Element structureElement = createElement(doc, STRUCTURE_NAME, null,
				null);
		doc.appendChild(structureElement);
		// Add the type of this document.
		structureElement.appendChild(createElement(doc, STRUCTURE_TYPE_NAME,
				null, getType()));
		// Return the skeleton document.
		return doc;
	}

	/**
	 * Given an node, returns the child text node of this element.
	 * 
	 * @param node
	 *            the node to get the text node from
	 * @return the text node that is a child of this node, or <CODE>null</CODE>
	 *         if there is no such child
	 */
	protected static String containedText(Node node) {
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node c = children.item(i);
			if (c.getNodeType() != Node.TEXT_NODE)
				continue;
			return ((Text) c).getData();
		}
		return null;
	}

	/**
	 * Given a node, returns a map where, for each immediate child of this node
	 * that is an element named A with a Text node with data B, there is an
	 * entry in the map from A to B. If A contains no textual node, A maps to
	 * <TT>null</TT>. If the element A appears more than once, the last
	 * element encountered is respected.
	 * 
	 * @param node
	 *            the node to get the map for
	 * @return the map from children element names to their textual contents
	 */
	protected static Map elementsToText(Node node) {
		NodeList children = node.getChildNodes();
		Map e2t = new java.util.HashMap();
		for (int i = 0; i < children.getLength(); i++) {
			Node c = children.item(i);
			if (c.getNodeType() != Node.ELEMENT_NODE)
				continue;
			String elementName = ((Element) c).getTagName();
			String text = containedText(c);
			e2t.put(elementName, text);
		}
		return e2t;
	}

	/**
	 * Convenience function for creating comments.
	 * 
	 * @param document
	 *            the DOM document we're creating the comment in
	 * @param comment
	 *            the comment text
	 * @return a comment node
	 */
	protected static Comment createComment(Document document, String comment) {
		return document.createComment(comment);
	}

	/**
	 * Convenience function for creating elements.
	 * 
	 * @param document
	 *            the DOM document we're creating the element in
	 * @param tagname
	 *            the tagname for the element
	 * @param attributes
	 *            a map from attribute names to attributes, or <CODE>null</CODE>
	 *            if this element should have no attributes
	 * @param text
	 *            the text for the element, which will be made into a text node
	 *            and added as a child of the created element, or <CODE>null</CODE>
	 *            if the element should have no children
	 * @return a new element
	 */
	protected static Element createElement(Document document, String tagname,
			Map attributes, String text) {
		// Create the new element.
        tagname = tagname.replaceAll("'", "");
        tagname = tagname.replaceAll("&", "");
        tagname = tagname.replaceAll("\"", "");
        tagname = tagname.replaceAll("<", "");
        tagname = tagname.replaceAll(">", "");
        tagname = tagname.replaceAll(" ", "");

        
      //  System.out.println("TAG NAME = "+tagname);
		Element element = document.createElement(tagname);
		// Set the attributes.
		if (attributes != null) {
			Iterator it = attributes.keySet().iterator();
			while (it.hasNext()) {
				String name = (String) it.next();
				String value = (String) attributes.get(name);
				element.setAttribute(name, value);
			}
		}
		// Add the text element.
		if (text != null)
			element.appendChild(document.createTextNode(text));
		return element;
	}

	static {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			docBuilder = factory.newDocumentBuilder();
		} catch (Throwable e) {
			// Err, this shouldn't happen.
			logger.error("Unknown error: ", e);
		}
	}

	/** The instance of the document builder. */
	private static DocumentBuilder docBuilder;
}
