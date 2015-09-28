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





package edu.duke.cs.jflap.gui.minimize;

import edu.duke.cs.jflap.automata.*;
import edu.duke.cs.jflap.automata.fsa.*;
import edu.duke.cs.jflap.automata.graph.*;
import edu.duke.cs.jflap.automata.graph.layout.GEMLayoutAlgorithm;
import edu.duke.cs.jflap.gui.SplitPaneFactory;
import edu.duke.cs.jflap.gui.editor.*;
import edu.duke.cs.jflap.gui.environment.Environment;
import edu.duke.cs.jflap.gui.tree.*;
import edu.duke.cs.jflap.gui.viewer.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.tree.*;

/**
 * The <CODE>MinimizePane</CODE> is a view created to minimize a DFA using
 * some minimizing tree structure.
 * 
 * @author Thomas Finley
 */

public class MinimizePane extends JPanel {
	/**
	 * Instantiates a <CODE>MinimizePane</CODE>.
	 * 
	 * @param dfa
	 *            a DFA to minimize
	 * @param environment
	 *            the environment this minimize pane will be added to
	 */
	public MinimizePane(FiniteStateAutomaton dfa, Environment environment) {
		// Set up the minimizable automaton, and the minimize tree.
		minimizer = new Minimizer();
		minimizer.initializeMinimizer();
		dfa = (FiniteStateAutomaton) minimizer.getMinimizeableAutomaton(dfa);
		// minimizer.initializeMinimizer();
		TreeModel tree = minimizer.getInitializedTree(dfa);
		// Set up the drawers.
		automatonDrawer = new SelectionDrawer(dfa);
		treeDrawer = new SelectTreeDrawer(tree);
		// Set up the minimize node drawer.
		MinimizeNodeDrawer nodeDrawer = new MinimizeNodeDrawer();
		treeDrawer.setNodeDrawer(nodeDrawer);
		TreeNode[] groups = Trees.children((MinimizeTreeNode) tree.getRoot());
		for (int i = 0; i < groups.length; i++) {
			MinimizeTreeNode group = (MinimizeTreeNode) groups[i];
			State[] states = (State[]) group.getUserObject();
			if (states.length == 0)
				continue;
			if (dfa.isFinalState(states[0]))
				nodeDrawer.setLabel(group, "Final");
			else
				nodeDrawer.setLabel(group, "Nonfinal");
		}

		// Set up the controller object.
		controller = new MinimizeController(this, automatonDrawer, treeDrawer,
				minimizer);
		JPanel right = new JPanel(new BorderLayout());
		right.add(initTreePane(), BorderLayout.CENTER);
		controlPanel = new ControlPanel(treeDrawer, controller);
		/*
		 * right.add(new ControlPanel(treeDrawer, controller),
		 * BorderLayout.SOUTH);
		 */

		// Finally, initialize the view.
		split = SplitPaneFactory.createSplit(environment, true, 0.5,
				initAutomatonPane(), right);
		setLayout(new BorderLayout());
		add(split, BorderLayout.CENTER);
		add(controlPanel, BorderLayout.NORTH);
		add(messageLabel, BorderLayout.SOUTH);
		split.setResizeWeight(0.5);
		controller.setEnabledness();
	}

	/**
	 * Initializes the automaton pane.
	 */
	public AutomatonPane initAutomatonPane() {
		AutomatonPane apane = new AutomatonPane(automatonDrawer);
		edu.duke.cs.jflap.gui.SuperMouseAdapter a = new ArrowMinimizeTool(apane, automatonDrawer);
		apane.addMouseListener(a);
		apane.addMouseMotionListener(a);
		return apane;
	}

	/**
	 * Initializes the tree pane.
	 */
	public TreePanel initTreePane() {
		final TreePanel tpane = new TreePanel(treeDrawer);
		edu.duke.cs.jflap.gui.SuperMouseAdapter a = new edu.duke.cs.jflap.gui.SuperMouseAdapter() {
			public void mouseClicked(MouseEvent event) {
				TreeNode n = tpane.nodeAtPoint(event.getPoint());
				controller.nodeClicked((MinimizeTreeNode) n, event);
			}

			public void mousePressed(MouseEvent event) {
				TreeNode n = tpane.nodeAtPoint(event.getPoint());
				controller.nodeDown((MinimizeTreeNode) n, event);
			}
		};
		tpane.addMouseListener(a);
		tpane.addMouseMotionListener(a);
		return tpane;
	}

	/**
	 * Tells the pane to replace the tree pane with a pane to build the
	 * minimized automaton. This should be called once the tree is completed and
	 * the user has elected to move on to the building of the minimized
	 * automaton.
	 * 
	 * @param dfa
	 *            the finite state automaton we're minimizing
	 * @param tree
	 *            the completed minimized tree; results will be unpredictable if
	 *            this tree is not truly minimized
	 */
	public void beginMinimizedAutomaton(FiniteStateAutomaton dfa,
			DefaultTreeModel tree) {
		// Create the new view.
		remove(controlPanel);
		FiniteStateAutomaton newAutomaton = new FiniteStateAutomaton();
		minimizer.createStatesForMinimumDfa(dfa, newAutomaton, tree);
		SelectionDrawer drawer = new SelectionDrawer(newAutomaton);
		EditorPane ep = new EditorPane(drawer, new ToolBox() {
			public java.util.List tools(AutomatonPane view,
					AutomatonDrawer drawer) {
				java.util.List tools = new java.util.LinkedList();
				tools.add(new ArrowMinimizeTool(view, drawer));
				tools.add(new TransitionTool(view, drawer));
				return tools;
			}
		});
		// Remove all selected stuff.
		automatonDrawer.clearSelected();
		// Set up the controller device.
		builderController = new BuilderController(dfa, newAutomaton, drawer,
				minimizer, tree, split);
		// Set the view in the right hand side.
		JPanel right = new JPanel(new BorderLayout());
		right.add(ep, BorderLayout.CENTER);
		/*
		 * right.add(new BuilderControlPanel(builderController),
		 * BorderLayout.SOUTH);
		 */
		ep.getToolBar().addSeparator();
		BuilderControlPanel.initView(ep.getToolBar(), builderController);
		split.setRightComponent(right);
		invalidate();
		repaint();

		// Do graph layout.
		AutomatonGraph graph = new AutomatonGraph(newAutomaton);
		graph.addVertex(newAutomaton.getInitialState(), new Point(0, 0));
		Iterator it = builderController.remainingTransitions.iterator();
		while (it.hasNext()) {
			Transition t = (Transition) it.next();
			graph.addEdge(t.getFromState(), t.getToState());
		}
		GEMLayoutAlgorithm layout = new GEMLayoutAlgorithm();
		Set constantStates = new HashSet();
		constantStates.add(newAutomaton.getInitialState());
		layout.layout(graph, constantStates);
		graph.moveAutomatonStates();
		validate();
		ep.getAutomatonPane().fitToBounds(10);
	}

	/**
	 * This extension of the arrow tool does not allow the editing of
	 * transitions.
	 */
	private class ArrowMinimizeTool extends edu.duke.cs.jflap.gui.editor.ArrowNontransitionTool {
		/**
		 * Instantiates a new <CODE>ArrowMinimizeTool</CODE>.
		 * 
		 * @param view
		 *            the view the automaton is drawn in
		 * @param drawer
		 *            the automaton drawer
		 */
		public ArrowMinimizeTool(AutomatonPane view, AutomatonDrawer drawer) {
			super(view, drawer);
		}

		/**
		 * On a mouse click, this simply returns,
		 * 
		 * @param event
		 *            the mouse event
		 */
		public void mouseClicked(java.awt.event.MouseEvent event) {
			super.mouseClicked(event);
			State s = automatonDrawer.stateAtPoint(event.getPoint());
			// If we're still building the tree...
			if (builderController == null)
				controller.stateDown(s, event);
		}
	}

	/** The object that handles the grit of the minimization. */
	Minimizer minimizer;

	/** The drawer for the original automaton. */
	SelectionDrawer automatonDrawer;

	/** The drawer for the tree. */
	SelectTreeDrawer treeDrawer;

	/** The minimize controller. */
	MinimizeController controller;

	/** The minimum automaton builder controller. */
	BuilderController builderController = null;

	/** The view for this pane. */
	JSplitPane split;

	/** The toolbar. */
	ControlPanel controlPanel;

	/** The message label. */
	JLabel messageLabel = new JLabel(" ");
}
