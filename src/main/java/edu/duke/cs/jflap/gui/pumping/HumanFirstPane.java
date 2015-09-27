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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.duke.cs.jflap.pumping.PumpingLemma;

/**
 * This class represents the implementation of <code>PumpingLemmaInputPane</code> wherein 
 * the user makes the first move.  The user will choose the <i>m</i> and decomposition values, 
 * while the computer will generate the <i>w</i> and <i>i</i> values based on the user's input.
 * The decomp panel has a network of sliders and displays allowing for the user to choose the decomposition.
 * 
 * @author Chris Morgan & Jinghui Lim
 */
public abstract class HumanFirstPane extends PumpingLemmaInputPane {
	/**
	 * The goal of the user, which is to try to find a valid pumping lemma.
	 */
	private static String OBJECTIVE = "Find a valid partition that can be pumped.";
    /**
     * The instruction that prompts for <i>m</i>.
     */
	private static String PROMPT_M = "Please select a value for m in Box 1 and press \"Enter\".";
    /**
     * The description that explains the selection of <i>w</i>.
     */
	private static String DESCRIBE_W = "I have selected w such that |w| >= m. It is displayed in Box 2.";
    /**
     * The instruction that prompts for the decomposition of <i>w</i>.
     */
	private static String PROMPT_DECOMPOSITION = "Please select the decomposition of w in Box 3 using the sliders.";
    /**
     * The description that explains the selection of <i>i</i>.
     */
	private static String DESCRIBE_I = "I have selected i to give a contradition. It is displayed in Box 4.";
    /**
     * This string allows subclasses to set the title of myDecompButton.
     */
    protected String decompButtonTitle;
    /**
     * The <code>SliderPanel</code> that controls <i>x</i> of the decomposition.
     */
    protected SliderPanel myXPanel;
    /**
     * The <code>SliderPanel</code> that controls <i>y</i> of the decomposition.
     */
    protected SliderPanel myYPanel;
    /**
     * The button that enters the decomposition into the lemma.
     */
    protected JButton myDecompButton;
    /**
     * The text area that displays <i>z</i> of the decomposition or a short
	 * message indicating a condition violated in selecting the decomposition.
     */
    protected JTextArea myZDisplay;
    /**
     * The text area that displays the length of <i>z</i>.
     */
    protected JTextArea myZLength;
    /**
     * The table that displays the string <i>w</i> with each character in a cell
     * for easy visualization.
     */
    protected JTable myXYZDisplay;
    /**
     * The panel that holds both the <i>u</i>, <i>v</i>, <i>x</i>, and <i>y</i>
     * sliders and the table that displays the string <i>w</i>.
     */
    protected JPanel myXYZPanel;
    /**
     * The panel the SliderPanels are put on.  Subclasses can add to it.
     */
    protected JPanel sliderPanel;    
    
	
	public HumanFirstPane(PumpingLemma l, String title) 
	{
		super(l, title);
		l.setFirstPlayer(PumpingLemma.HUMAN);
	}
	
	/**
     * Initializes and returns a panel that allows the user to choose the 
     * decomposition of <i>w</i> into <i>xyz</i> with sliders.
     * The panel also contains a single-row table in which each cell contains
     * one character of <i>w</i> such that each column corresponds to
     * one unit in the sliders. This is done to aid the visualization process.
     * 
     * @return a panel that takes the decomposition from the user
     */
    protected JPanel initDecompPanel()
    {
        JPanel n = new JPanel(new BorderLayout());
        
        JPanel o = new JPanel();
        o.setLayout(new BoxLayout(o, BoxLayout.X_AXIS));
        
        sliderPanel = new JPanel();
        sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.Y_AXIS));
        
        addDecompPanelGameFeatures(n);
        
        myXPanel = new SliderPanel("x", this);
        sliderPanel.add(myXPanel);
        
        myYPanel = new SliderPanel("y", this);
        sliderPanel.add(myYPanel);
        
        JPanel m = new JPanel();    // I'm really running out of letters here
        m.setLayout(new BoxLayout(m, BoxLayout.X_AXIS));
        m.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 7));
        m.add(new JLabel("z: "));
        myZDisplay = new JTextArea(1, 30);
        myZDisplay.setEditable(false);
        m.add(myZDisplay);
        m.add(new JLabel("    |z|: "));
        myZLength = new JTextArea(1, 5);
        myZLength.setEditable(false);
        m.add(myZLength);
        sliderPanel.add(m);        
        
        myXYZPanel = new JPanel(new BorderLayout());
        myXYZDisplay = new JTable(1, 1);
        myXYZDisplay.setEnabled(false);
        myXYZPanel.add(myXYZDisplay);
        myXYZPanel.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 7));       
        sliderPanel.add(myXYZPanel, BorderLayout.SOUTH);
        
        JPanel p = new JPanel(new BorderLayout());
        p.add(stageMessages[3], BorderLayout.WEST);
        sliderPanel.add(p);
        o.add(sliderPanel);
        
        JPanel q = new JPanel();
        q.setLayout(new BoxLayout(q, BoxLayout.Y_AXIS));
        myDecompButton = new JButton(decompButtonTitle);
        myDecompButton.setEnabled(false);
        myDecompButton.addActionListener(new ActionListener()
            {
                public void actionPerformed (ActionEvent ev)
                {	                                        
                	stages[4].setVisible(true);
                	stages[5].setVisible(true);
                	resetMessages();                	
                    setI();
                    displayIEnd();
                    if (myLemma.isInLang(myLemma.createPumpedString()))
                    	myLemma.addAttempt(myLemma.getDecompositionAsString()+"; I = "+myLemma.getI() + "; <i>Won</i>");
                    else
                    	myLemma.addAttempt(myLemma.getDecompositionAsString()+"; I = "+myLemma.getI() + "; <i>Failed</i>");
                    updateTopPane(false);
                    leftPanel.revalidate();
                    setCanvas();
                    myCanvas.stop();                    
                    /*
                     * If there is only one case and myCases will not 
                     * have been initialized.
                     */
                    if(myCases != null)
                        myCases.setAddReplaceButtonsEnabled(true);
                }
            });
        q.add(myDecompButton);
        o.add(q);
        n.add(o, BorderLayout.NORTH);               
        return n;
    }
	
    /**
     * Updates the individual cells in the single-row table such that each
     * show a character of the string. It resizes the table depending on how
     * many characters there are.
     */
    public void updateTable()
    {
        myXYZPanel.remove(myXYZDisplay);
        myXYZDisplay = new JTable(1, myLemma.getW().length());
        myXYZDisplay.setEnabled(false);
        String s = myLemma.getW();
        for(int i = 0; i < s.length(); i++)
            myXYZDisplay.setValueAt(s.substring(i, i + 1), 0, i);
        myXYZPanel.add(myXYZDisplay, BorderLayout.CENTER);
    }
    
    protected String addTopGameFeatures(JButton b) 
    {    	
    	b.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e) 
            {            	
            	myMDisplay.setText("");
            	updateTopPane(false);
                reset();                
            }
        });
    	return OBJECTIVE;
    }
    
	protected String addMGameFeatures() 
	{
		myMDisplay = new JTextField(10);
	    ((JTextField)myMDisplay).addActionListener(new ActionListener()
            {
                public void actionPerformed (ActionEvent ev)
                {
                    try
                    {
                        reset();
                        int m = Integer.parseInt(ev.getActionCommand());
                        if(!(m  >= myLemma.getRange()[0] && m <= myLemma.getRange()[1]))
                            throw new NumberFormatException();
                        myLemma.setM(m);
                        stages[2].setVisible(true);
                        stages[3].setVisible(true);                      
                        mEnteredReset();
                        /*
                         * If this is has only one case, myCases will not be initialized
                         * so we check for null.
                         */
                        if(myCases != null)
                            myCases.setListButtonEnabled(true);
                    }
                    catch(NumberFormatException e)
                    {
                        //Something other than a positive integer was entered.
                        String error = "Please enter a positive integer in range [" +
                            myLemma.getRange()[0] + ", " + myLemma.getRange()[1] + "] for best results.";
                        myMDisplay.selectAll();
                        stageMessages[1].setText(error);
                    }
                }
            });
	    return new String(PROMPT_M);
	}
	
    protected String addWGameFeatures() 
    {
        myWDisplay = new JTextArea(1, 20);
        myWDisplay.setEditable(false);
    	return new String(DESCRIBE_W);
    }
	
	/**
     * The method through which subclasses can customize features of the <i>w</i> stage panel
     * upon initialization of the <i>w</i> JPanel.
     * 
     * @param p the current decomposition panel
     */
	protected abstract void addDecompPanelGameFeatures(JPanel p);
    
    protected String addIGameFeatures() 
    {
    	myIDisplay = new JTextArea(1, 5);
        myIDisplay.setEditable(false);
    	return new String(DESCRIBE_I);
    }              
    
    public void displayEnd()
    {        
        String s = myLemma.createPumpedString();
        myPumpedStringDisplay.setText(s);        
        if(myLemma.isInLang(s))
            myLastWord.setText(createXYZ() + " = " + PumpingLemmaInputPane.toHTMLString(s) 
                + " = " + s + " is in the language.  YOU WIN!");
        else        	
            myLastWord.setText(createXYZ() + " = " + PumpingLemmaInputPane.toHTMLString(s) 
            + " = " + s + " is NOT in the language.  Please try again.");
    }
    
	/**
     * Resets various fields after <i>m</i> is entered.
     */
    protected abstract void mEnteredReset();
    
    /**
     * Refreshes the various fields accordingly after any action is taken when adjusting
     * SliderPanels.
     */
    protected abstract void refresh();
    
    /**
     * Calculates <i>i</i> and the associated pumped string based on the user's decomposition.
     */
    protected abstract void setI();
}
