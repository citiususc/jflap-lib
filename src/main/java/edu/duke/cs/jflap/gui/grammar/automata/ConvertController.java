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

import edu.duke.cs.jflap.automata.Automaton;
import edu.duke.cs.jflap.automata.State;
import edu.duke.cs.jflap.automata.Transition;
import edu.duke.cs.jflap.grammar.Grammar;
import edu.duke.cs.jflap.grammar.Production;
import edu.duke.cs.jflap.gui.environment.FrameFactory;
import edu.duke.cs.jflap.gui.grammar.*;
import edu.duke.cs.jflap.gui.viewer.SelectionDrawer;
import java.util.*;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * The <CODE>ConvertController</CODE> abstract class handles the operation to
 * convert an automaton to a PDA. At its most basic level, it simply maps
 * objects in the automaton to a set of productions in a grammar.
 * 
 * @author Thomas Finley
 */

public abstract class ConvertController {
	/**
	 * Instantiates a <CODE>ConvertController</CODE> for an automaton.
	 * 
	 * @param pane
	 *            the convert pane that holds the automaton pane and the grammar
	 *            table
	 * @param drawer
	 *            the selection drawer where the automaton is made
	 * @param automaton
	 *            the automaton to build the <CODE>ConvertController</CODE>
	 *            for; this automaton should be editable
	 * @see #fillMap
	 */
	public ConvertController(ConvertPane pane, SelectionDrawer drawer,
			Automaton automaton) {
		this.convertPane = pane;
		this.automaton = automaton;
		this.table = pane.getTable();
		this.drawer = drawer;

		table.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent e) {
						changeSelection();
					}
				});
	}

	/**
	 * Changes the selection in the automaton selection pane. This method is
	 * usually called as a result of the list selection changing.
	 */
	protected void changeSelection() {
		ListSelectionModel model = table.getSelectionModel();
		int min = model.getMinSelectionIndex(), max = model
				.getMaxSelectionIndex();
		drawer.clearSelected();
		if (min == -1) {
			convertPane.getAutomatonPane().repaint();
			return;
		}
		for (; min <= max; min++) {
			if (!model.isSelectedIndex(min))
				continue;
			Production p = table.getGrammarModel().getProduction(min);
			Object o = productionToObject.get(p);
			if (o == null)
				continue;
			if (o instanceof State)
				drawer.addSelected((State) o);
			else
				drawer.addSelected((Transition) o);
		}
		convertPane.getAutomatonPane().repaint();
	}

	/**
	 * Fills the maps. This method should be called by subclasses after the
	 * constructor, whenever the controller is ready to produce the productions.
	 */
	protected void fillMap() {
		State[] states = automaton.getStates();
		for (int i = 0; i < states.length; i++) {
			Production[] prods = getProductions(states[i]);
			if (prods.length == 0)
				continue;
			objectToProduction.put(states[i], prods);
			for (int j = 0; j < prods.length; j++)
				productionToObject.put(prods[j], states[i]);
		}
		// Now let's get the other cannon!
		Transition[] transitions = automaton.getTransitions();
		for (int i = 0; i < transitions.length; i++) {
			Production[] prods = getProductions(transitions[i]);
			if (prods.length == 0)
				continue;
			objectToProduction.put(transitions[i], prods);
			for (int j = 0; j < prods.length; j++)
				productionToObject.put(prods[j], transitions[i]);
		}
	}

	/**
	 * Adds productions to the grammar pane, and makes them selected.
	 * 
	 * @param productions
	 *            the collection that holds productions to add
	 */
	private void addProductions(Collection productions) {
		Iterator it = productions.iterator();
		if (!it.hasNext())
			return;
		GrammarTableModel model = table.getGrammarModel();
		int min = 1000000000, max = 0;
		while (it.hasNext()) {
			Production p = (Production) it.next();
			int row = model.addProduction(p);
			min = Math.min(min, row);
			max = Math.max(max, row);
		}
		table.setRowSelectionInterval(min, max);
	}

	/**
	 * This method reveals the productions for a particular object, whether it
	 * be a state or transition.
	 * 
	 * @param object
	 *            the object whose productions we should reveal
	 * @return a non-empty array of productions revealed, <CODE>null</CODE> if
	 *         there are no productions for this object, or an empty array if
	 *         there are productions for this object and they have already been
	 *         revealed
	 */
	public Production[] revealObjectProductions(Object object) {
		Production[] p = (Production[]) objectToProduction.get(object);
		if (p == null || p.length == 0) {
			// There are no productions!
			JOptionPane.showMessageDialog(convertPane,
					"There are no productions for that object!");
			return null;
		}
		if (alreadyDone.contains(object)) {
			// Been there, done that.
			JOptionPane.showMessageDialog(convertPane,
					"This object has already been converted!");
			return new Production[0];
		}
		alreadyDone.add(object);
		addProductions(Arrays.asList(p));
		return p;
	}

	/**
	 * This will reveal the productions for one object chosen at quasirandom
	 * (i.e. whatever comes first).
	 * 
	 * @return the object whose productions were revealed, or <CODE>null</CODE>
	 *         if no object remains to have its productions revealed
	 */
	public Object revealRandomProductions() {
		Iterator it = objectToProduction.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			Object key = entry.getKey();
			if (alreadyDone.contains(key))
				continue;
			Production[] p = (Production[]) objectToProduction.get(key);
			addProductions(Arrays.asList(p));
			alreadyDone.add(entry.getKey());
			return key;
		}
		return null;
	}

	/**
	 * This will reveal all the productions for all objects remaining to be
	 * revealed.
	 * 
	 * @return the number of objects revealed
	 */
	public int revealAllProductions() {
		Set remaining = new HashSet(objectToProduction.keySet());
		remaining.removeAll(alreadyDone);
		int number = remaining.size();
		Iterator it = remaining.iterator();
		Collection ps = new ArrayList();
		while (it.hasNext()) {
			Production[] p = (Production[]) objectToProduction.get(it.next());
			ps.addAll(Arrays.asList(p));
		}
		addProductions(ps);
		alreadyDone.addAll(remaining);
		return number;
	}

	/**
	 * This method sets all objects that may be tranformed to productions and as
	 * yet have been unselected to be selected.
	 * 
	 * @return an array of the objects which as yet have not been transformed
	 */
	public Object[] highlightUntransformed() {
		HashSet unselectedSet = new HashSet(objectToProduction.keySet());
		unselectedSet.removeAll(alreadyDone);
		Object[] unselected = unselectedSet.toArray();
		drawer.clearSelected();
		for (int i = 0; i < unselected.length; i++)
			if (unselected[i] instanceof State)
				drawer.addSelected((State) unselected[i]);
			else
				drawer.addSelected((Transition) unselected[i]);
		convertPane.getAutomatonPane().repaint();
		return unselected;
	}

	/**
	 * Returns the grammar that resulted from the conversion. This method should
	 * only be called once the conversion has finished.
	 * 
	 * @return the grammar that resulted from the conversion
	 * @throws GrammarCreationException
	 *             if there is some impediment to the creation of the grammar
	 */
	protected abstract Grammar getGrammar();

	/**
	 * Exports the grammar defined to a new window, or fails if the grammar is
	 * not yet converted.
	 * 
	 * @return the grammar that was converted as returned by <CODE>getGrammar</CODE>;
	 *         if there was an error in <CODE>getGrammar</CODE> or if the
	 *         conversion is unfinished, <CODE>null</CODE> is returned
	 * @see #getGrammar
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
			Grammar g = getGrammar();
			FrameFactory.createFrame(g);
			return g;
		} catch (GrammarCreationException e) {
			JOptionPane.showMessageDialog(convertPane, e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
			return null;
		}
	}

	/**
	 * Returns the <CODE>Automaton</CODE>.
	 * 
	 * @return the <CODE>Automaton</CODE> for this controller
	 */
	protected Automaton getAutomaton() {
		return automaton;
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
		return new Production[0];
	}

	/**
	 * Returns the table model for the grammar table.
	 * 
	 * @return the table model for the grammar table
	 */
	protected GrammarTableModel getModel() {
		return table.getGrammarModel();
	}

	/**
	 * The mapping of either states or transitions to an array of productions.
	 * If there are no productions for an object, the map will not contain the
	 * key.
	 */
	protected HashMap objectToProduction = new HashMap();

	/**
	 * The mapping of productions to whatever object they correspond to, which
	 * will be either a state or a transition.
	 */
	protected HashMap productionToObject = new HashMap();

	/** Which objects have already been added? */
	protected HashSet alreadyDone = new HashSet();

	/**
	 * The convert pane that holds the automaton pane and the grammar table.
	 */
	protected ConvertPane convertPane;

	/** The automaton we're converting. */
	private Automaton automaton;

	/** The selection drawer. */
	private SelectionDrawer drawer;

	/** The grammar table where the productions are stored. */
	private GrammarTable table;
}
