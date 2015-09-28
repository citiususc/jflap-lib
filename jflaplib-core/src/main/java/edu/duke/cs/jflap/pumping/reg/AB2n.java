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




package edu.duke.cs.jflap.pumping.reg;

import edu.duke.cs.jflap.pumping.RegularPumpingLemma;
import edu.duke.cs.jflap.pumping.LemmaMath;

/**
 * The regular pumping lemma for <i>L</i> = 
 * {(<i>ab</i>)<i><sup>2n</sup></i> : <i>n</i> = 1,2,...}.
 * 
 * @author Chris Morgan
 */
public class AB2n extends RegularPumpingLemma {
	
	public String getTitle() 
    {
        return "(ab)^2n : n = 1,2,...";
    }

    public String getHTMLTitle() 
    {
        return "(<i>ab</i>)<i><sup>2n</sup></i> : <i>n</i> "+
               "= 1,2,...";
    }

    public void setDescription()
    {
    	partitionIsValid = true;
    	explanation = "Because this is a regular language, a valid decomposition exists.  As long as <i>m</i> " + GREATER_OR_EQ + 
    			" 4, then if <i>y</i> = \"abab\" (or \"baba\" if <i>m</i>" + GREATER_OR_EQ + "5), the decomposition can be " +
    			"pumped for any <i>i</i> value.";
    }
    
    protected void setRange() 
	{
		myRange = new int[]{4, 10};
	}
    
    public void chooseI() 
	{
		i = LemmaMath.flipCoin();
	}

	protected void chooseW() 
	{
		if (m % 2 == 0)
		    w = pumpString("ab", m);
		else
		    w = pumpString("ab", m + 1);
	}	
	
	public void chooseDecomposition()
	{
		//The string "abab"
		setDecomposition(new int[] {0, 4});
	}
	
	public boolean isInLang(String s) 
	{
		String temp = s;
		int n = 0;
		while (temp.startsWith("ab")){
			temp = temp.substring(2);
			n++;
		}
		
		if (n > 0 && n%2 == 0 && temp.length() == 0)			
			return true;
		return false;
	}
}
