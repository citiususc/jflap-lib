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





package edu.duke.cs.jflap.automata.graph;

import java.awt.geom.Point2D;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Set;

/**
 * This class defines an algorithm that lays out a graph.
 * 
 * @author Thomas Finley & Chris Morgan
 */

public abstract class LayoutAlgorithm {
	/**
	 * The size of the canvas on which the <code>LayoutAlgorithm</code> will be implemented on.
	 */
	protected Dimension size;
	/**
	 * The value for how much space in the graph a vertex is assumed to take.
	 */
	protected Dimension vertexDim;
	/**
	 * The minimum space between vertices.
	 */
	protected double vertexBuffer;
	
	public LayoutAlgorithm() {
		size = new Dimension(900, 900);
		vertexDim = new Dimension(30, 30);
		vertexBuffer = 30;
	}
	
	/**
	 * Constructor allowing the user to customize certain values.
	 * 
	 * @param pSize - value for <code>size</code>.
	 * @param vDim - value for <code>vertexDim</code>.
	 * @param vBuffer - value for <code>vertexBuffer</code>.
	 */
	public LayoutAlgorithm(Dimension pSize, Dimension vDim, double vBuffer)
	{
		size = pSize;
		vertexDim = vDim;
		vertexBuffer = vBuffer;
	}
	
	/**
	 * Moves the vertices of the states of the graph so that they are in some pleasing position.
	 * 
	 * @param graph
	 *            the graph whose states this method shall move.
	 * @param notMoving
	 *            the set of vertices that will not move at all.
	 */
	public abstract void layout(Graph graph, Set notMoving);		
	
	/**
	 * Method that can make sure that all vertices are visible in the screen.  The
	 * leftmost vertex is shifted to an x-coordinate of <code>buffer.width</code>, the highest
	 * vertex is shifted to a y-coordinate of <code>buffer.height</code>, the rightmost vertex
	 * is shifted to a coordinate of <code>size.width</code> - <code>buffer.width</code>, and
	 * the bottom-most vertex is shifted to a coordinate of <code>size.width</code> - 
	 * <code>buffer.height</code>. All other vertices are adjusted accordingly.  Additionally, 
	 * if the graph is too large to fit onto the current screen, the distance between the 
	 * vertices is proportionally shrunk so that they are..
	 * 
	 * @param graph
	 *            The graph whose states this method shall move.
	 * @param size
	 * 			  The assumed size of the screen
	 * @param buffer
	 * 			  The minimum space from the side of the screen that a vertex can be placed at.  There
	 * 			  can be different values for the width and height.            
	 * @param scaleOnlyOverflow 
	 *            If true, will only fill the screen with a graph if the points are outside
	 *            the given size.
	 */
	public static void shiftOntoScreen(Graph graph, Dimension size, Dimension buffer, boolean scaleOnlyOverflow) 
	{
		if (size==null || size.getHeight() == 0 || size.getWidth() == 0)
			return;
		
		Object[] vertices = graph.vertices();
		double currentX, currentY, minX, minY, maxX, maxY, heightRatio, widthRatio;
		
		//First, find the extreme values of x & y
		minX=Integer.MAX_VALUE;   minY=Integer.MAX_VALUE;   
		maxX=Integer.MIN_VALUE;   maxY=Integer.MIN_VALUE;
		for (int i=0; i<vertices.length; i++) {
			currentX = graph.pointForVertex(vertices[i]).getX();
			currentY = graph.pointForVertex(vertices[i]).getY(); 
			if (currentX < minX)
				minX = currentX;
			if (currentX > maxX)
				maxX = currentX;
			if (currentY < minY)
				minY = currentY;			
			if (currentY > maxY)
				maxY = currentY;
		}
		
		//Then, set all points so that their coordinates range from (0...maxX-minX, 0...maxY-minY)
		for (int i=0; i<vertices.length; i++) 
			graph.moveVertex(vertices[i], new Point2D.Double(
				  graph.pointForVertex(vertices[i]).getX() - minX,
				  graph.pointForVertex(vertices[i]).getY() - minY));					
		
		//Calculate whether the points go off the defined screen minus buffer space, and adjust
		widthRatio = (maxX - minX) / (size.getWidth() - 2 * buffer.getWidth());
		heightRatio = (maxY - minY) / (size.getHeight() - 2 * buffer.getHeight());				
		if (widthRatio > 1.0 || !scaleOnlyOverflow) {
			for (int i=0; i<vertices.length; i++)
				graph.moveVertex(vertices[i], new Point2D.Double(					  
						  graph.pointForVertex(vertices[i]).getX() / widthRatio,
						  graph.pointForVertex(vertices[i]).getY()));			
		}
		if (heightRatio > 1.0 || !scaleOnlyOverflow) {
			for (int i=0; i<vertices.length; i++)
				graph.moveVertex(vertices[i], new Point2D.Double(
					  graph.pointForVertex(vertices[i]).getX(),
					  graph.pointForVertex(vertices[i]).getY() / heightRatio));
		}
		
		//Finally, shift the points right and down the respective buffer values
		for (int i=0; i<vertices.length; i++) 
			graph.moveVertex(vertices[i], new Point2D.Double(
				  graph.pointForVertex(vertices[i]).getX() + buffer.getWidth(),
				  graph.pointForVertex(vertices[i]).getY() + buffer.getHeight()));
	}
	
	/**
	 * Returns a list of vertices that are in the <code>graph</code> but are not in <code>notMoving
	 * </code>.  If called by <code>layout()</code>, it should return a list of vertices that can be 
	 * moved.
	 * 
	 * @return the list of vertices 
	 */
	public static ArrayList getMovableVertices(Graph graph, Set notMoving) {
		Object[] vArray = graph.vertices();
		ArrayList vertices = new ArrayList();		
		for (int i=0; i<vArray.length; i++)
			if (notMoving == null || !notMoving.contains(vArray[i]))
			   vertices.add(vArray[i]);
		return vertices;
	}
	
	/**
	 * Converts the points in the graph of all given vertices from Cartesian polar coordinates.  The points
	 * will be returned with x = <i>r</i> and y = <i>θ</i>.  The center is assumed to be the origin.
	 * 
	 * @param graph - the graph the points are listed in
	 * @param vertices - a list of objects whose points need to be changed
	 */
	public static void cartesianToPolar(Graph graph, ArrayList vertices) {
		double theta, r;
		Point2D cartesian;
		for (int i=0; i<vertices.size(); i++) {
			cartesian = graph.pointForVertex(vertices.get(i));
			if (cartesian.getY() != 0)
				theta = Math.atan(cartesian.getY() / cartesian.getX());
			else
				theta = Math.PI / 2;
			r = Math.sqrt(Math.pow(cartesian.getX(), 2) + Math.pow(cartesian.getY(), 2));
			graph.moveVertex(vertices.get(i), new Point2D.Double(r, theta));			
		}
	}
	
	/**
	 * Converts the points in the graph of all given vertices from polar to Cartesian coordinates.  It is
	 * assumed that the current points on the graph are in polar coordinates, with x = <i>r</i> and y = 
	 * <i>θ</i>.  The center is assumed to be the origin
	 * 
	 * @param graph - the graph the points are listed in
	 * @param vertices - a list of objects whose points need to be changed
	 */
	public static void polarToCartesian(Graph graph, ArrayList vertices) {
		Point2D polar, cartesian;
		for (int i=0; i<vertices.size(); i++) {
			polar = graph.pointForVertex(vertices.get(i));
			cartesian = new Point2D.Double(Math.cos(polar.getY()) * polar.getX(),
							Math.sin(polar.getY()) * polar.getX());
			graph.moveVertex(vertices.get(i), cartesian);
		}
	}
}
