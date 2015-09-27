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
 * The regular pumping lemma for <i>L</i> = 
 * {<i>bba(ba)<sup>n</sup>a<sup>n-1</sup></i>}.
 * 
 * @author Chris Morgan
 */
public class BBABAnAn extends RegularPumpingLemma {		

	public String getHTMLTitle() 
    {
        return "<i>bba(ba)<sup>n</sup>a<sup>n-1</sup></i>";
    }

	public String getTitle() 
	{
		return "bba(ba)^n a^(n-1)";
	}	

	public void setDescription()
    {
		partitionIsValid = false;
    	explanation = "For any <i>m</i> value, a possible value for <i>w</i> is \"bba(ba)<sup><i>m</i></sup>" +
			"a<sup><i>m</i>-1</sup>\".  No possible <i>y</i> value among the \"bba(ba)<sup><i>m</i></sup>\" " +
			"segment is possible to pump, meaning any possible generated string is not in the language.  " +
			"Thus, the language is not regular.";
    }
	
	public void chooseI() 
	{
		i = LemmaMath.flipCoin();
	}
	
	public void chooseDecomposition()
	{
		setDecomposition(new int[]{1, 2});
	}

	protected void chooseW() 
	{
		w = "bba" + pumpString("ba", m) + pumpString("a", m-1);
	}
	
	protected void setRange() 
	{
		myRange = new int[] {5, 10};
	}
	
	public boolean isInLang(String s) 
	{		
    	if (!s.startsWith("bba"))
			return false;
    	
    	String temp = s.substring(3);
		int n = 0;		
		while (temp.startsWith("ba")) {
			temp = temp.substring(2);
			n++;
		}		
		while (temp.startsWith("a")) {
			temp = temp.substring(1);
			n--;
		}
			
		if (n==1 && temp.length()==0)
    		return true;
		return false;
	}
}
