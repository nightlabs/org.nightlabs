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
import liquibase.statement.core.RawSqlStatement;

/**
 * @author abieber
 *
 */
public class DNUtil {
	
	public static final String NUCLEUS_TABLES = "nucleus_tables";
	public static final String TABLE_NAME_COL = "TABLE_NAME";
	public static final String CLASS_NAME_COL = "CLASS_NAME";

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
					Log.error("Could not find class " + className + " when scanning nucleus_tables, will index only by className", e);
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
			
			String sql = String.format("SELECT " + CLASS_NAME_COL + " , " + TABLE_NAME_COL +", TYPE FROM "+ NUCLEUS_TABLES);
			Log.debug(sql);
			Executor executor = ExecutorService.getInstance().getExecutor(database);
			List<Map> queryForList;
			try {
				queryForList = executor.queryForList(new RawSqlStatement(sql));
			} catch (DatabaseException e) {
				throw new RuntimeException(e);
			}
			
			for (Map map : queryForList) {
				String className = (String)map.get(CLASS_NAME_COL);
				String table = (String)map.get(TABLE_NAME_COL);
				index.add(className, table, "FCO".equals(map.get("TYPE")));
			}
			
			tableIndex.put(database, index);
		}
		return index;
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
	
	
}
