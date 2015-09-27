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

import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;

/**
 * The <CODE>NewHelpAction</CODE> is an extension of the <CODE>HelpAction</CODE>
 * that, whenever an action is received, puts up the help code for the
 * {@link edu.duke.cs.jflap.gui.action.NewAction}. This class is intended only for the special
 * purpose of being used in the new structure creation window, which is just a
 * list of buttons and does not have the same structure as a document window.
 * 
 * @author Thomas Finley
 */

public class NewHelpAction extends HelpAction {
	/**
	 * Instantiates an <CODE>EnvironmentHelpAction</CODE>.
	 */
	public NewHelpAction() {

	}

	/**
	 * Displays help according to the current display of the automaton.
	 * 
	 * @param event
	 *            the action event
	 */
	public void actionPerformed(ActionEvent event) {
		/* Formerly the help page was shown, but for now, we just refer
		 * one to the tutorial.
		 */
		//displayHelp(NewAction.class);
		
		//Temporary command
		JOptionPane.showMessageDialog(null, "For help, feel free to access the JFLAP tutorial at\n" +
				"                          www.jflap.org.", "Help", JOptionPane.PLAIN_MESSAGE);
	}
}
