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





package edu.duke.cs.jflap.gui.editor;

import edu.duke.cs.jflap.gui.viewer.*;

/**
 * This is a subclass of an <code>ArrowTool</code> for Mealy 
 * machines that removes the "final state" checkbox in the
 * right-click popup menu. It is otherwise identical in every 
 * way.
 *
 * @see edu.duke.cs.jflap.automata.mealy.MealyMachine
 * @see MealyToolBox
 * @author Jinghui Lim
 *
 */
public class MealyArrowTool extends ArrowTool 
{
    /**
     * Instantiates a new arrow tool.
     * 
     * @param view the view where the automaton is drawn
     * @param drawer the object that draws the automaton
     * @param creator the transition creator used for editing transitions
     */
    public MealyArrowTool(AutomatonPane view, AutomatonDrawer drawer,
            TransitionCreator creator)
    {
        super(view, drawer, creator);
//        stateMenu.makeFinal.setEnabled(false);
        stateMenu.remove(stateMenu.makeFinal);
    }
    
    /**
     * Instantiates a new arrow tool.
     * 
     * @param view the view where the automaton is drawn
     * @param drawer the object that draws the automaton
     */            
    public MealyArrowTool(AutomatonPane view, AutomatonDrawer drawer) 
    {
        super(view, drawer);
//        stateMenu.makeFinal.setEnabled(false);
        stateMenu.remove(stateMenu.makeFinal);
    }
}
