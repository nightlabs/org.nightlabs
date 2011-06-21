/**
 * 
 */
package org.nightlabs.liquibase.datanucleus.change;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.nightlabs.liquibase.datanucleus.util.DNUtil;

import liquibase.change.ChangeMetaData;
import liquibase.database.Database;
import liquibase.exception.DatabaseException;
import liquibase.exception.SetupException;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.DropTableStatement;

/**
 * @author abieber
 *
 */
public class ChangeInheritanceStrategyChange extends AbstractDNChange {
	
	private static final String NEW_TABLE = "NEW_TABLE";
	private static final String SUPERCLASS_TABLE = "SUPERCLASS_TABLE";

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
		if (null == getOldStrategy() || !NEW_TABLE.equals(getOldStrategy())) {
			throw new SetupException(getChangeMetaData().getName() + " currently only supports 'NEW_TABLE' as oldStrategy.");
		}
		if (null == getNewStrategy() || !SUPERCLASS_TABLE.equals(getNewStrategy())) {
			throw new SetupException(getChangeMetaData().getName() + " currently only supports 'SUPERCLASS_TABLE' as oldStrategy.");
		}
		super.init();
	}
	
	/* (non-Javadoc)
	 * @see liquibase.change.Change#getConfirmationMessage()
	 */
	@Override
	public String getConfirmationMessage() {
		return getChangeMetaData().getName() + " successfully changed inheritance strategy of " + getClassName();
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.liquibase.datanucleus.change.AbstractDNChange#doGenerateStatements(liquibase.database.Database)
	 */
	@Override
	protected SqlStatement[] doGenerateStatements(Database database) {
		List<SqlStatement> result = null;
		try {
			if (NEW_TABLE.equals(getOldStrategy()) && SUPERCLASS_TABLE.equals(getNewStrategy())) {
				result = doChangeNewTableToSuperClassTable();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		if (result != null) {
			return result.toArray(new SqlStatement[result.size()]);
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


	public static void main(String[] args) {
		String path = "org/nightlabs/";
		String entryName = "org/nightlabs/jfire/base/j2ee/DefaultMessageRenderer.class";
		
		System.out.println(entryName.substring(path.length()).contains("/"));
	}
	
}
