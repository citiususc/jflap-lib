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




package edu.duke.cs.jflap.gui.editor;

import edu.duke.cs.jflap.gui.viewer.AutomatonDrawer;
import edu.duke.cs.jflap.gui.viewer.AutomatonPane;

import java.awt.Font;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.duke.cs.jflap.automata.Note;

/**
* @author Henry Qin and Jonathan Su
*/

public class AutomatonSizeSlider extends JSlider{
    //Set up animation parameters.
    static final int AUTOMATON_SIZE_MIN = 1;
    static final int AUTOMATON_SIZE_MAX = 800;
    static final int AUTOMATON_SIZE_INIT = 220;
    static final String AUTOMATON_SIZE_TITLE = "Automaton Size";
    
	/** The view we receive events from. */
	private AutomatonPane view;

	/** The drawer of the automaton */
	private AutomatonDrawer drawer;
	
	private double currentScale = 1;
	
	/**
	 * Constructs the AutomatonSizeSlider
	 * @param view
	 * @param drawer
	 */
	public AutomatonSizeSlider(AutomatonPane view, AutomatonDrawer drawer) {
		super(AUTOMATON_SIZE_MIN, AUTOMATON_SIZE_MAX, AUTOMATON_SIZE_INIT); 
		this.view = view;
		this.drawer = drawer;
	    this.addChangeListener(new SliderListener());
	    setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), AUTOMATON_SIZE_TITLE));
	}


      class SliderListener implements ChangeListener {
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider)e.getSource();
                    double pass = source.getValue()*1./AUTOMATON_SIZE_INIT;
                    view.setScale(pass);
                    view.requestTransform();
            	    ArrayList<Note> noteslist = drawer.getAutomaton().getNotes();
            	    for(Note n : noteslist){
            	    	n.setFont(new Font("Default", Font.PLAIN, (int)source.getValue()/20));
            	    }
            }
      }

}
