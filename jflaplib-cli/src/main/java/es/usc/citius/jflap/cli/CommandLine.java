package es.usc.citius.jflap.cli;


import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import edu.duke.cs.jflap.automata.AutomatonSimulator;
import edu.duke.cs.jflap.automata.SimulatorFactory;
import edu.duke.cs.jflap.automata.fsa.FSAToRegularExpressionConverter;
import edu.duke.cs.jflap.automata.fsa.FiniteStateAutomaton;
import edu.duke.cs.jflap.automata.graph.FSAEqualityChecker;
import edu.duke.cs.jflap.file.xml.AutomatonTransducer;

import java.io.File;
import java.util.*;

public class CommandLine {

    public static void main(String[] args) throws IllegalAccessException, InstantiationException {

        Cli
                .include("run", RunInputCommand.class)
                .andInclude("equivalent", EquivalentCommand.class)
                .andInclude("regular", FiniteAutomatonToRE.class)
                .showTraceOnError(false)
                .parseAndRun(args);

    }


    private static File checked(File file){
        if (!file.exists()) throw new RuntimeException("File: " + file.getAbsolutePath() + " does not exist");
        if (!file.isFile()) throw new RuntimeException("File: " + file.getAbsolutePath() + " is not a valid file");
        return file;
    }

    @Parameters(separators = "=", commandDescription = "Convert a Finite State Automaton to a Regular Expression")
    public static class FiniteAutomatonToRE implements Runnable {

        @Parameter(description = "<file>", required = true, arity = 1)
        private List<String> file = new ArrayList<String>();

        @Override
        public void run() {
            FiniteStateAutomaton a = IO.loadAutomaton(file.get(0));
            FSAToRegularExpressionConverter.convertToSimpleAutomaton(a);
            String re = FSAToRegularExpressionConverter.convertToRegularExpression(a);
            System.out.println(re);
        }
    }

    @Parameters(separators = "=", commandDescription = "Check if two FSA accept the same language")
    public static class EquivalentCommand implements Runnable {

        @Parameter(description = "<file1> <file2>", arity = 2, required = true)
        private List<String> files = new ArrayList<String>();

        @Override
        public void run() {
            FiniteStateAutomaton a1 = IO.loadAutomaton(files.get(0));
            FiniteStateAutomaton a2 = IO.loadAutomaton(files.get(1));
            boolean equal = new FSAEqualityChecker().equals(a1, a2);
            System.out.println(equal);
        }
    }


    @Parameters(separators = "=", commandDescription = "Runs the JFLAP automaton file on the input string")
    public static class RunInputCommand implements Runnable {

        @Parameter(description = "<file> <input>", arity = 2, required = true)
        private List<String> params = new ArrayList<String>();

        @Override
        public void run() {
            if (params.size() != 2) throw new RuntimeException("Incorrect arguments. Please provide <file> <input>");
            File file = checked(new File(params.get(0)));
            String input = params.get(1);
            FiniteStateAutomaton automaton = IO.loadAutomaton(file);
            // Load a simulator to test the automaton
            AutomatonSimulator sim = SimulatorFactory.getSimulator(automaton);
            if (sim == null) throw new RuntimeException("Cannot load an automaton simulator for " + automaton.getClass());
            // Test the automaton with an input
            boolean accept = sim.simulateInput(input);
            System.out.println(accept);
        }
    }

    private static class Cli {
        private JCommander jc = new JCommander();
        private Map<String, Runnable> commands;
        private boolean showTrace = false;

        private Cli(Map<String, Runnable> commands) {
            this.commands = commands;
            for(Map.Entry<String, Runnable> cmd : commands.entrySet()){
                jc.addCommand(cmd.getKey(), cmd.getValue());
            }
        }

        private static Runnable instantiate(Class<? extends Runnable> cmd){
            try {
                return cmd.newInstance();
            } catch(Exception e){
                throw new RuntimeException(e);
            }
        }

        public static Cli include(String name, Class<? extends Runnable> cmdClass) {
            Map<String, Runnable> commands = new HashMap<String, Runnable>();
            commands.put(name, instantiate(cmdClass));
            return new Cli(commands);
        }

        public Cli andInclude(String name, Class<? extends Runnable> cmdClass){
            Runnable cmd = instantiate(cmdClass);
            commands.put(name, cmd);
            jc.addCommand(name, cmd);
            return this;
        }

        public Cli showTraceOnError(boolean show){
            this.showTrace = show;
            return this;
        }

        public void parseAndRun(String... args){
            jc.parse(args);
            String command = jc.getParsedCommand();
            if (command == null) {
                jc.usage();
                return;
            }
            // Invoke selected command
            try {
                commands.get(command).run();
            } catch (Exception e){
                if (showTrace) {
                    e.printStackTrace();
                } else {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }

        public JCommander getCli(){
            return jc;
        }
    }
}
