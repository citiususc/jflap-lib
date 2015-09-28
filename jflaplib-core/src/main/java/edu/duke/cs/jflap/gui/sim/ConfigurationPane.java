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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.JPanel;

import edu.duke.cs.jflap.automata.Automaton;
import edu.duke.cs.jflap.automata.Configuration;

/**
 * The <CODE>ConfigurationPane</CODE> is the pane where the configurations are
 * displayed and selected.
 * 
 * @see edu.duke.cs.jflap.automata.Configuration
 * 
 * @author Thomas Finley
 */

public class ConfigurationPane extends JPanel implements ActionListener {
	/**
	 * Creates a <CODE>ConfigurationPane</CODE>. The instance as created has
	 * no configurations loaded into it yet.
	 * 
	 * @param automaton
	 *            the automaton that configurations will come from
	 */
	public ConfigurationPane(Automaton automaton) {
		this.automaton = automaton;
		// this.setLayout(new FlowLayout(FlowLayout.LEFT));
	}

	/**
	 * Adds a configuration to the configuration pane.
	 * 
	 * @param configuration
	 *            the configuration to add
	 */
	public void add(Configuration configuration) {
		add(configuration, ConfigurationButton.NORMAL);
	}

	/**
	 * Adds configurations with the given state.
	 * 
	 * @param configuration
	 *            the configuration to add q *
	 * @param state
	 *            the state of the configuration, either NORMAL, ACCEPT, REJECT,
	 *            or FREEZE
	 */
	public void add(Configuration configuration, int state) {
		if (contains(configuration))
			return;
		ConfigurationButton button = new ConfigurationButton(configuration,
				state);
		configurationToButtonMap.put(configuration, button);
		add(button);
		button.addActionListener(this);
	}

	/**
	 * Given a configuration, returns the state for that configuration.
	 * 
	 * @param configuration
	 *            the configuration
	 * @return the state for that configuration
	 */
	public int getState(Configuration configuration) {
		return ((ConfigurationButton) configurationToButtonMap
				.get(configuration)).state;
	}

	/**
	 * Determines if this pane already contains this configuration.
	 * 
	 * @param configuration
	 *            the configuration to test for membership
	 * @return <CODE>true</CODE> if the pane holds this transition, <CODE>false</CODE>
	 *         if it does not
	 */
	public boolean contains(Configuration configuration) {
		return configurationToButtonMap.containsKey(configuration);
	}

	/**
	 * Sets a configuration to be a reject configuration.
	 * 
	 * @param configuration
	 *            the configuration to set to be a reject configuration
	 */
	public void setReject(Configuration configuration) {
		ConfigurationButton button = (ConfigurationButton) configurationToButtonMap
				.get(configuration);
		if (button == null)
			return;
		if (button.state == ConfigurationButton.NORMAL)
			button.setState(ConfigurationButton.REJECT);
			button.doClick();
	}

	/**
	 * Sets a configuration to be frozen. Only normal configurations can be
	 * frozen.
	 * 
	 * @param configuration
	 *            the configuration to freeze
	 */
	public void setFrozen(Configuration configuration) {
		ConfigurationButton button = (ConfigurationButton) configurationToButtonMap
				.get(configuration);
		if (button == null)
			return;
		if (button.state == ConfigurationButton.NORMAL)
			button.setState(ConfigurationButton.FREEZE);
			button.doClick();
	}

	public void setFocused(Configuration configuration) {
		ConfigurationButton button = (ConfigurationButton) configurationToButtonMap
				.get(configuration);
		if (button == null)
			return;
		if (button.state == ConfigurationButton.NORMAL) {
			//System.out.println("Setting color");
			button.setState(ConfigurationButton.FOCUSED);
			button.doClick();
		}
	}

	/**
	 * @param configuration
	 */
	public void defocus(Configuration configuration) {
		setNormal(configuration);
		Configuration parent = configuration;
		parent.setFocused(false);
		while (parent.getParent() != null) {
			parent = parent.getParent();
			parent.setFocused(false);
		}
	}

	/**
	 * Sets a configuration to be normal.
	 * 
	 * @param configuration
	 *            the configuration to thaw or unfocus
	 */
	public void setNormal(Configuration configuration) {
		ConfigurationButton button = (ConfigurationButton) configurationToButtonMap
				.get(configuration);
		if (button == null)
			return;
		if (button.state == ConfigurationButton.FREEZE)
			button.setState(ConfigurationButton.NORMAL);
		else if (button.state == ConfigurationButton.FOCUSED)
			button.setState(ConfigurationButton.NORMAL);
			button.doClick();
	}

	/**
	 * Removes a configuration from the configuration pane.
	 * 
	 * @param configuration
	 *            the configuration to remove
	 */
	public void remove(Configuration configuration) {
		Component comp = (Component) configurationToButtonMap
				.remove(configuration);
		if (comp == null)
			return;
		selected.remove(configuration);
		remove(comp);
	}

	/**
	 * Removes all configurations from this pane.
	 */
	public void clear() {
		configurationToButtonMap.clear();
		selected.clear();
		super.removeAll();
	}

	/**
	 * Renders all components deselected.
	 */
	public void deselectAll() {
		selected.clear();
	}

	/**
	 * Returns an array of selected configurations.
	 * 
	 * @return an array of selected configurations
	 */
	public Configuration[] getSelected() {
		return (Configuration[]) selected.toArray(new Configuration[0]);
	}

	/**
	 * Returns an array of all configurations.
	 * 
	 * @return an array of all configurations
	 */
	public Configuration[] getConfigurations() {
		return (Configuration[]) configurationToButtonMap.keySet().toArray(
				new Configuration[0]);
	}

	/**
	 * Returns an array of configurations that are, as far as is known, valid
	 * configurations for moving to other configurations.
	 * 
	 * @return an array of "valid" configurations
	 */
	public Configuration[] getValidConfigurations() {
		// A state is valid for return if it is normal.
		ArrayList list = new ArrayList();
		Iterator it = configurationToButtonMap.values().iterator();
		while (it.hasNext()) {
			ConfigurationButton button = (ConfigurationButton) it.next();
			if (button.state == ConfigurationButton.NORMAL
					|| button.state == ConfigurationButton.FOCUSED)
				list.add(button.getConfiguration());
		}
		return (Configuration[]) list.toArray(new Configuration[0]);
	}

	/**
	 * Clears out all configurations which are "final" configurations, i.e.,
	 * those that are marked either as accept or reject configurations.
	 */
	public void clearFinal() {
		// Avoid concurrent modification exceptions.
		ArrayList list = new ArrayList();
		list.addAll(configurationToButtonMap.values());
		Iterator it = list.iterator();

		while (it.hasNext()) {
			ConfigurationButton button = (ConfigurationButton) it.next();
			if (button.state == ConfigurationButton.ACCEPT
					|| button.state == ConfigurationButton.REJECT)
				remove(button.getConfiguration());
		}
	}

	/**
	 * Clears old all configurations which are not frozen.
	 */
	public void clearThawed() {
		// Avoid concurrent modification exceptions.
		ArrayList list = new ArrayList();
		list.addAll(configurationToButtonMap.values());
		Iterator it = list.iterator();

		while (it.hasNext()) {
			ConfigurationButton button = (ConfigurationButton) it.next();
			if (button.state != ConfigurationButton.FREEZE)
				remove(button.getConfiguration());
		}
	}

	/**
	 * Responds to actions, presumably generated by some button belonging to
	 * this view.
	 * 
	 * @param e
	 *            the action event generated
	 */
	public void actionPerformed(ActionEvent e) {
		ConfigurationButton button = null;
		try {
			button = (ConfigurationButton) e.getSource();
		} catch (ClassCastException ex) {
			return; // Then, we don't care.
		}
		Configuration config = button.getConfiguration();
		if (!configurationToButtonMap.containsKey(config))
			return;
		if (button.isSelected())
			selected.add(config);
		else
			selected.remove(config);
		distributeSelectionEvent(new ConfigurationSelectionEvent(this));
	}

	/**
	 * Adds a <CODE>ConfigurationSelectionListener</CODE> to this object.
	 * 
	 * @param listener
	 *            the listener to add
	 */
	public void addSelectionListener(ConfigurationSelectionListener listener) {
		selectionListeners.add(listener);
	}

	/**
	 * Remove a <CODE>ConfigurationSelectionListener</CODE> from this object.
	 * 
	 * @param listener
	 *            the listener to remove
	 */
	public void removeSelectionListener(ConfigurationSelectionListener listener) {
		selectionListeners.remove(listener);
	}

	/**
	 * Gives a <CODE>ConfigurationSelectionEvent</CODE> to all listeners.
	 * 
	 * @param listener
	 *            the listener to add
	 */
	void distributeSelectionEvent(ConfigurationSelectionEvent event) {
		Iterator it = selectionListeners.iterator();
		while (it.hasNext()) {
			ConfigurationSelectionListener listener = (ConfigurationSelectionListener) it
					.next();
			listener.configurationSelectionChange(event);
		}
	}

	/** The configurations in this pane will be from this automaton. */
	private Automaton automaton;

	/** The map from configurations to their buttons. */
	private HashMap configurationToButtonMap = new HashMap();

	/** The set of selected configurations. */
	private HashSet selected = new HashSet();

	/** The set of listeners to selection events. */
	private transient HashSet selectionListeners = new HashSet();

}
