package org.nightlabs.l10n;

import org.nightlabs.config.Config;
import org.nightlabs.util.NLLocale;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class GlobalDateFormatter {
	private static IDateFormatter _sharedInstance = null;

	/**
	 * In case there is no shared instance existing yet, it will be
	 * created with the shared instance of Config. Hence, this method
	 * fails if there is no shared instance of Config.
	 *
	 * @return Returns the shared instance of DateFormatter.
	 */
	public static IDateFormatter sharedInstance()
	{
		final DateFormatterFactory dateFormatterFactory = getDateFormatterFactory();
		if (dateFormatterFactory != null)
			return dateFormatterFactory.sharedInstance();

		if (_sharedInstance == null)
			_sharedInstance = new ConfigurationDateFormatter(Config.sharedInstance(), NLLocale.getDefault());

		return _sharedInstance;
	}

	/**
	 * Set the sharedInstance.
	 * @param sharedInstance the sharedInstance to set
	 */
	public static void setSharedInstance(final IDateFormatter sharedInstance) {
		_sharedInstance = sharedInstance;
	}

	/**
	 * If there is a system property set with this name, the DateFormatter's static methods
	 * will delegate to an instance of the class specified by this system property.
	 */
	public static final String PROPERTY_KEY_DATE_FORMATTER_FACTORY = DateFormatterFactory.class.getName();

	private static DateFormatterFactory dateFormatterFactory = null;

	private synchronized static DateFormatterFactory getDateFormatterFactory()
	{
		if (dateFormatterFactory == null) {
			final String dateFormatterFactoryClassName = System.getProperty(PROPERTY_KEY_DATE_FORMATTER_FACTORY);
			if (dateFormatterFactoryClassName == null)
				return null;

			Class<?> dateFormatterFactoryClass;
			try {
				dateFormatterFactoryClass = Class.forName(dateFormatterFactoryClassName);
			} catch (final ClassNotFoundException e) {
				throw new RuntimeException("The system-property '" + PROPERTY_KEY_DATE_FORMATTER_FACTORY + "' was specified as '"+dateFormatterFactoryClassName+"' but this class cannot be found!", e);
			}

			try {
				dateFormatterFactory = (DateFormatterFactory) dateFormatterFactoryClass.newInstance();
			} catch (final Exception e) {
				throw new RuntimeException("Instantiating the class '"+dateFormatterFactoryClassName+"' specified by the system-property '" + PROPERTY_KEY_DATE_FORMATTER_FACTORY + "' was found, but it could not be instantiated!", e);
			}
		}
		return dateFormatterFactory;
	}

	/**
	 * Set the dateFormatterFactory.
	 * @param dateFormatterFactory the dateFormatterFactory to set
	 */
	public static void setDateFormatterFactory(final DateFormatterFactory dateFormatterFactory) {
		GlobalDateFormatter.dateFormatterFactory = dateFormatterFactory;
	}
}
