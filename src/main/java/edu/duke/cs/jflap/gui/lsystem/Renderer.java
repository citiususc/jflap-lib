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

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

/**
 * A <CODE>Renderer</CODE> object allows a client to create an image of a
 * string of symbols generated, presumably, from an <CODE>LSystem</CODE>.
 * <P>
 * 
 * The following symbols have significance:
 * 
 * @see edu.duke.cs.jflap.grammar.lsystem.Expander
 * @see edu.duke.cs.jflap.grammar.lsystem.LSystem
 * 
 * @author Thomas Finley
 */

public class Renderer {
	/**
	 * Instantiates a renderer object.
	 */
	public Renderer() {
		// Set up all them gosh durned command handlers.
		handlers.put("g", new MoveHandler(true, true));
		handlers.put("f", new MoveHandler(false, true));
		handlers.put("+", new TurnHandler(true));
		handlers.put("-", new TurnHandler(false));
		handlers.put("&", new PitchHandler(true));
		handlers.put("^", new PitchHandler(false));
		handlers.put("/", new RollHandler(true));
		handlers.put("*", new RollHandler(false));
		handlers.put("[", new PushTurtleHandler());
		handlers.put("]", new PopTurtleHandler());
		handlers.put("!", new WidthChangeHandler(true));
		handlers.put("~", new WidthChangeHandler(false));
		handlers.put("{", new BeginPolygonHandler());
		handlers.put("}", new ClosePolygonHandler());
		handlers.put("%", new ReverseHandler());
		handlers.put("#", new HueChangeHandler(false, true));
		handlers.put("@", new HueChangeHandler(false, false));
		handlers.put("##", new HueChangeHandler(true, true));
		handlers.put("@@", new HueChangeHandler(true, false));

		// Not to mention the fucking assignment handlers... Jesus Christ.
		handlers.put("color", new DrawColorHandler());
		handlers.put("polygonColor", new PolygonColorHandler());
		CommandHandler angleIncrement = new AngleIncrementHandler();
		handlers.put("angle", angleIncrement);
		handlers.put("angleIncrement", angleIncrement);
		handlers.put("lineWidth", new LineWidthHandler());
		handlers.put("lineIncrement", new LineWidthIncrementHandler());
		handlers.put("distance", new DistanceHandler());
		handlers.put("hueChange", new HueAngleIncrementHandler());
	}

	/**
	 * Returns the command handler for a symbol.
	 * 
	 * @param symbol
	 *            the symbol
	 * @return the command handler for that symbol, or <CODE>null</CODE> if no
	 *         handler exists
	 */
	public Renderer.CommandHandler getHandler(String symbol) {
		if (handlers.containsKey(symbol))
			return (CommandHandler) handlers.get(symbol);
		return null;
	}

	/**
	 * Returns the progress in the current rendering.
	 * 
	 * @return the number of symbols processed, the max value of which is twice
	 *         the number of symbols passed into the <CODE>render</CODE>
	 *         method
	 */
	public int getDoneSymbols() {
		return completedSymbols;
	}

	/**
	 * Does an assignment from a key to a value, calling the handler as well as
	 * setting the value in the turtle.
	 * 
	 * @param key
	 *            the key
	 * @param value
	 *            the value, possibly a mathematical expression
	 */
	public void assign(String key, String value) {
		try {
			try {
				if (!NONASSIGN_WORDS.contains(key)) {
					currentTurtle.assign(key, value);
					value = currentTurtle.get(key).toString();
				}
			} catch (Throwable e) {

			}
			Renderer.CommandHandler handler = getHandler(key);
			handler.handle(value);
		} catch (Throwable e) {

		}
	}

	/**
	 * Given a list of symbols and a dictionary of parameters, this will render
	 * a representation of those symbols to either a graphics, or a returned
	 * image.
	 * 
	 * @param symbols
	 *            a list of symbols
	 * @param parameters
	 *            the parameters
	 * @param matrix
	 *            the initial transform matrix for the turtle, or if <CODE>null</CODE>
	 *            it is assumed to be the identity matrix
	 * @param graphics
	 *            If we want to render to a graphics, pass this in and the
	 *            L-system will be drawn in the graphic's clip bounds, or pass
	 *            in <CODE>null</CODE> to have this function return an image.
	 *            This graphics should have a clip area set!
	 * @param origin
	 *            stores in the passed in point the location where the turtle
	 *            started
	 * @return an image of a rendering of these symbols, or <CODE>null</CODE>
	 *         if there was a passed in graphics object
	 * @throws IllegalArgumentException
	 *             if there is a passed in graphics object and its clip area is
	 *             not set
	 */
	public Image render(List symbols, Map parameters, Matrix matrix,
			Graphics2D graphics, Point2D origin) {
		BufferedImage image = null;
		Rectangle2D bounds = new Rectangle2D.Double();
		if (graphics != null && graphics.getClip() == null)
			throw new IllegalArgumentException(
					"Graphics needs a non-null clip!");
		if (matrix == null)
			matrix = new Matrix();
		totalSymbols = symbols.size() * 2;
		completedSymbols = 0;
		isActive = true;
		for (int i = 0; i < 2; i++) {
			areDrawing = i == 1;
			drawnSofar = 0;
			// Set up the initial conditions.
			turtleStack.clear();
			currentTurtle = new Turtle();
			currentTurtle.matrix = matrix;
			currentTurtle = new Turtle(currentTurtle);
			// Set up the graphics object.
			if (!areDrawing || graphics == null) {
				image = new BufferedImage((int) bounds.getWidth() + 10,
						(int) bounds.getHeight() + 10,
						BufferedImage.TYPE_INT_ARGB);
				g = image.createGraphics();
				if (areDrawing) {
					g.translate(-bounds.getX() + 5.0, -bounds.getY() + 5.0);
					origin
							.setLocation(5.0 - bounds.getX(), 5.0 - bounds
									.getY());
					g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
							RenderingHints.VALUE_ANTIALIAS_ON);
				}
			} else {
				image = null;
				g = (Graphics2D) graphics.create();
				Rectangle2D newBounds = new Rectangle2D.Double(
						bounds.getX() - 5.0, bounds.getY() - 5.0, bounds
								.getWidth() + 10.0, bounds.getHeight() + 10.0);
				Rectangle2D ourBounds = g.getClipBounds();
				double aRatio = newBounds.getWidth() / newBounds.getHeight();
				double vRatio = ourBounds.getWidth() / ourBounds.getHeight();
				if (aRatio > vRatio) {
					// The L-system is wider than the clip bounds.
					double targetHeight = newBounds.getWidth() / vRatio;
					targetHeight -= newBounds.getHeight();
					// Must extend by targetHeight.
					newBounds.setRect(newBounds.getX(), newBounds.getY()
							- targetHeight / 2.0, newBounds.getWidth(),
							newBounds.getHeight() + targetHeight);
				} else {
					// The L-system is taller than the clip bounds.
					double targetWidth = newBounds.getHeight() * vRatio;
					targetWidth -= newBounds.getWidth();
					// Extend by targetWidth.
					newBounds.setRect(newBounds.getX() - targetWidth / 2.0,
							newBounds.getY(), newBounds.getWidth()
									+ targetWidth, newBounds.getHeight());
				}
				double scale = ourBounds.getWidth() / newBounds.getWidth();
				g.scale(scale, scale);
				g.translate(ourBounds.getX() - newBounds.getX(), ourBounds
						.getY()
						- newBounds.getY());
				origin.setLocation(ourBounds.getX() - newBounds.getX(),
						ourBounds.getY() - newBounds.getY());
			}
			// Do the initial parameters.
			Iterator it = parameters.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				try {
					assign((String) entry.getKey(), (String) entry.getValue());
				} catch (Throwable e) {
					// We have an error in the handler!
				}
			}
			// Set the initial drawing state.
			g.setColor(currentTurtle.getColor());
			capLinePath();
			// Repeatedly read symbols, and call the appropriate
			// command handler.
			it = symbols.iterator();
			while (it.hasNext()) {
				completedSymbols++;
				String symbol = (String) it.next();
				Renderer.CommandHandler handler = getHandler(symbol);
				if (handler != null) {
					try {
						handler.handle(null);
					} catch (Throwable e) {
						// We have an error!
					}
					continue;
				}
				// OKAY, perhaps this is an assignment?
				int equalsPosition = symbol.indexOf('=');
				if (equalsPosition != -1) {
					String key = symbol.substring(0, equalsPosition);
					String value = symbol.substring(equalsPosition + 1);
					// Get the assignment.
					assign(key, value);
				}
				// Well, let's go on. Perhaps this is a symbol with
				// an argument.
				int leftParenPosition = symbol.indexOf('('), rightParenPosition = symbol
						.lastIndexOf(')');
				if (leftParenPosition != -1 && rightParenPosition != -1
						&& leftParenPosition < rightParenPosition) {
					String key = symbol.substring(0, leftParenPosition);
					String value = symbol.substring(leftParenPosition + 1,
							rightParenPosition);
					handler = getHandler(key);
					try {
						handler.handle(value);
					} catch (Throwable e) {
						// Another error. Whew.
					}
					continue;
				}
			}
			capLinePath();
			g.dispose();
			// We pop all the turtle stacks to make sure the bounds
			// are okay...
			while (!turtleStack.isEmpty())
				popTurtleStack();
			bounds = currentTurtle.getBounds();
		}
		isActive = false;
		areDrawing = false;
		return image;
	}

	public boolean isActive() {
		return isActive;
	}

	/**
	 * This will pop the turtle stack.
	 */
	private void popTurtleStack() {
		try {
			Turtle lt = (Turtle) turtleStack.pop();
			lt.updateBounds(currentTurtle);
			currentTurtle = lt;
			g.setColor(currentTurtle.getColor());
			g.setStroke(currentTurtle.getStroke());
		} catch (EmptyStackException e) {
			// We just ignore it.
		}
	}

	/**
	 * 
	 */
	private final void capLinePath() {
		g.draw(linePath); // Dump the path to the graphics...
		linePath.reset(); // Clear the path...
		linePath.moveTo((float) currentTurtle.position.getX(),
				(float) currentTurtle.position.getY());
		// We've started anew!
	}

	/** The command handler maps from symbols to the appropriate handler. */
	private Map handlers = new HashMap();

	/**
	 * <CODE>true</CODE> if we are actually drawing, elsewise we're in the
	 * phase where we're still trying to discover the bounds (in which case
	 * actually drawing isn't strictly required).
	 */
	private boolean areDrawing = false;

	private boolean isActive = false;

	/** The stack of turtles. */
	private Stack turtleStack = new Stack();

	/** The current turtle. */
	private Turtle currentTurtle;

	/** The current graphics object. */
	private Graphics2D g;

	/** The polygon. Null if no polygon is being drawn right now. */
	private GeneralPath polygon = null;

	/** Lines paths. */
	private GeneralPath linePath = new GeneralPath();

	/** The number of objects drawn sofar. */
	private int drawnSofar;

	/** The number of symbols completed sofar. */
	private int completedSymbols;

	/** The number of symbols to process. */
	private int totalSymbols;

	/** The set of words that can be assigned to. */
	public static Set ASSIGN_WORDS;

	/** The set of words that cannot be assigned a numerical value. */
	public static Set NONASSIGN_WORDS;

	static {
		Set s = new TreeSet();
		s.add("color");
		s.add("polygonColor");
		NONASSIGN_WORDS = Collections.unmodifiableSet(new HashSet(s));
		s.add("angle");
		s.add("lineWidth");
		s.add("lineIncrement");
		s.add("distance");
		s.add("hueChange");
		ASSIGN_WORDS = Collections.unmodifiableSet(s);
	}

	// / THE COMMAND HANDLERS!

	/**
	 * This is a command handler. This is the object that responds to the
	 * command. This class is meant to alter the state of the <CODE>Renderer</CODE>
	 * object, so it is not a static class.
	 */
	protected class CommandHandler {
		/**
		 * Handles the command.
		 * 
		 * @param symbol
		 *            an optional argument to the handler, but may be <CODE>null</CODE>
		 */
		public void handle(String symbol) {
			// This does nothing, since the default behavior is to
			// just ignore commands. Subclasses will do something,
			// presumably.
		}
	}

	/**
	 * This handles moving the cursor.
	 */
	private class MoveHandler extends CommandHandler {
		public MoveHandler(boolean pendown, boolean forward) {
			this.pendown = pendown;
			this.forward = forward;
		}

		public final void handle(String symbol) {
			// Evaluate if necessary.
			if (symbol == null)
				currentTurtle.go(forward);
			else {
				double d = currentTurtle.valueOf(symbol).doubleValue();
				currentTurtle.go(forward ? d : -d);
			}

			if (!areDrawing)
				return;
			if (pendown) {
				if (polygon == null) {
					// We're not adding to the polygon!
					linePath.lineTo((float) currentTurtle.position.getX(),
							(float) currentTurtle.position.getY());
				} else {
					// We are adding to the polygon!
					polygon.lineTo((float) currentTurtle.position.getX(),
							(float) currentTurtle.position.getY());
				}
			} else {
				linePath.moveTo((float) currentTurtle.position.getX(),
						(float) currentTurtle.position.getY());
			}

		}

		private boolean pendown;

		private boolean forward;

		private Line2D line = new Line2D.Double();
	}

	/**
	 * This handles turning.
	 */
	private class TurnHandler extends CommandHandler {
		public TurnHandler(boolean clockwise) {
			this.clockwise = clockwise;
		}

		public final void handle(String symbol) {
			// Evaluate if necessary.
			if (symbol == null)
				currentTurtle.turn(clockwise);
			else {
				double d = currentTurtle.valueOf(symbol).doubleValue();
				currentTurtle.turn(clockwise ? -d : d);
			}
		}

		private boolean clockwise;
	}

	/**
	 * This handles pitching.
	 */
	private class PitchHandler extends CommandHandler {
		public PitchHandler(boolean down) {
			this.down = down;
		}

		public final void handle(String symbol) {
			if (symbol == null)
				currentTurtle.pitch(down);
			else {
				double d = currentTurtle.valueOf(symbol).doubleValue();
				currentTurtle.pitch(down ? d : -d);
			}
		}

		private boolean down;
	}

	/**
	 * This handles rolling.
	 */
	private class RollHandler extends CommandHandler {
		public RollHandler(boolean right) {
			this.right = right;
		}

		public final void handle(String symbol) {
			if (symbol == null)
				currentTurtle.roll(right);
			else {
				double d = currentTurtle.valueOf(symbol).doubleValue();
				currentTurtle.roll(right ? -d : d);
			}
		}

		private boolean right;
	}

	/**
	 * This handles pushing on the turtle stack.
	 */
	private class PushTurtleHandler extends CommandHandler {
		public final void handle(String symbol) {
			turtleStack.push(currentTurtle.clone());
		}
	}

	/**
	 * This handles popping the turtle stack.
	 */
	private class PopTurtleHandler extends CommandHandler {
		public final void handle(String symbol) {
			capLinePath();
			popTurtleStack();
			capLinePath();
		}
	}

	/**
	 * This handles changing the width of lines.
	 */
	private class WidthChangeHandler extends CommandHandler {
		public WidthChangeHandler(boolean increment) {
			this.increment = increment;
		}

		public final void handle(String symbol) {
			capLinePath();
			if (symbol == null)
				currentTurtle.changeLineWidth(increment);
			else {
				double d = currentTurtle.valueOf(symbol).doubleValue();
				currentTurtle.changeLineWidth(increment ? d : -d);
			}
			g.setStroke(currentTurtle.getStroke());
		}

		private boolean increment;
	}

	/**
	 * This handles change of the draw color.
	 */
	private class DrawColorHandler extends CommandHandler {
		public final void handle(String symbol) {
			if (!areDrawing)
				return;
			capLinePath();
			currentTurtle.setColor(symbol);
			g.setColor(currentTurtle.getColor());
		}
	}

	/**
	 * This handles change of the polygon color.
	 */
	private class PolygonColorHandler extends CommandHandler {
		public final void handle(String symbol) {
			if (!areDrawing)
				return;
			currentTurtle.setPolygonColor(symbol);
		}
	}

	/**
	 * This handles change of the angle increment.
	 */
	private class AngleIncrementHandler extends CommandHandler {
		public final void handle(String symbol) {
			currentTurtle.setAngleChange(Double.parseDouble(symbol));
		}
	}

	/**
	 * This handles change of the line width.
	 */
	private class LineWidthHandler extends CommandHandler {
		public final void handle(String symbol) {
			if (!areDrawing)
				return;
			capLinePath();
			currentTurtle.setLineWidth(Double.parseDouble(symbol));
			g.setStroke(currentTurtle.getStroke());
		}
	}

	/**
	 * This handles change of the line width increment.
	 */
	private class LineWidthIncrementHandler extends CommandHandler {
		public final void handle(String symbol) {
			currentTurtle.setLineIncrement(Double.parseDouble(symbol));
		}
	}

	/**
	 * This handles change of individual line lengths.
	 */
	private class DistanceHandler extends CommandHandler {
		public final void handle(String symbol) {
			currentTurtle.distance = Double.parseDouble(symbol);
		}
	}

	/**
	 * This handler begins a polygon.
	 */
	private class BeginPolygonHandler extends CommandHandler {
		public final void handle(String symbol) {
			if (!areDrawing || polygon != null)
				return; // Hrm.
			capLinePath();
			polygon = new GeneralPath();
			polygon.moveTo((float) currentTurtle.position.getX(),
					(float) currentTurtle.position.getY());
		}
	}

	/**
	 * This handler closes a polygon.
	 */
	private class ClosePolygonHandler extends CommandHandler {
		public final void handle(String symbol) {
			if (!areDrawing)
				return;
			capLinePath();
			polygon.closePath();
			g.setColor(currentTurtle.polygonColor);
			g.fill(polygon);
			polygon = null;
			g.setColor(currentTurtle.color);
			drawnSofar++;
		}
	}

	/**
	 * The reverse handler.
	 */
	private class ReverseHandler extends CommandHandler {
		public final void handle(String symbol) {
			currentTurtle.turn(180.0);
		}
	}

	/**
	 * This handles change of the hue angle increment.
	 */
	private class HueAngleIncrementHandler extends CommandHandler {
		public final void handle(String symbol) {
			currentTurtle.setHueChange(Double.parseDouble(symbol));
		}
	}

	/**
	 * This handles changing the hue angle.
	 */
	private class HueChangeHandler extends CommandHandler {
		public HueChangeHandler(boolean polygon, boolean add) {
			this.polygon = polygon;
			this.add = add;
		}

		public final void handle(String symbol) {
			if (!areDrawing)
				return;
			capLinePath();
			if (symbol == null)
				if (polygon)
					currentTurtle.changePolygonHue(add);
				else
					currentTurtle.changeHue(add);
			else {
				double d = currentTurtle.valueOf(symbol).doubleValue();
				d = add ? d : -d;
				if (polygon)
					currentTurtle.changePolygonHue(d);
				else
					currentTurtle.changeHue(d);
			}
			g.setColor(currentTurtle.getColor());
		}

		private boolean add, polygon;
	}
}
