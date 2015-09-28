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





package edu.duke.cs.jflap.automata.event;

import java.util.EventListener;

/**
 * An interface that those interested in changes in transitions in an automata
 * should listen to.
 * 
 * @see edu.duke.cs.jflap.automata.event.AutomataTransitionEvent
 * @see edu.duke.cs.jflap.automata.Automaton#addTransitionListener
 */

public interface AutomataNoteListener extends EventListener {
	/**
	 * Registers with the listener that an event has occurred.
	 * 
	 * @param event
	 *            the event
	 */
	public void automataNoteChange(AutomataNoteEvent event);
}
