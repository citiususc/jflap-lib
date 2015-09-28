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

import edu.duke.cs.jflap.automata.Automaton;
import edu.duke.cs.jflap.automata.State;
import edu.duke.cs.jflap.automata.Transition;

/**
 * The FSA label handler is an object that can convert a finite state automaton
 * with transition labels of more than one character in length into an
 * equivalent finite state automaton with all transition labels exactly one
 * character in length.
 * 
 * @author Ryan Cavalcante
 */

public class FSALabelHandler {
	/**
	 * Creates an instance of <CODE>FSALabelHandler</CODE>.
	 */
	private FSALabelHandler() {

	}

	/**
	 * Returns true if <CODE>automaton</CODE> has labels with multiple
	 * characters, instead of single character labels.
	 * 
	 * @param automaton
	 *            the automaton.
	 * @return true if <CODE>automaton</CODE> has labels with multiple
	 *         characters, instead of single character labels.
	 */
	public static boolean hasMultipleCharacterLabels(Automaton automaton) {
		Transition[] transitions = automaton.getTransitions();
		for (int k = 0; k < transitions.length; k++) {
			FSATransition transition = (FSATransition) transitions[k];
			String label = transition.getLabel();
			if (label.length() > 1)
				return true;
		}
		return false;
	}

	/**
	 * Changes <CODE>transition</CODE> in <CODE>automaton</CODE> to several
	 * transitions each with labels of one character in length. This algorithm
	 * introduces new states in <CODE>automaton</CODE>.
	 * 
	 * @param transition
	 *            the transition to break up into several transitions of one
	 *            character (in length) a piece.
	 * @param automaton
	 *            the automaton that has the transition.
	 */
	public static void handleLabel(Transition transition, Automaton automaton) {
		/*
		 * FSATransition trans = (FSATransition) transition; String label =
		 * trans.getLabel(); String firstChar = label.substring(0,1); String
		 * restOfLabel = label.substring(1);
		 * 
		 * StatePlacer sp = new StatePlacer();
		 * 
		 * State newState =
		 * automaton.createState(sp.getPointForState(automaton)); Transition
		 * newTrans1 = new FSATransition(trans.getFromState(), newState,
		 * firstChar); Transition newTrans2 = new FSATransition(newState,
		 * trans.getToState(), restOfLabel); automaton.addTransition(newTrans1);
		 * automaton.addTransition(newTrans2);
		 * automaton.removeTransition(transition); if(restOfLabel.length() > 1)
		 * handleLabel(newTrans2, automaton);
		 */

		FSATransition trans = (FSATransition) transition;
		State from = transition.getFromState(), f = from, to = transition
				.getToState();
		automaton.removeTransition(trans);
		String label = trans.getLabel();
		int length = label.length();
		for (int i = 0; i < length; i++) {
			State going = i == length - 1 ? to : automaton
					.createState(new java.awt.Point((f.getPoint().x
							* (length - i - 1) + to.getPoint().x * (i + 1))
							/ length, (f.getPoint().y * (length - i - 1) + to
							.getPoint().y
							* (i + 1))
							/ length));
			Transition newTrans = new FSATransition(from, going, label
					.substring(i, i + 1));
			automaton.addTransition(newTrans);
			from = going;
		}
	}
	
	public static void splitLabel(Transition transition, Automaton automaton){
		FSATransition trans = (FSATransition) transition;
		State from = transition.getFromState(), f = from, to = transition
		.getToState();
		automaton.removeTransition(trans);
		String label = trans.getLabel();
		for(int i=label.charAt(label.indexOf("[")+1); i<=label.charAt(label.indexOf("[")+3); i++){
			Transition newTrans = new FSATransition(from, to, Character.toString((char)i));
			automaton.addTransition(newTrans);
		}
	}
		

	/**
	 * Changes all transitions in <CODE>automaton</CODE> into transitions with
	 * at most one character per label. This could introduce more states into
	 * <CODE>automaton</CODE>.
	 * 
	 * @param automaton
	 *            the automaton.
	 */
	public static FiniteStateAutomaton removeMultipleCharacterLabels(
			Automaton automaton) {
		FiniteStateAutomaton fsa = (FiniteStateAutomaton) automaton.clone();
		Transition[] transitions = fsa.getTransitions();
		for (int k = 0; k < transitions.length; k++) {
			FSATransition transition = (FSATransition) transitions[k];
			String label = transition.getLabel();
			if (label.length() > 1) {
				handleLabel(transition, fsa);
			}
		}
		return fsa;
	}

	/**
	 * Changes all transitions in <CODE>automaton</CODE> into transitions with
	 * at most one character per label. This could introduce more states into
	 * <CODE>automaton</CODE>.
	 * 
	 * @param automaton
	 *            the automaton.
	 */
	public static void removeMultipleCharacterLabelsFromAutomaton(Automaton automaton) {
		Transition[] transitions = automaton.getTransitions();
		for (int k = 0; k < transitions.length; k++) {
			FSATransition transition = (FSATransition) transitions[k];
			String label = transition.getLabel();
			if (label.length() > 1) {
				handleLabel(transition, automaton);
			}
		}
	}
}
