/*******************************************************************************
 * Copyright (c) 2015 Luca Laurino.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Mozilla Public License, v. 2.0
 * which accompanies this distribution, and is available at
 * http://mozilla.org/MPL/2.0/
 ******************************************************************************/

package it.unisa.di.weblab.localcontext;
/*
 * La classe contiene le informazioni sul connettore ottenute mediante il parsing del file XML di input
 * Variabili d'istanza:
 * id - Identificativo connettore
 * graphicRef - Attacching point del connettore utilizzato
 * 
 */

/**
 * @author Luca Laurino
 * @version $Id$
 */

public class Connector {

	
	
	
	public Connector(String id, String graphicRef) {
		this.id = id;
		this.graphicRef = graphicRef;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getGraphicRef() {
		return graphicRef;
	}

	public void setGraphicRef(String graphicRef) {
		this.graphicRef = graphicRef;
	}

	public String toString(){
		return "Id: "+id+" Graphic Type: "+graphicRef+"\n"; 
	}
	
	private String id,graphicRef;
}
