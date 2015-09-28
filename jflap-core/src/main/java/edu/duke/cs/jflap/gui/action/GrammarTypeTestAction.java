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

import edu.duke.cs.jflap.grammar.CNFConverter;
import edu.duke.cs.jflap.grammar.Grammar;
import edu.duke.cs.jflap.grammar.Production;
import edu.duke.cs.jflap.grammar.ProductionChecker;
import edu.duke.cs.jflap.grammar.UnrestrictedGrammar;
import edu.duke.cs.jflap.gui.environment.EnvironmentFrame;
import edu.duke.cs.jflap.gui.environment.GrammarEnvironment;
import edu.duke.cs.jflap.gui.environment.Universe;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

/**
 * Action for testing grammar to see what type of grammar it is.
 * @author Kyung Min (Jason) Lee
 *
 */
public class GrammarTypeTestAction extends GrammarAction {


	/** The grammar environment. */
	private GrammarEnvironment environment;

	/** The frame for the grammar environment. */
	private EnvironmentFrame frame;
	
	/**
	 * Instantiates a new <CODE>GrammarTypeTestAction</CODE>.
	 * 
	 * @param environment
	 *            the grammar environment
	 */
	public GrammarTypeTestAction(GrammarEnvironment environment) {
		super("Test for Grammar Type", null);
		this.environment = environment;
		this.frame = Universe.frameForEnvironment(environment);
	}
	
	/**
	 * Performs the action.
	 */
	public void actionPerformed(ActionEvent e) {
		Grammar g=environment.getGrammar(UnrestrictedGrammar.class);
//        EDebug.print(g == null);
        if (g == null) return;

		Production[] p=g.getProductions();
		boolean isRegular=checkforLinearity(p);
		if (!isRegular)
		{
			if (!isContextFreeGrammar(p, g))
			{
				checkForSpecialUnrestrictedGrammar(p);
			}
		}
	}

	/**
	 * Check if the grammar is Unrestrcited Grammar from TM (through our conversion feature)
	 * @param p Production to check for
	 */
	private void checkForSpecialUnrestrictedGrammar(Production[] p) {
		// primitive, but it's not that bad. 
		//Check to see if production contains three special productions that indicates, it is converted from TM\
		Production[] sp=new Production[3];
		sp[0]=new Production("S", "V(==)S");
		sp[1]=new Production("S", "SV(==)");
		sp[2]=new Production("S", "T");
		boolean[] count=new boolean[3];
		for (int i=0; i<p.length; i++)
		{
			for (int j=0; j<sp.length; j++)
			{
				if (p[i].getLHS().equals(sp[j].getLHS()) && p[i].getRHS().equals(sp[j].getRHS()))
					count[i]=true;
			}
		}
		int pp=0;
		for (int i=0; i<count.length; i++)
		{
			if (count[i])
				pp++;
		}
		if (pp==count.length)
		{
			JOptionPane.showMessageDialog(environment.getComponent(0), "This is an Unrestricted Grammar (converted from TM)", "Grammar Type"
					, JOptionPane.INFORMATION_MESSAGE);
		}
		else
		{
			int tt=0;
			for (int i=0; i<p.length; i++)
			{
				if (p[i].getLHS().length()<=p[i].getRHS().length())
				{
					tt++;
				}
			}
			if (tt==p.length)
			{
				JOptionPane.showMessageDialog(environment.getComponent(0), "This is a Context-Sensitive Grammar (also Unrestricted Grammar)", "Grammar Type"
						, JOptionPane.INFORMATION_MESSAGE);
					
			}
			else
			{
				JOptionPane.showMessageDialog(environment.getComponent(0), "This is an Unrestricted Grammar", "Grammar Type"
					, JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

	/**
	 * Check to see if grammar is Context Free Grammar or in CNF form or in GNF form
	 * @param p Production to check for
	 * @return True if grammar is CFG
	 */
	private boolean isContextFreeGrammar(Production[] p, Grammar g) {
		// TODO Auto-generated method stub
		int count=0;
		for (int i=0; i<p.length; i++)
		{
			if (ProductionChecker.isRestrictedOnLHS(p[i]))
				count++;
		}
		if (count!=p.length)
			return false;
		CNFConverter converter = null;
		converter = new CNFConverter(g);
		boolean chomsky = true;
		for (int i = 0; i < p.length; i++)
			chomsky &= converter.isChomsky(p[i]);
		if (chomsky)
		{
			JOptionPane.showMessageDialog(environment.getComponent(0), "This is a CNF Grammar (also Context-Free Grammar)", "Grammar Type"
					, JOptionPane.INFORMATION_MESSAGE);
			return true;
		}
		else
		{
			count=0;
			boolean isGNF=true;
			for (int i=0; i<p.length; i++)
			{
				if (p[i].getRHS().length()==0)
					count++;
				else
				{
					char firstCh=p[i].getRHS().charAt(0);
					if (ProductionChecker.isTerminal(firstCh))
					{
						for (int j=1; j<p[i].getRHS().length(); j++)
						{
							if (!ProductionChecker.isVariable(p[i].getRHS().charAt(j)))
							{
								isGNF=false;
							}
						}
					}
					else
						isGNF=false;
				}
				
			}
			if (isGNF)
			{
				JOptionPane.showMessageDialog(environment.getComponent(0), "This is a GNF Grammar (also Context-Free Grammar)", "Grammar Type"
						, JOptionPane.INFORMATION_MESSAGE);
					
			}
			else
			{
				JOptionPane.showMessageDialog(environment.getComponent(0), "This is a Context-Free Grammar", "Grammar Type"
					, JOptionPane.INFORMATION_MESSAGE);
			}
			return true;
		}
	}

	/**
	 * Checking to see if the grammar is linear
	 * @param Production to check
	 * @return True if the grammar is linear
	 */
	private boolean checkforLinearity(Production[] p)
	{
		int count=0;
		for (int i=0; i<p.length; i++)
		{
			if (ProductionChecker.isRightLinear(p[i]))
					count++;
		}
		if (count==p.length)
		{
			JOptionPane.showMessageDialog(environment.getComponent(0), "This is a right-linear Grammar (Regular Grammar and Context-Free Grammar)", "Grammar Type"
					, JOptionPane.INFORMATION_MESSAGE);
			return true;
		}
		else
		{
			count=0;
			for (int i=0; i<p.length; i++)
			{
				if (ProductionChecker.isLeftLinear(p[i]))
						count++;
			}
			if (count==p.length)
			{
				JOptionPane.showMessageDialog(environment.getComponent(0), "This is a left-linear Grammar (Regular Grammar and Context-Free Grammar)", "Grammar Type"
						, JOptionPane.INFORMATION_MESSAGE);
				return true;
			}
		}
		return false;
	}
}
