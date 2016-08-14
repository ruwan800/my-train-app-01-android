package com.mta.db;

public enum ATTRIBUTE {
	PRIMARY("PRIMARY KEY"),
	NOT_NULL("NOT NULL"),
	UNIQUE("UNIQUE"),
	AI("AUTOINCREMENT");
	private final String value;
	ATTRIBUTE(String x){
		this.value = x;
	}
	public String value() {
		return value;
	}
}