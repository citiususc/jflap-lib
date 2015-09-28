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

import java.net.URL;
import java.io.IOException;
import javax.swing.*;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;
import javax.swing.text.html.HTMLDocument;

/**
 * The <TT>WebFrame</TT> class provides a simple method for poping up a
 * miniature web browser that has only back, forward, and home buttons, and no
 * URL entry form.
 * 
 * @author Justin Cross
 * @author Thomas Finley
 */

/*
 * This code was modified considerably from some freely distributed code kindly
 * provided by Justin Cross during the spring 2001 semester of the CPS 108
 * class.
 */

public class WebFrame extends JFrame {
	/**
	 * This constructs a new <TT>WebFrame</TT> that initializes its display to
	 * the location shown.
	 * 
	 * @param myHtmlFile
	 *            the URL to load, either in the form of a web page (starting
	 *            with `http') or some sort of file (starting, I suppose,
	 *            without the `http').
	 */
	public WebFrame(String myHtmlFile) {
		setTitle("Help Browser");
		JPanel mainpanel = new JPanel(new BorderLayout());

		Hyperactive hyper = new Hyperactive();
		myBrowserDisplay.setEditable(false);
		myBrowserDisplay.addHyperlinkListener(hyper);
		JScrollPane htmlscrollpane = new JScrollPane(myBrowserDisplay);
		mainpanel.add(htmlscrollpane, BorderLayout.CENTER);
		mainpanel.add(getToolBar(), BorderLayout.NORTH);

		String url = myHtmlFile;
		if (!myHtmlFile.startsWith("http://")) {
			URL u = this.getClass().getResource(myHtmlFile);
			url = u == null ? "" : u.toString();
		}
		setContentPane(mainpanel);
		pack();
		setSize(600, 700);
		setLocation(50, 50);
		setDefaultCloseOperation(HIDE_ON_CLOSE);

		goNew(url);
	}

	/**
	 * Goes to a particular page.
	 * 
	 * @param url
	 *            the url to go to
	 */
	public void gotoURL(String url) {
		if (!url.startsWith("http://")) {
			URL u = this.getClass().getResource(url);
			url = u == null ? "" : u.toString();
		}
		goNew(url);
	}

	/**
	 * Returns the toolbar for browsing this help display.
	 * 
	 * @return the toolbar for browsing this help display
	 */
	private JToolBar getToolBar() {
		JToolBar toReturn = new JToolBar();
		toReturn.setFloatable(false);
		myBackButton = makeButton("Back", "left.gif", new BackAction(), null);
		myForwardButton = makeButton("Forward", "right.gif",
				new ForwardAction(), null);
		myStartButton = makeButton("Main Index", "start.gif", new HomeAction(),
				null);
		toReturn.add(myBackButton);
		toReturn.add(myForwardButton);
		toReturn.add(myStartButton);
		return toReturn;
	}

	/**
	 * Makes a web browser button with the specified attributes.
	 * 
	 * @param label
	 *            the label on the button
	 * @param iconName
	 *            the icon name for the button in the web browser icon directory
	 * @param listener
	 *            the action listener for the button
	 * @param tooltip
	 *            the tool tip for the button
	 */
	private JButton makeButton(String label, String iconName,
			ActionListener listener, String tooltip) {
		ImageIcon icon = new ImageIcon(getClass().getResource(
				"/ICON/web/" + iconName));
		JButton button = new JButton(label, icon);
		button.addActionListener(listener);
		button.setToolTipText(tooltip);
		return button;
	}

	/**
	 * Goes to the previous address in the history.
	 */
	private void goBack() {
		try {
			myCurrentPosition--;
			String url = (String) myURLHistory.get(myCurrentPosition);
			setDisplay(url);
		} catch (Throwable e) {
			myCurrentPosition++;
		}
	}

	/**
	 * Goes to the start address.
	 */
	private void goHome() {
		int oldIndex = myCurrentPosition;
		try {
			myCurrentPosition = 0;
			String url = (String) myURLHistory.get(myCurrentPosition);
			setDisplay(url);
		} catch (Throwable e) {
			myCurrentPosition = oldIndex;
		}
	}

	/**
	 * Goes to the next address in the history.
	 */
	private void goForward() {
		try {
			myCurrentPosition++;
			String url = (String) myURLHistory.get(myCurrentPosition);
			setDisplay(url);
		} catch (Throwable e) {
			myCurrentPosition--;
		}
	}

	/**
	 * Sets the display of the browser to the URL.
	 * 
	 * @param url
	 *            the name of the url
	 */
	private void setDisplay(String url) {
		try {
			myBrowserDisplay.setPage(url);
		} catch (IOException e) {
			// Display an alert to that effect.
			System.err.println(e);
			JOptionPane.showMessageDialog(this, "Could not access URL " + url
					+ "!", "Web Error", JOptionPane.ERROR_MESSAGE);
			myURLHistory.remove(myCurrentPosition);
			myCurrentPosition--;
		}
		setEnabledStates();
	}

	/**
	 * Sets the display of the browser to the url
	 * 
	 * @param url
	 *            the name of the url
	 */
	private void setDisplay(URL url) {
		try {
			myBrowserDisplay.setPage(url);
		} catch (IOException e) {
			// Display an alert to that effect.
			System.err.println(e);
			JOptionPane.showMessageDialog(this, "Could not access URL " + url
					+ "!", "Web Error", JOptionPane.ERROR_MESSAGE);
			myURLHistory.remove(myCurrentPosition);
			myCurrentPosition--;
		}
		setEnabledStates();
	}

	/**
	 * Go to a completely new page, clearing all visited history past this
	 * point.
	 * 
	 * @param url
	 *            the new url to go to
	 */
	private void goNew(String url) {
		myCurrentPosition++;
		try {
			while (true) {
				String removed = (String) myURLHistory
						.remove(myCurrentPosition);
			}
		} catch (Throwable e) {

		}
		myURLHistory.add(url);
		setDisplay(url);
	}

	/**
	 * Sets the enabled states of the browsing buttons.
	 */
	private void setEnabledStates() {
		myBackButton.setEnabled(myCurrentPosition != 0);
		myStartButton.setEnabled(myCurrentPosition != 0);
		myForwardButton
				.setEnabled(myCurrentPosition != myURLHistory.size() - 1);
	}

	/**
	 * This listener listenens for hyperlink clicks, and updates the frame to
	 * the new contents.
	 */
	public class Hyperactive implements HyperlinkListener {
		public void hyperlinkUpdate(HyperlinkEvent e) {
			if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
				JEditorPane pane = (JEditorPane) e.getSource();
				if (e instanceof HTMLFrameHyperlinkEvent) {
					HTMLFrameHyperlinkEvent evt = (HTMLFrameHyperlinkEvent) e;
					HTMLDocument doc = (HTMLDocument) pane.getDocument();
					doc.processHTMLFrameHyperlinkEvent(evt);
				} else {
					try {
						goNew(e.getURL().toString());
					} catch (Throwable t) {
						t.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * An action to browse back.
	 */
	private class BackAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			goBack();
		}
	}

	/**
	 * An action to browse forward.
	 */
	private class ForwardAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			goForward();
		}
	}

	/**
	 * An action to browse back to the start.
	 */
	private class HomeAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			goHome();
		}
	}

	/** The JEditorPane display. */
	private JEditorPane myBrowserDisplay = new JEditorPane();

	/** The vector that holds old addresses. */
	private ArrayList myURLHistory = new ArrayList();

	/** Our current position in the vector of addresses. */
	private int myCurrentPosition = -1;

	/** The back button. */
	private JButton myBackButton;

	/** The forward button. */
	private JButton myForwardButton;

	/** The start button. */
	private JButton myStartButton;
}
