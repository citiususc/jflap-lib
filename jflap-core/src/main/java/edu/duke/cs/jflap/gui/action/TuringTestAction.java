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

import edu.duke.cs.jflap.gui.environment.Environment;

import java.awt.event.KeyEvent;

import edu.duke.cs.jflap.automata.Automaton;

/**
* The <CODE>TestTuringAction</CODE> is an action to load a structure from a file,
* and create a new environment with that object.
* 
* @author Stephen Reading
*/

public class TuringTestAction extends TestAction {


    /**
     * Instantiates a new <CODE>Turing Test Action</CODE>.
     */
    public TuringTestAction() {
        //super("Test Turing Machines", null);
        super("Test Turing Machines", KeyEvent.VK_T);       
    }
    
    protected void displayMultipleRunPane(Environment env){
    		MultipleOutputSimulateAction act = new MultipleOutputSimulateAction((Automaton)myObjects.get(0),env);
    		act.performAction(env);
    		env.getTag(env.getActive());
    	}

    

}
