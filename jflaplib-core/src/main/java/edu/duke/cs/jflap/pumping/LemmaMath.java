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




package edu.duke.cs.jflap.pumping;

import java.util.Random;

/**
 * This class encapsulates many static computational methods used by the pumping
 * lemmas that do not necessarily need to be in the PumpingLemma class.
 * 
 * @author Chris Morgan & Jinghui Lim
 */
public class LemmaMath {
	/**
     * A random number generator. Here, it is used to generate random <code>boolean</code>
     * and <code>int</code> values.
     */
    private static Random RAND_GENERATOR = new Random();
	/**
     * Returns 0 with probability 0.5 and 2 with probability 0.5.
     * This should be used to randomize whether a pumping lemma is
     * pumped 0 or 2 times when both will give a contradiction.
     * 
     * @return 0 with probability 0.5 and 2 with probability 0.5
     */
    public static int flipCoin()
    {
        if(RAND_GENERATOR.nextBoolean())
            return 0;
        else
            return 2;
    }
    
    /**
     * Returns a random integer between min & max (both inclusive).
     * 
     * @return a random integer between min & max (both inclusive).
     */
    public static int fetchRandInt(int min, int max){
    	return RAND_GENERATOR.nextInt(max-min+1) + min;
    }
    
    /**
     * Counts the number of times the character 'c' appears in s.  Used
     * for subclasses to determine if a particular string is a valid instance
     * of the language
     * 
     * @return # of instances of c in s
     */
    public static int countInstances(String s, char c){
    	int count=0;
    	for (int i=0; i<s.length(); i++)
    		if (s.charAt(i) == c)
    			count++;
    	return count;
    }
	
    /**
     * Checks to see if there is a "mixture" of intertwined characters.  A
     * mixture is defined as when all alike characters in a given string are not
     * adjacent to each other as a block.  For example, the strings "aaabbbccc" and
     *  "abcc" would not be mixtures, but the strings "aaabbbaa", "ababa", and
     *  "aaaccbbbc" would be, because not all a's, b's and c's therein form unique 
     *  blocks of that character.  This also performs the function of 
     *  otherCharactersFound()
     *  
     *  @param c 
     *  	the list of unique characters in the string, in the order the first
     *  	instances of them should be encountered 
     * 
     * @return whether this string is a "mixture"
     */
    public static boolean isMixture(String s, char[] c){
    	int cIndex = 0;
    	char currentChar = ' ';
    	for (int i=0; i<s.length(); i++) {
    		if (currentChar != s.charAt(i)) {
    			while (cIndex < c.length && s.charAt(i) != c[cIndex])
    			   cIndex++;
    			if (cIndex == c.length)
    				return true;    			
    		}
    	}    	   	    	    	    	
    	return false;
    }
    
    /**
     * Checks to see whether there are any other characters in the string besides 
     * those in the char[].
     */
    public static boolean otherCharactersFound(String s, char[] c){
    	boolean found = false;
    	for (int i=0; i<s.length(); i++) {
    		found = false;
    		for (int j=0; j<c.length; j++)
    			if (s.charAt(i)== c[j])
    				found = true;
    		if (!found)
    			return true;
    	}
    	return false;
    }
    
    /**
     * Factorial method.
     * 
     * @param n number to obtain the factorial value of
     * @return n factorial
     */
	public static int factorial(int n) {
        if(n <= 1)
            return 1;
        else
            return n * factorial(n - 1);
    }
    
    /**
     * Checks to see whether n is a factorial.
     * 
     * @return whether n is a factorial.
     */    
    public static boolean isFactorial(int n) {
    	int i, j;
    	i=1;  j=1;
    	do {
    		if (i==n)
    			return true;
    		j++;
    		i = i*j;    		
    	} 
    	while (i<=n);    	
    	return false;
    }
}
