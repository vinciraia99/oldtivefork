/*******************************************************************************
 * Copyright (c) 2015 Alfonso Ferraioli.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Mozilla Public License, v. 2.0
 * which accompanies this distribution, and is available at
 * http://mozilla.org/MPL/2.0/
 ******************************************************************************/

package it.unisa.di.weblab.localcontext.interactive;

import java.util.ArrayList;

/**
 * @author Alfonso Ferraioli
 */

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import it.unisa.di.weblab.localcontext.AttacchingPointCouple;
import it.unisa.di.weblab.localcontext.GraphDisambiguation;
import it.unisa.di.weblab.localcontext.GraphEdge;
import it.unisa.di.weblab.localcontext.Node;

public class Testing {
	
	private GraphDisambiguationInteractive gdI = null;
	private GraphDisambiguation gd = null;
	private boolean verbose = true;
	
	public Testing(GraphDisambiguation gd, GraphDisambiguationInteractive gdI) {
	
		this.gd = gd;
		this.gdI = gdI;
	}

		
	public boolean error(){
		
		if (verbose)
			System.out.println("	 	Inizio Controllo Vertici      ");
		
		HashMap<String,Node> list = createListVertex(gd);
		HashMap<String,Node> listIterative = createListVertex(gdI);
		
		Iterator<String> listKey = list.keySet().iterator();
		boolean error = false;
		
		while(listKey.hasNext()) {
			String key = listKey.next();
			Node c = list.get(key);
			Node in = listIterative.get(key);
				
			if (this.isEqualsNode(c,in)){
				if (verbose)
					System.out.println("\n  +  Nodi Uguali");
			} else {
				if (verbose)
					System.out.println("\n  -  Nodi Diversi");
				error = true;
			}
			
		}
	
		if (verbose) {
			System.out.println(" ------------------------------------------------- ");
			System.out.println("	 	Inizio Controllo Archi ");
		}
		HashMap<String,GraphEdge> listEdge = createListEdge(gd);
		HashMap<String,GraphEdge> listIterativeEdge = createListEdge(gdI);
		
		Iterator<String> listKeyEdge = listEdge.keySet().iterator();
		
		while(listKeyEdge.hasNext())
		{
			String key = listKeyEdge.next();
			
			GraphEdge edgec = listEdge.get(key);
			GraphEdge edgein = listIterativeEdge.get(key);
			
			if (this.isEqualsGraphEdge(edgec,edgein)){
				if (verbose)
					System.out.println("\n  +  Gli Archi Sono Uguali");
			} else {
				if (verbose)
					System.out.println("\n  -  Gli Archi Sono Diversi");
				error = true;
			}
						
		}
		
		if (verbose) {
			System.out.println(" ------------------------------------------------- ");
			System.out.println("	 	Inizio Controllo Problemi ");
			System.out.println("  C:  "+gd.mapProblems());
			System.out.println("  I:  "+gdI.mapProblems());
		}
		
		if (gd.mapProblems().size() != gdI.mapProblems().size()){
			if (verbose)
				System.out.println("       Nodo I: Il numero dei problemi sono diversi");
			error = true;
		} else {
			Iterator<String> keyproblem = gd.mapProblems().keySet().iterator();
			
			while(keyproblem.hasNext()) {
				String keyProblem = keyproblem.next();
				
				if (verbose)
					System.out.println("\n     Chiave Errore: "+keyProblem);
				
				if (!(gdI.mapProblems().containsKey(keyProblem))) {
					if (verbose)
						System.out.println("              Nodo I: Non contiene la chiave "+keyProblem);
					error = true;
				} else {
					
					ArrayList<String> valueError= gd.mapProblems().get(keyProblem);
					ArrayList<String> valueErrorIterative = gdI.mapProblems().get(keyProblem);
				
					if (valueError.size() != valueErrorIterative.size()) {
						if (verbose)
							System.out.println("           Nodo I: Hanno un Diverso numero di errori");
						error = true;
					} else {
						for (String val : valueError) {
							if (!(valueErrorIterative.contains(val))) {
								if (verbose)
									System.out.println("           Nodo I: Hanno Diversi Valori");
								error = true;
							}
						}
					}	
				}
			}
		}
		
		if (verbose)
			System.out.println("");
						
		return error;
	}
	
	private HashMap<String,Node> createListVertex(GraphDisambiguation graph){
		
		HashMap<String,Node> list = new HashMap<String,Node>();
		Iterator<Node> vertex = graph.getGraph().vertexSet().iterator();
		while(vertex.hasNext())
		{
			Node v = vertex.next();
			list.put(v.getId(), v);
		}	
		
		return list;
	}
	
	private HashMap<String,GraphEdge> createListEdge(GraphDisambiguation graph){
		
		HashMap<String,GraphEdge> list = new HashMap<String,GraphEdge>();
		Iterator<GraphEdge> edge = graph.getGraph().edgeSet().iterator();
		while(edge.hasNext())
		{
			GraphEdge e = edge.next();
			String id = "" + e.getSource().getId() + "-" + e.getTarget().getId();
			list.put(id, e);
		}	
		
		return list;
	}
		
	private boolean isEqualsNode(Node n,Node i) {
		
		if (verbose) {
			System.out.println(" ************************************************* ");
			System.out.println("  Nodo C: " +n.toString());
		}
		
		if (i == null) {
			if (verbose)
				System.out.println("  Nodo I: Non Trovato");
			return false;
		}
		
		if (verbose)
			System.out.println("  Nodo I: " +i.toString());
		
		if (!n.getId().equalsIgnoreCase(i.getId())){
			if (verbose)
				System.out.println("  Nodo I: Id Diverso");
			return false;
		}

		if (n.isConnector() ^ i.isConnector()){
			if (verbose)
				System.out.println("  Nodo I: Confronto tra Simbolo - Connettore");
			return false;
		}
		
		if (!n.getGraphicType().equalsIgnoreCase(i.getGraphicType())){
			if (verbose)
				System.out.println("  Nodo I: Tipo Rappresentazione Grafica Diversa");
			return false;
		}
		
		boolean ret = true;
		if (n.getRealGraphicType().size() != i.getRealGraphicType().size()){
			if (verbose)
				System.out.println("  Nodo I: Tipi Reali Diversi");
			ret = false;
		} else {
			Iterator<String> list = n.getRealGraphicType().iterator();
		
			while(list.hasNext()) {
				String type = list.next();
				if (!(i.getRealGraphicType().contains(type))) {
					if (verbose)
						System.out.println("  Nodo I: Non contiene il tipo "+type);
					ret = false;
				}
			}
		}
		
		if (verbose) {
			System.out.println("  Combinazioni \n");
			System.out.println("     Nodo C: "+n.getCombinationsInfo());
			System.out.println("     Nodo I: "+i.getCombinationsInfo());
		}
		
		if (n.getCombinationsInfo().size() != i.getCombinationsInfo().size()){
			if (verbose)
				System.out.println("     Nodo I: Hanno Diversi Possibili Tipi Grafici");
			ret = false;
		} else {
			Iterator<String> listKeyC = n.getCombinationsInfo().keySet().iterator();
		
			while(listKeyC.hasNext()) {
				String key = listKeyC.next();
			
				if (verbose)
					System.out.println("\n     Chiave Combinazione: "+key);
			
				if (!(i.getCombinationsInfo().containsKey(key))) {
					if (verbose)
						System.out.println("     Nodo I: Non contiene la chiave "+key);
					ret = false;
				} else {
					HashMap<String,HashSet<String>> mapN = n.getCombinationsInfo().get(key);
					HashMap<String,HashSet<String>> mapI = i.getCombinationsInfo().get(key);
				
					if (mapN.size() != mapI.size()) {
						if (verbose)
							System.out.println("     Nodo I: Hanno Diversi Possibili Connessioni");
						ret = false;
					} else {
				
						Iterator<String> listMapKeyC = mapN.keySet().iterator();
				
						while(listMapKeyC.hasNext()) {
							String keyMap = listMapKeyC.next();
						
							if (verbose)
								System.out.println("        Chiave Connessione: "+keyMap);
						
							if (!(mapI.containsKey(keyMap))) {
								if (verbose)
									System.out.println("           Nodo I: Non contiene la chiave "+keyMap);
								ret = false;
							} else {
							
								HashSet<String> setN = mapN.get(keyMap);
								HashSet<String> setI = mapI.get(keyMap);
							
								HashSet<String> t1=new HashSet<String>(setN);
								HashSet<String> t2=new HashSet<String>(setI);
						
								t1.retainAll(t2);
					
								if (t1.size() != setN.size()){
									if (verbose)
										System.out.println("           Nodo I: Hanno insiemi di valori diversi "+ t1.toString());
									ret = false;
								}				
							}
						}
					}
				}
			}
		}
		return ret;
	}
	
	private boolean isEqualsGraphEdge(GraphEdge n,GraphEdge i) {
		
		if (verbose) {
			System.out.println(" ************************************************* ");
			System.out.println("  Edge C: " +n.getSource().getId() + " - " + n.getTarget().getId() + " getAttacchings:" + n.getAttacchings() );
		}
		
		if (i == null) {
			if (verbose)
				System.out.println("  Edge I: Non Trovato");
			return false;
		}
		
		if (verbose)
			System.out.println("  Edge I: " +i.getSource().getId() + " - " + i.getTarget().getId() + " getAttacchings:" + i.getAttacchings() );
			
		if (!n.getSource().getId().equalsIgnoreCase(i.getSource().getId())){
			if (verbose)
				System.out.println("  Edge I: Ha un Diverso Source");
			return false;
		}
		
		if (!n.getTarget().getId().equalsIgnoreCase(i.getTarget().getId())){
			if (verbose)
				System.out.println("  Edge I: Ha un Diverso Target");
			return false;
		}
		
		boolean ret = true;
		Iterator<AttacchingPointCouple> appC = n.getAttacchings().iterator();
		while(appC.hasNext())
		{
			AttacchingPointCouple apC = appC.next();
						
			Iterator<AttacchingPointCouple> appI = i.getAttacchings().iterator();
			boolean trovato = false;
			while(appI.hasNext()) {
				AttacchingPointCouple apI = appI.next();
				if (apC.equals(apI)){
					trovato = true;
				}
			}
			
			if (!trovato) {
				if (verbose)
					System.out.println("  Nodo I: Manca Attacch "+ apC);
				ret = false;
			} 
		}	
				
		return ret;
	}

		
}
