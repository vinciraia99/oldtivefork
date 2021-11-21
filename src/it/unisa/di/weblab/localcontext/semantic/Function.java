package it.unisa.di.weblab.localcontext.semantic;

import java.util.ArrayList;

public class Function {
	
	private static int id;
	
	public Function(String name) {
		
		this.name = name;
	}
			
	public boolean isComplete() {
		if (name.equalsIgnoreCase("add")) return true;
		else if (name.equalsIgnoreCase("size")) return true;
		else if (name.equalsIgnoreCase("exist")) return true;
		else if (name.equalsIgnoreCase("assign")) return true;
		else if (name.equalsIgnoreCase("print")) return true;
		else if (name.equalsIgnoreCase("explode")) return true;
		else if (name.equalsIgnoreCase("if")) return true;
		else if (name.equalsIgnoreCase("generateId")) return true;
		return false;
	}
	
	public boolean isPartial() {
		if (name.equalsIgnoreCase("isset")) return true;
		return false;
	}
	
	public boolean exist() {
		if (name.equalsIgnoreCase("add")) return true;
		else if (name.equalsIgnoreCase("size")) return true;
		else if (name.equalsIgnoreCase("exist")) return true;
		else if (name.equalsIgnoreCase("assign")) return true;
		else if (name.equalsIgnoreCase("print")) return true;
		else if (name.equalsIgnoreCase("explode")) return true;
		else if (name.equalsIgnoreCase("if")) return true;
		else if (name.equalsIgnoreCase("generateId")) return true;
		else if (name.equalsIgnoreCase("isset")) return true;
		return false;
	}
	
	public void execute(ArrayList<String> input, ArrayList<String> result) {
				
		if (isComplete()) {
			if (name.equalsIgnoreCase("add")) add(input,result);
			else if (name.equalsIgnoreCase("size")) size(input,result);
			else if (name.equalsIgnoreCase("exist")) exist(input,result);
			else if (name.equalsIgnoreCase("assign")) assign(input,result);
			else if (name.equalsIgnoreCase("print")) add(input,result);
			else if (name.equalsIgnoreCase("explode")) explode(input,result);
			else if (name.equalsIgnoreCase("if")) funcIf(input,result);
			else if (name.equalsIgnoreCase("generateId")) generateId(input,result);
		} else if (isPartial()){
			if (name.equalsIgnoreCase("isset")) isset(input,result);
		}
	
	}
	
	private void assign(ArrayList<String> input, ArrayList<String> result) {
		if (!input.isEmpty()){
			result.addAll(input);
		}
	}
	
	private void add(ArrayList<String> input, ArrayList<String> result){
		if (!input.isEmpty()){
			result.addAll(input);
		}
	}
	
	private void size(ArrayList<String> input, ArrayList<String> result){
		
//		System.out.println("***");
//		System.out.println("Input Size"+input.size());
//		System.out.println("Result Size"+result.size());
//		System.out.println("***");
		
		
		int sizeInput = input.size();
		if (result.isEmpty()) {
			String sizeInputString = String.valueOf(sizeInput);
			result.add(sizeInputString);
		} else {
			int sizePrec = Integer.valueOf(result.get(0));
			String sizeInputString = String.valueOf(sizePrec + sizeInput);
			result.set(0, sizeInputString);
		}
	}
	
	private void isset(ArrayList<String> input, ArrayList<String> result){
		boolean ris = false;
		if (input.size()>0)	ris = true;
		result.add(String.valueOf(ris));
	}
	
	private void exist(ArrayList<String> input, ArrayList<String> result){
		boolean ris = false;
		if (input.size()>0)	ris = true;
		
		if (result.isEmpty()){
			result.add(String.valueOf(ris));
		} else {
			boolean resVechio = Boolean.parseBoolean(result.get(0));
			ris = ris || resVechio;
			result.set(0, String.valueOf(ris));
		}
	}
	
	private void explode(ArrayList<String> input, ArrayList<String> result){
		
		String res = "";
		if (input.size()>=2) {
			String separetorFine = input.get(0);
			int sizeInput = input.size();
			for (int i=1; i < sizeInput;i++){
				res = res + input.get(i);
				if (i!=sizeInput-1){
					res = res + separetorFine;
				}
			}
		}
		
		if (result.isEmpty()) {
			result.add(res);
		} else {
			result.set(0, result.get(0) + res);
		}
	}
	
	/*
	private void print(ArrayList<String> input, ArrayList<String> result){
		if (!input.isEmpty()) {
			add(input,result);
		}
	}*/
	
	private void funcIf(ArrayList<String> input, ArrayList<String> result){
		if (input.size()>=3) {
			if (input.get(0).equalsIgnoreCase("true")) {
				result.add(input.get(1));
			} else {
				result.add(input.get(2));
			}
		}
	}
	
	private void generateId(ArrayList<String> input, ArrayList<String> result){
		id++;
		String res =  String.valueOf(id);
		result.add(res);
	}
	
	public static void initStateFunction(){
		id=0;
	}
	
	public String getName(){
		return name;
	}

	private String name;
}
