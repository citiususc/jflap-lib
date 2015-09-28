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

import edu.duke.cs.jflap.pumping.ContextFreePumpingLemma;

/**
 * This is a subclass of <code>ComputerFirstPane</code> that deals with
 * context-free pumping lemmas.
 *
 * @author Chris Morgan & Jinghui Lim
 * @see edu.duke.cs.jflap.pumping.ContextFreePumpingLemma
 *
 */
public class CompCFPumpingLemmaInputPane extends ComputerFirstPane {

	/**
     * Creates a <code>CompCFPumpingInputPane</code> for a <code>ContextFreePumpingLemma</code>.
     *
     * @param l the <code>ContextFreePumpingLemma</code> we want to run
     */
	public CompCFPumpingLemmaInputPane(ContextFreePumpingLemma l)
    {
        super(l, "<i>L</i> = {" + l.getHTMLTitle() + "} Context-Free Pumping Lemma");
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
         * Has w been entered?
         *
         * If it hasn't, no point doing the rest.
         * If it has, then go on and load w.
         */
    	int[] decomp = pl.getDecomposition();    	
        if(decomp[0] == 0 && decomp[1] == 0 && decomp[2] == 0 && decomp[3] == 0)        	
            return;
        
        myWDisplay.setText(pl.getW());

        /*
         * Regardless of whether the decomposition has been set, we load
         * the stuff in the sliders and table, and send the temporary
         * decomposition to the case panel. Even if it hasn't been chosen,
         * it won't cause anything to go wrong.
         *
         */
        int[] decomposition = new int[]{pl.getU().length(), pl.getV().length(),
            pl.getX().length(), pl.getY().length()};        

        setDecomposition(decomposition, pl.getI());
        if (myCases != null)
        	myCases.setDecomposition(decomposition);
        decompLabel.setText(myLemma.getDecompositionAsString());
        
        stages[3].setVisible(true);
        stages[4].setVisible(true);
        
        if (pl.getI() == -1)
        	return;
        
        stages[5].setVisible(true);
        displayIEnd();
        stageMessages[5].setText("Click \"Restart\" to restart the animation.");
        stageMessages[5].setVisible(true);
        myCanvas.setRestartEnabled(true);
        if (myCases != null)
        	myCases.setAddReplaceButtonsEnabled(true);          
    }
}