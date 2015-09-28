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

import edu.duke.cs.jflap.grammar.lsystem.LSystem;
import edu.duke.cs.jflap.gui.environment.LSystemEnvironment;

import javax.swing.Icon;

/**
 * The <CODE>GrammarAction</CODE> is the general action that various
 * controllers for operators on grammars should subclass. The only real change
 * from the <CODE>RestrictedAction</CODE> is that by default the <CODE>.isAcceptable</CODE>
 * method now only returns true if the object is an instance of <CODE>Grammar</CODE>.
 * 
 * @see edu.duke.cs.jflap.grammar.Grammar
 * 
 * @author Thomas Finley
 */

public abstract class LSystemAction extends RestrictedAction {
	/**
	 * Instantiates a new <CODE>LSystemAction</CODE>.
	 * 
	 * @param env
	 *            the environment
	 * @param string
	 *            a string description
	 * @param icon
	 *            the optional icon, or <CODE>null</CODE> if there is to be no
	 *            icon associated with this action
	 */
	public LSystemAction(LSystemEnvironment env, String string, Icon icon) {
		super(string, icon);
		environment = env;
	}

	/**
	 * Given an object, determine if this grammar action is able to be applied
	 * to that object based on its class. By default, this method returns <CODE>true</CODE>
	 * if this object is an instance of <CODE>LSystem</CODE>.
	 * 
	 * @param object
	 *            the object to test for "applicability"
	 * @return <CODE>true</CODE> if this action should be available to an
	 *         object of this type, <CODE>false</CODE> otherwise.
	 */
	public static boolean isApplicable(Object object) {
		return object instanceof LSystem;
	}

	/**
	 * Returns the environment.
	 * 
	 * @return the L-system environment
	 */
	protected LSystemEnvironment getEnvironment() {
		return environment;
	}

	/** The L-system environment. */
	private LSystemEnvironment environment;
}
