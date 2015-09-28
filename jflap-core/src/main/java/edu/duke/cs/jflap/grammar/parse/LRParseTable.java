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
import edu.duke.cs.jflap.automata.fsa.*;
import java.util.*;
import java.io.Serializable;
import javax.swing.table.AbstractTableModel;

/**
 * The <CODE>LRParseTable</CODE> is an LR(1) parse table. It also has the
 * ability to function as a <CODE>TableModel</CODE> for a <CODE>javax.swing.JTable</CODE>.
 * <P>
 * 
 * In this table, entries are either of the form "", "s#", "r#", "acc", or "#",
 * where # is a number. If a user change is not parseable into one of those
 * forms, then the entry will be unchanged.
 * 
 * @author Thomas Finley
 */

public class LRParseTable extends AbstractTableModel implements Serializable,
		Cloneable {
	/**
	 * Instantiates a new LR parse table.
	 * 
	 * @param grammar
	 *            the augmented grammar
	 * @param fsa
	 *            the goto graph for the grammar
	 */
	public LRParseTable(Grammar grammar, FiniteStateAutomaton fsa) {
		ArrayList term = new ArrayList(Arrays.asList(grammar.getTerminals()));
		ArrayList vars = new ArrayList(Arrays.asList(grammar.getVariables()));
		this.grammar = grammar;
		Collections.sort(term);
		Collections.sort(vars);
		term.add("$");
		terminals = (String[]) term.toArray(new String[0]);
		variables = (String[]) vars.toArray(new String[0]);

		for (int i = 0; i < terminals.length; i++)
			symbolsToColumn.put(terminals[i], new Integer(i + 1));
		for (int i = 0; i < variables.length; i++)
			symbolsToColumn.put(variables[i], new Integer(i + 1
					+ terminals.length));
		entries = new String[fsa.getStates().length][terminals.length
				+ variables.length + 1];
		for (int i = 0; i < entries.length; i++)
			for (int j = 0; j < entries[i].length; j++)
				entries[i][j] = j == 0 ? Integer.toString(i) : "";
	}

	/**
	 * Instantiates a new LR parse table from another LR parse table.
	 * 
	 * @param table
	 *            the other LR parse table
	 */
	public LRParseTable(LRParseTable table) {
		terminals = table.terminals;
		variables = table.variables;
		grammar = table.grammar;
		entries = new String[table.entries.length][table.entries[0].length];
		for (int i = 0; i < entries.length; i++)
			for (int j = 0; j < entries[i].length; j++)
				entries[i][j] = table.entries[i][j];
		symbolsToColumn = table.symbolsToColumn;
	}

	/**
	 * Returns a clone of this object.
	 * 
	 * @return a copy of this object
	 */
	public Object clone() {
		return new LRParseTable(this);
	}

	/**
	 * This sets the table value for a state ID and a grammar symbol.
	 * 
	 * @param value
	 *            the value to put in the table
	 * @param id
	 *            the state ID
	 * @param symbol
	 *            the grammar symbol
	 * @throws IllegalArgumentException
	 *             if symbol is not in the grammar
	 */
	public void setValueAt(String value, int id, String symbol) {
		setValueAt(value, id, columnForSymbol(symbol));
	}

	/**
	 * This will return the value for a given state ID and grammar symbol.
	 * 
	 * @param id
	 *            the state ID
	 * @param symbol
	 *            the grammar symbol
	 * @return the table entry for the state and symbol
	 * @throws IllegalArgumentException
	 *             if symbol is not in the grammar
	 */
	public String getValueAt(int id, String symbol) {
		return (String) getValueAt(id, columnForSymbol(symbol));
	}

	/**
	 * Returns the set of parse directives at a particular entry. A set of no
	 * items means that that configuration is considered an error (blank entry
	 * in the table). A set with more than one item indicates ambiguity in the
	 * parse table.
	 * 
	 * @param id
	 *            the state ID
	 * @param symbol
	 *            the grammar symbol
	 * @return the set of parse directives at a location
	 * @throws IllegalArgumentException
	 *             if symbol is not in the grammar
	 */
	public SortedSet getSetAt(int id, String symbol) {
		return getSetAt(id, columnForSymbol(symbol));
	}

	/**
	 * Appends the parse table directive to existing parse table directives.
	 * 
	 * @param directive
	 *            the directive to add
	 * @param id
	 *            the state ID
	 * @param symbol
	 *            the grammar symbol
	 * @throws IllegalArgumentException
	 *             if symbol is not in the grammar
	 */
	public void appendValueAt(String directive, int id, String symbol) {
		appendValueAt(directive, id, columnForSymbol(symbol));
	}

	/**
	 * Returns the column for a particular grammar symbol.
	 * 
	 * @param symbol
	 *            the grammar symbol
	 * @return the column index of the column corresponding to that grammar
	 *         symbol
	 * @throws IllegalArgumentException
	 *             if symbol is not in the grammar
	 */
	public int columnForSymbol(String symbol) {
		Integer in = (Integer) symbolsToColumn.get(symbol);
		if (in == null) {
			throw new IllegalArgumentException(symbol
					+ " is not in the grammar!");
		}
		return in.intValue();
	}

	// ABSTRACT TABLE MODEL METHODS

	/**
	 * Returns the number of rows, equal to the number of states in the original
	 * DFA.
	 * 
	 * @return the number of rows
	 */
	public int getRowCount() {
		return entries.length;
	}

	/**
	 * Returns the number of columns, equals to the number of terminals,
	 * nonterminals, plus one column for displaying the numbers of states.
	 * 
	 * @return the number of columns
	 */
	public int getColumnCount() {
		return entries[0].length;
	}

	/**
	 * Returns the appropriate string for a value, and a column.
	 * 
	 * @param value
	 *            the value that was input into the table
	 * @param column
	 *            the column that we're trying to edit
	 * @return an improved form of the input, or <CODE>null</CODE> if, for
	 *         this location, the value could not be reasonably determined
	 */
	private String parseValue(String value, int column) {
		if (column < 1)
			return null;
		if (value.equals(""))
			return "";
		if (column > terminals.length) {
			// It's in the variable section.
			try {
				int i = Integer.parseInt(value);
				return Integer.toString(i);
			} catch (NumberFormatException e) {
				return null;
			}
		}
		// It's in the terminal section.
		value = value.toLowerCase();
		switch (value.charAt(0)) {
		case 'a':
			return "acc";
		case 's':
		case 'r':
			if (value.length() < 2)
				return null;
			int startDigits = 1;
			while (!Character.isDigit(value.charAt(startDigits)))
				startDigits++;
			try {
				int i = Integer.parseInt(value.substring(startDigits));
				return "" + value.charAt(0) + Integer.toString(i);
			} catch (NumberFormatException e) {
				return null;
			}
		default:
			return null;
		}
	}

	/**
	 * Given an entry in the parse table, returns the strings for that entry. If
	 * any entry is not a good entry, it is ignored.
	 * 
	 * @param input
	 *            the input in the table
	 */
	private String[] parseValues(String input, int column) {
		StringTokenizer st = new StringTokenizer(input);
		SortedSet values = new TreeSet();
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			token = parseValue(token, column);
			if (token == null)
				continue;
			values.add(token);
		}
		return (String[]) values.toArray(new String[0]);
	}

	/**
	 * Returns the name of a particular column.
	 * 
	 * @param column
	 *            the column index
	 * @return the name of the column
	 */
	public String getColumnName(int column) {
		if (column == 0)
			return " ";
		if (column > terminals.length)
			return variables[column - 1 - terminals.length];
		return terminals[column - 1];
	}

	/**
	 * Sets the value at a particular row and column.
	 * 
	 * @param value
	 *            the new value
	 * @param row
	 *            the row index
	 * @param column
	 *            the column index
	 */
	public void setValueAt(Object value, int row, int column) {
		if (column == 0) {
			return;
		}

		String[] values = parseValues((String) value, column);
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < values.length; i++) {
			if (i != 0)
				sb.append(' ');
			sb.append(values[i]);
		}
		entries[row][column] = sb.toString();
		fireTableCellUpdated(row, column);
	}

	/**
	 * Appends the parse table directive to existing parse table directives.
	 * 
	 * @param directive
	 *            the directive to add
	 * @param row
	 *            the row index
	 * @param column
	 *            the column index
	 */
	public void appendValueAt(String directive, int row, int column) {
		setValueAt(getValueAt(row, column) + " " + directive, row, column);
	}

	/**
	 * Returns the value at a particular index.
	 */
	public Object getValueAt(int row, int column) {
		return entries[row][column];
	}

	/**
	 * Returns the set of parse directives at a particular entry. A set of no
	 * items means that that configuration is considered an error (blank entry
	 * in the table). A set with more than one item indicates ambiguity in the
	 * parse table.
	 * 
	 * @param row
	 *            the row index
	 * @param column
	 *            the column index
	 * @return the set of parse directives at a location
	 */
	public SortedSet getSetAt(int row, int column) {
		StringTokenizer st = new StringTokenizer(entries[row][column]);
		SortedSet set = new TreeSet();
		while (st.hasMoreTokens())
			set.add(st.nextToken());
		return set;
	}

	/**
	 * All cells are editable except the first column.
	 * 
	 * @param row
	 *            the row index
	 * @param column
	 *            the column index
	 * @return if the column is not 0
	 */
	public boolean isCellEditable(int row, int column) {
		return column != 0;
	}

	/**
	 * Returns an desciption of a particular entry in a parse table.
	 * 
	 * @param entry
	 *            the entry
	 * @return the description of that entry
	 */
	private String getContentDescription(String entry) {
		switch (entry.charAt(0)) {
		case 'a':
			return "Accept";
		case 's':
			return "Shift current input and state " + entry.substring(1)
					+ " to stack";
		case 'r':
			Production p[] = grammar.getProductions();
			int i = Integer.parseInt(entry.substring(1));
			String reduceDesc = "Reduce by production " + i + ", ";
			try {
				reduceDesc += p[i];
			} catch (ArrayIndexOutOfBoundsException e) {
				reduceDesc += "which does not exist";
			}
			return reduceDesc;
		default:
			return "Goto state " + entry;
		}
	}

	/**
	 * Gets a plaintext user readable description of the contents of a cell.
	 * 
	 * @param row
	 *            the row index
	 * @param column
	 *            the column index
	 * @return the plaintext desciption of the parse table entry at this row and
	 *         column
	 */
	public String getContentDescription(int row, int column) {
		StringTokenizer st = new StringTokenizer(entries[row][column]);
		StringBuffer description = new StringBuffer();
		int n = 0;
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if ((n++) != 0)
				description.append('\n');
			description.append(getContentDescription(token));
		}
		// Nothing indicates that the string should be rejected.
		if (description.length() == 0)
			return "Reject";
		return description.toString();
	}

	/** The terminals of the grammar. */
	private String[] variables;

	/** The nonterminals of the grammar, including $ at the end. */
	private String[] terminals;

	/** The entries of the table. */
	private String[][] entries;

	/** The grammar for the parse table. */
	private Grammar grammar;

	/** The mapping of grammar symbols to an Integer indicating the column. */
	private Map symbolsToColumn = new HashMap();
}
