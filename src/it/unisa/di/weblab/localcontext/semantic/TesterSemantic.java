/*******************************************************************************
 * Copyright (c) 2016 Alfonso Ferraioli.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Mozilla Public License, v. 2.0
 * which accompanies this distribution, and is available at
 * http://mozilla.org/MPL/2.0/
 ******************************************************************************/

package it.unisa.di.weblab.localcontext.semantic;

/**
 * @author Alfonso Ferraioli
 * @version $Id: Tester.java 360 2015-07-18 10:02:27Z fabio85 $
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.script.ScriptException;

import it.unisa.di.weblab.localcontext.Tester;
import it.unisa.di.weblab.localcontext.Tester.Result;

public class TesterSemantic {
	public static class ResultSemantic {
		
		private final GraphSemantic graph;
		private final boolean error;
		private final HashMap<String, ArrayList<String>> problems;
		
		public HashMap<String, ArrayList<String>> getProblems() {
			return problems;
		}

		public boolean isError() {
			return error;
		}

		public GraphSemantic getGraphSemantic() {
			return graph;
		}
		
		
		public ResultSemantic(GraphSemantic graph,boolean error, HashMap<String, ArrayList<String>> problems) {
			this.graph = graph;
			this.error = error;
			this.problems = problems;
		}
			
	}
	
	public static boolean verbose = true;
	
	public static ResultSemantic runSem(InputStream semanticDefinition,Result res) throws CloneNotSupportedException, ScriptException {
		
		GraphSemantic gs = new GraphSemantic(res.getGraphDisambiguation().getGraph());			
		ParserXMLSemanticDefinition parser = new ParserXMLSemanticDefinition(semanticDefinition);
		
		gs.setVerbose(verbose);
		gs.init(parser);		
		gs.initTextArea(res.getTexts());
		if (!gs.getProblems().isEmpty()) {
			if (verbose) System.out.println(gs.getProblems());
			return new ResultSemantic(gs,true,gs.getProblems());
		}
		gs.createVisit(parser);
		if (!gs.getProblems().isEmpty()) {
			if (verbose) System.out.println(gs.getProblems());
			return new ResultSemantic(gs,true,gs.getProblems());
		}
		
		gs.setVerbose(verbose);
		gs.executeProperty();
		if (!gs.getProblems().isEmpty()) {
			if (verbose) System.out.println(gs.getProblems());
			return new ResultSemantic(gs,true,gs.getProblems());
		}
		
		gs.typeCheckStrctureAndPostCondition();	
		if (!gs.getProblems().isEmpty()) {
			if (verbose) System.out.println(gs.getProblems());
			return new ResultSemantic(gs,true,gs.getProblems());
		}
		
		return new ResultSemantic(gs,false,gs.getProblems());
	}
	
	/**
	 * @param args
	 * @throws ScriptException 
	 * @throws CloneNotSupportedException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws ScriptException, CloneNotSupportedException, FileNotFoundException {
		
		Result res = Tester.run(new FileInputStream(new File(args[0])), new FileInputStream(new File(args[1])));
		if (!res.isError()) {
			ResultSemantic resSem = runSem(new FileInputStream(new File(args[2])),res);
			if (!resSem.isError()){	
				System.out.println(resSem.getGraphSemantic().getResult());
			}else {
				System.out.println(resSem.getProblems());
			}
		} else {
			System.out.println(res.description);
		}	
	}

}
