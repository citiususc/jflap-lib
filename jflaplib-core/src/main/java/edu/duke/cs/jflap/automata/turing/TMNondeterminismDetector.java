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





package edu.duke.cs.jflap.automata.turing;

import edu.duke.cs.jflap.automata.NondeterminismDetector;
import edu.duke.cs.jflap.automata.Transition;

/**
 * The TTM nondeterminism detector object can be used to find all the
 * nondeterministic states in a Turing machine (i.e. all states with transitions
 * that read the same symbols on each tape).
 * 
 * @author Thomas Finley
 */

public class TMNondeterminismDetector extends NondeterminismDetector {
	/**
	 * Creates an instance of a <CODE>TMNondeterminismDetector</CODE>.
	 */
	public TMNondeterminismDetector() {
	}

	/**
	 * Returns true if the transitions introduce nondeterminism (e.g. the input
	 * to read from tapes one and two portions of the transition labels are
	 * identical).
	 * 
	 * @param t1
	 *            a transition
	 * @param t2
	 *            a transition
	 * @return true if the transitions introduce nondeterminism
	 */
	public boolean areNondeterministic(Transition t1, Transition t2) {
		TMTransition transition1 = (TMTransition) t1;
		TMTransition transition2 = (TMTransition) t2;
		for (int i = 0; i < transition1.tapes(); i++) {
			String read1 = transition1.getRead(i);
			String read2 = transition2.getRead(i);
			if (read1.equals(read2))
				return true;
			if (read1.equals("~") || read2.equals("~"))
				return true;
			String wordOne = read2;
			String wordTwo = read1;
			if (read1.startsWith("!")) {
				wordOne = read1;
				wordTwo = read2;
			}
			String firstWordsLetter = "";

			if (wordOne.startsWith("!")) {
				if (wordTwo.startsWith("!")) {
					return true;
				}
				for (int k = 0; k < wordTwo.length(); k++) {
					firstWordsLetter = wordTwo.substring(k, k + 1);
					if (firstWordsLetter.equals(","))
						continue;
					for (int m = 0; m < wordOne.length(); m++) {
						if (wordOne.indexOf(firstWordsLetter) == -1) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}
}
