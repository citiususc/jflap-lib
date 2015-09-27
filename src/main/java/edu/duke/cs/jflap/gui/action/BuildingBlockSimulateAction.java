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




/*
 * Created on Jun 28, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.duke.cs.jflap.gui.action;

import edu.duke.cs.jflap.gui.environment.Environment;
import edu.duke.cs.jflap.gui.environment.tag.CriticalTag;
import edu.duke.cs.jflap.gui.sim.SimulatorPane;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.Serializable;

import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import edu.duke.cs.jflap.automata.Automaton;
import edu.duke.cs.jflap.automata.AutomatonSimulator;
import edu.duke.cs.jflap.automata.Configuration;
import edu.duke.cs.jflap.automata.turing.TMSimulator;
import edu.duke.cs.jflap.automata.turing.TuringMachine;

/**
 * @author Andrew
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class BuildingBlockSimulateAction extends SimulateAction {
	/**
	 * Instantiates a new <CODE>NoInteractionSimulateAction</CODE>.
	 * 
	 * @param automaton
	 *            the automaton that input will be simulated on
	 * @param environment
	 *            the environment object that we shall add our simulator pane to
	 */
	public BuildingBlockSimulateAction(Automaton automaton,
			Environment environment) {
		super(automaton, environment);
		putValue(NAME, "Step by BuildingBlock");
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_B,
				MAIN_MENU_MASK));
		this.automaton = automaton;
		this.environment = environment;
	}

	/**
	 * Performs the action.
	 */
	public void actionPerformed(ActionEvent e) {
		if (automaton.getInitialState() == null) {
			JOptionPane.showMessageDialog((Component) e.getSource(),
					"Simulation requires an automaton\n"
							+ "with an initial state!", "No Initial State",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		Object input = initialInput((Component) e.getSource(), "");
		if (input == null)
			return;
		Configuration[] configs = null;
		AutomatonSimulator simulator = getSimulator(automaton);
		// Get the initial configurations.
		if (getObject() instanceof TuringMachine) {
			String[] s = (String[]) input;
			configs = ((TMSimulator) simulator).getInitialConfigurations(s);
		} else {
			String s = (String) input;
			configs = simulator.getInitialConfigurations(s);
		}
		handleInteraction(automaton, simulator, configs, input);
	}

	/**
	 * Given initial configurations, the simulator, and the automaton, takes any
	 * further action that may be necessary. In the case of stepwise operation,
	 * which is the default, an additional tab is added to the environment
	 * 
	 * @param automaton
	 *            the automaton input is simulated on
	 * @param simulator
	 *            the automaton simulator for this automaton
	 * @param configurations
	 *            the initial configurations generated
	 * @param initialInput
	 *            the object that represents the initial input; this is a String
	 *            object in most cases, but may differ for multiple tape turing
	 *            machines
	 */
	public void handleInteraction(Automaton automaton,
			AutomatonSimulator simulator, Configuration[] configurations,
			Object initialInput) {
		SimulatorPane simpane = new SimulatorPane(automaton, simulator,
				configurations, environment, true);
		if (initialInput instanceof String[])
			initialInput = java.util.Arrays.asList((String[]) initialInput);
		environment.add(simpane, "Simulate: " + initialInput,
				new CriticalTag() {
				});
		environment.setActive(simpane);
	}

	/**
	 * This particular action may only be applied to finite state automata.
	 * 
	 * @param object
	 *            the object to test for applicability
	 * @return <CODE>true</CODE> if the passed in object is a finite state
	 *         automaton, <CODE>false</CODE> otherwise
	 */
	public static boolean isApplicable(Serializable object) {
		return object instanceof TuringMachine;
	}

	/** The automaton this simulate action runs simulations on! */
	private Automaton automaton;

	/** The environment. */
	private Environment environment = null;
}
