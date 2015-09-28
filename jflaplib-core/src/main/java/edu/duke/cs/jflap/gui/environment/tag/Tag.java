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
 * A tag is simply an interface that can be applied to an object to indicate
 * that it satisfies some sort of property. The intention is for a tag object to
 * have absolutely no methods to implement. In this way a tag object functions
 * much like the bitfield vectors of yore for identifying them with particular
 * characteristics, but without the inconvinience of having particular bits tied
 * to certain values that absolutely everybody and his mother had to be made
 * aware of. A tag object may simply be something that implements <CODE>Tag</CODE>
 * only to indicate that it has no tag.
 * 
 * @see edu.duke.cs.jflap.gui.environment.Environment#add
 * 
 * @author Thomas Finley
 */

public interface Tag {

}
