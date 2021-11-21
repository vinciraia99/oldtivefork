/*******************************************************************************
 * Copyright (c) 2015 Luca Laurino.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Mozilla Public License, v. 2.0
 * which accompanies this distribution, and is available at
 * http://mozilla.org/MPL/2.0/
 ******************************************************************************/

package it.unisa.di.weblab.localcontext;


/*La classe permette di memorizzare informazioni relative a simboli e connettori aventi la stessa rappresentazione grafica
 * 
 * Variabili d'istanza:
 * 
 * names - nomi effettivi di una possibile rappresentazione grafica di un simbolo o di un connettore
 * refTypes - hashmap contenente i possibili tipi per ogni attacching point dei simboli/connettori con stessa rappresentazione grafica 
 */

/**
 * @author Luca Laurino
 * @version $Id$
 */

import java.util.HashMap;
import java.util.HashSet;

public class Information{
	
	
	public Information(HashSet<String>names,HashMap<String,HashSet<String>> refTypes) {
		this.names = names;
		this.refTypes = refTypes;
	}
	
	
	public HashMap<String,HashSet<String>> getRefTypes() {
		return refTypes;
	}
	
	public void setRefTypes(HashMap<String,HashSet<String>> refTypes) {
		this.refTypes = refTypes;
	}



	public HashSet<String> getNames() {
		return names;
	}



	public void setNames(HashSet<String> names) {
		this.names = names;
	}


	public String toString(){
		String content="";
		content=content+"Names "+names.toString()+"\n"+"Types: "+refTypes.toString()+"\n";
		return content;
	}

	
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((names == null) ? 0 : names.hashCode());
		result = prime * result
				+ ((refTypes == null) ? 0 : refTypes.hashCode());
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
		Information other = (Information) obj;
		if (names == null) {
			if (other.names != null)
				return false;
		} else if (!names.equals(other.names))
			return false;
		if (refTypes == null) {
			if (other.refTypes != null)
				return false;
		} else if (!refTypes.equals(other.refTypes))
			return false;
		return true;
	}




	private HashSet<String> names = new HashSet<String>();
	private HashMap<String,HashSet<String>> refTypes = new HashMap<String,HashSet<String>>();
}
