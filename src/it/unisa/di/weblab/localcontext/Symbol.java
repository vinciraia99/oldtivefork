/*******************************************************************************
 * Copyright (c) 2015 Luca Laurino.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Mozilla Public License, v. 2.0
 * which accompanies this distribution, and is available at
 * http://mozilla.org/MPL/2.0/
 ******************************************************************************/

package it.unisa.di.weblab.localcontext;
/*
 * La classe contiene le informazioni sul simbolo ottenute mediante il parsing del file XML di input
 * Variabili d'istanza:
 * 
 * id - Identificativo connettore
 * graphicType - Rappresentazione grafica
 * attacchings - Insieme degli attacching points utilizzati
 */

/**
 * @author Luca Laurino
 * @version $Id$
 */

import java.util.ArrayList;


public class Symbol {

	public Symbol(String id, String graphicType,ArrayList<AttacchingPointCouple> attacchings) {
		super();
		this.id = id;
		this.graphicType = graphicType;
		this.attacchings = attacchings;
	}
	
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
	
	public ArrayList<AttacchingPointCouple> getAttacchings() {
		return attacchings;
	}
	
	public void setAttacchings(ArrayList<AttacchingPointCouple> attacchings) {
		this.attacchings = attacchings;
	}
	
	
	@Override
	public String toString() {
		String content ="";
		for(int i=0;i<attacchings.size();i++)
		{
			content=content+attacchings.get(i).toString()+"\n";
		}	
		return "Symbol:\n"+"Id: "+id+"\n"+"Graphic Type: "+graphicType+"\n"+ "Attacchings: \n"+content;
	}
	
	private String id,graphicType;
	private ArrayList<AttacchingPointCouple> attacchings;
	
}
