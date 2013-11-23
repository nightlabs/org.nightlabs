/**
 * 
 */
package org.nightlabs.liquibase.datanucleus.change;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import liquibase.change.AbstractChange;
import liquibase.change.Change;
import liquibase.database.Database;
import liquibase.database.DatabaseConnection;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.executor.Executor;
import liquibase.executor.ExecutorService;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.RawSqlStatement;

/**
 * Base class for {@link Change}-imlementations for datanucleus-changes.
 * 
 * @author abieber
 */
public abstract class AbstractDNChange extends AbstractChange {
	
	/** Will be accessible during {@link #doGenerateStatements(Database)} */
	private transient Database db;
	
	/**
	 * Common optional parameter for all changes, most Statements take this as
	 * parameter but will support <code>null</code> as default, too.
	 */
	private String schemaName;
	

	/**
	 * @param changeName
	 * @param changeDescription
	 * @param priority
	 */
	public AbstractDNChange(String changeName, String changeDescription, int priority) {
		super(changeName, changeDescription, priority);
	}

	/**
	 * Implements the super-class method by delegating to {@link #doGenerateStatements(Database)}.
	 * While {@link #doGenerateStatements(Database)} runs the {@link #getDb()}-property will serve
	 * the Database passed to this method.
	 * <p>
	 * {@inheritDoc}
	 * </p>
	 */
	@Override
	public SqlStatement[] generateStatements(Database database) {
		this.db = database;
		List<SqlStatement> result = doGenerateStatements(database);
		this.db = null;
		if (result == null) {
			return new SqlStatement[0];
		}
		return result.toArray(new SqlStatement[result.size()]);
	}

	/**
	 * This method has to be implemented by subclasses and return the actual {@link SqlStatement}s 
	 * to be performed to complete the specific change. 
	 * 
	 * @param database The database the changes should be applied to.
	 * @return A list of {@link SqlStatement}s to be performed.
	 */
	protected abstract List<SqlStatement> doGenerateStatements(Database database);

	/**
	 * @return The {@link Database} passed to
	 *         {@link #generateStatements(Database)}, but only while
	 *         {@link #doGenerateStatements(Database)} runs, <code>null</code>
	 *         otherwise.
	 */
	protected Database getDb() {
		return db;
	}

	/**
	 * Executes the given Query using the current Liquibase Exectutor and Database.
	 * 
	 * @param sql The sql to execute.
	 * @return The resulting single Object.
	 */
	protected Object query(String sql) {
		try {
			Executor executor = ExecutorService.getInstance().getExecutor(db);
			return executor.queryForObject(new RawSqlStatement(sql),
					Object.class);
		} catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns the names of the primary-key columns of the given table.
	 * 
	 * @param tableName The name of the table to find the primary-key columns for.
	 * @return The names of the primary-key columns of the given table.
	 * @throws DatabaseException ...
	 * @throws SQLException ...
	 */
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
	
	/**
	 * Returns the names of all columns of the given table.
	 * 
	 * @param tableName The name of the table to find the column-names for.
	 * @return The names of all columns of the given table.
	 * @throws DatabaseException ...
	 * @throws SQLException ...
	 */
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

	/**
	 * The schemaName is a common optional parameter for all changes, most
	 * Statements take this as parameter but will support <code>null</code> as
	 * default, too.
	 * 
	 * @return The schemaName, might be <code>null</code>
	 */
	public String getSchemaName() {
		return schemaName;
	}
	
	/**
	 * @param schemaName The schemaName
	 */
	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}
}
