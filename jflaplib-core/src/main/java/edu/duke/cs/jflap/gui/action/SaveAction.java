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

import edu.duke.cs.jflap.gui.environment.Environment;
import edu.duke.cs.jflap.gui.environment.Universe;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

/**
 * The <CODE>SaveAction</CODE> is an action to save a serializable object
 * contained in an environment to a file.
 * 
 * @author Thomas Finley
 */

public class SaveAction extends SaveAsAction {
	/**
	 * Instantiates a new <CODE>SaveAction</CODE>.
	 * 
	 * @param environment
	 *            the environment that holds the serializable
	 */
	public SaveAction(Environment environment) {
		super(environment);
		putValue(NAME, "Save");
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S,
				MAIN_MENU_MASK));
		this.environment = environment;
	}

	/**
	 * If a save was attempted, call the methods that handle the saving of the
	 * serializable object to a file.
	 * 
	 * @param event
	 *            the action event
	 */
	public void actionPerformed(ActionEvent event) {
		Universe.frameForEnvironment(environment).save(false);
	}

	/** The environment this action will handle saving for. */
	private Environment environment;
}
