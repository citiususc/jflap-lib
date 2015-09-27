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





package edu.duke.cs.jflap.gui.sim;

import edu.duke.cs.jflap.gui.TooltipAction;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JToolBar;

/**
 * This is a control panel with buttons for invoking methods on a configuration
 * controller.
 * 
 * @author Thomas Finley
 */

public class ControlPanel extends JToolBar {
	/**
	 * Instantiates a new <CODE>ControlPanel</CODE> for the given
	 * configuration controller.
	 * 
	 * @param controller
	 *            the configuration controller object
	 */
	public ControlPanel(ConfigurationController controller) {
		this.controller = controller;
		initView();
	}

	/**
	 * Returns the configuration controller object this panel controls.
	 * 
	 * @return the configuration controller object this panel controls
	 */
	public ConfigurationController getController() {
		return controller;
	}

	/**
	 * A simple helper function that initializes the gui.
	 */
	protected void initView() {
		this.add(new TooltipAction("Step", "Moves existing valid "
				+ "configurations to the next " + "configurations.") {
			public void actionPerformed(ActionEvent e) {
				controller.step(blockStep);
			}
		});

		this.add(new TooltipAction("Reset", "Resets the simulation to "
				+ "start conditions.") {
			public void actionPerformed(ActionEvent e) {
				controller.reset();
			}
		});

        /*
         * Add Focus and Defocus buttons only if it is a Turing machine.
         */
        if(controller.isTuringMachine()) {
    		this.add(new AbstractAction("Focus") {
    			public void actionPerformed(ActionEvent e) {
    				controller.focus();
    			}
    		});
    
    		this.add(new AbstractAction("Defocus") {
    			public void actionPerformed(ActionEvent e) {
    				controller.defocus();
    			}
    		});
        }
		this.add(new AbstractAction("Freeze") {
			public void actionPerformed(ActionEvent e) {
				controller.freeze();
			}
		});

		this.add(new AbstractAction("Thaw") {
			public void actionPerformed(ActionEvent e) {
				controller.thaw();
			}
		});

		this.add(new AbstractAction("Trace") {
			public void actionPerformed(ActionEvent e) {
				controller.trace();
			}
		});

		this.add(new AbstractAction("Remove") {
			public void actionPerformed(ActionEvent e) {
				controller.remove();
			}
		});
	}


	/**
	 * @param blockStep
	 */
	public void setBlock(boolean step) {
		blockStep = step;

	}

	private boolean blockStep = false;

	/** The configuration controller object. */
	private ConfigurationController controller;

}
