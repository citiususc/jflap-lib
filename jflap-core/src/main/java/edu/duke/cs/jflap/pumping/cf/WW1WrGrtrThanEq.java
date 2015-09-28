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




package edu.duke.cs.jflap.pumping.cf;

import edu.duke.cs.jflap.pumping.ContextFreePumpingLemma;
import edu.duke.cs.jflap.pumping.LemmaMath;

/**
 * The context-free pumping lemma for <i>L</i> =
 * {<i>ww<sub>1</sub>w<sup>R</sup></i> : |<i>w<sub>1</sub></i>| &#8805; 5, 
 * <i>w</i> & <i>w<sub>1</sub></i> &#8712; {<i>a</i>, <i>b</i>}*}.
 * 
 * @author Chris Morgan
 */
public class WW1WrGrtrThanEq extends ContextFreePumpingLemma {

	public String getTitle() 
    {
        return "w w1 w^R : |w1| >= 5, w & w1 element_of {ab}*";
    }

    public String getHTMLTitle() 
    {
        return "<i>ww<sub>1</sub>w<sup>R</sup></i> : |<i>w<sub>1</sub></i>| "
        	   + GREATER_OR_EQ + " 5, <i>w</i> & <i>w<sub>1</sub></i> " 
        	   + ELEMENT_OF + " " + AB_STAR;
    }
    
    public void setDescription()
    {
    	partitionIsValid = true;
    	explanation = "Because this is a context-free language, a valid decomposition exists.  For any <i>m</i> value " + 
    			GREATER_OR_EQ + " 6, it is possible to assign to both 'w' and 'w<sup>R</sup>' the empty string.  Thus, " +
    			"|'w<sub>1</sub>'| "+ GREATER_OR_EQ +" 6.  If |<i>v</i>| = 0 and <i>y</i> is one character from " +
    			"'w<sub>1</sub>', |'w<sub>1</sub>'| " + GREATER_OR_EQ + " 5 for all values of <i>i</i>.";    		
    }
	
	protected void addCases() 
	{
		// TODO Auto-generated method stub
	}

	public void chooseI() 
	{
		i = LemmaMath.flipCoin();
	}

	protected void chooseW() 
	{
		w = pumpString("a", m) + "babab" + pumpString("a", m);
	}

	protected void setRange() 
	{
		myRange = new int[]{1, 7};
	}
	
	public void chooseDecomposition()
	{	
		//always chooses the middle character, which is part of w1
		setDecomposition(new int[] {w.length()/2, 1, 0, 0});
	}

	public boolean isInLang(String s) 
	{
		char[] list = new char[]{'a','b'};
		if (LemmaMath.otherCharactersFound(s, list))
			return false;
		
		if (s.length() >= 5)
			return true;
		return false;
	}
	
}
