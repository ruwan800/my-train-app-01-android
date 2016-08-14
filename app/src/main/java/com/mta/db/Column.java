package com.mta.db;


public final class Column {
	private String name;
	private String column;
	private TYPE type;
	private DEFAULT vdefault;
	private STATE state;
	private ATTRIBUTE[] attributes;
	Column(String name, String column, STATE state, TYPE t_type, DEFAULT vdefault, ATTRIBUTE... attributes) {
		this.name = name;
		this.column = column;
		this.type = t_type;
		this.vdefault = vdefault;
		this.attributes = attributes;
		this.state = state;
	}
	public TYPE getType(){
		return type;
	}
	public String[] getAttributes(){
		String[] x = new String[attributes.length];
		for (int i = 0; i < attributes.length; i++) {
			x[i] = attributes[i].value();
		}
		return x;
	}
	public String getColumnName(){
		return this.column;
	}
	public String getName(){
		return this.name;
	}	
	public STATE getState(){
		return this.state;
	}
	public String getColumn(){
		return this.column;
	}
	public String getQuerySubString(){
		StringBuilder substr = new StringBuilder();
		substr.append(this.column).append(" ");
		substr.append(this.type).append(" ");
		substr.append(this.vdefault.value()).append(" ");
		for(String attr: this.getAttributes()){
			substr.append(attr).append(" ");
		}
		if(this.state==STATE.REMOTE){
			return "";
		}
		return substr.toString();
	}
	public boolean isRemoteOnly(){
		return state.equals(STATE.REMOTE) ? true : false ;
	}
	public boolean isLocalOnly(){
		return state.equals(STATE.LOCAL) ? true : false ;
	}
}