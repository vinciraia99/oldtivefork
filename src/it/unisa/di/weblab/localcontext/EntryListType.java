/*******************************************************************************
 * Copyright (c) 2015 Luca Laurino.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Mozilla Public License, v. 2.0
 * which accompanies this distribution, and is available at
 * http://mozilla.org/MPL/2.0/
 ******************************************************************************/

package it.unisa.di.weblab.localcontext;
/* La classe viene utilizzata per memorizzare informazioni che verranno utilizzate nel metodo che provvede
 * alla disambiguazione degli archi del grafo formato da simboli e connettori 
 * 
 * Variabili d'istanza:
 * 
 * types - Insieme dei tipi
 * cApRef - Riferimento grafico dell'attacching point del connettore utilizzato memorizzato nell'arco
 * sApRef - Riferimento grafico dell'attacching point del simbolo utilizzato memorizzato nell'arco
 * sId - Identificativo del simbolo memorizzato nell'arco
 * cId - Identificativo del connettore memorizzato nell'arco
 * */

/**
 * @author Luca Laurino
 * @version $Id$
 */

import java.util.HashSet;



public class EntryListType implements Cloneable {

	
	public EntryListType(HashSet<String> types, String cApRef,String sId,String cId,String sApRef) {
	
		this.types = types;
		this.cApRef = cApRef;
		this.sId=sId;
		this.cId=cId;
		this.sApRef=sApRef;
		
	}
	

	

	public HashSet<String> getTypes() {
		return types;
	}
	public void setTypes(HashSet<String> types) {
		this.types = types;
	}
	public String getcApRef() {
		return cApRef;
	}
	public void setcApRef(String cApRef) {
		this.cApRef = cApRef;
	}
	public String getsApRef() {
		return sApRef;
	}
	public void setsApRef(String sApRef) {
		this.sApRef = sApRef;
	}
	public String getsId() {
		return sId;
	}
	public void setsId(String sId) {
		this.sId = sId;
	}
	public String getcId() {
		return cId;
	}
	public void setcId(String cId) {
		this.cId = cId;
	}



	@Override
	public String toString() {
		return "EntryListType [types=" + types + ", cApRef=" + cApRef
				+ ", sApRef=" + sApRef + ", sId=" + sId + ", cId=" + cId + "]";
	}

	
	



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cApRef == null) ? 0 : cApRef.hashCode());
		result = prime * result + ((cId == null) ? 0 : cId.hashCode());
		result = prime * result + ((sApRef == null) ? 0 : sApRef.hashCode());
		result = prime * result + ((sId == null) ? 0 : sId.hashCode());
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
		EntryListType other = (EntryListType) obj;
		if (cApRef == null) {
			if (other.cApRef != null)
				return false;
		} else if (!cApRef.equals(other.cApRef))
			return false;
		if (cId == null) {
			if (other.cId != null)
				return false;
		} else if (!cId.equals(other.cId))
			return false;
		if (sApRef == null) {
			if (other.sApRef != null)
				return false;
		} else if (!sApRef.equals(other.sApRef))
			return false;
		if (sId == null) {
			if (other.sId != null)
				return false;
		} else if (!sId.equals(other.sId))
			return false;
		if (types == null) {
			if (other.types != null)
				return false;
		} else if (!types.equals(other.types))
			return false;
		return true;
	}


	public Object clone() throws CloneNotSupportedException
	{
		return super.clone();
	}



	private HashSet<String> types = new HashSet<String>();
	private String cApRef,sApRef;
	private String sId;
	private String cId;
	
}
