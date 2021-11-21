/*******************************************************************************
 * Copyright (c) 2015 Luca Laurino.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Mozilla Public License, v. 2.0
 * which accompanies this distribution, and is available at
 * http://mozilla.org/MPL/2.0/
 ******************************************************************************/

package it.unisa.di.weblab.localcontext;
 /* La classe rappresenta l'informazione relativa alla connessione tra simbolo e connettore
  *  memorizzata sull'arco del grafo
  * 
  * Variabili d'istanza:
  * symbolAp - Riferimento grafico dell'attacching point del simbolo coinvolto nella connessione simbolo/connettore
  * connectorAP - Riferimento grafico dell'attacching point del connettore coinvolto nella connessione simbolo/connettore
  * types - Insieme dei possibili tipi presenti sull'arco
  * tempType - Tipo temporaneo utilizzato durante la fase di disambiguazione
  * 
  */

/**
 * @author Luca Laurino
 * @version $Id$
 */

import java.util.HashSet;
 



public class AttacchingPointCouple {

	public AttacchingPointCouple(String symbolAP,String connectorAP,HashSet<String> types,String tempType)
	{
		ap1 = symbolAP;
		ap2 = connectorAP;
		this.types=types;
		this.tempType=tempType;
	}
	
	public String getAp1() {
		return ap1;
	}
	public void setAp1(String ap1) {
		this.ap1 = ap1;
	}
	public String getAp2() {
		return ap2;
	}
	public void setAp2(String ap2) {
		this.ap2 = ap2;
	}
	public HashSet<String> getTypes() {
		return types;
	}
	public void setTypes(HashSet<String> types) {
		this.types = types;
	}

	@Override
	public String toString() {
		return "AttacchingPointCouple [ap1=" + ap1 + ", ap2=" + ap2
				+ ", tempType=" + tempType + ", types=" + types + "]";
	}


	
	
	public String getTempType() {
		return tempType;
	}

	public void setTempType(String tempType) {
		this.tempType = tempType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ap1 == null) ? 0 : ap1.hashCode());
		result = prime * result + ((ap2 == null) ? 0 : ap2.hashCode());
		result = prime * result
				+ ((tempType == null) ? 0 : tempType.hashCode());
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
		AttacchingPointCouple other = (AttacchingPointCouple) obj;
		if (ap1 == null) {
			if (other.ap1 != null)
				return false;
		} else if (!ap1.equals(other.ap1))
			return false;
		if (ap2 == null) {
			if (other.ap2 != null)
				return false;
		} else if (!ap2.equals(other.ap2))
			return false;
		if (tempType == null) {
			if (other.tempType != null)
				return false;
		} else if (!tempType.equals(other.tempType))
			return false;
		if (types == null) {
			if (other.types != null)
				return false;
		} else if (!types.equals(other.types))
			return false;
		return true;
	}




	private String ap1,ap2,tempType;
	private HashSet<String> types = new HashSet<String>();
	
}
