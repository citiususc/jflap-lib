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

import edu.duke.cs.jflap.grammar.*;
import edu.duke.cs.jflap.grammar.parse.*;
import edu.duke.cs.jflap.gui.environment.Universe;
import edu.duke.cs.jflap.gui.tree.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.RenderingHints;
import java.awt.geom.*;
import java.util.*;
import javax.swing.tree.*;

/**
 * This is the special drawer for an unrestricted parse tree. Woe betide any
 * that try to understand its inner workings.
 * 
 * @author Thomas Finley
 */

public class UnrestrictedTreePanel extends TreePanel {
	
	private HashMap <String, String> myVariableMap;
	/**
	 * Instantiates an unrestricted tree panel.
	 * 
	 * @param pane
	 *            the brute parse pane
	 */
	public UnrestrictedTreePanel(BruteParsePane pane) {
		super(new DefaultTreeModel(new DefaultMutableTreeNode("")));
		this.brutePane = pane;
	}
	
	public UnrestrictedTreePanel(BruteParsePane pane, HashMap <String, String> map) {
		super(new DefaultTreeModel(new DefaultMutableTreeNode("")));
		this.brutePane = pane;
		this.myVariableMap=map;
	}

	/**
	 * Returns the string representation of the tops and bottoms.
	 */
	public String getTB() {
		StringBuffer total = new StringBuffer();
		for (int i = 0; i < top.length; i++) {
			List t = new LinkedList();
			List b = new LinkedList();
			for (int j = 0; j < top[i].length; j++)
				t.add(Arrays.asList(top[i][j]));
			for (int j = 0; j < bottom[i].length; j++)
				b.add(Arrays.asList(bottom[i][j]));
			total.append("T." + i + ": " + t + "\n");
			total.append("B." + i + ": " + b + "\n");
		}
		return total.toString();
	}

	private UnrestrictedTreeNode[] levelNodes(int level) {
		List list = new ArrayList();
        if(top[level] != null){
            for (int i = 0; i < top[level].length; i++)
                for (int j = 0; j < top[level][i].length; j++)
                    list.add(top[level][i][j]);
        }
		return (UnrestrictedTreeNode[]) list
				.toArray(new UnrestrictedTreeNode[0]);
	}

	private void bridgeTo(int level) {
		UnrestrictedTreeNode[] prev = levelNodes(level - 1);
		Production[] prods = solutionParseNodes[level].getProductions();
		int[] prodStarts = solutionParseNodes[level].getSubstitutions();
		int length = 0, prodNum = 0;
		List bottomList = new LinkedList();
		List topList = new LinkedList();
		UnrestrictedTreeNode[] U = new UnrestrictedTreeNode[0];
		UnrestrictedTreeNode[][] UU = new UnrestrictedTreeNode[0][0];
		for (int i = 0; i < prev.length; i++) {
			if (prodNum >= prods.length || length < prodStarts[prodNum] || prev[i].toString().equals("")) {
				// Symbol doesn't change. We bring it down.
				UnrestrictedTreeNode[] a = new UnrestrictedTreeNode[] { prev[i] };
				bottomList.add(a);
				topList.add(a);
				length += prev[i].length();
				prev[i].lowest = level;
			} else if (length == prodStarts[prodNum]) {
				// Starting a production.

				
				List currentBottom = new LinkedList();
				List currentTop = new LinkedList();
				String rhs = prods[prodNum].getRHS();
				String lhs = prods[prodNum].getLHS();
				while (length < prodStarts[prodNum] + lhs.length()) {
					currentBottom.add(prev[i]);
					prev[i].lowest = level - 1;
					length += prev[i].length();
					i++;
				}
				UnrestrictedTreeNode[] b = (UnrestrictedTreeNode[]) currentBottom
						.toArray(U);
				i--;
				for (int j = 0; j < rhs.length(); j++) {
					UnrestrictedTreeNode node = new UnrestrictedTreeNode(""
							+ rhs.charAt(j));
					node.highest = node.lowest = level;
					currentTop.add(node);
					if (j == rhs.length() - 1)
						nodeToParentGroup.put(node, b);
				}
				
				if (rhs.length() == 0) {
					UnrestrictedTreeNode node = new UnrestrictedTreeNode("");
					node.highest = node.lowest = level;
					currentTop.add(node);
					nodeToParentGroup.put(node, b);
				}
				bottomList.add(b);
				topList.add((UnrestrictedTreeNode[]) currentTop.toArray(U));
				prodNum++;
			}
		}
		bottom[level - 1] = (UnrestrictedTreeNode[][]) bottomList.toArray(UU);
		top[level] = (UnrestrictedTreeNode[][]) topList.toArray(UU);
	}

	/**
	 * Returns if a group ends on a particular level. For each level, these are
	 * the bottom group number.
	 * 
	 * @param level
	 *            the level number
	 * @param group
	 *            the group number for that level
	 * @return if a group ends on a particular level
	 */
	private boolean ends(int level, int group) {
		try {
			if (level == bottom.length - 1)
				return true; // Everything ends at last.
			return !Arrays.equals(bottom[level][group], top[level + 1][group]);
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new IllegalArgumentException("Level " + level + ", group "
					+ group + " is out of range!");
		}
	}

	/**
	 * Returns if a group starts on a particular level. For each level, these
	 * are the top group numbers.
	 * 
	 * @param level
	 *            the level number
	 * @param group
	 *            the group number for that elvel
	 * @return if a group starts on a particular level
	 */
	private boolean begins(int level, int group) {
		if (level == 0)
			return true; // Everything starts at beginning.
		return ends(level - 1, group);
	}

	/**
	 * This function assigns proper weights to edges on levels <CODE>level</CODE>
	 * and <CODE>level+1</CODE>. This function may be called more than once
	 * per level; it is intended to operate in a somewhat iterative fashion.
	 * 
	 * @param level
	 *            the level to assign
	 * @param need
	 *            the array of needs
	 */
	private boolean assignWeights(int level, boolean[] need) {
		if (!need[level])
			return false;
		need[level] = false;
		boolean changed = false;
		double total = 0.0;
		for (int i = 0; i < bottom[level].length; i++) {
			UnrestrictedTreeNode[] s = bottom[level][i];
			UnrestrictedTreeNode[] c = top[level + 1][i];
			double cSum = 0.0, sSum = 0.0;
			for (int j = 0; j < s.length; j++)
				sSum += s[j].weight;
			if (!ends(level, i)) {
				total += sSum;
				continue;
			}
			for (int j = 0; j < c.length; j++) {
				cSum += c[j].weight;
			}
			Double TOTAL = new Double(total + Math.max(sSum, cSum) / 2.0);
			for (int j = 0; j < c.length; j++) {
				nodeToParentWeights.put(c[j], TOTAL);
			}
			total += Math.max(sSum, cSum);

			if (cSum > sSum) {
				double ratio = cSum / sSum;
				for (int j = 0; j < s.length; j++)
					s[j].weight *= ratio;
				if (level != 0)
					need[level - 1] = true;
				changed = true;
			} else if (cSum < sSum) {
				double ratio = sSum / cSum;
				for (int j = 0; j < c.length; j++)
					c[j].weight *= ratio;
				if (level != 0)
					need[level + 1] = true;
				changed = true;
			}
		}
		return changed;
	}

	/**
	 * Sets the answer to this tree panel.
	 * 
	 * @param answer
	 *            the end result of a parse tree derivation, or <CODE>null</CODE>
	 *            if no answer should be displayed
	 */
	public void setAnswer(ParseNode answer) {
		
		if (answer == null) {
			top = null;
			return;
		}

		metaWidth = -1.0;
		solutionParseNodes = new ParseNode[answer.getLevel() + 1];
		for (; answer != null; answer = (ParseNode) answer.getParent())
			solutionParseNodes[answer.getLevel()] = answer;
		
		top = new UnrestrictedTreeNode[solutionParseNodes.length][][];
		bottom = new UnrestrictedTreeNode[solutionParseNodes.length][][];
		// Initialize the top of the top.
		top[0] = new UnrestrictedTreeNode[1][];
		top[0][0] = new UnrestrictedTreeNode[1];
		top[0][0][0] = new UnrestrictedTreeNode(solutionParseNodes[0]
				.getDerivation());
		// Create the nodes.
	
		for (int i = 1; i < top.length; i++)
			bridgeTo(i);
		bottom[bottom.length - 1] = top[top.length - 1];
		// Assign the weights.
		boolean[] need = new boolean[top.length];
		for (int i = 0; i < need.length; i++)
			need[i] = true;
		boolean changed = true;
		for (int max = 0; changed && max < top.length * 2; max++) {
			changed = false;
			for (int i = 0; i < top.length - 1; i++)
				changed |= assignWeights(i, need);
		}
		level = group = 0;
		brutePane.derivationModel.setRowCount(0);
		brutePane.derivationModel.addRow(new String[] { "",
				solutionParseNodes[0].getDerivation() });
		return;
	}

	/**
	 * Paints a node at a particular point.
	 * 
	 * @param g
	 *            the graphics object
	 * @param node
	 *            the node to paint
	 * @param p
	 *            the point to paint at
	 */
	
	public void paintNode(Graphics2D g, UnrestrictedTreeNode node, Point2D p) {
		
		g.setColor(node.lowest == top.length - 1 ? LEAF : INNER);
		g.translate(p.getX(), p.getY());
		
		if (myVariableMap==null)
			nodeDrawer.draw(g, node);
		else
		{
			if (myVariableMap.containsKey(node.toString()))
			{
				node=new UnrestrictedTreeNode(myVariableMap.get(node.toString()));
				nodeDrawer.draw(g, node, true);
			}
			else
			{	
				nodeDrawer.draw(g, node);
			}
		}
		g.translate(-p.getX(), -p.getY());
	}

	
	double realWidth, realHeight, metaWidth = -1.0, metaHeight;

	/**
	 * Returns a point corresponding to a given row, and weight.
	 * 
	 * @param row
	 *            the row
	 * @param weight
	 *            the weight sum for the given point
	 * @param point
	 *            the point to store the result in
	 */
	protected Point2D getPoint(int row, double weight, Point2D p) {
		if (p == null)
			p = new Point2D.Double();
		p.setLocation(realWidth * weight / metaWidth, realHeight
				* ((double) row + 0.5) / metaHeight);
		return p;
	}

	protected void setMetaWidth() {
		for (int i = 0; i < top.length; i++) {
			UnrestrictedTreeNode[] nodes = levelNodes(i);
			double total = 0.0;
            if(nodes != null){
                for (int j = 0; j < nodes.length; j++)
                    total += nodes[j].weight;
            }
			metaWidth = Math.max(total, metaWidth);
		}
	}

	/**
	 * Paints the tree.
	 * 
	 * @param g
	 *            the graphics object
	 */
	private void paintTree(Graphics2D g) {
			
		Dimension d = getSize();
		realWidth = d.width;
		realHeight = d.height;
		if (metaWidth == -1.0)
			setMetaWidth();
		metaHeight = top.length;
		Point2D p = new Point2D.Double();
		nodeToPoint = new HashMap();
		for (int l = 0; l <= level; l++) {
			double total = 0.0;
			UnrestrictedTreeNode[][] GG = l < level ? bottom[l] : top[l];
			for (int gr = 0; gr < GG.length && (level != l || gr <= group); gr++) {
				double x, y;
				UnrestrictedTreeNode[] G = GG[gr];
				if (l <= level - 2 || (l == level - 1 && gr <= group)) {
					// Want the node on the bottom level.
					for (int i = 0; i < G.length; i++) {
						if (l == G[i].lowest) {
							// This group is drawn on the bottom.
							// Draw the line.
							Point2D point = getPoint(G[i].lowest, total
									+ G[i].weight / 2.0, null);
							getPoint(G[i].highest, total + G[i].weight / 2.0, p);
							g.drawLine((int) point.getX(), (int) point.getY(),
									(int) p.getX(), (int) p.getY());
							// Make the mapping.
							nodeToPoint.put(G[i], point);
						}
						if (l == G[i].highest) {
							// This group is just starting.
							Point2D point = getPoint(G[i].highest, total
									+ G[i].weight / 2.0, null);
							Double D = (Double) nodeToParentWeights.get(G[i]);
							if (D != null) {
								double pweight = D.doubleValue();
								getPoint(l - 1, pweight, p);
								g
										.drawLine((int) point.getX(),
												(int) point.getY(), (int) p
														.getX(), (int) p.getY());
							}
							// Draw the brackets.
							UnrestrictedTreeNode[] parent = (UnrestrictedTreeNode[]) nodeToParentGroup
									.get(G[i]);
						 	if (parent != null && parent.length != 1) {
								Point2D alpha = (Point2D) nodeToPoint
										.get(parent[0]);
								Point2D beta = (Point2D) nodeToPoint
										.get(parent[parent.length - 1]);
								g.setColor(BRACKET);
								int radius = (int) DefaultNodeDrawer.NODE_RADIUS;
								int ax = (int) (alpha.getX() - radius - 3);
								int ay = (int) (alpha.getY() - radius - 3);
								g.fillRoundRect(ax, ay, (int) (beta.getX()
										+ radius + 3)
										- ax, (int) (beta.getY() + radius + 3)
										- ay, 2 * radius + 6, 2 * radius + 6);
								g.setColor(BRACKET_OUT);
								g.drawRoundRect(ax, ay, (int) (beta.getX()
										+ radius + 3)
										- ax, (int) (beta.getY() + radius + 3)
										- ay, 2 * radius + 6, 2 * radius + 6);
								g.setColor(Color.black);
							}
							// Make the map.
							nodeToPoint.put(G[i], point);
						}
						total += G[i].weight;
					}
				} else if (l <= level) {
					// We're going to get the top level.
					for (int i = 0; i < G.length; i++) {
						if (l == G[i].highest) {
							// This node is just starting too.
							Point2D point = getPoint(G[i].highest, total
									+ G[i].weight / 2.0, null);
							Double D = (Double) nodeToParentWeights.get(G[i]);
							if (D != null) {
								double pweight = D.doubleValue();
								getPoint(l - 1, pweight, p);
								g
										.drawLine((int) point.getX(),
												(int) point.getY(), (int) p
														.getX(), (int) p.getY());
							}
							// Draw the brackets.
							UnrestrictedTreeNode[] parent = (UnrestrictedTreeNode[]) nodeToParentGroup
									.get(G[i]);
							if (parent != null && parent.length != 1) {
								Point2D alpha = (Point2D) nodeToPoint
										.get(parent[0]);
								Point2D beta = (Point2D) nodeToPoint
										.get(parent[parent.length - 1]);
								g.setColor(BRACKET);
								int radius = (int) DefaultNodeDrawer.NODE_RADIUS;
								int ax = (int) (alpha.getX() - radius - 3);
								int ay = (int) (alpha.getY() - radius - 3);
								g.fillRoundRect(ax, ay, (int) (beta.getX()
										+ radius + 3)
										- ax, (int) (beta.getY() + radius + 3)
										- ay, 2 * radius + 6, 2 * radius + 6);
								g.setColor(BRACKET_OUT);
								g.drawRoundRect(ax, ay, (int) (beta.getX()
										+ radius + 3)
										- ax, (int) (beta.getY() + radius + 3)
										- ay, 2 * radius + 6, 2 * radius + 6);
								g.setColor(Color.black);
							}
							// Make the map.
							nodeToPoint.put(G[i], point);
						}
						total += G[i].weight;
					}
				} else {
					System.err.println("Badness in the drawer!");
				}
			}
		}
		// Do the drawing of the nodes.
		Iterator it = nodeToPoint.entrySet().iterator();
		
		while (it.hasNext()) {
			Map.Entry e = (Map.Entry) it.next();
			paintNode(g, ((UnrestrictedTreeNode) e.getKey()), (Point2D) e.getValue());
		}
		
	}

	private String getDerivation(int level, int num) {
		StringBuffer b = new StringBuffer(solutionParseNodes[level - 1]
				.getDerivation());
		int[] subs = solutionParseNodes[level].getSubstitutions();
		Production[] ps = solutionParseNodes[level].getProductions();
		do {
			b.delete(subs[num], subs[num] + ps[num].getLHS().length());
			b.insert(subs[num], ps[num].getRHS());
		} while (--num >= 0);
		return b.toString();
	}

	/**
	 * This method should be called to go to the next part.
	 */
	public boolean next() {
		Production p = null, ps[] = solutionParseNodes[level].getProductions();
		String derivation = null;
		production++;
		if (production >= ps.length) {
			production = 0;
			p = solutionParseNodes[level + 1].getProductions()[0];
			derivation = getDerivation(level + 1, 0);
		} else {
			p = ps[production];
			derivation = getDerivation(level, production);
		}
		if (myVariableMap!=null)
		{
			String[] lhs=p.toString().split("");
			String[] rhs=derivation.split("");
			String new_lhs="";
			String new_rhs="";
			for (int i=0; i<lhs.length; i++)
			{
				if (myVariableMap.containsKey(lhs[i]))
				{
					new_lhs=new_lhs+myVariableMap.get(lhs[i]);
				}
				else
					new_lhs=new_lhs+lhs[i];
			}
			for (int i=0; i<rhs.length; i++)
			{
				if (myVariableMap.containsKey(rhs[i]))
				{
					new_rhs=new_rhs+myVariableMap.get(rhs[i]);
				}
				else
					new_rhs=new_rhs+rhs[i];
			}
			brutePane.derivationModel.addRow(new String[] { new_lhs + "", new_rhs });
		}
		else
		{
			brutePane.derivationModel.addRow(new String[] { p + "", derivation });
		}
		do {
			group++;
			if (group >= top[level].length) {
				group = 0;
				level++;
			}
			if (level >= top.length) {
				level = top.length - 1;
				group = top[level].length - 1;
				break;
			}
			if (level == top.length - 1 && group == top[level].length - 1)
				break;
		} while (!begins(level, group));
		String lhs = p.getRHS();
		if (lhs.length() == 0)
			lhs = Universe.curProfile.getEmptyString();
		String text = "Derived " + lhs + " from " + p.getLHS() + ".";
		if (level == top.length - 1
				&& production == solutionParseNodes[level].getProductions().length - 1) {
			text += "  Derivations complete.";
			brutePane.statusDisplay.setText(text);
			return true;
		}
		brutePane.statusDisplay.setText(text);

		return false;
	}

	/**
	 * Paints the component.
	 * 
	 * @param gr
	 *            the graphics object to draw on
	 */
	public void paintComponent(Graphics gr) {
		// super.paintComponent(g);
		Graphics2D g = (Graphics2D) gr.create();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(Color.white);
		Dimension d = getSize();
		g.fillRect(0, 0, d.width, d.height);
		g.setColor(Color.black);
		if (top != null)
			paintTree(g);
		g.dispose();
	}
	


	/** The brute parse pane. */
	protected BruteParsePane brutePane;

	/** The parse nodes. */
	protected ParseNode[] solutionParseNodes;

	/** The tops. */
	protected UnrestrictedTreeNode[][][] top = null;

	/** The bottoms. */
	protected UnrestrictedTreeNode[][][] bottom = null;

	/** The mapping of nodes to the center weight points of parent edges. */
	protected Map nodeToParentWeights = new HashMap();

	/** The mapping of nodes to their parent group. */
	protected Map nodeToParentGroup = new HashMap();

	
	protected Map nodeToPoint;
	
	
	/** The node drawer. */
	protected DefaultNodeDrawer nodeDrawer = new DefaultNodeDrawer();

	/** Colors. */
	protected static final Color INNER = new Color(100, 200, 120),
			LEAF = new Color(255, 255, 100),
			BRACKET = new Color(150, 150, 255), BRACKET_OUT = BRACKET.darker()
					.darker();

	/** Current level. */
	int level = 0;

	/** Current group. */
	int group = 0;

	/** Current production. */
	int production = -1;
}
