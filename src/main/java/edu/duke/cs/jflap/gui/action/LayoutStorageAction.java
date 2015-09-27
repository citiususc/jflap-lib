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




package edu.duke.cs.jflap.gui.action;

import java.awt.event.ActionEvent;

import edu.duke.cs.jflap.automata.Automaton;
import edu.duke.cs.jflap.automata.graph.AutomatonGraph;

/**
 * Action that allows for the current automaton layout to be saved and possibly restored later.
 * This action itself will save the current layout of the automaton, and an action stored inside
 * this action will restore the automaton's saved layout.
 * 
 * @author Chris Morgan
 */
public class LayoutStorageAction extends AutomatonAction {
	/**
	 * The automaton whose layout will be saved and possibly restored.
	 */
	private Automaton automaton;
	/**
	 * The saved automaton graph, in which the points used to restore the layout are
	 * kept.
	 */
	private AutomatonGraph graph;
	/**
	 * Action that when invoked restores the automaton to the points stored in the
	 * graph.
	 */
	private AutomatonAction restoreAction;
	
	/**
	 * Constructor.
	 * 
	 * @param saveString the title of this action.
	 * @param restoreString the title of the action that restores the saved layout.
	 * @param automaton the automaton whose layout will be saved or restored.
	 */
	public LayoutStorageAction(String saveString, String restoreString, Automaton a) {
		super(saveString, null);
		automaton = a;
		restoreAction = new AutomatonAction(restoreString, null) {
			public void actionPerformed(ActionEvent e) {
				graph.moveAutomatonStates();				
			}
		};
		restoreAction.setEnabled(false);			
	}
	
	/**
	 * Fetches the action used to restore the saved layout.
	 * 
	 * @return the action used to restore the saved layout.
	 */
	public AutomatonAction getRestoreAction() {
		return restoreAction;
	}		
	
	public void actionPerformed(ActionEvent e) {
		graph = new AutomatonGraph(automaton);
		restoreAction.setEnabled(true);
	}
}
