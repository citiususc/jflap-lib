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




package edu.duke.cs.jflap.gui;

import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 * A JSlider that adjusts the size of JTable cells and the font.
* @author Jonathan Su
*/

public class TableTextSizeSlider extends JSlider{

    static final int FONT_SIZE_MIN = 1;
    static final int FONT_SIZE_MAX = 600;
    static final int FONT_SIZE_INIT = 200;
    static final String TABLE_SIZE_TITLE = "Table Text Size";
    
    JTable myTable;
	
	public TableTextSizeSlider(JTable table) {
		super(FONT_SIZE_MIN, FONT_SIZE_MAX, FONT_SIZE_INIT); 
	    this.addChangeListener(new SliderListener());
	    setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), TABLE_SIZE_TITLE));
	    myTable = table;
	}


      class SliderListener implements ChangeListener {
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider)e.getSource();
                myTable.setFont(new Font("Default", Font.PLAIN, source.getValue()/10));
                myTable.setRowHeight(source.getValue()/10+10);
            }
      }
}
