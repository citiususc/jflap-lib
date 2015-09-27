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

import edu.duke.cs.jflap.grammar.Grammar;
import edu.duke.cs.jflap.grammar.UnrestrictedGrammar;
import edu.duke.cs.jflap.gui.environment.EnvironmentFrame;
import edu.duke.cs.jflap.gui.environment.GrammarEnvironment;
import edu.duke.cs.jflap.gui.environment.Universe;
import edu.duke.cs.jflap.gui.environment.tag.CriticalTag;
import edu.duke.cs.jflap.gui.grammar.parse.UserControlParsePane;


import java.awt.event.ActionEvent;

/**
 * Action for User Controlling Parsing
 * 
 * @author Kyung Min (Jason) Lee
 *
 */
public class UserControlParseAction extends GrammarAction {


	/** The grammar environment. */
	private GrammarEnvironment environment;

	/** The frame for the grammar environment. */
	private EnvironmentFrame frame;
	
	/**
	 * Instantiates a new <CODE>BruteParseAction</CODE>.
	 * 
	 * @param environment
	 *            the grammar environment
	 */
	public UserControlParseAction(GrammarEnvironment environment) {
		super("User Control Parse", null);
		this.environment = environment;
		this.frame = Universe.frameForEnvironment(environment);
	}

	public static boolean isApplicable(Object object) {
		return object instanceof Grammar;
	}
	/**
	 * Performs the action.
	 */
	public void actionPerformed(ActionEvent e) {
		Grammar g = environment.getGrammar(UnrestrictedGrammar.class);
		if (g == null)
			return;
		UserControlParsePane userPane = new UserControlParsePane(environment, g);
		environment.add(userPane, "User Control Parser", new CriticalTag() {
		});
		environment.setActive(userPane);
	}
}
