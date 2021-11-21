package it.unisa.di.weblab.localcontext.semantic;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

import org.jgrapht.graph.SimpleGraph;

import it.unisa.di.weblab.localcontext.GraphEdge;
import it.unisa.di.weblab.localcontext.Node;

/**
 * 
 * @author Alfonso Ferraioli
 *
 */

public class GraphSemantic {
	
	public GraphSemantic(SimpleGraph<Node, GraphEdge> gd) {
		
		this.verbose = false;
		this.gs = gd;
	}
	
	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	/**
	 * Metodo che serve per inizializzare i nodi. 
	 * Nello specifico per ogni nodo del grafo creo un oggetto SemanticDefinition vuoto.
	 *  
	 * @param parser oggetto che interagisce con il file XML di definizione semantico
	 */
	public void init(ParserXMLSemanticDefinition parser) {
		
		if (verbose) System.out.println("\n2) Inizializzo i nodi\n");
	
		ArrayList <Node> l = createListNode();
		for (int i=0;i<l.size();i++) {
			Node node = l.get(i);
			String id = node.getId();
			String rep = node.getSemanticRealGraphicType();
			SemanticDefinition sd =parser.getNode(rep);
			
			
			if (sd!=null) {
				sd.getProperty().get(0).getFunction().get(0).setParam(id);
				map.put(id, sd);
			}
			
			if (verbose) { 
				System.out.println("   Id: ["+id+"]");
				System.out.println("   RealGraphicType: ["+rep+"]");
				System.out.print("   ");
				if (sd==null)
					System.out.println("SemanticDefinition: null\n");
				else
					System.out.println(sd);
			}
			
		}
		
		Function.initStateFunction();
	}
	
	
	/**
	 * Metodo utilizzato per inizializzare le TextAreaDefinition degli oggetti SemanticDefinition. 
	 * Si esegue il type checking tra le TextArea definite dall'editor grafico e le TextAreaDefinition degli oggetti SemanticDefinition.  	 
	 * 
	 * @param texts
	 */
	public void initTextArea(HashMap<String, ArrayList<TextArea>> texts) {
		
		if (verbose) System.out.println("3) Eseguo il controllo dei tipi ed assegno i valori alle TextAreaDefinition degli oggetti SemanticDefinition \n");
		
		ArrayList <Node> l = createListNode();
		for (int i=0;i<l.size();i++) {
			
			Node node = l.get(i);
			String id = node.getId();
			String graphicType = node.getSemanticRealGraphicType();
						
			// tx contiene la lista delle aree di testo utilizzate nell'editor grafico
			ArrayList<TextArea> tx = null;
			if (texts.containsKey(id)){
				tx = texts.get(id);
			}
			
			// sm contiene la lista delle aree di testo definite dalla specifica semantica
			SemanticDefinition sm = null;
			if (map.containsKey(id)){
				sm = map.get(id);
				sm.setVerbose(verbose);
			}
			
			if (verbose) { 
				System.out.println("   Nodo Id=["+id+"] - RealGraphicType=["+graphicType+"] ");
				System.out.println("   Utilizza: "+tx);
				if (sm==null)
					System.out.println("   Definizione: null");
				else
					System.out.println("   Definizione: "+sm.toStringTextArea());
			}
						
			if ((tx!=null) && (sm==null)) {
				addProblem(id, ErrorMessagesSemantic.getSemanticErrorUndefined(node.getSemanticRealGraphicType()));
			} else if (((tx==null) && (sm!=null)) || ((tx!=null) && (sm!=null))) {
				if (sm.errorTypeCheck(tx)) {
					addProblem(id, sm.getProblems());
				}	
			}
			
			if (verbose) { 
				System.out.println("   Problemi: "+problems.get(id));
				if (sm!=null)
					System.out.println("   Result: "+sm.toStringTextArea());
				else
					System.out.println();
			}
		}
		
		if (verbose) {
			System.out.println("   Problemi Globali: "+problems);
		}
			
	}
	
	public void executeProperty() {
		
		if (verbose) System.out.println("4) Si inizia il calcolo delle propriet\340 \n");
		
		/*
		Set<Node> vertex = gs.vertexSet();
		Iterator<Node> it = vertex.iterator();
		while(it.hasNext())
		{
			Node node = it.next();
			System.out.println("Vertex [Id]: "+node.getId()+" -  [RealGraphicType]: "+node.getRealGraphicType()+"\n");
			Set<GraphEdge> edges = gs.edgesOf(node);
			Iterator<GraphEdge> iter = edges.iterator();
			while(iter.hasNext()){
				GraphEdge edg = iter.next();
				System.out.println("Edge: "+edg.getSource().getId()+" - "+edg.getTarget().getId()+"\n"+"Attacchings:\n");
				for(int k=0;k<edg.getAttacchings().size();k++)
				{
					System.out.println(edg.getAttacchings().get(k).getAp1()+" - "+edg.getAttacchings().get(k).getAp2()+" "+ edg.getAttacchings().get(k).getTypes()+"\n\n"); 
				}	
			}
			System.out.println("\n");
		}*/
		
		
		/*PathLocator pl = new PathLocator(this);
		pl.setVerbose(verbose);
		Set<Node> vertex = gs.vertexSet();
		Iterator<Node> it = vertex.iterator();
		while(it.hasNext()) {
			Node node = it.next();
			String id = node.getId();
			if (map.containsKey(id)){
				String ref = map.get(id).getRef();		
				if (ref.equals("entity")){
					
					PathResult pathResult = pl.executePath(node,ref, "/line1[@Tipo='']/attribute");
					
				}
			}
		}*/
		
		
		//System.out.println(gs);
		
		
		ArrayList <Node> l = getVisit();
		boolean deadlock = true;
		
		while (!l.isEmpty()) {
			
			if (verbose)
				System.out.println("   Lista di nodi da completare:"+ l);
			
			if (!deadlock){
				addProblem("Error", ErrorMessagesSemantic.getDeadlockError());
				return;
			}
			deadlock = false;
			ArrayList <Node> compleate = new ArrayList <Node>();
			
			
			for (Node node : l) {				
				String id = node.getId();
				
				PathLocator pl = new PathLocator(this);
				pl.setVerbose(this.verbose);
				
				if (verbose) System.out.println("\nNodo: "+node.getId());
				
				if (map.containsKey(id)) {
					SemanticDefinition sm = map.get(id);
					if (verbose) System.out.println("\n Prima: "+sm);
					if (!sm.isComplete()) {
						boolean stateSm = true;
						
						ArrayList<PropertyDefinition> property = sm.getProperty();
						if (!property.isEmpty()) {
							for (PropertyDefinition prop : property) {
								
								if (verbose) System.out.println(prop);
								
								boolean statePr = true;
								if (!prop.isComplete()) {
									
									ArrayList<FunctionDefinition> function = prop.getFunction();
									if (!function.isEmpty()) {
										for (FunctionDefinition func : function) {
											if (verbose) System.out.println(func);
											boolean stateFu = true;
											if (!func.isComplete()) {
												
												Function f= new Function(func.getName());												
												PathResult pathResult = pl.executePath(node,node.getSemanticRealGraphicType(), func.getPath());
												
												ArrayList<Node> nodeSels = pathResult.getNodeSelected();
												boolean statePath = pathResult.isComplete();
												boolean stateParam = true;
												
												if (verbose) System.out.println("\nNodi Selezionati: "+nodeSels);
												
												if (f.isComplete() && (!statePath)) {
													stateFu = false;
												} else {
													
													ArrayList <String> input = new ArrayList <String>();
													
													if (!nodeSels.isEmpty()) {
														
														if (!func.getParam().isEmpty()) {
															ParsingSyntactic ps=new ParsingSyntactic(verbose);
															if (verbose) System.out.println("Parametro: "+func.getParam());
															ArrayList<Token> listToken = ps.expoldeParam(func.getParam());
																											
															if (verbose) System.out.println("\nParametro Prima: "+listToken);
													
															for (Node nodeSel : nodeSels) {
																boolean stateParamNode = true; //MOD
																ArrayList<Token> resultToken = new ArrayList<Token>();
															
																SemanticDefinition sd = getSemanticDefinition(nodeSel.getId());
																if (sd!=null) {
																	int state1 = ps.readSemanticDefinition(listToken, sd, resultToken);
																	if (state1==0) {
																		stateParam = false;			// MOD
																		stateParamNode = false; //MOD
																	}
																	if (verbose) {
																		System.out.println("\nStato Read: "+state1);
																		System.out.println("\nParametro Completo: "+stateParam);
																		System.out.println("\nParametro Dopo: "+resultToken);
																	}
																}
														
																if (stateParamNode) { //MOD if (stateParam) {
																	ArrayList<Token> resultTokenValutation = ps.evaluate(resultToken);
																	String param = ""+func.getParam();
																	if (func.getName().equalsIgnoreCase("print")) {
																		input.add(ps.getStringEvaluateParam(param,resultTokenValutation));
																	} else {
																		input.addAll(ps.getResult(resultTokenValutation));
																	}
																}
															}						 
														} else {
															int size=nodeSels.size();
															for (int i =0;i<size;i++) {
																input.add(""+(i+1));									
															}
														}
													}
													
													if (f.isComplete() && (!stateParam)){
														stateFu = false;
													}
													
													if (stateFu) {
														if (this.verbose) {
															System.out.println("------PRIMA------------");											
															System.out.println("Input: ");														
															for (int i =0;i<input.size();i++) {
																System.out.println("|"+input.get(i)+"|");																								
															}
															
															System.out.println("Result:"+prop.getResult());
															System.out.println("-------------------------");
														}
														
														f.execute(input, prop.getResult());
														
														if (this.verbose) {
															System.out.println("--------DOPO-----------");
															System.out.println("Result:"+prop.getResult());
															System.out.println("-------------------------");
														}
													}
											}
												
											deadlock = deadlock || stateFu;
											func.setComplete(stateFu);
											statePr = statePr && stateFu;
										}
									}
								}
									
								prop.setComplete(statePr);
								stateSm = stateSm && statePr;
							}
							if (verbose) System.out.println(prop);
						}
					}
						
					if (stateSm) {
						sm.setComplete(stateSm);
						compleate.add(node);
					}
				}
				if (verbose) System.out.println("\n Dopo: "+sm);
			} else {
				compleate.add(node);
			}
				
		}
		
		if (verbose) System.out.println("   Lista di Completati:"+ compleate);
		l.removeAll(compleate);
		}
	}
	
	public void typeCheckStrctureAndPostCondition() {
		//boolean verbose  = true;
		Set<String> setSd = map.keySet();
		Iterator<String> it = setSd.iterator();
				
		while (it.hasNext()) {
			String id = it.next();
			SemanticDefinition sd = map.get(id);
			ArrayList<PropertyDefinition> property = sd.getProperty();
			if (!property.isEmpty()) {
				for (PropertyDefinition prop : property) {
					if (verbose) {
						System.out.println("----------------------------");
						System.out.println(prop);
					}
					
					if (!prop.getType().isEmpty()) {
						
						if (verbose) System.out.println(prop.getResult()+ " ----Type----> "+ prop.getType());
						
						Structure str = new Structure(prop.getType());
						if (!str.typeChecking(prop.getResult())) {
							if (verbose) System.out.println("La Propriet\340 "+ prop.getName() + " " +str.getError());
							addProblem(id,ErrorMessagesSemantic.getPropertyError(prop.getName(), str.getError()));
						} 
					} 
					
					if (!prop.getCondition().isEmpty()) {
						
						if (verbose) System.out.println("Condizione: "+prop.getCondition());
						
						PathLocator pl = new PathLocator(this);
						pl.setVerbose(verbose);
						ArrayList<PathStep> listPs = pl.explodePath(prop.getCondition(), "");
						if (verbose) System.out.println(listPs);
						
						PathStep ps = listPs.get(0);
						ArrayList<Condition> conds = ps.getCondition();
						ArrayList<Condition> conditions = new ArrayList<Condition>(); 
						if (conds.isEmpty()) {
							conditions.add(new Condition(ps.getGraphicRef()));
						} else {
							conditions = conds;
						}
						
						for (Condition condition : conditions) {
							condition.setVerbose(verbose);
							if (verbose) System.out.println(condition);
							ArrayList<Token> resultToken = new ArrayList<Token>(); 
							int state = condition.readSemanticDefinition(sd,resultToken);
							if (verbose) {
								if (state==0) System.out.println(" - Propriet\340 Non Completa");
								else if (state==1) System.out.println(" - Tutte le componenti sono state lette");
								else if (state==2) System.out.println(" - La Propriet\340 (o la TextArea) non \350 stata trovata");										
							}
											
							if (state==0){
								if (verbose) System.out.println(id + " la Condizione "+ prop.getCondition() + " ha delle propriet\340 non complete");
								if (prop.getErrorMsg().isEmpty()) {
									addProblem(id,ErrorMessagesSemantic.getConditionIncompleteError(prop.getCondition()));
								} else {
									addProblem(id,prop.getErrorMsg());
								}
								break;
							} else if (state==1) {	
								if (!condition.execute(resultToken)){
									if (verbose) System.out.println(id + " la Condizione "+ prop.getCondition() + " non viene rispettata");
									if (prop.getErrorMsg().isEmpty()) {
										addProblem(id,ErrorMessagesSemantic.getConditionFalseError(prop.getCondition()));
									} else {
										addProblem(id,prop.getErrorMsg());
									}
									break;
								}
							} else if(state==2){ //Nel Caso non trova la propriet\340
								if (verbose) System.out.println(id + " la Condizione "+ prop.getCondition() + " ha delle propriet\340 che non sono state trovate");
								if (prop.getErrorMsg().isEmpty()) {
									addProblem(id,ErrorMessagesSemantic.getConditionMissingError(prop.getCondition()));
								} else {
									addProblem(id,prop.getErrorMsg());
								}
								break;
							}
						}
					}
				}
			}
		}
	}
	
	/*
	private String executeParam(ArrayList<ConditionElement> read,String param){
		ArrayList<String> pred = new ArrayList<String> ();
		String parametro = "";
		
		if (read.isEmpty()) 
			return param;
		
		for (ConditionElement condEle : read) {
			if (condEle.getReadType().equals("string")) {
				
				ArrayList <String> resultFun = new ArrayList <String>();
				Function f = new Function("explode");
				f.execute(pred, resultFun);			
				 
				param = param.replace(condEle.getName(),resultFun.get(0));
				pred = new ArrayList<String> ();
				parametro = "";
			} else if (condEle.getReadType().equals("function")) {
				
				ArrayList <String> resultFun = new ArrayList <String>();
				Function f = null;
				if (parametro.isEmpty()) 
					f = new Function(condEle.getName());
				else 
					f = new Function(condEle.getName(),parametro);
				f.execute(pred, resultFun);
				pred.clear();
				pred.addAll(resultFun);
			} else if (condEle.getReadType().equals("param")) {
				parametro=condEle.getResult().get(0);
			} else {
				pred.addAll(condEle.getResult());				
			}	
		}
		return param;
	}*/
			
	/*private void createVisit(){
		Set<Node> setNodes = gs.vertexSet();
		Iterator<Node> iter = setNodes.iterator();
		while (iter.hasNext()) {
			listVisit.add(iter.next());
		}
	}*/
	
	private ArrayList<Node> createListNode(){
		ArrayList<Node> listLocal = new ArrayList<Node>(); 
		Set<Node> setNodes = gs.vertexSet();
		Iterator<Node> iter = setNodes.iterator();
		while (iter.hasNext()) {
			listLocal.add(iter.next());
		}
		return listLocal;
	}
	
	public void createVisit(ParserXMLSemanticDefinition parser) {
		final HashMap<String, VisitDefinition> visits = parser.getVisit();
		if (visits.isEmpty()) {
			listVisit.addAll(gs.vertexSet());
		} else {
			String[] orders = new String[visits.size()];
			for (Entry<String, VisitDefinition> e : visits.entrySet()) {
				int o = e.getValue().getOrder() - 1;
				if (o < orders.length && orders[o] == null)
					orders[o] = e.getKey();
				else
					addProblem(e.getKey(),
							ErrorMessagesSemantic.getVisitDefinitionError());
			}

			HashMap<String, Set<Node>> nN = new HashMap<String, Set<Node>>();
			for (Node node : gs.vertexSet()) {
				String key = node.getSemanticRealGraphicType().toLowerCase();
				Set<Node> nodeSet = nN.get(key);
				if (nodeSet == null) {
					nodeSet = new LinkedHashSet<Node>();
					nN.put(key, nodeSet);
				}
				nodeSet.add(node);
			}

			while (!nN.isEmpty()) {
				Node rem = null;
				for (String oname : orders) {
					Set<Node> nnodes = nN.get(oname);
					if (nnodes != null && !nnodes.isEmpty()) {
						rem = nnodes.iterator().next();
						if (nnodes.size() > 1)
							nnodes.remove(rem);
						else
							nN.remove(oname);
						break;
					}
				}
				if (rem == null) {
					Entry<String, Set<Node>> e = nN.entrySet().iterator()
							.next();
					Set<Node> nodes = e.getValue();
					rem = nodes.iterator().next();
					if (nodes.size() > 1)
						nodes.remove(rem);
					else
						nN.remove(e.getKey());
				}
				listVisit.add(rem);
				PathLocator pl = new PathLocator(this);
				ArrayList<Node> nodes = new ArrayList<Node>();
				try {
					followpath(rem, nodes, nN, pl, visits);
				} catch (ParseException e) {
					addProblem(e.getMessage(),
							ErrorMessagesSemantic.getPropretyVisitError());
					break;
				}
				listVisit.addAll(nodes);
				for (Node n : nodes) {
					String k = n.getSemanticRealGraphicType().toLowerCase();
					Set<Node> tns = nN.get(k);
					if (tns != null) {
						if (tns.size() > 1)
							tns.remove(n);
						else
							nN.remove(k);
					}
				}
			}
			Collections.sort(listVisit, new Comparator<Node>() {
				@Override
				public int compare(Node o1, Node o2) {
					VisitDefinition vd1 = visits
							.get(o1.getSemanticRealGraphicType().toLowerCase());
					VisitDefinition vd2 = visits
							.get(o2.getSemanticRealGraphicType().toLowerCase());
					return Integer.compare(vd1 == null ? 0 : vd1.getPriority(),
							vd2 == null ? 0 : vd2.getPriority());
				}
			});
		}

		if (this.verbose) {
			System.out.print("----------\nlistVist:");
			System.out.println(listVisit);
			System.out.println("----------");
		}
	}

	private static void followpath(Node nnode, ArrayList<Node> nodes,
			HashMap<String, Set<Node>> nN, PathLocator pl,
			HashMap<String, VisitDefinition> paths) throws ParseException {
		VisitDefinition vd = paths
				.get(nnode.getSemanticRealGraphicType().toLowerCase());
		if (vd == null)
			return;
		ArrayList<String> npaths = vd.getPaths();
		for (String npath : npaths) {
			PathResult res = pl.executePath(nnode,
					nnode.getSemanticRealGraphicType(), npath);
			if (!res.isComplete())
				throw new ParseException(nnode.getId(), 0);
			LinkedHashSet<Node> nds = new LinkedHashSet<Node>(
					res.getNodeSelected());
			nds.removeAll(nodes);
			for (Iterator<Node> it = nds.iterator(); it.hasNext();) {
				Node n = it.next();
				Set<Node> tns = nN
						.get(n.getSemanticRealGraphicType().toLowerCase());
				if (tns == null || !tns.contains(n))
					it.remove();
			}
			for (Node n : nds) {
				nodes.add(n);
				followpath(n, nodes, nN, pl, paths);
			}
		}
	}

	// a volte crea un ordine errato
	private void createVisitOld(ParserXMLSemanticDefinition parser){
		HashMap<String, VisitDefinition> visits = parser.getVisit();

		if (visits.isEmpty()){
			Set<Node> setNodes = gs.vertexSet();
			Iterator<Node> iter = setNodes.iterator();
			while (iter.hasNext()) {
				listVisit.add(iter.next());
			}			
		} else {
			ArrayList<String> orders = getOrders(visits);
			HashMap<String, ArrayList<Node>> mapGraph = createMapTypeGraph();
			
			if (this.verbose){
				System.out.println(orders);
				System.out.println("----------");
				System.out.println(mapGraph);
			}
			
			Stack<Node> select = new Stack<Node>();
			ArrayList <Node> compleate = new ArrayList <Node>();
			
			int numVertexVisit = gs.vertexSet().size();
			Node nodeExecute = null;
			boolean error = false;
			PathLocator pl = new PathLocator(this);
			pl.setVerbose(this.verbose);
			
			while (listVisit.size()!=numVertexVisit) {
				
				if (this.verbose){
					System.out.println("Nodi da Eseguire: "+select);
					System.out.println("Visitati: "+listVisit);
				}
				
				boolean findOrder = false;
				if (select.isEmpty()) {
					while (!findOrder) {
						
						if (orders.isEmpty()){
							findOrder = true; // Ho finito la ricerca perch\351 ho visitato tutti i nodi
						} else {
							String type = orders.get(0);
							ArrayList<Node> listNode = mapGraph.get(type);
							
							if (this.verbose){
								System.out.println("Tipo Selez: "+type);
								System.out.println("Lista Nodi: "+listNode);
								System.out.println("Nodi Compl: "+compleate);
							}
							
							if(listNode != null) for (int i=0;i<listNode.size();i++) {
								Node nodeVisit = listNode.get(i);
								
								if (!compleate.contains(nodeVisit)){
									findOrder = true;
									nodeExecute = nodeVisit;
									compleate.add(nodeVisit);
									i=listNode.size();
								}
							}
							
							if (!findOrder){
								orders.remove(0);
							} 
						}
						
					}
					if (nodeExecute==null){
						break;					
					}
				} else {
					
					boolean findStack = false;
					while ((select.size()>0) && (!findStack)){
						Node popNode = select.pop();
						if (!compleate.contains(popNode)){
							nodeExecute = popNode;
							compleate.add(popNode);
						}
					}
				}
				
				if (this.verbose){
					System.out.println("NodeExec");
					System.out.println(nodeExecute);
				}
				
				if (nodeExecute!=null) {
					
					listVisit.add(nodeExecute);
					VisitDefinition vis = visits.get(nodeExecute.getSemanticRealGraphicType().toLowerCase());
					if (vis!=null){
						ArrayList<String> paths = vis.getPaths();
						
						for (int j = paths.size()-1; j >=0; j--) {
							String path = paths.get(j);
							PathResult pathResult = pl.executePath(nodeExecute,nodeExecute.getSemanticRealGraphicType(), path);						
							if (pathResult.isComplete()) {
								ArrayList<Node> nodeSels = pathResult.getNodeSelected();
								for (Node nodeResPath : nodeSels) {
									if (!compleate.contains(nodeResPath)){
										select.push(nodeResPath);
									}
								}
							} else {
								error = true;
							}
						}
					} 
				
					if (error) {
						addProblem(nodeExecute.getId(), ErrorMessagesSemantic.getPropretyVisitError());
						break;
					}
				}
			}
			
			if (this.verbose){
				System.out.println("----------\n");
				System.out.println("listVisit: " + listVisit);
				System.out.println("----------");
			}
			
			int maxPr = getMaxPriority(visits);
			for (int k=2;k<=maxPr;k++){
				 ArrayList<String> types = getPrioriry(visits,k);
				 ArrayList<Node> nodes= new ArrayList<Node>();
				 ArrayList<Node> delete = new ArrayList<Node>();
				 for (Node nd : listVisit) {
					if (types.contains(nd.getSemanticRealGraphicType().toLowerCase())){
						nodes.add(nd);
						delete.add(nd);
					}
				}
				listVisit.removeAll(delete);
				listVisit.addAll(nodes);
			}
		}
		
		if (this.verbose){
			System.out.println("----------");
			System.out.println(listVisit);
			System.out.println("----------");
		}

	}
	
	private HashMap<String, ArrayList<Node>> createMapTypeGraph(){
		HashMap<String, ArrayList<Node>> mapGraph = new HashMap<String, ArrayList<Node>>();
			Set<Node> setNodes = gs.vertexSet();
			Iterator<Node> iter = setNodes.iterator();
			while (iter.hasNext()) {
				Node node = iter.next();
				String key = node.getSemanticRealGraphicType().toLowerCase();
				if (mapGraph.containsKey(key)){
					ArrayList<Node> listNode = mapGraph.get(key);
					listNode.add(node);
				} else {
					ArrayList<Node> listNode = new ArrayList<Node>(); 
					listNode.add(node);
					mapGraph.put(key, listNode);
				}
			}	
		return mapGraph;
	}
	
	private int getMaxPriority(HashMap<String, VisitDefinition> visits){
		Iterator<String> it = visits.keySet().iterator();
		int max = -1;
		while (it.hasNext()) {
			String key = it.next().toLowerCase();
			VisitDefinition vs = visits.get(key);
			if (max < vs.getPriority()) {
				max = vs.getPriority();
			}
		}
		return max;
	}
	
	private ArrayList<String> getPrioriry(HashMap<String, VisitDefinition> visits,int prior){
		Iterator<String> it = visits.keySet().iterator();
		ArrayList<String> priority = new ArrayList<String>();
		while (it.hasNext()) {
			String key = it.next().toLowerCase();
			VisitDefinition vs = visits.get(key);
			if (vs.getPriority()==prior) {
				priority.add(key.toLowerCase());
			}
		}		
		return priority;
	}

	
	private ArrayList<String> getOrders(HashMap<String, VisitDefinition> visits){
		Iterator<String> it = visits.keySet().iterator();
		
		int size = visits.size();
		ArrayList<String> orders = new ArrayList<String>();
		for (int i = 0; i < size; i++) {
			orders.add("");
		}
		
		while (it.hasNext()) {
			String key = it.next().toLowerCase();
			VisitDefinition vs = visits.get(key);
			orders.set(vs.getOrder()-1, key.toLowerCase());
			if (this.verbose){
				System.out.println("Key: "+ key + " Visit: "+ vs);
			}
		}
		
		return orders;
	}
	
	
	
	private ArrayList<Node> getVisit(){
		return (ArrayList<Node>) listVisit.clone();
	}
	
	public void toStringVisit() {
		ArrayList <Node> l = getVisit();
		for (int i=0;i<l.size();i++) {
			Node node = l.get(i);
			System.out.println(node.toString());
		}
	}
		
	public void addProblem(String key,String value){
		ArrayList<String> messages = null;
		
		if(!problems.containsKey(key)) {
			messages = new ArrayList<String>();
		}
		else {
			messages = problems.get(key);
		}
		
		messages.add(value);
		problems.put(key,messages);
	}
	
	public void addProblem(String key,ArrayList<String> value){
		ArrayList<String> messages = null;
		
		if(!problems.containsKey(key)) {
			messages = new ArrayList<String>();
		}
		else {
			messages = problems.get(key);
		}
		
		messages.addAll(value);
		problems.put(key,messages);
	}
	
	public SimpleGraph<Node, GraphEdge> getSimpleGraph() {
		return gs;
	}
	
	public HashMap<String, ArrayList<String>> getProblems() {
		return problems;
	}
	
	public SemanticDefinition getSemanticDefinition(String idNode){
		if (map.containsKey(idNode)){
			return map.get(idNode);
		}
		return null;
	}
	
	public String getResult(){
		String result = "";
		ArrayList <Node> l = getVisit();
		
		for (int i=0;i<l.size();i++) {
			Node node = l.get(i);
			String id = node.getId();
			if (map.containsKey(id)){
				SemanticDefinition sd = map.get(id);
				ArrayList<PropertyDefinition> props = sd.getProperty();
				if (!props.isEmpty()){
					for (PropertyDefinition prop : props) {
						if (prop.getName().isEmpty()){
							ArrayList<String> results = prop.getResult();
							for (String string : results) {
								result = result + string + "\n";
							}						
						}
					}
				}
			}			
		}
		
		return result;
	}
	
	private boolean verbose;
	private SimpleGraph<Node, GraphEdge> gs;
	private ArrayList<Node> listVisit = new ArrayList<Node>();
	private HashMap<String, SemanticDefinition> map = new HashMap<String, SemanticDefinition>();
	private HashMap<String,ArrayList<String>> problems = new HashMap<String,ArrayList<String>>();
	
}
