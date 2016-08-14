package com.mta.db;

public enum CONDITION {
	EQ("eq", "="),
	LT("lt", "<"),
	LTE("lte", "<="),
	GT("gt", ">"),
	GTE("gte", "=>"),
	NE("ne", "<>");
	private final String key;
	private final String value;
	CONDITION(String key, String value){
		this.key = key;
		this.value = value;
	}
	public String key() {
		return key;
	}
	
	public String value() {
		return value;
	}
}