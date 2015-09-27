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

import edu.duke.cs.jflap.gui.pumping.CFPumpingLemmaChooser;
import edu.duke.cs.jflap.gui.pumping.RegPumpingLemmaChooser;

import java.io.Serializable;
import java.awt.Dimension;

import edu.duke.cs.jflap.pumping.ContextFreePumpingLemma;
import edu.duke.cs.jflap.pumping.RegularPumpingLemma;

import edu.duke.cs.jflap.automata.Automaton;

/**
 * The <CODE>FrameFactory</CODE> is a factory for creating environment frames.
 * 
 * @author Thomas Finley
 */

public class FrameFactory {
	/**
	 * This creates an environment frame for a new item.
	 * 
	 * @param object
	 *            the object that we are to edit
	 * @return the environment frame for this new item, or <CODE>null</CODE>
	 *         if an error occurred
	 */
	public static EnvironmentFrame createFrame(Serializable object) {
		Environment environment = EnvironmentFactory.getEnvironment(object);
		if (environment == null)
			return null; // No environment could be found.
		EnvironmentFrame frame = new EnvironmentFrame(environment);
		if (object instanceof Automaton) {
			// //System.out.println("Setting Frame");
			((Automaton) object).setEnvironmentFrame(frame);
		}
		frame.pack();

		// Make sure that the size of the frame is above a certain
		// threshold.
		int width = 600, height = 400;
        
        /*
         * If it is a pumping lemma, make the window bigger.
         */
        if(object instanceof RegPumpingLemmaChooser || object instanceof RegularPumpingLemma)
        {
            width = 700;
            height = 700;
        }
        if(object instanceof CFPumpingLemmaChooser || object instanceof ContextFreePumpingLemma)
        {
            width = 800;
            height = 780;
        }
        
		width = Math.max(width, frame.getSize().width);
		height = Math.max(height, frame.getSize().height);
		frame.setSize(new Dimension(width, height));
		frame.setVisible(true);

		return frame;
	}
    
	/**
	 * Special method to deal with Grammar converted from Turing Machine
	 * @param object
	 * @param isTuring
	 * @return
	 */
	public static EnvironmentFrame createFrame(Serializable object, int isTuring) {
		Environment environment = EnvironmentFactory.getEnvironment(object);
		if (environment == null)
			return null; // No environment could be found.
		//call special constructor
		EnvironmentFrame frame = new EnvironmentFrame(environment, 0);
		if (object instanceof Automaton) {
			// //System.out.println("Setting Frame");
			((Automaton) object).setEnvironmentFrame(frame);
		}
		frame.pack();

		// Make sure that the size of the frame is above a certain
		// threshold.
		int width = 600, height = 400;
        
        /*
         * If it is a pumping lemma, make the window bigger.
         */
        if(object instanceof RegPumpingLemmaChooser || object instanceof RegularPumpingLemma)
        {
            width = 700;
            height = 700;
        }
        if(object instanceof CFPumpingLemmaChooser || object instanceof ContextFreePumpingLemma)
        {
            width = 800;
            height = 780;
        }
        
		width = Math.max(width, frame.getSize().width);
		height = Math.max(height, frame.getSize().height);
		frame.setSize(new Dimension(width, height));
		frame.setVisible(true);

		return frame;
	}
	
    public static EnvironmentFrame createFrame(Serializable object, boolean multiple) {
        EnvironmentFrame frame = createFrame(object);
        

        return frame;
    }
}
