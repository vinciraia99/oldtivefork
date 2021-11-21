/*******************************************************************************
 * Copyright (c) 2015 Luca Laurino.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Mozilla Public License, v. 2.0
 * which accompanies this distribution, and is available at
 * http://mozilla.org/MPL/2.0/
 ******************************************************************************/

package it.unisa.di.weblab.localcontext;
/*La classe si occupa del parsing del file XML di definizione del linguaggio utilizzato e della creazione degli oggetti
 * SymbolDefinition e ConnectorDefinition
 * 
 * Variabili d'istanza:
 * dom - Memorizza il parsing del file passato in input
 * symbolsDef - Insieme dei simboli ottenuti mediante l'elaborazione del file XML di definizione 
 * connectorsDef - Insieme dei simboli ottenuti mediante l'elaborazione del file XML di definizione 
 * globalConstraints - Vincoli globali contenuti nel file XML di definizione
 */

/**
 * @author Luca Laurino
 * @version $Id$
 */

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class ParserXMLDefinition {

public ParserXMLDefinition(String file){
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		try {

			//Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();

			//parse using builder to get DOM representation of the XML file
			dom = db.parse(file);


		}catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		}catch(SAXException se) {
			se.printStackTrace();
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}
		createSymbols();
		createConnectors();
		
	}

	public ParserXMLDefinition(InputStream is){
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	
		try {
	
			//Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();
	
			//parse using builder to get DOM representation of the XML file
			dom = db.parse(is);
	
	
		}catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		}catch(SAXException se) {
			se.printStackTrace();
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}
		createSymbols();
		createConnectors();
		
	}
	
	protected void createConnectors() {
	// TODO Auto-generated method stub
		NodeList connectors = dom.getElementsByTagName("connector");
		for(int i =0;i<connectors.getLength();i++)
		{
			Element connector = (Element) connectors.item(i);
			String name=connector.getAttribute("name");
			String graphicRef = connector.getAttribute("ref");
			NodeList attPoints = connector.getElementsByTagName("cap");
			ArrayList<AttacchingPoint> connectorAttP = new ArrayList<AttacchingPoint>();
			for(int j=0;j<attPoints.getLength();j++)
			{
				Element ap = (Element) attPoints.item(j);
				String apType = ap.getAttribute("type");
				String apName = ap.getAttribute("name");
				String apGraphicRef = ap.getAttribute("ref");
				String apConnNum = ap.getAttribute("connectNum");
				
				
				AttacchingPoint attP = new AttacchingPoint(apType, apName, apGraphicRef, apConnNum,"");
				connectorAttP.add(attP);
			}
			String localConstraint="";
			NodeList locConstr = connector.getElementsByTagName("localConstraint");
			if(locConstr.getLength()!=0)
				localConstraint = locConstr.item(0).getTextContent();
			ConnectorDefinition conn = new ConnectorDefinition(name,graphicRef, connectorAttP, localConstraint);
			
			/*Se il connettore in questione risulta essere una linea */
			
			if(isLine(conn))
			{
				// Se i tipi degli AP sono gli stessi vengono settati indifferentemente P1 e P2
				
				if(checkSameTypeAllAP(conn.getAttacchingPoints()))
				{
					conn.getAttacchingPoints().get(0).setGraphicRef("P1");
					conn.getAttacchingPoints().get(1).setGraphicRef("P2");
					connectorsDef.add(conn);
				}
				//Altrimenti vengono creati due Connector Definition alternando P1 e P2 come riferimennti grafici degli attacching points
				else
				{
					
					
					String[] split1 = connectorAttP.get(0).getGraphicRef().split("\\:");
					String[] split2 = connectorAttP.get(1).getGraphicRef().split("\\:");
					
					ArrayList<AttacchingPoint> conn1Ap = new ArrayList<AttacchingPoint>();
					conn1Ap.add(new AttacchingPoint(connectorAttP.get(0).getType(), connectorAttP.get(0).getName(), split1[0], connectorAttP.get(0).getConnectNum(), connectorAttP.get(0).getNumLoop()));
					conn1Ap.add(new AttacchingPoint(connectorAttP.get(1).getType(), connectorAttP.get(1).getName(), split1[1], connectorAttP.get(1).getConnectNum(), connectorAttP.get(1).getNumLoop()));
					ArrayList<AttacchingPoint> conn2Ap = new ArrayList<AttacchingPoint>();
					conn2Ap.add(new AttacchingPoint(connectorAttP.get(0).getType(), connectorAttP.get(0).getName(), split2[0], connectorAttP.get(0).getConnectNum(), connectorAttP.get(0).getNumLoop()));
					conn2Ap.add(new AttacchingPoint(connectorAttP.get(1).getType(), connectorAttP.get(1).getName(), split2[1], connectorAttP.get(1).getConnectNum(), connectorAttP.get(1).getNumLoop()));
					
					ConnectorDefinition conn1 = new ConnectorDefinition(name+"~1",graphicRef, conn1Ap, localConstraint);
					ConnectorDefinition conn2 = new ConnectorDefinition(name+"~2",graphicRef, conn2Ap, localConstraint);
					connectorsDef.add(conn1);
					connectorsDef.add(conn2);
				}	
			}
			else
				connectorsDef.add(conn);
		}
}


	protected void createSymbols() {
	// TODO Auto-generated method stub
		NodeList tokens = dom.getElementsByTagName("token");
		for(int i=0;i<tokens.getLength();i++)
		{
			Element symbol = (Element)tokens.item(i);
			String name = symbol.getAttribute("name");
			String graphicRef = symbol.getAttribute("ref");
			String occurrences = symbol.getAttribute("occurrences");
			NodeList attPoints = symbol.getElementsByTagName("ap");
			ArrayList<AttacchingPoint> symbolAttP = new ArrayList<AttacchingPoint>();
			for(int j=0;j<attPoints.getLength();j++)
			{
				Element ap = (Element) attPoints.item(j);
				String apType = ap.getAttribute("type");
				String apName = ap.getAttribute("name");
				String apGraphicRef = ap.getAttribute("ref");
				String apConnNum = ap.getAttribute("connectNum");
				String apNumLoop = "";
				if(ap.hasAttribute("numLoop"))
					apNumLoop = ap.getAttribute("numLoop");
				else
					apNumLoop = "";
				AttacchingPoint attP = new AttacchingPoint(apType, apName, apGraphicRef, apConnNum, apNumLoop);
				symbolAttP.add(attP);
			}
			String localConstraint="";
			NodeList locConstr = symbol.getElementsByTagName("localConstraint");
			if(locConstr.getLength()!=0)
				localConstraint = locConstr.item(0).getTextContent();
			
			SymbolDefinition sym = new SymbolDefinition(name, graphicRef, occurrences, symbolAttP, localConstraint);
			symbolsDef.add(sym);
		}
		
}


	/*Creazione e restituizione oggetti SymbolDefinition*/ 

	public ArrayList<SymbolDefinition> getSymbols(){
		
		return symbolsDef;
	}
	
	
	/*Creazione e restituzione insieme oggetti ConnectorDefinition*/
	
	public ArrayList<ConnectorDefinition> getConnectors(){
			
		return connectorsDef;
	}
	
	
	
	public String getGlobalConstraints(){
		globalConstraint = dom.getElementsByTagName("constraint").item(0).getTextContent();
		return globalConstraint;
	}
	
	
	public String toString(){
		String content ="";
		content=content+"Symbols Definition\n\n";
		for(int i=0;i<symbolsDef.size();i++)
		{
			content=content+symbolsDef.get(i).toString();
		}
		content=content+"Connector Definition\n\n";
		for(int i=0;i<connectorsDef.size();i++)
		{
			content=content+connectorsDef.get(i).toString();
		}
		content=content+"Global Constraint\n\n";
		content=content+getGlobalConstraints()+"\n";
		return content;
		
	}
	
	/*Il metodo controlla se l'oggetto ConnectorDefinition risulta essere un connettore linea o meno*/
	
	protected boolean isLine(ConnectorDefinition c) {
		// TODO Auto-generated method stub
		ArrayList<AttacchingPoint> aps = c.getAttacchingPoints();
		for(int i=0;i<aps.size();i++)
		{
			if(!(aps.get(i).getGraphicRef().equals("P1:P2")||aps.get(i).getGraphicRef().equals("P2:P1")))
				return false;
		}	
		return true;
	}
	
	
	/*Il metodo controlla se gli attacching points presentano tutti lo stesso tipo*/
	
	
	protected boolean checkSameTypeAllAP(ArrayList<AttacchingPoint> aps) {
		// TODO Auto-generated method stub
		HashSet<String> types = new HashSet<String>();
		for(int i=0;i<aps.size();i++)
			types.add(aps.get(i).getType());
		if(types.size()==1)
			return true;
		return false;
	}
	
	public String getName(){
		Element root=(Element)dom.getElementsByTagName("language").item(0);
		nameLanguage = root.getAttribute("name");
		return nameLanguage;
	}
	
	protected ArrayList<SymbolDefinition> symbolsDef = new ArrayList<SymbolDefinition>();
	protected ArrayList<ConnectorDefinition> connectorsDef = new ArrayList<ConnectorDefinition>();
	protected String nameLanguage;
	protected Document dom;
	protected String globalConstraint;
}
