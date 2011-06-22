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
 * A Change that changes the className of a persistent class in the
 * datanucleus-indes nucleus-tables. This Change can optionally rename the table
 * data of the of the old class was stored in to the new table-name.
 * <p>
 * Note that this can also be used to change the name and table of a
 * collection-type field inside a class (that is stored in an own table, for
 * example for &#064;Join - collections).
 * </p>
 * <p>
 * The change is used with the &lt;ext:dnChangeClassName&gt; Tag its attributes
 * are:
 * <ul>
 * <li>schemaName: Optional parameter, defaults to the current schemaName</li>
 * <li>className: The fully qualified name of the class (or) field to be changed
 * </li>
 * <li>newClassName: The fully qualified <b>new</b> name it should to be changed
 * to</li>
 * <li>renameTable: Optional parameter, defaults to <code>true</code>. Defines
 * whether the table the data for the old class (or field) was stored in should
 * be renamed to. If set to true, the 'newTableName' attribute has to be set,
 * too.</li>
 * <li>newTableName: The fully qualified <b>new</b> name the old table should be
 * changed to</li>
 * <ul>
 * </p>
 * 
 * @author abieber
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

	@Override
	public String getConfirmationMessage() {
		return getChangeMetaData().getName() + " successfully updated the tablename for " + getClassName() + " to " + getNewTableName();
	}

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
		UpdateStatement update = new UpdateStatement(getSchemaName(), DNUtil.getNucleusTablesName());
		update.setWhereClause(DNUtil.getNucleusTableNameColumn() + " = '" + database.escapeStringForDatabase(oldTableName) + "'");
		update.addNewColumnValue(DNUtil.getNucleusClassNameColumn(), getNewClassName());
		statements.add(update);
		
		if (null != getNewTableName() && !getNewClassName().isEmpty()) {
			// The table name has changed also, re-reference and rename the table
			update = new UpdateStatement(getSchemaName(), DNUtil.getNucleusTablesName());
			update.setWhereClause(DNUtil.getNucleusClassNameColumn() + " = '" + database.escapeStringForDatabase(getNewClassName()) + "'");
			update.addNewColumnValue(DNUtil.getNucleusTableNameColumn(), getNewTableName());
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
		return DNUtil.getIdentifierName(newTableName);
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
