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





package edu.duke.cs.jflap.gui.editor;

import edu.duke.cs.jflap.gui.viewer.AutomatonPane;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import edu.duke.cs.jflap.automata.State;
import edu.duke.cs.jflap.automata.Transition;
import edu.duke.cs.jflap.automata.fsa.FSATransition;

/**
 * This is a transition creator for finite state automata.
 * 
 * @author Thomas Finley
 */

public class FSATransitionCreator extends TableTransitionCreator {
	/**
	 * Instantiates a transition creator.
	 * 
	 * @param parent
	 *            the parent object that any dialogs or windows brought up by
	 *            this creator should be the child of
	 */
	public FSATransitionCreator(AutomatonPane parent) {
		super(parent);
	}

	/**
	 * Initializes a new empty transition.
	 * 
	 * @param from
	 *            the from state
	 * @param to
	 *            to too state
	 */
	protected Transition initTransition(State from, State to) {
		return new FSATransition(from, to, "");
	}

	/**
	 * Creates a new table model.
	 * 
	 * @param transition
	 *            the transition to create the model for
	 */
	protected TableModel createModel(Transition transition) {
		final FSATransition t = (FSATransition) transition;
		return new AbstractTableModel() {
			public Object getValueAt(int row, int column) {
				return s;
			}

			public void setValueAt(Object o, int r, int c) {
				s = (String) o;
			}

			public boolean isCellEditable(int r, int c) {
				return true;
			}

			public int getRowCount() {
				return 1;
			}

			public int getColumnCount() {
				return 1;
			}

			public String getColumnName(int c) {
				return "Label";
			}

			String s = t.getLabel();
		};
	}

	/**
	 * Modifies a transition according to what's in the table.
	 */
	public Transition modifyTransition(Transition t, TableModel model) {
		//EDebug.print("ModifyTransitionCalled");
		String s = (String) model.getValueAt(0, 0);
		try {
			return new FSATransition(t.getFromState(), t.getToState(), s);
		} catch (IllegalArgumentException e) {
			reportException(e);
			return null;
		}
	}
}
