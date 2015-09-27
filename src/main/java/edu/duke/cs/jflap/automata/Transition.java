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





package edu.duke.cs.jflap.automata;

import java.io.Serializable;
import java.awt.Point;

/**
 * A <CODE>Transition</CODE> object is a simple abstract class representing a
 * transition between two state objects in an automaton. Subclasses of this
 * transition class will have additional fields containing the particulars
 * necessary for their transition.
 * 
 * @see edu.duke.cs.jflap.automata.State
 * @see edu.duke.cs.jflap.automata.Automaton
 * 
 * @author Thomas Finley, Henry Qin
 */

public abstract class Transition implements Serializable, Cloneable {
	/**
	 * Instantiates a new <CODE>Transition</CODE>.
	 * 
	 * @param from
	 *            the state this transition is from
	 * @param to
	 *            the state this transition moves to
	 */
	public Transition(State from, State to) {
		this.from = from;
		this.to = to;
	}

	/**
	 * Returns a copy of this transition, except for a new <CODE>from</CODE>
	 * and <CODE>to</CODE> state.
	 * 
	 * @param from
	 *            the state this transition goes to
	 * @param to
	 *            the state this transition comes from
	 * @return a copy of this transition as described
	 */
	public abstract Transition copy(State from, State to);

	/**
	 * Returns a copy of this transition with the same <CODE>from</CODE> and
	 * <CODE>to</CODE> state.
	 * 
	 * @return a copy of this transition as described
	 */
	public Object clone() {
		Transition res = copy(getFromState(), getToState());
        res.isSelected = this.isSelected;
        res.myControlPoint = this.myControlPoint == null? null : new Point(this.myControlPoint);
        return res;
	}

	/**
	 * Returns the state this transition eminates from.
	 * 
	 * @return the state this transition eminates from
	 */
	public State getFromState() {
		return this.from;
	}

	/**
	 * Returns the state this transition travels to.
	 * 
	 * @return the state this transition travels to
	 */
	public State getToState() {
		return this.to;
	}
	/**
	 * Sets the state the transition starts at.
	 * @param newFrom the state the transition starts at
	 */
	public void setFromState(State newFrom) {
		this.from = newFrom;
	}

	/**
	 * Sets the state the transition goes to.
	 * @param newTo the state the transition goes to
	 */
	public void setToState(State newTo) {
		this.to = newTo;
	}

	/**
	 * Returns the automaton this transition is over.
	 * 
	 * @return the automaton this transition is over
	 */
	public Automaton getAutomaton() {
		return this.from.getAutomaton();
	}

	/**
	 * Gets the description for a Transition. This defaults to nothing.
	 * Subclasses should override.
	 * 
	 * @return an empty string
	 */
	public String getDescription() {
		return "";
	}

	/**
	 * Returns a string representation of this object. The string returned is
	 * the string representation of the first state, and the string
	 * representation of the second state.
	 * 
	 * @return a string representation of this object
	 */
	public String toString() {
		return "[" + getFromState().toString() + "] -> ["
				+ getToState().toString() + "]";
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
			Transition t = (Transition) object;
			return from == t.from && to == t.to;
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
		return from.hashCode() ^ to.hashCode();
	}
	
    /**
      *This hash code is specifically for dealing with clone matching.
    */

	public int specialHash(){
		int t = from == to ? from.specialHash() : from.specialHash() ^ to.specialHash();
        if (myControlPoint != null) t ^= myControlPoint.hashCode();
        return t;
	}

    public Point getControl(){
        return myControlPoint;
    }

    public void setControl(Point p){
        myControlPoint = p; 
    }


	/** The states this transition goes between. */
	protected State from, to;



    /**The control point, if this transition is under manual control*/
    private Point myControlPoint;

    public boolean isSelected = false;
}
