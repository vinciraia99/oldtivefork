/*******************************************************************************
 * Copyright (c) 2015 Luca Laurino.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Mozilla Public License, v. 2.0
 * which accompanies this distribution, and is available at
 * http://mozilla.org/MPL/2.0/
 ******************************************************************************/

package it.unisa.di.weblab.localcontext;
/*
 * La classe provvede alla costruzione del grafo dove i nodi sono i simboli ed i connettori del linguaggio 
 * e gli archi contengono informazioni riguardo la connessione simbolo-connettore
 * 
 * symbols - Insieme dei simboli ottenuti mediante il parsing del file XML di input
 * connectors - Insieme dei connettori ottenuti mediante il parsing del file XML di input
 */

/**
 * @author Luca Laurino
 * @version $Id$
 */

import java.util.ArrayList;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import java.util.Set;


import org.jgrapht.graph.SimpleGraph;




public class GraphBuilder {
	


	public GraphBuilder(ArrayList<Symbol> symbols, ArrayList<Connector> connectors) {
		
		graph = new SimpleGraph<Node,GraphEdge>(GraphEdge.class);
		this.symbols = symbols;
		this.connectors = connectors;
	}


	/*Il metodo provvede alla costruzione del grafo avente come nodi i simboli ed i connettori*/
	
	public SimpleGraph<Node, GraphEdge> buildGraph(){
		
		HashMap<String,Node> conn = new HashMap<String,Node>();
		for(int i=0;i<connectors.size();i++)
		{
			Connector el = connectors.get(i);
			Node connectorNode = new Node(el.getId(),el.getGraphicRef(),new HashSet<String>(),true);
			graph.addVertex(connectorNode);
			conn.put(el.getId(), connectorNode);
		}
		
		//Creazione vertici simboli ed aggiunta archi
		for(int i=0;i<symbols.size();i++)
		{
			Symbol el = symbols.get(i);
			Node symbolNode = new Node(el.getId(),el.getGraphicType(),new HashSet<String>(),false);
			graph.addVertex(symbolNode);
			/*In base agli attacching point couple del simbolo definiamo gli archi*/
			ArrayList<AttacchingPointCouple> attPs = el.getAttacchings();
			for(int j=0;j<attPs.size();j++)
			{
				AttacchingPointCouple element = attPs.get(j);
				String apc1 = element.getAp1();
				String apc2 = element.getAp2();
				/*Split per recuperare l'id del connettore*/
				String connId = apc2.split("\\.")[0];
				String connAp = apc2.split("\\.")[1];
				Node connector = conn.get(connId);
				/*Se l'arco simbolo connettore non è già presente si aggiunge al grafo 
				 * altrimenti si aggiunge agli attacching point couple dell'arco esistente*/
				if(!graph.containsEdge(symbolNode, connector))
				{
					
					ArrayList<AttacchingPointCouple> att1 = new ArrayList<AttacchingPointCouple>();
					HashSet<String> set = new HashSet<String>();
					att1.add(new AttacchingPointCouple(apc1, connAp,set,""));
					GraphEdge edge = new GraphEdge(symbolNode,connector,att1);
					graph.addEdge(symbolNode, connector, edge);	
				}
				else
				{
					GraphEdge edg = graph.getEdge(symbolNode, connector);
					HashSet<String> set = new HashSet<String>();
					AttacchingPointCouple ap = new AttacchingPointCouple(apc1, connAp,set,"");
					ArrayList<AttacchingPointCouple> att2 = edg.getAttacchings();
					att2.add(ap);
					edg.setAttacchings(att2);
				}
			}

		}	
		return graph;
	}

	
	
	
	public SimpleGraph<Node, GraphEdge> getGraph() {
		return graph;
	}




	public void setGraph(SimpleGraph<Node, GraphEdge> graph) {
		this.graph = graph;
	}




	public ArrayList<Symbol> getSymbols() {
		return symbols;
	}




	public void setSymbols(ArrayList<Symbol> symbols) {
		this.symbols = symbols;
	}




	public ArrayList<Connector> getConnectors() {
		return connectors;
	}




	public void setConnectors(ArrayList<Connector> connectors) {
		this.connectors = connectors;
	}


	public String toString(){
		
		String content="";
		Set<Node> vertex = graph.vertexSet();
		Iterator<Node> it = vertex.iterator();
		
		while(it.hasNext())
		{
			content=content+"Vertex id: "+it.next().getId()+"\n";
		}	
		content=content+"\n\n";
		
		Set<GraphEdge> edges = graph.edgeSet();
		
		Iterator<GraphEdge> it2 = edges.iterator();
		while(it2.hasNext())
		{
			GraphEdge edg = it2.next();
			content = content + "Edge: "+edg.getSource().getId()+" - "+edg.getTarget().getId()+"\n"+"Attacchings:\n";
			for(int k=0;k<edg.getAttacchings().size();k++)
			{
				content=content+edg.getAttacchings().get(k).getAp1()+" - "+edg.getAttacchings().get(k).getAp2()+"\n\n"; 
			}	
		}	
		
		return content ;
		
	}
	
	

	private SimpleGraph<Node, GraphEdge> graph;
	
	private ArrayList<Symbol> symbols;
	private ArrayList<Connector> connectors;
	
	
	

}
