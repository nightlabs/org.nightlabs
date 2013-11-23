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
import liquibase.statement.core.DropColumnStatement;

import org.nightlabs.liquibase.datanucleus.util.DNUtil;

/**
 * A Change that removes a field from the data of a persistent class, i.e. it drops the appropriate column.
 * <p>
 * The change is used with the &lt;ext:dnDropField&gt; Tag its attributes
 * are:
 * <ul>
 * <li>schemaName: Optional parameter, defaults to the current schemaName</li>
 * <li>className: The fully qualified name of the class of which a field should be dropped</li>
 * <li>fieldName: The name of the field</li>
 * <ul>
 * </p>
 * 
 * @author abieber
 */
public class DropFieldChange extends AbstractDNChange {

	private String className;
	private String fieldName;
	
	public DropFieldChange() {
		super("dnDropField", "Drop a field from the data of a persistent class", ChangeMetaData.PRIORITY_DEFAULT);
	}
	
	@Override
	public void init() throws SetupException {
		super.init();
		if (null == getClassName() || getClassName().isEmpty()) {
			throw new SetupException(getChangeMetaData().getName() + " requires the 'className'-attribute to be set.");
		}
		if (null == getFieldName() || getFieldName().isEmpty()) {
			throw new SetupException(getChangeMetaData().getName() + " requires the 'fieldName'-attribute to be set.");
		}
	}

	@Override
	public String getConfirmationMessage() {
		return getChangeMetaData().getName() + " successfully dropt the data for field " + getClassName() + "." + getFieldName();
	}

	@Override
	protected List<SqlStatement> doGenerateStatements(Database database) {
		
		List<SqlStatement> statements = new ArrayList<SqlStatement>(2);
		
		// We need the old table name to re-set the class_name
		String tableName = DNUtil.getTableName(database, getClassName());
		String fieldIdentifier = DNUtil.getFieldName(getFieldName());
		
		DropColumnStatement dropCol = new DropColumnStatement(getSchemaName(), tableName, fieldIdentifier);
		
		statements.add(dropCol);
		
		return statements;
	}
	
	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getFieldName() {
		return fieldName;
	}
	
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	
}
