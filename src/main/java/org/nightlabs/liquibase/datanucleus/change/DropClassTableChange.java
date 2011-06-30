/**
 * 
 */
package org.nightlabs.liquibase.datanucleus.change;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import liquibase.change.ChangeMetaData;
import liquibase.change.core.DropAllForeignKeyConstraintsChange;
import liquibase.database.Database;
import liquibase.exception.SetupException;
import liquibase.logging.LogFactory;
import liquibase.logging.Logger;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.DeleteStatement;
import liquibase.statement.core.DropTableStatement;

import org.nightlabs.liquibase.datanucleus.util.DNUtil;
import org.nightlabs.liquibase.datanucleus.util.DNUtil.NucleusTablesStruct;

/**
 * A Change that drops the table data for a persistent class was stored in.
 * <p>
 * The change is used with the &lt;ext:dnDropClassTable&gt; Tag its attributes
 * are:
 * <ul>
 * <li>schemaName: Optional parameter, defaults to the current schemaName</li>
 * <li>className: The fully qualified name of the class of which the data and table should be dropped</li>
 * <li>cascadeConstraints: Whether to cascade 
 * <ul>
 * </p>
 * 
 * @author abieber
 */
public class DropClassTableChange extends AbstractDNChange {

	private static final Logger logger = LogFactory.getLogger();
	
	private String className;
	private boolean cascadeConstraints = true;
	
	public DropClassTableChange() {
		super("dnDropClassTable", "Drop a field from the data of a persistent class", ChangeMetaData.PRIORITY_DEFAULT);
	}
	
	@Override
	public void init() throws SetupException {
		super.init();
		if (null == getClassName() || getClassName().isEmpty()) {
			throw new SetupException(getChangeMetaData().getName() + " requires the 'className'-attribute to be set.");
		}
	}

	@Override
	public String getConfirmationMessage() {
		return getChangeMetaData().getName() + " successfully dropped the table for " + getClassName() + ".";
	}

	@Override
	protected List<SqlStatement> doGenerateStatements(Database database) {
		
		List<SqlStatement> statements = new ArrayList<SqlStatement>(2);
		
		for (NucleusTablesStruct struct : DNUtil.getNucleusReferences(database, getClassName())) {
			
			String tableName = DNUtil.getTableName(database, struct.getClassName());
			
			if (tableName != null && !tableName.isEmpty()) {
				
				DropAllForeignKeyConstraintsChange dropFK = new DropAllForeignKeyConstraintsChange();
				dropFK.setBaseTableSchemaName(getSchemaName());
				dropFK.setBaseTableName(tableName);
				dropFK.setChangeSet(getChangeSet());
				SqlStatement[] dropFKStatements = dropFK.generateStatements(getDb());
				if (dropFKStatements != null) {
					statements.addAll(Arrays.asList(dropFKStatements));
				}
				
				DropTableStatement dropTable = new DropTableStatement(getSchemaName(), tableName, isCascadeConstraints());
				
				DeleteStatement delete = new DeleteStatement(getSchemaName(), DNUtil.getNucleusTablesName());
				delete.setWhereClause(DNUtil.getNucleusClassNameColumn() + " = ?");
				delete.addWhereParameter(struct.getClassName());
			
				statements.add(dropTable);
			} else {
				logger.warning("Could not find table for className " + getClassName() + " in nucleus_tables. " + getChangeMetaData().getName() + " will abort.");
			}
		}
		
		return statements;
	}
	
	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}
	
	public void setCascadeConstraints(boolean cacadeConstraints) {
		this.cascadeConstraints = cacadeConstraints;
	}
	
	public boolean isCascadeConstraints() {
		return cascadeConstraints;
	}

}
