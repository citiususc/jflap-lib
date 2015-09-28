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

import edu.duke.cs.jflap.grammar.CNFConverter;
import edu.duke.cs.jflap.grammar.Grammar;
import edu.duke.cs.jflap.grammar.LambdaProductionRemover;
import edu.duke.cs.jflap.grammar.Production;
import edu.duke.cs.jflap.grammar.UnitProductionRemover;
import edu.duke.cs.jflap.grammar.UselessProductionRemover;
import edu.duke.cs.jflap.gui.environment.GrammarEnvironment;
import edu.duke.cs.jflap.gui.grammar.GrammarInputPane;
import edu.duke.cs.jflap.gui.grammar.transform.ChomskyPane;
import edu.duke.cs.jflap.gui.grammar.transform.LambdaController;
import edu.duke.cs.jflap.gui.grammar.transform.LambdaPane;
import edu.duke.cs.jflap.gui.grammar.transform.UnitController;
import edu.duke.cs.jflap.gui.grammar.transform.UnitPane;
import edu.duke.cs.jflap.gui.grammar.transform.UselessController;
import edu.duke.cs.jflap.gui.grammar.transform.UselessPane;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.*;

/**
 * Class for converting CNF-converted productions back to their original productions
 * @author Kyung Min (Jason) Lee
 *
 */

public class CYKTracer {
	
	private Grammar myOriginalGrammar; 
	private ArrayList <Production> myTrace;
	private ArrayList <Production> myAnswer;
	private Production[] myOriginalProductions;
	private HashMap <ArrayList <Production>, Production> myLambdaStepMap;
	private HashMap <ArrayList <Production>, Production> myUnitStepMap;
	private ArrayList <Production> myTempCNF;
	private HashMap <Production, ArrayList <Production>> myCNFMap;
	
	public CYKTracer(Grammar grammar, ArrayList<Production> trace) {
		myOriginalGrammar=grammar;
		myTrace=trace;
		myAnswer=new ArrayList <Production>();
		myOriginalProductions=myOriginalGrammar.getProductions();
		initializeLambdaStepMap();
	}
	
	private void initializeLambdaStepMap()
	{
		LambdaProductionRemover remover = new LambdaProductionRemover();
		Set lambdaDerivers = remover.getCompleteLambdaSet(myOriginalGrammar);
		Grammar g=myOriginalGrammar;
	    //System.out.println("LD = "+lambdaDerivers);
		if (lambdaDerivers.size() > 0) {
			
			myLambdaStepMap=new HashMap<ArrayList <Production>, Production>();
			HashMap <String, Production> directLambdaProductions=new HashMap <String, Production>();
			HashMap <String, ArrayList <Production>> indirectLambdaProductions=new HashMap <String, ArrayList<Production>>();
			
			GrammarEnvironment env=new GrammarEnvironment(new GrammarInputPane(myOriginalGrammar));
			LambdaPane lp = new LambdaPane(env, myOriginalGrammar);
	    	LambdaController controller = new LambdaController(lp, myOriginalGrammar);
	    
	    	controller.doStep();
	    	
	    	for (Production production : (HashSet<Production>)controller.getLambdaSet())
	    	{
	    		directLambdaProductions.put(production.getLHS(), production);
	    	}
	    	
	    //	//System.out.println("DIRECT = "+directLambdaProductions);
	    	Production[] p=lp.getGrammar().getProductions();

	    	for (String key : directLambdaProductions.keySet())
	    	{
	    		for (int i=0; i<p.length; i++)
		    	{
	    			if (p[i].getRHS().equals(key))
		    		{
	    				ArrayList <Production> temp=new ArrayList <Production>();
	    		    	temp.add(p[i]);
	    		    	temp.add(directLambdaProductions.get(key));
		    			indirectLambdaProductions.put(p[i].getLHS(), temp);
		    		}
				}	
	    	}
	    //	//System.out.println("INDIRECT = "+indirectLambdaProductions);
	    	
	    	for (int i=0; i<p.length; i++)
	    	{
	    		Production[] p2 = remover.getProductionsToAddForProduction(
					p[i], lambdaDerivers);
	    	//	//System.out.println("Expanding From : "+p[i]);
	    		for (int j=0; j<p2.length; j++)
	    		{
	    			ArrayList <Production> temp=new ArrayList <Production>();
	    	    	
	    			if (!p2[j].equals(p[i]))
	    			{
	    				temp.add(p[i]);
	    				ArrayList <String> variables=getDifferentVariable(p[i].getRHS(), p2[j].getRHS());
	    		//		//System.out.println(p2[j]+"   Variables = "+variables);
	    				for (int pp=0; pp<variables.size(); pp++)
	    				{
	    					if (directLambdaProductions.keySet().contains(variables.get(pp)))
	    					{
	    						temp.add(directLambdaProductions.get(variables.get(pp)));
		    				}
	    					else
	    					{
	    						if (indirectLambdaProductions.keySet().contains(variables.get(pp)))
	    		    				temp.addAll(indirectLambdaProductions.get(variables.get(pp)));
	    						else
	    							reportError();
	    					}
	    				}
	    				myLambdaStepMap.put(temp, p2[j]);
	    			}
	    	//		//System.out.println(temp);
	    		}
	   // 		//System.out.println();
	    	}
	    	controller.doAll();
	    	g=controller.getGrammar();
	    }
	//	System.out.println("LAMBDA step Map = "+myLambdaStepMap);
		intializeUnitStepMap(g);
		
	}

	private void intializeUnitStepMap(Grammar g) {
		UnitProductionRemover remover = new UnitProductionRemover();
		if (remover.getUnitProductions(g).length > 0) {
			
			myUnitStepMap=new HashMap <ArrayList <Production>, Production>();
			
			GrammarEnvironment env=new GrammarEnvironment(new GrammarInputPane(g));
			UnitPane up = new UnitPane(env, g);
			UnitController controller=new UnitController(up, g);
			controller.doStep();
			Production[] units=remover.getUnitProductions(g);
			HashMap <String, Production> removedUnitProductions=new HashMap <String, Production>();
			
			for (int i=0; i<units.length; i++)
			{
				removedUnitProductions.put(units[i].getLHS(), units[i]);
			}
			//System.out.println("UNIT = "+removedUnitProductions);
			
			Grammar unitless=remover.getUnitProductionlessGrammar(controller.getGrammar(), remover.getVariableDependencyGraph(g));
			Production[] temp=unitless.getProductions();
			ArrayList <Production> productionsToAdd=new ArrayList <Production>();
			for (int i=0; i<temp.length; i++)
				productionsToAdd.add(temp[i]);
			// Now the grammar without unit productions
			g=controller.getGrammar();
			Production[] p=g.getProductions();
			for (int i=0; i<p.length; i++)
			{
				if (productionsToAdd.contains(p[i]))
					productionsToAdd.remove(p[i]);
			}
			//System.out.println(productionsToAdd);
			
			for (int i=0; i<productionsToAdd.size(); i++)
			{
				ArrayList <Production> tempToAdd=new ArrayList <Production>();
				String var1=productionsToAdd.get(i).getLHS();
				if (removedUnitProductions.get(var1)==null)
					reportError();
				else
				{
					tempToAdd.add(removedUnitProductions.get(var1));
					String var2=removedUnitProductions.get(var1).getRHS();
					boolean isDone=false;
					
					for (int pp=0; pp<p.length; pp++)
					{
						if (p[pp].getLHS().equals(var2))
						{
							String tempStr=p[pp].getRHS();
							if (tempStr.equals(productionsToAdd.get(i).getRHS()))
							{
								tempToAdd.add(p[pp]);
								isDone=true;
								break;
							}
						}
					}
					while (isDone==false && removedUnitProductions.keySet().contains(var2))
					{
						tempToAdd.add(removedUnitProductions.get(var2));
						var2=removedUnitProductions.get(var2).getRHS();
						for (int pp=0; pp<p.length; pp++)
						{
							if (p[pp].getLHS().equals(var2))
							{
								String tempStr=p[pp].getRHS();
								if (tempStr.equals(productionsToAdd.get(i).getRHS()))
								{
									tempToAdd.add(p[pp]);
									isDone=true;
									break;
								}
							}
						}
					}
				}
				myUnitStepMap.put(tempToAdd, productionsToAdd.get(i));
			}
			controller.doAll();
			g=controller.getGrammar();
		}
		//System.out.println("UNIT STEP MAP = "+myUnitStepMap);
		removeUseless(g);
	}

	private void removeUseless(Grammar g) 
	{
		UselessProductionRemover remover = new UselessProductionRemover();
		
		Grammar g2 = UselessProductionRemover
				.getUselessProductionlessGrammar(g);
		
		Production[] p1 = g.getProductions();
		Production[] p2 = g2.getProductions();
		if (p1.length > p2.length) {
			
			GrammarEnvironment env=new GrammarEnvironment(new GrammarInputPane(g));
			UselessPane up = new UselessPane(env, g);
			UselessController controller=new UselessController(up, g);
			controller.doAll();
			g=controller.getGrammar();
		}
		initializeChomskyMap(g);
	}

	private void initializeChomskyMap(Grammar g) {
	//	//System.out.println("Chomsky = "+g);
		CNFConverter converter = new CNFConverter(g);
		
		Production[] p = g.getProductions();
		boolean chomsky = true;
		for (int i = 0; i < p.length; i++)
			chomsky &= converter.isChomsky(p[i]);
		
		if (!chomsky) {
			
			myCNFMap=new HashMap <Production, ArrayList <Production>>();
			GrammarEnvironment env=new GrammarEnvironment(new GrammarInputPane(g));
			ChomskyPane cp = new ChomskyPane(env, g);
			ArrayList <Production> resultList=new ArrayList <Production>();
			cp.doAll();
			for (int i=0; i<p.length; i++)
			{
				myTempCNF=new ArrayList <Production>();
				CNFConverter cv = new CNFConverter(g);
				
				convertToCNF(cv, p[i]);
				myCNFMap.put(p[i], myTempCNF);
				resultList.addAll(myCNFMap.get(p[i]));
			}
		//	System.out.println("Initial CNF Map = "+myCNFMap);

			//System.out.println(resultList);
			Production[] pp=new Production[resultList.size()];
			HashMap <Production, Production> originalToCNF=new HashMap <Production, Production>();
			for (int i=0; i<pp.length; i++)
			{
				pp[i]=resultList.get(i);
			}
			pp=converter.convert(pp);
			//System.out.println("CONverted : "+Arrays.asList(pp));
			for (int i=0; i<pp.length; i++)
			{
				originalToCNF.put(resultList.get(i), pp[i]);
			}
		//	System.out.println("ORiginal = "+originalToCNF);
			finalizeCNFMap(originalToCNF);
			g=cp.getGrammar();
			
		//	System.out.println("FINAL CNF Map = "+myCNFMap);
			
		}
	//	System.out.println(g);
	}

	private void finalizeCNFMap(HashMap <Production, Production> map)
	{
		for (Production p : myCNFMap.keySet())
		{
			ArrayList <Production> temp=new ArrayList <Production>();
			for (Production pp : myCNFMap.get(p))
			{
				temp.add(map.get(pp));
			}
			myCNFMap.put(p, temp);
		}
	}
	
	private void convertToCNF(CNFConverter converter, Production p)
	{
		if (!converter.isChomsky(p))
		{
			Production temp[]=converter.replacements(p);

			for (int j=0; j<temp.length; j++)
			{
				p=temp[j];
				convertToCNF(converter, p);
			}
		}	
		else
			myTempCNF.add(p);
	}
	// always str1's length is longer than str2. (Assumption) 
	private ArrayList<String> getDifferentVariable(String str1, String str2) {

		ArrayList <String> result=new ArrayList <String>();
		char[] char1=str1.toCharArray();
		char[] char2=str2.toCharArray();
		
		int index=0;
		boolean breakOut=false;
		for (int i=0; i<char1.length; i++)
		{
		//	//System.out.println("index = "+index);
			if (index==char2.length)
			{
				breakOut=true;
				break;
			}
		//	//System.out.println(char1[i]+" and "+char2[index]);
			
			if (char1[i]!=char2[index])
			{
				result.add(""+char1[i]);
				index--;
		//		//System.out.println("EEEE "+char1[i]);
			}
			index++;
		}
		if (breakOut)
		{
			for (int i=index; i<char1.length; i++)
			{
				result.add(""+char1[i]);
			}
		}
		return result;
	}

	public void traceBack()
	{
	//	System.out.println("ANSWER NOW = "+myTrace);
		backTrackToCNF();
		backTrackToUnit();
		backTrackToLambda();
		
		//System.out.println("size is = "+myAnswer.size());
		if (myAnswer.size()==0)
			myAnswer.addAll(myTrace);
	//	System.out.println("final answer");
	//	System.out.println(myAnswer);
	}

	private void backTrackToCNF()
	{
		if (myCNFMap==null)
		{
			backTrackToUnit();
			return; 
		}
		
	//	System.out.println("MAP : "+myCNFMap);
		int[] visited=new int[myTrace.size()];
		for (int i=0; i<myTrace.size(); i++)
		{
			if (visited[i]==0)
			{
				Production target=myTrace.get(i);
				if (myCNFMap.keySet().contains(target))
				{
					myAnswer.add(target);
					visited[i]=1;
				}
				else
				{
					for (Production p : myCNFMap.keySet())
					{
						if (myCNFMap.get(p).contains(target))
						{
		//					System.out.println(p+" -> " + myCNFMap.get(p)+ " contains "+target);
							visited=searchForRest(myCNFMap.get(p), p, visited);
						}
					}
				}
			}
		}
	//	System.out.println("After Backtracking CNF = "+myAnswer);
		
	}
	
	private int[] searchForRest(ArrayList<Production> list, Production p, int[] visited) 
	{
		HashSet <Production> visitedProd=new HashSet <Production>();
	//	System.out.println("Searching through "+list);
		int[] original=visited;
		int count=0;
		for (int i=0; i<myTrace.size(); i++)
		{
			if (list.contains(myTrace.get(i)) && visited[i]==0 && !visitedProd.contains(myTrace.get(i)))
			{
		//		System.out.println("FOUDN = "+myTrace.get(i));
				visited[i]=1;
				visitedProd.add(myTrace.get(i));
				count++;
				
			}
		}
		if (count==list.size())
		{
			myAnswer.add(p);
			return visited;
		}
		else
			return original;

	}

	private void backTrackToUnit()
	{
		if (myUnitStepMap==null)
			return;
		int index=0;
		while (index<myAnswer.size())
		{
			for (ArrayList <Production> key: myUnitStepMap.keySet())
			{
				if (myUnitStepMap.get(key).equals(myAnswer.get(index)))
				{
					myAnswer.remove(index);
					int c=0;
					for (Production p : key)
					{
						myAnswer.add(index+c, p);
						c++;
					}
					index=index+key.size()-1;
				}
			}
			index++;
		}
		//System.out.println("After Backtracking Unit Step = "+myAnswer);
		
	}
	
	private void backTrackToLambda()
	{
		if (myLambdaStepMap==null)
			return;
		int index=0;
		while (index<myAnswer.size())
		{
			for (ArrayList <Production> key: myLambdaStepMap.keySet())
			{
				if (myLambdaStepMap.get(key).equals(myAnswer.get(index)))
				{
					//System.out.println("Found it = "+key);
					//System.out.println("For = "+myAnswer.get(index));
					myAnswer.remove(index);
					int c=0;
					for (Production p : key)
					{
						myAnswer.add(index+c, p);
						c++;
					}
					index=index+key.size()-1;
				}
			}
			index++;
		}	
	//	System.out.println("After Backtracking Lambda = "+myAnswer);
		
	}
	
	public Production[] getAnswer()
	{
	
	/*	Collections.sort(myAnswer, new Comparator<Production>(){
            public int compare(Production o1, Production o2) {
            	return (o2.getRHS().length()-o1.getRHS().length());
            }
        });
		*/
		Production[] answer=new Production[myAnswer.size()];
		for (int i=0; i<myAnswer.size(); i++)
		{
			answer[i]=myAnswer.get(i);
		}
		return answer;
	}	
	
	private void reportError()
	{
		//System.out.println("ERROR ~ ERROR!");
	}
}
