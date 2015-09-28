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

import java.util.Set;
import java.util.ArrayList;
import java.awt.Dimension;
import java.awt.geom.Point2D;

import edu.duke.cs.jflap.automata.graph.Graph;
import edu.duke.cs.jflap.automata.graph.LayoutAlgorithm;

/**
 * A layout algorithm that lays out groupings of vertices in circles.  Each grouping
 * consists of all vertices between which a path exists between any two vertices, and no
 * vertices in different groupings will have an edge between them.  If all vertices are
 * reachable from every one of the other vertices along a path, only one circle will be
 * drawn, with some edge minimizing code helping reduce edge intersections.
 * 
 * @author Chris Morgan
 */
public class CircleLayoutAlgorithm extends LayoutAlgorithm {
	/**
	 * This list contains all the boxes that are used in this algorithm.
	 */
	private ArrayList boxes;
	
	/**
	 * Assigns some default values.
	 */
	public CircleLayoutAlgorithm() {
		super();
	}
	
	/**
	 * Constructor allowing the user to customize certain values.
	 * 
	 * @param pSize 
	 *     value for <code>size</code>.
	 * @param vDim
	 *     value for <code>vertexDim</code>.
	 * @param vBuffer
	 *     value for <code>vertexBuffer</code>.
	 */
	public CircleLayoutAlgorithm(Dimension pSize, Dimension vDim, double vBuffer) {
		super(pSize, vDim, vBuffer);
	}
	
	
	public void layout(Graph graph, Set notMoving) {
		ArrayList vertices = getMovableVertices(graph, notMoving);
		if (graph==null || vertices.size() == 0)
			return;
			
		boxes = new ArrayList();		
		for (int i=0; i<vertices.size(); i++)
			if (!addToExistingBox(vertices.get(i))) {
				Box box = new Box(graph, vertexDim, vertexBuffer);
				box.addVertex(vertices.get(i));
				boxes.add(box);
			}
		
		for (int i=boxes.size()-1; i>0; i--)
			mergeIfPossible((Box) boxes.get(i), i);		
		
		for (int i=0; i<boxes.size(); i++)
			((Box) boxes.get(i)).layoutInCircleAndPack();
				
		shiftOntoScreen(graph, size, vertexDim, true);
	}
		
	/**
	 * Adds the given vertex to a box if it has an edge to one of them.
	 * 
	 * return Whether the given vertex was added.
	 */
	private boolean addToExistingBox(Object vertex) {
		for (int i=0; i<boxes.size(); i++)
			if (((Box) boxes.get(i)).isEdgeToChainMember(vertex)) {
				((Box) boxes.get(i)).addVertex(vertex);
				return true;
			}
		return false;
	}

	/**
	 * Tries to merge two boxes, and will do so if one vertex in the given
	 * box has an edge with any boxes in the list of boxes.
	 * 
	 * @param current 
	 *     the box for which the list will be searched for matches.
	 * @param max 
	 *     the maximum box, exclusive, in the list of boxes to search 
	 *     for matches.  All boxes in the list from 0 to max-1 are searched.
	 */
	private void mergeIfPossible(Box current, int max) {
		Box toSearch;
		for (int j=max-1; j>=0; j--) {
			toSearch = (Box) boxes.get(j);
			for (int k=0; k<current.size(); k++)
				if (toSearch.isEdgeToChainMember(current.get(k))) {					
					toSearch.merge(current);
					boxes.remove(current);
					return;
				}
		}
	}
	
	/**
	 * A box is a <code>CircleChain</code> with some additional code for placing it in the
	 * graph so that no box will be on top of another.
	 * 
	 * @author Chris Morgan
	 */
	private class Box extends CircleChain {
		/**
		 * The size of the square in which only this box may layout values.
		 */
		public Dimension size;
		/**
		 * Pointers to boxes immediately to the right and below this box.  They allow for the
		 * boxes to form a linked list according to how they are laid out.
		 */
		public Box down, right;
		/**
		 * The point in the upper left corner of the box.  All points determined by the <code>
		 * CircleChain</code> layout functions are relative to this point.
		 */
		public Point2D upperLeft;		
		
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
		public Box(Graph g, Dimension vDim, double vBuffer) {
			super(g, vDim, vBuffer);
			upperLeft = new Point2D.Double(0, 0);
			right = null;
			down = null;
		}
		
		/**
		 * Moves all vertices in the box given into this box.
		 * 
		 * @param b the box whose vertices will be added to this one.
		 */
		public void merge(Box b) {
			for (int i=0; i<b.size(); i++)
				addVertex(b.get(i));
		}
					
		/**
		 * Sets the value of the point representing this box's upper-left corner.  This is calculated
		 * by traversing the linked list the boxes form and attempting to find an open space.  If
		 * the height of this box is less than or equal to the parameter box, then this box's upperLeft 
		 * point will be in the first available point to the right of the parameter box, with the same
		 * height.  If not, the point will be below and perhaps to the right of the parameter box.   
		 * 
		 * @param current the current box in the traversal of the linked list.
		 */
		public void setUpperLeft(Box current) {
			if (size.getHeight() <= current.size.getHeight()) {
				upperLeft = new Point2D.Double(upperLeft.getX() + current.size.getWidth(), upperLeft.getY());
				while (current.right!=null) {					
					current = current.right;
					upperLeft = new Point2D.Double(upperLeft.getX() + current.size.getWidth(), upperLeft.getY());
				}
				current.right = this;				
				return;
			}
			upperLeft = new Point2D.Double(upperLeft.getX(), upperLeft.getY() + current.size.getHeight());
			if (current.down==null)
				current.down = this;
			else
				setUpperLeft(current.down);			
		}		
		
		/**
		 * Lays out the contents of the box in a circle and finds room on the screen to place the box.  
		 * All vertex points are returned as Cartesian points, and not polar points, as is done in
		 * <code>CircleChain.layoutInCircle()</code>.
		 */
		public void layoutInCircleAndPack() {
			layoutInCircle();
			polarToCartesian(graph, getVertices());
			size = new Dimension((int) (2 * (getRadius() + vertexBuffer) + vertexDim.width),
					(int) (2 * (getRadius() + vertexBuffer) + vertexDim.height));
			if (boxes.indexOf(this) != 0) {
				setUpperLeft((Box) boxes.get(0));
				for (int i=0; i<size(); i++)
					graph.moveVertex(get(i), new Point2D.Double(
						upperLeft.getX() + graph.pointForVertex(get(i)).getX(), 
						upperLeft.getY() + graph.pointForVertex(get(i)).getY()));
			}
		}
	}
}
