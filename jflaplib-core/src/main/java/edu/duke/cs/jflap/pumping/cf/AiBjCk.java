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
 * {<i>a<sup>i</sup>b<sup>j</sup>c<sup>k</sup></i> : 
 * <i>i</i> &#62; <i>j</i>, <i>i</i> &#62; <i>k</i>}.
 * 
 * @author Jinghui Lim & Chris Morgan
 *
 */
public class AiBjCk extends ContextFreePumpingLemma 
{
    public String getTitle() 
    {
        return "a^i b^j c^k : i > j, i > k";
    }

    public String getHTMLTitle() 
    {
        return "<i>a<sup>i</sup>b<sup>j</sup>c<sup>k</sup></i> : <i>i</i> " + GREATER_THAN +
            " <i>j</i>, <i>i</i> " + GREATER_THAN + " <i>k</i>";
    }
    
    public void setDescription()
    {
    	partitionIsValid = false;
    	explanation = "For any <i>m</i> value, a possible value for <i>w</i> is \"a<sup><i>m</i>+1</sup>" +
			"b<sup><i>m</i></sup>c<sup><i>m</i></sup>\".  The <i>v</i> and <i>y</i> values together " +
			"thus would have a maximum of two unique letters.  Any possible <i>v</i> or <i>y</i> values " +
			"would then be problematic if <i>i</i> = 0, <i>i</i> = 2, or perhaps both.  Thus, this language " +
			"is not context-free.";
    }

    protected void chooseW() 
    {
        w = pumpString("a", getM() + 1) + pumpString("b", getM()) + pumpString("c", getM());
    }
    
    public void chooseDecomposition()
    {
    	int a, b, c;
    	a = LemmaMath.countInstances(w, 'a');
    	b = LemmaMath.countInstances(w, 'b');
    	c = LemmaMath.countInstances(w, 'c');
    	if (a > b+1 && a > c+1)
    		setDecomposition(new int[] {0, 1, 0, 0});
    	else
    		super.chooseDecomposition();
    }

    public void chooseI() 
    {
        if(getV().indexOf("a") == -1 && getY().indexOf("a") == -1)
            i = 2;
        else
            i = 0;
    }
    
    protected void setRange()
    {
        myRange = new int[]{3, 7};
    }

    protected void addCases() 
    {
        /*
         * v is a string of a's and y is a string of a's
         */
        myAllCases.add(new Case()
            {
                public boolean isCase(String v, String y) 
                {
                    if(v.indexOf("a") > -1 && v.indexOf("b") == -1 && v.indexOf("c") == -1 &&
                        y.indexOf("a") > -1 && y.indexOf("b") == -1 && y.indexOf("c") == -1)
                        return true;
                    return false;
                }
                
                public String description() 
                {
                    return "v is a string of \"a\"s and y is a string of \"a\"s";
                }
                
                public int[] getPreset()
                {
                    return new int[]{0, 1, 0, 1};                    
                }
            });
        /*
         * v is a string of a's and y is a string of a's followed by b's
         */
        myAllCases.add(new Case()
            {
                public boolean isCase(String v, String y) 
                {
                    if(v.indexOf("a") > -1 && v.indexOf("b") == -1 && v.indexOf("c") == -1 &&
                        y.indexOf("a") > -1 && y.indexOf("b") > -1 && y.indexOf("c") == -1)
                        return true;
                    return false;
                }
                
                public String description() 
                {
                    return "v is a string of \"a\"s and y is a string of \"a\"s followed by \"b\"s";
                }
                
                public int[] getPreset()
                {
                    return new int[]{m - 1, 1, 0, 2};
                }
            });
        /*
         * v is a string of a's and y is a string of b's
         */
        myAllCases.add(new Case()
            {
                public boolean isCase(String v, String y) 
                {
                    if(v.indexOf("a") > -1 && v.indexOf("b") == -1 && v.indexOf("c") == -1 &&
                        y.indexOf("a") == -1 && y.indexOf("b") > -1 && y.indexOf("c") == -1)
                        return true;
                    return false;
                }
                
                public String description() 
                {
                    return "v is a string of \"a\"s and y is a string of \"b\"s";
                }
                
                public int[] getPreset()
                {
                    return new int[]{m, 1, 0, 1};
                }
            });
        /*
         * v is a string of a's followed by b's and y is a string of b's
         */
        myAllCases.add(new Case()
            {
                public boolean isCase(String v, String y) 
                {
                    if(v.indexOf("a") > -1 && v.indexOf("b") > -1 && v.indexOf("c") == -1 &&
                        y.indexOf("a") == -1 && y.indexOf("b") > -1 && y.indexOf("c") == -1)
                        return true;
                    return false;
                }
                
                public String description() 
                {
                    return "v is a string of \"a\"s followed by \"b\"s and y is a string of \"b\"s";
                }
                
                public int[] getPreset()
                {
                    return new int[]{m, 2, 0, 1};
                }
            });
        /*
         * v is a string of b's and y is a string of b's
         */
        myAllCases.add(new Case()
            {
                public boolean isCase(String v, String y) 
                {
                    if(v.indexOf("a") == -1 && v.indexOf("b") > -1 && v.indexOf("c") == -1 &&
                        y.indexOf("a") == -1 && y.indexOf("b") > -1 && y.indexOf("c") == -1)
                        return true;
                    return false;
                }
                
                public String description() 
                {
                    return "v is a string of \"b\"s and y is a string of \"b\"s";
                }
                
                public int[] getPreset()
                {
                    return new int[]{m + 1, 1, 0, 1};
                }
            });
        /*
         * v is a string of b's and y is a string of b's followed by c's
         */
        myAllCases.add(new Case()
            {
                public boolean isCase(String v, String y) 
                {
                    if(v.indexOf("a") == -1 && v.indexOf("b") > -1 && v.indexOf("c") == -1 &&
                        y.indexOf("a") == -1 && y.indexOf("b") > -1 && y.indexOf("c") > -1)
                        return true;
                    return false;
                }
                
                public String description() 
                {
                    return "v is a string of \"b\"s and y is a string of \"b\"s followed by \"c\"s";
                }
                
                public int[] getPreset()
                {
                    return new int[]{2 * m - 1, 1, 0, 2};
                }
            });
        /*
         * v is a string of b's and y is a string of c's
         */
        myAllCases.add(new Case()
            {
                public boolean isCase(String v, String y) 
                {
                    if(v.indexOf("a") == -1 && v.indexOf("b") > -1 && v.indexOf("c") == -1 &&
                        y.indexOf("a") == -1 && y.indexOf("b") == -1 && y.indexOf("c") > -1)
                        return true;
                    return false;
                }
                
                public String description() 
                {
                    return "v is a string of \"b\"s and y is a string of \"c\"s";
                }
                
                public int[] getPreset()
                {
                    return new int[]{2 * m, 1, 0, 1};
                }
            });
        /*
         * v is a string of b's followed by c's and y is a string of c's
         */
        myAllCases.add(new Case()
            {
                public boolean isCase(String v, String y) 
                {
                    if(v.indexOf("a") == -1 && v.indexOf("b") > -1 && v.indexOf("c") > -1 &&
                        y.indexOf("a") == -1 && y.indexOf("b") == -1 && y.indexOf("c") > -1)
                        return true;
                    return false;
                }
                
                public String description() 
                {
                    return "v is a string of \"b\"s followed by \"c\"s and y is a string of \"c\"s";
                }
                
                public int[] getPreset()
                {
                    return new int[]{2 * m, 2, 0, 1};
                }
            });
        /*
         * v is a string of c's and y is a string of c's
         */
        myAllCases.add(new Case()
            {
                public boolean isCase(String v, String y) 
                {
                    if(v.indexOf("a") == -1 && v.indexOf("b") == -1 && v.indexOf("c") > -1 &&
                        y.indexOf("a") == -1 && y.indexOf("b") == -1 && y.indexOf("c") > -1)
                        return true;
                    return false;
                }
                
                public String description() 
                {
                    return "v is a string of \"c\"s and y is a string of \"c\"s";
                }
                
                public int[] getPreset()
                {
                    return new int[]{2 * m + 1, 1, 0, 1};
                }
            });
        /*
         * v is an empty string and y is a non-empty string
         */
        myAllCases.add(new Case()
            {
                public boolean isCase(String v, String y) 
                {
                    if(v.length() == 0 && y.length() > 0)
                        return true;
                    return false;
                }
                
                public String description() 
                {
                    return "v is an empty string and y is a non-empty string";
                }
                
                public int[] getPreset()
                {
                    return new int[]{2 * m, 0, 1, 1};
                }
            });
        /*
         * v is a non-empty string and y is an empty string
         */
        myAllCases.add(new Case()
            {
                public boolean isCase(String v, String y) 
                {
                    if(v.length() > 0 && y.length() == 0)
                        return true;
                    return false;
                }
                
                public String description() 
                {
                    return "v is a non-empty string and y is an empty string";
                }
                
                public int[] getPreset()
                {
                    return new int[]{2 * m, 1, 0, 0};
                }
            });
    }
    
    public boolean isInLang(String s)
    {
    	int a, b, c;
    	char[] list = new char[]{'a','b','c'};
    	if (LemmaMath.isMixture(s, list))
    		return false;    	
    	a = LemmaMath.countInstances(s, 'a');
    	b = LemmaMath.countInstances(s, 'b');
    	c = LemmaMath.countInstances(s, 'c');
    	
    	if (a>b && a>c)
    		return true;    	
        return false;
    }
}
