package it.unisa.di.weblab.localcontext.semantic;

import java.util.ArrayList;

import it.unisa.di.weblab.localcontext.Node;

public class PathResult {
	
	public PathResult(boolean complete, ArrayList<Node> nodeSelected) {
		this.complete=complete;
		this.nodeSelected = nodeSelected;
	}
	
	public boolean isComplete() {
		return complete;
	}
	
	public ArrayList<Node> getNodeSelected() {
		return nodeSelected;
	}

	private boolean complete;
	private ArrayList<Node> nodeSelected;
}
