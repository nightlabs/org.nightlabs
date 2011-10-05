/**
 * 
 */
package org.nightlabs.liquibase.datanucleus.change;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import liquibase.change.ChangeMetaData;
import liquibase.database.Database;
import liquibase.database.core.MySQLDatabase;
import liquibase.exception.DatabaseException;
import liquibase.executor.Executor;
import liquibase.executor.ExecutorService;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.RawSqlStatement;

/**
 * @author abieber
 *
 */
public class RenameColumnChange extends AbstractDNChange {

	private String tableName;
	private String oldColumnName;
	private String newColumnName;
	
	public RenameColumnChange() {
		super("dnRenameColumn", "Rename a column", ChangeMetaData.PRIORITY_DEFAULT);
	}

	/* (non-Javadoc)
	 * @see liquibase.change.Change#getConfirmationMessage()
	 */
	@Override
	public String getConfirmationMessage() {
		return createDelegate().getConfirmationMessage();
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.liquibase.datanucleus.change.AbstractDNChange#doGenerateStatements(liquibase.database.Database)
	 */
	@Override
	protected List<SqlStatement> doGenerateStatements(Database database) {
		liquibase.change.core.RenameColumnChange delegate = createDelegate();
		if (database instanceof MySQLDatabase) {
			if (tableExists(database)) {
				delegate.setColumnDataType(getMySQLColumnDataType(database));
				return Arrays.asList(delegate.generateStatements(database));
			} else {
				return new LinkedList<SqlStatement>();
			}
		}
		return Arrays.asList(delegate.generateStatements(database));
	}
	
	private liquibase.change.core.RenameColumnChange createDelegate() {
		liquibase.change.core.RenameColumnChange delegate = new liquibase.change.core.RenameColumnChange();
		delegate.setChangeSet(getChangeSet());
		delegate.setOldColumnName(oldColumnName);
		delegate.setNewColumnName(newColumnName);
		delegate.setSchemaName(getSchemaName());
		delegate.setTableName(tableName);
		return delegate;
	}
	
	private boolean tableExists(Database database) {
		Executor executor = ExecutorService.getInstance().getExecutor(database);
		RawSqlStatement sql = new RawSqlStatement("SHOW TABLES LIKE '" + tableName + "'");
		List<Map> list = null;
		try {
			list = executor.queryForList(sql);
		} catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
		return list != null && list.size() == 1;
	}

	
	private String getMySQLColumnDataType(Database database) {
    	Executor executor = ExecutorService.getInstance().getExecutor(database);
    	RawSqlStatement sql = new RawSqlStatement(
    			"SHOW FULL COLUMNS FROM " + database.escapeTableName(getSchemaName(), tableName) + " WHERE Field = '" + oldColumnName +"'");
    	List<Map> list = null;
    	try {
			 list = executor.queryForList(sql);
		} catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
		if (list == null || list.size() != 1) {
			throw new IllegalStateException("Invalid result from " + sql.getSql());
		}
		@SuppressWarnings("unchecked")
		Map<String, Object> map = list.get(0);
		StringBuilder sb = new StringBuilder();
		sb.append(map.get("COLUMN_TYPE"));
		Object collation = map.get("COLLATION_NAME");
		if (collation != null && !"NULL".equals(collation)) {
			sb.append(" COLLATE ").append(collation);
		}
		Object nullable = map.get("IS_NULLABLE");
		if (nullable != null && "YES".equals(nullable)) {
			sb.append(" NOT NULL");
		} else {
			sb.append(" NULL");
		}
		return sb.toString();
    }		

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	public void setOldColumnName(String oldColumnName) {
		this.oldColumnName = oldColumnName;
	}
	
	public void setNewColumnName(String newColumnName) {
		this.newColumnName = newColumnName;
	}
	
}
