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
 * {<i>a<sup>n</sup>b<sup>n</sup>c<sup>n</sup></i> : <i>n</i> &#8805; 0}.
 * 
 * @author Jinghui Lim & Chris Morgan
 *
 */
public class AnBnCn extends ContextFreePumpingLemma 
{
    public String getTitle() 
    {
        return "a^n b^n c^n";
    }

    public String getHTMLTitle() 
    {
        return "<i>a<sup>n</sup>b<sup>n</sup>c<sup>n</sup></i> : <i>n</i> " 
            + GREATER_OR_EQ + " 0";
    }
    
    public void setDescription()
    {
    	partitionIsValid = false;
    	explanation = "For any <i>m</i> value, a possible value for <i>w</i> is \"a<sup><i>m</i></sup>" +
    			"b<sup><i>m</i></sup>c<sup><i>m</i></sup>\".  With this example, it is impossible to " +
    			"have \"a\"s, \"b\"s, and \"c\"s in both <i>v</i> and <i>y</i> together.  Thus, if " +
    			"<i>i</i> " + NOT_EQUAL + " 1, an inequality will be generated, meaning this is not a " +
    			"context-free language.";
    }
    
    protected void chooseW() 
    {
        w = pumpString("a", m) + pumpString("b", m) + pumpString("c", m);
    }

    public void chooseI() 
    {
        i = LemmaMath.flipCoin();
    }
    
    protected void setRange()
    {
        myRange = new int[]{3, 11};
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
                    return new int[]{1, 1, 0, m-1};
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
                    return new int[]{m - 1, 1, 0, 1};
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
                    return new int[]{m - 1, 2, 0, 1};
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
                    return new int[]{m, 1, 0, 1};
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
                    return new int[]{2 * m - 2, 1, 0, 2};
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
                    return new int[]{2 * m - 1, 1, 0, 1};
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
                    return new int[]{2 * m - 1, 2, 0, 1};
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
                    return new int[]{2 * m, 1, 0, 1};
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
    	if (a==b && a==c)
    		return true;
        return false;
    }
}
