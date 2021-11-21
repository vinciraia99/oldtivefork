/*******************************************************************************
 * Copyright (c) 2015 Alfonso Ferraioli.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Mozilla Public License, v. 2.0
 * which accompanies this distribution, and is available at
 * http://mozilla.org/MPL/2.0/
 ******************************************************************************/

package it.unisa.di.weblab.localcontext.semantic;
/* La classe si occupa del parsing del file XML di definizione del linguaggio contenente anche la semantica.
 * 
 * Variabili d'istanza aggiunte:
 */

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author Alfonso Ferraioli
 */

public class ParserXMLSemanticDefinition {

	public ParserXMLSemanticDefinition(String file) {

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		try {

			// Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();

			// parse using builder to get DOM representation of the XML file
			dom = db.parse(file);

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (SAXException se) {
			se.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
	}

	public ParserXMLSemanticDefinition(InputStream is) {

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		try {

			// Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();

			// parse using builder to get DOM representation of the XML file
			dom = db.parse(is);

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (SAXException se) {
			se.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

	}

	private SemanticDefinition createNode(String graphicRef) {

		NodeList semantics = dom.getElementsByTagName("semantic");
		for (int i = 0; i < semantics.getLength(); i++) {
				
			Element semantic = (Element) semantics.item(i);
			String ref=semantic.getAttribute("ref");
			
			if (ref.equalsIgnoreCase(graphicRef)) {
			
				NodeList textAreas = semantic.getElementsByTagName("text");
				HashMap<String, TextAreaDefinition> textArea = new HashMap<String, TextAreaDefinition>();
				for(int j=0;j<textAreas.getLength();j++)	{	
				
					Element txn = (Element) textAreas.item(j);
					String txGraphicRef= txn.getAttribute("graphicRef");
					String txName = txn.getAttribute("name");
					String txType = txn.getAttribute("type");
					String errorMsg = txn.getAttribute("errorMsg");
				
					TextAreaDefinition tx = new TextAreaDefinition(txGraphicRef,txName, txType,errorMsg); 
					textArea.put(txName,tx);
				}
			
				ArrayList<PropertyDefinition> property = new ArrayList<PropertyDefinition>();
				
				ArrayList<FunctionDefinition> fucntionList = new ArrayList<FunctionDefinition>();
				FunctionDefinition funcAssign = new FunctionDefinition("assign","","");
				fucntionList.add(funcAssign);
				PropertyDefinition pDefault = new PropertyDefinition("Id", "string", "","",fucntionList); 
				property.add(pDefault);
				
				NodeList prop = semantic.getElementsByTagName("property");
				for(int j=0;j<prop.getLength();j++)	{
					Element pn = (Element) prop.item(j);
					String pName = pn.getAttribute("name");
					String pType = pn.getAttribute("type");
					String pCondition = pn.getAttribute("condition");
					String errorMsg = pn.getAttribute("errorMsg");
				
					NodeList func = pn.getElementsByTagName("function");
					ArrayList<FunctionDefinition> pFunction = new ArrayList<FunctionDefinition>();
					for(int k=0;k<func.getLength();k++)	{
						Element fn = (Element) func.item(k);
						String fName =  fn.getAttribute("name");
						String fPath =  fn.getAttribute("path");
						String fParam = fn.getAttribute("param");
					
						FunctionDefinition f = new FunctionDefinition(fName, fPath, fParam); 
						pFunction.add(f);	
					}
				
					PropertyDefinition p = new PropertyDefinition(pName, pType, pCondition, errorMsg,pFunction); 
					property.add(p);
				}
			
				return new SemanticDefinition(ref, textArea, property);
			}
		}
		
		HashMap<String, TextAreaDefinition> textArea = new HashMap<String, TextAreaDefinition>();
		
		ArrayList<PropertyDefinition> property = new ArrayList<PropertyDefinition>();
		
		ArrayList<FunctionDefinition> fucntionList = new ArrayList<FunctionDefinition>();
		FunctionDefinition funcAssign = new FunctionDefinition("assign","","");
		fucntionList.add(funcAssign);
		PropertyDefinition pDefault = new PropertyDefinition("Id", "string", "","",fucntionList); 
		property.add(pDefault);
		
		return new SemanticDefinition(graphicRef, textArea, property);
	}
	
	public SemanticDefinition getNode(String graphicRef){
		return createNode(graphicRef);
	}
	
	public HashMap<String, VisitDefinition> getVisit(){
		HashMap<String, VisitDefinition> visits = new HashMap<String, VisitDefinition>();
		
		NodeList semantics = dom.getElementsByTagName("semantic");
		for (int i = 0; i < semantics.getLength(); i++) {
			
			Element semantic = (Element) semantics.item(i);
			String ref=semantic.getAttribute("ref");
			
			NodeList vis = semantic.getElementsByTagName("visit");
			for(int j=0;j<vis.getLength();j++)	{
				Element pn = (Element) vis.item(j);
				int priority = Integer.parseInt(pn.getAttribute("priority"));
				int order = Integer.parseInt(pn.getAttribute("order"));
				
				NodeList pathsTag = pn.getElementsByTagName("path");
				ArrayList<String> paths = new ArrayList<String>();
				for(int k=0;k<pathsTag.getLength();k++)	{
					Element pathTag = (Element) pathsTag.item(k);
					String value =  pathTag.getAttribute("value");
					paths.add(value);	
				}
				
				VisitDefinition vs = new VisitDefinition(priority, order, paths);
				visits.put(ref.toLowerCase(), vs);
			}
			
		}
		
		return visits;
	}

	protected Document dom;

}
