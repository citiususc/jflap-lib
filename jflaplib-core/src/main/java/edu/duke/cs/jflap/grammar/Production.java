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





package edu.duke.cs.jflap.grammar;

import edu.duke.cs.jflap.gui.environment.Universe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * A Production object is a simple abstract class that represents a production
 * rule in a grammar.
 * 
 * @author Ryan Cavalcante
 */

public class Production implements Serializable {
	/**
	 * Creates an instance of <CODE>Production</CODE>.
	 * 
	 * @param lhs
	 *            the left hand side of the production rule.
	 * @param rhs
	 *            the right hand side of the production rule.
	 */
	public Production(String lhs, String rhs) {
        if(lhs == null) lhs = "";
        if(rhs == null) rhs = "";
		myLHS = lhs;
		myRHS = rhs;
	}

	/**
	 * Sets the right hand side of production to <CODE>rhs</CODE>.
	 * 
	 * @param rhs
	 *            the right hand side
	 */
	public void setRHS(String rhs) {
		myRHS = rhs;
	}

	/**
	 * Sets the left hand side of production to <CODE>lhs</CODE>.
	 * 
	 * @param lhs
	 *            the left hand side
	 */
	public void setLHS(String lhs) {
		myLHS = lhs;
	}

	/**
	 * Returns a string representation of the left hand side of the production
	 * rule.
	 * 
	 * @return a string representation of the lhs.
	 */
	public String getLHS() {
		return myLHS;
	}

	/**
	 * Returns a string representation of the right hand side of the production
	 * rule.
	 * 
	 * @return a string representation of the rhs.
	 */
	public String getRHS() {
		return myRHS;
	}

	/**
	 * Returns all symbols (both variables in terminals) in a production.
	 * 
	 * @return all symbols in a production
	 */
	public String[] getSymbols() {
		SortedSet symbols = new TreeSet();
		symbols.addAll(Arrays.asList(getVariables()));
		symbols.addAll(Arrays.asList(getTerminals()));
		return (String[]) symbols.toArray(new String[0]);
	}

	/**
	 * Returns all variables in the production.
	 * 
	 * @return all variables in the production.
	 */
	public String[] getVariables() {

		ArrayList list = new ArrayList();
		String[] rhsVariables = getVariablesOnRHS();
		for (int k = 0; k < rhsVariables.length; k++) {
			if (!list.contains(rhsVariables[k])) {
				list.add(rhsVariables[k]);
			}
		}

		String[] lhsVariables = getVariablesOnLHS();
		for (int i = 0; i < lhsVariables.length; i++) {
			if (!list.contains(lhsVariables[i])) {
				list.add(lhsVariables[i]);
			}
		}

		return (String[]) list.toArray(new String[0]);
	}

	/**
	 * Returns all variables on the left hand side of the production.
	 * 
	 * @return all variables on the left hand side of the production.
	 */
	public String[] getVariablesOnLHS() {
		ArrayList list = new ArrayList();
        if(myLHS == null) return new String[0];
		for(int i = 0; i < myLHS.length(); i++) {
			char c = myLHS.charAt(i);
			if (ProductionChecker.isVariable(c))
				list.add(myLHS.substring(i, i + 1));
		}
		return (String[]) list.toArray(new String[0]);
	}

	/**
	 * Returns all variables on the right hand side of the production.
	 * 
	 * @return all variables on the right hand side of the production.
	 */
	public String[] getVariablesOnRHS() {
		ProductionChecker pc = new ProductionChecker();
		ArrayList list = new ArrayList();
		for (int i = 0; i < myRHS.length(); i++) {
			char c = myRHS.charAt(i);
			if (ProductionChecker.isVariable(c))
				list.add(myRHS.substring(i, i + 1));
		}
		return (String[]) list.toArray(new String[0]);
	}

	/**
	 * Returns all terminals in the production.
	 * 
	 * @return all terminals in the production.
	 */
	public String[] getTerminals() {
		ArrayList list = new ArrayList();
		String[] rhsTerminals = getTerminalsOnRHS();
		for (int k = 0; k < rhsTerminals.length; k++) {
			if (!list.contains(rhsTerminals[k])) {
				list.add(rhsTerminals[k]);
			}
		}

		String[] lhsTerminals = getTerminalsOnLHS();
		for (int i = 0; i < lhsTerminals.length; i++) {
			if (!list.contains(lhsTerminals[i])) {
				list.add(lhsTerminals[i]);
			}
		}

		return (String[]) list.toArray(new String[0]);
	}

	/**
	 * Returns all terminals on the right hand side of the production.
	 * 
	 * @return all terminals on the right hand side of the production.
	 */
	public String[] getTerminalsOnRHS() {
		ProductionChecker pc = new ProductionChecker();
		ArrayList list = new ArrayList();
		for (int i = 0; i < myRHS.length(); i++) {
			char c = myRHS.charAt(i);
			if (ProductionChecker.isTerminal(c))
				list.add(myRHS.substring(i, i + 1));
		}
		return (String[]) list.toArray(new String[0]);
	}

	/**
	 * Returns true if <CODE>production</CODE> is equivalent to this
	 * production (i.e. they have identical left and right hand sides).
	 * 
	 * @param production
	 *            the production being compared to this production
	 * @return true if <CODE>production</CODE> is equivalent to this
	 *         production (i.e. they have identical left and right hand sides).
	 */
	public boolean equals(Object production) {
		if (production instanceof Production) {
			Production p = (Production) production;
			return getRHS().equals(p.getRHS()) && getLHS().equals(p.getLHS());
		}
		return false;
	}

	/**
	 * Returns a hashcode for this production.
	 * 
	 * @return the hashcode for this production
	 */
	public int hashCode() {
		return myRHS.hashCode() ^ myLHS.hashCode();
	}

	/**
	 * Returns all terminals on the left hand side of the production.
	 * 
	 * @return all terminals on the left hand side of the production.
	 */
	public String[] getTerminalsOnLHS() {
		ArrayList list = new ArrayList();
		for (int i = 0; i < myLHS.length(); i++) {
			char c = myLHS.charAt(i);
			if (ProductionChecker.isTerminal(c))
				list.add(myLHS.substring(i, i + 1));
		}
		return (String[]) list.toArray(new String[0]);
	}

	/**
	 * Returns a string representation of the production object.
	 * 
	 * @return a string representation of the production object.
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(getLHS());
		// buffer.append("->");
		buffer.append('\u2192');
		String rhs = getRHS();
		buffer.append(rhs.length() == 0 ? Universe.curProfile.getEmptyString() : rhs);
		// buffer.append('\n');
		return buffer.toString();
	}

	/**
	 * Returns the sequence of symbols in either the left or right hand side.
	 * For example, for the production <CODE>A -> BCD</CODE> this would return
	 * the array of strings <CODE>{"B","C","D"}</CODE>.
	 */
	public String[] getSymbolsOnRHS() {
		ArrayList list = new ArrayList();
		for (int i = 0; i < myRHS.length(); i++) {
			char c = myRHS.charAt(i);
			list.add(myRHS.substring(i, i + 1));
		}
		return (String[]) list.toArray(new String[0]);
	}

	/** the left hand side of the production. */
	protected String myLHS;

	/** the right hand side of the production. */
	protected String myRHS;
}
