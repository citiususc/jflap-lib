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

import edu.duke.cs.jflap.gui.environment.Environment;
import edu.duke.cs.jflap.gui.environment.tag.CriticalTag;
import edu.duke.cs.jflap.pumping.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;


/**
 * A <code>PumpingLemmaChooserPane</code> is the intermediate pane that allows
 * the user to select which pumping lemma they wish to see.
 * 
 * @author Jinghui Lim
 *
 */
public class PumpingLemmaChooserPane extends JPanel 
{
    /**
     * The list of puming lemmas to choose from.
     */
    PumpingLemmaChooser myChooser;
    /**
     * The environment associated with this pane.
     */
    Environment myEnvironment;
    /**
     * Radio Buttons that determine who goes first.
     */
    JRadioButton humanButton, computerButton;
    
    /**
     * Constructs a <code>PumpingLemmaChooserPane</code> associated with a
     * {@link edu.duke.cs.jflap.gui.pumping.PumpingLemmaChooser} and an
     * {@link edu.duke.cs.jflap.gui.environment.Environment}.
     * 
     * @param plc the associated <code>PumpingLemmaChooser</code> 
     * @param env the associated <code>Environment</code>
     */
    public PumpingLemmaChooserPane(PumpingLemmaChooser plc, Environment env)
    {
        super.setLayout(new BorderLayout());
        myChooser = plc;
        myEnvironment = env;
        init();
    }
    
    /**
     * @see edu.duke.cs.jflap.file.xml.RegPumpingLemmaTransducer#fromDOM(Document)
     */
    public Environment getEnvironment()
    {
        return myEnvironment;
    }
    
    /**
     * Initializes the chooser pane.
     */
    private void init()
    {
    	JPanel listPanel = new JPanel();
    	listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
    	listPanel.setBorder(BorderFactory.createTitledBorder("Then select a lemma."));
    	add(initRadioButtonPanel(), BorderLayout.NORTH);
        for(int i = 0; i < myChooser.size(); i++)
            listPanel.add(addPumpingLemma(i));
        add(listPanel, BorderLayout.CENTER);
    }
    
    /**
     * Initiates the panel where the user, through radio buttons, decides whether
     * he/she or the computer will go first.
     */
    private JPanel initRadioButtonPanel(){    	
    	ButtonGroup group = new ButtonGroup();
    	JPanel buttonPanel;
    	buttonPanel = new JPanel(new BorderLayout());
    	buttonPanel.setBorder(BorderFactory.createTitledBorder("First choose who makes the first move."));
    	
    	humanButton = new JRadioButton("You go first");
    	computerButton = new JRadioButton("Computer goes first");    	
    	group.add(humanButton);
    	group.add(computerButton);
    	humanButton.setSelected(true);
    	buttonPanel.add(humanButton, BorderLayout.WEST);
    	buttonPanel.add(computerButton, BorderLayout.CENTER);
    	return buttonPanel;
    }
    
    /**
     * Creates a panel for the pumping lemma at index <code>i</code> in the 
     * pumping lemma chooser.
     * 
     * @param i the position of the pumping lemma we wish to add
     * @return a <code>JPanel</code> representing the pumping lemma
     */
    private JPanel addPumpingLemma(int i)
    {
        PumpingLemma lemma = myChooser.get(i);
        JPanel pane = new JPanel(new BorderLayout());
        JEditorPane ep = new JEditorPane("text/html", "<html><body align=center><b><i>L</i> = {" + 
                lemma.getHTMLTitle() + "}</b></body></html>");
        ep.setBackground(this.getBackground());
        ep.setDisabledTextColor(Color.BLACK);
        ep.setEnabled(false);
        pane.add(ep, BorderLayout.CENTER);

        PumpingLemmaChooseButton button = new PumpingLemmaChooseButton(myChooser.get(i), myEnvironment, i);

        pane.add(button, BorderLayout.EAST);
        pane.setBorder(BorderFactory.createEtchedBorder());
        
        return pane;
    }
    
    /**
     * A <code>PumpingLemmaChooseButton</code> is a <code>JButton</code>
     * that opens a {@link PumpingLemmaInputPane} for its associated
     * {@link edu.duke.cs.jflap.pumping.PumpingLemma}.
     * 
     * @author Jinghui Lim
     *
     */
    private class PumpingLemmaChooseButton extends JButton
    {
        /**
         * The button's environment.
         */
        private Environment myEnvironment;
        /**
         * The pumping lemma the button should start.
         */
        private PumpingLemma myLemma;
        private int myIndex;
        
        /**
         * Constructs a <code>PumpingLemmaChooseButton</code> that opens
         * a pumping lemma in the environment when it is clicked.
         *  
         * @param pl the pumping lemma it should open
         * @param env the environment it should open the pumping lemma in
         */
        public PumpingLemmaChooseButton(PumpingLemma pl, Environment env, int index)
        {
            super("Select");
            myEnvironment = env;
            myLemma = pl;
            myIndex = index;
            
            this.addActionListener(new ActionListener()
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        myChooser.reset(myIndex);
                        myChooser.setCurrent(myIndex);
                        PumpingLemmaInputPane pane = null;   //this value should change
                        if (humanButton.isSelected()) {
                        	if(myLemma instanceof RegularPumpingLemma)
                        		pane = new HumanRegPumpingLemmaInputPane((RegularPumpingLemma)myLemma);
                        	else if(myLemma instanceof ContextFreePumpingLemma)
                        		pane = new HumanCFPumpingLemmaInputPane((ContextFreePumpingLemma)myLemma);
                        }                        
                        else if (computerButton.isSelected()) {
                        	if(myLemma instanceof RegularPumpingLemma)
                        		pane = new CompRegPumpingLemmaInputPane((RegularPumpingLemma)myLemma);
                        	else if(myLemma instanceof ContextFreePumpingLemma)
                        		pane = new CompCFPumpingLemmaInputPane((ContextFreePumpingLemma)myLemma);
                        }
                        myEnvironment.add(pane, "Pumping Lemma", new CriticalTag(){});
                        myEnvironment.setActive(pane);
                    }
                });
        }
    }
}
