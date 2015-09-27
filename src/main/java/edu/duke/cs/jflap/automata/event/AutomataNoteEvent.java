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
import edu.duke.cs.jflap.automata.Note;

/**
 * This event is given to listeners of an automaton interested in events when a
 * transition on an automaton is added, removed, or changed.
 * 
 * @see edu.duke.cs.jflap.automata.Automaton
 * @see edu.duke.cs.jflap.automata.Transition
 * @see edu.duke.cs.jflap.automata.Automaton#addTransition
 * @see edu.duke.cs.jflap.automata.Automaton#removeTransition
 * @see edu.duke.cs.jflap.automata.event.AutomataTransitionListener
 * 
 * @author Thomas Finley
 */

public class AutomataNoteEvent extends EventObject {
	/**
	 * Instantiates a new <CODE>AutomataStateEvent</CODE>.
	 * 
	 * @param auto
	 *            the <CODE>Automaton</CODE> that generated the event
	 * @param note
	 *            the <CODE>Note</CODE> that was added or removed, or changed
	 * @param add
	 *            <CODE>true</CODE> if the note is added, <CODE>false</CODE>
	 *            if removed
	 * @param change
	 *            <CODE>true</CODE> if some property of the note was
	 *            changed, <CODE>false</CODE> if this is not simply added or removed
	 */
	public AutomataNoteEvent(Automaton auto, Note note,
			boolean add, boolean change) {
		super(auto);
		myNote = note;
		myAdd = add;
		myChange = change;
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
	 * Returns the <CODE>Transition</CODE> that was added/removed.
	 * 
	 * @return the <CODE>Transition</CODE> that was added/removed
	 */
	public Note getNote() {
		return myNote;
	}

	/**
	 * Returns if this was an add.
	 * 
	 * @return <CODE>true</CODE> if this event indicates the addition of a
	 *         transition, <CODE>false</CODE> otherwise
	 */
	public boolean isAdd() {
		return myAdd;
	}

	/**
	 * Returns if this was a delete.
	 * 
	 * @return <CODE>true</CODE> if this event indicates the removal of a
	 *         transition, <CODE>false</CODE> otherwise
	 */
	public boolean isDelete() {
		return !(myAdd || myChange);
	}

	/**
	 * Returns if this was a simple change in a property of the transition.
	 * 
	 * @return <CODE>true</CODE> if the properties of this transition were
	 *         changed, <CODE>false</CODE> otherwise
	 */
	public boolean isChange() {
		return myChange;
	}

	/** Was this an add? */
	private boolean myAdd;

	/** Which transition did we add/remove? */
	private Note myNote;

	/** Is this a change in property? */
	private boolean myChange;
}
