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

import java.io.File;
import java.io.Serializable;
import java.util.Map;

/**
 * This specifies the common interface for objects that parse documents and
 * produce a corresponding structure. Ideally the <CODE>toString</CODE> method
 * should be implemented with a brief description of the format of file this can
 * decode.
 * 
 * @author Thomas Finley
 */

public interface Decoder {
	/**
	 * Given a file, this will return a JFLAP structure associated with that
	 * file. This method should always return a structure, or throw a
	 * {@link ParseException} in the event of failure with a message detailing
	 * the nature of why the decoder failed.
	 * 
	 * @param file
	 *            the file to decode into a structure
	 * @param parameters
	 *            implementors have the option of accepting custom parameters in
	 *            the form of a map
	 * @return a JFLAP structure resulting from the interpretation of the file
	 * @throws ParseException
	 *             if there was a problem reading the file
	 */
	public Serializable decode(File file, Map parameters);

	/**
	 * Returns an instance of a corresponding encoder. In many cases the
	 * returned will be <CODE>this</CODE>.
	 * 
	 * @return an encoder that encodes in the same format this decodes in, or
	 *         <CODE>null</CODE> if there is no such encoder
	 */
	public Encoder correspondingEncoder();
}
