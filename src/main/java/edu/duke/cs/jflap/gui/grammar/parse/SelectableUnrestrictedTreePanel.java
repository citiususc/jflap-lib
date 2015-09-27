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




package edu.duke.cs.jflap.gui.grammar.parse;

import edu.duke.cs.jflap.grammar.parse.ParseNode;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.Map;

import javax.swing.tree.TreeNode;

/**
 * This class allows user to select items in the tree Panel.
 * This class was intended to be used with UserControlParsePane,
 * however we decided to not to use it.
 * 
 * However, this class is still called from UserControlParsePane for drawing. 
 * 
 * Could be useful in the future. 
 * 
 * @author Kyung Min (Jason) Lee
 *
 */
public class SelectableUnrestrictedTreePanel extends UnrestrictedTreePanel{

	private boolean myClicked=false;
	private Point2D myClickedNodePoint;
	
	private Color myColor;
	private static final Color CLICKED_COLOR=new Color(100,120,120);

	/**
	 * Constructor for SelectableUnrestrictedTreePanel 
	 * @param pane pane that is going to contain this tree panel
	 */
	public SelectableUnrestrictedTreePanel(BruteParsePane pane) {
		super(pane);
		myColor=super.INNER;
	}
	
	/**
	 * Returns the node at a particular point.
	 * 
	 * @param point
	 *            the point to check for nodeness
	 * @return the treenode at a particular point, or <CODE>null</CODE> if
	 *         there is no treenode at that point
	 */
	public TreeNode nodeAtPoint(Point2D point) {
		double x1=point.getX();
		double y1=point.getY();
		Iterator it = nodeToPoint.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry e = (Map.Entry) it.next();
			Point2D tempPoint=(Point2D) e.getValue();
			double x2=tempPoint.getX();
			double y2=tempPoint.getY();
			if (Math.pow((x2-x1), 2)+Math.pow((y2-y1), 2)<=Math.pow(nodeDrawer.NODE_RADIUS,2))
			{
				myClicked=true;
				myClickedNodePoint=new Point2D.Double(x2,y2);
		//		repaint();
				return (TreeNode)e.getKey();
			}
		}
		return null;
	}
	
	public Point2D getPointofSelectedNode()
	{
		if (myClicked)
			return myClickedNodePoint;
		return null;
			
	}
	
	
	/**
	 * Sets the answer to this tree panel.
	 * 
	 * @param answer
	 *            the end result of a parse tree derivation, or <CODE>null</CODE>
	 *            if no answer should be displayed
	 */
	
	public void setAnswer(ParseNode answer) {
		if (answer==null)
		{
			top=null;
			return;
		}
		super.setAnswer(answer);
		for (int i=1; i<solutionParseNodes.length; i++)
		{
			for (int j=0; j<solutionParseNodes[i].getSubstitutions().length; j++) 
				next();
		}
	}

	public void paintNode(Graphics2D g, UnrestrictedTreeNode node, Point2D p) {
	//	System.out.println("node out : "+node.getText());
		
		g.setColor(node.lowest == top.length - 1 ? LEAF : INNER);
		if (node.getText().toUpperCase().equals(node.getText()) && !node.getText().equals(""))
			g.setColor(INNER);
		
		
		
		
	/*	else
		{
		//	System.out.println("node : "+node.getText());
		//	System.out.println("myPrev = "+myPrev);
			if (myPrev==false)
				g.setColor(INNER);
		}
		//System.out.println("node out : "+node.getText());
		
		if (node.getText().equals("("))
			myPrev=false;
		if (node.getText().equals(")"))
			myPrev=true;
		*/
		g.translate(p.getX(), p.getY());
		nodeDrawer.draw(g, node);
		g.translate(-p.getX(), -p.getY());
	}
	
	//private boolean myPrev=true;
}
