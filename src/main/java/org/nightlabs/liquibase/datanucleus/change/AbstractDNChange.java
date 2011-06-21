/**
 * 
 */
package org.nightlabs.liquibase.datanucleus.change;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;

import liquibase.change.AbstractChange;
import liquibase.database.Database;
import liquibase.database.DatabaseConnection;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.executor.Executor;
import liquibase.executor.ExecutorService;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.RawSqlStatement;

/**
 * @author abieber
 * 
 */
public abstract class AbstractDNChange extends AbstractChange {

	private transient Database db;
	
	private String schemaName;
	

	/**
	 * @param changeName
	 * @param changeDescription
	 * @param priority
	 */
	public AbstractDNChange(String changeName, String changeDescription, int priority) {
		super(changeName, changeDescription, priority);
	}

	@Override
	public SqlStatement[] generateStatements(Database database) {
		this.db = database;
		SqlStatement[] result = doGenerateStatements(database);
		this.db = null;
		return result;
	}

	protected abstract SqlStatement[] doGenerateStatements(Database database);

	protected Database getDb() {
		return db;
	}

	protected Object query(String sql) {
		try {
			Executor executor = ExecutorService.getInstance().getExecutor(db);
			return executor.queryForObject(new RawSqlStatement(sql),
					Object.class);
		} catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}

	protected Collection<String> getPKColumns(String tableName)
			throws DatabaseException, SQLException {
		Collection<String> pkColumns = new LinkedList<String>();
		DatabaseConnection connection = getDb().getConnection();
		if (connection instanceof JdbcConnection) {
			DatabaseMetaData metaData = ((JdbcConnection) connection) .getMetaData();
			ResultSet primaryKeys = metaData.getPrimaryKeys(null, null, tableName);
			while (primaryKeys.next()) {
				String columnName = primaryKeys.getString("COLUMN_NAME");
				pkColumns.add(columnName);
			}
		}
		return pkColumns;
	}
	
	protected Collection<String> getColumnNames(String tableName) throws DatabaseException, SQLException {
		Collection<String> columnNames = new LinkedList<String>();
		DatabaseConnection connection = getDb().getConnection();
		if (connection instanceof JdbcConnection) {
			DatabaseMetaData metaData = ((JdbcConnection) connection) .getMetaData();
			ResultSet columnsRS = metaData.getColumns(null, null, tableName, null);
			while (columnsRS.next()) {
				String columnName = columnsRS.getString("COLUMN_NAME");
				columnNames.add(columnName);
			}
		}
		return columnNames;
	}

	public String getSchemaName() {
		return schemaName;
	}
	
	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}
}
