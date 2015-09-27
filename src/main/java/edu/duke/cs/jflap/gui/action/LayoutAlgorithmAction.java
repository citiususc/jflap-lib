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
import edu.duke.cs.jflap.automata.turing.TuringMachine;
import edu.duke.cs.jflap.automata.pda.PushdownAutomaton;
import edu.duke.cs.jflap.automata.mealy.MealyMachine;
import edu.duke.cs.jflap.automata.Automaton;
import edu.duke.cs.jflap.automata.graph.*;
import edu.duke.cs.jflap.gui.environment.Environment;

/**
 * This action allows for a layout algorithm to be applied to an automaton.
 * 
 * @author Chris Morgan
 */
public class LayoutAlgorithmAction extends AutomatonAction {
	/**
	 * The amount of space it is assumed that the <code>environment</code> will use for menus,
	 * buttons, framseBorders, etc.  It is not the shape of a height*width box, but rather it simply
	 * stores the relative height and width taken up when starting from the respective borders.
	 */
	private final Dimension assumedUsedSpace = new Dimension(25, 100);
	/**
	 * The automaton for which a layout algorithm will be applied.
	 */
	private Automaton automaton;
	/**
	 * The environment in which the automaton is placed.
	 */
	private Environment environment;
	/**
	 * Used to specify the specific algorithm chosen.  For the list of algorithm identifiers,
	 * look in <code>automata.graph.LayoutAlgorithmFactory</code>.
	 */
	private int algorithm;		 
	
	/**
	 * Constructor.
	 * 
	 * @param s 
	 *     the title of this action.
	 * @param a 
	 *     the automaton this action will move.
	 * @param e 
	 *     the environment this automaton is in.
	 * @param algm 
	 *     a numerical identifier for the algorithm that will be used.  The constants
	 *     utilized are stored in automata.graph.LayoutAlgorithmFactory.
	 */
	public LayoutAlgorithmAction(String s, Automaton a, Environment e, int algm) {
		super(s, null);
		automaton = a;
		environment = e;
		algorithm = algm;
	}
	
	public void actionPerformed(ActionEvent e) {		
		double vertexBuffer;
		if (automaton instanceof TuringMachine)
			vertexBuffer = 80 * ((TuringMachine) automaton).tapes();
		else if (automaton instanceof PushdownAutomaton)
			vertexBuffer = 80;
		else if (automaton instanceof MealyMachine)
			vertexBuffer = 65;
		else
			vertexBuffer = 50;
		AutomatonGraph graph = LayoutAlgorithmFactory.getAutomatonGraph(algorithm, automaton);
		LayoutAlgorithm layout = LayoutAlgorithmFactory.getLayoutAlgorithm(algorithm, 
				new Dimension((int) environment.getSize().getWidth() - (int)assumedUsedSpace.getWidth(),
				(int)environment.getSize().getHeight() - (int)assumedUsedSpace.getHeight()), 
				new Dimension(30, 30), vertexBuffer);
		layout.layout(graph, null);
		graph.moveAutomatonStates();
	}
}
