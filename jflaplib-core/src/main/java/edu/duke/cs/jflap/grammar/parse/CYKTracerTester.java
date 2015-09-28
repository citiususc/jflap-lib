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

import java.util.ArrayList;

import edu.duke.cs.jflap.grammar.Grammar;
import edu.duke.cs.jflap.grammar.Production;
import edu.duke.cs.jflap.grammar.cfg.ContextFreeGrammar;

/**
 * Test Suite for CYK Tracer
 * @author lkm
 *
 */
public class CYKTracerTester {

	public static void main(String[] args)
	{
		Grammar g=new ContextFreeGrammar();
		Production[] p=new Production[5];
	/*	p[0]=new Production("S","bAC");
		p[1]=new Production("A","C");
		p[2]=new Production("A","a");
		p[3]=new Production("B","bAE");
		p[4]=new Production("C","cC");
		p[5]=new Production("C","B");
		p[6]=new Production("C","");
		p[7]=new Production("E","cE");
		p[8]=new Production("D","dFA");
		p[9]=new Production("F","e");*/
		
		p[0]=new Production("S","aSb");
		p[1]=new Production("S","bB");
		p[2]=new Production("B","bbB");
		p[3]=new Production("B","");
		p[4]=new Production("S","SS");
		
	
		g.addProductions(p);
		
		ArrayList <Production> result=new ArrayList <Production>();
		result.add(new Production("S","AD"));
		result.add(new Production("A","a"));
		result.add(new Production("D","SC"));
		result.add(new Production("S","CS"));
		result.add(new Production("C","b"));
		result.add(new Production("S","SS"));
		result.add(new Production("S","b"));
		result.add(new Production("S","b"));
		result.add(new Production("C","b"));
		
		/*result.add(new Production("D","c"));
		result.add(new Production("C","c"));*/
		
		
		CYKTracer ct=new CYKTracer(g, result);
		ct.traceBack();
			
	}
}
