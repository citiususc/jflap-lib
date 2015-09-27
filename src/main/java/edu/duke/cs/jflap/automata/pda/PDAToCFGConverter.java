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





package edu.duke.cs.jflap.automata.pda;

import edu.duke.cs.jflap.grammar.Production;
import edu.duke.cs.jflap.grammar.cfg.ContextFreeGrammar;
import edu.duke.cs.jflap.gui.grammar.GrammarTableModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;

import edu.duke.cs.jflap.automata.Automaton;
import edu.duke.cs.jflap.automata.State;
import edu.duke.cs.jflap.automata.Transition;

/**
 * The PDA to context free grammar converter can be used to convert a pushdown
 * automaton into an equivalent context free grammar. The pda and grammar will
 * be equivalent in that they will accept exactly the same language. Before
 * using the converter, except for the first time, you must call
 * initializeConverter to prepare the converter for the conversion. This will
 * reset all data, maps, etc. used during the last conversion done by the
 * converter. You can do the conversion simply by calling
 * convertToContextFreeGrammar, or you can perform the conversion step by step
 * repeatedly calling getProductionsForTransition on every transition in the pda
 * and adding all of the returned productions to your context free grammar. If
 * you do this for every transition in the pda, you will have an equivalent cfg.
 * 
 * @see edu.duke.cs.jflap.grammar.cfg.ContextFreeGrammar
 * 
 * @author Ryan Cavalcante
 */

public class PDAToCFGConverter {
	/**
	 * Creates an instance of <CODE>PDAToCFGConverter</CODE>.
	 */
	public PDAToCFGConverter() {
		initializeConverter();
	}

	/**
	 * Initializes converter for pda to cfg conversion (clears map and sets
	 * unique id)
	 */
	public void initializeConverter() {
		MAP = new HashMap();
		UNIQUE_ID = 0;
	}

	/**
	 * Returns true if <CODE>automaton</CODE> has a single final state that is
	 * entered if and only if the stack is empty.
	 * 
	 * @param automaton
	 *            the automaton.
	 * @return true if <CODE>automaton</CODE> has a single final state that is
	 *         entered if and only if the stack is empty.
	 */
	public boolean hasSingleFinalState(Automaton automaton) {
		State[] finalStates = automaton.getFinalStates();
		if (finalStates.length != 1) {
			// System.err.println("There is not exactly one final state!");
			return false;
		}

		State finalState = finalStates[0];
		Transition[] transitions = automaton.getTransitionsToState(finalState);
		for (int k = 0; k < transitions.length; k++) {
			PDATransition trans = (PDATransition) transitions[k];
			String toPop = trans.getStringToPop();
			if (!(toPop.substring(toPop.length() - 1)).equals(BOTTOM_OF_STACK)) {
				// System.err.println("Bad transition to final state! "+trans);
				// System.err.println(toPop.substring(toPop.length()-1));
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns true if all transitions in <CODE>automaton</CODE> read a single
	 * character from the input, pop a single character from the stack and push
	 * either zero or two characters on to the stack.
	 * 
	 * @param automaton
	 *            the automaton
	 * @return true if all transitions in <CODE>automaton</CODE> read a single
	 *         character from the input, pop a single character from the stack
	 *         and push either zero or two characters on to the stack.
	 */
	public boolean hasTransitionsInCorrectForm(Automaton automaton) {
		Transition[] transitions = automaton.getTransitions();
		for (int k = 0; k < transitions.length; k++) {
			if (!isPushLambdaTransition(transitions[k])
					&& !isPushTwoTransition(transitions[k])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns true if <CODE>automaton</CODE> is in the correct form to
	 * perform the conversion to CFG. The correct form enforces two restrictions
	 * on <CODE>automaton</CODE> : 1. it has a single final state that is
	 * entered if and only if the stack is empty. 2. all transitions read a
	 * single character from the input, pop a single character from the stack
	 * and either push two or zero characters on to the stack.
	 * 
	 * @param automaton
	 *            the automaton
	 * @return true if <CODE>automaton</CODE> is in the correct form to
	 *         perform the conversion to CFG.
	 */
	public boolean isInCorrectFormForConversion(Automaton automaton) {
		if (hasSingleFinalState(automaton)
				&& hasTransitionsInCorrectForm(automaton)) {
			return true;
		}
		return false;
	}

	/**
	 * Returns true if <CODE>transition</CODE> reads a single character from
	 * the input tape, pops a single character from the stack, and writes TWO
	 * characters to the stack.
	 * 
	 * @param transition
	 *            the transition
	 * @return true if <CODE>transition</CODE> reads a single character from
	 *         the input tape, pops a single character from the stack, and
	 *         writes TWO characters to the stack.
	 */
	public boolean isPushTwoTransition(Transition transition) {
		PDATransition trans = (PDATransition) transition;
		String toPush = trans.getStringToPush();
		if (toPush.length() != 2)
			return false;
		/*
		 * String input = trans.getInputToRead(); if(input.length() != 1) return
		 * false;
		 */
		String toPop = trans.getStringToPop();
		if (toPop.length() != 1)
			return false;
		return true;
	}

	/**
	 * Returns true if <CODE>transition</CODE> reads a single character from
	 * the input tape, pops a single character from the stack, and writes NO
	 * characters to the stack.
	 * 
	 * @param transition
	 *            the transition
	 * @return true if <CODE>transition</CODE> reads a single character from
	 *         the input tape, pops a single character from the stack, and
	 *         writes NO characters to the stack.
	 */
	public boolean isPushLambdaTransition(Transition transition) {
		PDATransition trans = (PDATransition) transition;
		String toPush = trans.getStringToPush();
		if (toPush.length() != 0)
			return false;
		/*
		 * String input = trans.getInputToRead(); if(input.length() != 1) return
		 * false;
		 */
		String toPop = trans.getStringToPop();
		if (toPop.length() != 1)
			return false;
		return true;
	}

	/**
	 * Returns a unique variable.
	 * 
	 * @return a unique variable.
	 */
	private String getUniqueVariable() {
		char[] ch = new char[1];
		ch[0] = (char) ('A' + UNIQUE_ID);
		UNIQUE_ID++;
		if (('A' + UNIQUE_ID) == 'S')
			UNIQUE_ID++;
		return new String(ch);
	}

	/**
	 * Returns true if <CODE>variable</CODE> is the start symbol. (i.e.
	 * "(q0Zqf)")
	 * 
	 * @param variable
	 *            the variable.
	 * @param automaton
	 *            the automaton.
	 * @return true if <CODE>variable</CODE> is the start symbol.
	 */
	public boolean isStartSymbol(String variable, Automaton automaton) {
		State startState = automaton.getInitialState();
		State[] finalStates = automaton.getFinalStates();
		if (finalStates.length > 1) {
			// System.err.println("MORE THAN ONE FINAL STATE");
			return false;
		}
		State finalState = finalStates[0];
		String startSymbol = LEFT_PAREN.concat(startState.getName().concat(
				BOTTOM_OF_STACK
						.concat(finalState.getName().concat(RIGHT_PAREN))));
		if (variable.equals(startSymbol))
			return true;
		return false;
	}

	/**
	 * Returns a list of productions created for <CODE>transition</CODE>, a
	 * transition that pushes TWO characters on the stack.
	 * 
	 * @param transition
	 *            the transition
	 * @param automaton
	 *            the automaton
	 * @return a list of productions created for <CODE>transition</CODE>, a
	 *         transition that pushes TWO characters on the stack.
	 */
	public ArrayList getProductionsForPushTwoTransition(Transition transition,
			Automaton automaton) {
		ArrayList list = new ArrayList();
		String fromState = transition.getFromState().getName();
		String toState = transition.getToState().getName();
		PDATransition trans = (PDATransition) transition;
		String toPop = trans.getStringToPop();
		String toRead = trans.getInputToRead();
		String toPush = trans.getStringToPush();
		String toPushOne = toPush.substring(0, 1);
		String toPushTwo = toPush.substring(1);

		State[] states = automaton.getStates();
		for (int k = 0; k < states.length; k++) {
			String state = states[k].getName();
			String lhs = LEFT_PAREN.concat(fromState.concat(toPop.concat(state
					.concat(RIGHT_PAREN))));
			for (int j = 0; j < states.length; j++) {
				String lstate = states[j].getName();
				String variable1 = LEFT_PAREN.concat(toState.concat(toPushOne
						.concat(lstate.concat(RIGHT_PAREN))));
				String variable2 = LEFT_PAREN.concat(lstate.concat(toPushTwo
						.concat(state.concat(RIGHT_PAREN))));

				/** Map to unique variables. */
				if (MAP.get(lhs) == null) {
					if (isStartSymbol(lhs, automaton))
						MAP.put(lhs, START_SYMBOL);
					else
						MAP.put(lhs, getUniqueVariable());
				}
				if (MAP.get(variable1) == null) {
					if (isStartSymbol(variable1, automaton))
						MAP.put(variable1, START_SYMBOL);
					else
						MAP.put(variable1, getUniqueVariable());
				}
				if (MAP.get(variable2) == null) {
					if (isStartSymbol(variable2, automaton))
						MAP.put(variable2, START_SYMBOL);
					else
						MAP.put(variable2, getUniqueVariable());
				}

				String rhs = toRead.concat(variable1.concat(variable2));

				Production p = new Production(lhs, rhs);
				list.add(p);
			}
		}
		return list;
	}

	/**
	 * Returns a list of productions created for <CODE>transition</CODE>, a
	 * transition that pushes NO characters on the stack. This list will always
	 * contain a single production.
	 * 
	 * @param transition
	 *            the transition
	 * @return a list of productions created for <CODE>transition</CODE>, a
	 *         transition that pushes NO characters on the stack. This list will
	 *         always contain a single produciton.
	 */
	public ArrayList getProductionsForPushLambdaTransition(
			Transition transition, Automaton automaton) {
		ArrayList list = new ArrayList();
		String fromState = transition.getFromState().getName();
		String toState = transition.getToState().getName();
		PDATransition trans = (PDATransition) transition;
		String toPop = trans.getStringToPop();
		String toRead = trans.getInputToRead();

		String lhs = LEFT_PAREN.concat(fromState.concat(toPop.concat(toState
				.concat(RIGHT_PAREN))));
		if (MAP.get(lhs) == null) {
			if (isStartSymbol(lhs, automaton))
				MAP.put(lhs, START_SYMBOL);
			else
				MAP.put(lhs, getUniqueVariable());
		}
		String rhs = toRead;

		Production production = new Production(lhs, rhs);
		list.add(production);
		return list;
	}

	/**
	 * Returns a list of productions that represent the same functionality as
	 * <CODE>transition</CODE> in <CODE>automaton</CODE>.
	 * 
	 * @param transition
	 *            the transition
	 * @param automaton
	 *            the automaton that transition is a part of.
	 * @return a list of productions.
	 */
	public ArrayList createProductionsForTransition(Transition transition,
			Automaton automaton) {
		ArrayList list = new ArrayList();
		if (isPushLambdaTransition(transition)) {
			list.addAll(getProductionsForPushLambdaTransition(transition,
					automaton));
		} else if (isPushTwoTransition(transition)) {
			list.addAll(getProductionsForPushTwoTransition(transition,
					automaton));
		}

		return list;
	}

	/**
	 * Returns an equivalent production to <CODE>production</CODE> but with
	 * each variable (e.g. "q1Aq3") replaced by a unique variable (e.g. "B");
	 * 
	 * @param production
	 *            the production
	 * @return an equivalent production to <CODE>production</CODE> with a
	 *         single variable replacing groups of characters.
	 */
	public Production getSimplifiedProduction(Production production) {
		String lhs = (String) MAP.get(production.getLHS());
		String rhs = production.getRHS();
		int leftIndex, rightIndex; // Position of left and right parentheses.
		StringBuffer newRhs = new StringBuffer();
		while ((leftIndex = rhs.indexOf('(')) != -1
				&& (rightIndex = rhs.indexOf(')')) != -1) {
			newRhs.append(rhs.substring(0, leftIndex));
			String variable = rhs.substring(leftIndex, rightIndex + 1);
			newRhs.append(MAP.get(variable));
			rhs = rhs.substring(rightIndex + 1);
		}
		newRhs.append(rhs);
		Production p = new Production(lhs, newRhs.toString());
		return p;
	}

	/**
	 * Returns the number of unique variables defined sofar in this conversion.
	 * 
	 * @return the number of unique variables
	 */
	public int numberVariables() {
		return (new HashSet(MAP.values())).size();
	}

	/**
	 * Returns a ContextFreeGrammar object that represents a grammar equivalent
	 * to <CODE>automaton</CODE>.
	 * 
	 * @param automaton
	 *            the automaton.
	 * @return a cfg equivalent to <CODE>automaton</CODE>.
	 */
	public ContextFreeGrammar convertToContextFreeGrammar(Automaton automaton) {
		/** check if automaton is pda. */
		if (!(automaton instanceof PushdownAutomaton))
			throw new IllegalArgumentException(
					"automaton must be PushdownAutomaton");

		if (!isInCorrectFormForConversion(automaton))
			throw new IllegalArgumentException(
					"automaton not in correct form for conversion to CFG");

		initializeConverter();

		ArrayList list = new ArrayList();
		ContextFreeGrammar grammar = new ContextFreeGrammar();

		Transition[] transitions = automaton.getTransitions();
		for (int k = 0; k < transitions.length; k++) {
			list.addAll(createProductionsForTransition(transitions[k],
					automaton));
		}

		Iterator it = list.iterator();
		while (it.hasNext()) {
			Production p = (Production) it.next();
			grammar.addProduction(getSimplifiedProduction(p));
		}

		return grammar;
	}
	
	
	/**
	 * Recursive function used by <code>purgeProductions()</code> to determine which productions should be
	 * included in the grammar.  It takes a variable and recursively checks all productions that have it on
	 * the left-hand side to determine whether each production should be accepted
	 * 
	 * @param lhs 
	 		     the variable on the left-hand side.  The initial variable is the initial state + 'Z' +
	 * 			 the final state.
	 * @param productions
	 * 			 the current list of productions.
	 * @param valid 
	 *           set of variables that potentially end in terminals
	 * @param validProductions 
	 *           an int array used to mark all productions that should not be removed.  The values that 
	 *           can be in it are: <br><br> -1 - production should be removed <br> 0 - yet to be processed <br> 
	 *           1 - currently being processed (helps prevent cycling) <br> 2 - production should be kept
	 * @return true if one of the following three are true, false otherwise.<br><br> 1.  the lhs leads to a leaf node.
	 * <br> 2.  the lhs leads to a cycle. <br> 3. All variables on the right side lead to a cycle or a leaf node.
	 */
	private void purgeProductionsHelper(String lhs, Production[] productions, HashSet valid, int[] validProductions){		
		ArrayList variables;
		String rhs;			
		for (int i=0; i<productions.length; i++)
			if (productions[i].getLHS().equals(lhs) && validProductions[i] == 0) {				 
				validProductions[i] = 1;
				variables = new ArrayList();
				rhs = new String(productions[i].getRHS());
				while (rhs.indexOf(LEFT_PAREN) > -1) {				
					variables.add(rhs.substring(rhs.indexOf(LEFT_PAREN), rhs.indexOf(RIGHT_PAREN)+1));
					if (rhs.indexOf(RIGHT_PAREN) != rhs.length()-1)
						rhs = rhs.substring(rhs.indexOf(RIGHT_PAREN) + 1);
					else
						rhs = "";
				}
				for (int j=0; j<variables.size(); j++) {
					if (validProductions[i] == 1 && !valid.contains(variables.get(j)))
						validProductions[i] = -1;					
				}
				if (validProductions[i] == 1) {
					validProductions[i] = 2;	
					for (int j=0; j<variables.size(); j++)
						purgeProductionsHelper((String) variables.get(j), productions, valid, validProductions);
				}
			}
	}
	
	/**
	 * Gets rid of superfluous productions in the table before transforming into a CFG.
	 * 
	 * @param automaton
	 *            the current automaton
	 * @param model 
	 *            the table of productions
	 * @author Chris Morgan
	 */
	public void purgeProductions(Automaton automaton, GrammarTableModel model) {		
		Production[] productions = model.getProductions();
		HashSet valid = new HashSet();
		Stack variables, invalid;
		boolean updated;
		int[] validProductions = new int[productions.length];
		for (int i = 0; i < productions.length; i++)
			validProductions[i] = 0;
		
		//After initializing variables, add all variables that can eventually end in terminals
		//to a stack.
		do {			
			updated = false;						
			for (int i=0; i<validProductions.length; i++) {
				variables = new Stack();
				invalid = new Stack();
				String rhs = productions[i].getRHS();
				while (rhs.indexOf(LEFT_PAREN) > -1) {								
					variables.push(rhs.substring(rhs.indexOf(LEFT_PAREN), rhs.indexOf(RIGHT_PAREN)+1));
					if (rhs.indexOf(RIGHT_PAREN) != rhs.length()-1)
						rhs = rhs.substring(rhs.indexOf(RIGHT_PAREN) + 1);
					else
						rhs = "";
				}
				
				while (variables.size() > 0)
					if (!valid.contains((String) variables.peek()))						
						invalid.push(variables.pop());					
					else
						variables.pop();
				if (invalid.size() == 0 && !valid.contains(productions[i].getLHS())) {
					updated = true;
					valid.add(productions[i].getLHS());
				}									
			}			
		} while (updated);
		
		//Then, trace a path from the initial variable to all terminals that it can reach.
		String initVar = LEFT_PAREN + automaton.getInitialState().getName() + BOTTOM_OF_STACK + 
					automaton.getFinalStates()[0].getName() + RIGHT_PAREN;
		purgeProductionsHelper(initVar, productions, valid, validProductions);
		
		//Next, delete all superfluous rows and make note of those capital-letter variable
		//assignments that are freed up in a new map.
		HashMap newMap = new HashMap();
		HashSet freeValues = new HashSet();
		String key;
		for (int i=0; i<26; i++)
			freeValues.add("" + (char)('A' + i));
		for (int i=validProductions.length-1; i>=0; i--)  
			if (validProductions[i] < 2) 
				model.deleteRow(i);
			else {
				key = productions[i].getLHS();
				newMap.put(key, MAP.get(key));
				if (((String)MAP.get(key)).charAt(0) <= 'Z')
					freeValues.remove((String)MAP.get(key));
			}
		
		//Finally, assign the new map to the old map, and assign one-letter variables to 
		//any variables that need one.
		MAP = newMap;
		Iterator freeIter, mapIter;
		freeIter = freeValues.iterator();
		mapIter = newMap.keySet().iterator();
		
		while (mapIter.hasNext()) {			
			key = (String) mapIter.next();
			if (((String)MAP.get(key)).charAt(0) > 'Z')
				MAP.put(key, (String)freeIter.next());
		}
	}

	protected static final String START_SYMBOL = "S";

	protected int UNIQUE_ID;

	protected HashMap MAP;

	protected static final String LEFT_PAREN = "(";

	protected static final String RIGHT_PAREN = ")";

	protected static final String BOTTOM_OF_STACK = "Z";
}
