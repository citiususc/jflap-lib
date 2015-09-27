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
 * This is a special <code>ToolBox</code> for Moore machines
 * that loads the <code>MooreArrowTool</code> and <code>
 * MooreStateTool</code> instead of the default <code>
 * ArrowTool</code> and <code>StateTool</code>.
 * 
 * @see edu.duke.cs.jflap.automata.mealy.MooreMachine
 * @see MooreArrowTool
 * @see MooreStateTool
 * @author Jinghui Lim
 * 
 */
public class MooreToolBox implements ToolBox 
{
    /**
     * Returns a list of tools for Moore machines, similar to
     * the <code>DefaultToolBox</code>. This includes a
     * <code>MooreArrowTool</code>, <code>MooreStateTool</code>
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
        list.add(new MooreArrowTool(view, drawer));
        list.add(new MooreStateTool(view, drawer));
        list.add(new TransitionTool(view, drawer));
        list.add(new DeleteTool(view, drawer));
		list.add(new UndoTool(view, drawer));
		list.add(new RedoTool(view, drawer));
        return list;
    }
}
