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





package edu.duke.cs.jflap.gui.environment;

import edu.duke.cs.jflap.grammar.lsystem.LSystem;
import edu.duke.cs.jflap.gui.lsystem.LSystemInputEvent;
import edu.duke.cs.jflap.gui.lsystem.LSystemInputListener;
import edu.duke.cs.jflap.gui.lsystem.LSystemInputPane;
import java.io.Serializable;

/**
 * The <CODE>LSystemEnvironment</CODE> is an environment for holding a
 * L-system. Owing to certain eccentricities of the way that the L-system is set
 * up as a non-editable object (inherited from the grammar), what is passed into
 * the environment is a <CODE>LSystemInputPane</CODE> which is then used to
 * retrieve the current L-system.
 * 
 * Unlike, for example, the automaton environment, the <CODE>LSystem</CODE>
 * returned by the <CODE>.getObject</CODE> method will not point to the same
 * object throughout the environment's execution.
 * 
 * @see edu.duke.cs.jflap.grammar.lsystem.LSystem
 * @see edu.duke.cs.jflap.gui.lsystem.LSystemInputPane
 * 
 * @author Thomas Finley
 */

public class LSystemEnvironment extends Environment {
	/**
	 * Instantiates a new <CODE>GrammarEnvironment</CODE> with the given
	 * <CODE>GrammarInputPane</CODE>.
	 * 
	 * @param input
	 *            the <CODE>GrammarInputPane</CODE>
	 */
	public LSystemEnvironment(LSystemInputPane input) {
		super(null);
		this.input = input;
		input.addLSystemInputListener(new LSystemInputListener() {
			public void lSystemChanged(LSystemInputEvent event) {
				setDirty();
			}
		});
	}

	/**
	 * Returns the L-system of this <CODE>LSystemEnvironment</CODE>, which is
	 * retrieved from the <CODE>LSystemInputPane</CODE>'s <CODE>.getLSystem</CODE>
	 * method.
	 * 
	 * @see edu.duke.cs.jflap.gui.lsystem.LSystemInputPane#getLSystem
	 * @return the <CODE>LSystem</CODE> for this environment
	 */
	public Serializable getObject() {
		return getLSystem();
	}

	/**
	 * Returns the L-system.
	 * 
	 * @see edu.duke.cs.jflap.gui.grammar.GrammarInputPane#getGrammar()
	 * @return the <CODE>ContextFreeGrammar</CODE> for this environment
	 */
	public LSystem getLSystem() {
		return input.getLSystem();
	}

	/** The L-system input pane. */
	private LSystemInputPane input = null;
}
