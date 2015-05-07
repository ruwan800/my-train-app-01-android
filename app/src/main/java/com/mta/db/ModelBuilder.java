package com.mta.db;

import android.provider.BaseColumns;

public abstract class ModelBuilder extends BaseBuilder{
	
	/**
     * Add new column to the model.
     *
     * @param name
     *            the name of the model.
     * @param column
     *            the name of the table column.
     * @param state
     * 			  {@link STATE} of the column.
     * @param t_type
     * 			  {@link TYPE} of the column.
     * @param vdefault
     * 			  {@link DEFAULT} value of the column.
     * @param attributes
     * 			  Set of attributes in type of {@link ATTRIBUTE}.
     */
	protected final void addColumn(String name, String column, STATE state, TYPE t_type, DEFAULT vdefault, ATTRIBUTE... attributes){
		Column col = new Column(name, column, state, t_type, vdefault, attributes);
		if(columns.size() == 0){
			Column idColumn = new Column("ID", BaseColumns._ID, STATE.LOCAL, TYPE.INTEGER, DEFAULT.NONE, ATTRIBUTE.PRIMARY, ATTRIBUTE.AI);
			columns.put("ID", idColumn);
		}
		columns.put(name, col);
	}

	protected abstract void tableStructure();
	
}