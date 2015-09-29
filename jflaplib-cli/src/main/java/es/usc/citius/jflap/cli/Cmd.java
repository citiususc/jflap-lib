package es.usc.citius.jflap.cli;


import edu.duke.cs.jflap.automata.AutomatonSimulator;
import edu.duke.cs.jflap.automata.SimulatorFactory;
import edu.duke.cs.jflap.automata.fsa.FiniteStateAutomaton;
import edu.duke.cs.jflap.automata.graph.FSAEqualityChecker;
import edu.duke.cs.jflap.file.XMLCodec;
import io.airlift.airline.*;

import java.io.File;
import java.util.List;


public class Cmd {

    public static void main(String[] args) {
        Cli.CliBuilder<Runnable> builder = Cli.<Runnable>builder("jflaplib-cli")
                .withDescription("Jflap-lib command line tool for JFLAP 7.0")
                .withDefaultCommand(Help.class)
                .withCommand(Help.class);

        builder.withGroup("automaton")
                .withDescription("Test JFLAP automata")
                .withDefaultCommand(SimulateAutomaton.class)
                .withCommand(SimulateAutomaton.class)
                .withCommand(EqualAutomata.class);

        Cli<Runnable> parser = builder.build();

        parser.parse(args).run();
    }

    private static FiniteStateAutomaton loadAutomaton(String path){
        return loadAutomaton(new File(path));
    }

    private static FiniteStateAutomaton loadAutomaton(File file){
        if (!file.isFile()) throw new RuntimeException(file.getAbsolutePath() + " does not exist");
        return (FiniteStateAutomaton)new XMLCodec().decode(file, null);
    }

    @Command(name = "equivalent", description = "Check if two FSA accept the same language")
    public static class EqualAutomata implements Runnable {

        @Arguments(description = "Path of the two FSA to be compared")
        public List<String> automatons;

        @Override
        public void run() {
            FiniteStateAutomaton a1 = loadAutomaton(automatons.get(0));
            FiniteStateAutomaton a2 = loadAutomaton(automatons.get(1));
            boolean equal = new FSAEqualityChecker().equals(a1, a2);
            System.out.println("equivalent?: " + equal);
        }
    }

    @Command(name = "run", description = "Runs the automaton on the input string")
    public static class SimulateAutomaton implements Runnable {

        @Arguments(
                description = "JFLAP (*.jff) automaton file and input to be tested",
                usage = "[automaton file] [input]"
        )
        public List<String> arguments;

        @Override
        public void run() {
            String path = arguments.get(0);
            String input = arguments.get(1);
            FiniteStateAutomaton automaton = loadAutomaton(path);
            // Load a simulator to test the automaton
            AutomatonSimulator sim = SimulatorFactory.getSimulator(automaton);
            if (sim == null) throw new RuntimeException("Cannot load an automaton simulator for " + automaton.getClass());
            // Test the automaton with an input
            boolean accept = sim.simulateInput(input);
            System.out.println(path + "; input: " + input + "; accept?: " + accept);
        }
    }


}
