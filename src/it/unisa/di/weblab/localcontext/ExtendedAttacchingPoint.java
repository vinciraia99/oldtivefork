/*******************************************************************************
 * Copyright (c) 2015 Luca Laurino.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Mozilla Public License, v. 2.0
 * which accompanies this distribution, and is available at
 * http://mozilla.org/MPL/2.0/
 ******************************************************************************/

package it.unisa.di.weblab.localcontext;
/* La classe rappresenta le informazioni dell'attacching point in modo esteso andando a separare nello specifico
 * gli operatori ed i valori dei vincoli per il numero di connessioni e di autocicli (non presente nei connettori).
 * Utilizzo nel metodo removeEdgeAmbiguity al fine di eliminare l'ambiguità negli archi del grafo
 * 
 * Variabili d'istanza:
 * type - Tipo Ap
 * name - Nome
 * graphicRef - Riferimento grafico
 * opConnectNum - Operatore vincolo connectNum
 * connectNum - Valore vincolo connectNum
 * opNumLoop - Operatore vincolo numLoop (non presente nei connettori)
 * numLoop - Valore vincolo numLoop (non presente nei connettori)
 * 
 */

/**
 * @author Luca Laurino
 * @version $Id$
 */

public class ExtendedAttacchingPoint implements Cloneable {

	
	
	
	public ExtendedAttacchingPoint(String type, String name, String graphicRef,
			String opConnectNum, int connectNum, String opNumLoop,
			int numLoop) {
		
		this.type = type;
		this.name = name;
		this.graphicRef = graphicRef;
		this.opConnectNum = opConnectNum;
		this.connectNum = connectNum;
		this.opNumLoop = opNumLoop;
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



	public String getOpConnectNum() {
		return opConnectNum;
	}



	public void setOpConnectNum(String opConnectNum) {
		this.opConnectNum = opConnectNum;
	}



	public int getConnectNum() {
		return connectNum;
	}



	public void setConnectNum(int connectNum) {
		this.connectNum = connectNum;
	}



	public String getOpNumLoop() {
		return opNumLoop;
	}



	public void setOpNumLoop(String opNumLoop) {
		this.opNumLoop = opNumLoop;
	}



	public int getNumLoop() {
		return numLoop;
	}



	public void setNumLoop(int numLoop) {
		this.numLoop = numLoop;
	}



	@Override
	public String toString() {
		return "ExtendedAttacchingPoint [type=" + type + ", name=" + name
				+ ", graphicRef=" + graphicRef + ", opConnectNum="
				+ opConnectNum + ", connectNum=" + connectNum + ", opNumLoop="
				+ opNumLoop + ", numLoop=" + numLoop + "]";
	}


	public Object clone() throws CloneNotSupportedException{
		return super.clone();
	}
	
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + connectNum;
		result = prime * result
				+ ((graphicRef == null) ? 0 : graphicRef.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + numLoop;
		result = prime * result
				+ ((opConnectNum == null) ? 0 : opConnectNum.hashCode());
		result = prime * result
				+ ((opNumLoop == null) ? 0 : opNumLoop.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExtendedAttacchingPoint other = (ExtendedAttacchingPoint) obj;
		if (connectNum != other.connectNum)
			return false;
		if (graphicRef == null) {
			if (other.graphicRef != null)
				return false;
		} else if (!graphicRef.equals(other.graphicRef))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (numLoop != other.numLoop)
			return false;
		if (opConnectNum == null) {
			if (other.opConnectNum != null)
				return false;
		} else if (!opConnectNum.equals(other.opConnectNum))
			return false;
		if (opNumLoop == null) {
			if (other.opNumLoop != null)
				return false;
		} else if (!opNumLoop.equals(other.opNumLoop))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}



	private String type,name,graphicRef,opConnectNum,opNumLoop;
	private int connectNum,numLoop;
	
	
	
}
