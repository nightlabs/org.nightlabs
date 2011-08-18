/**
 * 
 */
package org.nightlabs.config.internal.osgi;

import org.nightlabs.config.Config;
import org.nightlabs.config.internal.IClassLoaderResolver;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.ExportedPackage;
import org.osgi.service.packageadmin.PackageAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link IClassLoaderResolver} that uses the OSGI {@link PackageAdmin} to search for a ClassLoader.
 *  
 * @author abieber
 */
public class OSGIClassLoaderResolver implements IClassLoaderResolver {

	private static final Logger logger = LoggerFactory.getLogger(OSGIClassLoaderResolver.class);
	private PackageAdmin packageAdmin;
	/**
	 * Constructor 
	 */
	public OSGIClassLoaderResolver() {
	}

	
	@Override
	public ClassLoader getClassLoader(String className) {
		if (logger.isDebugEnabled())
			logger.debug("Resolving ClassLoader for " + className);
		if (packageAdmin == null) {
			if (logger.isDebugEnabled())
				logger.debug("Lazy resolving PackageAdmin...");
			Bundle configBundle = FrameworkUtil.getBundle(Config.class);
			if (configBundle == null) {
				throw new IllegalStateException("Could not access Config-Bundle");
			}
			if (logger.isDebugEnabled())
				logger.debug("Have config bundle {}", configBundle.getSymbolicName());
			if (configBundle.getState() != Bundle.ACTIVE) {
				try {
					configBundle.start();
				} catch (BundleException e) {
					throw new IllegalStateException("Could not start Config-Bundle", e);
				}
			}
			ServiceReference ref = configBundle.getBundleContext().getServiceReference(PackageAdmin.class.getName());
			packageAdmin = (PackageAdmin) configBundle.getBundleContext().getService(ref);
		}
		if (logger.isDebugEnabled())
			logger.debug("Have PackageAdmin, resolving exported package for class {}", className);
		ExportedPackage exportedPackage = packageAdmin.getExportedPackage(className.substring(0, className.lastIndexOf('.')));
		if (logger.isDebugEnabled())
			logger.debug("Have exported package {}, {} " + exportedPackage.getName(), exportedPackage.getExportingBundle().getSymbolicName());
		if (exportedPackage != null) {
			try {
				return exportedPackage.getExportingBundle().loadClass(className).getClassLoader();
			} catch (ClassNotFoundException e) {
				// This should not happen, as we asked the PackageAdmin for the correct bundle
				throw new IllegalStateException("Bundle classloader for bundle " + exportedPackage.getExportingBundle().getSymbolicName() + " could not load class " + className);
			}
		}
		// As fallback we use the Classloader of Config.class
		return Config.class.getClassLoader();
	}
}
