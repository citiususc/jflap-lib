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

import edu.duke.cs.jflap.automata.State;
import java.awt.Point;
import edu.duke.cs.jflap.automata.Automaton;


/**
  This class represents the TuringMachine-specific aspects of states, such as the ability to hold inner machines.


  @author Henry Qin
  */
public class TMState extends State{
    private TuringMachine myInnerTuringMachine;

    public TMState(int id, Point point, Automaton tm){ //do we really need a pointer to the parent?
        super(id, point, tm);

        assert(tm instanceof TuringMachine);

        myInnerTuringMachine = new TuringMachine();
        myInnerTuringMachine.setParent(this);
    }

    public TMState(TMState copyMe){ //do we really need a pointer to the parent?
        this(copyMe.getID(), (Point)copyMe.getPoint().clone(), copyMe.getAutomaton());

        myInnerTuringMachine = (TuringMachine) copyMe.getInnerTM().clone(); //this should result in recursion until we reach a TMState whose inner TM does not contain states.
    }

    public void setInnerTM(TuringMachine tm){
        myInnerTuringMachine = tm;
        myInnerTuringMachine.setParent(this);
        assert (myInnerTuringMachine.getParent() == this);
    }
    public TuringMachine getInnerTM(){
        return myInnerTuringMachine;
    }
    public String getInternalName(){ //just for trying to preserve old way of saving.
        //ASSUME that ID's are Independent
        return myInternalName == null? myInternalName = "Machine"+ getID() : myInternalName; //create an internal name if one has not been assigned explicitly
    }
    public void setInternalName(String s){ //just for trying to preserve old way of saving.
        myInternalName = s;
    }

    private String myInternalName = null;

}
