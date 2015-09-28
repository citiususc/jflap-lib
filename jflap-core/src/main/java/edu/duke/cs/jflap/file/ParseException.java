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
 * prevent the interpreter from working correctly. This should be thrown by
 * {@link edu.duke.cs.jflap.file.Decoder} implementing objects to indicate a problem processing
 * the file that could indicate that it is not the target type of file at all.
 * 
 * @author Thomas Finley
 */

public class ParseException extends RuntimeException {
	/**
	 * Creates a generic parse exception.
	 */
	public ParseException() {
		super();
	}

	/**
	 * Creates a parse exception with the given message.
	 * 
	 * @param message
	 *            the exception message
	 */
	public ParseException(String message) {
		super(message);
	}
}
