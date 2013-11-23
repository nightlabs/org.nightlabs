/**
 * 
 */
package org.nightlabs.config.internal;

/**
 * Used to resolve an appropriate ClassLoader for a given class.
 * 
 * @author abieber
 */
public interface IClassLoaderResolver {
	/**
	 * Resolve a ClassLoader for the given className.
	 * 
	 * @param className The class to resolve a classloader for.
	 * @return A class-loader that is able to load the given class.
	 */
	public ClassLoader getClassLoader(String className);
}
