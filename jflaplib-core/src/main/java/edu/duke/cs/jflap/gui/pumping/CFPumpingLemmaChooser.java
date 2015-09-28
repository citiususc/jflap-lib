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





package edu.duke.cs.jflap.gui.pumping;

import edu.duke.cs.jflap.pumping.cf.*;
import java.util.ArrayList;

/**
 * A <code>CFPumpingLemmaChooser</code> is a {@link edu.duke.cs.jflap.gui.pumping.PumpingLemmaChooser}
 * for {@link edu.duke.cs.jflap.pumping.ContextFreePumpingLemma}s.
 * 
 * @author Jinghui Lim
 *
 */
public class CFPumpingLemmaChooser extends PumpingLemmaChooser 
{
    /**
     * Adds all the context-free pumping lemmas.
     *
     */
    public CFPumpingLemmaChooser()
    {
        myList = new ArrayList();
        
        //old languages
        myList.add(new AnBnCn());
        myList.add(new WW());
        myList.add(new AnBjAnBj());
        myList.add(new NaNbNc());
        myList.add(new NagNbeNc());
        myList.add(new AiBjCk());
        myList.add(new AnBn());
        
        //new languages (JFLAP 6.2)
        myList.add(new AkBnCnDj());
        myList.add(new WW1WrGrtrThanEq());
        myList.add(new WW1WrEquals());
        myList.add(new W1BnW2());
        myList.add(new W1CW2CW3CW4());                
        myList.add(new W1VVrW2());
    }
}
