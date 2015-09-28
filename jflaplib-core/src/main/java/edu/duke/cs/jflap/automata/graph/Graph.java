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

import java.util.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * A graph data structure. The idea behind the graph data structure is that a
 * vertex is just some sort of data structure whose type is not important, and
 * associated with a point. There is therefore no explicit node structure.
 * 
 * @author Thomas Finley
 */

public class Graph {
	/** Creates a new empty graph structure. */
	public Graph() {

	}

	/** Clears all vertices and edges. */
	public void clear() {
		verticesToPoints.clear();
		verticesToNeighbors.clear();
	}

	/** Returns the degree of a vertex. */
	public int degree(Object vertex) {
		return adjacent(vertex).size();
	}

	/** Returns the number of vertices. */
	public int numberOfVertices() {
		return verticesToPoints.size();
	}

	/** Returns the set of vertices a vertex is adjacent to. */
	public Set adjacent(Object vertex) {
		if (!verticesToNeighbors.containsKey(vertex))
			verticesToNeighbors.put(vertex, new HashSet());
		return (Set) verticesToNeighbors.get(vertex);
	}

	/** Adds an edge between two vertices. */
	public void addEdge(Object vertex1, Object vertex2) {
		adjacent(vertex1).add(vertex2);
		adjacent(vertex2).add(vertex1);
	}

	/** Removes an edge between two vertices. */
	public void removeEdge(Object vertex1, Object vertex2) {
		adjacent(vertex1).remove(vertex2);
		adjacent(vertex2).remove(vertex1);
	}

	/** Returns if an edge exists between two vertices. */
	public boolean hasEdge(Object vertex1, Object vertex2) {
		return adjacent(vertex1).contains(vertex2);
	}

	/** Adds a vertex. */
	public void addVertex(Object vertex, Point2D point) {
		verticesToPoints.put(vertex, point.clone());
	}

	/** Removes a vertex. */
	public void removeVertex(Object vertex) {
		Set others = adjacent(vertex);
		Iterator it = others.iterator();
		while (it.hasNext())
			adjacent(it.next()).remove(vertex);
		verticesToNeighbors.remove(vertex);
		verticesToPoints.remove(vertex);
	}

	/** Moves a vertex to a new point. */
	public void moveVertex(Object vertex, Point2D point) {
		addVertex(vertex, point);
	}

	/** Returns the point for a given vertex. */
	public Point2D pointForVertex(Object vertex) {
		return (Point2D) verticesToPoints.get(vertex);
	}

	/** Returns the list of vertex objects. */
	public Object[] vertices() {
		return verticesToPoints.keySet().toArray();
	}

	/**
	 * Returns the list of vertex points. The order they appear is not
	 * necessarily the same as the vertices.
	 */
	public Point2D[] points() {
		return (Point2D[]) verticesToPoints.values().toArray(new Point2D[0]);
	}

	/** Reforms the points so they are enclosed within a certain frame. */
	public void moveWithinFrame(Rectangle2D bounds) {
		Object[] vertices = vertices();
		if (vertices.length == 0)
			return;
		Point2D p = pointForVertex(vertices[0]);
		double minx = p.getX(), miny = p.getY(), maxx = minx, maxy = miny;
		for (int i = 1; i < vertices.length; i++) {
			p = pointForVertex(vertices[i]);
			minx = Math.min(minx, p.getX());
			miny = Math.min(miny, p.getY());
			maxx = Math.max(maxx, p.getX());
			maxy = Math.max(maxy, p.getY());
		}
		// Now, scale them!
		for (int i = 0; i < vertices.length; i++) {
			p = pointForVertex(vertices[i]);
			p = new Point2D.Double((p.getX() - minx) * bounds.getWidth()
					/ (maxx - minx) + bounds.getX(), (p.getY() - miny)
					* bounds.getHeight() / (maxy - miny) + bounds.getY());
			moveVertex(vertices[i], p);
		}
	}

	/** Returns a string description of the graph. */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(super.toString() + "\n");
		sb.append(verticesToPoints);
		return sb.toString();
	}

	protected Map verticesToPoints = new HashMap();

	protected Map verticesToNeighbors = new HashMap();
}
