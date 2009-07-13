package org.nightlabs.environment;

import org.apache.log4j.Logger;

/**
 * An Enum to define values for the runtime environment of applications.
 * The static method {@link #getEnvironment()} reads this value from the 
 * system properties using the key 'org.nightlabs.environment'.
 * <p>
 * The value of {@link Environment} can be used by applications to check
 * whether they are in a testing/development or in a product runtime environment
 * and react specifically. 
 * </p>
 *
 * @author Chairat Kongarayawetchakun <chairat[]nightlabs[DOT]de>
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 */
public enum Environment {
	
	development, productive;
	
	/**
	 * System property name
	 */
	private static final String SYS_PROPERTY_ENVIRONMENT = "org.nightlabs.environment";
	
	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(Environment.class);

	/**
	 * Reads the {@link Environment} value from the system properties.
	 * Note that this will default to {@link #productive}.
	 * 
	 * @return The {@link Environment} value read from system properties if possible.
	 */
	public static Environment getEnvironment() {
		String environment = System.getProperty(SYS_PROPERTY_ENVIRONMENT);
		if (environment == null) {
			return Environment.productive;
		}
		try {
			return Environment.valueOf(environment);
		} catch (Throwable t) {
			logger.error("System property " + SYS_PROPERTY_ENVIRONMENT + " is invalid: " + environment, t);
			return Environment.productive;
		}
	}
}