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





package edu.duke.cs.jflap.gui.grammar.parse;

import javax.swing.tree.*;
import edu.duke.cs.jflap.grammar.parse.*;
import edu.duke.cs.jflap.grammar.*;
import java.util.*;

import edu.duke.cs.jflap.gui.environment.Universe;
import edu.duke.cs.jflap.gui.tree.Trees;
import javax.swing.JOptionPane;

/**
 * This is the parse controller for an LR parse pane.
 * 
 * @author Thomas Finley
 */

class LRParseController {
	/**
	 * Instantiates a new LR parse controller.
	 * 
	 * @param pane
	 *            the LR parse pane
	 */
	public LRParseController(LRParsePane pane) {
		this.pane = pane;
		productions = pane.grammar.getProductions();
	}

	/**
	 * Sets up for new input.
	 * 
	 * @param string
	 *            the new string to parse
	 */
	public void initialize(String string) {
		DefaultTreeModel oldTree = tree;
		tree = parseTree(string, pane.grammar, pane.table);
		if (tree == null) {
			tree = oldTree;
			pane.statusDisplay.setText("Preliminary parse failed.");
			return;
		}
		dehighlight();
		pane.treeDrawer.setModel(tree);
		pane.treeDrawer.hideAll();
		pane.treePanel.repaint();
		pane.stepAction.setEnabled(true);
		pane.derivationModel.setRowCount(0);
		pane.derivationModel.addRow(new String[] { "", string });
		reduceStep = 0;
		// Initialize those global structures! :)
		STRING = string + "$";
		P = 0;
		STACK = new IntStack();
		NODECOUNT = 0;
		// STACK.push("$");
		STACK.push(0);
		updateStatus();
		pane.statusDisplay.setText("Press step to begin.");
	}

	/**
	 * Sets the current stack and remaining input displays in the view to
	 * reflect current realities.
	 */
	private void updateStatus() {
		pane.stackDisplay.setText(stackString());
		pane.inputDisplay.setText(STRING.substring(P));
	}

	/**
	 * Returns the stack string.
	 */
	private String stackString() {
		Object[] o = STACK.toArray();
		StringBuffer sb = new StringBuffer();
		for (int i = o.length - 1; i >= 0; i--)
			sb.append(o[i]);
		return sb.toString();
	}

	/**
	 * Returns the current string derivation.
	 */
	private String derivationString() {
		StringBuffer sb = new StringBuffer();
		for (int i = 1; i < STACK.size(); i += 2)
			sb.append(STACK.get(i));
		sb.append(STRING.substring(P, STRING.length() - 1));
		return sb.toString();
	}

	/**
	 * The step function. Yay!
	 */
	public void step() {
		dehighlight();
		int state = STACK.peekInt();
		if (reduceStep == 1)
			state = reduceState;
		String read = "" + STRING.charAt(P);
		String entry = "";
		try {
			entry = pane.table.getValueAt(state, read);
		} catch (IllegalArgumentException e) {

		}
		if (entry.length() == 0 || Character.isDigit(entry.charAt(0))) {
			// Error! No derivation.
			pane.stepAction.setEnabled(false);
			pane.statusDisplay.setText("String rejected");
		} else if (entry.charAt(0) == 's') {
			// Shift!
			int newState = Integer.parseInt(entry.substring(1));
			STACK.push(read); // Push the symbol.
			STACK.push(newState);
			P++; // Move to next input symbol.
			// Show the tree node.
			pane.treeDrawer.show(nodes[NODECOUNT++]);
			pane.treePanel.repaint();
			pane.statusDisplay.setText("Shifting " + read);
			highlight(state, read);
		} else if (entry.charAt(0) == 'r') {
			// Reduce!
			int prodNumber = Integer.parseInt(entry.substring(1));
			Production red = productions[prodNumber];
			String displayString = "Reducing by " + red + ", ";
			highlight(prodNumber);
			if (reduceStep == 0) {
				// We're in the 'pop' substep of reduce.
				highlight(state, read);
				// Take care of case where this is a lambda node.
				TreeNode[] children = Trees.children(nodes[NODECOUNT]);
				if (children.length == 1
						&& !pane.treeDrawer.isVisible(children[0])) {
					pane.statusDisplay.setText(displayString);
					pane.treeDrawer.show(children[0]);
					pane.treePanel.repaint();
					// updateStatus();
					// return;
				}
				// Past lambda case.
				String popped = "";
				for (int i = 0; i < red.getRHS().length(); i++) {
					STACK.pop(); // Pops the state.
					popped = STACK.pop() + popped; // Pops the symbol.
				}
				if (popped.length() == 0)
					popped = Universe.curProfile.getEmptyString();
				displayString += popped + " popped off stack";
				reduceStep = 1;
				reduceState = state; // Save that state.
			} else if (reduceStep == 1) {
				// We're in the 'push' substep of reduce.
				// highlight(state, read);
				state = STACK.peekInt();
				highlight(state, red.getLHS());
				displayString += red.getLHS() + " pushed on stack";
				STACK.push(red.getLHS());
				STACK.push(Integer.parseInt(pane.table.getValueAt(state, red
						.getLHS())));
				pane.treeDrawer.show(nodes[NODECOUNT++]);
				pane.derivationModel.addRow(new String[] { red.toString(),
						derivationString() });
				reduceStep = 0;
			}
			pane.statusDisplay.setText(displayString);
			pane.treePanel.repaint();
		} else if (entry.charAt(0) == 'a') {
			STACK.pop();
			pane.stepAction.setEnabled(false);
			pane.statusDisplay.setText("String accepted");
			highlight(state, read);
		}
		updateStatus();
	}

	/**
	 * Hightlights the cell in the parse table indexed by the state ID and
	 * grammar symbol.
	 * 
	 * @param id
	 *            the state id
	 * @param symbol
	 *            the grammar symbol
	 */
	private void highlight(int id, String symbol) {
		int column = pane.table.columnForSymbol(symbol);
		pane.tablePanel.highlight(id, column);
		pane.tablePanel.repaint();
		pane.grammarTable.repaint();
	}

	/**
	 * Highlights a row in the grammar table.
	 * 
	 * @param row
	 *            the row to highlight
	 */
	private void highlight(int row) {
		pane.grammarTable.highlight(row, 0);
		// pane.grammarTable.highlight(row,1);
		pane.grammarTable.highlight(row, 2);
		pane.tablePanel.repaint();
		pane.grammarTable.repaint();
	}

	/**
	 * Dehighlights stuff.
	 */
	private void dehighlight() {
		pane.tablePanel.dehighlight();
		pane.grammarTable.dehighlight();
	}

	/**
	 * This action will perform parsing of a string.
	 * 
	 * @param string
	 *            the string to parse
	 * @param grammar
	 *            the augmented grammar
	 * @param table
	 *            the parse table
	 * @return the parse tree, or <CODE>null</CODE> if the user cancelled due
	 *         to it taking too long
	 */
	private DefaultTreeModel parseTree(String string, Grammar grammar,
			LRParseTable table) {
		string = string + "$";
		int p = 0;
		int numberOfIterations = 0, numberTillNextWarning = 500;
		IntStack stack = new IntStack();
		stack.push(0);
		Production[] productions = grammar.getProductions();
		ArrayList nodeList = new ArrayList();
		while (true) {
			int state = stack.peekInt();
			String read = "" + string.charAt(p);
			String entry = "";
			try {
				entry = table.getValueAt(state, read);
			} catch (IllegalArgumentException e) {

			}
			if (entry.length() == 0 || Character.isDigit(entry.charAt(0))) {
				// Error! No derivation.
				DefaultMutableTreeNode node = new DefaultMutableTreeNode();
				Object[] elements = stack.toArray();
				for (int i = 0; i < elements.length; i++)
					if (elements[i] instanceof MutableTreeNode)
						node.add((MutableTreeNode) elements[i]);
				nodes = (TreeNode[]) nodeList.toArray(new TreeNode[0]);
				return new DefaultTreeModel(node);
			} else if (entry.charAt(0) == 's') {
				// Shift!
				TreeNode node = new DefaultMutableTreeNode(read, false);
				stack.push(node); // Push the symbol.
				nodeList.add(node);
				stack.push(Integer.parseInt(entry.substring(1)));
				p++; // Move to next input symbol.
			} else if (entry.charAt(0) == 'r') {
				// Reduce!
				int prodNumber = Integer.parseInt(entry.substring(1));
				Production red = productions[prodNumber];
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(red
						.getLHS());
				for (int i = 0; i < red.getRHS().length(); i++) {
					stack.pop(); // Pops the state.
					MutableTreeNode c = (MutableTreeNode) stack.pop();
					node.insert(c, 0);
					// Pops the symbol.
				}
				if (red.getRHS().length() == 0) {
					// Lambda.
					MutableTreeNode lambda = new DefaultMutableTreeNode(
							Universe.curProfile.getEmptyString());
					node.insert(lambda, 0);
				}
				state = stack.peekInt();
				stack.push(node);
				stack.push(Integer.parseInt(table.getValueAt(state, red
						.getLHS())));
				nodeList.add(node);
			} else if (entry.charAt(0) == 'a') {
				stack.pop();
				nodes = (TreeNode[]) nodeList.toArray(new TreeNode[0]);
				return new DefaultTreeModel((TreeNode) stack.pop());
			}
			// It is possible for an LR parse table to be built that
			// will have an infinite loop under some inputs. In this
			// case it would be nice if we could get out of this
			// somehow.
			numberOfIterations++;
			if (numberOfIterations >= numberTillNextWarning) {
				int result = JOptionPane
						.showConfirmDialog(
								pane,
								numberOfIterations
										+ " nodes have been generated.  Should we continue?");
				if (result == JOptionPane.NO_OPTION)
					return null;
				numberTillNextWarning *= 2;
			}
		}
	}

	// VARIABLES FOR THE PARSING STEPPING
	// These would be local variables in a parse function...
	private IntStack STACK;

	private Production[] productions;

	private int P;

	private String STRING;

	private int NODECOUNT;

	private int reduceStep = 0, reduceState;

	/** The parse pane. */
	private LRParsePane pane;

	/** The parse tree. */
	private DefaultTreeModel tree;

	/** The array of nodes as they are added. */
	private TreeNode[] nodes;

	private static class IntStack extends Stack {
		int push(int item) {
			push(new Integer(item));
			return item;
		}

		int popInt() {
			return ((Integer) pop()).intValue();
		}

		int peekInt() {
			return ((Integer) peek()).intValue();
		}
	}
}
