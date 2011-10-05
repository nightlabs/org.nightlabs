/**
 * 
 */
package org.nightlabs.liquibase.datanucleus.change;

import java.util.Arrays;
import java.util.List;

import liquibase.change.ChangeMetaData;
import liquibase.database.Database;
import liquibase.statement.SqlStatement;

/**
 * @author abieber
 *
 */
public class DropAllForeignKeyConstraintsChange extends AbstractDNChange {

	private String tableName;
	
	/**
	 * @param changeName
	 * @param changeDescription
	 * @param priority
	 */
	public DropAllForeignKeyConstraintsChange() {
		super("dnDropAllForeignKeyConstraints", "Drop all foreign key constraints", ChangeMetaData.PRIORITY_DEFAULT);
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
		return Arrays.asList(createDelegate().generateStatements(database));
	}

	private liquibase.change.core.DropAllForeignKeyConstraintsChange createDelegate() {
		liquibase.change.core.DropAllForeignKeyConstraintsChange delegate = new liquibase.change.core.DropAllForeignKeyConstraintsChange();
		delegate.setBaseTableSchemaName(getSchemaName());
		delegate.setBaseTableName(tableName);
		delegate.setChangeSet(getChangeSet());
		return delegate;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
}
