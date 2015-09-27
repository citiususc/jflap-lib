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

import edu.duke.cs.jflap.pumping.*;
import edu.duke.cs.jflap.pumping.reg.*;
import edu.duke.cs.jflap.pumping.cf.*;

/**
 * 
 * @author Jinghui Lim
 *
 */
public class PumpingLemmaFactory 
{
    public static PumpingLemma createPumpingLemma(String type, String name)
    {
        if(type.equals(RegPumpingLemmaTransducer.TYPE))
        {
            if(name.equals(new ABnAk().getTitle()))
                return new ABnAk();
            else if(name.equals(new AnBkCnk().getTitle()))
                return new AnBkCnk();
            else if(name.equals(new AnBlAk().getTitle()))
                return new AnBlAk();
            else if(name.equals(new edu.duke.cs.jflap.pumping.reg.AnBn().getTitle()))
                return new edu.duke.cs.jflap.pumping.reg.AnBn();
            else if(name.equals(new AnEven().getTitle()))
                return new AnEven();
            else if(name.equals(new NaNb().getTitle()))
                return new NaNb();
            else if(name.equals(new Palindrome().getTitle()))
                return new Palindrome();
            
            else if (name.equals(new BBABAnAn().getTitle()))
            	return new BBABAnAn();
            else if (name.equals(new B5W().getTitle()))
            	return new B5W();
            else if (name.equals(new BkABnBAn().getTitle()))
            	return new BkABnBAn();
            else if (name.equals(new AnBk().getTitle()))
            	return new AnBk();
            else if (name.equals(new AB2n().getTitle()))
            	return new AB2n();
            else if (name.equals(new B5Wmod().getTitle()))
            	return new B5Wmod();            
            else    // this should not happen 
                return null;
        }
        else if(type.equals(CFPumpingLemmaTransducer.TYPE))
        {
            if(name.equals(new AnBjAnBj().getTitle()))
                return new AnBjAnBj();
            else if(name.equals(new AiBjCk().getTitle()))
                return new AiBjCk();
            else if(name.equals(new edu.duke.cs.jflap.pumping.cf.AnBn().getTitle()))
                return new edu.duke.cs.jflap.pumping.cf.AnBn();
            else if(name.equals(new AnBnCn().getTitle()))
                return new AnBnCn();
            else if(name.equals(new NagNbeNc().getTitle()))
                return new NagNbeNc();
            else if(name.equals(new NaNbNc().getTitle()))
                return new NaNbNc();
            else if(name.equals(new WW().getTitle()))
                return new WW();
            
            else if (name.equals(new WW1WrEquals().getTitle()))
            	return new WW1WrEquals();
            else if (name.equals(new W1BnW2().getTitle()))
            	return new W1BnW2();
            else if (name.equals(new W1CW2CW3CW4().getTitle()))
            	return new W1CW2CW3CW4();
            else if (name.equals(new WW1WrGrtrThanEq().getTitle()))
            	return new WW1WrGrtrThanEq();
            else if (name.equals(new AkBnCnDj().getTitle()))
            	return new AkBnCnDj();
            else if (name.equals(new W1VVrW2().getTitle()))
            	return new W1VVrW2();  
            else    // this shouldn't happen
                return null;
        }
        else    // this shouldn't happen
            return null;
    }
}
