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





package edu.duke.cs.jflap.grammar.parse;

import edu.duke.cs.jflap.grammar.*;
import java.util.*;
import javax.swing.table.AbstractTableModel;
import java.io.Serializable;

/**
 * The <CODE>LLParseTable</CODE> is a parse table for LL grammars. It also has
 * the ability to function as a <CODE>TableModel</CODE> for a <CODE>javax.swing.JTable</CODE>.
 * Variables are in the rows, lookahead terminals in the columns. In its
 * capacity as a <CODE>TableModel</CODE> the first row is taken up with the
 * names of the variables.
 * 
 * @author Thomas Finley
 */

public class LLParseTable extends AbstractTableModel implements Serializable,
		Cloneable {
	/**
	 * Instantiates a new <CODE>LLParseTable</CODE> for a given grammar.
	 * 
	 * @param grammar
	 *            the grammar to create the table for
	 */
	public LLParseTable(Grammar grammar) {
		variables = grammar.getVariables();
		Arrays.sort(variables);
		terminals = grammar.getTerminals();
		Arrays.sort(terminals);

		entries = new SortedSet[variables.length][terminals.length + 1];
		for (int i = 0; i < entries.length; i++)
			for (int j = 0; j < entries[i].length; j++)
				entries[i][j] = new TreeSet();
	}

	/**
	 * Copy constructor for a LL parse table.
	 * 
	 * @param table
	 *            the table to copy
	 */
	public LLParseTable(LLParseTable table) {
		variables = table.variables;
		terminals = table.terminals;
		entries = new SortedSet[variables.length][terminals.length + 1];
		for (int i = 0; i < entries.length; i++)
			for (int j = 0; j < entries[i].length; j++)
				entries[i][j] = new TreeSet(table.entries[i][j]);
	}

	/**
	 * Clone method for the LL parse table.
	 * 
	 * @return a copy of this table
	 */
	public Object clone() {
		return new LLParseTable(this);
	}

	/**
	 * Returns <CODE>true</CODE> if this object equals another.
	 * 
	 * @param object
	 *            the object to compare against
	 */
	public boolean equals(Object object) {
		try {
			LLParseTable other = (LLParseTable) object;
			if (!Arrays.equals(variables, other.variables))
				return false;
			if (!Arrays.equals(terminals, other.terminals))
				return false;
			for (int i = 0; i < variables.length; i++)
				if (!Arrays.equals(entries[i], other.entries[i]))
					return false;
			return true;
		} catch (ClassCastException e) {
			return false;
		}
	}

	/**
	 * Returns a hashcode for this table.
	 * 
	 * @return the hashcode for this parse table
	 */
	public int hashCode() {
		// Lazy, stupid, and dangerous, but unlike me has the virtue
		// of working...
		return variables.length ^ terminals.length;
	}

	/**
	 * Returns a 2 integer array of the row and column of a variable and
	 * lookahead, in that order.
	 * 
	 * @param variable
	 *            the variable to look for
	 * @param lookahead
	 *            the lookahead terminal
	 * @throws IllegalArgumentException
	 *             if either variable or lookahead is not a variable or terminal
	 *             (or $) respectively in the grammar
	 */
	private int[] getLocation(String variable, String lookahead) {
		int[] r = new int[2];
		r[0] = getRow(variable);
		r[1] = getColumn(lookahead) - 1;
		return r;
	}

	/**
	 * Returns the row location in the table model of the variable.
	 * 
	 * @param variable
	 *            the variable to find the row for
	 * @return the row where this variable is
	 * @throws IllegalArgumentException
	 *             if the variable is not a variable in the grammar
	 */
	public int getRow(String variable) {
		int row = Arrays.binarySearch(variables, variable);
		if (row < 0)
			throw new IllegalArgumentException(variable + " is not a variable!");
		return row;
	}

	/**
	 * Returns the column location in the table model of the lookahead terminal.
	 * 
	 * @throws IllegalArgumentException
	 *             if either variable or lookahead is not a variable or terminal
	 *             (or $) respectively in the grammar
	 */
	public int getColumn(String lookahead) {
		int column = terminals.length;
		if (!lookahead.equals("$"))
			column = Arrays.binarySearch(terminals, lookahead);
		if (column < 0)
			throw new IllegalArgumentException(lookahead
					+ " is not a terminal!");
		return column + 1;
	}

	/**
	 * Given another parse table with the same variable and lookahead, return a
	 * listing of those pairs of variables and terminals where there are
	 * differences.
	 * 
	 * @param table
	 *            the other parse table
	 * @return an array, each element of which is a 2-array of the variable and
	 *         lookahead, in that order, of those entries in the table which do
	 *         not match
	 * @throws IllegalArgumentException
	 *             if the other parse table does not have the same variables and
	 *             lookahead terminals
	 */
	public String[][] getDifferences(LLParseTable table) {
		if (!Arrays.equals(variables, table.variables)
				|| !Arrays.equals(terminals, table.terminals))
			throw new IllegalArgumentException(
					"Tables differ in variables or terminals.");
		ArrayList differences = new ArrayList();
		for (int v = 0; v < entries.length; v++)
			for (int t = 0; t < entries[v].length; t++)
				if (!entries[v][t].equals(table.entries[v][t])) {
					if (t == terminals.length)
						differences.add(new String[] { variables[v], "$" });
					else
						differences.add(new String[] { variables[v],
								terminals[t] });
				}
		return (String[][]) differences.toArray(new String[0][0]);
	}

	/**
	 * Adds a new entry to the LL parse table
	 * 
	 * @param variable
	 *            the stack variable
	 * @param lookahead
	 *            the lookahead terminal
	 * @param expansion
	 *            what to expand the stack variable on when the lookahead
	 *            terminal is <CODE>lookahead</CODE>
	 * @return the number of expansion entries for this variable and lookahead
	 *         after this value was added
	 * @throws IllegalArgumentException
	 *             if either variable or lookahead is not a variable or terminal
	 *             (or $) respectively in the grammar
	 */
	public int addEntry(String variable, String lookahead, String expansion) {
		int[] r = getLocation(variable, lookahead);
		entries[r[0]][r[1]].add(expansion);
		fireTableCellUpdated(r[0], r[1] + 1);
		return entries[r[0]][r[1]].size();
	}

	/**
	 * Removes an entry from the LL parse table
	 * 
	 * @param variable
	 *            the stack variable
	 * @param lookahead
	 *            the lookahead terminal
	 * @param expansion
	 *            what to expand the stack variable on when the lookahead
	 *            terminal is <CODE>lookahead</CODE>
	 * @return if the expansion was even in this entry to start with
	 * @throws IllegalArgumentException
	 *             if either variable or lookahead is not a variable or terminal
	 *             (or $) respectively in the grammar
	 */
	public boolean removeEntry(String variable, String lookahead,
			String expansion) {
		int[] r = getLocation(variable, lookahead);
		boolean removed = entries[r[0]][r[1]].remove(expansion);
		fireTableCellUpdated(r[0], r[1] + 1);
		return removed;
	}

	/**
	 * This will clear all entries in the table.
	 */
	public void clear() {
		for (int i = 0; i < entries.length; i++)
			for (int j = 0; j < entries[i].length; j++)
				entries[i][j].clear();
		fireTableDataChanged();
	}

	/**
	 * This will clear the entry in the table for the given variable and
	 * lookahead.
	 * 
	 * @param variable
	 *            the stack variable
	 * @param lookahead
	 *            the lookahead terminal
	 * @throws IllegalArgumentException
	 *             if either variable or lookahead is not a variable or terminal
	 *             (or $) respectively in the grammar
	 */
	public void clear(String variable, String lookahead) {
		int[] r = getLocation(variable, lookahead);
		entries[r[0]][r[1]].clear();
		fireTableCellUpdated(r[0], r[1] + 1);
	}

	/**
	 * Returns the set of mapping for a variable and lookahead.
	 * 
	 * @param variable
	 *            the stack variable
	 * @param lookahead
	 *            the lookahead terminal
	 * @throws IllegalArgumentException
	 *             if either variable or lookahead is not a variable or terminal
	 *             (or $) respectively in the grammar
	 */
	public SortedSet get(String variable, String lookahead) {
		int[] r = getLocation(variable, lookahead);
		return Collections.unmodifiableSortedSet(entries[r[0]][r[1]]);
	}

	/**
	 * Defines the set of mapping for a variable and lookahead.
	 * 
	 * @param productions
	 *            the new set for the entry
	 * @param variable
	 *            the stack variable
	 * @param lookahead
	 *            the lookahead terminal
	 * @throws IllegalArgumentException
	 *             if either variable or lookahead is not a variable or terminal
	 *             (or $) respectively in the grammar
	 */
	public void set(Set productions, String variable, String lookahead) {
		int[] r = getLocation(variable, lookahead);
		entries[r[0]][r[1]].clear();
		entries[r[0]][r[1]].addAll(productions);
	}

	// ABSTRACT TABLE MODEL METHODS BELOW

	/**
	 * Returns the number of rows, equal to the number of variables.
	 * 
	 * @return the number of rows
	 */
	public int getRowCount() {
		return variables.length;
	}

	/**
	 * Returns the number of columns, equal to the number of terminals plus 1
	 * for the $ symbol plus 1 for the column devoted to variables.
	 */
	public int getColumnCount() {
		return terminals.length + 2;
	}

	/**
	 * Returns the name of a column.
	 * 
	 * @param column
	 *            the index for a column
	 */
	public String getColumnName(int column) {
		if (column == 0)
			return " ";
		if (column == terminals.length + 1)
			return "$";
		return terminals[column - 1];
	}

	/**
	 * Takes a set, and returns a string with the entries of that set space
	 * delimited.
	 * 
	 * @param set
	 *            the set to put in a space delimited string
	 */
	private String spaceSet(Set set) {
		Iterator it = set.iterator();
		boolean first = true;
		StringBuffer sb = new StringBuffer();
		while (it.hasNext()) {
			if (!first)
				sb.append(" ");
			String s = (String) it.next();
			sb.append(s.equals("") ? "!" : s);
			first = false;
		}
		return sb.toString();
	}

	/**
	 * Given a whitespace delimited string and a set, this clears the set and
	 * adds all distinct tokens from the string to the set.
	 * 
	 * @param string
	 *            the string to process
	 * @param set
	 *            the set to add to
	 * @return the number of elements processed
	 */
	private int despaceSet(String string, Set set) {
		set.clear();
		StringTokenizer st = new StringTokenizer(string);
		while (st.hasMoreTokens()) {
			String s = st.nextToken();
			if (s.equals("!"))
				s = "";
			set.add(s);
		}
		return set.size();
	}

	/**
	 * Returns the value for the table.
	 * 
	 * @param row
	 *            the row to get data for
	 * @param column
	 *            the column to get data for
	 * @return <CODE>M[ variables[row], terminals[column-1] ]</CODE> if
	 *         </CODE>column != 0</CODE>, or if <CODE>column == 0</CODE>
	 *         returns the variable for the row.
	 */
	public Object getValueAt(int row, int column) {
		if (column == 0)
			return variables[row];
		return spaceSet(entries[row][column - 1]);
	}

	/**
	 * Sets the value at a particular entry of the table.
	 * 
	 * @param value
	 *            the new value for an entry
	 */
	public void setValueAt(Object value, int row, int column) {
		despaceSet((String) value, entries[row][column - 1]);
		fireTableCellUpdated(row, column);
	}

	/**
	 * All cells are editable except those of the first column.
	 * 
	 * @param row
	 *            the row index
	 * @param column
	 *            the column index
	 */
	public boolean isCellEditable(int row, int column) {
		if (frozen)
			return false;
		return column != 0;
	}

	/**
	 * Sets whether any entries in this table should be editable.
	 * 
	 * @param editable
	 *            <CODE>true</CODE> if entries in this table should be
	 *            editable, <CODE>false</CODE> if the entries should be frozen
	 */
	public void setEditable(boolean editable) {
		frozen = !editable;
	}

	/** The list of terminals, each corresponding to a column. */
	private String[] terminals;

	/** The variables in the grammar, each corresponding to a row. */
	private String[] variables;

	/** The entries in the parse table. */
	private SortedSet[][] entries;

	/** Is the table noneditable? */
	private boolean frozen = false;
}
