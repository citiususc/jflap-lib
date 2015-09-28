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




package edu.duke.cs.jflap.gui.deterministic;

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
import edu.duke.cs.jflap.gui.editor.ArrowNontransitionTool;
import edu.duke.cs.jflap.gui.editor.ToolBox;
import edu.duke.cs.jflap.gui.environment.AutomatonEnvironment;
import edu.duke.cs.jflap.gui.environment.Universe;
import edu.duke.cs.jflap.gui.viewer.AutomatonDrawer;
import edu.duke.cs.jflap.gui.viewer.AutomatonPane;
import edu.duke.cs.jflap.gui.viewer.SelectionDrawer;

/**
 * Pane used for adding trap state
 * @author Kyung Min (Jason) Lee
 *
 */
public class AddTrapStatePane extends JPanel{

	/**
	 * The environment that holds the automaton. The automaton from the
	 * environment is itself not modified.
	 */
	private AutomatonEnvironment myEnvironment;

	/**
	 * The copy of the original automaton, which will be modified throughout
	 * this process.
	 */
	private FiniteStateAutomaton myAutomaton;
	
	/**
	 * Constructor for creating Trap State Pane
	 * @param environment
	 */
	public AddTrapStatePane(AutomatonEnvironment environment) 
	{
		myAutomaton = (FiniteStateAutomaton) environment.getAutomaton().clone();
		JFrame frame = Universe.frameForEnvironment(environment);

		setLayout(new BorderLayout());

		JPanel labels = new JPanel(new BorderLayout());
		JLabel mainLabel = new JLabel();
		JLabel detailLabel = new JLabel();
		labels.add(mainLabel, BorderLayout.NORTH);
		labels.add(detailLabel, BorderLayout.SOUTH);

		add(labels, BorderLayout.NORTH);
		SelectionDrawer automatonDrawer = new SelectionDrawer(myAutomaton);

		final AddTrapStateController controller = new AddTrapStateController(myAutomaton,
				automatonDrawer, mainLabel, detailLabel, frame);

		edu.duke.cs.jflap.gui.editor.EditorPane ep = new edu.duke.cs.jflap.gui.editor.EditorPane(automatonDrawer,
				new ToolBox() {
					public List tools(AutomatonPane view, AutomatonDrawer drawer) {
						LinkedList tools = new LinkedList();
						tools.add(new ArrowNontransitionTool(view, drawer));
						tools.add(new TrapStateTool(view, drawer,
										controller));
						tools.add(new TrapTransitionTool(view, drawer,
								controller));
						return tools;
					}
				});

		JToolBar bar = ep.getToolBar();
		
		bar.addSeparator();
		bar.add(new JButton(new AbstractAction("Do All") {
			public void actionPerformed(ActionEvent e) {
				controller.doAll();
			}
		}));

		add(ep, BorderLayout.CENTER);
	}
}
