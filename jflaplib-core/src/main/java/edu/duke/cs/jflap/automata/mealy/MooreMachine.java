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





package edu.duke.cs.jflap.automata.mealy;

import edu.duke.cs.jflap.gui.environment.Universe;

import java.util.*;

import edu.duke.cs.jflap.automata.State;

/**
 * A <code>MooreMachine</code> is a special subclass of a
 * <code>MealyMachine</code> which restricts the output of
 * all transitions to one state to be the same output
 * defined in the state. This is done with a map of outputs
 * to states, and is implemented through {@link 
 * #getOutput(State)} and {@link #setOutput(State, String)}. 
 * 
 * @see MealyMachine
 * @see MooreTransition
 * @author Jinghui Lim
 *
 */
public class MooreMachine extends MealyMachine 
{
    /**
     * Map of states (keys) to output (values).
     */
    private Map myMap;
    
    /**
     * Creates a Moore machine with no states or transitions.
     *
     */
    public MooreMachine()
    {
        super();
        myMap = new HashMap();
    }
    
    /**
     * Returns the class of <code>Transition</code> this automaton
     * must accept.
     * 
     * @return the <code>Class</code> object for the <code>
     * MooreTransition</code>
     */
    protected Class getTransitionClass()
    {
        return MooreTransition.class;
    }
    
    /**
     * Sets the output for a state to be the given string, <code>
     * output</code>.
     * 
     * @param state state to set the output for
     * @param output value to set the state output to
     */
    public void setOutput(State state, String output)
    {
        /*
         * The null check occurs here but the input string can also
         * be checked before.
         */
        if(output == null)
            myMap.put(state, "");
        else
            myMap.put(state, output);
    }
    
    /**
     * Returns the output a state produces.
     * 
     * @param state the state whose output value we want
     * @return the output of the state
     */
    public String getOutput(State state)
    {
        if(myMap.get(state) == null)
            return "";
        else
            return (String) myMap.get(state);
    }
    
    /**
     * Returns a description of a state. If the output is the
     * empty string, it returns <code>MealyTransition.LAMBDA</code>
     * otherwise, it returns the output of the state. Called by
     * {@link edu.duke.cs.jflap.gui.viewer.MooreStateDrawer#drawState(Graphics, Automaton, State, Point, Color)}.
     * 
     * @param state the state whose description we want
     * @return a description of the state
     */
    public String getStateDescription(State state)
    {
        /*
         * If the output has not been set i.e. this is a brand new state
         * before the user has entered a state output, then an
         * empty string shows up instead of lamba. It is purely cosmetic.
         */
        if(myMap.get(state) == null)
            return "";
        else if(getOutput(state).length() == 0) // if output is empty string
            return Universe.curProfile.getEmptyString();
        else
            return getOutput(state);
    }
}
