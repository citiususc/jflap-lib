package es.usc.citius.jflap.cli;


import edu.duke.cs.jflap.automata.fsa.FiniteStateAutomaton;
import edu.duke.cs.jflap.file.XMLCodec;

import java.io.File;

public final class IO {
    private IO(){}

    public static FiniteStateAutomaton loadAutomaton(String path){
        return loadAutomaton(new File(path));
    }

    public static FiniteStateAutomaton loadAutomaton(File file){
        if (!file.isFile()) throw new RuntimeException(file.getAbsolutePath() + " does not exist");
        return (FiniteStateAutomaton)new XMLCodec().decode(file, null);
    }

}
