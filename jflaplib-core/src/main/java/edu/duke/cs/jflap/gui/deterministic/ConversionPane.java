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

import edu.duke.cs.jflap.gui.SplitPaneFactory;
import edu.duke.cs.jflap.gui.TooltipAction;
import edu.duke.cs.jflap.gui.editor.ArrowNontransitionTool;
import edu.duke.cs.jflap.gui.editor.EditorPane;
import edu.duke.cs.jflap.gui.editor.ToolBox;
import edu.duke.cs.jflap.gui.environment.Environment;
import edu.duke.cs.jflap.gui.viewer.AutomatonDraggerPane;
import edu.duke.cs.jflap.gui.viewer.AutomatonDrawer;
import edu.duke.cs.jflap.gui.viewer.AutomatonPane;
import edu.duke.cs.jflap.gui.viewer.SelectionDrawer;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;

import edu.duke.cs.jflap.automata.fsa.FiniteStateAutomaton;

/**
 * This is the pane where the user defines all that is needed for the conversion
 * of an NFA to a DFA.
 * 
 * @author Thomas Finley
 */

public class ConversionPane extends JPanel {
	/**
	 * Instantiates a new <CODE>ConversionPane</CODE>.
	 * 
	 * @param nfa
	 *            the NFA we are converting to a DFA
	 * @param environment
	 *            the environment this pane will be added to
	 */
	public ConversionPane(FiniteStateAutomaton nfa, Environment environment) {
		super(new BorderLayout());
		FiniteStateAutomaton dfa = new FiniteStateAutomaton();
		controller = new ConversionController(nfa, dfa, this);
		// Create the left view of the original NFA.
		AutomatonPane nfaPane = new AutomatonDraggerPane(nfa);
		// Put it all together.
		JSplitPane split = SplitPaneFactory.createSplit(environment, true, .25,
				nfaPane, createEditor(dfa));
		add(split, BorderLayout.CENTER);

		// When the component is first shown, perform layout.
		addComponentListener(new ComponentAdapter() {
			public void componentShown(ComponentEvent event) {
				// We may now lay out the states...
				controller.performFirstLayout();
				editor.getAutomatonPane().repaint();
			}

			boolean doneBefore = false;
		});
	}

	/**
	 * Creates the editor pane for the DFA.
	 * 
	 * @param dfa
	 *            the dfa to create the editor pane for
	 */
	private EditorPane createEditor(FiniteStateAutomaton dfa) {
		SelectionDrawer drawer = new SelectionDrawer(dfa);
		editor = new EditorPane(drawer, new ToolBox() {
			public java.util.List tools(AutomatonPane view,
					AutomatonDrawer drawer) {
				java.util.List tools = new java.util.LinkedList();
				tools.add(new ArrowNontransitionTool(view, drawer));
				tools.add(new TransitionExpanderTool(view, drawer, controller));
				tools.add(new StateExpanderTool(view, drawer, controller));
				return tools;
			}
		});
		addExtras(editor.getToolBar());
		return editor;
	}

	/**
	 * Adds the extra controls to the toolbar for the editorpane.
	 * 
	 * @param toolbar
	 *            the tool bar to add crap to
	 */
	private void addExtras(JToolBar toolbar) {
		toolbar.addSeparator();
		toolbar.add(new TooltipAction("Complete",
				"This will finish all expansion.") {
			public void actionPerformed(ActionEvent e) {
				controller.complete();
			}
		});
		toolbar.add(new TooltipAction("Done?", "Are we finished?") {
			public void actionPerformed(ActionEvent e) {
				controller.done();
			}
		});
	}

	/** The controller object. */
	private ConversionController controller;

	/** The editor pane. */
	EditorPane editor;
}
