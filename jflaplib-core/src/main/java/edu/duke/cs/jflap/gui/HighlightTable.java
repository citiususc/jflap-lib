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
import java.util.*;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

/**
 * This table allows the highlighting of specific cells in a <CODE>JTable</CODE>.
 * This assumes that highlighted cells will render just fine with a default cell
 * renderer. Key methods used are {@link #highlight(int,int)} and
 * {@link #dehighlight(int,int)}, though there are variations of these for
 * added convinience, and other variations for added customizability of the
 * highlighting (in case highlighted cells will not render properly with a
 * default cell renderer, for example).
 * 
 * @author Thomas Finley
 */

public class HighlightTable extends JTable {
	public HighlightTable() {
	}

	public HighlightTable(TableModel dm) {
		super(dm);
	}

	/**
	 * Highlights a particular cell.
	 * 
	 * @param row
	 *            the row index of the cell to highlight
	 * @param column
	 *            the column index of the cell to highlight
	 */
	public void highlight(int row, int column) {
		highlight(row, column, THRG);
	}

	/**
	 * Converts a row and column index to an index.
	 * 
	 * @param row
	 *            the row to get
	 * @param column
	 *            the column to get
	 */
	private static int singleIndex(int row, int column) {
		return row + (column << 22);
	}

	/**
	 * Dehighlights a particular cell.
	 * 
	 * @param row
	 *            the row index of the cell to dehighlight
	 * @param column
	 *            the column index of the cell to dehighlight
	 */
	public void dehighlight(int row, int column) {
		highlightRenderers.remove(new Integer(singleIndex(row, column)));
		repaint();
	}

	/**
	 * This method is the highlight method but allows the specification of a
	 * custom table cell renderer.
	 * 
	 * @param rc
	 *            the array of row and column entries, done in the model-space
	 *            rather than view-space of the table
	 * @param generator
	 *            the generator of the cell renderers for this particular
	 *            highlight
	 * @throws IllegalArgumentException
	 *             if the array specifies rows and columns that do not exist
	 */
	public void highlight(int[][] rc,
			TableHighlighterRendererGenerator generator) {
		highlightRenderers = new HashMap();
		for (int i = 0; i < rc.length; i++)
			highlight(rc[i][0], rc[i][1], generator);
	}

	/**
	 * This method is the highlight method but allows the specification of a
	 * custom table cell renderer. This will retain previous highlightings.
	 * 
	 * @param row
	 *            the row index of the cell to highlight
	 * @param column
	 *            the column index of the cell to highlight
	 * @param generator
	 *            the generator of the cell renderers for this particular
	 *            highlight
	 * @throws IllegalArgumentException
	 *             if the specified rows and columns do not exist
	 */
	public void highlight(int row, int column,
			TableHighlighterRendererGenerator generator) {
		if (highlightRenderers == null)
			highlightRenderers = new HashMap();
		Integer in = new Integer(singleIndex(row, column));
		highlightRenderers.put(in, generator.getRenderer(row, column));
		repaint();
	}

	/**
	 * Clears all highlighting.
	 */
	public void dehighlight() {
		highlightRenderers = null;
		repaint();
	}

	/**
	 * Returns the possibly modified cell renderer for this column.
	 */
	public TableCellRenderer getCellRenderer(int row, int column) {
		int column2 = convertColumnIndexToModel(column);
		if (highlightRenderers != null) {
			TableCellRenderer ren = (TableCellRenderer) highlightRenderers
					.get(new Integer(singleIndex(row, column2)));
			if (ren != null)
				return ren;
		}
		return super.getCellRenderer(row, column);
	}

	/**
	 * This class is used to get the cell renderer for a table highlighting
	 * action.
	 */
	public static interface TableHighlighterRendererGenerator {
		/**
		 * Returns a table cell renderer for this row and column.
		 * 
		 * @param row
		 *            the row index to get the table cell renderer for
		 * @param column
		 *            the model-space column index to get the table cell render
		 *            for
		 * @return a table cell renderer for this row and column
		 */
		public TableCellRenderer getRenderer(int row, int column);
	}

	/** The built in renderer. */
	private static TableHighlighterRendererGenerator THRG = new TableHighlighterRendererGenerator() {
		public TableCellRenderer getRenderer(int row, int column) {
			if (renderer == null) {
				renderer = new DefaultTableCellRenderer();
				renderer.setBackground(new Color(255, 150, 150));
			}
			return renderer;
		}

		private DefaultTableCellRenderer renderer = null;
	};

	/** The mapping of single indices to their respective renderers. */
	private Map highlightRenderers = new HashMap();
}
