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
 * {<i>w</i> &#8712; {<i>a</i>, <i>b</i>}* : <i>n<sub>a</sub></i> 
 * (<i>w</i>) &#60; <i>n<sub>b</sub></i> (<i>w</i>)}.
 * 
 * @author Jinghui Lim & Chris Morgan
 */
public class NaNb extends RegularPumpingLemma 
{
    public String getHTMLTitle() 
    {
        return "<i>w</i> " + ELEMENT_OF + " " + AB_STAR + " : <i>n<sub>a</sub></i> (<i>w</i>) " +
            LESS_THAN + " <i>n<sub>b</sub></i> (<i>w</i>)";
    }
	
	public String getTitle() 
    {
        return "w element_of {ab}* : na(w) < nb(w)";
    }

    public void setDescription()
    {
    	partitionIsValid = false;
    	explanation = "For any <i>m</i> value, a possible value for <i>w</i> is \"a<sup><i>m</i></sup>" +
			"b<sup><i>m</i>+1</sup>\".  The <i>y</i> value thus would be a multiple of \"a\".  " +
			"For any <i>i</i> " + GREATER_THAN + " 1, n<sub>a</sub> " + GREATER_OR_EQ + " n<sub>b</sub>, " +
			"giving a string which is not in the language.  Thus, the language is not regular.";
    }
    
    protected void chooseW() 
    {
        w = pumpString("a", getM()) + pumpString("b", getM() + 1);
    }

    public void chooseDecomposition() 
    {
    	setDecomposition(new int[] {Math.min(m-1, w.indexOf('b')), 1});
    }
    
    public void chooseI() 
    {
        i = 2;
    }
    
    protected void setRange()
    {
        myRange = new int[]{2, 17};
    }        
    
    public boolean isInLang(String s)
    {
    	int a, b;
    	char[] list = new char[]{'a', 'b'};
    	if (LemmaMath.otherCharactersFound(s, list))
    		return false;
    	
    	a = LemmaMath.countInstances(s, 'a');
    	b = LemmaMath.countInstances(s, 'b');    	
    	if (a < b)
    		return true;
        return false;
    }
}
