package it.unisa.di.weblab.localcontext.semantic;

/**
 * 
 * @author Alfonso Ferraioli
 *
 */

public class ErrorMessagesSemantic {
	private static final boolean DEBUG = true;
	
	private ErrorMessagesSemantic() {
	}

	
	/*
	 * Errore generato quando i valori delle aree di testo non rispettono la struttura
	 * Il simbolo non e' usato correttamente. 
	 * Usato nella Classe GraphSemantic
	 * 
	 */
	public static String getGenericError(){
		return "The symbol or connector is not used correctly";
	}
	
	/*
	 *  
	 * Usato nella Classe GraphSemantic
	 * 
	 */
	public static String getPropretyVisitError(){
		return "You can not use the properties in the definition of the visit";
	}
	
	/*
	 * Nella specifica semantica e' stato rilevato un deadlock
	 * Usato nella Classe GraphSemantic
	 * 
	 */
	public static String getDeadlockError (){
		return "In the semantic specification is detected deadlock";
	}
	
	/*
	 * Il valore contenuto nell'area di testo non rispetta il tipo 
	 * Usato nella Classe SemanticDefinition
	 * 
	 */
	public static String getTextAreaValueError (String type){
		return "The value contained in the text does not respect the type "+type;
	}
	
	/*
	 * Nella specifica non e' stata definita un'area di testo legata al riferimento grafico.
	 * Usato nella Classe SemanticDefinition
	 * 
	 */
	public static String getGraphicRefErrorUndefined (String graphicRef){
		return "In the specification is not defined text area connected to "+graphicRef;
	}
	
	/*
	 * Per il nodo non e' possibile associare del testo.
	 * Usato nella Classe GraphSemantic
	 * 
	 */
	public static String getSemanticErrorUndefined (String nameSymbol){
		return "For " + nameSymbol +" it is not possible to associate the text";
	}

	public static String getConditionIncompleteError(String condition) {
		return DEBUG ? "The post-condition " + condition
				+ " uses incomplete properties" : getGenericError();
	}

	public static String getConditionFalseError(String condition) {
		return DEBUG
				? "The post-condition " + condition + " has not been satisfied"
				: getGenericError();
	}

	public static String getConditionMissingError(String condition) {
		return DEBUG
				? "The post-condition " + condition + " uses missing properties"
				: getGenericError();
	}

	public static String getPropertyError(String name, String error) {
		return DEBUG ? "Error on propery " + name + ": " + error
				: getGenericError();
	}

	public static String getVisitDefinitionError() {
		return DEBUG ? "Visit definition error: duplicate order"
				: getGenericError();
	}

}
