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
import edu.duke.cs.jflap.gui.environment.GrammarEnvironment;
import edu.duke.cs.jflap.gui.environment.Universe;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

/**
 * Multiple CYK Parse Action class
 * @author Kyung Min (Jason) Lee
 *
 */

public class MultipleCYKParseAction extends CYKParseAction{
	
	/**
	 * Instantiates a new <CODE>BruteParseAction</CODE>.
	 * 
	 * @param environment
	 *            the grammar environment
	 */
	public MultipleCYKParseAction(GrammarEnvironment environment) {
		super("Multiple CYK Parse", environment);
		this.environment = environment;
		this.frame = Universe.frameForEnvironment(environment);
	}

	
	/**
	 * Performs the action.
	 */
	public void actionPerformed(ActionEvent e) {
		Grammar g = environment.getGrammar(UnrestrictedGrammar.class);
		myGrammar=g;
		if (g == null)
			return;
		if (g.getTerminals().length==0)
		{
			JOptionPane.showMessageDialog(environment,
					"Error : This grammar does not accept any Strings. ",
					"Cannot Proceed with CYK", JOptionPane.ERROR_MESSAGE);
			myErrorInTransform=true;
			return;
		}
		hypothesizeLambda(environment, g);
		if (!myErrorInTransform)
		{
			MultipleCYKSimulateAction mult = new MultipleCYKSimulateAction(g, myGrammar, environment);
			mult.performAction((Component)e.getSource());
		}
	}
}
