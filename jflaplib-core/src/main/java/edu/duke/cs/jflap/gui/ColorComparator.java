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

import java.awt.Color;
import java.util.Comparator;

/**
 * This imposes a natural ordering on colors. First colors are ordered by the
 * transparency (more opaque comes first), then by saturation (more colorful
 * comes first), then by brightness (lighter comes first) then by hue (order
 * proceeds from red, to green, to blue).
 * <P>
 * This can be used to order colors in such a way that they have a nice
 * "rainbowness" to them, but also so that less vivid colors are segregated from
 * the more vivid colors.
 * 
 * @author Thomas Finley
 */

public class ColorComparator implements Comparator {
	/**
	 * Imposes the ordering on colors, first by alpha, then by saturation, then
	 * by brightness, then by hue.
	 * 
	 * @param o1
	 *            the first color
	 * @param o2
	 *            the second color
	 * @return a negative quantity if <TT>o1</TT> is ordered before <TT>o2</TT>,
	 *         0 if they're equal, and positive otherwise.
	 */
	public int compare(Object o1, Object o2) {
		Color first = (Color) o1;
		Color second = (Color) o2;
		if (first.getAlpha() != second.getAlpha())
			return (second.getAlpha() - first.getAlpha());
		// Extract the HSB, and impose the ordering.
		float[] firstHSB = Color.RGBtoHSB(first.getRed(), first.getGreen(),
				first.getBlue(), null);
		float[] secondHSB = Color.RGBtoHSB(second.getRed(), second.getGreen(),
				second.getBlue(), null);
		int[] comp = new int[3];
		// First saturation...
		comp[0] = -compareFloat(firstHSB[1], secondHSB[1]);
		// Then brightness...
		comp[1] = -compareFloat(firstHSB[2], secondHSB[2]);
		// Then hue...
		comp[2] = compareFloat(firstHSB[0], secondHSB[0]);

		// Run through the comparisons, return if not zero.
		for (int i = 0; i < 3; i++)
			if (comp[i] != 0)
				return comp[i];
		return 0;
	}

	/**
	 * Small utility for making comparing floats easier.
	 * 
	 * @param float1
	 *            the first float
	 * @param float2
	 *            the second float
	 * @return -1 if float1 is smaller, 0 if they're equal, 1 if float1 is
	 *         larger
	 */
	private int compareFloat(float float1, float float2) {
		if (float1 > float2)
			return 1;
		if (float2 > float1)
			return -1;
		return 0;
	}

	/**
	 * Compares this comparator against another for equality.
	 * 
	 * @return <TT>true</TT>> if the passed in object is equal to this
	 *         comparator.
	 */
	public boolean equals(Object object) {
		try {
			ColorComparator comparator = (ColorComparator) object;
		} catch (ClassCastException e) {
			return false;
		}
		return true;
	}

	/**
	 * Some very simple test code for the color comparator.
	 */
	public static void main(String args[]) {
		Color first = Color.black;
		Color second = Color.red;
		Comparator comp = new ColorComparator();
		// //System.out.println(comp.compare(first, second));
	}
}
