/* ********************************************************************
 * NightLabsBase - Utilities by NightLabs                             *
 * Copyright (C) 2004-2008 NightLabs GmbH - http://NightLabs.org      *
 *                                                                    *
 * This library is free software; you can redistribute it and/or      *
 * modify it under the terms of the GNU Lesser General Public         *
 * License as published by the Free Software Foundation; either       *
 * version 2.1 of the License, or (at your option) any later version. *
 *                                                                    *
 * This library is distributed in the hope that it will be useful,    *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of     *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU  *
 * Lesser General Public License for more details.                    *
 *                                                                    *
 * You should have received a copy of the GNU Lesser General Public   *
 * License along with this library; if not, write to the              *
 *     Free Software Foundation, Inc.,                                *
 *     51 Franklin St, Fifth Floor,                                   *
 *     Boston, MA  02110-1301  USA                                    *
 *                                                                    *
 * Or get it online:                                                  *
 *     http://www.gnu.org/copyleft/lesser.html                        *
 **********************************************************************/
package org.nightlabs.config;

import java.beans.ExceptionListener;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * The manager for config objects called ConfigModules. Each subsystem can
 * register an own config-object, which will be serialized to and
 * deserialized from XML config files.
 * <p>
 * See {@link org.nightlabs.config.ConfigModule} for an introduction how to
 * set up a custom config object.
 * <p>
 * A plugins config-object is identified by its <code>Class</code> combined
 * with a unique identifier for this class, wich can be <code>null</code>.
 * <p>
 * Use the configuration framework in this way:
 * <p><blockquote><pre>
 * // create and get a shared instance:
 * Config config = Config.createSharedInstance(
 *     "MyConfig.xml",
 *     true,
 *     System.getProperty("user.home"));
 *
 * // create the config module lazily:
 * MyConfigModule myConfigModule = config
 *     .createConfigModule(MyConfigModule.class);
 *
 * // change some values:
 * myConfigModule.setConfigValueFoo("foo-changed");
 * myConfigModule.setConfigValueBar("bar-changed");
 *
 * // save the config (you might want to to this
 * // in a shutdown hook or something similar):
 * config.save(true);
 * </pre></blockquote>
 *
 * @version $Revision: 14382 $ - $Date: 2009-05-03 12:44:48 +0200 (So, 03 Mai 2009) $
 * @author Marco Schulze
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class Config
{
	// =================================
	// STATIC PROPERTIES
	// =================================

	/**
	 * The logger for this class.
	 */
	private static final Logger logger = Logger.getLogger(Config.class);

	/**
	 * The character used to append a unique identifier to a config file name.
	 */
	public static final char beforeIdentifier = '-';

	/**
	 * The character used to append a midfix to a config file name.
	 */
	public static final char beforeMidfix = '-';

	/**
	 * The character used to append the class name of the {@link ConfigModule}
	 * to a config file name.
	 */
	public static final char beforeClassName = '-';

	/**
	 * The shared instance
	 */
	private static Config sharedInstance;

	/**
	 * If there is a system property set with this name, the Config's static methods
	 * will delegate to an instance of the class specified by this system property.
	 */
	public static final String PROPERTY_KEY_CONFIG_FACTORY = ConfigFactory.class.getName();

	private static ConfigFactory configFactory = null;

	private synchronized static ConfigFactory getConfigFactory()
	{
		if (configFactory == null) {
			String configFactoryClassName = System.getProperty(PROPERTY_KEY_CONFIG_FACTORY);
			if (configFactoryClassName == null)
				return null;

			Class<?> configFactoryClass;
			try {
				configFactoryClass = Class.forName(configFactoryClassName);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException("The system-property '" + PROPERTY_KEY_CONFIG_FACTORY + "' was specified as '"+configFactoryClassName+"' but this class cannot be found!", e);
			}

			try {
				configFactory = (ConfigFactory) configFactoryClass.newInstance();
			} catch (Exception e) {
				throw new RuntimeException("Instantiating the class '"+configFactoryClassName+"' specified by the system-property '" + PROPERTY_KEY_CONFIG_FACTORY + "' was found, but it could not be instantiated!", e);
			}
		}
		return configFactory;
	}

	// =================================
	// STATIC METHODS
	// =================================

	/**
	 * Do we have a shared instance?
	 * @return <code>true</code> if we have already created a shared instance -
	 * 		<code>false</code> otherwise.
	 * @see #sharedInstance()
	 */
	public static boolean isSharedInstanceExisting()
	{
		ConfigFactory configFactory = getConfigFactory();
		if (configFactory != null)
			return configFactory.isSharedInstanceExisting();

		return sharedInstance != null;
	}

	/**
	 * Do we have a shared instance?
	 *
	 * @deprecated Use {@link #isSharedInstanceExisting()} instead.
	 * @return <code>true</code> if we have already created a shared instance.
	 * @see #sharedInstance()
	 */
	@Deprecated
	public static boolean isConfigExisting()
	{
		return isSharedInstanceExisting();
	}

	/**
	 * Creates the shared instance of Config with the given parameters.
	 *
	 * @param configFile A relative filename. This name is used as name for the
	 *            base config file and used to generate the include file names.
	 * @param loadConfFile Indicates wether or not to load the file contents now.
	 * @param configDir Where to look for the config files.
	 * @return The newly created shared instance.
	 * @throws ConfigException if creation of config object fails.
	 * @see #sharedInstance()
	 * @deprecated Use {@link #createSharedInstance(File)} instead!
	 */
	@Deprecated
	public static Config createSharedInstance(String configFile, boolean loadConfFile, String configDir)
	throws ConfigException
	{
		ConfigFactory configFactory = getConfigFactory();
		if (configFactory != null)
			throw new IllegalStateException("This deprecated method is not supported when using a ConfigFactory!");

		sharedInstance = new Config(configFile, loadConfFile, configDir);
		return sharedInstance;
	}

	/**
	 * This is a convenience method calling {@link #createSharedInstance(File, boolean)}
	 * with <code>loadConfFile = true</code>.
	 */
	public static Config createSharedInstance(File configFile)
	throws ConfigException
	{
		ConfigFactory configFactory = getConfigFactory();
		if (configFactory != null)
			return configFactory.createSharedInstance(configFile);

		return createSharedInstance(configFile, true);
	}

	/**
	 * Creates the shared instance of Config with the given parameters.
	 *
	 * @param configFile A relative or absolute filename. This name is used as name for the
	 *		base config file and used to generate the include file names. All include files will
	 *		be created in the same directory as this file.
	 * @return The newly created shared instance.
	 * @throws ConfigException if creation of config object fails.
	 * @see #sharedInstance()
	 */
	public static Config createSharedInstance(File configFile, boolean loadConfFile)
	throws ConfigException
	{
		ConfigFactory configFactory = getConfigFactory();
		if (configFactory != null)
			return configFactory.createSharedInstance(configFile, loadConfFile);

		sharedInstance = new Config(configFile, loadConfFile);
		return sharedInstance;
	}

	/**
	 * Get the shared instance.
	 *
	 * @param throwExceptionIfNotExisting Indicates whether or not to throw an
	 *     exception if shared instance does not yet exist.
	 * @return The shared config instance or <code>null</code> if <code>throwExceptionIfNotExisting</code>
	 *     is <code>false</code> and the shared instance does not yet exist.
	 * @throws IllegalStateException If <code>throwExceptionIfNotExisting</code>
	 *     is <code>true</code> and the shared instance does not yet exist.
	 */
	public static Config sharedInstance(boolean throwExceptionIfNotExisting)
	{
		ConfigFactory configFactory = getConfigFactory();
		if (configFactory != null)
			return configFactory.sharedInstance(throwExceptionIfNotExisting);

		if (sharedInstance == null && throwExceptionIfNotExisting)
			throw new IllegalStateException("Shared instance of Config was not yet created! You have to execute createSharedInstance() before using sharedInstance()!");
		return sharedInstance;
	}

	/**
	 * Get the shared instance. This is a convenience method for
	 * <code>sharedInstance(true)</code>.
	 *
	 * @return The shared config instance.
	 * @throws IllegalStateException If the shared instance does not yet exist.
	 */
	public static Config sharedInstance()
	{
		return sharedInstance(true);
	}


	// =================================
	// PRIVATE PROPERTIES
	// =================================


//	/**
//	* Base directory for configuration files.
//	*/
//	private String configDir;

//	/**
//	* The filename for the basic config file. All include config file names
//	* are also created using this property.
//	*/
//	private String configFilename;

	private File configFile;
	private File configDir;
	private String includeFileMidfix;
	private boolean loaded = false;

	protected void assertLoaded()
	{
		if (!loaded)
			throw new IllegalStateException("load(...) has not been called!");
	}

	/**
	 * These objects get read from and written to the xml-config file via Castor.
	 * Each server-plugin can register a config plugin via {@link #putConfigModule(ConfigModule)}.
	 */
	private HashMap<String, ConfigModule> configModules = new HashMap<String, ConfigModule>();

	/**
	 * Key: String relativeCfModFileName<br/>
	 * Value: ConfigModule cfMod
	 */
	private HashMap<String, ConfigModule> includedCfModsByFileName = new HashMap<String, ConfigModule>();

	/**
	 * Key: ConfigModule cfMod<br/>
	 * Value: String relativeCfModFileName
	 */
	private HashMap<ConfigModule, String> includedCfModsByCfMod = new HashMap<ConfigModule, String>();

	/**
	 * A mutex for loading and saving config files.
	 */
	protected Object ioMutex = new Object();

	/**
	 * This Set is used to remember wich config modules have been
	 * created lazily for a user request.
	 * <p>
	 * These config modules won't be added to the config includes
	 * if they have not changed and thus are not written to disk.
	 * <p>
	 * The instance of the Set itself will be created lazily when
	 * needed.
	 */
	private HashSet<String> _lazyConfigModules = null;

	/**
	 * Indicate whether the given config module was created lazily
	 * (i.e. by a call to {@link #createConfigModule(Class, String)}).
	 * @param cfMod The config module to check
	 * @return <code>true</code> if the given config module was created
	 * 			lazily - <code>false</code> otherwise.
	 */
	protected boolean isLazyConfigModule(ConfigModule cfMod)
	{
		return
		_lazyConfigModules != null &&
		_lazyConfigModules.contains(getConfigModuleIdentifyingName(cfMod));
	}

	/**
	 * Register a lazily created config module.
	 * @param cfMod The config module to register.
	 */
	protected void registerLazyConfigModule(ConfigModule cfMod)
	{
		if (_lazyConfigModules == null)
			_lazyConfigModules = new HashSet<String>();
		_lazyConfigModules.add(getConfigModuleIdentifyingName(cfMod));
	}

	/**
	 * Lazy ConfigModules are removed from the list when
	 * written once (because they have changed), so they
	 * won't be omitted the next time.
	 *
	 * @param cfMod The config module to remove
	 */
	protected void removeLazyConfigModule(ConfigModule cfMod)
	{
		if (_lazyConfigModules == null)
			return;
		_lazyConfigModules.remove(getConfigModuleIdentifyingName(cfMod));
	}


	// =================================
	// CONSTRUCTORS
	// =================================

	/**
	 * Create a config object using an <code>InputStream</code> as source.
	 *
	 * @param configStream The stream to read the config from.
	 * @throws ConfigException If something fails.
	 */
	public Config(InputStream configStream)
	throws ConfigException
	{
		this.readFromInputStream(configStream, Config.class.getClassLoader());
	}

	/**
	 * Creates a config object with the given parameters.

	 * @param configFile A relative filename. This name is used as name for the
	 *            base config file and used to generate the include file names.
	 * @param loadConfFile Indicates wether or not to load the file contents now. Note, that config modules
	 *		are now ALWAYS loaded lazily!
	 * @param configDir Where to look for the config files.
	 *
	 * @throws ConfigException if creation of config object fails.
	 * @see #createSharedInstance(String, boolean, String)
	 * @see #sharedInstance()
	 * @see #Config(String configFile, boolean loadConfFile, String configDir)
	 * @deprecated Use {@link #Config(File, boolean)} instead.
	 */
	@Deprecated
	public Config(String configFile, boolean loadConfFile, String configDir)
	throws ConfigException
	{
		this(new File(configDir, configFile), loadConfFile);
	}

	/**
	 * Creates a config object with the given parameters.

	 * @param configFile A relative or absolute filename. This file is used as name for the
	 *		base config file and used to generate the include file names. All include files will
	 *		be created in the same directory as this file.
	 */
	public Config(File configFile)
	throws ConfigException
	{
		this(configFile, true);
	}

	/**
	 * Creates a config object with the given parameters.

	 * @param configFile A relative or absolute filename. This name is used as name for the
	 *		base config file and used to generate the include file names. All include files will
	 *		be created in the same directory as this file.
	 * @param loadConfFile If true, the method {@link #load(String)} will be called with
	 *
	 * @throws ConfigException if creation of config object fails.
	 * @see #createSharedInstance(String, boolean, String)
	 * @see #sharedInstance()
	 * @see #Config(String configFile, boolean loadConfFile, String configDir)
	 *
	 * @deprecated It makes not much sense to create a config without loading it. Therefore this constructor has been marked deprecated.
	 */
	@Deprecated
	public Config(File configFile, boolean loadConfFile)
	throws ConfigException
	{
		this.setConfigFile(configFile);
		if (loadConfFile)
			load(null);

//		try {
//		/**
//		* Because the framework can be used with different application-plugins,
//		* we give configDir as parameter. Application parameters are given to the
//		* app as array parameter of the main method.
//		*/
//		this.setConfigDir(configDir);
//		configDir = this.configDir;

//		logger.info("configDir = \""+this.configDir+"\"");

//		if (configFile == null || "".equals(configFile))
//		configFile = configDir + "config.xml";
//		else {
//		File cf = new File(configFile);
//		if (! cf.isAbsolute())
//		cf = new File(configDir + configFile);

//		configFile = cf.getCanonicalPath();
//		}

//		this.configFilename = configFile;

//		logger.info("configFilename = \""+this.configFilename+"\"");

//		if (loadConfFile)
//		this.load();
//		} catch (ConfigException x) {
//		throw x;
//		} catch (Exception x) {
//		throw new ConfigException(x);
//		}
	}


	// =================================
	// ACCESSOR METHODS
	// =================================

	public File getConfigFile()
	{
		return configFile;
	}

	/**
	 * After calling this method, you must call {@link #load(String)} again!
	 *
	 * @param configFile Sets a new main config file.
	 */
	public void setConfigFile(File configFile)
	{
		loaded = false;
		this.configFile = configFile;
		this.configDir = configFile.getParentFile();
		loadedConfigModuleClassNames.clear();
		configModules.clear();
		if (_lazyConfigModules != null)
			_lazyConfigModules.clear();
	}


//	/**
//	 * Get the filename for the basic config file. All include config file names
//	 * are also created using this filename.
//	 * @return The configFilename
//	 */
//	public String getConfigFilename()
//	{
//		return configFile.getName();
////		return configFilename;
//	}

//	/**
//	* Get the filename for the basic config file. All include config file names
//	* are also created using this filename.
//	* @param configFilename The new value.
//	*/
//	public void setConfigFilename(String configFilename)
//	{
//	this.configFilename = configFilename;
//	}

	/**
	 * Get the directory where this instance of Config
	 * will store its config XML files.
	 * @return The directory where this instance of Config
	 * will store its config XML files.
	 */
	public File getConfigDir()
	{
		return configDir;
	}

//	/**
//	* Set the directory where this instance of Config
//	* will store its config XML files.
//	* @param configDir The directory to use for config files.
//	* @throws ConfigException in case of an error
//	*/
//	public void setConfigDir(String configDir)
//	throws ConfigException
//	{
//	if(configDir == null)
//	throw new NullPointerException("configDir must not be null");
//	File fcfdir = new File(configDir);
//	try {
//	this.configDir = Utils.addFinalSlash(fcfdir.getCanonicalPath());
//	} catch (IOException e) {
//	throw new ConfigException(e);
//	}
//	}

	// =================================
	// METHODS
	// =================================

	/**
	 * Load the configuration from files without using a midfix.
	 *
	 * @throws ConfigException if loading the config fails.
	 * @see #loadConfFile(String includeFileMidfix)
	 *
	 * @deprecated You should not call this method directly - all non-deprecated ways to create/obtain a Config
	 *		return a loaded Config, anyway.
	 */
	@Deprecated
	public void load()
	throws ConfigException
	{
		load(null);
	}

	/**
	 * Load the configuration from files.
	 *
	 * @param includeFileMidfix The file midfix to use. This is used by language
	 *     config sub-system. If <code>includeFileMidfix</code> is <code>null</code>,
	 *     no midfixes are used.
	 * @throws ConfigException if loading the config fails.
	 *
	 * @deprecated You should not call this method directly - all non-deprecated ways to create/obtain a Config
	 *		return a loaded Config, anyway.
	 */
	@Deprecated
	public void load(String includeFileMidfix)
	throws ConfigException
	{
		synchronized (ioMutex) {
			try {
				logger.info("Loading config with includeFileMidfix=\""+includeFileMidfix+"\"");

				loaded = true;

				configModules.clear();
				loadedConfigModuleClassNames.clear();
				loadedConfigModuleClassNames.add(ConfigIncludes.class.getName());
				if (_lazyConfigModules != null)
					_lazyConfigModules.clear();

				if (configFile.exists()) {
					FileInputStream fin = new FileInputStream(configFile);
					try {
						readFromInputStream(fin, Config.class.getClassLoader());
						if(logger.isDebugEnabled())
							logger.debug("Config file read: "+configFile.getAbsolutePath());
					} finally {
						fin.close();
					}
				}

				ConfigIncludes configIncludes = getConfigModule(ConfigIncludes.class, false);

				if (configIncludes == null) {
					configIncludes = new ConfigIncludes();
					configIncludes._setConfig(this);
					configIncludes.init();
					putConfigModule(configIncludes);
				} // if (configIncludes == null) {

//				// iterate all config includes
//				for (Iterator iterator = configIncludes.getConfigIncludes().iterator(); iterator.hasNext(); ) {
//				ConfigInclude configInclude = (ConfigInclude)iterator.next();
//				String relativeIncludeFileNameOrg = configInclude.getIncludeFile();
//				String relativeIncludeFileNameWithMidfix = createIncludeFileName(
//				relativeIncludeFileNameOrg,
//				null,
//				includeFileMidfix);

//				try {

//				// assumed location of config module include file on disk
//				File includeFile = new File(configDir, relativeIncludeFileNameWithMidfix);

//				if(!includeFile.exists()) {
//				URL url = null;
//				// search in package path
//				if(configInclude.getSearchClass() != null) {
//				url = Class.forName(configInclude.getSearchClass()).
//				getResource(
//				relativeIncludeFileNameWithMidfix);
//				}
//				if(url == null)  {
//				// search in config modules resources
//				url = Class.forName(configInclude.getIncludeClass()).
//				getResource(
//				relativeIncludeFileNameWithMidfix);
//				if (url == null) {
//				// The resource can not be assigned, thus we don't have any
//				// serialized configuration.
//				throw new ConfigException("Configuration file "
//				+ relativeIncludeFileNameWithMidfix + " cannot be found. File does not exist in ConfigDir or class resource directory.");
//				}
//				}
//				includeFile = new File(url.getFile());
//				}

//				// read include file from input stream
//				fin = new FileInputStream(includeFile);
//				try {
//				ConfigModule cfMod;
//				XMLDecoder d = new XMLDecoder(fin);
//				try {
//				cfMod = (ConfigModule)d.readObject();
//				cfMod.setChanged(false);
//				if(logger.isDebugEnabled())
//				logger.debug("Config file read: "+includeFile.getAbsolutePath());
//				} finally {
//				d.close();
//				}

//				// get deserialized config module, call init() and add it to local lists.
//				cfMod._setConfig(this);
//				cfMod.init();
//				this.putIncludedCfMod(relativeIncludeFileNameWithMidfix, cfMod);
//				this.configModules.put(getConfigModuleIdentifyingName(cfMod), cfMod);

//				} finally {
//				fin.close();
//				}

//				} catch (Exception x) {
//				logger.warn(
//				"Unable to load config module file: "+relativeIncludeFileNameWithMidfix,
//				x);
//				}

//				} // for (Iterator itCfIncs = configIncludes.getConfigIncludeFiles().iterator(); itCfIncs.hasNext(); ) {

			} catch (ConfigException x) {
				loaded = false;
				throw x;
			} catch (Throwable x) {
				loaded = false;
				throw new ConfigException(x);
			}
		} // synchronized (ioMutex) {
	}

	private ClassLoader classLoader;

	public ClassLoader getClassLoader() {
		return classLoader;
	}
	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	private ClassLoader getDefaultClassLoader()
	{
		if (classLoader == null)
			return Config.class.getClassLoader();
		else
			return classLoader;
	}

	private XMLDecoder createXMLDecoder(InputStream in, ExceptionListener exceptionListener, ClassLoader classLoader)
	{
		return new XMLDecoder(
				in,
				null,
				exceptionListener,
				classLoader == null ? getDefaultClassLoader() : classLoader
		);
	}


	private Set<String> loadedConfigModuleClassNames = new HashSet<String>();

	private void loadConfigModulesForClass(Class<? extends ConfigModule> configModuleClass, String configModuleClassName)
	{
		assertLoaded();

		if (configModuleClass == null && configModuleClassName == null)
			throw new IllegalArgumentException("Arguments configModuleClass and configModuleClassName must not both be null! One of them must be assigned!");

		if (configModuleClass != null)
			configModuleClassName = configModuleClass.getName();
		else {
			try {
				configModuleClass = (Class<? extends ConfigModule>) getDefaultClassLoader().loadClass(configModuleClassName);
			} catch (ClassNotFoundException e) {
				configModuleClass = null;
				logger.warn("Loading ConfigModule-class " + configModuleClassName + " failed.", e);
			}
		}

		synchronized (ioMutex) {
			if (loadedConfigModuleClassNames.contains(configModuleClassName))
				return;

			loadedConfigModuleClassNames.add(configModuleClassName);

			ConfigIncludes configIncludes = getConfigModule(ConfigIncludes.class, true); // TODO is it really existing now?
			List<ConfigInclude> configIncludeList = configIncludes.getConfigIncludeListForConfigModuleClass(configModuleClassName);

			if (configIncludeList == null)
				return;

			for (Object element : configIncludeList) {
				ConfigInclude configInclude = (ConfigInclude)element;
				String relativeIncludeFileNameOrg = configInclude.getIncludeFile();
				String relativeIncludeFileNameWithMidfix = createIncludeFileName(
						relativeIncludeFileNameOrg,
						null,
						includeFileMidfix);

				try {

					// assumed location of config module include file on disk
					URL includeFileURL;
					{
						File includeFile = new File(configDir, relativeIncludeFileNameWithMidfix);

						if(includeFile.exists())
							includeFileURL = includeFile.toURI().toURL();
						else {
							URL url = null;
							// search in package path
							if(configInclude.getSearchClass() != null) {
								url = Class.forName(configInclude.getSearchClass()).
								getResource(
										relativeIncludeFileNameWithMidfix);
							}
							if(url == null)  {
								// search in config modules resources
								url = Class.forName(configInclude.getIncludeClass()).
								getResource(relativeIncludeFileNameWithMidfix);
								if (url == null) {
									// The resource can not be assigned, thus we don't have any
									// serialized configuration.
									throw new ConfigException("Configuration file "
											+ relativeIncludeFileNameWithMidfix + " cannot be found. File does not exist in:"+ configDir+" or class resource directory.");
								}
							}
							includeFileURL = url;
						}
					}

					// read include file from input stream
					InputStream in = includeFileURL.openStream();
					try {
						ConfigModule cfMod = null;
						RuntimeException readFileException = null;
						loopTryDifferentClassLoaders: for (int classLoaderMode = 0; classLoaderMode < 2; ++classLoaderMode) {
							if (cfMod != null)
								break loopTryDifferentClassLoaders;

							ClassLoader cl = null;
							switch (classLoaderMode) {
								case 0:
									if (configModuleClass == null)
										continue loopTryDifferentClassLoaders;

									cl = configModuleClass.getClassLoader();
									break;
								case 1:
									cl = null;

								default:
									throw new IllegalStateException("Unknown classLoaderMode: " + classLoaderMode);
							}
							XMLDecoder d = createXMLDecoder(
									in,
									new ConfigExceptionListener("Error reading config module file \"" + includeFileURL + "\"!"),
									cl
							);
							try {
								cfMod = (ConfigModule)d.readObject();
								cfMod.setChanged(false);
								if(logger.isDebugEnabled())
									logger.debug("Config file read: "+includeFileURL);

								readFileException = null;
							} catch (RuntimeException x) { // our ConfigExceptionListener encapsulates every exception in a RuntimeException
								readFileException = x;
							} finally {
								d.close();
							}
						}
						if (readFileException != null)
							throw readFileException;

						// get deserialized config module, call init() and add it to local lists.
						cfMod._setConfig(this);
						cfMod.init();
						this.putIncludedCfMod(relativeIncludeFileNameWithMidfix, cfMod);
						this.configModules.put(getConfigModuleIdentifyingName(cfMod), cfMod);

					} finally {
						in.close();
					}

				} catch (Exception x) {
					logger.warn(
							"Unable to load config module file: "+relativeIncludeFileNameWithMidfix,
							x);
				}

			} // for (Iterator iterator = configIncludeList.iterator(); iterator.hasNext(); ) {
		} // synchronized (ioMutex) {
	}

	private static class ConfigExceptionListener implements ExceptionListener
	{
		private String message;
		public ConfigExceptionListener(String message) {
			this.message = message;
		}

		@Override
		public void exceptionThrown(Exception e) {
			logger.error(message + " :: " + e, e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Load the config from file without using a midfix.
	 *
	 * @deprecated Use {@link #load()} instead.
	 * @throws ConfigException if loading the config fails.
	 * @see #loadConfFile(String includeFileMidfix)
	 */
	@Deprecated
	public void loadConfFile()
	throws ConfigException
	{
		load(null);
	}

	/**
	 * @deprecated Use {@link #load(String)} instead.
	 * @param includeFileMidfix The file midfix to use. This is used by language
	 *     config sub-system. If <code>includeFileMidfix</code> is <code>null</code>,
	 *     no midfixes are used.
	 * @throws ConfigException if loading the config fails.
	 */
	@Deprecated
	public void loadConfFile(String includeFileMidfix)
	throws ConfigException
	{
		load(includeFileMidfix);
	}

	/**
	 * Save all registered config modules of this instance of
	 * Config into their corresponding configuration XML-files.
	 * <p>
	 * This is a convenience method that calls <code>save(null, false)</code>.
	 *
	 * @see #save(String, boolean)
	 * @throws ConfigException In case of an error
	 */
	public void save()
	throws ConfigException
	{
		save(null, false);
	}

	/**
	 * Save all registered config modules of this instance of
	 * Config into their corresponding configuration XML-files.
	 * <p>
	 * This is a convenience method that calls <code>save(null, forceSaveAllModules)</code>.
	 *
	 * @param forceSaveAllModules Usually, only config modules that are marked as changed
	 *						are written to disk. If forceSaveAllModules is true, all config modules
	 *						are saved and the changed flag is ignored.
	 * @see #save(String, boolean)
	 * @throws ConfigException In case of an error
	 */
	public void save(boolean forceSaveAllModules)
	throws ConfigException
	{
		save(null, forceSaveAllModules);
	}

	/**
	 * Save all registered config modules of this instance of
	 * Config into their corresponding configuration XML-files.
	 * <p>
	 * This is a convenience method that calls <code>save(includeFileMidfix, false)</code>.
	 *
	 * @param includeFileMidfix The midfix <code>String</code> to distinguish
	 *            different versions of the same configuration module as used
	 *            in language configuration.
	 *            <p>If <code>includeFileMidfix</code> is <code>null</code>, no
	 *            midfix will be used (standard behaviour).
	 * @see #save(String, boolean)
	 * @throws ConfigException In case of an error
	 */
	public void save(String includeFileMidfix)
	throws ConfigException
	{
		save(includeFileMidfix, false);
	}

	/**
	 * Save all registered config modules of this instance of
	 * Config into their corresponding configuration XML-files.
	 * <p>
	 * First, it creates a temporary file (with a pretty unique suffix including
	 * time stamp and random number ending on ".new") and replaces the real config by this one after the
	 * config could be successfully serialized. This avoids to crash the config
	 * file if the marshaller throws an exception.
	 * <p>
	 * During the save process the config modules that are saved are locked via
	 * {@link org.nightlabs.util.RWLock}. The save method itsself is synchronized
	 * with a mutex making double calls to this method impossible.
	 *
	 * @param includeFileMidfix The midfix <code>String</code> to distinguish
	 *            different versions of the same configuration module as used
	 *            in language configuration.
	 *            <p>If <code>includeFileMidfix</code> is <code>null</code>, no
	 *            midfix will be used (standard behaviour).
	 * @param forceSaveAllModules Usually, only config modules that are marked as changed
	 *						are written to disk. If forceSaveAllModules is true, all config modules
	 *						are saved and the changed flag is ignored.
	 * @throws ConfigException In case of an error
	 */
	public void save(String includeFileMidfix, boolean forceSaveAllModules)
	throws ConfigException
	{
		// BEGIN temporary workaround
		// TODO implement a clean way to get the Config running in RAP. Maybe use a user-dependent config-provider (pseudo-shared-instance).
		String wsSysPropKey = "osgi.ws";
		String wsSysPropVal = System.getProperty(wsSysPropKey);
		if ("rap".equalsIgnoreCase(wsSysPropVal))
			logger.warn("save: System property '" + wsSysPropKey + "' is '" + wsSysPropVal + "'. Skipping!!!");
		else if (logger.isDebugEnabled())
			logger.debug("save: System property '" + wsSysPropKey + "' is '" + wsSysPropVal + "'. Saving normally.");
		// END temporary workaround
		
		String tempConfigFileSuffix = '.' + Long.toString(System.currentTimeMillis(), 36) + '-' + Integer.toString(System.identityHashCode(this), 36) + '-' + Integer.toString((int)(1296 * Math.random()), 36) + ".new";
		List<File> temporaryFilesToCleanup = new LinkedList<File>();
		synchronized (ioMutex) {
			logger.info("Saving config with includeFileMidfix=\""+includeFileMidfix+"\"");
			try {
				List<ConfigModule> internalConfigModules = new ArrayList<ConfigModule>(this.configModules.size());

				ConfigIncludes configIncludes = null;
				HashSet<ConfigInclude> origIncludes = null;

				// get the config modules responsable for included config modules
				configIncludes = getConfigModule(ConfigIncludes.class, false);
				if (configIncludes == null) {
					// if we don't have config includes create them and add them this config
					configIncludes = new ConfigIncludes();
					configIncludes._setConfig(this);
					configIncludes.init();
					putConfigModule(configIncludes);
				}
				else {
					// if we have an old set of include files, clear it
					origIncludes = new HashSet<ConfigInclude>(configIncludes.getConfigIncludes());
					configIncludes.getConfigIncludes().clear();
				}

				// This map is used to save changed config modules (all modules that must
				// be written in multi file mode due to a change) to avoid multiple calls
				// of _isChanged().
				HashMap<String, ConfigModule> changedIncludedConfigModules = new HashMap<String, ConfigModule>();

				for (Map.Entry<String, ConfigModule> entry : configModules.entrySet()) {
					ConfigModule cfMod = entry.getValue();

					String relativeCfIncFileName = getIncludedCfModFileName(cfMod);
					ConfigInclude cfInc = new ConfigInclude(
							cfMod, relativeCfIncFileName
					);

					// remove this ConfigInclude from the old ones as it is re-added
					if (origIncludes != null)
						origIncludes.remove(cfInc);

					if(relativeCfIncFileName == null) {
						// inline config modules
						internalConfigModules.add(cfMod);
					}
					else {
						if(!isLazyConfigModule(cfMod) || cfMod._isChanged() || forceSaveAllModules)
							configIncludes.getConfigIncludes().add(new ConfigInclude(cfMod, relativeCfIncFileName));
						if(cfMod._isChanged() || forceSaveAllModules) {
							// external changed config modules
							changedIncludedConfigModules.put(relativeCfIncFileName, includedCfModsByFileName.get(relativeCfIncFileName));
						}
					}
				} // for (Iterator it = configModules.entrySet().iterator(); it.hasNext(); ) {

				// add all old includes that were not lazily loaded during this session
				if (origIncludes != null) {
					for (ConfigInclude configInclude : origIncludes)
						configIncludes.getConfigIncludes().add(configInclude);
				}

				// write inline configuration vector to temporary file
				String tmpCf = configFile.getAbsolutePath() + tempConfigFileSuffix;
				File tmpCfFile = new File(tmpCf);
				temporaryFilesToCleanup.add(tmpCfFile);

				FileOutputStream fout = new FileOutputStream(tmpCfFile);
				try {
					XMLEncoder encoder = new XMLEncoder(fout);
					try {
						encoder.setExceptionListener(new ConfigExceptionListener("Error writing main config file \"" + tmpCf + "\"!"));
						encoder.writeObject(internalConfigModules);
					} finally {
						encoder.close();
					}
				} finally {
					fout.close();
				}

				// write all include files that have changed to temporary files.
				if(!changedIncludedConfigModules.isEmpty()) {
					for (Map.Entry<String, ConfigModule> entry : changedIncludedConfigModules.entrySet()) {
						String includedConfigModuleFilename = entry.getKey();
						ConfigModule includedConfigModule = entry.getValue();

						if(logger.isDebugEnabled())
							logger.debug("waiting for readLock of configModule \"" + includedConfigModuleFilename + "\"...");
						includedConfigModule.acquireReadLock();
						try {
							boolean successfullySaved = false;
							includedConfigModule.beforeSave();
							try {
								if(logger.isDebugEnabled())
									logger.debug("got readLock of configModule \"" + includedConfigModuleFilename + "\". Opening file...");

								File tmpCfModFile = new File(configDir, includedConfigModuleFilename + tempConfigFileSuffix);
								temporaryFilesToCleanup.add(tmpCfModFile);

								fout = new FileOutputStream(tmpCfModFile);
								try {
									XMLEncoder encoder = new XMLEncoder(fout);
									try {
										encoder.setExceptionListener(new ConfigExceptionListener("Error writing config module file \"" + tmpCfModFile.getAbsolutePath() + "\"!"));

										if(logger.isDebugEnabled())
											logger.debug("opened file for configModule \"" + includedConfigModuleFilename + "\". writing object...");
										encoder.writeObject(includedConfigModule);
										includedConfigModule.setChanged(false);
									} finally {
										encoder.close();
									}
								} finally {
									fout.close();
								}
								if (isLazyConfigModule(includedConfigModule))
									removeLazyConfigModule(includedConfigModule);

								if(logger.isDebugEnabled())
									logger.debug("configModule \"" + includedConfigModuleFilename + "\" written.");

								successfullySaved = true;
							} finally {
								includedConfigModule.afterSave(successfullySaved);
							}
						} finally {
							includedConfigModule.releaseLock();
						}
						if(logger.isDebugEnabled())
							logger.debug("read lock of configModule \"" + includedConfigModuleFilename + "\" released.");

					} // for (Iterator it = changedIncludedConfigModules.entrySet().iterator(); it.hasNext(); ) {
				}

				// copy new base config file over old
				configFile.delete();
				tmpCfFile.renameTo(configFile);
				if(logger.isDebugEnabled())
					logger.debug("Config file written: "+configFile.getAbsolutePath());

				// copy all changed include config files
				if(!changedIncludedConfigModules.isEmpty()) {
					for (Object element : changedIncludedConfigModules.keySet()) {
						String includedConfigModuleFilename = (String) element;

						File tmpCfModFile = new File(configDir, includedConfigModuleFilename + tempConfigFileSuffix);
						File cfModFile = new File(configDir, includedConfigModuleFilename);
						cfModFile.delete();
						tmpCfModFile.renameTo(cfModFile);
						if(logger.isDebugEnabled())
							logger.debug("Config file written: "+cfModFile.getAbsolutePath());
					} // for (Iterator it = changedIncludedConfigModules.entrySet().iterator(); it.hasNext(); ) {
				}

				temporaryFilesToCleanup.clear();
			} catch (Exception x) {
				throw new ConfigException(x);
			} finally {
				// In case of an error, we delete all temporary config files.
				for (File f : temporaryFilesToCleanup) {
					try {
						f.delete();
					} catch (Exception x) {
						// ignore
					}
				}
			}
		} // synchronized (ioMutex) {
	}


	/**
	 * This method calls <tt>saveConfFile(String includeFileMidfix, boolean forceSaveAllModules)</tt>
	 * with <tt>includeFileMidFix=null</tt> and <tt>forceSaveAllModules=false</tt>.
	 *
	 * @deprecated Use {@link #save()} instead.
	 * @throws ConfigException in case of an error
	 * @see #saveConfFile(String, boolean)
	 */
	@Deprecated
	public void saveConfFile()
	throws ConfigException
	{
		save();
	}

	/**
	 * This method calls <tt>saveConfFile(String includeFileMidfix, boolean forceSaveAllModules)</tt>
	 * with <tt>includeFileMidFix=null</tt>.
	 *
	 * @deprecated Use {@link #save(boolean)} instead.
	 * @throws ConfigException in case of an error
	 * @see #saveConfFile(String, boolean)
	 */
	@Deprecated
	public void saveConfFile(boolean forceSaveAllModules)
	throws ConfigException
	{
		save(forceSaveAllModules);
	}

	/**
	 * This method calls <tt>saveConfFile(String includeFileMidfix, boolean forceSaveAllModules)</tt>
	 * with <tt>forceSaveAllModules=false</tt>.
	 *
	 * @deprecated Use {@link #save(String)} instead.
	 * @throws ConfigException in case of an error
	 * @see #saveConfFile(String, boolean)
	 */
	@Deprecated
	public void saveConfFile(String includeFileMidfix)
	throws ConfigException
	{
		save(includeFileMidfix);
	}

	/**
	 * This method writes all via <code>putConfigModule()</code> registered ConfigModules into
	 * the configuration XML-file.
	 * <p>
	 * First, it creates a temporary file (with the
	 * extension ".new") and replaces the real config by this one after the
	 * config could be successfully serialized. This avoids to crash the config
	 * file if the marshaller throws an exception.
	 *
	 * @deprecated Use {@link #save(String, boolean)} instead.
	 * @param includeFileMidfix The midfix <code>String</code> to distinguish
	 *            different versions of the same configuration module as used
	 *            in language configuration.
	 *            <p>If <code>includeFileMidfix</code> is <code>null</code>, no
	 *            midfix will be used (standard behaviour).
	 * @param forceSaveAllModules Normally, only config modules that are marked as changed
	 *						are written to disk. If forceSaveAllModules is true, all config modules
	 *						are saved and the changed flag is ignored.
	 * @throws ConfigException in case of an error
	 */
	@Deprecated
	public void saveConfFile(String includeFileMidfix, boolean forceSaveAllModules)
	throws ConfigException
	{
		save(includeFileMidfix, forceSaveAllModules);
	}


	// =================================
	// CONFIG MODULE MAMAGEMENT PROPERTIES
	// =================================

	/**
	 * Create an instance of the given ConfigModule or return
	 * an existing one that has been previously created.
	 *
	 * @param configModuleClass
	 * @return The requested ConfigModule
	 * @throws ConfigException in case of an error
	 * @see #createConfigModule(Class configModuleClass, String identifier)
	 */
	public <T extends ConfigModule> T createConfigModule(Class<T> configModuleClass)
	throws ConfigException
	{
		return createConfigModule(configModuleClass, null);
	}

	/**
	 * Get a config module from this Config instance and create it if it does
	 * not yet exist.
	 * <p>
	 * A config module is identified by its <code>Class</code> combined
	 * with a unique identifier for this class, wich can be <code>null</code>.
	 * <p>
	 * This method will never return null, but create an instance of the given class.
	 * If the instance does already exist, it will return the existing instance.
	 *
	 * @param configModuleClass The class of the config module.
	 * @param identifier An identifier for that special config module if
	 * 			you need different modules of the same class. This parameter
	 * 			can be <code>null</code>.
	 * @return An instance of <code>configModuleClass</code> with given
	 * 			<code>identifier</code>.
	 * @throws ConfigException in case of an error
	 * @see #getConfigModule(Class, String)
	 */
	public <T extends ConfigModule> T createConfigModule(Class<T> configModuleClass, String identifier)
	throws ConfigException
	{
		loadConfigModulesForClass(configModuleClass, null);

		try {
			ConfigModule cfMod = configModules.get(getConfigModuleIdentifyingName(configModuleClass, identifier));
			if (cfMod == null) {
				cfMod = configModuleClass.newInstance();
				cfMod._setConfig(this);
				cfMod.setIdentifier(identifier);
				cfMod.init();
				configModules.put(getConfigModuleIdentifyingName(cfMod), cfMod);
				registerLazyConfigModule(cfMod);
			}

			StringBuffer sbPrefix = new StringBuffer(configFile.getName());
			int lastSlashPos = sbPrefix.lastIndexOf(File.separator);
			sbPrefix.delete(0, lastSlashPos + 1);
			int lastDotPos = sbPrefix.lastIndexOf(".");
			// if there is no '.' in the filename use end of string
			if(lastDotPos == -1)
				lastDotPos = sbPrefix.length();
			String suffix = sbPrefix.substring(lastDotPos);
			sbPrefix.delete(lastDotPos, sbPrefix.length());


			putIncludedCfMod(
					sbPrefix.toString()+beforeClassName+
					//configModuleClass.getName()+ //
					getConfigModuleIdentifyingName(cfMod)+
					suffix,
					cfMod
			);

			return configModuleClass.cast(cfMod);
		} catch (Throwable t) {
			throw new ConfigException(t);
		}
	}

	/**
	 * Get a config module from this Config instance.
	 * <p>
	 * This is a convenience method for
	 * <code>getConfigModule(configModuleClassName, null, true)</code>.
	 * <p>
	 * If one instance of the type that <code>configModuleClassName</code>
	 * points to is registered, it will be returned. Otherwise a
	 * <code>ConfigModuleNotFoundException</code> will be thrown.
	 * <p>
	 * If you want a config module to be lazily created, call
	 * {@link #createConfigModule(Class)}.
	 *
	 * @param configModuleClassName The full qualified class name of the
	 * 			config module.
	 * @return The registered ConfigModule.
	 * @throws ConfigModuleNotFoundException if the config module was not found.
	 */
	public ConfigModule getConfigModule(String configModuleClassName)
	{
		return getConfigModule(configModuleClassName, null, true);
	}

	/**
	 * Get a config module from this Config instance.
	 * <p>
	 * This is a convenience method for
	 * <code>getConfigModule(configModuleClassName, identifier, true)</code>.
	 * <p>
	 * A config module is identified by its <code>Class</code> combined
	 * with a unique identifier for this class, wich can be <code>null</code>.
	 * <p>
	 * If one instance of the type that <code>configModuleClassName</code>
	 * and <code>identifier</code> point to is registered, it will be returned.
	 * Otherwise a <code>ConfigModuleNotFoundException</code> will be thrown.
	 * <p>
	 * If you want a config module to be lazily created, call
	 * {@link #createConfigModule(Class, String)}.
	 *
	 * @param configModuleClassName The full qualified class name of the
	 * 			config module.
	 * @param identifier An identifier for that special config module if
	 * 			you need different modules of the same class. This parameter
	 * 			can be <code>null</code>.
	 * @return The registered ConfigModule.
	 * @throws ConfigModuleNotFoundException if the config module was not found.
	 */
	public ConfigModule getConfigModule(String configModuleClassName, String identifier)
	{
		return getConfigModule(configModuleClassName, identifier, true);
	}

	/**
	 * Get a config module from this Config instance.
	 * <p>
	 * This is a convenience method for
	 * <code>getConfigModule(configModuleClassName, null, throwExceptionIfNotExistent)</code>.
	 * <p>
	 * If one instance of the type that <code>configModuleClassName</code>
	 * points to is registered, it will be returned. Otherwise the result
	 * of this method will be <code>null</code>.
	 * If you set <code>throwExceptionIfNotExistent = true</code> a
	 * <code>ConfigModuleNotFoundException</code> will be thrown instead
	 * of returning <code>null</code>.
	 * <p>
	 * If you want a config module to be lazily created, call
	 * {@link #createConfigModule(Class)}.
	 *
	 * @param configModuleClassName The full qualified class name of the
	 * 			config module.
	 * @param throwExceptionIfNotExistent If set to <code>true</code> this
	 * 			method will throw a {@link ConfigModuleNotFoundException} in
	 * 			case that the desired config module could not be found.
	 * @return The registered ConfigModule or <code>null</code> if no ConfigModule
	 * 			could be found.
	 * @throws ConfigModuleNotFoundException if the config module was not found and
	 *     <code>throwExceptionIfNotExistent</code> is set to <code>true</code>.
	 */
	public ConfigModule getConfigModule(String configModuleClassName, boolean throwExceptionIfNotExistent)
	{
		return getConfigModule(configModuleClassName, null, throwExceptionIfNotExistent);
	}

	/**
	 * Get a config module from this Config instance.
	 * <p>
	 * A config module is identified by its <code>Class</code> combined
	 * with a unique identifier for this class, wich can be <code>null</code>.
	 * <p>
	 * If one instance of the type that <code>configModuleClassName</code>
	 * and <code>identifier</code> point to is registered, it will be returned.
	 * Otherwise the result of this method will be <code>null</code>.
	 * If you set <code>throwExceptionIfNotExistent = true</code> a
	 * <code>ConfigModuleNotFoundException</code> will be thrown instead
	 * of returning <code>null</code>.
	 * <p>
	 * If you want a config module to be lazily created, call
	 * {@link #createConfigModule(Class, String)}.
	 *
	 * @param configModuleClassName The full qualified class name of the
	 * 			config module.
	 * @param identifier An identifier for that special config module if
	 * 			you need different modules of the same class. This parameter
	 * 			can be <code>null</code>.
	 * @param throwExceptionIfNotExistent If set to <code>true</code> this
	 * 			method will throw a {@link ConfigModuleNotFoundException} in
	 * 			case that the desired config module could not be found.
	 * @return The registered ConfigModule or <code>null</code> if no ConfigModule
	 * 			could be found.
	 * @throws ConfigModuleNotFoundException if the config module was not found and
	 *     <code>throwExceptionIfNotExistent</code> is set to <code>true</code>.
	 */
	public ConfigModule getConfigModule(String configModuleClassName, String identifier, boolean throwExceptionIfNotExistent)
	{
		loadConfigModulesForClass(null, configModuleClassName);
		ConfigModule cfMod = configModules.get(getConfigModuleIdentifyingName(configModuleClassName, identifier));
		if (cfMod == null && throwExceptionIfNotExistent)
			throw new ConfigModuleNotFoundException("No ConfigModule of type \""+configModuleClassName+"\" existent!");
		return cfMod;
	}

	/**
	 * Get a config module from this Config instance.
	 * <p>
	 * This is a convenience method for
	 * <code>getConfigModule(configModuleClass.getName(), null, true)</code>.
	 * <p>
	 * If one instance of the type <code>configModuleClass</code>
	 * is registered, it will be returned. Otherwise a
	 * <code>ConfigModuleNotFoundException</code> will be thrown.
	 * <p>
	 * If you want a config module to be lazily created, call
	 * {@link #createConfigModule(Class)}.
	 *
	 * @param configModuleClass The class of the config module.
	 * @return The registered ConfigModule.
	 * @throws ConfigModuleNotFoundException if the config module was not found.
	 */
	public <T extends ConfigModule> T getConfigModule(Class<T> configModuleClass)
	{
		return configModuleClass.cast(getConfigModule(configModuleClass.getName(), null, true));
	}

	/**
	 * Get a config module from this Config instance.
	 * <p>
	 * This is a convenience method for
	 * <code>getConfigModule(configModuleClassName, identifier, true)</code>.
	 * <p>
	 * A config module is identified by its <code>Class</code> combined
	 * with a unique identifier for this class, wich can be <code>null</code>.
	 * <p>
	 * A config module is identified by its <code>Class</code> combined
	 * with a unique identifier for this class, wich can be <code>null</code>.
	 * <p>
	 * If one instance of the type <code>configModuleClass</code>
	 * with the given <code>identifier</code>
	 * is registered, it will be returned. Otherwise a
	 * <code>ConfigModuleNotFoundException</code> will be thrown.
	 * <p>
	 * If you want a config module to be lazily created, call
	 * {@link #createConfigModule(Class, String)}.
	 *
	 * @param configModuleClass The class of the config module.
	 * @param identifier An identifier for that special config module if
	 * 			you need different modules of the same class. This parameter
	 * 			can be <code>null</code>.
	 * @return The registered ConfigModule.
	 * @throws ConfigModuleNotFoundException if the config module was not found.
	 */
	public <T extends ConfigModule> T getConfigModule(Class<T> configModuleClass, String identifier)
	{
		return configModuleClass.cast(getConfigModule(configModuleClass.getName(), identifier, true));
	}

	/**
	 * Get a config module from this Config instance.
	 * <p>
	 * This is a convenience method for
	 * <code>getConfigModule(configModuleClassName, null, throwExceptionIfNotExistent)</code>.
	 * <p>
	 * If one instance of the type <code>configModuleClass</code>
	 * is registered, it will be returned. Otherwise the result
	 * of this method will be <code>null</code>.
	 * If you set <code>throwExceptionIfNotExistent = true</code> a
	 * <code>ConfigModuleNotFoundException</code> will be thrown instead
	 * of returning <code>null</code>.
	 * <p>
	 * If you want a config module to be lazily created, call
	 * {@link #createConfigModule(Class)}.
	 *
	 * @param configModuleClass The class of the config module.
	 * @param throwExceptionIfNotExistent If set to <code>true</code> this
	 * 			method will throw a {@link ConfigModuleNotFoundException} in
	 * 			case that the desired config module could not be found.
	 * @return The registered ConfigModule or <code>null</code> if no ConfigModule
	 * 			could be found.
	 * @throws ConfigModuleNotFoundException if the config module was not found and
	 *     <code>throwExceptionIfNotExistent</code> is set to <code>true</code>.
	 */
	public <T extends ConfigModule> T getConfigModule(Class<T> configModuleClass, boolean throwExceptionIfNotExistent)
	{
		return configModuleClass.cast(getConfigModule(configModuleClass.getName(), null, throwExceptionIfNotExistent));
	}

	/**
	 * Get a config module from this Config instance.
	 * <p>
	 * A config module is identified by its <code>Class</code> combined
	 * with a unique identifier for this class, wich can be <code>null</code>.
	 * <p>
	 * If one instance of the type <code>configModuleClass</code>
	 * with the given <code>identifier</code>
	 * is registered, it will be returned. Otherwise the result
	 * of this method will be <code>null</code>.
	 * If you set <code>throwExceptionIfNotExistent = true</code> a
	 * <code>ConfigModuleNotFoundException</code> will be thrown instead
	 * of returning <code>null</code>.
	 * <p>
	 * If you want a config module to be lazily created, call
	 * {@link #createConfigModule(Class, String)}.
	 *
	 * @param configModuleClass The class of the config module.
	 * @param identifier An identifier for that special config module if
	 * 			you need different modules of the same class. This parameter
	 * 			can be <code>null</code>.
	 * @param throwExceptionIfNotExistent If set to <code>true</code> this
	 * 			method will throw a {@link ConfigModuleNotFoundException} in
	 * 			case that the desired config module could not be found.
	 * @return The registered ConfigModule or <code>null</code> if no ConfigModule
	 * 			could be found.
	 * @throws ConfigModuleNotFoundException if the config module was not found and
	 *     <code>throwExceptionIfNotExistent</code> is set to <code>true</code>.
	 */
	public <T extends ConfigModule> T getConfigModule(Class<T> configModuleClass, String identifier, boolean throwExceptionIfNotExistent)
	{
		return configModuleClass.cast(getConfigModule(configModuleClass.getName(), identifier, throwExceptionIfNotExistent));
	}

	/**
	 * Registers an instance of a config module. If one instance of the same type
	 * and identifier already exists, it will be replaced by the new one.
	 * <p>
	 * You might want to use @{link {@link #createConfigModule(Class, String)}
	 * which registers lazily generated config modules automatically.
	 *
	 * @param configModule The config module to register.
	 */
	public void putConfigModule(ConfigModule configModule)
	{
		String cfModFileName = getIncludedCfModFileName(configModule);
		if (cfModFileName != null) {
			includedCfModsByFileName.remove(cfModFileName);
			includedCfModsByCfMod.remove(configModule);
		} // if (cfModFileName != null) {

		configModules.put(getConfigModuleIdentifyingName(configModule), configModule);
	}


	// =======================================
	// PRIVATE HELPING METHODS
	// =======================================
	private void readFromInputStream(InputStream fin, ClassLoader classLoader)
	throws ConfigException
	{
		try {
//			InputStreamReader isReader = new InputStreamReader(fin, ENCODING);
//			Unmarshaller unMarshaller = new Unmarshaller(ListEnvelope.class); // , org.nightlabs.plugin.PluginMan.getClassLoader());
//			ListEnvelope env = (ListEnvelope)unMarshaller.unmarshal(isReader);
			List<?> ls;
			XMLDecoder d = createXMLDecoder(fin, new ConfigExceptionListener("Error reading main config file!"), classLoader);
			try {
//				d.setExceptionListener(new ConfigExceptionListener("Error reading main config file!"));
				ls = (List<?>)d.readObject();
			} finally {
				d.close();
			}

//			fin.close(); // Bad practice to close a resource in a level where it was not opened! Commented it out. Marco.

//			List ls = env.getList();
//			if (ls == null)
//			ls = new Vector();

			this.configModules.clear();

			Iterator<?> it = ls.iterator();
			while (it.hasNext()) {
				ConfigModule cfMod = (ConfigModule)it.next();
				cfMod._setConfig(this);
				cfMod.init();
				this.configModules.put(getConfigModuleIdentifyingName(cfMod), cfMod);
				loadedConfigModuleClassNames.add(cfMod.getClass().getName());
			}
		} catch (Exception x) {
			throw new ConfigException(x);
		}
	}

	/**
	 * Register a filename with a config module.
	 * @param relativeCfModFileName The filename
	 * @param cfMod the config module
	 */
	protected void putIncludedCfMod(String relativeCfModFileName, ConfigModule cfMod)
	{
		includedCfModsByFileName.put(relativeCfModFileName, cfMod);
		includedCfModsByCfMod.put(cfMod, relativeCfModFileName);
	}

	/**
	 * Get the config module that lives in the given file.
	 * @param relativeCfModFileName The filename
	 * @return The config module that lives in the given file
	 */
	protected ConfigModule getIncludedCfMod(String relativeCfModFileName)
	{
		return includedCfModsByFileName.get(relativeCfModFileName);
	}

	/**
	 * Get the filename for the given config module.
	 * @param cfMod The config module
	 * @return The filename for the given config module.
	 */
	protected String getIncludedCfModFileName(ConfigModule cfMod)
	{
		return includedCfModsByCfMod.get(cfMod);
	}

	private static String createIncludeFileName(String orgFileName, String identifier, String midfix)
	{
		boolean haveIdentifier = identifier != null && !identifier.equals("");
		boolean haveMidfix = midfix != null && !midfix.equals("");

		if(!haveMidfix && !haveIdentifier)
			return orgFileName;

		StringBuffer sb = new StringBuffer(orgFileName);
		int lastDotPos = sb.lastIndexOf(".");
		String ext;
		if(lastDotPos != -1) {
			ext = sb.substring(lastDotPos);
			sb.delete(lastDotPos, sb.length());
		} else {
			ext = "";
		}

//		int lastDCPos = sb.lastIndexOf("#");
//		if (lastDCPos >= 0)
//		sb.delete(lastDCPos, sb.length());

		if(haveIdentifier) {
			sb.append(beforeIdentifier);
			sb.append(identifier);
			sb.append(ext);
		}

		if(haveMidfix) {
			sb.append(beforeMidfix);
			sb.append(midfix);
			sb.append(ext);
		}

		return sb.toString();
	}

	private static String getConfigModuleIdentifyingName(ConfigModule module)
	{
		return getConfigModuleIdentifyingName(module.getClass(), module.getIdentifier());
	}

	private static String getConfigModuleIdentifyingName(Class<? extends ConfigModule> moduleClass, String identifier)
	{
		return getConfigModuleIdentifyingName(moduleClass.getName(), identifier);
	}

	private static String getConfigModuleIdentifyingName(String moduleClassName, String identifier)
	{
		StringBuffer result = new StringBuffer(moduleClassName);
		if(identifier != null && !identifier.equals("")) {
			result.append(beforeIdentifier);
			result.append(identifier);
		}
		return result.toString();
	}

}
