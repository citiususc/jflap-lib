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
import java.awt.event.*;

import javax.swing.*;
import javax.swing.text.JTextComponent;

import edu.duke.cs.jflap.pumping.*;

/**
 * A <code>PumpingLemmaInputPane</code> is a <code>JPanel</code> that 
 * provides the user with an interface to work with regular or
 * context-free pumping lemmas where either the user or the computer
 * go first.
 * 
 * @author Jinghui Lim & Chris Morgan
 * @see edu.duke.cs.jflap.pumping.PumpingLemma
 *
 */
public abstract class PumpingLemmaInputPane extends JPanel 
{
	/**
     * The maximum size of the window. It should fit onto most
     * screens.
     */
    protected static Dimension MAX_SIZE = new Dimension(640, 580);   
    /**
     * The instruction that prompts the user to view the animation.
     */
    private static String PROMPT_ANIM = "Click \"Step\" in Box 5 to step the animation.";
    /**
     * The instruction that prompts the user to view the animation or add the case.
     */
    private static String PROMPT_CASE = "Click \"Step\" in Box 5 to step the animation or " +
            "\"Add\" in the right panel to add this case.";    
    /**
     * The <code>PumpingLemma</code> that we are demonstrating.
     */
    protected PumpingLemma myLemma;    
    /**
     * The text component where <i>m</i> is displayed and/or entered, depending on the subclass.
     */
    protected JTextComponent myMDisplay;
    /**
     * The text component where <i>w</i> is displayed and/or entered, depending on the subclass.
     */
    protected JTextComponent myWDisplay;
    /**
     * The text component where <i>i</i> is displayed and/or entered, depending on the subclass.
     */    
    protected JTextComponent myIDisplay;
    /**
     * The text area that the program displays the pumped string in.
     */    
    protected JTextArea myPumpedStringDisplay;
    /**
     * The <code>Canvas</code> the animation takes place in.
     */
    protected Canvas myCanvas;
    /**
     * The button that restarts the animation.
     */
    protected JButton myStartAnimation;
    /**
     * The button that steps the animation.
     */
    protected JButton myStepAnimation; 
    /**
     * The HTML text area that provides a short explanation of the animation.
     */    
    protected JEditorPane myLastWord;
    /**
     * The HTML text area where the explanation is displayed, the attempts are listed,
     * and the "File Loaded." message appears.
     */
    private JTextPane myTopTextPane;
    /**
     * The <code>CasePanel</code> that manages the cases of this pumping lemma.
     */
    protected CasePanel myCases;    
    /**
     * The panel where most things occur, all except the case display in myCases.
     */
    protected JPanel leftPanel; 
    /**
     * The subpanels of leftPanel.  There is one "stage" each for the top display,
     * <i>m</i> panel, <i>w</i> panel, decomposition panel, <i>i</i> panel, and canvas
     * panel.
     */
    protected JPanel[] stages;
    /**
     * The labels associated with the stages.  Most are in the lower left of the
     * associated stage panels.  The label for stage[0] isn't explicitly added onto
     * the screen, but the text is always added to the <code>JTextPane</code> at the top.
     */
    protected JLabel[] stageMessages;        
    
    /**
     * Creates a user interface for a <code>PumpingLemma</code> with the stated
     * title. The title can contain HTML tags to display superscripts or 
     * subscripts.
        myShowAll.setToolTipText("
     * 
     * @param l the pumping lemma we are working with
     * @param title the title of the lemma
     */
    public PumpingLemmaInputPane(PumpingLemma l, String title)
    {
        this.setLayout(new BorderLayout());
        myLemma = l;
        
        JPanel p = new JPanel(new BorderLayout());
        
        JEditorPane ep = new JEditorPane("text/html", "<html><body align=center><b>" + 
                title + "</b></body></html>");        
        
        ep.setBackground(this.getBackground());
        ep.setDisabledTextColor(Color.BLACK);
        ep.setEnabled(false);
        p.add(ep, BorderLayout.NORTH);
        
        /*
         * If there is only one case, do not set up the split pane.
         * 
         * Otherwise, set up the split pane with the case panel on the right.
         */                
        leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        initLeftPanel();
        if(myLemma.numCasesTotal() <= 1 || this instanceof ComputerFirstPane)
        {
            leftPanel.setPreferredSize(MAX_SIZE);
            leftPanel.setMaximumSize(MAX_SIZE);
            p.add(leftPanel, BorderLayout.CENTER);
            
        }
        else
        {
            JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
            
            leftPanel.revalidate();
            leftPanel.setPreferredSize(new Dimension(400, 300));
            leftPanel.setMaximumSize(MAX_SIZE);
            splitPane.setLeftComponent(leftPanel);
            
            JComponent rightPanel = initRightPanel();
            rightPanel.setPreferredSize(new Dimension(400, 300));
            rightPanel.setMaximumSize(MAX_SIZE);
            splitPane.setRightComponent(rightPanel);
            
            p.add(splitPane, BorderLayout.CENTER);
        }                
        JScrollPane scrollPane = new JScrollPane(p);
        add(scrollPane, BorderLayout.CENTER);
        this.setMaximumSize(MAX_SIZE);        
        this.setPreferredSize(MAX_SIZE);
    }
    
    /**
     * Initializes the left panel, or, if the pumping lemma 
     * has only one case, the only panel. This function also sets the initial
     * visibility of the various stages. 
     */    
    private void initLeftPanel() 
    {
    	stages = new JPanel[6];
    	stageMessages = new JLabel[6];
    	for (int i=0; i<stageMessages.length; i++)
    		stageMessages[i] = new JLabel();    	

    	stages[0] = initTop(); 
        stages[1] = initM();    	
    	stages[2] = initW(); 
    	stages[3] = initDecompPanel(); 
    	stages[4] = initI(); 
    	stages[5] = initCanvas();
     	
    	for (int i=0; i<stages.length; i++)
    		leftPanel.add(stages[i]);
    	if (this instanceof HumanFirstPane)
        	stages[2].setVisible(false);
        for (int i=3; i<stages.length; i++)
    		stages[i].setVisible(false);
    }
        
    /**
     * Initializes and returns the right panel, a {@link edu.duke.cs.jflap.gui.pumping.CasePanel}
     * that manages the different cases of the pumping lemma. This is called
     * in the constructor and should not be called if the pumping lemma has
     * only one case.
     * 
     * @return a <code>CasePanel</code> that manages user interaction for the
     * different cases of the pumping lemma
     */
    private JComponent initRightPanel()
    {
        myCases = new CasePanel((ContextFreePumpingLemma)myLemma, this);
        return new JScrollPane(myCases);
    }
    
    /**
     * Initializes and returns the the top stage and its JPanel.
     * 
     * @return the initialized top JPanel
     */
    private JPanel initTop()
    {
    	JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        
        JButton clear, explain;
        clear = new JButton("Clear All");
        explain = new JButton("Explain");
        myTopTextPane = new JTextPane();
        JScrollPane sp = new JScrollPane(myTopTextPane);
        myTopTextPane.setContentType("text/html");
        myTopTextPane.setEditable(false);
        
        explain.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {   
            	updateTopPane(true);
            }
        });
                
        String objectiveText = addTopGameFeatures(clear);        
        p.add(clear);
        p.add(explain);
        p.add(sp);
        p.setBorder(BorderFactory.createTitledBorder("Objective: "+objectiveText));
        p.setMaximumSize(new Dimension(MAX_SIZE.width, MAX_SIZE.height/10));
        p.setPreferredSize(new Dimension(MAX_SIZE.width, MAX_SIZE.height/10));
    	return p;
    }
    
    /**
     * Initializes and returns the <i>m</i> stage and its JPanel.
     * 
     * @return the initialized <i>m</i> JPanel
     */
    private JPanel initM()
    {
        JPanel p = new JPanel(new BorderLayout());        
        String message = addMGameFeatures();
        
        JPanel q = new JPanel(new BorderLayout());
        q.add(myMDisplay, BorderLayout.NORTH);
        q.setBorder(BorderFactory.createTitledBorder("1. " + message));
        q.add(stageMessages[1], BorderLayout.SOUTH);
        p.add(q, BorderLayout.CENTER);
        
        JPanel s = new JPanel();
        s.setLayout(new BoxLayout(s, BoxLayout.X_AXIS));     
        p.setMaximumSize(new Dimension(MAX_SIZE.width, MAX_SIZE.height/10));
        p.setPreferredSize(new Dimension(MAX_SIZE.width, MAX_SIZE.height/10));
        return p;
    }
    
    /**
     * Initializes and returns the <i>w</i> stage and its JPanel.
     * 
     * @return the initialized <i>w</i> JPanel
     */
    private JPanel initW()
    {
        JPanel p = new JPanel(new BorderLayout());
        String message = addWGameFeatures();
        p.setBorder(BorderFactory.createTitledBorder("2. " + message));
        p.add(myWDisplay, BorderLayout.NORTH);
        p.add(stageMessages[2], BorderLayout.SOUTH);
        p.setMaximumSize(new Dimension(MAX_SIZE.width, MAX_SIZE.height/10));
        p.setPreferredSize(new Dimension(MAX_SIZE.width, MAX_SIZE.height/10));
        return p;
    }
    
    /**
     * Initializes and returns the decomposition stage and its JPanel.  Specified by
     * subclasses.
     * 
     * @return the initialized decomposition JPanel
     */
    protected abstract JPanel initDecompPanel();
    
    /**
     * Initializes and returns the <i>i</i> stage and its JPanel.
     * 
     * @return the initialized <i>i</i> JPanel
     */
    private JPanel initI()
    {
        JPanel o = new JPanel(new BorderLayout());
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.add(new JLabel("i: "));
        String message = addIGameFeatures();        
        p.add(myIDisplay);
        p.add(new JLabel("    pumped string: "));

        myPumpedStringDisplay = new JTextArea(1, 30);
        myPumpedStringDisplay.setEditable(false);
        p.add(myPumpedStringDisplay);        
        o.setBorder(BorderFactory.createTitledBorder("4. " + message));
        o.add(p, BorderLayout.NORTH);
        o.add(p.add(stageMessages[4]), BorderLayout.SOUTH);
        o.setMaximumSize(new Dimension(MAX_SIZE.width, MAX_SIZE.height/10));
        o.setPreferredSize(new Dimension(MAX_SIZE.width, MAX_SIZE.height/10));
        return o;
    }    
    
    /**
     * Initializes and returns the animation canvas.
     * 
     * @return the animation canvas
     */
    private JPanel initCanvas()
    {
        JPanel p = new JPanel(new BorderLayout());
        myCanvas = new Canvas();
        p.add(myCanvas, BorderLayout.CENTER);
        
        myLastWord = new JEditorPane("text/html", "");
        myLastWord.setBackground(this.getBackground());
        myLastWord.setDisabledTextColor(Color.BLACK);
        myLastWord.setEnabled(false);
        
        JPanel q = new JPanel();
        q.setLayout(new BoxLayout(q, BoxLayout.X_AXIS));
        
        q.add(myLastWord);
        
        myStepAnimation = new JButton("Step");
        myStepAnimation.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    stepAnimation();
                    myCanvas.start();
                }
            });
        myStepAnimation.setEnabled(false);
        q.add(myStepAnimation);
        myCanvas.setStepButton(myStepAnimation);
        
        myStartAnimation = new JButton("Restart");
        myStartAnimation.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    setCanvas();
                    myCanvas.stop();
                    repaint();
                }
            });
        myStartAnimation.setEnabled(false);
        q.add(myStartAnimation);
        myCanvas.setRestartButton(myStartAnimation);
           
        p.add(stageMessages[5], BorderLayout.NORTH);
        p.add(q, BorderLayout.SOUTH);
        p.setBorder(BorderFactory.createTitledBorder("5. Animation"));
        p.setMaximumSize(new Dimension(MAX_SIZE.width, 35*MAX_SIZE.height/100));
        p.setPreferredSize(new Dimension(MAX_SIZE.width, 35*MAX_SIZE.height/100));
        return p;
    }        
    
    /**
     * This method sets the visibility of the parameter stage and all
     * stages after it to the given value.
     * 
     * @param minStage the minimum stage whose visibility will be the given value.
     * @param visibility whether or not the stages will be visible.
     */
    protected void setVisibilityStages(int minStage, boolean visibility)
    {
    	for (int i=minStage; i<stages.length; i++)
    		stages[i].setVisible(visibility);
    }
    
    /**
     * Sets the text of all stageMessages to "".
     */
    protected void resetMessages() 
    {
    	for (int i=0; i<stageMessages.length; i++)
    		stageMessages[i].setText("");
    }
    
    /**
     * Resets all information in the decomposition panel.
     */
    protected abstract void resetDecompPanel();        
    
    /**
     * Resets the entire leftPanel (except for the attempt list) and the pumping lemma.
     *
     */
    protected void reset()
    {   	
        myLemma.reset();        
        resetDecompPanel();
        resetMessages();
        if(myCases != null)
        {
            myCases.clearAll();
            myCases.setMessage("");
            myCases.setListButtonEnabled(false);
            myCases.setAddReplaceButtonsEnabled(false);
        }
        
        if (this instanceof HumanFirstPane)
        	setVisibilityStages(2, false);
        else
        	setVisibilityStages(3, false);
        leftPanel.revalidate();
    }                
    
    /**
     * Updates the top panel, specifically the JTopPane at the top to show the done
     * decompositions and possibly the explanation.
     * 
     * @param printExplanation whether or not to also print the proof.
     */
    public void updateTopPane(boolean printExplanation) 
    {
    	String s, a, output;
    	s = "<b>My Attempts:</b>";
    	output = "<html>";
    	if (stageMessages[0].getText().length() > 0)
    		output = output + "<b><i>" + stageMessages[0].getText() +"</i></b><br>";
    	
    	for (int i=myLemma.getAttempts().size()-1; i>=0; i--) {
    		a = (String) myLemma.getAttempts().get(i);
    		s = s + "<br>" + (i+1) + ":  " + a;    	    		
    	}
    	
    	if (printExplanation) {
    		String result;
    		if (myLemma.getPartitionValidity())
    			result = "<b>A valid partition of <i>w</i> exists!</b><br>";
    		else
    			result = "<b>Unfortunately no valid partition of <i>w</i> exists.</b><br>";
    		if (myLemma.getAttempts().size() > 0)
    			output = output + result + myLemma.getExplanation() + "<br><br>";
    		else 
    			output = output + result + myLemma.getExplanation();
    	}
    	
    	if (myLemma.getAttempts().size() > 0)
    		output = output + s;
    	output = output + "</html>";
    	myTopTextPane.setText(output);
    	myTopTextPane.setCaretPosition(0);
    }
    
    /**
     * The method through which subclasses can customize features of the top stage panel
     * upon initialization of the top JPanel.  The "Clear All" button is a parameter so 
     * subclasses can customize the ActionListeners assigned to it.
     * 
     * @param b the "Clear All" button
     */
    protected abstract String addTopGameFeatures(JButton b);
    
    /**
     * The method through which subclasses can customize features of the <i>m</i> stage panel
     * upon initialization of the <i>m</i> JPanel.
     * 
     * @return the title/instruction for the <i>m</i> stage panel.
     */
    protected abstract String addMGameFeatures();
    
    /**
     * The method through which subclasses can customize features of the <i>w</i> stage panel
     * upon initialization of the <i>w</i> JPanel.
     * 
     * @return the title/instruction for the <i>w</i> stage panel.
     */
    protected abstract String addWGameFeatures();
    
    /**
     * The method through which subclasses can customize features of the <i>i</i> stage panel
     * upon initialization of the <i>i</i> JPanel.
     * 
     * @return the title/instruction for the <i>i</i> stage panel.
     */
    protected abstract String addIGameFeatures();    
    
    /**
     * Sets the decomposition in the input pane and sets the decomposition and 
     * the value of <i>i</i> of this lemma.
     * 
     * @param decomposition the decomposition to be set
     * @param num the number to set <i>i</i> to
     * @see #setDecomposition(int[])
     */
    public void setDecomposition(int[] decomposition, int num)
    {
        setDecomposition(decomposition);
        myLemma.setDecomposition(decomposition, num);
    }
    
    /**
     * Sets the decomposition in this input pane. It changes the values in
     * the decomp panel, but does nothing more; the user is left to
     * set the values in the lemma and so on. 
     * 
     * @param decomposition the values to set the sliders to
     */
    public abstract void setDecomposition(int[] decomposition);
    
    /**
     * Sets up the animation canvas with according to the decomposition of 
     * <i>w</i> chosen.
     */
    protected abstract void setCanvas();       
    
    /**
     * Creates and returns a string with HTML tags that is the representation of the
     * pumped string.
     * 
     * @return a title of the pumped string
     */
    protected abstract String createXYZ();
    
    /**
     * Calls displayEnd() in addition to setting myIDislpay to the current <i>i</i> value.  
     */
    public void displayIEnd() 
    {
    	myIDisplay.setText(Integer.toString(myLemma.getI()));
    	displayEnd();
    }
    
    /**
     * Displays <i>i</i> and the pumped string based on the decomposition of <i>w</i> and
     * the current <i>i</i> value.  Usually called when the user enters <i>i</i>, such as when 
     * the computer goes first.
     */
    public abstract void displayEnd();           
    
    /**
     * Moves the animation forward one step.
     */
    private void stepAnimation()
    {
        repaint();
    }
    
    /**
     * Converts strings into HTML form that uses superscripts. For instance,
     * for input string "<i>aaabbb</i>" it produces 
     * "<i>a</i><sup>3</sup><i>b</i><sup>3</sup>".
     *  
     * @param s the string to be converted
     * @return a string with HTML tags that reprents input string <code>s</code>
     */
    public static String toHTMLString(String s)
    {
        if(s.length() < 2)
            return s;
        
        int count = 1;
        StringBuffer ret = new StringBuffer();
        for(int i = 0; i < s.length(); i++)
        {
            for(int j = i + 1; j < s.length(); j++)
            {
                if(s.substring(j, j + 1).equals(s.substring(i, i + 1)))
                {
                    count++;
                }
                else
                {
                    ret.append("<i>");
                    ret.append(s.substring(i, i + 1));
                    ret.append("</i>");
                    if(count > 1)
                    {
                        ret.append("<sup>");
                        ret.append("" + count);
                        ret.append("</sup>");
                    }
                    
                    i = j;
                    count = 1;
                }
                if(j == s.length() - 1)
                {
                    ret.append("<i>");
                    ret.append(s.substring(s.length() - 1));
                    ret.append("</i>");
                    if(count > 1)
                    {
                        ret.append("<sup>");
                        ret.append("" + count);
                        ret.append("</sup>");
                    }
                    return ret.toString();
                }
            }
        }        
        System.err.println("BUG FOUND: PumpingLemmaInputPane.toHTMLString(String)");
        System.err.println("String = "+s);
        return ret.toString();  // we should not be reaching here
    }  
    
    /**
     * This updates all the fields of the input pane to display the information 
     * in the pumping lemma. This is used mainly for loading a file.
     */
    public abstract void update();
}
