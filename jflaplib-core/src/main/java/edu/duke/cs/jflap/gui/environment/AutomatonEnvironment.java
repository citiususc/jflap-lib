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

import edu.duke.cs.jflap.gui.editor.UndoKeeper;
import edu.duke.cs.jflap.automata.Automaton;
import edu.duke.cs.jflap.automata.event.AutomataStateEvent;
import edu.duke.cs.jflap.automata.event.AutomataStateListener;
import edu.duke.cs.jflap.automata.event.AutomataTransitionEvent;
import edu.duke.cs.jflap.automata.event.AutomataTransitionListener;
import edu.duke.cs.jflap.automata.event.AutomataNoteEvent;
import edu.duke.cs.jflap.automata.event.AutomataNoteListener;

public class AutomatonEnvironment extends Environment {
	/**
	 * Instantiates an <CODE>AutomatonEnvironment</CODE> for the given
	 * automaton. By default this method will set up an environment with an
	 * editor pane for this automaton.
	 * 
	 * @param automaton
	 *            the automaton to set up an environment for
	 * @see edu.duke.cs.jflap.gui.editor.EditorPane
	 */
	public AutomatonEnvironment(Automaton automaton) {
		super(automaton);
		Listener listener = new Listener();
		automaton.addStateListener(listener);
		automaton.addTransitionListener(listener);
		automaton.addNoteListener(listener);
		initUndoKeeper();
	}

	/**
	 * Returns the automaton that this environment manages.
	 * 
	 * @return the automaton that this environment manages
	 */
	public Automaton getAutomaton() {
		return (Automaton) super.getObject();
	}
	
	/*Start undo methods*/
    public UndoKeeper getUndoKeeper(){
        return myKeeper;	
    }
    public void initUndoKeeper(){
        myKeeper = new UndoKeeper(getAutomaton());
    }
    public void saveStatus(){
        myKeeper.saveStatus();	
    }
    public void restoreStatus(){
        myKeeper.restoreStatus();	
    }
    
    public boolean shouldPaint(){
        return myKeeper == null ? true: !myKeeper.sensitive;	
    }
    
    public void setWait(){
    	myKeeper.setWait();
    }

    public void redo(){
        myKeeper.redo();
    }
	
	private UndoKeeper myKeeper;
    /*End undo methods*/

	/**
	 * The transition and state listener for an automaton detects if there are
	 * changes in the environment, and if so, sets the dirty bit.
	 */
	private class Listener implements AutomataStateListener,
			AutomataTransitionListener, AutomataNoteListener {
		public void automataTransitionChange(AutomataTransitionEvent e) {
			setDirty();
		}

		public void automataStateChange(AutomataStateEvent e) {
			setDirty();
		}

        public void automataNoteChange(AutomataNoteEvent e){
            setDirty();
        }
	}
}
