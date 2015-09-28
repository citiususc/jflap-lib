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





package edu.duke.cs.jflap.gui.editor;

import edu.duke.cs.jflap.gui.viewer.AutomatonDrawer;
import edu.duke.cs.jflap.gui.viewer.AutomatonPane;
import java.util.List;

/**
 * A <CODE>ToolBox</CODE> is an object used for defining what tools are in a
 * <CODE>ToolBar</CODE> object.
 * 
 * @see edu.duke.cs.jflap.gui.editor.ToolBar
 * @see edu.duke.cs.jflap.gui.editor.Tool
 * 
 * @author Thomas Finley
 */

public interface ToolBox {
	/**
	 * Returns a list of tools in the order they should be in the tool bar.
	 * 
	 * @param view
	 *            the view that the automaton will be drawn in
	 * @param drawer
	 *            the automaton drawer for the view
	 */
	public List tools(AutomatonPane view, AutomatonDrawer drawer);
}
