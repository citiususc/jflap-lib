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





package edu.duke.cs.jflap.gui.regular;

import java.awt.*;
import java.awt.event.*;
import java.lang.ref.*;
import javax.swing.*;
import javax.swing.event.*;
import edu.duke.cs.jflap.regular.*;

/**
 * The editor pane for a regular expression allows the user to change the
 * regular expression.
 * 
 * @author Thomas Finley
 */

public class EditorPane extends JPanel {
	/**
	 * Instantiates a new editor pane for a given regular expression.
	 * 
	 * @param expression
	 *            the regular expression
	 */
	public EditorPane(RegularExpression expression) {
		// super(new BorderLayout());
		this.expression = expression;
		field.setText(expression.asString());
		field.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				updateExpression();
			}
		});
		field.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {
				updateExpression();
			}

			public void removeUpdate(DocumentEvent e) {
				updateExpression();
			}

			public void changedUpdate(DocumentEvent e) {
				updateExpression();
			}
		});
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;

		add(new JLabel("Edit the regular expression below:"), c);
		add(field, c);
	}

	/**
	 * This is called when the regular expression should be updated to accord
	 * with the field.
	 */
	private void updateExpression() {
		expression.change(ref);
	}

	/** The regular expression. */
	private RegularExpression expression;

	/** The field where the expression is displayed and edited. */
	private JTextField field = new JTextField("");

	/**
	 * The expression change listener for a regular expression detects if there
	 * are changes in the environment, and if so, changes the display.
	 */
	private ExpressionChangeListener listener = new ExpressionChangeListener() {
		public void expressionChanged(ExpressionChangeEvent e) {
			field.setText(e.getExpression().asString());
		}
	};

	/** The reference object. */
	private Reference ref = new WeakReference(null) {
		public Object get() {
			return field.getText();
		}
	};
}
