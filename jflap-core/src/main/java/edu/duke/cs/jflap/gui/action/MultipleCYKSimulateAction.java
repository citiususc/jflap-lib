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




package edu.duke.cs.jflap.gui.action;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JToolBar;

import edu.duke.cs.jflap.grammar.Grammar;
import edu.duke.cs.jflap.gui.JTableExtender;
import edu.duke.cs.jflap.gui.SplitPaneFactory;
import edu.duke.cs.jflap.gui.environment.Environment;
import edu.duke.cs.jflap.gui.environment.EnvironmentFrame;
import edu.duke.cs.jflap.gui.environment.GrammarEnvironment;
import edu.duke.cs.jflap.gui.environment.Profile;
import edu.duke.cs.jflap.gui.environment.Universe;
import edu.duke.cs.jflap.gui.environment.tag.CriticalTag;
import edu.duke.cs.jflap.gui.grammar.GrammarInputPane;
import edu.duke.cs.jflap.gui.grammar.parse.CYKParsePane;
import edu.duke.cs.jflap.gui.sim.multiple.InputTableModel;
import edu.duke.cs.jflap.automata.Automaton;

import edu.duke.cs.jflap.automata.turing.TuringMachine;

/**
 * Similiar code to MultipleSimulateAction.  Once again, did not want to mess up the original code.
 * So, I created the new MutlipleCYKSimulateAction class just for Multiple CYK Parsing.
 * 
 * @author Kyung Min (Jason) Lee
 *
 */
public class MultipleCYKSimulateAction extends MultipleSimulateAction {

	private Grammar myOriginalGrammar;
	private Grammar myCNFGrammar;
	private Environment myEnvironment;
	
	public MultipleCYKSimulateAction(Grammar original, Grammar cnf, Environment environment) {
		super(original, environment);
		myOriginalGrammar=original;
		myEnvironment=environment;
		myCNFGrammar=cnf;
	}

	public void performAction(Component source){
		
        table = initializeTable(getObject());
		if(((InputTableModel)table.getModel()).isMultiple){
			getEnvironment().remove(getEnvironment().getActive());
		}
		
		JPanel panel = new JPanel(new BorderLayout());
		JToolBar bar = new JToolBar();
		panel.add(new JScrollPane(table), BorderLayout.CENTER);
		panel.add(bar, BorderLayout.SOUTH);
		// Add the running input thing.
		bar.add(new AbstractAction("Load Inputs"){

			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				try {
					// Make sure any recent changes are registered.
					table.getCellEditor().stopCellEditing();
				} catch (NullPointerException exception) {
					// We weren't editing anything, so we're OK.
				}
				InputTableModel model = (InputTableModel) table.getModel();
				JFileChooser ourChooser=new JFileChooser (System.getProperties().getProperty("user.dir"));
				int retval=ourChooser.showOpenDialog(null);
				File f=null;
				if (retval==JFileChooser.APPROVE_OPTION)
				{
					f=ourChooser.getSelectedFile();
					try 
					{
						Scanner sc=new Scanner(f);
						int last=model.getRowCount()-1;
						while (sc.hasNext())
						{
							String temp=sc.next();
						//	System.out.println(temp);
							model.setValueAt(temp, last, 0);
							last++;
						}
					}
					catch (FileNotFoundException e1) {
						// TODO Auto-generate catch block
						e1.printStackTrace();
					}
				}
			}
			
		});
		bar.add(new AbstractAction("Run Inputs") {
			public void actionPerformed(ActionEvent e) {
				try {
					// Make sure any recent changes are registered.
					table.getCellEditor().stopCellEditing();
				} catch (NullPointerException exception) {
					// We weren't editing anything, so we're OK.
				}
				InputTableModel model = (InputTableModel) table.getModel();
				
				if(getObject() instanceof Grammar){
					String[][] inputs = model.getInputs();
					int uniqueInputs = inputs.length;
					Grammar currentGram = (Grammar)getObject();
					CYKParsePane parsePane = new CYKParsePane((GrammarEnvironment)getEnvironment(), myOriginalGrammar, myCNFGrammar, model);
					parsePane.inputField.setEditable(false);    
                    parsePane.row = -1;
					parsePane.parseMultiple();
				}
			}
			
		});
		if(!((InputTableModel)table.getModel()).isMultiple){
		// Add the clear button.
		bar.add(new AbstractAction("Clear") {
			public void actionPerformed(ActionEvent e) {
				try {
					// Make sure any recent changes are registered.
					table.getCellEditor().stopCellEditing();
				} catch (NullPointerException exception) {
					// We weren't editing anything, so we're OK.
				}
				InputTableModel model = (InputTableModel) table.getModel();              
				model.clear();
			}
		});
		
        /*
         * So that it will show up as Lambday or Epsilon, depending on the
         * profile. Sorry about the cheap hack. 
         * 
         * Jinghui Lim
         */
        String empty = "Lambda";
        if(Universe.curProfile.getEmptyString().equals(Profile.LAMBDA))
            empty = "Lambda";
        else if(Universe.curProfile.getEmptyString().equals(Profile.EPSILON))
            empty = "Epsilon";
		bar.add(new AbstractAction("Enter " + empty/*"Enter Lambda"*/) {
			public void actionPerformed(ActionEvent e) {
				int row = table.getSelectedRow();
				if (row == -1)
					return;
				for (int column = 0; column < table.getColumnCount() - 1; column++)
					table.getModel().setValueAt("", row, column);
			}
		});
		}
		if(((InputTableModel)table.getModel()).isMultiple){
		    
		    bar.add(new AbstractAction("Edit File"){
		        public void actionPerformed(ActionEvent arg0) {		            
		            int k = getMachineIndexBySelectedRow(table);
		            if(k>=0 && k < getEnvironment().myObjects.size()){
		               if(getObject() instanceof Grammar){
		                    Grammar cur = (Grammar)getEnvironment().myObjects.get(k);
		                    GrammarInputPane ep = new GrammarInputPane(cur);      
		                    ep.setName(cur.getFileName());
		                    getEnvironment().add(ep, "Edit", new CriticalTag() {
		                    });
		                    getEnvironment().setActive(ep);
		                }
                    }	            
		        }
		    });
		    
        	bar.add(new AbstractAction("Add input string"){
        		public void actionPerformed(ActionEvent arg0) {
                    //add input
                    int inputsNeeded = 1;
                    boolean turing = false;
        			if(getEnvironment().myObjects.get(0) instanceof TuringMachine){      			 
                        turing = true;
                    }
                        Object input = initialInput((Component) getEnvironment().getActive(), "Input");
    
            			if(input instanceof String){
            				String s = (String)input;
            				((ArrayList)getEnvironment().myTestStrings).add(s);
            			}
            			else if(input instanceof String[]){
            				String[] s = (String[]) input;
                            for(int k = 0; k < s.length; k++){
                                ((ArrayList)getEnvironment().myTestStrings).add(s[k]);
                            }
            			}
                        else return;
                    
                    //add expected output
                    if(turing){
                            Object output = initialInput((Component) getEnvironment().getActive(), "Expected Output?");
        
    
                            if(output instanceof String){
                                String s = (String)output;
                                ((ArrayList)getEnvironment().myTransducerStrings).add(s);
                            }
                            else if(output instanceof String[]){
                                String[] s = (String[]) output;
                                for(int k = 0; k < s.length; k++){
                                    ((ArrayList)getEnvironment().myTransducerStrings).add(s[k]);
                                }
                            }
                            else{
                                getEnvironment().myTestStrings.remove(getEnvironment().myTestStrings.size()-1);
                                return;
                            }
                        
                    }
                    //add expected result
                    Object result = initialInput((Component) getEnvironment().getActive(), "Expected Result? (Accept or Reject)");

                    if(result instanceof String){
                        String s = (String)result;
                        ((ArrayList)getEnvironment().myTransducerStrings).add(s);
                    }
                    else if(result instanceof String[]){
                        String[] s = (String[]) result;
                        ((ArrayList)getEnvironment().myTransducerStrings).add(s[0]);
                    }
                    else {
                        getEnvironment().myTestStrings.remove(getEnvironment().myTestStrings.size()-1);
                        getEnvironment().myTransducerStrings.remove(getEnvironment().myTestStrings.size()-1);
                        return;
                    }
                    
        			getEnvironment().remove(getEnvironment().getActive());
                    performAction(getEnvironment().getActive());
                    
                }
            	});
        	
        	bar.add(new AbstractAction("Add file"){
        		public void actionPerformed(ActionEvent arg0) {
        			TestAction test = new TestAction();
        			test.chooseFile(getEnvironment().getActive(), false);
        			getEnvironment().remove(getEnvironment().getActive());
                    performAction(getEnvironment().getActive());                  
                }
            	});
        	
        	bar.add(new AbstractAction("Remove file"){
        		public void actionPerformed(ActionEvent arg0) {
        			int k = getMachineIndexBySelectedRow(table);
                    if(k>=0 && k < getEnvironment().myObjects.size()){
            			getEnvironment().myObjects.remove(k);
            			int row = table.getSelectedRow();
       			
            			int objSize = getEnvironment().myObjects.size();
            			int stringSize = getEnvironment().myTestStrings.size();
            			
            			int beginOffset = row%stringSize;
            			int begin = (row-beginOffset);
            			
            			for(int i = 0; i < (stringSize); i++){
            				((InputTableModel)table.getModel()).deleteRow(begin);      				
            		 }  
            			table.changeSelection(0,0, false, false);
                    }
                }
            	});
        	
        	bar.add(new AbstractAction("Save Results"){
        	    public void actionPerformed(ActionEvent arg0) {
        	        final JFrame frame = new JFrame("Save Location");
        	        
        	        
        	        final JRadioButton defaultLocation = new JRadioButton("Save Results with Original File");
        	        defaultLocation.setMnemonic(KeyEvent.VK_B);
        	        defaultLocation.setActionCommand("Save Results with Original File");
        	        defaultLocation.addActionListener(new ActionListener() {
        	            public void actionPerformed(ActionEvent event){
        	            }
        	        });
        	        final JRadioButton specifyLocation = new JRadioButton("Specify New Location");
        	        specifyLocation.addActionListener(new ActionListener() {
        	            public void actionPerformed(ActionEvent event){
        	            }
        	        });
        	        specifyLocation.setMnemonic(KeyEvent.VK_C);
        	        specifyLocation.setActionCommand("Specify New Location");
        	        defaultLocation.setSelected(true);
        	        ButtonGroup group = new ButtonGroup();
        	        group.add(defaultLocation);
        	        group.add(specifyLocation);
        	        
        	        
        	        JPanel panel = new JPanel();
        	        panel.add(defaultLocation);
        	        panel.add(specifyLocation);
        	        frame.getContentPane().add(panel, BorderLayout.CENTER);
        	        
        	        JButton accept = new JButton("Accept");
        	        accept.addActionListener(new ActionListener() {
        	            public void actionPerformed(ActionEvent event) {
        	                frame.setVisible(false);
                            String filepath = "";
                            boolean failedSave = false;
        	                if(specifyLocation.isSelected()){
        	                    //                                  The save as loop.
        	                    File file = null;
        	                    boolean badname = false;
        	                    while (badname || file ==null) {
        	                        if (!badname) {
                                        Universe.CHOOSER.setFileFilter(null);
                                        Universe.CHOOSER.setDialogTitle("Choose directory to save files in");
                                        Universe.CHOOSER.setFileSelectionMode(Universe.CHOOSER.DIRECTORIES_ONLY);
        	                            int result = Universe.CHOOSER.showSaveDialog(frame);
        	                            if (result != JFileChooser.APPROVE_OPTION)
        	                                break;
        	                            file = Universe.CHOOSER.getSelectedFile();
        	                            
        	                            try {
        	                                // Get the suggested file name.
        	                                filepath = file.getCanonicalPath();
                                            int last = filepath.lastIndexOf("\\");
                                            if(last == -1) filepath = filepath+"/";
                                            else filepath = filepath+"\\";
        	                                
        	                            }
        	                            catch (IOException e) {
        	                                // TODO Auto-generated catch block
        	                                e.printStackTrace();
        	                            }
        	                        }
        	                    }
        	                }
                            if(filepath.equals("")) failedSave = true;
        	                InputTableModel model = (InputTableModel)table.getModel();
        	                String oldfileName = (String)model.getValueAt(0, 0);
        	                String fileName = (String)model.getValueAt(0, 0);
        	                boolean turing = false;
        	                Object machine = getEnvironment().myObjects.get(0);
        	                String base = filepath;
        	                if(machine instanceof Automaton){
        	                    if(machine instanceof TuringMachine){
        	                        turing = true;
        	                    }
        	                    if(failedSave) base = ((Automaton)machine).getFilePath();
        	                }
        	                else if(machine instanceof Grammar){
        	                    if(failedSave) base = ((Grammar)machine).getFilePath();
        	                }
        	                
        	                
        	                try{
            	                FileWriter writer = new FileWriter(base+"results"+fileName+".txt");   
            	                BufferedWriter bwriter = new BufferedWriter(writer);
            	                PrintWriter out = new PrintWriter(bwriter);;
            	                for(int r = 0; r<model.getRowCount(); r++){
            	                    fileName = (String)model.getValueAt(r, 0);                      
            	                    if(!fileName.equals(oldfileName)){
            	                        oldfileName = fileName;  
            	                        out.flush();
            	                        out.close();
            	                        if(fileName.equals("")) break;
            	                        int index = getMachineIndexByName(fileName);
            	                        machine = getEnvironment().myObjects.get(index);
            	                        if(machine instanceof Automaton){                                 
            	                            if(!specifyLocation.isSelected() || failedSave) base = ((Automaton)machine).getFilePath();
            	                        }
            	                        else if(machine instanceof Grammar){
                                            if(!specifyLocation.isSelected() || failedSave) base = ((Grammar)machine).getFilePath();
            	                        }
            	                        bwriter = new BufferedWriter(new FileWriter(base+"results"+fileName+".txt"));
            	                        out = new PrintWriter(bwriter);
            	                    }
            	                    boolean input = false;
            	                    boolean end = false;
            	                    boolean output = false;
            	                    
            	                    for(int c = 1; c < model.getColumnCount(); c++){   
            	                        if((((String)model.getColumnName(c)).startsWith("Input")) && !input){
            	                            out.write("Input: ");
            	                            input = true;
            	                        }
            	                        if((((String)model.getColumnName(c)).startsWith("Output")) && !output && turing){
            	                            out.write("Output: ");
            	                            output = true;
            	                        }
            	                        if(((String)model.getColumnName(c)).startsWith("Result")){
            	                            end = true;
            	                            out.write("Result: ");
            	                        }
            	                        String value = (String)model.getValueAt(r, c);
            	                        
            	                        out.write(value+" ");
            	                        try {
            	                            if(end){
            	                                bwriter.newLine();
            	                            }
            	                        }
            	                        catch (IOException e) {
            	                            
            	                        }
            	                    }                          
            	                }
            	                out.close();
            	            }
                            catch(IOException e){
                                
                            }
                        }
            
        	        });
        	        
        	        frame.getContentPane().add(accept, BorderLayout.SOUTH);
        	        frame.pack();
        	        Point point = new Point(100, 50);
        	        frame.setLocation(point);
        	        frame.setVisible(true);
        	    }
        	});
        	
        }
        
        myPanel = panel;
		// Set up the final view.
        Object finObject = getObject();

        if(finObject instanceof Grammar){
        	CYKParsePane bp = new CYKParsePane((GrammarEnvironment)getEnvironment(), (Grammar)finObject, myCNFGrammar, (InputTableModel)table.getModel());
        	bp.inputField.setEditable(false);
            if(getEnvironment().myTestStrings != null && getEnvironment().myTestStrings.size()>0) bp.inputField.setText((String)getEnvironment().myTestStrings.get(0));
        	JSplitPane split = SplitPaneFactory.createSplit(getEnvironment(), true,
    				0.5, bp, panel);
          
        	MultiplePane mp = new MultiplePane(split);
        	getEnvironment().add(mp, getComponentTitle(), new CriticalTag() {
    		});
    		getEnvironment().setActive(mp);
        }
		
	}
	
	private int getMachineIndexBySelectedRow(JTable table){
		InputTableModel model = (InputTableModel) table.getModel();
        int row = table.getSelectedRow();
        if(row < 0) return -1;
        String machineFileName = (String)model.getValueAt(row, 0);
        return getMachineIndexByName(machineFileName);
	}
	
	public int getMachineIndexByName(String machineFileName){
	        ArrayList machines = getEnvironment().myObjects;
	        if(machines == null) return -1;
	        for(int k = 0; k < machines.size(); k++){            
	            Object current = machines.get(k);
	            if(current instanceof Grammar){
	            	Grammar cur = (Grammar)current;
	            	if(cur.getFileName().equals(machineFileName)){
	            		return k;
	                }
	            }
	            
	        }
	        return -1;
	}
	
	
	/**
	 * Handles the creation of the multiple input pane.
	 * 
	 * @param e
	 *            the action event
	 */
	public void actionPerformed(ActionEvent e) {
		performAction((Component)e.getSource());		
	}

	/**
	 * @param machineFileName
     * 
     */
    protected void updateView(String machineFileName, String input, JTableExtender table) {
        ArrayList machines = this.getEnvironment().myObjects;
        Object current = null;
        if(machines != null) current = machines.get(0);
        else current = this.getEnvironment().getObject();
            if(current instanceof Grammar && (table.getSelectedRow() < (table.getRowCount()-1))){
            	int spot = this.getMachineIndexBySelectedRow(table);
            	Grammar cur = null;
            	if(spot != -1) cur = (Grammar)machines.get(spot);
            	else cur = (Grammar)this.getEnvironment().getObject();
                
	                CYKParsePane bp = new CYKParsePane((GrammarEnvironment)getEnvironment(), cur, myCNFGrammar, null);
	                int column = 1;
	                if(spot == -1) column = 0;
	               bp.inputField.setText((String)table.getModel().getValueAt(table.getSelectedRow(), column));
	               //bp.inputField.setEnabled(false); 
                   bp.inputField.setEditable(false);              
	                JSplitPane split = SplitPaneFactory.createSplit(getEnvironment(), true,
	                        0.5, bp, myPanel);
	                MultiplePane mp = new MultiplePane(split);
	                getEnvironment().add(mp, getComponentTitle(), new CriticalTag() {
	                });
	                
	                EnvironmentFrame frame = Universe.frameForEnvironment(getEnvironment());
	                String newTitle = cur.getFileName();
                    if(newTitle != "") frame.setTitle(newTitle);
	                getEnvironment().remove(getEnvironment().getActive());
	 
	                
	                getEnvironment().add(mp, getComponentTitle(), new CriticalTag() {
	                });
	                getEnvironment().setActive(mp);
	            
            }
    }
    
    /**
	 * This auxillary class is convenient so that the help system can easily
	 * identify what type of component is active according to its class.
	 */
	public class MultiplePane extends JPanel {
		public MultiplePane(JSplitPane split) {
			super(new BorderLayout());
			add(split, BorderLayout.CENTER);
			mySplit = split;
		}
		public JSplitPane mySplit = null;
	}


}
