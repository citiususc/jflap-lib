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





package edu.duke.cs.jflap.gui.viewer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import edu.duke.cs.jflap.automata.Automaton;
import edu.duke.cs.jflap.automata.Note;
import edu.duke.cs.jflap.automata.State;
import edu.duke.cs.jflap.automata.Transition;
import edu.duke.cs.jflap.automata.event.AutomataStateEvent;
import edu.duke.cs.jflap.automata.event.AutomataStateListener;
import edu.duke.cs.jflap.automata.event.AutomataTransitionEvent;
import edu.duke.cs.jflap.automata.event.AutomataTransitionListener;
import java.util.HashSet;

/**
 * This is the very basic class of an Automaton drawer. It has facilities to
 * draw the Automaton. Subclasses may be derived to have finer control over how
 * things are drawn.
 * 
 * @author Thomas Finley
 * @version 1.0
 */

public class AutomatonDrawer {
	/**
	 * Instantiates an object to draw an automaton.
	 * 
	 * @param automaton
	 *            the automaton to handle
	 */
	public AutomatonDrawer(Automaton automaton) {
		this.automaton = automaton;
		DrawerListener listener = new DrawerListener();
		getAutomaton().addStateListener(listener);
		getAutomaton().addTransitionListener(listener);
        
        /*
         * Use a moore state drawer if it is a Moore machine.
         * This draws a little box that shows the output of the
         * state.
         */
        if(automaton instanceof edu.duke.cs.jflap.automata.mealy.MooreMachine)
            statedrawer = new MooreStateDrawer();
	}

	/**
	 * Retrieves the <CODE>Automaton</CODE> handled by this drawer.
	 * 
	 * @return the <CODE>Automaton</CODE> handled by this drawer
	 */
	public Automaton getAutomaton() {
		return automaton;
	}

    //naive optimization for drawing
    ArrayList<State> hs = new ArrayList<State>();
    HashSet<Point> lhs = new HashSet<Point>();
    int specHash = Integer.MIN_VALUE;
    //


	/**
	 * Draws our automaton.
	 * 
	 * @param g2
	 *            the Graphics object to draw the automaton on
	 */
	public void drawAutomaton(Graphics g2) {
		if (!valid)
			refreshArrowMap();

		Graphics2D g = (Graphics2D) g2.create();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setFont(g.getFont().deriveFont(12.0f));

		// Draw transitions between states.
		g.setColor(Color.black);
		drawTransitions(g);


//        int sh = automaton.hashCode();

//        if (specHash != sh){
//            specHash = sh;
//            hs.clear();
//            lhs.clear();
//            // Draw every state...or not
//            State[] states = automaton.getStates();
//
//    //		for (int i = 0; i < states.length; i++) {
//            for (int i = states.length - 1; i >= 0; i--) {
//    //			drawState(g, states[i]);
//                if (!lhs.contains(states[i].getPoint())){
//                    hs.add(states[i]);
//                    lhs.add(states[i].getPoint());
//                }
//            }
//        }
        

//        //reverse again, to get the correct ordering for non-overlapping things
//        for (int i = hs.size() - 1; i >= 0; i--)
//			drawState(g, hs.get(i));
        
        State[] states = automaton.getStates();
        for (int i = 0; i < states.length; i++){
            drawState(g, states[i]);
        }
		
		
		this.drawSelectionBox(g);
		g.dispose();
	}

	
	/**
	 * Returns the bounds for an individual state.
	 * 
	 * @param state
	 *            the state to get the bounds for
	 * @return the rectangle that the state needs to be in to completely enclose
	 *         itself
	 */
	public Rectangle getBounds(State state) {
//		int radius = (int)(statedrawer.getRadius()*scaleBy);
//		int radius = (int)(statedrawer.getRadius()*curTransform.getScaleX()); //getScaleX and Y should be same
		int radius = statedrawer.getRadius(); //getScaleX and Y should be same
		
		
		Point p = state.getPoint();
		
		
		
		int yAdd = state.getLabels().length * 15;
		if (getAutomaton().getInitialState() == state)
			return new Rectangle(p.x - radius * 2, p.y - radius, radius * 3,
					radius * 2 + yAdd);

		return new Rectangle(p.x - radius, p.y - radius, radius * 2, radius * 2
				+ yAdd);
	}

	/**
	 * Returns the bounds for an individual transition.
	 * 
	 * @param transition
	 *            the transition to get the bounds for
	 * @return the rectangle that the transition needs to be in to completely
	 *         enclose itself
	 */
	public Rectangle getBounds(Transition transition) {
		if (!valid)
			refreshArrowMap();
		CurvedArrow arrow = (CurvedArrow) transitionToArrowMap.get(transition);
		Rectangle2D r = arrow.getBounds();
		return new Rectangle((int) r.getX(), (int) r.getY(),
				(int) r.getWidth(), (int) r.getHeight());
	}

	/**
	 * Returns the bounds that the automaton is drawn in.
	 * 
	 * @return the bounds that the automaton is drawn in, or <CODE>null</CODE>
	 *         if there is nothing to draw, i.e., the automaton has no states
	 */
	public Rectangle getBounds() {
		if (validBounds){
//			System.out.println("Using cache");
			return cachedBounds;
		}
		if (!valid)
			refreshArrowMap();
		State[] states = getAutomaton().getStates();
		if (states.length == 0)
			return null;
		Rectangle rect = getBounds(states[0]);
		for (int i = 1; i < states.length; i++)
			rect.add(getBounds(states[i]));

		ArrayList notes = getAutomaton().getNotes();
		for(int k = 0; k < notes.size(); k++){
			Note curNote = ((Note)notes.get(k));
			Rectangle newBounds = new Rectangle(curNote.getAutoPoint(), new Dimension(curNote.getBounds().getSize()));
			rect.add(newBounds);
		}
		Iterator it = arrowToTransitionMap.keySet().iterator();
		while (it.hasNext()) {
			CurvedArrow arrow = (CurvedArrow) it.next();
			Rectangle2D arrowBounds = arrow.getBounds();
			rect.add(arrowBounds);
		}
		validBounds = true;
//		return cachedBounds = rect;
		return cachedBounds = curTransform.createTransformedShape(rect).getBounds();
	}

	/**
	 * Draws a state on the automaton.
	 * 
	 * @param g
	 *            the graphics object to draw upon
	 * @param state
	 *            the state to draw
	 */
	protected void drawState(Graphics g, State state) {
		statedrawer.drawState(g, getAutomaton(), state);
		if (drawLabels) {
			statedrawer.drawStateLabel(g, state, state.getPoint(),
					StateDrawer.STATE_COLOR);
		}
	}

	/**
	 * Draws the transitions of the automaton.
	 * 
	 * @param g
	 *            the graphics object to draw upon
	 */
	protected void drawTransitions(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		Set arrows = arrowToTransitionMap.keySet();
		Iterator it = arrows.iterator();
		while (it.hasNext()) {
			CurvedArrow arrow = (CurvedArrow) it.next();
            if (arrow.myTransition.isSelected){
                arrow.drawHighlight(g2);
                arrow.drawControlPoint(g2);
            }
            else 
                arrow.draw(g2);
		}
	}

	

	/**
	 * draws selection box
	 */
	protected void drawSelectionBox(Graphics g){
		g.drawRect(mySelectionBounds.x, mySelectionBounds.y, mySelectionBounds.width, mySelectionBounds.height);
	}

	/**
	 * Refreshes the <CODE>arrowToTransitionMap</CODE> structure.
	 */
	private void refreshArrowMap() {
		if (automaton == null) {
			//System.out.println("Automaton is null, how?");
			return;
		}
		State[] states = automaton.getStates();
		arrowToTransitionMap.clear(); // Remove old entries.
		transitionToArrowMap.clear(); // Remove old entries.

		for (int i = 0; i < states.length; i++) {
			// This is some code that handles interstate (heh) transitions.
			for (int j = i + 1; j < states.length; j++) {
				// We want all transitions.
				Transition[] itoj = automaton.getTransitionsFromStateToState(
						states[i], states[j]);
				Transition[] jtoi = automaton.getTransitionsFromStateToState(
						states[j], states[i]);
				float top = jtoi.length > 0 ? 0.5f : 0.0f;
				float bottom = itoj.length > 0 ? 0.5f : 0.0f;

				if (itoj.length + jtoi.length == 0)
					continue;

				
				// Get where points should appear to emanate from.
				double angle = angle(states[i], states[j]);
				Point fromI = pointOnState(states[i], angle - ANGLE);
				Point fromJ = pointOnState(states[j], angle + Math.PI + ANGLE);
				for (int n = 0; n < itoj.length; n++) {
					if(curveTransitionMap.containsKey(itoj[n])){
						top = curveTransitionMap.get(itoj[n]);
					}
					float curvy = top+n;
					CurvedArrow arrow = n == 0 ? new CurvedArrow(fromI, fromJ,
							curvy, itoj[n]) : new InvisibleCurvedArrow(fromI, fromJ,
							curvy, itoj[n]);


					arrow.setLabel(itoj[n].getDescription());

					arrowToTransitionMap.put(arrow, itoj[n]);
					transitionToArrowMap.put(itoj[n], arrow);
				}
				fromI = pointOnState(states[i], angle + ANGLE);
				fromJ = pointOnState(states[j], angle + Math.PI - ANGLE);
				for (int n = 0; n < jtoi.length; n++) {
					if(curveTransitionMap.containsKey(jtoi[n])){
						bottom = curveTransitionMap.get(jtoi[n]);
					}
					float curvy = bottom+n;
					CurvedArrow arrow = n == 0 ? new CurvedArrow(fromJ, fromI,
							curvy, jtoi[n]) : new InvisibleCurvedArrow(fromJ, fromI,
							curvy, jtoi[n]);
					String label = jtoi[n].getDescription();


					arrow.setLabel(label);
					arrowToTransitionMap.put(arrow, jtoi[n]);
					transitionToArrowMap.put(jtoi[n], arrow);
				}
			}
			// Now handle transitions between a single state.
			Transition[] trans = automaton.getTransitionsFromStateToState(
					states[i], states[i]);
			if (trans.length == 0)
				continue;
			Point from = pointOnState(states[i], -Math.PI * 0.333);
			Point to = pointOnState(states[i], -Math.PI * 0.667);
			for (int n = 0; n < trans.length; n++) {
				if(selfTransitionMap.containsKey(trans[n])){
					//EDebug.print(selfTransitionMap);
					Point storedfrom = pointOnState(states[i], (selfTransitionMap.get(trans[n])+Math.PI*.166));
					Point storedto = pointOnState(states[i], (selfTransitionMap.get(trans[n])-Math.PI*.166));
					CurvedArrow arrow = n == 0 ? new CurvedArrow(storedfrom, storedto, -2.0f, trans[n])
					: new InvisibleCurvedArrow(storedfrom, storedto, -2.0f - n, trans[n]);


					arrow.setLabel(trans[n].getDescription());
					arrowToTransitionMap.put(arrow, trans[n]);
					transitionToArrowMap.put(trans[n], arrow);
				}else{
					//EDebug.print(selfTransitionMap);
					selfTransitionMap.put(trans[n], -Math.PI*.5);
					CurvedArrow arrow = n == 0 ? new CurvedArrow(from, to, -2.0f, trans[n])
						: new InvisibleCurvedArrow(from, to, -2.0f - n, trans[n]);

                    //INSERTED for TransitionGUI
                    arrow.myTransition = trans[n];
                    //END INSERTED for TransitionGUI
                    //MERLIN MERLIN MERLIN MERLIN MERLIN//


					arrow.setLabel(trans[n].getDescription());
					arrowToTransitionMap.put(arrow, trans[n]);
					transitionToArrowMap.put(trans[n], arrow);
				}
			}
		}
		valid = true;
	}

	/**
	 * Given two states, if there were a line connecting the center of the two
	 * states, at which point would that line intersect the outside of the first
	 * state? In other words, the point on state1 closest to the point on
	 * state2.
	 * 
	 * @param state1
	 *            the first state
	 * @param state2
	 *            the second state
	 * @return as described, the point of intersection on <CODE>state1</CODE>
	 */
	protected Point getCenterIntersection(State state1, State state2) {
		return pointOnState(state1, angle(state1, state2));
	}

	/**
	 * What is the angle on state1 of the point closest to state2?
	 * 
	 * @param state1
	 *            the first state
	 * @param state2
	 *            the second state
	 * @return the angle on state1 of the point closest to state2
	 */
	private double angle(State state1, State state2) {
		Point p1 = state1.getPoint();
		Point p2 = state2.getPoint();
		double x = (double) (p2.x - p1.x);
		double y = (double) (p2.y - p1.y);
		return Math.atan2(y, x);
	}

	/**
	 * Given a state and an angle, if we treat the state as a circle, what point
	 * does that angle represent?
	 * 
	 * @param state
	 *            the state
	 * @param angle
	 *            the angle on the state
	 * @return the point on the outside of the state with this angle
	 */
	public Point pointOnState(State state, double angle) {
		Point point = new Point(state.getPoint());
		double x = Math.cos(angle) * (double) StateDrawer.STATE_RADIUS;
		double y = Math.sin(angle) * (double) StateDrawer.STATE_RADIUS;
		point.translate((int) x, (int) y);
		return point;
	}

	/**
	 * Informs the drawer that states in the automata have changed to the point
	 * where a redraw is appropriate.
	 */
	public void invalidate() {
		valid = false;
		this.invalidateBounds();
	}

	/**
	 * Informs the drawer that it should recalculate the bounds the next time
	 * they are requested. This method is called automatically if the automaton
	 * changes.
	 */
	public void invalidateBounds() {
		validBounds = false;
	}

	/**
	 * Gets the state at a particular point.
	 * 
	 * @param point
	 *            the point to check
	 * @return a <CODE>State</CODE> object at this particular point, or <CODE>null</CODE>
	 *         if no state is at this point
	 */
	public State stateAtPoint(Point point) {
		State[] states = getAutomaton().getStates();
		// Work backwards, since we want to select the "top" state,
		// and states are drawn forwards so later is on top.
		for (int i = states.length - 1; i >= 0; i--)
			if (point.distance(states[i].getPoint()) <= StateDrawer.STATE_RADIUS)
				return states[i];
		// Not found. Drat!
		return null;
	}

	/**
	 * Gets the transition at a particular point.
	 * 
	 * @param point
	 *            the point to check
	 * @return a <CODE>Transition</CODE> object at this particular point, or
	 *         <CODE>null</CODE> if no transition is at this point
	 */
	public Transition transitionAtPoint(Point point) {
		if (!valid)
			refreshArrowMap();
		Set arrows = arrowToTransitionMap.keySet();
		Iterator it = arrows.iterator();
		while (it.hasNext()) {
			CurvedArrow arrow = (CurvedArrow) it.next();
			if (arrow.isNear(point, 2))
				return (Transition) arrowToTransitionMap.get(arrow);
		}
		return null;
	}
	

	/**
	 * Returns the state drawer.
	 * 
	 * @return the state drawer
	 */
	public StateDrawer getStateDrawer() {
		return statedrawer;
	}

	/**
	 * Listens for changes in transitions of our automaton. This method is
	 * called by the internal automaton listener for this object, and while not
	 * called directly by the automaton, is passed along the same event.
	 * 
	 * @param event
	 *            the transition event
	 */
	protected void transitionChange(AutomataTransitionEvent event) {
		invalidate();
	}

	/**
	 * Listens for changes in states of our automaton. This method is called by
	 * the internal automaton listener for this object, and while not called
	 * directly by the automaton, is passed along the same event.
	 * 
	 * @param event
	 *            the state event
	 */
	protected void stateChange(AutomataStateEvent event) {
		if (event.isMove())
			invalidate();
		else
			invalidateBounds();
	}

	/**
	 * Returns the curved arrow object that represents a particular transition.
	 * 
	 * @param transition
	 *            the transition to find the arrow for
	 * @return the curved arrow object that is used to draw this transition
	 */
	protected CurvedArrow arrowForTransition(Transition transition) {
		return (CurvedArrow) transitionToArrowMap.get(transition);
	}

	/**
	 * Returns if state labels are drawn in the diagram.
	 * 
	 * @return if state labels are drawn in the diagram
	 */
	public boolean doesDrawStateLabels() {
		return drawLabels;
	}

	/**
	 * Sets if state labels should be drawn in the diagram or not.
	 * 
	 * @param drawLabels
	 *            <CODE>true</CODE> if state labels should be drawn in the
	 *            state diagram, <CODE>false</CODE> if they should not be
	 */
	public void shouldDrawStateLabels(boolean drawLabels) {
		this.drawLabels = drawLabels;
	}

	public void setAutomaton(Automaton newAuto) {
		if (newAuto == null) {

			//System.out.println("Setting automaton null");
			return;
		}

		automaton = newAuto;
		this.invalidate();
	}
	

	public void setSelectionBounds(Rectangle bounds) {
		mySelectionBounds = bounds;
		
	}
	public Rectangle getSelectionBounds() {
		return mySelectionBounds;
	}
	
//	public void setScale(double scale){
//	    scaleBy = scale;	
//	    validBounds = false;
//	}
	public void setTransform(AffineTransform af){
		curTransform = af;
	}
	
	private Rectangle mySelectionBounds = new Rectangle(0, 0, -1, -1);

	/** The automaton we're handling. */
	private Automaton automaton;

	/** If we should draw state labels or not. */
	private boolean drawLabels = true;

	/**
	 * The difference in angle from the emination point of the transitions from
	 * the point closest to the other state.
	 */
	protected static final double ANGLE = Math.PI / 25.0;

	/**
	 * Whether or not the drawing objects should be redone on the next draw.
	 */
	private boolean valid = false;

	/**
	 * If any change happens at all that could effect the bounds, this is
	 * changed.
	 */
	private boolean validBounds = false;

	/** The cached bounds. */
	private Rectangle cachedBounds = null;
	
	/**
	 * A map of self transitions mapped to their angle of appearance.
	 */
	public HashMap<Transition, Double> selfTransitionMap = new HashMap();
	
	/**
	 * Map of curvatures for transitions
	 */
	public HashMap<Transition, Float> curveTransitionMap = new HashMap();

	/**
	 * A map of curved arrows to transitions. This object is also used for
	 * iteration over all arrows when drawing must be done
	 */
	public HashMap arrowToTransitionMap = new HashMap();

	/** The map from transitions to their respective arrows. */
	public HashMap transitionToArrowMap = new HashMap();

	/** The state drawer. */
	public StateDrawer statedrawer = new StateDrawer();
	
//	/**Amount to scale by, purely for scroll calclulation*/
//	private double scaleBy = 1;
	
	/**The transform instead*/
	private AffineTransform curTransform = new AffineTransform();
	

	/**
	 * This automaton listener takes care of responding to the events.
	 */
	private class DrawerListener implements AutomataStateListener,
			AutomataTransitionListener {
		/**
		 * Listens for changes in transitions of our automaton.
		 * 
		 * @param event
		 *            the transition event
		 */
		public void automataTransitionChange(AutomataTransitionEvent event) {
			transitionChange(event);
		}

		/**
		 * Listens for changes in states of our automaton.
		 * 
		 * @param event
		 *            the state event
		 */
		public void automataStateChange(AutomataStateEvent event) {
			stateChange(event);
		}
	}

}
