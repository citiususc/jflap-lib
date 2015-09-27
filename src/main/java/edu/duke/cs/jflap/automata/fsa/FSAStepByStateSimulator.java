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
import java.util.Iterator;

import edu.duke.cs.jflap.debug.EDebug;

import edu.duke.cs.jflap.automata.Automaton;
import edu.duke.cs.jflap.automata.AutomatonSimulator;
import edu.duke.cs.jflap.automata.Configuration;
import edu.duke.cs.jflap.automata.State;
import edu.duke.cs.jflap.automata.Transition;

/**
 * The FSA step by state simulator object simulates the behavior of a finite
 * state automaton. It takes an FSA object and an input string and runs the
 * machine on the input. It simulates the machine's behavior by stepping one
 * state at a time, even if there are surrounding lambda transitions that the
 * path could explore without reading more input.
 * 
 * @author Ryan Cavalcante
 */

public class FSAStepByStateSimulator extends AutomatonSimulator {
	/**
	 * Creates an FSA step by state simulator for the given automaton.
	 * 
	 * @param automaton
	 *            the machine to simulate
	 */
	public FSAStepByStateSimulator(Automaton automaton) {
		super(automaton);
	}

	/**
	 * Returns an FSAConfiguration object that represents the initial
	 * configuration of the FSA, before any input has been processed. This
	 * method returns an array of length one, since the closure of the initial
	 * state is not taken.
	 * 
	 * @param input
	 *            the input string.
	 */
	public Configuration[] getInitialConfigurations(String input) {
		Configuration[] configs = new Configuration[1];
		configs[0] = new FSAConfiguration(myAutomaton.getInitialState(), null,
				input, input);
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
			else if (unprocessedInput.startsWith(transLabel)) {
				String input = "";
				if (transLabel.length() < unprocessedInput.length()) {
					input = unprocessedInput.substring(transLabel.length());
				}
				State toState = transition.getToState();
				FSAConfiguration configurationToAdd = new FSAConfiguration(
						toState, configuration, totalInput, input);
				list.add(configurationToAdd);
			}
		}
		return list;
	}

	/**
	 * Returns true if the simulation of the input string on the automaton left
	 * the machine in a final state. If the entire input string is processed and
	 * the machine is in a final state, return true.
	 * 
	 * @return true if the simulation of the input string on the automaton left
	 *         the machine in a final state.
	 */
	public boolean isAccepted() {
		Iterator it = myConfigurations.iterator();
		while (it.hasNext()) {
			FSAConfiguration configuration = (FSAConfiguration) it.next();
			State currentState = configuration.getCurrentState();
			if (configuration.getUnprocessedInput().equals("")
					&& myAutomaton.isFinalState(currentState)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Runs the automaton on the input string.
	 * 
	 * @param input
	 *            the input string to be run on the automaton
	 * @return true if the automaton accepts the input
	 */
	public boolean simulateInput(String input) {
		/** clear the configurations to begin new simulation. */
		myConfigurations.clear();
		Configuration[] initialConfigs = getInitialConfigurations(input);
		for (int k = 0; k < initialConfigs.length; k++) {
			FSAConfiguration initialConfiguration = (FSAConfiguration) initialConfigs[k];
			myConfigurations.add(initialConfiguration);
		}
		while (!myConfigurations.isEmpty()) {
			if (isAccepted())
				return true;
			ArrayList configurationsToAdd = new ArrayList();
			Iterator it = myConfigurations.iterator();
			while (it.hasNext()) {
				FSAConfiguration configuration = (FSAConfiguration) it.next();
				ArrayList configsToAdd = stepConfiguration(configuration);
				configurationsToAdd.addAll(configsToAdd);
				/**
				 * Remove configuration since just stepped from that
				 * configuration to all reachable configurations.
				 */
				it.remove();
			}
			myConfigurations.addAll(configurationsToAdd);
		}
		return false;
	}

}
