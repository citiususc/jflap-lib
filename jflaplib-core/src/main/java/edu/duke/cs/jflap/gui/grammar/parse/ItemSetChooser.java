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

import edu.duke.cs.jflap.grammar.*;
import edu.duke.cs.jflap.grammar.parse.*;
import edu.duke.cs.jflap.gui.SuperMouseAdapter;
import edu.duke.cs.jflap.gui.grammar.GrammarTable;
import edu.duke.cs.jflap.gui.grammar.GrammarTableModel;
import edu.duke.cs.jflap.gui.grammar.ImmutableGrammarTableModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

/**
 * This allows a user to specify sets of items.
 * 
 * @author Thomas Finley
 */

public class ItemSetChooser {
	/**
	 * Instantiates a new item set chooser for a grammar.
	 * 
	 * @param grammar
	 *            the grammar to create the item set chooser for
	 */
	public ItemSetChooser(Grammar grammar, Component parent) {
		this.grammar = grammar;
		this.parent = parent;
		chooseTable = new GrammarTable(new ImmutableGrammarTableModel(grammar));
		chooseTable.addMouseListener(GTListener);
		choiceTable = new GrammarTable(new ImmutableGrammarTableModel());
		JScrollPane p = new JScrollPane(chooseTable);
		p.setPreferredSize(new Dimension(200, 200));
		panel.add(p, BorderLayout.WEST);
		p = new JScrollPane(choiceTable);
		p.setPreferredSize(new Dimension(200, 200));
		panel.add(p, BorderLayout.EAST);
		// Set up the tool bar.
		JToolBar bar = new JToolBar();
		bar.add(new AbstractAction("Closure") {
			public void actionPerformed(ActionEvent e) {
				closure();
			}
		});
		bar.add(new AbstractAction("Finish") {
			public void actionPerformed(ActionEvent e) {
				finish();
			}
		});
		panel.add(bar, BorderLayout.NORTH);
	}

	/**
	 * This will add the closure of all selected items in choice to the choice.
	 */
	private void closure() {
		HashSet selected = new HashSet();
		GrammarTableModel model = choiceTable.getGrammarModel();
		for (int i = 0; i < model.getRowCount() - 1; i++)
			if (choiceTable.isRowSelected(i))
				selected.add(model.getProduction(i));
		if (selected.size() == 0) {
			JOptionPane.showMessageDialog(parent,
					"Select an item (or items) in the right table.",
					"Nothing Selected", JOptionPane.ERROR_MESSAGE);
			return;
		}
		Set closureSet = Operations.closure(grammar, selected);
		closureSet.removeAll(alreadyChosen);
		Iterator it = closureSet.iterator();
		while (it.hasNext())
			addItem((Production) it.next());
	}

	/**
	 * This will complete the items in choice.
	 */
	private void finish() {
		if (restricted == null) {
			JOptionPane.showMessageDialog(parent,
					"There is no one right answer in this case.", "Ambiguity",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		HashSet toAdd = new HashSet(restricted);
		toAdd.removeAll(alreadyChosen);
		Iterator it = toAdd.iterator();
		while (it.hasNext())
			addItem((Production) it.next());
	}

	/**
	 * This will bring up a dialog box allowing a user to specify item sets.
	 * 
	 * @param items
	 *            the target item set in the event that the user is being asked
	 *            to give some information that is already known, or <CODE>null</CODE>
	 *            if there is no prearranged target
	 * @param message
	 *            a small message to display to the user
	 * @return an array containing the items the user selected, or <CODE>null</CODE>
	 *         if the user cancelled the action
	 */
	public Production[] getItemSet(Set items, String message) {
		restricted = items;
		choiceTable.setModel(new ImmutableGrammarTableModel());
		alreadyChosen = new HashSet();
		while (true) {
			int choice = JOptionPane.showConfirmDialog(parent, panel, message,
					JOptionPane.OK_CANCEL_OPTION);
			if (choice == JOptionPane.CANCEL_OPTION)
				return null;
			// Get those selected.
			List selected = new ArrayList();
			GrammarTableModel model = choiceTable.getGrammarModel();
			for (int i = 0; i < model.getRowCount() - 1; i++)
				selected.add(model.getProduction(i));
			// Check if it's our target.
			if (items != null) {
				Set selectedSet = new HashSet(selected);
				if (!selectedSet.equals(items)) {
					JOptionPane.showMessageDialog(parent,
							"Some items are missing!", "Items Missing",
							JOptionPane.ERROR_MESSAGE);
					continue;
				}
			}
			return (Production[]) selected.toArray(new Production[0]);
		}
	}

	/**
	 * This method should be called when an item should be added.
	 * 
	 * @param item
	 *            the item that was selected for addition
	 */
	private void addItem(Production item) {
		if (restricted != null && !restricted.contains(item)) {
			JOptionPane.showMessageDialog(parent, item.toString()
					+ " is not part of the set.", "Item not Desirable",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (alreadyChosen.contains(item)) {
			JOptionPane.showMessageDialog(parent, item.toString()
					+ " is already chosen.", "Item Already Chosen",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		alreadyChosen.add(item);
		choiceTable.getGrammarModel().addProduction(item);
	}

	private final GrammarTableListener GTListener = new GrammarTableListener();

	private class GrammarTableListener extends SuperMouseAdapter {
		public void mouseClicked(MouseEvent event) {
			GrammarTable gt = (GrammarTable) event.getSource();
			Point at = event.getPoint();
			int row = gt.rowAtPoint(at);
			if (row == -1)
				return;
			if (row == gt.getGrammarModel().getRowCount() - 1)
				return;
			Production p = gt.getGrammarModel().getProduction(row);
			Production[] pItems = Operations.getItems(p);
			JPopupMenu menu = new JPopupMenu();
			ItemMenuListener itemListener = new ItemMenuListener(p);
			for (int i = 0; i < pItems.length; i++) {
				JMenuItem item = new JMenuItem(pItems[i].toString());
				item.setActionCommand(pItems[i].getRHS());
				item.addActionListener(itemListener);
				menu.add(item);
			}
			menu.show(gt, at.x, at.y);
		}
	}

	private class ItemMenuListener implements ActionListener {
		public ItemMenuListener(Production p) {
			prod = p;
		}

		public void actionPerformed(ActionEvent event) {
			String rhs = event.getActionCommand();
			Production p = new Production(prod.getLHS(), rhs);
			addItem(p);
		}

		Production prod;
	}

	/** The parent for dialog boxes. */
	private Component parent;

	/** This is the pane for the grammar. */
	private JPanel panel = new JPanel(new BorderLayout());

	/** Productions we can choose items from. */
	private GrammarTable chooseTable;

	/** What items we have already chosen. */
	private GrammarTable choiceTable;

	/**
	 * Items able to be added are restricted to this set. If null, there are no
	 * restrictions.
	 */
	private Set restricted = null;

	/** The items that have been added sofar are listed here. */
	private Set alreadyChosen;

	/** The grammar for the item set chooser. */
	private Grammar grammar;
}
