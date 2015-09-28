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

import edu.duke.cs.jflap.automata.mealy.*;

/**
 * This is the transducer for encoding and decoding 
 * {@link edu.duke.cs.jflap.automata.mealy.MealyMachine} objects.
 * 
 * @author Jinghui Lim
 *
 */
public class MealyTransducer extends AutomatonTransducer 
{
    /**
     * The tag name for the read string transition elements.
     */
    public static final String TRANSITION_READ_NAME = "read";
    /**
     * The tag name for the output string transition elements.
     */
    public static final String TRANSITION_OUTPUT_NAME = "transout";
    
    /**
     * Creates and returns an empty Mealy machine.
     * 
     * @param document the DOM document that is being red
     * @return an empty Mealy machine
     */
    protected Automaton createEmptyAutomaton(Document document) 
    {
        return new MealyMachine();
    }

    /**
     * Creates and returns a transition consistent with this node.
     *      
     * @param from the from state
     * @param to the to state
     * @param node the DOM node corresponding to the transition, which
     * should contain a "read" element, a "pop" element, and a "push"
     * elements
     * @param e2t elements to text from {@link #elementsToText}
     * @param isBlock
     * @return the new transition
     */
    protected Transition createTransition(State from, State to, Node node, Map e2t, boolean isBlock) 
    {
        /*
         * The boolean isBlock seems to be ignored in FSATransducer.java, so I'm ignoring
         * it here too.
         */
        String label = (String) e2t.get(TRANSITION_READ_NAME);
        String output = (String) e2t.get(TRANSITION_OUTPUT_NAME);
        if(label == null)
            label = "";
        if(output == null)
            output = "";
        return new MealyTransition(from, to, label, output);
    }
    
    /**
     * Produces a DOM element that encodes a given transition. This adds
     * the strings to read and the output.
     * 
     * @param document the document to create the state in
     * @param transition the transition to encode
     * @return the newly created element that encodes the transition
     * @see edu.duke.cs.jflap.file.xml.AutomatonTransducer#createTransitionElement
     */
    protected Element createTransitionElement(Document document, Transition transition)
    {
        Element te = super.createTransitionElement(document, transition);
        MealyTransition t = (MealyTransition) transition;
        te.appendChild(createElement(document, TRANSITION_READ_NAME, null, t.getLabel()));
        te.appendChild(createElement(document, TRANSITION_OUTPUT_NAME, null, t.getOutput()));
        return te;
    }
    
    /**
     * Returns the type string for this transducer, "mealy".
     * 
     * @return the string "mealy"
     */
    public String getType() 
    {
        return "mealy";
    }
}
