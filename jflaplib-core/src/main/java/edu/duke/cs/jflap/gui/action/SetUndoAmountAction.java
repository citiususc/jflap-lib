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
import edu.duke.cs.jflap.gui.environment.Universe;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

/**
 * The <CODE>SetUndoAmount</CODE> is an action to set the amount of Undos that are stored for automaton construction.
 * 
 * @author Henry Qin
 */

public class SetUndoAmountAction extends RestrictedAction {
	/**
	 * Instantiates a new <CODE>SetUndoAmountAction</CODE>.
	 * 
	 * @param environment
	 *            the environment that holds the serializable object
	 */
	public SetUndoAmountAction () {
		super("Set Undo Amount", null);
		//this.environment = environment;
	}

	/**
	 * If an Undo amount change was requested, then show a dialog and ask about it. 
	 * @param event
	 *            the action event
	 */
	public void actionPerformed(ActionEvent event) {
		String str; 
        int n;
        while (true){
//            str = JOptionPane.showInputDialog(null, "Please type the number of Undos:", "How many undo?", ""+Universe.curProfile.undo_num,  JOptionPane.PLAIN_MESSAGE);
            str = JOptionPane.showInputDialog("Please type the number of Undos:", ""+Universe.curProfile.undo_num);
            try {
                n = Integer.parseInt(str);
            }
            catch (NumberFormatException e){
                if (str != null)
                    continue;
                else 
                    return;
            }
                break;
        }

        //we better make sure this option is disabled for places where Undo does not apply.
        //((AutomatonEnvironment) environment).getUndoKeeper().setNumUndo(n);
        Universe.curProfile.setNumUndo(n);
        Universe.curProfile.savePreferences();


	}

	/**
	 * This action is restricted to those objects that are serializable.
	 * 
	 * @param object
	 *            the object to check for serializable-ness
	 * @return <CODE>true</CODE> if the object is an instance of a
	 *         serializable object, <CODE>false</CODE> otherwise
	 */
	public static boolean isApplicable(Object object) {
		return true;
	}

	/** The environment that this save action accesses its Undo from. */
	protected Environment environment;

}
