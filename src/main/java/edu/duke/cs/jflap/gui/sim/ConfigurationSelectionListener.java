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





package edu.duke.cs.jflap.gui.sim;

import java.util.EventListener;

/**
 * An interface that those interested in changes in selection of the
 * configuration pane should implement.
 * 
 * @see edu.duke.cs.jflap.gui.sim.ConfigurationSelectionEvent
 * @see edu.duke.cs.jflap.gui.sim.ConfigurationPane#addSelectionListener
 */

public interface ConfigurationSelectionListener extends EventListener {
	/**
	 * Registers with the listener that an event has occurred.
	 * 
	 * @param event
	 *            the event
	 */
	public void configurationSelectionChange(ConfigurationSelectionEvent event);
}
