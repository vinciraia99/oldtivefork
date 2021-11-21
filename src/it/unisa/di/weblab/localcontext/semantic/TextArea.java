package it.unisa.di.weblab.localcontext.semantic;
/* La classe memorizza le aree di testo del disegno in input 
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

public class TextArea {
	
	public TextArea(String graphicRef, String value) {
		
		this.graphicRef = graphicRef;
		this.value = value;
	}
		
	public String toString(){
		String content="TextArea: [GraphicRef]="+graphicRef+" [Value]="+value;
		return content;
	}
	
	public String getValue() {
		return value;
	}
	
	public String getGraphicRef() {
		return graphicRef;
	}

	private String graphicRef;
	private String value;
}
