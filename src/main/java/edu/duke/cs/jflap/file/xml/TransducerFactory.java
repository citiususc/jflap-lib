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
import java.io.Serializable;
import java.util.*;

/**
 * Maps either a DOM or a object to the appropriate transducer.
 * 
 * @author Thomas Finley
 */

public class TransducerFactory {
	/**
	 * Given a DOM document, this will return an appropriate instance of a
	 * transducer for the type of document. Note that the type of the structure
	 * should be specified with in the "type" tags.
	 * 
	 * @param document
	 *            the document to get the transducer for
	 * @return the correct transducer for this document
	 * @throws IllegalArgumentException
	 *             if the document does not map to a transducer, or if it does
	 *             not contain a "type" tag at all
	 */
	public static Transducer getTransducer(Document document) {
		Element elem = document.getDocumentElement();
		// Check for the type tag.
		NodeList typeNodes = elem.getElementsByTagName("type");
		if (typeNodes.getLength() == 0)
			throw new IllegalArgumentException(
					"No <type> tag appears to exist!");
		// Find the type node. Assume the first is valid.
		Node typeElement = typeNodes.item(0);
		NodeList subtypeNodes = typeElement.getChildNodes();
		String type = "";
		// Check for the text nodes in <type> tag.
		for (int i = 0; i < subtypeNodes.getLength(); i++) {
			Node node = subtypeNodes.item(i);
			if (node.getNodeType() == Node.TEXT_NODE) {
				type = ((Text) node).getData();
				break;
			}
		}
		// Check for the type.
		Object o = typeToTransducer.get(type);
		if (o == null)
			throw new IllegalArgumentException("The type \"" + type
					+ "\" is not recognized.");
		return instantiate(o);
	}

	/**
	 * Given a JFLAP structure, this will return an appropriate instance of a
	 * transducer for the class of structure passed in. Note, if there are
	 * multiple transducer types that could be applied to this structure, the
	 * transducer that corresponds to the closest superclass type is returned.
	 * For example, if class A is a superclass of B is a superclass of C is a
	 * superclass of D, and this factory has transducers for A and C, instances
	 * of A and B will get A transducers, and instances of C and D will get C
	 * transducers.
	 * 
	 * @param structure
	 *            the structure to get the transducer for
	 * @return the correct transducer for this structure
	 * @throws IllegalArgumentException
	 *             if the structure does not map to a transducer
	 */
	public static Transducer getTransducer(Serializable structure) {
		Class c = structure.getClass();
		// Cycle through the superclasses.
		while (c != null) {
			Object o = classToTransducer.get(c);
			if (o != null)
				return instantiate(o);
			c = c.getSuperclass();
		}
		// Apparently no transducer could be found.
		throw new IllegalArgumentException(
				"Cannot get transducer for object of " + structure.getClass()
						+ "!");
	}

	/**
	 * Turns an object into a transducer. If the passed in object is of type
	 * <TT>java.lang.Class</TT>, a new instance is returned. If the passed in
	 * is a {@link edu.duke.cs.jflap.file.Transducer}, the argument is cast and returned.
	 * 
	 * @param object
	 *            should be either a class or a transducer
	 * @return an instance of a transducer appropriate for the passed in
	 *         argument
	 * @throws IllegalArgumentException
	 *             if the passed argument is not class, nor a transducer, or is
	 *             a class that cannot be instantiated
	 */
	private static Transducer instantiate(Object object) {
		if (object instanceof Class) {
			try {
				return (Transducer) ((Class) object).newInstance();
			} catch (Throwable e) {
				throw new IllegalArgumentException("Could not instantiate "
						+ object + "!");
			}
		} else if (object instanceof Transducer) {
			return (Transducer) object;
		} else {
			throw new IllegalArgumentException("Object " + object
					+ " does not correspond to a transducer!");
		}
	}

	/**
	 * Creates a correspondence between type tags, structure classes, and a
	 * transducer class.
	 * 
	 * @param type
	 *            the type tag, or <CODE>null</CODE> if <CODE>transducer</CODE>
	 *            is in fact an instance and the transducer type should be
	 *            retrieved from there
	 * @param structureClass
	 *            the class of the structure
	 * @param transducer
	 *            either a transducer instance, or a transducer class
	 */
	private static void add(String type, Class structureClass, Object transducer) {
		if (type == null)
			type = ((Transducer) transducer).getType();
		typeToTransducer.put(type, transducer);
		classToTransducer.put(structureClass, transducer);
	}

	/**
	 * Initializes the maps.
	 */
	static {
		typeToTransducer = new HashMap();
		classToTransducer = new HashMap();
		add(null, edu.duke.cs.jflap.automata.fsa.FiniteStateAutomaton.class, new FSATransducer());
		add(null, edu.duke.cs.jflap.automata.pda.PushdownAutomaton.class, new PDATransducer());
		add(null, edu.duke.cs.jflap.automata.turing.TuringMachine.class, new TMTransducer());
		add(null, edu.duke.cs.jflap.grammar.Grammar.class, new GrammarTransducer());
		add(null, edu.duke.cs.jflap.regular.RegularExpression.class, new RETransducer());
		add(null, edu.duke.cs.jflap.grammar.lsystem.LSystem.class, new LSystemTransducer());
        add(null, edu.duke.cs.jflap.automata.mealy.MealyMachine.class, new MealyTransducer());
        add(null, edu.duke.cs.jflap.automata.mealy.MooreMachine.class, new MooreTransducer());
        add(null, edu.duke.cs.jflap.gui.pumping.RegPumpingLemmaChooser.class, new RegPumpingLemmaTransducer());
        add(null, edu.duke.cs.jflap.gui.pumping.CFPumpingLemmaChooser.class, new CFPumpingLemmaTransducer());
	}

	/** Mapping of DOM "type" tags to a corresponding transducer class. */
	private static Map typeToTransducer;

	/** Mapping of structure classes to a corresponding transducer class. */
	private static Map classToTransducer;
}
