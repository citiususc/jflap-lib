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

import edu.duke.cs.jflap.automata.Automaton;
import edu.duke.cs.jflap.automata.AutomatonSimulator;
import edu.duke.cs.jflap.automata.Configuration;
import edu.duke.cs.jflap.automata.NondeterminismDetector;
import edu.duke.cs.jflap.automata.NondeterminismDetectorFactory;
import edu.duke.cs.jflap.automata.SimulatorFactory;
import edu.duke.cs.jflap.automata.State;
import edu.duke.cs.jflap.grammar.Grammar;
import edu.duke.cs.jflap.gui.editor.EditBlockPane;
import edu.duke.cs.jflap.gui.environment.Environment;
import edu.duke.cs.jflap.gui.environment.Universe;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import edu.duke.cs.jflap.gui.sim.SimulatorPane;
import edu.duke.cs.jflap.gui.environment.tag.CriticalTag;
import javax.swing.*;

import java.awt.GridLayout;
import java.awt.Component;
import java.util.*;
import java.io.*;
import edu.duke.cs.jflap.automata.graph.AutomatonDirectedGraph;
import edu.duke.cs.jflap.automata.mealy.MealyMachine;
import edu.duke.cs.jflap.automata.turing.TuringMachine;
import edu.duke.cs.jflap.automata.turing.TMSimulator;

/**
 * This is the action used for the stepwise simulation of data. This method can
 * operate on any automaton. It uses a special exception for the two tape case.
 * 
 * @author Thomas Finley
 */

public class SimulateAction extends AutomatonAction {
	private Grammar gram;

	/**
	 * Instantiates a new <CODE>SimulateAction</CODE>.
	 * 
	 * @param automaton
	 *            the automaton that input will be simulated on
	 * @param environment
	 *            the environment object that we shall add our simulator pane to
	 */
	public SimulateAction(Automaton automaton, Environment environment) {
		super("Step...", null);
		if (SimulateNoClosureAction.isApplicable(automaton))
			putValue(NAME, "Step with Closure...");
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_R,
				MAIN_MENU_MASK));
		this.automaton = automaton;
		this.environment = environment;
	}
	
	public SimulateAction(Grammar gram, Environment environment) {
		super("Step...", null);
		if (SimulateNoClosureAction.isApplicable(automaton))
			putValue(NAME, "Step with Closure...");
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_R,
				MAIN_MENU_MASK));
		this.gram = gram;
		this.environment = environment;
	}

	/**
	 * Returns the simulator for this automaton.
	 * 
	 * @param automaton
	 *            the automaton to get the simulator for
	 * @return a simulator for this automaton
	 */
	protected AutomatonSimulator getSimulator(Automaton automaton) {
		return SimulatorFactory.getSimulator(automaton);
	}

	/**
	 * Given initial configurations, the simulator, and the automaton, takes any
	 * further action that may be necessary. In the case of stepwise operation,
	 * which is the default, an additional tab is added to the environment
	 * 
	 * @param automaton
	 *            the automaton input is simulated on
	 * @param simulator
	 *            the automaton simulator for this automaton
	 * @param configurations
	 *            the initial configurations generated
	 * @param initialInput
	 *            the object that represents the initial input; this is a String
	 *            object in most cases, but may differ for multiple tape turing
	 *            machines
	 */
	public void handleInteraction(Automaton automaton,
			AutomatonSimulator simulator, Configuration[] configurations,
			Object initialInput) {
		SimulatorPane simpane = new SimulatorPane(automaton, simulator,
				configurations, environment, false);
		if (initialInput instanceof String[])
			initialInput = java.util.Arrays.asList((String[]) initialInput);
		environment.add(simpane, "Simulate: " + initialInput,
				new CriticalTag() {
				});
		environment.setActive(simpane);
	}

	/**
	 * This returns an object that encapsulates the user input for the starting
	 * configuration. In most cases this will be a string, except in the case of
	 * a multiple tape Turing machine. This method will probably involve some
	 * prompt to the user. By default this method prompts the user using a
	 * dialog box and returns the result from that dialog.
	 * 
	 * @param component
	 *            a parent for whatever dialogs are brought up
	 * @return the object that represents the initial input to the machine, or
	 *         <CODE>null</CODE> if the user elected to cancel
	 */
	protected Object initialInput(Component component, String title) {
        if(title.equals("")) title = "Input";
        if(getObject() instanceof TuringMachine){
    		// Do the multitape stuff.
    		TuringMachine tm = (TuringMachine) getObject();       
    		int tapes = tm.tapes();
            if(title.equals("Expected Result? (Accept or Reject)")){
                title = "Result";
                tapes = 1;
            }
            if(title.equals("Expected Output?")){
                title = "Output";
            }
    		return openInputGUI(component, title, tapes);
        }
        else{
            if (title.equals("")){
            	return openInputGUI(component, "Input?", 0);
                //return JOptionPane.showInputDialog(component, "Input?");
                
            }
            else
            {
            	return openInputGUI(component, title, 0);
            	//return JOptionPane.showInputDialog(component, title+ "?!!!!");
            }
        }
	}

	/**
	 * Opens pop-up GUI for taking input. Now JFLAP can take file as an input.
	 * @param component
	 * @param title
	 * @return
	 */
	private Object openInputGUI(final Component component, String title, final int tapes) {
		// TODO Auto-generated method stub
		JPanel panel;
		JTextField[] fields;
		
		//for FA, PDA
		if (tapes==0)
		{
			panel = new JPanel(new GridLayout(3, 1));
			fields = new JTextField[1];
			for (int i = 0; i < 1; i++) {
				panel.add(new JLabel(title + " "));
				panel.add(fields[i] = new JTextField());
			}
		}
		else
		{
			panel = new JPanel(new GridLayout(tapes*2+1, 2));
			fields = new JTextField[tapes];
			for (int i = 0; i < tapes; i++) {
				panel.add(new JLabel(title + " "+(i+1)));
				panel.add(fields[i] = new JTextField());
			}
		}
		JButton jb=new JButton("Click to Open Input File");
		jb.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JFileChooser ourChooser=new JFileChooser (System.getProperties().getProperty("user.dir"));
				int retval=ourChooser.showOpenDialog(null);
				File f=null;
				if (retval==JFileChooser.APPROVE_OPTION)
				{
					f=ourChooser.getSelectedFile();
					try {
						Scanner sc=new Scanner(f);
						if (tapes!=0)
						{
							String[] input = new String[tapes];
				    		for (int i = 0; i < tapes; i++)
				    		{
				    			if (sc.hasNext())
				    				input[i] = sc.next();
				    			else
				    			{
				    				JOptionPane.showMessageDialog(component, "Input file does not have enough input for all tapes", "File read error"
				    						, JOptionPane.ERROR_MESSAGE);
				    				return;
				    			}
				    		}
							JOptionPane.getFrameForComponent(component).dispose();
							handleInputFile(input);
						}
						else
						{
							String tt=sc.next();
							JOptionPane.getFrameForComponent(component).dispose();
							handleInputFile(tt);
						}
						
					} catch (FileNotFoundException e1) {
						// TODO Auto-generate catch block
						e1.printStackTrace();
					}
					
				}
				
			}
			
		});
		panel.add(jb);
		int result = JOptionPane.showOptionDialog(component, panel, title,
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
				null, null, null);
		if (result != JOptionPane.YES_OPTION && result != JOptionPane.OK_OPTION)
			return null;
		if (tapes==0)
		{
			String input = fields[0].getText();
			return input;
		}
		else
		{
    		String[] input = new String[tapes];
    		for (int i = 0; i < tapes; i++)
    			input[i] = fields[i].getText();
    		return input;
		}
	}
	
	private void handleInputFile(Object input)
	{
		Configuration[] configs = null;
		AutomatonSimulator simulator = getSimulator(automaton);
	/*	if (input instanceof InputBox)
		{
			InputBox tt=(InputBox) input;
			if (getObject() instanceof TuringMachine)
				tt.addSimulator(automaton, simulator, true);
			else
				tt.addSimulator(automaton, simulator, false);
			return;
		}*/
		if (input == null)
			return;
		
		// Get the initial configurations.
		if (getObject() instanceof TuringMachine) {
			String[] s = (String[]) input;
			configs = ((TMSimulator) simulator).getInitialConfigurations(s);
		} else {
			String s = (String) input;
			configs = simulator.getInitialConfigurations(s); 
		}
		handleInteraction(automaton, simulator, configs, input);

	}

	/**
	 * Performs the action.
	 */
	public void actionPerformed(ActionEvent e) {
		boolean blockEdit = false;
		if (environment.getActive() instanceof EditBlockPane) {
			EditBlockPane newPane = (EditBlockPane) environment.getActive();
			automaton = newPane.getAutomaton();
			blockEdit = true;
		}
		if (!automatonActionPermissible((Component) e.getSource()))
			return;
		Object input = initialInput((Component) e.getSource(), "");
		Configuration[] configs = null;
		AutomatonSimulator simulator = getSimulator(automaton);
	/*	if (input instanceof InputBox)
		{
			InputBox tt=(InputBox) input;
			if (getObject() instanceof TuringMachine)
				tt.addSimulator(automaton, simulator, true);
			else
				tt.addSimulator(automaton, simulator, false);
			return;
		}*/
		if (input == null)
			return;
		
		// Get the initial configurations.
		if (getObject() instanceof TuringMachine) {
			String[] s = (String[]) input;
			configs = ((TMSimulator) simulator).getInitialConfigurations(s);
		} else {
			String s = (String) input;
			configs = simulator.getInitialConfigurations(s); 
		}
		handleInteraction(automaton, simulator, configs, input);
	}
	
	/**
	 * Returns whether the current automaton can legally be simulated.  If not,
	 * a dialog pops up.  Will true if <code>automaton</code> = <i>null</i>.
	 * 
	 * @param source
	 * 		the source of this action
	 * @return whether the current automaton can legally be simulated
	 */
	protected boolean automatonActionPermissible(Component source) {
		if (!(getObject() instanceof Automaton))
			return true;
		if (automaton.getInitialState() == null) {
			JOptionPane.showMessageDialog(source,
					"Simulation requires an automaton\n"
							+ "with an initial state!", "No Initial State",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
        /*
         * If it is a Moore or Mealy machine, don't let it start if it has
         * nondeterministic states.
         */
        if(automaton instanceof MealyMachine)
        {
            NondeterminismDetector d = NondeterminismDetectorFactory.getDetector(automaton);
            State[] nd = d.getNondeterministicStates(automaton);
            if(nd.length > 0)
            {
                JOptionPane.showMessageDialog(source,
                    "Please remove nondeterminism for simulation.\n" +
                    "Select menu item Test : Highlight Nondeterminism\nto see nondeterministic states.",
                    "Nondeterministic states detected", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        /*
         * If it is a Turing machine, there are transitions from the final state, and that preference
         * hasn't been enabled, give a warning and return. 
         */
        else if (automaton instanceof TuringMachine &&
        	!Universe.curProfile.transitionsFromTuringFinalStateAllowed()) {
        	TuringMachine turingMachine = (TuringMachine) automaton;
        	Object[] finalStates = turingMachine.getFinalStates();
        	AutomatonDirectedGraph graph = new AutomatonDirectedGraph(turingMachine);
        	for (int i=0; i<finalStates.length; i++)
        		if (graph.fromDegree(finalStates[i], false) > 0) {
        			JOptionPane.showMessageDialog(source,
                            "There are transitions from final states.  Please remove them or change " +
                            "\nthe preference in the \"Preferences\" menu in the JFLAP main menu.",
                            "Transitions From Final States", JOptionPane.ERROR_MESSAGE);
        			return false;
        		}        	
        }
        return true;
	}

	/**
	 * Simulate actions are applicable to every automaton which accepts a single
	 * string of input, i.e., every automaton except for dual tape turing
	 * machines.
	 * 
	 * @param object
	 *            to object to test for applicability
	 */
	public static boolean isApplicable(Object object) {
		return object instanceof Automaton;
	}

	/**
	 * Returns the automaton.
	 * 
	 * @return the automaton
	 */
	protected Object getObject() {
		if(automaton != null)return automaton;
		else return gram;
	}

	/**
	 * Returns the environment.
	 * 
	 * @return the environment
	 */
	protected Environment getEnvironment() {
		return environment;
	}
	
	protected void setEnvironment(Environment newEnv) {
		environment = newEnv;
	}

	/** The automaton this simulate action runs simulations on! */
	private Automaton automaton;

	/** The environment that the simulation pane will be put in. */
	private Environment environment;
	
}
