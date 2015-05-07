package com.mta.db;

import java.util.ArrayList;

public final class QueryHolder{
	
	private ArrayList<Column> fields = new ArrayList<Column>();
	private ArrayList<Column> whereFields = new ArrayList<Column>();
	private	ArrayList<CONDITION> whereConditions = new ArrayList<CONDITION>();
	private ArrayList<String> whereArgs = new ArrayList<String>();
	private Column sortBy = null;
	private boolean sortAscending = true;
	private int limit = -1;
	private BaseBuilder baseBuilder;
	
	public QueryHolder(BaseBuilder baseBuilder) {
		this.baseBuilder = baseBuilder;
	}
	
	public void setFields(String... fields){
		for(String field : fields){
			this.fields.add(baseBuilder.getColumn(field));
		}
	}
	
	public void addCondition(String field, CONDITION condition, String argument){
		this.whereFields.add(baseBuilder.getColumn(field));
		this.whereConditions.add(condition);
		this.whereArgs.add(argument);
	}
	
	public void setSortBy(String field, boolean ascending){
		this.sortBy = baseBuilder.getColumn(field);
		this.sortAscending = ascending;
		//TODO do this fro multiple fields.
	}
	
	public void setLimit(int limit){
		this.limit = limit;
	}
	
	public ArrayList<Column> getFields(){
		if(fields.size()==0){
			fields = new ArrayList<Column>(baseBuilder.columns.values());
		}
		return this.fields;
	}
	
	public String[] getFieldStrings(){
		if(fields == null || 0 == fields.size()){
			return null;
		}
		String[] x = new String[fields.size()];
		for (int i = 0; i < fields.size(); i++) {
			x[i] = fields.get(i).getColumnName();
		}
		return x;
	}
	
	public String getWhereConditionString(){
		if(whereFields == null || 0 == whereFields.size()){
			return null;
		}
		String q = "";
		for (int i = 0; i < whereFields.size(); i++) {
			q += whereFields.get(i).getColumnName() + " "+ whereConditions.get(i).value() +" ? ";
			if(i < whereFields.size()-1){
				q += "AND ";
			}
		}
		return q;
	}
	
	public String[] getWhereFields(){
		if(whereFields == null || 0 == whereFields.size()){
			return null;
		}
		String[] x = new String[whereFields.size()];
		for (int i = 0; i < whereFields.size(); i++) {
			x[i] = whereFields.get(i).getColumnName();
		}
		return x;
	}

	public String[] getWhereConditions(){
		if(whereFields == null || 0 == whereFields.size()){
			return null;
		}
		String[] x = new String[whereConditions.size()];
		for (int i = 0; i < whereConditions.size(); i++) {
			x[i] = whereConditions.get(i).value();
		}
		return x;
	}
	
	public String[] getWhereConditionKeys(){
		if(whereFields == null || 0 == whereFields.size()){
			return null;
		}
		String[] x = new String[whereConditions.size()];
		for (int i = 0; i < whereConditions.size(); i++) {
			x[i] = whereConditions.get(i).key();
		}
		return x;
	}
	
	public String[] getWhereArgs(){
		if(whereFields == null || 0 == whereFields.size()){
			return null;
		}
		return whereArgs.toArray(new String[whereArgs.size()]);
	}
	
	public String getSortBy(){
		return (sortBy != null) ? this.sortBy.getColumnName()+" "+ (sortAscending ? "" : "DESC") : null;
	}
	
	public String getLimit(){
		return limit != -1 ? String.valueOf(this.limit) : null;
	}
}