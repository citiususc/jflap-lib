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

import java.util.*;
import java.awt.*;
import javax.swing.*;

import edu.duke.cs.jflap.automata.graph.Graph;
import edu.duke.cs.jflap.automata.graph.LayoutAlgorithm;

import java.awt.geom.Point2D;

/**
 * This layout algorithm arranges vertices according to a circle algorithm.  Vertices
 * with a relatively high degree are placed in an inner circle.  Those with lower degrees
 * are placed in an outer circle.  Outer circle vertices that link to a inner circle vertex
 * are placed near that vertex in the outer circle.  All vertices are moved, despite any 
 * that may be flagged as nonmovable.
 * 
 * @see LayoutAlgorithm
 * @author Chris Morgan
 */
public class TwoCircleLayoutAlgorithm extends LayoutAlgorithm {		
	/**
	 * The graph onto which the vertices will be laid out
	 */
	public Graph graph;
	/**
	 * The vertices associated with the graph
	 */
	ArrayList vertices;
	/**
	 * Two lists that represent the division of the vertices into an inner and an outer circle
	 */
	ArrayList innerCircle, outerCircle;
	/**
	 * <code>VertexChains</code> that represent values in the outer circle.  Each <code>VertexChain</code> corresponds 
	 * to a vertex in the <code>innerCircleChain</code>, and values in each <code>outerCircleChain</code> are laid
	 * up opposite to its corresponding vertex.  The <code>outerCircleChains</code>, when laid out on the
	 * graph, do not necessarily have the same radius in their layouts, as this varies based on the number
	 * of vertices in each <code>outerCircleChain</code>.
	 */
	CircleChain[] outerCircleChains;
	/**
	 * The <code>VertexChain</code> that represents values in the inner circle
	 */
	CircleChain innerCircleChain;	
	
	/**
	 * Assigns some default values.  To have different values, use the other constructor.
	 */
	public TwoCircleLayoutAlgorithm() 
	{
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
	public TwoCircleLayoutAlgorithm(Dimension pSize, Dimension vDim, double vBuffer)
	{
		super(pSize, vDim, vBuffer);
	}
	
	
	public void layout(Graph g, Set notMoving) 
	{
		//First, initialize classwide variables.
		graph = g;		
		innerCircle = new ArrayList();
		outerCircle = new ArrayList();
		vertices = getMovableVertices(graph, notMoving);
		if (graph==null || vertices.size() == 0)
			return;
		
		//Put in the inner circle all vertices with degree >= 3 and all those pointing to two members of the inner circle.				
		assignToCircles();
		
		//Create inner circle chain	and place it in graph
		innerCircleChain = new CircleChain(graph, vertexDim, vertexBuffer);
		for (int i=0; i<innerCircle.size(); i++)
			innerCircleChain.addVertex(innerCircle.get(i));		
		innerCircleChain.layout(0, Math.PI, 2*Math.PI);
		innerCircle = innerCircleChain.getVertices();  //getting correct order for outer circle chains
		
		//Create outer circle chains and place them in the graph.
		if (outerCircle.size()>0) {					
			createOuterCircleChains();
			shuffleOuterChains();
			double radius, span, division;			
			radius = innerCircleChain.getRadius();
			division = 2*Math.PI / outerCircleChains.length;
			span = division * 4/5;			
			for (int i=0; i<outerCircleChains.length; i++)
				outerCircleChains[i].layout(radius, division*i, span);
		}
		
		//Finally, adjust the points so that they can be presented on the screen.
		polarToCartesian(graph, vertices);
		shiftOntoScreen(graph, size, vertexDim, true);
	}
	
	/**
	 * Divides the vertices by placing them either in the inner circle or outer circle.  All inner circle vertices will have 
	 * a degree greater than 2 or will be adjacent to two other inner circle vertices.  The rest of the vertices are placed
	 * in the outer circles. 
	 */
	protected void assignToCircles() 
	{		
		for (int i=0; i<vertices.size(); i++)
			if (graph.degree(vertices.get(i)) > 2)
				innerCircle.add(vertices.get(i));
			else
				outerCircle.add(vertices.get(i));
		if (innerCircle.size()==0) {
			innerCircle = outerCircle;
			outerCircle = new ArrayList();
			return;
		}
		
		boolean innerCircleInsertion;
		int count;		
		do {			
			innerCircleInsertion = false;			
			for (int i=0; i<outerCircle.size(); i++) {
				count = 0;
				for (int j=0; j<innerCircle.size(); j++)					
					if (graph.hasEdge(outerCircle.get(i), innerCircle.get(j)))
						count++;
				if (count>=2) {
					innerCircle.add(outerCircle.get(i));
					outerCircle.remove(i);
					innerCircleInsertion = true;
				}					
			}		
		} while (innerCircleInsertion);
	}
	
	/**
	 * Divides the vertices that are in the outer circle into <code>VertexChains</code>, which correspond to an inner circle
	 * vertex.  Outer circle vertices are assigned to a specific <code>VertexChain</code> according to the following priorities. 
	 * <br><br> 1. Whether they link to an inner circle vertex <br> 2.  Whether they link to an existing outer circle item <br>
	 * 3.  If they link to two outer circle items in different <code>VertexChains</code> or have a degree = 0, they are
	 * assigned to the smaller of the two <code>VertexChains</code> or the smallest existing <code>VertexChain</code>, 
	 * respectively.
	 */
	protected void createOuterCircleChains() 
	{
		outerCircleChains = new CircleChain[innerCircle.size()];
		int[] chainIndex = new int[outerCircle.size()];
		for (int i=0; i<outerCircleChains.length; i++)
			outerCircleChains[i] = new CircleChain(graph, vertexDim, vertexBuffer);
		
		//First add the outerCircle vertices linking to an innerCircle vertex to the corresponding chain.
		for (int i=0; i<outerCircle.size(); i++) {
			chainIndex[i] = -1;
			for (int j=0; j<innerCircle.size(); j++)				
				if (graph.hasEdge(outerCircle.get(i), innerCircle.get(j))) {					
					outerCircleChains[j].addVertex(outerCircle.get(i));
					chainIndex[i] = j;
				}
		}
		
		//Next, if a vertex is linked to an outercircle item, add it; if to items in two different chains, add
		//it to the one with the minimum size.
		int match1, match2, min;
		boolean addedToChain = false;
		do {
			addedToChain = false;
			for (int i=0; i<outerCircle.size(); i++) {				
				if (chainIndex[i] == -1) {
					match1 = -1;
					match2 = -1;
					for (int j=0; j<outerCircleChains.length; j++)					
						if (outerCircleChains[j].isEdgeToChainMember(outerCircle.get(i)) && chainIndex[i] == -1)
							if (match1 == -1)
								match1 = j;
							else
								match2 = j;
					if (match1 > -1 && match2 == -1) {						
						outerCircleChains[match1].addVertex(outerCircle.get(i));
						chainIndex[i] = match1;
						addedToChain = true;
					}
					else if (match1 > -1 && match2 > -1) {
						if (outerCircleChains[match1].size() < 
							outerCircleChains[match2].size())
							min = match1;
						else
							min = match2;						
						outerCircleChains[min].addVertex(outerCircle.get(i));
						chainIndex[i] = min;
						addedToChain = true;
					}
				}									
			}
		} while (addedToChain);	
		
		//If no vertex can be added, find the chain with minimum length, and then add all unplaceded vertices to it.
		min = 0;
		for (int i=0; i<outerCircleChains.length; i++)
			if (outerCircleChains[min].getVertices().size() > outerCircleChains[i].getVertices().size())
				min = i;
		for (int i=0; i<outerCircle.size(); i++)
			if (chainIndex[i] == -1)
				outerCircleChains[min].addVertex(outerCircle.get(i));			
	}
	
	/**
	 * Shuffles the order of vertices in the outer circle <code>CircleChains</code> in order to minimize overlapping
	 * transitions between vertices in two different <code>CircleChains</code>.  If a vertex from one <code>CircleChain
	 * </code> has an edge to another vertex in an adjacent <code>CircleChain</code>, the two vertices will be moved to 
	 * their <code>CircleChain's</code> common border, and whatever other vertices to which the two vertices are linked 
	 * will be adjusted accordingly. 
	 */
	protected void shuffleOuterChains() {
		CircleChain currentChain, nextChain;	
		for (int i=0; i<outerCircleChains.length; i++) {
			currentChain = outerCircleChains[i];
			if (i<outerCircleChains.length-1) 
				nextChain = outerCircleChains[i+1];							
			else
				nextChain = outerCircleChains[0];			
			VertexChain.alignTwoChains(currentChain, nextChain, graph);											
		}
	}				
	
	/**
	 * Some test code used to test out layout algorithms graphically without goint through JFLAP.
	 */
	public static void main(String[] args) {	
		JFrame f = new JFrame();
		JPanel p = new JPanel();
    	f.setSize(900, 900);
    	f.setContentPane(p);    	    	
    	f.setLocationRelativeTo(null); // center
    	f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	f.setVisible(true);
    	
    	//Point2D.Double center = new Point2D.Double(f.getWidth()/2, f.getHeight()/2);
		String[] vertices = new String[20];
		Graph graph = new Graph();
		LayoutAlgorithm layout = new RandomLayoutAlgorithm();		
		for (int i=0; i<vertices.length; i++) {
			vertices[i] = "V" + i;
			graph.addVertex(vertices[i], new Point2D.Double(0,0));
			if (i>0)
				graph.addEdge(vertices[i], vertices[i-1]);
		//	if (i>1 && i<5)
		//		graph.addEdge(vertices[i], vertices[i-2]);
		}
	//  graph.addEdge(vertices[4], vertices[8]);
	//	graph.addEdge(vertices[13], vertices[8]);
	//	graph.addEdge(vertices[4], vertices[13]);
	//	graph.addEdge(vertices[13], vertices[16]);
	//	graph.addEdge(vertices[4], vertices[16]);
	//	graph.addEdge(vertices[39], vertices[17]);		
	/*	graph.addEdge(vertices[0], vertices[1]);
		graph.addEdge(vertices[2], vertices[0]);
		graph.addEdge(vertices[1], vertices[2]);
		graph.addEdge(vertices[3], vertices[1]);
		graph.addEdge(vertices[3], verticesadjacent(finalStates[i]).size()[0]);
		graph.addEdge(vertices[3], vertices[2]);*/
		HashSet set = new HashSet();
		//temp.add(vertices[6]);

		layout.layout(graph, set);
						
		for (int i=0; i<vertices.length; i++) {		
			Point2D p2d = graph.pointForVertex(vertices[i]);			
			p.getGraphics().drawString(vertices[i], (int)(p2d.getX()), (int)(p2d.getY()) - 10);
			p.getGraphics().fillRect((int)(p2d.getX()), (int)(p2d.getY()), 30, 30);
		}
		for (int i=0; i<vertices.length; i++)
			for (int j=0; j<vertices.length; j++)	
				if (graph.hasEdge(vertices[i], vertices[j])){					
					p.getGraphics().drawLine((int) (graph.pointForVertex(vertices[i]).getX()),
											 (int) (graph.pointForVertex(vertices[i]).getY()),
											 (int) (graph.pointForVertex(vertices[j]).getX()),
											 (int) (graph.pointForVertex(vertices[j]).getY()));}
		p.revalidate();
	}
}
