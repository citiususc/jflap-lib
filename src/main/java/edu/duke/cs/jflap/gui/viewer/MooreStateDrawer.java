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




package edu.duke.cs.jflap.gui.viewer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Rectangle2D;

import edu.duke.cs.jflap.automata.Automaton;
import edu.duke.cs.jflap.automata.State;
import edu.duke.cs.jflap.automata.mealy.MooreMachine;

/**
 * This is a special subclass of a <code>StateDrawer</code> that 
 * draws the output of states for Moore machines. The output is
 * obtained by calling {@link edu.duke.cs.jflap.automata.mealy.MooreMachine#getOutput(State)}.
 * 
 * @author Jinghui Lim
 *
 */
public class MooreStateDrawer extends StateDrawer 
{
    /**
     * The default constructor for a <code>MooreStateDrawer</code>.
     *
     */
    public MooreStateDrawer() 
    {
        super();
    }

    /**
     * Creates a <code>MooreStateDrawer</code> with states drawn to
     * a particular radius.
     * 
     * @param radius the radius of the states drawn
     */
    public MooreStateDrawer(int radius) 
    {
        super(radius);
    }
    
    /**
     * Draws the state by calling 
     * {@link StateDrawer#drawState(Graphics, Automaton, State, Point, Color)}
     * then draws the output of this Moore machine state.
     * 
     * @param g the graphics object to draw upon
     * @param state the state to draw
     * @param automaton the automaton this state is a part of
     * @param point the point to draw the state at
     * @param color the color of the inner portion of the state
     */
    public void drawState(Graphics g, Automaton automaton, State state, Point point, Color color)
    {
        super.drawState(g, automaton, state, point, color);
        drawStateOutput(g, state, point, color);
    }
    
    /**
     * Draws the state output.
     * 
     * @param g the graphics object to draw upon
     * @param state the state to draw
     * @param point the point to draw the state at
     * @param color the color of the inner portion of the state
     */
    private void drawStateOutput(Graphics g, State state, Point point, Color color) 
    {
        String output = ((MooreMachine) state.getAutomaton()).getStateDescription(state);
        
        int ascent = g.getFontMetrics().getAscent();
        int heights = 0;
        int textWidth = 0;
        
        Rectangle2D bounds = g.getFontMetrics().getStringBounds(output, g);
        textWidth = Math.max((int) bounds.getWidth(), textWidth);
        heights += ascent + STATE_LABEL_PAD;
        
        heights -= STATE_LABEL_PAD;
        
        // Width of the box.
        int width = textWidth + (STATE_LABEL_PAD<<1);
        int height = heights + (STATE_LABEL_PAD<<1);
        // Upper corner of the box.
        int x = point.x - (width>>1) + STATE_RADIUS;
        int y = point.y - STATE_RADIUS - height/2;
        // Where the y point of the baseline is.
        int baseline = y;
        
        g.setColor(color);
        g.fillRect(x, y, width, height);
        g.setColor(Color.black);
        baseline += ascent + STATE_LABEL_PAD;
        g.drawString(output, x + STATE_LABEL_PAD, baseline);
        g.drawRect(x, y, width, height);
    }
}
