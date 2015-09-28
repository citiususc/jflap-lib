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





package edu.duke.cs.jflap.gui.action;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.duke.cs.jflap.automata.Automaton;
import edu.duke.cs.jflap.automata.State;
import edu.duke.cs.jflap.automata.turing.TuringMachine;

import edu.duke.cs.jflap.gui.editor.EditBlockPane;
import edu.duke.cs.jflap.gui.editor.EditorPane;
import edu.duke.cs.jflap.gui.environment.Environment;
import edu.duke.cs.jflap.gui.environment.tag.PermanentTag;
import edu.duke.cs.jflap.gui.environment.tag.Tag;

/**
 * The <code>CloseButton</code> is a button for removing tabs in
 * an environment. It automatically detects changes in the activation 
 * of panes in the environment, and changes its enabledness whether 
 * a pane in the environment is permanent (i.e. should not be closed).
 * 
 * @see CloseAction
 * @author Jinghui Lim
 *
 */
public class CloseButton extends javax.swing.JButton 
{
    /**
     * The environment to handle closing tabs for.
     */
    private Environment env;
    
    /**
     * Instantiates a <code>CloseButton</code>, and sets its values
     * with {@link #setDefaults()}.
     * 
     * @param environment the environment to handle the closing for
     */
    public CloseButton(Environment environment) 
    {
        super();
        setDefaults();
        env = environment;
        env.addChangeListener(new ChangeListener() 
            {
                public void stateChanged(ChangeEvent e) 
                { 
                    checkEnabled(); 
                }
            });
        addActionListener(new ActionListener()
            {

                public void actionPerformed(ActionEvent e) 
                {
                    boolean editor = false;
                    Automaton inside = null;
                    State block = null;
                    if(env.getActive() instanceof EditBlockPane)
                    {
                        editor = true;
                        EditBlockPane blockEditor = (EditBlockPane) env.getActive();
                        inside = blockEditor.getAutomaton();
                        block = blockEditor.getBlock();
                    }
                    env.remove(env.getActive());
                    if(editor) 
                    {
                        EditorPane higherEditor = (EditorPane) env.getActive();
                        assert(higherEditor.getAutomaton() instanceof TuringMachine);
                        TuringMachine higher = (TuringMachine) higherEditor.getAutomaton();
                        //MERLIN MERLIN MERLIN MERLIN MERLIN//

//                        higher.replaceBlock(block, inside); this shouldn't be necessary if we are not making a clone, but editing the real thing.
                    }
                }
            });
        checkEnabled();
    }
    
    /**
     * A convenience method that sets the button with certian values. 
     * The icon, size, and tooltip are set.
     *
     */
    public void setDefaults() 
    {
        setIcon(new ImageIcon(getClass().getResource("/ICON/x.gif")));
        setPreferredSize(new Dimension(22, 22));
        setToolTipText("Dismiss Tab");
    }

    /**
     * Checks the environment to see if the currently active object
     * has the <CODE>PermanentTag</CODE> associated with it, and if it
     * does, disables this action; otherwise it makes it activate.
     */
    private void checkEnabled() 
    {
        Tag tag = env.getTag(env.getActive());
//        setEnabled(!(tag instanceof PermanentTag));
        if(env.tabbed.getTabCount() == 1)
            setEnabled(false);
        else 
            setEnabled(!(tag instanceof PermanentTag));
    }
}
