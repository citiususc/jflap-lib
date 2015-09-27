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

import edu.duke.cs.jflap.pumping.LemmaMath;
import edu.duke.cs.jflap.pumping.RegularPumpingLemma;

/**
 * The regular pumping lemma for <i>L</i> = {<i>b<sup>5</sup>w</i> : <i>w</i> &#8712; 
 * {<i>a</i>, <i>b</i>}*, (2<i>n<sub>a</sub></i> (<i>w</i>) +  5<i>n<sub>b</sub></i> 
 * (<i>w</i>)) mod 3 = 0}.
 * 
 * @author Chris Morgan
 */
public class B5Wmod extends RegularPumpingLemma {
	
	public String getTitle() 
	{
		return "b^5w: w element_of {ab}* : (2na(w) + 5nb(w)) % 3 = 0";
	}
	
	public String getHTMLTitle() 
	{
		return "<i>b<sup>5</sup>w</i> : <i>w</i> " + ELEMENT_OF + " " + AB_STAR+ 
			   ", (2<i>n<sub>a</sub></i> (<i>w</i>) + " +
			   " 5<i>n<sub>b</sub></i> (<i>w</i>)) mod 3 = 0";
	}			
	
	public void setDescription()
    {
		partitionIsValid = true;
    	explanation = "Because this is a regular language, a valid decomposition exists.  If <i>m</i> " + GREATER_OR_EQ +" 8, " +
    			"as long as |<i>y</i>| % 3 = 0 and none of the first 5 \"b\"s are in <i>y</i>, the decomposition is " +
    			"successful.  Pumping any possible combination of 3 characters yields a string divisible by 3.";	
    }
	
	public void chooseI() 
	{
		i = 0;
	}

	protected void chooseW() 
	{
		w = "bbbbb";
		for (int i=5; i < m || (i-5)%3 != 0; i++)
			if (LemmaMath.flipCoin() == 2)
				w = w + 'a';
			else
				w = w + 'b';
	}

	public void chooseDecomposition()
	{
		//first three values of 'w' in the equation fit the equation no matter what.
		setDecomposition(new int[] {5, 3});
	}
	
	protected void setRange() 
	{
		myRange = new int[]{8, 20};
	}

	public boolean isInLang(String s) 
	{
		char[] list = new char[]{'a', 'b'};
    	if (LemmaMath.otherCharactersFound(s, list))
    		return false;
    	if (!s.startsWith("bbbbb"))
			return false;
    	
    	int a, b;
    	String temp = s.substring(5);
    	a = LemmaMath.countInstances(temp, 'a');
    	b = LemmaMath.countInstances(temp, 'b');    	
    	if ((2*a + 5*b) % 3 == 0)
    		return true;
        return false;
	}
}
