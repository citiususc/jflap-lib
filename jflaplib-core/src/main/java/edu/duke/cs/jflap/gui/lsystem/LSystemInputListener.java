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





package edu.duke.cs.jflap.gui.lsystem;

import java.util.EventListener;

/**
 * An interface that those interested in user change of an L-system should
 * listen to.
 * 
 * @see edu.duke.cs.jflap.gui.lsystem.LSystemInputEvent
 * @see edu.duke.cs.jflap.gui.lsystem.LSystemInputPane
 */

public interface LSystemInputListener extends EventListener {
	/**
	 * Registers with the listener that the user has changed the L-system.
	 * 
	 * @param event
	 *            the event
	 */
	public void lSystemChanged(LSystemInputEvent event);
}
