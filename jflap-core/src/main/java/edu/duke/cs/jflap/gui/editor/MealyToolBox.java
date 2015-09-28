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

import edu.duke.cs.jflap.gui.viewer.AutomatonDrawer;
import edu.duke.cs.jflap.gui.viewer.AutomatonPane;

import java.util.List;

/**
 * This is a special <code>ToolBox</code> for Mealy machines
 * that loads the <code>MealyArrowTool</code> instead of the 
 * default <code> ArrowTool</code>.
 * 
 * @see edu.duke.cs.jflap.automata.mealy.MealyMachine
 * @see MealyArrowTool
 * @author Jinghui Lim
 * 
 */
public class MealyToolBox implements ToolBox 
{
    /**
     * Returns a list of tools for Mealy machines, similar to
     * the <code>DefaultToolBox</code>. This includes a
     * <code>MealyArrowTool</code>, <code>StateTool</code>
     * <code>TransitionTool</code>, and <code>DeleteTool</code>
     * in that order.
     * 
     * @param view the component that the automaton will be drawn in
     * @param drawer the drawer that will draw the automaton in the
     * view
     * @return a list of <CODE>Tool</CODE> objects.
     */
    public List tools(AutomatonPane view, AutomatonDrawer drawer) 
    {
        List list = new java.util.ArrayList();
        list.add(new MealyArrowTool(view, drawer));
        list.add(new StateTool(view, drawer));
        list.add(new TransitionTool(view, drawer));
        list.add(new DeleteTool(view, drawer));
        list.add(new UndoTool(view, drawer));
        list.add(new RedoTool(view, drawer));
        return list;
    }
}
