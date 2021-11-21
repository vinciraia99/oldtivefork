package it.unisa.di.weblab.localcontext.semantic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jgrapht.graph.SimpleGraph;

import it.unisa.di.weblab.localcontext.GraphEdge;
import it.unisa.di.weblab.localcontext.Node;

public class PathLocator {
	
	public PathLocator(GraphSemantic gs) {
		
		this.gs = gs;
		this.verbose = false;
	}
	
		
	public PathResult executePath(Node start,String graphicRef,String path) {

		ArrayList<Node> visit = new ArrayList<Node>();
		ArrayList<Node> allVisit = new ArrayList<Node>();
		ArrayList<Node> toVisit = new ArrayList<Node>();
		ArrayList<Node> select = new ArrayList<Node>();
		toVisit.add(start);
		
		boolean statePath = true;
		
		SimpleGraph<Node, GraphEdge> graph = gs.getSimpleGraph();
		ArrayList<PathStep> result = explodePath(path,graphicRef);
		
		if (verbose) {
			System.out.println("Path Explode");
			System.out.println(result);
		}
		
		int size = result.size();
		for(int i=0;i<size;i++) {
			PathStep pathStep = result.get(i);
			
			if (verbose) {
				System.out.println("*********** Inizio *************");
				System.out.println("Step["+ i +"]: GraphicRef=["+pathStep.getGraphicRef()+"] - Condition="+pathStep.getCondition());
				System.out.println("--------------------------------");
				System.out.println("Da Visitare: "+toVisit);
				System.out.println("Tutti i Visitati: "+allVisit);
				System.out.println("--------------------------------");
			}
			
			for (Node node : toVisit) {
				if (verbose) System.out.println("Node [Id]: "+node.getId()+" -  [RealGraphicType]: "+node.getRealGraphicType());
				
				if (graph.containsVertex(node)) {
					String gr =  node.getSemanticRealGraphicType();
					
					if (verbose) {
						System.out.println("["+gr+"]="+"["+pathStep.getGraphicRef()+"]");
					}
					
					if ((gr.equalsIgnoreCase(pathStep.getGraphicRef())) || (pathStep.getGraphicRef().equalsIgnoreCase("*"))) {
							ArrayList<Condition> condTypeEdge = new ArrayList<Condition> (); 
							ArrayList<Condition> condition = pathStep.getCondition();
							boolean stateCondition = true;
							
							if (!condition.isEmpty()) {
								SemanticDefinition sd = gs.getSemanticDefinition(node.getId());
								if (sd!=null) {
									for (Condition cond : condition) {
									
										if (cond.isConditionEdge()){
											condTypeEdge.add(cond);
										} else {
										
											ArrayList<Token> resultToken = new ArrayList<Token>(); 
											int state = cond.readSemanticDefinition(sd,resultToken);
											if (verbose) {
												if (state==0) System.out.println(" - Proprietà Non Completa");
												else if (state==1) System.out.println(" - Tutte le componenti sono state lette");
												else if (state==2) System.out.println(" - La Proprietà (o la TextArea) non è stata trovata");										
											}
										
											if (state==0){
												statePath=false;
												stateCondition = false;
											} else if (state==1) {	
												stateCondition = cond.execute(resultToken);
											} else if(state==2){ //Nel Caso non trova la proprietà
												stateCondition = false;
											}
																				
											if (!stateCondition) break;
										}
									}
									if (verbose) {
										System.out.println("\n * Condizione: "+stateCondition);
										System.out.println("\n * Condizione Archi: "+condTypeEdge);
									}
								} else {
									if (verbose) System.out.println("Il Nodo non ha una Definizione Semantica!!!");
									stateCondition = false;
								}
							}
																				
					if (stateCondition) {
						if (i!=size-1) {
							Set<GraphEdge> edges = graph.edgesOf(node);
							Iterator<GraphEdge> iter = edges.iterator();
							while(iter.hasNext()) {
								GraphEdge edg = iter.next();
								boolean selectEdge = true;
								if (verbose) System.out.println("\nEdge: "+edg.getSource().getId()+" - "+edg.getTarget().getId()+"\n"+"Attacchings:");
								
								for (int k=0;k<edg.getAttacchings().size();k++)	{
									if (verbose) System.out.println(edg.getAttacchings().get(k).getAp1()+" - "+edg.getAttacchings().get(k).getAp2()+" "+ edg.getAttacchings().get(k).getTypes());
									if (!condTypeEdge.isEmpty()) {
										for (Condition condType : condTypeEdge) {
											ArrayList<String> input1 = new ArrayList<String>();
											ArrayList<String> input2 = new ArrayList<String>();
											
											ArrayList<String> listString = condType.getConditionEdge();
											String type = listString.get(0);
											input1.add(listString.get(1));
											
											if (verbose) System.out.println("Type: "+type+" - "+"Input:"+ input1);
											
											if (type.equalsIgnoreCase("follAttType")) {												
												input2.add(edg.getAttacchings().get(k).getTypes().iterator().next());
											} else {
												if (node.isConnector())
													input2.add(edg.getAttacchings().get(k).getAp2());
												else
													input2.add(edg.getAttacchings().get(k).getAp1());
											}
											Operation o = new Operation(verbose);
											selectEdge = selectEdge && o.operationExecute(input1, "=",input2);
										}
									}
								}
								
								if (verbose) System.out.println("Arco Selezionato:" + selectEdge);
							
								if (selectEdge) {
									Node nodeSel = edg.getSource(); 
									if (edg.getSource().getId().equals(node.getId())) {
										nodeSel = edg.getTarget();						
									}
									
									if (!allVisit.contains(nodeSel)) {
										visit.add(nodeSel);
									}
								}
								
							}
						} else {
							select.add(node);
						}
					}
					}
					allVisit.add(node);
				}
			}
			
			if (verbose) {
				System.out.println("*********** FINE ***************");
				System.out.println("Step: "+i);
				System.out.println("Visitati: "+toVisit);
				System.out.println("Da Visitare: "+visit);
				System.out.println("Tutti i Visitati: "+allVisit);
				System.out.println("--------------------------------");
			}
			
			toVisit.clear();
			toVisit.addAll(visit);
			visit.clear();
			
			if (toVisit.isEmpty()){
				break;
			}
		}
		
		if (verbose) {
			System.out.println("+++++++++++ RESULT +++++++++++++++");
			System.out.println("Selezionati: "+select);
			System.out.println("++++++++++++++++++++++++++++++++++");
		}
		
		
		return new PathResult(statePath, select);
	}
	
	
//	public ArrayList<PathStep> explodePath(String path,String graphicRefInit){
//		
//		ArrayList<PathStep> ps = new ArrayList<PathStep>();
//		
//		if (!path.isEmpty()) {
//			String[] passi = path.split("/");
//			int size = passi.length;
//			for (int i = 0;i<size;i++) {
//				String passo = passi[i];
//				
//				String graphicRef = passo;
//				ArrayList<Condition> condition = new ArrayList<Condition>();
//				
//				if (passo.isEmpty()) {
//					graphicRef=graphicRefInit;
//				} else if (passo.contains("[")) {
//					
//		    		String[] array = passo.split("\\[");
//		    		graphicRef=array[0];
//		    		
//		    		String cond = "";
//		    		if (!graphicRef.isEmpty()) {
//		    			String[] array1 = passo.split(graphicRef);
//		    			cond=array1[1];
//		    		} else {
//		    			graphicRef=graphicRefInit;
//		    			cond=passo;
//		    		}
//		    		
//		    		String[] array2 = null;
//		    		String concat = "";
//		    		if (cond.contains("'")){
//		    			array2 = cond.split("']");
//		    			concat = "'";
//		    		} else {
//		    			array2 = cond.split("]");
//				    }
//		    		
//		    		int sizeCond = array2.length;
//		    		for(int j =0;j<sizeCond;j++){
//		    			Condition con = new Condition(array2[j].substring(1)+concat);
//		    			condition.add(con);
//		    			con.setVerbose(verbose);
//		    		}		
//		    	}		
//		    	ps.add(new PathStep(graphicRef, condition));
//			}
//		} else {
//			ps.add(new PathStep(graphicRefInit, new ArrayList<Condition>()));
//		}
//		
//		/*      
//		Pattern pattern = Pattern.compile("([^/]+)");
//	    Matcher matcher = pattern.matcher("/line1/generalization[@Tipo='[01]:1'][@Tipo='[01]:1']/touch/entity");
//	    while(matcher.find()) {
//	    	String passo = matcher.group(0);
//	    	String ref = passo;
//	    	if (ref.contains("[")) {
//	    		 String[] array = ref.split("\\[");
//	    		 ref=array[0];
//	    	}
//	    	/*System.out.println("Ent/Sym : "+ref);
//	    	System.out.println("Passo : "+passo);
//	    	Pattern patternCond = Pattern.compile("\\[(.)+\\]");
//		    Matcher matcherCond = patternCond.matcher(passo);
//		    while(matcherCond.find()) {
//		    	System.out.println("Cond : "+matcherCond.group(0));
//		    }
//	    }*/
//	    
//	    return ps;
//	}
	
	public ArrayList<PathStep> explodePath(String path,String graphicRefInit){
		
		ArrayList<PathStep> ps = new ArrayList<PathStep>();
		path = graphicRefInit+ path;
		
		Pattern pattern = Pattern.compile("[^\\/]*[^\\/]");
		Matcher matcher = pattern.matcher(path);
		while (matcher.find()) {
			String contenutoPath = matcher.group();

			if (verbose) System.out.println("Match: "+contenutoPath);
			
			if (contenutoPath.contains("[") && contenutoPath.contains("]")) {
				String[] array = contenutoPath.split("\\[");
				String graphicRef=array[0];
				
				ArrayList<Condition> condition = new ArrayList<Condition>();
    			String[] array1 = contenutoPath.split(graphicRef);
    			String cond=array1[1];
    			
    			ArrayList<String> contenutoString = new ArrayList<String>();
    			Pattern patternCond = Pattern.compile("\\'([^\\']*)\\'");
				Matcher matcherCond = patternCond.matcher(cond);
    			while (matcherCond.find()) {
    				contenutoString.add(matcherCond.group());
    			} 
    			
    			//if (verbose) System.out.println("Stringhe: "+contenutoString);
    			
    			String condReplace= cond.replaceAll("\\'([^\\']*)\\'", "03d85a0d629d2c442e987525319fc471");
    			
    			//if (verbose) System.out.println("Cond Replace: "+condReplace);
    			
    			String[] array2 = condReplace.split("\\]");
	    		int sizeCond = array2.length;
	    		for(int j =0;j<sizeCond;j++){
	    			String condS = array2[j];
	    			if (condS.contains("03d85a0d629d2c442e987525319fc471")){
	    				condS = condS.replace("03d85a0d629d2c442e987525319fc471", contenutoString.get(0));
	    				contenutoString.remove(0);
	    			}
	    			//if (verbose) System.out.println("Cond" + (j+1) +": "+condS.substring(1));
	    			Condition con = new Condition(condS.substring(1));
	    			condition.add(con);
	    		}	
    			
    			ps.add(new PathStep(graphicRef, condition));		
			} else {
				ps.add(new PathStep(contenutoPath, new ArrayList<Condition>()));
			}
		}
				
	    return ps;
	}
		

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}
	 
	private GraphSemantic gs;
	private boolean verbose;

}
