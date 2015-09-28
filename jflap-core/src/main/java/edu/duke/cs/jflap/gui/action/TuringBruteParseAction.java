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

import edu.duke.cs.jflap.grammar.Grammar;
import edu.duke.cs.jflap.grammar.Production;
import edu.duke.cs.jflap.grammar.UnrestrictedGrammar;
import edu.duke.cs.jflap.gui.environment.EnvironmentFrame;
import edu.duke.cs.jflap.gui.environment.GrammarEnvironment;
import edu.duke.cs.jflap.gui.environment.Universe;
import edu.duke.cs.jflap.gui.environment.tag.CriticalTag;
import edu.duke.cs.jflap.gui.grammar.parse.TMBruteParsePane;

import java.awt.event.ActionEvent;
import java.util.HashMap;

/**
 * Special brute force parser for parsing grammar that was converted from Turing Machine
 * @author Kyung Min (Jason) Lee
 *
 */
public class TuringBruteParseAction extends GrammarAction {
	
	/** The grammar environment. */
	private GrammarEnvironment environment;

	/** The frame for the grammar environment. */
	private EnvironmentFrame frame;
	
	private HashMap <String, String> myVariableMap;
	
	/**
	 * Instantiates a new <CODE>BruteParseAction</CODE>.
	 * 
	 * @param environment
	 *            the grammar environment
	 */
	
	public TuringBruteParseAction(GrammarEnvironment environment) {
		super("Parser for Converted Grammar from TM", null);
		this.environment = environment;
		this.frame = Universe.frameForEnvironment(environment);
	}
	
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		Grammar g = environment.getGrammar(UnrestrictedGrammar.class);
		Production[] p=g.getProductions();
		Grammar g_trimmed=trim(p);
//		FrameFactory.createFrame(g_trimed);

		if (g == null)
			return;
		TMBruteParsePane bpp = new TMBruteParsePane(environment, g, g_trimmed, myVariableMap, null);
		environment.add(bpp, "Parser for Converted Grammar from TM", new CriticalTag() {
		});
		environment.setActive(bpp);
	}

	/**
	 * Trimming grammar, so variable V(a0a) becomes normal variable like "A" or "B", etc.
	 * @param prods Productions that needs to be trimmed.
	 * @return
	 */
	private Grammar trim(Production[] prods)
	{
		myVariableMap=new HashMap <String, String>();
		char ch='A';
		for (int i=0; i<prods.length; i++)
		{
			String lhs=prods[i].getLHS();
			if (ch=='S' || ch=='T')
			{
				ch++;
			}
			int aa=lhs.indexOf("V(");
			while (aa>-1)
			{
			
		//		System.out.println("in 1st "+lhs+"===>    ");
				int bb=lhs.indexOf(")");
				String var="";
				if ((aa+bb+1) > lhs.length())
				{
					var=lhs.substring(aa, aa+bb);
					lhs=lhs.substring(0, aa)+ch;
				}
				else
				{
					var=lhs.substring(aa, aa+bb+1);
					lhs=lhs.substring(0, aa)+ch+lhs.substring(aa+bb);
				}
			//	System.out.println(var+ " and new lhs is = "+lhs);
				aa=lhs.indexOf("V(");
				
				myVariableMap.put(""+ch, var);
			//	System.out.println(var+" converted to : "+ch);
					
				//	lhs.replaceAll("V"+aa[j], "A");
				for (int k=0; k<prods.length; k++)
				{
					
					String inner_lhs=prods[k].getLHS();
					String inner_rhs=prods[k].getRHS();
					int a=inner_lhs.indexOf(var);
					if (a>-1)
					{
				//		System.out.println("in inner lhs  "+inner_lhs+"   ===>    ");
						inner_lhs=inner_lhs.substring(0, a)+""+ch+inner_lhs.substring(a+var.length());
				//		System.out.println(inner_lhs);
					}
					a=inner_rhs.indexOf(var);
					if (a>-1)
					{
			//			System.out.println("in inner rhs   "+inner_rhs+"   ===>    ");
						
						inner_rhs=inner_rhs.substring(0, a)+""+ch+inner_rhs.substring(a+var.length());
			//			System.out.println(inner_rhs);
						
					}
					prods[k]=new Production(inner_lhs, inner_rhs);
				}
				ch=(char) (ch+1);
		
			//	System.out.println(lhs);
			}
		}
		Grammar g=new UnrestrictedGrammar();
		g.addProductions(prods);
		return g;
	}

}
