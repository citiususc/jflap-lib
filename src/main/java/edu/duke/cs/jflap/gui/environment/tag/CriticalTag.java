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
 * A critical tag is used to mark a component whose "stability" requires that
 * the object which is part of an environment remain uneditable for the course
 * of an action. In short, those components with tags marked as <CODE>EditorTag</CODE>
 * must be deactivated. The intention is that an <CODE>Environment</CODE> will
 * detect the presence of critical tagged objects, and will not allow other
 * <CODE>EditorTag</CODE> objects to be selectable.
 * 
 * @see edu.duke.cs.jflap.gui.environment.Environment
 * @see edu.duke.cs.jflap.gui.environment.tag.EditorTag
 * 
 * @author Thomas Finley
 */

public interface CriticalTag extends Tag {

}
