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





package edu.duke.cs.jflap.automata.vdg;

import edu.duke.cs.jflap.automata.Transition;
import edu.duke.cs.jflap.automata.State;

/**
 * A <CODE>VDGTransition</CODE> is a <CODE>Transition</CODE> object used by
 * Variable Dependecy Graphs (VDGs). They have no labels.
 * 
 * @author Ryan Cavalcante
 */

public class VDGTransition extends Transition {
	/**
	 * Instantiates a new <CODE>VDGTransition</CODE> object.
	 * 
	 * @param from
	 *            the state this transition comes from.
	 * @param to
	 *            the state this transition goes to.
	 */
	public VDGTransition(State from, State to) {
		super(from, to);
	}

	/**
	 * Produces a copy of this transition with new from and to states.
	 * 
	 * @param from
	 *            the new from state
	 * @param to
	 *            the new to state
	 * @return a copy of this transition with the new states
	 */
	public Transition copy(State from, State to) {
		return new VDGTransition(from, to);
	}

	/**
	 * Returns a string representation of this object. This is the same as the
	 * string representation for a regular transition object.
	 * 
	 * @see edu.duke.cs.jflap.automata.Transition#toString
	 * @return a string representation of this object
	 */
	public String toString() {
		return super.toString();
	}
}
