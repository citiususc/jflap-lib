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

import edu.duke.cs.jflap.automata.Automaton;

/**
 * This subclass of <code>Automaton</code> is specifically for
 * a definition of a Mealy machine.
 * 
 * @author Jinghui Lim
 *
 */
public class MealyMachine extends Automaton 
{
    /**
     * Creates a Mealy machine with no states or transitions.
     *
     */
    public MealyMachine()
    {
        super();
    }
    
    /**
     * Returns the class of <code>Transition</code> this automaton
     * must accept.
     * 
     * @return the <code>Class</code> object for the <code>
     * MealyTransition</code>
     */
    protected Class getTransitionClass()
    {
        return MealyTransition.class;
    }
}
