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
 * {<i>w<sub>1</sub>vv<sup>R</sup>w<sub>2</sub></i>, : 
 * <i>n<sub>a</sub></i>(<i>w<sub>1</sub></i>) = 
 * <i>n<sub>a</sub></i>(<i>w<sub>2</sub></i>),  
 * |<i>v</i>| &#8805; 3, <i>v</i>, <i>w<sub>1</sub></i>,   
 * & <i>w<sub>2</sub></i> &#8712; {<i>a</i>, <i>b</i>}*}.
 * 
 * @author Chris Morgan
 */
public class W1VVrW2 extends ContextFreePumpingLemma {

	public String getTitle() 
    {
        return "w1 v v^R w2 : na(w1) = na(w2), |v|>=3, w1 & w2 element_of {ab}*";
    }

    public String getHTMLTitle() 
    {
        return "<i>w<sub>1</sub>vv<sup>R</sup>w<sub>2</sub></i>, : " +
        	   "<i>n<sub>a</sub></i>(<i>w<sub>1</sub></i>) = " +
        	   "<i>n<sub>a</sub></i>(<i>w<sub>2</sub></i>),  " +
        	   "|<i>v</i>| " + GREATER_THAN +" 3,  <i>v</i>, <i>w<sub>1</sub>, " +
        	   "w<sub>2</sub> " + ELEMENT_OF + " " + AB_STAR;        	    
    }
    
    public void setDescription()
    {
    	partitionIsValid = true;
    	explanation = "Because this is a context-free language, a valid decomposition exists.  If |'v'| " + GREATER_THAN + " 3, " +
    			"or if <i>m</i> " + GREATER_OR_EQ + " 8 and there are no \"b\"s in w<sub>1</sub> and w<sub>2</sub>, one could " +
    			"just pump single opposite characters in 'v' and 'v<sup>R</sup>' repeatedly to find a valid decomposition.  " +
    			"For example, if |'v'| = 4, then <i>v</i> could equal the fourth character of 'v' and <i>y</i> the first " +
    			"character of 'v<sup>R</sup>'.  Otherwise, if <i>m</i> " + GREATER_OR_EQ + " 8 and |v| = 3, one could just " +
    			"pump the first \"b\" value in w<sub>1</sub> or w<sub>2</sub>.";			
    }
    
	protected void addCases() 
	{
		// TODO Auto-generated method stub
	}

	public void chooseI()
	{
		i = 3;
	}

	protected void chooseW() 
	{
		int power = m / 2;
		w = pumpString("ab", power) + "abbbba" + pumpString("ab", power);
	}

	protected void setRange()
	{
		myRange = new int[]{2, 15};
	}
	
	/**
	 * This method returns the first acceptable vv<sup>R</sup> segment that it 
	 * can find.
	 * 
	 * @param s the string in which to find the vv<sup>R</sup> segment.
	 * @return the segment in an int[], with the first index of the segment of the 
	 * given string in the first array item, and the last index in the second array item.
	 */
	private int[] getVVr(String s) {
		if (s.length()<6)
			return null;
				
		boolean match;
		for (int end = s.length() - 1; end >= 5; end--)
			for (int start = 0; start <= end - 5; start++)				
				if ((end-start) % 2 == 1 && s.charAt(start) == s.charAt(end)) {					
					match = true;
					for (int i=0; i<=(end-start)/2; i++)
						if (s.charAt(start+i) != s.charAt(end-i))
							match = false;
					if (match && LemmaMath.countInstances(s.substring(0, start), 'a') == 
			    		LemmaMath.countInstances(s.substring(end+1), 'a'))
						return new int[] {start, end};				
				}
		
		return null;
	}
	
	public void chooseDecomposition()
	{
		int[] v = getVVr(w);
		String w1, w2;
		w1 = w.substring(0, v[0]);
		w2 = w.substring(v[1]+1);
		//If possible, the last character in v and the first in v^R
		if (v[1]-v[0] > 5 || LemmaMath.countInstances(w1, 'b') == 0 &&  
			LemmaMath.countInstances(w2, 'b') == 0)
			setDecomposition(new int[] {v[0] + (v[1]-v[0]) / 2, 1, 0, 1});
		//Otherwise, pump the first 'b' character found
		else if (w1.indexOf('b') > -1)
			setDecomposition(new int[] {w1.indexOf('b'), 1, 0, 0});
		else
			setDecomposition(new int[] {w2.indexOf('b') + v[1] + 1, 1, 0, 0});
	}

	public boolean isInLang(String s) 
	{	
		char[] list = new char[]{'a', 'b'};
    	if (LemmaMath.otherCharactersFound(s, list))
    		return false;
		
		if (getVVr(s) == null)
			return false;
		else
    		return true;
	}
}
