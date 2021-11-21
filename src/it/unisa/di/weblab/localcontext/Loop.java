/*******************************************************************************
 * Copyright (c) 2015 Luca Laurino.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Mozilla Public License, v. 2.0
 * which accompanies this distribution, and is available at
 * http://mozilla.org/MPL/2.0/
 ******************************************************************************/

package it.unisa.di.weblab.localcontext;
/*
 * La classe permette di memorizzare informazioni riguardanti le situazioni 
 * di autociclo di un connettore sul simbolo
 * 
 * Variabili d'istanza
 * apS - set degli attacching points del simbolo coinvolti nel ciclo
 * apC - set degli attacching points del connettore coinvolti nel ciclo
 * types - set dei tipi presenti sull'arco
 * */

/**
 * @author Luca Laurino
 * @version $Id$
 */

import java.util.HashSet;


public class Loop {
	

	public Loop(HashSet<String> apS, HashSet<String> apC, HashSet<String> types) {
		
		this.apS = apS;
		this.apC = apC;
		this.types = types;
	}
	
	public HashSet<String> getApS() {
		return apS;
	}
	
	public void setApS(HashSet<String> apS) {
		this.apS = apS;
	}
	
	public HashSet<String> getApC() {
		return apC;
	}
	
	public void setApC(HashSet<String> apC) {
		this.apC = apC;
	}
	
	public HashSet<String> getTypes() {
		return types;
	}
	
	public void setTypes(HashSet<String> types) {
		this.types = types;
	}
	
	
	
	@Override
	public String toString() {
		return "Loop [apS=" + apS + ", apC=" + apC + ", types=" + types + "]";
	}


	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((apC == null) ? 0 : apC.hashCode());
		result = prime * result + ((apS == null) ? 0 : apS.hashCode());
		result = prime * result + ((types == null) ? 0 : types.hashCode());
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
		Loop other = (Loop) obj;
		if (apC == null) {
			if (other.apC != null)
				return false;
		} else if (!apC.equals(other.apC))
			return false;
		if (apS == null) {
			if (other.apS != null)
				return false;
		} else if (!apS.equals(other.apS))
			return false;
		if (types == null) {
			if (other.types != null)
				return false;
		} else if (!types.equals(other.types))
			return false;
		return true;
	}




	private HashSet<String> apS = new HashSet<String>() ;
	private HashSet<String> apC = new HashSet<String>() ;
	private HashSet<String> types = new HashSet<String>();

}
