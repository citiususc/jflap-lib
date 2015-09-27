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
import edu.duke.cs.jflap.gui.environment.Universe;

import java.util.*;

/**
 * This is the parse controller for an LL parse pane.
 * 
 * @author Thomas Finley
 */

class LLParseController {
	/**
	 * Instantiates a new LL parse controller.
	 * 
	 * @param pane
	 *            the LL parse pane
	 */
	public LLParseController(LLParsePane pane) {
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
		dehighlight();
		ArrayList nodes = new ArrayList();
		tree = parseTree(string, pane.grammar, pane.table, nodes);
		pane.treeDrawer.setModel(tree);
		pane.treeDrawer.hideAll();
		pane.treePanel.repaint();
		pane.stepAction.setEnabled(true);
		pane.derivationModel.setRowCount(0);
		// Initialize those global structures! :)
		NODES = (TreeNode[]) nodes.toArray(new TreeNode[0]);
		STRING = string + "$";
		STACK = new Stack();
		P = 0;
		NODECOUNT = 0;
		stepMode = INITIALIZE;
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
		return sb.toString();
	}

	/**
	 * The step function. Yay!
	 */
	public void step() {
		String read = STRING.substring(P, P + 1);
		switch (stepMode) {
		case INITIALIZE:
			STACK.push(NODES[0]); // Push the root.
			pane.treeDrawer.show(NODES[0]);
			pane.treePanel.repaint();
			NODECOUNT++;
			stepMode = NORMAL;
			updateStatus();
			pane.statusDisplay.setText("Initialization complete.");
			derivationString = pane.grammar.getStartVariable();
			pane.derivationModel.addRow(new String[] { "", derivationString });
			break;
		case NORMAL:
			dehighlight();
			if (STACK.empty()) {
				stepMode = SUCCESS;
				step();
				return;
			}
			String top = STACK.peek().toString();
			if (pane.grammar.isTerminal(top)) {
				if (top.equals(read)) {
					TreeNode node = (TreeNode) STACK.pop();
					pane.nodeDrawer.clearSelected();
					pane.nodeDrawer.setSelected(node, true);
					pane.treePanel.repaint();
					P++;
					pane.statusDisplay.setText("Matched " + read + ".");
				} else {
					stepMode = ERROR;
					pane.statusDisplay.setText("Stack and input don't match.");
				}
				updateStatus();
				return;
			}

			if (pane.grammar.isVariable(top)) {
				TreeNode node = (TreeNode) STACK.pop();
				pane.nodeDrawer.clearSelected();
				pane.nodeDrawer.setSelected(node, true);
				pane.treePanel.repaint();

				ENTRY = get(top, read);
				if (ENTRY == null) {
					stepMode = ERROR;
					pane.statusDisplay.setText("No rule for " + top + " with "
							+ read + " as lookahead.");
					updateStatus();
					return;
				}
				highlight(top, read);
				// Now the derivation table garbage.
				String rule = (new Production(top, ENTRY)).toString();
				int first = derivationString.indexOf(top.charAt(0));
				derivationString = derivationString.substring(0, first) + ENTRY
						+ derivationString.substring(first + 1);
				pane.derivationModel.addRow(new String[] { rule,
						derivationString });
				// What? About? Lambda?
				if (ENTRY.length() == 0)
					ENTRY = Universe.curProfile.getEmptyString();
				ENTRYP = ENTRY.length() - 1;
				pane.statusDisplay.setText("Replacing " + top + " with "
						+ ENTRY + ".");

				stepMode = REPLACING;
			}
			updateStatus();
			return;
		case REPLACING:
			if (ENTRYP < 0) {
				stepMode = NORMAL;
				step();
				return;
			}
			TreeNode node = NODES[NODECOUNT++];
			pane.treeDrawer.show(node);
			pane.treePanel.repaint();
			if (!node.toString().equals(Universe.curProfile.getEmptyString()))
				STACK.push(node);
			ENTRYP--;
			updateStatus();
			return;
		case ERROR:
			dehighlight();
			pane.statusDisplay.setText("String rejected.");
			pane.stepAction.setEnabled(false);
			return;
		case SUCCESS:
			dehighlight();
			// The stack may be empty... but is it really correct?
			if (!read.equals("$")) {
				pane.statusDisplay
						.setText("The stack is empty, but the input is not.");
				stepMode = ERROR;
			} else {
				pane.stepAction.setEnabled(false);
				pane.statusDisplay.setText("String successfully parsed!");
			}
			return;
		default:

		}
	}

	String ENTRY;

	int ENTRYP;

	/**
	 * Hightlights the cell in the parse table indexed by a variable and
	 * terminal.
	 * 
	 * @param id
	 *            the state id
	 * @param symbol
	 *            the grammar symbol
	 */
	private void highlight(String variable, String terminal) {
		int row = pane.table.getRow(variable);
		int column = pane.table.getColumn(terminal);
		pane.tablePanel.highlight(row, column);
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
		pane.tablePanel.repaint();
		pane.grammarTable.repaint();
	}

	/**
	 * Returns the rule for a particular lookahead and variable of the LL parse
	 * table.
	 * 
	 * @param variable
	 *            the variable to look under
	 * @param lookahead
	 *            the lookahead to look under
	 * @return the rule for the grammar, or <CODE>null</CODE> if no such entry
	 *         exists
	 */
	private String get(String variable, String lookahead) {
		try {
			return (String) pane.table.get(variable, lookahead).first();
		} catch (IllegalArgumentException e) {
			return null;
		} catch (NoSuchElementException e) {
			return null;
		}
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
	 * @return the parse tree
	 */
	private DefaultTreeModel parseTree(String string, Grammar grammar,
			LLParseTable table, List nodes) {
		int p = 0;
		string = string + "$";
		Stack stack = new Stack();
		MutableTreeNode root = new DefaultMutableTreeNode(grammar
				.getStartVariable());
		stack.push(root);
		nodes.add(root);
		DefaultTreeModel tree = new DefaultTreeModel(root);
		String read = string.substring(p, p + 1);
		p++;
		while (!stack.empty()) {
			String top = stack.peek().toString();
			if (pane.grammar.isTerminal(top)) {
				if (top.equals(read)) {
					stack.pop();
					read = string.substring(p, p + 1);
					p++;
				} else {
					return tree;
				}
			} else if (pane.grammar.isVariable(top)) {
				String entry = get(top, read);
				if (entry == null) {
					return tree;
				} else {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) stack
							.pop();
					if (entry.length() == 0) {
						MutableTreeNode child = new DefaultMutableTreeNode(
								Universe.curProfile.getEmptyString());
						node.insert(child, 0);
						nodes.add(child);
					} else {
						for (int i = entry.length() - 1; i >= 0; i--) {
							MutableTreeNode child = new DefaultMutableTreeNode(
									entry.substring(i, i + 1));
							node.insert(child, 0);
							stack.push(child);
							nodes.add(child);
						}
					}
				}
			} else {
				// This should never happen.
			}
		}
		return tree;
	}

	// VARIABLES FOR THE PARSING STEPPING
	// These would be local variables in a parse function...
	private Production[] productions;

	private int P;

	private String STRING;

	private int NODECOUNT;

	private Stack STACK;

	private TreeNode[] NODES;

	/** The current derivation string. */
	private String derivationString;

	/** The parse pane. */
	private LLParsePane pane;

	/** The parse tree. */
	private DefaultTreeModel tree;

	/** The array of nodes as they are added. */
	private TreeNode[] nodes;

	/** The current mode for the step function. */
	private int stepMode = INITIALIZE;

	/** The modes for the step function. */
	private static final int INITIALIZE = 1, NORMAL = 2, REPLACING = 3,
			ERROR = 4, SUCCESS = 5;
}
