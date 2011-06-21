/**
 * 
 */
package org.nightlabs.liquibase.datanucleus.update;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import liquibase.statement.AbstractSqlStatement;

/**
 * @author abieber
 *
 */
public class JoinedUpdateStatement extends AbstractSqlStatement {

	private String schemaName;
	private String tableName;
	private Map<String, Object> newColumnValues = new HashMap<String, Object>();
	private Map<String, Map<String, String>> joinTable2JoinColumns = new HashMap<String, Map<String,String>>();
	private String whereClause;
	private List<Object> whereParameters = new ArrayList<Object>();
	
	public JoinedUpdateStatement() {
	}

	public JoinedUpdateStatement(String schemaName, String tableName) {
		this.schemaName = schemaName;
		this.tableName = tableName;
	}
	
	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}
	
	public String getSchemaName() {
		return schemaName;
	}
	
	public String getTableName() {
		return tableName;
	}
	
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	public void addNewColumnValue(String columnName, Object value) {
		newColumnValues.put(columnName, value);
	}
	
	public Map<String, Object> getNewColumnValues() {
		return newColumnValues;
	}
	
	public void addJoinTable(String tableName, Map<String, String> joinColumns) {
		joinTable2JoinColumns.put(tableName, joinColumns);
	}
	
	public Map<String, Map<String, String>> getJoinTable2JoinColumns() {
		return joinTable2JoinColumns;
	}
	
	public String getWhereClause() {
		return whereClause;
	}
	
    public JoinedUpdateStatement setWhereClause(String whereClause) {
        this.whereClause = whereClause;
        return this;
    }

    public JoinedUpdateStatement addWhereParameter(Object value) {
        this.whereParameters.add(value);
        return this;
    }

    public JoinedUpdateStatement addWhereParameters(Object... value) {
        this.whereParameters.addAll(Arrays.asList(value));
        return this;
    }
    
	public List<Object> getWhereParameters() {
		return whereParameters;
	}

}
