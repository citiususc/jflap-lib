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
import javax.swing.filechooser.FileFilter;

/**
 * This object is both an encoder and decoder, and is useful as a file filter.
 */

public abstract class Codec extends FileFilter implements Encoder, Decoder {
	/**
	 * Whether the given file is accepted by the codec. This method is
	 * implemented to, by default, always return true.
	 * 
	 * @param f
	 *            the file to check for acceptance
	 * @return in this implementation, always <CODE>true</CODE>
	 */
	public boolean accept(File f) {
		return true;
	}

	/**
	 * Given a proposed filename, returns a new suggested filename. This method
	 * is implemented in this abstract class to always return the original
	 * filename, so subclasses that wish more desirable functionality should
	 * override this method to, say, add a suffix for example.
	 * 
	 * @param filename
	 *            the proposed name
	 * @param structure
	 *            the structure that will be saved
	 * @return the new suggestion for a name
	 */
	public String proposeFilename(String filename, Serializable structure) {
		return filename;
	}

	/**
	 * Returns an instance of a corresponding encoder. In many cases the
	 * returned will be <CODE>this</CODE>.
	 * 
	 * @return an encoder that encodes in the same format this decodes in, or
	 *         <CODE>null</CODE> if there is no such encoder
	 */
	public Encoder correspondingEncoder() {
		return this;
	}

	/**
	 * Returns the root of a filename that supposedly belongs to this codec. The
	 * default implementation assumes that if a modification will happen, it
	 * will be some suffix with a period.
	 * 
	 * @param filename
	 *            the filename, perhaps with an extension
	 * @param structure
	 *            the structure saved at the file with that name
	 * @return the "root" of the filename
	 */
	public String rootFilename(String filename, Serializable structure) {
		int lastIndex = filename.lastIndexOf('.');
		if (lastIndex == -1)
			return filename;
		String smallname = filename.substring(0, lastIndex);
		if (filename.equals(proposeFilename(smallname, structure)))
			return smallname;
		return filename;
	}
}
