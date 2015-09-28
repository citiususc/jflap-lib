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


import org.w3c.dom.*;

import edu.duke.cs.jflap.automata.Automaton;
import edu.duke.cs.jflap.automata.State;
import edu.duke.cs.jflap.automata.Transition;
import edu.duke.cs.jflap.automata.mealy.MooreMachine;
import edu.duke.cs.jflap.automata.mealy.MooreTransition;

/**
 * This is the transducer for encoding and decoding {@link
 * edu.duke.cs.jflap.automata.mealy.MooreMachine} objects.
 * 
 * @author Jinghui Lim
 *
 */
public class MooreTransducer extends MealyTransducer 
{
    /**
     * The tag name for the state output string transition elements.
     */
    public static final String STATE_OUTPUT_NAME = "output";
    
    /**
     * Creates and returns an empty Moore machine.
     * 
     * @param document the DOM document that is being read
     * @return an empty Moore machine
     */
    protected Automaton createEmptyAutomaton(Document document) 
    {
        return new MooreMachine();
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
        return new MooreTransition(from, to, label, output);
    }
    
    /**
     * Returns the type string for this transducer, "moore".
     * 
     * @return the string "moore"
     */
    public String getType() 
    {
        return "moore";
    }
    
    /**
     * Produces a DOM element that encodes a given state. This adds
     * the strings to read and the output.
     * 
     * @param document the document to create the state in
     * @param state the state to encode
     * @return the newly created element that encodes the transition
     * @see edu.duke.cs.jflap.file.xml.AutomatonTransducer#createTransitionElement
     */
    protected Element createStateElement(Document document, State state, Automaton container)
    {
//        System.out.println("moore create state element called");
        Element se = super.createStateElement(document, state, state.getAutomaton());
        se.appendChild(createElement(document, STATE_OUTPUT_NAME, null,
                ((MooreMachine) state.getAutomaton()).getOutput(state)));
        return se;
    }
}
