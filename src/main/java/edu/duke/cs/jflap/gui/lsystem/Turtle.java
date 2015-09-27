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

import edu.duke.cs.jflap.gui.transform.Matrix;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.*;
import java.io.*;
import java.util.*;

/**
 * This represents the current context for rendering strings of symbols. Aside
 * from various methods.
 * 
 * @author Thomas Finley
 */

class Turtle implements Cloneable, Serializable {
	/**
	 * Instantiates a turtle.
	 */
	public Turtle() {
		parametersToNumbers = new HashMap();

		setDistance(15.0);
		setAngleChange(15.0);
		setHueChange(10.0);
		setLineWidth(1.0);
		setLineIncrement(1.0);

		updateBounds();
	}

	/**
	 * Instantiates a turtle with the settings of an existing turtle.
	 * 
	 * @param turtle
	 *            the turtle to copy
	 */
	public Turtle(Turtle turtle) {
		// The matrix.
		matrix = new Matrix(turtle.matrix);
		// The position settings.
		distance = turtle.distance;
		bounds = null; // Invalidate
		matrix.origin(position);

		// The line width variables.
		lineWidth = turtle.lineWidth;
		incrementWidth = turtle.incrementWidth;
		// The color settings.
		color = turtle.color;
		polygonColor = turtle.polygonColor;
		hueChange = turtle.hueChange;
		// The direction settings.
		angleChange = turtle.angleChange;
		// The parameters to the number values.
		parametersToNumbers = new HashMap(turtle.parametersToNumbers);
	}

	/**
	 * Updates the bounds to include the current position.
	 */
	private final void updateBounds() {
		try {
			bounds.add(position);
		} catch (NullPointerException e) {
			bounds = new Rectangle2D.Double(position.getX(), position.getY(),
					0.0, 0.0);
		}
	}

	/**
	 * Returns the bounds.
	 */
	public final Rectangle2D getBounds() {
		if (bounds == null)
			updateBounds();
		return bounds;
	}

	/**
	 * Given a turtle, this will update this turtle's bounds to include those of
	 * the bounds of the passed in turtle.
	 * 
	 * @param turtle
	 *            the turtle whose bounds we want to include
	 */
	public final void updateBounds(Turtle turtle) {
		bounds.add(turtle.bounds);
	}

	/**
	 * Creates a copy of the current turtle.
	 * 
	 * @return a copy of the current turtle
	 */
	public Object clone() {
		return new Turtle(this);
	}

	// METHODS RELATING TO DIRECTION

	/**
	 * Turns the turtle either left or right.
	 * 
	 * @param clockwise
	 *            if <CODE>true</CODE> this is a clockwise turn, and if <CODE>false</CODE>
	 *            this is a counter-clockwise turn
	 */
	public final void turn(boolean clockwise) {
		turn(clockwise ? -angleChange : angleChange);
	}

	/**
	 * Turns the turtle a specified number of degrees.
	 * 
	 * @param degrees
	 *            the amount counter-clockwise to turn the turtle
	 */
	public final void turn(double degrees) {
		matrix.yaw(degrees);
	}

	/**
	 * Pitches the turtle either down or up.
	 * 
	 * @param down
	 *            if <CODE>true</CODE> this is a down pitch and if <CODE>false</CODE>
	 *            this is an up pitch.
	 */
	public final void pitch(boolean down) {
		pitch(down ? angleChange : -angleChange);
	}

	/**
	 * Pitches the turtle the specified number of degrees.
	 * 
	 * @param degrees
	 *            the amount counter-clockwise to turn the turtle
	 */
	public final void pitch(double degrees) {
		matrix.pitch(degrees);
	}

	/**
	 * Rolls the turtle either to the right or left.
	 * 
	 * @param right
	 *            if <CODE>true</CODE> this is a right roll and if <CODE>false</CODE>
	 *            this is a left roll.
	 */
	public final void roll(boolean right) {
		roll(right ? -angleChange : angleChange);
	}

	/**
	 * Rolls the turtle the specified number of degrees to the left
	 * 
	 * @param degrees
	 *            the amount left to roll the turtle
	 */
	public final void roll(double degrees) {
		matrix.roll(degrees);
	}

	/**
	 * Returns the angle increment.
	 * 
	 * @return the angle increment
	 */
	public final double getAngleChange() {
		return angleChange;
	}

	/**
	 * Sets the angle increment.
	 * 
	 * @param change
	 *            the new angle change
	 */
	public final void setAngleChange(double change) {
		angleChange = Math.IEEEremainder(change, 360.0);
		parametersToNumbers.put("angle", new Double(change));
	}

	// METHODS RELATING TO POSITION

	/**
	 * Sets the new change in distance for moves.
	 * 
	 * @param distance
	 *            the new distance
	 */
	public final void setDistance(double distance) {
		this.distance = distance;
		parametersToNumbers.put("distance", new Double(distance));
	}

	/**
	 * Moves the turtle in the current direction of the angle.
	 * 
	 * @param distance
	 *            the distance to move forward (negative value is backward)
	 */
	public final void go(double distance) {
		matrix.translate(0.0, -distance, 0.0);
		matrix.origin(position);
	}

	/**
	 * Moves the turtle the default distance forward (or backward) specified in
	 * the <CODE>distance</CODE> field.
	 * 
	 * @param forward
	 *            will be <CODE>true</CODE> if the user wishes to move
	 *            forward, <CODE>false</CODE> if the user wishes to move
	 *            backward
	 */
	public final void go(boolean forward) {
		go(forward ? distance : -distance);
	}

	// / METHODS RELATING TO COLOR

	/**
	 * Returns a color name.
	 * 
	 * @param colorName
	 *            the name of the color to find, which should be a field of
	 *            <CODE>java.awt.Color</CODE> (e.g. "red", "black", etc)
	 * @return the named color, or <CODE>null</CODE> if the color could not be
	 *         found
	 * @see java.awt.Color
	 */
	public static Color colorForString(String colorName) {
		try {
			return (Color) Color.class.getField(colorName).get(null);
		} catch (NoSuchFieldException e) {
			// The field was not found.
		} catch (IllegalAccessException e) {
			// Cannot access it!
		} catch (NullPointerException e) {
			// This is actually an instance field of java.awt.Color.
		}
		// Maybe it's in the map?
		Color c = (Color) COLORS.get(colorName);
		if (c != null)
			return c;
		// Okay, maybe it's an interpretable color?
		try {
			StringTokenizer st = new StringTokenizer(colorName, ",");
			float c1 = Float.parseFloat(st.nextToken());
			float c2 = Float.parseFloat(st.nextToken());
			float c3 = Float.parseFloat(st.nextToken());
			if (c1 < 0f || c1 > 255f || c2 < 0f || c2 > 255f || c3 < 0f
					|| c3 > 255f)
				return null; // Out of range!
			if (c1 <= 1f && c2 <= 1f && c3 <= 1f) // A HSB color!
				return Color.getHSBColor(c1, c2, c3);
			// An RGB color!
			return new Color((int) c1, (int) c2, (int) c3);
		} catch (Throwable e) {
			// We ran into trouble in the formatting, so we can't do
			// anything with it...
		}
		return null;
	}

	/**
	 * Sets the draw color.
	 * 
	 * @param colorName
	 *            the name of the color to find
	 * @see #colorForString
	 * @throws IllegalArgumentException
	 *             if the color name could not be retrieved
	 */
	public final void setColor(String colorName) {
		Color c = colorForString(colorName);
		if (c == null)
			throw new IllegalArgumentException("No color named " + colorName
					+ " found!");
		setColor(c);
	}

	/**
	 * Sets the draw color.
	 * 
	 * @param color
	 *            the new color to change to
	 */
	public final void setColor(Color color) {
		this.color = color;
	}

	/**
	 * Returns the current draw color of the turtle.
	 * 
	 * @reutrn the current draw color of the turtle
	 */
	public final Color getColor() {
		return color;
	}

	/**
	 * Sets the polygon color.
	 * 
	 * @param colorName
	 *            the name of the color to find
	 * @see #colorForString
	 * @throws IllegalArgumentException
	 *             if the color name could not be retrieved
	 */
	public final void setPolygonColor(String colorName) {
		Color c = colorForString(colorName);
		if (c == null)
			throw new IllegalArgumentException("No color named " + colorName
					+ " found!");
		setPolygonColor(c);
	}

	/**
	 * Sets the polygon color.
	 * 
	 * @param color
	 *            the new color to change to
	 */
	public final void setPolygonColor(Color color) {
		this.polygonColor = color;
	}

	/**
	 * Returns the current polygon color of the turtle.
	 * 
	 * @reutrn the current polygon color of the turtle
	 */
	public final Color getPolygonColor() {
		return polygonColor;
	}

	/**
	 * Sets the hue angle change.
	 * 
	 * @param change
	 *            the value in degrees to change the hue angle
	 */
	public void setHueChange(double change) {
		hueChange = Math.IEEEremainder(change, 360.0);
		parametersToNumbers.put("hueChange", new Double(change));
	}

	/**
	 * Changes the current color's hue angle by the turtle's value.
	 * 
	 * @param increment
	 *            <CODE>true</CODE> if we want to progress, and <CODE>false</CODE>
	 *            if we want to regress
	 */
	public void changeHue(boolean increment) {
		changeHue(increment ? hueChange : -hueChange);
	}

	/**
	 * Changes the current color by the given hue angle.
	 * 
	 * @param change
	 *            the amount to change the hue angle by
	 */
	public void changeHue(double change) {
		float[] hsbvals = Color.RGBtoHSB(color.getRed(), color.getGreen(),
				color.getBlue(), null);
		hsbvals[0] += ((float) change) / 360f;
		setColor(Color.getHSBColor(hsbvals[0], hsbvals[1], hsbvals[2]));
	}

	/**
	 * Changes the current polygon color's hue angle by the turtle's value.
	 * 
	 * @param increment
	 *            <CODE>true</CODE> if we want to progress, and <CODE>false</CODE>
	 *            if we want to regress
	 */
	public void changePolygonHue(boolean increment) {
		changePolygonHue(increment ? hueChange : -hueChange);
	}

	/**
	 * Changes the current polygon color's hue angle by the given hue angle.
	 */
	public void changePolygonHue(double change) {
		float[] hsbvals = Color.RGBtoHSB(polygonColor.getRed(), polygonColor
				.getGreen(), polygonColor.getBlue(), null);
		hsbvals[0] += ((float) change) / 360f;
		setPolygonColor(Color.getHSBColor(hsbvals[0], hsbvals[1], hsbvals[2]));
	}

	// METHODS RELATING TO LINE WIDTH

	/**
	 * Changes the amount line width changes.
	 * 
	 * @param increment
	 *            the new line increment
	 */
	public final void setLineIncrement(double increment) {
		incrementWidth = increment;
		parametersToNumbers.put("lineIncrement", new Double(increment));
	}

	/**
	 * Changes the line width.
	 * 
	 * @param broaden
	 *            should be <CODE>true</CODE> if the user wants to add the
	 *            width increment to the width, or <CODE>false</CODE> if the
	 *            user wants to decrement the width increment from the width
	 */
	public final void changeLineWidth(boolean broaden) {
		changeLineWidth(broaden ? incrementWidth : -incrementWidth);
	}

	/**
	 * Changes the line width by the specified amount.
	 * 
	 * @param increment
	 *            the amount to add to the line width; if negative, naturally
	 *            the width shall decrease
	 */
	public final void changeLineWidth(double increment) {
		setLineWidth(lineWidth + increment);
		stroke = null;
	}

	/**
	 * Explicitly sets the line width.
	 * 
	 * @param width
	 *            the new line width
	 */
	public final void setLineWidth(double width) {
		lineWidth = width;
		parametersToNumbers.put("lineWidth", new Double(width));
		stroke = null;
	}

	/**
	 * Returns the line width of the turtle.
	 * 
	 * @return the line width of the turtle
	 */
	public final double getLineWidth() {
		return lineWidth;
	}

	/**
	 * Returns the current stroke object given the line width.
	 * 
	 * @return the current stroke object
	 */
	public final Stroke getStroke() {
		if (stroke == null)
			stroke = new BasicStroke((float) Math.max(0.0, getLineWidth()));
		return stroke;
	}

	/**
	 * Returns a string representation of this turtle.
	 * 
	 * @return a string representation of this turtle
	 */
	public final String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("{ " + super.toString());
		sb.append(", distance=" + distance);
		sb.append(", position=(" + position.getX() + "," + position.getY()
				+ ")");
		sb.append(", lineWidth=" + lineWidth);
		sb.append(", incrementWidth=" + incrementWidth);
		sb.append(", angleChange=" + angleChange);
		sb.append(", color=" + color);
		sb.append(", polygonColor=" + polygonColor);
		sb.append(" }");

		return sb.toString();
	}

	/**
	 * /** Given a string representing a mathematical expression, this returns
	 * the value of that expression.
	 * 
	 * @param string
	 *            the mathematical expression
	 * @return the value of the evaluation
	 */
	public Number valueOf(String string) {
		return valueOf(string, parametersToNumbers);
	}

	/**
	 * Given a string representing a mathematical expression, this returns the
	 * value of that expression. If there are any variables in the expression
	 * they should be contained within the map of values.
	 * 
	 * @param string
	 *            the mathematical expression
	 * @param values
	 *            the map of string objects to number objects
	 */
	private static Number valueOf(String string, Map values) {
		string = string.replaceAll("-", " -");
		StringReader reader = new StringReader(string);
		StreamTokenizer st = new StreamTokenizer(reader);
		st.ordinaryChar('/');
		ArrayList list = new ArrayList();
		Number zero = new Integer(0);
		boolean number = false;
		Character plus = new Character('+');

		try {
			while (st.nextToken() != StreamTokenizer.TT_EOF) {
				switch (st.ttype) {
				case StreamTokenizer.TT_WORD:
					// Attempt to resolve the symbol to a number.
					Number n = (Number) values.get(st.sval);
					if (number)
						list.add(plus);
					number = true;
					list.add(n == null ? zero : n);
					break;
				case StreamTokenizer.TT_NUMBER:
					if (number)
						list.add(plus);
					number = true;
					list.add(new Double(st.nval));
					break;
				case StreamTokenizer.TT_EOL:
					// Who cares?
					break;
				default:
					number = false;
					list.add(new Character((char) st.ttype));
					break;
				}
			}
		} catch (IOException e) {
			return new Double(Double.NaN); // We canna do it, captain!
		}
		// So now we have all these symbols in a list... great!
		Iterator it = list.iterator();
		return valueOf(it);
	}

	/**
	 * The recursive helper function for the <CODE>valueOf</CODE> function.
	 * 
	 * @param it
	 *            the iterator through operators and numbers
	 */
	private static Number valueOf(Iterator it) {
		Stack values = new Stack();
		Stack operators = new Stack();
		values.push(new Double(0.0));

		while (it.hasNext()) {
			Object o = it.next();
			if (o instanceof Number) {
				values.push(o);
				continue;
			}
			Character character = (Character) o;
			char c = character.charValue();
			if (c == ')')
				break; // Done!
			if (c == '(') {
				values.push(valueOf(it));
				continue;
			}
			while (!operators.isEmpty()) {
				boolean toCollapse = false;
				char last = ((Character) operators.peek()).charValue();
				switch (c) {
				case '+':
				case '-':
					if (last == '-' || last == '+')
						toCollapse = true;
				case '*':
				case '/':
					if (last == '*' || last == '/')
						toCollapse = true;
				case '^':
					if (last == '^')
						toCollapse = true;
					break;
				default:
					throw new IllegalArgumentException("Bad operator " + c);
				// Eh.
				}
				if (!toCollapse)
					break; // Let it be.
				// Collapse!
				double b = ((Number) values.pop()).doubleValue(), a = ((Number) values
						.pop()).doubleValue();
				operators.pop(); // Get rid of it...
				switch (last) {
				case '^':
					a = Math.pow(a, b);
					break;
				case '*':
					a *= b;
					break;
				case '/':
					a /= b;
					break;
				case '+':
					a += b;
					break;
				case '-':
					a -= b;
					break;
				default:
				// Eh.
				}
				values.push(new Double(a));
			}
			operators.push(character);
			continue;

		}
		// We've run out, or it's time to return. Do pending ops and leave.

		while (!operators.isEmpty()) {
			// Collapse!
			char last = ((Character) operators.pop()).charValue();
			double b = ((Number) values.pop()).doubleValue(), a = ((Number) values
					.pop()).doubleValue();
			switch (last) {
			case '^':
				a = Math.pow(a, b);
				break;
			case '*':
				a *= b;
				break;
			case '/':
				a /= b;
				break;
			case '+':
				a += b;
				break;
			case '-':
				a -= b;
				break;
			default:
			// Eh.
			}
			values.push(new Double(a));
		}
		return (Number) values.pop();
	}

	/**
	 * Assigns a value to a parameter from a mathematical expression which may
	 * include other parameters.
	 * 
	 * @param parameter
	 *            the parameter name
	 * @param expression
	 *            the mathematical expression
	 */
	public void assign(String parameter, String expression) {
		parametersToNumbers.put(parameter, valueOf(expression));
	}

	/**
	 * Returns the value for a parameter.
	 * 
	 * @param parameter
	 *            the parameter to get the value for
	 * @return the number for the parameter
	 */
	public Number get(String parameter) {
		return (Number) parametersToNumbers.get(parameter);
	}

	/** The distance to travel per time. */
	public double distance = 15;

	/** The current location. */
	public final Point2D position = new Point2D.Double(0.0, 0.0) {
		public void setLocation(double x, double y) {
			// This should ensure our bounds are always kept up
			// to date. Excellent...
			oldPosition.setLocation(this);
			super.setLocation(x, y);
			updateBounds();
		}
	};

	/** The old location. */
	public final Point2D oldPosition = new Point2D.Double();

	/** The current bounds that this turtle has travelled. */
	public Rectangle2D bounds = null;

	/** The line width. */
	public double lineWidth = 1.0;

	/** The amount the line changes on increment. */
	public double incrementWidth = 1.0;

	/** The current stroke object. */
	private Stroke stroke = null;

	/** The color for the L-system. */
	public Color color = Color.black;

	/** The polygon color for the L-system. */
	public Color polygonColor = Color.red;

	/** The hue angle change. */
	public double hueChange = 10.0;

	/** The amount the angle changes in degrees. */
	public double angleChange = 15.0;

	/** The mapping of string parameter names to numbers. */
	public Map parametersToNumbers;

	/**
	 * The current matrix. The translation of the origin into this matrix
	 * represents the current point.
	 */
	public Matrix matrix = new Matrix();

	/** The mapping of strings to special colors. */
	public static Map COLORS;

	static {
		Map m = new HashMap();
		m.put("dukeBlue", new Color(0, 0, 156));
		m.put("brown", new Color(129, 0, 0));
		m.put("oliveDrab", new Color(114, 93, 0));
		m.put("darkOliveGreen", new Color(109, 111, 0));
		m.put("orangeRed", new Color(252, 118, 0));
		m.put("maroon", new Color(190, 0, 0));
		m.put("forestGreen", new Color(0, 127, 0));
		m.put("purple", new Color(209, 0, 255));
		m.put("springGreen", new Color(193, 255, 157));
		m.put("violetRed", new Color(210, 0, 205));
		m.put("goldenrod", new Color(255, 214, 0));
		m.put("darkOliveGreen2", new Color(10, 127, 0));
		COLORS = Collections.unmodifiableMap(m);
	}
}
