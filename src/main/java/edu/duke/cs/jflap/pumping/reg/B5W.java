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
 * {<i>b<sup>5</sup>w</i> : <i>w</i> &#8712; {<i>a</i>, <i>b</i>}*, 
 * 2<i>n<sub>a</sub></i> (<i>w</i>) = 3<i>n<sub>b</sub></i> (<i>w</i>)}.
 * 
 * @author Chris Morgan
 */
public class B5W extends RegularPumpingLemma {

	public String getTitle() 
	{
		return "b^5w: w element_of {ab}* : 2na(w) = 3nb(w)";
	}
	
	public String getHTMLTitle() 
	{
		return "<i>b<sup>5</sup>w</i> : <i>w</i> " + ELEMENT_OF + " " + AB_STAR+ ", 2<i>n<sub>a</sub></i> (<i>w</i>) = " +
        " 3<i>n<sub>b</sub></i> (<i>w</i>)";
	}

	public void setDescription()
    {
		partitionIsValid = false;
    	explanation = "For any <i>m</i> value " + GREATER_OR_EQ +" 6, a possible value for <i>w</i> is " +
    	"\"b<sup>5</sup>b<sup>2(<i>m</i>-5)</sup>a<sup>3(<i>m</i>-5)</sup>\".  The <i>y</i> value thus would " +
    	"be a multiple of \"b\".  For any <i>i</i> " + NOT_EQUAL + " 1, 2n<sub>a</sub>('w') " + NOT_EQUAL + 
    	" 3n<sub>b</sub>('w') or n<sub>b</sub> in the whole string" + LESS_THAN +" 5, giving a string which is " +
    	"not in the language.  Thus, the language is not regular.";	   
    }
	
	public void chooseI() 
	{
		i = LemmaMath.flipCoin();
	}		

	protected void chooseW() 
	{		
		int count = m-5;
		w = "bbbbb" + pumpString("b", 2*count) + pumpString("a", 3*count);
	}

	public void chooseDecomposition()
	{
		int a, count;
		count = 5;   a = 0;
		//must be at least 3 a's for equality to work
		while (a<3) {
			if (w.charAt(count) == 'a')
				a++;
			count++;
		}
		setDecomposition(new int[]{Math.min(count-5, m-5), 5});
	}
	
	protected void setRange() 
	{
		myRange = new int[] {6, 10};
	}
	
	public boolean isInLang(String s) {
		char[] list = new char[]{'a', 'b'};
    	if (LemmaMath.otherCharactersFound(s, list))
    		return false;
    	if (!s.startsWith("bbbbb"))
			return false;
    	
    	int a, b;
    	String temp = s.substring(5);
    	a = LemmaMath.countInstances(temp, 'a');
    	b = LemmaMath.countInstances(temp, 'b');    	
    	if (2*a == 3*b)
    		return true;
        return false;
	}
}
