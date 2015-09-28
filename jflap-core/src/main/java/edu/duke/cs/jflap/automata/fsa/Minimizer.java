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





package edu.duke.cs.jflap.automata.fsa;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.tree.DefaultTreeModel;

import edu.duke.cs.jflap.automata.AlphabetRetriever;
import edu.duke.cs.jflap.automata.Automaton;
import edu.duke.cs.jflap.automata.AutomatonChecker;
import edu.duke.cs.jflap.automata.State;
import edu.duke.cs.jflap.automata.StatePlacer;
import edu.duke.cs.jflap.automata.Transition;
import edu.duke.cs.jflap.automata.UnreachableStatesDetector;

/**
 * The Minimizer object can be used to minimize a deterministic finite state
 * automaton. In order to use the Minimizer, you must call initializeMinimizer
 * before each minimization. Then you must get a compatible automaton (i.e. one
 * with no reachable states, and an added trap state if necessary) by calling
 * getMinimizeableAutomaton. You can then immediately get the tree of
 * distinugishable groups by calling getDistinguishableGroupsTree, or you can
 * build the tree yourself step by step by continually getting another
 * distinguishable group (i.e a group that can still be further split) by
 * calling getDistinguishableGroup. Then you call split on this group and it
 * will return the groups obtained from the split of the distinguishable group.
 * Or instead of calling split (since you will want to know what terminal in the
 * alphabet you can split the group on), you can call getTerminalToSplit on the
 * group and then call splitOnTerminal on that same group (this is essentially
 * what split does anyway). Or if the user tries to split a node, you can call
 * isSplittable to see if the group can be split further. Or if the user tries
 * to split a group on a particular terminal, you can call
 * isSplittableOnTerminal to see if the group can be split on the terminal. When
 * you call splitOnTerminal, it will return a list of new groups created from
 * splitting the old group. You can add these groups to your tree under the
 * distinguishable group. If you continue to call getDistinguishableGroup until
 * it returns null (or until a call to isMinimized returns false), you will have
 * split the groups as far as they can go. At this point, you should have a
 * complete tree of distinguishable groups. You can then use this tree to get
 * the minimum dfa by calling getMinimumDfa. Or you can build the dfa step by
 * step from the tree by first calling createStatesForMinimumDfa, which will
 * create a state for each of the distinguishable groups. Then you can expand
 * each of these states one by one by calling getTransitionsForState, which will
 * return a list of all transitions coming from the state. By adding all these
 * transitions to the minimum dfa, you will have successfully created the
 * minimum dfa.
 * 
 * @author Ryan Cavalcante
 */

public class Minimizer {
	/**
	 * Creates an instance of <CODE>Minimizer</CODE>.
	 */
	public Minimizer() {

	}

	/**
	 * Initializes the minimizer to begin a new minimization.
	 */
	public void initializeMinimizer() {
		MAP = new HashMap();
		// DISTINGUISHABLE_GROUPS = new ArrayList();
		TRAP_STATE = null;
	}

	/**
	 * Returns a string representation of <CODE>states</CODE>
	 * 
	 * @param states
	 *            the array of State objects.
	 * @return a string representation of <CODE>states</CODE>
	 */
	public String getString(State[] states) {
		if (states.length == 0)
			return "";
		StringBuffer buffer = new StringBuffer();
		for (int k = 0; k < states.length - 1; k++) {
			buffer.append(Integer.toString(states[k].getID()));
			buffer.append(",");
		}
		buffer.append(Integer.toString(states[states.length - 1].getID()));
		return buffer.toString();
	}

	/**
	 * Returns the terminal that <CODE>group</CODE> can be split on.
	 * 
	 * @param group
	 *            the group being split
	 * @param automaton
	 *            the automaton
	 * @return the terminal that <CODE>group</CODE> can be split on.
	 */
	public String getTerminalToSplit(State[] group, Automaton automaton,
			DefaultTreeModel tree) {
		AlphabetRetriever far = new FSAAlphabetRetriever();
		String[] alphabet = far.getAlphabet(automaton);
		for (int k = 0; k < alphabet.length; k++) {
			if (isSplittableOnTerminal(group, alphabet[k], automaton, tree)) {
				return alphabet[k];
			}
		}
		return null;
	}

	/**
	 * Returns true if <CODE>group</CODE> is splittable on <CODE>terminal</CODE>.
	 * 
	 * @param group
	 *            the group to split
	 * @param terminal
	 *            the terminal to split group on
	 * @param automaton
	 *            the automaton
	 * @return true if <CODE>group</CODE> is splittable on <CODE>terminal</CODE>.
	 */
	public boolean isSplittableOnTerminal(State[] group, String terminal,
			Automaton automaton, DefaultTreeModel tree) {
		/** if group goes to more than one group on terminal. */
		if (getGroupsFromGroupOnTerminal(group, terminal, automaton, tree)
				.size() > 1) {
			return true;
		}
		return false;
	}

	/**
	 * Returns true if <CODE>group</CODE> can be further split.
	 * 
	 * @param group
	 *            the group
	 * @param automaton
	 *            the automaton
	 * @return true if <CODE>group</CODE> can be further split.
	 */
	public boolean isSplittable(State[] group, Automaton automaton,
			DefaultTreeModel tree) {
		/** if only one state in group, can't be split. */
		if (group.length <= 1)
			return false;
		AlphabetRetriever far = new FSAAlphabetRetriever();
		String[] alphabet = far.getAlphabet(automaton);
		for (int k = 0; k < alphabet.length; k++) {
			/** if group splittable on a terminal in alphabet. */
			if (isSplittableOnTerminal(group, alphabet[k], automaton, tree)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the array of states that represent a distinguishable group (i.e.
	 * a group that can be further split/distinguished).
	 * 
	 * @param automaton
	 *            the automaton
	 * @return the array of States that represent a distinguishable group.
	 */
	public State[] getDistinguishableGroup(Automaton automaton,
			DefaultTreeModel tree) {
		MinimizeTreeNode root = (MinimizeTreeNode) tree.getRoot();
		ArrayList distinguishableGroups = getLeaves(tree, root);
		Iterator it = distinguishableGroups.iterator();
		while (it.hasNext()) {
			State[] group = (State[]) it.next();
			if (isSplittable(group, automaton, tree))
				return group;
		}
		return null;
	}

	/**
	 * Returns an array of states that represents a group, of which <CODE>state</CODE>
	 * is a member.
	 * 
	 * @param state
	 *            the state.
	 * @return an array of states that represents a group, of which <CODE>state</CODE>
	 *         is a member.
	 */
	public State[] getGroupForState(State state, DefaultTreeModel tree) {
		MinimizeTreeNode root = (MinimizeTreeNode) tree.getRoot();
		ArrayList distinguishableGroups = getLeaves(tree, root);
		Iterator it = distinguishableGroups.iterator();
		// Iterator it = DISTINGUISHABLE_GROUPS.iterator();
		while (it.hasNext()) {
			State[] group = (State[]) it.next();
			for (int k = 0; k < group.length; k++) {
				if (group[k] == state) {
					return group;
				}
			}
		}
		return null;
	}

	/**
	 * Returns true if <CODE>state</CODE> in <CODE>automaton</CODE> has a
	 * transition to one of the states in <CODE>group</CODE> on <CODE>terminal</CODE>.
	 * 
	 * @param state
	 *            the state.
	 * @param group
	 *            the set of states that state might have a transition to.
	 * @param terminal
	 *            the terminal
	 * @param automaton
	 *            the automaton.
	 * @return true if <CODE>state</CODE> in <CODE>automaton</CODE> has a
	 *         transition to one of the states in <CODE>group</CODE> on <CODE>terminal</CODE>.
	 */
	public boolean stateGoesToGroupOnTerminal(State state, State[] group,
			String terminal, Automaton automaton) {
		for (int k = 0; k < group.length; k++) {
			Transition[] transitions = automaton
					.getTransitionsFromStateToState(state, group[k]);
			for (int j = 0; j < transitions.length; j++) {
				FSATransition trans = (FSATransition) transitions[j];
				String label = trans.getLabel();
				if (label.equals(terminal)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Returns a list of groups (State[]) that <CODE>group</CODE> goes to on
	 * <CODE>terminal</CODE>.
	 * 
	 * @param group
	 *            the group
	 * @param terminal
	 *            the terminal
	 * @param automaton
	 *            the automaton
	 * @return a list of groups (State[]) that <CODE>group</CODE> goes to on
	 *         <CODE>terminal</CODE>.
	 */
	public ArrayList getGroupsFromGroupOnTerminal(State[] group,
			String terminal, Automaton automaton, DefaultTreeModel tree) {
		ArrayList list = new ArrayList();
		for (int k = 0; k < group.length; k++) {
			if (group[k].getAutomaton() != automaton)
				System.err.println("BADNESS!  BADNESS!");
			Transition[] transitions = automaton
					.getTransitionsFromState(group[k]);
			for (int j = 0; j < transitions.length; j++) {
				FSATransition trans = (FSATransition) transitions[j];
				if (trans.getLabel().equals(terminal)) {
					State[] node = getGroupForState(
							transitions[j].getToState(), tree);
					if (!list.contains(node)) {
						list.add(node);
					}
				}
			}
		}
		return list;
	}

	/**
	 * Returns a TreeNode object in <CODE>tree</CODE> whose user object is
	 * <CODE>group</CODE>.
	 * 
	 * @param tree
	 *            the tree model to search.
	 * @param root
	 *            the root of tree
	 * @param group
	 *            the object to look for in tree.
	 * @return a TreeNode object in <CODE>tree</CODE> whose user object is
	 *         <CODE>group</CODE>.
	 */
	// public DefaultMutableTreeNode getTreeNodeForObject
	// (DefaultTreeModel tree, DefaultMutableTreeNode root, State[] group) {
	public MinimizeTreeNode getTreeNodeForObject(DefaultTreeModel tree,
			MinimizeTreeNode root, State[] group) {
		State[] rootNode = (State[]) root.getUserObject();
		if (rootNode == group) {
			return root;
		}
		for (int k = 0; k < root.getChildCount(); k++) {
			MinimizeTreeNode node = getTreeNodeForObject(tree,
					(MinimizeTreeNode) root.getChildAt(k), group);
			// DefaultMutableTreeNode node =
			// getTreeNodeForObject(tree,
			// (DefaultMutableTreeNode) root.getChildAt(k), group);
			if (node != null)
				return node;
		}
		return null;
	}

	/**
	 * Returns list of groups (as State[]) created by split of <CODE>group</CODE>
	 * on <CODE>terminal</CODE>. Returns null if group is indistinguishable
	 * on <CODE>terminal</CODE>.
	 * 
	 * @param group
	 *            the group to split.
	 * @param terminal
	 *            the terminal to split group on.
	 * @param automaton
	 *            the automaton.
	 * @return list of groups (as State[]) that represent distinguishable groups
	 *         as subsets of <CODE>group</CODE>.
	 */
	public ArrayList splitOnTerminal(State[] group, String terminal,
			Automaton automaton, DefaultTreeModel tree) {
		ArrayList newGroups = new ArrayList();
		MinimizeTreeNode root = (MinimizeTreeNode) tree.getRoot();
		ArrayList distinguishableGroups = getLeaves(tree, root);
		Iterator it = distinguishableGroups.iterator();
		// Iterator it = DISTINGUISHABLE_GROUPS.iterator();
		while (it.hasNext()) {
			ArrayList statesInGroup = new ArrayList();
			State[] temp = (State[]) it.next();
			for (int i = 0; i < group.length; i++) {
				if (stateGoesToGroupOnTerminal(group[i], temp, terminal,
						automaton)) {
					statesInGroup.add(group[i]);
				}
			}
			if (statesInGroup.size() > 0) {
				State[] groupstates = (State[]) statesInGroup
						.toArray(new State[0]);
				newGroups.add(groupstates);
			}
		}

		return newGroups;
	}

	/**
	 * Returns true if <CODE>automaton</CODE> is minimized (i.e. there are no
	 * distinguishable groups).
	 * 
	 * @param automaton
	 *            the automaton.
	 * @return true if <CODE>automaton</CODE> is minimized
	 */
	public boolean isMinimized(Automaton automaton, DefaultTreeModel tree) {
		State[] states = getDistinguishableGroup(automaton, tree);
		return states == null;
	}

	/**
	 * Returns list of groups (as State[]) obtained by splitting <CODE>group</CODE>.
	 * 
	 * @param group
	 *            the group to split.
	 * @param automaton
	 *            the automaton.
	 * @return list of groups (as State[]) obtained by splitting <CODE>group</CODE>.
	 */
	public ArrayList split(State[] group, Automaton automaton,
			DefaultTreeModel tree) {
		String terminal = getTerminalToSplit(group, automaton, tree);
		ArrayList list = new ArrayList();
		list.addAll(splitOnTerminal(group, terminal, automaton, tree));
		return list;
	}

	/**
	 * Returns true if there is a transition in <CODE>transitions</CODE> with
	 * a label equal to <CODE>terminal</CODE>. This assumes that the
	 * transitions are actually FSATransition objects.
	 * 
	 * @param transitions
	 *            the set of transitions.
	 * @param terminal
	 *            the terminal.
	 * @return true if there is a transition on terminal.
	 */
	public boolean isTransitionOnTerminal(Transition[] transitions,
			String terminal) {
		for (int k = 0; k < transitions.length; k++) {
			FSATransition transition = (FSATransition) transitions[k];
			if (transition.getLabel().equals(terminal)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns true if <CODE>automaton</CODE> needs a trap state. (i.e. there
	 * is an implied trap state since there are not transitions on every letter
	 * in the alphabet for every state in <CODE>automaton</CODE>.)
	 * 
	 * @param automaton
	 *            the automaton.
	 * @return true if automaton needs a trap state.
	 */
	public boolean needsTrapState(Automaton automaton) {
		AlphabetRetriever far = new FSAAlphabetRetriever();
		String[] alphabet = far.getAlphabet(automaton);
		State[] states = automaton.getStates();
		for (int k = 0; k < states.length; k++) {
			Transition[] transitions = automaton
					.getTransitionsFromState(states[k]);
			for (int j = 0; j < alphabet.length; j++) {
				if (!isTransitionOnTerminal(transitions, alphabet[j])) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Adds a trap state to <CODE>automaton</CODE> and all implied transitions
	 * to that trap state. This actually alters the Automaton object itself.
	 * 
	 * @param automaton
	 *            the automaton.
	 */
	public void addTrapState(Automaton automaton) {
		if (!needsTrapState(automaton))
			return;

		StatePlacer sp = new StatePlacer();
		Point point = sp.getPointForState(automaton);
		State trapState = automaton.createState(point);
		TRAP_STATE = trapState;
		AlphabetRetriever far = new FSAAlphabetRetriever();
		String[] alphabet = far.getAlphabet(automaton);
		State[] states = automaton.getStates();
		for (int k = 0; k < states.length; k++) {
			Transition[] transitions = automaton
					.getTransitionsFromState(states[k]);
			for (int j = 0; j < alphabet.length; j++) {
				if (!isTransitionOnTerminal(transitions, alphabet[j])) {
					FSATransition trans = new FSATransition(states[k],
							trapState, alphabet[j]);
					automaton.addTransition(trans);
				}
			}
		}
	}

	/**
	 * Returns a minimizeable automaton obtained from <CODE>automaton</CODE>
	 * by removing all unreachable states and multiple character labels on
	 * transitions. This does not alter <CODE>automaton</CODE>.
	 * 
	 * @param a
	 *            the automaton
	 * @return a minimizeable automaton obtained from <CODE>automaton</CODE>
	 *         by removing all unreachable states and multiple character labels
	 *         on transitions.
	 */
	public Automaton getMinimizeableAutomaton(Automaton a) {
		// Automaton a = (Automaton) automaton.clone();
		AutomatonChecker ac = new AutomatonChecker();
		if (ac.isNFA(a))
			return null;
		/** Remove all unreachable states. */
		UnreachableStatesDetector usd = new UnreachableStatesDetector(a);
		State[] unreachableStates = usd.getUnreachableStates();
		for (int k = 0; k < unreachableStates.length; k++) {
			a.removeState(unreachableStates[k]);
		}
		/** Remove all multiple character labels. */
		FSALabelHandler.removeMultipleCharacterLabelsFromAutomaton(a);
		/** Remove the unnecessary states. */
		// StateCleaner.clean(a);
		/** Add trap state if necessary. */
		addTrapState(a);
		return a;
	}

	/**
	 * Prints the string representation of <CODE>treeNode</CODE> to stdout
	 * (assumes that treeNode stores a StateNode object).
	 * 
	 * @param treeNode
	 *            the node to print.
	 */
	// public void printNode(DefaultMutableTreeNode treeNode) {
	public void printNode(MinimizeTreeNode treeNode) {
		State[] node = (State[]) treeNode.getUserObject();
		System.out.print(getString(node));
		// //System.out.println(" TERMINAL: " + treeNode.getTerminal());
	}

	/**
	 * Prints the string representation of <CODE>tree</CODE> rooted at <CODE>root</CODE>
	 * to stdout. (assumes all nodes in tree contain StateNode objects).
	 * 
	 * @param tree
	 *            the tree to print.
	 * @param root
	 *            the root of tree.
	 */
	// public void printTree(DefaultTreeModel tree, DefaultMutableTreeNode root)
	// {
	public void printTree(DefaultTreeModel tree, MinimizeTreeNode root) {
		printNode(root);
		for (int k = 0; k < root.getChildCount(); k++) {
			MinimizeTreeNode child = (MinimizeTreeNode) root.getChildAt(k);
			// DefaultMutableTreeNode child =
			// (DefaultMutableTreeNode) root.getChildAt(k);
			printTree(tree, child);
		}
	}

	/**
	 * Returns the tree for <CODE>automaton</CODE> with the root representing
	 * all states in <CODE>automaton</CODE>, and then the root split into two
	 * children, one containing all final states in <CODE>automaton</CODE>,
	 * the other containing all nonfinal states in <CODE>automaton</CODE>.
	 * 
	 * @param automaton
	 *            the automaton being minimized.
	 * @return the tree of distinguishable groups with all states in <CODE>automaton</CODE>
	 *         split into groups of final and nonfinal states.
	 */
	public DefaultTreeModel getInitializedTree(Automaton automaton) {
		State[] states = automaton.getStates();

		// DefaultMutableTreeNode root = new DefaultMutableTreeNode(states);
		MinimizeTreeNode root = new MinimizeTreeNode(states);
		DefaultTreeModel tree = new DefaultTreeModel(root);

		ArrayList list = new ArrayList();
		for (int k = 0; k < states.length; k++) {
			if (!automaton.isFinalState(states[k]))
				list.add(states[k]);
		}
		State[] nonFinalStates = (State[]) list.toArray(new State[0]);

		int childIndex = 0;

		if (nonFinalStates.length > 0) {
			MinimizeTreeNode nfstates = new MinimizeTreeNode(nonFinalStates);
			// DISTINGUISHABLE_GROUPS.add(nonFinalStates);
			tree.insertNodeInto(nfstates, root, childIndex);
			childIndex++;
		}

		State[] finalStates = automaton.getFinalStates();
		// DefaultMutableTreeNode fstates =
		// new DefaultMutableTreeNode(finalStates);

		if (finalStates.length > 0) {
			MinimizeTreeNode fstates = new MinimizeTreeNode(finalStates);
			// DISTINGUISHABLE_GROUPS.add(finalStates);
			tree.insertNodeInto(fstates, root, childIndex);
		}

		return tree;
	}

	/**
	 * Creates tree nodes for each group of states (State[]) in <CODE>children</CODE>
	 * and places them in <CODE>tree</CODE> under <CODE>parent</CODE>.
	 * 
	 * @param children
	 *            the list of groups that were obtained by splitting the group
	 *            represented by parent
	 * @param parent
	 *            the parent node in the tree
	 * @param tree
	 *            the tree
	 */
	public void addChildrenToParent(ArrayList children,
			MinimizeTreeNode parent, DefaultTreeModel tree) {
		int index = 0;
		Iterator it = children.iterator();
		while (it.hasNext()) {
			State[] childGroup = (State[]) it.next();
			MinimizeTreeNode child = new MinimizeTreeNode(childGroup);
			// DefaultMutableTreeNode child =
			// new DefaultMutableTreeNode(childGroup);
			tree.insertNodeInto(child, parent, index);
			index++;
		}
	}

	/**
	 * Returns the tree model of the process of distinguishing groups of states
	 * in <CODE>automaton</CODE>.
	 * 
	 * @param automaton
	 *            the automaton.
	 * @return the tree model of the process of distinguishing groups of states
	 *         in <CODE>automaton</CODE>.
	 */
	public DefaultTreeModel getDistinguishableGroupsTree(Automaton automaton) {
		DefaultTreeModel tree = getInitializedTree(automaton);
		// DefaultMutableTreeNode root =
		// (DefaultMutableTreeNode) tree.getRoot();
		MinimizeTreeNode root = (MinimizeTreeNode) tree.getRoot();
		while (!isMinimized(automaton, tree)) {
			State[] group = getDistinguishableGroup(automaton, tree);
			ArrayList children = new ArrayList();
			String terminal = getTerminalToSplit(group, automaton, tree);
			children.addAll(splitOnTerminal(group, terminal, automaton, tree));
			// children.addAll(split(group, automaton));
			MinimizeTreeNode parent = getTreeNodeForObject(tree, root, group);
			parent.setTerminal(terminal);
			// DefaultMutableTreeNode parent =
			// getTreeNodeForObject(tree, root, group);
			addChildrenToParent(children, parent, tree);
		}

		return tree;
	}

	/**
	 * Returns true if <CODE>states</CODE> contains a state that is a final
	 * state in <CODE>automaton</CODE>.
	 * 
	 * @param states
	 *            the set of states.
	 * @param automaton
	 *            the automaton.
	 * @return true if <CODE>states</CODE> contains a state that is a final
	 *         state in <CODE>automaton</CODE>.
	 */
	public boolean hasFinalState(State[] states, Automaton automaton) {
		for (int k = 0; k < states.length; k++) {
			if (automaton.isFinalState(states[k]))
				return true;
		}
		return false;
	}

	/**
	 * Returns true if <CODE>states</CODE> contains the initial state of
	 * <CODE>automaton</CODE>.
	 * 
	 * @param states
	 *            the set of states.
	 * @param automaton
	 *            the automaton.
	 * @return true if <CODE>states</CODE> contains the initial state of
	 *         <CODE>automaton</CODE>.
	 */
	public boolean hasInitialState(State[] states, Automaton automaton) {
		State initialState = automaton.getInitialState();
		for (int k = 0; k < states.length; k++) {
			if (states[k] == initialState)
				return true;
		}
		return false;
	}

	/**
	 * Maps <CODE>state</CODE> to <CODE>states</CODE>
	 * 
	 * @param state
	 *            the state
	 * @param group
	 *            the group of states.
	 */
	public void mapStateToGroup(State state, State[] group) {
		MAP.put(state, group);
	}

	/**
	 * Returns the group (State[]) mapped to <CODE>state</CODE>.
	 * 
	 * @param state
	 *            the state
	 * @return the group (State[]) mapped to <CODE>state</CODE>.
	 */
	public State[] getGroupMappedToState(State state) {
		return (State[]) MAP.get(state);
	}

	/**
	 * Creates states in <CODE>minDfa</CODE> for each group at a leaf in
	 * <CODE>tree</CODE>.
	 * 
	 * @param dfa
	 *            the dfa being minimized
	 * @param minDfa
	 *            the minimized form of <CODE>dfa</CODE>.
	 * @param tree
	 *            the distinguishable groups tree for the automaton being
	 *            minimized.
	 */
	public void createStatesForMinimumDfa(Automaton dfa, Automaton minDfa,
			DefaultTreeModel tree) {
		MinimizeTreeNode root = (MinimizeTreeNode) tree.getRoot();
		// DefaultMutableTreeNode root =
		// (DefaultMutableTreeNode) tree.getRoot();
		ArrayList groups = getLeaves(tree, root);
		StatePlacer sp = new StatePlacer();
		Iterator it = groups.iterator();
		while (it.hasNext()) {
			State[] group = (State[]) it.next();
			if (!containsTrapState(group)) {
				Point point = sp.getPointForState(minDfa);
				State state = minDfa.createState(point);
				state.setLabel(getString(group));
				if (hasInitialState(group, dfa))
					minDfa.setInitialState(state);
				if (hasFinalState(group, dfa))
					minDfa.addFinalState(state);
				mapStateToGroup(state, group);
			}
		}
	}

	/**
	 * Returns the state from <CODE>minDfa</CODE> that is mapped to <CODE>group</CODE>
	 * 
	 * @param group
	 *            the group of states
	 * @param minDfa
	 *            the minimized dfa
	 * @return the state from <CODE>minDfa</CODE> that is mapped to <CODE>group</CODE>
	 */
	public State getStateMappedToGroup(State[] group, Automaton minDfa) {
		State[] states = minDfa.getStates();
		for (int k = 0; k < states.length; k++) {
			State[] tempGroup = getGroupMappedToState(states[k]);
			if (tempGroup == group)
				return states[k];
		}
		return null;
	}

	/**
	 * Returns a list of all transitions from <CODE>state</CODE> in <CODE>minDfa</CODE>,
	 * based on <CODE>dfa</CODE>.
	 * 
	 * @param state
	 *            the state in the minimized dfa.
	 * @param minDfa
	 *            the minimized dfa.
	 * @param dfa
	 *            the dfa being minimized.
	 */
	public ArrayList getTransitionsForState(State state, Automaton minDfa,
			Automaton dfa, DefaultTreeModel tree) {
		ArrayList list = new ArrayList();
		State[] group = getGroupMappedToState(state);
		State stateInGroup = group[0];
		Transition[] transitions = dfa.getTransitionsFromState(stateInGroup);
		for (int k = 0; k < transitions.length; k++) {
			FSATransition trans = (FSATransition) transitions[k];
			State toState = trans.getToState();
			State[] toGroup = getGroupForState(toState, tree);
			if (!containsTrapState(toGroup)) {
				State to = getStateMappedToGroup(toGroup, minDfa);

				Transition transition = new FSATransition(state, to, trans
						.getLabel());
				list.add(transition);
			}
		}
		return list;
	}

	/**
	 * Returns a list of State[] objects that are contained in the leaves of
	 * <CODE>tree</CODE>
	 * 
	 * @param tree
	 *            the tree
	 * @param root
	 *            the root of <CODE>tree</CODE>
	 * @return a list of State[] objects that are contained in the leaves of
	 *         <CODE>tree</CODE>
	 */
	// public ArrayList getLeaves(DefaultTreeModel tree,
	// DefaultMutableTreeNode root) {
	public ArrayList getLeaves(DefaultTreeModel tree, MinimizeTreeNode root) {
		ArrayList list = new ArrayList();
		if (tree.isLeaf(root)) {
			State[] group = (State[]) root.getUserObject();
			list.add(group);
		}
		for (int k = 0; k < root.getChildCount(); k++) {
			MinimizeTreeNode child = (MinimizeTreeNode) root.getChildAt(k);
			// DefaultMutableTreeNode child =
			// (DefaultMutableTreeNode) root.getChildAt(k);
			list.addAll(getLeaves(tree, child));
		}
		return list;
	}

	/**
	 * Returns true if <CODE>states</CODE> contains the trap state
	 * 
	 * @param states
	 *            the states.
	 * @return true if <CODE>states</CODE> contains the trap state
	 */
	public boolean containsTrapState(State[] states) {
		for (int k = 0; k < states.length; k++) {
			if (states[k] == TRAP_STATE)
				return true;
		}
		return false;
	}

	/**
	 * Returns the minimized version of <CODE>automaton</CODE>, for which
	 * <CODE>tree</CODE> is the tree of distinguishable groups.
	 * 
	 * @param automaton
	 *            the automaton being minimized
	 * @param tree
	 *            the tree of distinguishable groups for automaton
	 * @return the minimized version of <CODE>automaton</CODE>, for which
	 *         <CODE>tree</CODE> is the tree of distinguishable groups.
	 */
	public FiniteStateAutomaton getMinimumDfa(Automaton automaton,
			DefaultTreeModel tree) {
		// if(isMinimized(automaton))
		// return (FiniteStateAutomaton) automaton.clone();
		FiniteStateAutomaton minDfa = new FiniteStateAutomaton();
		createStatesForMinimumDfa(automaton, minDfa, tree);

		ArrayList list = new ArrayList();
		State[] states = minDfa.getStates();
		for (int k = 0; k < states.length; k++) {
			list.addAll(getTransitionsForState(states[k], minDfa, automaton,
					tree));
		}

		Iterator it = list.iterator();
		while (it.hasNext()) {
			Transition transition = (Transition) it.next();
			minDfa.addTransition(transition);
		}
		return minDfa;
	}

	/**
	 * the list of distinguishable groups at a given moment in the process of
	 * determining distinguishable groups.
	 */
	// protected ArrayList DISTINGUISHABLE_GROUPS;
	/**
	 * map of states in minimum dfa to groups of states from non-minimized dfa.
	 */
	protected HashMap MAP;

	/** the trap state added to dfa in order to minimize. */
	protected State TRAP_STATE;
}
