/*******************************************************************************
 * Copyright (c) 2015 Alfonso Ferraioli.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Mozilla Public License, v. 2.0
 * which accompanies this distribution, and is available at
 * http://mozilla.org/MPL/2.0/
 ******************************************************************************/

package it.unisa.di.weblab.localcontext.interactive;

/**
 * @author Alfonso Ferraioli
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

import it.unisa.di.weblab.localcontext.Connector;
import it.unisa.di.weblab.localcontext.ConnectorDefinition;
import it.unisa.di.weblab.localcontext.GraphBuilder;
import it.unisa.di.weblab.localcontext.GraphDisambiguation;
import it.unisa.di.weblab.localcontext.GraphEdge;
import it.unisa.di.weblab.localcontext.Node;
import it.unisa.di.weblab.localcontext.ParserXML;
import it.unisa.di.weblab.localcontext.ParserXMLDefinition;
import it.unisa.di.weblab.localcontext.Symbol;
import it.unisa.di.weblab.localcontext.SymbolDefinition;

public class TesterInteractive {
	
	private static GraphDisambiguationInteractive gd = null;
	private static ParserXMLDefinition parser2 = null;
	private static ArrayList<ConnectorDefinition> c = null;
	private static ArrayList<SymbolDefinition> s = null;
	private static ArrayList<Node> listNodeCheck = null;
	private static String constraint = null;
	private static HashMap<String, ArrayList<String>> resultSelfCheck = null;
	private static ArrayList<String> resultGlobalCheck = null;

	public static void parserXMLDefinition(InputStream definition) throws CloneNotSupportedException, ScriptException {
		
		parser2 = new ParserXMLDefinition(definition);
		c =parser2.getConnectors();
		s= parser2.getSymbols();
		constraint=parser2.getGlobalConstraints();
		gd = new GraphDisambiguationInteractive(s,c);
		gd.createSymMap();
		gd.createConnMap();
		gd.createConnMap2();
		gd.createSymMap2();
		gd.buildSymbolsInfoMap();
		gd.buildConnectorsInfoMap();
		gd.createSymbolOcc();
		listNodeCheck =  new ArrayList<Node>();
		resultSelfCheck = gd.selfCheck(listNodeCheck);
		resultGlobalCheck = gd.globalCheck(constraint);
	}
	
	public static void addConnector(String id,String graphicRef) {
		
		listNodeCheck = gd.addConnector(id,graphicRef);
		resultSelfCheck = gd.selfCheck(listNodeCheck);
		resultGlobalCheck = gd.globalCheck(constraint);
		
	}
	
	public static void removeConnector(String id) {
		
		listNodeCheck = gd.removeConnector(id);
		resultSelfCheck = gd.selfCheck(listNodeCheck);
		resultGlobalCheck = gd.globalCheck(constraint);
	}
	
	public static void addSymbol(String id,String graphicRef) {
		
		listNodeCheck = gd.addSymbol(id,graphicRef);
		resultSelfCheck = gd.selfCheck(listNodeCheck);
		resultGlobalCheck = gd.globalCheck(constraint);
	}
	
	public static void removeSymbol(String id) {
		
		listNodeCheck = gd.removeSymbol(id);
		resultSelfCheck = gd.selfCheck(listNodeCheck);
		resultGlobalCheck = gd.globalCheck(constraint);
	}
	
	public static void addConnection(String idS,String idC,String graphicRef,String connRef) {
		
		listNodeCheck = gd.addConnection(idS,idC,graphicRef,connRef);
		resultSelfCheck = gd.selfCheck(listNodeCheck);
		resultGlobalCheck = gd.globalCheck(constraint);
	}
	
	public static void removeConnection(String idS,String idC,String graphicRef,String connRef) {
		
		listNodeCheck = gd.removeConnection(idS,idC,graphicRef,connRef);
		resultSelfCheck = gd.selfCheck(listNodeCheck);
		resultGlobalCheck = gd.globalCheck(constraint);
		
	}
	
	public static HashMap<String, ArrayList<String>> resultSelfCheck(){
		return resultSelfCheck;
	}
	
	public static ArrayList<String> resultGlobalCheck(){
		return resultGlobalCheck;
	}
	
	public static String test(GraphDisambiguation gdt){
		
		Testing test = new Testing(gdt,gd);
		boolean result = test.error();
		String ret = "";
		
		if (result){
			ret = " ERRORE: SONO DIVERSI ";
			System.out.println(ret);
		} else {
			ret = " SONO UGUALI ";
			System.out.println(ret);
		}
		

		return ret;
	}
	
	
	public static void toStringGraphBuilderInteractive() {
//		System.out.println(" -------------------   Info Simboli   ---------------------");
//		gd.toStringInfoSymbols();
//		System.out.println(" -------------------  Info Connettori  ---------------------");
//		gd.toStringInfoConnectors();
		System.out.println(" -------------------   Inizio Grafo   ---------------------");
		System.out.println(gd);
//		System.out.println(" -------------------   Stampa Lista   ---------------------");
//		gr.toStringListNode();
//		System.out.println(" -------------------   Fine   Lista   ---------------------");
		System.out.println(" -------------------   Fine   Grafo   ---------------------");
		System.out.println(" -------------------  selfCheck Nodi  ---------------------");
		System.out.println(listNodeCheck);
		System.out.println(" -------------------  selfCheck Risultato  ---------------------");
		System.out.println(resultSelfCheck);
		System.out.println(" -------------------     selfCheck    ---------------------");
		System.out.println(" -------------------    globalCheck   ---------------------");
		System.out.println(resultGlobalCheck);
		System.out.println(" -------------------    globalCheck   ---------------------");
		
	}
		

}
