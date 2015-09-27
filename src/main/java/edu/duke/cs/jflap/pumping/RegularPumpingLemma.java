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





package edu.duke.cs.jflap.pumping;

import edu.duke.cs.jflap.gui.environment.Universe;
import java.io.Serializable;

/**
 * A <code>RegularPumpingLemma</code> is a <code>PumpingLemma</code> that 
 * handles the three string segments that <i>w</i> is broken into, <i>xyz</i>.
 * 
 * @author Jinghui Lim & Chris Morgan
 *
 */
public abstract class RegularPumpingLemma extends PumpingLemma implements Serializable, Cloneable
{
    /**
     * The <i>x</i> segment of the <i>w</i>.
     */
    protected String x;
    /**
     * The <i>y</i> segment of the <i>w</i>.
     */
    protected String y;
    /**
     * The <i>z</i> segment of the <i>w</i>.
     */
    protected String z;
        
    /**
     * Returns segment <i>x</i> of the decomposition.
     * 
     * @return <i>x</i> of the decomposition
     */
    public String getX()
    {
        return x;
    }
    
    /**
     * Returns segment <i>y</i> of the decomposition.
     * 
     * @return <i>y</i> of the decomposition
     */
    public String getY()
    {
        return y;
    }
    
    /**
     * Returns segment <i>z</i> of the decomposition.
     * 
     * @return <i>z</i> of the decomposition
     */
    public String getZ()
    {
        return z;
    }    
    
    
    public String getDecompositionAsString()
    {		
		String[] s = new String[3];
		int counter = 0;
		for (int i=0; i<=1; i++) {
			s[i] = w.substring(counter, counter + myDecomposition[i]);			
			counter += myDecomposition[i];
		}
		s[2] = w.substring(counter);
		
		for (int i=0; i<s.length; i++)
			if (s[i].length() == 0)
				s[i] = "" + Universe.curProfile.getEmptyString();;
		
		return "X = " + s[0] + ";   Y = " + s[1] + ";   Z = " + s[2];
    }
    
    /**
     * Clears the information the user and program have set, 
     * <i>m</i>, <i>w</i>, <i>x</i>, <i>y</i>, and <i>z</i>. 
     */
    public void reset()
    {
        m = -1;
        i = -1;
        w = "";
        x = "";
        y = "";
        z = "";
    }
    
    /**
     * Sets <i>i</i> and sets the decomposition, with the length of 
     * each part of the decomposition in the corresponding space of the 
     * input array. That is, |<i>x</i>| = <code>decomposition[0]</code>, 
     * |<i>y</i>| = <code>decomposition[1]</code>. 
     * 
     * @param decomposition the array that contains the length of each part of the decomposition
     * @param num the number to set <i>i</i> to
     * @return <code>true</code> if this decomposition is legal, <code>false</code> otherwise
     */
    public boolean setDecomposition(int[] decomposition, int num)
    {
        i = num;
        return setDecomposition(decomposition);
    }
    
    /**
     * Chooses the decomposition, with the length of each part of the 
     * decomposition in the corresponding space of the input array, 
     * then chooses an acceptable <i>i</i>.
     * 
     * @see #setDecomposition(int[], int)
     * @param decomposition the array that contains the length of each part of the decomposition
     * @return <code>true</code> if this decomposition is legal, <code>false</code> otherwise
     */
    public boolean setDecomposition(int[] decomposition)
    {
    	myDecomposition = decomposition;
    	
        int xLength = decomposition[0];
        int yLength = decomposition[1];
        if(xLength + yLength > m || yLength < 1 || xLength < 0)
            return false;
        
        x = w.substring(0, xLength);
        y = w.substring(xLength, xLength + yLength);
        z = w.substring(xLength + yLength);
        
        return true;
    }
    
    /**
     * Returns the pumped string <i>xy<sup>i</sup>z</i> according the the 
     * decomposition and choice of <i>i</i>.
     * 
     * @return the pumped string, <i>xy<sup>i</sup>z</i>
     */
    public String createPumpedString()
    {
        return x + pumpString(y, getI()) + z;
    }
    
    
    public int addCase(int[] decomposition, int num) 
    {
        /*
         * This shouldn't be called for most regular pumping lemmas.
         */
        /*
         * addCase(int[]) should only be called after chooseDecomposition(int[]), 
         * so it should be a legal decomposition and uvxyz should have been set. 
         * Nonetheless, we check here.
         */
        if(!setDecomposition(decomposition))
            return -1;
        /*
         * Check if this case has been done.
         */
        for(int j = 0; j < myDoneCases.size(); j++)
        {
            if(((Case)(myDoneCases.get(j))).isCase(y, y))
                return j;
        }
        /*
         * Since case has not been done, find the case and put it into the
         * "done" pile.
         */
        for(int j = 0; j < myAllCases.size(); j++)
        {
            if(((Case)(myAllCases.get(j))).isCase(y, y))
            {
                Case c = (Case)(myAllCases.get(j));
                c.setI(num);
                c.setUserInput(decomposition);
                myDoneCases.add(c);
                return myAllCases.size();
            }
        }
        System.err.println("BUG FOUND: ContextFreePumpingLemma.addCase(int[])");
        return -1;
    }
    
    
    /**
     * Initializes all the possible cases for this pumping lemma. 
     * In most regular pumping lemmas, there is only one case and 
     * no initialization needs to be done. Subclasses that need to 
     * initialize cases should implement an overriding method.
     *
     */
    protected void addCases() 
    {
        /*
         * For most regular pumping lemmas, there is only one case so we 
         * don't bother. Those that have more than one case should write
         * their own method.
         */
    }
    
    
    public boolean replaceCase(int[] decomposition, int num, int index)
    {
        Case c = (Case)myDoneCases.get(index);
        if(c.isCase(y, y))
        {
            c.setI(num);
            c.setUserInput(decomposition);
            return true;
        }
        return false;
    }
        
    /**
     * Chooses a random regular decomposition.
     */
    public void chooseDecomposition() 
    {
    	//Note m must be >= 2 to use the default
    	int x = LemmaMath.fetchRandInt(0, getM() - 1);
    	setDecomposition(new int[] {x, getM() - x});
    }
}
