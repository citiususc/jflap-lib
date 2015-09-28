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





package edu.duke.cs.jflap.gui.pumping;

import java.awt.geom.*;

/**
 * A <code>MovingText</code> object is a <code>Text</code> object that allows movement
 * of the text. It contains extra fields that record the start and end position, and
 * the size of each step.
 * 
 * @author Jinghui Lim
 * @see edu.duke.cs.jflap.gui.pumping.Canvas
 *
 */
public class MovingText extends Text 
{
    /**
     * The number of steps each movement should take.
     */
    public static int STEPS = 50;
    /**
     * The original position.
     */
    private Point2D.Double myStart;
    /**
     * The final position.
     */
    private Point2D.Double myFinish;
    /**
     * The size of each step.
     */
    private Point2D.Double myStep;
    
    /**
     * Constructs a <code>MovingText</code> object with a finish point. This 
     * constructor makes a copy of the <code>Text</code> object such that the
     * original copy will still be drawn.
     * 
     * @param t the <code>Text</code> we wish to construct a moving copy of
     * @param finish the final position
     */
    public MovingText(Text t, Point2D finish)
    {
        super(t);
        myStart = new Point2D.Double(t.getPos().getX(), t.getPos().getY());
        myFinish = new Point2D.Double(finish.getX(), finish.getY());
        myStep = new Point2D.Double((myFinish.getX() - myStart.getX()) / STEPS,
                (myFinish.getY() - myStart.getY()) / STEPS);
    }
    
    /**
     * Moves the <code>MovingText</code> object one step and returns
     * <code>true</code> if it has reached its final position and 
     * <code>false</code> otherwise.
     * 
     * @return <code>true</code> if it has reached its final position, 
     * <code>false</code> otherwise
     */
    public boolean move()
    {
        if(myStep.x > 0)
        {
            if(myBottomLeft.x > myFinish.x)
                return true;
        }
        else
        {
            if(myBottomLeft.x < myFinish.x)
                return true;
        }
        
        if(myStep.y > 0)
        {
            if(myBottomLeft.y > myFinish.y)
                return true;
        }
        else
        {
            if(myBottomLeft.y < myFinish.y)
                return true;
        }
        myBottomLeft.x += myStep.x;
        myBottomLeft.y += myStep.y;
        return false;
    }
    
    /**
     * "Finalizes" the position of this and returns a this as a
     * <code>Text</code> object. After this is called, this
     * <code>MovingText</code> object should be treated as a
     * <code>Text</code> object.
     * 
     * @return a <code>Text</code> object whose position is the
     * final position of this <code>MovingText</code> object
     */
    public Text finalText()
    {
        myBottomLeft = myFinish;
        return (Text) this;
    }
}
