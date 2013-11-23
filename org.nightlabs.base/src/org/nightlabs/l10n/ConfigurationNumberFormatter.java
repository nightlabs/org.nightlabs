package org.nightlabs.l10n;

import java.util.Locale;

import org.nightlabs.config.Config;
import org.nightlabs.config.ConfigException;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class ConfigurationNumberFormatter extends AbstractNumberFormatter {
	private final Config config;
	private final Locale locale;

	public ConfigurationNumberFormatter(final Config config, final Locale locale)
	{
		this.config = config;
		this.locale = locale;
		try {
			config.createConfigModule(GlobalL10nSettings.class);
		} catch (final ConfigException e) {
			throw new RuntimeException(e);
		}
	}

	private NumberFormatProvider numberFormatProvider = null;

	public NumberFormatProvider getNumberFormatProvider()
	{
		if (numberFormatProvider != null)
			return numberFormatProvider;

		L10nFormatCfMod l10nFormatCfMod;
		try {
			l10nFormatCfMod = ConfigUtil.createConfigModule(
					config, L10nFormatCfMod.class, locale.getLanguage(), locale.getCountry());

			final String className = l10nFormatCfMod.getNumberFormatProvider();

			final Class<?> clazz = Class.forName(className);
			if (!NumberFormatProvider.class.isAssignableFrom(clazz))
				throw new ClassCastException("class \""+className+"\" defined in config module \""+L10nFormatCfMod.class.getName()+"\" does not implement interface \""+NumberFormatProvider.class.getName()+"\"!");

			final NumberFormatProvider nfp = (NumberFormatProvider) clazz.newInstance();
			nfp.init(config, locale);

			this.numberFormatProvider = nfp;
			return numberFormatProvider;
		} catch (final RuntimeException e) {
			throw e;
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Get the locale.
	 * @return the locale
	 */
	public Locale getLocale() {
		return locale;
	}
}
