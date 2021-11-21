/*******************************************************************************
 * Copyright (c) 2015 Luca Laurino.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Mozilla Public License, v. 2.0
 * which accompanies this distribution, and is available at
 * http://mozilla.org/MPL/2.0/
 ******************************************************************************/
package it.unisa.di.weblab.localcontext;
/*La classe rappresenta l'attacching point di un simbolo o di un connettore
 * 
 * Variabili d'istanza:
 * type - tipo dell'attaching point
 * name - nome
 * graphicRef - riferimento grafico
 * connectNum - vincolo sul numero di connessioni possibili
 * numLoop - vincolo sul numero di possibili autocicli
 * */

/**
 * @author Luca Laurino
 * @version $Id$
 */
public class AttacchingPoint {

	
	
	public AttacchingPoint(String type, String name, String graphicRef,String connectNum, String numLoop) {
		this.type = type;
		this.name = name;
		this.graphicRef = graphicRef;
		this.connectNum = connectNum;
		this.numLoop = numLoop;
	}
	
	

	public String getType() {
		return type;
	}



	public void setType(String type) {
		this.type = type;
	}



	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public String getGraphicRef() {
		return graphicRef;
	}



	public void setGraphicRef(String graphicRef) {
		this.graphicRef = graphicRef;
	}



	public String getConnectNum() {
		return connectNum;
	}



	public void setConnectNum(String connectNum) {
		this.connectNum = connectNum;
	}



	public String getNumLoop() {
		return numLoop;
	}



	public void setNumLoop(String numLoop) {
		this.numLoop = numLoop;
	}


	public String toString(){
		return "Type: "+type+" Name: "+name+" Graphic Ref: "+graphicRef+" connectNum: "+connectNum+" numLoop: "+numLoop+"\n";
	}
	
	
	private String type,name,graphicRef,connectNum,numLoop;
}
