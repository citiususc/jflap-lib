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

import edu.duke.cs.jflap.automata.Automaton;
import edu.duke.cs.jflap.automata.State;
import edu.duke.cs.jflap.automata.Transition;
import edu.duke.cs.jflap.automata.fsa.FSATransition;
import edu.duke.cs.jflap.automata.fsa.FiniteStateAutomaton;

/**
 * This is the transducer for encoding and decoding
 * {@link edu.duke.cs.jflap.automata.fsa.FiniteStateAutomaton} objects.
 * 
 * @author Thomas Finley
 */

public class FSATransducer extends AutomatonTransducer {
	/**
	 * Creates and returns an empty FSA.
	 * 
	 * @param document
	 *            the DOM document that is being read
	 * @return an empty FSA
	 */
	protected Automaton createEmptyAutomaton(Document document) {
		return new FiniteStateAutomaton();
	}

	/**
	 * Creates and returns a transition consistent with this node.
	 * 
	 * @param from
	 *            the from state
	 * @param to
	 *            the to state
	 * @param node
	 *            the DOM node corresponding to the transition, which should
	 *            contain a "read" element
	 * @param e2t
	 *            elements to text from {@link #elementsToText}
	 * @return the new transition
	 */
	protected Transition createTransition(State from, State to, Node node,
			Map e2t, boolean bool) {
		String s = (String) e2t.get(TRANSITION_READ_NAME);
		if (s == null)
			s = ""; // Allow lambda transition.
		return new FSATransition(from, to, s);
	}

	/**
	 * Produces a DOM element that encodes a given transition. This adds the
	 * string to read.
	 * 
	 * @param document
	 *            the document to create the state in
	 * @param transition
	 *            the transition to encode
	 * @return the newly created element that encodes the transition
	 * @see edu.duke.cs.jflap.file.xml.AutomatonTransducer#createTransitionElement
	 */
	protected Element createTransitionElement(Document document,
			Transition transition) {
		Element te = super.createTransitionElement(document, transition);
		FSATransition t = (FSATransition) transition;
		// Add what the label is.
		te.appendChild(createElement(document, TRANSITION_READ_NAME, null, t
				.getLabel()));
		return te;
	}

	/**
	 * Returns the type string for this transducer, "fa".
	 * 
	 * @return the string "fa"
	 */
	public String getType() {
		return "fa";
	}

	/** The tag name for the read string transition elements. */
	public static final String TRANSITION_READ_NAME = "read";
}
