/**
 * 
 */
package org.nightlabs.liquibase.datanucleus.change;

import java.util.ArrayList;
import java.util.List;

import liquibase.change.ChangeMetaData;
import liquibase.database.Database;
import liquibase.exception.SetupException;
import liquibase.logging.LogFactory;
import liquibase.logging.Logger;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.RenameTableStatement;
import liquibase.statement.core.UpdateStatement;

import org.nightlabs.liquibase.datanucleus.util.DNUtil;
import org.nightlabs.liquibase.datanucleus.util.LiquibaseUtil;

/**
 * A Change that changes the name of the table the data fof a persistent class
 * is stored in.
 * <p>
 * Note that this can also be used to change the name of the table the data of a
 * collection-type field inside a class is stored in (that is stored in an own
 * table, for example for &#064;Join - collections).
 * </p>
 * <p>
 * The change is used with the &lt;ext:dnChangeTableName&gt; Tag its attributes
 * are:
 * <ul>
 * <li>schemaName: Optional parameter, defaults to the current schemaName</li>
 * <li>className: The fully qualified name of the class (or) field to be changed
 * </li>
 * <li>renameTable: Optional parameter, defaults to <code>true</code>. Defines
 * whether the table should be actually renamed. If set to <code>false</code>,
 * only the reference in the datanucleus-index will be adapted.</li>
 * <li>newTableName: The <b>new</b> name of the table</li>
 * <li>dropContraints: Optional parameter, defaults to <code>true</code>.
 * Defines whether the contraints of the table should be dropped (assuming that
 * they will be recreated by datanucleus)</li>
 * <ul>
 * </p>
 * 
 * @author abieber
 */
public class ChangeTableNameChange extends AbstractDNChange {

	private static final Logger logger = LogFactory.getLogger();
	
	private String className;
	private String newTableName;
	private boolean renameTable = true;
	private boolean dropConstraints = true;
	
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

	@Override
	public String getConfirmationMessage() {
		return getChangeMetaData().getName() + " successfully updated the tablename for " + getClassName() + " to " + getNewTableName();
	}

	@Override
	protected List<SqlStatement> doGenerateStatements(Database database) {
		
		List<SqlStatement> statements = new ArrayList<SqlStatement>(2);
		
		String oldTableName = null;
		if (isRenameTable()) {
			oldTableName = DNUtil.getTableName(database, getClassName());
			if (null == oldTableName || oldTableName.isEmpty()) {
				logger.warning(getChangeMetaData().getName() + " have renameTable=\"true\" but could not find the old table name value in nulceus_tables");
			} else {
				statements.add(new RenameTableStatement(getSchemaName(), oldTableName, getNewTableName()));
				if (isDropConstraints()) {
					LiquibaseUtil.addDropConstraintsStatements(getDb(), getChangeSet(), statements, getSchemaName(), getNewTableName());
				}
			}
		}
		
		UpdateStatement update = new UpdateStatement(getSchemaName(), DNUtil.getNucleusTablesName());
		update.setWhereClause(DNUtil.getNucleusClassNameColumn() + " = '" + database.escapeStringForDatabase(getClassName()) + "'");
		update.addNewColumnValue(DNUtil.getNucleusTableNameColumn(), getNewTableName());
		
		statements.add(update);
		
		return statements;
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
	
	public void setDropConstraints(boolean dropConstraints) {
		this.dropConstraints = dropConstraints;
	}
	
	public boolean isDropConstraints() {
		return dropConstraints;
	}
}
