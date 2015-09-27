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

import edu.duke.cs.jflap.pumping.*;

/**
 * The context-free pumping lemma for <i>L</i> = 
 * {<i>w<sub>1</sub>b<sup>n</sup>w<sub>2</sub></i> : <i>n<sub>a</sub></i>(<i>w<sub>1</sub></i>) 
 * &#60; <i>n<sub>a</sub></i>(<i>w<sub>2</sub></i>),  <i>n</i> > n<sub>a</sub>(<i>w<sub>1</sub></i>), 
 * <i>w<sub>1</sub></i> & <i>w<sub>2</sub></i> &#8712; {<i>a</i>, <i>b</i>}*}.
 * 
 * @author Chris Morgan
 */
public class W1BnW2 extends ContextFreePumpingLemma {

	public String getTitle() 
    {
        return "w1 + b^n + w2 : na(w1) < na(w2) & na(w1) < n, w1 & w2 element_of {ab}*";
    }

    public String getHTMLTitle() 
    {
        return "<i>w<sub>1</sub>b<sup>n</sup>w<sub>2</sub></i> : <i>n<sub>a</sub></i>" +
        	   "(<i>w<sub>1</sub></i>) " + LESS_THAN + " <i>n<sub>a</sub></i>(<i>w<sub>2</sub></i>" +
        	   "),  n<sub>a</sub>(<i>w<sub>1</sub></i>) " + LESS_THAN +" <i>n</i>, " +
        	    "<i>w<sub>1</sub></i> & <i>w<sub>2</sub></i> " + ELEMENT_OF + " " + AB_STAR;
    }
    
    public void setDescription()
    {
    	partitionIsValid = false;
    	explanation = "For any <i>m</i> value, a possible value for <i>w</i> is \"a<sup><i>m</i></sup>" +
    			"b<sup><i>m</i>+1</sup>a<sup><i>m</i>+1</sup>\".  To be in the language with " +
    			"this example, <i>v</i> & <i>y</i> together cannot possess substrings that are from " +
    			"'w'<sub>1</sub>, from b<sup>n</sup>, and from 'w<sub>2</sub>'.  Thus, if <i>i</i> = 0, " +
    			"<i>i</i> = 2, or perhaps both, either <i>v</i> or <i>y</i> will violate one of the " +
    			"conditions, meaning there is no valid decomposition.  Thus, this language is not " +
    			"context-free.";
    }
	
	protected void addCases() 
	{
		// TODO Auto-generated method stub

	}	

	protected void chooseW() 
	{
		w = pumpString("a", m) + pumpString("b", m+1) + pumpString("a", m+1);
	}
	
	public void chooseDecomposition()
	{
		String s;
		for (int k=w.length()-1; k>=0; k--) {
			s = w.substring(0, k) + w.substring(k+1);
			if (isInLang(s)) {
				setDecomposition(new int[]{k, 1, 0, 0});
				return;
			}
		}
		super.chooseDecomposition();
	}

	public void chooseI() 
	{
		if (getU().length() < m)
			i = 2;
		else
			i = 0;
	}
	
	protected void setRange() 
	{
		myRange = new int[]{2, 10};
	}		

	public boolean isInLang(String s) 
	{
		char[] list = new char[]{'a','b'};
    	if (LemmaMath.otherCharactersFound(s, list))
    		return false;
		
		int i, a1, a2;
		String w1, w2, temp;
		temp = null;
		i = 0;	
		while (i<s.length()) {
			if (s.charAt(i) == 'b') {
				temp = new String();
				while (i < s.length() && s.charAt(i) == 'b') {
					temp = temp + 'b';
					i++;
				}
								
				w1 = s.substring(0, i-temp.length());
				if (i != s.length())
					w2 = s.substring(i);
				else
					w2 = "";
				a1 = LemmaMath.countInstances(w1, 'a');
				a2 = LemmaMath.countInstances(w2, 'a');
				if (a1 < a2 && temp.length() > a1)
					return true;
			}
			i++;
		}		

		return false;
	}
}
