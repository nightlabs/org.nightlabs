/**
 *
 */
package org.nightlabs.i18n;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

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

		Locale[] locales = null;
		if (!useOnlyLanguages)
			locales = Locale.getAvailableLocales();
		else {
			String[] langs = Locale.getISOLanguages();
			locales = new Locale[langs.length];
			for (int i = 0; i < langs.length; i++) {
				locales[i] = new Locale(langs[i]);
			}
		}

		Properties fallbackProps = loadPropertiesFromResource(loader, baseResourcePath, null);
		for (Locale locale : locales) {
			// We want the fallback only for English - not all locales that exist in the Java VM, because
			// we otherwise populate the properties map with hundreds of same elements.

			Properties localeProps = loadPropertiesFromResource(loader, baseResourcePath, locale);

			if (localeProps != null || Locale.ENGLISH.getLanguage().equals(locale.getLanguage())) {
				Properties props;
				if (fallbackProps == null)
					props = localeProps;
				else {
					// merge via Properties' defaults feature
					props = new Properties(fallbackProps);

					if (localeProps != null)
						props.putAll(localeProps);
				}

				if (props != null)
					properties.put(locale, props);
			}
		}
	}

	private Properties loadPropertiesFromResource(ClassLoader loader, String baseResourcePath, Locale locale)
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
			Properties props = new Properties();
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
		return Collections.unmodifiableCollection(properties.keySet());
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

}
