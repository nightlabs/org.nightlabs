/**
 * 
 */
package org.nightlabs.liquibase.datanucleus.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import liquibase.database.Database;
import liquibase.exception.DatabaseException;
import liquibase.executor.Executor;
import liquibase.executor.ExecutorService;
import liquibase.logging.LogFactory;
import liquibase.logging.Logger;
import liquibase.statement.core.RawSqlStatement;

import org.nightlabs.liquibase.datanucleus.LiquibaseDNConstants;
import org.nightlabs.liquibase.datanucleus.LiquibaseDNConstants.IdentifierCase;

/**
 * @author abieber
 *
 */
public class DNUtil {
	
	private static final Logger logger = LogFactory.getLogger();
	
	private static final String NUCLEUS_TABLES = "NUCLEUS_TABLES";
	private static final String TABLE_NAME_COL = "TABLE_NAME";
	private static final String CLASS_NAME_COL = "CLASS_NAME";
	
	public static class NucleusTablesStruct {
		private String className;
		private String tableName;
		private String type;
		
		public NucleusTablesStruct(String className, String tableName,
				String type) {
			super();
			this.className = className;
			this.tableName = tableName;
			this.type = type;
		}
		
		public NucleusTablesStruct() {
		}

		public String getClassName() {
			return className;
		}

		public void setClassName(String className) {
			this.className = className;
		}

		public String getTableName() {
			return tableName;
		}

		public void setTableName(String tableName) {
			this.tableName = tableName;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}
	}
	 

	public static class DNClassTableIndex {
		private Map<String, Class<?>> table2Class = new HashMap<String, Class<?>>();
		private Map<Class<?>, String> class2Table = new HashMap<Class<?>, String>();
		/** Note: Here also SCOs, i.e. className.fieldName */
		private Map<String, String> className2Table = new HashMap<String, String>();
		
		public Class<?> getClass(String tableName) {
			return table2Class.get(tableName);
		}
		
		public String getTableName(Class<?> clazz) {
			return class2Table.get(clazz);
		}
		
		public String getTableName(String className) {
			return className2Table.get(className);
		}		
		
		public void add(String className, String table, boolean isFCO) {
			if (isFCO) {
				try {
					Class<?> clazz = loadClass(className);
					class2Table.put(clazz, table);
					table2Class.put(table, clazz);
				} catch (ClassNotFoundException e) {
					logger.warning("Could not find class " + className + " when scanning nucleus_tables (class probably deleted), will index only by className");
				}
			}
			className2Table.put(className, table);
		}
	}
	
	private static Map<Database, DNClassTableIndex> tableIndex = new ConcurrentHashMap<Database, DNUtil.DNClassTableIndex>();
	
	@SuppressWarnings("rawtypes")
	private synchronized static DNClassTableIndex getIndex(Database database) {
		DNClassTableIndex index = tableIndex.get(database);
		if (index == null) {
			index = new DNClassTableIndex();
			
			String sql = String.format("SELECT " + getIdentifierName(CLASS_NAME_COL) + " , " + getIdentifierName(TABLE_NAME_COL) +", " + getIdentifierName("TYPE") + " FROM "+ getNucleusTablesName());
			logger.debug("Executing sql " + sql);
			Executor executor = ExecutorService.getInstance().getExecutor(database);
			List<Map> queryForList;
			try {
				queryForList = executor.queryForList(new RawSqlStatement(sql));
			} catch (DatabaseException e) {
				throw new RuntimeException(e);
			}
			
			for (Map map : queryForList) {
//				Log.debug(map.toString());
				// omitted getIdentifierName in map.get() as somehow the columns always have capital-case?!?
				String className = (String)map.get(CLASS_NAME_COL);
				String table = (String)map.get(TABLE_NAME_COL);
				String type = (String)map.get("TYPE");
				index.add(className, table, "FCO".equals(type));
			}
			
			tableIndex.put(database, index);
		}
		return index;
	}
	
	public static void invalidateIndex(Database database, String className) {
		// For the moment we simply remove the complete index for this database
		tableIndex.remove(database);
	}

	public static Class<?> getClass(Database database, String tableName) {
		DNClassTableIndex index = getIndex(database);
		if (index != null) {
			return index.getClass(tableName);
		}
		throw new RuntimeException("DNClassTableMap could not be built for " + database);
	}
	
	public static String getTableName(Database database, Class<?> clazz) {
		DNClassTableIndex index = getIndex(database);
		if (index != null) {
			String tableName = index.getTableName(clazz);
			if (tableName != null)
				return tableName;
			return index.getTableName(clazz.getName());
		}
		throw new RuntimeException("DNClassTableMap could not be built for " + database);
	}
	
	public static String getTableName(Database database, String className) {
		DNClassTableIndex index = getIndex(database);
		if (index != null) {
			return index.getTableName(className);
		}
		throw new RuntimeException("DNClassTableMap could not be built for " + database);
	}
	
	public static Collection<Class<?>> getKnownSubClasses(Database database, String className) {
		Collection<Class<?>> result = new LinkedList<Class<?>>();
		try {
//			Class<?> baseClass = DNClassesLoader.sharedInstance().loadClass(className);
			Class<?> baseClass = loadClass(className);
			DNClassTableIndex index = getIndex(database);
			if (index == null) {
				throw new RuntimeException("DNClassTableMap could not be built for " + database);
			}
			
			for (Map.Entry<Class<?>, String> entry : index.class2Table.entrySet()) {
				if (entry.getKey() != baseClass && baseClass.isAssignableFrom(entry.getKey())) {
					result.add(entry.getKey());
				}
			}
			
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		return result;
	}

	private static Class<?> loadClass(String className)
			throws ClassNotFoundException {
		return DNUtil.class.getClassLoader().loadClass(className);
	}

	public static Class<?> findSuperClass(String className) {
		try {
			return findSuperClass(loadClass(className));
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static Class<?> findSuperClass(Class<?> clazz) {
		Class<?> superClass = clazz.getSuperclass();
		while (superClass != null && superClass != Object.class) {
			Class<?>[] interfaces = superClass.getInterfaces();
			for (Class<?> iface : interfaces) {
				if (iface.getName().equals("javax.jdo.spi.PersistenceCapable")) {
					return superClass;
				}
			}
			superClass = superClass.getSuperclass();
		}
		return null;
	}

	public static Collection<NucleusTablesStruct> getNucleusReferences(Database database, String className) {
		Collection<NucleusTablesStruct> result = new LinkedList<DNUtil.NucleusTablesStruct>();
		result.addAll(getNucleusReferences(database, className, true));
		result.addAll(getNucleusReferences(database, className, false));
		return result;
	}
	
	private static Collection<NucleusTablesStruct> getNucleusReferences(Database database, String className, boolean fields) {
		Collection<NucleusTablesStruct> result = new LinkedList<DNUtil.NucleusTablesStruct>();
		String sql = "SELECT " + getIdentifierName(CLASS_NAME_COL) + " , " + getIdentifierName(TABLE_NAME_COL) +", " + getIdentifierName("TYPE") + " " +
				"FROM " + getNucleusTablesName() + " " +
				"WHERE " + getIdentifierName(CLASS_NAME_COL) + " LIKE '" + className;
		if (fields) {
			sql = sql + ".%";
		}
		sql = sql + "'";
		logger.debug("Executing sql: " + sql);
		Executor executor = ExecutorService.getInstance().getExecutor(database);
		List<Map> queryForList;
		try {
			queryForList = executor.queryForList(new RawSqlStatement(sql));
		} catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
		
		for (Map map : queryForList) {
//			Log.debug(map.toString());
			// omitted getIdentifierName in map.get() as somehow the columns always have capital-case?!?
			String name = (String)map.get(CLASS_NAME_COL);
			String table = (String)map.get(TABLE_NAME_COL);
			String type = (String)map.get("TYPE");
			result.add(new NucleusTablesStruct(name, table, type));
		}
		return result;
	}
	
	
	public static String getNucleusTablesName() {
		return getIdentifierName(NUCLEUS_TABLES);
	}
	
	public static String getNucleusTableNameColumn() {
		return getIdentifierName(TABLE_NAME_COL);
	}
	
	public static String getNucleusClassNameColumn() {
		return getIdentifierName(CLASS_NAME_COL);
	}
	
	public static String getIdentifierName(String identifier) {
		String identifierCase = System.getProperty(LiquibaseDNConstants.IDENTIFIER_CASE);
		if ("lowercase".equalsIgnoreCase(identifierCase)) {
			return identifier.toLowerCase();
		} else if ("uppercase".equalsIgnoreCase(identifierCase)) {
			return identifier.toUpperCase();
		}
		return identifier;
	}
	
	public static String getFieldName(String fieldName) {
		return getIdentifierName(generateFieldName(fieldName));
	}
	
	public static String generateFieldName(String javaName)
    {
		// TODO: Make generation strategy for field-names configurable: DN-property: name="datanucleus.identifierFactory" value="datanucleus"
		String identCase = System.getProperty(LiquibaseDNConstants.IDENTIFIER_CASE);
		IdentifierCase identifierCase = IdentifierCase.parse(identCase);
		if (identifierCase == null) {
			return null;
		}
        if (javaName == null)
        {
            return null;
        }

        StringBuffer s = new StringBuffer();
        char prev = '\0';

        for (int i = 0; i < javaName.length(); ++i)
        {
            char c = javaName.charAt(i);

            if (c >= 'A' && c <= 'Z' &&
                (identifierCase != IdentifierCase.MIXED_CASE && identifierCase != IdentifierCase.MIXED_CASE_QUOTED))
            {
                if (prev >= 'a' && prev <= 'z')
                {
                    s.append("_");
                }

                s.append(c);
            }
            else if (c >= 'A' && c <= 'Z' &&
                (identifierCase == IdentifierCase.MIXED_CASE || identifierCase == IdentifierCase.MIXED_CASE_QUOTED))
            {
                s.append(c);
            }
            else if (c >= 'a' && c <= 'z' &&
                (identifierCase == IdentifierCase.MIXED_CASE || identifierCase == IdentifierCase.MIXED_CASE_QUOTED))
            {
                s.append(c);
            }
            else if (c >= 'a' && c <= 'z' &&
                (identifierCase != IdentifierCase.MIXED_CASE && identifierCase != IdentifierCase.MIXED_CASE_QUOTED))
            {
                s.append((char)(c - ('a' - 'A')));
            }
            else if (c >= '0' && c <= '9' || c=='_')
            {
                s.append(c);
            }
            else if (c == '.')
            {
                s.append("_");
            }
            else
            {
                String cval = "000" + Integer.toHexString(c);

                s.append(cval.substring(cval.length() - (c > 0xff ? 4 : 2)));
            }

            prev = c;
        }

        // Remove leading and trailing underscores
        while (s.length() > 0 && s.charAt(0) == '_')
        {
            s.deleteCharAt(0);
        }
        if (s.length() == 0)
        {
            throw new IllegalArgumentException("Illegal Java identifier: " + javaName);
        }

        return s.toString();
    }
	
	public static void main(String[] args) {
		System.out.println(getFieldName("primaryKey"));
	}
}
