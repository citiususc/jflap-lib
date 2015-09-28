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

import java.awt.event.MouseEvent;
import java.util.*;
import javax.swing.*;
import edu.duke.cs.jflap.automata.event.*;

/**
 * This is the controller for the useless panel. Bleh.
 * 
 * @author Thomas Finley
 */

public class UselessController {
	/**
	 * This instantiates a new useless controller.
	 * 
	 * @param pane
	 *            the useless panel
	 * @param grammar
	 *            the grammar to produce
	 */
	public UselessController(UselessPane pane, Grammar grammar) {
		this.pane = pane;
		this.grammar = grammar;
		nextStep();
	}

	/**
	 * This is called to move the lambda controller to the next step.
	 */
	private void nextStep() {
		if (step < FINISHED)
			step++;
		switch (step) {
		case DERIVE_TERMINALS: {
			pane.mainLabel.setText("Select variables that derive terminals.");
			pane.detailLabel
					.setText("Click productions; the LHS variable will be added.");
			terminalVariables = UselessProductionRemover
					.getCompleteUsefulVariableSet(grammar);
			// Do enabledness of actions.
			pane.deleteAction.setEnabled(false);
			pane.doStepAction.setEnabled(true);
			pane.doAllAction.setEnabled(true);
			pane.proceedAction.setEnabled(false);
			pane.exportAction.setEnabled(false);
			grammar = UselessProductionRemover.getTerminalGrammar(grammar);
			// ////System.out.println("Grammar now: "+grammar);
			break;
		}
		case VARAIBLE_GRAPH: {
			if (!terminalVariables.contains(grammar.getStartVariable())) {
				// The start variable is not here.
				step = START_NOT_TERMINAL_DERIVING_ERROR;
				nextStep();
				return;
			}
			pane.mainLabel.setText("Complete dependency graph.");
			pane.detailLabel
					.setText("For every production, connect start and end.");
			// Make the VDG.
			UselessProductionRemover.initializeVariableDependencyGraph(vdg,
					grammar);
			State s[] = vdg.getStates();
			for (int i = 0; i < s.length; i++)
				if (terminalVariables.contains(s[i].getName()))
					vdg.addFinalState(s[i]);
			// Cache the transitions we have to add.
			Production[] p = grammar.getProductions();
			String[] variables = (String[]) terminalVariables
					.toArray(new String[0]);
			for (int i = 0; i < variables.length; i++)
				for (int j = 0; j < variables.length; j++) {
					String v1 = variables[i], v2 = variables[j];
					if (i != j
							&& UselessProductionRemover.isDependentOn(v1, v2,
									grammar))
						vdgTransitions.add(UselessProductionRemover
								.getTransition(v1, v2, vdg));
				}
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
			// Display only those productions with terminal deriving variables.
			for (int i = 0; i < p.length; i++) {
				pane.editingGrammarModel.addProduction(p[i]);
				currentProductions.add(p[i]);
			}
			// Set the actions.
			updateDisplay();
			break;
		}
		case PRODUCTION_MODIFY: {
			pane.updateDeleteEnabledness();
			pane.mainLabel
					.setText("Modify the grammar to remove useless productions.");
			Grammar g = UselessProductionRemover
					.getUselessProductionlessGrammar(grammar);
			Production[] p = grammar.getProductions(), p2 = g.getProductions();
			Set usefulProductions = new HashSet(Arrays.asList(p2));
			for (int i = 0; i < p.length; i++) {
				if (usefulProductions.contains(p[i]))
					continue;
				uselessProductions.add(p[i]);
			}
			// ////System.out.println("USEFUL PRODUCTIONS: "+usefulProductions);
			updateDisplay();
			break;
		}
		case FINISHED: {
			pane.editingActive = false;
			pane.deleteAction.setEnabled(false);
			pane.mainLabel.setText("Useless removal complete.");
			pane.detailLabel.setText("\"Proceed\" or \"Export\" available.");
			pane.doStepAction.setEnabled(false);
			pane.doAllAction.setEnabled(false);
			pane.proceedAction.setEnabled(true);
			pane.exportAction.setEnabled(true);
			break;
		}
		case START_NOT_TERMINAL_DERIVING_ERROR: {
			pane.mainLabel.setText("The start variable "
					+ grammar.getStartVariable()
					+ " does not derive terminals.");
			pane.detailLabel.setText("The grammar cannot have rules.  "
					+ "No further action is sensible.");
			pane.doStepAction.setEnabled(false);
			pane.doAllAction.setEnabled(false);
			pane.proceedAction.setEnabled(false);
			pane.exportAction.setEnabled(false);

			break;
		}
		}
	}

	/**
	 * Does the current step.
	 */
	void doStep() {
		switch (step) {
		case DERIVE_TERMINALS:
			derivedTerminalVariables.addAll(terminalVariables);
			pane.terminalLabel.setText("Variables that predicate terminals: "
					+ derivedTerminalVariables);
			nextStep();
			break;
		case VARAIBLE_GRAPH:
			Transition[] t = (Transition[]) vdgTransitions
					.toArray(new Transition[0]);
			for (int i = 0; i < t.length; i++)
				vdg.addTransition(t[i]);
			break;
		case PRODUCTION_MODIFY:
			for (int i = pane.editingGrammarModel.getRowCount() - 2; i >= 0; i--) {
				Production p = pane.editingGrammarModel.getProduction(i);
				if (uselessProductions.contains(p)) {
					pane.editingGrammarModel.deleteRow(i);
					uselessProductions.remove(p);
				}
			}
			updateDisplay();
			break;
		case FINISHED:
		default:
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
			int toRemove = uselessProductions.size();
			pane.detailLabel.setText(toRemove + " more remove(s) needed.");
			if (toRemove == 0)
				nextStep();
			break;
		}
		}
	}

	/**
	 * When a production is clicked in the grammar table, this method is told
	 * about it.
	 * 
	 * @param production
	 *            the production clicked in the table
	 * @param event
	 *            the mouse event that was the clicked
	 */
	void productionClicked(Production production, MouseEvent event) {
		switch (step) {
		case DERIVE_TERMINALS:
			String var = production.getLHS();
			if (derivedTerminalVariables.contains(var)) {
				// Already here!
				pane.detailLabel.setText(var
						+ " already selected!  "
						+ (terminalVariables.size() - derivedTerminalVariables
								.size()) + " more variable(s) needed.");
				return;
			}
			if (terminalVariables.contains(var)) {
				// Not here, but should be added!
				derivedTerminalVariables.add(var);
				pane.detailLabel.setText(var
						+ " added!  "
						+ (terminalVariables.size() - derivedTerminalVariables
								.size()) + " more variable(s) needed.");
				pane.terminalLabel
						.setText("Variables that predicate terminals: "
								+ derivedTerminalVariables);
				if (derivedTerminalVariables.size() == terminalVariables.size())
					nextStep();
				return;
			}
			pane.detailLabel.setText(var
					+ " does not predicate terminals!  "
					+ (terminalVariables.size() - derivedTerminalVariables
							.size()) + " more variable(s) needed.");
			break;
		default:
			break;
		}
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
		return uselessProductions.remove(production)
				&& currentProductions.remove(production);
	}

	/** The unit pane. */
	UselessPane pane;

	/** The grammar being converted. */
	Grammar grammar;

	/** The unit remover object. */
	UselessProductionRemover remover = new UselessProductionRemover();

	// Variables related to the VDG.

	/** The set of variables that derive terminals. */
	Set terminalVariables;

	/** The set of variables discovered by the user that derive terminals. */
	Set derivedTerminalVariables = new TreeSet();

	/** The variable dependency graph. */
	VariableDependencyGraph vdg = new VariableDependencyGraph();

	/** The set of transitions that should be added to the VDG. */
	Set vdgTransitions = new HashSet();

	/**
	 * The set of productions that should comprise the grammar, those that
	 * currently do, and those that should be removed.
	 */
	Set currentProductions = new HashSet(), uselessProductions = new HashSet();

	/** The current step. */
	int step = 0;

	/** The steps available. */
	final static int DERIVE_TERMINALS = 1, VARAIBLE_GRAPH = 2,
			PRODUCTION_MODIFY = 3, FINISHED = 4,
			START_NOT_TERMINAL_DERIVING_ERROR = 5;
}
