package org.nightlabs.l10n;

import java.util.Locale;

import org.nightlabs.config.Config;
import org.nightlabs.config.ConfigException;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class ConfigurationDateFormatter extends AbstractDateFormatter {
	private final Config config;
	private final Locale locale;

	public ConfigurationDateFormatter(final Config config, final Locale locale)
	{
		this.config = config;
		this.locale = locale;
		try {
			config.createConfigModule(GlobalL10nSettings.class);
		} catch (final ConfigException e) {
			throw new RuntimeException(e);
		}
	}

	private DateFormatProvider dateFormatProvider = null;

	public DateFormatProvider getDateFormatProvider()
	{
		if (dateFormatProvider != null)
			return dateFormatProvider;

		L10nFormatCfMod l10nFormatCfMod;
		try {
			l10nFormatCfMod = ConfigUtil.createConfigModule(
					config, L10nFormatCfMod.class, locale.getLanguage(), locale.getCountry());

			final String className = l10nFormatCfMod.getDateFormatProvider();

			final Class<?> clazz = Class.forName(className);
			if (!DateFormatProvider.class.isAssignableFrom(clazz))
				throw new ClassCastException("class \""+className+"\" defined in config module \""+L10nFormatCfMod.class.getName()+"\" does not implement interface \""+DateFormatProvider.class.getName()+"\"!");

			final DateFormatProvider dfp = (DateFormatProvider) clazz.newInstance();
			dfp.init(config, locale);

			this.dateFormatProvider = dfp;
			return dateFormatProvider;
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
