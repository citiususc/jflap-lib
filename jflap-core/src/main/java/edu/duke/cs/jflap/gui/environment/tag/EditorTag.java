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
 * An editor tag is a tag intended for use to tag some object that is intended
 * to be used as some sort of editor. In general the tag is meant for something
 * that can change the inner workings of some sort of object, presumably for the
 * purpose, one would assume, of keeping it from doing so if such an edit would
 * be inconvenient or hazardous at some critical time.
 * 
 * @author Thomas Finley
 */

public interface EditorTag extends Tag {

}
