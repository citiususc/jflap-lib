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

import java.awt.*;
import javax.swing.*;

import edu.duke.cs.jflap.pumping.ContextFreePumpingLemma;

/**
 * This is a subclass of <code>HumanFirstPane</code> that deals with
 * context-free pumping lemmas.
 *
 * @author Jinghui Lim & Chris Morgan
 * @see edu.duke.cs.jflap.pumping.ContextFreePumpingLemma
 *
 */
public class HumanCFPumpingLemmaInputPane extends HumanFirstPane
{
    /**
     * Message displayed if computer wins.
     */
    private static String I_WIN = "I WIN. Do you want to play again or concede "
+
        "that the language is not context-free?";
    /**
     * Message displayed if player wins.
     */
    private static String YOU_WIN = "YOU WIN. Does this mean the language is context-free?";
    /**
     * The <code>SliderPanel</code> that controls <i>u</i> of the decomposition.
     */
    private SliderPanel myUPanel;
    /**
     * The <code>SliderPanel</code> that controls <i>v</i> of the decomposition.
     */
    private SliderPanel myVPanel;

    /**
     * Creates a <code>HumanCFPumpingInputPane</code> for a <code>ContextFreePumpingLemma</code>.
     *
     * @param l the <code>ContextFreePumpingLemma</code> we want to run
     */
    public HumanCFPumpingLemmaInputPane(ContextFreePumpingLemma l)
    {
        super(l, "<i>L</i> = {" + l.getHTMLTitle() + "} Context-Free Pumping Lemma");
    }

    
    protected void addDecompPanelGameFeatures(JPanel n){
    	decompButtonTitle = new String("Set uvxyz");
    	n.setMaximumSize(new Dimension(MAX_SIZE.width, 9*MAX_SIZE.height/20));
        n.setPreferredSize(new Dimension(MAX_SIZE.width, 9*MAX_SIZE.height/20));
    	
        myUPanel = new SliderPanel("u", this);
        sliderPanel.add(myUPanel);

        myVPanel = new SliderPanel("v", this);
        sliderPanel.add(myVPanel);
        n.setBorder(BorderFactory.createTitledBorder("3. Select decomposition of w into uvxyz.")); 
    }

    /**
     * After <i>m</i> is chosen, prompts the lemma for <i>w</i>
     * and displays it, and sets up the sliders and table accordingly.
     *
     */
    protected void mEnteredReset()
    {
    	/*int m = Integer.parseInt(myMDisplay.getText());
    	reset();
    	myMDisplay.setText(Integer.toString(m));*/    	        
    	
        String s = myLemma.getW();
        myWDisplay.setText(s);

        myUPanel.setSliderMax(s.length());
        myUPanel.setText(s);
        myUPanel.setVal(0);

        myVPanel.setSliderMax(s.length());
        myVPanel.setText(s);
        myVPanel.setVal(0);

        myXPanel.setSliderMax(s.length());
        myXPanel.setText(s);
        myXPanel.setVal(0);

        myYPanel.setSliderMax(s.length());
        myYPanel.setText(s);
        myYPanel.setVal(0);

        refresh();
        updateTable();
        leftPanel.revalidate();
    }

    
    protected void resetDecompPanel()
    {
        myUPanel.reset();
        myVPanel.reset();
        myXPanel.reset();
        myYPanel.reset();
        myZDisplay.setText("");
        myZLength.setText("");
        updateTable();
    }
    
    /**
     * Resets the various fields of <i>u</i>, <i>v</i>, <i>x</i>, <i>y</i>,
     * and <i>z</i>, the display for <i>i</i> and the pumped string, and
     * the canvas.
     */
    protected void refresh()
    {
        try
        {
            /*
             * Reset the range of each panel depending on the values of the
             * other panels.
             */
            myUPanel.setRange(0, myVPanel.getVal());
            myVPanel.setRange(myUPanel.getVal(), myXPanel.getVal());
            myXPanel.setRange(myVPanel.getVal(), myYPanel.getVal());
            myYPanel.setRange(myXPanel.getVal(), myLemma.getW().length());
            /*
             * Set the text and length of z.
             */
            myZDisplay.setText(myLemma.getW().substring(myYPanel.getVal()));
            myZLength.setText(Integer.toString(myZDisplay.getText().length()));
            /*
             * Clear the display of i and the pumped string, and reset the
             * canvas.
             */
            myIDisplay.setText("");
            myPumpedStringDisplay.setText("");
            myLastWord.setText("");
            myCanvas.reset();
            /*
             * Check various conditions.
             */
            if(myUPanel.getVal() > myVPanel.getVal())
            {
                myZDisplay.setText("");
                stageMessages[3].setText("Condition violated: |v| >= 0");
                myZLength.setText("");
                myDecompButton.setEnabled(false);
            }
            else if(myVPanel.getVal() > myXPanel.getVal())
            {
                myZDisplay.setText("");
                stageMessages[3].setText("Condition violated: |x| >= 0");
                myZLength.setText("");
                myDecompButton.setEnabled(false);
            }
            else if(myXPanel.getVal() > myYPanel.getVal())
            {
                myZDisplay.setText("");
                stageMessages[3].setText("Condition violated: |y| >= 0");
                myZLength.setText("");
                myDecompButton.setEnabled(false);
            }
            else if(myYPanel.getVal() - myUPanel.getVal() > myLemma.getM())
            {
                myZDisplay.setText("");
                stageMessages[3].setText("Condition violated: |vxy| <= m");
                myZLength.setText("");
                myDecompButton.setEnabled(false);
            }
            else if((myVPanel.getVal() - myUPanel.getVal()) +
                    (myYPanel.getVal() - myXPanel.getVal()) < 1)
            {
                myZDisplay.setText("");
                stageMessages[3].setText("Condition violated: |vy| >= 1");
                myZLength.setText("");
                myDecompButton.setEnabled(false);
            }
            else
            {
            	stageMessages[3].setText("Click \"Set uvxyz\" to set decomposition.");
                myDecompButton.setEnabled(true);
            }
        }
        catch(StringIndexOutOfBoundsException e)
        {
            /*
             * Refresh is triggered whenever the slider is moved or the
             * range is reset. This can cause multiple exceptions when
             * we are merely changing the max of the slider or adjusting
             * the slider. Thus, the exception is ignored.
             */
        }
    }

    
    public void setDecomposition(int[] decomposition)
    {
        int u = decomposition[0];
        int v = decomposition[1];
        int x = decomposition[2];
        int y = decomposition[3];

        myUPanel.setVal(u);
        myVPanel.setVal(u + v);
        myXPanel.setVal(u + v + x);
        myYPanel.setVal(u + v + x + y);

        myLemma.setDecomposition(decomposition);
    }    
    
    /**
     * Calculates <i>i</i> and the string <i>uv<sup>i</sup>xy<sup>i</sup>z</i>.
     * based on the user's decomposition of <i>uvxyz</i>.
     */
    protected void setI()
    {    	
        int[] d = new int[]{myUPanel.getVal(), myVPanel.getVal() - myUPanel.getVal(),
                myXPanel.getVal() - myVPanel.getVal(), myYPanel.getVal() - myXPanel.getVal(),
                myLemma.getW().length() - myYPanel.getVal()};
        myLemma.setDecomposition(d);
        myLemma.chooseI();
        if(myCases != null)
        {
            myCases.setDecomposition(d);
            myCases.setI(myLemma.getI());
        }
    }

    /**
     * Initializes the animation canvas with the values of <i>u</i>, <i>v</i>,
     * <i>x</i>, <i>y</i>, and <i>z</i>.
     *
     */
    protected void setCanvas()
    {
    	stages[5].setVisible(true);
        myCanvas.reset();
        myCanvas.addText("w =");
        myCanvas.addText(((ContextFreePumpingLemma)myLemma).getU(), "u");
        myCanvas.addText(((ContextFreePumpingLemma)myLemma).getV(), "v");
        myCanvas.addText(((ContextFreePumpingLemma)myLemma).getX(), "x");
        myCanvas.addText(((ContextFreePumpingLemma)myLemma).getY(), "y");
        myCanvas.addText(((ContextFreePumpingLemma)myLemma).getZ(), "z");
        myCanvas.moveText(new int[]{0, 1, myLemma.getI(), 1, myLemma.getI(), 1});
        myStepAnimation.setEnabled(true);
        myStartAnimation.setEnabled(false);
        repaint();
    }

    /**
     * Creates an HTML string <i>uv<sup>i</sup>xy<sup>i</sup>z</i>, with the
     * real value of <i>i</i> instead of the variable <i>i</i>.
     *
     * @return a string representing <i>uv<sup>i</sup>xy<sup>i</sup>z</i>
     */
    protected String createXYZ()
    {
        return "<i>uv</i><sup>" + myLemma.getI() + "</sup><i>xy</i><sup>" +
        		myLemma.getI() + "</sup><i>z</i>";
    }


    public void update()
    {
        ContextFreePumpingLemma pl = (ContextFreePumpingLemma)myLemma;
        stageMessages[0].setText("File loaded.");
        updateTopPane(false);
        /*
         * Has m been entered?
         *
         * If it hasn't, no point doing the rest.
         * If it has, then go on and load m and w.
         */
        if(pl.getM() == -1)        	
            return;
        
        myMDisplay.setText("" + pl.getM());
        myWDisplay.setText(pl.getW());

        /*
         * Regardless of whether the decomposition has been set, we load
         * the stuff in the sliders and table, and send the temporary
         * decomposition to the case panel. Even if it hasn't been chosen,
         * it won't cause anything to go wrong.
         *
         * Everything below has to be done in a specific order.
         */
        int[] decomposition = new int[]{pl.getU().length(), pl.getV().length(),
            pl.getX().length(), pl.getY().length()};

        /*
         * We need to call SliderPanel.setText and SliderPanel.setSliderMax
         * before calling setDecomposition. Otherwise, the sliders' default
         * max is 10, and if we try to set the value anything above 10, we
         * will just get 10.
         */
        myUPanel.setText(pl.getW());
        myUPanel.setSliderMax(pl.getW().length());
        myVPanel.setText(pl.getW());
        myVPanel.setSliderMax(pl.getW().length());
        myXPanel.setText(pl.getW());
        myXPanel.setSliderMax(pl.getW().length());
        myYPanel.setText(pl.getW());
        myYPanel.setSliderMax(pl.getW().length());

        setDecomposition(decomposition, pl.getI());        
        if (myCases != null) {
        	myCases.setDecomposition(decomposition);
        	myCases.setListButtonEnabled(true);
        }                
        updateTable();        
        stages[2].setVisible(true);
    	stages[3].setVisible(true);
        /*
         * If the decomposition hasn't been set, i.e. the sliders are all set to zero,
         * don't allow setting of decomposition.
         *
         * Else, initialize animation etc.
         */
        if(pl.getU().length() == 0 && pl.getV().length() == 0 &&
            pl.getX().length() == 0 && pl.getY().length() == 0)
        {
            myDecompButton.setEnabled(false);
        }
        else
        {
        	stages[4].setVisible(true);
        	stages[5].setVisible(true);
        	stageMessages[5].setText("Click \"Restart\" to restart the animation.");
        	stageMessages[5].setVisible(true);
        	if (myCases != null)
        		myCases.setAddReplaceButtonsEnabled(true);
            displayIEnd();
            myCanvas.setRestartEnabled(true);
        }
    }
}


