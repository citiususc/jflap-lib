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

import edu.duke.cs.jflap.automata.*;
import edu.duke.cs.jflap.automata.mealy.MooreMachine;
import edu.duke.cs.jflap.automata.turing.TuringMachine;
import edu.duke.cs.jflap.gui.environment.*;
import edu.duke.cs.jflap.gui.viewer.AutomatonDrawer;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.geom.Rectangle2D;
import java.util.*;
import javax.swing.*;

/**
 * This combines two automatons into a new automaton.
 * 
 * @author Thomas Finley
 */

public class CombineAutomaton extends AutomatonAction {
	/**
	 * Instantiates a new action to combine automatons.
	 * 
	 * @param environment
	 *            the automaton environment
	 */
	public CombineAutomaton(AutomatonEnvironment environment) {
		super("Combine Automata", null);
		this.environment = environment;
	}

	/**
	 * Creates a new automaton.
	 * 
	 * @param e
	 *            the action event
	 */
	public void actionPerformed(ActionEvent e) {
		JComboBox combo = new JComboBox();
		// Figure out what existing environments in the program have
		// the type of automaton that we need.
		EnvironmentFrame[] frames = Universe.frames();
		for (int i = 0; i < frames.length; i++) {
			Environment env = frames[i].getEnvironment();
			if (env == environment
					|| !(env instanceof AutomatonEnvironment)
					|| environment.getObject().getClass() != env.getObject()
							.getClass())
				continue;
			if (environment.getObject() instanceof TuringMachine) {
				TuringMachine t1 = (TuringMachine) environment.getObject();
				TuringMachine t2 = (TuringMachine) env.getObject();
				if (t1.tapes() != t2.tapes())
					continue;
			}
			combo.addItem(frames[i]);
		}
		if (combo.getItemCount() == 0) {
			JOptionPane.showMessageDialog(Universe
					.frameForEnvironment(environment),
					"No other automatons of this type around!");
			return;
		}
		// Prompt the user.
		int result = JOptionPane.showOptionDialog(Universe
				.frameForEnvironment(environment), combo, "Combine Two",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
				null, null, null);
		if (result != JOptionPane.YES_OPTION && result != JOptionPane.OK_OPTION)
			return;

		Automaton other = (Automaton) ((EnvironmentFrame) combo
				.getSelectedItem()).getEnvironment().getObject();

		Automaton newOne = (Automaton) environment.getAutomaton().clone();

		add(newOne, other);

		FrameFactory.createFrame(newOne);
	}

	/**
	 * Appends other to the <CODE>newOne</CODE> automaton.
	 * 
	 * @param newOne
	 */
	private void add(Automaton newOne, Automaton other) {
		AutomatonDrawer d1 = new AutomatonDrawer(newOne), d2 = new AutomatonDrawer(
				other);
		Rectangle2D bounds1 = d1.getBounds(), bounds2 = d2.getBounds();
		if (bounds1 == null)
			bounds1 = new Rectangle2D.Float();
		if (bounds2 == null)
			bounds2 = new Rectangle2D.Float();
		double d = bounds1.getY() + bounds1.getHeight() - bounds2.getY() + 20.0;
		State[] otherStates = other.getStates();
		Map otherToNew = new HashMap();
		for (int i = 0; i < otherStates.length; i++) {
			State s = otherStates[i];
			Point p = new Point(s.getPoint().x, s.getPoint().y + (int) d);
			State s2 = newOne.createState(p);
			if (other.isFinalState(s))
				newOne.addFinalState(s2);
			s2.setLabel(s.getLabel());
            
            /*
             * To maintain Moore machine state output.
             */
            if(newOne instanceof MooreMachine && other instanceof MooreMachine)
            {
                MooreMachine m = (MooreMachine) newOne;
                MooreMachine n = (MooreMachine) other;
                m.setOutput(s2, n.getOutput(s));
            }
            
			otherToNew.put(s, s2);
		}
		Transition[] otherTransitions = other.getTransitions();
		for (int i = 0; i < otherTransitions.length; i++) {
			Transition t = otherTransitions[i];
			State from = (State) otherToNew.get(t.getFromState()), to = (State) otherToNew
					.get(t.getToState());
			newOne.addTransition(t.copy(from, to));
		}
	}

	/** The environment. */
	private AutomatonEnvironment environment;
}
