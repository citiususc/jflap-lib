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





package edu.duke.cs.jflap.regular;

import java.util.EventObject;

/**
 * This event should be distributed when a regular expression object changes.
 * 
 * @author Thomas Finley
 */

public class ExpressionChangeEvent extends EventObject {
	/**
	 * Instantiates a change event.
	 * 
	 * @param expression
	 *            the expression object that was changed
	 * @param old
	 *            the string representing the old regular expression
	 */
	public ExpressionChangeEvent(RegularExpression expression, String old) {
		super(expression);
		this.expression = expression;
		this.old = old;
	}

	/**
	 * Returns the regular expression that was changed.
	 * 
	 * @return the regular expression that was changed
	 */
	public RegularExpression getExpression() {
		return expression;
	}

	/**
	 * Returns the old string representation of the expression.
	 * 
	 * @return the old string representation of the expression
	 */
	public String getOld() {
		return old;
	}

	/** The changed RE. */
	private RegularExpression expression;

	/** The old string representation of the RE. */
	private String old;
}
