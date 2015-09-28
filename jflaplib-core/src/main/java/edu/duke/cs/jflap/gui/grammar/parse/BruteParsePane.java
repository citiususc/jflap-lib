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





package edu.duke.cs.jflap.gui.grammar.parse;

import edu.duke.cs.jflap.grammar.Grammar;
import edu.duke.cs.jflap.grammar.parse.*;
import edu.duke.cs.jflap.gui.environment.GrammarEnvironment;
import edu.duke.cs.jflap.gui.sim.multiple.InputTableModel;
import edu.duke.cs.jflap.gui.tree.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.tree.*;

/**
 * This is a brute force parse pane.
 * 
 * @author Thomas Finley
 */

public class BruteParsePane extends ParsePane {
	
	public BruteParsePane (GrammarEnvironment environment, Grammar g)
	{
		super(environment, g);
	}
	/**
	 * Instantiates a new brute force parse pane.
	 * 
	 * @param environment
	 *            the grammar environment
	 * @param grammar
	 *            the augmented grammar
	 */
	public BruteParsePane(GrammarEnvironment environment, Grammar grammar, InputTableModel model) {
		super(environment, grammar);
		initView();
		myModel = model;
	}

	/**
	 * Inits a parse table.
	 * 
	 * @return a table to hold the parse table
	 */
	protected JTable initParseTable() {
		return null;
	}

	/**
	 * Returns the interface that holds the input area.
	 */
	protected JPanel initInputPanel() {
		JPanel bigger = new JPanel(new BorderLayout());
		JPanel panel = new JPanel();
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		panel.setLayout(gridbag);

		c.fill = GridBagConstraints.BOTH;

		c.weightx = 0.0;
		panel.add(new JLabel("Input"), c);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		panel.add(inputField, c);
		inputField.addActionListener(startAction);
		// c.weightx = 0.0;
		// JButton startButton = new JButton(startAction);
		// startButton.addActionListener(listener);
		// panel.add(startButton, c);

		panel.add(progress, c);

		bigger.add(panel, BorderLayout.CENTER);
		bigger.add(initInputToolbar(), BorderLayout.NORTH);

		return bigger;
	}

	/**
	 * Returns a toolbar for the parser.
	 * 
	 * @return the toolbar for the parser
	 */
	protected JToolBar initInputToolbar() {
		JToolBar tb = super.initInputToolbar();
		tb.add(new JButton(pauseResumeAction), 1);
		pauseResumeAction.setEnabled(false);
		return tb;
	}
	
	
	public void parseMultiple(){
		String[][] inputs = myModel.getInputs();
		int size = 1;
		if(environment.myObjects != null) size = environment.myObjects.size();
		int uniqueInputs = inputs.length/size;
		Grammar currentGram = grammar;
		if(environment.myObjects != null) currentGram = (Grammar)environment.myObjects.get(0);
		if(row < (inputs.length-1)) {		
			row++;			
				if(row%uniqueInputs==0 && environment.myObjects != null){
					currentGram = (Grammar)environment.myObjects.get(row/uniqueInputs);
					this.grammar = currentGram;        
				}
			try
			{
				parser = BruteParser.get(grammar, inputs[row][0]);
			} catch (IllegalArgumentException e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), "Bad Input",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			parseInput(inputs[row][0], parser);
		}
	}
	
	public void parseInput(String string, BruteParser newParser){
        if(string.equals("")) return;
		if (newParser == null) {
			try {
				parser = BruteParser.get(grammar, string);
			} catch (IllegalArgumentException e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), "Bad Input",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		else parser = newParser;
		final Timer timer = new Timer(10, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (parser == null)
					return;
				String nodeCount = "Nodes generated: "
						+ parser.getTotalNodeCount() + "("
						+ parser.getConsiderationNodeCount() + ")";
				progress.setText("Parser running.  " + nodeCount);
			}
		});
		parser.addBruteParserListener(new BruteParserListener() {
			public void bruteParserStateChange(BruteParserEvent e) {
				synchronized (e.getParser()) {
					String nodeCount = e.getParser().getTotalNodeCount()
							+ " nodes generated.";
					String status = null;
					switch (e.getType()) {
					case BruteParserEvent.START:
						pauseResumeAction.setEnabled(true);
						pauseResumeAction.putValue(Action.NAME, "Pause");
						timer.start();
						status = "Parser started.";
						statusDisplay.setText(status);
						break;
					case BruteParserEvent.REJECT:
						pauseResumeAction.setEnabled(false);
						timer.stop();
						status = "String rejected.";
						if(myModel != null){
                            String[][] inputs = myModel.getInputs();
                            int size = 1;
                            if(environment.myObjects != null) size = environment.myObjects.size();
                            int uniqueInputs = inputs.length/size;                          
							myModel.setResult(row, "Reject", null, environment.myTransducerStrings, (row%uniqueInputs)*2);
							parseMultiple();
						}
						break;
					case BruteParserEvent.PAUSE:
						timer.stop();
						pauseResumeAction.putValue(Action.NAME, "Resume");
						pauseResumeAction.setEnabled(true);
						status = "Parser paused.";
						statusDisplay.setText(status);
						break;
					case BruteParserEvent.ACCEPT:
						pauseResumeAction.setEnabled(false);
						stepAction.setEnabled(true);
						timer.stop();
						status = "String accepted!";
						if(myModel != null){ 
							myModel.setResult(row, "Accept", null, environment.myTransducerStrings, row);
							parseMultiple();
						}
						break;
					}
					progress.setText(status + "  " + nodeCount);
					if (parser.isFinished()) {
//						parser = null;

						if (!e.isAccept()) {
							// Rejected!
							treePanel.setAnswer(null);
							treePanel.repaint();
							stepAction.setEnabled(false);
							statusDisplay.setText("Try another string.");
							return;
						}
						TreeNode node = e.getParser().getAnswer();
						do {
							node = node.getParent();
						} while (node != null);
						statusDisplay
								.setText("Press step to show derivations.");
						treePanel.setAnswer(e.getParser().getAnswer());
						treePanel.repaint();
					}
				}
			}

		});
		parser.start();
	}

	/**
	 * This method is called when there is new input to parse.
	 * 
	 * @param string
	 *            a new input string
	 */
	public void input(String string) {
		if (parser != null) {
			parser.pause();	
		}
		parseInput(string, null);	
	}

	/**
	 * Returns the choices for the view.
	 * 
	 * @return an array of strings for the choice of view
	 */
	protected String[] getViewChoices() {
		return new String[] { "Noninverted Tree", "Derivation Table" };
	}

	/**
	 * This method is called when the step button is pressed.
	 */
	public boolean step() {
		// controller.step();
        boolean worked = false;
		if (treePanel.next()){
            stepAction.setEnabled(false);
            worked = true;
        }
			
		treePanel.repaint();
        return worked;
	}

	/**
	 * Inits a new tree panel. This overriding adds a selection node drawer so
	 * certain nodes can be highlighted.
	 * 
	 * @return a new display for the parse tree
	 */
	protected JComponent initTreePanel() {
		return treePanel;
	}

	public int row = -1;
	/** The tree pane. */
	protected UnrestrictedTreePanel treePanel = new UnrestrictedTreePanel(this);

	/** The selection node drawer. */
	protected SelectNodeDrawer nodeDrawer = new SelectNodeDrawer();

	/** The progress bar. */
	protected JLabel progress = new JLabel(" ");

	/** The current parser object. */
	protected BruteParser parser = null;
	
	protected InputTableModel myModel = null;

	/** The pause/resume action. */
	protected Action pauseResumeAction = new AbstractAction("Pause") {
		public void actionPerformed(ActionEvent e) {
			synchronized (parser) {
				if (parser == null)
					return;
				if (parser.isActive())
					parser.pause();
				else
					parser.start();
			}
		}
	};
}
