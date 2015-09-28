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





package edu.duke.cs.jflap.automata.fsa;

import edu.duke.cs.jflap.gui.environment.Universe;
import edu.duke.cs.jflap.automata.Transition;
import edu.duke.cs.jflap.automata.State;

/**
 * An <CODE>FSATransition</CODE> is a <CODE>Transition</CODE> object with an
 * additional field for the label, which determines if the machine should move
 * on this transition.
 * 
 * @see edu.duke.cs.jflap.automata.fsa.FiniteStateAutomaton
 * 
 * @author Thomas Finley
 */

public class FSATransition extends Transition {
	/**
	 * Instantiates a new <CODE>FSATransition</CODE> object.
	 * 
	 * @param from
	 *            the state this transition comes from
	 * @param to
	 *            the state this transition goes to
	 * @param label
	 *            the label for this transition, roughly intended to be that
	 *            string that the current string in the machine should satisfy
	 *            before moving on to the next state
	 */
	public FSATransition(State from, State to, String label) {
		super(from, to);
		setLabel(label);
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
		return new FSATransition(from, to, myLabel);
	}

	/**
	 * Returns the label for this transition.
	 */
	public String getLabel() {
		return myLabel;
	}

	/**
	 * Sets the label for this transition.
	 * 
	 * @param label
	 *            the new label for this transition
	 * @throws IllegalArgumentException
	 *             if the label contains any "bad" characters, i.e., not
	 *             alphanumeric
	 */
	protected void setLabel(String label) {
		myLabel = label;
	}

	/**
	 * Returns the description for this transition.
	 * 
	 * @return the description, in this case, simply the label
	 */
	public String getDescription() {
		String desc = getLabel();
		if (desc.length() == 0)
			return Universe.curProfile.getEmptyString(); // I am a badass.
		return getLabel();
	}

	/**
	 * Returns a string representation of this object. This is the same as the
	 * string representation for a regular transition object, with the label
	 * tacked on.
	 * 
	 * @see edu.duke.cs.jflap.automata.Transition#toString
	 * @return a string representation of this object
	 */
	public String toString() {
		return super.toString() + ": \"" + getLabel() + "\"";
	}

	/**
	 * Returns if this transition equals another object.
	 * 
	 * @param object
	 *            the object to test against
	 * @return <CODE>true</CODE> if the two are equal, <CODE>false</CODE>
	 *         otherwise
	 */
	public boolean equals(Object object) {
		try {
			FSATransition t = (FSATransition) object;
			return super.equals(t) && myLabel.equals(t.myLabel);
		} catch (ClassCastException e) {
			return false;
		}
	}

	/**
	 * Returns the hash code for this transition.
	 * 
	 * @return the hash code for this transition
	 */
	public int hashCode() {
		return super.hashCode() ^ myLabel.hashCode();
	}

	/**
	 * The label for this transition, which is intended to be used as the
	 * precondition that a string must satisfy before the machine continues.
	 */
	protected String myLabel = "";
}
