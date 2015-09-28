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
 * {(<i>ab</i>)<i><sup>n</sup>a<sup>k</sup></i> : <i>n</i> &#62;
 *  <i>k</i>, <i>k</i> &#8805; 0}.
 * 
 * @author Jinghui Lim & Chris Morgan
 *
 */
public class ABnAk extends RegularPumpingLemma 
{
    public String getTitle() 
    {
        return "(ab)^n a^k : n > k, k >= 0";
    }

    public String getHTMLTitle() 
    {
        return "(<i>ab</i>)<i><sup>n</sup>a<sup>k</sup></i> : <i>n</i> "
            + GREATER_THAN + " <i>k</i>, <i>k</i> " + GREATER_OR_EQ + " 0";
    }
    
    protected void setRange()
    {
        myRange = new int[]{2, 11};
    }

    public void setDescription()
    {
    	partitionIsValid = false;
    	explanation = "For any <i>m</i> value, a possible value for <i>w</i> is \"(ab)<sup><i>m</i>+1</sup>" +
    			"a<sup><i>m</i></sup>\".  To be in the language, <i>y</i> must possess \"ab\"s, \"ba\"s, " +
    			"\"a\"s, and/or \"b\"s.  Any multiple or combination thereof yields a string that is not in " +
    			"the language when <i>i</i> = 0, meaning this is not a regular language.";
    }
    
    protected void chooseW() 
    {
        w = pumpString("ab", m + 1) + pumpString("a", m);
    }
    
    public void chooseDecomposition()
    {
    	setDecomposition(new int[]{0, 2});
    }

    public void chooseI() 
    {    	
        i = 0;
    }
     
    public boolean isInLang(String s)
    {
    	int a, b;    	    	
    	char[] list = new char[] {'a'};
    	String temp = s;
    	
    	while (temp.startsWith("ab"))
    		temp = temp.substring(2);
    	if (temp.equals(new String("ab")))
    		return true;
    	if (LemmaMath.isMixture(temp, list))
    		return false;
    	
    	a = LemmaMath.countInstances(s, 'a');
    	b = LemmaMath.countInstances(s, 'b');
    	if (a >= 2*b)
    		return false;    	    	
        return true;
    }
}
