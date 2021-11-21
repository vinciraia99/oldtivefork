package it.unisa.di.weblab.localcontext;

/**
 * 
 * @author Alfonso Ferraioli
 *
 */

public class ErrorMessages {
	
	public ErrorMessages() {
	}


	/*
	 *
	 * The graph is not connected 
	 * Usato nella Classe Tester
	 * 
	 */
	public String getDiagramNotConnectedError(){
		return "The diagram is not connected";
	}
	
	/*
	 *
	 * Ambiguity still present because the constraints are satisfied for more than possible types of symbol (or connector) 
	 * Usato nella Classe GraphDisambiguation
	 * 
	 */
	public String getAmbiguousError(boolean isConnector){
		String str = "symbol";
		if (isConnector){
			str = "connector";
		}
		return "The "+str+" is not used correctly";
	}
	
	/*
	 *
	 * Ambiguity still present because the constraints are satisfied for the different types of the symbol (or connector) using more than one of the possible combinations of the types present on the edges 
	 * Usato nella Classe GraphDisambiguation
	 * 
	 */
	public String getAmbiguousMoreCombinationsError(boolean isConnector){
		String str = "symbol";
		if (isConnector){
			str = "connector";
		}
		return "The "+str+" is not used correctly";
	}
	
	/*
	 *
	 * Unable to disambiguate the node because they are not respected the constraints for any possible type of symbol (or connector) 
	 * Usato nella Classe GraphDisambiguation
	 * 
	 */
	public String getConstraintsNotSatisfiedError(boolean isConnector){
		String str = "symbol";
		if (isConnector){
			str = "connector";
		}
		return "The "+str+" is not used correctly";
	}
	
	/*
	 *
	 * The occurences of symbol entity must be > 1 
	 * Usato nella Classe GraphDisambiguation
	 * 
	 */
	public String getOccurencesError(String name,String op,int number){
		return "The occurences of symbol "+ name +" must be "+op+" "+number;
	}
		
	
}
