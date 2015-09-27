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
 * This error indicates that a structure could not be properly encoded with the
 * encoder. This should be thrown by {@link edu.duke.cs.jflap.file.Encoder} implementing objects
 * to indicate a problem writing the file.
 * 
 * @author Thomas Finley
 */

public class EncodeException extends RuntimeException {
	/**
	 * Creates a generic encoder exception.
	 */
	public EncodeException() {
		super();
	}

	/**
	 * Creates a encode exception with the given message.
	 * 
	 * @param message
	 *            the exception message
	 */
	public EncodeException(String message) {
		super(message);
	}
}
