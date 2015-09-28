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





package edu.duke.cs.jflap.automata;

/**
 * This class represents a configuration of an automaton for simulation of input
 * on an automaton (i.e. the state the automaton is currently in, the
 * unprocessed input, and the contents of the tapes/stack if applicable). A
 * <CODE>Configuration</CODE> object is simply a data structure that stores
 * this information, and does not do any work of progressing this configuration
 * to other configurations as a simulator would.
 * 
 * @see edu.duke.cs.jflap.automata.AutomatonSimulator
 * 
 * @author Ryan Cavalcante
 */

public abstract class Configuration implements Cloneable {
	/**
	 * Instantiates a new configuration.
	 * 
	 * @param state
	 *            the state the automaton is currently in.
	 */
	public Configuration(State state, Configuration parent) {
		myCurrentState = state;
		this.parent = parent;
	}

	/**
	 * Returns the state the automaton is currently occupying.
	 * 
	 * @return the state the automaton is currently occupying.
	 */
	public State getCurrentState() {
		return myCurrentState;
	}

	/**
	 * Sets current state.
	 * 
	 * @param state
	 *            the state the automaton is currently in.
	 */
	public void setCurrentState(State state) {
		myCurrentState = state;
	}

	/**
	 * Returns a string representation of this object. The string returned is
	 * the string representation of the current state.
	 * 
	 * @return a string representation of this object
	 */
	public String toString() {
		return "[" + getCurrentState().toString() + "]";
	}

	/**
	 * Returns the "parent" for this configuration, that is, the configuration
	 * that led to this configuration.
	 * 
	 * @see edu.duke.cs.jflap.automata.AutomatonSimulator#stepConfiguration
	 * @return the <CODE>Configuration</CODE> that led to this configuration,
	 *         or <CODE>null</CODE> if this is the initial configuration
	 */
	public Configuration getParent() {
		return parent;
	}

	/**
	 * Returns if this configuration is an accepting configuration.
	 * 
	 * @return <CDOE>true</CODE> if this configuration is an accepting
	 *         configuration, <CODE>false</CODE> otherwise
	 */
	public abstract boolean isAccept();

	/**
	 * The basic equals method considers two configurations equal if they both
	 * have the same state, and proceeded from the same configuration. By "the
	 * same configuration" it is meant a comparison of the parents via the ==
	 * operation rather than the <CODE>.equals()</CODE> operation, since the
	 * latter would lead to rather lengthly traversions.
	 * 
	 * @param configuration
	 *            the configuration to test for equality
	 */
	public boolean equals(Object configuration) {
		Configuration config = (Configuration) configuration;
		if (parent != config.parent)
			return false;
		return config.myCurrentState == myCurrentState;
	}

	/**
	 * Returns the base hash code for a configuration. Subclasses should
	 * override so as not to have all configurations with the same parent
	 * configuration and state map to the same hash entry.
	 * 
	 * @return a value for hashing
	 */
	public int hashCode() {
		return myCurrentState.hashCode()
				^ (parent == null ? 0 : parent.primitiveHashCode());
	}

	/**
	 * Returns the "primitive" hash code of the superclass, which is the generic
	 * hash code of the object.
	 */
	private int primitiveHashCode() {
		return super.hashCode();
	}
	/**
	 * Returns whether we are focussed on a block or at the top automaton
	 * @return <CODE>true</CODE> if focused on a block
	 */
	public boolean getFocused() {
		return focused;
	}
	
	/**
	 * Sets whether we are focused or not.
	 * @param focus <CODE>true</CODE> if focused, false if not
	 */
	public void setFocused(boolean focus) {
		focused = focus;
	}
//	/**
//	 * Gets the stack of parent automatons (arranged in a hierarchy, top level of the stack
//	 * is the immediate parent automaton)
//	 * @return the stack of automatons
//	 */
//	public Stack getAutoStack() {
//		return parentAutos;
//	}
//
//	/**
//	 * Sets the stack of parent automatons
//	 * @param parentAutos the stack of parents
//	 */
//	public void setAutoStack(Stack autos) {
//		parentAutos = autos;
//	}
//	
//	/**
//	 * Get the stack of blocks
//	 * TODO: What is this?
//	 * @return
//	 */
//	public Stack getBlockStack() {
//		return parentBlocks;
//	}
//
//	/**
//	 * @param parentAutos
//	 */
//	public void setBlockStack(Stack blocks) {
//		parentBlocks = blocks;
//	}
	/**
	 * Resets to be unfocused.
	 *
	 */
	public void reset() {
		this.setFocused(false);
	}
//	/** The stack of parent automatons - top of the stack is closest parent*/
//	private Stack parentAutos = new Stack();
	
	
	/** True if focused in on a block, false otherwise. */
	private boolean focused = false;

	/** The state the automaton is currently in. */
	private State myCurrentState;

	/** The parent for this configuration. */
	private Configuration parent;
}
