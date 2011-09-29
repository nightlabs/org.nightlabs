/**
 * 
 */
package org.nightlabs.liquibase.datanucleus.util;

import java.util.Arrays;
import java.util.List;

import liquibase.change.core.DropAllForeignKeyConstraintsChange;
import liquibase.changelog.ChangeSet;
import liquibase.database.Database;
import liquibase.statement.SqlStatement;

/**
 * @author abieber
 *
 */
public class LiquibaseUtil {

	protected LiquibaseUtil() {
	}
	
	public static void addDropConstraintsStatements(Database db, ChangeSet changeSet, List<SqlStatement> statements, String schemaName, String tableName) {
		DropAllForeignKeyConstraintsChange dropFK = new DropAllForeignKeyConstraintsChange();
		dropFK.setBaseTableSchemaName(schemaName);
		dropFK.setBaseTableName(tableName);
		dropFK.setChangeSet(changeSet);
		SqlStatement[] dropFKStatements = dropFK.generateStatements(db);
		if (dropFKStatements != null) {
			statements.addAll(Arrays.asList(dropFKStatements));
		}
	}
	


}
