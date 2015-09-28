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

import edu.duke.cs.jflap.gui.tree.SelectTreeDrawer;
import edu.duke.cs.jflap.gui.tree.Trees;
import edu.duke.cs.jflap.gui.viewer.SelectionDrawer;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JOptionPane;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import edu.duke.cs.jflap.automata.State;
import edu.duke.cs.jflap.automata.fsa.FiniteStateAutomaton;
import edu.duke.cs.jflap.automata.fsa.MinimizeTreeNode;
import edu.duke.cs.jflap.automata.fsa.Minimizer;

/**
 * The <CODE>MinimizeController</CODE> serves as an intermediary between the
 * <CODE>Minimizer</CODE> object and the various gui classes that handles the
 * drawing of the unminimized automaton and minimization tree. This does not
 * handle the building of the minimized automata, however, which is handled by
 * {@link edu.duke.cs.jflap.gui.minimize.BuilderController}.
 * 
 * @author Thomas Finley
 */

class MinimizeController {
	/**
	 * Instantiates a new <CODE>MinimizeController</CODE>.
	 */
	public MinimizeController(MinimizePane view, SelectionDrawer adrawer,
			SelectTreeDrawer tdrawer, Minimizer minimizer) {
		this.view = view;
		automatonDrawer = adrawer;
		treeDrawer = tdrawer;
		this.minimizer = minimizer;
	}

	/**
	 * This method should be called whenever a state has the mouse down over it.
	 * 
	 * @param state
	 *            the state that was under the mouse
	 * @param event
	 *            the corresponding mouse event
	 */
	public void stateDown(State state, MouseEvent event) {
		if (state == null)
			return;
		TreeNode[] selected = treeDrawer.getSelected();
		if (selected.length != 1)
			return;
		toggleState((MinimizeTreeNode) selected[0], state);
	}

	/**
	 * This method should be called whenever a node is clicked.
	 * 
	 * @param node
	 *            the node that was under the mouse
	 * @param event
	 *            the corresponding mouse event
	 */
	public void nodeClicked(MinimizeTreeNode node, MouseEvent event) {
		if (event.isPopupTrigger())
			return;
		// if (menu.isVisible()) return;
		if (node == null) {
			automatonDrawer.clearSelected();
			treeDrawer.clearSelected();
			view.repaint();
			setEnabledness();
			return;
		}
		setSelectedStates(node);
		setEnabledness();
	}

	/**
	 * This method will set the enabledness and the tool tips of items in the
	 * control panel based on the currently selected tree node.
	 */
	void setEnabledness() {
		TreeNode[] selected = treeDrawer.getSelected();
		ControlPanel cp = view.controlPanel;
		// We can only proceed if all has been finished.
		State[] group = minimizer.getDistinguishableGroup(getAutomaton(),
				getTree());
		boolean done = expanding == null && group == null;
		if (done) {
			cp.finishAction.setEnabled(true);
			selected = new TreeNode[0];
			cp.finishAction.setTip("Proceed to automaton building phase.");
		} else {
			cp.finishAction.setEnabled(false);
			cp.finishAction
					.setTip("Can't proceed.  Distinguishable groups still exist.");
		}
		// Actions that require one be selected...
		if (selected.length != 1) {
			// Can't do anything if not exactly one node is selected.
			String s = done ? "Tree is complete.  No action needed."
					: "This requires one node be selected.";
			cp.setTerminalAction.setEnabled(false);
			cp.setTerminalAction.setTip(s);
			cp.autoPartitionAction.setEnabled(false);
			cp.autoPartitionAction.setTip(s);
			cp.completeSubtreeAction.setEnabled(false);
			cp.autoPartitionAction.setTip(s);
			cp.removeAction.setEnabled(false);
			cp.autoPartitionAction.setTip(s);
		}
		// What about checking?
		if (expanding == null) {
			cp.checkNodeAction.setEnabled(false);
			cp.checkNodeAction.setTip("No group is being expanded.");
			cp.addChildAction.setEnabled(false);
			cp.addChildAction.setTip("No group is being expanded.");
		} else {
			// We can check!
			String es = minimizer.getString(expanding.getStates());
			cp.checkNodeAction.setEnabled(true);
			cp.checkNodeAction.setTip("Press to check expansion of group " + es
					+ ".");
			cp.addChildAction.setEnabled(true);
			cp.addChildAction.setTip("Add another partition to " + es + ".");
		}
		if (selected.length != 1)
			return;
		// Do items related to beginning an expansion.
		MinimizeTreeNode node = (MinimizeTreeNode) selected[0];

		if (expanding != null && node.getParent() == expanding) {
			String es = minimizer.getString(expanding.getStates());
			cp.removeAction.setEnabled(true);
			cp.removeAction.setTip("Remove this partition from " + es + ".");
		} else {
			cp.removeAction.setEnabled(false);
			cp.removeAction
					.setTip("We're not expanding the parent.  Cannot delete.");
		}

		if (expanding == null) {
			String completeS = minimizer.getString(node.getStates());
			cp.completeSubtreeAction
					.setTip("Complete all distinguishable groups descending from group "
							+ completeS + ".");
			cp.completeSubtreeAction.setEnabled(true);
		} else {
			String es = minimizer.getString(expanding.getStates());
			cp.completeSubtreeAction.setEnabled(false);
			cp.completeSubtreeAction.setTip("Must finish group " + es
					+ " before we do this.");
		}

		if (expanding == node) {
			cp.setTerminalAction.setEnabled(true);
			cp.setTerminalAction
					.setTip("Set this group to expand on a different terminal.");
			cp.autoPartitionAction.setEnabled(true);
			cp.autoPartitionAction
					.setTip("Complete the expansion of this group on "
							+ node.getTerminal() + ".");
		} else if (expanding == null) {
			// We're perhaps free to expand.
			if (node.getChildCount() == 0) {
				cp.setTerminalAction.setEnabled(true);
				cp.setTerminalAction
						.setTip("Attempt to expand the group on a terminal.");
				cp.autoPartitionAction.setEnabled(true);
				cp.autoPartitionAction
						.setTip("Complete the expansion of this group on some terminal.");
			} else {
				String s = "This group is already expanded.";
				cp.setTerminalAction.setEnabled(false);
				cp.setTerminalAction.setTip(s);
				cp.autoPartitionAction.setEnabled(false);
				cp.autoPartitionAction.setTip(s);
			}
		} else {
			// We're currently expanding, but not the selected node.
			String es = minimizer.getString(expanding.getStates());
			cp.setTerminalAction.setEnabled(false);
			cp.setTerminalAction.setTip("Cannot expand another group while "
					+ es + " is in progress.");
			cp.autoPartitionAction.setEnabled(false);
			cp.autoPartitionAction.setTip("Cannot expand another group while "
					+ es + " is in progress.");
			return;
		}
	}

	/**
	 * Splits the node on a terminal.
	 * 
	 * @param node
	 *            the node to split
	 * @return <CODE>true</CODE> if the node was able to be split, <CODE>false</CODE>
	 *         otherwise
	 */
	public boolean splitOnTerminal(MinimizeTreeNode node) {
		if (!canExpand(node))
			return false;

		// Is this the root?
		if (node.getParent() == null) {
			JOptionPane.showMessageDialog(view, "You can't split the root!");
			return false;
		}
		// Remove the children of this node.
		MinimizeTreeNode[] children = killChildren(node);
		// Can this even be split?
		if (!minimizer
				.isSplittable(node.getStates(), getAutomaton(), getTree())) {
			JOptionPane.showMessageDialog(view, CANT_SPLIT);
			addChildren(node, children);
			return false;
		}
		// Get whatever terminal from the user.
		String terminal = (String) JOptionPane.showInputDialog(view,
				"What terminal?");
		if (terminal == null) {
			addChildren(node, children);
			return false;
		}
		// Can we expand on this terminal?
		if (!minimizer.isSplittableOnTerminal(node.getStates(), terminal,
				getAutomaton(), getTree())) {
			JOptionPane.showMessageDialog(view, "The group doesn't split "
					+ "on that terminal!");
			addChildren(node, children);
			return false;
		}
		// Set the terminal.
		node.setTerminal(terminal);
		// Set the state so the controller knows we're in the process
		// of modifying this guy.
		expanding = node;
		// If it's splittable, then at least two children are
		// appropriate.
		addChild(node);
		addChild(node);
		view.repaint();
		return true;
	}

	/**
	 * This method should be called whenever a node has the mouse pressed on it.
	 * 
	 * @param node
	 *            the node that was under the mouse
	 * @param event
	 *            the corresponding mouse event
	 */
	public void nodeDown(MinimizeTreeNode node, MouseEvent event) {
		if (node == null)
			return;
	}

	/**
	 * Possibly add or remove a state from a node.
	 * 
	 * @param node
	 *            the node in question
	 * @param state
	 *            the state to add or remove
	 */
	public void toggleState(MinimizeTreeNode node, State state) {
		if (!canModifyChild((MinimizeTreeNode) node.getParent()))
			return;

		try {
			MinimizeTreeNode parent = (MinimizeTreeNode) node.getParent();
			// Are we actually expanding this parent?

			// Does the parent even have this state?
			if (!Arrays.asList(parent.getStates()).contains(state)) {
				JOptionPane.showMessageDialog(view,
						"The group being split does not contain state "
								+ state.getID() + "!");
				return;
			}
			;
			// Do any of the other children already contain it?
			TreeNode[] children = Trees.children(parent);
			for (int i = 0; i < children.length; i++) {
				MinimizeTreeNode child = (MinimizeTreeNode) children[i];
				if (child == node)
					continue;
				Collection c = Arrays.asList(child.getStates());
				if (c.contains(state)) {
					JOptionPane.showMessageDialog(view,
							"Another partition already contains state "
									+ state.getID() + "!");
					return;
				}
			}
		} catch (NullPointerException e) {
			// That was the root.
			JOptionPane.showMessageDialog(view,
					"One cannot change the states in the root!");
		}

		// Add/remove the state to/from the list of states.
		State[] states = node.getStates();
		java.util.List list = new LinkedList(Arrays.asList(states));
		if (list.contains(state))
			list.remove(state);
		else
			list.add(state);
		states = (State[]) list.toArray(new State[0]);
		node.setUserObject(states);
		setSelectedStates(node);
		view.repaint();
	}

	/**
	 * Splits a node on its terminal.
	 * 
	 * @param node
	 *            the node to split, which must already have a terminal on it
	 *            that can split this group
	 */
	private void split(MinimizeTreeNode node) {
		expanding = node;
		// Remove any children that may exist.
		killChildren(node);
		// Get the states for the correct splittage.
		ArrayList groups = minimizer.splitOnTerminal(node.getStates(), node
				.getTerminal(), getAutomaton(), getTree());
		Iterator it = groups.iterator();
		while (it.hasNext())
			addChild(node, (State[]) it.next());
		expanding = null;
	}

	/**
	 * This does the splitting of a state for you, asking for a terminal if
	 * necessary.
	 * 
	 * @param node
	 *            the node to split
	 */
	public void splitWithInput(MinimizeTreeNode node) {
		if (!canExpand(node))
			return;
		if (node.getTerminal().equals("")) {
			if (!minimizer.isSplittable(node.getStates(), getAutomaton(),
					getTree())) {
				JOptionPane.showMessageDialog(view, CANT_SPLIT);
				return;
			}
			// We need to get a terminal for this node!
			if (!splitOnTerminal(node))
				return;
		}
		split(node);
	}

	/**
	 * This does the splitting of a state for you, providing on its own a
	 * terminal if necessary.
	 * 
	 * @param node
	 *            the node to split
	 */
	public void splitWithoutInput(MinimizeTreeNode node) {
		if (!canExpand(node))
			return;
		if (node.getTerminal().equals("")) {
			if (!minimizer.isSplittable(node.getStates(), getAutomaton(),
					getTree())) {
				JOptionPane.showMessageDialog(view, CANT_SPLIT);
				return;
			}
			// We need to get a terminal for this node!
			node.setTerminal(minimizer.getTerminalToSplit(node.getStates(),
					getAutomaton(), getTree()));
		}
		split(node);
	}

	/**
	 * This does the splitting of all states in a tree for you.
	 * 
	 * @param root
	 *            the root of the subtree to split completely
	 */
	public void splitSubtree(MinimizeTreeNode root) {
		if (expanding != null) {
			JOptionPane.showMessageDialog(view,
					"We must finish expanding group "
							+ minimizer.getString(expanding.getStates())
							+ "\nbefore we expand anything else.");
		}
		TreeNode[] children = Trees.children(root);
		if (children.length == 0) {
			if (!minimizer.isSplittable(root.getStates(), getAutomaton(),
					getTree())) {
				root.setTerminal("");
				return;
			}
			root.setTerminal(minimizer.getTerminalToSplit(root.getStates(),
					getAutomaton(), getTree()));
			split(root);
			children = Trees.children(root);
		}
		for (int i = 0; i < children.length; i++)
			splitSubtree((MinimizeTreeNode) children[i]);
	}

	/**
	 * Changes the selection of states in the state drawer to be those states in
	 * the given node.
	 * 
	 * @param node
	 *            the node to get the states from
	 */
	private void setSelectedStates(MinimizeTreeNode node) {
		automatonDrawer.clearSelected();
		State[] states = node.getStates();
		for (int i = 0; i < states.length; i++)
			automatonDrawer.addSelected(states[i]);
		treeDrawer.clearSelected();
		treeDrawer.setSelected(node, true);
		view.repaint();
	}

	/**
	 * This method destroys all children of a particular node.
	 * 
	 * @param node
	 *            the node whose children must be destroyed
	 * @return the array of children removed
	 */
	private MinimizeTreeNode[] killChildren(MinimizeTreeNode node) {
		TreeNode[] children = Trees.children(node);
		MinimizeTreeNode[] toReturn = new MinimizeTreeNode[children.length];
		for (int i = 0; i < children.length; i++) {
			toReturn[i] = (MinimizeTreeNode) children[i];
			getTree().removeNodeFromParent(toReturn[i]);
		}
		return toReturn;
	}

	/**
	 * This method returns the tree this controller, eh, controls.
	 * 
	 * @return the default tree model
	 */
	private DefaultTreeModel getTree() {
		return (DefaultTreeModel) treeDrawer.getModel();
	}

	/**
	 * Returns the automaton that this controller is helping to minimize.
	 * 
	 * @return the automaton
	 */
	private FiniteStateAutomaton getAutomaton() {
		return (FiniteStateAutomaton) automatonDrawer.getAutomaton();
	}

	/**
	 * Adds a child to the currently expanding group.
	 */
	public MinimizeTreeNode addChild() {
		return addChild(expanding);
	}

	/**
	 * Adds a empty child to a node.
	 * 
	 * @param parent
	 *            the node to add a child to
	 * @return the node that was created, or <CODE>null</CODE> if the node
	 *         could not be created
	 */
	public MinimizeTreeNode addChild(MinimizeTreeNode parent) {
		if (parent.getStates().length <= parent.getChildCount()) {
			JOptionPane.showMessageDialog(view,
					"A group cannot have more partitions than elements!");
			return null;
		}
		return addChild(parent, new State[0]);
	}

	/**
	 * Adds a child to a node with the given group.
	 * 
	 * @param parent
	 *            the node to add a child to
	 * @param group
	 *            the group
	 * @return the node that was created, or <CODE>null</CODE> if the node
	 *         could not be created
	 */
	public MinimizeTreeNode addChild(MinimizeTreeNode parent, State[] group) {
		if (!canModifyChild(parent))
			return null;
		MinimizeTreeNode node = new MinimizeTreeNode(group);
		getTree().insertNodeInto(node, parent, parent.getChildCount());
		view.repaint();
		return node;
	}

	/**
	 * Adds an array of children to a node.
	 * 
	 * @param parent
	 *            the node to add children to
	 * @param children
	 *            the child to add
	 */
	private void addChildren(MinimizeTreeNode parent,
			MinimizeTreeNode[] children) {
		// Restore the children.
		for (int i = 0; i < children.length; i++)
			getTree().insertNodeInto((MinimizeTreeNode) children[i], parent,
					parent.getChildCount());
	}

	/**
	 * Removes a node.
	 * 
	 * @param node
	 *            the node to remove
	 */
	public void removeNode(MinimizeTreeNode node) {
		MinimizeTreeNode parent = (MinimizeTreeNode) node.getParent();
		if (parent == null) {
			JOptionPane.showMessageDialog(view, "One can't remove the root!");
			return;
		}
		if (!canModifyChild(parent))
			return;
		getTree().removeNodeFromParent(node);
		view.repaint();
	}

	/**
	 * Checks a node, which is by default the expanding group.
	 */
	public boolean check() {
		if (expanding == null)
			return false;
		return check(expanding);
	}

	/**
	 * Checks the splitting on a particular node to see if it is correct.
	 * Firstly, this will check the node to make sure if it is splittable; if
	 * not, then it is only correct if it has no children and the terminal is
	 * empty. If it is splittable, then it is only correct if it has a terminal,
	 * and if the partitions are correct for that terminal. The user will be
	 * presented with dialogs to indicate whatever condition was not met, or, if
	 * they are met, that the node is correct.
	 * 
	 * @param node
	 *            the node to check
	 * @return <CODE>true</CODE> if it is correct, <CODE>false</CODE> if it
	 *         is incorrect
	 */
	public boolean check(MinimizeTreeNode node) {
		MinimizeTreeNode[] children = killChildren(node);

		// Take care of case where it's not splittable.
		if (!minimizer
				.isSplittable(node.getStates(), getAutomaton(), getTree())) {
			if (node.getTerminal().equals("") && children.length == 0) {
				JOptionPane.showMessageDialog(view, "This group is correct!");
				addChildren(node, children);
				return true;
			}
			addChildren(node, children);
			JOptionPane.showMessageDialog(view,
					"This group is unsplittable, so it must\n"
							+ "have no terminal, and no partitions.");
			return false;
		}

		// If it is splittable, make sure no subpartitions are empty.
		HashSet userPartitions = new HashSet();
		for (int i = 0; i < children.length; i++) {
			MinimizeTreeNode child = (MinimizeTreeNode) children[i];
			if (child.getStates().length == 0) {
				addChildren(node, children);
				JOptionPane.showMessageDialog(view,
						"One of the partitions is empty!");
				return false;
			}
			// While we're at it...
			userPartitions.add(new HashSet(Arrays.asList(child.getStates())));
		}
		// Check to make sure that the partitions are the same.
		// Remove any children that may exist.
		HashSet realPartitions = new HashSet();
		// Remove the children so as not to confuse the minimizer.
		// killChildren(node);
		ArrayList groups = minimizer.splitOnTerminal(node.getStates(), node
				.getTerminal(), getAutomaton(), getTree());

		addChildren(node, children);

		Iterator it = groups.iterator();
		while (it.hasNext())
			realPartitions.add(new HashSet(Arrays.asList((State[]) it.next())));
		if (!realPartitions.equals(userPartitions)) {
			JOptionPane.showMessageDialog(view, "The parititons are wrong!");
			return false;
		}
		JOptionPane.showMessageDialog(view, "The expansion is correct!");
		expanding = null;
		return true;
	}

	/**
	 * Check if we're able to expand some node at this time.
	 * 
	 * @param node
	 *            the node we may expand
	 * @return <CODE>true</CODE> if we may expand the node, <CODE>false</CODE>
	 *         if we may not
	 */
	private boolean canExpand(MinimizeTreeNode node) {
		if (expanding == null && node.getChildCount() > 0) {
			JOptionPane.showMessageDialog(view,
					"This group has already been expanded.");
			return false;
		}
		if (expanding == null || expanding == node)
			return true;
		JOptionPane.showMessageDialog(view,
				"We're already expanding the group "
						+ minimizer.getString(expanding.getStates()) + "!");
		return false;
	}

	/**
	 * This should be called if we're seeking to modify the partitions of a
	 * group in some way. In the event that we cannot, and error message is
	 * displayed.
	 * 
	 * @param parent
	 *            the node that holds the group we're modifying partitions of,
	 *            or <CODE>null</CODE> if the child in question is the root of
	 *            the tree
	 * @return <CODE>true</CODE> if the parent's children can be added
	 *         to/deleted from, or changed in the states they hold, <CODE>false</CODE>
	 *         if they should not be
	 */
	private boolean canModifyChild(MinimizeTreeNode parent) {
		if (expanding == parent && parent != null)
			return true;
		if (parent == null) {
			JOptionPane.showMessageDialog(view, "The root cannot be changed!");
			return false;
		}
		JOptionPane.showMessageDialog(view,
				"We cannot modify the partitions of a"
						+ "\ngroup we're not expanding!"
						+ (expanding == null ? "" : "\nWe are expanding group "
								+ minimizer.getString(expanding.getStates())));
		return false;
	}

	/**
	 * Determines if we are finished with building the tree or not. All leaves
	 * will be examined. If the tree is not finished, a dialog box will be
	 * popped up saying which group can be expanded further.
	 * 
	 * @return <CODE>true</CODE> if the tree is done, <CODE>false</CODE> if
	 *         it is not
	 */
	public boolean finished() {
		if (expanding != null) {
			JOptionPane.showMessageDialog(view, "We are expanding group "
					+ minimizer.getString(expanding.getStates())
					+ "\nand so are not done.");
			return false;
		}

		State[] group = minimizer.getDistinguishableGroup(getAutomaton(),
				getTree());
		if (group == null) {
			/*
			 * JOptionPane.showMessageDialog(view, "The minimize tree is
			 * complete.");
			 */
			view.beginMinimizedAutomaton(getAutomaton(), getTree());
			return true;
		}
		JOptionPane.showMessageDialog(view, "The tree is unfinished.  Group "
				+ minimizer.getString(group) + " may be partitioned.");
		return false;
	}

	/** The component to repaint whenever something cool happens. */
	private MinimizePane view;

	/** The automaton selection drawer. */
	private SelectionDrawer automatonDrawer;

	/** The tree selection drawer. */
	private SelectTreeDrawer treeDrawer;

	/** The DFA minimizer. */
	private Minimizer minimizer;

	/**
	 * The current node being expanded, or null if no node is being expanded. If
	 * this is not null, that could indicate that the tree is quite probably in
	 * some sort of intermediate state.
	 */
	private MinimizeTreeNode expanding = null;

	/** The "not splittable" message. */
	private static final String CANT_SPLIT = "This group cannot be split on any terminal!";
}
