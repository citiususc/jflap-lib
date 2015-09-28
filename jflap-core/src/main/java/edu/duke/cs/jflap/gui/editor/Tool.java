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

import edu.duke.cs.jflap.automata.Automaton;
import edu.duke.cs.jflap.gui.SuperMouseAdapter;
import edu.duke.cs.jflap.gui.viewer.AutomatonDrawer;
import edu.duke.cs.jflap.gui.viewer.AutomatonPane;
import java.awt.Graphics;
import javax.swing.Icon;
import javax.swing.KeyStroke;

/**
 * The <CODE>Tool</CODE> abstract class is a type of input adapter for the
 * pane used to edit the view, and the automaton. The tool also has the ability
 * to draw on the view.
 */

public abstract class Tool extends SuperMouseAdapter {
	/**
	 * Constructs a new tool.
	 * 
	 * @param view
	 *            the view the tool is in, useful for calling <CODE>repaint</CODE>
	 * @param drawer
	 *            the drawer of the automaton
	 */
	public Tool(AutomatonPane view, AutomatonDrawer drawer) {
		this.view = view;
		this.drawer = drawer;
		automaton = drawer.getAutomaton();
	}

	/**
	 * Returns the tool tip for this tool, modified to have the tool tip
	 * shortcut highlighted.
	 * 
	 * @return the string from <CODE>getToolTip</CODE> slightly modified
	 */
	public String getShortcutToolTip() {
		String tip = getToolTip();
		KeyStroke stroke = getKey();
		if (stroke == null)
			return tip;
		int index = findDominant(tip, stroke.getKeyChar());
		if (index == -1)
			return tip + "(" + Character.toUpperCase(stroke.getKeyChar()) + ")";
		return tip.substring(0, index) + "(" + tip.substring(index, index + 1)
				+ ")" + tip.substring(index + 1, tip.length());
	}

	/**
	 * Returns the tool tip for this tool.
	 * 
	 * @return a string containing the tool tip
	 */
	public String getToolTip() {
		return "Tool";
	}

	/**
	 * Retrieves the view.
	 * 
	 * @return the view the tool is in
	 */
	protected AutomatonPane getView() {
		return view;
	}

	/**
	 * Returns the automaton drawer.
	 * 
	 * @return the automaton drawer
	 */
	protected AutomatonDrawer getDrawer() {
		return drawer;
	}

	/**
	 * Returns the tool icon.
	 * 
	 * @return the default tool icon
	 */
	protected Icon getIcon() {
		java.net.URL url = getClass().getResource("/ICON/default.gif");
		return new javax.swing.ImageIcon(url);
	}

	/**
	 * The tool drawer, given a graphics context, draws for the tool. Most tools
	 * will have no cause to use this, though some will have certain states that
	 * they will express through some graphics.
	 * 
	 * @param g
	 *            the graphics object to draw upon
	 */
	public void draw(Graphics g) {

	}

	/**
	 * Returns the automaton.
	 * 
	 * @return the automaton
	 */
	protected Automaton getAutomaton() {
		return automaton;
	}

	/**
	 * Returns the key stroke that will activate this tool.
	 * 
	 * @return the key stroke that will activate this tool, or <CODE>null</CODE>
	 *         if there is no shortcut keystroke for this tool
	 */
	public KeyStroke getKey() {
		return false ? KeyStroke.getKeyStroke('a') : null;
	}

	/**
	 * This automatically finds the index of a character in the string for which
	 * then given character is at its most prominant. The intended use is to
	 * automatically, given a tooltip and a key shortcut, find the key in the
	 * string that should be highlighted as the shortcut for that particular
	 * tool.
	 * 
	 * @param string
	 *            the string to search for a character
	 * @param c
	 *            the character to search for in the string
	 * @return the index of the character c "at its best", or -1 if the
	 *         indicated character is not in the string
	 */
	protected static int findDominant(String string, char c) {
		int index = string.indexOf(Character.toUpperCase(c));
		if (index != -1)
			return index;
		return string.indexOf(Character.toLowerCase(c));
	}

	/** The view we receive events from. */
	private AutomatonPane view;

	/** The drawer of the automaton */
	private AutomatonDrawer drawer;

	/** The automaton. */
	private Automaton automaton;

}
