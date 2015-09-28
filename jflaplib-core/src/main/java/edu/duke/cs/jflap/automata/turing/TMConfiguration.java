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





package edu.duke.cs.jflap.automata.turing;

import edu.duke.cs.jflap.automata.*;

/**
 * A <CODE>TMConfiguration</CODE> object is a <CODE>Configuration</CODE>
 * object with additional fields for the input string and the tape contents. The
 * current state of the automaton and the tape contents, are the only necessary
 * data for the simulation of a 1-tape Turing machine.
 * 
 * @author Ryan Cavalcante
 */

public class TMConfiguration extends Configuration implements Cloneable {
	/**
	 * Instantiates a new TMConfiguration.
	 * 
	 * @param state
	 *            the state the automaton is currently in
	 * @param parent
	 *            the immediate ancestor for this configuration
	 * @param tapes
	 *            the read/write tapes
	 */
	public TMConfiguration(State state, TMConfiguration parent, Tape[] tapes, AcceptanceFilter[] filters) {
		super(state, parent);
		this.myTapes = tapes;
        myFilters = filters;
	}

	/**
	 * Returns the tapes.
	 * 
	 * @return the tapes
	 */
	public Tape[] getTapes() {
		return myTapes;
	}

	/**
	 * Returns a string representation of this object. This is the same as the
	 * string representation for a regular configuration object, with the
	 * additional fields tacked on.
	 * 
	 * @see edu.duke.cs.jflap.automata.Configuration#toString
	 * @return a string representation of this object.
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer(super.toString());
		for (int i = 0; i < myTapes.length; i++) {
			sb.append(" TAPE ");
			sb.append(i);
			sb.append(": ");
			sb.append(myTapes[i].toString());
		}
		return sb.toString();
	}

	/**
	 * Returns <CODE>true</CODE> if this configuration is an accepting
	 * configuration, based on the chosen criteria. Currently, we look at accept by halting and accept by final state. 
     //MERLIN MERLIN MERLIN MERLIN MERLIN//
	 * 
	 * @return <CODE>true</CODE> if this configuration is accepting, <CODE>false</CODE>
	 *         otherwise
	 */
	public boolean isAccept() {

        for (int i = 0; i < myFilters.length; i++){
             if (myFilters[i].accept(this)) return true;
        }
        return false;

	}
    
   /* 
    private boolean isFinalStateInAutomaton(Automaton auto, State state){
        State[] finals = auto.getFinalStates();
        for(int m = 0; m < finals.length; m++){
          if(finals[m]==state){
              return true;
          }
        }
        return false;
    }*/

	/**
	 * Compares two TM configurations for equality. Two configurations are equal
	 * if the tapes are equal, and if they arose from the same configuration and
	 * are at the same state.
	 * 
	 * @param configuration
	 *            the configuration to test for equality
	 * @return <CODE>true</CODE> if the configurations are equal, <CODE>false</CODE>
	 *         if they are not
	 */
	public boolean equals(Object configuration) {
		if (configuration == this)
			return true;
		try {
			if (!super.equals(configuration))
				return false;
			Tape[] tapes = ((TMConfiguration) configuration).myTapes;
			if (tapes.length != myTapes.length)
				return false;
			for (int i = 0; i < tapes.length; i++)
				if (!tapes[i].equals(myTapes[i]))
					return false;
			return true;
		} catch (ClassCastException e) {
			return false;
		}
	}

	/**
	 * Returns a hash code for this configuration.
	 * 
	 * @return a hash code for this configuration
	 */
	public int hashCode() {
		int code = super.hashCode();
		for (int i = 0; i < myTapes.length; i++)
			code = code ^ myTapes[i].hashCode();
		return code;
	}

	/** The tapes. */
	protected Tape[] myTapes;

    private AcceptanceFilter[] myFilters; //constructed outside and passed in in the constructor. //Constructed once and passed to multiple people.

    //MERLIN MERLIN MERLIN MERLIN MERLIN//
    private boolean isHalted = false; //this is a special flag which is checked by the accept by halt. The first time that step-configuration method of TMSimulator cannot go forth, it will set this flag, and return the thing that it was handed. The second time, it will see this flag, and return an empty list to indicate failure, if the configuration was not previously accepted by the filter (that is, if the filter was not activated)

    public boolean isHalted(){
        return isHalted;
    }
    public void setHalted(boolean b){
        isHalted = b;
    }

	public Object clone() {
		TMConfiguration newConfig = new TMConfiguration(this.getCurrentState(),
				(TMConfiguration) this.getParent(), myTapes, myFilters);
		newConfig.setFocused(this.getFocused());
        newConfig.setHalted(this.isHalted());
		return newConfig;

	}

}
