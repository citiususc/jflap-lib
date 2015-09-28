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

import edu.duke.cs.jflap.automata.NondeterminismDetector;
import edu.duke.cs.jflap.automata.Transition;

/**
 * The <code>MealyNondeterminismDetector</code> can be used to find
 * all the nondeterministic states in a Mealy or Moore machine (all 
 * states with equal outward transitions).
 * 
 * @author Jinghui Lim
 *
 */
public class MealyNondeterminismDetector extends NondeterminismDetector 
{
    /**
     * Creates an instance of <code>MealyNondeterminismDetector</code>.
     *
     */
    public MealyNondeterminismDetector() 
    {
    }

    /**
     * Returns true if the transitions are identical (the labels are 
     * equivalent), or if they introduce nondeterminism (for example, 
     * the label of one could be a prefix of the label of the other).
     * 
     * @param t1 a transition
     * @param t2 a transition
     * @return true if the transitions are nondeterministic.
     */
    public boolean areNondeterministic(Transition t1, Transition t2) 
    {
        MealyTransition transition1 = (MealyTransition) t1;
        MealyTransition transition2 = (MealyTransition) t2;
        if(transition1.getLabel().equals(transition2.getLabel())) 
            return true;
        else if(transition1.getLabel().startsWith(transition2.getLabel()))
            return true;
        else if(transition2.getLabel().startsWith(transition1.getLabel()))
            return true;
        else
            return false;
    }
}
