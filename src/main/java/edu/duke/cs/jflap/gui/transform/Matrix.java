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





package edu.duke.cs.jflap.gui.transform;

import java.awt.geom.Point2D;
import java.io.Serializable;

/**
 * This is an affine transform matrix rather like the <CODE>AffineTransform</CODE>
 * of <CODE>jawa.awt.geom</CODE> fame, but for three dimensions. This type of
 * matrix does not support shearing or scaling.
 * 
 * @author Thomas Finley
 */

public class Matrix implements Cloneable, Serializable {
	/**
	 * Instantiates a new identity matrix.
	 */
	public Matrix() {
		this(1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0);
	}

	/**
	 * Instantiates a new matrix with the given entries. Twelve entries are
	 * given as parameters, as if reading the entries of the matrix off from
	 * left to right, and then from top to bottom.
	 */
	public Matrix(double a11, double a12, double a13, double a14, double a21,
			double a22, double a23, double a24, double a31, double a32,
			double a33, double a34) {
		entry = new double[][] { { a11, a12, a13, a14 },
				{ a21, a22, a23, a24 }, { a31, a32, a33, a34 },
				{ 0.0, 0.0, 0.0, 1.0 } };
	}

	/**
	 * Instantiates a copy of the passed in matrix.
	 * 
	 * @param m
	 *            the matrix to copy
	 */
	public Matrix(Matrix m) {
		this(m.entry[0][0], m.entry[0][1], m.entry[0][2], m.entry[0][3],
				m.entry[1][0], m.entry[1][1], m.entry[1][2], m.entry[1][3],
				m.entry[2][0], m.entry[2][1], m.entry[2][2], m.entry[2][3]);
	}

	/**
	 * Returns a copy of this object.
	 * 
	 * @return a copy of this matrix
	 */
	public Object clone() {
		return new Matrix(this);
	}

	/**
	 * Returns the entry at the given entry.
	 * 
	 * @param row
	 *            the row index, must be 0 through 3
	 * @param column
	 *            the column index, must be 0 through 3
	 * @return the entry at the given row and column
	 */
	public final double valueAt(int row, int column) {
		return entry[row][column];
	}

	/**
	 * Given another matrix, this will premultiply that matrix times this
	 * matrix, and store the result in this matrix. If <I>A</I> is this matrix
	 * and <I>B</I> is the matrix passed in as a parameter, then this is
	 * similar to <I>A = BA</I>.
	 * 
	 * @param matrix
	 *            the matrix to premultiply
	 */
	public final void premultiply(Matrix matrix) {
		entry2[3][0] = entry[3][1] = entry[3][2] = 0.0;
		entry2[3][3] = 1.0;

		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 4; j++) {
				entry2[i][j] = 0.0;
				for (int k = 0; k < 4; k++)
					entry2[i][j] += matrix.entry[i][k] * entry[k][j];
			}
		// Swap!
		double[][] oldentry = entry;
		entry = entry2;
		entry2 = oldentry;
	}

	/**
	 * Given another matrix, this will premultiply that matrix times this
	 * matrix, and store the result in this matrix. If <I>B</I> is this matrix
	 * and <I>A</I> is the matrix passed in as a parameter, then this is
	 * similar to <I>B = BA</I>.
	 * 
	 * @param matrix
	 *            the matrix to premultiply
	 */
	public final void postmultiply(Matrix matrix) {
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++) {
				entry2[i][j] = 0.0;
				for (int k = 0; k < 4; k++)
					entry2[i][j] += entry[i][k] * matrix.entry[k][j];
			}
		// Swap!
		double[][] oldentry = entry;
		entry = entry2;
		entry2 = oldentry;
	}

	/**
	 * Turns the current matrix about the X-axis.
	 * 
	 * @param angle
	 *            the angle to turn
	 */
	public final void pitch(double angle) {
		if (XAXIS_ANGLE == -angle) {
			XAXIS_ANGLE = angle;
			XAXIS_TURN.entry[1][2] = -XAXIS_TURN.entry[1][2];
			XAXIS_TURN.entry[2][1] = -XAXIS_TURN.entry[2][1];
		} else if (XAXIS_ANGLE != angle) {
			// Cache it!
			XAXIS_ANGLE = angle;
			angle = Math.toRadians(angle);
			double c = Math.cos(angle), s = Math.sin(angle);
			XAXIS_TURN = new Matrix(1.0, 0.0, 0.0, 0.0, 0.0, c, -s, 0.0, 0.0,
					s, c, 0.0);
		}
		premultiply(XAXIS_TURN);
	}

	/**
	 * Turns the current matrix about the Y-axis.
	 * 
	 * @param angle
	 *            the angle to turn
	 */
	public final void roll(double angle) {
		if (YAXIS_ANGLE == -angle) {
			YAXIS_ANGLE = angle;
			YAXIS_TURN.entry[0][2] = -YAXIS_TURN.entry[0][2];
			YAXIS_TURN.entry[2][0] = -YAXIS_TURN.entry[2][0];
		} else if (YAXIS_ANGLE != angle) {
			// Cache it!
			YAXIS_ANGLE = angle;
			angle = Math.toRadians(angle);
			double c = Math.cos(angle), s = Math.sin(angle);
			YAXIS_TURN = new Matrix(c, 0.0, s, 0.0, 0.0, 1.0, 0.0, 0.0, -s,
					0.0, c, 0.0);
		}
		premultiply(YAXIS_TURN);
	}

	/**
	 * Turns the current matrix about the Z-axis.
	 * 
	 * @param angle
	 *            the angle to turn
	 */
	public final void yaw(double angle) {
		if (ZAXIS_ANGLE == -angle) {
			ZAXIS_ANGLE = angle;
			ZAXIS_TURN.entry[0][1] = -ZAXIS_TURN.entry[0][1];
			ZAXIS_TURN.entry[1][0] = -ZAXIS_TURN.entry[1][0];
		} else if (ZAXIS_ANGLE != angle) {
			// Cache it!
			ZAXIS_ANGLE = angle;
			angle = Math.toRadians(angle);
			double c = Math.cos(angle), s = Math.sin(angle);
			ZAXIS_TURN = new Matrix(c, -s, 0.0, 0.0, s, c, 0.0, 0.0, 0.0, 0.0,
					1.0, 0.0);
		}
		premultiply(ZAXIS_TURN);
	}

	/**
	 * Translates the current matrix
	 */
	public final void translate(double x, double y, double z) {
		if (DIRS[0] != x || DIRS[1] != y || DIRS[2] != z) {
			// Cache it!
			DIRS[0] = TRANSLATE.entry[0][3] = x;
			DIRS[1] = TRANSLATE.entry[1][3] = y;
			DIRS[2] = TRANSLATE.entry[2][3] = z;
		}
		premultiply(TRANSLATE);
	}

	/**
	 * Returns the point (the x and y coordinates) of the transformed origin.
	 * 
	 * @param point
	 *            the point to store the location in
	 * @return either the point passed in or, if that point was null, a newly
	 *         allocated point
	 */
	public final Point2D origin(Point2D point) {
		if (point == null)
			point = new Point2D.Double();
		origin(ORIGIN_REUSE);
		point.setLocation(ORIGIN_REUSE[0], ORIGIN_REUSE[1]);
		return point;
	}

	/**
	 * Returns the x, y, and z coordinates of the transformed origin.
	 * 
	 * @param array
	 *            the array of three entries which will hold, in order, the <I>x</I>,
	 *            <I>y</I>, and <I>z</I> coordinates, or null if you wish a
	 *            newly allocated array
	 * @return the array
	 */
	public final double[] origin(double[] array) {
		if (array == null)
			array = new double[3];
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				INVERSE.entry[i][j] = entry[j][i];
		premultiply(INVERSE);
		for (int i = 0; i < 3; i++)
			array[i] = entry[i][3];
		// Swap it back!
		double[][] old = entry;
		entry = entry2;
		entry2 = old;
		// Yay.
		return array;
	}

	/**
	 * Returns a string representation of this matrix.
	 * 
	 * @return a string representation of this matrix
	 */
	public final String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append('(');
		for (int i = 0; i < 4; i++) {
			if (i != 0)
				sb.append(";  ");
			for (int j = 0; j < 4; j++) {
				if (j != 0)
					sb.append(',');
				sb.append(' ');
				sb.append(entry[i][j]);
			}
		}
		sb.append(" )");
		return sb.toString();
	}

	/**
	 * The entries, where the first index in the array is the row, and the
	 * second index is the column.
	 */
	public double[][] entry;

	/** The backup entries. */
	private double[][] entry2 = new double[4][4];

	/** The old angles for each of the cached turn matrices. */
	private static double XAXIS_ANGLE = Double.NaN, YAXIS_ANGLE = Double.NaN,
			ZAXIS_ANGLE = Double.NaN;

	/** The cached matrices for turning. */
	private static Matrix XAXIS_TURN, YAXIS_TURN, ZAXIS_TURN;

	/** The old distances for each of the cache translation matrices. */
	private static double[] DIRS = new double[] { 0.0, 0.0, 0.0 };

	/** The old translation matrix. */
	private static final Matrix TRANSLATE = new Matrix();

	/**
	 * The old matrix used for computing inversions back to user space...
	 */
	private static final Matrix INVERSE = new Matrix();

	/**
	 * Used for the origin methods, so new arrays needn't constantly be
	 * allocated.
	 */
	private static final double[] ORIGIN_REUSE = new double[3];

	public static final String arrayString(double[] d) {
		return "( " + d[0] + ", " + d[1] + ", " + d[2] + " )";
	}
}
