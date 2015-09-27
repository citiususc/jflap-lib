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





package edu.duke.cs.jflap.gui.environment.tag;

/**
 * A satisfier is a general object that takes an object and its tags, and
 * returns whether or not it satisfies some general property. Usually the tagged
 * object should be enough to satisfy the requirements, but the object that is
 * tagged is passed along as well in case it is important.
 * 
 * @see edu.duke.cs.jflap.gui.environment.tag.Tag
 * @see edu.duke.cs.jflap.gui.environment.Environment#add
 * 
 * @author Thomas Finley
 */

public interface Satisfier {
	/**
	 * Checks to see if an object and its tag satisfy some properties
	 * 
	 * @param object
	 *            the object, in case it is useful
	 * @param tag
	 *            an object associated with <CODE>object</CODE>, which
	 *            presumably implements some varieties of <CODE>tag</CODE> to
	 *            identify the object
	 * @return <CODE>true</CODE> if this object with this tag satisfies
	 *         whatever this satisfier wishes to satisfy, or <CODE>false</CODE>
	 *         if it does not
	 */
	public boolean satisfies(Object object, Tag tag);
}
