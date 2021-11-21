package it.unisa.di.weblab.localcontext.semantic;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParsingSyntactic {
	
	private boolean verbose;
	
	public ParsingSyntactic(boolean verbose) {
		this.verbose=verbose;
	}
		
	public boolean isTrueEvaluateCondition(ArrayList<Token> listToken){
		
		listToken = evaluateConditions(listToken);
		if (verbose) {
			System.out.println("Risultato Esecuzione Condizioni");
			System.out.println(listToken);
		}
		
		boolean result = false;
		for (Token token : listToken) {
			if ((!token.getType().equals("closeblock")) && (!token.getType().equals("block"))) {
				result = Boolean.valueOf(token.getResult().get(0));
			}
		}
		
		return result;
	}
	
	public ArrayList<Token> evaluate(ArrayList<Token> listToken){
		
		ArrayList<Token> list = new ArrayList<Token>(); 
		
		list = evaluateConditions(listToken);
		if (verbose) {
			System.out.println("Risultato Esecuzione Condizioni");
			System.out.println(list);
		}
		
		list = evaluateFunctions(list);
		if (verbose) {
			System.out.println("Risultato Esecuzione Funzioni");
			System.out.println(list);
		}
		
		return list;
	}
	
	public String getStringEvaluateParam(String param, ArrayList<Token> listToken){
	
		ArrayList<String> input = new ArrayList<String> ();
		for (Token token : listToken) {
			if (token.getType().equals("closeblock")){
				
				String rep ="";
				if (input.size()==1) {
					rep = input.get(0);
				} if (input.size()>1) {
					ArrayList <String> resultFun = new ArrayList <String>();
					Function f = new Function("explode");
					input.add(0,", ");
					f.execute(input, resultFun);
					rep = resultFun.get(0);
				}
				
				param = param.replace(token.getName(),rep);
			} else if (!token.getType().equals("block")){
				input = token.getResult();
			}
		}
		
		return param;
	}
	
	public ArrayList<String> getResult(ArrayList<Token> listToken){
		
		for (Token token : listToken) {
			if ((!token.getType().equals("closeblock")) && (!token.getType().equals("block"))) {
				return token.getResult();
			}
		}
		return null;
	}
	
	public int readSemanticDefinition(ArrayList<Token> listToken,SemanticDefinition sd,ArrayList<Token> resultToken){
		
		for (Token t : listToken) {
			ArrayList<String> l = new ArrayList<String>();
			Token newToken = new Token(t.getType(), t.getName());
			if (t.getType().equalsIgnoreCase("textarea")) {
				
				TextAreaDefinition ta = sd.getTextAreaName(t.getName());
				if (ta==null) {
					newToken.setResult(null);
					return 2;
				}
				l.add(ta.getResult());
				newToken.setResult(l);
				
			} else if (t.getType().equalsIgnoreCase("property")) {
				
//				System.out.println("-------------------");
//				System.out.println(t.getName());
//				System.out.println(sd.getPropertyName(t.getName()));
//				System.out.println("----------------------------");
				
				
				
				PropertyDefinition p = sd.getPropertyName(t.getName());
				if (p==null) {
					newToken.setResult(null);
					return 2;
				} else if (p.isComplete()) {
					ArrayList<String> clone = (ArrayList<String>) p.getResult().clone();
					newToken.setResult(clone);
				} else {
					return 0;
				}
				
			} else {
				newToken.setResult(t.getResult());
			} 
			
			resultToken.add(newToken);
		}
		return 1;
	}
		
	private ArrayList<Token> evaluateConditions(ArrayList<Token> listToken) {
		ArrayList<Token> list = new ArrayList<Token>();
		int i=0;
		int size = listToken.size();
		while (i < size) {
			Token t = listToken.get(i);
//			System.out.println(t);
//			System.out.println("i: "+ i);
//			System.out.println("size: "+ size);
			
			if (t.getType().equalsIgnoreCase("condition")) {
				int j=i+1;
				ArrayList<Token> removeCondition = new ArrayList<Token>();
				ArrayList<Token> cond1 = new ArrayList<Token>();
				Token operation = null ;
				ArrayList<Token> cond2 = new ArrayList<Token>();
				boolean findOp = false;
				removeCondition.add(t);
				while (j < size) {
//					System.out.println("j: "+ j);
//					System.out.println("size: "+ size);
					Token temp = listToken.get(j);
					removeCondition.add(temp);
					if (temp.getType().equals("closecondition")){
						break;
					}
					
					if (temp.getType().equals("operation")){
						operation=temp;
						findOp=true;
					} else if (!findOp) {
						cond1.add(temp);
					} else {
						cond2.add(temp);
					}
					j++;
				}
				
				ArrayList<String> result = new ArrayList<String>();
				Token res = new Token("string","");
				result.add(executeCondition(cond1,operation,cond2));
				
				if (this.verbose) {
					System.out.println("Result: "+result);
				}
				
				res.setResult(result);
				list.add(res);
				listToken.removeAll(removeCondition);
//				System.out.println("List token Remove: "+ listToken);
				size = listToken.size();
			} else {
				i++;
				list.add(t);
			}
		}
		
		return list;
	}
	
	private ArrayList<Token> evaluateFunctions(ArrayList<Token> listToken) {
		ArrayList<Token> list = new ArrayList<Token>();
		int i=0;
		int size = listToken.size();
		while (i < size) {
			Token t = listToken.get(i);
//			System.out.println(t);
//			System.out.println("i: "+ i);
//			System.out.println("size: "+ size);
			
			if (t.getType().equalsIgnoreCase("function")) {
				int j=i+1;
				ArrayList<Token> removeCondition = new ArrayList<Token>();
				ArrayList<Token> input = new ArrayList<Token>();
				removeCondition.add(t);
				input.add(t);
				while (j < size) {
//					System.out.println("j: "+ j);
//					System.out.println("size: "+ size);
					Token temp = listToken.get(j);
					removeCondition.add(temp);
					input.add(temp);
					if (temp.getType().equals("closefunction")){
						break;
					}
					j++;
				}
				
				ArrayList<String> result = executeFunction(input);
				
				if (this.verbose) {
					System.out.println("Result: "+result);
				}
				
				Token res = new Token("string","");
				res.setResult(result);
				list.add(res);
				listToken.removeAll(removeCondition);
//				System.out.println("List token Remove: "+ listToken);
				size = listToken.size();
			} else {
				i++;
				list.add(t);
			}
		}
		
		return list;
	}
	
	private String executeCondition(ArrayList<Token> cond1,Token operation,ArrayList<Token> cond2) {
		
		if (this.verbose) {
			System.out.println("Condizione 1");
			System.out.println(cond1);
			System.out.println("Operazione");
			System.out.println(operation);
			System.out.println("Condizione 2");
			System.out.println(cond2);	
		}
		
		ArrayList<String> input1;
		if (cond1.get(0).getType().equals("function")){
			input1 = executeFunction(cond1);
		} else {
			input1 = cond1.get(0).getResult();
		}
	
		ArrayList<String> input2;
		if (cond2.get(0).getType().equals("function")){
			input2 = executeFunction(cond2);
		} else {
			input2 = cond2.get(0).getResult();
		}
		
		Operation o = new Operation(verbose);
		return String.valueOf(o.operationExecute(input1, operation.getName(), input2));
	}

	private ArrayList<String> executeFunction(ArrayList<Token> tokens){
		
		if (this.verbose) {
			System.out.println("Funzione");
			System.out.println(tokens);	
		}
		
		ArrayList <String> input = new ArrayList <String>();
		ArrayList <String> result = new ArrayList <String>();
		String functionName = "";
		
		for (Token token : tokens) {
			if (token.getType().equals("function")){
				functionName = token.getName();
			} else if (!token.getType().equals("closefunction")){
				input.addAll(token.getResult());
			}
		}
		
		Function f = new Function(functionName);

		f.execute(input, result);

//		System.out.println(functionName);
//		System.out.println(input);
//		System.out.println(result);
		
		return result;
	}
	
//	public ArrayList<Token> expoldeParam(String input){
//		
//		ArrayList<Token> listToken = new ArrayList<Token>();		
//		if (input.contains("$") || input.contains("@")) {			
//			Pattern pattern = Pattern.compile("[^\\{]*[\\$\\@][^\\}]*");
//			Matcher matcher = pattern.matcher(input);
//			while (matcher.find()) {
//				String contenutoGraffe = matcher.group();
//				listToken.add(new Token("block",""));
//				if (verbose) System.out.println("Match: "+contenutoGraffe);
//				listToken.addAll(getToken(contenutoGraffe));
//				
//				if (input.equals(contenutoGraffe)) {
//					listToken.add(new Token("closeblock",contenutoGraffe));
//				} else {
//					listToken.add(new Token("closeblock","{"+contenutoGraffe+"}"));
//				}
//				
//			}
//			
//		} else {
//			listToken.addAll(getToken(input));
//		}
//	
//		//System.out.println(listToken);
//		return listToken;
//	}
	
	
	public ArrayList<Token> expoldeParam(String input){
		
		ArrayList<Token> listToken = new ArrayList<Token>();		
		if (input.contains("{%") && input.contains("%}")) {			
			//Pattern pattern = Pattern.compile("\\{([^\\}]*)\\}");
			Pattern pattern = Pattern.compile("\\{\\%([^\\%\\}]*)\\%\\}");
			Matcher matcher = pattern.matcher(input);
			while (matcher.find()) {
				String contenutoGraffe = matcher.group();
				listToken.add(new Token("block",""));
				
				if (verbose) System.out.println("Match: "+contenutoGraffe);
				int size = contenutoGraffe.length() -2;
				listToken.addAll(getToken(contenutoGraffe.substring(2,size)));
				listToken.add(new Token("closeblock",contenutoGraffe));
			}
		} else {
			listToken.add(new Token("block",""));
			listToken.addAll(getToken(input));
			listToken.add(new Token("closeblock",input));
		}
	
		//System.out.println(listToken);
		return listToken;
	}
	
	public ArrayList<Token> expoldeCondition(String cond){
		ArrayList<Token> listToken = new ArrayList<Token>();
		listToken.add(new Token("block",""));
		listToken.addAll(getToken(cond));
		listToken.add(new Token("closeblock",cond));
		//System.out.println(listToken);
		return listToken;
	}
		
	private ArrayList<Token> getToken(String input){
		ArrayList<Token> listToken = new ArrayList<Token>();
		if (isProperty(input)){
			listToken.add(new Token("property",input.substring(1)));
		} else if (isTextArea(input)){
			listToken.add(new Token("textarea",input.substring(1)));
		} else if (isCondition(input)){
			int size = input.length();
			String cond = input.substring(1,size-1);
			if (!cond.isEmpty()) {
				listToken.add(new Token("condition",""));
				Operation o = new Operation(verbose);
				String[] op = o.getOperation();
				int sizeOp = op.length;
				for(int i=0;i<sizeOp;i++){
					if (cond.contains(op[i])){
						String[] res = cond.split(op[i]);
						listToken.addAll(getToken(res[0]));
						listToken.add(new Token("operation",op[i]));
						listToken.addAll(getToken(res[1]));
						break;
					}
				}
				listToken.add(new Token("closecondition",cond));
			}
		}  else if (isFunction(input)) {
			int initParam = input.indexOf("(");
			int fineParam = input.lastIndexOf(")");
			String parametri = input.substring(initParam+1,fineParam);
			String function = input.replace("("+parametri+")","");
			
			//System.out.println("Function: |"+ function+"|");
			listToken.add(new Token("function", function));
			//System.out.println("Paramatri: |"+parametri+"|");
			boolean find;
			do{
				find = false;
				int pos=0;
				String key = "";
				
				if (parametri.contains(",\'")) {
					find = true;
					pos = parametri.indexOf(",\'");
					key = ",\'";
				}
				
				if (parametri.contains(",\\[")) {
					int posSwap = parametri.indexOf(",\\["); 
					if ((!find) || (posSwap < pos)){
						find = true;
						pos = posSwap;
						key = ",\\[";
					}
				}
				
				if (parametri.contains(",$")) {
					int posSwap = parametri.indexOf(",$"); 
					if ((!find) || (posSwap < pos)){
						find = true;
						pos = posSwap;
						key = ",$";
					}
				}
				
				if (parametri.contains(",@")) {
					int posSwap = parametri.indexOf(",@"); 
					if ((!find) || (posSwap < pos)){
						find = true;
						pos = posSwap;
						key = ",@";
					}
				}
					
				if (!key.isEmpty()){	
					
					String left = parametri.substring(0,pos);
					//System.out.println("Parametro= |"+ left+"|");
					listToken.addAll(getToken(left));
					
					parametri = parametri.substring(pos+1);
					//System.out.println("Finale= |"+ parametri+"|");
				}
			}while(find);
			
			//System.out.println("Parametro= |"+ parametri+"|");
			listToken.addAll(getToken(parametri));
			listToken.add(new Token("closefunction", ""));
		} else { //STRINGA
			
			Token tk = new Token("string","");
			ArrayList<String> l = new ArrayList<String>();
			if (isString(input)) {
				int size = input.length()-1;
				l.add(input.substring(1,size));
			} else {
				l.add(input);
			}
			tk.setResult(l);
			listToken.add(tk);
		}
		return listToken;
	}
	
	// $prop
	private boolean isProperty(String prop){
		if (prop.contains("$") && (prop.indexOf("$")==0)) {
			return true;
		}
		return false;
	}
	
	//@text
	private boolean isTextArea(String text){
		if (text.contains("@") && (text.indexOf("@")==0)) {
			return true;
		}
		return false;
	}
		
	//'regExpr' o String
	private boolean isString(String regExpr){
		int size = regExpr.length()-1;
		if (regExpr.contains("'") && (regExpr.indexOf("'")==0) && (regExpr.lastIndexOf("'")==size)) {
			return true;
		}
		return false;
	}
	
	//'function($prop)'
	private boolean isFunction(String function) {
		int size = function.length()-1;
		if ((function.contains("(")) && (function.contains(")")) && (function.indexOf("(")>=1) && (function.lastIndexOf(")")==size)) {
			return true;
		}
		return false;
	}
	
	private boolean isCondition(String condition) {
		int size = condition.length()-1;
		if ((condition.contains("[")) && (condition.contains("]")) && (condition.indexOf("[")==0) && (condition.lastIndexOf("]")==size)) {
			return true;
		}
		return false;
	}
	
}
