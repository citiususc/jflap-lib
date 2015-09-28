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

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

/**
 * This class defines methods for producing transforms that will allow.
 * 
 * @author Thomas Finley
 */

public abstract class TransformFitter {
	/**
	 * This produces a transform whereby content within rectangle <I>rectDraw</I>
	 * will fit entirely within the area defined by rectangle <I>rectSpace</I>.
	 * 
	 * @param rectDraw
	 *            the rectangle defining the area painting commands will be sent
	 * @param rectSpace
	 *            the rec
	 */
	public static AffineTransform fit(Rectangle rectDraw, Rectangle rectSpace) {
		return new AffineTransform();
	}
}
