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





package edu.duke.cs.jflap.gui.grammar.transform;

import edu.duke.cs.jflap.automata.*;
import edu.duke.cs.jflap.automata.vdg.*;
import edu.duke.cs.jflap.grammar.*;
import edu.duke.cs.jflap.gui.grammar.GrammarTableModel;
import java.awt.event.MouseEvent;
import java.util.*;
import javax.swing.*;
import edu.duke.cs.jflap.automata.event.*;

/**
 * This is the controller for the unit panel. Bleh.
 * 
 * @author Thomas Finley
 */

public class UnitController {
	/**
	 * This instantiates a new unit controller.
	 * 
	 * @param pane
	 *            the unit panel
	 * @param grammar
	 *            the grammar to produce
	 */
	public UnitController(UnitPane pane, Grammar grammar) {
		this.pane = pane;
		this.grammar = grammar;
		nextStep();
	}

	/**
	 * This is called to move the lambda controller to the next step.
	 */
	private void nextStep() {
		if (step != FINISHED)
			step++;
		switch (step) {
		case VARAIBLE_GRAPH: {
			pane.mainLabel.setText("Complete unit production visualization.");
			pane.detailLabel
					.setText("For every unit production, connect start and end.");
			// Make the VDG.
			vdg = new VariableDependencyGraph();
			remover.initializeDependencyGraph(vdg, grammar);
			// Cache the transitions we have to add.
			Production[] p = grammar.getProductions();
			for (int i = 0; i < p.length; i++)
				if (ProductionChecker.isUnitProduction(p[i]))
					vdgTransitions.add(remover.getTransitionForUnitProduction(
							p[i], vdg));
			// Set up the listener so we know when new actions get
			// added to the VDG.
			vdg.addTransitionListener(new AutomataTransitionListener() {
				public void automataTransitionChange(AutomataTransitionEvent e) {
					if (!e.isAdd())
						return;
					if (vdgTransitions.contains(e.getTransition())) {
						vdgTransitions.remove(e.getTransition());
						updateDisplay();
						return;
					}
					JOptionPane.showMessageDialog(pane,
							"Transition is not part of VDG.", "Bad Transition",
							JOptionPane.ERROR_MESSAGE);
					vdg.removeTransition(e.getTransition());
				}
			});
			// Set the actions.
			pane.deleteAction.setEnabled(false);
			pane.completeSelectedAction.setEnabled(false);
			pane.doStepAction.setEnabled(true);
			pane.doAllAction.setEnabled(true);
			pane.proceedAction.setEnabled(false);
			pane.exportAction.setEnabled(false);
			updateDisplay();
			break;
		}
		case PRODUCTION_MODIFY: {
			pane.updateDeleteEnabledness();
			pane.updateCompleteSelectedEnabledness();
			pane.mainLabel
					.setText("Modify the grammar to remove unit productions.");
			Production[] p = grammar.getProductions();
			for (int i = 0; i < p.length; i++) {
				pane.editingGrammarModel.addProduction(p[i]);
				currentProductions.add(p[i]);
				if (ProductionChecker.isUnitProduction(p[i])) {
					unitProductions.add(p[i]);
					continue;
				}
			}
			// Get the desired productions.
			Grammar desiredGrammar = remover.getUnitProductionlessGrammar(
					grammar, vdg);
			p = desiredGrammar.getProductions();
			for (int i = 0; i < p.length; i++)
				desiredProductions.add(p[i]);
			updateDisplay();
			pane.editingActive = true;
			break;
		}
		case FINISHED: {
			pane.editingActive = false;
			pane.deleteAction.setEnabled(false);
			pane.completeSelectedAction.setEnabled(false);
			pane.mainLabel.setText("Unit removal complete.");
			pane.detailLabel.setText("\"Proceed\" or \"Export\" available.");

			pane.doStepAction.setEnabled(false);
			pane.doAllAction.setEnabled(false);
			pane.proceedAction.setEnabled(true);
			pane.exportAction.setEnabled(true);
			break;
		}
		}
	}

	/**
	 * Does the expansion of the production for those states selected.
	 */
	void doSelected() {
		pane.editingActive = false;

		// Which of the selected rows are selected unit productions?
		int[] selectedRows = pane.editingGrammarView.getSelectedRows();
		GrammarTableModel model = pane.editingGrammarModel;
		Set selectedUnitProductions = new HashSet();
		for (int i = selectedRows.length - 1; i >= 0; i--) {
			Production p = model.getProduction(selectedRows[i]);
			if (!ProductionChecker.isUnitProduction(p))
				continue;
			selectedUnitProductions.add(p);
			pane.editingGrammarModel.deleteRow(selectedRows[i]);
			unitProductions.remove(p);
			currentProductions.remove(p);
		}

		// Determine what productions need to be added as a result of
		// each selected unit production.
		Set toAdd = new HashSet();
		for (Iterator it = selectedUnitProductions.iterator(); it.hasNext();) {
			// This will do each selected unit production.
			Production unit = (Production) it.next();
			for (Iterator dit = desiredProductions.iterator(); dit.hasNext();) {
				// Determine if this desired production is good to go.
				Production p = (Production) dit.next();
				if (p.getLHS().equals(unit.getRHS()))
					toAdd.add(new Production(unit.getLHS(), p.getRHS()));
			}
		}

		// Add those productions!
		for (Iterator it = toAdd.iterator(); it.hasNext();) {
			Production p = (Production) it.next();
			if (!currentProductions.add(p))
				continue;
			pane.editingGrammarModel.addProduction(p);
		}
		pane.editingActive = true;

		if (currentProductions.equals(desiredProductions))
			nextStep();
		else
			updateDisplay();
	}

	/**
	 * Does the current step.
	 */
	public void doStep() {
		switch (step) {
		case VARAIBLE_GRAPH:
			Transition[] t = (Transition[]) vdgTransitions
					.toArray(new Transition[0]);
			for (int i = 0; i < t.length; i++)
				vdg.addTransition(t[i]);
			break;
		case PRODUCTION_MODIFY:
			for (int i = pane.editingGrammarModel.getRowCount() - 2; i >= 0; i--) {
				Production p = pane.editingGrammarModel.getProduction(i);
				if (unitProductions.contains(p)) {
					pane.editingGrammarModel.deleteRow(i);
					unitProductions.remove(p);
				}
			}
			pane.editingActive = false;
			Production[] p = (Production[]) desiredProductions
					.toArray(new Production[0]);
			for (int i = 0; i < p.length; i++) {
				if (!currentProductions.add(p[i]))
					continue;
				pane.editingGrammarModel.addProduction(p[i]);
			}
			nextStep();
			break;
		case FINISHED:
			break;
		}
	}

	/**
	 * Does all steps.
	 */
	public void doAll() {
		while (step != FINISHED)
			doStep();
	}
	
	public Grammar getGrammar()
	{
		return pane.getGrammar();
	}

	/**
	 * Updates the detail display to show how many more removes, and additions
	 * are needed in the grammar modification step.
	 */
	void updateDisplay() {
		switch (step) {
		case VARAIBLE_GRAPH: {
			int toAdd = vdgTransitions.size();
			pane.detailLabel.setText(toAdd + " more transition(s) needed.");
			if (toAdd == 0)
				nextStep();
			break;
		}
		case PRODUCTION_MODIFY: {
			int toRemove = unitProductions.size();
			int toAdd = desiredProductions.size() - currentProductions.size()
					+ toRemove;
			pane.detailLabel.setText(toRemove + " more remove"
					+ (toRemove == 1 ? "" : "s") + ", and " + toAdd
					+ " more addition" + (toAdd == 1 ? "" : "s") + " needed.");
			if (toAdd == 0 && toRemove == 0)
				nextStep();
			break;
		}
		}
	}

	/**
	 * When a production is added manually by the user, this is told about it.
	 * 
	 * @param production
	 *            the production added
	 * @param row
	 *            the row that was added
	 * @return if this production should be accepted
	 */
	boolean productionAdded(Production production, int row) {
		if (currentProductions.contains(production)) {
			// We already have it.
			JOptionPane.showMessageDialog(pane,
					"This production is already in the grammar.",
					"Production Already Here", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		if (!desiredProductions.contains(production)) {
			// We don't have it, and don't want it!
			JOptionPane.showMessageDialog(pane,
					"This production is not part of the reformed grammar.",
					"Production Not Desired", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		// We want it. We want it so bad.
		currentProductions.add(production);
		updateDisplay();
		return true;
	}

	/**
	 * When a state is clicked during the step of production modification, this
	 * method is told about it.
	 * 
	 * @param state
	 *            the state that was clicked
	 * @param event
	 *            the mouse event associated with the click
	 */
	void stateClicked(State state, MouseEvent event) {
		if (event.isShiftDown()) {
			if (state == null)
				return;
			if (pane.vdgDrawer.isSelected(state))
				pane.vdgDrawer.removeSelected(state);
			else
				pane.vdgDrawer.addSelected(state);
		} else {
			if (state == null) {
				pane.vdgDrawer.clearSelected();
			} else {
				if (!pane.vdgDrawer.isSelected(state)) {
					pane.vdgDrawer.clearSelected();
					pane.vdgDrawer.addSelected(state);
				}
			}
		}
		pane.vdgEditor.repaint();
		pane.completeSelectedAction
				.setEnabled(pane.vdgDrawer.numberSelected() > 0);
	}

	/**
	 * When a production is chosen to be removed, this is told about it. This
	 * happens before the deletion occurs.
	 * 
	 * @param production
	 *            the production chosen to be removed
	 * @param row
	 *            the row for this production
	 * @return if this production should be deleted
	 */
	boolean productionDeleted(Production production, int row) {
		if (!ProductionChecker.isUnitProduction(production)) {
			return false;
		}
		unitProductions.remove(production);
		currentProductions.remove(production);
		return true;
	}

	/** The unit pane. */
	UnitPane pane;

	/** The grammar being converted. */
	Grammar grammar;

	/** The unit remover object. */
	UnitProductionRemover remover = new UnitProductionRemover();

	// Variables related to the VDG.

	/** The variable dependency graph. */
	VariableDependencyGraph vdg;

	/** The set of transitions that should be added to the VDG. */
	Set vdgTransitions = new HashSet();

	/**
	 * The set of productions that should comprise the grammar, those that
	 * currently do, and those that should be removed.
	 */
	Set desiredProductions = new HashSet(), currentProductions = new HashSet(),
			unitProductions = new HashSet();

	/** The current step. */
	int step = 0;

	/** The steps available. */
	static final int VARAIBLE_GRAPH = 1, PRODUCTION_MODIFY = 2, FINISHED = 3;
}
