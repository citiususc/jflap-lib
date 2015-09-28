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

import java.awt.font.*;
import java.awt.geom.*;
import java.awt.*;

/**
 * A <code>Text</code> object is a <code>String</code> that contains information
 * on how to paint itself on a {@link edu.duke.cs.jflap.gui.pumping.Canvas}.
 * 
 * @author Jinghui Lim
 * @see edu.duke.cs.jflap.gui.pumping.Canvas
 *
 */
public class Text 
{
    /**
     * A space.
     */
    public static final Text SPACE = new Text("_");
    /**
     * Using a default monospace font.
     */
    protected static final Font FONT = new Font("Courier", Font.BOLD, 20);
    /**
     * The string this object represents.
     */
    protected String myText;
    /**
     * The position of the bottom left hand corner of this text on the 
     * <code>Canvas</code>.
     */
    protected Point2D.Double myBottomLeft;       
    
    /**
     * Constructs a <code>Text</code> object with an empty string.
     *
     */
    public Text()
    {
        this("");
    }
    
    /**
     * Contructs a <code>Text</code> object with an input string.
     * 
     * @param s the string this <code>Text</code> represents
     */
    public Text(String s)
    {
        myText = s;
        myBottomLeft = new Point2D.Double(0, 0);
    }
    
    /**
     * Constructs a <code>Text</code> object with an input string and
     * a position.
     * 
     * @param s the string this <code>Text</code> reprsents
     * @param p the position this <code>Text</code> is at
     */
    public Text(String s, Point2D p)
    {
        myText = s;
        myBottomLeft = new Point2D.Double(p.getX(), p.getY());
    }
    
    /**getgraphics
     * Constructs a new <code>Text</code> object with the same fields as
     * <code>t</code>.
     * 
     * @param t the <code>Text</code> to make a copy of
     */
    public Text(Text t)
    {
        myText = t.myText;
        myBottomLeft = new Point2D.Double(t.myBottomLeft.getX(), t.myBottomLeft.getY());
    }
    
    /**
     * Set the position of the bottom left corner of this <code>Text</code>
     * 
     * @param p the position to set it to
     */
    public void setPos(Point2D p)
    {
        myBottomLeft = new Point2D.Double(p.getX(), p.getY());
    }
    
    /**
     * Returns the position of the bottom left corner this <code>Text</code>.
     * 
     * @return the position of this text
     */
    public Point2D.Double getPos()
    {
        return myBottomLeft;
    }
    
    /**
     * Move this <code>Text</code> by the values in <code>p</code>.
     * 
     * @param p the amount to move the <code>Text</code> by
     */
    public void move(Point2D p)
    {
        myBottomLeft.x += p.getX();
        myBottomLeft.y += p.getY();
    }
    
    /**
     * Returns the width of this <code>Text</code> object.
     * 
     * @param pen the <code>Graphics</code> this object will be painted with
     * @return the width of this object
     */
    public double getWidth(Graphics pen)
    {
        if(myText.length() == 0)
            return 0;
        
        Graphics2D p = (Graphics2D)pen;
        FontRenderContext frc = p.getFontRenderContext();
        TextLayout layout = new TextLayout(myText, FONT, frc);
        return layout.getBounds().getWidth();
    }
    
    /**
     * Returns the height of "b". Different letters return different heights,
     * so we use the tallest letter available to ensure we get the same kind
     * of spacing.
     * 
     * @param pen the <code>Graphics</code> this object will be painted with
     * @return the height of a "b"
     */
    public double getHeight(Graphics pen)
    {
        Graphics2D p = (Graphics2D) pen;
        FontRenderContext frc = p.getFontRenderContext();
        TextLayout layout = new TextLayout("b", FONT, frc);  // use the same letter to determine the height
        return layout.getBounds().getHeight();
    }
    
    /**
     * Paint this object.
     * 
     * @param pen the <code>Graphics</code> to paint this object with
     */
    public void paint(Graphics pen)
    {    	
        if(myText.length() == 0)
            return;
        Graphics2D p = (Graphics2D)pen;
        FontRenderContext frc = p.getFontRenderContext();
        TextLayout layout = new TextLayout(myText, FONT, frc);
        layout.draw(p, (float)myBottomLeft.getX(), (float)myBottomLeft.getY());
    }
    
    /**
     * This returns a <code>Text</code> object that acts as a label to the give
     * <code>Text</code>, <code>text</code>.
     * 
     * @param pen the <code>Graphics</code> to paint the text with
     * @param text the <code>Text</code> we want a label for
     * @param label the string the label should show
     * @return a <code>Text</code> object that is a lable for the input
     */
    public static Text getLabel(Graphics pen, Text text, String label)
    {
        Text temp = new Text(label);
        double x = text.getPos().x + text.getWidth(pen) / 2 - temp.getWidth(pen) / 2; 
        double y = text.getPos().y - 2 * text.getHeight(pen);
        temp.setPos(new Point2D.Double(x, y));
        return temp;
    }
    
    /**
     * Returns the <code>String</code> this <code>Text</code> object holds.
     * 
     * @return the string of this <code>Text</code> object
     */
    public String toString()
    {
        return myText;
    }
}
