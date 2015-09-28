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





/*---------------------------------------------------------------------------
 File:				gui/SuperMouseAdapter.java
 Package:			JAWAA Editor Version 1.0
 Author:				Thomas Finley 
 Date:				August 2001
 --------------------------------------------------------------------------*/

/*---------------------------------------------------------------------------    
 -- JAWAA Editor 1.0 --

 Copyright information:

 Susan H. Rodger, Thomas Finley
 Computer Science Department
 Duke University
 August 2001
 Supported by National Science Foundation DUE-9752583.

 Copyright (c) 2001
 All rights reserved.

 Redistribution and use in source and binary forms are permitted
 provided that the above copyright notice and this paragraph are
 duplicated in all such forms and that any documentation,
 advertising materials, and other materials related to such
 distribution and use acknowledge that the software was developed
 by the author.  The name of the author may not be used to
 endorse or promote products derived from this software without
 specific prior written permission.
 THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ---------------------------------------------------------------------------*/

package edu.duke.cs.jflap.gui;

import java.awt.event.*;

/**
 * The SuperMouseAdapter takes on the responsibilities we see both in the
 * MouseAdapter and the MouseMotionAdapter, because we unfortunately cannot be a
 * subclass of two classes. This is a convinience class so that when we want to
 * have something that is clickable or dragable or something, we don't have to
 * define all the methods when we just want to do, say, two things.
 * 
 * @author Thomas Finley
 */

public abstract class SuperMouseAdapter implements MouseListener,
		MouseMotionListener {

	/**
	 * Invoked when a mouse button is clicked on a component.
	 * 
	 * @param event
	 *            the MouseEvent to process
	 */
	public void mouseClicked(MouseEvent event) {
	}

	/**
	 * Invoked when the mouse enters a component.
	 * 
	 * @param event
	 *            the MouseEvent to process
	 */
	public void mouseEntered(MouseEvent event) {
	}

	/**
	 * Invoked when the mouse exits a component.
	 * 
	 * @param event
	 *            the MouseEvent to process
	 */
	public void mouseExited(MouseEvent event) {
	}

	/**
	 * Invoked when a mouse button is held down on a component.
	 * 
	 * @param event
	 *            the MouseEvent to process
	 */
	public void mousePressed(MouseEvent event) {
	}

	/**
	 * Invoked when a mouse button is released on a component.
	 * 
	 * @param event
	 *            the MouseEvent to process
	 */
	public void mouseReleased(MouseEvent event) {
	}

	/**
	 * Invoked when a mouse is dragged over this component with a button down.
	 * 
	 * @param event
	 *            the MouseEvent to process
	 */
	public void mouseDragged(MouseEvent event) {
	}

	/**
	 * Invoked when a mouse is moved over this component with no buttons down.
	 * 
	 * @param event
	 *            the MouseEvent to process
	 */
	public void mouseMoved(MouseEvent event) {
	}
}
