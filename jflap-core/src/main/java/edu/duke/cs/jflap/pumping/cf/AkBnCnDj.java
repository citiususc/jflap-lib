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
 * {<i>a<sup>k</sup>b<sup>n</sup>c<sup>n</sup>d<sup>j</sup></i> : 
 * <i>j</i> &#8800; k}.
 * 
 * @author Chris Morgan & Chris Morgan
 */
public class AkBnCnDj extends ContextFreePumpingLemma {

	public String getTitle() 
    {
        return "a^k b^n c^n d^j : j != k";
    }

    public String getHTMLTitle() 
    {
        return "<i>a<sup>k</sup>b<sup>n</sup>c<sup>n</sup>d<sup>j</sup></i> : <i>j</i> " +
        		NOT_EQUAL + " k";
    }

    public void setDescription()
    {
    	partitionIsValid = true;
    	explanation = "Because this is a context-free language, a valid decomposition exists.  For all m " + GREATER_OR_EQ +" 2, " +
    			"if <i>n</i> " + GREATER_OR_EQ +" 1, <i>v</i> could equal \"b\" and <i>y</i> could equal \"c\".  If <i>n</i> " +
    			"= 0 and <i>k</i> & <i>j</i> " + GREATER_OR_EQ +" 1, <i>v</i> could equal \"a\" and <i>y</i> could equal " +
    			"\"d\".  If <i>n</i> = 0 and only one of <i>k</i> or <i>j</i> " + GREATER_OR_EQ +" 1, <i>v</i> could equal \"a\" " +
    			"or \"d\" (whichever one is in the string).  and <i>y</i> could be empty.  This covers all possible combinations.";
    }
    
	public void chooseI() 
	{
		int da = LemmaMath.countInstances(getV(), 'a') + LemmaMath.countInstances(getY(), 'a');		
		if (da == 1)
			i = 2;
		else
			i = 0;
	}

	protected void chooseW() 
	{
		w = pumpString("a", m) + pumpString("b", m) + pumpString("c", m) + pumpString("d", m+1);		
	}
	
	public void chooseDecomposition() 
	{
		int a, b, c, d;
		a = w.indexOf('a');
		b = w.indexOf('b');
		c = w.indexOf('c');
		d = w.indexOf('d');
		
		if (b > -1 && c > -1)
			setDecomposition(new int[] {c-1, 1, 0, 1});
		else if (a > -1 && d > -1)
			setDecomposition(new int[] {d-1, 1, 0, 1});
		else
			super.chooseDecomposition();
	}

	protected void setRange() 
	{		
		myRange = new int[]{3, 5};
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
                    if(v.indexOf("a") > -1 && v.indexOf("b") == -1 && v.indexOf("c") == -1 && v.indexOf("d") == -1 &&
                        y.indexOf("a") > -1 && y.indexOf("b") == -1 && y.indexOf("c") == -1 && y.indexOf("d") == -1)
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
                    if(v.indexOf("a") > -1 && v.indexOf("b") == -1 && v.indexOf("c") == -1 && v.indexOf("d") == -1 &&
                        y.indexOf("a") > -1 && y.indexOf("b") > -1 && y.indexOf("c") == -1 && y.indexOf("d") == -1)
                        return true;
                    return false;
                }
                
                public String description() 
                {
                    return "v is a string of \"a\"s and y is a string of \"a\"s followed by \"b\"s";
                }
                
                public int[] getPreset()
                {
                    return new int[]{m - 2, 1, 0, 2};
                }
            });
        /*
         * v is a string of a's and y is a string of b's
         */
        myAllCases.add(new Case()
            {
                public boolean isCase(String v, String y) 
                {
                    if(v.indexOf("a") > -1 && v.indexOf("b") == -1 && v.indexOf("c") == -1 && v.indexOf("d") == -1 &&
                        y.indexOf("a") == -1 && y.indexOf("b") > -1 && y.indexOf("c") == -1 && y.indexOf("d") == -1)
                        return true;
                    return false;
                }
                
                public String description() 
                {
                    return "v is a string of \"a\"s and y is a string of \"b\"s";
                }
                
                public int[] getPreset()
                {
                    return new int[]{m-1, 1, 0, 1};
                }
            });
        /*
         * v is a string of a's followed by b's and y is a string of b's
         */
        myAllCases.add(new Case()
            {
                public boolean isCase(String v, String y) 
                {
                    if(v.indexOf("a") > -1 && v.indexOf("b") > -1 && v.indexOf("c") == -1 && v.indexOf("d") == -1 &&
                        y.indexOf("a") == -1 && y.indexOf("b") > -1 && y.indexOf("c") == -1 && y.indexOf("d") == -1)
                        return true;
                    return false;
                }
                
                public String description() 
                {
                    return "v is a string of \"a\"s followed by \"b\"s and y is a string of \"b\"s";
                }
                
                public int[] getPreset()
                {
                    return new int[]{m-1, 2, 0, 1};
                }
            });
        /*
         * v is a string of b's and y is a string of b's
         */
        myAllCases.add(new Case()
            {
                public boolean isCase(String v, String y) 
                {
                    if(v.indexOf("a") == -1 && v.indexOf("b") > -1 && v.indexOf("c") == -1 && v.indexOf("d") == -1 &&
                        y.indexOf("a") == -1 && y.indexOf("b") > -1 && y.indexOf("c") == -1 && y.indexOf("d") == -1)
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
                    if(v.indexOf("a") == -1 && v.indexOf("b") > -1 && v.indexOf("c") == -1 && v.indexOf("d") == -1 &&
                        y.indexOf("a") == -1 && y.indexOf("b") > -1 && y.indexOf("c") > -1 && y.indexOf("d") == -1)
                        return true;
                    return false;
                }
                
                public String description() 
                {
                    return "v is a string of \"b\"s and y is a string of \"b\"s followed by \"c\"s";
                }
                
                public int[] getPreset()
                {
                    return new int[]{2*m-2, 1, 0, 2};
                }
            });
        /*
         * v is a string of b's and y is a string of c's
         */
        myAllCases.add(new Case()
            {
                public boolean isCase(String v, String y) 
                {
                    if(v.indexOf("a") == -1 && v.indexOf("b") > -1 && v.indexOf("c") == -1 && v.indexOf("d") == -1 &&
                        y.indexOf("a") == -1 && y.indexOf("b") == -1 && y.indexOf("c") > -1 && y.indexOf("d") == -1)
                        return true;
                    return false;
                }
                
                public String description() 
                {
                    return "v is a string of \"b\"s and y is a string of \"c\"s";
                }
                
                public int[] getPreset()
                {
                    return new int[]{2*m-1, 1, 0, 1};
                }
            });
        /*
         * v is a string of b's followed by c's and y is a string of c's
         */
        myAllCases.add(new Case()
            {
                public boolean isCase(String v, String y) 
                {
                    if(v.indexOf("a") == -1 && v.indexOf("b") > -1 && v.indexOf("c") > -1 && v.indexOf("d") == -1 &&
                        y.indexOf("a") == -1 && y.indexOf("b") == -1 && y.indexOf("c") > -1 && y.indexOf("d") == -1)
                        return true;
                    return false;
                }
                
                public String description() 
                {
                    return "v is a string of \"b\"s followed by \"c\"s and y is a string of \"c\"s";
                }
                
                public int[] getPreset()
                {
                    return new int[]{2*m-1, 2, 0, 1};
                }
            });
        /*
         * v is a string of c's and y is a string of c's
         */
        myAllCases.add(new Case()
            {
                public boolean isCase(String v, String y) 
                {
                    if(v.indexOf("a") == -1 && v.indexOf("b") == -1 && v.indexOf("c") > -1 && v.indexOf("d") == -1 &&
                        y.indexOf("a") == -1 && y.indexOf("b") == -1 && y.indexOf("c") > -1 && y.indexOf("d") == -1)
                        return true;
                    return false;
                }
                
                public String description() 
                {
                    return "v is a string of \"c\"s and y is a string of \"c\"s";
                }
                
                public int[] getPreset()
                {
                    return new int[]{2*m, 1, 0, 1};
                }
            });
        /*
         * v is a string of c's and y is a string of c's followed by d's
         */
        myAllCases.add(new Case()
            {
                public boolean isCase(String v, String y) 
                {
                    if(v.indexOf("a") == -1 && v.indexOf("b") == -1 && v.indexOf("c") > -1 && v.indexOf("d") == -1 &&
                        y.indexOf("a") == -1 && y.indexOf("b") == -1 && y.indexOf("c") > -1 && y.indexOf("d") > -1)
                        return true;
                    return false;
                }
                
                public String description() 
                {
                    return "v is a string of \"c\"s and y is a string of \"c\"s followed by \"d\"s";
                }
                
                public int[] getPreset()
                {
                    return new int[]{3*m-2, 1, 0, 2};
                }
            });
        /*
         * v is a string of c's and y is a string of d's
         */
        myAllCases.add(new Case()
            {
                public boolean isCase(String v, String y) 
                {
                    if(v.indexOf("a") == -1 && v.indexOf("b") == -1 && v.indexOf("c") > -1 && v.indexOf("d") == -1 &&
                        y.indexOf("a") == -1 && y.indexOf("b") == -1 && y.indexOf("c") == -1 && y.indexOf("d") > -1)
                        return true;
                    return false;
                }
                
                public String description() 
                {
                    return "v is a string of \"c\"s and y is a string of \"d\"s";
                }
                
                public int[] getPreset()
                {
                    return new int[]{3*m-1, 1, 0, 1};
                }
            });
        /*
         * v is a string of c's followed by d's and y is a string of d's
         */
        myAllCases.add(new Case()
            {
                public boolean isCase(String v, String y) 
                {
                	if(v.indexOf("a") == -1 && v.indexOf("b") == -1 && v.indexOf("c") > -1 && v.indexOf("d") > -1 &&
                        y.indexOf("a") == -1 && y.indexOf("b") == -1 && y.indexOf("c") == -1 && y.indexOf("d") > -1)
                        return true;
                    return false;
                }
                
                public String description() 
                {
                    return "v is a string of \"c\"s followed by \"d\"s and y is a string of \"d\"s";
                }
                
                public int[] getPreset()
                {
                    return new int[]{3*m-1, 2, 0, 1};
                }
            });
        /*
         * v is a string of d's and y is a string of d's
         */
        myAllCases.add(new Case()
            {
                public boolean isCase(String v, String y) 
                {
                    if(v.indexOf("a") == -1 && v.indexOf("b") == -1 && v.indexOf("c") == -1 && v.indexOf("d") > -1 &&
                        y.indexOf("a") == -1 && y.indexOf("b") == -1 && y.indexOf("c") == -1 && y.indexOf("d") > -1)
                        return true;
                    return false;
                }
                
                public String description() 
                {
                    return "v is a string of \"d\"s and y is a string of \"d\"s";
                }
                
                public int[] getPreset()
                {
                    return new int[]{3*m, 1, 0, 1};
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
                    return new int[]{2*m, 0, 1, 1};
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
                    return new int[]{2*m, 1, 0, 0};
                }
            });
    }
	
	public boolean isInLang(String s) 
	{
		char[] list = new char[]{'a','b','c','d'};
		if (LemmaMath.isMixture(s, list))
			return false;
		
		int[] sections = new int[] {0, 0, 0, 0};		
		int i, j;
		
		i = 0;  j = 0;
		while (i<s.length())
			if (s.charAt(i) != list[j])
				j++;				
			else {
				sections[j]++;
				i++;
			}
		
		if (sections[1] == sections[2] && sections[0] != sections[3])
			return true;		
		return false;
	}
}
