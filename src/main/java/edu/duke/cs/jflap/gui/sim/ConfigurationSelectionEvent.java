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

import java.util.EventObject;

/**
 * The <CODE>ConfigurationSelectionEvent</CODE> is an event thrown by a <CODE>ConfigurationPane</CODE>
 * whenever a transition is selected or deselected in that pane. Because many
 * transitions may be selected or deselected at once, this merely registers that
 * a change has happened without registering exactly which have been changed.
 * 
 * @see edu.duke.cs.jflap.gui.sim.ConfigurationSelectionListener
 * @see edu.duke.cs.jflap.gui.sim.ConfigurationPane
 * 
 * @author Thomas Finley
 */

public class ConfigurationSelectionEvent extends EventObject {
	/**
	 * Instantiates a new <CODE>ConfigurationSelectionEvent</CODE> object.
	 * 
	 * @param configurationPane
	 *            the configuration pane where the selection state has changed
	 */
	public ConfigurationSelectionEvent(ConfigurationPane configurationPane) {
		super(configurationPane);
	}

	/**
	 * Returns the configuration pane that generated this event.
	 * 
	 * @return the configuration pane that generated this event
	 */
	public ConfigurationPane getPane() {
		return (ConfigurationPane) getSource();
	}
}
