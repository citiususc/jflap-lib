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

import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.duke.cs.jflap.grammar.Grammar;
import edu.duke.cs.jflap.grammar.Production;
import edu.duke.cs.jflap.grammar.UnboundGrammar;

/**
 * This transducer is the codec for {@link edu.duke.cs.jflap.grammar.Grammar} objects.
 * 
 * @author Thomas Finley
 */

public class GrammarTransducer extends AbstractTransducer {
	/**
	 * Returns the type this transducer recognizes, "grammar".
	 * 
	 * @return the string "grammar"
	 */
	public String getType() {
		return "edu/duke/cs/jflap/grammar";
	}

	/**
	 * Returns a production for a given node.
	 * 
	 * @param node
	 *            the node the encapsulates a production
	 */
	public static Production createProduction(Node node) {
		Map e2t = elementsToText(node);
		String left = (String) e2t.get(PRODUCTION_LEFT_NAME);
		String right = (String) e2t.get(PRODUCTION_RIGHT_NAME);
		if (left == null)
			left = "";
		if (right == null)
			right = "";
		return new Production(left, right);
	}

	/**
	 * Returns an element that encodes a given production.
	 * 
	 * @param document
	 *            the document to create the element in
	 * @param production
	 *            the production to encode
	 * @return an element that encodes a production
	 */
	public static Element createProductionElement(Document document,
			Production production) {
		Element pe = createElement(document, PRODUCTION_NAME, null, null);
		pe.appendChild(createElement(document, PRODUCTION_LEFT_NAME, null,
				production.getLHS()));
		pe.appendChild(createElement(document, PRODUCTION_RIGHT_NAME, null,
				production.getRHS()));
		return pe;
	}

	/**
	 * Given a document, this will return the corresponding grammar encoded in
	 * the DOM document.
	 * 
	 * @param document
	 *            the DOM document to convert
	 * @return the {@link edu.duke.cs.jflap.grammar.Grammar} instance
	 */
	public java.io.Serializable fromDOM(Document document) {
		Grammar g = new UnboundGrammar();
		NodeList list = document.getDocumentElement().getElementsByTagName(
				PRODUCTION_NAME);
		for (int i = 0; i < list.getLength(); i++) {
			Production p = createProduction(list.item(i));
			g.addProduction(p);
		}
		return g;
	}

	/**
	 * Given a JFLAP grammar, this will return the corresponding DOM encoding of
	 * the structure.
	 * 
	 * @param structure
	 *            the JFLAP grammar to encode
	 * @return a DOM document instance
	 */
	public Document toDOM(java.io.Serializable structure) {
		Grammar grammar = (Grammar) structure;
		Document doc = newEmptyDocument();
		Element se = doc.getDocumentElement();
		// Add the productions as subelements of the structure element.
		Production[] productions = grammar.getProductions();
		if (productions.length > 0)
			se.appendChild(createComment(doc, COMMENT_PRODUCTIONS));
		for (int i = 0; i < productions.length; i++)
			se.appendChild(createProductionElement(doc, productions[i]));
		// Return the completed document.
		return doc;
	}

	/** The tag name for productions. */
	public static final String PRODUCTION_NAME = "production";

	/** The tag name for the left of the production. */
	public static final String PRODUCTION_LEFT_NAME = "left";

	/** The tag name for the right of the production. */
	public static final String PRODUCTION_RIGHT_NAME = "right";

	/** The comment for the list of productions. */
	private static final String COMMENT_PRODUCTIONS = "The list of productions.";
}
