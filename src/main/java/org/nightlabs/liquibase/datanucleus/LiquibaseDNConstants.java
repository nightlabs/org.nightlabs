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
	
	public enum IdentifierCase {
	    UPPER_CASE("UPPERCASE"),
	    UPPER_CASE_QUOTED("\"UPPERCASE\""),
	    LOWER_CASE("lowercase"),
	    LOWER_CASE_QUOTED("\"lowercase\""),
	    MIXED_CASE("MixedCase"),
	    MIXED_CASE_QUOTED("\"MixedCase\"");

	    String name;
	    private IdentifierCase(String name)
	    {
	        this.name = name;
	    }
	    public String toString()
	    {
	        return name;
	    }
	    
	    public static final IdentifierCase parse(String identifierCase) {
	    	if (identifierCase == null) {
	    		return null;
	    	}
	    	String identCase = identifierCase.toLowerCase();
	    	if ("uppercase".equals(identCase)) {
	    		return UPPER_CASE;
	    	} else if ("lowercase".equals(identCase)) {
	    		return LOWER_CASE;
	    	} if ("mixedcase".equals(identCase)) {
	    		return MIXED_CASE;
	    	} 
	    	return null;
	    }
	}

	/**
	 * Interface for constants of the inheritance-strageties
	 */
	public static interface InheritanceStratey {
		public static final String NEW_TABLE = "NEW_TABLE";
		public static final String SUPERCLASS_TABLE = "SUPERCLASS_TABLE";
	}
	
}
