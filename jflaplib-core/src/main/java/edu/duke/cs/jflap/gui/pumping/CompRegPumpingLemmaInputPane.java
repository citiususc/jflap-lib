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

import edu.duke.cs.jflap.pumping.RegularPumpingLemma;

/**
 * This is a subclass of <code>ComputerFirstPane</code> that deals with
 * regular pumping lemmas.
 *
 * @author Chris Morgan & Jinghui Lim
 * @see edu.duke.cs.jflap.pumping.ContextFreePumpingLemma
 *
 */
public class CompRegPumpingLemmaInputPane extends ComputerFirstPane {

	/**
     * Creates a <code>CompRegPumpingInputPane</code> for a <code>RegularPumpingLemma</code>.
     *
     * @param l the <code>RegularPumpingLemma</code> we want to run
     */
	public CompRegPumpingLemmaInputPane(RegularPumpingLemma l)
    {		
        super(l, "<i>L</i> = {" + l.getHTMLTitle() + "} Regular Pumping Lemma");
    }				    
	
	 /**
     * Initializes the animation canvas with the values of <i>x</i>, <i>y</i>, 
     * and <i>z</i>.
     * 
     */
    protected void setCanvas()
    {
    	stages[5].setVisible(true);
        myCanvas.reset();
        myCanvas.addText("w =");
        myCanvas.addText(((RegularPumpingLemma)myLemma).getX(), "x");
        myCanvas.addText(((RegularPumpingLemma)myLemma).getY(), "y");
        myCanvas.addText(((RegularPumpingLemma)myLemma).getZ(), "z");
        myCanvas.moveText(new int[]{0, 1, myLemma.getI(), 1});
        myStepAnimation.setEnabled(true);
        myStartAnimation.setEnabled(false);
        repaint();
    }
    
    /**
     * Creates an HTML string <i>xy<sup>i</sup>z</i>, with the real value of <i>i</i>
     * instead of the variable <i>i</i>.
     * 
     * @return a string representing <i>xy<sup>i</sup>z</i>
     */
    protected String createXYZ()
    {
        return "<i>xy</i><sup>" + myLemma.getI() + "</sup><i>z</i>";
    }
	
    
	public void update() 
	{
		RegularPumpingLemma pl = (RegularPumpingLemma)myLemma;
		stageMessages[0].setText("File loaded.");
		updateTopPane(false);
		
		int[] decomp = pl.getDecomposition();		
        if(decomp[0] == 0 && decomp[1] == 0)        	
            return;
        
        myWDisplay.setText(pl.getW());
        
        setDecomposition(new int[]{pl.getX().length(), pl.getY().length()}, pl.getI());
        decompLabel.setText(myLemma.getDecompositionAsString());                
        
        stages[3].setVisible(true);
        stages[4].setVisible(true);
        
        if (pl.getI() == -1)
        	return;
        
        stages[5].setVisible(true);
        displayIEnd();
        myCanvas.setRestartEnabled(true);
        stageMessages[5].setText("Click \"Restart\" to restart the animation.");
        stageMessages[5].setVisible(true);
	}
}
