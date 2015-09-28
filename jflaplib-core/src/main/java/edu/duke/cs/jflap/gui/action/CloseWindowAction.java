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





package edu.duke.cs.jflap.gui.action;

import edu.duke.cs.jflap.gui.environment.EnvironmentFrame;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

/**
 * The <CODE>CloseWindowAction</CODE> invokes the close method on the <CODE>EnvironmentFrame</CODE>
 * to which they belong.
 * 
 * @author Thomas Finley
 */

public class CloseWindowAction extends RestrictedAction {
	/**
	 * Instantiates a <CODE>CloseWindowAction</CODE>.
	 * 
	 * @param frame
	 *            the <CODE>EnvironmentFrame</CODE> to dismiss when an action
	 *            is registered
	 */
	public CloseWindowAction(EnvironmentFrame frame) {
		super("Close", null);
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_W,
				MAIN_MENU_MASK));
		this.frame = frame;
	}

	/**
	 * Handles the closing of the window.
	 */
	public void actionPerformed(ActionEvent event) {
		frame.close();
	}

	/** The environment frame to call the close method on. */
	EnvironmentFrame frame;
}
