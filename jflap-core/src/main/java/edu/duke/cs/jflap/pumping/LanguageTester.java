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




package edu.duke.cs.jflap.pumping;

/**
 * This class is simply used to test Pumping Lemma languages and whether given strings are in them.
 * 
 * @author Chris Morgan
 */
public class LanguageTester {
	
	private void test(PumpingLemma p, String[] s, int n) {
		for (int i=0; i<s.length; i++){
			System.out.println("Lemma "+n+" String "+s[i]+" ... "+p.isInLang(s[i]));
		}
		System.out.println();
	}
	
	public static void main(String[] args){
		PumpingLemma r;
		LanguageTester t = new LanguageTester();
		String[] s;		
		
/*		r = new ABnAk();
		s = new String[] {"abababaa", "ababab", "ab", "ababac", "aba", "abc", "a", "b", ""};
		t.test(r, s, 1);
		
		r = new AnBkCnk();
		s = new String[] {"aabbcccc", "abbccc", "aabccc", "ac", "bbcc", "", "acc", "bcc", "ab", "a", "b", "c", "d"};
		t.test(r, s, 2);
		
		r = new AnBlAk();
		s = new String[] {"aaaaaabbbbaaaa", "aaaaaabbbbaaaaa", "abbbb", "aaaaaab", "bbbb", "aaaaaa", "c", ""};
		t.test(r, s, 3);
		
		r = new pumping.reg.AnBn();
		s = new String[] {"aaabbb", "", "ababab", "aab", "abb", "abc"};
		t.test(r, s, 4);
		
		r = new AnEven();
		s = new String[] {"aaaa", "", "aaa", "aba", "b"};
		t.test(r, s, 5);
		
		r = new pumping.reg.AnFac();
		s = new String[] {"aaaaaa", "aa", "aaaaa", "", "baa"};
		t.test(r, s, 6);
		
		r = new AnPrime();
		s = new String[] {"aaaaaaaaaaa", "aaaaa", "aa", "aaaa", "aaaaaaaaaa", "aab", "ab", "a", ""};
		t.test(r, s, 7);
		
		r = new NaNb();
		s = new String[] {"aabbb", "abababb", "bbb", "aaabbb", "abbc", "a", ""};
		t.test(r, s, 8);
		
		r = new Palindrome();
		s = new String[] {"aababbabaa", "aba", "a", "b", "", "aaab", "baba", "abcba"};
		t.test(r, s, 9);*/
/* 
		r = new AiBjCk();
		s = new String[] {"aaabbcc", "aab", "aac", "a", "caa", "aad", "ababaab", "bbaaacc", ""};
		t.test(r, s, 10);
		
		r = new AnBj();
		s = new String[] {"aaaaaaaaabbb", "aaaabb", "ab", "", "bbaaaa", "ba", "abc", "ababaa"};
		t.test(r, s, 11);
		
		r = new AnBjAnBj();
		s = new String[] {"aaabbaaabb", "aa", "bb", "abab", "", "aba", "bab", "aabab", "abba", "aaa", "abcab"};
		t.test(r, s, 12);
		
		r = new pumping.cf.AnBn();
		s = new String[] {"aaabbb", "", "ababab", "aab", "abb", "abc"};
		t.test(r, s, 13);
		
		r = new AnBnCn();
		s = new String[] {"aaabbbccc", "abc", "", "abcabc", "aabc", "abbc", "bac", "bc", "abcc", "ab", "abcd"};
		t.test(r, s, 14);
		
		r = new pumping.cf.AnFac();
		s = new String[] {"aaaaaa", "aa", "aaaaa", "", "baa"};
		t.test(r, s, 15);
		
		r = new NagNbeNc();
		s = new String[] {"aaaabbbccc", "abcabcacabcba", "abcb", "aaabbbccc", "aaabbccd", "c", ""};
		t.test(r, s, 16);
		
		r = new NaNbNc();
		s = new String[] {"aabbbcccc", "cabcabcacbcb", "bcbb", "aaabbbccc", "abbcccd", "a", ""};
		t.test(r, s, 17);
		
		r = new WW();
		s = new String[] {"abbaabba", "baba", "aa", "bb", "", "bab", "ab", "a", "b", "abcabc", "cc"};
		t.test(r, s, 18);  
		
		r = new BBABAnAn();
		s = new String[] {"bbabababaaa", "bbaba", "bbba", "bbabababababababaaaaa", "bbabababaa", "bbabab", "c", ""};
		t.test(r, s, 19);
	
		r = new B5W();
		s = new String[] {"bbbbbabaab", "bbbbb", "bbbbbbbaaa", "bbbbabaaabb", "bbbbbabbab", "bbbbbabacab", ""};
		t.test(r, s, 20);
		
		r = new BkABnBAn();
		s = new String[] {"bbbbabababbababa", "bbbbbabba", "bbbbb", "bbbabba", "bbbbabab", "bbbba", 
						  "bbbbabbaa", "bbbbaabba", "bbbbabbaba", "c", ""};
		t.test(r, s, 21);
		
		r = new AnBk();
		s = new String[] {"aaabb", "ab", "aa", "", "b", "aabbb", "aaabbc", "caaabb"};
		t.test(r, s, 22);
		
		r = new AB2n();
		s = new String[] {"abababab", "ab", "aab", "ababa", "ababcab", ""};
		t.test(r, s, 23);
		
		r = new B5Wmod();
		s = new String[] {"bbbbbaba", "bbbbb", "bbbbbabaabb", "bbbbabaabb", "bbbbbabbab", "bbbbbabacab", ""};
		t.test(r, s, 24); 		
		
		r = new WW1WrEquals();
		s = new String[] {"abbbaabba", "bbbbbb", "bab", "aabaaa", "aabaaaa", "bb", "b", "aca", ""};
		t.test(r, s, 25);*/
		/*
		r = new W1CW2CW3CW4();
		s = new String[] {"abaaacabaaacabbbbbcbbbba", "babbacabaaacbaabacbaaba", "babbacabaacbaabacbaaba", 
						  "abaaacabaaacabbbbbbbbba", "abaaacabaaacabbdbbbcbbbba", ""};
		t.test(r, s, 26);
		
		r = new W1BnW2();
		s = new String[] {"abbaabbbbaaaba", "ababbbabaa", "abbbaa", "bba", "aabbbcaaa", "babbaab", "aabba",
						  "abba", "b", ""};
		t.test(r, s, 27);					
		
		r = new WW1WrGrtrThanEq();
		s = new String[] {"abbbaabba", "aabaaa", "aabaaaa", "bab", "bb", "b", "abbcbba", ""};
		t.test(r, s, 28);
		
		r = new AkBnCnDj();
		s = new String[] {"abc", "bcd", "aabbccddd", "abcd", "aab", "abcda", "bced", "bc", "ab", "bbccc", ""};
		t.test(r, s, 29);
		
		r = new W1VVrW2();
		s = new String[] {"aaaaaaaa", "bbbbbbbbababbabbabbbb", "bbbbbbbbabbbbabb", "bbabbbabb", "aaccaa", "aaabaabaa", ""};  
		t.test(r, s, 30);*/
	}
}
