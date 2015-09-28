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
import edu.duke.cs.jflap.grammar.Production;
import edu.duke.cs.jflap.gui.GrowableTableModel;
import java.util.ArrayList;
import javax.swing.Icon;

/**
 * The <CODE>GrammarTableModel</CODE> is used as a model for the input of a
 * grammar. The first column is the left hand side of a production, the second
 * middle column is reserved for a little icon that has a little arrow pointing
 * to the right, and the third and last column is the right hand side of the
 * production. Each row corresponds to a production, or to nothing if what is in
 * the table is not properly a production.
 * 
 * @see edu.duke.cs.jflap.grammar.Production
 * 
 * @author Thomas Finley
 */

public class GrammarTableModel extends GrowableTableModel {
	/**
	 * Instantiates a <CODE>GrammarTableModel</CODE>.
	 */
	public GrammarTableModel() {
		super(3);
	}

	/**
	 * Instantiates a <CODE>GrammarTableModel</CODE>.
	 * 
	 * @param grammar
	 *            the grammar to have for the grammar table model initialized to
	 */
	public GrammarTableModel(Grammar grammar) {
		this();
		Production[] ps = grammar.getProductions();
		for (int i = 0; i < ps.length; i++)
			addProduction(ps[i]);
	}

	/**
	 * This adds a production to the grammar table model.
	 * 
	 * @param production
	 *            the production to add to the data
	 * @return the row number where the production was added
	 */
	public int addProduction(Production production) {
		int row = getRowCount() - 1;
		setValueAt(production.getLHS(), row, 0);
		setValueAt(production.getRHS(), row, 2);
		return row;
	}

	/**
	 * This adds a production to the grammar table model at a specified index.
	 * 
	 * @param production
	 *            the production to add to the data
	 * @param row
	 *            the row number to insert the production at
	 * @return the row where the data was added
	 */
	public int addProduction(Production production, int row) {
		Object[] o = initializeRow(row);
		o[0] = production.getLHS();
		o[2] = production.getRHS();
		insertRow(o, row);
		return row;
	}

	/**
	 * Returns an empty string for each name.
	 * 
	 * @param column
	 *            the index of the column
	 * @return the empty string
	 */
	public String getColumnName(int column) {
		return "";
	}

	/**
	 * Returns the production at a row.
	 * 
	 * @param row
	 *            the row to get the production for
	 * @return the production at this row, or <CODE>null</CODE> if what is
	 *         there is not properly a production
	 */
	public Production getProduction(int row) {
		String lhs = (String) getValueAt(row, 0);
		if (lhs.equals(""))
			return null;
		return new Production(lhs, (String) getValueAt(row, 2));
	}

	/**
	 * Returns an array containing all the productions. If a particular row does
	 * not have a valid production, it is skipped. In this way, index <CODE>i</CODE>
	 * of the array does not necessarily correspond to row <CODE>i</CODE> of
	 * the model.
	 * 
	 * @return an array of the productions
	 */
	public Production[] getProductions() {
		ArrayList list = new ArrayList();
		for (int i = 0; i < getRowCount() - 1; i++) {
			Production production = getProduction(i);
			if (production != null)
				list.add(production);
		}
		return (Production[]) list.toArray(new Production[0]);
	}

	/**
	 * Everything in the table model is editable except for the middle arrow.
	 * 
	 * @param row
	 *            the index for the row
	 * @param column
	 *            the index for the column
	 * @return <CODE>true</CODE> if and only if this is a column other than
	 *         the middle column, which is column index 1
	 */
	public boolean isCellEditable(int row, int column) {
		return column != 1;
	}

	/**
	 * Initializes a row. For this particular object, a row is two strings
	 * surrounding an icon.
	 * 
	 * @param row
	 *            the row we're initializing, which is ignored
	 * @return an array containing the column entries for this new row
	 */
	protected Object[] initializeRow(int row) {
		Object[] newRow = { "", ARROW, "" };
		return newRow;
	}

	/**
	 * Returns the class of each column, which is a string for both the right
	 * and left hand sides, and an icon for the middle.
	 * 
	 * @param column
	 *            the column to get the class for
	 */
	public Class getColumnClass(int column) {
		return column == 1 ? Icon.class : String.class;
	}

	/**
	 * Returns the object at a particular location in the model. This is
	 * overridden to see that the arrow does not display itself in the last
	 * column.
	 * 
	 * @param row
	 *            the row of the object to retrieve
	 * @param column
	 *            the column of the object to retrieve
	 * @return the object at that location
	 */
	public Object getValueAt(int row, int column) {
		if (column == 1 && row == getRowCount() - 1)
			return null;
		return super.getValueAt(row, column);
	}

	/**
	 * The arrow icon. This is simply the item returned for the second column.
	 */
	private static Icon ARROW = new ArrowIcon(20, 8);
}
