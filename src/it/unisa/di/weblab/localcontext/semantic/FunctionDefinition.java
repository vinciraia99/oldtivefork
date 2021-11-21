package it.unisa.di.weblab.localcontext.semantic;
/* La classe memorizza le funzioni
 * 
 * Variabili d'istanza:
 * name - Il Nome della funzione
 * path - Indica la path del grafo 
 * param - Il parametro delle funzione
 * 
 */

/**
 * 
 * @author Alfonso Ferraioli
 *
 */

public class FunctionDefinition {
	
	public FunctionDefinition(String name, String path,String param) {
		
		this.name = name;
		this.path = path;
		this.param = param;
		this.complete = false;
	}
	

	public String getName() {
		return name;
	}
	
	public String getPath() {
		return path;
	}
	
	public String getParam() {
		return param;
	}
	
	public void setParam(String param) {
		this.param=param;
	}
	
	public boolean isComplete() {
		return complete;
	}

	public void setComplete(boolean complete) {
		this.complete = complete;
	}
	
	public String toString(){
		String content="FunctionDefinition [Name]="+name+" [Path]="+path+" [Param]="+param+" [Complete]="+complete+"\n";
		return content;
	}
	
	private String name;
	private String path;
	private String param;
	private boolean complete;
}
