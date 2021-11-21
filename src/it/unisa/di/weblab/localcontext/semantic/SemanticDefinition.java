/*******************************************************************************
 * Copyright (c) 2016 Alfonso Ferraioli.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Mozilla Public License, v. 2.0
 * which accompanies this distribution, and is available at
 * http://mozilla.org/MPL/2.0/
 ******************************************************************************/

package it.unisa.di.weblab.localcontext.semantic;
/*La classe memorizza le informazioni semantiche di un determinato nodo
 * ottenute mediante il parsing del file XML di definizione linguaggio utilizzato
 * 
 * Variabili d'istanza:
 * ref - Nome nodo a cui associare le definizioni
 * text - Rappresenta la lista delle aree di testo
 * property - Insieme delle proprieta'
 * 
 */

/**
 * @author Alfonso Ferraioli
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SemanticDefinition {

	public SemanticDefinition(String ref, HashMap<String, TextAreaDefinition> textArea,ArrayList<PropertyDefinition> property) {
		this.ref = ref;
		this.textArea = textArea;
		this.property = property;
		this.complete = false;
		this.verbose = false;
		createMapTextArea();
		createMapProperty();
	}
	
	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}
	
	public void setRef(String ref) {
		this.ref = ref;
	}

	public String getRef() {
		return ref;
	}

	public HashMap<String, TextAreaDefinition> getTextArea() {
		return textArea;
	}
	
	public TextAreaDefinition getTextAreaName(String name) {
		if (textArea.containsKey(name)){
			return textArea.get(name);
		}
		return null;
	}

	public ArrayList<PropertyDefinition> getProperty() {
		return property;
	}
	
	public PropertyDefinition getPropertyName(String name) {
		if (propertyMap.containsKey(name.toLowerCase())) {
			return propertyMap.get(name.toLowerCase());
		}
		return null;
	}
	
	public boolean isComplete() {
		return complete;
	}
	
	public void setComplete(boolean complete) {
		this.complete = complete;
	}
			
	public String toString(){
		String content="SemanticDefinition: [Ref]="+ref+" - [Complete]="+complete+"\n";
		
		Iterator<String> iter = textArea.keySet().iterator();
		while(iter.hasNext()){
			String key =iter.next(); 
			content=content+ "      - " +textArea.get(key).toString();
		}
		
		for(int i=0;i<property.size();i++) {
			content=content+ "      + " +property.get(i).toString();
		}
		return content;
	}
	
	public String toStringTextArea(){
		String content="SemanticDefinition: \n";
		
		Iterator<String> iter = textArea.keySet().iterator();
		while(iter.hasNext()){
			String key =iter.next(); 
			content=content+ "      - " +textArea.get(key).toString();
		}
		
		return content;
	}
	
	private void createMapProperty(){
		propertyMap = new HashMap<String, PropertyDefinition>();
		if (!property.isEmpty()) {
			for (PropertyDefinition prop : property) {
				propertyMap.put(prop.getName().toLowerCase(), prop);
			}
		}
	}
	
	private void createMapTextArea(){
		textAreaGraphic = new HashMap<String, TextAreaDefinition>();
		if (!textArea.isEmpty()) {	
			Iterator<String> it = textArea.keySet().iterator();
			while (it.hasNext()) {
				TextAreaDefinition text = textArea.get(it.next());
				textAreaGraphic.put(text.getGraphicRef().toLowerCase(), text);
			}
		}
	}
			
	private boolean isErrorTypeChecking(String type,String value){
		
		Structure str = new Structure(type);
		
		if (!str.getRegExpr().isEmpty()){
			type = str.getRegExpr();
		}
				
		Pattern pattern = Pattern.compile(type);
	    Matcher matcher = pattern.matcher(value);
	    if(matcher.matches()) return false;
		return true;
	}

	
	public boolean errorTypeCheck(ArrayList<TextArea> list) {
				
		boolean err = false;
		HashMap<String, String> m = new HashMap<String, String>();		
		
		if (list!=null) {
			for (TextArea ta : list) {
				m.put(ta.getGraphicRef().toLowerCase(), ta.getValue());
				if (!textAreaGraphic.containsKey(ta.getGraphicRef().toLowerCase())){
					err = true;
					//String error = "Nella specifica non e' stata definita un'area di testo "+ta.getGraphicRef();
					String error = ErrorMessagesSemantic.getGraphicRefErrorUndefined(ta.getGraphicRef());
					problems.add(error);
				}
			}
		}
		
		Iterator<String> it = textAreaGraphic.keySet().iterator();
		while (it.hasNext()) {
			String key=it.next();
			TextAreaDefinition txd = textAreaGraphic.get(key);
			if (m.containsKey(txd.getGraphicRef().toLowerCase())){
				txd.setResult(m.get(txd.getGraphicRef().toLowerCase()));
			}
			
			boolean errorType = isErrorTypeChecking(txd.getType(),txd.getResult());
			
			if (verbose) {
				System.out.println("   Type Checking [Name]="+ txd.getName() +": |"+txd.getResult()+"| --"+ (!errorType) +"--> "+txd.getType()+"");
			}
			
			if (errorType){
				err = true;
				//String error = "L'area di testo "+ txd.getName() +" con valore |"+ txd.getResult() +"| non rispetta il tipo "+ txd.getType();
				String error = "";
				if (txd.getErrorMsg().isEmpty()) {
					error = ErrorMessagesSemantic.getTextAreaValueError(txd.getType());
				} else {
					error = txd.getErrorMsg();
				}
				problems.add(error);
			}
		}
		
		return err;
	}
		
	public ArrayList<String> getProblems() {
		return problems;
	}
		
	private String ref;
	private HashMap<String, TextAreaDefinition> textArea; // NomeTextArea - TextAreaDefinition
	private HashMap<String, TextAreaDefinition> textAreaGraphic; // GraphicRef - TextAreaDefinition
	private ArrayList<PropertyDefinition> property;
	private HashMap<String, PropertyDefinition> propertyMap;
	private boolean complete;
	
	private boolean verbose;
	private ArrayList<String> problems = new ArrayList<String>();

}
