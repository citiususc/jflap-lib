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





package edu.duke.cs.jflap.gui.environment;

import edu.duke.cs.jflap.gui.pumping.PumpingLemmaChooser;

/**
 * An environment for {@link edu.duke.cs.jflap.pumping.PumpingLemma}s.
 * 
 * @author Jinghui Lim
 *
 */
public class PumpingLemmaEnvironment extends Environment 
{
    public PumpingLemmaEnvironment(PumpingLemmaChooser lemma) 
    {
        super(lemma);
    }
}
