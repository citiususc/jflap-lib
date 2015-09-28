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

import edu.duke.cs.jflap.gui.environment.RegularEnvironment;

import javax.swing.Icon;

import edu.duke.cs.jflap.regular.RegularExpression;

/**
 * The <CODE>RegularAction</CODE> is the general action that various
 * controllers for operators on regular expressions should subclass. The only
 * real change from the <CODE>RestrictedAction</CODE> is that by default the
 * <CODE>.isAcceptable</CODE> method now only returns true if the object is an
 * instance of <CODE>RegularExpression</CODE>.
 * 
 * @author Thomas Finley
 */

public abstract class RegularAction extends RestrictedAction {
	/**
	 * Instantiates a new <CODE>RegularAction</CODE>.
	 * 
	 * @param string
	 *            a string description
	 * @param icon
	 *            the optional icon, or <CODE>null</CODE> if there is to be no
	 *            icon associated with this action
	 * @param environment
	 */
	public RegularAction(String string, Icon icon,
			RegularEnvironment environment) {
		super(string, icon);
		this.environment = environment;
	}

	/**
	 * Given an object, determine if this automaton action is able to be applied
	 * to that object based on its class. By default, this method returns <CODE>true</CODE>
	 * if this object is an instance of <CODE>Automaton</CODE>.
	 * 
	 * @param object
	 *            the object to test for "applicability"
	 * @return <CODE>true</CODE> if this action should be available to an
	 *         object of this type, <CODE>false</CODE> otherwise.
	 */
	public static boolean isApplicable(Object object) {
		return object instanceof RegularExpression;
	}

	/**
	 * Returns the environment associated with this action
	 * 
	 * @return the environment for this action
	 */
	protected RegularEnvironment getEnvironment() {
		return environment;
	}

	/**
	 * Returns expression for this action's environment.
	 * 
	 * @return the expression for this action's environment
	 */
	protected RegularExpression getExpression() {
		return environment.getExpression();
	}

	/** The environment associated with this action. */
	private RegularEnvironment environment;
}
