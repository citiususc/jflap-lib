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

import java.awt.Dimension;
import java.awt.geom.Point2D;

import edu.duke.cs.jflap.automata.graph.Graph;

/**
 * A <code>VertexChain</code> that also has the ability to layout its vertices in a circle or part of a circle.
 * 
 * @see VertexChain
 * @author Chris Morgan
 */
public class CircleChain extends VertexChain {
	/**
	 * Distance from the center of the graph of the <code>CircleChain</code>.  The polar coordinate <i>r</i> for all 
	 * vertices in the <code>CircleChain</code>.
	 */
	protected double radius;
	/**
	 * The value for how much space in the graph a vertex is assumed to take.
	 */
	protected Dimension vertexDim;
	/**
	 * The minimum space between vertices.
	 */
	protected double vertexBuffer;
		
	/**
	 * Constructor.
	 * 
	 * @param g 
	 *     the graph from which edge information is processed.
	 * @param vDim 
	 *     value for <code>vertexDim</code>.
	 * @param vBuffer 
	 *     value for <code>vertexBuffer</code>.
	 */
	public CircleChain(Graph g, Dimension vDim, double vBuffer) {
		super(g);
		radius = 0;
		vertexDim = vDim;
		vertexBuffer = vBuffer;
	}
	
	/**
	 * Returns the <code>CircleChain's</code> radius.
	 * 
	 * @return the <code>CircleChain's</code> radius.
	 */
	public double getRadius() 
	{
		return radius;
	}
	
	/**
	 * Places the vertices of the <code>CircleChain</code> onto the graph associated with this 
	 * <code>LayoutAlgorithm</code> in a circle.  All points assigned are in polar coordinates, 
	 * so they will eventually need to be converted in cartesian coordinates if displayed
	 * on the screen.
	 */
	public void layoutInCircle() 
	{
		layout(0, Math.PI, 2*Math.PI);		
	}
	
	/**
	 * Places the vertices of the <code>CircleChain</code> onto the graph associated with this 
	 * <code>LayoutAlgorithm</code> in a circle or part of a circle.  All points assigned are in polar
	 * coordinates, so they will eventually need to be converted in cartesian coordinates if displayed
	 * on the screen.
	 * 
	 * @param r 
	 *     the starting radius from the graph's center from which the <code>radius</code> is calculated.
	 *     This is not necessarily the final <code>radius</code>, as the final <code>radius</code>, in order to
	 *     give each vertex ample room in the graph, gets larger based on the number of vertices in the chain. 
	 * @param midTheta
	 *     the degree, in radians, that represents the midpoint of the <code>CircleChain</code>
	 *     with respect to the center of the graph.
	 * @param span 
	 *     The degree, in radians, that represents the maximum degree of the circle part over which the 
	 *     <code>CircleChain</code> can be laid out.  This degree is in reference to a circle who's center is the 
	 *     graph's center.
	 */
	public void layout(double r, double midTheta, double span)
	{
		//Is distance from a corner of the alloted space of a vertex to the center.
		double diagonalLength = Math.sqrt(Math.pow(vertexDim.getHeight(), 2) + Math.pow(vertexDim.getWidth(), 2))
										  + vertexBuffer;	

		if (size() == 0)
			return;
		if (size() == 1) {
			if (r==0)
				graph.moveVertex(vertices.get(0), new Point2D.Double(0, 0));
			else
				graph.moveVertex(vertices.get(0), new Point2D.Double(r+diagonalLength, midTheta));
			return;
		}
		
		double startTheta, thetaDivision;
		int divisions;
		startTheta = midTheta - span / 2;			
				
		if (2* Math.PI - span < .0001) //aka if it's a circle
			divisions = size();		
		else
			divisions = size() - 1;
		thetaDivision = span / divisions;				
		
		//radius = circumference / 2pi = (diagonalLength * (2pi / thetaDivision)) / 2pi = diagonalLength / thetaDivision	
		radius = diagonalLength / thetaDivision;
		
		//If starting radius + diagonallength > R, we want to use that as the radius from the center of
		//the graph instead.
		if (radius < r + diagonalLength)
			radius = r + diagonalLength;
		
		for (int i=0; i<size(); i++)
			graph.moveVertex(get(i), new Point2D.Double(radius, startTheta + thetaDivision * i));
	}
}
