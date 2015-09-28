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





package edu.duke.cs.jflap.gui.event;

import java.util.EventObject;

/**
 * The <CODE>SelectionEvent</CODE> is an event indicating that a selection has
 * changed in an object.
 * 
 * @author Thomas Finley
 */

public class SelectionEvent extends EventObject {
	/**
	 * Instantiates a new <CODE>SelectionEvent</CODE>.
	 * 
	 * @param object
	 *            the generating object, whose selection has changed
	 */
	public SelectionEvent(Object object) {
		super(object);
	}
}
