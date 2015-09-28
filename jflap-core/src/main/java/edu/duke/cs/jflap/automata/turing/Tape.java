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

import java.io.Serializable;

/**
 * A tape for a Turing machine. The tape head can move across the tape, reading
 * and writing individual characters.
 * 
 * @author Ryan Cavalcante
 */

public class Tape implements Serializable {
	/**
	 * Instantiates an empty tape object.
	 */
	public Tape() {
		this("");
	}

	/**
	 * Instantiates a tape object with the tape head pointing to the first
	 * character of <CODE>input</CODE>.
	 * 
	 * @param input
	 *            the input string to write to the tape
	 */
	public Tape(String input) {
		buffer = new StringBuffer();
		if (input.equals(""))
			input = "" + BLANK;
		buffer.insert(0, input);
		tapeHead = 0;
	}

	/**
	 * Instantiates a tape that is a copy of a given tape.
	 * 
	 * @param tape
	 *            the tape to copy
	 */
	public Tape(Tape tape) {
		this.buffer = new StringBuffer(tape.buffer.toString());
		tapeHead = tape.getTapeHead();
		cachedHash = tape.cachedHash;
	}

	/**
	 * Writes <CODE>character</CODE> to the tape.
	 * 
	 * @param character
	 *            the character to write to the tape.
	 */
	public void writeChar(char character) {
		buffer.deleteCharAt(tapeHead);
		buffer.insert(tapeHead, character);
		cachedHash = 0xdeadbeef;
	}

	/**
	 * Writes <CODE>symbol</CODE> to the tape.
	 * 
	 * @param symbol
	 *            the symbol to write to the tape.
	 */
	public void write(String symbol) {
		buffer.deleteCharAt(tapeHead);
		buffer.insert(tapeHead, symbol);
		cachedHash = 0xdeadbeef;
	}

	/**
	 * Returns the character pointed to by the tape head.
	 * 
	 * @return the character pointed to by the tape head.
	 */
	public char readChar() {
		char[] toReturn = new char[1];
		buffer.getChars(tapeHead, tapeHead + 1, toReturn, 0);
		return toReturn[0];
	}

	/**
	 * Returns the character pointed to by the tape head in a string.
	 * 
	 * @return a string representation of the character pointed to by the tape
	 *         head.
	 */
	public String read() {
		char[] toReturn = new char[1];
		buffer.getChars(tapeHead, tapeHead + 1, toReturn, 0);
		return new String(toReturn);
	}

	/**
	 * Moves the tape head in <CODE>direction</CODE>.
	 * 
	 * @param direction
	 *            the direction to move the tape head.
	 * @throws IllegalArgumentException
	 *             if <CODE>direction</CODE> is not one of "L", "R", or "S"
	 */
	public void moveHead(String direction) {
		try {
			switch (direction.charAt(0)) {
			case 'L':
				tapeHead--;
				break;
			case 'R':
				tapeHead++;
				break;
			case 'S':
				break;
			default:
				throw new IllegalArgumentException("Bad tape direction "
						+ direction);
			}
		} catch (IndexOutOfBoundsException e) {
			throw new IllegalArgumentException(
					"Tape direction is empty string!");
		}

		/**
		 * If the tape head is moved to an index out of the range of the buffer,
		 * the buffer needs to grow accordingly, filled with blanks where
		 * appropriate.
		 */

		if (tapeHead >= buffer.length()) {
			int bufferLength = buffer.length();
			buffer.setLength(tapeHead + 1);
			for (int k = bufferLength; k < buffer.length(); k++) {
				buffer.deleteCharAt(k);
				buffer.insert(k, BLANK);
			}
		} else if (tapeHead < 0) {
			int numToInsert = Math.abs(tapeHead);
			char[] toInsert = new char[numToInsert];
			for (int i = 0; i < numToInsert; i++) {
				toInsert[i] = BLANK;
			}
			buffer.insert(0, toInsert);
			tapeHead = 0;
		}
	}

	/**
	 * Returns the contents of the tape, from tape index 0 till the end of the
	 * tape.
	 * 
	 * @return the contents of the tape as a string
	 */
	public String getContents() {
		return buffer.toString();
	}

	/**
	 * Returns the output of the tape. The first character of the output
	 * consists of the symbol underneath the tape head, and further consists of
	 * all symbols to the right of the tape head in order until a blank is
	 * encountered. In the event that the tape head is on a blank the empty
	 * string is returned.
	 * 
	 * @return the output of the tape
	 */
	public String getOutput() {
		int nextBlank = buffer.indexOf("" + BLANK, getTapeHead());
		if (nextBlank == -1)
			nextBlank = buffer.length();
		return buffer.substring(getTapeHead(), nextBlank);
	}

	/**
	 * Returns the index in the buffer that the tape head is currently pointing
	 * to.
	 * 
	 * @return the index in the buffer that the tape head is currently pointing
	 *         to.
	 */
	public int getTapeHead() {
		return tapeHead;
	}

	/**
	 * Returns a string representation of the tape object.
	 * 
	 * @return a string representation of the tape object.
	 */
	public String toString() {
		return "[" + buffer.toString() + "]" + " TAPE HEAD AT " + tapeHead;
	}

	/**
	 * Retrieves the "non-trivial" section of the tape. What is meant by
	 * non-trivial is the section of tape modulo a prefix and suffix of blank
	 * tape symbols.
	 * 
	 * @param section
	 *            an array of two intergers, which will hold, when finished, the
	 *            index of the first non-blank character in the first entry, and
	 *            the index of the first blank character of the suffix. Here,
	 *            <CODE>section[1]-section[0]</CODE> is the length of the
	 *            non-trivial section.
	 */
	private void nonTrivial(int[] section) {
		int s, e;
		for (e = buffer.length() - 1; e > 0 && buffer.charAt(e) == BLANK; e--)
			;
		if (buffer.charAt(e) != BLANK)
			e++;
		for (s = 0; s < e && buffer.charAt(s) == BLANK; s++)
			;
		section[0] = s;
		section[1] = e;
	}

	/**
	 * Compares two tapes for equality. Two tapes are equal if they contain the
	 * same characters and are at the same position in the tape, modulo a prefix
	 * of some blank characters.
	 * 
	 * @param tape
	 *            the tape to compare against for equality
	 * @return <CODE>true</CODE> if the tapes are equal, <CODE>false</CODE>
	 *         if they are not
	 */
	public boolean equals(Object tape) {
		if (tape == this)
			return true;
		Tape t;
		try {
			t = (Tape) tape;
		} catch (ClassCastException e) {
			return false;
		}
		// These variables are necessary for going into the tape so we
		// can consider everything other than the "blank" prefix.
		int[] first = new int[2], second = new int[2];
		// Do not consider the blank prefixes.
		this.nonTrivial(first);
		t.nonTrivial(second);
		// If they're not the same length, who cares?
		if (first[1] - first[0] != second[1] - second[0])
			return false;
		// If they're at different positions, who cares?
		if (tapeHead - first[0] != t.tapeHead - second[0])
			return false;
		// If all else fails, compare the characters.
		for (; first[0] < first[1]; first[0]++, second[0]++)
			if (buffer.charAt(first[0]) != t.buffer.charAt(second[0]))
				return false;
		// We've made it!
		return true;
	}

	/**
	 * Returns a hash code for this tape.
	 * 
	 * @return a hash code for this tape
	 */
	public int hashCode() {
		if (cachedHash != 0xdeadbeef)
			return cachedHash;
		int[] bounds = new int[2];
		this.nonTrivial(bounds);
		return cachedHash = buffer.substring(bounds[0], bounds[1]).hashCode();
	}

	/** The string buffer. */
	private StringBuffer buffer = new StringBuffer();

	/** The tape head (index in buffer). */
	private int tapeHead;

	/** The cached hash code, since it takes a bit to compute. */
	private int cachedHash = 0xdeadbeef;

	/** The blank tape symbol. */
	public static final char BLANK = '\u25A1';
}
