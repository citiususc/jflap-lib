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
import edu.duke.cs.jflap.automata.State;
import edu.duke.cs.jflap.automata.Transition;

/**
 * A <code>MooreTransition</code> is a special subclass of <code>
 * MealyTransition</code> in which the output of each transition
 * is constrained to be a the value of output of its "to" state.
 * 
 * @see MooreMachine
 * @author Jinghui Lim
 * 
 */
public class MooreTransition extends MealyTransition 
{
    /**
     * Instantiates a new <code>MooreTransition</code> object and changes 
     * the output of the state <code>to</code> to <code>output</code>.
     * 
     * @see #MooreTransition(State, State, String)
     * @param from the state this transition comes from
     * @param to the state this transition goes to
     * @param label the label for this transition that the input string
     * in the machine should match before moving through this transition
     * @param output the output this transition's to state produces
     */
    public MooreTransition(State from, State to, String label, String output)
    {
        super(from, to, label, output);
        setOutput(output);
    }
    
    /**
     * Instantiates a new <code>MooreTransition</code> object without changing
     * the output of the transition's to state.
     * 
     * @see #MooreTransition(State, State, String, String)
     * @param from the state this transition comes from
     * @param to the state this transition goes to
     * @param label the label for this transition that the input string
     * in the machine should match before moving through this transition
     */
    public MooreTransition(State from, State to, String label) 
    {
        this(from, to, label, ((MooreMachine) (to.getAutomaton())).getOutput(to));
    }
    
    /**
     * Produces a copy of this transition with new from and to states.
     * 
     * @param from the new from state
     * @param to the new to state
     * @return a copy of this transition with new states
     */
    public Transition copy(State from, State to)
    {
        return new MooreTransition(from, to, getLabel(), getOutput());
    }
    

    /**
     * Gets the output for this transition or its "to" state by calling {@link 
     * MooreMachine#getOutput(State)}.
     * 
     * @see MooreMachine#getOutput(State)
     */
    public String getOutput()
    {
        return ((MooreMachine) (to.getAutomaton())).getOutput(to);
    }
    
    /**
     * Sets the output for this transition, and its "to" state, by calling {@link 
     * MooreMachine#setOutput(State, String)}.
     * 
     * @see MooreMachine#setOutput(State, String)
     * @param output the new output for this transition
     */
    protected void setOutput(String output)
    {
        ((MooreMachine) to.getAutomaton()).setOutput(to, output);
    }
    
    /**
     * Returns a string description of this transition. This consists of the
     * label. The output of the transition is shown in the state.
     * 
     * @return the description of this transition
     */
    public String getDescription()
    {
        if(getLabel() == null || getLabel().length() == 0)
            return Universe.curProfile.getEmptyString();
        else
            return getLabel();
    }
}