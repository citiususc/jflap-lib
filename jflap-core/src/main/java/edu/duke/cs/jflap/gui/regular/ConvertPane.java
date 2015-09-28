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





package edu.duke.cs.jflap.gui.regular;

import edu.duke.cs.jflap.gui.editor.ArrowNontransitionTool;
import edu.duke.cs.jflap.gui.editor.ToolBox;
import edu.duke.cs.jflap.gui.environment.AutomatonEnvironment;
import edu.duke.cs.jflap.gui.environment.Universe;
import edu.duke.cs.jflap.gui.viewer.AutomatonDrawer;
import edu.duke.cs.jflap.gui.viewer.AutomatonPane;
import edu.duke.cs.jflap.gui.viewer.SelectionDrawer;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import edu.duke.cs.jflap.automata.fsa.FiniteStateAutomaton;

/**
 * This is the pane that holds the tools necessary for the conversion of a
 * finite state automaton to a regular expression.
 * 
 * @author Thomas Finley
 */

public class ConvertPane extends JPanel {
	/**
	 * Creates a new conversion pane for the conversion of an automaton to a
	 * regular expression.
	 * 
	 * @param environment
	 *            the environment that this convert pane will be a part of
	 */
	public ConvertPane(AutomatonEnvironment environment) {
		this.environment = environment;
		automaton = (FiniteStateAutomaton) environment.getAutomaton().clone();
		JFrame frame = Universe.frameForEnvironment(environment);

		setLayout(new BorderLayout());

		JPanel labels = new JPanel(new BorderLayout());
		JLabel mainLabel = new JLabel();
		JLabel detailLabel = new JLabel();
		labels.add(mainLabel, BorderLayout.NORTH);
		labels.add(detailLabel, BorderLayout.SOUTH);

		add(labels, BorderLayout.NORTH);
		SelectionDrawer automatonDrawer = new SelectionDrawer(automaton);

		final FSAToREController controller = new FSAToREController(automaton,
				automatonDrawer, mainLabel, detailLabel, frame);

		edu.duke.cs.jflap.gui.editor.EditorPane ep = new edu.duke.cs.jflap.gui.editor.EditorPane(automatonDrawer,
				new ToolBox() {
					public List tools(AutomatonPane view, AutomatonDrawer drawer) {
						LinkedList tools = new LinkedList();
						tools.add(new ArrowNontransitionTool(view, drawer));
						tools
								.add(new RegularStateTool(view, drawer,
										controller));
						tools.add(new RegularTransitionTool(view, drawer,
								controller));
						tools.add(new CollapseTool(view, drawer, controller));
						tools.add(new StateCollapseTool(view, drawer,
								controller));
						return tools;
					}
				});

		JToolBar bar = ep.getToolBar();
		bar.addSeparator();
		bar.add(new JButton(new AbstractAction("Do It") {
			public void actionPerformed(ActionEvent e) {
				controller.moveNextStep();
			}
		}));
		bar.add(new JButton(new AbstractAction("Export") {
			public void actionPerformed(ActionEvent e) {
				controller.export();
			}
		}));
		/*
		 * bar.add(new JButton(new AbstractAction("Export Automaton") { public
		 * void actionPerformed(ActionEvent e) { controller.exportAutomaton(); }
		 * }));
		 */

		add(ep, BorderLayout.CENTER);
	}

	/**
	 * The environment that holds the automaton. The automaton from the
	 * environment is itself not modified.
	 */
	AutomatonEnvironment environment;

	/**
	 * The copy of the original automaton, which will be modified throughout
	 * this process.
	 */
	private FiniteStateAutomaton automaton;
}
