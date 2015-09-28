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

import edu.duke.cs.jflap.pumping.*;

/**
 * The regular pumping lemma for <i>L</i> = 
 * {<i>b<sup>k</sup>(ab)<sup>n</sup>(ba)<sup>n</sup></i> : 
 * <i>k</i> &#8805; 4, <i>n</i> = 1,2...}.
 * 
 * @author Chris Morgan
 */
public class BkABnBAn extends RegularPumpingLemma {	

	public String getHTMLTitle() 
	{
		return "<i>b<sup>k</sup>(ab)<sup>n</sup>(ba)<sup>n</sup></i> : <i>k</i> " + GREATER_OR_EQ + " 4"+
			   ", <i>n</i> = 1,2...";	    
	}

	public String getTitle() 
	{
		return "b^k (ab)^n (ba)^n: k>=4, n = 1,2,...";
	}
	
	public void setDescription()
    {
		partitionIsValid = false;
    	explanation = "For any <i>m</i> value, a possible value for <i>w</i> is \"b<sup>4</sup>" +
			"(ab)<sup><i>m</i>/2</sup>(ba)<sup><i>m</i>/2</sup>\".  No possible <i>y</i> value among the " +
			"\"b<sup>4</sup>(ab)<sup><i>m</i>/2</sup>\" segment will work, so the " +
			"language is not regular.";	
    }
	
	public void chooseI() 
	{
		i = 0;
	}

	public void chooseDecomposition()
	{
		int a, abba;
		a = w.indexOf('a');
		abba = a + (w.length() - a) / 2 - 2;
		if (a > 4)
			setDecomposition(new int[] {0, 1});
		else if (abba + 4 <= m)
			setDecomposition(new int[] {abba, 4});		
		else
			super.chooseDecomposition();
		
	}
	
	protected void chooseW() 
	{
		int power = m / 2;
		w = "bbbb" + pumpString("ab", power) + pumpString("ba", power);
	}
	
	protected void setRange() 
	{
		myRange = new int[] {4, 15};
	}

	public boolean isInLang(String s) 
	{
		int k, n;
		k = s.indexOf("a");
		if (k<4)
			return false;
		
		String temp = s.substring(k);
		if (!temp.startsWith("ab"))
			return false;
		
		n = 0;		
		while (temp.startsWith("ab")) {
			temp = temp.substring(2);
			n++;
		}		
	
		while (temp.startsWith("ba")) {
			temp = temp.substring(2);
			n--;
		}
		
		if (n==0 && temp.length()==0)
			return true;
		// TODO Auto-generated method stub
		return false;
	}

}
