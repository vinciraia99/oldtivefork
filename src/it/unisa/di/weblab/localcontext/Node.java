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

import java.util.HashMap;
import java.util.HashSet;

/* La classe node definisce la struttura del nodo del grafo contenente simboli e connettori
 * Il nodo del grafo corrisponde ad un simbolo o un connettore e le informazioni contenute sono:
 * 
 * id - Id
 * graphicType - Rappresentazione grafica
 * real-Tipo grafico effettivo
 * -Indicatore se simbolo o connettore
 * 
 */

public class Node {

	//Costruttore
	
	public Node(String id, String graphicType, HashSet<String> realGraphicType, boolean isConnector) {
		this.id = id;
		this.graphicType = graphicType;
		this.realGraphicType = realGraphicType;
		this.isConnector = isConnector;
	}
	
	/*Metodi get e set*/
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getGraphicType() {
		return graphicType;
	}
	
	public void setGraphicType(String graphicType) {
		this.graphicType = graphicType;
	}
	
	public HashSet<String> getRealGraphicType() {
		return realGraphicType;
	}
	
	public void setRealGraphicType(HashSet<String> realGraphicType) {
		this.realGraphicType = realGraphicType;
	}
	
	public boolean isConnector() {
		return isConnector;
	}
	
	public void setConnector(boolean isConnector) {
		this.isConnector = isConnector;
	}
	
	public String getSemanticRealGraphicType() {
		String realGraphic = "";
		if (!realGraphicType.isEmpty()) {
			realGraphic = realGraphicType.iterator().next();
			if (realGraphic.contains("~")){
				String[] ris = realGraphic.split("~");
				realGraphic = ris[0];
			}
		}
		return realGraphic;
	}

	/*Variabili d'istanza*/
	
	@Override
	public String toString() {
		return "Node [id=" + id + ", graphicType=" + graphicType
				+ ", realGraphicType=" + realGraphicType.toString() + ", isConnector="
				+ isConnector + "]\n";
	}



	public HashMap<String, HashMap<String, HashSet<String>>> getCombinationsInfo() {
		return combinationsInfo;
	}

	public void setCombinationsInfo(
			HashMap<String, HashMap<String, HashSet<String>>> combinationsInfo) {
		this.combinationsInfo = combinationsInfo;
	}



	private String id;
	private String graphicType;
	private HashSet<String> realGraphicType = new HashSet<String>();
	private boolean isConnector;
	private HashMap<String,HashMap<String,HashSet<String>>> combinationsInfo = new HashMap<String,HashMap<String,HashSet<String>>>();
}
