/**
 * 
 */
package org.nightlabs.config.internal;

import org.nightlabs.config.Config;

/**
 * @author abieber
 *
 */
public class DefaultClassLoaderResolver implements IClassLoaderResolver {

	private ClassLoader classLoader;
	
	/**
	 * Constructor 
	 */
	public DefaultClassLoaderResolver(ClassLoader cl) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClassLoader getClassLoader(String className) {
		if (classLoader != null) {
			return classLoader;
		} 
		return Config.class.getClassLoader();
	}

}
