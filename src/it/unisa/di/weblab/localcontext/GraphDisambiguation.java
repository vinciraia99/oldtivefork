/*******************************************************************************
 * Copyright (c) 2015 Luca Laurino.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Mozilla Public License, v. 2.0
 * which accompanies this distribution, and is available at
 * http://mozilla.org/MPL/2.0/
 ******************************************************************************/

package it.unisa.di.weblab.localcontext;
/*La classe provvede alla rimozione delle ambiguità presenti negli archi e nei nodi del grafo costruito dall'oggetto GraphBuilder
 * 
 *Variabili d'istanza
 *
 * graph - Grafo proveniente dall'oggetto GraphBuilder
 * symDef - Insieme dei simboli provenienti dall'eleborazione del file XML di definizione avvenuta mediante l'oggetto ParserXMLDefinition
 * connDef - Insieme dei connettori provenienti dall'eleborazione del file XML di definizione avvenuta mediante l'oggetto ParserXMLDefinition
 * symMap - Hashmap avente come chiave il nome della rappresentazione grafica e come valore l'insieme dei simboli che condividono la stessa rappresentazione
 * connMap - Hashmap avente come chiave il nome della rappresentazione grafica e come valore l'insieme dei connettori che condividono la stessa rappresentazione
 * infoSymbols - Hashmap avente informazioni generali sui simboli (Chiave = nome rappresentazione grafica Valore= oggetto Information)
 * infoConnectors - Hashmap avente informazioni generali sui connettori (Chiave = nome rappresentazione grafica Valore= oggetto Information)
 * 
 * */


/**
 * @author Luca Laurino
 * @version $Id$
 */


import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.JFrame;

import org.jgraph.JGraph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.ListenableDirectedGraph;
import org.jgrapht.graph.SimpleGraph;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxFastOrganicLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.layout.mxParallelEdgeLayout;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxMorphing;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxGraph;


public class GraphDisambiguation {

	protected static HashMap<SymbolDefinition, Integer> symbolOcc=new HashMap<SymbolDefinition, Integer>();
	
	
	public GraphDisambiguation(SimpleGraph<Node, GraphEdge> graph,
			ArrayList<SymbolDefinition> symDef,
			ArrayList<ConnectorDefinition> connDef) {
		super();
		this.graph = graph;
		this.symDef = symDef;
		this.connDef = connDef;
	}
	
	public GraphDisambiguation(
			ArrayList<SymbolDefinition> symDef,
			ArrayList<ConnectorDefinition> connDef) {
		super();
		this.graph = new SimpleGraph<Node,GraphEdge>(GraphEdge.class);
		this.symDef = symDef;
		this.connDef = connDef;
	}
	
	

	/*Il metodo va a popolare l'hashmap avente come chiave il nome della rappresentazione grafica
	 * e come valore l'insieme dei simboli che condividono la rappresentazione grafica*/

	public void createSymMap()
	{
		for(int i=0;i<symDef.size();i++)
		{
			SymbolDefinition s = symDef.get(i);
			
			//Se la rappresentazione grafica del simbolo non è presente si aggiunge una nuova entry
			
			if(symMap.get(s.getGraphicRepresentation())== null)
			{
				ArrayList<SymbolDefinition> ss = new ArrayList<SymbolDefinition>();
				ss.add(s);
				symMap.put(s.getGraphicRepresentation(),ss);
			}
			
			//Altrimenti si aggiorna il valore della entry avente come chiave la rappresentazione 
			else
			{
				ArrayList<SymbolDefinition> ss = symMap.get(s.getGraphicRepresentation());
				ss.add(s);
				symMap.put(s.getGraphicRepresentation(), ss);
			}
		}
		/*for (Map.Entry<String, ArrayList<SymbolDefinition>> entry : symMap.entrySet()) {
		    // Stampo le coppie chiave-valore
		    System.out.println("Key = " + entry.getKey());
		    System.out.println("Value = " + entry.getValue().toString());
		  }*/
	}
	
	
	/*Il metodo va a popolare l'hashmap avente come chiave il nome della rappresentazione grafica
	 * e come valore l'insieme dei connettori che condividono la rappresentazione grafica*/
	
	public void createConnMap()
	{
		for(int i=0;i<connDef.size();i++)
		{
			ConnectorDefinition c = connDef.get(i);
			
			//Se la rappresentazione grafica del connettore non è presente si aggiunge una nuova entry
			
			if(connMap.get(c.getGraphicRepresentation())== null)
			{
				ArrayList<ConnectorDefinition> ss = new ArrayList<ConnectorDefinition>();
				ss.add(c);
				connMap.put(c.getGraphicRepresentation(),ss);
			}
			
			//Altrimenti si aggiorna il valore della entry avente come chiave la rappresentazione
			else
			{
				ArrayList<ConnectorDefinition> ss = connMap.get(c.getGraphicRepresentation());
				ss.add(c);
				connMap.put(c.getGraphicRepresentation(), ss);
			}
		}
		/*for (Map.Entry<String, ArrayList<ConnectorDefinition>> entry : connMap.entrySet()) {
		    // Stampo le coppie chiave-valore
		    System.out.println("Key = " + entry.getKey());
		    System.out.println("Value = " + entry.getValue().toString());
		  }*/
	}
	
	/*Hashmap chiave nome valore connectorDefinition*/
	
	public void createConnMap2(){
		for(int i=0;i<connDef.size();i++)
		{
			ConnectorDefinition c = connDef.get(i);
			connMapN.put(c.getName(), c);
		}
		
	}
	
	/*Hashmap chiave nome valore SymbolDefinition*/
	
	public void createSymMap2(){
		for(int i=0;i<symDef.size();i++)
		{
			SymbolDefinition c = symDef.get(i);
			symMapN.put(c.getName(), c);
		}
		
	}
	
	public SimpleGraph<Node, GraphEdge> getGraph() {
		return graph;
	}
	public void setGraph(SimpleGraph<Node, GraphEdge> graph) {
		this.graph = graph;
	}
	public ArrayList<SymbolDefinition> getSymDef() {
		return symDef;
	}
	public void setSymDef(ArrayList<SymbolDefinition> symDef) {
		this.symDef = symDef;
	}
	public ArrayList<ConnectorDefinition> getConnDef() {
		return connDef;
	}
	public void setConnDef(ArrayList<ConnectorDefinition> connDef) {
		this.connDef = connDef;
	}


	/*Il metodo va a costruire l'hashmap generale per i simboli con Chiave = rappresentazione grafica Valore = oggetto Information*/
	
	
	public void buildSymbolsInfoMap()
	{
		
		Set<String> graphicsRepresentation = symMap.keySet(); //Insieme dei riferimenti grafici dei simboli
		Iterator<String> it = graphicsRepresentation.iterator();
		
		/*Per ogni possibile rappresentazione grafica dei simboli determiniamo i possibili nomi dei simboli
		 * ed i tipi degli attacching points
		 */
		
		while(it.hasNext())
		{
			String representation = it.next();
			//Ottenimento nomi dei simboli avente la rappresentazione grafica in esame
			HashSet<String> names = getSymbolNames(representation);
			
			//Ottenimento di tutti i possibili tipi per i diversi attacching points della rappresentazione grafica in esame
			HashMap<String,HashSet<String>> refTypes = getSymbolRefTypes(representation);
			
			
			infoSymbols.put(representation, new Information(names,refTypes));
		}	
		System.out.println(infoSymbols.toString());
	}
	
	/*Il metodo va a costruire l'hashmap generale per i simboli con Chiave = rappresentazione grafica Valore = oggetto Information*/
	
	public void buildConnectorsInfoMap()
	{
		Set<String> graphicsRepresentation = connMap.keySet();
		Iterator<String> it = graphicsRepresentation.iterator();
		
		/*Per ogni possibile rappresentazione grafica dei connettori determiniamo i possibili nomi dei simboli
		 * ed i tipi degli attacching points
		 */
		
		while(it.hasNext())
		{
			String representation = it.next();
			//Ottenimento nomi dei simboli avente la rappresentazione grafica in esame
			HashSet<String> names = getConnectorNames(representation);
			
			//Ottenimento di tutti i possibili tipi per i diversi attacching points della rappresentazione grafica in esame
			HashMap<String,HashSet<String>> refTypes = getConnectorRefTypes(representation);
			infoConnectors.put(representation, new Information(names,refTypes));
		}	
		System.out.println(infoConnectors.toString());
	}
	
	
	/*Il metodo restituisce l'insieme dei tipi dei possibili attacching point del connettore avente la rappresentazione grafica fornita come parametro*/
	
	private HashMap<String, HashSet<String>> getConnectorRefTypes(
			String representation) {
		// TODO Auto-generated method stub
		ArrayList<ConnectorDefinition> ss = connMap.get(representation);
		HashMap<String, HashSet<String>> refTypes = new HashMap<String, HashSet<String>>();
		
		for(int i=0;i<ss.size();i++)
		{
			ArrayList<AttacchingPoint> aps = ss.get(i).getAttacchingPoints();
			for(int j=0;j<aps.size();j++)
			{
				HashSet<String> types = new HashSet<String>();
				// Se l'attaching point non è presente nell'hashmap si aggiunge
				if(refTypes.get(aps.get(j).getGraphicRef())== null)
				{	
					types.add(aps.get(j).getType());
					refTypes.put(aps.get(j).getGraphicRef(), types);
				}
				//Altrimenti si aggiunge il tipo corrispondente nell'insieme dei tipi già presenti per l'attacching point
				else
				{
					HashSet<String> types2= refTypes.get(aps.get(j).getGraphicRef());
					types2.add(aps.get(j).getType());
					refTypes.put(aps.get(j).getGraphicRef(), types2);
				}	
			}	
		}	
		return refTypes;
	}


	/*Il metodo restituisce l'insieme dei nomi dei connettori che condividono la rappresentazione grafica fornita come parametro*/

	private HashSet<String> getConnectorNames(String representation) {
		// TODO Auto-generated method stub
		HashSet<String> names = new HashSet<String>();
		ArrayList<ConnectorDefinition> conns = connMap.get(representation);
		for(int i=0;i<conns.size();i++)
		{
			names.add(conns.get(i).getName());
		}	
		return names;
	}


	/*Il metodo restituisce l'insieme dei tipi dei possibili attacching point dei simboli aventi la rappresentazione grafica fornita come parametro*/

	private HashMap<String, HashSet<String>> getSymbolRefTypes(
			String representation) {
		// TODO Auto-generated method stub
		ArrayList<SymbolDefinition> ss = symMap.get(representation);
		HashMap<String, HashSet<String>> refTypes = new HashMap<String, HashSet<String>>();
		
		for(int i=0;i<ss.size();i++)
		{
			ArrayList<AttacchingPoint> aps = ss.get(i).getAttacchingPoints();
			for(int j=0;j<aps.size();j++)
			{
				HashSet<String> types = new HashSet<String>();
				// Se l'attaching point non è presente nell'hashmap si aggiunge
				if(refTypes.get(aps.get(j).getGraphicRef())== null)
				{	
					types.add(aps.get(j).getType());
					refTypes.put(aps.get(j).getGraphicRef(), types);
				}
				//Altrimenti si aggiunge il tipo corrispondente nell'insieme dei tipi già presenti per l'attacching point
				else
				{
					HashSet<String> types2= refTypes.get(aps.get(j).getGraphicRef());
					types2.add(aps.get(j).getType());
					refTypes.put(aps.get(j).getGraphicRef(), types2);
				}	
			}	
		}	
		return refTypes;
	}


	/*Il metodo restituisce l'insieme dei nomi dei simboli che condividono la rappresentazione grafica fornita come parametro*/

	private HashSet<String> getSymbolNames(String representation) {
		
		// TODO Auto-generated method stub
		HashSet<String> names = new HashSet<String>();
		ArrayList<SymbolDefinition> syms = symMap.get(representation);
		for(int i=0;i<syms.size();i++)
		{
			names.add(syms.get(i).getName());
		}	
		return names;
	}

	// Il metodo permette di settare ogni nodo del grafo con i possibili nomi effettivi
	
	public void setNodeNames()
	{
		Iterator<Node> vertex = graph.vertexSet().iterator();
		while(vertex.hasNext())
		{
			Node v = vertex.next();
			if(v.isConnector())
				v.setRealGraphicType(new HashSet<String>(infoConnectors.get(v.getGraphicType()).getNames()));
			else
				v.setRealGraphicType(new HashSet<String>(infoSymbols.get(v.getGraphicType()).getNames()));
		}	
		
	}
	
	/*Il metodo va a settare i possibili tipi di ogni arco del grafo*/
	
	public void setEdgeTypes()
	{
		Iterator<GraphEdge> edges = graph.edgeSet().iterator();
		while(edges.hasNext())
		{
			GraphEdge edg = edges.next();
			Node v1 = edg.getSource(); //Simbolo
			Node v2 = edg.getTarget(); //Connettore
			ArrayList<AttacchingPointCouple> app = edg.getAttacchings();
			Information inf1 = infoSymbols.get(v1.getGraphicType());
			Information inf2 = infoConnectors.get(v2.getGraphicType());
			HashSet<String> types1 = new HashSet<String>();
			HashSet<String> t1,t2;
			HashSet<String> types2 = new HashSet<String>();
			for(int i=0;i<app.size();i++)
			{
				//Recuperiamo i tipi associati all'attacching point del simbolo utilizzato nella connessione simbolo/connettore
				types1 =inf1.getRefTypes().get(app.get(i).getAp1()); 
				if(types1!=null){t1=new HashSet<String>(types1);
				//System.out.println("Tipi simbolo: \n"+types1.toString());
				
				//Recuperiamo i tipi associati all'attacching point del simbolo utilizzato nella connessione simbolo/connettore
				types2 =inf2.getRefTypes().get(app.get(i).getAp2());
				t2=new HashSet<String>(types2);	
				//System.out.println("Tipi connettore: \n"+types2.toString());
				
				//I possibili tipi sull'arco risultano essere l'intersezione dei set dei tipi del simbolo e del connettore
				t1.retainAll(t2);
				//System.out.println("Tipi intersezione: \n"+t1.toString());
				app.get(i).setTypes(t1);}
			}	
			
		}	
		//System.out.println(graph.toString());
	}
	
	
	

	public String toString()
	{
		String content="";
		Set<Node> vertex = graph.vertexSet();
		Iterator<Node> it = vertex.iterator();
		
		while(it.hasNext())
		{
			Node n = it.next();
			content=content+"Vertex id: "+n.getId()+" Graphic Type: "+n.getGraphicType()+" Names: "+n.getRealGraphicType().toString()+"\n";
		}
		
		content = content+"\n Archi \n";
		Set<GraphEdge> edges = graph.edgeSet();
		Iterator<GraphEdge> it2 = edges.iterator();
		
		while(it2.hasNext())
		{
			GraphEdge edg = it2.next();
			content = content +edg.getSource().getId()+"-"+edg.getTarget().getId()+"\n";
			for(int i=0;i<edg.getAttacchings().size();i++)
			{
				content=content+edg.getAttacchings().get(i).getAp1()+"-"+edg.getAttacchings().get(i).getAp2()+"\n";
				content = content+edg.getAttacchings().get(i).getTypes().toString()+"\n\n";
			}	
		}	
		
		return content;
	}
	
	
	public void removeAmbiguity3() throws CloneNotSupportedException, ScriptException{
		for(String symName:symMapN.keySet()){
			
			SymbolDefinition s = symMapN.get(symName);
			symbolOcc.put(s, 0);
		}
		Iterator<Node> vertex = graph.vertexSet().iterator();
		System.out.println("Rimuovo ambiguità archi");
		//Fase di rimozione delle ambiguità presenti negli archi
		while(vertex.hasNext())
		{
			Node v = vertex.next();
			
			
				removeEdgeAmbiguity2(v);
			
		}	
		vertex = graph.vertexSet().iterator();
		
		ErrorMessages msgError = new ErrorMessages();
		
		while(vertex.hasNext())
		{
			Node v = vertex.next();
			System.out.println("Id: "+ v.getId()+"\nRappresentazione: "+v.getGraphicType()+"\n Possibili tipi: "+v.getRealGraphicType()+"\n");
			if(v.isConnector())
			{
				
				
				HashMap<String,ArrayList<String>> result = new HashMap<String,ArrayList<String>>();
				
				Iterator<String> names = new HashSet<String>(v.getRealGraphicType()).iterator();
				while(names.hasNext())
				{
					String name = names.next();
					ConnectorDefinition c = connMapN.get(name);
					//Genero le combinazioni
					ArrayList<ArrayList<String>> combinations = getCombinations(v,name);
					System.out.println("\nPer il connettore "+name+" le possibili combinazioni sono: \n"+combinations.toString()+"\n"+"Attacching points:\n"+c.getAttacchingPoints().toString()+"\n");
					ArrayList<Boolean> resultFromCombination = new ArrayList<Boolean>();
					if(combinations.size()==0)
					{
						HashMap<Couple,Integer> typeOccurrences = getNodeConnectorTypeValues2(v);
						
						//Verifica se tutti i vincoli sono rispettati per il connettore passato come parametro
						boolean resultCombination = checkConnectorConstraint2(c,typeOccurrences);
						resultFromCombination.add(resultCombination);
					}	
					for(ArrayList<String> comb:combinations)
					{
						//getEdgesTempType(v);
						//Se c'è effettuo assegno i tipi della combinazione agli archi del nodo
						setEdgeTempType(v,name,comb);
						//getEdgesTempType(v);
						HashMap<Couple,Integer> typeOccurrences = getNodeConnectorTypeValues2(v);
						
						//Verifica se tutti i vincoli sono rispettati per il connettore passato come parametro
						boolean resultCombination = checkConnectorConstraint2(c,typeOccurrences);
						resultFromCombination.add(resultCombination);
					}	
					if(Collections.frequency(resultFromCombination, true)==1)
					{
						if(combinations.size()>=1)
							result.put(name,combinations.get(resultFromCombination.indexOf(true)));
						else
							result.put(name, new ArrayList<String>());
					}
					else if(Collections.frequency(resultFromCombination, true)==0)
					{
						v.getRealGraphicType().remove(name);
					}	
				}	
				if(result.size()==1)
				{
					Map.Entry<String, ArrayList<String>> entry = result.entrySet().iterator().next();
					setEdgeTempType(v,entry.getKey(),entry.getValue());
					setEdgeRealType(v);
					HashSet<String> nodeRealName = new HashSet<String>();
					nodeRealName.add(entry.getKey());
					v.setRealGraphicType(nodeRealName);
				}
				else if(result.size()>1)
				{
					if(!problems.containsKey(v.getId()))
					{
						ArrayList<String> messages = new ArrayList<String>();
						//messages.add("Ambiguity still present because more than one connector's type satisfy present constraints");
						messages.add(msgError.getAmbiguousError(true));						
						problems.put(v.getId(),messages);
					}
					else
					{
						ArrayList<String> messages = problems.get(v.getId());
						//messages.add("Ambiguity still present because more than one connector's type satisfy present constraints");
						messages.add(msgError.getAmbiguousError(true));
						problems.put(v.getId(),messages);
					}	
				}
				else if(result.size()==0 && v.getRealGraphicType().size()>0)
				{
					if(!problems.containsKey(v.getId()))
					{
						ArrayList<String> messages = new ArrayList<String>();
						//messages.add("Ambiguity still present because the constraints are satisfied for the different types of the connector using more than one of the possible combinations of the types present on the edges");
						messages.add(msgError.getAmbiguousMoreCombinationsError(true));
						problems.put(v.getId(),messages);
					}
					else
					{
						ArrayList<String> messages = problems.get(v.getId());
						//messages.add("Ambiguity still present because the constraints are satisfied for the different types of the connector using more than one of the possible combinations of the types present on the edges");
						messages.add(msgError.getAmbiguousMoreCombinationsError(true));
						problems.put(v.getId(),messages);
					}
				}
				else
				{
					if(!problems.containsKey(v.getId()))
					{
						ArrayList<String> messages = new ArrayList<String>();
						//messages.add("Unable to disambiguate the node because they are not respected the constraints for any possible type of connector");
						messages.add(msgError.getConstraintsNotSatisfiedError(true));
						problems.put(v.getId(),messages);
					}
					else
					{
						ArrayList<String> messages = problems.get(v.getId());
						//messages.add("Unable to disambiguate the node because they are not respected the constraints for any possible type of connector");
						messages.add(msgError.getConstraintsNotSatisfiedError(true));
						problems.put(v.getId(),messages);
					}
				}
				 
			}
			else
			{
				
				//Gestire tutto per il simbolo invece
				HashMap<String,ArrayList<String>> result = new HashMap<String,ArrayList<String>>();
				 //Indice della combinazione che soddisferà i vincoli
				Iterator<String> names = new HashSet<String>(v.getRealGraphicType()).iterator();
			
				while(names.hasNext())
				{
					String name = names.next();
					SymbolDefinition s = symMapN.get(name);
					//Genero le combinazioni
					ArrayList<ArrayList<String>> combinations = getCombinations(v,name);
					System.out.println("Per il simbolo "+name+" le possibili combinazioni sono: \n"+combinations.toString()+"\n"+"Attacching points:\n"+s.getAttacchingPoints().toString()+"\n");
					ArrayList<Boolean> resultFromCombination = new ArrayList<Boolean>();
					if(combinations.size()==0)
					{
						HashMap<Couple,Integer> typeOccurrences = getNodeSymbolTypeValues2(v);
						HashMap<Loop,Integer> loopOccurrences = getNodeLoopValues2(v);
						boolean resultCombination = checkSymbolConstraint2(s,typeOccurrences,loopOccurrences);
						
						if(resultCombination){
							int numOcc=symbolOcc.get(s);
							symbolOcc.put(s, numOcc+1);
						}
						resultFromCombination.add(resultCombination);
					}	
					for(ArrayList<String> comb:combinations)
					{
						//getEdgesTempType(v);
						//assegno i tipi della combinazione agli archi del nodo
						setEdgeTempType(v,name,comb);
						//getEdgesTempType(v);
						HashMap<Couple,Integer> typeOccurrences = getNodeSymbolTypeValues2(v);
						HashMap<Loop,Integer> loopOccurrences = getNodeLoopValues2(v);
						boolean resultCombination = checkSymbolConstraint2(s,typeOccurrences,loopOccurrences);
						
						if(resultCombination){
							int numOcc=symbolOcc.get(s);
							symbolOcc.put(s, numOcc+1);
						}
						resultFromCombination.add(resultCombination);
					}
					if(Collections.frequency(resultFromCombination, true)==1)
					{
						if(combinations.size()>=1)
							result.put(name,combinations.get(resultFromCombination.indexOf(true)));
						else
							result.put(name, new ArrayList<String>());
					}
					else if(Collections.frequency(resultFromCombination, true)==0)
					{
						v.getRealGraphicType().remove(name);
					}
				}	
				if(result.size()==1)
				{
					Map.Entry<String, ArrayList<String>> entry = result.entrySet().iterator().next();
					setEdgeTempType(v,entry.getKey(),entry.getValue());
					setEdgeRealType(v);
					HashSet<String> nodeRealName = new HashSet<String>();
					nodeRealName.add(entry.getKey());
					v.setRealGraphicType(nodeRealName);
				}	
				else if(result.size()>1)
				{
					if(!problems.containsKey(v.getId()))
					{
						ArrayList<String> messages = new ArrayList<String>();
						//messages.add("Ambiguity still present because the constraints are satisfied for more than possible types of symbol");
						messages.add(msgError.getAmbiguousError(false));
						problems.put(v.getId(),messages);
					}
					else
					{
						ArrayList<String> messages = problems.get(v.getId());
						//messages.add("Ambiguity still present because the constraints are satisfied for more than possible types of symbol");
						messages.add(msgError.getAmbiguousError(false));
						problems.put(v.getId(),messages);
					}	
				}
				else if(result.size()==0 && v.getRealGraphicType().size()>0)
				{
					if(!problems.containsKey(v.getId()))
					{
						ArrayList<String> messages = new ArrayList<String>();
						//messages.add("Ambiguity still present because the constraints are satisfied for the different types of the symbol by using more than one of the possible combinations of the types present on the edges");
						messages.add(msgError.getAmbiguousMoreCombinationsError(false));
						problems.put(v.getId(),messages);
					}
					else
					{
						ArrayList<String> messages = problems.get(v.getId());
						//messages.add("Ambiguity still present because the constraints are satisfied for the different types of the symbol by using more than one of the possible combinations of the types present on the edges");
						messages.add(msgError.getAmbiguousMoreCombinationsError(false));
						problems.put(v.getId(),messages);
					}
				}
				else
				{
					if(!problems.containsKey(v.getId()))
					{
						ArrayList<String> messages = new ArrayList<String>();
						//messages.add("Unable to disambiguate the node because the constraints are not respected for any possible type of symbol");
						messages.add(msgError.getConstraintsNotSatisfiedError(false));
						problems.put(v.getId(),messages);
					}
					else
					{
						ArrayList<String> messages = problems.get(v.getId());
						//messages.add("Unable to disambiguate the node because the constraints are not respected for any possible type of symbol");
						messages.add(msgError.getConstraintsNotSatisfiedError(false));
						problems.put(v.getId(),messages);
					}
				}
				
		
				 
				 
			}	
		}
		for(String symName:symMapN.keySet()){
			
			SymbolDefinition s = symMapN.get(symName);
			String occ=(s.getOccurrences());
			if(occ.startsWith("==")){
				int n=Integer.parseInt(occ.split("==")[1]);
				if(!(symbolOcc.get(s)==n)){
					if(problems.containsKey("Contraints")){
						ArrayList<String> messages = problems.get("Contraints");
						//messages.add("The occurences of symbol "+s.getName()+" must be == "+n);
						messages.add(msgError.getOccurencesError(s.getName(),"==",n));
						problems.put("Contraints",messages);
					}
					else{
						ArrayList<String> messages = new ArrayList<String>();
						//messages.add("The occurences of symbol "+s.getName()+" must be == "+n);
						messages.add(msgError.getOccurencesError(s.getName(),"==",n));
						problems.put("Contraints",messages);
					}
					
					}
			}
				
			else if(occ.startsWith("<=")){
				int n=Integer.parseInt(occ.split("<=")[1]);
				if(!(symbolOcc.get(s)<=n)){
					if(problems.containsKey("Contraints")){
						ArrayList<String> messages = problems.get("Contraints");
						//messages.add("The occurences of symbol "+s.getName()+" must be <= "+n);
						messages.add(msgError.getOccurencesError(s.getName(),"<=",n));
						problems.put("Contraints",messages);
					}
					else{
						ArrayList<String> messages = new ArrayList<String>();
						//messages.add("The occurences of symbol "+s.getName()+" must be <= "+n);
						messages.add(msgError.getOccurencesError(s.getName(),"<=",n));
						problems.put("Contraints",messages);
					}
					
					}
			}
			else if(occ.startsWith(">=")){
				int n=Integer.parseInt(occ.split(">=")[1]);
				if(!(symbolOcc.get(s)>=n)){
					if(problems.containsKey("Contraints")){
						ArrayList<String> messages = problems.get("Contraints");
						//messages.add("The occurences of symbol "+s.getName()+" must be >= "+n);
						messages.add(msgError.getOccurencesError(s.getName(),">=",n));
						problems.put("Contraints",messages);
					}
					else{
						ArrayList<String> messages = new ArrayList<String>();						
						//messages.add("The occurences of symbol "+s.getName()+" must be >= "+n);
						messages.add(msgError.getOccurencesError(s.getName(),">=",n));
						problems.put("Contraints",messages);
					}
					
					}
			}
			else if(occ.startsWith("<")){
				int n=Integer.parseInt(occ.split("<")[1]);
				if(!(symbolOcc.get(s)<n)){
					if(problems.containsKey("Contraints")){
						ArrayList<String> messages = problems.get("Contraints");
						//messages.add("The occurences of symbol "+s.getName()+" must be < "+n);
						messages.add(msgError.getOccurencesError(s.getName(),"<",n));
						problems.put("Contraints",messages);
					}
					else{
						ArrayList<String> messages = new ArrayList<String>();
						//messages.add("The occurences of symbol "+s.getName()+" must be < "+n);
						messages.add(msgError.getOccurencesError(s.getName(),"<",n));
						problems.put("Contraints",messages);
					}
					
					}
			}
			else if(occ.startsWith(">")){
				int n=Integer.parseInt(occ.split(">")[1]);
				if(!(symbolOcc.get(s)>n)){
					if(problems.containsKey("Contraints")){
						ArrayList<String> messages = problems.get("Contraints");
						//messages.add("The occurences of symbol "+s.getName()+" must be > "+n);
						messages.add(msgError.getOccurencesError(s.getName(),">",n));
						problems.put("Contraints",messages);
					}
					else{
						ArrayList<String> messages = new ArrayList<String>();
						//messages.add("The occurences of symbol "+s.getName()+" must be > "+n);
						messages.add(msgError.getOccurencesError(s.getName(),">",n));
						problems.put("Contraints",messages);
					}
					
					}
			}
			
		}
	}
	
	
	
	
	protected void removeEdgeAmbiguity2(Node v) throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		if(v.isConnector())
		{
			ArrayList<ConnectorDefinition> connectors = connMap.get(v.getGraphicType());
			ArrayList<EntryListType> result = new ArrayList<EntryListType>();
			
			ArrayList<Boolean> results = new ArrayList<Boolean>();
			for(int y=0;y<connectors.size();y++)
			{	
				/*Inizializzazione liste*/
				
				ArrayList<EntryListType> list1 = new ArrayList<EntryListType>(); //lista contenente set di tipi con size == 1
				ArrayList<EntryListType> list2 = new ArrayList<EntryListType>(); //lista contenente set di tipi con size > 1
				
				Iterator<GraphEdge> edgesOfV = graph.edgesOf(v).iterator();
				while(edgesOfV.hasNext())
				{
					GraphEdge edg = edgesOfV.next();
					ArrayList<AttacchingPointCouple> aps = edg.getAttacchings();
					for(int i =0;i<aps.size();i++)
					{
						EntryListType e = new EntryListType(aps.get(i).getTypes(),aps.get(i).getAp2(),edg.getSource().getId(),edg.getTarget().getId(),aps.get(i).getAp1());
						if(aps.get(i).getTypes().size() == 1)
						{
							list1.add(e);
						}	
						else if(aps.get(i).getTypes().size() > 1)
								list2.add(e);
						
							
					}	
				}
					
					
				//System.out.println("Per il vertice avente rappresentazione "+v.getGraphicType()+"ed id: "+v.getId()+"\nLista 1: \n"+list1.toString());
				//System.out.println("Lista 2:\n"+list2.toString());
				ConnectorDefinition c = connectors.get(y);
				ArrayList<AttacchingPoint> apC = c.getAttacchingPoints();
				//Costruzione ExtendedAttacchingPoint
				ArrayList<ExtendedAttacchingPoint> apE = new ArrayList<ExtendedAttacchingPoint>();
				for(int i=0;i<apC.size();i++)
				{
					AttacchingPoint ap = apC.get(i);
					String type = ap.getType();
					String name = ap.getName();
					String graphicRef = ap.getGraphicRef();
					String connectNum = ap.getConnectNum();
					String opConnNum ="";
					int connNum=0;
					String numLoop = ap.getNumLoop();
					String opNumLoop ="";
					int nLoop=0;
					if(connectNum.contains("=="))
					{
						opConnNum="==";
						connNum=Integer.parseInt(connectNum.split("==")[1]);
					}	
					else if(connectNum.contains("<="))
					{
						opConnNum="<=";
						connNum=Integer.parseInt(connectNum.split("<=")[1]);
					}
					else if(connectNum.contains(">="))
					{
						opConnNum=">=";
						connNum=Integer.parseInt(connectNum.split(">=")[1]);
					}	
					else if(connectNum.contains("<")&& !(connectNum.contains("<=")))
					{
						//Trasformazione < in <=
						opConnNum="<=";
						int n =Integer.parseInt(connectNum.split("<")[1])-1;
						connNum=n;
					}
					else if(connectNum.contains(">")&& !(connectNum.contains(">=")))
					{
						//Trasformazion > in >=
						opConnNum=">=";
						int n =Integer.parseInt(connectNum.split(">")[1])+1;
						connNum=n;
					}
					
					if(numLoop.contains("=="))
					{
						opNumLoop="==";
						nLoop=Integer.parseInt(connectNum.split("==")[1]);
					}	
					else if(numLoop.contains("<="))
					{
						opNumLoop="<=";
						nLoop=Integer.parseInt(numLoop.split("<=")[1]);
					}
					else if(numLoop.contains(">="))
					{
						opNumLoop=">=";
						nLoop=Integer.parseInt(numLoop.split(">=")[1]);
					}	
					else if(numLoop.contains("<")&& !(numLoop.contains("<=")))
					{
						opNumLoop="<=";
						int n =Integer.parseInt(numLoop.split("<")[1])-1;
						nLoop=n;
					}
					else if(numLoop.contains(">")&& !(numLoop.contains(">=")))
					{
						opNumLoop=">=";
						int n =Integer.parseInt(numLoop.split(">")[1])+1;
						nLoop=n;
					}
					apE.add(new ExtendedAttacchingPoint(type, name, graphicRef, opConnNum, connNum, opNumLoop, nLoop));
				}	
				//System.out.println("Gli attacching point sono: \n"+apC.toString());
				//System.out.println("\n Quelli estesi sono:\n"+apE.toString());
				//System.out.println("La dimensione della lista degli attacching point è : "+apE.size());
				/*Controllo su lista con set di dimensione 1*/
				boolean errorSatisfy=false;
				for(int i=0;i<list1.size();i++)
				{
					ArrayList<ExtendedAttacchingPoint> copyAP = new ArrayList<ExtendedAttacchingPoint>(apE);
					/*Se sono stati rimossi tutti gli attacching points ma il for ancora non è terminato significa che vi sono più archi di quanti ne sono ammessi dai vincoli del connettore preso in esame*/
					if(apE.size()==0)
						errorSatisfy=true;
						
					String type = list1.get(i).getTypes().iterator().next();
					String graphicRef = list1.get(i).getcApRef();
					for(int j=0;j<copyAP.size();j++)
					{
						ExtendedAttacchingPoint e = copyAP.get(j);
						/*Se c'è corrispondenza andiamo a decrementare il valore di connectNum*/
						if((e.getGraphicRef().equals(graphicRef)&& e.getType().equals(type))/*||(e.getType().equals(type)&& e.getGraphicRef().startsWith("P_")&& graphicRef.equals("P"))*/)
						{
							int n = e.getConnectNum()-1;
							e.setConnectNum(n);
							//Eliminiamo gli attacching points che soddisfano le seguenti condizioni
							if((e.getOpConnectNum().equals("==")&&e.getConnectNum()==0))
								apE.remove(e);
							if(e.getOpConnectNum().equals("<=") && e.getConnectNum()==0)
								apE.remove(e);
						}	
					}
					
				}	
				//System.out.println("La dimensione attuale della lista degli ap è "+apE.size());
				
				//System.out.println("Dimensione list1: "+list1.size());
				//System.out.println("Dimensione list2: "+list2.size());
				/*Controllo su lista con dimensione > 1*/
				int counter =0; //Al fine di evitare un numero infinito di iterazioni introduciamo la variabile counter che terrà conto del numero di volte in cui non può essere eliminata un'ambiguità
				while(counter<(graph.edgesOf(v).size()-list1.size()))
				{
					ArrayList<EntryListType> copyList = cloneList(list2);
					//ArrayList<EntryListType> copy2 = new ArrayList<EntryListType>(list2);
					for(int i=0;i<copyList.size();i++)
					{
						if(apE.size()==0)
						{	
							errorSatisfy=true;
						
						}	
						EntryListType el = copyList.get(i);
						Iterator<String> types = el.getTypes().iterator();
						String graphicRef = el.getcApRef();
						int index=0;
						int matches=0;
						while(types.hasNext())
						{
							String t = types.next();
							boolean found = false;
							for(int j=0;j<apE.size();j++)
							{
								ExtendedAttacchingPoint e = apE.get(j);
								if((e.getGraphicRef().equals(graphicRef)&& e.getType().equals(t))/*||(e.getType().equals(t)&& e.getGraphicRef().startsWith("P_")&& graphicRef.equals("P"))*/)
								{
									found=true;
									index = j;
									matches++;
								}	
							}
							if(!found)
							{	
								HashSet<String> newTypes = new HashSet<String>(el.getTypes());
								//System.out.println("Pre modifica \n"+newTypes.toString());
								newTypes.remove(t);
								//System.out.println("Post modifica \n"+newTypes.toString());
								el.setTypes(newTypes);
							}	
						}
						//caso di errore in cui al connettore è collegato qualcosa che non può essere connesso al connettore
						if(el.getTypes().size() == 0)
							errorSatisfy=true;
						if(matches==1)
						{
							ExtendedAttacchingPoint e1 = apE.get(index);
							e1.setConnectNum(e1.getConnectNum()-1);
							if(e1.getOpConnectNum().equals("==")&&e1.getConnectNum()==0)
								apE.remove(e1);
							if(e1.getOpConnectNum().equals("<=")&& e1.getConnectNum()==0)
								apE.remove(e1);
						}	
						if(el.getTypes().size()==1)
						{	
							counter = 0;
							for(int j=0;j<list2.size();j++)
							{
								EntryListType et = list2.get(j);
								if(et.getcApRef().equals(el.getcApRef())&& et.getsApRef().equals(el.getsApRef())&&et.getcId().equals(el.getcId())&&et.getsId().equals(el.getsId()))
									list2.remove(et);
							}	
							//list2.remove(el);
							list1.add(el);
						}
						else
							counter++;
					}
					
					
					if((apE.size()==0 && list2.size()==0) || (apE.size()>0 && list2.size()==0))
						break;
					
				}
				/*Se sono stati eliminati tutti gli attacching point del connettore*/
				if(apE.size()==0)
				{
					if(!errorSatisfy)
					{	
						results.add(true);
						result = cloneList(list1);
						/*Aggiunta*/
						HashMap<String,HashSet<String>> combIn = new HashMap<String,HashSet<String>>();
						for(int i=0;i<list1.size();i++)
						{
							EntryListType el = list1.get(i);
							String edgeString=el.getsId()+"-"+el.getcId()+":"+el.getsApRef()+"-"+el.getcApRef();
							combIn.put(edgeString, el.getTypes());
						}	
						for(int i=0;i<list2.size();i++)
						{
							EntryListType el = list2.get(i);
							String edgeString=el.getsId()+"-"+el.getcId()+":"+el.getsApRef()+"-"+el.getcApRef();
							combIn.put(edgeString, el.getTypes());
						}
						v.getCombinationsInfo().put(connectors.get(y).getName(), combIn);
						
						//
					}	
					else
					{	
						results.add(false);
						v.getRealGraphicType().remove(connectors.get(y).getName());
					
					}
				}
				/*Altrimenti controlliamo se quelli restanti hanno operatore >= e valore <=0*/
				else
				{
					if(!errorSatisfy)
					{	
						boolean satisfy = true;
						for(int i=0;i<apE.size();i++)
						{
							if((apE.get(i).getOpConnectNum().equals(">=") && (apE.get(i).getConnectNum()>0))||(apE.get(i).getOpConnectNum().equals("<=") && (!(apE.get(i).getConnectNum()>0)))||apE.get(i).getOpConnectNum().equals("=="))
								satisfy=false;
							/*if((!apE.get(i).getOpConnectNum().equals(">=")) || (apE.get(i).getConnectNum()>0))
								satisfy=false;*/
						}
						if(satisfy)
						{	
							results.add(true);
							result = cloneList(list1);
							/*Aggiunta*/
							HashMap<String,HashSet<String>> combIn = new HashMap<String,HashSet<String>>();
							for(int i=0;i<list1.size();i++)
							{
								EntryListType el = list1.get(i);
								String edgeString=el.getsId()+"-"+el.getcId()+":"+el.getsApRef()+"-"+el.getcApRef();
								combIn.put(edgeString, el.getTypes());
							}	
							for(int i=0;i<list2.size();i++)
							{
								EntryListType el = list2.get(i);
								String edgeString=el.getsId()+"-"+el.getcId()+":"+el.getsApRef()+"-"+el.getcApRef();
								combIn.put(edgeString, el.getTypes());
							}
							v.getCombinationsInfo().put(connectors.get(y).getName(), combIn);
							
							//
						}	
						else
						{	
							results.add(false);
							v.getRealGraphicType().remove(connectors.get(y).getName());
							//System.out.println("Tipi restanti : "+v.getRealGraphicType().toString() );
							//System.out.println("Tipi originali : "+getConnectorNames(v.getGraphicType()).toString() );
						}
					}
					else
					{	
						results.add(false);
						v.getRealGraphicType().remove(connectors.get(y).getName());
					
					}
						
				}
				System.out.println("\nAl termine del controllo per il vertice avente id: "+v.getId()+ " |rappresentazione grafica: "+v.getGraphicType()+ " | nome:  "+c.getName());
				System.out.println(list1.toString());
				System.out.println(list2.toString()+"\n");
				
			}	
			
			/*Se per un solo connettore sono state rimosse tutte le ambiguità sui tipi*/
			if(Collections.frequency(results,true)==1)
			{	
				Iterator<GraphEdge>edgesOfV = graph.edgesOf(v).iterator();
				
				// Andiamo a settare i tipi degli archi del vertice che sono stati disambiguati
				
				
				while(edgesOfV.hasNext())
				{
					GraphEdge edg = edgesOfV.next();
					ArrayList<AttacchingPointCouple> aps =edg.getAttacchings();
					
					if(aps.size()==1)
					{
						for(int i=0;i<result.size();i++)
						{
							EntryListType el = result.get(i);
							
							if(edg.getSource().getId().equals(el.getsId())&&edg.getTarget().getId().equals(el.getcId()))
								aps.get(0).setTypes(el.getTypes());
						}
					}
					else
					{
						ArrayList<EntryListType> edgeLoopComponents = new ArrayList<EntryListType>();
						for(int i=0;i<result.size();i++)
						{
							EntryListType el = result.get(i);
							
							if(edg.getSource().getId().equals(el.getsId())&&edg.getTarget().getId().equals(el.getcId()))
								edgeLoopComponents.add(el);
						}
						
						for(int j=0;j<aps.size();j++)
						{
							AttacchingPointCouple ap = aps.get(j);
							for(int k=0;k<edgeLoopComponents.size();k++)
							{
								EntryListType e = edgeLoopComponents.get(k);
								if(e.getcApRef().equals(ap.getAp2())&&e.getsApRef().equals(ap.getAp1()))
								{
									
										aps.get(j).setTypes(e.getTypes());
								}	
							}	
						}	
					}	
				}
			}
			else if(Collections.frequency(results,true)>1 && Collections.frequency(results,true) < connectors.size())
			{
				System.out.println("TO DO");
				/*
				if(Collections.frequency(results,false) == 1)
				{
					connectors.remove(connectors.indexOf(false));
					
				}
				else
				{
					int falses = Collections.frequency(results,false);
					for(int k =0;k<falses;k++)
						connectors.remove(results.indexOf(false));
				}	
				System.out.println(connectors.toString());*/
			}	
		}
		else
		{
			
			ArrayList<SymbolDefinition> symbols = symMap.get(v.getGraphicType());
			ArrayList<EntryListType> result = new ArrayList<EntryListType>();
			
			ArrayList<Boolean> results = new ArrayList<Boolean>();
			for(int y=0;y<symbols.size();y++)
			{	
				
				/*Inizializzazione liste*/
				ArrayList<EntryListType> list1 = new ArrayList<EntryListType>(); //lista contenente set di tipi con size == 1
				ArrayList<EntryListType> list2 = new ArrayList<EntryListType>(); //lista contenente set di tipi con size > 1
				
				Iterator<GraphEdge> edgesOfV = graph.edgesOf(v).iterator();
				
				while(edgesOfV.hasNext())
				{
					GraphEdge edg = edgesOfV.next();
					ArrayList<AttacchingPointCouple> aps = edg.getAttacchings();
					for(int i =0;i<aps.size();i++)
					{
						EntryListType e = new EntryListType(aps.get(i).getTypes(),aps.get(i).getAp2(),edg.getSource().getId(),edg.getTarget().getId(),aps.get(i).getAp1());
						if(aps.get(i).getTypes().size() == 1)
						{
							list1.add(e);
						}	
						else if(aps.get(i).getTypes().size() > 1)
							list2.add(e);
							
					}	
				}
					
				
				//System.out.println("Per il vertice avente rappresentazione "+v.getGraphicType()+"ed id: "+v.getId()+"\nLista 1: \n"+list1.toString());
				//System.out.println("Lista 2:\n"+list2.toString());
				SymbolDefinition s = symbols.get(y);
				ArrayList<AttacchingPoint> apC = s.getAttacchingPoints();
				//Costruzione ExtendedAttacchingPoint
				ArrayList<ExtendedAttacchingPoint> apE = new ArrayList<ExtendedAttacchingPoint>();
				for(int i=0;i<apC.size();i++)
				{
					AttacchingPoint ap = apC.get(i);
					String type = ap.getType();
					String name = ap.getName();
					String graphicRef = ap.getGraphicRef();
					String connectNum = ap.getConnectNum();
					String opConnNum ="";
					int connNum=0;
					String numLoop = ap.getNumLoop();
					String opNumLoop ="";
					int nLoop=0;
					if(connectNum.contains("=="))
					{
						opConnNum="==";
						connNum=Integer.parseInt(connectNum.split("==")[1]);
					}	
					else if(connectNum.contains("<="))
					{
						opConnNum="<=";
						connNum=Integer.parseInt(connectNum.split("<=")[1]);
					}
					else if(connectNum.contains(">="))
					{
						opConnNum=">=";
						connNum=Integer.parseInt(connectNum.split(">=")[1]);
					}	
					else if(connectNum.contains("<")&& !(connectNum.contains("<=")))
					{
						//Trasformazione < in <=
						opConnNum="<=";
						int n =Integer.parseInt(connectNum.split("<")[1])-1;
						connNum=n;
					}
					else if(connectNum.contains(">")&& !(connectNum.contains(">=")))
					{
						//Trasformazion > in >=
						opConnNum=">=";
						int n =Integer.parseInt(connectNum.split(">")[1])+1;
						connNum=n;
					}
					
					if(numLoop.contains("=="))
					{
						opNumLoop="==";
						nLoop=Integer.parseInt(numLoop.split("==")[1]);
					}	
					else if(numLoop.contains("<="))
					{
						opNumLoop="<=";
						nLoop=Integer.parseInt(numLoop.split("<=")[1]);
					}
					else if(numLoop.contains(">="))
					{
						opNumLoop=">=";
						nLoop=Integer.parseInt(numLoop.split(">=")[1]);
					}	
					else if(numLoop.contains("<")&& !(numLoop.contains("<=")))
					{
						opNumLoop="<=";
						int n =Integer.parseInt(numLoop.split("<")[1])-1;
						nLoop=n;
					}
					else if(numLoop.contains(">")&& !(numLoop.contains(">=")))
					{
						opNumLoop=">=";
						int n =Integer.parseInt(numLoop.split(">")[1])+1;
						nLoop=n;
					}
					apE.add(new ExtendedAttacchingPoint(type, name, graphicRef, opConnNum, connNum, opNumLoop, nLoop));
				}	
				System.out.println("Gli attacching point sono: \n"+apC.toString());
				System.out.println("\n Quelli estesi sono:\n"+apE.toString());
				//System.out.println("La dimensione della lista degli attacching point è : "+apE.size());
				/*Controllo su lista con set di dimensione 1*/
				boolean errorSatisfy=false;
				for(int i=0;i<list1.size();i++)
				{
					ArrayList<ExtendedAttacchingPoint> copyAP = new ArrayList<ExtendedAttacchingPoint>(apE);
					/*Se sono stati rimossi tutti gli attacching points ma il for ancora non è terminato significa che vi sono più archi di quanti ne sono ammessi dai vincoli del connettore preso in esame*/
					if(apE.size()==0)
						errorSatisfy=true;
						
					String type = list1.get(i).getTypes().iterator().next();
					String graphicRef = list1.get(i).getsApRef();
					for(int j=0;j<copyAP.size();j++)
					{
						ExtendedAttacchingPoint e = copyAP.get(j);
						/*Se c'è corrispondenza andiamo a decrementare il valore di connectNum*/
						if((e.getGraphicRef().equals(graphicRef)&& e.getType().equals(type)))
						{	
								int n = e.getConnectNum()-1;
								e.setConnectNum(n);
								//Eliminiamo gli attacching points che soddisfano le seguenti condizioni
								if((e.getOpConnectNum().equals("==")&&e.getConnectNum()==0))
									apE.remove(e);
								if(e.getOpConnectNum().equals("<=") && e.getConnectNum()==0)
									apE.remove(e);	
						}	
					}
					
				}
				/*Se è rimasto qualcosa che è ==0 va rimosso BugFix*/
				ArrayList<ExtendedAttacchingPoint> copyAP = new ArrayList<ExtendedAttacchingPoint>(apE);
				for(ExtendedAttacchingPoint e:copyAP){
					if(e.getConnectNum()==0&&e.getOpConnectNum().equals("=="))apE.remove(e);
				}
				//System.out.println("La dimensione attuale della lista degli ap è "+apE.size());
				//System.out.println("La lista degli ap attuale è: "+apE);
				//System.out.println("Dimensione list1: "+list1.size());
				//System.out.println("Dimensione list2: "+list2.size());
				/*Controllo su lista con dimensione > 1*/
				int counter =0; //Al fine di evitare un numero infinito di iterazioni introduciamo la variabile counter che terrà conto del numero di volte in cui non può essere eliminata un'ambiguità
				while(counter<(graph.edgesOf(v).size()-list1.size()))
				{
					ArrayList<EntryListType> copyList = cloneList(list2);
					//ArrayList<EntryListType> copy2 = new ArrayList<EntryListType>(list2);
					for(int i=0;i<copyList.size();i++)
					{
						if(apE.size()==0)
						{	
							errorSatisfy=true;
						
						}	
						EntryListType el = copyList.get(i);
						Iterator<String> types = el.getTypes().iterator();
						String graphicRef = el.getsApRef();
						int index=0;
						int matches=0;
						while(types.hasNext())
						{
							String t = types.next();
							boolean found = false;
							for(int j=0;j<apE.size();j++)
							{
								ExtendedAttacchingPoint e = apE.get(j);
								if((e.getGraphicRef().equals(graphicRef)&& e.getType().equals(t)))
								{
									found=true;
									index = j;
									matches++;
								}	
							}
							if(!found)
							{	
								HashSet<String> newTypes = new HashSet<String>(el.getTypes());
								//System.out.println("Pre modifica \n"+newTypes.toString());
								newTypes.remove(t);
								//System.out.println("Post modifica \n"+newTypes.toString());
								el.setTypes(newTypes);
							}	
						}
						//caso di errore in cui al connettore è collegato qualcosa che non può essere connesso al connettore
						if(el.getTypes().size() == 0)
							errorSatisfy=true;
						if(matches==1)
						{
							ExtendedAttacchingPoint e1 = apE.get(index);
							e1.setConnectNum(e1.getConnectNum()-1);
							if(e1.getOpConnectNum().equals("==")&&e1.getConnectNum()==0)
								apE.remove(e1);
							if(e1.getOpConnectNum().equals("<=")&& e1.getConnectNum()==0)
								apE.remove(e1);
						}	
						if(el.getTypes().size()==1)
						{	
							counter = 0;
							for(int j=0;j<list2.size();j++)
							{
								EntryListType et = list2.get(j);
								if(et.getcApRef().equals(el.getcApRef())&& et.getsApRef().equals(el.getsApRef())&&et.getcId().equals(el.getcId())&&et.getsId().equals(el.getsId()))
									list2.remove(et);
							}	
							//list2.remove(el);
							list1.add(el);
						}
						else
							counter++;
					}
					
					
					if((apE.size()==0 && list2.size()==0)||(apE.size()>0 && list2.size()==0))
						break;
					
				}
				/*Se sono stati eliminati tutti gli attacching point del connettore*/
				if(apE.size()==0)
				{
					if(!errorSatisfy)
					{	
						results.add(true);
						result = cloneList(list1);
						result.addAll(cloneList(list2));
						/*Aggiunta*/
						HashMap<String,HashSet<String>> combIn = new HashMap<String,HashSet<String>>();
						for(int i=0;i<list1.size();i++)
						{
							EntryListType el = list1.get(i);
							String edgeString=el.getsId()+"-"+el.getcId()+":"+el.getsApRef()+"-"+el.getcApRef();
							combIn.put(edgeString, el.getTypes());
						}	
						for(int i=0;i<list2.size();i++)
						{
							EntryListType el = list2.get(i);
							String edgeString=el.getsId()+"-"+el.getcId()+":"+el.getsApRef()+"-"+el.getcApRef();
							combIn.put(edgeString, el.getTypes());
						}
						v.getCombinationsInfo().put(symbols.get(y).getName(), combIn);
						
						//
					}	
					else
					{	
						results.add(false);
						v.getRealGraphicType().remove(symbols.get(y).getName());
						//System.out.println("Tipi restanti : "+v.getRealGraphicType().toString() );
						//System.out.println("Tipi originali : "+getSymbolNames(v.getGraphicType()).toString() );
					}
				}
				/*Altrimenti controlliamo se quelli restanti hanno operatore >= e valore <=0*/
				else
				{
					if(!errorSatisfy)
					{	
						boolean satisfy = true;
						for(int i=0;i<apE.size();i++)
						{
							if((apE.get(i).getOpConnectNum().equals(">=") && (apE.get(i).getConnectNum()>0))||(apE.get(i).getOpConnectNum().equals("<=") && (!(apE.get(i).getConnectNum()>0)))||apE.get(i).getOpConnectNum().equals("=="))
								satisfy=false;
							/*if((!apE.get(i).getOpConnectNum().equals(">=")) || (apE.get(i).getConnectNum()>0))
								satisfy=false;*/
						}
						if(satisfy)
						{	
							results.add(true);
							result = cloneList(list1);
							result.addAll(cloneList(list2));
							/*Aggiunta*/
							HashMap<String,HashSet<String>> combIn = new HashMap<String,HashSet<String>>();
							for(int i=0;i<list1.size();i++)
							{
								EntryListType el = list1.get(i);
								String edgeString=el.getsId()+"-"+el.getcId()+":"+el.getsApRef()+"-"+el.getcApRef();
								combIn.put(edgeString, el.getTypes());
							}	
							for(int i=0;i<list2.size();i++)
							{
								EntryListType el = list2.get(i);
								String edgeString=el.getsId()+"-"+el.getcId()+":"+el.getsApRef()+"-"+el.getcApRef();
								combIn.put(edgeString, el.getTypes());
							}
							
							v.getCombinationsInfo().put(symbols.get(y).getName(), combIn);
							
							//
						}	
						else
						{	
							results.add(false);
							v.getRealGraphicType().remove(symbols.get(y).getName());
							//System.out.println("Tipi restanti : "+v.getRealGraphicType().toString() );
							//System.out.println("Tipi originali : "+getSymbolNames(v.getGraphicType()).toString() );
						}
					}
					else
					{
						results.add(false);
						v.getRealGraphicType().remove(symbols.get(y).getName());
					}	
				}
				System.out.println("Al termine del controllo per il vertice avente id: "+v.getId()+ " |rappresentazione grafica: "+v.getGraphicType()+ " | nome:  "+s.getName());
				System.out.println(list1.toString());
				System.out.println(list2.toString()+"\n");
				
			}	
			
			/*Se per un solo connettore sono state rimosse tutte le ambiguità sui tipi*/
			if(Collections.frequency(results,true)==1)
			{	
				Iterator<GraphEdge>edgesOfV = graph.edgesOf(v).iterator();
				
				// Andiamo a settare i tipi degli archi del vertice che sono stati disambiguati
				
				
				while(edgesOfV.hasNext())
				{
					GraphEdge edg = edgesOfV.next();
					ArrayList<AttacchingPointCouple> aps =edg.getAttacchings();
					
					if(aps.size()==1)
					{
						for(int i=0;i<result.size();i++)
						{
							EntryListType el = result.get(i);
							
							if(edg.getSource().getId().equals(el.getsId())&&edg.getTarget().getId().equals(el.getcId()))
								aps.get(0).setTypes(el.getTypes());
						}
					}
					else
					{
						ArrayList<EntryListType> edgeLoopComponents = new ArrayList<EntryListType>();
						for(int i=0;i<result.size();i++)
						{
							EntryListType el = result.get(i);
							
							if(edg.getSource().getId().equals(el.getsId())&&edg.getTarget().getId().equals(el.getcId()))
								edgeLoopComponents.add(el);
						}
						
						for(int j=0;j<aps.size();j++)
						{
							AttacchingPointCouple ap = aps.get(j);
							for(int k=0;k<edgeLoopComponents.size();k++)
							{
								EntryListType e = edgeLoopComponents.get(k);
								if(e.getcApRef().equals(ap.getAp2())&&e.getsApRef().equals(ap.getAp1()))
								{
									
										aps.get(j).setTypes(e.getTypes());
								}	
							}	
						}	
					}	
				}
			}
			else if(Collections.frequency(results,true)>1 && Collections.frequency(results,true) < symbols.size())
			{
				System.out.println("TO DO");
				/*
				if(Collections.frequency(results,false) == 1)
				{
					connectors.remove(connectors.indexOf(false));
					
				}
				else
				{
					int falses = Collections.frequency(results,false);
					for(int k =0;k<falses;k++)
						connectors.remove(results.indexOf(false));
				}	
				System.out.println(connectors.toString());*/
			}	
			//Generiamo solo le combinazioni in generale per i simboli (determinare come poter compiere la disambiguazione degli archi pure se il nodo è un vertice)
			/*ArrayList<SymbolDefinition> symbols = symMap.get(v.getGraphicType());
			for(int i=0;i<symbols.size();i++)
			{
				HashMap<String,HashSet<String>> combIn = new HashMap<String,HashSet<String>>();
				Iterator<GraphEdge> edges = graph.edgesOf(v).iterator();
				while(edges.hasNext())
				{
					GraphEdge edg = edges.next();
					String idPart = edg.getSource().getId()+"-"+edg.getTarget().getId()+":";
					ArrayList<AttacchingPointCouple> apc = edg.getAttacchings();
					for(int j=0;j<apc.size();j++)
					{
						String apPart = apc.get(j).getAp1()+"-"+apc.get(j).getAp2();
						combIn.put(idPart+apPart, apc.get(j).getTypes());
					}	
				}
				v.getCombinationsInfo().put(symbols.get(i).getName(), combIn);
						
			}*/	
		}	
	}



	protected void setEdgeRealType(Node v) {
		// TODO Auto-generated method stub
		Iterator<GraphEdge> edges = graph.edgesOf(v).iterator();
		while(edges.hasNext())
		{
			GraphEdge edg  = edges.next();
			ArrayList<AttacchingPointCouple> apc = edg.getAttacchings();
			for(int i=0;i<apc.size();i++)
			{
				HashSet<String> realType = new HashSet<String>();
				realType.add(apc.get(i).getTempType());
				apc.get(i).setTypes(realType);
			}	
		}
	}


/*
	private void getEdgesTempType(Node v) {
		// TODO Auto-generated method stub
		Iterator<GraphEdge> edges = graph.edgesOf(v).iterator();
		while(edges.hasNext())
		{
			GraphEdge edg  = edges.next();
			for(int i=0;i<edg.getAttacchings().size();i++)
				System.out.println(edg.getSource().getId()+"-"+edg.getTarget().getId()+" Temp type= "+edg.getAttacchings().get(i).getTempType());
		}	
	}

*/

	protected void setEdgeTempType(Node v,String name, ArrayList<String> comb) {
		// TODO Auto-generated method stub
		Iterator<String> edgesInfo = v.getCombinationsInfo().get(name).keySet().iterator();
		int j=0;
		while(edgesInfo.hasNext())
		{
			String edgeInfo = edgesInfo.next();
			String idS =edgeInfo.split(":")[0].split("-")[0];
			String idC =edgeInfo.split(":")[0].split("-")[1];
			String apS=edgeInfo.split(":")[1].split("-")[0];
			String apC=edgeInfo.split(":")[1].split("-")[1];
			
			Iterator<GraphEdge> edgesOfV = graph.edgesOf(v).iterator();
			while(edgesOfV.hasNext())
			{
				GraphEdge edg = edgesOfV.next();
				if(edg.getSource().getId().equals(idS) && edg.getTarget().getId().equals(idC))
				{
					ArrayList<AttacchingPointCouple> apc = edg.getAttacchings();
					for(int i=0;i<apc.size();i++)
					{
						AttacchingPointCouple ap = apc.get(i);
						if(ap.getAp1().equals(apS) && ap.getAp2().equals(apC))
						{
							ap.setTempType(comb.get(j));
							j++;
						}	
					}	
				}	
			}	
		}	
		
	}



	protected ArrayList<ArrayList<String>> getCombinations(Node v, String name) {
		// TODO Auto-generated method stub
		HashMap<String,HashSet<String>> combinationInfo = v.getCombinationsInfo().get(name);
		ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
		for (Map.Entry<String, HashSet<String>> entry : combinationInfo.entrySet()) {
			   
		    System.out.println("Key = " + entry.getKey());
		    System.out.println("Value = " + entry.getValue().toString());
		    String[] types = entry.getValue().toArray(new String[entry.getValue().size()]);
			ArrayList<String> listTypes = new ArrayList<String>(Arrays.asList(types));
			  
			  list.add(listTypes);
		    
		  }
		//System.out.println("\n");
		ArrayList<ArrayList<String>> result = permutations(list);
		return result;
	}



	/*Il metodo principale che compie la rimozione delle ambiguità nel grafo e l'attribuzione dei nomi effettivi ai nodi*/

	public void removeAmbiguity2() throws ScriptException, CloneNotSupportedException
	{
		Iterator<Node> vertex = graph.vertexSet().iterator();
		System.out.println("Rimuovo ambiguità archi");
		//Fase di rimozione delle ambiguità presenti negli archi
		while(vertex.hasNext())
		{
			Node v = vertex.next();
			if(v.isConnector())
			{
				removeEdgeAmbiguity(v);
			}
		}	
		vertex = graph.vertexSet().iterator();
		while(vertex.hasNext())
		{
			Node v = vertex.next();
			//Calcolo delle combinazioni dei possibili tipi presenti sugli archi del vertice
			ArrayList<ArrayList<String>> combinations = getTypesCombinations(v);
			if(v.isConnector())
			{
				//Possibili connettori aventi la rappresentazione grafica uguale a quella del vertice 
				ArrayList<ConnectorDefinition> conns = connMap.get(v.getGraphicType());
				ArrayList<Boolean> results = new ArrayList<Boolean>();
				int indexC =-1; //Indice della combinazione che soddisferà i vincoli
				
				for(int k=0;k<conns.size();k++)
				{
					System.out.println(conns.get(k).toString());
					ArrayList<Boolean> resultFromCombination = new ArrayList<Boolean>();
					for(ArrayList<String> comb:combinations)
					{
						//Si attribuisce ad ogni arco il tipo corrispondente nella combinazione
						Iterator<GraphEdge> edges = graph.edgesOf(v).iterator();
						for(int i=0;i<comb.size();i++)
						{	
							if(edges.hasNext())
							{
								GraphEdge edg = edges.next();
								ArrayList<AttacchingPointCouple> apc = edg.getAttacchings();
								for(int j=0;j<apc.size();j++)
								{
									
									apc.get(j).setTempType(comb.get(i+j));
								}
								System.out.println(edg.toString()+"\n");
								i=i+apc.size()-1; // Gestione eventuale autociclo
								
							}
							
						}
						
						//Otteniamo le occorrenze dei tipi degli archi del vertice
						HashMap<Couple,Integer> typeOccurrences = getNodeConnectorTypeValues2(v);
						
						//Verifica se tutti i vincoli sono rispettati per il connettore passato come parametro
						boolean resultCombination = checkConnectorConstraint2(conns.get(k),typeOccurrences);
						resultFromCombination.add(resultCombination);
					}
					//Se il numero di combinazioni che soddisfano i vincoli è 1 (o nel caso di linea normale)
					if(Collections.frequency(resultFromCombination, true)==1 /*||(Collections.frequency(resultFromCombination, true)>=2 && isLine(conns.get(k)))*/)
					{
						results.add(true);
						indexC = resultFromCombination.indexOf(true);
					}	
					else
						results.add(false);
				}
				//Se il numero di connettori per i quali viene soddisfatta una combinazione è 1, si procede a disambiguare nodo ed arco
				if(Collections.frequency(results, true)==1 || (isLine(conns.get(0)) && Collections.frequency(results, true)==2) )
				{
					HashSet<String> realType = new HashSet<String>();
					realType.add(conns.get(results.indexOf(true)).getName());
					v.setRealGraphicType(realType);
					if(indexC!=-1)
					{	
						System.out.println("La combinazione che restituisce true è: "+combinations.get(indexC).toString());
						ArrayList<String> combT = combinations.get(indexC);
						Iterator<GraphEdge> edges = graph.edgesOf(v).iterator();
						for(int i=0;i<combT.size();i++)
						{	
							if(edges.hasNext())
							{
								GraphEdge edg = edges.next();
								ArrayList<AttacchingPointCouple> apc = edg.getAttacchings();
								for(int j=0;j<apc.size();j++)
								{
									//Gestire il ciclo
									HashSet<String> t = new HashSet<String>();
									t.add(combT.get(i+j));
									apc.get(j).setTypes(t);
								}
								i=i+apc.size()-1;
								System.out.println(edg.toString()+"\n");
							}
						
							
						}
					}
				}
				else
					v.setRealGraphicType(new HashSet<String>());
			}
			else
			{
				//Stessa procedura con i simboli con la differenza che in questo caso vengono contate le occorrenze anche dei possibili autocicli
				ArrayList<SymbolDefinition> syms = symMap.get(v.getGraphicType());
				ArrayList<Boolean> results = new ArrayList<Boolean>();
				int indexC =-1;
				for(int k=0;k<syms.size();k++)
				{
					System.out.println(syms.get(k).toString());
					ArrayList<Boolean> resultFromCombination = new ArrayList<Boolean>();
					for(ArrayList<String> comb:combinations)
					{
						Iterator<GraphEdge> edges = graph.edgesOf(v).iterator();
						for(int i=0;i<comb.size();i++)
						{	
							if(edges.hasNext())
							{
								GraphEdge edg = edges.next();
								ArrayList<AttacchingPointCouple> apc = edg.getAttacchings();
								for(int j=0;j<apc.size();j++)
								{
									
									apc.get(j).setTempType(comb.get(i+j));
								}
								System.out.println(edg.toString()+"\n");
								i=i+apc.size()-1;
								
								
							}
							
						}
						HashMap<Couple,Integer> typeOccurrences = getNodeSymbolTypeValues2(v);
						HashMap<Loop,Integer> loopOccurrences = getNodeLoopValues2(v);
						boolean resultCombination = checkSymbolConstraint2(syms.get(k),typeOccurrences,loopOccurrences);
						resultFromCombination.add(resultCombination);
					}
					if(Collections.frequency(resultFromCombination, true)==1)
					{
						results.add(true);
						indexC = resultFromCombination.indexOf(true);
					}	
					else
						results.add(false);
				}
				if(Collections.frequency(results, true)==1)
				{
					HashSet<String> realType = new HashSet<String>();
					realType.add(syms.get(results.indexOf(true)).getName());
					v.setRealGraphicType(realType);
					if(indexC!=-1)
					{	
						System.out.println("La combinazione che restituisce true è: "+combinations.get(indexC).toString());
						ArrayList<String> combT = combinations.get(indexC);
						Iterator<GraphEdge> edges = graph.edgesOf(v).iterator();
						for(int i=0;i<combT.size();i++)
						{	
							
							if(edges.hasNext())
							{
								GraphEdge edg = edges.next();
								ArrayList<AttacchingPointCouple> apc = edg.getAttacchings();
								for(int j=0;j<apc.size();j++)
								{
									//Gestire il ciclo
									HashSet<String> t = new HashSet<String>();
									t.add(combT.get(i+j));
									apc.get(j).setTypes(t);
								}
								i=i+apc.size()-1;
								System.out.println(edg.toString()+"\n");
							}
							
						}
					}
				}
				else
					v.setRealGraphicType(new HashSet<String>());
			}	
		}
	}

	/*Il metodo restituisce le occorrenze dei tipi degli archi di un vertice (Simbolo) del grafo da utilizzare successivamente nel controllo del soddisfacimento dei vincoli per la disambiguazione dei simboli */

	protected HashMap<Couple, Integer> getNodeSymbolTypeValues2(Node v) {
		// TODO Auto-generated method stub
		HashMap<Couple,Integer> typesOcc = new HashMap<Couple,Integer>();
		Iterator<GraphEdge> edges = graph.edgesOf(v).iterator();
		/*Per ogni arco del nodo in esame*/
		while(edges.hasNext())
		{
			GraphEdge edg = edges.next();
			ArrayList<AttacchingPointCouple> apps = edg.getAttacchings(); /*recupero attacching points couple*/
			for(int i=0;i<apps.size();i++)
			{
				/*Recupero i possibili tipi dell'attacching point couple*/
				AttacchingPointCouple ap = apps.get(i);
				HashSet<String> types = new HashSet<String>();
				types.add(ap.getTempType());
				Iterator<String> it = types.iterator();
				while(it.hasNext())
				{
					/*Per ogni tipo definisco un nuovo oggetto couple e controllo se è già presente nella map*/
					Couple c = new Couple(it.next(),ap.getAp1());
					/*Se la coppia tipo-riferimento grafico è presente nella map incrementiamo le occorrenze*/
					if(typesOcc.get(c)!=null)
						typesOcc.put(c, typesOcc.get(c)+1);
					/*Altrimenti inseriamo una nuova entry nella map*/
					else
						typesOcc.put(c, 1);
				}	
			}	
			
		}	
		
		
		// System.out.println("Per il vertice "+v.getId());
		for (Map.Entry<Couple, Integer> entry : typesOcc.entrySet()) {
		   
		    System.out.println("Key = " + entry.getKey());
		    System.out.println("Value = " + entry.getValue().toString());
		  }
		System.out.println("\n");
			return typesOcc; /*hashmap dove la chiave è costituita dalla coppia (tipo-nomeAp) ed il valore è il numero di occorrenze*/
	}


	/*Il metodo restituisce le occorrenze degli autocicli presenti in un determinato nodo simbolo ed i relativi tipi degli archi da utilizzare successivamente nel controllo del soddisfacimento dei vincoli per la disambiguazione dei simboli */

	protected HashMap<Loop, Integer> getNodeLoopValues2(Node v) {
		// TODO Auto-generated method stub
		HashMap<Loop,Integer> nodeLoops = new HashMap<Loop,Integer>();
		Iterator<GraphEdge> edges = graph.edgesOf(v).iterator();
		while(edges.hasNext())
		{
			HashSet<String> apsSRef = new HashSet<String>();
			HashSet<String> apsCRef = new HashSet<String>();
			HashSet<String> types = new HashSet<String>();
			GraphEdge edg = edges.next();
			ArrayList<AttacchingPointCouple> apc = edg.getAttacchings();
			/*Se le dimensione della lista di AttacchingPointCouple è > 1 ci troviamo in una situazione di autociclo del connettore che caratterizza l'arco nel simbolo*/
			if(apc.size()>1)
			{
				
				for(int i=0;i<apc.size();i++)
				{
					apsSRef.add(apc.get(i).getAp1());
					apsCRef.add(apc.get(i).getAp2());
					types.add(apc.get(i).getTempType());
				}
				Loop l = new Loop(apsSRef,apsCRef,types);
				/*Se il ciclo è giaà presente nella map viene incrementato il numero di occorrenze*/
				if(nodeLoops.get(l)!=null)
					nodeLoops.put(l, nodeLoops.get(l)+1);
				//Altrimenti viene creata una nuova entry della map
				else
					nodeLoops.put(l, 1);
			}	
		}
		
		//System.out.println("Per il vertice "+v.getId());
		for (Map.Entry<Loop, Integer> entry : nodeLoops.entrySet()) {
		   
		    System.out.println("Key = " + entry.getKey());
		    System.out.println("Value = " + entry.getValue().toString());
		  }
		System.out.println("\n");	
		return nodeLoops;
	}


	/*Il metodo controlla se le occorrenze dei tipi utilizzati e dei possibili autocicli vanno a soddisfare i vincoli definiti per il Simbolo passato come parametro al fine di favorire la disambiguazione del nodo del grafo*/

	protected boolean checkSymbolConstraint2(SymbolDefinition symbolDefinition,
			HashMap<Couple, Integer> typeOccurrences,
			HashMap<Loop, Integer> nodeLoops) throws ScriptException {
		// TODO Auto-generated method stub
			ArrayList<AttacchingPoint> aps = symbolDefinition.getAttacchingPoints();
			
			boolean isSatisfy = true;
			
			/*Verifico che i vincoli definiti per ogni attacching point del simbolo vengono rispettati*/
			for(int j=0;j<aps.size();j++)
			{
				/*Costruisco un oggetto couple per ogni attacching point dell'oggetto Symbol Definition*/
				Couple c1 = new Couple(aps.get(j).getType(),aps.get(j).getGraphicRef());
				boolean foundLoop=false;
			
				/*Se ci sono occorrenze dello specifico attacching point recupero il suo valore*/
				
				if(typeOccurrences.get(c1)!=null)
				{
					String occ =""+typeOccurrences.get(c1);
					String constr = aps.get(j).getConnectNum();
					ScriptEngineManager factory = new ScriptEngineManager();
					ScriptEngine engine = factory.getEngineByName("JavaScript");
					System.out.println(occ+constr+"| "+c1.toString());
					//Controllo soddisfacimento vincolo per attacching point
					boolean val = (Boolean)engine.eval(occ+constr);
					if(!val)
						isSatisfy = false;
				}
				
				/*Altrimenti valore=0*/
				else
				{
					String occ =""+0;
					String constr = aps.get(j).getConnectNum();
					ScriptEngineManager factory = new ScriptEngineManager();
					ScriptEngine engine = factory.getEngineByName("JavaScript");
					System.out.println(occ+constr+"| "+c1.toString());
					//Controllo soddisfacimento vincolo per attacching point
					boolean val = (Boolean)engine.eval(occ+constr);
					if(!val)
						isSatisfy = false;
				}	
				/*controllo sulla presenza del vincolo numLoop*/
				if((aps.get(j).getNumLoop().length()!=0))
				{
					/*Se sono presenti degli autocicli sul simbolo andiamo a vedere se il tipo in considerazione è coinvolto nel ciclo*/
					if( !nodeLoops.isEmpty())
					{	
						Iterator<Loop> itL = nodeLoops.keySet().iterator();
						while(itL.hasNext())
						{
							Loop l = itL.next();
							if(l.getApS().contains(aps.get(j).getGraphicRef()) && l.getTypes().contains(aps.get(j).getType()) && l.getTypes().size()==1)
							{
								foundLoop=true;
								String occ =""+nodeLoops.get(l);
								String constr = aps.get(j).getNumLoop();
								ScriptEngineManager factory = new ScriptEngineManager();
								ScriptEngine engine = factory.getEngineByName("JavaScript");
								System.out.println(occ+constr+"| "+l.toString());
								boolean val = (Boolean)engine.eval(occ+constr);
								if(!val)
									isSatisfy = false;
							}	
						}
					}	
					/*Se non ci sono cicli che coinvolgono il tipo dell'attacching point in considerazione*/
					if(!foundLoop)
					{
						String occ =""+0;
						String constr = aps.get(j).getNumLoop();
						ScriptEngineManager factory = new ScriptEngineManager();
						ScriptEngine engine = factory.getEngineByName("JavaScript");
						System.out.println(occ+constr+" Controllo loop");
						boolean val = (Boolean)engine.eval(occ+constr);
						if(!val)
							isSatisfy = false;
					}
				}	
				
			}
			
			
			if(symbolDefinition.getLocalConstraint().length()!=0)
			{
				//System.out.println("Mo ti devi divertire");
				if(!checkSymbolLocalConstraint(symbolDefinition,typeOccurrences,nodeLoops))
					isSatisfy=false;
			}
			
			if(isSatisfy)
				return true;
			else
				return false;
			
		
	
	}


	/*Il metodo restituisce le occorrenze dei tipi degli archi di un vertice (Connettore) del grafo da utilizzare successivamente nel controllo del soddisfacimento dei vincoli per la disambiguazione dei connettori */

	protected HashMap<Couple, Integer> getNodeConnectorTypeValues2(Node v) {
		// TODO Auto-generated method stub
		HashMap<Couple,Integer> typesOcc = new HashMap<Couple,Integer>();
		Iterator<GraphEdge> edges = graph.edgesOf(v).iterator();
		while(edges.hasNext())
		{
			GraphEdge edg = edges.next();
			ArrayList<AttacchingPointCouple> apps = edg.getAttacchings();
			for(int i=0;i<apps.size();i++)
			{
				AttacchingPointCouple ap = apps.get(i);
				HashSet<String> types = new HashSet<String>();
				types.add(ap.getTempType());
				Iterator<String> it = types.iterator();
				 
				while(it.hasNext())
				{
					Couple c = new Couple(it.next(),ap.getAp2());
					
					if(typesOcc.get(c)!=null)
						typesOcc.put(c, typesOcc.get(c)+1);
					else
						typesOcc.put(c, 1);
				}	
			}	
			
		}	
		
		
		// System.out.println("Per il vertice "+v.getId());
		for (Map.Entry<Couple, Integer> entry : typesOcc.entrySet()) {
		   
		    System.out.println("Key = " + entry.getKey());
		    System.out.println("Value = " + entry.getValue().toString());
		  }
		System.out.println("\n");
			return typesOcc;
	}


	/*Il metodo controlla se le occorrenze dei tipi utilizzati vanno a soddisfare i vincoli definiti per il Connettore passato come parametro al fine di favorire la disambiguazione del nodo del grafo*/

	protected boolean checkConnectorConstraint2(
			ConnectorDefinition connectorDefinition,
			HashMap<Couple, Integer> typeOccurrences) throws ScriptException {
		
		ArrayList<AttacchingPoint> aps = connectorDefinition.getAttacchingPoints();
		//boolean sameTypeAps = checkSameTypeAllAP(aps);
		boolean isSatisfy = true;
		
		if(typeOccurrences.size()!=aps.size())
			isSatisfy=false;
		for(int j=0;j<aps.size();j++)
		{
			Couple c1 = new Couple(aps.get(j).getType(),aps.get(j).getGraphicRef());
			/*Se ci sono occorrenze dello specifico attacching point recupero il suo valore*/
			
			if(typeOccurrences.get(c1)!=null)
			{
				String occ =""+typeOccurrences.get(c1);
				String constr = aps.get(j).getConnectNum();
				ScriptEngineManager factory = new ScriptEngineManager();
				ScriptEngine engine = factory.getEngineByName("JavaScript");
				System.out.println(occ+constr+"| "+c1.toString());
				boolean val = (Boolean)engine.eval(occ+constr);
				if(!val)
					isSatisfy = false;
			
			}
			/*Altrimenti valore=0*/
			else
			{
				String occ =""+0;
				String constr = aps.get(j).getConnectNum();
				ScriptEngineManager factory = new ScriptEngineManager();
				ScriptEngine engine = factory.getEngineByName("JavaScript");
				System.out.println(occ+constr+"| "+c1.toString());
				boolean val = (Boolean)engine.eval(occ+constr);
				if(!val)
					isSatisfy = false;
				
			}
			
			
		}
		System.out.println("\n");	
		if(connectorDefinition.getLocalConstraint().length()==0)
			System.out.println("Local Constraint assente");
		else
		{
			System.out.println("Local Constraint presente");
			if(!checkConnectorLocalConstraint(connectorDefinition,typeOccurrences))
				isSatisfy=false;
		}
			
		if(isSatisfy)
			return true;
		else
			return false;
			
		
	}




	/*Il metodo permette di individuare la presenza di autocicli su un simbolo indicandone le occorrenze e tipi e ap coinvolti in essi*/

	private void removeEdgeAmbiguity(Node v) throws CloneNotSupportedException {
		// TODO Auto-generated megthod stub
		ArrayList<ConnectorDefinition> connectors = connMap.get(v.getGraphicType());
		ArrayList<EntryListType> result = new ArrayList<EntryListType>();
		
		ArrayList<Boolean> results = new ArrayList<Boolean>();
		for(int y=0;y<connectors.size();y++)
		{	
			/*Inizializzazione liste*/
			ArrayList<EntryListType> list1 = new ArrayList<EntryListType>(); //lista contenente set di tipi con size == 1
			ArrayList<EntryListType> list2 = new ArrayList<EntryListType>(); //lista contenente set di tipi con size > 1
			
			Iterator<GraphEdge> edgesOfV = graph.edgesOf(v).iterator();
			while(edgesOfV.hasNext())
			{
				GraphEdge edg = edgesOfV.next();
				ArrayList<AttacchingPointCouple> aps = edg.getAttacchings();
				for(int i =0;i<aps.size();i++)
				{
					EntryListType e = new EntryListType(aps.get(i).getTypes(),aps.get(i).getAp2(),edg.getSource().getId(),edg.getTarget().getId(),aps.get(i).getAp1());
					if(aps.get(i).getTypes().size() == 1)
					{
						list1.add(e);
					}	
					else
						list2.add(e);
				}	
			}
			
			System.out.println("Per il vertice avente rappresentazione "+v.getGraphicType()+"ed id: "+v.getId()+"\nLista 1: \n"+list1.toString());
			System.out.println("Lista 2:\n"+list2.toString());
			ConnectorDefinition c = connectors.get(y);
			ArrayList<AttacchingPoint> apC = c.getAttacchingPoints();
			//Costruzione ExtendedAttacchingPoint
			ArrayList<ExtendedAttacchingPoint> apE = new ArrayList<ExtendedAttacchingPoint>();
			for(int i=0;i<apC.size();i++)
			{
				AttacchingPoint ap = apC.get(i);
				String type = ap.getType();
				String name = ap.getName();
				String graphicRef = ap.getGraphicRef();
				String connectNum = ap.getConnectNum();
				String opConnNum ="";
				int connNum=0;
				String numLoop = ap.getNumLoop();
				String opNumLoop ="";
				int nLoop=0;
				if(connectNum.contains("=="))
				{
					opConnNum="==";
					connNum=Integer.parseInt(connectNum.split("==")[1]);
				}	
				else if(connectNum.contains("<="))
				{
					opConnNum="<=";
					connNum=Integer.parseInt(connectNum.split("<=")[1]);
				}
				else if(connectNum.contains(">="))
				{
					opConnNum=">=";
					connNum=Integer.parseInt(connectNum.split(">=")[1]);
				}	
				else if(connectNum.contains("<")&& !(connectNum.contains("<=")))
				{
					//Trasformazione < in <=
					opConnNum="<=";
					int n =Integer.parseInt(connectNum.split("<")[1])-1;
					connNum=n;
				}
				else if(connectNum.contains(">")&& !(connectNum.contains(">=")))
				{
					//Trasformazion > in >=
					opConnNum=">=";
					int n =Integer.parseInt(connectNum.split(">")[1])+1;
					connNum=n;
				}
				
				if(numLoop.contains("=="))
				{
					opNumLoop="==";
					nLoop=Integer.parseInt(connectNum.split("==")[1]);
				}	
				else if(numLoop.contains("<="))
				{
					opNumLoop="<=";
					nLoop=Integer.parseInt(numLoop.split("<=")[1]);
				}
				else if(numLoop.contains(">="))
				{
					opNumLoop=">=";
					nLoop=Integer.parseInt(numLoop.split(">=")[1]);
				}	
				else if(numLoop.contains("<")&& !(numLoop.contains("<=")))
				{
					opNumLoop="<=";
					int n =Integer.parseInt(numLoop.split("<")[1])-1;
					nLoop=n;
				}
				else if(numLoop.contains(">")&& !(numLoop.contains(">=")))
				{
					opNumLoop=">=";
					int n =Integer.parseInt(numLoop.split(">")[1])+1;
					nLoop=n;
				}
				apE.add(new ExtendedAttacchingPoint(type, name, graphicRef, opConnNum, connNum, opNumLoop, nLoop));
			}	
			System.out.println("Gli attacching point sono: \n"+apC.toString());
			System.out.println("\n Quelli estesi sono:\n"+apE.toString());
			System.out.println("La dimensione della lista degli attacching point è : "+apE.size());
			/*Controllo su lista con set di dimensione 1*/
			boolean errorSatisfy=false;
			for(int i=0;i<list1.size();i++)
			{
				ArrayList<ExtendedAttacchingPoint> copyAP = new ArrayList<ExtendedAttacchingPoint>(apE);
				/*Se sono stati rimossi tutti gli attacching points ma il for ancora non è terminato significa che vi sono più archi di quanti ne sono ammessi dai vincoli del connettore preso in esame*/
				if(apE.size()==0)
					errorSatisfy=true;
					
				String type = list1.get(i).getTypes().iterator().next();
				String graphicRef = list1.get(i).getcApRef();
				for(int j=0;j<copyAP.size();j++)
				{
					ExtendedAttacchingPoint e = copyAP.get(j);
					/*Se c'è corrispondenza andiamo a decrementare il valore di connectNum*/
					if((e.getGraphicRef().equals(graphicRef)&& e.getType().equals(type))/*||(e.getType().equals(type)&& e.getGraphicRef().startsWith("P_")&& graphicRef.equals("P"))*/)
					{
						int n = e.getConnectNum()-1;
						e.setConnectNum(n);
						//Eliminiamo gli attacching points che soddisfano le seguenti condizioni
						if((e.getOpConnectNum().equals("==")&&e.getConnectNum()==0))
							apE.remove(e);
						if(e.getOpConnectNum().equals("<=") && e.getConnectNum()==0)
							apE.remove(e);
					}	
				}
				
			}	
			System.out.println("La dimensione attuale della lista degli ap è "+apE.size());
			
			System.out.println("Dimensione list1: "+list1.size());
			System.out.println("Dimensione list2: "+list2.size());
			/*Controllo su lista con dimensione > 1*/
			int counter =0; //Al fine di evitare un numero infinito di iterazioni introduciamo la variabile counter che terrà conto del numero di volte in cui non può essere eliminata un'ambiguità
			while(counter<(graph.edgesOf(v).size()-list1.size()))
			{
				ArrayList<EntryListType> copyList = cloneList(list2);
				//ArrayList<EntryListType> copy2 = new ArrayList<EntryListType>(list2);
				for(int i=0;i<copyList.size();i++)
				{
					if(apE.size()==0)
					{	
						errorSatisfy=true;
					
					}	
					EntryListType el = copyList.get(i);
					Iterator<String> types = el.getTypes().iterator();
					String graphicRef = el.getcApRef();
					int index=0;
					int matches=0;
					while(types.hasNext())
					{
						String t = types.next();
						boolean found = false;
						for(int j=0;j<apE.size();j++)
						{
							ExtendedAttacchingPoint e = apE.get(j);
							if((e.getGraphicRef().equals(graphicRef)&& e.getType().equals(t))/*||(e.getType().equals(t)&& e.getGraphicRef().startsWith("P_")&& graphicRef.equals("P"))*/)
							{
								found=true;
								index = j;
								matches++;
							}	
						}
						if(!found)
						{	
							HashSet<String> newTypes = new HashSet<String>(el.getTypes());
							System.out.println("Pre modifica \n"+newTypes.toString());
							newTypes.remove(t);
							System.out.println("Post modifica \n"+newTypes.toString());
							el.setTypes(newTypes);
						}	
					}
					//caso di errore in cui al connettore è collegato qualcosa che non può essere connesso al connettore
					if(el.getTypes().size() == 0)
						errorSatisfy=false;
					if(matches==1)
					{
						ExtendedAttacchingPoint e1 = apE.get(index);
						e1.setConnectNum(e1.getConnectNum()-1);
						if(e1.getOpConnectNum().equals("==")&&e1.getConnectNum()==0)
							apE.remove(e1);
						if(e1.getOpConnectNum().equals("<=")&& e1.getConnectNum()==0)
							apE.remove(e1);
					}	
					if(el.getTypes().size()==1)
					{	
						counter = 0;
						for(int j=0;j<list2.size();j++)
						{
							EntryListType et = list2.get(j);
							if(et.getcApRef().equals(el.getcApRef())&& et.getsApRef().equals(el.getsApRef())&&et.getcId().equals(el.getcId())&&et.getsId().equals(el.getsId()))
								list2.remove(et);
						}	
						//list2.remove(el);
						list1.add(el);
					}
					else
						counter++;
				}
				/*copy = new ArrayList<ExtendedAttacchingPoint>(apE);
				for(int i=0;i<copy.size();i++)
				{
					ExtendedAttacchingPoint e = copy.get(i);
					if(e.getOpConnectNum().equals("==") && isLine(c) && e.getConnectNum()<=0)
						apE.remove(e);
				}*/	
				
				if(apE.size()==0 && list2.size()==0)
					break;
				
			}
			/*Se sono stati eliminati tutti gli attacching point del connettore*/
			if(apE.size()==0)
			{
				if(!errorSatisfy)
				{	
					results.add(true);
					result = cloneList(list1);
					/*Aggiunta*/
					HashMap<String,HashSet<String>> combIn = new HashMap<String,HashSet<String>>();
					for(int i=0;i<list1.size();i++)
					{
						EntryListType el = list1.get(i);
						String edgeString=el.getsId()+"-"+el.getcId()+":"+el.getsApRef()+"-"+el.getcApRef();
						combIn.put(edgeString, el.getTypes());
					}	
					for(int i=0;i<list2.size();i++)
					{
						EntryListType el = list2.get(i);
						String edgeString=el.getsId()+"-"+el.getcId()+":"+el.getsApRef()+"-"+el.getcApRef();
						combIn.put(edgeString, el.getTypes());
					}
					v.getCombinationsInfo().put(connectors.get(y).getName(), combIn);
					
					//
				}	
				else
				{	
					results.add(false);
					v.getRealGraphicType().remove(connectors.get(y).getName());
				
				}
			}
			/*Altrimenti controlliamo se quelli restanti hanno operatore >= e valore <=0*/
			else
			{
				boolean satisfy = true;
				for(int i=0;i<apE.size();i++)
				{
					if((apE.get(i).getOpConnectNum().equals(">=") && (apE.get(i).getConnectNum()>0))||(apE.get(i).getOpConnectNum().equals("<=") && (!(apE.get(i).getConnectNum()>0)))||apE.get(i).getOpConnectNum().equals("=="))
						satisfy=false;
					/*if((!apE.get(i).getOpConnectNum().equals(">=")) || (apE.get(i).getConnectNum()>0))
						satisfy=false;*/
				}
				if(satisfy)
				{	
					results.add(true);
					result = cloneList(list1);
					/*Aggiunta*/
					HashMap<String,HashSet<String>> combIn = new HashMap<String,HashSet<String>>();
					for(int i=0;i<list1.size();i++)
					{
						EntryListType el = list1.get(i);
						String edgeString=el.getsId()+"-"+el.getcId()+":"+el.getsApRef()+"-"+el.getcApRef();
						combIn.put(edgeString, el.getTypes());
					}	
					for(int i=0;i<list2.size();i++)
					{
						EntryListType el = list2.get(i);
						String edgeString=el.getsId()+"-"+el.getcId()+":"+el.getsApRef()+"-"+el.getcApRef();
						combIn.put(edgeString, el.getTypes());
					}
					v.getCombinationsInfo().put(connectors.get(y).getName(), combIn);
					
					//
				}	
				else
				{	
					results.add(false);
					v.getRealGraphicType().remove(connectors.get(y).getName());
					System.out.println("Tipi restanti : "+v.getRealGraphicType().toString() );
					System.out.println("Tipi originali : "+getConnectorNames(v.getGraphicType()).toString() );
				}
			}
			System.out.println("Dopo il controllo su lista2");
			System.out.println("Dimensione list1: "+list1.size());
			System.out.println("Dimensione list2: "+list2.size());
			
		}	
		
		/*Se per un solo connettore sono state rimosse tutte le ambiguità sui tipi*/
		if(Collections.frequency(results,true)==1)
		{	
			Iterator<GraphEdge>edgesOfV = graph.edgesOf(v).iterator();
			
			// Andiamo a settare i tipi degli archi del vertice che sono stati disambiguati
			
			
			while(edgesOfV.hasNext())
			{
				GraphEdge edg = edgesOfV.next();
				ArrayList<AttacchingPointCouple> aps =edg.getAttacchings();
				
				if(aps.size()==1)
				{
					for(int i=0;i<result.size();i++)
					{
						EntryListType el = result.get(i);
						
						if(edg.getSource().getId().equals(el.getsId())&&edg.getTarget().getId().equals(el.getcId()))
							aps.get(0).setTypes(el.getTypes());
					}
				}
				else
				{
					ArrayList<EntryListType> edgeLoopComponents = new ArrayList<EntryListType>();
					for(int i=0;i<result.size();i++)
					{
						EntryListType el = result.get(i);
						
						if(edg.getSource().getId().equals(el.getsId())&&edg.getTarget().getId().equals(el.getcId()))
							edgeLoopComponents.add(el);
					}
					
					for(int j=0;j<aps.size();j++)
					{
						AttacchingPointCouple ap = aps.get(j);
						for(int k=0;k<edgeLoopComponents.size();k++)
						{
							EntryListType e = edgeLoopComponents.get(k);
							if(e.getcApRef().equals(ap.getAp2())&&e.getsApRef().equals(ap.getAp1()))
							{
								
									aps.get(j).setTypes(e.getTypes());
							}	
						}	
					}	
				}	
			}
		}
		else if(Collections.frequency(results,true)>1 && Collections.frequency(results,true) < connectors.size())
		{
			System.out.println("TO DO");
			/*
			if(Collections.frequency(results,false) == 1)
			{
				connectors.remove(connectors.indexOf(false));
				
			}
			else
			{
				int falses = Collections.frequency(results,false);
				for(int k =0;k<falses;k++)
					connectors.remove(results.indexOf(false));
			}	
			System.out.println(connectors.toString());*/
		}	
	}
	

	/*Il metodo controlla se il connettore risulta essere una linea*/
	

	private boolean isLine(ConnectorDefinition c) {
		// TODO Auto-generated method stub
		ArrayList<AttacchingPoint> aps = c.getAttacchingPoints();
		for(int i=0;i<aps.size();i++)
		{
			if(!(aps.get(i).getGraphicRef().equals("P1")||aps.get(i).getGraphicRef().equals("P2")))
				return false;
		}	
		return true;
	}


	/*Il metodo restituisce tutte le possibili combinazioni dei tipi degli archi di un nodo*/
	
	private ArrayList<ArrayList<String>> getTypesCombinations(Node v) {
		// TODO Auto-generated method stub
		ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
		Iterator<GraphEdge> edges = graph.edgesOf(v).iterator();
		while(edges.hasNext())
		{
			
			GraphEdge edg = edges.next();
			ArrayList<AttacchingPointCouple> apc = edg.getAttacchings();
			for(int i=0;i<apc.size();i++)
			{
				ArrayList<String> types = new ArrayList<String>();
				Iterator<String> it = apc.get(i).getTypes().iterator();
				while(it.hasNext())
				{
					types.add(it.next());
				}	
				list.add(types);
			}	
		}	
		System.out.println("L'insieme degli arraylist dei tipi è: \n"+list.toString());
		ArrayList<ArrayList<String>> result = permutations(list);
		System.out.println("\nL'insieme delle combinazioni è:\n "+result.toString());
		return result;
	}


	

	/*Il metodo va a controllare il soddisfacimento del localConstraint eventualmente definito per il simbolo*/

	
	
	private boolean checkSymbolLocalConstraint(SymbolDefinition sym,
			HashMap<Couple, Integer> typeOccurrences,HashMap<Loop,Integer> nodeLoops) throws ScriptException {
		String s = sym.getLocalConstraint().trim();
		s=s.replace("\t", "");
		System.out.println("Pre modifica");
		System.out.println(s);
		ArrayList<AttacchingPoint> aps = sym.getAttacchingPoints();
		if(s.contains("connectNum"))
		{	
			for(int i =0;i<aps.size();i++)
			{
				if(s.contains(aps.get(i).getName()))
				{
					
					Couple c1 = new Couple(aps.get(i).getType(),aps.get(i).getGraphicRef());
					
					if(typeOccurrences.get(c1)!=null)
						s=s.replace("connectNum("+aps.get(i).getName()+")", ""+typeOccurrences.get(c1));
					else
						s=s.replace("connectNum("+aps.get(i).getName()+")", "0");
				}	
			}
		}	
		
		ScriptEngineManager factory = new ScriptEngineManager();
		ScriptEngine engine = factory.getEngineByName("JavaScript");
		if(s.contains("numLoop"))
		{
			HashSet<String> matchList = new HashSet<String>();
			Pattern regex = Pattern.compile("numLoop\\(([^()]*)\\)");
			Matcher regexMatcher = regex.matcher(s);
			 while (regexMatcher.find()) {//Finds Matching Pattern in String
		          matchList.add(regexMatcher.group(1));//Fetching Group from String
		    }
			Iterator<String> strings = matchList.iterator();
		    while(strings.hasNext())
		    {
		    	  String name = strings.next();
		    	  //Caso numLoop(typeA)
		          if(!name.contains(","))
		          {
		        	  for(int k=0;k<aps.size();k++)
		        	  {
		        		  if(name.equals(aps.get(k).getName()))
		        		  {
		        			  String graphicRef = aps.get(k).getGraphicRef();
		        			  String type = aps.get(k).getType();
		        			  Iterator<Loop> loops = nodeLoops.keySet().iterator();
		        			  boolean found=false;
		        			  while(loops.hasNext())
		        			  {
		        				  Loop l = loops.next();
		        				  if(l.getApS().contains(graphicRef)&& l.getTypes().contains(type) && l.getTypes().size()==1)
		        				  {
		        					  found=true;
		        					  String value = ""+nodeLoops.get(l);
		        					  s = s.replace("numLoop("+name+")", value);
		        				  }  
		        			  }
		        			  if(!found)
		        			  {
		        				  String value=""+0;
		        				  s = s.replace("numLoop("+name+")", value);
		        			  }  
		        		  }
		        	  }	  
		          }
		          //Caso numLoop(typeA,typeB..)
		          else
		          {
		        	  String[] names = name.split("\\,");
		        	  HashSet<String> grRef = new HashSet<String>();
		        	  HashSet<String> types = new HashSet<String>();
		        	  for(int i=0;i<names.length;i++)
		        	  {
		        		  for(int x=0;x<aps.size();x++)
		        		  {
		        			  if(names[i].equals(aps.get(x).getName()))
		        			  {
		        				  grRef.add(aps.get(x).getGraphicRef());
		        				  types.add(aps.get(x).getType());
		        			  }	  
		        		  }	  
		        	  }
		        	  boolean found=false;
		        	  Iterator<Loop> loops = nodeLoops.keySet().iterator();
		        	  while(loops.hasNext())
		        	  {
		        		  Loop l = loops.next();
		        		  if(l.getApS().containsAll(grRef)&& l.getTypes().containsAll(types) && l.getTypes().size()==types.size())
		        		  {
		        			  found=true;
        					  String value = ""+nodeLoops.get(l);
        					  s = s.replace("numLoop("+name+")", value);
		        		  }	  
		        	  }	
		        	  if(!found)
		        	  {
		        		  String value=""+0;
        				  s = s.replace("numLoop("+name+")", value);
		        	  }	  
		          }  
		    }
		    
		}	
		System.out.println("Post modifica");
		System.out.println(s);	
		boolean val = (Boolean)engine.eval(s);
		//System.out.println(val);
		
		return val;
		
	}



	/*Il metodo va a controllare il soddisfacimento del localConstraint eventualmente definito per il connettore*/

	
	private boolean checkConnectorLocalConstraint(ConnectorDefinition c,
			HashMap<Couple, Integer> typeOccurrences) throws ScriptException {
		String s = c.getLocalConstraint().trim();
		s=s.replace("\t", "");
		ArrayList<AttacchingPoint> aps = c.getAttacchingPoints();
		for(int i =0;i<aps.size();i++)
		{
			if(s.contains(aps.get(i).getName()))
			{
				
				Couple c1 = new Couple(aps.get(i).getType(),aps.get(i).getGraphicRef());
				
				if(typeOccurrences.get(c1)!=null)
				{
					s=s.replace("connectNum("+aps.get(i).getName()+")", ""+typeOccurrences.get(c1));
				}
				else
					s=s.replace("connectNum("+aps.get(i).getName()+")", "0");
			}	
		}
		System.out.println(s);
		ScriptEngineManager factory = new ScriptEngineManager();
		ScriptEngine engine = factory.getEngineByName("JavaScript");
		//System.out.println(occ+constr);
		boolean val = (Boolean)engine.eval(s);
		//System.out.println(val);
		return val;
	}


	/*Metodi per la generazione delle combinazioni*/

	public static <T> ArrayList<ArrayList<T>> permutations(ArrayList<ArrayList<T>> collections) {
		if (collections == null || collections.isEmpty()) {
		    return new ArrayList<ArrayList<T>>();
		} 
		else 
		{
		    ArrayList<ArrayList<T>> res = new ArrayList<ArrayList<T>>();
		    ArrayList<T> current = new ArrayList<T>();
		    permutationsImpl(collections, res, 0, current);
		    return res;
		} 

	}
	
	private static <T> void permutationsImpl(ArrayList<ArrayList<T>> ori, ArrayList<ArrayList<T>> res, int d, ArrayList<T> current) {
		  // if depth equals number of original collections, final reached, add and return
		  if (d == ori.size()) {
		    res.add(current);
		   
		    return;
		  }

		  // iterate from current collection and copy 'current' element N times, one for each element
		  Iterator<ArrayList<T>> collectionArray = ori.iterator();
		  int i = 0;
		  
		  
		  Collection<T> currentCollection = null;
		  
		  while(collectionArray.hasNext()) {
			  currentCollection = collectionArray.next();
			  
			  if (i == d) {
				  break;
			  }
			  
			  ++i;
		  }
		  
		  for (T element : currentCollection) {
		    ArrayList<T> copy = new ArrayList<T>(current);
		    copy.add(element);
		    permutationsImpl(ori, res, d + 1, copy);
		  }
		}

	
	
	private ArrayList<EntryListType> cloneList(ArrayList<EntryListType> list) throws CloneNotSupportedException
	{
		ArrayList<EntryListType> cloned = new ArrayList<EntryListType>();
		for(int i=0;i<list.size();i++)
		{
			cloned.add((EntryListType)list.get(i).clone());
		}	
		return cloned;
	}
	
	public String getProblems()
	{
		String result ="";
		if(problems.size()==0)
		{
			error = false;
			result ="The graph is correctly disambiguated";
			System.out.println("Il grafo risulta essere correttamente disambiguato");
		}
		else
		{
			for(Map.Entry<String,ArrayList<String>> entry:problems.entrySet())
			{
				result=result+entry.getKey()+"\n";
				System.out.println(entry.getKey());
				for(int i=0;i<entry.getValue().size();i++)
				{
					result = result+entry.getValue().get(i)+"\n";
					System.out.println(entry.getValue().get(i));
				}
				result = result+"\n";
				System.out.println("\n");
			}	
		}
		return result;
	}
	
	public void show() {
		
		  JFrame frame = new JFrame();
		  Toolkit tk = Toolkit.getDefaultToolkit();
		  int xSize = ((int) tk.getScreenSize().getWidth());
		  int ySize = ((int) tk.getScreenSize().getHeight());
		  frame.setSize(xSize,ySize);
	        frame.setTitle("Grafo correttezza");
	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        //frame.pack();
	        ListenableDirectedGraph<String, LabelEdge> g =
	                new ListenableDirectedGraph<String,LabelEdge >(
	                    LabelEdge.class);
	        JGraphXAdapter<String, LabelEdge> jgxAdapter = new JGraphXAdapter<String, LabelEdge>(g);
	        
	       
	    
	        mxGraphComponent graphComponent = new mxGraphComponent(jgxAdapter);
	        
	        frame.getContentPane().add(graphComponent);
	       
	        
	      
	        Iterator<Node> vertex = graph.vertexSet().iterator();
	        while(vertex.hasNext())
	        {
	        	Node v = vertex.next();
	        	g.addVertex(v.getId()+" - "+v.getRealGraphicType().toString());
	        }
	       vertex = graph.vertexSet().iterator();
	        while(vertex.hasNext())
	        {
	        	Node v = vertex.next();
	        	
	        	Iterator<GraphEdge> edges = graph.edgesOf(v).iterator();
	        	while(edges.hasNext())
	        	{
	        		GraphEdge e = edges.next();
	        		ArrayList<AttacchingPointCouple> atp = e.getAttacchings();
	        		String label="";
	        		for(int i =0;i<atp.size();i++)
	        		{
	        			label=label+atp.get(i).getTypes().toString()+"\n";
	        		}	
	        		Node source = e.getSource();
	        		Node target = e.getTarget();
	        		g.addEdge(source.getId()+" - "+source.getRealGraphicType().toString(),target.getId()+" - "+target.getRealGraphicType().toString(),new LabelEdge(source.getId()+" - "+source.getRealGraphicType().toString(),target.getId()+" - "+target.getRealGraphicType().toString(),label));
	        	}	
	        	
	        }
	        /*mxRectangle bounds = graphComponent.getGraph().getGraphBounds();
	       graphComponent.getGraph().setMaximumGraphBounds(new mxRectangle(-20,-5,20,20));
	        System.out.println(bounds);
	      /*  jgxAdapter.getView().setTranslate(new mxPoint(-bounds.getX(), -bounds.getY()));

	        		

	        int w = graphComponent.getWidth();
	        int h = graphComponent.getHeight();
	        double s = Math.min(w / bounds.getWidth(), h / bounds.getHeight());
	       jgxAdapter.getView().setScale(s);*/
	       // jgxAdapter.getModel().beginUpdate();
	        /*double x = 20, y = 20;
	        for (mxICell cell : jgxAdapter.getVertexToCellMap().values()) {
	        	jgxAdapter.getModel().setGeometry(cell, new mxGeometry(x, y, 20, 20));
	            x += 40;
	            if (x > 200) {
	                x = 20;
	                y += 40;
	            }
	        }*/
	       // jgxAdapter.setMaximumGraphBounds(new mxRectangle(10, 10, 20, 20));
	        //jgxAdapter.getModel().endUpdate();
	       mxCircleLayout layout = new mxCircleLayout(jgxAdapter);
	       
	        layout.execute(jgxAdapter.getDefaultParent());
	        
	        frame.setVisible(true);
	}
	
	
	public boolean isError() {
		return error;
	}

	public HashMap<String,ArrayList<String>> mapProblems(){
		return problems;
	}
	private boolean error = true;
	protected SimpleGraph<Node, GraphEdge> graph;
	private ArrayList<SymbolDefinition> symDef;
	private ArrayList<ConnectorDefinition> connDef;
	private HashMap<String,ArrayList<SymbolDefinition>> symMap = new HashMap<String,ArrayList<SymbolDefinition>>(); //Coppia formata da nome della rappresentazione grafica e simboli corrispondenti
	private HashMap<String,ArrayList<ConnectorDefinition>> connMap = new HashMap<String,ArrayList<ConnectorDefinition>>(); //Coppia formata da nome della rappresentazione grafica e connettori corrispondenti
	/*Aggiunta*/
	protected HashMap<String,SymbolDefinition> symMapN = new HashMap<String,SymbolDefinition>(); //Coppia formata da nome della rappresentazione grafica e simboli corrispondenti
	protected HashMap<String,ConnectorDefinition> connMapN = new HashMap<String,ConnectorDefinition>(); //Coppia formata da nome della rappresentazione grafica e connettori corrispondenti
	//
	protected HashMap<String,Information> infoSymbols = new HashMap<String,Information>(); //hashmap generale sui simboli
	protected HashMap<String,Information> infoConnectors = new HashMap<String,Information>(); //hashmap generale sui connettori
	
	protected HashMap<String,ArrayList<String>> problems = new HashMap<String,ArrayList<String>>();

	
}
