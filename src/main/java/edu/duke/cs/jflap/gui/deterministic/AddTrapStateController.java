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




package edu.duke.cs.jflap.gui.deterministic;

import java.awt.Point;
import java.util.*;

import edu.duke.cs.jflap.gui.environment.FrameFactory;
import edu.duke.cs.jflap.gui.viewer.SelectionDrawer;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import edu.duke.cs.jflap.automata.State;
import edu.duke.cs.jflap.automata.Transition;
import edu.duke.cs.jflap.automata.fsa.FSATransition;
import edu.duke.cs.jflap.automata.fsa.FiniteStateAutomaton;

/**
 * Controller that is responsible for creating trap state and adding necessary transitions
 * This class is very similar to FSATOREController.java under gui.regular
 * @author Kyung Min (Jason) Lee
 *
 */
public class AddTrapStateController{

	/** The current step of the conversion process. */
	private int currentStep = -1;

	/** The automaton that's going to add trap state */
	private FiniteStateAutomaton automaton;

	/** The selection drawer that the editor holds. */
	private SelectionDrawer drawer;

	/** The main step label. */
	private JLabel mainStep;

	/** The detail step label. */
	private JLabel detailStep;

	/** The frame holding all this. */
	private JFrame frame;

	/**
	 * The number of things left to do. This can be used by different steps.
	 */
	private int remaining = 0;

	/**
	 * Tree Set of readable alphabets in DFA
	 */
	private TreeSet <String> myReadSets;
	
	/**
	 * Hash Map that consists of key of state number and value as transitions
	 */
	private HashMap <Integer, ArrayList<String>> myTransitionsMap;
	
	/**
	 * Hash Map of transitions that needed to complete DFA
	 */
	private HashMap <Integer, ArrayList<String>> myNeededTransitionMap;
	
	/**
	 * Map holds state ID as key and actual state object as value
	 */
	private HashMap <Integer, State> myStateMap;
	/**
	 * Trap State
	 */
	private State myTrapState;
	
	/**
	 * The state IDs of each of the steps.
	 */
	private static final int CREATE_SINGLE_TRAPSTATE = 0,
			TRANSITIONS_TO_TRAPSTATE = 1, FINISHED = 200;
	
	
	/**
	 * Instantiates a new <CODE>AddTrapStateController</CODE>.
	 * 
	 * @param automaton
	 *            the automaton that is in the process of being converted
	 * @param drawer
	 *            the selection drawer in the editor
	 * @param mainStep
	 *            the label holding the description of the main step
	 * @param detailStep
	 *            the label holding the detail description of whatever the user
	 *            must do now
	 * @param frame
	 *            the window that this is all happening in
	 */
	public AddTrapStateController(FiniteStateAutomaton automaton,SelectionDrawer drawer, JLabel mainStep, JLabel detailStep,
			JFrame frame) 
	{
		this.automaton = automaton;
		this.drawer = drawer;
		this.mainStep = mainStep;
		this.detailStep = detailStep;
		this.frame = frame;
		currentStep=CREATE_SINGLE_TRAPSTATE;
		nextStep();
	}
	
	/**
	 * Proceed the next step
	 */
	private void nextStep()
	{
		switch (currentStep) 
		{
		case CREATE_SINGLE_TRAPSTATE:
			currentStep = CREATE_SINGLE_TRAPSTATE;
			mainStep.setText("Make Single Trap State");
			detailStep
					.setText("Create a new state to make a single trap state.");
			if (automaton.getFinalStates().length != 1
					|| automaton.getFinalStates()[0] == automaton
							.getInitialState()) {
				return;
			}
			return;
		case TRANSITIONS_TO_TRAPSTATE:
			if (myReadSets==null)
				determineRemainingTransition();
			mainStep.setText("Adding Transitions    Readable String : "+myReadSets);
			detailStep
					.setText("Put transitions from all states to the trap state.   "+remaining+" transitions must be added");
			// We know we're done when...
			if (drawer.numberSelected() != 0)
				return;
			return;

		case FINISHED:
			mainStep.setText("Adding a Trap State and Transitions is Finished!");
			detailStep
			.setText("");
			JOptionPane.showMessageDialog(frame, "The DFA is now complete!\n"
					+ "It will now be placed in a new window.");
			FrameFactory.createFrame((FiniteStateAutomaton) automaton.clone());
			return;
		}
	}

	/**
	 * Determines how many transitions are needed from states to a single trap state
	 */
	private void determineRemainingTransition() {
		myTransitionsMap=new HashMap <Integer, ArrayList <String>>();
		myNeededTransitionMap=new HashMap <Integer, ArrayList <String>>();
		myReadSets=new TreeSet <String>();
		myStateMap=new HashMap <Integer, State>();
		State[] s=automaton.getStates();
		for (int i=0; i<s.length; i++)
		{
			myTransitionsMap.put(s[i].getID(), new ArrayList <String>());
			myStateMap.put(s[i].getID(), s[i]);
		}
		
		Transition[] t=automaton.getTransitions();
		for (int i=0; i<t.length; i++)
		{
			myReadSets.add(t[i].getDescription());
			int id=t[i].getFromState().getID();
			ArrayList <String> temp=myTransitionsMap.get(id);
			temp.add(t[i].getDescription());
			myTransitionsMap.put(id, temp);
		}
		
		for (Integer key : myTransitionsMap.keySet())
		{
			ArrayList <String> temp=new ArrayList <String>();
			for (String alpha : myReadSets)
			{
				if (!myTransitionsMap.get(key).contains(alpha))
				{
					temp.add(alpha);
					remaining++;
				}
			}
			myNeededTransitionMap.put(key, temp);
		}
		
	/*	System.out.println("Read Sets = "+myReadSets);
		System.out.println("MAP = "+myTransitionsMap);
		System.out.println("NEEDED MAP = "+myNeededTransitionMap);
	*/	
	}

	/**
	 * Called when user clicks on "Do All" option
	 * it automatically finishes adding transitions, if user created a single trap state
	 */
	public void doAll() {
		// TODO Auto-generated method stub
		switch (currentStep) {
		case CREATE_SINGLE_TRAPSTATE:
			JOptionPane.showMessageDialog(frame,
				"Just create a state.\nIt's not too difficult.",
				"Create the State", JOptionPane.ERROR_MESSAGE);
			return;
		}
		for (Integer key:myNeededTransitionMap.keySet())
		{
			ArrayList <String> list=myNeededTransitionMap.get(key);
			for (String terminal: list)
			{
				FSATransition t=new FSATransition(myStateMap.get(key), myTrapState, terminal);
				automaton.addTransition(t);
				frame.repaint();
			}
		}
		currentStep=FINISHED;
		nextStep();
		return;
	}

	/**
	 * Called when the user creates a new state on the pane
	 * @param point
	 * @return the trap state
	 */
	public State stateCreate(Point point) {

		if (currentStep != CREATE_SINGLE_TRAPSTATE) {
			outOfOrder();
			return null;
		}
		myTrapState = automaton.createState(point);
		myTrapState.setLabel("Trap State");
		frame.repaint();
		currentStep=TRANSITIONS_TO_TRAPSTATE;
		nextStep();
		return myTrapState;
	}

	/**
	 * Create transition from one state to another
	 * @param from From state
	 * @param to To state
	 */
	public void transitionCreate(State from, State to) {
		// TODO Auto-generated method stub
		if (currentStep != TRANSITIONS_TO_TRAPSTATE) {
			outOfOrder();
			return;
		}
		if (!to.equals(myTrapState))
		{
			JOptionPane.showMessageDialog(frame,
					"You have to make transition to the trap state!", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
	/*	Transition t = new FSATransition(from, to, "");
		automaton.addTransition(t);
		frame.repaint();*/
	
		String terminal = JOptionPane.showInputDialog(frame, "Transition on what terminal?");
		if (terminal==null)
			return;
		if (terminal.length()>1)
		{
			JOptionPane.showMessageDialog(frame,
					"Terminal can only be a single letter", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		ArrayList <String> list=myNeededTransitionMap.get(from.getID());
		if (list.contains(terminal))
		{
			FSATransition t=new FSATransition(from, to, terminal);
			automaton.addTransition(t);
			frame.repaint();
			list.remove(terminal);
			myNeededTransitionMap.put(from.getID(), list);
			remaining--;
			if (remaining==0)
			{
				currentStep=FINISHED;
				nextStep();
				return;
			}
			detailStep
			.setText("Put transitions from all states to the trap state.   "+remaining+" transitions must be added");
			
			return;
		}
		else
		{
			if (!myReadSets.contains(terminal))
			{
				JOptionPane.showMessageDialog(frame,
					"Terminal "+terminal+" is not part of readable string in DFA", "Incorrect input",
					JOptionPane.ERROR_MESSAGE);
			}
			else
			{
				JOptionPane.showMessageDialog(frame,
						"There is already a transition using Terminal "+terminal+" from this state", "Incorrect input",
						JOptionPane.ERROR_MESSAGE);
			}
			return;
		}
	}

	/**
	 * This method should be called when the user undertakes an action that is
	 * inappropriate for the current step. This merely displays a small dialog
	 * to the user informing him of this fact, and takes no further action.
	 */
	protected void outOfOrder() {
		JOptionPane.showMessageDialog(frame,
				"That action is inappropriate for this step!", "Out of Order",
				JOptionPane.ERROR_MESSAGE);
	}

}
