package it.unisa.di.weblab.localcontext.semantic;

import java.util.ArrayList;

public class PathStep {
	
	public PathStep(String graphicRef,ArrayList<Condition> condition) {
		this.graphicRef = graphicRef;
		this.condition = condition;
	}
	
	public String toString(){
		String content="\nPathStep: [GraphicRef]="+graphicRef;
		for(int i=0;i<condition.size();i++)	{
			content=content + condition.get(i).toString();
		}
		if(condition.isEmpty()) content=content + "\n";
		return content;
	}
	
	public String getGraphicRef() {
		return graphicRef;
	}
	
	public ArrayList<Condition> getCondition() {
		return condition;
	}

	public String graphicRef;
	public ArrayList<Condition> condition;

}
