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




package edu.duke.cs.jflap.automata.turing;

import edu.duke.cs.jflap.grammar.Production;

import java.util.ArrayList;
import java.util.*;

import edu.duke.cs.jflap.automata.State;
import edu.duke.cs.jflap.automata.Transition;

/**
 * Converter for turing to unrestricted grammar
 * NEEDS to make abstraction (super class for both TuringToGrammar and PDAtoCFGconverter)
 * 
 * @author Kyung Min (Jason) Lee
 *
 */
public class TuringToGrammarConverter {

	// Some String constants.
	private static final String SQUARE_SYMBOL=""+'\u25A1';
	private static final String SQUARE="=";
	private static final String VAR_START="V(";
	private static final String VAR_END=")";
	
	// Z
	private HashSet <String> myAllReadableString;
	
	// |- finite set of symbols in the tape alphabet
	private HashSet <String> myAllWritableString;
	
	/**
	 * Constructor for converting TM to Unrestrcited grammar
	 */
	public TuringToGrammarConverter()
	{
		myAllReadableString=new HashSet <String>();
		myAllWritableString=new HashSet <String>();
	}

	public Production[] createProductionsForInit(State state, Transition[] tm) {
		// TODO Auto-generated method stub
		int id=state.getID();
		ArrayList <Production> init=new ArrayList <Production>();

		// for now
		init.add(new Production("S", VAR_START+SQUARE+SQUARE+VAR_END+"S"));
		init.add(new Production("S", "S"+VAR_START+SQUARE+SQUARE+VAR_END));
		init.add(new Production("S", "T"));
		myAllReadableString.add(SQUARE);
		
		for (int i=0; i<tm.length; i++)
		{
			TMTransition trans=(TMTransition)tm[i];
			int tape=trans.getTapeLength();
			for (int j=0; j<tape; j++)
			{
				String str=trans.getRead(j);
				if (str.equals(SQUARE_SYMBOL))
					str=SQUARE;
				String write=trans.getWrite(j);
				if (write.equals(SQUARE_SYMBOL))
					write=SQUARE;
				myAllWritableString.add(write);
				if (!myAllReadableString.contains(str))
				{
					myAllReadableString.add(str);
					String var1=VAR_START+str+str+VAR_END;
					String var2=VAR_START+str+id+str+VAR_END;
					init.add(new Production("T", "T"+var1));
					init.add(new Production("T", var2));
				}
			}
		}
		init.add(new Production(SQUARE, null));
		
		Production[] answer=new Production[init.size()];
		for (int i=0; i<answer.length; i++)
		{
			answer[i]=init.get(i);
		}
		return answer;
	}

	/**
	 * For each transition apply Peter Linz's algorithm to generate new productions
	 * @param transition
	 * @param states
	 * @return
	 */
	public Production[] createProductionsForTransition(Transition transition, State[] states) {
		// TODO Auto-generated method stub
		ArrayList <Production> list=new ArrayList <Production>();
		TMTransition trans=(TMTransition) transition;
		HashMap <Integer, Boolean> finalStateMap=new HashMap<Integer, Boolean>();
		for (int i=0; i<states.length; i++)
		{
			finalStateMap.put(states[i].getID(), true);
		}
		//what is exactly tape??
		int fromState=trans.getFromState().getID();
		int toState=trans.getToState().getID();
		int tape=trans.getTapeLength();
		for (int i=0; i<tape; i++)
		{
			String direction=trans.getDirection(i);
			String read=trans.getRead(i);
			String write=trans.getWrite(i);
			if (read.equals(SQUARE_SYMBOL))
				read=SQUARE;
			if (write.equals(SQUARE_SYMBOL))
				write=SQUARE;
			
			for (String p : myAllReadableString)
			{
				for (String a : myAllReadableString)
				{
				
					for (String q : myAllWritableString)
					{
						if (direction.equals("R"))
						{
							String lhs_var1=VAR_START+a+fromState+read+VAR_END;
							String lhs_var2=VAR_START+p+q+VAR_END;
							String rhs_var1=VAR_START+a+write+VAR_END;
							String rhs_var2=VAR_START+p+toState+q+VAR_END;
					
							
							Production prod=new Production(lhs_var1+lhs_var2, rhs_var1+rhs_var2);
							list.add(prod);
						
							if (finalStateMap.containsKey(toState))
							{
								String lhs=VAR_START+p+toState+q+VAR_END;
								String rhs=p;
								list.add(new Production(lhs, rhs));
								String lhs2=VAR_START+a+q+VAR_END+p;
								list.add(new Production(lhs2, a+rhs));
								
								String lhs3=p+VAR_START+a+q+VAR_END;
								list.add(new Production(lhs3, p+a));
								
							}

						}
						if (direction.equals("L"))
						{
							String lhs_var1=VAR_START+p+q+VAR_END;
							String lhs_var2=VAR_START+a+fromState+read+VAR_END;
							
							String rhs_var1=VAR_START+p+toState+q+VAR_END;
							String rhs_var2=VAR_START+a+write+VAR_END;
							
							
							Production prod=new Production(lhs_var1+lhs_var2, rhs_var1+rhs_var2);
							
							list.add(prod);

							if (finalStateMap.containsKey(toState))
							{
								String lhs=VAR_START+p+toState+q+VAR_END;
								String rhs=p;
								String lhs2=p+VAR_START+a+q+VAR_END;
								list.add(new Production(lhs, rhs));
								list.add(new Production(lhs2, p+a));
								
								//TODO: Change this later
						
								String lhs3=VAR_START+a+q+VAR_END+p;
								list.add(new Production(lhs3, a+rhs));
							}
						}
						if (direction.equals("S"))
						{
							//what to do? : DO nothing standard TM only has left and right
						}
						
					}
				}
			}
		}
		
		
		Production[] answer=new Production[list.size()];
		for (int i=0; i<answer.length; i++)
		{
			answer[i]=list.get(i);
		}
		return answer;
	}

}
