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
import java.net.URL;
import javax.swing.*;

/**
 * The <CODE>ImageDisplayComponent</CODE> is a single component that displays
 * an image within itself, and sets its size to match that of the image.
 * 
 * @author Thomas Finley
 */

public class ImageDisplayComponent extends JComponent implements Scrollable {
	/**
	 * Instantiates a new <CODE>ImageDisplayComponent</CODE> without an image
	 * at this time.
	 */
	public ImageDisplayComponent() {
		this((Image) null);
	}

	/**
	 * Instantiates a new <CODE>ImageDisplayComponent</CODE>.
	 * 
	 * @param image
	 *            the new image for this component
	 */
	public ImageDisplayComponent(Image image) {
		setImage(image);
	}

	/**
	 * Instantiates a new <CODE>ImageDisplayComponent</CODE>.
	 * 
	 * @param url
	 *            the new image for this component
	 */
	public ImageDisplayComponent(URL url) {
		setImage(getBaseImage(url));
	}

	/**
	 * Instantiates a new <CODE>ImageDisplayComponent</CODE>.
	 * 
	 * @param filename
	 *            the path where to find the new image for this component
	 */
	public ImageDisplayComponent(String filename) {
		setImage(getBaseImage(filename));
	}

	/**
	 * Returns the image associated with this component.
	 * 
	 * @return the image associated with this component
	 */
	public Image getImage() {
		return myImage;
	}

	/**
	 * Sets the image associated with this component.
	 * 
	 * @param image
	 *            the new image for this component
	 * @param origin
	 *            the point in the image that is the real "origin" of the image
	 *            (should be 0,0 in most cases)
	 */
	public void setImage(Image image, Point origin) {
		myImage = image;
		myOrigin = origin;
		trackImage(getImage());
		redefineSize();
	}

	/**
	 * Sets the image associated with this component, with (0,0) set as the
	 * origin.
	 * 
	 * @param image
	 *            the new image for this component
	 */
	public void setImage(Image image) {
		setImage(image, ZERO_ORIGIN);
	}

	/**
	 * Returns an untracked unloaded base image based on a path name.
	 * 
	 * @param path
	 *            the path name
	 */
	private Image getBaseImage(String path) {
		return Toolkit.getDefaultToolkit().getImage(path);
	}

	/**
	 * Returns an untracked unloaded base image based on a URL.
	 * 
	 * @param url
	 *            the url for the image
	 */
	private Image getBaseImage(URL url) {
		return Toolkit.getDefaultToolkit().getImage(url);
	}

	/**
	 * When this method returns, the image is sure to be fully loaded.
	 * 
	 * @param image
	 *            the image to make sure is loaded
	 * @return <TT>true</TT> if the tracking succeeded, <TT>false</TT> if it
	 *         was interrupted
	 */
	private boolean trackImage(Image image) {
		if (image == null)
			return true; // Why not...
		MediaTracker tracker = new MediaTracker(this);
		tracker.addImage(image, 0);
		try {
			tracker.waitForID(0);
		} catch (InterruptedException e) {
			return false;
		}
		return true;
	}

	/**
	 * Based on the image of this component, sets the preferred size.
	 */
	private void redefineSize() {
		Dimension d = new Dimension(1, 1);
		if (myImage != null) {
			d = new Dimension(getImage().getWidth(this), getImage().getHeight(
					this));
		}
		setPreferredSize(d);
		setBounds(-myOrigin.x, -myOrigin.y, d.width, d.height);
	}

	/**
	 * Paints this component.
	 * 
	 * @param g
	 *            the graphics object to paint upon
	 */
	public void paintComponent(Graphics g) {
		if (myImage == null)
			return;

		Rectangle r = getVisibleRect(), r2 = new Rectangle(getPreferredSize());
		int offsetx = r.width > r2.width ? (r.width - r2.width) / 2 : 0;
		int offsety = r.height > r2.height ? (r.height - r2.height) / 2 : 0;
		r = r.intersection(r2);
		g.drawImage(myImage, r.x + offsetx, r.y + offsety, r.x + r.width
				+ offsetx, r.y + r.height + offsety, r.x, r.y, r.x + r.width,
				r.y + r.height, this);
	}

	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}

	public int getScrollableBlockIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		return orientation == SwingConstants.VERTICAL ? visibleRect.height
				: visibleRect.width;
	}

	public int getScrollableUnitIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		return 5;
	}

	/**
	 * We want this view sized so that it is either larger than or just as wide
	 * as the containing scroll pane.
	 */
	public boolean getScrollableTracksViewportWidth() {
		return getPreferredSize().width < getParent().getSize().width;
	}

	/**
	 * We want this view sized so that it is either larger than or just as tall
	 * as the containing scroll pane.
	 */
	public boolean getScrollableTracksViewportHeight() {
		return getPreferredSize().height < getParent().getSize().height;
	}

	/** The image to display. */
	private Image myImage;

	/** The origin. */
	private Point myOrigin;

	/** Default (0,0) instance of the origin. */
	private static final Point ZERO_ORIGIN = new Point(0, 0);
}
