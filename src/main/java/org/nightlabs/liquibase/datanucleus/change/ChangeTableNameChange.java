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
public class ChangeTableNameChange extends AbstractDNChange {

	private String className;
	private String newTableName;
	private boolean renameTable = true;
	
	public ChangeTableNameChange() {
		super("dnChangeTableName", "Change the name of a table objects of a class are stored in", ChangeMetaData.PRIORITY_DEFAULT);
	}
	
	@Override
	public void init() throws SetupException {
		super.init();
		if (null == getClassName() || getClassName().isEmpty()) {
			throw new SetupException(getChangeMetaData().getName() + " requires the 'className'-attribute to be set.");
		}
		if (null == getNewTableName() || getNewTableName().isEmpty()) {
			throw new SetupException(getChangeMetaData().getName() + " requires the 'newTableName'-attribute to be set.");
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
		
		String oldTableName = null;
		if (isRenameTable()) {
			oldTableName = DNUtil.getTableName(database, getClassName());
			if (null == oldTableName || oldTableName.isEmpty()) {
				Log.warn(getChangeMetaData().getName() + " have renameTable=\"true\" but could not find the old table name value in nulceus_tables");
			} else {
				statements.add(new RenameTableStatement(getSchemaName(), oldTableName, getNewTableName()));
			}
		}
		
		UpdateStatement update = new UpdateStatement(getSchemaName(), DNUtil.NUCLEUS_TABLES);
		update.setWhereClause(DNUtil.CLASS_NAME_COL + " = '" + database.escapeStringForDatabase(getClassName()) + "'");
		update.addNewColumnValue(DNUtil.TABLE_NAME_COL, getNewTableName());
		
		statements.add(update);
		
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
}
