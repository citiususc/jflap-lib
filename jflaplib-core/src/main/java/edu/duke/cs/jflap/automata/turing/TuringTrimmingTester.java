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

import edu.duke.cs.jflap.grammar.Production;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Temporary tester for turing trimming algo
 * TODO: delete this tester class
 * @author Kyung Min (Jason) Lee
 *
 */
public class TuringTrimmingTester {


	private Production[] prods;
	public TuringTrimmingTester(String filename)
	{
		int index=0;
		Scanner sc;
		prods=new Production[42];
		try {
			sc = new Scanner(new File(filename));
			while (sc.hasNextLine())
			{
				String line=sc.nextLine()+" ";
				String[] aa=line.split("->");
				Production p=new Production(aa[0], aa[1]);
				prods[index]=p;
				index++;
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		trim();
		
		System.out.println("New Productions");
		
		for (int i=0; i<prods.length; i++)
		{
			System.out.println(prods[i].getLHS()+"->"+prods[i].getRHS());
		}

	}
	
	private void trim()
	{
		char ch='A';
		for (int i=0; i<prods.length; i++)
		{
			String lhs=prods[i].getLHS();
			if (ch=='S' || ch=='T')
			{
				ch++;
			}
			int aa=lhs.indexOf("V(");
			while (aa>-1)
			{
			
		//		System.out.println("in 1st "+lhs+"===>    ");
				int bb=lhs.indexOf(")");
				String var="";
				if ((aa+bb+1) > lhs.length())
				{
					var=lhs.substring(aa, aa+bb);
					lhs=lhs.substring(0, aa)+ch;
				}
				else
				{
					var=lhs.substring(aa, aa+bb+1);
					lhs=lhs.substring(0, aa)+ch+lhs.substring(aa+bb);
				}
			//	System.out.println(var+ " and new lhs is = "+lhs);
				aa=lhs.indexOf("V(");
				
				System.out.println(var+" converted to : "+ch);
					
				//	lhs.replaceAll("V"+aa[j], "A");
				for (int k=0; k<prods.length; k++)
				{
					
					String inner_lhs=prods[k].getLHS();
					String inner_rhs=prods[k].getRHS();
					int a=inner_lhs.indexOf(var);
					if (a>-1)
					{
				//		System.out.println("in inner lhs  "+inner_lhs+"   ===>    ");
						inner_lhs=inner_lhs.substring(0, a)+""+ch+inner_lhs.substring(a+var.length());
				//		System.out.println(inner_lhs);
					}
					a=inner_rhs.indexOf(var);
					if (a>-1)
					{
			//			System.out.println("in inner rhs   "+inner_rhs+"   ===>    ");
						
						inner_rhs=inner_rhs.substring(0, a)+""+ch+inner_rhs.substring(a+var.length());
			//			System.out.println(inner_rhs);
						
					}
					prods[k]=new Production(inner_lhs, inner_rhs);
				}
				ch=(char) (ch+1);
		
			//	System.out.println(lhs);
			}
		}
	}
	
	public static void main(String[] args)
	{
		TuringTrimmingTester t=new TuringTrimmingTester("productions.txt");
	}
}
