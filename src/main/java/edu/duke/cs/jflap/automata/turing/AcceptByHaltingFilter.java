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




//MERLIN MERLIN MERLIN MERLIN MERLIN//

package edu.duke.cs.jflap.automata.turing;

public class AcceptByHaltingFilter implements AcceptanceFilter{ //note that building blocks should be STRIPPED of FINAL states //or we could simply ignore final states in nonTopLevel.
    public boolean accept(TMConfiguration tmc){
        return tmc.isHalted();
    }
    public String getName(){
        return "Accept by Halting";
    }
}
