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





package edu.duke.cs.jflap.gui.environment;

import java.util.EventObject;
import java.io.File;

/**
 * This is an event that registers with a listener that an environment has
 * changed its file.
 * 
 * @see edu.duke.cs.jflap.gui.environment.FileChangeListener
 * @see edu.duke.cs.jflap.gui.environment.Environment
 * @see edu.duke.cs.jflap.gui.environment.Environment#setFile
 * @see edu.duke.cs.jflap.gui.environment.Environment#getFile
 * 
 * @author Thomas Finley
 */

public class FileChangeEvent extends EventObject {
	/**
	 * Instantiates a new <CODE>FileChangeEvent</CODE>.
	 * 
	 * @param environment
	 *            the environment that threw this event
	 * @param oldFile
	 *            the previous file that was the file of the <CODE>Environment</CODE>
	 */
	public FileChangeEvent(Environment environment, File oldFile) {
		super(environment);
		this.oldFile = oldFile;
	}

	/**
	 * Returns the native file for the environment before the change.
	 * 
	 * @return the native file for the environment before the change
	 */
	public File getOldFile() {
		return oldFile;
	}

	/** The old file that was the native file for the environment. */
	private File oldFile;
}
