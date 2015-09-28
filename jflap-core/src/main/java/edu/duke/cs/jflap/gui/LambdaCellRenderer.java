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





package edu.duke.cs.jflap.gui;

import edu.duke.cs.jflap.gui.environment.Universe;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * This is a cell renderer that displays a specified character if the
 * quantity to display is the empty string.
 * 
 * @author Thomas Finley
 */

public class LambdaCellRenderer extends DefaultTableCellRenderer {
	/**
	 * Instantiates a new lambda cell renderer with the specified string to
	 * substitute for the empty string in the event that we display the empty
	 * string.
	 * 
	 * @param string
	 *            the string to display in lieu of the empty string
	 */
	public LambdaCellRenderer(String string) {
		toSubstitute = string;
	}

	/**
	 * Instantiates a new lambda cell renderer where the unicode string for
	 * lambda is substituted for the empty string when displaying the empty
	 * string.
	 */
	public LambdaCellRenderer() {
		this(Universe.curProfile.getEmptyString());
	}

	/**
	 * Returns the string this renderer substitutes for the empty string.
	 * 
	 * @return the string displayed in lieu of the empty string
	 */
	public final String getEmpty() {
		return toSubstitute;
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		JLabel l = (JLabel) super.getTableCellRendererComponent(table, value,
				isSelected, hasFocus, row, column);
		if (hasFocus && table.isCellEditable(row, column))
			return l;
		if (!"".equals(value))
			return l;
		l.setText(toSubstitute);
		return l;
	}

	/** The string to substitute for the empty string. */
	private String toSubstitute;
}
