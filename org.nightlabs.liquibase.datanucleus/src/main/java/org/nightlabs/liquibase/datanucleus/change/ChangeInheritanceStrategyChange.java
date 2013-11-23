/**
 * 
 */
package org.nightlabs.liquibase.datanucleus.change;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import liquibase.change.ChangeMetaData;
import liquibase.database.Database;
import liquibase.exception.DatabaseException;
import liquibase.exception.SetupException;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.DropTableStatement;

import org.nightlabs.liquibase.datanucleus.LiquibaseDNConstants;
import org.nightlabs.liquibase.datanucleus.util.DNUtil;

/**
 * A change that rearranges the storage-data of a given persistent subclass in
 * order to conform to a new inheritance-strategy.
 * <p>
 * The change is used with the &lt;ext:dnChangeInheritanceStrategy&gt; Tag its
 * attributes are:
 * <ul>
 * <li>schemaName: Optional parameter, defaults to the current schemaName</li>
 * <li>className: The fully qualified name of the class (subclass) whose
 * inheritance strategy should be changed</li>
 * <li>oldStrategy: The old inheritance-strategy of the class</li>
 * <li>oldStrategy: The inheritance-strategy it should be changed to</li>
 * </p>
 * <p>
 * Currently only changes from 'NEW_TABLE' to 'SUPERCLASS_TABLE' are supported.
 * This change then assumes that the superclass uses a discriminator and will
 * move all additional columns to the superclass-table and then drop the
 * subclass table.
 * </p>
 * 
 * @author abieber
 */
public class ChangeInheritanceStrategyChange extends AbstractDNChange {
	
	private String className;
	private String oldStrategy;
	private String newStrategy;
	
	public ChangeInheritanceStrategyChange() {
		super("dnChangeInheritanceStrategy", "Change the inheritance strategy of a class", ChangeMetaData.PRIORITY_DEFAULT);
	}

	@Override
	public void init() throws SetupException {
		if (null == getClassName() || getClassName().isEmpty()) {
			throw new SetupException(getChangeMetaData().getName() + " requires the 'className'-attribute to be set.");
		}
		if (null == getOldStrategy() || !LiquibaseDNConstants.InheritanceStratey.NEW_TABLE.equals(getOldStrategy())) {
			throw new SetupException(getChangeMetaData().getName() + " currently only supports 'NEW_TABLE' as oldStrategy.");
		}
		if (null == getNewStrategy() || !LiquibaseDNConstants.InheritanceStratey.SUPERCLASS_TABLE.equals(getNewStrategy())) {
			throw new SetupException(getChangeMetaData().getName() + " currently only supports 'SUPERCLASS_TABLE' as oldStrategy.");
		}
		super.init();
	}
	
	@Override
	public String getConfirmationMessage() {
		return getChangeMetaData().getName() + " successfully changed inheritance strategy of " + getClassName();
	}

	@Override
	protected List<SqlStatement> doGenerateStatements(Database database) {
		List<SqlStatement> result = null;
		try {
			if (LiquibaseDNConstants.InheritanceStratey.NEW_TABLE.equals(getOldStrategy()) && 
					LiquibaseDNConstants.InheritanceStratey.SUPERCLASS_TABLE.equals(getNewStrategy())) {
				result = doChangeNewTableToSuperClassTable();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		if (result != null) {
			return result;
		}
		return null;
	}
	
	
	protected List<SqlStatement> doChangeNewTableToSuperClassTable() throws DatabaseException, SQLException {
		List<SqlStatement> result = new ArrayList<SqlStatement>();
		
		// First we find the columns the subclass has 
		Class<?> superClass = DNUtil.findSuperClass(getClassName());
		
		String subclassTable = DNUtil.getTableName(getDb(), getClassName());
		Collection<String> subclassColumnNames = getColumnNames(subclassTable);
		
		String superclassTable = DNUtil.getTableName(getDb(), superClass);
		Collection<String> superclassColumnNames = getColumnNames(superclassTable);
		
		for (String superclassColumn : superclassColumnNames) {
			subclassColumnNames.remove(superclassColumn);
		}		
		
		if (subclassColumnNames.size() != 0) {
			// TODO move columns to superclass-table
			throw new IllegalStateException("Changing Inheritance of subclass with additional columns is not yet supported");
		}
		
		DropTableStatement dropTableStatement = new DropTableStatement(getSchemaName(), subclassTable, true);
		result.add(dropTableStatement);
		
		return result;
	}
	
	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getOldStrategy() {
		return oldStrategy;
	}

	public void setOldStrategy(String oldStrategy) {
		this.oldStrategy = oldStrategy;
	}

	public String getNewStrategy() {
		return newStrategy;
	}

	public void setNewStrategy(String newStrategy) {
		this.newStrategy = newStrategy;
	}
}
