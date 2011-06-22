/**
 * 
 */
package org.nightlabs.liquibase.datanucleus;

/**
 * Konstants used in the Liquibase-Datanucleus plugin.
 * 
 * @author abieber
 *
 */
public interface LiquibaseDNConstants {
	
	/** Prefix for all system properties  */
	public static final String SYS_PROP_PREFIX = LiquibaseDNConstants.class.getPackage().getName();
	
	/**
	 * System property name to set the identifier.case property used to modify
	 * identifer-names like table-names column-names
	 */
	public static final String IDENTIFIER_CASE = SYS_PROP_PREFIX + ".identifier.case";

	/**
	 * Interface for constants of the inheritance-strageties
	 */
	public static interface InheritanceStratey {
		public static final String NEW_TABLE = "NEW_TABLE";
		public static final String SUPERCLASS_TABLE = "SUPERCLASS_TABLE";
	}
	
}
