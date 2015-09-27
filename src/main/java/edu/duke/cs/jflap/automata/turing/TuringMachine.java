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

import edu.duke.cs.jflap.automata.Automaton;
import edu.duke.cs.jflap.automata.Transition;
import edu.duke.cs.jflap.automata.State;
import edu.duke.cs.jflap.automata.Note;
import java.awt.Point;

import java.util.HashMap;
import java.util.Map;
import java.util.Collection;

import java.io.Serializable;
import java.io.File;

import edu.duke.cs.jflap.gui.action.OpenAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JButton;

/**
 * This subclass of <CODE>Automaton</CODE> is specifically for a definition of
 * a Turing machine, possibly with multiple tapes.
 *
 * We also redefine equals(), and clone(), as well as become() to deal with Building Blocks, so that we don't have to deal with them in Automaton.
 * 
 * @author Thomas Finley, Henry Qin
 */

public class TuringMachine extends Automaton {
	private static final Logger logger = LoggerFactory.getLogger(TuringMachine.class);
	/**
	 * Creates a 1-tape Turing machine with no states and no transitions.
	 */
	public TuringMachine() {
		this(1);
	}

	/**
	 * Creates a Turing machine with a variable number of tapes, no states, and
	 * no transitions.
	 * 
	 * @param tapes
	 *            the number of tapes for the Turing machine
	 */
	public TuringMachine(int tapes) {
		super();
		this.tapes = tapes;
	}

	/**
	 * Returns the class of <CODE>Transition</CODE> this automaton must
	 * accept.
	 * 
	 * @return the <CODE>Class</CODE> object for <CODE>automata.tm.TMTransition</CODE>
	 */
	protected Class getTransitionClass() {
		return edu.duke.cs.jflap.automata.turing.TMTransition.class;
	}

	/**
	 * Adds a transition to this Turing machine.
	 * 
	 * @param t
	 *            the transition to add
	 * @throws IllegalArgumentException
	 *             if this transition requires a different number of tapes than
	 *             required by other Turing machines
	 */
	public void addTransition(Transition t) {
		try {
			int ttapes = ((TMTransition) t).tapes();
			if (tapes == 0)
				tapes = ttapes;
			if (ttapes != tapes)
				throw new IllegalArgumentException("Transition has " + ttapes
						+ " tapes while TM has " + tapes);
			super.addTransition(t);
		} catch (ClassCastException e) {

		}
	}

	/**
	 * Returns the number of tapes this Turing machine uses.
	 * 
	 * @return the number of tapes this Turing machine uses
	 */
	public int tapes() {
		return tapes;
	}

	/**
	 * Creates a TMState, inserts it in this automaton, and returns that state.
	 * The ID for the state is set appropriately.
     * This method was once Final in Automaton, but it must be overriden here, because TMStates are not like standard states.
	 * 
	 * @param point
	 *            the point to put the state at
	 */
	public State createState(Point point) {
		return createTMState(point);
	}

    /**
      * We need to implement our own clone() method, rather than use that of Automaton, because we use TMStates instead of ordinary states, and we handle the building block cloning in a more elegant way.
      */
	public Object clone() {
        //MERLIN MERLIN MERLIN MERLIN MERLIN//

        TuringMachine a = new TuringMachine(this.tapes());
		a.setEnvironmentFrame(this.getEnvironmentFrame());

		HashMap<TMState, TMState> map = new HashMap<TMState, TMState>(); // Old states to new states.
        for (Object o:  states){ 

//            System.out.println(o.getClass().getName());

            TMState tms = (TMState)o;
            TMState ntms = new TMState(tms);//I could write a clone for that TM too, I suppose, but there's nothing wrong with a nice C++ style copy constructor
			ntms.setAutomaton(a); //recognize thine new master, after the convenience of the copy constructor, lest there be great many bugs.
            
			ntms.setLabel(tms.getLabel());
			ntms.setName(tms.getName());

			map.put(tms, ntms);

			a.addState(ntms);

            //using OBJECT equality, and OBJECT hashcode, which is fine here because we want to know if the objects are literally the same (which they should be)
        }
        for (Object o: finalStates){
            TMState tms = (TMState) o;
            a.addFinalState(map.get(tms));
        }
		a.setInitialState((TMState) map.get((TMState) getInitialState()));

        for (Object o:  states){ 
            TMState tms = (TMState)o;
			Transition[] ts = getTransitionsFromState(tms);
			TMState from =  map.get(tms);
			for (int i = 0; i < ts.length; i++) {
				TMState to =  map.get(ts[i].getToState());
                Transition toBeAdded = (Transition)ts[i].clone();
                toBeAdded.setFromState(from);
                toBeAdded.setToState(to);
//				a.addTransition(ts[i].copy(from, to));
				a.addTransition(toBeAdded);

			}

        }

        return a;
    }

	/**
	 * Creates a state, inserts it in this automaton, and returns that state.
	 * The ID for the state is set appropriately.
	 * 
	 * @param point
	 *            the point to put the state at
	 */
	public final TMState createBlock(Point point) {
		int i = 0;
		while (getStateWithID(i) != null)
			i++;
		OpenAction read = new OpenAction();
		OpenAction.setOpenOrRead(true);
		JButton button = new JButton(read);
		button.doClick();
		OpenAction.setOpenOrRead(false);
		return getAutomatonFromFile(i, point);
	}
  
  /**
   * Reads the automaton in from a file.
   */
	private TMState getAutomatonFromFile(int i, Point point) {
		TMState block = new TMState(i, point, this);
		Serializable serial = OpenAction.getLastObjectOpened();
		File lastFile = OpenAction.getLastFileOpened();
		if (lastFile == null || OpenAction.isOpened() == false) {
			return null;
		}
        
//		block = putBlockContentsInAutomaton(block, serial, lastFile.getName(),
//				this);
        assert serial instanceof TuringMachine;
        TuringMachine tm = (TuringMachine) serial;

        tm.setEnvironmentFrame(this.getEnvironmentFrame());
        block.setInternalName(lastFile.getName());
        
        //MERLIN MERLIN MERLIN MERLIN MERLIN//

        block.setInnerTM(tm);

		block.setName(lastFile.getName().substring(0, lastFile.getName().length() - 4));
		addState(block);
		return block;
	}

    /**
      * For the sake of separation, some methods must unfortunately be duplicated.
      *
      *
      */
    public final TMState createTMStateWithID(Point p, int i){
		TMState state = new TMState(i, p, this);
		addState(state);
		return state;
    }

	/**
      * For the sake of separation, some methods must unfortunately be duplicated.
      *
      *
      */
    public final TMState createTMState(Point point) {
		int i = 0;
		while (getStateWithID(i) != null)
			i++;
		TMState state = new TMState(i, point, this);
		addState(state);
		return state;
	}

    public TMState createInnerTM(Point point, Serializable auto, String name, int i) {
         TMState ntms = new TMState(i, point, this);
         TuringMachine innerTM = (TuringMachine) auto;
         addState(ntms);
         ntms.setInnerTM(innerTM);
         ntms.setInternalName(name);
         return ntms;
    }
	public static void become(TuringMachine dest, TuringMachine src){
        logger.debug("Calling the real become");
		System.out.println("Calling the real become");
		
		dest.clear();
		// Copy over the states.
		HashMap<TMState, TMState> map = new HashMap<TMState, TMState>(); // Old states to new states.
//		Iterator it = src.states.iterator();
		for (Object o: src.states){
			logger.debug("become method, processing {}", o.getClass().getName());
			TMState state = (TMState) o;
			TMState nstate = new TMState(state.getID(),
					new Point(state.getPoint()), dest); //this time we're not using copy constructor
//			copyStaticRelevantDataForBlocks(nstate, state, dest, src);
			nstate.setLabel(state.getLabel());
			nstate.setName(state.getName());
			map.put(state, nstate);
			dest.addState(nstate);
            
		}
		// Set special states.
        for (Object o: src.finalStates){
            TMState tms = (TMState) o;
            dest.addFinalState(map.get(tms));
        }
		dest.setInitialState((TMState) map.get((TMState) src.getInitialState()));

		// Copy over the transitions.
        for (Object o: src.states){ 
            TMState tms = (TMState)o;
			Transition[] ts = src.getTransitionsFromState(tms);
			TMState from =  map.get(tms);
			for (int i = 0; i < ts.length; i++) {
				TMState to =  map.get(ts[i].getToState());
                Transition toBeAdded = (Transition)ts[i].clone();
                toBeAdded.setFromState(from);
                toBeAdded.setToState(to);

//				dest.addTransition(ts[i].copy(from, to));
				dest.addTransition(toBeAdded);
			}

        }
		for(int k = 0; k < src.getNotes().size(); k++){
			Note curNote = (Note)src.getNotes().get(k);		
			dest.addNote(new Note(curNote.getAutoPoint(), curNote.getText()));
            ((Note)dest.getNotes().get(k)).initializeForView(curNote.getView());
		}
        dest.setEnvironmentFrame(src.getEnvironmentFrame());
//		EDebug.print("finished");
    }

    public Map<String, TuringMachine> getBlockMap(){
        Map<String, TuringMachine> ret = new HashMap<String, TuringMachine>();
        for (TMState s: (Collection<TMState>) states) //that's right, EVERY state in TM has an inner Auto, even if that inner auto might be empty.
            ret.put(s.getInternalName(), s.getInnerTM());
         
        return ret;
    }

    public void setParent(TMState tms){
        parent = tms;
    }
    public TMState getParent(){ 
        return parent;
    }

	/**
	 * The number of tapes. It's public for some hacky reasons related to
	 * serialization.
	 */
	public int tapes;

    public boolean isOuterMost;

    //MERLIN MERLIN MERLIN MERLIN MERLIN//
    private TMState parent = null; //not going to force it with compiler, just make sure you set it WHERE it MATTERS
}
