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
 * {<i>ww</i> : <i>w</i> &#8712; {<i>a</i>, <i>b</i>}*}.
 * 
 * @author Jinghui Lim & Chris Morgan
 *
 */
public class WW extends ContextFreePumpingLemma 
{
    public String getTitle() 
    {
        return "ww : w element_of {ab}*";
    }

    public String getHTMLTitle() 
    {
        return "<i>ww</i> : <i>w</i> " + ELEMENT_OF + " " + AB_STAR;
    }

    public void setDescription()
    {
    	partitionIsValid = false;
    	explanation = "For any <i>m</i> value, a possible value for <i>w</i> is \"a<sup><i>m</i></sup>" +
    			"b<sup><i>m</i></sup>a<sup><i>m</i></sup>b<sup><i>m</i></sup>\".  To be in the language with " +
    			"this example, <i>v</i> & <i>y</i> together cannot possess identical letters that are from separate " +
    			"blocks of alike letters (ex: <i>v</i> has \"b\"s from the first set of \"b\"s, " +
    			"while <i>y</i> has \"b\"s from the second set of \"b\"s.  Because of this, any increase or decrease in " +
    			"\"a\"s or \"b\"s will not be matched by any corresponding change in the other blocks of similar letters, " +
    			"resulting in an inequality that prevents the decomposition from working.  Thus, this language is " +
    			"not context-free.";
    }
    
    protected void chooseW() 
    {
        w = pumpString("a", getM()) + pumpString("b", getM()) + 
            pumpString("a", getM()) + pumpString("b", getM());
    }

    public void chooseDecomposition() 
    {       	
    	int x, half;
    	half = w.length()/2;
    	String first, last;    	
    	//If possible, just set the first characters in the w strings.
    	if (m > half) {
    		setDecomposition(new int[] {0, 1, half-1, 1});
    		return;
    	}
    	//Otherwise, check to see if there is a pattern.
    	for (int i=half-m+1; i<half; i++)
    		for (int j=0; j<i; j++)
    			if (w.charAt(i) == w.charAt(j)) {
    				first = w.substring(0, i) + pumpString("" + w.charAt(i), 2) + w.substring(i+1, half);
    				last = w.substring(0, j) + pumpString("" + w.charAt(j), 2) + w.substring(j+1, half);
    				x = half-i+j-1;
    				if (first.equals(last) && x <= m-2) {
    					setDecomposition(new int[] {i, 1, x, 1});
    					return;
    				}
    			}
    	//If none will always win, choose a random one.
    	super.chooseDecomposition();
    }
    
    public void chooseI() 
    {
        i = 0;
    }
    
    protected void setRange()
    {
        myRange = new int[]{3, 6};
    }

    protected void addCases() 
    {
        /*
         * v is string of a's and y is string of a's
         */
        myAllCases.add(new Case()
            {
                public boolean isCase(String v, String y) 
                {
                    if(v.indexOf("a") > -1 && v.indexOf("b") == -1 &&
                        y.indexOf("a") > -1 && y.indexOf("b") == -1)
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
         * v is string of a's and y is string of a's followed by b's
         */
        myAllCases.add(new Case()
            {
                public boolean isCase(String v, String y) 
                {
                    if(v.indexOf("a") > -1 && v.indexOf("b") == -1 &&
                        y.indexOf("a") > -1 && y.indexOf("b") > -1 && 
                        y.indexOf("a") < y.indexOf("b"))
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
         * v is string of a's and y is string of b's
         */
        myAllCases.add(new Case()
            {
                public boolean isCase(String v, String y) 
                {
                    if(v.indexOf("a") > -1 && v.indexOf("b") == -1 && 
                        y.indexOf("a") == -1 && y.indexOf("b") > -1)
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
         * v is string of a's followed by b's and y is string of b's
         */
        myAllCases.add(new Case()
            {
                public boolean isCase(String v, String y) 
                {
                    if(v.indexOf("a") > -1 && v.indexOf("b") > -1 &&
                        v.indexOf("a") < v.indexOf("b") &&
                        y.indexOf("a") == -1 && y.indexOf("b") > -1)
                        
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
                    if(v.indexOf("a") == -1 && v.indexOf("b") > -1 &&
                        y.indexOf("a") == -1 && y.indexOf("b") > -1)
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
         * v is a string of b's and y is a string of b's followed by a's
         */
        myAllCases.add(new Case()
            {
                public boolean isCase(String v, String y) 
                {
                    if(v.indexOf("a") == -1 && v.indexOf("b") > -1 &&
                        y.indexOf("a") > -1 && y.indexOf("b") > -1 && 
                        y.indexOf("a") > y.indexOf("b"))
                        return true;
                    return false;
                }

                public String description() 
                {
                    return "v is a string of \"b\"s and y is a string of \"b\"s followed by \"a\"s";
                }

                public int[] getPreset()
                {
                    return new int[]{2 * m - 2, 1, 0, 2};
                }
            });
        /*
         * v is a string of b's and y is a string of a's
         */
        myAllCases.add(new Case()
            {
                public boolean isCase(String v, String y) 
                {
                    if(v.indexOf("a") == -1 && v.indexOf("b") > -1 &&
                        y.indexOf("a") > -1 && y.indexOf("b") == -1)
                        return true;
                    return false;
                }

                public String description() 
                {
                    return "v is a string of \"b\"s and y is a string of \"a\"s";
                }

                public int[] getPreset()
                {
                    return new int[]{2 * m - 1, 1, 0, 1};
                }
            });
        /*
         * v is a string of b'a followed by a's and y is a string of a's
         */
        myAllCases.add(new Case()
            {
                public boolean isCase(String v, String y) 
                {
                    if(v.indexOf("a") > -1 && v.indexOf("b") > -1 && 
                        v.indexOf("a") > v.indexOf("b") &&
                        y.indexOf("a") > -1 && y.indexOf("b") > -1 )
                        return true;
                    return false;
                }

                public String description() 
                {
                    return "v is a string of \"b\"s followed by \"a\"s and y is a string of \"a\"s";
                }
                        
                public int[] getPreset()
                {
                    return new int[]{2 * m - 1, 2, 0, 1};
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
    	int a, b;
    	char[] list = new char[]{'a','b'};
    	if (LemmaMath.otherCharactersFound(s, list))
    		return false;
    	
    	String first, last;
    	int halfSize = s.length() / 2;
    	first = s.substring(0, halfSize);
    	last = s.substring(halfSize);
    	if (first.equals(last))
    		return true;
    	
        return false;
    }
}
