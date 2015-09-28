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
 * This class is an exception that is thrown in the event an incompatible <CODE>Transition</CODE>
 * object is assigned to an automaton.
 * 
 * @see edu.duke.cs.jflap.automata.Automaton
 * @see edu.duke.cs.jflap.automata.Transition
 * @see edu.duke.cs.jflap.automata.Automaton#getTransitionClass
 * @see edu.duke.cs.jflap.automata.Automaton#addTransition
 * @author Thomas Finley
 */

public class IncompatibleTransitionException extends RuntimeException {

}
