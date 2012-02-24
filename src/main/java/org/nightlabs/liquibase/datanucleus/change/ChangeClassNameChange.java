package org.nightlabs.liquibase.datanucleus.change;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import liquibase.change.ChangeMetaData;
import liquibase.database.Database;
import liquibase.exception.DatabaseException;
import liquibase.exception.SetupException;
import liquibase.logging.LogFactory;
import liquibase.logging.Logger;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.RenameTableStatement;
import liquibase.statement.core.UpdateStatement;

import org.nightlabs.liquibase.datanucleus.util.DNUtil;
import org.nightlabs.liquibase.datanucleus.util.LiquibaseUtil;
import org.nightlabs.liquibase.datanucleus.util.DNUtil.NucleusTablesStruct;

/**
 * A change that changes the className of a persistent class in the
 * datanucleus-index nucleus-tables. This change can optionally rename the table
 * where the old class was stored in.
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
 * <li>discriminatorColumn: The optional discriminator column name; defaults to 'discriminator'.</li>
 * <li>oldDiscriminatorValue: The optional old discriminator value which will be replaced with the new one. 
 * 	If none is given, the class name strategy is assumed and the className is taken.</li>
 * <li>newDiscriminatorValue: The optional new discriminator value which will be replacing the old one. 
 * 	If none is given, the class name strategy is assumed and the newClassName is taken.</li>
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

	private static final String DISCRIMINATOR_COLUMN_DEFAULT = "discriminator";

	private static final Logger logger = LogFactory.getLogger();
	
	private String className;
	private String newClassName;
	private String newTableName;
	private String discriminatorColumn = DISCRIMINATOR_COLUMN_DEFAULT;
	private String oldDiscriminatorValue;
	private String newDiscriminatorValue;
	private boolean renameTable = true;
	private boolean dropConstraints = true;
	
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
		
		// Assumes the classname as default discriminator strategy.
		if (oldDiscriminatorValue == null && newDiscriminatorValue == null)
		{
			oldDiscriminatorValue = className;
			newDiscriminatorValue = newClassName;
		}
		else if (oldDiscriminatorValue != null && newDiscriminatorValue == null || 
				oldDiscriminatorValue == null && newDiscriminatorValue != null)
		{
			throw new SetupException(getChangeMetaData().getName() + " requires that either no {old,new}DiscriminatorValue " +
					"is set or both have to be set!");
		}
	}

	@Override
	public String getConfirmationMessage() {
		return getChangeMetaData().getName() + " successfully updated the tablename for " + getClassName() + " to " + getNewTableName();
	}

	@Override
	protected List<SqlStatement> doGenerateStatements(Database database) {
		
		List<SqlStatement> statements = new ArrayList<SqlStatement>(2);
		
		// We need the old table name to re-set the class_name
		String oldTableName = DNUtil.getTableName(database, getClassName());
		if (null == oldTableName || oldTableName.isEmpty()) {
			logger.severe(getChangeMetaData().getName() + " can't find the oldTableName for class " + getClassName() + ", aborting");
			return statements;
		}		
		
		statements.addAll(updateDNClassInformation(oldTableName));
		
		updateDiscriminatorColumns(database, statements);
		
		if (isTableNameUpdateNeeded(oldTableName)) {
			statements.addAll(updateTableName(oldTableName));
		}
		return statements;
	}

	private void updateDiscriminatorColumns(Database database, List<SqlStatement> statements)
	{
		Class<?> parentClass = DNUtil.findSuperClass(newClassName);
		while (parentClass != null)
		{
			String parentClassTableName = DNUtil.getTableName(database, parentClass); 
			statements.addAll(updateDiscriminatorColumn(parentClassTableName));
			parentClass = DNUtil.findSuperClass(parentClass);
		}
	}

	private List<SqlStatement> updateDNClassInformation(String oldTableName)
	{
		// For the class-entry itself but as well as for all collection-type fields we update the nucleus tables.
		Collection<NucleusTablesStruct> nucleusReferences = DNUtil.getNucleusReferences(getDb(), getClassName());
		List<SqlStatement> statements = new ArrayList<SqlStatement>(nucleusReferences.size() + 1);
		
		for (NucleusTablesStruct struct : nucleusReferences) {
			String newClassValue = getNewClassName() + struct.getClassName().replace(getClassName(), "");
			// Update nucleus_tables to reference the new class-name
			UpdateStatement update = new UpdateStatement(getSchemaName(), DNUtil.getNucleusTablesName());
			update.setWhereClause(DNUtil.getNucleusClassNameColumn() + " = ?");
			update.addWhereParameter(struct.getClassName());
			update.addNewColumnValue(DNUtil.getNucleusClassNameColumn(), newClassValue);
			statements.add(update);
		}
		
		// Update nucleus_tables to reference the new class-name
		UpdateStatement update = new UpdateStatement(getSchemaName(), DNUtil.getNucleusTablesName());
		update.setWhereClause(DNUtil.getNucleusTableNameColumn() + " = ?");
		update.addWhereParameter(oldTableName);
		update.addNewColumnValue(DNUtil.getNucleusClassNameColumn(), getNewClassName());
		statements.add(update);
		
		return statements;
	}
	
	private List<SqlStatement> updateDiscriminatorColumn(String rootTableName)
	{
		if (rootTableName == null) {
			return Collections.emptyList();
		}
		
		try {
			return doUpdateDiscriminatorColumn(rootTableName);
		} catch (Exception e) {
			if (e instanceof RuntimeException) {
				throw (RuntimeException) e;
			}
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Update the discriminator column if existing.
	 * 
	 * @param rootTableName The old class's table name.
	 */
	private List<SqlStatement> doUpdateDiscriminatorColumn(String rootTableName)
		throws DatabaseException, SQLException
	{
		List<SqlStatement> statements = new ArrayList<SqlStatement>(1);
		Collection<String> columnNames = getColumnNames(rootTableName);
		if (columnNames.contains(getDiscriminatorColumn())) {
			// always assumes that the default discriminator column name is used and no other. 
			UpdateStatement discriminatorUpdate = new UpdateStatement(getSchemaName(), rootTableName);
			discriminatorUpdate.setWhereClause(getDiscriminatorColumn() + " = ?");
			discriminatorUpdate.addWhereParameter(getOldDiscriminatorValue());
			discriminatorUpdate.addNewColumnValue(getDiscriminatorColumn(), getNewDiscriminatorValue());
			statements.add(discriminatorUpdate);
		}
		return statements;
	}

	/**
	 * Returns true if a new table name is set and it differs from the old one.
	 * 
	 * @param oldTableName The old table name.
	 * @return true if a new table name is set and it differs from the old one.
	 */
	private boolean isTableNameUpdateNeeded(String oldTableName)
	{
		return null != getNewTableName() && !getNewTableName().isEmpty() && !getNewTableName().equals(oldTableName);
	}
	
	/**
	 * Creates update statements to update the Tablename for the given class in datanucleus management tables and 
	 * of the class itself if {@link #renameTable} is set.
	 * 
	 * @param oldTableName The old table name.
	 * @return the update statements changing the table name.
	 */
	private List<SqlStatement> updateTableName(String oldTableName)
	{
		List<SqlStatement> statements = new ArrayList<SqlStatement>(2);
		// The table name has changed also, re-reference and rename the table
		UpdateStatement update = new UpdateStatement(getSchemaName(), DNUtil.getNucleusTablesName());
		update.setWhereClause(DNUtil.getNucleusClassNameColumn() + " = ?");
		update.addWhereParameter(getNewClassName());
		update.addNewColumnValue(DNUtil.getNucleusTableNameColumn(), getNewTableName());
		statements.add(update);
		
		if (isRenameTable()) {
			statements.add(new RenameTableStatement(getSchemaName(), oldTableName, getNewTableName()));
			if (isDropConstraints()) {
				LiquibaseUtil.addDropConstraintsStatements(getDb(), getChangeSet(), statements, getSchemaName(), getNewTableName());
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

	public String getNewTableName() {
		return DNUtil.getIdentifierName(newTableName);
	}

	public void setNewTableName(String newTableName) {
		this.newTableName = newTableName;
	}

	/**
	 * @return the discriminatorColumn
	 */
	public String getDiscriminatorColumn()
	{
		return discriminatorColumn;
	}

	/**
	 * @param discriminatorColumn the discriminatorColumn to set
	 */
	public void setDiscriminatorColumn(String discriminatorColumn)
	{
		this.discriminatorColumn = discriminatorColumn;
	}

	/**
	 * @return the oldDiscriminatorValue
	 */
	public String getOldDiscriminatorValue()
	{
		return oldDiscriminatorValue;
	}

	/**
	 * @param oldDiscriminatorValue the oldDiscriminatorValue to set
	 */
	public void setOldDiscriminatorValue(String oldDiscriminatorValue)
	{
		this.oldDiscriminatorValue = oldDiscriminatorValue;
	}

	/**
	 * @return the newDiscriminatorValue
	 */
	public String getNewDiscriminatorValue()
	{
		return newDiscriminatorValue;
	}

	/**
	 * @param newDiscriminatorValue the newDiscriminatorValue to set
	 */
	public void setNewDiscriminatorValue(String newDiscriminatorValue)
	{
		this.newDiscriminatorValue = newDiscriminatorValue;
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
	
	public void setDropConstraints(boolean dropConstraints) {
		this.dropConstraints = dropConstraints;
	}
	public boolean isDropConstraints() {
		return dropConstraints;
	}
}

