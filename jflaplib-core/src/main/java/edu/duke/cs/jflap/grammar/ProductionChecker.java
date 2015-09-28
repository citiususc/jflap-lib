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

/**
 * The Production checker object can be used to check certain properties of
 * production objects.
 * 
 * @author Ryan Cavalcante
 */

public class ProductionChecker {
	/**
	 * Creates an instance of <CODE>ProductionChecker</CODE>.
	 */
	public ProductionChecker() {

	}

	/**
	 * Returns true if <CODE>production</CODE> is linear (i.e. either right or
	 * left linear).
	 * 
	 * @param production
	 *            the production
	 * @return true if <CODE>production</CODE> is linear.
	 */
	public static boolean isLinear(Production production) {
		if (isRightLinear(production) || isLeftLinear(production)) {
			return true;
		}
		return false;
	}

	/**
	 * Returns true if <CODE>production</CODE> is right linear.
	 * 
	 * @param production
	 *            the production.
	 * @return true if <CODE>production</CODE> is right linear.
	 */
	public static boolean isRightLinear(Production production) {
		if (isRightLinearProductionWithVariable(production)
				|| isLinearProductionWithNoVariable(production))
			return true;
		return false;
	}

	/**
	 * Returns true if <CODE>production</CODE> is left linear.
	 * 
	 * @param production
	 *            the production.
	 * @return true if <CODE>production</CODE> is left linear.
	 */
	public static boolean isLeftLinear(Production production) {
		if (isLeftLinearProductionWithVariable(production)
				|| isLinearProductionWithNoVariable(production))
			return true;
		return false;
	}

	/**
	 * Returns true if <CODE>production</CODE> is a production of the form
	 * A->Bx where x is a series of 0 or more terminals
	 * 
	 * @param production
	 *            the production
	 * @return true if <CODE>production</CODE> is a production of the form
	 *         A->Bx where x is a series of 0 or more terminals
	 */
	public static boolean isLeftLinearProductionWithVariable(
			Production production) {
		if (!isRestrictedOnLHS(production))
			return false;
		String rhs = production.getRHS();
		/**
		 * if only one variable on rhs and it is first char on rhs.
		 */
		String[] variables = production.getVariablesOnRHS();
		if (variables.length == 1) {
			char ch = rhs.charAt(0);
			if (isVariable(ch)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns true if <CODE>production</CODE> is a production of the form
	 * A->xB where x is a series of 0 or more terminals
	 * 
	 * @param production
	 *            the production
	 * @return true if <CODE>production</CODE> is a production of the form
	 *         A->xB where x is a series of 0 or more terminals
	 */
	public static boolean isRightLinearProductionWithVariable(
			Production production) {
		if (!isRestrictedOnLHS(production))
			return false;
		String rhs = production.getRHS();
		/**
		 * if only one variable on rhs and it is last char on rhs.
		 */
		String[] variables = production.getVariablesOnRHS();
		if (variables.length == 1) {
			char ch = rhs.charAt(rhs.length() - 1);
			if (isVariable(ch)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns true if <CODE>production</CODE> is a production of the form
	 * A->x where x is a series of 0 or more terminals.
	 * 
	 * @param production
	 *            the production
	 * @return true if <CODE>production</CODE> is a production of the form
	 *         A->x where x is a series of 0 or more terminals.
	 */
	public static boolean isLinearProductionWithNoVariable(Production production) {
		if (!isRestrictedOnLHS(production))
			return false;
		String rhs = production.getRHS();
		/** if rhs is all terminals. */
		String[] terminals = production.getTerminalsOnRHS();
		if (rhs.length() == terminals.length)
			return true;
		return false;
	}

	/**
	 * Returns true if <CODE>production</CODE> is a unit production.
	 * 
	 * @param production
	 *            the production.
	 * @return true if <CODE>production</CODE> is a unit production.
	 */
	public static boolean isUnitProduction(Production production) {
		if (!isRestrictedOnLHS(production))
			return false;
		String rhs = production.getRHS();
		String[] variablesOnRHS = production.getVariablesOnRHS();
		if (rhs.length() == 1 && variablesOnRHS.length == 1) {
			return true;
		}
		return false;
	}

	/**
	 * Returns true if <CODE>production</CODE> is a lambda production.
	 * 
	 * @param production
	 *            the production.
	 * @return true if <CODE>production</CODE> is a lambda production.
	 */
	public static boolean isLambdaProduction(Production production) {
		if (!isRestrictedOnLHS(production))
			return false;
		String rhs = production.getRHS();
		if (rhs.length() == 0) {
			return true;
		}
		return false;
	}

	/**
	 * Returns true if the left hand side of <CODE>production</CODE> is a
	 * single variable.
	 * 
	 * @param production
	 *            the production.
	 * @return true if the left hand side of <CODE>production</CODE> is a
	 *         single variable.
	 */
	public static boolean isRestrictedOnLHS(Production production) {
		String lhs = production.getLHS();
		String[] variablesOnLHS = production.getVariablesOnLHS();
		if (lhs.length() == 1 && variablesOnLHS.length == 1) {
			return true;
		}
		return false;
	}

	/**
	 * Returns true if <CODE>variable</CODE> is in the production, either on
	 * the right or left hand side of the production.
	 * 
	 * @param variable
	 *            the variable.
	 * @param production
	 *            the production.
	 * @return true if <CODE>variable</CODE> is in the production.
	 */
	public static boolean isVariableInProduction(String variable,
			Production production) {
		String[] variables = production.getVariables();
		for (int k = 0; k < variables.length; k++) {
			if (variables[k].equals(variable))
				return true;
		}
		return false;
	}

	/**
	 * Returns true if <CODE>terminal</CODE> is in the production, either on
	 * the right or left hand side of the production.
	 * 
	 * @param terminal
	 *            the terminal.
	 * @param production
	 *            the production.
	 * @return true if <CODE>terminal</CODE> is in the production.
	 */
	public static boolean isTerminalInProduction(String terminal,
			Production production) {
		String[] terminals = production.getTerminals();
		for (int k = 0; k < terminals.length; k++) {
			if (terminals[k].equals(terminal))
				return true;
		}
		return false;
	}

	/**
	 * Returns true if there are 1 or more terminals on the rhs of <CODE>productions</CODE>.
	 * 
	 * @param production
	 *            the production
	 * @return true if there are 1 or more terminals on the rhs of <CODE>productions</CODE>.
	 */
	public static boolean areTerminalsOnRHS(Production production) {
		String rhs = production.getRHS();
		for (int k = 0; k < rhs.length(); k++) {
			char ch = rhs.charAt(k);
			if (isTerminal(ch))
				return true;
		}
		return false;
	}

	/**
	 * Returns true if <CODE>ch</CODE> is a variable. A variable is determined
	 * to be any uppercase character.
	 * 
	 * @param ch
	 *            the character being checked.
	 * @return true if <CODE>ch</CODE> is a variable.
	 */
	public static boolean isVariable(char ch) {
		return Character.isUpperCase(ch);
	}

	/**
	 * Returns true if <CODE>ch</CODE> is a terminal. A terminal is determined
	 * to be any lowercase character.
	 * 
	 * @param ch
	 *            the character being checked.
	 * @return true if <CODE>ch</CODE> is a terminal.
	 */
	public static boolean isTerminal(char ch) {
		return !isVariable(ch);
	}

	/**
	 * Returns true if <CODE>variable</CODE> is on the right hand side of
	 * <CODE>production</CODE>.
	 * 
	 * @param production
	 *            the production
	 * @param variable
	 *            the variable
	 * @return true if <CODE>variable</CODE> is on the right hand side of
	 *         <CODE>production</CODE>.
	 */
	public static boolean isVariableOnRHS(Production production, String variable) {
		String[] variables = production.getVariablesOnRHS();
		for (int k = 0; k < variables.length; k++) {
			if (variables[k].equals(variable))
				return true;
		}
		return false;
	}
}
