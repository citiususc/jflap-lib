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

import edu.duke.cs.jflap.gui.viewer.AutomatonPane;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import edu.duke.cs.jflap.automata.State;
import edu.duke.cs.jflap.automata.Transition;
import edu.duke.cs.jflap.automata.mealy.MealyTransition;

/**
 * This is a transition creator for Mealy machines.
 * 
 * @author Jinghui Lim
 *
 */
public class MealyTransitionCreator extends TableTransitionCreator 
{
    /**
     * Table column names.
     */
    private static final String NAME[] = {"Label", "Output"};
    
    /**
     * Instantiates a new transition creator. 
     * 
     * @param parent the parent object that any dialogs or windows
     * brought up by this creator should be the child of
     */
    public MealyTransitionCreator(AutomatonPane parent)
    {
        super(parent);
    }
    
    /**
     * Initializes an empty transition.
     * 
     * @param from the from state
     * @param to the to state
     */
    protected Transition initTransition(State from, State to) 
    {
        return new MealyTransition(from, to, "", "");
    }

    /**
     * Creates a new table model.
     * 
     * @param transition the transition to create the model for
     */
    protected TableModel createModel(Transition transition) 
    {
        final MealyTransition t = (MealyTransition) transition;
        return new AbstractTableModel()
            {
                String s[] = new String[] {t.getLabel(), t.getOutput()};
                public Object getValueAt(int r, int c)          {return s[c];}
                public void setValueAt(Object o, int r, int c)  {s[c] = (String) o;}
                public boolean isCellEditable(int r, int c)     {return true;}
                public int getRowCount()                        {return 1;}
                public int getColumnCount()                     {return 2;}
                public String getColumnName(int c)              {return NAME[c];}
            };
    }
    
    /**
     * Modifies a transition according to what is in the table.
     * 
     * @param transition transition to modify
     * @param model table to get information from
     */
    public Transition modifyTransition(Transition transition, TableModel model) 
    {
        String label = (String) model.getValueAt(0, 0);
        String output = (String) model.getValueAt(0, 1);
        MealyTransition t = (MealyTransition) transition;
        try
        {
            return new MealyTransition(t.getFromState(), t.getToState(), label, output);
        }
        catch(IllegalArgumentException e)
        {
            reportException(e);
            return null;
        }
    }
}
