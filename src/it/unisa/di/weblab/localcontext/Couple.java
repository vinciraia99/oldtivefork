/*******************************************************************************
 * Copyright (c) 2015 Luca Laurino.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Mozilla Public License, v. 2.0
 * which accompanies this distribution, and is available at
 * http://mozilla.org/MPL/2.0/
 ******************************************************************************/

package it.unisa.di.weblab.localcontext;
/*
 * Classe memorizza informazioni utilizzate nelle strutture di supporto per il conteggio delle occorrenze di tipi ed attacching point utilizzate per il controllo dei vincoli
 * Variabili d'istanza:
 * type - Tipo
 * apRef - Riferimento grafico dell'attacching point
 */

/**
 * @author Luca Laurino
 * @version $Id$
 */

public class Couple {

	
	
	public Couple(String type, String apRef) {
		
		this.type = type;
		this.apRef = apRef;
	}
	
	

	public String getType() {
		return type;
	}



	public void setType(String type) {
		this.type = type;
	}



	public String getApRef() {
		return apRef;
	}



	public void setApRef(String apRef) {
		this.apRef = apRef;
	}



	@Override
	public String toString() {
		return "Couple [type=" + type + ", apRef=" + apRef + "]";
	}


	
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((apRef == null) ? 0 : apRef.hashCode());
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
		Couple other = (Couple) obj;
		if (apRef == null) {
			if (other.apRef != null)
				return false;
		} else if (!apRef.equals(other.apRef))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}





	private String type,apRef;
}
