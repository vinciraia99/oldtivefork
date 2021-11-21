package it.unisa.di.weblab.localcontext.semantic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Operation {

	private boolean verbose;
	private String[] op = {">=","<=","==","=","<",">"};
	
	public String[] getOperation() {
		return op;
	}

	public Operation(boolean verbose) {
		this.verbose=verbose;
	}
	
	public boolean operationExecute(ArrayList<String> input1,String op,ArrayList<String> input2){
		if ((input1.size()==1) && (input2.size()==1)) {
			return operationResult(input1.get(0).toLowerCase(),op,input2.get(0).toLowerCase());
		} 
		return operationListResult(input1,op,input2);
	}
	
	private boolean operationResult(String value1,String operation,String value2) {
		if (verbose) System.out.println("|"+value1+"| "+ operation +" |"+value2+"|");
		
		if (operation.equals("=") || operation.equals("==")) {
			Pattern pattern = Pattern.compile(value2);
		    Matcher matcher = pattern.matcher(value1);
		    if(matcher.matches()) return true;
			return false;
		}
		
		int val1 = Integer.valueOf(value1);
		int val2 = Integer.valueOf(value2);
		
		if (operation.equals("<")) {
			if (val1 < val2){
				return true;
			}
			return false;
		}
		
		if (operation.equals("<=")) {
			if (val1 <= val2){
				return true;
			}
			return false;
		}
		
		if (operation.equals(">")) {
			if (val1 > val2){
				return true;
			}
			return false;
		}
		
		if (operation.equals(">=")) {
			if (val1 >= val2){
				return true;
			}
			return false;
		}
		
		return false;
	}
	
	private boolean operationListResult(ArrayList<String> value1,String operation,ArrayList<String> value2) {
		if (verbose) System.out.println("|"+value1+"| "+ operation +" |"+value2+"|");
		
		if (operation.equals("==")) {
			return equalLists(value1,value2);
		}
		
		if (operation.equals("=")) {
			return value1.containsAll(value2);
		}
		
		int val1 = value1.size();
		int val2 = value2.size();
		
		if (operation.equals("<")) {
			if (val1 < val2){
				return true;
			}
			return false;
		}
		
		if (operation.equals("<=")) {
			if (val1 <= val2){
				return true;
			}
			return false;
		}
		
		if (operation.equals(">")) {
			if (val1 > val2){
				return true;
			}
			return false;
		}
		
		if (operation.equals(">=")) {
			if (val1 >= val2){
				return true;
			}
			return false;
		}
		
		return false;
	}
	
	private  boolean equalLists(List<String> one, List<String> two){     
	    if (one == null && two == null){
	        return true;
	    }

	    if((one == null && two != null) 
	      || one != null && two == null
	      || one.size() != two.size()){
	        return false;
	    }

	    one = new ArrayList<String>(one); 
	    two = new ArrayList<String>(two);   

	    Collections.sort(one);
	    Collections.sort(two);      
	    return one.equals(two);
	}
}
