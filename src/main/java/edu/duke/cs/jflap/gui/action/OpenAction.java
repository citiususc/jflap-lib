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

import edu.duke.cs.jflap.file.Codec;
import edu.duke.cs.jflap.file.DataException;
import edu.duke.cs.jflap.file.ParseException;
import edu.duke.cs.jflap.gui.environment.EnvironmentFrame;
import edu.duke.cs.jflap.gui.environment.FrameFactory;
import edu.duke.cs.jflap.gui.environment.Universe;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

import edu.duke.cs.jflap.automata.turing.TuringMachine;

/**
 * The <CODE>OpenAction</CODE> is an action to load a structure from a file,
 * and create a new environment with that object.
 * 
 * @author Thomas Finley
 */

public class OpenAction extends RestrictedAction {
	/**
	 * Instantiates a new <CODE>OpenAction</CODE>.
	 */
	public OpenAction() {
		super("Open...", null);
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O,
				MAIN_MENU_MASK));
		this.fileChooser = Universe.CHOOSER;
		//this.fileChooser = new JFileChooser (System.getProperties().getProperty("user.dir"));
	}

	/**
	 * If an open is attempted, call the methods that handle the retrieving of
	 * an object, then create a new frame for the environment.
	 * 
	 * @param event
	 *            the action event
	 */
	public void actionPerformed(ActionEvent event) {
		Component source = null;
		lastFileOpened = false;
		try {
			source = (Component) event.getSource();
		} catch (Throwable e) {
			// Might not be a component, or the event may be null.
			// Who cares.
		}

		// Apple is so stupid.
		
		File tempFile = fileChooser.getCurrentDirectory();
		fileChooser.setCurrentDirectory(tempFile.getParentFile());
		fileChooser.setCurrentDirectory(tempFile);
		fileChooser.rescanCurrentDirectory();
		fileChooser.setMultiSelectionEnabled(true);
		Codec[] codecs = null;
		codecs = makeFilters();

		// Open the dialog.
		int result = fileChooser.showOpenDialog(source);
		if (result != JFileChooser.APPROVE_OPTION)
			return;
		File[] files = fileChooser.getSelectedFiles();
		for(int k = 0; k < files.length; k++){
		    File file = files[k];           
    		if (!openOrRead) {
    			// Is this file already open?
    			if (Universe.frameForFile(file) != null) {
    				Universe.frameForFile(file).toFront();
    				return;
    			}
    		}
    		try {
    			openFile(file, codecs);
    			
    		} catch (ParseException e) {
    			JOptionPane.showMessageDialog(source, e.getMessage(), "Read Error",
    					JOptionPane.ERROR_MESSAGE);
    		} catch (DataException e) {
    			JOptionPane.showMessageDialog(source, e.getMessage(), "Data Error",
    					JOptionPane.ERROR_MESSAGE);
    		}
        }
        Universe.CHOOSER.resetChoosableFileFilters();
		lastFileOpened = true;
	}

	public static java.io.Serializable readFileAndCodecs(File file) {
		OpenAction.setOpenOrRead(true);
		Codec[] codecs = null;
		codecs = makeFilters();
		openFile(file, codecs);
		OpenAction.setOpenOrRead(false);
		return OpenAction.getLastObjectOpened();
	}

	public static Codec[] makeFilters() {
		// Set up the file filters.
		Universe.CHOOSER.resetChoosableFileFilters();
		List decoders = Universe.CODEC_REGISTRY.getDecoders();
		Iterator it = decoders.iterator();
		while (it.hasNext())
			Universe.CHOOSER.addChoosableFileFilter((FileFilter) it.next());
		Universe.CHOOSER.setFileFilter(Universe.CHOOSER
				.getAcceptAllFileFilter());

		// Get the decoders.
		Codec[] codecs = null;
		FileFilter filter = Universe.CHOOSER.getFileFilter();
		if (filter == Universe.CHOOSER.getAcceptAllFileFilter()) {
			codecs = (Codec[]) decoders.toArray(new Codec[0]);
		} else {
			codecs = new Codec[1];
			codecs[0] = (Codec) filter;
		}
		

		return codecs;
	}

	/**
	 * Attempts to open a specified file with a set of codecs.
	 * 
	 * @param file
	 *            the file to attempt to open
	 * @param codecs
	 *            the codecs to use
	 * @throws ParseException
	 *             if there was an error with all or one of the codecs
	 */
	public static void openFile(File file, Codec[] codecs) {
		ParseException p = null;
		for (int i = 0; i < codecs.length; i++) {
			try {
				Serializable object = codecs[i].decode(file, null);
				if (openOrRead && !(object instanceof TuringMachine)) {
                    JOptionPane.showMessageDialog(null,
                            "Only Turing Machine files can be added as building blocks.", "Wrong File Type",
                            JOptionPane.ERROR_MESSAGE);
                    return;
					
				}
				lastObject = object;
				lastFile = file;
				// Set the file on the thing.
				if (!openOrRead) {
					EnvironmentFrame ef = FrameFactory.createFrame(object);
					if (ef == null)
						return;
					ef.getEnvironment().setFile(file);
					ef.getEnvironment().setEncoder(
							codecs[i].correspondingEncoder());
				}
				return;
			} catch (ParseException e) {
				p = e;
			}
		}
		if (codecs.length != 1)
			p = new ParseException("No format could read the file!");
		throw p;
	}

	/**
	 * The open action is completely environment independent.
	 * 
	 * @param object
	 *            some object, which is ignored
	 * @return always returns <CODE>true</CODE>
	 */
	public static boolean isApplicable(Object object) {
		return true;
	}

	/**
	 * Returns the last object opened by the filebrowswer.
	 * 
	 * @return
	 */
	public static Serializable getLastObjectOpened() {
		return lastObject;
	}

	/**
	 * Returns the last file opened by the filebrowswer.
	 * 
	 * @return
	 */
	public static File getLastFileOpened() {
		return lastFile;
	}
	
	public static boolean isOpened()	{
		return lastFileOpened;
	}

	/**
	 * @param b
	 */
	public static void setOpenOrRead(boolean b) {
		openOrRead = b;
	}

	// ** False causes file to be opened, True causes file to be read but not
	// opened"
	private static boolean openOrRead = false;

	/** The file chooser. */
	private JFileChooser fileChooser;

	private static Serializable lastObject = null;

	private static File lastFile = null;
	
	private static boolean lastFileOpened = false;

	/** The exception class for when a file could not be read properly. */
	protected static class FileReadException extends RuntimeException {
		/**
		 * Instantiates a file read exception with a given message.
		 * 
		 * @param message
		 *            the specific message for why the read failed
		 */
		public FileReadException(String message) {
			super(message);
		}
	}

}
