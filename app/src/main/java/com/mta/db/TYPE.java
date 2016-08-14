package com.mta.db;

public enum TYPE {
	INTEGER("INTEGER"),
	DOUBLE("INTEGER"),
	FLOAT("INTEGER"),
	SHORT("INTEGER"),
	BOOLEAN("BOOLEAN"),
	REAL("REAL"),
	TEXT("TEXT"),
	TIMESTAMP("INTEGER");
	private final String value;
	TYPE(String x){
		this.value = x;
	}
	public String value() {
		return value;
	}
}