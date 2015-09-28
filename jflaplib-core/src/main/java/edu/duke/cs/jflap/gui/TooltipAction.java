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





package edu.duke.cs.jflap.gui;

import javax.swing.AbstractAction;

/**
 * This is a subclass of abstract action that allows one to set the tooltip text
 * from the constructor.
 * 
 * @author Thomas Finley
 */

public abstract class TooltipAction extends AbstractAction {
	public TooltipAction(String name, String tooltip) {
		super(name);
		putValue(SHORT_DESCRIPTION, tooltip);
	}

	/**
	 * Sets the tool tip description.
	 * 
	 * @param tip
	 *            the new tool tip
	 */
	public void setTip(String tip) {
		if (tip == null) {
			putValue(SHORT_DESCRIPTION, tip);
			return;
		}
		if (tip.equals(getValue(SHORT_DESCRIPTION)))
			return;
		putValue(SHORT_DESCRIPTION, tip);
	}

	/**
	 * Gets the tool tip description.
	 * 
	 * @return the tool tip for this action
	 */
	public String getTip() {
		return (String) getValue(SHORT_DESCRIPTION);
	}
}
