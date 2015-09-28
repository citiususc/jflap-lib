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

import java.awt.Color;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

/**
 * This table is specifically for those tables where the leftmost column is an
 * identifier for the row, i.e., it should not truly be considered data of the
 * table.
 * 
 * @author Thomas Finley
 */

public class LeftTable extends HighlightTable {
	public LeftTable() {
		initView();
	}

	public LeftTable(TableModel model) {
		super(model);
		initView();
	}

	/**
	 * Makes the leftmost column's data cells have renderers the same as the
	 * table column headers.
	 */
	private void initView() {
		setGridColor(Color.lightGray);
		TableColumn column = getColumnModel().getColumn(0);
		// column.setCellRenderer(column.getHeaderRenderer());
		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		renderer.setBackground(new Color(200, 200, 200));
		column.setCellRenderer(renderer);
	}
}
