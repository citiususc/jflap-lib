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

import edu.duke.cs.jflap.gui.environment.Profile;
import edu.duke.cs.jflap.gui.environment.Universe;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.KeyStroke;

import java.awt.BorderLayout;
import java.awt.Point;

/**
* The <CODE>TestTuringAction</CODE> is an action to load a structure from a file,
* and create a new environment with that object.
* 
* @author Stephen Reading
*/

public class EmptyStringCharacterAction extends  RestrictedAction{
    private JFileChooser fileChooser;

    /**
     * Instantiates a new <CODE>Turing Test Action</CODE>.
     */
    public EmptyStringCharacterAction() {
        //super("Test Turing Machines", null);
    	super("Set the Empty String Character", null);   
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_P,
                MAIN_MENU_MASK));
        this.fileChooser = Universe.CHOOSER;
    }

	public void actionPerformed(ActionEvent e) {
		Profile current = Universe.curProfile;
		
//		JFrame.setDefaultLookAndFeelDecorated(true);    
		final JFrame frame = new JFrame("Preferences");
		
		JRadioButton lambda = new JRadioButton("Lambda");
		lambda.setMnemonic(KeyEvent.VK_B);
        lambda.setActionCommand("Lambda");
        lambda.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent event){
        		Universe.curProfile.setEmptyString(Universe.curProfile.lambda);
        	}
        });
        JRadioButton epsilon = new JRadioButton("Epsilon");
        epsilon.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent event){
        		Universe.curProfile.setEmptyString(Universe.curProfile.epsilon);
        	}
        });
        epsilon.setMnemonic(KeyEvent.VK_C);
        epsilon.setActionCommand("Epsilon");
        
        if(current.getEmptyString().equals(current.lambda))	lambda.setSelected(true);
        else if(current.getEmptyString().equals(current.epsilon)) epsilon.setSelected(true);
        
        ButtonGroup group = new ButtonGroup();
        group.add(lambda);
        group.add(epsilon);
        

		JPanel panel = new JPanel();
		panel.add(lambda);
		panel.add(epsilon);
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		
		JButton accept = new JButton("Accept");
		accept.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				frame.setVisible(false);
				Universe.curProfile.savePreferences();
			}
		});

		frame.getContentPane().add(accept, BorderLayout.SOUTH);
		frame.pack();
		Point point = new Point(100, 50);
		frame.setLocation(point);
		frame.setVisible(true);
	}
}
