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

import javax.swing.AbstractAction;
import javax.swing.Icon;

/**
 * The <CODE>RestrictedAction</CODE> is the general action that various
 * controllers for operators on objects should subclass. In addition to the
 * usual <CODE>Action</CODE> nicities, the <CODE>RestrictedAction</CODE>
 * also provides facilities for determining which types of objects it may be
 * applied to.
 * 
 * @author Thomas Finley
 */

public abstract class RestrictedAction extends AbstractAction {
	/**
	 * Instantiates a new <CODE>RestrictedAction</CODE>.
	 * 
	 * @param string
	 *            a string description
	 * @param icon
	 *            the optional icon, or <CODE>null</CODE> if there is to be no
	 *            icon associated with this action
	 */
	public RestrictedAction(String string, Icon icon) {
		super(string, icon);
	}

	/**
	 * Given an object, determine if this action is able to be applied to that
	 * object based on its class. By default, this method simply returns <CODE>true</CODE>
	 * to indicate that any sort of object is acceptable.
	 * 
	 * @param object
	 *            the object to test for "applicability"
	 * @return <CODE>true</CODE> if this action should be available to an
	 *         object of this type, <CODE>false</CODE> otherwise.
	 */
	public static boolean isApplicable(Object object) {
		return true;
	}

	/**
	 * This is the main "menu mask" for items, for those that wish to have
	 * accelerators for their items and want the "traditional" platform specific
	 * enabler (e.g., command on Macintosh, control on Wintel, and meta on
	 * *n[iu]x).
	 */
	protected static int MAIN_MENU_MASK = MenuConstants.getMainMenuMask();
}
