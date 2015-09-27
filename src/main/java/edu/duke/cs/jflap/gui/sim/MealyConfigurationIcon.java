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

import java.awt.Component;
import java.awt.Graphics2D;

import edu.duke.cs.jflap.automata.Configuration;
import edu.duke.cs.jflap.automata.mealy.MealyConfiguration;

/**
 * 
 * @author Jinghui Lim
 *
 */
public class MealyConfigurationIcon extends ConfigurationIcon 
{
    public MealyConfigurationIcon(Configuration configuration)
    {
        super(configuration);
    }

    public void paintConfiguration(Component c, Graphics2D g, int width, int height)
    {
        super.paintConfiguration(c, g, width, height);
        MealyConfiguration config = (MealyConfiguration) getConfiguration();
        // Draw the torn tape with the rest of the input.
        Torn.paintString((Graphics2D)g, config.getInput(),
                 RIGHT_STATE.x+5.0f,
                 ((float)super.getIconHeight())*0.5f, 
                 Torn.MIDDLE, width-RIGHT_STATE.x-5.0f,
                 false, true, config.getInput().length()-
                 config.getUnprocessedInput().length());
        // Draw the stack.
        Torn.paintString((Graphics2D)g, config.getOutput(),
                 BELOW_STATE.x, BELOW_STATE.y + 5.0f,
                 Torn.TOP, getIconWidth(), false, true, -1);
    }
}
