/*******************************************************************************
 * Copyright (c) 2015 Luca Laurino.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Mozilla Public License, v. 2.0
 * which accompanies this distribution, and is available at
 * http://mozilla.org/MPL/2.0/
 ******************************************************************************/

package it.unisa.di.weblab.localcontext;

/**
 * @author Luca Laurino
 * @version $Id$
 */

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.script.ScriptException;

import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.SimpleGraph;

import it.unisa.di.weblab.localcontext.semantic.TextArea;



public class Tester {
	public static class Result {
		public final boolean result;
		public final String description;
		private final GraphDisambiguation graph;
		private final boolean error;
		private final HashMap<String,ArrayList<TextArea>> texts;
		private final ArrayList<String> resultGlobalCheck;
		private final HashMap<String, ArrayList<String>> resultSelfCheck;
		
		public ArrayList<String> getResultGlobalCheck() {
			return resultGlobalCheck;
		}

		public HashMap<String, ArrayList<String>> getResultSelfCheck() {
			return resultSelfCheck;
		}

		
		public HashMap<String, ArrayList<TextArea>> getTexts() {
			return texts;
		}

		public boolean isError() {
			return error;
		}

		public GraphDisambiguation getGraphDisambiguation() {
			return graph;
		}
		
		public Result(boolean result, String description,
				GraphDisambiguation graph,boolean error, HashMap<String, ArrayList<String>> resultSelfCheck, ArrayList<String> resultGlobalCheck,HashMap<String,ArrayList<TextArea>> texts) {
			this.result = result;
			this.description = description;
			this.graph = graph;
			this.error = error;
			this.texts = texts;
			this.resultGlobalCheck = resultGlobalCheck;
			this.resultSelfCheck = resultSelfCheck;
		}
			
	}
	public static Result run(InputStream in, InputStream definition) throws CloneNotSupportedException, ScriptException {
		/*File file = new File("test2.txt");  
		FileOutputStream fis = new FileOutputStream(file);  
		PrintStream out = new PrintStream(fis);  
		System.setOut(out);*/ 
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		PrintStream old = System.out;
		System.setOut(ps);
		ParserXML parser = new ParserXML(in);
		ParserXMLDefinition parser2 = new ParserXMLDefinition(definition);
		
		ArrayList<ConnectorDefinition> c =parser2.getConnectors();
		ArrayList<SymbolDefinition> s= parser2.getSymbols();
		System.out.println(parser2.toString());
		
		String constraint=parser2.getGlobalConstraints();
		
		ArrayList<Symbol> symbols =parser.getSymbols();
		ArrayList<Connector> connectors =parser.getConnectors();
		GraphBuilder gr = new GraphBuilder(symbols,connectors);
		gr.buildGraph();
		SimpleGraph<Node,GraphEdge> graph = gr.getGraph();
		GraphDisambiguation gd = new GraphDisambiguation(graph, s, c);
		gd.createSymMap();
		gd.createConnMap();
		gd.createConnMap2();
		gd.createSymMap2();
		System.out.println("\nInfo Simboli\n");
		gd.buildSymbolsInfoMap();
		System.out.println("\nInfo connettori \n");
		gd.buildConnectorsInfoMap();
		
		//System.out.println("\n Inserimento nomi nodi \n");
		gd.setNodeNames();
		gd.setEdgeTypes();
		
		System.out.println("\n Grafo di partenza \n");
		System.out.println(gd.toString());
		//gd.removeAmbiguity2();
		gd.removeAmbiguity3();
		System.out.println("\n Ambiguità rimosse \n");
		System.out.println(gd.toString());
		System.out.println("\n");
		ConnectivityInspector<Node,GraphEdge> ins= new ConnectivityInspector<Node,GraphEdge>(gd.getGraph());
		String result = gd.getProblems();
		ArrayList<String> globalResult = new ArrayList<String>(); 
		 
		ErrorMessages erroreMsg = new ErrorMessages();
		//if(constraint.equalsIgnoreCase("Connected")&&!ins.isGraphConnected()) globalResult.add("The graph is not connected");
		if(constraint.equalsIgnoreCase("Connected")&&!ins.isGraphConnected()) globalResult.add(erroreMsg.getDiagramNotConnectedError());
		//gd.show();
	    System.out.flush();
	    System.setOut(old);
	    
	    boolean err =  gd.isError();
	    if (!globalResult.isEmpty()){
	    	err=true;
	    }
	    
	    // Show what happened
		//return baos.toString();
		return new Result(!(result.contains("S_") || result.contains("C_")),
				result, gd, err,gd.mapProblems(),globalResult,parser.getTexts());
	}
	
	/**
	 * @param args
	 * @throws ScriptException 
	 * @throws CloneNotSupportedException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws ScriptException, CloneNotSupportedException, FileNotFoundException {
		//String xml = "<language name=\"ClassDiagram\">\n	<symbol id=\"S_0\" graphicRef=\"Class.svg\">\n		<ap graphicRef=\"Border\" connRef=\"C_1.Head\" />\n		<ap graphicRef=\"Border\" connRef=\"C_2.P1\" />\n		<ap graphicRef=\"Border\" connRef=\"C_6.Tail\" />\n	</symbol>\n	<symbol id=\"S_1\" graphicRef=\"Interface.svg\">\n		<ap graphicRef=\"Border\" connRef=\"C_1.Tail\" />\n		<ap graphicRef=\"Border\" connRef=\"C_3.Head\" />\n	</symbol>\n	<symbol id=\"S_2\" graphicRef=\"Class.svg\">\n		<ap graphicRef=\"Border\" connRef=\"C_2.P2\" />\n	</symbol>\n	<symbol id=\"S_3\" graphicRef=\"Interface.svg\">\n		<ap graphicRef=\"Border\" connRef=\"C_3.Tail\" />\n		<ap graphicRef=\"Border\" connRef=\"C_4.Head\" />\n	</symbol>\n	<symbol id=\"S_4\" graphicRef=\"Interface.svg\">\n		<ap graphicRef=\"Border\" connRef=\"C_3.Tail\" />\n		<ap graphicRef=\"Border\" connRef=\"C_5.Head\" />\n	</symbol>\n	<symbol id=\"S_5\" graphicRef=\"Class.svg\">\n		<ap graphicRef=\"Border\" connRef=\"C_4.Tail\" />\n	</symbol>\n	<symbol id=\"S_6\" graphicRef=\"Class.svg\">\n		<ap graphicRef=\"Border\" connRef=\"C_5.Tail\" />\n	</symbol>\n	<symbol id=\"S_7\" graphicRef=\"Class.svg\">\n		<ap graphicRef=\"Border\" connRef=\"C_6.Head\" />\n		<ap graphicRef=\"Border\" connRef=\"C_7.Tail\" />\n		<ap graphicRef=\"Border\" connRef=\"C_7.Head\" />\n		<ap graphicRef=\"Border\" connRef=\"C_8.Head\" />\n		<ap graphicRef=\"Border\" connRef=\"C_9.Head\" />\n		<ap graphicRef=\"Border\" connRef=\"C_10.Head\" />\n	</symbol>\n	<symbol id=\"S_8\" graphicRef=\"Class.svg\">\n		<ap graphicRef=\"Border\" connRef=\"C_8.Tail\" />\n	</symbol>\n	<symbol id=\"S_9\" graphicRef=\"Class.svg\">\n		<ap graphicRef=\"Border\" connRef=\"C_9.Tail\" />\n	</symbol>\n	<symbol id=\"S_10\" graphicRef=\"Class.svg\">\n		<ap graphicRef=\"Border\" connRef=\"C_10.Tail\" />\n	</symbol>\n	<connector id=\"C_1\" graphicRef=\"Dependency.svg\" />\n	<connector id=\"C_2\" graphicRef=\"Association.svg\" />\n	<connector id=\"C_3\" graphicRef=\"Inheritance.svg\" />\n	<connector id=\"C_4\" graphicRef=\"InterfaceRealization.svg\" />\n	<connector id=\"C_5\" graphicRef=\"InterfaceRealization.svg\" />\n	<connector id=\"C_6\" graphicRef=\"Dependency.svg\" />\n	<connector id=\"C_7\" graphicRef=\"Composition.svg\" />\n	<connector id=\"C_8\" graphicRef=\"Aggregation.svg\" />\n	<connector id=\"C_9\" graphicRef=\"Aggregation.svg\" />\n	<connector id=\"C_10\" graphicRef=\"Aggregation.svg\" />\n</language>";
		//fai(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)), new FileInputStream(new File(args[1])));
		System.out.println(run(new FileInputStream(new File(args[0])), new FileInputStream(new File(args[1]))).description);
		//Result r=(run(new FileInputStream(new File(args[0])), new FileInputStream(new File(args[1]))));
		//System.out.println(r.description);
		
	}
//	/**
//	 * @param args
//	 * @throws ScriptException 
//	 * @throws CloneNotSupportedException 
//	 * @throws FileNotFoundException 
//	 */
//	public static void main(String[] args) throws ScriptException, CloneNotSupportedException, FileNotFoundException {
//		// TODO Auto-generated method stub
//		/*File file = new File("test2.txt");  
//		FileOutputStream fis = new FileOutputStream(file);  
//		PrintStream out = new PrintStream(fis);  
//		System.setOut(out);*/
//		ParserXML parser = new ParserXML(args[0]);
//		ParserXMLDefinition parser2 = new ParserXMLDefinition(args[1]);
//		
//		ArrayList<ConnectorDefinition> c =parser2.getConnectors();
//		ArrayList<SymbolDefinition> s= parser2.getSymbols();
//		System.out.println(parser2.toString());
//		ArrayList<Symbol> symbols =parser.getSymbols();
//		ArrayList<Connector> connectors =parser.getConnectors();
//		GraphBuilder gr = new GraphBuilder(symbols,connectors);
//		gr.buildGraph();
//		
//		SimpleGraph<Node,GraphEdge> graph = gr.getGraph();
//		GraphDisambiguation gd = new GraphDisambiguation(graph, s, c);
//		gd.createSymMap();
//		gd.createConnMap();
//		gd.createConnMap2();
//		gd.createSymMap2();
//		System.out.println("\nInfo Simboli\n");
//		gd.buildSymbolsInfoMap();
//		System.out.println("\nInfo connettori \n");
//		gd.buildConnectorsInfoMap();
//		
//		//System.out.println("\n Inserimento nomi nodi \n");
//		gd.setNodeNames();
//		gd.setEdgeTypes();
//		
//		System.out.println("\n Grafo di partenza \n");
//		System.out.println(gd.toString());
//		//gd.removeAmbiguity2();
//		gd.removeAmbiguity3();
//		System.out.println("\n Ambiguità rimosse \n");
//		System.out.println(gd.toString());
//		System.out.println("\n");
//		gd.getProblems();
//		//gd.show();
//	}

}
