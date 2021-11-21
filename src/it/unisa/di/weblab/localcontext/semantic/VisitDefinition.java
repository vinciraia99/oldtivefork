/*******************************************************************************
 * Copyright (c) 2016 Alfonso Ferraioli.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Mozilla Public License, v. 2.0
 * which accompanies this distribution, and is available at
 * http://mozilla.org/MPL/2.0/
 ******************************************************************************/

package it.unisa.di.weblab.localcontext.semantic;
/*
 */

/**
 * @author Alfonso Ferraioli
 */

import java.util.ArrayList;
import java.util.Iterator;

public class VisitDefinition {

	public VisitDefinition(int priority, int order,ArrayList<String> paths) {
		this.verbose = false;
		this.priority = priority;
		this.order = order;
		this.paths = paths;
	}
	
	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}
	
	public int getPriority() {
		return priority;
	}

	public int getOrder() {
		return order;
	}

	public ArrayList<String> getPaths() {
		return paths;
	}
	
	public String toString(){
		String content="VisitDefinition: [Order]="+order+" - [Priority]="+priority+" Paths:";
		
		for(int i=0;i<paths.size();i++) {
			content=content+ "\n      + " +paths.get(i);
		}
		return content;
	}

	private boolean verbose;
	private int priority;
	private int order;
	private ArrayList<String> paths;
}
