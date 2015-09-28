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




package edu.duke.cs.jflap.automata.graph.layout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.Set;
import java.awt.Dimension;
import java.awt.geom.Point2D;

import edu.duke.cs.jflap.automata.graph.Graph;
import edu.duke.cs.jflap.automata.graph.LayoutAlgorithm;

/**
 * This algorithm assigns all vertices to random points in the graph, while applying a
 * little effort taken to minimize edge intersections.
 * 
 * @see LayoutAlgorithm
 * @author Chris Morgan
 */
public class RandomLayoutAlgorithm extends LayoutAlgorithm {
	/**
	 * A list of all movable vertices.
	 */
	private ArrayList vertices;
	/**
	 * A list of all randomly generated points.
	 */
	private ArrayList points;
	/**
	 * The <code>VertexChain</code> used to minimize edge collision.
	 */
	private VertexChain chain;
	
	/**
	 * Assigns some default values.  To have different values, use the other constructor.
	 */
	public RandomLayoutAlgorithm() {
		super();
	}
	
	/**
	 * Constructor allowing the user to customize certain values.  The <code>vertexDim</code> 
	 * is not used in this algorithm, but it is here so the superclass constructor can 
	 * be used and for the <code>LayoutAlgorithmFactory</code>.
	 * 
	 * @param pSize 
	 *     value for <code>size</code>.
	 * @param vDim 
	 *     value for <code>vertexDim</code>.
	 * @param vBuffer 
	 *     value for <code>vertexBuffer</code>.
	 */
	public RandomLayoutAlgorithm(Dimension pSize, Dimension vDim, double vBuffer) {
		super(pSize, vDim, vBuffer);
	}	
	
	public void layout(Graph graph, Set notMoving) {
		//First, check to see that movable vertices exist
		vertices = getMovableVertices(graph, notMoving);
		if (graph==null || vertices.size() == 0)
			return;
		
		//Then, generate random points and assign the vertices to a
		//VertexChain to minimize a few edge collisions
		chain = new VertexChain(graph);
		assignPointsAndVertices();
		
		//Then minimize vertex overlap.
		lessenVertexOverlap();
		
		//Next, find a more optimal point order with which to match the points to the vertices.
		findCorrectPointOrder();
		
		//Finally, move all vertices to their corresponding points.  Wrap up the algorithm by 
		//making sure all points are on the screen.		  
		for (int i=0; i<points.size(); i++)
			graph.moveVertex(chain.get(i), (Point2D) points.get(i));
		shiftOntoScreen(graph, size, vertexDim, true);		
	}
	
	/**
	 * This method creates random points and assigns all movable vertices to the VertexChain
	 */
	private void assignPointsAndVertices() {
		double x, y;		
		Random random = new Random();				
		points = new ArrayList();		
		for (int i=0; i<vertices.size(); i++) {			
			x = random.nextDouble() * (size.getWidth() - vertexBuffer * 2);
			y = random.nextDouble() * (size.getHeight() - vertexBuffer * 2);
			points.add(new Point2D.Double(x, y));
			chain.addVertex(vertices.get(i));			
		}
	}
	
	/**
	 * This method shifts the random points away from each other, if needed, in order to minimize
	 * vertex overlap.
	 */
	private void lessenVertexOverlap() {				
		//First, sort the vertices by their x and y values
		ArrayList xOrder, yOrder;				
		xOrder = new ArrayList();   yOrder = new ArrayList();
		xOrder.addAll(points);      yOrder.addAll(points);
		Collections.sort(xOrder, new Comparator() {			
			public int compare(Object o1, Object o2) {				
				if (((Point2D) o1).getX() == ((Point2D) o2).getX())
					return 0;
				else if (((Point2D) o1).getX() < ((Point2D) o2).getX())
					return 1;
				else
					return -1;
		}});
		Collections.sort(yOrder, new Comparator() {			
			public int compare(Object o1, Object o2) {				
				if (((Point2D) o1).getY() == ((Point2D) o2).getY())
					return 0;
				else if (((Point2D) o1).getY() < ((Point2D) o2).getY())
					return 1;
				else
					return -1;
		}});
		
		//Then, shift over any points that need to be shifted over
		Point2D point;
		double xBuffer, yBuffer, xDiff, yDiff;
		xBuffer = vertexDim.getWidth() + vertexBuffer;
		yBuffer = vertexDim.getHeight() + vertexBuffer;		
		for (int i=0; i<vertices.size()-1; i++) {
			xDiff = ((Point2D)xOrder.get(i)).getX() - ((Point2D)xOrder.get(i+1)).getX();
			yDiff = ((Point2D)xOrder.get(i)).getY() - ((Point2D)xOrder.get(i+1)).getY();
			if (xDiff < xBuffer && yDiff < yBuffer)
				for (int j=i; j>=0; j--) {
					point = (Point2D) xOrder.get(j);
					point.setLocation(point.getX() + xBuffer - xDiff, point.getY());
				}
			xDiff = ((Point2D)yOrder.get(i)).getX() - ((Point2D)yOrder.get(i+1)).getX();
			yDiff = ((Point2D)yOrder.get(i)).getY() - ((Point2D)yOrder.get(i+1)).getY();
			if (xDiff < xBuffer && yDiff < yBuffer)
				for (int j=i; j>=0; j--) {
					point = (Point2D) yOrder.get(j);
					point.setLocation(point.getX(), point.getY() + yBuffer - yDiff);
				}		
		}
	}
		
	/**
	 * The method reassigns the randomly generated points into a new order, placing them in an order such
	 * that the vertices assigned to them will spiral toward the center as one progresses through the chain.
	 */
	private void findCorrectPointOrder() {		
		ArrayList notProcessedPoints, newPointOrder;
		Point2D current, anchor, minPoint;
		double currentTheta, minTheta, anchorTheta;
		
		anchor = new Point2D.Double(0,0);
		anchorTheta = 0;		
		newPointOrder = new ArrayList();
		notProcessedPoints = new ArrayList();		
		notProcessedPoints.addAll(points);
		
		//Find the angle of all points relative to the last point placed and "anchorTheta".  Then place
		//the point with the minimum angle.  "anchorTheta" will slowly rotate around a circle counterclockwise.
		while (notProcessedPoints.size() > 0) {	
			minPoint = (Point2D) notProcessedPoints.get(0);			
			minTheta = 2*Math.PI + 1;				
			for (int i=0; i<notProcessedPoints.size(); i++) {				
				current = (Point2D) notProcessedPoints.get(i);
								
				if (current.getY() != anchor.getY())
					currentTheta = Math.atan((current.getX() - anchor.getX()) / (current.getY() - anchor.getY()));
				else if (current.getX() > anchor.getX())
					currentTheta = Math.PI / 2;
				else
					currentTheta = Math.PI / -2;
				
				/* atan -> -pi/2...pi/2.  Adding 4pi to the currentTheta, subtracting the anchorTheta, and taking
				 * the remainder when dividing by pi works for all four quadrants the angle could be in.
				 * The object is to find the smallest absolute polar theta of the current point from the anchor
				 * which is greater than, or next in a counterclockwise traversal, from the anchorTheta.
				 */
				currentTheta = (currentTheta + 4*Math.PI - anchorTheta) % (Math.PI);
				if (currentTheta < minTheta) {
					minTheta = currentTheta;
					minPoint = current;
				}					
			}
			anchor = minPoint;			
			anchorTheta = (anchorTheta + minTheta) % (2*Math.PI);			
			notProcessedPoints.remove(minPoint);
			newPointOrder.add(minPoint);
		}
		
		points = newPointOrder;		
	}
}