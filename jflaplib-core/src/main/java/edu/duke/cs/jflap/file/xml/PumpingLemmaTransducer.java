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

/**
 * This is an abstract implementation of a transducer for
 * {@link edu.duke.cs.jflap.pumping.PumpingLemma} objects.
 * 
 * @author Jinghui Lim
 * @see edu.duke.cs.jflap.gui.pumping.PumpingLemmaChooser
 *
 */
public abstract class PumpingLemmaTransducer extends AbstractTransducer 
{
    /**
     * The tag for the name of the pumping lemma.
     */
    public static String LEMMA_NAME = "name";
    /**
     * The tag for who goes first.
     */
    public static String FIRST_PLAYER = "first_player";
    /**
     * The tag for the <i>m</i> value of the pumping lemma.
     */
    public static String M_NAME = "m";
    /**
     * The tag for the <i>w</i> value of the pumping lemma.
     */
    public static String W_NAME = "w";
    /**
     * The tag for the <i>i</i> value of the pumping lemma.
     */
    public static String I_NAME = "i";
    /**
     * The tag for a representation of a prior attempt.
     */
    public static String ATTEMPT = "attempt";
    /**
     * The comment for <i>m</i>.
     */
    public static String COMMENT_M = "The user's input of m.";
    /**
     * The comment for <i>i</i>. The value of <i>i</i> is needed because
     * it is sometimes randomized.
     */
    public static String COMMENT_I = "The program's value of i.";
}
