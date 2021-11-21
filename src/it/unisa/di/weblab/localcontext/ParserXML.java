/*******************************************************************************
 * Copyright (c) 2015 Luca Laurino.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Mozilla Public License, v. 2.0
 * which accompanies this distribution, and is available at
 * http://mozilla.org/MPL/2.0/
 ******************************************************************************/

package it.unisa.di.weblab.localcontext;
/*La classe si occupa del parsing del file XML di input e la costruzione degli oggetti
 * Symbol e Connector
 * 
 * Variabili d'istanza
 * dom - Memorizza il parsing del file passato in input
 * symbols - Insieme dei simboli ottenuti mediante l'elaborazione del file XML di input 
 * connectors - Insieme dei simboli ottenuti mediante l'elaborazione del file XML di input 
 * idS - Insieme degli id di simboli e connettori
 * */

/**
 * @author Luca Laurino
 * @version $Id$
 */

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import it.unisa.di.weblab.localcontext.semantic.TextArea;


public class ParserXML {

	public ParserXML(String file){
		
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
		
		
	}
	
	public ParserXML(InputStream is){
		
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
		
		
	}
	
	
	/*Creazione degli oggetti Symbol*/
	
	public ArrayList<Symbol> getSymbols(){
		
		NodeList tagSym = dom.getElementsByTagName("symbol");
		for(int i=0;i<tagSym.getLength();i++)
		{
			Element element = (Element) tagSym.item(i);
			String id = element.getAttribute("id");
			String graphicType = element.getAttribute("graphicRef");
			NodeList symAp = element.getElementsByTagName("ap");
			ArrayList<AttacchingPointCouple> atts = new ArrayList<AttacchingPointCouple>();
			for(int j=0;j<symAp.getLength();j++)
			{
				Element element1 = (Element) symAp.item(j);
				String ap1 = element1.getAttribute("graphicRef");
				String ap2=element1.getAttribute("connRef");
				HashSet<String> tp = new HashSet<String>();
				AttacchingPointCouple ap = new AttacchingPointCouple(ap1, ap2,tp,"");
				atts.add(ap);
			}
			if(ids.add(id))
			{
				Symbol sym = new Symbol(id,graphicType,atts);
				symbols.add(sym);
			}
			else
			{
				throw new IllegalArgumentException("Non possono esserci id duplicati");
			}
			
		}	
		return symbols;
		
	}
	
	/*Creazione degli oggetti connector*/
	
	public ArrayList<Connector> getConnectors(){
		NodeList tagConn = dom.getElementsByTagName("connector");
		for(int i=0;i<tagConn.getLength();i++)
		{
			 Element element = (Element) tagConn.item(i);
			String id = element.getAttribute("id");
			String graphicType = element.getAttribute("graphicRef");
			if(ids.add(id))
			{
				Connector conn = new Connector(id,graphicType);
				connectors.add(conn);
			}
			else
			{
				throw new IllegalArgumentException("Non possono esserci id duplicati");
			}
			
		}	
		return connectors;
	}
	
	public HashMap<String,ArrayList<TextArea>> getTexts(){

		createTexts("symbol");
		createTexts("connector");
		return texts;
	}
	
	private void createTexts(String nodo) {
		NodeList tag = dom.getElementsByTagName(nodo);
		for(int i=0;i<tag.getLength();i++)
		{
			Element element = (Element) tag.item(i);
			String id = element.getAttribute("id");
			NodeList listAp = element.getElementsByTagName("aptext");
			ArrayList<TextArea> atts = new ArrayList<TextArea>();
			for(int j=0;j<listAp.getLength();j++)
			{
				Element element1 = (Element) listAp.item(j);
				String graphicRef = element1.getAttribute("graphicRef");				
				String value = element1.getAttribute("value");
				TextArea tx = new TextArea(graphicRef,value);
				atts.add(tx);
			}
			if (atts.size()>0){
				texts.put(id, atts);
			}
		}
	}
	
	
	public Document getDom() {
		return dom;
	}
		
	private Document dom;
	private HashMap<String,ArrayList<TextArea>> texts = new HashMap<String,ArrayList<TextArea>>();
	private ArrayList<Symbol> symbols = new ArrayList<Symbol>();
	private ArrayList<Connector> connectors = new ArrayList<Connector>();
	private HashSet<String> ids = new HashSet<String>();
}
