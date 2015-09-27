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





package edu.duke.cs.jflap.file;

/**
 * This error indicates elements in a file are arranged in such a fashion as to
 * prevent the correct initialization of a structure. This should be thrown by
 * {@link edu.duke.cs.jflap.file.Decoder} implementing objects to indicate a problem processing
 * the data in the file that indicates that the data is formatted correctly, but
 * that trying to make a JFLAP structure out of it is impossible. For example,
 * the data could be formatted correctly, but be inconsistent.
 * 
 * @author Thomas Finley
 */

public class DataException extends RuntimeException {
	/**
	 * Creates a generic data exception.
	 */
	public DataException() {
		super();
	}

	/**
	 * Creates a data exception with the given message.
	 * 
	 * @param message
	 *            the exception message
	 */
	public DataException(String message) {
		super(message);
	}
}
