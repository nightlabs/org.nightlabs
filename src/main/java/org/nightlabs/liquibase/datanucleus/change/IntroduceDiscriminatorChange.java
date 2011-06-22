/**
 * 
 */
package org.nightlabs.liquibase.datanucleus.change;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import liquibase.change.ChangeMetaData;
import liquibase.database.Database;
import liquibase.exception.SetupException;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.AddColumnStatement;
import liquibase.statement.core.UpdateStatement;

import org.nightlabs.liquibase.datanucleus.update.JoinedUpdateStatement;
import org.nightlabs.liquibase.datanucleus.util.DNUtil;

/**
 * A Change that changes introduces a discriminator-column for the given class.
 * This applies only to the discriminator-strategy of 'CLASS_NAME'.
 * <p>
 * The change will add the discriminator column and then update all rows in the
 * table according to the class name of that row.
 * </p>
 * <p>
 * The change is used with the &lt;ext:dnIntroduceDiscriminator&gt; Tag its
 * attributes are:
 * <ul>
 * <li>schemaName: Optional parameter, defaults to the current schemaName</li>
 * <li>className: The fully qualified name of the class in whose table a
 * discriminator should be introduced</li>
 * <li>discriminatorStrategy: Optional parameter, If set currently only
 * "CLASS_NAME" is supported.</li>
 * <li>discriminatorColumn: Optional parameter. If set it defines the name of
 * the discriminator column. Defaults to "DISCRIMINATOR"</li>
 * <ul>
 * </p>
 * 
 * @author abieber
 */
public class IntroduceDiscriminatorChange extends AbstractDNChange {

	private static final String STRATEGY_CLASS_NAME = "CLASS_NAME"; 
	
	private String className;
	private String discriminatorStrategy;
	private String discriminatorColumn;
	
	/**
	 */
	public IntroduceDiscriminatorChange() {
		super("dnIntroduceDiscriminator", "DataNucleus: Introduce Discirminator", ChangeMetaData.PRIORITY_DEFAULT);
	}
	
	@Override
	public void init() throws SetupException {
		super.init();
		if (getDiscriminatorStrategy() != null && !STRATEGY_CLASS_NAME.equals(getDiscriminatorStrategy())) {
			throw new SetupException(getChangeMetaData().getName()
					+ " currently only supports discriminatorStrategy '"
					+ STRATEGY_CLASS_NAME + "', '" + getDiscriminatorStrategy()
					+ "' is not supported.");
		}
		if (null == getClassName() || getClassName().isEmpty()) {
			throw new SetupException(getChangeMetaData().getName() + " requires a className to be set");
		}
	}

	/* (non-Javadoc)
	 * @see liquibase.change.Change#getConfirmationMessage()
	 */
	public String getConfirmationMessage() {
		return "Introduced discriminator-column for class " + getClassName();
	}

	/* (non-Javadoc)
	 * @see liquibase.change.Change#generateStatements(liquibase.database.Database)
	 */
	public SqlStatement[] doGenerateStatements(Database database) {
		
		List<SqlStatement> statements = new ArrayList<SqlStatement>();
		
		statements.add(
				new AddColumnStatement(
						getSchemaName(), getClassTableName(), getDiscriminatorColumn(), "VARCHAR(255)", null));
		
		
		Collection<Class<?>> knownSubClasses = DNUtil.getKnownSubClasses(database, getClassName());
		Map<String, String> pkColumnMapping = new HashMap<String, String>();
		if (knownSubClasses.size() > 0) {
			try {
				Collection<String> pkColumnNames = getPKColumns(getClassTableName());
				for (String columnName : pkColumnNames) {
					pkColumnMapping.put(columnName, columnName);
				}
			} catch (Exception e) {
				throw new RuntimeException("Could not get PK columns", e);
			}
		}

		for (Class<?> subClass : knownSubClasses) {
			String tableName = DNUtil.getTableName(database, subClass);
			
			JoinedUpdateStatement updateStatement = new JoinedUpdateStatement(getSchemaName(), getClassTableName());
			updateStatement.addJoinTable(tableName, pkColumnMapping);
			updateStatement.addNewColumnValue(getDiscriminatorColumn(), subClass.getName());
			
			statements.add(updateStatement);
		}
		
//		Collection<Class<?>> knownSubClasses = DNUtil.getKnownSubClasses(database, getClassName());
//		for (Class<?> subClass : knownSubClasses) {
//			String tableName = DNUtil.getTableName(database, subClass);
//			StringBuilder sb = new StringBuilder();
//			sb.append("update " + getClassTableName() + " inner join "  + tableName + " on ");
//			Collection<String> pkColumns = null;
//			try {
//				pkColumns = getPKColumns(getClassTableName());
//			} catch (Exception e) {
//				throw new RuntimeException("Could not get PK columns", e);
//			}
//			for (Iterator<String> it = pkColumns.iterator(); it.hasNext();) {
//				String columnName = it.next();
//				sb.append(getClassTableName()).append(".").append(columnName).append(" = ").append(tableName).append(".").append(columnName);
//				if (it.hasNext()) {
//					sb.append(" and ");
//				}
//			}
//			sb.append("set ").append(getDiscriminatorColumn()).append(" = ").append(database.escapeStringForDatabase(subClass.getName()));
//			statements.add(new RawSqlStatement(sb.toString()));
//		}
		
		
		UpdateStatement updateRemainingRows = new UpdateStatement(getSchemaName(), getClassTableName());
		updateRemainingRows.setWhereClause(getDiscriminatorColumn() + " IS NULL");
		updateRemainingRows.addNewColumnValue(getDiscriminatorColumn(), getClassName());
		statements.add(updateRemainingRows);
		
		return statements.toArray(new SqlStatement[statements.size()]);
	}
	
	private String classTableName = null;
	
	String getClassTableName() {
		if (classTableName == null) {
			classTableName = DNUtil.getTableName(getDb(), getClassName());
		}
		return classTableName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getDiscriminatorStrategy() {
		return discriminatorStrategy;
	}

	public void setDiscriminatorStrategy(String discriminatorStrategy) {
		this.discriminatorStrategy = discriminatorStrategy;
	}

	public String getDiscriminatorColumn() {
		if (discriminatorColumn == null) {
			discriminatorColumn = "DISCRIMINATOR";
		}
		return DNUtil.getIdentifierName(discriminatorColumn);
	}

	public void setDiscriminatorColumn(String discriminatorColumn) {
		this.discriminatorColumn = discriminatorColumn;
	}
}
