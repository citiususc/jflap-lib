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





package edu.duke.cs.jflap.gui.editor;

import edu.duke.cs.jflap.gui.environment.AutomatonEnvironment;
import edu.duke.cs.jflap.gui.environment.Environment;
import edu.duke.cs.jflap.gui.environment.EnvironmentFrame;
import edu.duke.cs.jflap.gui.environment.tag.CriticalTag;
import edu.duke.cs.jflap.gui.viewer.AutomatonDrawer;
import edu.duke.cs.jflap.gui.viewer.AutomatonPane;
import edu.duke.cs.jflap.gui.viewer.CurvedArrow;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

import edu.duke.cs.jflap.automata.Note;
import edu.duke.cs.jflap.automata.State;
import edu.duke.cs.jflap.automata.StateRenamer;
import edu.duke.cs.jflap.automata.Transition;
import edu.duke.cs.jflap.automata.graph.AutomatonGraph;
import edu.duke.cs.jflap.automata.graph.LayoutAlgorithm;
import edu.duke.cs.jflap.automata.graph.layout.GEMLayoutAlgorithm;
import edu.duke.cs.jflap.automata.turing.TMTransition;
import edu.duke.cs.jflap.automata.turing.TMState;
import edu.duke.cs.jflap.automata.turing.TuringMachine;
import edu.duke.cs.jflap.debug.EDebug;

/**
 * The arrow tool is used mostly for editing existing objects.
 * 
 * @author Thomas Finley, Henry Qin
 */

public class ArrowTool extends Tool {
	/**
	 * Instantiates a new arrow tool.
	 * 
	 * @param view
	 *            the view where the automaton is drawn
	 * @param drawer
	 *            the object that draws the automaton
	 * @param creator
	 *            the transition creator used for editing transitions
	 */
	public ArrowTool(AutomatonPane view, AutomatonDrawer drawer,
			TransitionCreator creator) {
		super(view, drawer);
		this.creator = creator;
	}

	/**
	 * Instantiates a new arrow tool.
	 * 
	 * @param view
	 *            the view where the automaton is drawn
	 * @param drawer
	 *            the object that draws the automaton
	 */
	public ArrowTool(AutomatonPane view, AutomatonDrawer drawer) {
		super(view, drawer);
		this.creator = TransitionCreator.creatorForAutomaton(getAutomaton(),
				getView());
	}

	/**
	 * Gets the tool tip for this tool.
	 * 
	 * @return the tool tip for this tool
	 */
	public String getToolTip() {
		return "Attribute Editor";
	}

	/**
	 * Returns the tool icon.
	 * 
	 * @return the arrow tool icon
	 */
	protected Icon getIcon() {
		java.net.URL url = getClass().getResource("/ICON/arrow.gif");
		return new javax.swing.ImageIcon(url);
	}

	/**
	 * On a mouse click, if this is a double click over a transition edit the
	 * transition. If this was a single click, then we select the transition.
	 * 
	 * @param event
	 *            the mouse event
	 */
	public void mouseClicked(MouseEvent event) {
		if (event.getClickCount() == 1){
            Transition trans = getDrawer().transitionAtPoint(event.getPoint());
            if (trans != null){
                if (trans.isSelected){
                    trans.isSelected = false;
                    selectedTransition = null;
                } 
                else{
                    if (selectedTransition != null) selectedTransition.isSelected = false;
                    trans.isSelected = true;
                    selectedTransition = trans;
                     
                }
                return;
            }
        }
		Transition trans = getDrawer().transitionAtPoint(event.getPoint());
		if (trans == null){
			Rectangle bounds;
			bounds = new Rectangle(0, 0, -1, -1);
			getView().getDrawer().getAutomaton().selectStatesWithinBounds(bounds);
			getView().repaint();
			return;
		}
        EDebug.print("Beginning to Edit with creator "+ creator.getClass());
		creator.editTransition(trans, event.getPoint());

	}

	/**
	 * Possibly show a popup menu.
	 * 
	 * @param event
	 *            the mouse event
	 */
	protected void showPopup(MouseEvent event) {
		// Should we show a popup menu?
		if (event.isPopupTrigger()) {
			Point p = getView().transformFromAutomatonToView(event.getPoint());
			if (lastClickedState != null && shouldShowStatePopup()) {
				stateMenu.show(lastClickedState, getView(), p);
			} else {
				emptyMenu.show(getView(), p);
			}
		}
		lastClickedState = null;
		lastClickedTransition = null;
	}

	/**
	 * On a mouse press, allows the state to be dragged about unless this is a
	 * popup trigger.
	 */
	public void mousePressed(MouseEvent event) {
		if (getDrawer().getAutomaton().getEnvironmentFrame() !=null)
    		((AutomatonEnvironment)getDrawer().getAutomaton().getEnvironmentFrame().getEnvironment()).saveStatus();
        else
            EDebug.print("I cannot preserve what you ask");
		initialPointClick.setLocation(event.getPoint());
		lastClickedState = getDrawer().stateAtPoint(event.getPoint());
		if (lastClickedState == null)
			lastClickedTransition = getDrawer().transitionAtPoint(
					event.getPoint());


		// Should we show a popup menu?
		if (event.isPopupTrigger())
			showPopup(event);

		if (lastClickedState != null) {
			initialPointState.setLocation(lastClickedState.getPoint());
			if(!lastClickedState.isSelected()){
				Rectangle bounds = new Rectangle(0, 0, -1, -1);
				getView().getDrawer().getAutomaton().selectStatesWithinBounds(bounds);
				getView().getDrawer().setSelectionBounds(bounds);
				lastClickedState.setSelect(true);
			}
			getView().repaint();
		}
		else if (lastClickedTransition != null) {
			initialPointClick.setLocation(event.getPoint());
		}	
		else {
			ArrayList notes = getDrawer().getAutomaton().getNotes();
			for(int k = 0; k < notes.size(); k++){
				((Note)notes.get(k)).setEditable(false);
				((Note)notes.get(k)).setEnabled(false);
				((Note)notes.get(k)).setCaretColor(new Color(255, 255, 150));		
			}

			Rectangle bounds = new Rectangle(0, 0, -1, -1);
			getView().getDrawer().getAutomaton().selectStatesWithinBounds(bounds);
			getView().getDrawer().setSelectionBounds(bounds);
		}

        //reset the selectedTransition after an Undo has happened.

        
        Transition[] trans = getAutomaton().getTransitions();
        for (int i = 0; i < trans.length; i++)
            if (trans[i].isSelected){
                selectedTransition = trans[i];
                return;
            }
        

        selectedTransition = null;




	}

	/**
	 * Returns if the state popup menu should be shown whenever applicable.
	 * 
	 * @return <CODE>true</CODE> if the state menu should be popped up, <CODE>false</CODE>
	 *         if it should not be... returns <CODE>true</CODE> by default
	 */
	protected boolean shouldShowStatePopup() {
		return true;
	}

	/**
	 * On a mouse drag, possibly move a state if the first press was on a state.
	 */
	public void mouseDragged(MouseEvent event) {



		if (lastClickedState != null) {
			if (event.isPopupTrigger())
				return;
			Point p = event.getPoint();
			
			State[] states = getView().getDrawer().getAutomaton().getStates();
			for(int k = 0; k < states.length; k++){
				State curState = states[k];
				if(curState.isSelected()){
					int x = curState.getPoint().x + p.x - initialPointClick.x;
					int y = curState.getPoint().y + p.y - initialPointClick.y;
					curState.getPoint().setLocation(x, y);
					curState.setPoint(curState.getPoint());									
				}
			}
			initialPointClick = p;
			getView().repaint();
		} else if (lastClickedTransition != null) {
			if (event.isPopupTrigger())
				return;
			Point p = event.getPoint();
			int x = p.x - initialPointClick.x;
			int y = p.y - initialPointClick.y;
			State f = lastClickedTransition.getFromState(), t = lastClickedTransition
					.getToState();
			if (f==t){

                //uncomment this code for Transitions movement
                /*
				double circlex = (p.x-f.getPoint().x);
				double circley = (p.y-f.getPoint().y);
				double angle = Math.atan2(circley, circlex);
				Point from = getView().getDrawer().pointOnState(f, angle+Math.PI*.166);
				Point to = getView().getDrawer().pointOnState(f, angle-Math.PI*.166);

				Transition[] trans = getAutomaton().getTransitionsFromStateToState(f, t);
                
				for (int n = 0; n < trans.length; n++) {
					CurvedArrow arrow = (CurvedArrow) getView().getDrawer().transitionToArrowMap.get(trans[n]);
//					arrow.setStart(from);
//					arrow.setEnd(to);
					getView().getDrawer().selfTransitionMap.put(trans[n], angle);
					getView().getDrawer().arrowToTransitionMap.put(arrow, trans[n]);
					getView().getDrawer().transitionToArrowMap.put(trans[n], arrow);
				}
                */
			}
			if (f != t) {
				//f.getPoint().translate(x, y);
				//f.setPoint(f.getPoint());
				// Don't want self loops moving twice the speed...
				//t.getPoint().translate(x, y);
				//t.setPoint(t.getPoint());
				double circlex = (p.x-f.getPoint().x);
				double circley = (p.y-f.getPoint().y);
				//double angle = Math.atan2(circley, circlex);
				//Point from = getView().getDrawer().pointOnState(f, angle+Math.PI*.166);
				//Point to = getView().getDrawer().pointOnState(f, angle-Math.PI*.166);
				Transition[] trans = getAutomaton().getTransitionsFromStateToState(f, t);
				for (int n = 0; n < trans.length; n++) {
					CurvedArrow arrow = (CurvedArrow) getView().getDrawer().transitionToArrowMap.get(trans[n]);

                    

                    //uncomment this code for Transitions movement
                    /*
					float centerx = (t.getPoint().x+f.getPoint().x)/2;
					float centery = (t.getPoint().y+f.getPoint().y)/2;
					float pvecx = p.x-centerx; float pvecy = p.y-centery;
					float svecx = t.getPoint().x-centerx; float svecy = t.getPoint().y-centery;
					float dprod = pvecx*svecx+pvecy*svecy;
					dprod = dprod/(float) Math.abs((Math.sqrt(pvecx*pvecx+pvecy*pvecy)))/(float) Math.abs((Math.sqrt(svecx*svecx+svecy*svecy)));
					float theta = (float) Math.acos(dprod);
					float curv = (float) (Math.sqrt(pvecx*pvecx+pvecy*pvecy)*Math.sin(theta))/10;
                    */


					//float curv = (float) Math.sqrt((p.x-centerx)*(p.x-centerx)+(p.y-centery)*(p.y-centery))/10;
					//Float curv = (float) -(p.y-(f.getPoint().y+t.getPoint().y)/2)/10;
					//if (curv>=0){

                    //uncomment this code for Transitions
                    /*
					arrow.setCurvy(curv+n);
					getView().getDrawer().curveTransitionMap.put(trans[n], curv);
                    */


					//}else{
					//	arrow.setCurvy(-(curv-n));
					//	getView().getDrawer().curveTransitionMap.put(trans[n], -curv);
					//}
					//QuadCurve2D curve = arrow.getCurve();
					//curve.setCurve(curve.getX1(), curve.getY1(), p.x, p.y, curve.getX2(), curve.getY2());
					
					getView().getDrawer().arrowToTransitionMap.put(arrow, trans[n]);
					getView().getDrawer().transitionToArrowMap.put(trans[n], arrow);
				}
			}
			initialPointClick.setLocation(p);
			getView().repaint();
			//EDebug.print(getView().getDrawer().selfTransitionMap);
		}
		else{
			Rectangle bounds;
			int nowX = event.getPoint().x;
			int nowY = event.getPoint().y;
			int leftX = initialPointClick.x;
			int topY = initialPointClick.y;
			if(nowX < initialPointClick.x) leftX = nowX;
			if(nowY < initialPointClick.y) topY = nowY;
			bounds = new Rectangle(leftX, topY, Math.abs(nowX-initialPointClick.x), Math.abs(nowY-initialPointClick.y));

            if (!transitionInFlux){
                getView().getDrawer().getAutomaton().selectStatesWithinBounds(bounds);
                getView().getDrawer().setSelectionBounds(bounds);
            }

			getView().repaint();
		}
        
        //Deal with transition dragging here
        if (selectedTransition != null){ //simply set ...but we need to get the initial point to be clever
            CurvedArrow ca = (CurvedArrow)getView().getDrawer().transitionToArrowMap.get(selectedTransition);

            Point myClickP = event.getPoint();
            Point2D control = ca.getCurve().getCtrlPt();

            if (transitionInFlux || Math.sqrt((control.getX() - myClickP.x)*(control.getX() - myClickP.x) 
                        + (control.getY() - myClickP.y)*(control.getY() - myClickP.y)) < 15){
                            selectedTransition.setControl(myClickP);
        //                System.out.println("Move it damn it");
                             ca.refreshCurve();
                             transitionInFlux = true;
                             return;
                        }

        }
	}

    private boolean transitionInFlux = false;

	/**
	 * On a mouse release, sets the tool to the "virgin" state.
	 */
	public void mouseReleased(MouseEvent event) {
        transitionInFlux = false;
		if (event.isPopupTrigger())
			showPopup(event);
		
		
		State[] states = getView().getDrawer().getAutomaton().getStates();
		int count = 0;
		for(int k = 0; k < states.length; k++){			
			if(states[k].isSelected()){	
				count++;
			}
		}
		Rectangle bounds = getView().getDrawer().getSelectionBounds();
		if(count == 1 && bounds.isEmpty() && lastClickedState!=null) lastClickedState.setSelect(false);
		bounds = new Rectangle(0, 0, -1, -1);
		getView().getDrawer().setSelectionBounds(bounds);
		lastClickedState = null;
		lastClickedTransition = null;
		getView().repaint();
	}

	/**
	 * Returns the key stroke that will activate this tool.
	 * 
	 * @return the key stroke that will activate this tool
	 */
	public KeyStroke getKey() {
		return KeyStroke.getKeyStroke('a');
	}

	/**
	 * Returns true if only changing the final stateness of a state should be
	 * allowed in the state menu.
	 */
	public boolean shouldAllowOnlyFinalStateChange() {
		return false;
	}

	/**
	 * The contextual menu class for editing states.
	 */
    /*
     * I changed this from private class to protected class so I can 
     * remove the "Final State" option from Moore and Mealy machines.
     */
	protected class StateMenu extends JPopupMenu implements ActionListener {
		public StateMenu() {
			makeFinal = new JCheckBoxMenuItem("Final");
			makeFinal.addActionListener(this);
			this.add(makeFinal);
			makeInitial = new JCheckBoxMenuItem("Initial");
			changeLabel = new JMenuItem("Change Label");
			deleteLabel = new JMenuItem("Clear Label");
			deleteAllLabels = new JMenuItem("Clear All Labels");
			editBlock = new JMenuItem("Edit Block");
			copyBlock = new JMenuItem("Duplicate Block");
			replaceSymbol = new JMenuItem("Replace Symbol");
			setName = new JMenuItem("Set Name");
			if (shouldAllowOnlyFinalStateChange())
				return;
			makeInitial.addActionListener(this);
			changeLabel.addActionListener(this);
			deleteLabel.addActionListener(this);
			deleteAllLabels.addActionListener(this);
			editBlock.addActionListener(this);
			setName.addActionListener(this);
			copyBlock.addActionListener(this);
			replaceSymbol.addActionListener(this);
			this.add(makeInitial);
			this.add(changeLabel);
			this.add(deleteLabel);
			this.add(deleteAllLabels);
			this.add(setName);
		}

		public void show(State state, Component comp, Point at) {
			this.remove(editBlock);
			this.state = state;
//			if (state.getInternalName() != null) {
			if (state instanceof TMState) {
				this.add(editBlock);
				this.add(copyBlock);
				editBlock.setEnabled(true);
				copyBlock.setEnabled(true);
				this.add(replaceSymbol);
				replaceSymbol.setEnabled(true);
			}
			makeFinal.setSelected(getAutomaton().isFinalState(state));
			makeInitial.setSelected(getAutomaton().getInitialState() == state);
			deleteLabel.setEnabled(state.getLabel() != null);
			show(comp, at.x, at.y);
		}

		public void actionPerformed(ActionEvent e) {
			JMenuItem item = (JMenuItem) e.getSource();
            if (getDrawer().getAutomaton().getEnvironmentFrame() !=null)
                ((AutomatonEnvironment)getDrawer().getAutomaton().getEnvironmentFrame().getEnvironment()).saveStatus();

			if (item == makeFinal) {
				if (item.isSelected())
					getAutomaton().addFinalState(state);
				else
					getAutomaton().removeFinalState(state);
			} else if (item == makeInitial) {
				if (!item.isSelected())
					state = null;
				getAutomaton().setInitialState(state);
			} else if (item == changeLabel) {
				String oldlabel = state.getLabel();
				oldlabel = oldlabel == null ? "" : oldlabel;
				String label = (String) JOptionPane.showInputDialog(this,
						"Input a new label, or \n"
								+ "set blank to remove the label", "New Label",
						JOptionPane.QUESTION_MESSAGE, null, null, oldlabel);
				if (label == null)
					return;
				if (label.equals(""))
					label = null;
				state.setLabel(label);
			} else if (item == deleteLabel) {
				state.setLabel(null);
			} else if (item == deleteAllLabels) {
				State[] states = getAutomaton().getStates();
				for (int i = 0; i < states.length; i++)
					states[i].setLabel(null);
			} else if (item == editBlock) { //this implies that this was a TMState to begin with, because only TM states would have this menu option
			
                //not sure why need highest level automaton, but okay
				TMState parent = (TMState) state;
				while (((TuringMachine)parent.getAutomaton()).getParent() != null) {
					parent = ((TuringMachine)parent.getAutomaton()).getParent();
				}
				EditBlockPane editor = new EditBlockPane(((TMState)state).getInnerTM()); //give it a Turing Machine //just edit the Automaton directly; there is no need for a repaint either, because the other guy does not paint it

				EnvironmentFrame rootFrame = parent.getAutomaton().getEnvironmentFrame();

				editor.setBlock(state);
				Environment envir = rootFrame.getEnvironment();
				envir.add(editor, "Edit Block", new CriticalTag() {
				});

				envir.setActive(editor);
			} else if (item == setName) {
				String oldName = state.getName();
				oldName = oldName == null ? "" : oldName;
				String name = (String) JOptionPane.showInputDialog(this,
						"Input a new name, or \n"
								+ "set blank to remove the name", "New Name",
						JOptionPane.QUESTION_MESSAGE, null, null, oldName);
				if (name == null)
					return;
				if (name.equals(""))
					name = null;
				state.setName(name);

			}else if (item == copyBlock) { 
                //MERLIN MERLIN MERLIN MERLIN MERLIN// 

//				TMState buffer = ((TuringMachine) getAutomaton()).createTMState((Point)state.getPoint()); //again, we assume that the cast will work, since copyBlock hould never be there except with Turing.
				TMState buffer = ((TuringMachine) getAutomaton()).createTMState(new Point(state.getPoint().x+4, state.getPoint().y)); //again, we assume that the cast will work, since copyBlock hould never be there except with Turing.
                buffer.setInnerTM((TuringMachine)((TMState) state).getInnerTM().clone()); //all states have an inner TM, although this inner TM might have zero states within it, in which case it acts as a simple state.


			}
            else if (item == replaceSymbol) {
				
                assert state instanceof TMState;

				String replaceWith = null;
				String toReplace = null;						
				Object old = JOptionPane.showInputDialog(null, "Find");		
    			if (old == null)
    				return;
    			if(old instanceof String){
    				toReplace = (String)old;
    			}
    				
    			Object newString = JOptionPane.showInputDialog(null, "Replace With");
    			if (newString == null)
    				return;
    			if(newString instanceof String){
    				replaceWith = (String)newString;
    			}

                replaceCharactersInBlock((TMState) state, toReplace, replaceWith);
				}
			

    
			getView().repaint();
		}
		
		private void replaceCharactersInBlock(TMState start, String toReplace, String replaceWith){ //this shall be a recursive method, replacing the inside and then the out

            TuringMachine tm = start.getInnerTM();
                
            for (int i = 0; i < tm.getStates().length; i++)
                replaceCharactersInBlock((TMState)tm.getStates()[i], toReplace, replaceWith);      
            
            Transition[] trans = tm.getTransitions();
            
            for (int i = 0; i < trans.length; ++i){
                TMTransition tmTrans = (TMTransition)trans[i];
                for(int k = 0; k < tmTrans.tapes(); k++){
                    String read = tmTrans.getRead(k);
                    tmTrans.setRead(k, read.replaceAll(toReplace, replaceWith));
                    String write = tmTrans.getWrite(k);
                    tmTrans.setWrite (k,write.replaceAll(toReplace, replaceWith));
                }

            }
        }
		

        private State state;

        /*
         * Changed this from private to protected so I can remove
         * "Final State" option from Moore and Mealy machines.
         */
		protected JCheckBoxMenuItem makeFinal, makeInitial;

		private JMenuItem changeLabel, deleteLabel, deleteAllLabels, editBlock, copyBlock, replaceSymbol,
				setName;
	}

	/**
	 * The contextual menu class for editing transitions.
	 */
	private class TransitionMenu extends JPopupMenu {

	}

	/**
	 * The contextual menu class for context clicks in blank space.
	 */
	private class EmptyMenu extends JPopupMenu implements ActionListener {
		public EmptyMenu() {
			stateLabels = new JCheckBoxMenuItem("Display State Labels");
			stateLabels.addActionListener(this);
			this.add(stateLabels);
			layoutGraph = new JMenuItem("Layout Graph");
			if (!(ArrowTool.this instanceof ArrowDisplayOnlyTool)) {
				layoutGraph.addActionListener(this);
				this.add(layoutGraph);
			}
			renameStates = new JMenuItem("Rename States");
			if (!(ArrowTool.this instanceof ArrowDisplayOnlyTool)) {
				renameStates.addActionListener(this);
				this.add(renameStates);
			}
			
			addNote = new JMenuItem("Add Note");
			if (!(ArrowTool.this instanceof ArrowDisplayOnlyTool)) {
				addNote.addActionListener(this);
				this.add(addNote);
			}

//           BEGIN SJK add
            adaptView = new JCheckBoxMenuItem("Auto-Zoom");
            if (!(ArrowTool.this instanceof ArrowDisplayOnlyTool)) {
                adaptView.addActionListener(this);
                this.add(adaptView);
            }
//          END SJK add

            
		}

		public void show(Component comp, Point at) {
			stateLabels.setSelected(getDrawer().doesDrawStateLabels());
			adaptView.setSelected(getView().getAdapt());
			myPoint = at;
			show(comp, at.x, at.y);
		}

		public void actionPerformed(ActionEvent e) {
			JMenuItem item = (JMenuItem) e.getSource();
			if (item == stateLabels) {
				getView().getDrawer().shouldDrawStateLabels(item.isSelected());
			} else if (item == layoutGraph) {
				AutomatonGraph g = new AutomatonGraph(getAutomaton());
				LayoutAlgorithm alg = new GEMLayoutAlgorithm();
				alg.layout(g, null);
				g.moveAutomatonStates();
				getView().fitToBounds(30);
			} else if (item == renameStates) {
    		    ((AutomatonEnvironment)getDrawer().getAutomaton().getEnvironmentFrame().getEnvironment()).saveStatus();
				StateRenamer.rename(getAutomaton());
			} else if (item == adaptView)
            {
                getView().setAdapt(item.isSelected());
            } else if (item == addNote)
            {		 	
                ((AutomatonEnvironment)getDrawer().getAutomaton().getEnvironmentFrame().getEnvironment()).saveStatus();
                Note newNote = new Note(myPoint, "insert_text");
                newNote.initializeForView(getView());
        		getView().getDrawer().getAutomaton().addNote(newNote);
        		
            }
			getView().repaint();
			//boolean selected = adaptView.isSelected();
			emptyMenu = new EmptyMenu();
			//adaptView.setSelected(selected);
		}
		private Point myPoint;
		
		private JCheckBoxMenuItem stateLabels;
		private Note curNote;
		private JMenuItem layoutGraph;
		private JMenuItem addNote;
		private JMenuItem renameStates, adaptView;
	}

	/** The transition creator for editing transitions. */
	private TransitionCreator creator;

	/** The state that was last clicked. */
	private State lastClickedState = null;

	/** The transition that was last clicked. */
	private Transition lastClickedTransition = null;
	
	/** The note that was last clicked. */
	private Note lastClickedNote = null;

	/** The initial point of the state. */
	private Point initialPointState = new Point();

	/** The initial point of the click. */
	private Point initialPointClick = new Point();

	/** The state menu. */
    /*
     * I changed it to protected because I needed to mess with
     * it in a subclass. This is to remove the "Final State"
     * option in Moore and Mealy machines.
     */
	protected StateMenu stateMenu = new StateMenu();

	/** The transition menu. */
	private TransitionMenu transitionMenu = new TransitionMenu();

	/** The empty menu. */
	private EmptyMenu emptyMenu = new EmptyMenu();

    private Transition selectedTransition = null;
}
