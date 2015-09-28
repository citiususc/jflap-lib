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




package edu.duke.cs.jflap.gui.grammar.automata;

import java.util.*;

import javax.swing.JOptionPane;

import edu.duke.cs.jflap.grammar.ConvertedUnrestrictedGrammar;
import edu.duke.cs.jflap.grammar.Grammar;
import edu.duke.cs.jflap.grammar.Production;
import edu.duke.cs.jflap.grammar.UnrestrictedGrammar;
import edu.duke.cs.jflap.gui.environment.FrameFactory;
import edu.duke.cs.jflap.gui.viewer.SelectionDrawer;
import edu.duke.cs.jflap.automata.State;
import edu.duke.cs.jflap.automata.Transition;
import edu.duke.cs.jflap.automata.turing.TuringMachine;
import edu.duke.cs.jflap.automata.turing.TuringToGrammarConverter;


/**
 * Controller for conversion
 * @author Kyung Min (Jason) Lee
 *
 */
public class TuringConvertController extends ConvertController {
	
	private TuringToGrammarConverter converter;
	private TuringMachine myTuringMachine;
	
	/**
	 * Instantiates a <CODE>PDAConvertController</CODE> for an automaton.
	 * 
	 * @param pane
	 *            the convert pane that holds the automaton pane and the grammar
	 *            table
	 * @param drawer
	 *            the selection drawer where the automaton is made
	 * @param automaton
	 *            the automaton to build the <CODE>PDAConvertController</CODE>
	 *            for
	 */
	public TuringConvertController(ConvertPane pane, SelectionDrawer drawer,
			TuringMachine automaton) {
		super(pane, drawer, automaton);
		myTuringMachine=automaton;
		converter = new TuringToGrammarConverter();
//		converter.initializeConverter();

		pane.getTable().getColumnModel().getColumn(0).setMinWidth(150);

		pane.getTable().getColumnModel().getColumn(0).setMaxWidth(250);
		fillMap();
	}

	/**
	 * Returns the productions for a particular state. This method will only be
	 * called once.
	 * 
	 * @param state
	 *            the state to get the productions for
	 * @return an array containing the productions that correspond to a
	 *         particular state
	 */
	protected Production[] getProductions(State state) {
		if (myTuringMachine.isInitialState(state))
		{
			Transition[] tm=myTuringMachine.getTransitions();
			return converter.createProductionsForInit(state, tm);
		}
		return new Production[0];
	}

	/**
	 * Returns the productions for a particular transition. This method will
	 * only be called once.
	 * 
	 * @param transition
	 *            the transition to get the productions for
	 * @return an array containing the productions that correspond to a
	 *         particular transition
	 */
	protected Production[] getProductions(Transition transition) {
/*		return (Production[]) converter.createProductionsForTransition(
				transition, getAutomaton()).toArray(new Production[0]);
*/
		return converter.createProductionsForTransition(transition, myTuringMachine.getFinalStates());
	}
	
	
	@Override
	protected ConvertedUnrestrictedGrammar getGrammar() {
		// TODO Auto-generated method stub
		
		//Put error check here
		
		int rows = getModel().getRowCount();
		ConvertedUnrestrictedGrammar grammar = new ConvertedUnrestrictedGrammar();
		grammar.setStartVariable("S");
		ArrayList <Production> productions = new ArrayList <Production>();
		for (int i = 0; i < rows; i++) {
			Production production = getModel().getProduction(i);
			if (production == null)
				continue;
		//	production = converter.getSimplifiedProduction(production);
			productions.add(production);
		}

		Collections.sort(productions, new Comparator() {
			public int compare(Object o1, Object o2) {
				Production p1 = (Production) o1, p2 = (Production) o2;
				if ("S".equals(p1.getLHS())) {
					if (p1.getLHS().equals(p2.getLHS()))
						return 0;
					else
						return -1;
				}
				if ("S".equals(p2.getLHS()))
					return 1;
				return p2.getLHS().compareTo(p1.getRHS());
			}

			public boolean equals(Object o) {
				return false;
			}
		});
		for (int i=0; i<productions.size(); i++)
		{
			grammar.addProduction(productions.get(i));
		}

	/*	UselessProductionRemover remover = new UselessProductionRemover();
		
		Grammar g2 = UselessProductionRemover
				.getUselessProductionlessGrammar(grammar);
		
		if (g2.getTerminals().length==0)
		{
			System.out.println("Error : This grammar does not accept any Strings. ");
		}
		Production[] p1 = grammar.getProductions();
		Production[] p2 = g2.getProductions();
		if (p1.length > p2.length) {
			UselessPane up = new UselessPane(new GrammarEnvironment(null), grammar);
			UselessController controller=new UselessController(up, grammar);
			controller.doAll();
			grammar=controller.getGrammar();
		}*/
		return grammar;

	}
	
	/**
	 * Called by when export grammar button is clicked
	 */
	public Grammar exportGrammar() {
		// Are any yet unconverted?
		if (objectToProduction.keySet().size() != alreadyDone.size()) {
			highlightUntransformed();
			JOptionPane
					.showMessageDialog(
							convertPane,
							"Conversion unfinished!  Objects to convert are highlighted.",
							"Conversion Unfinished", JOptionPane.ERROR_MESSAGE);
			changeSelection();
			return null;
		}
		try {
			ConvertedUnrestrictedGrammar g=getGrammar();
			ArrayList <Production> prods=new ArrayList <Production>();
			Production[] temp=g.getProductions();
			for (int i=0; i<temp.length; i++)
				prods.add(temp[i]);
			
			Collections.sort(prods, new Comparator<Production>(){
		            public int compare(Production o1, Production o2) {
		            	if (o1.getLHS().equals("S"))
		            		return -1;
		            	return (o1.getRHS().length()-o2.getRHS().length());
		            }
		    });
			ConvertedUnrestrictedGrammar gg=new ConvertedUnrestrictedGrammar();
			for (int i=0; i<temp.length; i++)
				temp[i]=prods.get(i);
			gg.setStartVariable("S");
			gg.addProductions(temp);
			FrameFactory.createFrame(gg, 0);
			return gg;
		} catch (GrammarCreationException e) {
			JOptionPane.showMessageDialog(convertPane, e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
			return null;
		}
	}
	
	/**
	 * Trimming the grammar. Gets rid of variable V(aa) to regular variable "A" or "B"
	 * NOTE: It is no longer used in this class 
	 * @param prods
	 * @return
	 */
	private Grammar trim(Production[] prods)
	{
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
