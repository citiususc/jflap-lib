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

import edu.duke.cs.jflap.automata.*;
import edu.duke.cs.jflap.grammar.Grammar;
import edu.duke.cs.jflap.grammar.Production;
import edu.duke.cs.jflap.grammar.UnboundGrammar;
import java.io.*;
import java.util.Map;
import edu.duke.cs.jflap.regular.RegularExpression;

/**
 * This is the codec for reading JFLAP structures in the JFLAP 3 saved file
 * format.
 * 
 * @author Thomas Finley
 */

public class JFLAP3Codec extends Codec {
	/**
	 * Given a file, this will return a JFLAP structure associated with that
	 * file. This method should always return a structure, or throw a
	 * {@link ParseException} in the event of failure with a message detailing
	 * the nature of why the decoder failed.
	 * 
	 * @param file
	 *            the file to decode into a structure
	 * @param parameters
	 *            this decoder ignores all parameters
	 * @return a JFLAP structure resulting from the interpretation of the JFLAP
	 *         3 saved file
	 * @throws ParseException
	 *             if there was a problem reading the file
	 */
	public Serializable decode(File file, Map parameters) {
		if (file.getName().endsWith(GRAMMAR_SUFFIX))
			return readGrammar(file);
		if (file.getName().endsWith(REGULAR_EXPRESSION_SUFFIX))
			return readRE(file);
		return readAutomaton(file);
	}

	/**
	 * Reads the file as a regular expression.
	 * 
	 * @param file
	 *            the file to read
	 * @return the regular expression associated with this document
	 */
	private RegularExpression readRE(File file) {
		String rstring = ""; // If nothing found, RE is blank.
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (line.startsWith("#"))
					continue;
				if (line.length() == 0)
					continue;
				rstring = line;
			}
		} catch (FileNotFoundException e) {
			throw new ParseException("Could not find file " + file.getName()
					+ "!");
		} catch (IOException e) {
			throw new ParseException("Error accessing file to write!");
		}
		return new RegularExpression(rstring);
	}

	/**
	 * Reads the file as a grammar.
	 * 
	 * @param file
	 *            the file to read
	 * @return the grammar associated with this document
	 */
	private Grammar readGrammar(File file) {
		Grammar g = new UnboundGrammar();
		int lineNum = 0;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				lineNum++;
				if (line.length() == 0)
					continue;
				if (line.startsWith("#"))
					continue;
				String[] elems = line.split("\\s+");
				int len = elems.length;
				if (len > 3 || len < 2 || !elems[1].equals("->")) {
					throw new ParseException("Line " + lineNum
							+ " is not formatted properly!");
				}
				g.addProduction(new Production(elems[0], len == 3 ? elems[2]
						: ""));
			}
		} catch (FileNotFoundException e) {
			throw new ParseException("Could not find file " + file.getName()
					+ "!");
		} catch (IOException e) {
			throw new ParseException("Error accessing file to write!");
		}
		return g;
	}

	/**
	 * Reads the file as an automaton.
	 * 
	 * @param file
	 *            the file to read
	 * @return the automaton associated with this document
	 */
	private Automaton readAutomaton(File file) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			// Read the automaton type.
			String line = reader.readLine().trim();
			if (line.equals(FINITE_AUTOMATON_CODE))
				return readFA(reader);
			if (line.equals(PUSHDOWN_AUTOMATON_CODE))
				return readPDA(reader);
			if (line.equals(TURING_MACHINE_CODE))
				return readTM(reader);
			throw new ParseException("Unknown machine type " + line + "!");
		} catch (NullPointerException e) {
			throw new ParseException("Unexpected end of file!");
		} catch (FileNotFoundException e) {
			throw new ParseException("Could not find file " + file.getName()
					+ "!");
		} catch (IOException e) {
			throw new ParseException("Error accessing file to write!");
		}
	}

	/**
	 * Reads the lines in the reader as a finite automaton.
	 * 
	 * @param reader
	 *            the source of lines in the file
	 */
	private edu.duke.cs.jflap.automata.fsa.FiniteStateAutomaton readFA(BufferedReader reader)
			throws IOException {
		edu.duke.cs.jflap.automata.fsa.FiniteStateAutomaton fa = new edu.duke.cs.jflap.automata.fsa.FiniteStateAutomaton();
		// Generic states.
		State[] states = readStateCreate(fa, reader);
		String[][][] groups = readTransitionGroups(2, 1, states.length, reader);
		for (int s = 0; s < groups.length; s++) {
			for (int g = 0; g < groups[s].length; g++) {
				String[] group = groups[s][g];
				State to = states[Integer.parseInt(group[1]) - 1], from = states[s];
				if (group[0].equals("null"))
					group[0] = "";
				Transition t = new edu.duke.cs.jflap.automata.fsa.FSATransition(from, to,
						group[0]);
				fa.addTransition(t);
			}
		}
		readStateMove(states, reader);
		return fa;
	}

	/**
	 * Reads the lines in the reader as a pushdown automaton.
	 * 
	 * @param reader
	 *            the source of lines in the file
	 */
	private edu.duke.cs.jflap.automata.pda.PushdownAutomaton readPDA(BufferedReader reader)
			throws IOException {
		String ender = reader.readLine().trim();
		if (!(ender.equals("FINAL") || ender.equals("EMPTY") || ender
				.equals("FINAL+EMPTY")))
			throw new ParseException(ender
					+ " is a bad finishing type for PDA!");
		edu.duke.cs.jflap.automata.pda.PushdownAutomaton pda = new edu.duke.cs.jflap.automata.pda.PushdownAutomaton();
		// Generic states.
		State[] states = readStateCreate(pda, reader);
		String[][][] groups = readTransitionGroups(5, 3, states.length, reader);
		for (int s = 0; s < groups.length; s++) {
			for (int g = 0; g < groups[s].length; g++) {
				String[] group = groups[s][g];
				State to = states[Integer.parseInt(group[3]) - 1], from = states[s];
				try {
					Transition t;
					// Take care of lambda symbols.
					int[] check = { 0, 1, 4 };
					for (int i = 0; i < check.length; i++)
						if (group[check[i]].equals("null"))
							group[check[i]] = "";
					// Create the transition.
					t = new edu.duke.cs.jflap.automata.pda.PDATransition(from, to, group[0],
							group[1], group[4]);
					pda.addTransition(t);
				} catch (IllegalArgumentException e) {
					throw new ParseException(e.getMessage());
				}
			}
		}
		readStateMove(states, reader);
		return pda;
	}

	/**
	 * Reads the lines in the reader as a Turing machine.
	 * 
	 * @param reader
	 *            the source of lines in the file
	 */
	private edu.duke.cs.jflap.automata.turing.TuringMachine readTM(BufferedReader reader)
			throws IOException {
		if (!reader.readLine().trim().equals("TAPE"))
			throw new ParseException("Expected TAPE line absent!");
		// Try to read the number of tapes.
		int tapes = 0;
		try {
			tapes = Integer.parseInt(reader.readLine());
			if (tapes != 1 && tapes != 2)
				throw new ParseException("May only have 1 or 2 tapes!");
		} catch (NumberFormatException e) {
			throw new ParseException("Bad format for number of tapes!");
		}
		edu.duke.cs.jflap.automata.turing.TuringMachine tm = new edu.duke.cs.jflap.automata.turing.TuringMachine(
				tapes);
		// Generic states.
		State[] states = readStateCreate(tm, reader);
		String[][][] groups = readTransitionGroups(1 + 3 * tm.tapes(), 1,
				states.length, reader);
		for (int s = 0; s < groups.length; s++) {
			for (int g = 0; g < groups[s].length; g++) {
				String[] group = groups[s][g];
				State to = states[Integer.parseInt(group[1]) - 1], from = states[s];
				try {
					Transition t;
					// Take care of blank tape symbols.
					int[] check = tm.tapes() == 1 ? new int[] { 0, 2 }
							: new int[] { 0, 2, 4, 5 };
					for (int i = 0; i < check.length; i++)
						if (group[check[i]].equals("B"))
							group[check[i]] = "";
					// Create the transition.
					if (tm.tapes() == 1)
						t = new edu.duke.cs.jflap.automata.turing.TMTransition(from, to,
								group[0], group[2], group[3].toUpperCase());
					else
						t = new edu.duke.cs.jflap.automata.turing.TMTransition(from, to,
								new String[] { group[0], group[4] },
								new String[] { group[2], group[5] },
								new String[] { group[3].toUpperCase(),
										group[6].toUpperCase() });
					tm.addTransition(t);
				} catch (IllegalArgumentException e) {
					throw new ParseException(e.getMessage());
				}
			}
		}
		readStateMove(states, reader);
		return tm;
	}

	/**
	 * Reads the number of states for the automaton.
	 * 
	 * @param automaton
	 *            the automaton
	 * @param reader
	 *            the buffered reader
	 * @return an array of the states created
	 */
	private State[] readStateCreate(Automaton automaton, BufferedReader reader)
			throws IOException {
		// Read the number of states.
		State[] states = null;
		try {
			int numStates = Integer.parseInt(reader.readLine());
			if (numStates < 0)
				throw new ParseException("Number of states cannot be "
						+ numStates + "!");
			states = new State[numStates];
		} catch (NumberFormatException e) {
			throw new ParseException("Bad format for number of states!");
		}
		for (int i = 0; i < states.length; i++)
			states[i] = automaton.createState(new java.awt.Point(0, 0));
		// Next possibly two lines have something to do with alphabet.
		reader.readLine();
		if (!(automaton instanceof edu.duke.cs.jflap.automata.fsa.FiniteStateAutomaton))
			reader.readLine();
		// Read the ID of the initial state.
		try {
			int initStateID = Integer.parseInt(reader.readLine());
			if (initStateID < 1 || initStateID > states.length)
				throw new ParseException("Initial state cannot be "
						+ initStateID + ".");
			automaton.setInitialState(states[initStateID - 1]);
		} catch (NumberFormatException e) {
			throw new ParseException("Bad format for initial state ID!");
		}
		// Read the IDs of the final states.
		String line = reader.readLine();
		String[] lineTokens = line.split("\\s+");
		if (lineTokens.length == 0)
			throw new ParseException("Final state list is empty line!");
		try {
			int last = Integer.parseInt(lineTokens[lineTokens.length - 1]);
			if (last != 0)
				throw new ParseException(
						"Final state list not terminated with 0!");
			try {
				for (int i = 0; i < lineTokens.length - 1; i++) {
					automaton.addFinalState(states[Integer
							.parseInt(lineTokens[i]) - 1]);
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				throw new ParseException("Bad final state ID read!");
			}
		} catch (NumberFormatException e) {
			throw new ParseException("Bad format in final state list!");
		}
		return states;
	}

	/**
	 * Reads the state positions and moves the states.
	 * 
	 * @param states
	 *            the array of states
	 * @param reader
	 *            the buffered reader
	 */
	private void readStateMove(State[] states, BufferedReader reader)
			throws IOException {
		for (int i = 0; i < states.length; i++) {
			int x, y;
			String[] tokens = reader.readLine().split("\\s+");
			try {
				x = Integer.parseInt(tokens[1]);
				y = Integer.parseInt(tokens[2]);
			} catch (NumberFormatException e) {
				throw new ParseException("State " + (i + 1)
						+ "'s position badly formatted.");
			} catch (ArrayIndexOutOfBoundsException e) {
				throw new ParseException("State " + (i + 1)
						+ "'s position string too short.");
			}
			states[i].getPoint().setLocation(x, y);
		}
	}

	/**
	 * Read the string sequences for each transition.
	 * 
	 * @param groupSize
	 *            the number of tokens for each transition
	 * @param idPosition
	 *            the position in each group of the state ID
	 * @param numStates
	 *            the number of states
	 * @param reader
	 *            the buffered reader
	 * @return an array of size number of states, with each entry an array of
	 *         size number of transitions for a given state, with each entry of
	 *         that an array of size "groupSize"
	 */
	private String[][][] readTransitionGroups(int groupSize, int idPosition,
			int numStates, BufferedReader reader) throws IOException {
		String[][][] groups = new String[numStates][][];
		for (int s = 0; s < numStates; s++) {
			ParseException p = new ParseException("Transition line " + (s + 1)
					+ " badly formatted.");
			String[] tokens = reader.readLine().split("\\s+");
			if ((tokens.length % groupSize) != 1
					|| !tokens[tokens.length - 1].equals("EOL"))
				throw p;
			groups[s] = new String[tokens.length / groupSize][];
			for (int g = 0; g < groups[s].length; g++) {
				groups[s][g] = new String[groupSize];
				for (int i = 0; i < groupSize; i++)
					groups[s][g][i] = tokens[groupSize * g + i];
				try {
					int i = Integer.parseInt(groups[s][g][idPosition]);
					if (i < 1 || i > numStates)
						throw p;
				} catch (NumberFormatException e) {
					throw p;
				}
			}
		}
		return groups;
	}

	/**
	 * Given a structure, this will attempt to write the structure as a JFLAP 3
	 * saved file.
	 * 
	 * @param structure
	 *            the structure to encode
	 * @param file
	 * @param parameters
	 *            implementors have the option of accepting custom parameters in
	 *            the form of a map
	 * @return the file to which the structure was written
	 * @throws EncodeException
	 *             if there was a problem writing the file
	 */
	public File encode(Serializable structure, File file, Map parameters) {
		return file;
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
		return false;
	}

	/**
	 * Returns the description of this codec.
	 * 
	 * @return the description of this codec
	 */
	public String getDescription() {
		return "JFLAP 3 File";
	}

	/** The JFLAP 3 file suffixes. */
	public static final String FINITE_AUTOMATON_SUFFIX = ".FA",
			PUSHDOWN_AUTOMATON_SUFFIX = ".PDA", TURING_MACHINE_SUFFIX = ".TM",
			TWO_TAPE_TURING_MACHINE_SUFFIX = ".TTM", GRAMMAR_SUFFIX = ".GRM",
			REGULAR_EXPRESSION_SUFFIX = ".REX";

	/** Automaton type codes internal to the file. */
	private static final String FINITE_AUTOMATON_CODE = "One-Way-FSA",
			PUSHDOWN_AUTOMATON_CODE = "PDAP", TURING_MACHINE_CODE = "REGTM";
}
