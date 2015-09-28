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
import edu.duke.cs.jflap.grammar.LambdaProductionRemover;
import edu.duke.cs.jflap.grammar.Production;
import edu.duke.cs.jflap.grammar.UnitProductionRemover;
import edu.duke.cs.jflap.grammar.UnrestrictedGrammar;
import edu.duke.cs.jflap.grammar.UselessProductionRemover;
import edu.duke.cs.jflap.gui.environment.EnvironmentFrame;
import edu.duke.cs.jflap.gui.environment.GrammarEnvironment;
import edu.duke.cs.jflap.gui.environment.Universe;
import edu.duke.cs.jflap.gui.environment.tag.CriticalTag;
import edu.duke.cs.jflap.gui.grammar.parse.CYKParsePane;
import edu.duke.cs.jflap.gui.grammar.transform.LambdaController;
import edu.duke.cs.jflap.gui.grammar.transform.LambdaPane;
import edu.duke.cs.jflap.gui.grammar.transform.UnitController;
import edu.duke.cs.jflap.gui.grammar.transform.UnitPane;
import edu.duke.cs.jflap.gui.grammar.transform.UselessController;
import edu.duke.cs.jflap.gui.grammar.transform.UselessPane;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Set;

import javax.swing.JOptionPane;

/**
 * CYK Parsing Action class
 * @author Kyung Min (Jason) Lee
 *
 */
public class CYKParseAction extends GrammarAction{

	/** The grammar environment. */
	protected GrammarEnvironment environment;

	/** The frame for the grammar environment. */
	protected EnvironmentFrame frame;
	
	/** The Grammar that is going to be transformed into CNF */
	protected Grammar myGrammar;
	
	/** Boolean variable that would tell whehter or not error has occured during transformation */
	protected boolean myErrorInTransform;

	private ArrayList <Production> myTempCNF;
	/**
	 * Instantiates a new <CODE>CYKParse Action</CODE>.
	 * 
	 * @param environment
	 *            the grammar environment
	 */
	public CYKParseAction(GrammarEnvironment environment) {
		super("CYK Parse", null);
		this.environment = environment;
		this.frame = Universe.frameForEnvironment(environment);
	}
	
	/**
	 * Another Constructor that is going to be called by its subclasses
	 */
	public CYKParseAction(String tag, GrammarEnvironment environment) {
		super(tag, null);
		this.environment = environment;
		this.frame = Universe.frameForEnvironment(environment);
	}


	/**
	 * Performs the action.
	 */
	public void actionPerformed(ActionEvent e) {
		Grammar g = environment.getGrammar(UnrestrictedGrammar.class);
		myGrammar=g;
		if (g == null)
			return;
		if (g.getTerminals().length==0)
		{
			JOptionPane.showMessageDialog(environment,
					"Error : This grammar does not accept any Strings. ",
					"Cannot Proceed with CYK", JOptionPane.ERROR_MESSAGE);
			myErrorInTransform=true;
			return;
		}
		hypothesizeLambda(environment, g);
		if (!myErrorInTransform)
		{
			CYKParsePane cykPane = new CYKParsePane(environment, g, myGrammar);
			environment.add(cykPane, "CYK Parse", new CriticalTag() {
			});
			environment.setActive(cykPane);
		}
	}

	/**
	 * Getting rid of the lambda variable and lambda derivers in the grammar
	 * @param env Our grammar environment
	 * @param g Original grammar that is going to be changed
	 */
	protected void hypothesizeLambda(GrammarEnvironment env, Grammar g) {
		LambdaProductionRemover remover = new LambdaProductionRemover();
		Set lambdaDerivers = remover.getCompleteLambdaSet(g);
		if (lambdaDerivers.contains(g.getStartVariable())) {
			JOptionPane.showMessageDialog(env,
					"WARNING : The start variable derives lambda.\n"
							+ "New Grammar will not produce lambda String.",
					"Start Derives Lambda", JOptionPane.ERROR_MESSAGE);
		}
	    if (lambdaDerivers.size() > 0) {
	    	LambdaPane lp = new LambdaPane(env, g);
	    	LambdaController controller = new LambdaController(lp, g);
	    	controller.doAll();
	    	g=controller.getGrammar();
	    }
	    hypothesizeUnit(env, g);
	}
	
	/**
	 * Method for getting rid of unit productions
	 * @param env Our grammar environment
	 * @param g Grammar in transformation
	 */
	protected void hypothesizeUnit(GrammarEnvironment env, Grammar g) {
		UnitProductionRemover remover = new UnitProductionRemover();
		if (remover.getUnitProductions(g).length > 0) {
			UnitPane up = new UnitPane(env, g);
			UnitController controller=new UnitController(up, g);
			controller.doAll();
			g=controller.getGrammar();
		}
		hypothesizeUseless(env, g);
	}

	/**
	 * Method for getting rid of useless productions
	 * @param env Our grmmar environment
	 * @param g Grammar in transformation
	 */
	protected void hypothesizeUseless(GrammarEnvironment env, Grammar g) {
		UselessProductionRemover remover = new UselessProductionRemover();
		
		Grammar g2 = UselessProductionRemover
				.getUselessProductionlessGrammar(g);
		
		if (g2.getTerminals().length==0)
		{
			JOptionPane.showMessageDialog(env,
					"Error : This grammar does not accept any Strings. ",
					"Cannot Proceed with CYK", JOptionPane.ERROR_MESSAGE);
			myErrorInTransform=true;
			return;
		}
		Production[] p1 = g.getProductions();
		Production[] p2 = g2.getProductions();
		if (p1.length > p2.length) {
			UselessPane up = new UselessPane(env, g);
			UselessController controller=new UselessController(up, g);
			controller.doAll();
			g=controller.getGrammar();
		}
		hypothesizeChomsky(env, g);
	}

	/**
	 * Method for finalizing Chomsky form
	 * @param env Our grammar environment
	 * @param g Grammar in transformation
	 */
	protected void hypothesizeChomsky(GrammarEnvironment env, Grammar g) {
		//System.out.println("Chomsky TIME");
		
		CNFConverter converter = null;
		try {
			converter = new CNFConverter(g);
		} catch (IllegalArgumentException e) {
			JOptionPane.showMessageDialog(env, e.getMessage(),
					"Illegal Grammar", JOptionPane.ERROR_MESSAGE);
			return;
		}
		Production[] p = g.getProductions();
		boolean chomsky = true;
		for (int i = 0; i < p.length; i++)
			chomsky &= converter.isChomsky(p[i]);
		
		if (!chomsky) {
			ArrayList <Production> resultList=new ArrayList <Production>();
			for (int i=0; i<p.length; i++)
			{
				myTempCNF=new ArrayList <Production>();
				converter = new CNFConverter(g);
				convertToCNF(converter, p[i]);
				resultList.addAll(myTempCNF);
			}
			Production[] pp=new Production[resultList.size()];
			for (int i=0; i<pp.length; i++)
			{
				pp[i]=resultList.get(i);
			}
			pp=converter.convert(pp);
			String var=g.getStartVariable();
			g=new UnrestrictedGrammar();
			g.addProductions(pp);
			g.setStartVariable(var);
			
		}
		myGrammar=g;
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
}
