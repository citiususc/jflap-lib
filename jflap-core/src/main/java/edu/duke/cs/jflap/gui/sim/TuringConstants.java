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





package edu.duke.cs.jflap.gui.sim;

/**
 * This interface holds constants necessary for the drawing of configuration
 * icons for Turing machines.
 * 
 * @author Thomas Finley
 */

interface TuringConstants {
	/** A simple prefix/postfix string for tape. */
	public static final String FIX = FixCreator.getFix();

	/** The size of the tape head. */
	public static final int SIZE_HEAD = 4;

	static class FixCreator {
		public static String getFix() {
			char c = edu.duke.cs.jflap.automata.turing.Tape.BLANK;
			StringBuffer b = new StringBuffer();
			for (int i = 0; i < 20; i++)
				b.append(c);
			return b.toString();
		}
	}
}
