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
 * encode.
 * 
 * @author Thomas Finley
 */

public interface Encoder {
	/**
	 * Given a structure, this will attempt to write the structure to a file.
	 * This method should always return a file, or throw an
	 * {@link EncodeException} in the event of failure with a message detailing
	 * the nature of why the encoding failed.
	 * 
	 * @param structure
	 *            the structure to encode
	 * @param file
	 *            the file to save to
	 * @param parameters
	 *            implementors have the option of accepting custom parameters in
	 *            the form of a map
	 * @return the file to which the structure was written
	 * @throws EncodeException
	 *             if there was a problem writing the file
	 */
	public File encode(Serializable structure, File file, Map parameters);

	/**
	 * Returns if this type of structure can be encoded with this encoder. This
	 * should not perform a detailed check of the structure, since the user will
	 * have no idea why it will not be encoded correctly if the {@link #encode}
	 * method does not throw a {@link ParseException}.
	 * 
	 * @param structure
	 *            the structure to check
	 * @return if the structure, perhaps with minor changes, could possibly be
	 *         written to a file
	 */
	public boolean canEncode(Serializable structure);

	/**
	 * Proposes a file name for a given structure. This encoder should return
	 * either the file name, or a file name more amenable to the format this
	 * encoder will encode in. The file name suggested should be a fixed point
	 * for this method, i.e.
	 * <code>x.proposeFilename(x.proposeFilename(name,S),S)</code> should
	 * always equal <code>x.proposeFilename(name,S)</code>, where
	 * <code>S</code> is any structure.
	 * 
	 * @param filename
	 *            the proposed file name
	 * @param structure
	 *            the structure that will be saved
	 * @return the file name, either original or modified
	 */
	public String proposeFilename(String filename, Serializable structure);
}
