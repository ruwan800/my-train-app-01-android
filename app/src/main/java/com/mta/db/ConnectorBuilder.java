package com.mta.db;

public abstract class ConnectorBuilder extends BaseBuilder{
	private String table;
	
	public final void setTableName(String table){
		this.table = table;
	}

	public final String getTableName(){
		return this.table;
	}

}