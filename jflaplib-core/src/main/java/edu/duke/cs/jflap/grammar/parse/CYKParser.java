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

import edu.duke.cs.jflap.grammar.Grammar;
import edu.duke.cs.jflap.grammar.Production;

import java.util.*;

/**
 * CYK Parser 
 * It parses grammar that is in CNF form and returns whether the String is accepted by language or not.
 * 
 * @author Kyung Min (Jason) Lee
 *
 */

public class CYKParser {
	
	/** Production array that will contain all the productions of grammar */ 
	private Production[] myProductions;

	/** Start variable of the grammar */
	private static String START_VARIABLE;
	
	/** Length of the input String */
	private int myTargetLength;
	
	/** Productions that leads to the answer */
	private ArrayList <Production> myAnswerProductions;
	
	/** Map to store the result of subparts */
	private HashMap <String, HashSet<String>> myMap;
	
	/** Input string that CYK is trying to parse */
	private String myTarget;
	
	private OrderCorrectly myOrderComparator;
	/**
	 * Constructor for CYK Parser
	 * @param grammar Grammar that is going to be used in CYK Parsing (It has to be in CNF Form)
	 */
	public CYKParser(Grammar grammar)
	{
		myProductions=grammar.getProductions();
		START_VARIABLE=grammar.getStartVariable();
	//	System.out.println("GRAMMAR = "+Arrays.asList(grammar.getProductions()));
	}
	
	/**
	 * Check whether the grammar accepts the string or not 
	 * using DP
	 */
	public boolean solve(String target)
	{
		myMap=new HashMap <String, HashSet<String>>();
		int targetLength=target.length();
		myTargetLength=targetLength;
		myTarget=target;
		
		if (target.equals(""))
			return false;
		
		for (int i=0; i<targetLength; i++)
		{
			String a=target.substring(i,i+1);
			HashSet <String> temp=new HashSet <String>();
			int count=0;

			for (int j=0; j<myProductions.length; j++)
			{
				if (myProductions[j].getRHS().equals(a))
				{
					count++;
					temp.add(myProductions[j].getLHS());
				}
			}
			String key=i+","+i;
			myMap.put(key, temp);
			if (count==0)
			{
				return false;
			}
			count=0;
				
		}
		//System.out.println(myMap);
		
		int increment=1;
		for (int i=0; i<targetLength; i++)
		{
			for (int j=0; j<targetLength; j++)
			{
				if (targetLength<=j+increment)
					break;
				int k=j+increment;
				checkProductions(j,k);
				
			//	System.out.print(myMap.get(j+","+k));
			}
			//System.out.println();
			increment++;
		}
		
		if (increment==2)
		{
			return myMap.get("0,"+(targetLength-1)).contains(START_VARIABLE);
		}

		if (myMap.get("0,"+(targetLength-1)).contains(START_VARIABLE))
			return true;
		else
			return false;
	
	}
	
	/**
	 * Helper method of solve method that checks the surrounding production
	 * @param x
	 * @param y
	 */
	private void checkProductions(int x,int y)
	{
		HashSet <String> tempSet=new HashSet <String>();
		
		for (int i=0; i<myProductions.length; i++)
		{
			for (int k=x; k<y; k++)
			{
				String key1=x+","+k;
				String key2=(k+1)+","+y;
				for (String A : myMap.get(key1))
				{
					for (String B : myMap.get(key2))
					{
						String target=A+B;
						if (myProductions[i].getRHS().equals(target))
						{
							HashSet <String> temp2Set=new HashSet <String>();
							tempSet.add(myProductions[i].getLHS());
							temp2Set.add("0"+A+"/"+key1);
							temp2Set.add("1"+B+"/"+key2);

							String tempKey=x+","+y+myProductions[i].getLHS();
							if (myMap.get(tempKey)!=null)
								temp2Set.addAll(myMap.get(tempKey));

							myMap.put(tempKey, temp2Set);
						}
					}
				}
			}
		}
		String key=x+","+y;
		myMap.put(key, tempSet);
	}
	
	/**
	 * Method for getting the trace of how the parser achieved the target String
	 * @return ArrayList of Productions that was applied to attain target String
	 */
	public ArrayList <Production> getTrace()
	{
		myAnswerProductions=new ArrayList <Production>();
		myOrderComparator=new OrderCorrectly();
		
	//	System.out.println("WHOLE MAP = "+myMap);
		
		getMoreProductions(START_VARIABLE, "0,"+(myTargetLength-1));
		
	//	System.out.println(myAnswerProductions);
		
		return myAnswerProductions;
	}
	
	/**
	 * Helper method of getTrace method which recursively backtracks how Parser achieved the target String
	 * @param variable Variable that we are chekcing
	 * @param location Location of the variable
	 */
	private void getMoreProductions(String variable, String location)
	{
	//	//System.out.println("WHOLE MAP = "+myMap);
		if (myMap.get(location+variable)==null)
		{
		//	//System.out.println("Location inside = "+location);
		//	//System.out.println("Variable inside = "+variable);
			int loc=Integer.parseInt(location.substring(0,location.indexOf(",")));
			myAnswerProductions.add(new Production(variable, myTarget.substring(loc,loc+1)));
			return;
			
		}
		
	/*	//System.out.println("Map = "+myMap.get(location+variable));
		//System.out.println("Location = "+location);
		//System.out.println("Variable = "+variable);*/
		
		ArrayList <String> optionsA=new ArrayList <String>();
		ArrayList <String> optionsB=new ArrayList <String>();
		
		String[] A=new String[2];
		String[] B=new String[2];
		for (String var : myMap.get(location+variable))
		{
			if (var.startsWith("0"))
				optionsA.add(var);
			else
				optionsB.add(var);
		}
		Collections.sort(optionsA, myOrderComparator);
		Collections.sort(optionsB, myOrderComparator);
		
	//	//System.out.println("AAA = "+optionsA);
	//	//System.out.println("BBB = "+optionsB);
		
	
		
		boolean isDone=false;
		for (int i=0; i<optionsA.size(); i++)
		{
			int index=optionsA.get(i).indexOf("/");
			String a=optionsA.get(i).substring(1, index);
			String locA=optionsA.get(i).substring(index+1);
			
			for (int j=0; j<optionsB.size(); j++)
			{
				index=optionsB.get(i).indexOf("/");
				String b=optionsB.get(i).substring(1, index);
				String locB=optionsB.get(i).substring(index+1);
				
				Production p=new Production(variable, a+b);
				for (int k=0; k<myProductions.length; k++)
				{
					if (myProductions[k].getLHS().equals(p.getLHS()) &&
							myProductions[k].getRHS().equals(p.getRHS()))
					{
						A[0]=a;
						A[1]=locA;
						B[0]=b;
						B[1]=locB;
						isDone=true;
						break;
					}
				}
				if (isDone)
					break;
			}
			if (isDone)
				break;
		}
		
	//	//System.out.println("Selected = "+A[0]+" at "+A[1]);
	//	//System.out.println("Selected = "+B[0]+" at "+B[1]);
		
		myAnswerProductions.add(new Production(variable, A[0]+B[0]));
		getMoreProductions(A[0],A[1]);
		getMoreProductions(B[0],B[1]);
		
	}
}

final class OrderCorrectly implements Comparator
{
	public int compare(Object o1, Object o2) {
		String str1=(String) o1;
		String str2=(String) o2;
		int index1=str1.indexOf("/");
		String loc1=str1.substring(index1+1);
		int index1_1=loc1.indexOf(",");
		int lc1_1=Integer.parseInt(loc1.substring(0,index1_1));
		int lc1_2=Integer.parseInt(loc1.substring(index1_1+1));
	
		int index2=str2.indexOf("/");
		String loc2=str2.substring(index2+1);
		int index2_1=loc2.indexOf(",");
		int lc2_1=Integer.parseInt(loc2.substring(0,index2_1));
		int lc2_2=Integer.parseInt(loc2.substring(index2_1+1));
		
		if (lc1_1==lc2_1)
		{
			return lc1_2-lc2_2;
		}
		
		return lc1_1-lc2_1;
	}
	
}