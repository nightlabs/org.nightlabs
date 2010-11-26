package org.nightlabs.i18n;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A {@link MultiLanguagePropertiesBundle} iterates all installed locales (or languages)
 * and looks for a properties file for that locale that can be loaded using a
 * given baseName and {@link ClassLoader}. This properties bundle will store
 * a {@link Properties} object for all resources it finds and make the values
 * from that resource available.
 *
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] -->
 * @author Sebastian <!-- sebastian [AT] nightlabs [DOT] de -->
 */
public class MultiLanguagePropertiesBundle {

	private static final Logger logger = LoggerFactory.getLogger(MultiLanguagePropertiesBundle.class);

	private Map<Locale, Properties> properties = new HashMap<Locale, Properties>();

	/**
	 * Constructs a new {@link MultiLanguagePropertiesBundle} for the given baseName and {@link ClassLoader}.
	 * Note, that this constructor will use only the available languages not all available locales
	 * (with language, country and variant).
	 *
	 * @param baseName
	 * 		This is the baseName for the resources to find. Note that {@link MultiLanguagePropertiesBundle}
	 * 		will replace all '.' with '/' in the given baseName and look for resources named with the result
	 * 		of this operation as prefix followed by the current locale and '.properties'.
	 * @param loader
	 * 		This is the {@link ClassLoader} {@link MultiLanguagePropertiesBundle} will use to search
	 * 		for the resource of a given locale.
	 */
	public MultiLanguagePropertiesBundle(String baseName, ClassLoader loader) {
		this(baseName, loader, true);
	}

//	public static void main(String[] args) {
//		Locale[] locales = Locale.getAvailableLocales();
//		for (Locale locale : locales) {
//			if (isLanguageOnly(locale))
//				System.out.println(locale);
//		}
//
//		Properties properties1 = new Properties();
//		Properties properties2 = new Properties(properties1);
//		Properties properties3 = new Properties(properties2);
//
//		properties1.setProperty("only1", "value1");
//
//		String val = properties3.getProperty("only1");
//		System.out.println("val: " + val);
//		System.out.println("keyset: " + properties3.stringPropertyNames());
//	}

	/**
	 * Tests if the given {@link Locale} has a country midfix defined or not.
	 *
	 * @return <code>true</code> if the given {@link Locale} has a country midfix defined.
	 */
	private static boolean isLanguageOnly(Locale locale)
	{
		return locale.getCountry() == null || locale.getCountry().isEmpty();
	}

	/**
	 * Constructs a new {@link MultiLanguagePropertiesBundle} for the given baseName and {@link ClassLoader}.
	 * @param baseName
	 * 		This is the baseName for the resources to find. Note that {@link MultiLanguagePropertiesBundle}
	 * 		will replace all '.' with '/' in the given baseName and look for resources named with the result
	 * 		of this operation as prefix followed by the current locale and '.properties'.
	 * @param loader
	 * 		This is the {@link ClassLoader} {@link MultiLanguagePropertiesBundle} will use to search
	 * 		for the resource of a given locale.
	 * @param useOnlyLanguages
	 * 		This controls whether the {@link MultiLanguagePropertiesBundle} should search only for resources
	 * 		for languages and not for resources for all locales (containing language, country and variant).
	 */
	public MultiLanguagePropertiesBundle(String baseName, ClassLoader loader, boolean useOnlyLanguages) {

		String baseResourcePath = baseName.replaceAll("\\.", "/");

		// Step 1: Read default properties
		Properties defaultProps = loadPropertiesFromResource(null, loader, baseResourcePath, null);
		if (defaultProps != null)
			properties.put(null, defaultProps);

		final Locale[] locales = Locale.getAvailableLocales();

		// Step 2: Read all existing language properties
		for (Locale locale : locales) {
			if (!isLanguageOnly(locale))
				continue;

			Properties langProps = loadPropertiesFromResource(defaultProps, loader, baseResourcePath, locale);
			if (langProps != null)
				properties.put(locale, langProps);
		}

		if (useOnlyLanguages)
			return;

		// Step 3: Process countries (which of course have a language, too)
		for (Locale locale : locales) {
			if (isLanguageOnly(locale))
				continue;

			Properties defaults = properties.get(new Locale(locale.getLanguage()));
			if (defaults == null)
				defaults = defaultProps;

			Properties countryProps = loadPropertiesFromResource(defaults, loader, baseResourcePath, locale);
			if (countryProps != null)
				properties.put(locale, countryProps);
		}
	}

	private Properties loadPropertiesFromResource(Properties defaultProps, ClassLoader loader, String baseResourcePath, Locale locale)
	{
		String resPath = locale == null ? baseResourcePath + ".properties" : baseResourcePath + "_" + locale.toString() + ".properties";
		URL resURL = loader.getResource(resPath);
		if (resURL == null)
			return null;

		URLConnection connection = null;
		try {
			connection = resURL.openConnection();
		} catch (IOException e) {
			logger.error("Failed opening connection to " + resURL, e);
			return null;
		}

		if (connection == null)
			return null;

		InputStream in;
		try {
			in = connection.getInputStream();
		} catch (IOException e) {
			logger.error("Failed creating InputStream for " + resURL, e);
			return null;
		}
		try {
			Properties props = new Properties(defaultProps);
			props.load(in);
			return props;
		} catch (IOException e) {
			logger.error("Failed loading Properties from " + resURL, e);
			return null;
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				logger.error("Failed closing InputStream to " + resURL, e);
			}
		}
	}

	/**
	 * Get all {@link Locale}s this {@link MultiLanguagePropertiesBundle}
	 * has found resources for.
	 *
	 * @return All {@link Locale}s this {@link MultiLanguagePropertiesBundle}
	 * 		has found resources for.
	 */
	public Collection<Locale> getLocales() {
		Set<Locale> keySet = new HashSet<Locale>(properties.keySet());
		keySet.remove(null);
		return Collections.unmodifiableSet(keySet);
	}

	/**
	 * Check whether this bundle has found a resource for the given
	 * locale and whether that resource includes the given key.
	 * Note, that this method will only look in the resource
	 * for the given locale, the fallback-properties will not
	 * be taken into account.
	 *
	 * @param locale The locale to find the key for.
	 * @param key The key to look for.
	 * @return Whether this bundle contains properties for the given locale
	 * 		and that properties contain the given key.
	 */
	public boolean hasEntry(Locale locale, String key) {
		return hasEntry(locale, key, false);
	}

	/**
	 * Check whether this bundle has found a resource for the given
	 * locale and whether that resource includes the given key.
	 * If <code>fallbackToDefault</code> is <code>true</code> this
	 * method will also look in the fallback-properties found
	 * for the baseName this {@link MultiLanguagePropertiesBundle}
	 * was instantiated with.
	 *
	 * @param locale The locale to find the key for.
	 * @param key The key to look for.
	 * @param fallbackToDefault Whether to look also in the fallback-properties.
	 * @return Whether this bundle contains properties for the given locale
	 * 		and that properties contain the given key.
	 */
	public boolean hasEntry(Locale locale, String key, boolean fallbackToDefault) {
		Properties props = properties.get(locale);
		if (props == null)
			return false;
		else {
			if (fallbackToDefault)
				return props.getProperty(key) != null;
			else
				return props.containsKey(key);
		}
	}

	/**
	 * Get the value for the given key from the resource
	 * found for the given locale. This method will return
	 * <code>null</code> in case it can not find the properties
	 * or the key within the properties. It will also return
	 * <code>null</code> if the value is not a String.
	 *
	 * @param locale The locale to find the properties for.
	 * @param key The key to find the value for.
	 * @return The value for the given locale and key or <code>null</code> if not found.
	 */
	public String getProperty(Locale locale, String key) {
		Properties props = properties.get(locale);
		if (props == null)
			return null;
		else
			return props.getProperty(key);
	}

	/**
	 * Retrieves all keys corresponding to one given {@link Locale}.
	 *
	 * @param locale {@link Locale}.
	 * @return Will return an empty {@link Set} if no keys are saved for this {@link Locale}.
	 * Otherwise the keys of this Locale will be returned as {@link Set}.
	 */
	public Set<String> getKeys(Locale locale)
	{
		Properties localeProps = properties.get(locale);
		if (localeProps == null)
			return Collections.emptySet();
		else
			return Collections.unmodifiableSet(localeProps.stringPropertyNames());
	}

	/**
	 * Retrieves <i>all keys </i> of <i>all resource files</i> (*.properties).
	 * This might be useful, as in some cases the resource files are not maintained
	 * very careful and there are different keys and/or different amount of keys in some resource-files.
	 * Or there is not always a fallback/default properties file (with <i>no</i> LANGUAGE[_COUNTRY]
	 * codes in the *.properties files), so a programmer can not easily iterate just this file and count on
	 * the constraint that this one really contains all keys.
	 * @return Returns a new {@link Set} with all different keys of all resource-files.
	 * All keys of all resources will be added in a {@link Set}, so the union will be build.
	 */
	public Set<String> getAllKeys()
	{
		Set<String> allKeys = new HashSet<String>();
		for (Map.Entry<Locale, Properties> entry : properties.entrySet()) {
			Locale locale = entry.getKey();
			Properties props = properties.get(locale);
			allKeys.addAll(props.stringPropertyNames());
		}
		return Collections.unmodifiableSet(allKeys);
	}
}
