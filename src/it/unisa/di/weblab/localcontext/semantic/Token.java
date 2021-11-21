package it.unisa.di.weblab.localcontext.semantic;

import java.util.ArrayList;

public class Token {
	public Token(String type, String name) {
		this.type = type;
		this.name = name;
		this.result = new ArrayList<String>();
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
		
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
		
	public ArrayList<String> getResult() {
		return result;
	}

	public void setResult(ArrayList<String> result) {
		this.result = result;
	}
	
	public String toString(){
		String content="\n - Token: [type]=|"+type+"| - [Name]=|"+name+"| - |Result]=";
		int size = result.size();
		for (int i = 0; i < size; i++) {
			content=content+"|"+result.get(i)+"|";
		}
		return content;
	}
	
	public String type;
	public String name;
	public ArrayList<String> result;
}
