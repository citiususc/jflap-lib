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

import java.util.ArrayList;
import java.util.HashSet;

import edu.duke.cs.jflap.debug.EDebug;

import edu.duke.cs.jflap.automata.Automaton;
import edu.duke.cs.jflap.automata.ClosureTaker;
import edu.duke.cs.jflap.automata.Configuration;
import edu.duke.cs.jflap.automata.State;
import edu.duke.cs.jflap.automata.Transition;

/**
 * The FSA step with closure simulator object simulates the behavior of a finite
 * state automaton. It takes an FSA object and an input string and runs the
 * machine on the input. It simulates the machine's behavior by stepping one
 * state at a time, then taking the closure of each state reached by one step of
 * the machine to find out all possible configurations of the machine at any
 * given point in the simulation.
 * 
 * @author Ryan Cavalcante
 */

public class FSAStepWithClosureSimulator extends FSAStepByStateSimulator {
	/**
	 * Creates an instance of <CODE>StepWithClosureSimulator</CODE>
	 */
	public FSAStepWithClosureSimulator(Automaton automaton) {
		super(automaton);
	}

	/**
	 * Returns an array of FSAConfiguration objects that represent the possible
	 * initial configurations of the FSA, before any input has been processed,
	 * calculated by taking the closure of the initial state.
	 * 
	 * @param input
	 *            the input string.
	 */
	public Configuration[] getInitialConfigurations(String input) {
		State init = myAutomaton.getInitialState();
		State[] closure = ClosureTaker.getClosure(init, myAutomaton);
		Configuration[] configs = new Configuration[closure.length];
		for (int k = 0; k < closure.length; k++) {
			configs[k] = new FSAConfiguration(closure[k], null, input, input);
		}
		return configs;
	}

	/**
	 * Simulates one step for a particular configuration, adding all possible
	 * configurations reachable in one step to set of possible configurations.
	 * 
	 * @param config
	 *            the configuration to simulate the one step on.
	 */
	public ArrayList stepConfiguration(Configuration config) {
		ArrayList list = new ArrayList();
		FSAConfiguration configuration = (FSAConfiguration) config;
		/** get all information from configuration. */
		String unprocessedInput = configuration.getUnprocessedInput();
		String totalInput = configuration.getInput();
		State currentState = configuration.getCurrentState();
		Transition[] transitions = myAutomaton
				.getTransitionsFromState(currentState);
		for (int k = 0; k < transitions.length; k++) {
			FSATransition transition = (FSATransition) transitions[k];
			/** get all information from transition. */
			String transLabel = transition.getLabel();
			HashSet<String> trange = new HashSet<String>();
			if (transLabel.contains("[")){
				for(int i=transLabel.charAt(transLabel.indexOf("[")+1); i<=transLabel.charAt(transLabel.indexOf("[")+3); i++){
					trange.add(Character.toString((char)i));
					EDebug.print(Character.toString((char)i));
				}
				if (transLabel.length() > 0) {
					for(String element : trange){
						if (unprocessedInput.startsWith(element)) {
							String input = "";
							if (element.length() < unprocessedInput.length()) {
								input = unprocessedInput.substring(element.length());
							}
							State toState = transition.getToState();
							FSAConfiguration configurationToAdd = new FSAConfiguration(
								toState, configuration, totalInput, input);
							list.add(configurationToAdd);
						}
					}
				}
			}
			else if (transLabel.length() > 0) {
				if (unprocessedInput.startsWith(transLabel)) {
					String input = "";
					if (transLabel.length() < unprocessedInput.length()) {
						input = unprocessedInput.substring(transLabel.length());
					}
					State toState = transition.getToState();
					State[] closure = ClosureTaker.getClosure(toState,
							myAutomaton);
					for (int i = 0; i < closure.length; i++) {
						FSAConfiguration configurationToAdd = new FSAConfiguration(
								closure[i], configuration, totalInput, input);
						list.add(configurationToAdd);
					}
				}
			}
		}
		return list;
	}

}
