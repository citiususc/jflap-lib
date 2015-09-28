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





package edu.duke.cs.jflap.gui.lsystem;

import edu.duke.cs.jflap.grammar.lsystem.Expander;
import edu.duke.cs.jflap.grammar.lsystem.LSystem;
import edu.duke.cs.jflap.gui.ImageDisplayComponent;
import edu.duke.cs.jflap.gui.transform.Matrix;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * The L-system display pane has the interface to display an L-system.
 * 
 * @author Thomas Finley
 */

public class DisplayPane extends JPanel {
	/**
	 * Implements a display pane.
	 * 
	 * @param lsystem
	 *            the L-system to display
	 */
	public DisplayPane(LSystem lsystem) {
		super(new BorderLayout());
		this.lsystem = lsystem;

		expander = new Expander(lsystem);
		// We can't edit the expansion, of course.
		expansionDisplay.setEditable(false);
		// The user has to be able to change the recursion depth.
		JSpinner spinner = new JSpinner(spinnerModel);
		spinnerModel.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				updateDisplay();
			}
		});
		// Now, for the angle at which the damn thing is viewed...
		JSpinner s1 = new JSpinner(pitchModel), s2 = new JSpinner(rollModel), s3 = new JSpinner(
				yawModel);
		ChangeListener c = new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				updateDisplay();
				// displayAction.setEnabled(true);
			}
		};
		pitchModel.addChangeListener(c);
		rollModel.addChangeListener(c);
		yawModel.addChangeListener(c);
		
		// Lay out the component.
		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.add(spinner, BorderLayout.EAST);
		topPanel.add(expansionDisplay, BorderLayout.CENTER);
		topPanel.add(progressBar, BorderLayout.WEST);
		add(topPanel, BorderLayout.NORTH);
		JPanel bottomPanel = new JPanel();
		bottomPanel.add(new JLabel("Pitch"));
		bottomPanel.add(s1);
		bottomPanel.add(new JLabel("Roll"));
		bottomPanel.add(s2);
		bottomPanel.add(new JLabel("Yaw"));
		bottomPanel.add(s3);
		//bottomPanel.setBackground(Color.WHITE);
		JScrollPane scroller = new JScrollPane(imageDisplay);
		add(scroller, BorderLayout.CENTER);
		add(bottomPanel, BorderLayout.SOUTH);
		// Finally, set the initial display.
		updateDisplay();
	}

	/**
	 * Updates the display.Graphics2D;
	 */
	private void updateDisplay() {
		int recursionDepth = spinnerModel.getNumber().intValue();
		final List expansion = expander.expansionForLevel(recursionDepth);
		progressBar.setMaximum(expansion.size() * 2);
		imageDisplay.setImage(null);
		Image renderImage = null;

		final javax.swing.Timer t = new javax.swing.Timer(30,
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						int i = renderer.getDoneSymbols() - 1;
						progressBar.setValue(i);
						progressBar.repaint();
					}
				});

		final Thread drawThread = new Thread() {
			public void run() {
				if (expansion.size() < 70) {
					String expansionString = LSystemInputPane
							.listAsString(expansion);
					expansionDisplay.setText(expansionString);
				} else
					expansionDisplay.setText("Suffice to say, quite long.");
				// Now, set the display.
				Map parameters = lsystem.getValues();

				t.start();
				Matrix m = new Matrix();
				double pitch = pitchModel.getNumber().doubleValue(), roll = rollModel
						.getNumber().doubleValue(), yaw = yawModel.getNumber()
						.doubleValue();
				m.pitch(pitch);
				m.roll(roll);
				m.yaw(yaw);
				Point origin = new Point(); // Ignored, for now.
				Image image = renderer.render(expansion, parameters, m, null,
						origin);
				imageDisplay.setImage(image);
				t.stop();
				imageDisplay.repaint();
				imageDisplay.revalidate();
				progressBar.setValue(progressBar.getMaximum());
			}
		};
		drawThread.start();
	}

	/**
	 * Prints the current displayed L-system.
	 * 
	 * @param g
	 *            the graphics interface for the printer device
	 */
	public void printComponent(Graphics g) {
		int recursionDepth = spinnerModel.getNumber().intValue();
		List expansion = expander.expansionForLevel(recursionDepth);
		// Now, set the display.
		Map parameters = lsystem.getValues();
		Matrix m = new Matrix();
		double pitch = pitchModel.getNumber().doubleValue(), roll = rollModel
				.getNumber().doubleValue(), yaw = yawModel.getNumber()
				.doubleValue();
		m.pitch(pitch);
		m.roll(roll);
		m.yaw(yaw);
		renderer.render(expansion, parameters, m, (Graphics2D) g, new Point());
	}

	/**
	 * Children are not painted here.
	 * 
	 * @param g
	 *            the graphics object to paint to
	 */
	public void printChildren(Graphics g) {

	}

	/** The L-system we are displaying here. */
	private LSystem lsystem;

	/** The current expander. */
	private Expander expander = null;

	/** The renderer. */
	private Renderer renderer = new Renderer();

	/** The image display component. */
	private ImageDisplayComponent imageDisplay = new ImageDisplayComponent();

	/** The spinner model. */
	private SpinnerNumberModel spinnerModel = new SpinnerNumberModel(0, 0, 200,
			1);

	/** The text field which displays the expansion. */
	private JTextField expansionDisplay = new JTextField();

	/** The progress indicator. */
	private JProgressBar progressBar = new JProgressBar(0, 1);

	/** The action for redisplaying. */
	private Action displayAction = new AbstractAction("Redisplay") {
		public void actionPerformed(ActionEvent e) {
			updateDisplay();
			displayAction.setEnabled(false);
		}
	};

	/** The spinner models for the transforms. */
	private SpinnerNumberModel pitchModel = new SpinnerNumberModel(0, 0, 359,
			15), rollModel = new SpinnerNumberModel(0, 0, 359, 15),
			yawModel = new SpinnerNumberModel(0, 0, 359, 15);

}
