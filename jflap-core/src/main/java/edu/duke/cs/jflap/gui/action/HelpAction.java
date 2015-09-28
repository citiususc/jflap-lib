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





package edu.duke.cs.jflap.gui.action;

import edu.duke.cs.jflap.gui.WebFrame;
import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import java.util.*;

/**
 * The <CODE>HelpAction</CODE> is an abstract action that is meant to bring up
 * a help page appropriate to whatever context. It also serves as a general sort
 * of database to relate types of objects to a particular URL to bring up in the
 * help web frame for the various subclasses that will implement this action.
 * The idea is that an object can store its help page in the <CODE>HelpAction</CODE>
 * via <CODE>setURL</CODE>
 * 
 * @see edu.duke.cs.jflap.gui.WebFrame
 * 
 * @author Thomas Finley
 */

public abstract class HelpAction extends RestrictedAction {
	/**
	 * Instantiates a new <CODE>HelpAction</CODE>.
	 */
	public HelpAction() {
		super("Help...", null);
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_SLASH,
				MAIN_MENU_MASK));
	}

	/**
	 * Associates a particular object with a URL. This object may be a
	 * particular instance (in which case when this instance gets its help it
	 * gets it immediately) or a Class object holding the class or superclass of
	 * the instance.
	 * 
	 * @param object
	 *            the key which will map to a URL
	 * @param url
	 *            the string representation of the URL to visit
	 * @see #getURL(Object)
	 */
	public static void setURL(Object object, String url) {
		HELP_MAP.put(object, url);
	}

	/**
	 * Returns the URL for a particular object as set via <CODE>setURL</CODE>.
	 * If there is a direct mapping from the object to a URL, that URL is
	 * returned. If there is no direct mapping, a mapping from this object's
	 * class to a URL is attempted. If <i>that</i> yields nothing, then a
	 * mapping from the object's superclass to a URL is attempted. If no
	 * superclass yields a URL, then <CODE>null</CODE> is returned.
	 * 
	 * @param object
	 *            the object to get help for
	 * @return a URL of help for this object, or <CODE>null</CODE> if no help
	 *         for this object exists
	 * @see #setURL(Object, String)
	 */
	public static String getURL(Object object) {
		String url = (String) HELP_MAP.get(object);
		if (url != null)
			return url;
		Class c = object instanceof Class ? (Class) object : object.getClass();
		while (c != null) {
			url = (String) HELP_MAP.get(c);
			if (url != null)
				return url;
			url = "/DOCS/" + c.getName() + ".html";
			if (c.getResource(url) != null)
				return url;
			c = c.getSuperclass();
		}
		return null;
	}

	/**
	 * Displays help for this object. If there is no particular help for this
	 * object available according to <CODE>getURL</CODE>, then the URL
	 * indicated by <CODE>DEFAULT_HELP</CODE> will be displayed in a <CODE>WebFrame</CODE>.
	 * 
	 * @param object
	 *            the object to display help for
	 * @see #getURL(Object)
	 * @see edu.duke.cs.jflap.gui.WebFrame
	 */
	public static void displayHelp(Object object) {
		String url = getURL(object);
		if (url == null)
			url = DEFAULT_HELP;
		FRAME.gotoURL(url);
		FRAME.setVisible(true);
	}

	/** The mapping of objects to URLs. */
	private static final WeakHashMap HELP_MAP = new WeakHashMap();

	/** The default URL in case there is no help for a subject. */
	public static final String DEFAULT_HELP = "/DOCS/nohelp.html";

	/** The web frame. */
	private static final WebFrame FRAME = new WebFrame("/DOCS/index.html");
}
