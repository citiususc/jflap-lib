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

import java.util.EventListener;

/**
 * An interface that those interested in changes in the file of an environment
 * should implement.
 * 
 * @see edu.duke.cs.jflap.gui.environment.FileChangeEvent
 * @see edu.duke.cs.jflap.gui.environment.Environment
 * @see edu.duke.cs.jflap.gui.environment.Environment#setFile
 * @see edu.duke.cs.jflap.gui.environment.Environment#getFile
 */

public interface FileChangeListener extends EventListener {
	/**
	 * Registers with the listener that an event has occurred.
	 * 
	 * @param event
	 *            the event
	 */
	public void fileChanged(FileChangeEvent event);
}
