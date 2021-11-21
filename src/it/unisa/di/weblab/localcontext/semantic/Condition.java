package it.unisa.di.weblab.localcontext.semantic;

import java.util.ArrayList;

public class Condition {
	
	public Condition(String cond) {
		this.cond= cond;
		this.verbose=false;
		createCondition();
	}
	
	private void createCondition() {
		ParsingSyntactic ps=new ParsingSyntactic(verbose);
		listToken = ps.expoldeCondition("["+cond+"]");
	}
	
	public int readSemanticDefinition(SemanticDefinition sd,ArrayList<Token> result){
		ParsingSyntactic ps=new ParsingSyntactic(verbose);
		return ps.readSemanticDefinition(listToken, sd, result);
	}
	
	public boolean execute(ArrayList<Token> result){
		ParsingSyntactic ps=new ParsingSyntactic(verbose);
		return ps.isTrueEvaluateCondition(result);
	}
	
	public boolean isConditionEdge(){
		
		if (listToken.size() > 2) {
			Token token = listToken.get(2);
			if (token.getType().equals("string")) {
				if (token.getResult().isEmpty()){
					return false;
				}
				
				if ((token.getResult().get(0).equalsIgnoreCase("follAttName")) || (token.getResult().get(0).equalsIgnoreCase("follAttType"))){
					return true;
				} else {
					return false;
				}
			}
		}
		return false;
	}
	
	public ArrayList<String> getConditionEdge(){
		ArrayList<String> list = new ArrayList<String>();
		for (Token token : listToken) {
			if (token.getType().equals("string")) {
				
				if (!token.getResult().isEmpty())	{				
					if ((token.getResult().get(0).equalsIgnoreCase("follAttName")) || (token.getResult().get(0).equalsIgnoreCase("follAttType"))){
						list.add(token.getResult().get(0));	
					} else {
						list.add(token.getResult().get(0));
					}
				}
			}
		}
		return list;
	}

	public String toString(){
		String content="\n - Condition ["+ cond +"]: [ListToken]=|"+listToken+"|";
		return content;
	}
		
	public void setVerbose(boolean verbose) {
		this.verbose=verbose;
	}
		
	public String cond;
	public ArrayList<Token> listToken;
	public boolean verbose;
}
