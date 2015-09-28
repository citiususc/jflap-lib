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





package edu.duke.cs.jflap.automata;

/**
 * The StringChecker class is useful for determining whether a string has
 * certain characteristics.
 * 
 * @author Thomas Finley
 */

public class StringChecker {
	/**
	 * We can't have people creating instances of us, now can we?
	 */
	private StringChecker() {
	}

	/**
	 * Determines if all characters in a string are alphanumeric, i.e., are
	 * either digits or numbers.
	 * 
	 * @param string
	 *            the string to check
	 * @return <CODE>true</CODE> if all characters in the string are
	 *         alphanumeric, <CODE>false</CODE> if at least one character in
	 *         the string is non-alphanumeric
	 */
	public static boolean isAlphanumeric(String string) {
		for (int i = 0; i < string.length(); i++)
			if (!Character.isLetterOrDigit(string.charAt(i)))
				return false;
		return true;
	}
}
