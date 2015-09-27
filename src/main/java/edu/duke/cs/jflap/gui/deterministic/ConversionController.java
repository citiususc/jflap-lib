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





package edu.duke.cs.jflap.gui.deterministic;

import edu.duke.cs.jflap.gui.environment.FrameFactory;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

import edu.duke.cs.jflap.automata.Automaton;
import edu.duke.cs.jflap.automata.State;
import edu.duke.cs.jflap.automata.Transition;
import edu.duke.cs.jflap.automata.event.AutomataStateEvent;
import edu.duke.cs.jflap.automata.event.AutomataStateListener;
import edu.duke.cs.jflap.automata.fsa.FSATransition;
import edu.duke.cs.jflap.automata.fsa.FiniteStateAutomaton;
import edu.duke.cs.jflap.automata.fsa.NFAToDFA;
import edu.duke.cs.jflap.automata.graph.Graph;
import edu.duke.cs.jflap.automata.graph.LayoutAlgorithm;
import edu.duke.cs.jflap.automata.graph.layout.GEMLayoutAlgorithm;
import edu.duke.cs.jflap.debug.EDebug;

/**
 * This is the class that controls the conversion of an NFA to a DFA.
 * 
 * @author Thomas Finley
 */

public class ConversionController {
	/**
	 * Instantiates a <CODE>ConversionController</CODE>.
	 * 
	 * @param nfa
	 *            the NFA we're converting to a DFA
	 * @param dfa
	 *            the DFA being built
	 * @param view
	 *            the view that needs to be repainted
	 */
	public ConversionController(FiniteStateAutomaton nfa,
			FiniteStateAutomaton dfa, ConversionPane view) {
		this.nfa = nfa;
		this.dfa = dfa;
		this.view = view;
		// Set up an initial state in the DFA.
		converter.createInitialState(nfa, dfa).setPoint(new Point(50, 50));
		registerState(dfa.getInitialState());
		answer = converter.convertToDFA(nfa);

		// Create the graph.
		initializeGraph();
	}

	private void initializeGraph() {
		Map stateToSet = new HashMap(); // Different...
		State[] s = answer.getStates();
		Transition[] t = answer.getTransitions();
		for (int i = 0; i < s.length; i++) {
			Set fromNfa = new HashSet(Arrays.asList(getStatesForString(s[i]
					.getLabel(), nfa)));
			stateToSet.put(s[i], fromNfa);
			// setToState.put(s[i], fromNfa);
			graph.addVertex(fromNfa, s[i].getPoint());
		}
		for (int i = 0; i < t.length; i++) {
			graph.addEdge(stateToSet.get(t[i].getFromState()), stateToSet
					.get(t[i].getToState()));
		}
	}

	public void performFirstLayout() {
		view.validate();
		Set isonodes = new HashSet();
		Set initialSet = (Set) stateToSet.get(dfa.getInitialState());
		isonodes.add(initialSet);
		graph.addVertex(initialSet, new Point(0, 0));
		layout.layout(graph, isonodes);
		Rectangle r = view.editor.getBounds(null);
		r.grow(-50, -50);
		graph.moveWithinFrame(r);
		// Set the position of the initial state.
		Point p = new Point();
		p.setLocation(graph.pointForVertex(initialSet));
		dfa.getInitialState().setPoint(p);
	}

	private State[] getStatesForString(String label, Automaton automaton) {
		StringTokenizer tokenizer = new StringTokenizer(label, " \t\n\r\f,q");
		ArrayList states = new ArrayList();
		while (tokenizer.hasMoreTokens())
			states.add(automaton.getStateWithID(Integer.parseInt(tokenizer
					.nextToken())));
		states.remove(null);
		return (State[]) states.toArray(new State[0]);
	}

	/**
	 * Registers that a state has been added to the DFA.
	 * 
	 * @param state
	 *            the state that was added
	 * @throws IllegalArgumentException
	 *             if the state registered conflicts with any existing
	 */
	private void registerState(State state) {
		Set set = new HashSet(Arrays.asList(getStatesForString(
				state.getLabel(), nfa)));
		State inMap = (State) setToState.get(set);
        EDebug.print(set);
        EDebug.print(inMap);
        EDebug.print(state);

//        EDebug.print(setToState.size());
//        EDebug.print(state.getLabel());
        for (Object o: setToState.keySet())  
        	EDebug.print(o.toString());
        
		if (inMap != null && inMap != state)
			throw new IllegalArgumentException("This set is in the DFA!");
		setToState.put(set, state);
		stateToSet.put(state, set);
	}

	/**
	 * Expands all the transitions for a state. This will add all of the
	 * transitions of a state with no user interaction.
	 * 
	 * @param state
	 *            the state to expand
	 */
	public void expandState(State state) {
		List createdStates = converter.expandState(state, nfa, dfa);
		// We want to lay out those states.
		// First, get the sets of states the new states represent.
		Set iso = new HashSet(setToState.keySet()), added = new HashSet();
		Iterator it = createdStates.iterator();
		while (it.hasNext()) {
			State dfaState = (State) it.next();
			registerState(dfaState);
			iso.remove(stateToSet.get(dfaState));
		}
		// Run the layout algorithm.
		layout.layout(graph, iso);
		it = createdStates.iterator();
		while (it.hasNext()) {
			State dfaState = (State) it.next();
			Object o = stateToSet.get(dfaState);
			dfaState.getPoint().setLocation(graph.pointForVertex(o));
			dfaState.setPoint(dfaState.getPoint());
		}
	}

	/**
	 * Asks the user for input regarding a new state.
	 * 
	 * @see edu.duke.cs.jflap.gui.deterministic.TransitionExpanderTool
	 * @param start
	 *            the initial state we're expanding on a terminal
	 * @param point
	 *            the point that the mouse was released on
	 * @param end
	 *            optionally the state the mouse was released on, or <CODE>null</CODE>
	 */
	public void expandState(State start, Point point, State end) {
		// Ask the user for a terminal.
		String terminal = JOptionPane.showInputDialog(view,
				"Expand on what terminal?");
		if (terminal == null)
			return;

		if (terminal.equals("")) {
			JOptionPane.showMessageDialog(view,
					"One can't have lambda in the DFA!", "Improper terminal",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		State[] states = getStatesForString(start.getLabel(), nfa);
		State[] endStates = converter
				.getStatesOnTerminal(terminal, states, nfa);

		if (endStates.length == 0) {
			JOptionPane.showMessageDialog(view, "The group {"
					+ start.getLabel() + "} does not expand on the terminal "
					+ terminal + "!", "Improper expansion",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		// Ask the user for the states it should expand to.
		String userEnd = "";
		if (end == null)
			userEnd = JOptionPane.showInputDialog(view,
					"Which group of NFA states will that go to on " + terminal
							+ "?");
		if (userEnd == null)
			return;
		State[] userEndStates = endStates;
		try {
			if (end == null)
				userEndStates = getStatesForString(userEnd, nfa);
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(view,
					"The list of states is not formatted correctly!",
					"Format error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		// Was the user right?
		if (!converter.containSameStates(userEndStates, endStates)) {
			JOptionPane.showMessageDialog(view,
					"That list of states is incorrect!", "Wrong set",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		State end2 = converter.getStateForStates(userEndStates, dfa, nfa);
		if (end == null)
			end = end2;
		if (end != end2) {
			// Group mismatch.
			JOptionPane.showMessageDialog(view, "The group {"
					+ start.getLabel() + "} does not go to\n" + "group {"
					+ end.getLabel() + "} on terminal " + terminal + "!",
					"Improper transition", JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (end == null) {
			// We need to create the state.
			end = converter.createStateWithStates(dfa, userEndStates, nfa);
			registerState(end);
			end.setPoint(point);
		}

		// Set up the transition.
		Transition t = new FSATransition(start, end, terminal);
		dfa.addTransition(t);
	}

	/**
	 * This method will expand all states in an automaton.
	 */
	public void complete() {
		final LinkedList stateQueue = new LinkedList();
		// Add all states to the state queue.
		stateQueue.addAll(Arrays.asList(dfa.getStates()));
		// When a state is added to the DFA, make sure we know about it.
		AutomataStateListener listener = new AutomataStateListener() {
			public void automataStateChange(AutomataStateEvent e) {
				if (!e.isAdd())
					return;
				// When the DFA gets a new state, add the state to
				// the end of the queue.
				stateQueue.addLast(e.getState());
			}
		};
		dfa.addStateListener(listener);

		while (stateQueue.size() != 0)
			expandState((State) stateQueue.removeFirst());

		// Remove the state listener.
		dfa.removeStateListener(listener);
	}

	/**
	 * This method checks to make sure that the finite state automaton is done.
	 * If it is, then the finished automaton is put in a new window.
	 */
	public void done() {
		int statesRemaining = answer.getStates().length
				- dfa.getStates().length, transitionsRemaining = answer
				.getTransitions().length
				- dfa.getTransitions().length;
		if (statesRemaining + transitionsRemaining != 0) {
			String states = statesRemaining == 0 ? "All the states are there.\n"
					: statesRemaining + " more state"
							+ (statesRemaining == 1 ? "" : "s")
							+ " must be placed.\n";
			String trans = transitionsRemaining == 0 ? "All the transitions are there.\n"
					: transitionsRemaining + " more transition"
							+ (transitionsRemaining == 1 ? "" : "s")
							+ " must be placed.\n";
			String message = "The DFA has not been completed.\n" + states
					+ trans;
			JOptionPane.showMessageDialog(view, message);
			return;
		}
		JOptionPane.showMessageDialog(view, "The DFA is fully built!\n"
				+ "It will now be placed in a new window.");
		FrameFactory.createFrame((FiniteStateAutomaton) dfa.clone());
	}

	/** The NFA we're converting to a DFA. */
	private FiniteStateAutomaton nfa;

	/** The DFA being built. */
	private FiniteStateAutomaton dfa;

	/** The final answer DFA, used for reference. */
	private FiniteStateAutomaton answer;

	/** The view that's being repainted. */
	private ConversionPane view;

	/** The NFA to DFA converter helper object. */
	private NFAToDFA converter = new NFAToDFA();

	/**
	 * The graph object that we use to help lay out those states that are
	 * automatically placed. Here, vertex objects are the sets of states from
	 * the original NFA.
	 */
	private Graph graph = new Graph();

	/**
	 * Whether the user has interacted with the layout of the automaton in such
	 * a way as the automatic layout needs to be redone.
	 */
	private boolean validLayout = false;

	/** The layout algorithm. */
	private LayoutAlgorithm layout = new GEMLayoutAlgorithm();

	/**
	 * Maps a set of NFA states to a DFA state. This structure is maintained in
	 * part through the <CODE>registerState</CODE> method.
	 */
	private Map setToState = new HashMap();

	/** Maps a state to a set of NFA states. */
	private Map stateToSet = new HashMap();
}
