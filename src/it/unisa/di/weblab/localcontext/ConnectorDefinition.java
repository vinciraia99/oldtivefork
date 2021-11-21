/*******************************************************************************
 * Copyright (c) 2015 Luca Laurino.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Mozilla Public License, v. 2.0
 * which accompanies this distribution, and is available at
 * http://mozilla.org/MPL/2.0/
 ******************************************************************************/

package it.unisa.di.weblab.localcontext;
/*La classe memorizza le informazioni di un determinato connettore 
 * ottenute mediante il parsing del file XML di definizione del linguaggio utilizzato
 * 
 * Variabili d'istanza:
 * name - Nome connettore
 * graphicRepresentation - Rappresentazione grafica del connettore
 * attacchingPoints - Insieme degli attacching points
 * localConstraint - Eventuali vincoli locali da rispettare
 */

/**
 * @author Luca Laurino
 * @version $Id$
 */

import java.util.ArrayList;



public class ConnectorDefinition {

	
	
	
	public ConnectorDefinition(String name,String graphicRepresentation,
			 ArrayList<AttacchingPoint> attacchingPoints,String localConstraint) {
		this.name=name;
		this.graphicRepresentation = graphicRepresentation;
		this.localConstraint = localConstraint;
		this.attacchingPoints = attacchingPoints;
	}
	
	
	
	public String getGraphicRepresentation() {
		return graphicRepresentation;
	}
	
	public void setGraphicRepresentation(String graphicRepresentation) {
		this.graphicRepresentation = graphicRepresentation;
	}
	
	public String getLocalConstraint() {
		return localConstraint;
	}
	
	public void setLocalConstraint(String localConstraint) {
		this.localConstraint = localConstraint;
	}
	
	public ArrayList<AttacchingPoint> getAttacchingPoints() {
		return attacchingPoints;
	}
	
	public void setAttacchingPoints(ArrayList<AttacchingPoint> attacchingPoints) {
		this.attacchingPoints = attacchingPoints;
	}

	
	
	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}
	
	public String toString(){
		String content="";
		content = content+"Name: "+name+" Graphic Representation: "+graphicRepresentation+" AttacchingPoints: \n";
		for(int i =0;i<attacchingPoints.size();i++)
		{
			content = content+attacchingPoints.get(i).toString();
		}	
		content = content+"Local Constraint: "+localConstraint+"\n";
		return content;
	}

	private String graphicRepresentation,localConstraint,name;
	private ArrayList<AttacchingPoint> attacchingPoints = new ArrayList<AttacchingPoint>();
}
