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





package edu.duke.cs.jflap.gui.grammar;

import edu.duke.cs.jflap.grammar.Grammar;
import edu.duke.cs.jflap.grammar.TuringChecker;
import edu.duke.cs.jflap.grammar.cfg.ContextFreeGrammar;
import edu.duke.cs.jflap.grammar.reg.RegularGrammar;
import edu.duke.cs.jflap.gui.TableTextSizeSlider;

import javax.swing.table.*;
import javax.swing.*;
import java.awt.*;

/**
 * The <CODE>GrammarInputPane</CODE> is a pane that is used for the input and
 * graphical display of a grammar.
 * 
 * @author Thomas Finley
 */

public class GrammarInputPane extends JPanel {
	/**
	 * Instantiates an empty <CODE>GrammarInputPane</CODE>.
	 */
	public GrammarInputPane() {
		model = new GrammarTableModel();
		initView();
	}

	/**
	 * Instantiates a <CODE>GrammarInputPane</CODE> that holds and displays
	 * the indicated grammar.
	 * 
	 * @param grammar
	 *            the grammar to display
	 */
	public GrammarInputPane(Grammar grammar) {
		//System.out.println("bool - "+grammar.isConverted());
		model = new GrammarTableModel(grammar);
		if (grammar.isConverted() || TuringChecker.check(grammar))
			initLargerView();
		else
			initView();
	}

	
	private void initLargerView()
	{
		table = new GrammarTable(model);
		table.getTableHeader().setReorderingAllowed(false);
		TableColumn lhs = table.getColumnModel().getColumn(0);
		TableColumn arrows = table.getColumnModel().getColumn(1);
		TableColumn rhs = table.getColumnModel().getColumn(2);
		lhs.setHeaderValue("LHS");
		table.getTableHeader().resizeAndRepaint();
		rhs.setHeaderValue("RHS");
		table.getTableHeader().resizeAndRepaint();
		table.getColumnModel().getColumn(0).setPreferredWidth(100);
		//lhs.setMaxWidth(400);
		//lhs.setMinWidth(100);
		//arrows.setMaxWidth(30);
		//arrows.setMinWidth(30);
		// table.getColumnModel().getColumn(1).setPreferredWidth(30);
		table.setShowGrid(true);
		table.setGridColor(Color.lightGray);

		// Put the table in this pane.
		setLayout(new BorderLayout());
		add(new JScrollPane(table), BorderLayout.CENTER);
	}
	/**
	 * This is a constructor helper function that initializes the view.
	 */
	private void initView() {
		// Set up the table.
		table = new GrammarTable(model);
		table.setTableHeader(new JTableHeader(table.getColumnModel()));
		table.getTableHeader().setReorderingAllowed(false);
		table.getTableHeader().setResizingAllowed(true);
		TableColumn lhs = table.getColumnModel().getColumn(0);
		TableColumn arrows = table.getColumnModel().getColumn(1);
		TableColumn rhs = table.getColumnModel().getColumn(2);
		lhs.setHeaderValue("LHS");
		table.getTableHeader().resizeAndRepaint();
		rhs.setHeaderValue("RHS");
		table.getTableHeader().resizeAndRepaint();
		table.getColumnModel().getColumn(0).setPreferredWidth(70);
		lhs.setMaxWidth(200);
		//lhs.setMinWidth(20);
		arrows.setMaxWidth(30);
		arrows.setMinWidth(30);
		table.getColumnModel().getColumn(1).setPreferredWidth(30);
		table.setShowGrid(true);
		table.setGridColor(Color.lightGray);

		// Put the table in this pane.
		setLayout(new BorderLayout());
		add(new JScrollPane(table), BorderLayout.CENTER);
		add(new TableTextSizeSlider(table), BorderLayout.NORTH);
	}

	/**
	 * Returns the grammar that has been defined through this <CODE>GrammarInputPane</CODE>.
	 * This method returns a grammar of the type <CODE>ContextFreeGrammar</CODE>.
	 * 
	 * @return the grammar defined by this input pane, or <CODE>null</CODE> if
	 *         an error occurred
	 */
	public Grammar getGrammar() {
		return getGrammar(ContextFreeGrammar.class);
	}

	/**
	 * Returns the grammar that has been defined through this <CODE>GrammarInputPane</CODE>,
	 * where the grammar is an instance of the class passed into this function.
	 * 
	 * @param grammarClass
	 *            the type of grammar that is passed in
	 * @return a grammar of the variant returned by this grammar
	 * @throws IllegalArgumentException
	 *             if the grammar class passed in could not be instantiated with
	 *             an empty constructor, or is not even a subclass of <CODE>Grammar</CODE>.
	 */
	public Grammar getGrammar(Class grammarClass) {
		return table.getGrammar(grammarClass);
	}

	/**
	 * Returns the grammar that has been defined through this <CODE>GrammarInputPane</CODE>.
	 * 
	 * @return the grammar defined by this input pane, or <CODE>null</CODE> if
	 *         an error occurred
	 */
	public RegularGrammar getRegularGrammar() {
		return (RegularGrammar) getGrammar(RegularGrammar.class);
	}

	/**
	 * Returns the table.
	 * 
	 * @return the table where the productions are edited
	 */
	public GrammarTable getTable() {
		return table;
	}

	/** The table where the productions are edited. */
	private GrammarTable table;

	/** The model for the table. */
	private GrammarTableModel model;
}
