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





package edu.duke.cs.jflap.gui.environment;

import edu.duke.cs.jflap.grammar.ConvertedUnrestrictedGrammar;
import edu.duke.cs.jflap.grammar.Grammar;
import edu.duke.cs.jflap.grammar.lsystem.LSystem;
import edu.duke.cs.jflap.gui.editor.EditorPane;
import edu.duke.cs.jflap.gui.editor.MealyToolBox;
import edu.duke.cs.jflap.gui.editor.MooreToolBox;
import edu.duke.cs.jflap.gui.environment.tag.CriticalTag;
import edu.duke.cs.jflap.gui.environment.tag.EditorTag;
import edu.duke.cs.jflap.gui.environment.tag.PermanentTag;
import edu.duke.cs.jflap.gui.environment.tag.Tag;
import edu.duke.cs.jflap.gui.grammar.GrammarInputPane;
import edu.duke.cs.jflap.gui.lsystem.LSystemInputPane;
import edu.duke.cs.jflap.gui.pumping.*;

import java.io.Serializable;

import javax.swing.JOptionPane;

import edu.duke.cs.jflap.pumping.ContextFreePumpingLemma;
import edu.duke.cs.jflap.pumping.PumpingLemma;
import edu.duke.cs.jflap.pumping.RegularPumpingLemma;

import edu.duke.cs.jflap.regular.RegularExpression;
import edu.duke.cs.jflap.automata.Automaton;
import edu.duke.cs.jflap.automata.mealy.MealyMachine;
import edu.duke.cs.jflap.automata.mealy.MooreMachine;

/**
 * The <CODE>EnvironmentFactory</CODE> creates environments for some
 * predefined types of objects with an editing pane already added. For
 * situations where a new <CODE>Environment</CODE> must be created with
 * customized trimmings, this sort of factory will be inappropriate. The
 * intended use is for <CODE>Environment</CODE>s opened in a file, or created
 * in a new file action, or some other such action.
 * 
 * @author Thomas Finley
 */

public class EnvironmentFactory {
	/**
	 * Returns a new environment with an editor pane.
	 * 
	 * @param object
	 *            the object that this environment will edit
	 * @return a new environment for the passed in object, ready to be edited,
	 *         or <CODE>null</CODE> if no environment could be defined for
	 *         this object
	 */
	public static Environment getEnvironment(Serializable object) {
        
		/*
         * For loading regular pumping lemmas.
         */
        if(object instanceof RegularPumpingLemma)
        {
            RegPumpingLemmaChooser plc = new RegPumpingLemmaChooser();
            Environment env = new PumpingLemmaEnvironment(plc);
            PumpingLemmaChooserPane pane = new PumpingLemmaChooserPane(plc, env);
            env.add(pane, "Select a Pumping Lemma", EDITOR_PERMANENT_TAG);
            RegularPumpingLemma pl = (RegularPumpingLemma)object;
            PumpingLemmaInputPane inputPane;
            if (pl.getFirstPlayer().equals(PumpingLemma.COMPUTER))
            	inputPane = new CompRegPumpingLemmaInputPane(pl);                    	
            else
            	inputPane = new HumanRegPumpingLemmaInputPane(pl);
            inputPane.update();
            plc.replace(pl);
            env.add(inputPane, "Pumping Lemma", new CriticalTag(){});
            env.setActive(inputPane);
            return env;
        }
        /*
         * For loading context-free pumping lemmas.
         */
        if(object instanceof ContextFreePumpingLemma)
        {
            CFPumpingLemmaChooser plc = new CFPumpingLemmaChooser();
            Environment env = new PumpingLemmaEnvironment(plc);
            PumpingLemmaChooserPane pane = new PumpingLemmaChooserPane(plc, env);
            env.add(pane, "Select a Pumping Lemma", EDITOR_PERMANENT_TAG);
            ContextFreePumpingLemma pl = (ContextFreePumpingLemma)object;
            PumpingLemmaInputPane inputPane;            
            if (pl.getFirstPlayer().equals(PumpingLemma.COMPUTER))            	
            	inputPane = new CompCFPumpingLemmaInputPane(pl);
            else
            	inputPane = new HumanCFPumpingLemmaInputPane(pl);
            inputPane.update();
            plc.replace(pl);
            env.add(inputPane, "Pumping Lemma", new CriticalTag(){});
            env.setActive(inputPane);
            return env;
        }
        /*
         * For starting new regular pumping lemmas.
         */
        if(object instanceof RegPumpingLemmaChooser)
        {
            RegPumpingLemmaChooser plc = (RegPumpingLemmaChooser) object;
            Environment env = new PumpingLemmaEnvironment(plc);
            PumpingLemmaChooserPane pane = new PumpingLemmaChooserPane(plc, env);
            env.add(pane, "Select a Pumping Lemma", EDITOR_PERMANENT_TAG);
            return env;
        }
        /*
         * For starting context-free pumping lemmas.
         */
        if(object instanceof CFPumpingLemmaChooser)
        {
            CFPumpingLemmaChooser plc = (CFPumpingLemmaChooser) object;
            Environment env = new PumpingLemmaEnvironment(plc);
            PumpingLemmaChooserPane pane = new PumpingLemmaChooserPane(plc, env);
            env.add(pane, "Select a Pumping Lemma", EDITOR_PERMANENT_TAG);
            return env;
        }
        if(object instanceof MooreMachine)
        {
            Automaton aut = (Automaton) object;
            Environment env = new AutomatonEnvironment(aut);
            EditorPane editor = new EditorPane(aut, new MooreToolBox());
            env.add(editor, EDITOR_NAME, EDITOR_PERMANENT_TAG);
            return env;
        } 
        if(object instanceof MealyMachine)
        {
            Automaton aut = (Automaton) object;
            Environment env = new AutomatonEnvironment(aut);
            EditorPane editor = new EditorPane(aut, new MealyToolBox());
            env.add(editor, EDITOR_NAME, EDITOR_PERMANENT_TAG);
            return env;
        } 
        
		if (object instanceof Automaton) {
			Automaton aut = (Automaton) object;
			Environment env = new AutomatonEnvironment(aut);
			EditorPane editor = new EditorPane(aut);
			env.add(editor, EDITOR_NAME, EDITOR_PERMANENT_TAG);
			return env;
		} else if (object instanceof Grammar) {
			if (object instanceof ConvertedUnrestrictedGrammar)
			{
				ConvertedUnrestrictedGrammar grammar = (ConvertedUnrestrictedGrammar) object;
				
				GrammarInputPane input = new GrammarInputPane(grammar);
				
				Environment env = new GrammarEnvironment(input);
				// Set up the pane for the input pane.
				
				env.add(input, EDITOR_NAME, EDITOR_PERMANENT_TAG);
				return env;
			
			}
			else
			{
				Grammar grammar = (Grammar) object;
				GrammarInputPane input = new GrammarInputPane(grammar);
				Environment env = new GrammarEnvironment(input);
				// Set up the pane for the input pane.
				env.add(input, EDITOR_NAME, EDITOR_PERMANENT_TAG);
				return env;
			}
		} else if (object instanceof RegularExpression) {
			RegularExpression re = (RegularExpression) object;
			edu.duke.cs.jflap.gui.regular.EditorPane editor = new edu.duke.cs.jflap.gui.regular.EditorPane(re);
			Environment env = new RegularEnvironment(re);
			env.add(editor, EDITOR_NAME, EDITOR_PERMANENT_TAG);
			return env;
		} else if (object instanceof LSystem) {
			LSystem lsystem = (LSystem) object;
			LSystemInputPane lsinput = new LSystemInputPane(lsystem);
			Environment env = new LSystemEnvironment(lsinput);
			env.add(lsinput, EDITOR_NAME, EDITOR_PERMANENT_TAG);
			return env;
		} else {
			JOptionPane.showMessageDialog(null, "Unknown type "
					+ object.getClass() + " read!", "Bad Type",
					JOptionPane.ERROR_MESSAGE);
			// Nothing else yet.
			return null;
		}
		
	}

	/**
	 * A class for an editor, which in most applications one will want both
	 * permanent and marked as an editor.
	 */
	public static class EditorPermanentTag implements EditorTag, PermanentTag {
	};

	/** An instance of such an editor permanent tag. */
	private static final Tag EDITOR_PERMANENT_TAG = new EditorPermanentTag();

	/** The name for editor panes. */
	private static final String EDITOR_NAME = "Editor";
}
