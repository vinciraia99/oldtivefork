/*******************************************************************************
 * Copyright (c) 2015 Alfonso Ferraioli.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Mozilla Public License, v. 2.0
 * which accompanies this distribution, and is available at
 * http://mozilla.org/MPL/2.0/
 ******************************************************************************/

package it.unisa.di.weblab.localcontext.interactive;
/*
 * 
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import javax.script.ScriptException;

import org.jgrapht.alg.ConnectivityInspector;

import it.unisa.di.weblab.localcontext.AttacchingPointCouple;
import it.unisa.di.weblab.localcontext.Connector;
import it.unisa.di.weblab.localcontext.ConnectorDefinition;
import it.unisa.di.weblab.localcontext.Couple;

import it.unisa.di.weblab.localcontext.GraphDisambiguation;
import it.unisa.di.weblab.localcontext.GraphEdge;
import it.unisa.di.weblab.localcontext.Information;
import it.unisa.di.weblab.localcontext.Loop;
import it.unisa.di.weblab.localcontext.Node;
import it.unisa.di.weblab.localcontext.Symbol;
import it.unisa.di.weblab.localcontext.SymbolDefinition;


/**
 * @author Alfonso Ferraioli 
 * 
 */

public class GraphDisambiguationInteractive extends GraphDisambiguation{
	

	public GraphDisambiguationInteractive(ArrayList<SymbolDefinition> symDef,ArrayList<ConnectorDefinition> connDef) {
		super(symDef, connDef);
	}
	
	public ArrayList<Node> addConnector(String id, String graphicRef){
		ArrayList<Node> listNodeCheck = new ArrayList<Node>();
		if (listNode.get(id)==null) {
			Connector c = new Connector(id,graphicRef);
			Node connectorNode = new Node(c.getId(),c.getGraphicRef(),new HashSet<String>(),true);
			graph.addVertex(connectorNode);
			listNode.put(c.getId(), connectorNode);
			listNodeCheck.add(connectorNode);
			return listNodeCheck;
		} else {
			throw new IllegalArgumentException("Non possono esserci id duplicati");
		}
	}
	
	public ArrayList<Node> removeConnector(String id) {
		ArrayList<Node> listNodeCheck = new ArrayList<Node>();
		Node connectorNode = listNode.get(id);
		
		if (connectorNode == null)
			throw new IllegalArgumentException("Il Connettore " + id +" non esiste");
		
		Iterator<GraphEdge> edgesOfV = graph.edgesOf(connectorNode).iterator();
		while(edgesOfV.hasNext())
		{
			GraphEdge edg = edgesOfV.next();
			Node symbolNode;
			if (edg.getSource().isConnector()){
				symbolNode = listNode.get(edg.getTarget().getId());
			} else if (edg.getTarget().isConnector()){
				symbolNode = listNode.get(edg.getSource().getId());
			} else {
				throw new IllegalArgumentException("Il Connettore non è connesso a nessun simbolo");
			}
			listNodeCheck.add(symbolNode);
		}
		
		if(problems.containsKey(id)) {
			problems.remove(id);
		}
		
		graph.removeVertex(connectorNode);
		listNode.remove(id);
		return listNodeCheck;
	}
	
	public ArrayList<Node> addSymbol(String id, String graphicRef){
		ArrayList<Node> listNodeCheck = new ArrayList<Node>();
		if (listNode.get(id)==null) {
			ArrayList<AttacchingPointCouple> atts = new ArrayList<AttacchingPointCouple>();
			Symbol s = new Symbol(id,graphicRef,atts);	
			Node symbolNode = new Node(s.getId(),s.getGraphicType(),new HashSet<String>(),false);
			graph.addVertex(symbolNode);
			listNode.put(s.getId(), symbolNode);
			listNodeCheck.add(symbolNode);
			return listNodeCheck;
		} else {
			throw new IllegalArgumentException("Non possono esserci id duplicati");
		}
	}
		
	public ArrayList<Node> removeSymbol(String id){
		ArrayList<Node> listNodeCheck = new ArrayList<Node>();
		Node symbolNode = listNode.get(id);
		
		if (symbolNode == null)
			throw new IllegalArgumentException("Il Simbolo " + id +" non esiste");
		
		Iterator<GraphEdge> edgesOfV = graph.edgesOf(symbolNode).iterator();
		while(edgesOfV.hasNext())
		{
			GraphEdge edg = edgesOfV.next();
			Node connectorNode;
			if (edg.getSource().isConnector()){
				connectorNode = listNode.get(edg.getSource().getId());
			} else if (edg.getTarget().isConnector()){
				connectorNode = listNode.get(edg.getTarget().getId());
			} else {
				throw new IllegalArgumentException("Il Connettore non è connesso a nessun simbolo");
			}
			listNodeCheck.add(connectorNode);
		}
		
//-*-*-		System.out.println("\n--------- REMOVE  -----------");
//		System.out.println(listOccRemove);
//		System.out.println("---------------------------");
		
		removeSymbolEntry(symbolNode.getId());
		
//		System.out.println("---------------------------");
//		System.out.println(listOccRemove);
//		System.out.println("--------- FINE REMOVE -----------");
		
		graph.removeVertex(symbolNode);
		listNode.remove(id);
		return listNodeCheck;
	}
	
	public ArrayList<Node> addConnection(String idS,String idC,String graphicRef,String connRef) {
		ArrayList<Node> listNodeCheck = new ArrayList<Node>();
		Node symbolNode = listNode.get(idS);
		Node connector = listNode.get(idC);
		
		if (symbolNode == null)
			throw new IllegalArgumentException("Il Simbolo " + idS +" non esiste");
		
		if (connector == null)
			throw new IllegalArgumentException("Il Connettore " + idC +" non esiste");
				
		listNodeCheck.add(symbolNode);
		listNodeCheck.add(connector);
		if(!graph.containsEdge(symbolNode, connector))
		{
			
			ArrayList<AttacchingPointCouple> att1 = new ArrayList<AttacchingPointCouple>();
			HashSet<String> set = new HashSet<String>();
			att1.add(new AttacchingPointCouple(graphicRef, connRef,set,""));
			GraphEdge edge = new GraphEdge(symbolNode,connector,att1);
			graph.addEdge(symbolNode, connector, edge);
		}
		else
		{
			GraphEdge edg = graph.getEdge(symbolNode, connector);
			HashSet<String> set = new HashSet<String>();
			AttacchingPointCouple ap = new AttacchingPointCouple(graphicRef, connRef,set,"");
			ArrayList<AttacchingPointCouple> att2 = edg.getAttacchings();
			att2.add(ap);
			edg.setAttacchings(att2);
		}
		setEdgeTypes(connector);
		Iterator<GraphEdge> edgesOfV = graph.edgesOf(connector).iterator();
			
		while(edgesOfV.hasNext()) {
			GraphEdge edg = edgesOfV.next();
			if (edg.getSource().getId().equals(connector.getId()) && !(edg.getTarget().getId().equals(symbolNode.getId()))){
				listNodeCheck.add(edg.getTarget());
			} else if (edg.getTarget().getId().equals(connector.getId()) && !(edg.getSource().getId().equals(symbolNode.getId()))) {
				listNodeCheck.add(edg.getSource());
			}
		}	
	
		
		
		return listNodeCheck;
	}
	
	public ArrayList<Node> removeConnection(String idS,String idC,String graphicRef,String connRef) {
		ArrayList<Node> listNodeCheck = new ArrayList<Node>();
		Node symbolNode = listNode.get(idS);
		Node connector = listNode.get(idC);
		
		if (symbolNode == null)
			throw new IllegalArgumentException("Il Simbolo " + idS +" non esiste");
		
		if (connector == null)
			throw new IllegalArgumentException("Il Connettore " + idC +" non esiste");
		
		if(graph.containsEdge(symbolNode, connector))
		{
			listNodeCheck.add(symbolNode);
			listNodeCheck.add(connector);
			GraphEdge edg = graph.getEdge(symbolNode, connector);
			ArrayList<AttacchingPointCouple> att2 = edg.getAttacchings();
			for (int i=0;i<att2.size();i++){
				AttacchingPointCouple att = att2.get(i);
				if ((att.getAp1().equals(graphicRef)) && (att.getAp2().equals(connRef))){
					att2.remove(i);
					break;
				}
			}
			if (att2.size()>0)
				edg.setAttacchings(att2);
			else 
				graph.removeEdge(edg);
		} else {
			throw new IllegalArgumentException("La connessione che si vuole eliminare non è presente!!");
		}
		return listNodeCheck;
		
	}
	
	public HashMap<String, ArrayList<String>> selfCheck(ArrayList<Node> listNode) {
		
		listNodeCheck = listNode;
		
//-*-*-	System.out.println("\n--------- LIST  -----------");
//		System.out.println(listOccRemove);
//		System.out.println("---------------------------");
		
		for (Node v : listNodeCheck) {			

			removeSymbolEntry(v.getId());
			v.setCombinationsInfo(new HashMap<String, HashMap<String, HashSet<String>>>());
		}
		
//-*-*- System.out.println("---------------------------");
//		System.out.println(listOccRemove);
//		System.out.println("--------- FINE LIST -----------");
		
		if(problems.containsKey("Contraints")) {
			problems.remove("Contraints");
		}
		
		/*for (Node v : listNodeCheck) {
			Iterator<GraphEdge> edgesOfV = graph.edgesOf(v).iterator();
			System.out.println(v);
			while(edgesOfV.hasNext())
			{
				GraphEdge edg = edgesOfV.next();
				System.out.println(edg);
			}	
		}*/
		
//-*-*- System.out.println("--------- Controlli -----------");
		setNodeNames();
//		System.out.println("--------- Controlli -----------");
		
//-*-*- for (Node v : listNodeCheck) {
//			Iterator<GraphEdge> edgesOfV = graph.edgesOf(v).iterator();
//			System.out.println(v);
//			while(edgesOfV.hasNext())
//			{
//				GraphEdge edg = edgesOfV.next();
//				System.out.println(edg);
//			}	
//		}
		
				
		try {
			removeAmbiguity3();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return problems;
		
	}
	
	public ArrayList<String> globalCheck(String constraint) {
		ArrayList<String> result = new ArrayList<String>();
		ConnectivityInspector<Node,GraphEdge> ins= new ConnectivityInspector<Node,GraphEdge>(graph);
		if(constraint.equalsIgnoreCase("Connected")&&!ins.isGraphConnected()) result.add("The graph is not connected");
		return result;
	}
	
	public void setNodeNames() {
		
		for (Node v : listNodeCheck) {
			if(v.isConnector())
				v.setRealGraphicType(new HashSet<String>(infoConnectors.get(v.getGraphicType()).getNames()));
			else
				v.setRealGraphicType(new HashSet<String>(infoSymbols.get(v.getGraphicType()).getNames()));
		}
		
	}
	
	public void setEdgeTypes(Node v) {
			Iterator<GraphEdge> edgesOfV = graph.edgesOf(v).iterator();
			while(edgesOfV.hasNext()) {
				GraphEdge edg = edgesOfV.next();
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
					
					//Recuperiamo i tipi associati all'attacching point del simbolo utilizzato nella connessione simbolo/connettore
					types2 =inf2.getRefTypes().get(app.get(i).getAp2());
					t2=new HashSet<String>(types2);	
					
					//I possibili tipi sull'arco risultano essere l'intersezione dei set dei tipi del simbolo e del connettore
					t1.retainAll(t2);
					app.get(i).setTypes(t1);}
				}		
			}				
	}
	
	public void createSymbolOcc(){
		for(String symName:symMapN.keySet()){
			
			SymbolDefinition s = symMapN.get(symName);
			symbolOcc.put(s, 0);
		}
	}
	
	public void removeAmbiguity3() throws CloneNotSupportedException, ScriptException{
				
		for (Node v : listNodeCheck) {
			
			if (v.isConnector()) {
				removeEdgeAmbiguity2(v);
			}
		}
		
		for (Node v : listNodeCheck) {
			
			if (!v.isConnector()) {
				removeEdgeAmbiguity2(v);
			}
		}
		
		//vertex = graph.vertexSet().iterator();
		for (Node v : listNodeCheck) //{
		{
			//Node v = vertex.next();
			//System.out.println("Id: "+ v.getId()+"\nRappresentazione: "+v.getGraphicType()+"\n Possibili tipi: "+v.getRealGraphicType()+"\n");
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
					//System.out.println("\nPer il connettore "+name+" le possibili combinazioni sono: \n"+combinations.toString()+"\n"+"Attacching points:\n"+c.getAttacchingPoints().toString()+"\n");
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
						messages.add("Ambiguity still present because more than one connector's type satisfy present constraints");
						problems.put(v.getId(),messages);
					}
					else
					{
						ArrayList<String> messages = problems.get(v.getId());
						messages.add("Ambiguity still present because more than one connector's type satisfy present constraints");
						problems.put(v.getId(),messages);
					}	
				}
				else if(result.size()==0 && v.getRealGraphicType().size()>0)
				{
					if(!problems.containsKey(v.getId()))
					{
						ArrayList<String> messages = new ArrayList<String>();
						messages.add("Ambiguity still present because the constraints are satisfied for the different types of the connector using more than one of the possible combinations of the types present on the edges");
						problems.put(v.getId(),messages);
					}
					else
					{
						ArrayList<String> messages = problems.get(v.getId());
						messages.add("Ambiguity still present because the constraints are satisfied for the different types of the connector using more than one of the possible combinations of the types present on the edges");
						problems.put(v.getId(),messages);
					}
				}
				else
				{
					if(!problems.containsKey(v.getId()))
					{
						ArrayList<String> messages = new ArrayList<String>();
						messages.add("Unable to disambiguate the node because they are not respected the constraints for any possible type of connector");
						problems.put(v.getId(),messages);
					}
					else
					{
						ArrayList<String> messages = problems.get(v.getId());
						messages.add("Unable to disambiguate the node because they are not respected the constraints for any possible type of connector");
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
					//System.out.println("Per il simbolo "+name+" le possibili combinazioni sono: \n"+combinations.toString()+"\n"+"Attacching points:\n"+s.getAttacchingPoints().toString()+"\n");
					ArrayList<Boolean> resultFromCombination = new ArrayList<Boolean>();
					if(combinations.size()==0)
					{
						HashMap<Couple,Integer> typeOccurrences = getNodeSymbolTypeValues2(v);
						HashMap<Loop,Integer> loopOccurrences = getNodeLoopValues2(v);
						boolean resultCombination = checkSymbolConstraint2(s,typeOccurrences,loopOccurrences);
						
						if(resultCombination){
							int numOcc=symbolOcc.get(s);
							symbolOcc.put(s, numOcc+1);
							
//-*-*-					 	System.out.println("--------- ADD -----------");
//							System.out.println(listOccRemove);
//							System.out.println("---------------------------");
							
							listOccRemove.put(v.getId(),name);
							
//-*-*-						System.out.println("Add " + v.getId()+"= "+name + " : "+ (numOcc+1));
//							System.out.println("---------------------------");
//							System.out.println(listOccRemove);
//							System.out.println("--------- FINE ADD -----------");
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
							
//-*-*-						System.out.println("--------- ADD -----------");
//							System.out.println(listOccRemove);
//							System.out.println("---------------------------");
							
							listOccRemove.put(v.getId(),name);
							
//							System.out.println("Add " + v.getId()+"= "+name + " : "+ (numOcc+1));
//							System.out.println("---------------------------");
//							System.out.println(listOccRemove);
//							System.out.println("--------- FINE ADD -----------");
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
						messages.add("Ambiguity still present because the constraints are satisfied for more than possible types of symbol");
						problems.put(v.getId(),messages);
					}
					else
					{
						ArrayList<String> messages = problems.get(v.getId());
						messages.add("Ambiguity still present because the constraints are satisfied for more than possible types of symbol");
						problems.put(v.getId(),messages);
					}	
				}
				else if(result.size()==0 && v.getRealGraphicType().size()>0)
				{
					if(!problems.containsKey(v.getId()))
					{
						ArrayList<String> messages = new ArrayList<String>();
						messages.add("Ambiguity still present because the constraints are satisfied for the different types of the symbol by using more than one of the possible combinations of the types present on the edges");
						problems.put(v.getId(),messages);
					}
					else
					{
						ArrayList<String> messages = problems.get(v.getId());
						messages.add("Ambiguity still present because the constraints are satisfied for the different types of the symbol by using more than one of the possible combinations of the types present on the edges");
						problems.put(v.getId(),messages);
					}
				}
				else
				{
					if(!problems.containsKey(v.getId()))
					{
						ArrayList<String> messages = new ArrayList<String>();
						messages.add("Unable to disambiguate the node because the constraints are not respected for any possible type of symbol");
						problems.put(v.getId(),messages);
					}
					else
					{
						ArrayList<String> messages = problems.get(v.getId());
						messages.add("Unable to disambiguate the node because the constraints are not respected for any possible type of symbol");
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
						messages.add("The occurences of symbol "+s.getName()+" must be == "+n);
						problems.put("Contraints",messages);
					}
					else{
						ArrayList<String> messages = new ArrayList<String>();
						messages.add("The occurences of symbol "+s.getName()+" must be == "+n);
						problems.put("Contraints",messages);
					}
					
					}
			}
				
			else if(occ.startsWith("<=")){
				int n=Integer.parseInt(occ.split("<=")[1]);
				if(!(symbolOcc.get(s)<=n)){
					if(problems.containsKey("Contraints")){
						ArrayList<String> messages = problems.get("Contraints");
						messages.add("The occurences of symbol "+s.getName()+" must be <= "+n);
						problems.put("Contraints",messages);
					}
					else{
						ArrayList<String> messages = new ArrayList<String>();
						messages.add("The occurences of symbol "+s.getName()+" must be <= "+n);
						problems.put("Contraints",messages);
					}
					
					}
			}
			else if(occ.startsWith(">=")){
				int n=Integer.parseInt(occ.split(">=")[1]);
				if(!(symbolOcc.get(s)>=n)){
					if(problems.containsKey("Contraints")){
						ArrayList<String> messages = problems.get("Contraints");
						messages.add("The occurences of symbol "+s.getName()+" must be >= "+n);
						problems.put("Contraints",messages);
					}
					else{
						ArrayList<String> messages = new ArrayList<String>();
						messages.add("The occurences of symbol "+s.getName()+" must be >= "+n);
						problems.put("Contraints",messages);
					}
					
					}
			}
			else if(occ.startsWith("<")){
				int n=Integer.parseInt(occ.split("<")[1]);
				if(!(symbolOcc.get(s)<n)){
					if(problems.containsKey("Contraints")){
						ArrayList<String> messages = problems.get("Contraints");
						messages.add("The occurences of symbol "+s.getName()+" must be < "+n);
						problems.put("Contraints",messages);
					}
					else{
						ArrayList<String> messages = new ArrayList<String>();
						messages.add("The occurences of symbol "+s.getName()+" must be < "+n);
						problems.put("Contraints",messages);
					}
					
					}
			}
			else if(occ.startsWith(">")){
				int n=Integer.parseInt(occ.split(">")[1]);
				if(!(symbolOcc.get(s)>n)){
					if(problems.containsKey("Contraints")){
						ArrayList<String> messages = problems.get("Contraints");
						messages.add("The occurences of symbol "+s.getName()+" must be > "+n);
						problems.put("Contraints",messages);
					}
					else{
						ArrayList<String> messages = new ArrayList<String>();
						messages.add("The occurences of symbol "+s.getName()+" must be > "+n);
						problems.put("Contraints",messages);
					}
					
					}
			}
			
		}
//-*-*-	System.out.println("----");
//		System.out.println(problems);
//		System.out.println("----");
	}
	
	private void removeSymbolEntry(String id){
		
		if (problems.containsKey(id)) {
			problems.remove(id);
		}
		
		if (listOccRemove.containsKey(id)) {
			
			String nameRemove = listOccRemove.get(id);
			SymbolDefinition sr = symMapN.get(nameRemove);
			int numOccRem=symbolOcc.get(sr);
			symbolOcc.put(sr, numOccRem-1);
			listOccRemove.remove(id);
			
//-*-*-		System.out.println("Delete " + id+"= "+nameRemove + " : "+ (numOccRem-1));
		}	
	}
	
	public String getProblems()
	{
		String result ="";
		if(problems.size()==0)
		{
			result ="The graph is correctly disambiguated";
			System.out.println("Il grafo risulta essere correttamente disambiguato");
		}
		else
		{
			for(Map.Entry<String,ArrayList<String>> entry:problems.entrySet())
			{
				result=result+entry.getKey()+"\n";
				//System.out.println(entry.getKey());
				for(int i=0;i<entry.getValue().size();i++)
				{
					result = result+entry.getValue().get(i)+"\n";
					//System.out.println(entry.getValue().get(i));
				}
				result = result+"\n";
				//System.out.println("\n");
			}	
		}
		return result;
	}
	
	
	public void toStringListNode() {
		
		System.out.println(listNode);
	}
	
	private HashMap<String,Node> listNode = new HashMap<String,Node>();
	private ArrayList<Node> listNodeCheck = new ArrayList<Node>();
	
	private HashMap<String,String> listOccRemove = new HashMap<String,String>();
}
