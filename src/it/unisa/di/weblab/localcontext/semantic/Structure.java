package it.unisa.di.weblab.localcontext.semantic;

import java.util.ArrayList;

public class Structure {
	
	public Structure(String name) {
		this.error = "";
		this.name = name;
	}
		
	public boolean exist() {
		if (name.equalsIgnoreCase("string")) return true;
		else if (name.equalsIgnoreCase("int")) return true;
		else if (name.equalsIgnoreCase("boolean")) return true;
		else if (name.equalsIgnoreCase("float")) return true;
		else if (name.equalsIgnoreCase("list(string)")) return true;
		else if (name.equalsIgnoreCase("list(int)")) return true;
		return false;
	}
	
	public boolean typeChecking(ArrayList<String> input) {
				
		if (name.equalsIgnoreCase("string")) return isString(input);
		else if (name.equalsIgnoreCase("int")) return isInt(input);
		else if (name.equalsIgnoreCase("boolean")) return isBoolean(input);
		else if (name.equalsIgnoreCase("float")) return isFloat(input);
		else if (name.equalsIgnoreCase("list(string)")) return true;
		else if (name.equalsIgnoreCase("list(int)")) return isListInt(input);
		else if (name.equalsIgnoreCase("list(float)")) return isListFloat(input);
		
		error = "La Struttura "+ name +" non esiste";
		return false;
	}
	
	public String getRegExpr(){
		if (name.equalsIgnoreCase("string")) return "(.)*";
		else if (name.equalsIgnoreCase("int")) return "[-+]?[0-9]+";
		else if (name.equalsIgnoreCase("boolean")) return "([Tt][Rr][Uu][Ee]|[Ff][Aa][Ll][Ss][Ee])";
		else if (name.equalsIgnoreCase("float")) return "[-+]?[0-9]+\\.?[0-9]+";
		return "";
	}
	
	private boolean isString(ArrayList<String> input) {
		if (input.size()>1) {
			error="non rispetta la struttura string perché possiede più risultati";
			return false;
		}
		return true;
	}
	
	private boolean isInt(String input) {
		try{
			Integer.parseInt(input);
			return true;
		}catch(Exception e){
			return false;
		}
	}
	
	private boolean isInt(ArrayList<String> input) {
		if (input.size()>1) {
			error="non rispetta la struttura int perché possiede più risultati";
			return false;
		} else if (input.size()==1) {
			if (!isInt(input.get(0))) {
				error="non rispetta la struttura int perchè non è un intero";
				return false;
			}
		}
		return true;
	}
		
	private boolean isListInt(ArrayList<String> input) {
		
		for (String inp : input) {
			if (!isInt(inp)) {
				error="non rispetta la struttura list(int) perchè alcuni valori non sono degli interi";
				return false;
			}
		}
		return true;
	}
	
	private boolean isBoolean(ArrayList<String> input) {
		if (input.size()!=1) {
			error="non rispetta la struttura boolean perchè non è un booleano";
			return false;
		} 
		try {
			Boolean.parseBoolean(input.get(0));
			return true;
		} catch(Exception e){
			return false;
		}
	}
	
	private boolean isFloat(String input) {
		try{
			Float.parseFloat(input);
			return true;
		}catch(Exception e){
			return false;
		}
	}
	
	private boolean isFloat(ArrayList<String> input) {
		if (input.size()>1) {
			error="non rispetta la struttura float perché possiede più risultati";
			return false;
		} else if (input.size()==1) {
			if (!isFloat(input.get(0))) {
				error="non rispetta la struttura float perchè non è un float";
				return false;
			}
		}
		return true;
	}
	
	private boolean isListFloat(ArrayList<String> input) {
		
		for (String inp : input) {
			if (!isFloat(inp)) {
				error="non rispetta la struttura list(float) perchè alcuni valori non sono dei float";
				return false;
			}
		}
		return true;
	}
	
	public String getError() {
		return error;
	}
	
	private String name;
	private String error;

}

