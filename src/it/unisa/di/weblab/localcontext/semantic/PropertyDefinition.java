package it.unisa.di.weblab.localcontext.semantic;
/* La classe memorizza le proprietà dei nodi
 * 
 * Variabili d'istanza:
 * name - Nome della proprietà
 * type - Rappresenta il tipo
 * condition - Memorizza una condizione di errore
 * function - L'insieme delle funzioni associate alla proprietà
 * 
 */

import java.util.ArrayList;

/**
 * 
 * @author Alfonso Ferraioli
 *
 */

public class PropertyDefinition {
	
	public PropertyDefinition(String name, String type,String condition,String errorMsg, ArrayList<FunctionDefinition> function) {
		
		this.name = name;
		this.type = type;
		this.condition = condition;
		this.errorMsg = errorMsg;
		this.function = function;
		this.complete = false;
		this.result = new ArrayList<String>();		
	}
		
	public String getName() {
		return name;
	}
	
	public String getType() {
		return type;
	}
	
	public String getCondition() {
		return condition;
	}
	
	public ArrayList<FunctionDefinition> getFunction() {
		return function;
	}
	
	public boolean isComplete() {
		return complete;
	}
	
	public void setComplete(boolean complete) {
		this.complete = complete;
	}
	
	public ArrayList<String> getResult() {
		return result;
	}
	
	public String toString(){
		String content="PropertyDefinition: [Name]="+name+" [Type]="+type+" [Condition]="+condition+" [Complete]="+complete+" |Result]=";
		int size = result.size();
		for (int i = 0; i < size; i++) {
			content=content+"|"+result.get(i)+"|";
		}
		content=content+"\n";
		for(int i=0;i<function.size();i++)	{
			content=content+ "           - " +function.get(i).toString();
		}
		return content;
	}
	
	public String getErrorMsg() {
		return errorMsg;
	}
	
	private String name;
	private String type;
	private String errorMsg;
	private String condition;
	private ArrayList<FunctionDefinition> function;
	private ArrayList<String> result;

	private boolean complete;
}
