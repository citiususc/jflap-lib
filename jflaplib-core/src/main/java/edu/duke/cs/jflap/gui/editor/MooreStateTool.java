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

import java.awt.event.MouseEvent;

import javax.swing.JOptionPane;

import edu.duke.cs.jflap.automata.State;
import edu.duke.cs.jflap.automata.mealy.MooreMachine;

/**
 * This is a special <code>StateTool</code> for Moore machines 
 * that prompts the user for the output of the Moore state 
 * after the state is created (when the mouse is released).
 * It is otherwise the same as the <code>StateTool</code>.
 * 
 * @see #mouseReleased(MouseEvent)
 * @see edu.duke.cs.jflap.automata.mealy.MooreMachine
 * @see MooreArrowTool
 * @see MooreToolBox
 * @author Jinghui Lim
 *
 */
public class MooreStateTool extends StateTool 
{
    /**
     * Instantiates a new state tool.
     * 
     * @param view the view where the automaton is drawn
     * @param drawer the object that draws the automaton
     */
    public MooreStateTool(AutomatonPane view, AutomatonDrawer drawer) 
    {
        super(view, drawer);
    }
    
    /**
     * This method overrides the superclass and allows us to indicate to undo that the action is incomplete.
     * 
     */
    @Override
    public void mousePressed(MouseEvent m){
         super.mousePressed(m); 	
    }
    
    /**
     * This allows the user to edit the output of a Moore machine state.
     * It is called by {@link #mouseReleased(MouseEvent)} and
     * {@link MooreArrowTool#mouseClicked(MouseEvent)}.
     * 
     * @param s the state to edit the output of
     */
    protected static void editState(State s)
    {
        MooreMachine m = (MooreMachine) (s.getAutomaton());

        String output = (String) JOptionPane.showInputDialog(null, // I don't seem to need a parent component
            "Enter output:", "Set Output", JOptionPane.QUESTION_MESSAGE, null, null, m.getOutput(s));
          
        /*
         * null checking happens in setOutput too, but this is just to be safe
         */
        if(output == null)
            m.setOutput(s, "");
        else
            m.setOutput(s, output);
        /*
         * This is a cheap hack. It will not immediately display 
         * the output otherwise, and I don't really want to mess 
         * around with the guts of this program.
         */
        s.setLabel(s.getLabel());
    }
    
    /**
     * This prompts the user for the state output after a state is
     * created (when the mouse is released).
     * 
     * @param event the mouse event
     */
    public void mouseReleased(MouseEvent event)
    {
        editState(state);
    }
}
