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
import edu.duke.cs.jflap.gui.SplitPaneFactory;
import edu.duke.cs.jflap.gui.TableTextSizeSlider;
import edu.duke.cs.jflap.gui.environment.GrammarEnvironment;
import edu.duke.cs.jflap.gui.grammar.*;
import edu.duke.cs.jflap.gui.tree.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.tree.*;

/**
 * The parse pane is an abstract class that defines the interface common between
 * parsing panes.
 * 
 * @author Thomas Finley
 */

abstract class ParsePane extends JPanel {
	/**
	 * Instantiates a new parse pane. This will not place components. A call to
	 * {@link #initView} by a subclass is necessary.
	 * 
	 * @param grammar
	 *            the grammar that is being parsed
	 */
	public ParsePane(GrammarEnvironment environment, Grammar grammar) {
		super(new BorderLayout());
		this.grammar = grammar;
		this.environment = environment;
	}

	/**
	 * Initializes the GUI.
	 */
	protected void initView() {
		treePanel = initTreePanel();

		// Sets up the displays.
		JComponent pt = initParseTable();
		JScrollPane parseTable = pt == null ? null : new JScrollPane(pt);
		GrammarTable g = initGrammarTable(grammar);
		JScrollPane grammarTable = new JScrollPane(g);

		treeDerivationPane.add(initTreePanel(), "0");
		derivationPane = new JScrollPane(initDerivationTable());
		treeDerivationPane.add(derivationPane, "1");
		bottomSplit = SplitPaneFactory.createSplit(environment, true, 0.3,
				grammarTable, treeDerivationPane);
		topSplit = SplitPaneFactory.createSplit(environment, true, 0.4,
				parseTable, initInputPanel());
		mainSplit = SplitPaneFactory.createSplit(environment, false, 0.3,
				topSplit, bottomSplit);
		add(mainSplit, BorderLayout.CENTER);
		add(statusDisplay, BorderLayout.SOUTH);
		add(new TableTextSizeSlider(g), BorderLayout.NORTH);
	}

	/**
	 * Initializes a table for the grammar.
	 * 
	 * @param grammar
	 *            the grammar
	 * @return a table to display the grammar
	 */
	protected GrammarTable initGrammarTable(Grammar grammar) {
		grammarTable = new GrammarTable(new GrammarTableModel(grammar) {
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		}) {
			public String getToolTipText(java.awt.event.MouseEvent event) {
				try {
					int row = rowAtPoint(event.getPoint());
					return getGrammarModel().getProduction(row).toString()
							+ " is production " + row;
				} catch (Throwable e) {
					return null;
				}
			}
		};
		return grammarTable;
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
		// panel.add(startButton, c);

		c.weightx = 0.0;
		c.gridwidth = 1;
		panel.add(new JLabel("Input Remaining"), c);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		inputDisplay.setEditable(false);
		panel.add(inputDisplay, c);

		c.weightx = 0.0;
		c.gridwidth = 1;
		panel.add(new JLabel("Stack"), c);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		stackDisplay.setEditable(false);
		panel.add(stackDisplay, c);

		bigger.add(panel, BorderLayout.CENTER);
		bigger.add(initInputToolbar(), BorderLayout.NORTH);

		return bigger;
	}

	/**
	 * Returns the choices for the view.
	 * 
	 * @return an array of strings for the choice of view
	 */
	protected String[] getViewChoices() {
		return new String[] { "Noninverted Tree", "Inverted Tree",
				"Derivation Table" };
	}

	/**
	 * Returns the tool bar for the main user input panel.
	 * 
	 * @return the tool bar for the main user input panel
	 */
	protected JToolBar initInputToolbar() {
		JToolBar toolbar = new JToolBar();
		toolbar.add(startAction);
		stepAction.setEnabled(false);
		toolbar.add(stepAction);

		// Set up the view customizer controls.
		toolbar.addSeparator();

		final JComboBox box = new JComboBox(getViewChoices());
		box.setSelectedIndex(0);
		ActionListener listener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changeView((String) box.getSelectedItem());
			}
		};
		box.addActionListener(listener);
		toolbar.add(box);
		return toolbar;
	}

	/**
	 * Changes the view.
	 * 
	 * @param name
	 *            the view button name that was pressed
	 */
	protected void changeView(String name) {
		if (name.equals("Noninverted Tree")) {
			treeDerivationLayout.first(treeDerivationPane);
			treeDrawer.setInverted(false);
			treePanel.repaint();
		} else if (name.equals("Inverted Tree")) {
			treeDerivationLayout.first(treeDerivationPane);
			treeDrawer.setInverted(true);
			treePanel.repaint();
		} else if (name.equals("Derivation Table")) {
			treeDerivationLayout.last(treeDerivationPane);
		}
	}

	/**
	 * Inits a parse table.
	 * 
	 * @return a table to hold the parse table
	 */
	protected abstract JTable initParseTable();

	/**
	 * Inits a new tree panel.
	 * 
	 * @return a new display for a parse tree
	 */
	protected JComponent initTreePanel() {
		treeDrawer.hideAll();
		treeDrawer.setNodePlacer(new LeafNodePlacer());
		return treePanel;
	}

	/**
	 * Inits a new derivation table.
	 * 
	 * @return a new display for the derivation of the parse
	 */
	protected JTable initDerivationTable() {
		JTable table = new JTable(derivationModel);
		table.setGridColor(Color.lightGray);
		return table;
	}

	/**
	 * This method is called when there is new input to parse.
	 * 
	 * @param string
	 *            a new input string
	 */
	protected abstract void input(String string);

	/**
	 * This method is called when the step button is pressed.
	 */
	protected abstract boolean step();

	/**
	 * Prints this component. This will print only the tree section of the
	 * component.
	 * 
	 * @param g
	 *            the graphics object to print to
	 */
	public void printComponent(Graphics g) {
		treeDerivationPane.print(g);
	}

	/**
	 * Children are not painted here.
	 * 
	 * @param g
	 *            the graphics object to paint to
	 */
	public void printChildren(Graphics g) {

	}

	/** The label that displays the remaining input. */
	JTextField inputDisplay = new JTextField();

	/** The label that displays the stack. */
	JTextField stackDisplay = new JTextField();

	/** The label that displays the current status of the parse. */
	JLabel statusDisplay = new JLabel("Input a string to begin.");

	/** The input text field. */
	public JTextField inputField = new JTextField();

	/** The grammar being displayed. */
	public Grammar grammar;

	/** The display for the grammar. */
	GrammarTable grammarTable;

	/** The environment. */
	GrammarEnvironment environment;

	/** The action for the stepping control. */
	public AbstractAction stepAction = new AbstractAction("Step") {
		public void actionPerformed(ActionEvent e) {
			step();
		}
	};

	/** The action for the start control. */
	AbstractAction startAction = new AbstractAction("Start") {
		public void actionPerformed(ActionEvent e) {
			input(inputField.getText());
		}
	};

	/** A default tree drawer. */
	DefaultTreeDrawer treeDrawer = new DefaultTreeDrawer(new DefaultTreeModel(
			new DefaultMutableTreeNode())) {
		protected Color getNodeColor(TreeNode node) {
			return node.isLeaf() ? LEAF : INNER;
		}

		private final Color INNER = new Color(100, 200, 120), LEAF = new Color(
				255, 255, 100);
	};

	/** A default tree display. */
	JComponent treePanel = new TreePanel(treeDrawer);

	/** The table model for the derivations. */
	DefaultTableModel derivationModel = new DefaultTableModel(new String[] {
			"Production", "Derivation" }, 0) {
		public boolean isCellEditable(int r, int c) {
			return false;
		}
	};

	/** The split views. */
	JSplitPane mainSplit, topSplit, bottomSplit;

	/** The card layout. */
	CardLayout treeDerivationLayout = new CardLayout();

	/** The derivation/parse tree view. */
	public JPanel treeDerivationPane = new JPanel(treeDerivationLayout);

	/** The derivation view. */
	JScrollPane derivationPane;
}
