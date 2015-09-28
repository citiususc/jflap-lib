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





package edu.duke.cs.jflap.gui;

import java.awt.*;
import java.io.*;
import javax.swing.*;

/**
 * Handles uncaught exceptions and errors in the AWT thread. The goal of JFLAP's
 * code is, of course, to not have any uncaught exceptions at all. However,
 * inevitably mistakes happen, and when they do it is best if the users can be
 * made to give information of somewhat better quality than "it doesn't work!"
 * This will bring up a dialog box with the stack trace and any other relevant
 * information.
 * 
 * @author Thomas Finley
 */

public class ThrowableCatcher {
	/**
	 * Handles an exception uncaught by our code.
	 * 
	 * @param throwable
	 *            the throwable we are trying to catch
	 */
	public void handle(Throwable throwable) {
		String message = null;
		String report = null;
		try {
			// Read the error message.
			InputStream is = getClass().getResource(ERROR_LOCATION)
					.openStream();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));
			StringBuffer sb = new StringBuffer();
			String nextLine = null;
			while ((nextLine = reader.readLine()) != null)
				sb.append(nextLine);
			message = sb.toString();

			// Compose the report.
			StringWriter w = new StringWriter();
			PrintWriter writer = new PrintWriter(w);
			writer.println("PROPERTIES");
			System.getProperties().list(writer);
			writer.println("TRACE");
			throwable.printStackTrace(writer);
			writer.flush();
			w.flush();
			report = w.toString();
		} catch (Throwable e) {
			System.err.println("Could not display AWT error message.");
			throwable.printStackTrace(); // Not a total loss.
			return;
		}
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(new JLabel(message), BorderLayout.NORTH);
		JTextArea area = new JTextArea(report);
		area.setEditable(false);
		panel.add(new JScrollPane(area), BorderLayout.CENTER);
		panel.setPreferredSize(new Dimension(400, 400));
		JOptionPane.showMessageDialog(null, panel);
	}

	/** The location of the uncaught error message. */
	private static final String ERROR_LOCATION = "/DOCS/error.html";
}
