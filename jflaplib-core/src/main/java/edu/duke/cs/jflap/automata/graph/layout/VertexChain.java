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

import edu.duke.cs.jflap.automata.graph.Graph;

/**
 * Class that orders vertices assigned to it one-dimensionally.  The order of the vertices is changed as vertices
 * are added in order to minimize the number of edges between non-adjacent vertices.
 * 
 * @author Chris Morgan
 */
public class VertexChain{
	/**
	 * List of vertices in the chain.
	 */
	ArrayList vertices;	
	/**
	 * The graph from which edge information is processed.
	 */
	Graph graph;
	
	/**
	 * Constructor.
	 * 
	 * @param g - The graph from which edge information is processed.
	 */
	public VertexChain(Graph g) 
	{
		vertices = new ArrayList();
		graph = g;
	}				
	
	/**
	 * Returns the object held in <code>vertices</code> in the given index.
	 * 
	 * @return the object in the given index.
	 */
	public Object get(int index) {
		return vertices.get(index);
	}
	
	/**
	 * Returns the ArrayList containing the vertices in the <code>VertexChain</code>.
	 * 
	 * @return the vertices in the chain.
	 */
	public ArrayList getVertices()
	{
		return vertices;
	}
	
	/**
	 * Returns the number of elements stored in this chain.
	 * 
	 * @return the number of elements stored in this chain.
	 */
	public int size() {
		return vertices.size();
	}
	
	/**
	 * Returns whether the given vertex has an edge to a <code>VertexChain</code> member.
	 *  
	 * @return whether the given vertex has an edge to a <code>VertexChain</code> member.
	 */
	public boolean isEdgeToChainMember(Object vertex) 
	{
		if (getDegreeInChain(vertex) > 0)
			return true;
		return false;
	}
	
	/**
	 * Returns the degree of a given vertex with respect to current vertices in the <code>VertexChain</code>.
	 * The vertex does not need to be in the graph, but is not counted toward the degree if it is.
	 * 
	 * @return the given vertex's degree with respect to the <code>VertexChain</code>.
	 */
	public int getDegreeInChain(Object vertex) 
	{
		int count = 0;
		for (int i=0; i<vertices.size(); i++)
			if (graph.hasEdge(vertex, vertices.get(i)) && !vertices.get(i).equals(vertex))
				count++;
		return count;
	}
	
	/**
	 * Adjusts the order of the given subchain in the <code>VertexChain</code>.  The subchain is removed
	 * from its present position in the <code>VertexChain</code> and placed next to a given vertex.
	 * 
	 * @param destIndex 
	 *     the index of the vertex in the <code>VertexChain</code> next to which the subchain will be placed.
	 * @param matchingIndex 
	 *     either the <code>start</code> or the <code>end</code> value.  Determines the vertex that is
	 *     closest to the <code>destIndex's</code> vertex in the new <code>VertexChain</code> ordering.  Often 
	 *     is the index of a vertex linking to the <code>destIndex's</code> vertex.
	 * @param start 
	 *     the index in the <code>VertexChain</code> representing the start of the subchain.
	 * @param end 
	 *     the index in the <code>VertexChain</code> representing the end of the subchain.
	 * @param shuffleDirection 
	 *     if true, the subchain will be placed to the right of the <code>destIndex's</code> vertex.
	 *     If false, to the left.
	 */
	public void orientSubChain(int destIndex, int matchingIndex, int start, int end, boolean shuffleDirection) {
		Object[] toMove = new Object[end-start+1];
		int dest, chainSize;
		chainSize = size();
		if (destIndex > 0 && destIndex >= start)
			dest = destIndex+start-end-1;
		else
			dest = destIndex;			
		
		for (int i=start; i<=end; i++)  
			toMove[i-start] = get(i);
		for (int i=0; i<toMove.length; i++) 
			vertices.remove(toMove[i]);
		for (int i=0; i<toMove.length; i++)
			if (shuffleDirection) {		
				if (destIndex == chainSize || dest == size()) {
					if (matchingIndex == start) 						
				    	vertices.add(toMove[toMove.length-1-i]);
				    else
				    	vertices.add(toMove[i]);
				}
				else if (matchingIndex == start) 						
			    	vertices.add(dest+1, toMove[toMove.length-1-i]);
			    else
			    	vertices.add(dest+1, toMove[i]);
			}
			else if (matchingIndex == start)
				vertices.add(dest, toMove[i]);
			else
				vertices.add(dest, toMove[toMove.length-1-i]);	
	}	
	
	/**
	 * Adds a given vertex to the <code>VertexChain</code>, adjusting the <code>VertexChain's</code> order to
	 * minimize the number of edges between non-adjacent vertices.
	 * 
	 * @param vertex the vertex to be added
	 */
	public void addVertex(Object vertex) 
	{
		 int destIndex, subChainBound;
		 for (int i=0; i<size(); i++)
			 if (graph.hasEdge(vertex, get(i))) {
				 //If there is an open node to the right of the vertex, then it is inserted there.
				 if (i==size()-1 || !graph.hasEdge(get(i), get(i+1)))
					 destIndex = i+1;
				 //Otherwise, then the node is inserted to the left.
				 else
					 destIndex = i;				 					 
				 vertices.add(destIndex, vertex);
				 					 
				 //Now check to see if there is at least one more node in the chain that this links to.  That node,
				 //if it has a degree <= 2 (inc. the newly added node), can be put on the opposite side of this.
				 for (int j=i+2; j<size(); j++)
					 if (graph.hasEdge(vertex, get(j)) && getDegreeInChain(get(j)) <= 2) { 						 					
						 if (j<size()-1 && graph.hasEdge(get(j), get(j+1)))
						 	orientSubChain(destIndex, j, j, size()-1, (destIndex==i+1));
						 else {
						  	subChainBound = j;
						  	while (subChainBound>i+2 && graph.hasEdge(get(subChainBound-1), 
								get(subChainBound)))
							 	subChainBound--;
						 	orientSubChain(destIndex, j, subChainBound, j, (destIndex==i+1));
						}
						return;
					 }
				 return;
			 }			 
		 //Added at end if there are no adjacencies.
		 vertices.add(vertex);
	}
	
	/**
	 * If there is an edge between a vertex in <code>first</code> and a vertex in <code>last</code>, then the two
	 * vertices are moved in their respective chains to their common border, with subchains in tow behind them.  This
	 * only happens for the first matching pair, and other matching pairs have no effect.  The vertex in 
	 * <code>first</code> will be moved to the end of vertices in its <code>VertexChain</code>, and the vertex in 
	 * <code>last</code> will be moved to the beginning of vertices in its <code>VertexChain</code>.
	 * 
	 * @param first
	 *     the first <code>VertexChain</code> to search
	 * @param next 
	 *     the second <code>VertexChain</code> to search
	 * @param graph 
	 *     the graph used to search for edges
	 */
	public static void alignTwoChains(VertexChain first, VertexChain next, Graph graph) {
		int fstart, fend, nstart, nend;	
		for (int j=0; j<first.size(); j++)
			for (int k=0; k<next.size(); k++)
				if (first.getDegreeInChain(first.get(j)) < 2 &&
					next.getDegreeInChain(next.get(k)) < 2 && 
					graph.hasEdge(first.get(j), next.get(k))) {						
					fstart=j;   fend=j;   nstart=k;   nend=k;
					while (fstart > 0 && graph.hasEdge(first.get(fstart), first.get(fstart-1)))
						fstart--;
					while (fend < first.size()-1 && graph.hasEdge(first.get(fend), first.get(fend+1)))
						fend++;
					while (nstart > 0 && graph.hasEdge(next.get(nstart), next.get(nstart-1)))
						nstart--;
					while (nend < next.size()-1 && graph.hasEdge(next.get(nend), next.get(nend+1)))
						nend++;
					first.orientSubChain(first.size()-1, fstart+fend-j, fstart, fend, true);
					next.orientSubChain(0, nstart+nend-k, nstart, nend, false);
					return;
				}
	}
}
