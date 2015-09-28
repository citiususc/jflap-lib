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





package edu.duke.cs.jflap.gui.viewer;

import edu.duke.cs.jflap.automata.event.AutomataStateEvent;
import edu.duke.cs.jflap.automata.Automaton;
import edu.duke.cs.jflap.automata.State;
import edu.duke.cs.jflap.automata.Transition;

import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * An extension of the <CODE>AutomatonDrawer</CODE> that allows the selection
 * (i.e. highlighting) of states.
 * 
 * @author Thomas Finley
 */

public class SelectionDrawer extends AutomatonDrawer {
	/**
	 * Instantiates a new selection drawer with no states selected.
	 * 
	 * @param automaton
	 *            the automaton to select
	 */
	public SelectionDrawer(Automaton automaton) {
		super(automaton);
	}

	/**
	 * Listens for changes in the states of the automaton. In the event that one
	 * has it checks the selected states.
	 * 
	 * @param event
	 *            the state event
	 */
	protected void stateChange(AutomataStateEvent event) {
		if (event.isDelete())
			selected.remove(event.getState());
		super.stateChange(event);
	}

	/**
	 * If a state is selected, draw it somewhat darker than the others. If it is
	 * not, then simply use the regular means for drawing a state.
	 * 
	 * @param g
	 *            the graphics object to draw on
	 * @param state
	 *            the state to draw
	 */
	public void drawState(Graphics g, State state) {
		if (selected.contains(state)) {
			getStateDrawer().drawState(g, getAutomaton(), state,
					state.getPoint(), SELECTED_COLOR);
			if (doesDrawStateLabels())
				getStateDrawer().drawStateLabel(g, state, state.getPoint(),
						StateDrawer.STATE_COLOR);
		} else
			super.drawState(g, state);
	}

	/**
	 * Draws the transitions normally, then draws the highlight for the selected
	 * transitions.
	 * 
	 * @param g
	 *            the graphics object to draw upon
	 */
	protected void drawTransitions(Graphics g) {
		java.awt.Graphics2D g2 = (java.awt.Graphics2D) g;
		super.drawTransitions(g);
		Iterator it = selectedTransitions.iterator();
		while (it.hasNext()) {
			Transition t = (Transition) it.next();
			try {
				arrowForTransition(t).drawHighlight(g2);
			} catch (NullPointerException e) {
				// Then this transition isn't in here.
			}
		}
	}

	/**
	 * Adds a state to the selected states.
	 * 
	 * @param state
	 *            the state to add
	 */
	public void addSelected(State state) {
		// Automaton replacer = null;
		// if(state.getParentBlock()!=null){
		// replacer =
		// (Automaton)state.getAutomaton().getBlockMap().get(state.getParentBlock().getInternalName());
		// }
		// if (state.getAutomaton() != getAutomaton() && replacer !=
		// getAutomaton())
		// throw new IllegalArgumentException
		// ("State to select not in correct automaton!");
		if (!selected.contains(state)) {
			selected.add(state);
			distributeChangeEvent();
		}
	}

	/**
	 * Removes the state from the selected states.
	 * 
	 * @param state
	 *            the state to remove
	 */
	public void removeSelected(State state) {
		if (selected.contains(state)) {
			selected.remove(state);
			distributeChangeEvent();
		}
	}

	/**
	 * Returns the number of selected states.
	 * 
	 * @return the number of selected states
	 */
	public int numberSelected() {
		return selected.size();
	}

	/**
	 * Returns an array of the selected states.
	 * 
	 * @return an array of the selected states
	 */
	public State[] getSelected() {
		return (State[]) selected.toArray(new State[0]);
	}

	/**
	 * Determines if a particular state is selected.
	 * 
	 * @param state
	 *            the state to check for "selectedness"
	 * @return <CODE>true</CODE> if the state is selected, <CODE>false</CODE>
	 *         if it is not
	 */
	public boolean isSelected(State state) {
		return selected.contains(state);
	}

	/**
	 * Clears all selected states, so that there are no selected states.
	 */
	public void clearSelected() {
		if (selected.size() + selectedTransitions.size() > 0) {
			selected.clear();
			selectedTransitions.clear();
			distributeChangeEvent();
		}
	}

	/**
	 * Retrieves the set of selected states.
	 * 
	 * @return the set of selected states
	 */
	protected Set selected() {
		return selected;
	}

	/**
	 * Returns the set of selected transitions.
	 * 
	 * @return the set of selected transitions
	 */
	protected Set selectedTransitions() {
		return selectedTransitions;
	}

	/**
	 * Adds a transition to the selected transitions.
	 * 
	 * @param transition
	 *            the transition to add
	 */
	public void addSelected(Transition transition) {
		if (transition.getFromState().getAutomaton() != getAutomaton())
			throw new IllegalArgumentException(
					"Transition to select not in correct automaton!");
		if (!selectedTransitions.contains(transition)) {
			selectedTransitions.add(transition);
			distributeChangeEvent();
		}
	}

	/**
	 * Removes the transition from the selected transitions.
	 * 
	 * @param transition
	 *            the transition to set as unselected
	 */
	public void removeSelected(Transition transition) {
		if (selectedTransitions.contains(transition)) {
			selectedTransitions.remove(transition);
			distributeChangeEvent();
		}
	}

	/**
	 * Returns the number of selected transitions.
	 * 
	 * @return the number of selected transitions
	 */
	public int numberSelectedTransitions() {
		return selectedTransitions.size();
	}

	/**
	 * Returns an array of the selected transitions.
	 * 
	 * @return an array of the selected transitions
	 */
	public Transition[] getSelectedTransitions() {
		return (Transition[]) selectedTransitions.toArray(new Transition[0]);
	}

	/**
	 * Determines if a particular transition is selected.
	 * 
	 * @param transition
	 *            the transition to check for "selectedness"
	 * @return <CODE>true</CODE> if the transition is selected, <CODE>false</CODE>
	 *         if it is not
	 */
	public boolean isSelected(Transition transition) {
		return selectedTransitions.contains(transition);
	}

	/**
	 * Clears all selected transitions, so that there are no selected
	 * transitions.
	 */
	public void clearSelectedTransitions() {
		selectedTransitions.clear();
	}

	/**
	 * Adds a change listener to this object that listens to changes in what is
	 * selected in this selection drawer.
	 * 
	 * @param listener
	 *            the listener to add
	 */
	public void addChangeListener(ChangeListener listener) {
		listeners.add(listener);
	}

	/**
	 * Removes a change listener from this object.
	 * 
	 * @param listener
	 *            the listener to remove
	 */
	public void removeChangeListener(ChangeListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Distributes a <CODE>ChangeEvent</CODE> to all listeners when the
	 * selection has changed.
	 */
	protected void distributeChangeEvent() {
		ChangeEvent e = new ChangeEvent(this);
		Iterator it = listeners.iterator();
		while (it.hasNext())
			((ChangeListener) it.next()).stateChanged(e);
	}

	/** The set of selected states, and the set of selected transitions. */
	private Set selected = new HashSet(), selectedTransitions = new HashSet();

	/** The color to draw selected states in. */
	protected static final Color SELECTED_COLOR = StateDrawer.STATE_COLOR
			.darker().darker();

	/** This set of listeners. */
	private Set listeners = new HashSet();
}
