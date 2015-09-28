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





package edu.duke.cs.jflap.gui.grammar.convert;

import edu.duke.cs.jflap.automata.Automaton;
import edu.duke.cs.jflap.grammar.*;
import edu.duke.cs.jflap.gui.SplitPaneFactory;
import edu.duke.cs.jflap.gui.TableTextSizeSlider;
import edu.duke.cs.jflap.gui.editor.*;
import edu.duke.cs.jflap.gui.environment.Environment;
import edu.duke.cs.jflap.gui.viewer.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

/**
 * This is a graphical component to aid in the conversion of a context free
 * grammar to some form of pushdown automaton.
 * 
 * @author Thomas Finley
 */

public class ConvertPane extends JPanel {
	/**
	 * Instantiates a <CODE>ConvertPane</CODE>.
	 * 
	 * @param grammar
	 *            the grammar to convert
	 * @param automaton
	 *            a "starting automaton" that may already have some start points
	 *            predefined
	 * @param productionsToTransitions
	 *            the mapping of productions to transitions, which should be one
	 *            to one
	 * @param env
	 *            the environment to which this pane will be added
	 */
	public ConvertPane(Grammar grammar, Automaton automaton,
			Map productionsToTransitions, Environment env) {
		this.grammar = grammar;
		this.automaton = automaton;

		this.setLayout(new BorderLayout());
		JSplitPane split = SplitPaneFactory.createSplit(env, true, .4, null,
				null);
		this.add(split, BorderLayout.CENTER);

		grammarViewer = new GrammarViewer(grammar);
		this.add(new TableTextSizeSlider(grammarViewer), BorderLayout.NORTH);
		JScrollPane scroller = new JScrollPane(grammarViewer);
		split.setLeftComponent(scroller);
		// Create the right view.

		automatonDrawer = new SelectionDrawer(automaton);
		EditorPane ep = new EditorPane(automatonDrawer, new ToolBox() {
			public java.util.List tools(AutomatonPane view,
					AutomatonDrawer drawer) {
				LinkedList tools = new LinkedList();
				tools.add(new ArrowNontransitionTool(view, drawer));
				tools.add(new TransitionTool(view, drawer));
				return tools;
			}
		});
		// Create the controller device.
		ConvertController controller = new ConvertController(grammarViewer,
				automatonDrawer, productionsToTransitions, this);
		controlPanel(ep.getToolBar(), controller);
		split.setRightComponent(ep);
		editorPane = ep;
	}

	/**
	 * Initializes the control objects in the editor pane's tool bar.
	 * 
	 * @param controller
	 *            the controller object
	 */
	private void controlPanel(JToolBar bar, final ConvertController controller) {
		bar.addSeparator();
		bar.add(new AbstractAction("Show All") {
			public void actionPerformed(ActionEvent e) {
				controller.complete();
			}
		});
		bar.add(new AbstractAction("Create Selected") {
			public void actionPerformed(ActionEvent e) {
				controller.createForSelected();
			}
		});
		bar.add(new AbstractAction("Done?") {
			public void actionPerformed(ActionEvent e) {
				controller.isDone();
			}
		});
		bar.add(new AbstractAction("Export") {
			public void actionPerformed(ActionEvent e) {
				controller.export();
			}
		});
	}

	/**
	 * 
	 * /** Returns the editor pane.
	 * 
	 * @return the editor pane
	 */
	public EditorPane getEditorPane() {
		return editorPane;
	}

	/** The grammar that this convertpane holds. */
	private Grammar grammar;

	/** The grammar viewer. */
	private GrammarViewer grammarViewer;

	/** The automaton selection drawer. */
	private SelectionDrawer automatonDrawer;

	/** The automaton. */
	private Automaton automaton;

	/** The editor pane. */
	private EditorPane editorPane;
}
