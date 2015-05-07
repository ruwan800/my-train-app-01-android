package com.mta.db;

public enum DEFAULT {
	NONE(""),
	V_0("0"),
	V_1("1"),
	V_NULL("NULL"),
	V_TRUE("1"),
	V_FALSE("0");
	private final String value;
	DEFAULT(String x){
		this.value = x.equals("") ? x : "DEFAULT "+x;
	}
	public String value() {
		return value;
	}
}