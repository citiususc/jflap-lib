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





package edu.duke.cs.jflap.file;

import edu.duke.cs.jflap.file.xml.*;
import java.io.*;
import java.util.Map;
import javax.xml.parsers.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * This is the codec for reading and writing JFLAP structures as XML documents.
 * 
 * @author Thomas Finley, Henry Qin
 */

public class XMLCodec extends Codec {

	private final Logger logger = LoggerFactory.getLogger(XMLCodec.class);
    /**
      * Determines which files this FileFilter will allow. We are only allowing files with extension XML and jff.
      * 
      */
    @Override
    public boolean accept(File f){
        if (f.isDirectory()) return true;
        if (f.getName().endsWith(".xml") || (f.getName().endsWith(".jff"))) return true;
        return false;
    } 

	/**
	 * Given a file, this will return a JFLAP structure associated with that
	 * file.
	 * 
	 * @param file
	 *            the file to decode into a structure
	 * @param parameters
	 *            these parameters are ignored
	 * @return a JFLAP structure resulting from the interpretation of the file
	 * @throws ParseException
	 *             if there was a problem reading the file
	 */
	public Serializable decode(File file, Map parameters) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(file);
			Transducer transducer = TransducerFactory.getTransducer(doc);
			return transducer.fromDOM(doc);
		} catch (ParserConfigurationException e) {
			throw new ParseException("Java could not create the parser!");
		} catch (IOException e) {
			throw new ParseException("Could not open file to read!");
		} catch (org.xml.sax.SAXException e) {
			throw new ParseException("Could not parse XML!\n" + e.getMessage());
		} catch (ExceptionInInitializerError e) {
			// Hmm. That shouldn't be.
			logger.error("Static Init: {}", e.getLocalizedMessage(), e);
			throw new ParseException("Unexpected Error!");
		}
	}

	/**
	 * Given a structure, this will attempt to write the structure as a
	 * serialized object to a file.
	 * 
	 * @param structure
	 *            the structure to encode
	 * @param file
	 *            the file to save the structure to
	 * @param parameters
	 *            implementors have the option of accepting custom parameters in
	 *            the form of a map
	 * @return the file to which the structure was written
	 * @throws EncodeException
	 *             if there was a problem writing the file
	 */
	public File encode(Serializable structure, File file, Map parameters) {
		Transducer transducer = null;
		try {
			transducer = TransducerFactory.getTransducer(structure);
            
            /*
             * If we are saving a pumping lemma, the associated structure would
             * actually be a pumping lemma chooser. Thus, we have to get the
             * lemma from the chooser.
             */
            Document dom;
            if(structure instanceof edu.duke.cs.jflap.gui.pumping.PumpingLemmaChooser)
                dom = transducer.toDOM(((edu.duke.cs.jflap.gui.pumping.PumpingLemmaChooser)structure).getCurrent());
            else
                dom = transducer.toDOM(structure);
            
//			Document dom = transducer.toDOM(structure);    // original line
            
			DOMPrettier.makePretty(dom);
			Source s = new DOMSource(dom);
			Result r = new StreamResult(file);
			Transformer t = TransformerFactory.newInstance().newTransformer();
			t.transform(s, r);
			return file;
		} catch (IllegalArgumentException e) {
			throw new EncodeException(
					"No XML transducer available for this structure!");
		} catch (TransformerConfigurationException e) {
			throw new EncodeException("Could not open file to write!");
		} catch (TransformerException e) {
			throw new EncodeException("Could not open file to write!");
		}
	}

	/**
	 * Returns if this type of structure can be encoded with this encoder. This
	 * should not perform a detailed check of the structure, since the user will
	 * have no idea why it will not be encoded correctly if the {@link #encode}
	 * method does not throw a {@link ParseException}.
	 * 
	 * @param structure
	 *            the structure to check
	 * @return if the structure, perhaps with minor changes, could possibly be
	 *         written to a file
	 */
	public boolean canEncode(Serializable structure) {
		return true;
	}

	/**
	 * Returns the description of this codec.
	 * 
	 * @return the description of this codec
	 */
	public String getDescription() {
		return "JFLAP 4 File";
	}

	/**
	 * Given a proposed filename, returns a new suggested filename. JFLAP 4
	 * saved files have the suffix <CODE>.jf4</CODE> appended to them.
	 * 
	 * @param filename
	 *            the proposed name
	 * @param structure
	 *            the structure that will be saved
	 * @return the new suggestion for a name
	 */
	public String proposeFilename(String filename, Serializable structure) {
		if (!filename.endsWith(SUFFIX))
			return filename + SUFFIX;
		return filename;
	}

	/** The filename suffix. */
	public static final String SUFFIX = ".jff";
}
