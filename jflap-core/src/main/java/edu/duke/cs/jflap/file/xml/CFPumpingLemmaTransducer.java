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





package edu.duke.cs.jflap.file.xml;

import java.io.Serializable;
import java.util.ArrayList;

import org.w3c.dom.*;

import edu.duke.cs.jflap.pumping.*;

/**
 * This is the transducer for encoding and decoding 
 * {@link edu.duke.cs.jflap.pumping.ContextFreePumpingLemma} objects.
 * 
 * @author Jinghui Lim
 * @see edu.duke.cs.jflap.gui.pumping.PumpingLemmaChooser
 *
 */
public class CFPumpingLemmaTransducer extends PumpingLemmaTransducer 
{
    /**
     * The type of pumping lemma.
     */
    public static String TYPE = "context-free pumping lemma";
    /**
     * The tag for the length of <i>u</i>.
     */
    public static String U_NAME = "uLength";
    /**
     * The tag for the length of <i>v</i>.
     */
    public static String V_NAME = "vLength";
    /**
     * The tag for the length of <i>x</i>.
     */
    public static String X_NAME = "xLength";
    /**
     * The tag for the length of <i>y</i>.
     */
    public static String Y_NAME = "yLength";
    /**
     * The tag for a case.
     */
    public static String CASE_NAME = "case";
    /**
     * The tag for the <i>i</i> of a case.
     */
    public static String CASE_I_NAME = "caseI";
    /**
     * The tag for the length of <i>u</i> of a case.
     */
    public static String CASE_U_NAME = "caseULength";
    /**
     * The tag for the length of <i>v</i> of a case.
     */
    public static String CASE_V_NAME = "caseVLength";
    /**
     * The tag for the length of <i>x</i> of a case.
     */
    public static String CASE_X_NAME = "caseXLength";
    /**
     * The tag for the length of <i>y</i> of a case.
     */
    public static String CASE_Y_NAME = "caseYLength";
    
    public Serializable fromDOM(Document document) 
    {
        ContextFreePumpingLemma pl = (ContextFreePumpingLemma)PumpingLemmaFactory.createPumpingLemma
            (TYPE, document.getElementsByTagName(LEMMA_NAME).item(0).getTextContent());
        /*
         * Decode m, w, & i.
         */
        pl.setM(Integer.parseInt(document.getElementsByTagName(M_NAME).item(0).getTextContent()));
        pl.setW(document.getElementsByTagName(W_NAME).item(0).getTextContent());
        pl.setI(Integer.parseInt(document.getElementsByTagName(I_NAME).item(0).getTextContent()));       

        /*
         * Decode cases. 
         * 
         * Must decode cases before decoding the decomposition, otherwise 
         * the decomposition will be that of the last case. This is because,
         * when add case is called, the pumping lemma chooses the decomposition
         * to check if it's legal. 
         */
        readCases(document, pl);

        //Decode the attempts
        NodeList attempts = document.getDocumentElement().getElementsByTagName(ATTEMPT);
        for(int i = 0; i < attempts.getLength(); i++)
            pl.addAttempt(attempts.item(i).getTextContent());        
        
        //Decode the first player.         
        pl.setFirstPlayer(document.getElementsByTagName(FIRST_PLAYER).item(0).getTextContent());
             
        // Decode the decomposition.
        int uLength = Integer.parseInt(document.getElementsByTagName(U_NAME).item(0).getTextContent());
        int vLength = Integer.parseInt(document.getElementsByTagName(V_NAME).item(0).getTextContent());
        int xLength = Integer.parseInt(document.getElementsByTagName(X_NAME).item(0).getTextContent());
        int yLength = Integer.parseInt(document.getElementsByTagName(Y_NAME).item(0).getTextContent());
        
        pl.setDecomposition(new int[]{uLength, vLength, xLength, yLength});
        
        //Return!
        return pl;
    }
    
    protected void readCases(Document doc, ContextFreePumpingLemma pl)
    {
        NodeList caseNodes = doc.getDocumentElement().getElementsByTagName(CASE_NAME);
        for(int i = 0; i < caseNodes.getLength(); i++)
        {
            Node caseNode = caseNodes.item(i);
            if(caseNode.getNodeType() != Node.ELEMENT_NODE)
                continue;
            int u = Integer.parseInt(((Element)caseNode).getElementsByTagName(CASE_U_NAME).item(0).getTextContent());
            int v = Integer.parseInt(((Element)caseNode).getElementsByTagName(CASE_V_NAME).item(0).getTextContent());
            int x = Integer.parseInt(((Element)caseNode).getElementsByTagName(CASE_X_NAME).item(0).getTextContent());
            int y = Integer.parseInt(((Element)caseNode).getElementsByTagName(CASE_Y_NAME).item(0).getTextContent());
            int j = Integer.parseInt(((Element)caseNode).getElementsByTagName(CASE_I_NAME).item(0).getTextContent());
            pl.addCase(new int[]{u, v, x, y}, j);
        }
    }

    public Document toDOM(Serializable structure) 
    {
        ContextFreePumpingLemma pl = (ContextFreePumpingLemma)structure;
        Document doc = newEmptyDocument();
        Element elem = doc.getDocumentElement();
        elem.appendChild(createElement(doc, LEMMA_NAME, null, pl.getTitle()));
        elem.appendChild(createElement(doc, FIRST_PLAYER, null, pl.getFirstPlayer()));
        elem.appendChild(createElement(doc, M_NAME, null, "" + pl.getM()));
        elem.appendChild(createElement(doc, W_NAME, null, "" + pl.getW()));
        elem.appendChild(createElement(doc, I_NAME, null, "" + pl.getI()));
        elem.appendChild(createElement(doc, U_NAME, null, "" + pl.getU().length()));
        elem.appendChild(createElement(doc, V_NAME, null, "" + pl.getV().length()));
        elem.appendChild(createElement(doc, X_NAME, null, "" + pl.getX().length()));
        elem.appendChild(createElement(doc, Y_NAME, null, "" + pl.getY().length()));
        
        //Encode the list of attempts.
        ArrayList attempts = pl.getAttempts();
        if(attempts != null && attempts.size() > 0)        
            for(int i = 0; i < attempts.size(); i++)
                elem.appendChild(createElement(doc, ATTEMPT, null, (String)attempts.get(i)));
                
        //Encode the list of attempts.
        ArrayList cases = pl.getDoneCases();
        if(cases != null && cases.size() > 0)
            for(int i = 0; i < cases.size(); i++)
                elem.appendChild(createCaseElement(doc, (Case)cases.get(i)));
        
        return doc;
    }
    
    protected Element createCaseElement(Document doc, Case c)
    {
        Element elem = createElement(doc, CASE_NAME, null, null);
        
        int[] decomposition = c.getInput();
        elem.appendChild(createElement(doc, CASE_U_NAME, null, "" + decomposition[0]));
        elem.appendChild(createElement(doc, CASE_V_NAME, null, "" + decomposition[1]));
        elem.appendChild(createElement(doc, CASE_X_NAME, null, "" + decomposition[2]));
        elem.appendChild(createElement(doc, CASE_Y_NAME, null, "" + decomposition[3]));
        elem.appendChild(createElement(doc, CASE_I_NAME, null, "" + c.getI()));
        return elem;
    }

    public String getType() 
    {
        return TYPE;
    }
}
