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





package edu.duke.cs.jflap.gui.lsystem;

import edu.duke.cs.jflap.gui.GrowableTableModel;
import java.util.*;

/**
 * A mapping of parameters to values.
 * 
 * @author Thomas Finley
 */

public class ParameterTableModel extends GrowableTableModel {
	/**
	 * Constructs an empty parameter table model.
	 */
	public ParameterTableModel() {
		super(2);
	}

	/**
	 * Constructs a parameter table model out of the map.
	 * 
	 * @param parameters
	 *            the mapping of parameter names to parameter objects
	 */
	public ParameterTableModel(Map parameters) {
		this();
		Iterator it = parameters.entrySet().iterator();
		int i = 0;
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			setValueAt(entry.getKey(), i, 0);
			setValueAt(entry.getValue(), i, 1);
			i++;
		}
	}

	/**
	 * Initializes a row. In this object, a row is two empty strings.
	 * 
	 * @return an array with two empty strings
	 */
	public Object[] initializeRow(int row) {
		return new Object[] { "", "" };
	}

	/**
	 * Returns the mapping of names of parameters.
	 * 
	 * @return the mapping from parameter names to parameters (i.e., map of
	 *         contents of the left column to contents of the right column)
	 */
	public SortedMap getParameters() {
		TreeMap map = new TreeMap();
		for (int i = 0; i < getRowCount() - 1; i++) {
			Object o = getValueAt(i, 0);
			if (o.equals(""))
				continue;
			map.put(o, getValueAt(i, 1));
		}
		return map;
	}

	/**
	 * Values in the table are editable.
	 * 
	 * @param row
	 *            the row index
	 * @param column
	 *            the column index
	 * @return <CODE>true</CODE> always
	 */
	public boolean isCellEditable(int row, int column) {
		return true;
	}

	/**
	 * Returns the column name.
	 * 
	 * @param column
	 *            the index of the column
	 * @return the name of a particular column
	 */
	public String getColumnName(int column) {
		return column == 0 ? "Name" : "Parameter";
	}
}
