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

import java.awt.BorderLayout;

import javax.swing.*;
import javax.swing.event.*;

/**
 * A <code>SliderPanel</code> object allows the user to choose the 
 * decomposition of <i>w</i> with sliders, and will display the the 
 * string segement as well as its length in text areas.
 * 
 * @author Jinghui Lim
 * @see edu.duke.cs.jflap.gui.pumping.PumpingLemmaInputPane
 *
 */
public class SliderPanel extends JPanel 
{
    private static int START_MAX = 10;
    /**
     * The parent <code>PumpingLemmaInputPane</code> of this <code>SliderPanel</code>.
     */
    private HumanFirstPane myPane;
    /**
     * The text area that displays the string segment.
     */
    private JTextArea myDisplay;
    /**
     * The text area that displays the length of the string segment.
     */
    private JTextArea myLengthDisplay;
    /**
     * The slider that allows the user to choose a the decomposition.
     */
    private JSlider mySlider;
    /**
     * The index of the first letter of the string segment.
     */
    private int myMin;
    /**
     * The index of the last letter of the string segment.
     */
    private int myMax;
    /**
     * The entire string <i>w</i>. The string segment that this
     * <code>SliderPanel</code> is concerned with is set with
     * <code>myMin</code> and the value of <code>mySlider</code>.
     */
    private String myText;
    
    /**
     * Constructs the slider panel from the "name" of the string 
     * segment given with the <code>PumpingInputPane</code> as its
     * "parent" pane.
     * 
     * @param s the name of the string segment
     * @param p the parten <code>PumpingInputPane</code>
     */
    public SliderPanel(String s, HumanFirstPane p) 
    {
        super(new BorderLayout());
        myPane = p;
        
        myDisplay = new JTextArea(1, 30);
        myDisplay.setEditable(false);
        myLengthDisplay = new JTextArea(1, 5);
        myLengthDisplay.setEditable(false);
        JPanel displayPanel = new JPanel();
        displayPanel.setLayout(new BoxLayout(displayPanel, BoxLayout.X_AXIS));
        displayPanel.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 7));
        displayPanel.add(new JLabel(s + ": "));
        displayPanel.add(myDisplay);
        displayPanel.add(new JLabel("    |" + s + "|: "));
        displayPanel.add(myLengthDisplay);
        add(displayPanel, BorderLayout.CENTER);
        mySlider = new JSlider(0, START_MAX);
        mySlider.setValue(0);
        mySlider.setPaintTicks(true);
        mySlider.setMinorTickSpacing(1);
        mySlider.addChangeListener(new ChangeListener()
            {
                public void stateChanged(ChangeEvent e) 
                {
                    refresh();
                    myPane.refresh();
                }
            });
        add(mySlider, BorderLayout.SOUTH);
        myMin = 0;
        myMax = 0;
        myText = "";
    }
    
    /**
     * Sets the minimum and maximum of the segment (which is a
     * substring of <i>w</i>.
     * 
     * @param min the index of the first letter of the segment
     * @param max the index of the last letter of the segment
     * @see #setText(String)
     */
    public void setRange(int min, int max)
    {
        myMax = max;
        setMin(min);
    }
    
    /**
     * Sets the minimum of this string segment.
     * 
     * @param min the index of the first letter of the segment
     * @see #setRange(int, int)
     */
    public void setMin(int min)
    {
        myMin = min;
        refresh();
    }
    
    /**
     * Sets the maximum of the slider. This is different from
     * {@link #setMin(int)} and {@link #setRange(int, int)} in
     * that it sets the maximum of the slider, not the string
     * segement. Usually, <code>i</code> = |<i>w</i>|.
     * 
     * @param i the maximum of the slider
     */
    public void setSliderMax(int i)
    {
        mySlider.setMaximum(i);
    }
    
    /**
     * Sets the values of the slider.
     * 
     * @param i the value of the slider
     */
    public void setVal(int i)
    {
        mySlider.setValue(i);
        refresh();
//        getRootPane().repaint();
    }
    
    /**
     * Sets the string of this <code>SliderPanel</code>. The 
     * paramter <code>s</code> should be <i>w</i>. The string segment
     * then depends on values set in {@link #setRange(int, int)} or
     * {@link #setMin(int)}.  
     * 
     * @param s the string to set
     */
    public void setText(String s)
    {
        myText = s;
        refresh();
    }
    
    /**
     * Updates the various text areas depending on the value of
     * the slider.
     *
     */
    public void refresh()
    {
        try
        {
            int i = mySlider.getValue();
            if(i > myMax)
                i = myMax;
            if(mySlider.getValue() < myMin)
                mySlider.setValue(myMin);
            myDisplay.setText(myText.substring(myMin, i));
            myLengthDisplay.setText("" + (mySlider.getValue() - myMin));
//            myPane.stages[4].setVisible(false);
//            myPane.stages[5].setVisible(false);
//            myPane.updateLeftPanel();
            
//            getRootPane().repaint();
            repaint();
        }
        catch(StringIndexOutOfBoundsException e)
        {
            /*
             * Refresh is triggered whenever the slider is moved or the 
             * range is reset. This can cause multiple exceptions when
             * we are merely adjusting the max of the slider. Thus, the
             * exception is ignored. 
             */
        }
//        getRootPane().repaint();
    }
    
    /**
     * Returns the value the slider is at.
     * 
     * @return the value the slider is at
     */
    public int getVal()
    {
        return mySlider.getValue();
    }
    
    /**
     * Resets the <code>SliderPanel</code>. Clears the string and sets 
     * the slider to zero.
     *
     */
    public void reset()
    {
        mySlider.setMaximum(START_MAX);
        setVal(0);
        setText("");
        myLengthDisplay.setText("");
    }
}
