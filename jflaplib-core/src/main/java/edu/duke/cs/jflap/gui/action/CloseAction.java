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

import edu.duke.cs.jflap.gui.editor.EditBlockPane;
import edu.duke.cs.jflap.gui.environment.Environment;
import edu.duke.cs.jflap.gui.environment.tag.PermanentTag;
import edu.duke.cs.jflap.gui.environment.tag.Tag;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.duke.cs.jflap.automata.Automaton;
import edu.duke.cs.jflap.automata.State;

/**
 * The <CODE>CloseAction</CODE> is an action for removing tabs in an
 * environment. It automatically detects changes in the activation of panes in
 * the environment, and changes its enabledness whether or not a pane in the
 * environment is permanent (i.e. should not be closed).
 * 
 * @author Thomas Finley
 */

public class CloseAction extends RestrictedAction {
	/**
	 * Instantiates a <CODE>CloseAction</CODE>.
	 * 
	 * @param environment
	 *            the environment to handle the closing for
	 */
	public CloseAction(Environment environment) {
		super("Dismiss Tab", null);
		this.environment = environment;
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,
				MAIN_MENU_MASK));
		environment.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				checkEnabled();
			}
		});
		checkEnabled();
	}

	/**
	 * Handles the closing on the environment.
	 * 
	 * @param e
	 *            the action event
	 */
	public void actionPerformed(ActionEvent e) {
		boolean editor = false;
		Automaton inside = null;
		State block = null;
		if (environment.getActive() instanceof EditBlockPane) {
			editor = true;
			EditBlockPane blockEditor = (EditBlockPane) environment.getActive();
			inside = blockEditor.getAutomaton();
			block = blockEditor.getBlock();
		}
		environment.remove(environment.getActive());
//		if (editor) {
//			EditorPane higherEditor = (EditorPane) environment.getActive();
//			Automaton higher = higherEditor.getAutomaton();
//			higher.replaceBlock(block, inside);
//		}
	}

	/**
	 * Checks the environment to see if the currently active object has the
	 * <CODE>PermanentTag</CODE> associated with it, and if it does, disables
	 * this action; otherwise it makes it activate.
	 */
	private void checkEnabled() {
		Tag tag = environment.getTag(environment.getActive());
		
		if(environment.tabbed.getTabCount() == 1){
			setEnabled(false);
		}
		else setEnabled(!(tag instanceof PermanentTag));
	}

	/** The environment to handle the closing of tabs for. */
	private Environment environment;
}
