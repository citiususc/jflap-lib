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

import edu.duke.cs.jflap.automata.*;

/**
 * The Mealy machine step by state simulator simulates the behavior
 * of a Mealy machine. It takes a <code>MooreMachine</code> object
 * and runs an input string on the object. The
 * <code>MooreStepByStateSimulator</code> is different from the
 * <code>MealyStepByStateSimulator</code> in that it produces output
 * in states, not transitions, and output is produced in the first
 * state.
 * 
 * <p>It simulates the machine's behavior by stepping through one state
 * at a time. Output of the machine can be accessed through 
 * {@link MealyConfiguration#getOutput()} and is printed out on the 
 * tape in the simulation window. This does not deal with lambda
 * transitions.
 * 
 * @author Jinghui Lim
 * @see edu.duke.cs.jflap.automata.mealy.MealyConfiguration
 *
 */
public class MooreStepByStateSimulator extends MealyStepByStateSimulator 
{
    /**
     * Creates a Moore step by state simulator for the given automaton.
     * 
     * @param automaton the machine to simulate
     */
    public MooreStepByStateSimulator(Automaton automaton)
    {
        super(automaton);
    }
    
    /**
     * Returns a <code>MooreConfiguration</code> that represents the 
     * initial configuration of the Moore machine, before any input
     * has been processed. This returns an array of length one.
     * 
     * @param input the input string to simulate
     */
    public Configuration[] getInitialConfigurations(String input) 
    {
        Configuration[] configs = new Configuration[1];
        configs[0] = new MealyConfiguration(myAutomaton.getInitialState(), null, input, 
                input, ((MooreMachine)myAutomaton).getOutput(myAutomaton.getInitialState()));
        return configs;
    }
}
