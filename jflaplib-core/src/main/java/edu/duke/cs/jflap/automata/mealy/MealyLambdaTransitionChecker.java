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

import edu.duke.cs.jflap.automata.LambdaTransitionChecker;
import edu.duke.cs.jflap.automata.Transition;

/**
 * The <code>MealyLambdaTransitionChecker</code> can be used to determine 
 * if a Mealy or Moore machine's transition is a lambda transition.
 * 
 * @author Jinghui Lim
 *
 */
public class MealyLambdaTransitionChecker extends LambdaTransitionChecker 
{
    /**
     * Creates an instance of <code>MealyLambdaTransitionChecker</code>.
     */
    public MealyLambdaTransitionChecker() 
    {
        super();
    }

    /**
     * Returns <code>true</code> if <code>transition</code> is a lambda
     * transition (i.e. its label is the lambda string).
     * 
     * @param transition the transtion
     * @return <code>true</code> if the transition is a lambday transition,
     * <code>false</code> otherwise
     */
    public boolean isLambdaTransition(Transition transition) 
    {
        MealyTransition t = (MealyTransition) transition;
        if(t.getLabel().equals(LAMBDA))
            return true;
        else
            return false;
    }
}
