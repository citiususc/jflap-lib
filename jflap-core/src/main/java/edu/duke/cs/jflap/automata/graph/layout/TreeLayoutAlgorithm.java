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
import java.util.Set;
import java.awt.Dimension;
import java.awt.geom.Point2D;

import edu.duke.cs.jflap.automata.graph.Graph;
import edu.duke.cs.jflap.automata.graph.AutomatonDirectedGraph;
import edu.duke.cs.jflap.automata.graph.LayoutAlgorithm;

/**
 * This algorithm will lay out the graph in a tree.
 * 
 * @author Chris Morgan
 */
public class TreeLayoutAlgorithm extends LayoutAlgorithm {
	/**
	 * The graph used for this LayoutAlgorithm
	 */
	protected Graph graph;
	/**
	 * If true, this is a hierarchical tree.  If false, a degree tree.
	 */
	protected boolean hierarchical;
	
	/**
	 * Assigns some default values, although one must specify whether the tree is vertical
	 * or horizontal.  To have different values, use the other constructor.
	 * 
	 * @param hier
	 *     if <code>true</code>, a hierarchy tree layout.  If false, a degree one.
	 */
	public TreeLayoutAlgorithm(boolean hier) 
	{
		super();
		hierarchical = hier;
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
	 * @param hier
	 *     if <code>true</code>, a hierarchy tree layout.  If false, a degree one.
	 */
	public TreeLayoutAlgorithm(Dimension pSize, Dimension vDim, double vBuffer, boolean hier)
	{
		super(pSize, vDim, vBuffer);
		hierarchical = hier;
	}
	
	public void layout(Graph g, Set notMoving) {
		graph = g;
		ArrayList vertices = getMovableVertices(graph, notMoving);
		if (graph==null || vertices.size() == 0)
			return;
		
		/* After checking to see if the graph has movable vertices, sort the vertices
		 * degree in the graph, and then insert them in a VertexChain to minimize
		 * edge intersections.  If hierarchical, then those vertices with the smallest
		 * number of edges in them are moved to the top.  If otherwise, then those edges 
		 * with the highest degree are moved to the top.
		 */
		if (hierarchical) {
			//It is up to the programmer to make sure the right kind of graph is present for
			//hierarchical graphs.  If not, the algorithm will return with nothing happening.
			if (!(graph instanceof AutomatonDirectedGraph))
				return;
			final AutomatonDirectedGraph adg = (AutomatonDirectedGraph) graph;			
			Collections.sort(vertices, new Comparator()	{			
				public int compare(Object o1, Object o2) {					 
					if (adg.toDegree(o1, true) == adg.toDegree(o2, true))
						return 0;
					else if (adg.toDegree(o1, true) > adg.toDegree(o2, true))
						return 1;
					else
						return -1;
			}});
		}
		else
			Collections.sort(vertices, new Comparator() {
				public int compare(Object o1, Object o2) {				
					if (graph.degree(o1) == graph.degree(o2))
						return 0;
					else if (graph.degree(o1) > graph.degree(o2))
						return -1;
					else
						return 1;
			}});					
		
		// Finally, add the vertices to levels and adjust them so all vertices are on the screen
		ArrayList notPlaced = new ArrayList();		
		notPlaced.addAll(vertices);
		Level firstLevel, counter;
		firstLevel = new Level();		
		while (notPlaced.size() > 0) {
			firstLevel.vertices.add(notPlaced.get(0));
			notPlaced.remove(notPlaced.get(0));
			counter = firstLevel;
			while (counter!=null && notPlaced.size() > 0) {
				counter.processChildren(notPlaced);
				counter = counter.nextLevel;
			}			
		}
		firstLevel.layout(0);
		shiftOntoScreen(graph, size, vertexDim, true);
	}
	
	/**
	 * This class represents a level in the tree.  It points to the next level in the hierarchy, so thus
	 * the levels are implemented as a linked list.
	 * 
	 * @author Chris Morgan
	 */
	private class Level {
		/**
		 * The list of vertices in this level.
		 */
		public ArrayList vertices;
		/**
		 * The next level in the hierarchy.
		 */
		public Level nextLevel;
				
		/**
		 * The constructor.
		 */
		public Level() {
			vertices = new ArrayList();
			nextLevel = null;
		}
		
		/**
		 * This method checks the list of vertices that haven't been placed in a level to determine if any
		 * vertices in this level have any non-placed vertices as children.  All children found are placed in
		 * the next level down the hierarchy.
		 * 
		 * @param notPlaced
		 */
		public void processChildren(ArrayList notPlaced) {
			VertexChain chain, lastChain;
			lastChain = null;
						
			for (int i=0; i<vertices.size(); i++) {
				//For each vertex, add its children to a VertexChain, which helps in minimizing intralevel
				//edge overlaps.
				chain = new VertexChain(graph);							
				for (int j=notPlaced.size()-1; j>=0; j--) {
					if (graph.hasEdge(vertices.get(i), notPlaced.get(j)) && 
						!vertices.get(i).equals(notPlaced.get(j))) {						
						chain.addVertex(notPlaced.get(j));
						notPlaced.remove(j);						
					}
				}
				
				/* Then, align this VertexChain to the last chain generated in the level to minimize overlaps.				
				 * If there are vertices in the last chain, add the vertices in the last chain generated to the next, 
				 * level, since the last chain is done with alignment.  Define the next level if necessary.  After
				 * this, set the current chain to be the last chain.
				 */
				if (lastChain!=null) {
					VertexChain.alignTwoChains(lastChain, chain, graph);				
					if (lastChain.size() > 0) {
						if (nextLevel == null)
							nextLevel = new Level();
						nextLevel.vertices.addAll(lastChain.getVertices());
					}
				}
				lastChain = chain;
			}
			//Finally, add the last chain generated to the graph.
			if (lastChain != null && lastChain.size() > 0) {
				if (nextLevel == null)
					nextLevel = new Level();
				nextLevel.vertices.addAll(lastChain.getVertices());					
			}
		}
		
		/**
		 * Lay out all vertices in the current row, centering the vertex row along the central 
		 * vertical axis of the tree for symmetry.  This method also calls the layout method of the
		 * next level, so the tree will be laid out throughout the linked list through recursion.
		 * 
		 * @param height the height of the current row.
		 */
		public void layout(double height) {
			double currentX = -1.0 * vertices.size() * (vertexDim.getWidth() + vertexBuffer) / 2;
			for (int i=0; i<vertices.size(); i++) {
				graph.moveVertex(vertices.get(i), new Point2D.Double(currentX, height));				
				currentX += vertexBuffer + vertexDim.getWidth();
			}
			if (nextLevel != null)
				nextLevel.layout(height + vertexDim.getHeight() + vertexBuffer);
		}
	}
}
