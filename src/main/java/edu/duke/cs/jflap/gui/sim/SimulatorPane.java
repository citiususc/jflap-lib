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





package edu.duke.cs.jflap.gui.sim;

import edu.duke.cs.jflap.gui.SplitPaneFactory;
import edu.duke.cs.jflap.gui.editor.ArrowDisplayOnlyTool;
import edu.duke.cs.jflap.gui.environment.Environment;
import edu.duke.cs.jflap.gui.viewer.AutomatonPane;
import edu.duke.cs.jflap.gui.viewer.SelectionDrawer;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.duke.cs.jflap.automata.Automaton;
import edu.duke.cs.jflap.automata.AutomatonSimulator;
import edu.duke.cs.jflap.automata.Configuration;

/**
 * The <CODE>SimulatorPane</CODE> is the main view for the GUI front end for
 * the simulation of input on automatons. The automaton has two major subviews:
 * a section for the drawing of the automaton, and a section for the list of
 * machine configurations.
 * 
 * @see edu.duke.cs.jflap.automata.Automaton
 * @see edu.duke.cs.jflap.automata.Configuration
 * @see edu.duke.cs.jflap.automata.AutomatonSimulator
 * 
 * @author Thomas Finley
 */

public class SimulatorPane extends JPanel {
	/**
	 * Instantiates a simulator pane for a given automaton, and a given input
	 * string.
	 * 
	 * @param automaton
	 *            the automaton to create the simulator pane for
	 * @param simulator
	 *            the automaton simulator which we step through the automaton on
	 * @param configurations
	 *            the initial configurations that this simulator should start
	 *            with
	 * @param env
	 *            the environment this simulator pane will be added to
	 */
	public SimulatorPane(Automaton automaton, AutomatonSimulator simulator,
			Configuration[] configurations, Environment env, boolean blockStep) {
		this.automaton = automaton;
		this.simulator = simulator;
		initView(configurations, env, blockStep);
	}

	/**
	 * Initiates the views, or in general, sets up the GUI.
	 * 
	 * @param configs
	 *            an array of the initial configuration for this simulator
	 * @param env
	 *            the environment the simulator pane will be added to
	 */
	private void initView(Configuration[] configs, final Environment env,
			boolean blockStep) {
		this.setLayout(new BorderLayout());
		// Set up the main display.
		SelectionDrawer drawer = new SelectionDrawer(automaton);
		AutomatonPane display = new AutomatonPane(drawer, true);
		// Add the listener to the display.
		ArrowDisplayOnlyTool arrow = new ArrowDisplayOnlyTool(display, drawer);
		display.addMouseListener(arrow);

		// Initialize the lower display.
		JPanel lower = new JPanel();
		lower.setLayout(new BorderLayout());

		// Initialize the scroll pane for the configuration view.
		JScrollPane scroller = new JScrollPane(
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		// Set up the configurations pane.
		ConfigurationPane configurations = new ConfigurationPane(automaton);
		configurations.setLayout(new GridLayout(0, 4));
		for (int i = 0; i < configs.length; i++){
			configurations.add(configs[i]);
		}
		// Set up the bloody controller device.
		final ConfigurationController controller = new ConfigurationController(
				configurations, simulator, drawer, display);
		env.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (env.contains(SimulatorPane.this))
					return;
				env.removeChangeListener(this);
				controller.cleanup();
			}
		});
		ControlPanel controlPanel = new ControlPanel(controller);
		controlPanel.setBlock(blockStep);
		// Set up the lower display.
		scroller.getViewport().setView(configurations);
		lower.add(scroller, BorderLayout.CENTER);
		lower.add(controlPanel, BorderLayout.SOUTH);

		// Set up the main view.
		JSplitPane split = SplitPaneFactory.createSplit(env, false, .6,
				display, lower);
		this.add(split, BorderLayout.CENTER);
	}

	public void setAutomaton(Automaton newAuto) {
		automaton = newAuto;
	}

	/** The automaton that this simulator pane is simulating. */
	private Automaton automaton;

	/** The simulator */
	private AutomatonSimulator simulator;

}
