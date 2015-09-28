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
 * A <code>MealyTransition</code> is a <code>Transition</code>
 * object with additional fields for the label and output. The
 * label determines if the machine should move on this transition
 * and the output is the output produced  if the machine moves
 * on this transition.
 * 
 * @see MealyMachine
 * @author Jinghui Lim
 *
 */
public class MealyTransition extends Transition 
{
    /**
     * Transition label
     */
    protected String myLabel;
    /**
     * Transition output
     */
    protected String myOutput;
    
    /**
     * Instantiates a new <code>MealyTransition</code> object.
     * 
     * @param from the state this transition comes from
     * @param to the state this transition goes to
     * @param label the label for this transition that the input string
     * in the machine should match before moving through this transition
     * @param output the output this transition produces
     */
    public MealyTransition(State from, State to, String label, String output) 
    {
        super(from, to);
        setLabel(label);
        setOutput(output);
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
        return new MealyTransition(from, to, myLabel, myOutput);
    }
    
    /**
     * Sets the label for this transition.
     * 
     * @param label the new label for this transition
     */
    protected void setLabel(String label)
    {
        /*
         * The null check should not be needed as label should not be null
         * so this is being too careful.
         */
        if(label == null)
            myLabel = "";
        else
            myLabel = label;
    }
    
    /**
     * Sets the output for this transition.
     * 
     * @param output the new output for this transition
     */
    protected void setOutput(String output)
    {
        /*
         * Too careful again.
         */
        if(output == null)
            myOutput = "";
        else
            myOutput = output;
    }
    
    /**
     * Returns the label for this transition.
     * 
     * @return the label for this transition
     */
    public String getLabel()
    {
        return myLabel;
    }
    
    /**
     * Returns the output for this transition.
     * 
     * @return the output for this transition
     */
    public String getOutput()
    {
        return myOutput;
    }
    
    /**
     * Returns a string description for this transition. This
     * consists of the label and output of the transition.
     * 
     * @return the description for this transition
     */
    public String getDescription()
    {
        String label = getLabel();
        String output = getOutput();
        if(label == null || label.length() == 0)
            label = Universe.curProfile.getEmptyString();
        if(output == null || output.length() == 0)
            output = Universe.curProfile.getEmptyString();;
        return label + " ; " + output;
    }
    
    /**
     * Returns a string representation of this object. This is the same
     * as the string representation for a regular transition object with
     * the label and output tacked on.
     * 
     * @see edu.duke.cs.jflap.automata.Transition#toString()
     * @return a string representation of this object
     */
    public String toString()
    {
        return super.toString() + ": \"" + getLabel() + "/" + getOutput() + "\"";
    }
    
    /**
     * Returns if this transition is equal to another object. They are equal
     * if they satisfy automata.Transition.equals(Object) and have the same
     * label and output.
     * 
     * @see edu.duke.cs.jflap.automata.Transition#equals(Object)
     * @param object the object to compare against
     * @return <code>true</code> if the two are equal,
     * <code>false</code> otherwise
     */
    public boolean equals(Object object)
    {
        try
        {
            MealyTransition t = (MealyTransition) object;
            return super.equals(t) && getLabel().equals(t.getLabel()) && getOutput().equals(t.getOutput());
        }
        catch(ClassCastException e)
        {
            return false;
        }
    }

    /**
     * Returns the hash code for this transition object.
     * 
     * @return the hash code for this transition
     */
    public int hashCode()
    {
        return super.hashCode() ^ getLabel().hashCode() ^ getOutput().hashCode();
    }
}
