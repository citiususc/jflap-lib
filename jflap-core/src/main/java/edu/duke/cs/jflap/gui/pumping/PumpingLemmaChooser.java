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





package edu.duke.cs.jflap.gui.pumping;

import java.io.Serializable;
import java.util.ArrayList;

import edu.duke.cs.jflap.pumping.PumpingLemma;

/**
 * A <code>PumpingLemmaChooser</code> holds a list of 
 * {@link edu.duke.cs.jflap.pumping.PumpingLemma}s that allows the user to select
 * which pumping lemma they want to work on.
 * 
 * @author Jinghui Lim
 *
 */
public abstract class PumpingLemmaChooser implements Serializable
{
    /**
     * The list of pumping lemmas
     */
    protected ArrayList myList;
    /**
     * The index of the current (or most recently opened) pumping lemma.
     */
    protected int myCurrent;
    
    /**
     * Resets the pumping lemma at index <code>i</code>.
     * 
     * @param i the index of the pumping lemma we wish to reset
     * @see edu.duke.cs.jflap.pumping.PumpingLemma#clearDoneCases()
     */
    public void reset(int i)
    {
    	PumpingLemma pl = (PumpingLemma) myList.get(i);
        pl.clearDoneCases();
        pl.clearAttempts();
        pl.reset();
    }
    
    /**
     * Resets all the pumping lemmas.
     * 
     * @see #reset(int)
     */
    public void reset()
    {
        for(int i = 0; i < myList.size(); i++)
            reset(i);
    }
    
    /**
     * Returns the <code>PumpingLemma</code> at index <code>i</code>.
     * 
     * @param i the pumping lemma we wish to retrieve
     * @return the <code>PumpingLemma<code> at index <code>i</code>
     */
    public PumpingLemma get(int i)
    {
        return (PumpingLemma)myList.get(i);
    }
    
    /**
     * Returns the current (or most recently opened) pumping lemma.
     * 
     * @return the current (or most recently opened) pumping lemma
     */
    public PumpingLemma getCurrent()
    {
        return get(myCurrent);
    }
    
    /**
     * Sets the current pumping lemma.
     * 
     * @param i the index of the current pumping lemma
     */
    protected void setCurrent(int i)
    {
        myCurrent = i;
    }
    
    /**
     * Returns the total number of pumping lemmas.
     * 
     * @return the total number of pumping lemmas
     */
    public int size()
    {
        return myList.size();
    }
    
    /**
     * Replace a pumping lemma in the chooser with another of the same
     * class. The old pumping lemma of the same class will be removed
     * and the new pumping lemma will be added. This is mainly used for
     * loading.
     *  
     * @param pl the pumping lemma to be added
     */
    public void replace(PumpingLemma pl)
    {
        for(int i = 0; i < myList.size(); i++)
        {
            if(pl.getClass().equals(myList.get(i).getClass()))
            {
                myList.remove(i);
                myList.add(i, pl);
            }
        }
    }
}
