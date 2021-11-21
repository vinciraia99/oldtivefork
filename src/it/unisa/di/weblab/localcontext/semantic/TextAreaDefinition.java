package it.unisa.di.weblab.localcontext.semantic;
/* La classe memorizza le aree di testo
 * 
 * Variabili d'istanza:
 * name - Nome nodo
 * type - Rappresenta il tipo
 * 
 */

/**
 * 
 * @author Alfonso Ferraioli
 *
 */

public class TextAreaDefinition {
	
	public TextAreaDefinition(String graphicRef,String name, String type,String errorMsg) {
		
		this.graphicRef = graphicRef;
		this.name = name;
		this.type = type;
		this.errorMsg = errorMsg;
		this.result = new String();	
	}
	
	public String getName() {
		return name;
	}
	
	public String getType() {
		return type;
	}
	
	public String getResult() {
		return result;
	}
	
	public void setResult(String result) {
		this.result = result;
	}
	
	public String getGraphicRef() {
		return graphicRef;
	}
	
	public String getErrorMsg() {
		return errorMsg;
	}
	
	public String toString(){
		String content="TextAreaDefinition: [GraphicRef]="+graphicRef+" [Name]="+name+" [Type]="+type+" [Result]="+result+" \n";
		return content;
	}

	private String graphicRef;
	private String name;
	private String type;
	private String errorMsg;
	private String result;
}
