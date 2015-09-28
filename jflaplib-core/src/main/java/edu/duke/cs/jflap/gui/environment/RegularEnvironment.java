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





package edu.duke.cs.jflap.gui.environment;

import edu.duke.cs.jflap.regular.RegularExpression;
import edu.duke.cs.jflap.regular.ExpressionChangeListener;
import edu.duke.cs.jflap.regular.ExpressionChangeEvent;

/**
 * This is the environment for a regular expression.
 * 
 * @author Thomas Finley
 */

public class RegularEnvironment extends Environment {
	/**
	 * Instantiates an <CODE>RegularEnvironment</CODE> for the given regular
	 * expression.
	 * 
	 * @param expression
	 *            the regular expression
	 */
	public RegularEnvironment(RegularExpression expression) {
		super(expression);
		expression.addExpressionListener(new Listener());
	}

	/**
	 * Returns the regular expression that this environment manages.
	 * 
	 * @return the regular expression that this environment manages
	 */
	public RegularExpression getExpression() {
		return (RegularExpression) super.getObject();
	}

	/**
	 * The expression change listener for a regular expression detects if there
	 * are changes in the environment, and if so, sets the dirty bit for the
	 * file.
	 */
	private class Listener implements ExpressionChangeListener {
		public void expressionChanged(ExpressionChangeEvent e) {
			setDirty();
		}
	}

	/**
	 * Returns if this environment dirty. An environment is called dirty if the
	 * object it holds has been modified since the last save.
	 * 
	 * @return <CODE>true</CODE> if the environment is dirty, <CODE>false</CODE>
	 *         otherwise
	 */
	public boolean isDirty() {
		getExpression().asString(); // Force resolution of reference.
		return super.isDirty();
	}
}
