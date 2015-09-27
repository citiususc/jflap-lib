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

import edu.duke.cs.jflap.automata.Automaton;
import edu.duke.cs.jflap.automata.Note;
import edu.duke.cs.jflap.automata.State;
import edu.duke.cs.jflap.automata.event.*;
import edu.duke.cs.jflap.gui.JMultiLineToolTip;
import edu.duke.cs.jflap.gui.editor.EditorPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.util.ArrayList;

import javax.swing.*;

/**
 * A simple view that draws an automaton.
 * 
 * @author Thomas Finley
 */

public class AutomatonPane extends JPanel implements Scrollable {
	private final Logger logger = LoggerFactory.getLogger(AutomatonPane.class);
	/**
	 * Instantiates an AutomatonPane.
	 * 
	 * @param drawer
	 *            the automaton drawer
	 */
	public AutomatonPane(AutomatonDrawer drawer) {
		this(drawer, true);
		this.setLayout(null);
	}

	/**
	 * Since labels support multiple lines, so too must the tool tips for this
	 * component.
	 * 
	 * @return a multiline tool tip
	 */
	public JToolTip createToolTip() {
		return new JMultiLineToolTip();
	}

	/**
	 * Returns the location for a tool tip to display.
	 * 
	 * @return the location for a tool tip
	 */
	public Point getToolTipLocation(MouseEvent event) {
		try {
			return transformFromAutomatonToView(drawer.stateAtPoint(
					event.getPoint()).getPoint());
		} catch (NullPointerException e) {
			return null;
		}
	}

	/**
	 * Return the text for a tool tip.
	 * 
	 * @return text for a tool tip
	 */
	public String getToolTipText(MouseEvent event) {
		if (!drawer.doesDrawStateLabels()) {
			State s = drawer.stateAtPoint(event.getPoint());
			if (s == null)
				return null;
			return s.getLabel();
		}
		return null;
	}

	/**
	 * Instantiates an AutomatonPane.
	 * 
	 * @param drawer
	 *            the automaton drawer
	 * @param adapt
	 *            whether or not to adapt the size of the view
	 */
	public AutomatonPane(AutomatonDrawer drawer, boolean adapt) {
		super();
		this.drawer = drawer;
		this.adapt = adapt;
		setPreferredSize(new Dimension(400, 300));
		Listener listener = new Listener();
		drawer.getAutomaton().addStateListener(listener);
		drawer.getAutomaton().addTransitionListener(listener);
		addComponentListener(listener);
		setToolTipText("Beavis"); // Tool tips require some text. :P
		setOpaque(true);
	}

	/**
	 * Instantiates an AutomatonPane with the default automaton drawer as the
	 * drawer for the passed in automaton.
	 * 
	 * @param automaton
	 *            the automaton to draw
	 */
	public AutomatonPane(Automaton automaton) {
		this(new AutomatonDrawer(automaton));
	}

	/**
	 * Draws itself, and the automaton.
	 * 
	 * @param g
	 *            the graphics object to draw upon
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		if (transformNeedsReform)
			reformTransform(new Rectangle(getSize()));
		g.setColor(java.awt.Color.white);
		g.fillRect(0, 0, getSize().width, getSize().height);
		Graphics2D g2 = (Graphics2D) g;

		g2.transform(transform);
		drawer.drawAutomaton(g);

		//g2.translate(-transform.getTranslateX(), -transform.getTranslateY());
		//g2.scale(1.0/transform.getScaleX(), 1.0/transform.getScaleY());
		//reposition the notes on scroll...since I can't figure out where it is scrolling, I do it here.
		ArrayList notes = this.getDrawer().getAutomaton().getNotes();
		for(int k = 0; k < notes.size(); k++){
			Note curNote = (Note)notes.get(k);
			curNote.updateView();	
		}
		

		//g2.translate(transform.getTranslateX(), transform.getTranslateY());
		//g2.scale(transform.getScaleX(), transform.getScaleY());
	}

	/**
	 * Prints itself, with the automaton drawn so that it fills the space.
	 * 
	 * @param g
	 *            the graphics interface for the printer device
	 */
//	public void printComponent(Graphics g) {
//		boolean oldAdapt = adapt;
//		adapt = true;
//
//		reformTransform(g.getClipBounds());
//		g.setColor(java.awt.Color.white);
//		g.fillRect(0, 0, g.getClipBounds().width, g.getClipBounds().height);
////		Graphics2D g2 = (Graphics2D) g.create();
//		Graphics2D g2 = (Graphics2D) g;
//		g2.transform(transform);
//		drawer.drawAutomaton(g2);
//		adapt = oldAdapt;
//		reformTransform(new Rectangle(getSize()));
//	}
	public void printComponent(Graphics g) {
//		boolean oldAdapt = adapt;
//		adapt = true;

		if (transformNeedsReform)
			reformTransform(g.getClipBounds());
		g.setColor(java.awt.Color.white);
		g.fillRect(0, 0, g.getClipBounds().width, g.getClipBounds().height);
        
		Graphics2D g2 = (Graphics2D) g;
		g2.transform(transform);
        drawer.invalidate();
		drawer.drawAutomaton(g);

		ArrayList notes = this.getDrawer().getAutomaton().getNotes();
		for(int k = 0; k < notes.size(); k++){
			Note curNote = (Note)notes.get(k);
			curNote.updateView();	
		}

//		adapt = oldAdapt;
//		reformTransform(new Rectangle(getSize()));
	}

	/**
	 * Returns a rectangle that roughly bounds the automaton, or the section of
	 * the automaton that should be shown if not all should be shown.
	 * 
	 * @return a rectangle that bounds the automaton
	 */
	protected Rectangle getAutomatonBounds() {
		Rectangle rect = drawer.getBounds();
		if (rect == null)
			return new Rectangle(getSize());
		return rect;
	}

	/**
	 * Reforms the transform.
	 * 
	 * @param bounds
	 *            the current bounds of the drawing area, which may be used if
	 *            this component adapts it's size
	 */
	public void reformTransform(Rectangle viewBounds) {
		//completely unrelated
//		MouseListener[] k;
//	    System.out.println((k = this.getListeners(MouseListener.class)).length);	
//	    for (int i = 0; i < k.length; i++)
//	    	System.out.println(k[i]);
		
		//end completely unrelated
	    
	    
	    
		transformNeedsReform = false;
//		System.out.print("hello\n");
		Rectangle bounds = new Rectangle(getAutomatonBounds());
		if (!adapt) {
			// Much of this is to make sure that this component
			// displays properly in a scroll box. (It must work
			// either in a scroll box, or not!)
			Rectangle visible = getVisibleRect();
			// What is the point of the upper left corner of the
			// viewport in automaton drawer space?
			Point viewportUpperLeft = new Point((int) (visible.x - transform
					.getTranslateX()), (int) (visible.y - transform
					.getTranslateY()));
			// What is the point of the upper left corner of the
			// scrolling component in automaton drawer space?
			Point componentUpperLeft = new Point(Math.min(bounds.x, Math.min(0,
					viewportUpperLeft.x)), Math.min(bounds.y, Math.min(0,
					viewportUpperLeft.y)));
			// Make the transform draw the automaton in the
			// appropriate location.
			transform = new AffineTransform();
		    //push to drawer
			drawer.setTransform(transform);
			drawer.invalidateBounds();
			
			transform.translate(-componentUpperLeft.x, -componentUpperLeft.y);
			
		    
			//inserted by Henry
//			if (scaleBy > 0){
//                System.out.println("scaling now");
			    transform.scale(scaleBy, scaleBy); 	 //always and forever, you should scale
//				scaleBy = -1;
//			}
			
			
			// Set the size of the thing appropriately.
			Dimension newSize = new Dimension(Math.max(bounds.width + bounds.x,
					viewportUpperLeft.x + visible.width)
					- componentUpperLeft.x, Math.max(bounds.height + bounds.y,
					viewportUpperLeft.y + visible.height)
					- componentUpperLeft.y);
			if (newSize.equals(getPreferredSize()))
				return;
			
			
			setPreferredSize(newSize);
			// Scroll to make viewportUpperLeft an "isopoint".
			/*Rectangle scrollRect = new Rectangle(viewportUpperLeft.x
					- componentUpperLeft.x, viewportUpperLeft.y
					- componentUpperLeft.y, visible.width, visible.height);*/
			revalidate();
			scrollRectToVisible(visible);
			
			return;
		}

		// We do make the automaton fit in the visible space!
		transform = new AffineTransform();
		bounds.grow(20, 20);
		Rectangle ourBounds = viewBounds;
		double aRatio = bounds.getWidth() / bounds.getHeight();
		double vRatio = ourBounds.getWidth() / ourBounds.getHeight();
		if (aRatio > vRatio) {
			// The automaton is wider than the view.
			double targetHeight = bounds.getWidth() / vRatio;
			targetHeight -= bounds.getHeight();
			// Must extend by targetHeight.
			bounds.setRect(bounds.getX(), bounds.getY() - targetHeight / 2.0,
					bounds.getWidth(), bounds.getHeight() + targetHeight);
		} else {
			// The automaton is taller than the view.
			double targetWidth = bounds.getHeight() * vRatio;
			targetWidth -= bounds.getWidth();
			// Extend by targetWidth.
			bounds.setRect(bounds.getX() - targetWidth / 2.0, bounds.getY(),
					bounds.getWidth() + targetWidth, bounds.getHeight());
		}
		double scale = ourBounds.getWidth() / bounds.getWidth();
		transform.scale(scale, scale);
		logger.debug("reformTransform() {}", scale+"..."+(ourBounds.getX() - bounds.getX()));
		transform.translate(ourBounds.getX() - bounds.getX(), ourBounds.getY()
				- bounds.getY());
		
	}

	/**
	 * If an automaton changes, repaint this object.
	 */
	private class Listener extends ComponentAdapter implements
			AutomataStateListener, AutomataTransitionListener {
		public void automataTransitionChange(AutomataTransitionEvent e) {
			transformNeedsReform = true;
			repaint();
		}

		public void automataStateChange(AutomataStateEvent e) {
			transformNeedsReform = true;
			repaint();
		}

		public void componentResized(ComponentEvent e) {
			if (adapt)
				transformNeedsReform = true;
			repaint();
		}
	}

	/**
	 * Transforms a mouse event to have its mouse click location reflect the
	 * automaton space, not the view space.
	 * 
	 * @param event
	 *            the mouse event to transform
	 */
	public void transformMouseEvent(MouseEvent event) {
		if (transformNeedsReform)
			reformTransform(new Rectangle(getSize()));
		Point point = new Point(), ePoint = event.getPoint();
		try {
			// transform.transform(ePoint, point);
			transform.inverseTransform(ePoint, point);
			event.translatePoint(point.x - ePoint.x, point.y - ePoint.y);
		} catch (NoninvertibleTransformException e) {
			// Well, what CAN we do?
		}
	}

	/**
	 * Transforms a point from automaton space to view space.
	 * 
	 * @param point
	 *            the point, which will be modified
	 */
	public Point transformFromAutomatonToView(Point point) {
		return (Point) transform.transform(point, new Point());
	}

	/**
	 * Transforms a point from view space to automaton space.
	 * 
	 * @param point
	 *            the point, which will be modified
	 */
	public Point transformFromViewToAutomaton(Point point) {
		try {
			return (Point) transform.inverseTransform(point, new Point());
		} catch (NoninvertibleTransformException e) {
			// Nothing to do.
		}
		return new Point(point);
	}

	/**
	 * Processes mouse events with a transformed mouse event.
	 * 
	 * @param event
	 *            the mouse event
	 */
	public void processMouseEvent(MouseEvent event) {
		transformMouseEvent(event);
		super.processMouseEvent(event);
	}

	/**
	 * Processes mouse motion events with a transformed mouse event.
	 * 
	 * @param event
	 *            the mouse event
	 */
	public void processMouseMotionEvent(MouseEvent event) {
		transformMouseEvent(event);
		super.processMouseMotionEvent(event);
	}

	/**
	 * Returns the automaton drawer.
	 * 
	 * @return the automaton drawer
	 */
	public AutomatonDrawer getDrawer() {
		return drawer;
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
	 * If this is an adaptive automaton pane we want this component to be the
	 * width of a viewport of course, but if this is NOT an adaptive container
	 * we want it to be stretched so its width is at least that of the viewport
	 * (to avoid clicks seemingly "not happening" to the right of automata).
	 */
	public boolean getScrollableTracksViewportWidth() {
		if (adapt)
			return true;
		return getPreferredSize().width < getParent().getSize().width;
	}

	/**
	 * If this is an adaptive automaton pane we want this component to be the
	 * height of a viewport of course, but if this is NOT an adaptive container
	 * we want it to be stretched so its height is at least that of the viewport
	 * (to avoid clicks seemingly "not happening" below automata).
	 */
	public boolean getScrollableTracksViewportHeight() {
		if (adapt)
			return true;
		return getPreferredSize().height < getParent().getSize().height;
	}

	/**
	 * Returns what the point (0,0) in this component is in automaton space.
	 */
	public Point getAutomatonOrigin() {
		return new Point(-(int) transform.getTranslateX(), -(int) transform
				.getTranslateY());
	}

	/**
	 * This will edit the underlying automaton so that it fits within the bounds
	 * of the view (at 1 to 1 scale).
	 * 
	 * @param padding
	 *            the amount of padding
	 */
	public void fitToBounds(int padding) {
		Rectangle viewBounds = getVisibleRect();
        Rectangle automatonBounds;
        if(drawer != null && drawer.getBounds() != null) automatonBounds = new Rectangle(drawer.getBounds());
        else automatonBounds = new Rectangle(0, 0);
		automatonBounds.grow(padding, padding);
		viewBounds.x = viewBounds.y = 0; // Just pretend...
		State[] states = drawer.getAutomaton().getStates();

		// Translate and scale each point.
		for (int i = 0; i < states.length; i++) {
			Point p = states[i].getPoint();
			p.setLocation((p.getX() - automatonBounds.getX())
					* (viewBounds.getWidth() / automatonBounds.getWidth()), (p
					.getY() - automatonBounds.getY())
					* (viewBounds.getHeight() / automatonBounds.getHeight()));
			states[i].setPoint(p);
		}
	}

	public Component add(Component c) {
		if (c instanceof JTable)
			table = (JTable) c;
		return super.add(c);
	}

	public void setTablePoint(Point p) {
		tp = p;
	}

	public void doLayout() {
		try {
			super.doLayout();
			table.setLocation(tp);
		} catch (NullPointerException e) {
			super.doLayout();
		}
	}
    
    public void setAdapt(boolean newAdapt)
    {
        adapt = newAdapt;
        transformNeedsReform = true;
        repaint();
    }
    
    
    public boolean getAdapt()
    {
        return adapt;
     
    }
    
    public void setCreator(EditorPane pane) {
		myCreator = pane;
	}
	
	public EditorPane getCreator(){
		return myCreator;
	}
	
	public void setScale(double scale){
	    scaleBy = scale;	
//	    drawer.setScale(scale);
	}
	public double getScale(){
		return scaleBy;
	}
	
	public void requestTransform(){
		transformNeedsReform = true;
		repaint();
	}
	

	private EditorPane myCreator;


	/** The table point. */
	private Point tp = null;

	/** The drawer. */
	protected AutomatonDrawer drawer;

	/** The transform. */
	public AffineTransform transform = new AffineTransform();

	/** Whether or not we should adapt the view by transforming it. */
	private boolean adapt;

	/** Whether the transform needs to be reformed. */
	private boolean transformNeedsReform = true;

	/** THe table... bleh. */
	private JTable table;
	
	
	/**Factor to scale everyone by, when we don't use auto-scale*/
	private double scaleBy = 1;
}
