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
 * The context-free pumping lemma for <i>L</i> = {<i>w</i> &#8712; 
 * {<i>a</i>, <i>b</i>, <i>c</i>}* : <i>n<sub>a</sub></i> (<i>w</i>) 
 * &#62; <i>n<sub>b</sub></i> (<i>w</i>) = <i>n<sub>c</sub></i> (<i>w</i>)}.
 * 
 * @author Jinghui Lim & Chris Morgan
 *
 */
public class NagNbeNc extends ContextFreePumpingLemma 
{
    public String getTitle() 
    {
        return "w element_of {abc}* : na(w) > nb(w) = nc(w)";
    }

    public String getHTMLTitle() 
    {
        return "<i>w</i> " + ELEMENT_OF + " {<i>a</i>, <i>b</i>, <i>c</i>}* :" +
        " <i>n<sub>a</sub></i> (<i>w</i>) " + GREATER_THAN + 
        " <i>n<sub>b</sub></i> (<i>w</i>) = <i>n<sub>c</sub></i> (<i>w</i>)";
    }
    
    public void setDescription()
    {
    	partitionIsValid = false;
    	explanation = "For any <i>m</i> value, a possible value for <i>w</i> is \"a<sup><i>m</i>+1</sup>" +
    			"b<sup><i>m</i></sup>c<sup><i>m</i></sup>\".  With this example, it is impossible to " +
    			"have \"a\"s, \"b\"s, and \"c\"s in both <i>v</i> and <i>y</i> together.  Thus, if " +
    			"<i>i</i> = 0, <i>i</i> = 2, or perhaps both, one of the inequalities will be violated, " +
    			"meaning there is no valid decomposition.  Thus, this language is not context-free.";
    }

    protected void chooseW() 
    {
        w = pumpString("a", getM() + 1) + pumpString("b", getM()) + pumpString("c", getM());
    }
    
    public void chooseDecomposition() 
    {
    	int a, b;
    	a = LemmaMath.countInstances(w, 'a');
    	b = LemmaMath.countInstances(w, 'b');
    	if (a > b+1)
    		setDecomposition(new int[]{0, 1, 0, 0});
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
    	if (LemmaMath.otherCharactersFound(s, list))
    		return false;
    	
    	a = LemmaMath.countInstances(s, 'a');
    	b = LemmaMath.countInstances(s, 'b');
    	c = LemmaMath.countInstances(s, 'c');
    	if (a > b && b == c)
    		return true;
        return false;
    }
}
