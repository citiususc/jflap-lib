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

import edu.duke.cs.jflap.grammar.cfg.ContextFreeGrammar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * The lambda production remover object can be used to convert a grammar to an
 * equivalent grammar with no lambda productions. This operation consists of 3
 * steps: 1. Add all variables that have lamba productions (e.g. A->lambda) to
 * lambdaSet. 2. Add all variables that have productions that are reducable to
 * lambda (if B->A1A2...Am and Ai are all in lambdaSet, then put B in lambdaSet)
 * to lambdaSet. 3. Construct a new grammar with productions such that for each
 * production in the original grammar A->x1x2x3...xm, put all productions formed
 * when xj is replaced by lambda for all xj in lambdaSet. So, if there are more
 * than 1 variable in a given production from the original grammar in the
 * lambdaSet, you must account for all permutations (i.e. all combinations of
 * which variables are replaced by lambda and which aren't). Each of these three
 * steps can be performed immediately or step by step. You can perform step 1
 * immediately by calling {@link #addVariablesWithLambdaProductions} after
 * creating an empty lambdaSet by calling {@link #getNewLambdaSet}. Or you can
 * add the variables to lambdaSet step by step while {@link
 * #areMoreVariablesWithLambdaProductions} is true, calling {@link
 * #getNewVariableWithLambdaProduction} and adding it to lambdaSet. If the user
 * tries to add a variable to lambdaSet, you can check if it has a lambda
 * production by calling {@link #isVariableWithLambdaProduction} before adding
 * it to the lambdaSet. Step 2 can also be completed immediately by calling
 * {@link #getCompleteLambdaSet}. Or you can do it step by step by continually
 * calling getNewVariableThatBelongsInLambdaSet while
 * areMoreVariablesThatBelongInLambdaSet returns true. Or if the user tries to
 * add a variable to lambdaSet, you can see if it belongs by calling
 * belongsInLambdaSet. Once the lambdaSet has been completely filled with all
 * variables that have lambda productions and all variables that have
 * productions that can reduce to lambda, we move to step 3--constructing the
 * new grammar. You can perform this step immediately by calling
 * getLamdaProductionlessGrammar, or you can build it step by step by calling
 * getProductionsToAddForProduction for each production in the original grammar
 * and adding all returned productions to the new grammar.
 * 
 * @author Ryan Cavalcante
 */

public class LambdaProductionRemover {
	/**
	 * Creates instance of <CODE>LambdaProductionRemover</CODE>.
	 */
	public LambdaProductionRemover() {

	}

	/**
	 * Returns an empty hash set
	 */
	public HashSet getNewLambdaSet() {
		return new HashSet();
	}

	/**
	 * Adds <CODE>variable</CODE> to <CODE>lambdaSet</CODE>.
	 * 
	 * @param variable
	 *            the string to add to set
	 * @param lambdaSet
	 *            the set to add to
	 */
	public void addVariableToLambdaSet(String variable, Set lambdaSet) {
		if (!lambdaSet.contains(variable))
			lambdaSet.add(variable);
	}

	/**
	 * Returns true if there is a production in <CODE>grammar</CODE> with
	 * <CODE>variable</CODE> on the left hand side and lambda on the right
	 * hand side.
	 * 
	 * @param variable
	 *            the variable
	 * @param grammar
	 *            the grammar.
	 * @return true if there is a production in <CODE>grammar</CODE> with
	 *         <CODE>variable</CODE> on the left hand side and lambda on the
	 *         right hand side.
	 */
	public boolean isVariableWithLambdaProduction(String variable,
			Grammar grammar) {
		ProductionChecker pc = new ProductionChecker();
		GrammarChecker gc = new GrammarChecker();
		Production[] productions = GrammarChecker.getProductionsOnVariable(
				variable, grammar);
		for (int k = 0; k < productions.length; k++) {
			if (ProductionChecker.isLambdaProduction(productions[k]))
				return true;
		}
		return false;
	}

	/**
	 * Returns true if there are more variables in <CODE>grammar</CODE> that
	 * are not already in <CODE>lambdaSet</CODE> but belong there because they
	 * have lambda productions (e.g. A->lambda)
	 * 
	 * @param grammar
	 *            the grammar
	 * @param lambdaSet
	 *            the set of variables in grammar that are on the left hand side
	 *            of productions that go to lambda.
	 * @return true if there are more variables in <CODE>grammar</CODE> that
	 *         are not already in <CODE>lambdaSet</CODE> but belong there
	 *         because they have lambda productions (e.g. A->lambda)
	 */
	public boolean areMoreVariablesWithLambdaProductions(Grammar grammar,
			Set lambdaSet) {
		if (getNewVariableWithLambdaProduction(grammar, lambdaSet) == null) {
			return false;
		}
		return true;
	}

	/**
	 * Returns a new variable (i.e. one that is not already in <CODE>lambdaSet</CODE>
	 * from <CODE>grammar</CODE> that has a lambda production (i.e. a
	 * production with the variable on the left hand side and lambda on the
	 * right hand side.)
	 * 
	 * @param grammar
	 *            the grammar
	 * @param lambdaSet
	 *            the set of variables that have lambda productions
	 * @return a new variable (i.e. one that is not already in <CODE>lambdaSet</CODE>
	 *         from <CODE>grammar</CODE> that has a lambda production (i.e. a
	 *         production with the variable on the left hand side and lambda on
	 *         the right hand side.)
	 */
	public String getNewVariableWithLambdaProduction(Grammar grammar,
			Set lambdaSet) {
		String[] variables = grammar.getVariables();
		for (int k = 0; k < variables.length; k++) {
			if (!lambdaSet.contains(variables[k])
					&& isVariableWithLambdaProduction(variables[k], grammar)) {
				return variables[k];
			}
		}
		return null;
	}

	/**
	 * Adds all variables in <CODE>grammar</CODE> that have lambda transitions
	 * to <CODE>lambdaSet</CODE>.
	 * 
	 * @param grammar
	 *            the grammar
	 * @param lambdaSet
	 *            the set of variables that have lambda transitions (upon
	 *            returning from this method it will contain all variables in
	 *            grammar that have lambda transitions).
	 */
	public void addVariablesWithLambdaProductions(Grammar grammar, Set lambdaSet) {
		while (areMoreVariablesWithLambdaProductions(grammar, lambdaSet)) {
			String variable = getNewVariableWithLambdaProduction(grammar,
					lambdaSet);
			addVariableToLambdaSet(variable, lambdaSet);
		}
	}

	/**
	 * Returns true if <CODE>variable</CODE> is in <CODE>lambdaSet</CODE>.
	 * 
	 * @param variable
	 *            the variable
	 * @param lambdaSet
	 *            the set
	 * @return true if <CODE>variable</CODE> is in <CODE>lambdaSet</CODE>.
	 */
	public boolean isInLambdaSet(String variable, Set lambdaSet) {
		return lambdaSet.contains(variable);
	}

	/**
	 * Returns true if <CODE>production</CODE> can be reduced to a lambda
	 * production (i.e. it is a production that goes either directly to lambda,
	 * or to a series of variables that all have lambda productions, or that
	 * themselves could be reducable to lambda productions).
	 * 
	 * @param production
	 *            the production
	 * @param lambdaSet
	 *            the set of all variables that have lambda productions and
	 *            variables that have productions that are reducable to lambda
	 *            productions.
	 * @return true if <CODE>production</CODE> can be reduced to a lambda
	 *         production (i.e. it is a production that goes either directly to
	 *         lambda, or to a series of variables that all have lambda
	 *         productions, or that themselves could be reducable to lambda
	 *         productions).
	 */
	public boolean isReducableToLambdaProduction(Production production,
			Set lambdaSet) {
		ProductionChecker pc = new ProductionChecker();
		if (ProductionChecker.areTerminalsOnRHS(production))
			return false;
		String[] variables = production.getVariablesOnRHS();
		for (int j = 0; j < variables.length; j++) {
			if (!isInLambdaSet(variables[j], lambdaSet))
				return false;
		}
		return true;
	}

	/**
	 * Returns true if either <CODE>variable</CODE> in <CODE>grammar</CODE>
	 * has a lambda production or a production that is reducable to lambda.
	 * 
	 * @param variable
	 *            the variable
	 * @param grammar
	 *            the grammar
	 * @param lambdaSet
	 *            the set of all variables that either have lambda productions
	 *            or productions that are reducable to lambda.
	 * @return true if either <CODE>variable</CODE> in <CODE>grammar</CODE>
	 *         has a lambda production or a production that is reducable to
	 *         lambda.
	 */
	public boolean belongsInLambdaSet(String variable, Grammar grammar,
			Set lambdaSet) {
		if (isVariableWithLambdaProduction(variable, grammar))
			return true;
		GrammarChecker gc = new GrammarChecker();
		Production[] productions = GrammarChecker.getProductionsOnVariable(
				variable, grammar);
		for (int k = 0; k < productions.length; k++) {
			if (isReducableToLambdaProduction(productions[k], lambdaSet)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns true if there are more variables in <CODE>grammar</CODE> that
	 * belong in the lambda set. This includes variables that have lambda
	 * productions and variables that have productions that are reducable to
	 * lambda.
	 * 
	 * @param grammar
	 *            the grammar
	 * @param lambdaSet
	 *            the set of all variables that either have lambda productions
	 *            or productions that are reducable to lambda.
	 * @return true if there are more variables in <CODE>grammar</CODE> that
	 *         belong in the lambda set. This includes variables that have
	 *         lambda productions and variables that have productions that are
	 *         reducable to lambda.
	 */
	public boolean areMoreVariablesToAddToLambdaSet(Grammar grammar,
			Set lambdaSet) {
		if (getNewVariableThatBelongsInLambdaSet(grammar, lambdaSet) == null)
			return false;
		return true;
	}

	/**
	 * Returns a variable that is not already in <CODE>lambdaSet</CODE> but
	 * belongs there (i.e. a variable that has either a lambda production or a
	 * production that is reducable to lambda).
	 * 
	 * @param grammar
	 *            the grammar.
	 * @param lambdaSet
	 *            the set of all variables that either have lambda productions
	 *            or productions that are reducable to lambda.
	 * @return a variable that is not already in <CODE>lambdaSet</CODE> but
	 *         belongs there (i.e. a variable that has either a lambda
	 *         production or a production that is reducable to lambda).
	 */
	public String getNewVariableThatBelongsInLambdaSet(Grammar grammar,
			Set lambdaSet) {
		String[] variables = grammar.getVariables();
		for (int k = 0; k < variables.length; k++) {
			if (!isInLambdaSet(variables[k], lambdaSet)
					&& belongsInLambdaSet(variables[k], grammar, lambdaSet))
				return variables[k];
		}
		return null;
	}

	/**
	 * Returns the set of all variables in <CODE>grammar</CODE> that either
	 * have lambda productions or productions that are reducable to lambda.
	 * 
	 * @param grammar
	 *            the grammar
	 * @return the set of all variables in <CODE>grammar</CODE> that either
	 *         have lambda productions or productions that are reducable to
	 *         lambda.
	 */
	public HashSet getCompleteLambdaSet(Grammar grammar) {
		HashSet lambdaSet = getNewLambdaSet();
		while (areMoreVariablesToAddToLambdaSet(grammar, lambdaSet)) {
			String variable = getNewVariableThatBelongsInLambdaSet(grammar,
					lambdaSet);
			addVariableToLambdaSet(variable, lambdaSet);
		}
		return lambdaSet;
	}

	/**
	 * Returns a list of productions to add to a new grammar to replace <CODE>production</CODE>.
	 * The returned list of productions are all permutations of <CODE>production</CODE>.
	 * Each variable on the right hand side of <CODE>production</CODE> that is
	 * in <CODE>lambdaSet</CODE> can be replaced by lambda. So, in order to
	 * remove the lambda productions, we need to account for all permutations of
	 * production where different variables go to lambda. (e.g. if the
	 * production is S->AB and both A and B are in lambdaSet, then this would
	 * return S->AB, S->A, and S->B).
	 * 
	 * @param production
	 *            the production to replace
	 * @param lambdaSet
	 *            the set of all variables that either have lambda productions
	 *            or productions that are reducable to lambda.
	 * @return a list of productions to add to a new grammar to replace <CODE>production</CODE>.
	 *         The returned list of productions are all permutations of <CODE>production</CODE>.
	 */
	public Production[] getProductionsToAddForProduction(Production production,
			Set lambdaSet) {
		// Stupid...
		/*
		 * ProductionChecker pc = new ProductionChecker(); String[] variables =
		 * production.getVariablesOnRHS(); ArrayList list = new ArrayList();
		 * for(int k = 0; k < variables.length; k++) {
		 * if(isInLambdaSet(variables[k],lambdaSet)) list.add(variables[k]); }
		 * String[] lambdaVar = (String[]) list.toArray(new String[0]); String[]
		 * combs = getCombinations(lambdaVar); ArrayList productions = new
		 * ArrayList(); for(int k = 0; k < combs.length; k++) { Production p =
		 * getProductionForCombination(production,combs[k]);
		 * if(!pc.isLambdaProduction(p)) productions.add(p); } return
		 * (Production[]) productions.toArray(new Production[0]);
		 */

		String[] start = new String[] { "" };
		String rhs = production.getRHS();
		for (int i = 0; i < rhs.length(); i++) {
			String v = rhs.substring(i, i + 1);
			if (lambdaSet.contains(v)) {
				String s[] = new String[start.length * 2];
				for (int j = 0; j < start.length; j++) {
					s[j] = start[j] + v;
					s[j + start.length] = start[j];
				}
				start = s;
			} else {
				for (int j = 0; j < start.length; j++)
					start[j] += v;
			}
		}
		Arrays.sort(start);
		ArrayList list = new ArrayList();
		String lhs = production.getLHS();
		for (int i = (start[0].length() == 0) ? 1 : 0; i < start.length; i++)
			list.add(new Production(lhs, start[i]));
		return (Production[]) list.toArray(new Production[0]);
	}

	/**
	 * Returns all productions created by replacing each production in <CODE>grammar</CODE>
	 * based on the variables in <CODE>lambdaSet</CODE>.
	 * 
	 * @param grammar
	 *            the grammar
	 * @param lambdaSet
	 *            the set of all variables that either have lambda productions
	 *            or productions that are reducable to lambda.
	 * @return all productions created by replacing each production in <CODE>grammar</CODE>
	 *         based on the variables in <CODE>lambdaSet</CODE>.
	 */
	public Production[] getProductionsToAddToGrammar(Grammar grammar,
			Set lambdaSet) {
		ArrayList list = new ArrayList();
		Production[] productions = grammar.getProductions();
		for (int k = 0; k < productions.length; k++) {
			Production[] prods = getProductionsToAddForProduction(
					productions[k], lambdaSet);
			for (int j = 0; j < prods.length; j++) {
				list.add(prods[j]);
			}
		}
		return (Production[]) list.toArray(new Production[0]);
	}

	/**
	 * Returns all non lambda productions in <CODE>grammar</CODE>.
	 * 
	 * @param grammar
	 *            the grammar
	 * @return all non lambda productions in <CODE>grammar</CODE>.
	 */
	public Production[] getNonLambdaProductions(Grammar grammar) {
		ProductionChecker pc = new ProductionChecker();
		ArrayList list = new ArrayList();
		Production[] productions = grammar.getProductions();
		for (int k = 0; k < productions.length; k++) {
			if (!ProductionChecker.isLambdaProduction(productions[k]))
				list.add(productions[k]);
		}
		return (Production[]) list.toArray(new Production[0]);
	}

	/**
	 * Returns a grammar equivalent to <CODE>grammar</CODE> that has no lambda
	 * productions.
	 * 
	 * @param grammar
	 *            the grammar
	 * @param lambdaSet
	 *            the set of all variables that either have lambda productions
	 *            or productions that are reducable to lambda.
	 */
	public Grammar getLambdaProductionlessGrammar(Grammar grammar, Set lambdaSet) {
		Grammar g = new ContextFreeGrammar();
		g.addProductions(getProductionsToAddToGrammar(grammar, lambdaSet));
		return g;
	}

	/**
	 * Returns true if <CODE>ch</CODE> is a character in <CODE>comb</CODE>.
	 * 
	 * @param ch
	 *            the character
	 * @param comb
	 *            the string
	 * @return true if <CODE>ch</CODE> is a character in <CODE>comb</CODE>.
	 */
	private boolean isNotReplacedByLambda(char ch, String comb) {
		for (int k = 0; k < comb.length(); k++) {
			if (ch == comb.charAt(k))
				return true;
		}
		return false;
	}

	/**
	 * Returns the production that represents the combination <CODE>comb</CODE>.
	 * (e.g. if comb is the string "AB", that means that the variables "A" and
	 * "B" in <CODE>production</CODE> should not be replaced by lambda, but
	 * that all other variables on the right hand side of <CODE>production</CODE>
	 * should be. therefore, if production was S->aABC, this function would
	 * return S->aAB).
	 * 
	 * @param production
	 *            the production
	 * @param comb
	 *            the set of variables that should not be replaced by lambda.
	 * @return the production that represents the combination <CODE>comb</CODE>.
	 *         (e.g. if comb is the string "AB", that means that the variables
	 *         "A" and "B" in <CODE>production</CODE> should not be replaced
	 *         by lambda, but that all other variables on the right hand side of
	 *         <CODE>production</CODE> should be. therefore, if production was
	 *         S->aABC, this function would return S->aAB).
	 */
	private Production getProductionForCombination(Production production,
			String comb) {
		ProductionChecker pc = new ProductionChecker();
		String rhs = production.getRHS();
		StringBuffer buffer = new StringBuffer();
		for (int k = 0; k < rhs.length(); k++) {
			char ch = rhs.charAt(k);
			if (ProductionChecker.isTerminal(ch)
					|| isNotReplacedByLambda(ch, comb))
				buffer.append(ch);
		}
		Production p = new Production(production.getLHS(), buffer.toString());
		return p;
	}

	/**
	 * Returns a string that represents the set of variables from <CODE>variables</CODE>
	 * indicated by <CODE>binary</CODE>. (e.g. if variables was the set
	 * "A,B,C", in that order, and binary was 011, this function would return
	 * "BC" since there is a one at index 1 and 2).
	 * 
	 * @param binary
	 *            a binary number whose length is the same as the length of
	 *            <CODE>variables</CODE>
	 * @param variables
	 *            the set of variables that <CODE>binary</CODE> refers to.
	 * @return a string that represents the set of variables from <CODE>variables</CODE>
	 *         indicated by <CODE>binary</CODE>.
	 */
	private String getRepresentation(String binary, String[] variables) {
		StringBuffer buffer = new StringBuffer();
		for (int k = 0; k < binary.length(); k++) {
			char ch = binary.charAt(k);
			if (ch == ONE_CHAR) {
				buffer.append(variables[k]);
			}
		}
		return buffer.toString();
	}

	/**
	 * Returns a binary number equivalent to <CODE>binaryNum</CODE> padded
	 * with enough zeros to make it a string of length <CODE>length</CODE>.
	 * 
	 * @param binaryNum
	 *            the number to pad with zeros
	 * @param length
	 *            the length to make <CODE>binaryNum</CODE>
	 * @return a binary number equivalent to <CODE>binaryNum</CODE> padded
	 *         with enough zeros to make it a string of length <CODE>length</CODE>.
	 */
	private String pad(String binaryNum, int length) {
		int numZeros = length - binaryNum.length();
		for (int k = 0; k < numZeros; k++) {
			binaryNum = ZERO.concat(binaryNum);
		}
		return binaryNum;
	}

	/**
	 * Returns a list of binary numbers (as strings) that encode all possible
	 * permutations of <CODE>variables</CODE>
	 * 
	 * @param variables
	 *            the set of variables
	 * @return a list of binary numbers (as strings) that encode all possible
	 *         permutations of <CODE>variables</CODE>
	 */
	private String[] getCombinations(String[] variables) {
		ArrayList list = new ArrayList();
		for (int k = 0; k < ((int) Math.pow(2, variables.length)); k++) {
			String comb = pad(Integer.toBinaryString(k), variables.length);
			list.add(getRepresentation(comb, variables));
		}
		return (String[]) list.toArray(new String[0]);
	}

	/** the string for zero. */
	protected String ZERO = "0";

	/** the char '1' . */
	protected char ONE_CHAR = '1';

}
