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

import edu.duke.cs.jflap.gui.LambdaCellRenderer;
import edu.duke.cs.jflap.gui.viewer.AutomatonPane;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import edu.duke.cs.jflap.automata.State;
import edu.duke.cs.jflap.automata.Transition;
import edu.duke.cs.jflap.automata.turing.TMTransition;
import edu.duke.cs.jflap.automata.turing.Tape;

/**
 * This allows the user to create transition creators that have tables directly
 * in the editing window with a minimum of effort.
 * 
 * @author Thomas Finley
 */

public abstract class TableTransitionCreator extends TransitionCreator {
	/**
	 * Instantiates a transition creator.
	 * 
	 * @param parent
	 *            the automaton pane the automaton is drawn in
	 */
	public TableTransitionCreator(AutomatonPane parent) {
		super(parent);
		parent.addMouseListener(viewListener);
	}

	/**
	 * Creates a fresh new transition between two states.
	 * 
	 * @param from
	 *            the from state for the new transition
	 * @param to
	 *            the to state for the new transition
	 */
	protected abstract Transition initTransition(State from, State to);

	/**
	 * Instantiates a new table model based on the given transition.
	 * 
	 * @param transition
	 *            the transition to create a table model for
	 */
	protected abstract TableModel createModel(Transition transition);

	/**
	 * Instantiates a new table view based on the given transition. This method
	 * uses the {@link #createModel} method. This default method simply sticks
	 * the model in a table, and sets some visual constants.
	 * 
	 * @param transition
	 *            the transition to create a table for
	 */
	protected JTable createTable(final Transition transition) {
		TableModel model = createModel(transition);
		final TipLambdaCellRenderer[] renders = new TipLambdaCellRenderer[model
				.getColumnCount()];
		for (int i = 0; i < model.getColumnCount(); i++)
			renders[i] = transition instanceof TMTransition ? new TipLambdaCellRenderer(
					"" + Tape.BLANK, model.getColumnName(i))
					: new TipLambdaCellRenderer(model.getColumnName(i));
		JTable table = new JTable(createModel(transition)) {
			public TableCellRenderer getCellRenderer(int r, int c) {
				return renders[c];
			}

			protected boolean processKeyBinding(KeyStroke ks, KeyEvent e,
					int condition, boolean pressed) {
				if (ks.getKeyCode() == KeyEvent.VK_ENTER
						&& !ks.isOnKeyRelease()) {
					stopEditing(false);
					if (e.isShiftDown()) {
						createTransition(transition.getFromState(), transition
								.getToState());
					}
					return true;
				} else if (ks.getKeyCode() == KeyEvent.VK_ESCAPE) {
					stopEditing(true);
					return true;
				}
				return super.processKeyBinding(ks, e, condition, pressed);
			}
		};

		table.setGridColor(Color.gray);
		table.setBorder(new javax.swing.border.EtchedBorder());
		return table;
	}

	/**
	 * Given a table model instantiated earlier and a transition, return a
	 * transition with the changes in the model.
	 * 
	 * @param transition
	 *            the transition to modify
	 * @param model
	 *            the table model
	 * @return the new transition, or <code>null</code> if changing failed
	 */
	protected abstract Transition modifyTransition(Transition transition,
			TableModel model);

	/**
	 * Stops the editing.
	 * 
	 * @param cancel
	 *            if this was a cancel
	 */
	private void stopEditing(boolean cancel) {
		if (editingTable == null)
			return; // Nothing to do.
		try {
			editingTable.getCellEditor().stopCellEditing();
		} catch (NullPointerException e) {

		} catch (IllegalArgumentException e) {
			System.err.println("Odd 'focusCycleRoot' exception thrown "
					+ "from the depths of Java again.");
		}
		if (!cancel) {
			TableModel oldModel = createModel(transition);
			Transition t = modifyTransition(transition, editingTable.getModel());
			if (t != null) {
				if (isNew) {
					getParent().getDrawer().getAutomaton().addTransition(t);
				} else{
					getParent().getDrawer().getAutomaton().replaceTransition(
							transition, t);
				}
			}
		}
		if (this instanceof TMTransitionCreator) {
			TMTransitionCreator stop = (TMTransitionCreator) this;
			stop.setBlockTransition(false);
		}
		getParent().remove(editingTable);
		getParent().validate();
		getParent().repaint();
		editingTable = null;
		getParent().requestFocus();
	}

	public boolean editTransition(Transition t) {
		return false;
	}

	/**
	 * Begins the process of creating a transition.
	 * 
	 * @param from
	 *            the from state
	 * @param to
	 *            the to state
	 */
	public Transition createTransition(State from, State to) {
		Transition t = initTransition(from, to);
		editTransition(t, null);
		
		//can you say "ugly hack?"
//		editTransition(t, new Point((from.getPoint().x+to.getPoint().x)/2, (from.getPoint().y+to.getPoint().y)/2));
		
		return null;
	}

	/**
	 * Edits a transition. This implementation will create an editing view from
	 * the {@link #createModel} and {@link #createTable} methods.
	 * 
	 * @param transition
	 *            the transition to edit
	 * @param point
	 *            the point to edit the transition at; should be null if this is
	 *            a new transition yet to be added
	 */
	public void editTransition(Transition transition, Point point) {
		stopEditing(false); // Make sure...
		this.transition = transition;
		isNew = point == null;
		if (isNew) {
			State from = transition.getFromState(), to = transition
					.getToState();
			point = new Point((from.getPoint().x + to.getPoint().x) / 2, (from
					.getPoint().y + to.getPoint().y) / 2);
		}

		getParent().setTablePoint(tablePoint);
		editingTable = createTable(transition);
		getParent().add(editingTable);
		getParent().validate();
		tableDimensions = editingTable.getSize();

		tablePoint = getParent().transformFromAutomatonToView(point);
		tablePoint.translate(-tableDimensions.width / 2,
				-tableDimensions.height / 2);
		getParent().setTablePoint(tablePoint);

		editingTable.addComponentListener(new ComponentListener() {
			public void componentHidden(ComponentEvent e) {
			}

			public void componentMoved(ComponentEvent e) {
				e.getComponent().setLocation(tablePoint);
			}

			public void componentResized(ComponentEvent e) {
				e.getComponent().setSize(tableDimensions);
			}

			public void componentShown(ComponentEvent e) {
			}
		});
		
		
		editingTable.setLocation(tablePoint);
		editingTable.setSize(tableDimensions);
		
//		editingTable.editCellAt(0, 0);
		
		editingTable.setCellSelectionEnabled(true);
		editingTable.changeSelection(0, 0, false, false);
		editingTable.requestFocus();
		
//		EDebug.print("Focus has been requested");
//	    EDebug.print("hasFocus? "+editingTable.hasFocus());	
//	    EDebug.print(getParent().getClass().getName());
		
		getParent().repaint();
	}

	protected void editTransition(Transition transition, Point point,
			int tableSize) {

	}

	/**
	 * The current table view. This will be null if no transition is currently
	 * being edited.
	 */
	protected JTable editingTable = null;

	/** The table's dimensions. */
	private Dimension tableDimensions;

	/** The location of the table. */
	private Point tablePoint = new Point();

	/** The mouse listener for stopping editing. */
	private MouseListener viewListener = new MouseAdapter() {
		public void mousePressed(MouseEvent e) {
			stopEditing(false);
		}
	};

	/** If this is a new transition being edited. */
	private boolean isNew;

	/** The transition being edited. */
	private Transition transition;

	/** The cell renderer. */
	private static class TipLambdaCellRenderer extends LambdaCellRenderer {
		public TipLambdaCellRenderer(String replace, String tip) {
			super(replace);
			setToolTipText(tip);
		}

		public TipLambdaCellRenderer(String tip) {
			setToolTipText(tip);
		}
	}
}
