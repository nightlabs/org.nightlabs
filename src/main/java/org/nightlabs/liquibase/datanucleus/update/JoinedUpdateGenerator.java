/**
 * 
 */
package org.nightlabs.liquibase.datanucleus.update;

import java.util.Date;
import java.util.Map;

import liquibase.database.Database;
import liquibase.database.typeconversion.TypeConverterFactory;
import liquibase.exception.ValidationErrors;
import liquibase.sql.Sql;
import liquibase.sql.UnparsedSql;
import liquibase.sqlgenerator.SqlGeneratorChain;
import liquibase.sqlgenerator.core.AbstractSqlGenerator;

/**
 * @author abieber
 *
 */
public class JoinedUpdateGenerator extends
		AbstractSqlGenerator<JoinedUpdateStatement> {

	/**
	 * 
	 */
	public JoinedUpdateGenerator() {
	}

	/* (non-Javadoc)
	 * @see liquibase.sqlgenerator.SqlGenerator#validate(liquibase.statement.SqlStatement, liquibase.database.Database, liquibase.sqlgenerator.SqlGeneratorChain)
	 */
	@Override
	public ValidationErrors validate(JoinedUpdateStatement statement,
			Database database, SqlGeneratorChain sqlGeneratorChain) {
        ValidationErrors validationErrors = new ValidationErrors();
        validationErrors.checkRequiredField("tableName", statement.getTableName());
        validationErrors.checkRequiredField("columns", statement.getNewColumnValues());
        return validationErrors;
	}

	/* (non-Javadoc)
	 * @see liquibase.sqlgenerator.SqlGenerator#generateSql(liquibase.statement.SqlStatement, liquibase.database.Database, liquibase.sqlgenerator.SqlGeneratorChain)
	 */
	@Override
	public Sql[] generateSql(JoinedUpdateStatement statement,
			Database database, SqlGeneratorChain sqlGeneratorChain) {
		
        StringBuffer sql = new StringBuffer("UPDATE " + database.escapeTableName(statement.getSchemaName(), statement.getTableName()));
        
        for (String joinTableName : statement.getJoinTable2JoinColumns().keySet()) {
			sql.append(", ");
			sql.append(database.escapeTableName(statement.getSchemaName(), joinTableName));
		}
        
        sql.append(" SET");
        for (String column : statement.getNewColumnValues().keySet()) {
            sql.append(" ").append(database.escapeColumnName(statement.getSchemaName(), statement.getTableName(), column)).append(" = ");
            sql.append(convertToString(statement.getNewColumnValues().get(column), database));
            sql.append(",");
        }
        sql.deleteCharAt(sql.lastIndexOf(","));

        StringBuffer where = new StringBuffer();
        
        for (Map.Entry<String, Map<String, String>> joinColumnsEntry : statement.getJoinTable2JoinColumns().entrySet()) {
        	if (where.length() > 0) {
        		where.append(" AND ");
        	}
        	String joinTableName = joinColumnsEntry.getKey();
        	where.append("(");
        	StringBuilder joinWhere = new StringBuilder();
			for (Map.Entry<String, String> joinColumns : joinColumnsEntry.getValue().entrySet()) {
				if (joinWhere.length() > 0) {
					joinWhere.append(" AND ");
				}
				String updateTableColumnName = joinColumns.getKey();
				String joinTableColumnName = joinColumns.getValue();
				joinWhere.append(database.escapeTableName(statement.getSchemaName(), statement.getTableName())).append(".");
				joinWhere.append(database.escapeColumnName(statement.getSchemaName(), statement.getTableName(), updateTableColumnName));
				joinWhere.append(" = ");
				joinWhere.append(database.escapeTableName(statement.getSchemaName(), joinTableName)).append(".");
				joinWhere.append(database.escapeColumnName(statement.getSchemaName(), joinTableName, joinTableColumnName));
			}
			where.append(joinWhere);
			where.append(")");
		}
        
        if (statement.getWhereClause() != null && statement.getWhereClause().length() > 0) {
	        if (where.length() > 0) {
	        	where.append(" AND ");
	        }
	        where.append(statement.getWhereClause().trim());
        }
        
        if (where.length() > 0) {
			String fixedWhereClause = "WHERE " + where.toString();
            for (Object param : statement.getWhereParameters()) {
                fixedWhereClause = fixedWhereClause.replaceFirst("\\?", convertToString(param, database));
            }
            sql.append(" ").append(fixedWhereClause);
        }

        return new Sql[]{
                new UnparsedSql(sql.toString())
        };
	}
	
    private String convertToString(Object newValue, Database database) {
        String sqlString;
        if (newValue == null || newValue.toString().equalsIgnoreCase("NULL")) {
            sqlString = "NULL";
        } else if (newValue instanceof String && database.shouldQuoteValue(((String) newValue))) {
            sqlString = "'" + database.escapeStringForDatabase(newValue.toString()) + "'";
        } else if (newValue instanceof Date) {
          // converting java.util.Date to java.sql.Date
            Date date = (Date) newValue;
            if (date.getClass().equals(java.util.Date.class)) {
                date = new java.sql.Date(date.getTime());
            }

            sqlString = database.getDateLiteral(date);
        } else if (newValue instanceof Boolean) {
            if (((Boolean) newValue)) {
                sqlString = TypeConverterFactory.getInstance().findTypeConverter(database).getBooleanType().getTrueBooleanValue();
            } else {
                sqlString = TypeConverterFactory.getInstance().findTypeConverter(database).getBooleanType().getFalseBooleanValue();
            }
        } else {
            sqlString = newValue.toString();
        }
        return sqlString;
    }	

}
