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




package edu.duke.cs.jflap.grammar.parse;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Set;

import edu.duke.cs.jflap.grammar.Grammar;
import edu.duke.cs.jflap.grammar.Production;

/**
 * Similar to BruteParser abstract class, UserParser abstract class is created to deal with User Parsing
 * 
 * NOTE: This code is very similar to BruteParser and it would be better to combine two classes and extract hiearchy.
 *       However, since Brute Parser is fully functional, I did not want to mess wtih BruteParser class.
 *       
 * @author Kyung Min (Jason) Lee
 *
 */
public abstract class UserParser 
{
	/** Stuff for the possibilities. **/
	private static final Production[] P = new Production[0];
	private static final int[] S = new int[0];

	/** Starting ParseNode **/
	private static final ParseNode E = new ParseNode("", P, S);

	/** This is the grammar. */
	protected Grammar myGrammar;

	/** The array of productions. */
	protected Production[] myProductions;

	/** This is the target string. */
	protected String myTarget;

	/** This should be set to done when the operation has completed. */
	private boolean isDone = false;

	/** The "answer" to the parse question. */
	private ParseNode myAnswer;

	/**
	 * The "smaller" set, those symbols that may possibly reduce to nothing.
	 */
	protected Set mySmallerSet;

	/** The Production rule that the User has set to apply to the String **/
	private Production myCurrentProduction;
	
	/** Integer variable that shows how many times the derivation has occured **/
	private int myCount=0;
	
	/** This holds the list of nodes for the BFS. */
	private LinkedList myQueue = new LinkedList();
	
	/**
	 * Constructor for UserParser abstract class.
	 * This is intialized by sub-classes of UserParser class.
	 * 
	 * @param grammar The grammar that is going to be used for parsing
	 * @param target The target string that user is trying to derive
	 */
	public UserParser(Grammar grammar, String target) {
		initialize(grammar, target);
	}
	
	/**
	 * Intialize all the variables before starting Parser.
	 * This method is called from the constructor.
	 * 
	 * @param grammar The grammar that is going to be used for parsing
	 * @param target The target string that user is trying to derive
	 */
	private void initialize(Grammar grammar, String target)
	{
		for (int i = 0; i < target.length(); i++)
			if (!grammar.isTerminal(target.substring(i, i + 1)))
				throw new IllegalArgumentException(
						"String to parse has nonterminal "
								+ target.substring(i, i + 1) + ".");

		if (grammar == null)
			return;
		myQueue.clear();
		myAnswer=new ParseNode(grammar.getStartVariable(), P, S);
		myQueue.add(myAnswer);
		mySmallerSet = Collections.unmodifiableSet(Unrestricted
				.smallerSymbols(grammar));
		myGrammar = grammar;
		myProductions = grammar.getProductions();
		myTarget = target;
	}

	/**
	 * This factory method will return a user parser appropriate for the
	 * grammar.
	 * 
	 * @param grammar
	 *            the grammar to get a brute force parser for
	 * @param target
	 *            the target string
	 */
	public static UserParser get(Grammar grammar, String target) {
		if (Unrestricted.isUnrestricted(grammar))
		{
			return new UnrestrictedUserParser(grammar, target);
			
		}
		return new RestrictedUserParser(grammar, target);
	}
	
	/**
	 * Given an index of Productino rule array,
	 * this method finds LHS variable of chosen production rule and 
	 * count how many of LHS variable is present in our current String. 
	 * 
	 * @param index Index of our production rule that is selected by user.
	 * @return Return the count of LHS variables present in the String.
	 */
	public int checkValidAndParse(int index)
	{
	/*	for (int i=0; i<myProductions.length; i++)
		{
			System.out.println(" index : "+i+ "  production = "+myProductions[i]);
		}*/
		
		myCurrentProduction=myProductions[index];
		int length=myCurrentProduction.getLHS().length();
		int count=0;
		for (int i=0; i<myAnswer.getDerivation().length(); i++)
		{
			if (i+length>myAnswer.getDerivation().length())
				break;
			if (myAnswer.getDerivation().substring(i,i+length).equals(myCurrentProduction.getLHS()))
			{
				count++;
			}
		}
		return count;
	}

	
	/**
	 * Returns a Next possible one step parse for a given string. 
	 * The first entry is always the identity.
	 * 
	 * @param c the current String
	 * @param index the index of String where we are going to apply the production rule
	 */
	private ParseNode getNextResult(String c, int index) {
		if (c.length() == 0) {
			return E;
		}
		
		// Find the start of the production.
		int start=0;
		
		if (index<0)
			index=c.indexOf(myCurrentProduction.getLHS());
		//System.out.println("MY Current Production = "+myCurrentProduction);
		
		//System.out.println("MY RHS = "+myCurrentProduction.getRHS());
		
		start=index;
		String prepend = c.substring(0, start) + myCurrentProduction.getRHS()+c.substring(start+myCurrentProduction.getLHS().length());
		Production[] singleProductionArray=new Production[1];
		singleProductionArray[0]=myCurrentProduction;
		int[] singleSubstitutionArray=new int[1];
		singleSubstitutionArray[0]=start;
		return new ParseNode(prepend, singleProductionArray, singleSubstitutionArray);
	}

	/**
	 * Checks whether it is possible derivation or not given a String
	 * @param derivation String that is going to be checked
	 * @return True for possible derivation and false for impossible derivation.
	 */
	public boolean isPossibleDerivation(String derivation) {
		return Unrestricted.minimumLength(derivation, mySmallerSet) <= myTarget
				.length();
	}
	
	/**
	 * The parsing method.
	 */
	public synchronized void parse(int index) {
		if (myCount==0) {
			myCount++;
			return;
		}
		
		ParseNode node=(ParseNode) myQueue.removeFirst();
		ParseNode pNode=getNextResult(myAnswer.getDerivation(), index);
		pNode = new ParseNode(pNode);

		node.add(pNode);
		myQueue.add(pNode);
		myAnswer=pNode;
		if (pNode.getDerivation().equals(myTarget)) {
			isDone = true;
			return;
		}
		isDone=false;
	}
	
	
	/**
	 * This will start the parsing. This method will return immediately. The
	 * parsing is done in a separate thread since the potential for the parsing
	 * to take forever on some brute force parses exists.
	 * 
	 * @return if the starting of the parsing was successful, which will not be
	 *         successful if the parsing is already underway, or if the parser
	 *         is finished
	 */
	public synchronized boolean start() {
		if (isFinished())
			return false;
		parse(-1);
		return true;
	}

	/**
	 * Returns if the parser has finished, with success or otherwise.
	 * 
	 * @return <CODE>true</CODE> if the
	 */
	public synchronized boolean isFinished() {
		return isDone;
	}

	/**
	 * This returns the answer node for the parser.
	 * 
	 * @return the answer node for the parse, or <CODE>null</CODE> if there
	 *         was no answer, or one has not been discovered yet
	 */
	public synchronized ParseNode getAnswer() {
		return myAnswer;
	}
	
	/**
	 * This method retrieves the previous step performed by the user
	 * @return
	 */
	public synchronized ParseNode getPreviousAnswer()
	{
		myAnswer=(ParseNode) myAnswer.getParent();
		myQueue.clear();
		myQueue.add(myAnswer);
		return myAnswer;
	}

	/**
	 * Given selectedRow index, this method returns the LHS variable of the selected prodcution
	 * @param selectedRow Row that user selected
	 * @return LHS variable of selected production.
	 */
	public String getLHSForProduction(int selectedRow) {
		// TODO Auto-generated method stub
		return myProductions[selectedRow].getLHS();
	}

	/**
	 * This method is similar to parse method.
	 * However, this method is called whenever more than two variables are going to be applied with same production
	 * at same time 
	 * @param tempIndices Indices of where the substitution will occur
	 */
	public void subsitute(int[] tempIndices) {
		ParseNode node=(ParseNode) myQueue.removeFirst();
		ParseNode pNode=getNextSubstitution(myAnswer.getDerivation(), tempIndices);

		pNode = new ParseNode(pNode);
	
		node.add(pNode);
		myQueue.add(pNode);
		myAnswer=pNode;
			
		if (pNode.getDerivation().equals(myTarget)) {
			isDone = true;
			return;
		}
		isDone=false;
	}

	/**
	 * This method is called when one production is applied to multiple variable.
	 * It is similar to getNextResult method.
	 * However, unlike getNextResult method, this one creates multiple production and substituion array.
	 * 
	 * @param c Current String
	 * @param tempIndices Indices of where the substitution will occur
	 * @return The next parseNode derived from this production
	 */
	private ParseNode getNextSubstitution(String c, int[] tempIndices) {
		// Find the start of the production.
		int[] multipleSubstitutionArray=tempIndices;
		
		int start=0;
		String prepend="";
		Production[] multipleProductionArray=new Production[multipleSubstitutionArray.length];
		multipleProductionArray[0]=myCurrentProduction;
		for (int i=0; i<multipleSubstitutionArray.length; i++)
		{
			if (i==0)
				start=multipleSubstitutionArray[i];
			else
				start=multipleSubstitutionArray[i]+i*(myCurrentProduction.getRHS().length()-1);
			prepend = c.substring(0, start) + myCurrentProduction.getRHS()+c.substring(start+myCurrentProduction.getLHS().length());
			c=prepend;
			multipleProductionArray[i]=myCurrentProduction;
		}

		return new ParseNode(prepend, multipleProductionArray, multipleSubstitutionArray);
	}

	/**
	 * This method whether the user have reached the final step regardless of the acceptance of the String.
	 * In other words, if there is no more variable to apply the production rule, 
	 * this method returns false
	 * @param finalString current String that user have derived
	 * @return True is more production is possible, false is there are no variables left.
	 */
	public boolean isStringTerminal(String finalString) {
		for (int i=0; i<myProductions.length; i++)
		{
			if (finalString.contains(myProductions[i].getLHS()))
				return false;
		}
		return true;
	}
}
