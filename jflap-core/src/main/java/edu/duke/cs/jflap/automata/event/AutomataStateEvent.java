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





package edu.duke.cs.jflap.automata.event;

import java.util.EventObject;
import edu.duke.cs.jflap.automata.Automaton;
import edu.duke.cs.jflap.automata.State;

/**
 * This event is given to listeners of an automaton interested in events when a
 * state on an automaton is added or removed, or moved, or it's label was
 * changed, etc.
 * 
 * @see edu.duke.cs.jflap.automata.Automaton
 * @see edu.duke.cs.jflap.automata.State
 * @see edu.duke.cs.jflap.automata.Automaton#addState
 * @see edu.duke.cs.jflap.automata.Automaton#removeState
 * @see edu.duke.cs.jflap.automata.event.AutomataStateListener
 * 
 * @author Thomas Finley
 */

public class AutomataStateEvent extends EventObject {
	/**
	 * Instantiates a new <CODE>AutomataStateEvent</CODE>.
	 * 
	 * @param auto
	 *            the <CODE>Automaton</CODE> that generated the event
	 * @param region
	 *            the <CODE>State</CODE> that was added or removed
	 * @param add
	 *            <CODE>true</CODE> if state added
	 * @param move
	 *            <CODE>true</CODE> if the state was merely moved
	 * @param label
	 *            <CODE>true</CODE> if the state was only changed in such a
	 *            fashion as
	 */
	public AutomataStateEvent(Automaton auto, State state, boolean add,
			boolean move, boolean label) {
		super(auto);
		myState = state;
		myAdd = add;
		myMove = move;
		myLabel = label;
	}

	/**
	 * Returns the <CODE>Automaton</CODE> that generated this event.
	 * 
	 * @return the <CODE>Automaton</CODE> that generated this event
	 */
	public Automaton getAutomaton() {
		return (Automaton) getSource();
	}

	/**
	 * Returns the <CODE>State</CODE> that was added/removed.
	 * 
	 * @return the <CODE>State</CODE> that was added/removed
	 */
	public State getState() {
		return myState;
	}

	/**
	 * Returns if this was an add.
	 * 
	 * @return <CODE>true</CODE> if this event indicates the addition of a
	 *         state, <CODE>false</CODE> otherwise
	 */
	public boolean isAdd() {
		return myAdd;
	}

	/**
	 * Returns if this was a move.
	 * 
	 * @return <CODE>true</CODE> if this event indicates the mere moving of a
	 *         state, <CODE>false</CODE> otherwise
	 */
	public boolean isMove() {
		return myMove;
	}

	/**
	 * Returns if this was a label change.
	 * 
	 * @return <CODE>true</CODE> if this event indicates the mere relabeling
	 *         of a state, <CODE>false</CODE> otherwise
	 */
	public boolean isLabel() {
		return myLabel;
	}

	/**
	 * Returns if this was a delete of a state.
	 * 
	 * @return <CODE>true</CODE> if this event was the deletion of a state,
	 *         false otherwise
	 */
	public boolean isDelete() {
		return !(myMove || myAdd || myLabel);
	}

	/** Was this an add? */
	private boolean myAdd;

	/** Was this a move? */
	private boolean myMove;

	/** Was the label for the state changed? */
	public boolean myLabel;

	/** Which state did we add/remove? */
	private State myState;
}
