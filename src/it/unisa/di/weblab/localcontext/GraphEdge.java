/*******************************************************************************
 * Copyright (c) 2015 Luca Laurino.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Mozilla Public License, v. 2.0
 * which accompanies this distribution, and is available at
 * http://mozilla.org/MPL/2.0/
 ******************************************************************************/

package it.unisa.di.weblab.localcontext;
/* La classe rappresenta il tipo di arco utilizzato nel grafo avente come nodi simboli e connettori
 * 
 * Variabili d'istanza
 * source - Vertice dell'arco
 * target - Vertice dell'arco
 * attacchings - insieme di AttacchingPointCouple che indicano quali attacching point vengono utilizzati nella connessione tra i vertici dell'arco
 * 
 * */

/**
 * @author Luca Laurino
 * @version $Id$
 */

import java.util.ArrayList;


public class GraphEdge{
	
	
	public GraphEdge(Node source, Node target,ArrayList<AttacchingPointCouple> attacchings) {
		this.source = source;
		this.target = target;
		this.attacchings = attacchings;
	}
	
	public Node getSource() {
		return source;
	}
	
	public void setSource(Node source) {
		this.source = source;
	}
	
	public Node getTarget() {
		return target;
	}
	
	public void setTarget(Node target) {
		this.target = target;
	}
	
	public ArrayList<AttacchingPointCouple> getAttacchings() {
		return attacchings;
	}
	
	public void setAttacchings(ArrayList<AttacchingPointCouple> attacchings) {
		this.attacchings = attacchings;
	}
	
	public String toString(){


		return "Source: "+source.toString()+ " - Target: " +target.toString()+attacchings.toString();
	}
	
	private Node source;
	private Node target;
	private ArrayList<AttacchingPointCouple> attacchings;
}
