/**
 * 
 */
package org.nightlabs.liquibase.datanucleus.change;

import java.util.ArrayList;
import java.util.List;

import liquibase.change.ChangeMetaData;
import liquibase.database.Database;
import liquibase.exception.SetupException;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.RenameTableStatement;
import liquibase.statement.core.UpdateStatement;

import org.nightlabs.liquibase.datanucleus.util.DNUtil;
import org.nightlabs.liquibase.datanucleus.util.Log;

/**
 * @author abieber
 *
 */
public class ChangeClassNameChange extends AbstractDNChange {

	private String className;
	private String newClassName;
	private String newTableName;
	private boolean renameTable = true;
	
	public ChangeClassNameChange() {
		super("dnChangeClassName", "Change the name of a class and re-refernce that for datanulceus", ChangeMetaData.PRIORITY_DEFAULT);
	}
	
	@Override
	public void init() throws SetupException {
		super.init();
		if (null == getClassName() || getClassName().isEmpty()) {
			throw new SetupException(getChangeMetaData().getName() + " requires the 'className'-attribute to be set.");
		}
		if (null == getNewClassName() || getNewClassName().isEmpty()) {
			throw new SetupException(getChangeMetaData().getName() + " requires the 'newClassName'-attribute to be set.");
		}
	}

	/* (non-Javadoc)
	 * @see liquibase.change.Change#getConfirmationMessage()
	 */
	@Override
	public String getConfirmationMessage() {
		return getChangeMetaData().getName() + " successfully updated the tablename for " + getClassName() + " to " + getNewTableName();
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.liquibase.datanucleus.change.AbstractDNChange#doGenerateStatements(liquibase.database.Database)
	 */
	@Override
	protected SqlStatement[] doGenerateStatements(Database database) {
		
		List<SqlStatement> statements = new ArrayList<SqlStatement>(2);
		
		// We need the old table name to re-set the class_name
		String oldTableName = DNUtil.getTableName(database, getClassName());
		if (null == oldTableName || oldTableName.isEmpty()) {
			Log.error(getChangeMetaData().getName() + " can't find the oldTableName, aborting");
			throw new RuntimeException(getChangeMetaData().getName() + " can't find the oldTableName, aborting.");
		}		
		
		// Update nucleus_tables to reference the new class-name
		UpdateStatement update = new UpdateStatement(getSchemaName(), DNUtil.NUCLEUS_TABLES);
		update.setWhereClause(DNUtil.TABLE_NAME_COL + " = '" + database.escapeStringForDatabase(oldTableName) + "'");
		update.addNewColumnValue(DNUtil.CLASS_NAME_COL, getNewClassName());
		statements.add(update);
		
		if (null != getNewTableName() && !getNewClassName().isEmpty()) {
			// The table name has changed also, re-reference and rename the table
			update = new UpdateStatement(getSchemaName(), DNUtil.NUCLEUS_TABLES);
			update.setWhereClause(DNUtil.CLASS_NAME_COL + " = '" + database.escapeStringForDatabase(getNewClassName()) + "'");
			update.addNewColumnValue(DNUtil.TABLE_NAME_COL, getNewTableName());
			statements.add(update);
			
			if (isRenameTable()) {
				statements.add(new RenameTableStatement(getSchemaName(), oldTableName, getNewTableName()));
			}
		}
		
		return statements.toArray(new SqlStatement[statements.size()]);
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getNewTableName() {
		return newTableName;
	}

	public void setNewTableName(String newTableName) {
		this.newTableName = newTableName;
	}

	public boolean isRenameTable() {
		return renameTable;
	}

	public void setRenameTable(boolean renameTable) {
		this.renameTable = renameTable;
	}

	public String getNewClassName() {
		return newClassName;
	}

	public void setNewClassName(String newClassName) {
		this.newClassName = newClassName;
	}
}
