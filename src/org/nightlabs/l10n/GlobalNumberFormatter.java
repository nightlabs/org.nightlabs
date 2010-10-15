package org.nightlabs.l10n;

import org.nightlabs.config.Config;
import org.nightlabs.util.NLLocale;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class GlobalNumberFormatter {
	private static INumberFormatter _sharedInstance = null;

	/**
	 * In case there is no shared instance existing yet, it will be
	 * created with the shared instance of Config. Hence, this method
	 * fails if there is no shared instance of Config.
	 *
	 * @return Returns the shared instance of DateFormatter.
	 */
	public static INumberFormatter sharedInstance()
	{
		final NumberFormatterFactory numberFormatterFactory = getNumberFormatterFactory();
		if (numberFormatterFactory != null)
			return numberFormatterFactory.sharedInstance();

		if (_sharedInstance == null)
			_sharedInstance = new ConfigurationNumberFormatter(Config.sharedInstance(), NLLocale.getDefault());

		return _sharedInstance;
	}

	/**
	 * Set the sharedInstance.
	 * @param sharedInstance the sharedInstance to set
	 */
	public static void setSharedInstance(final INumberFormatter sharedInstance) {
		_sharedInstance = sharedInstance;
	}

	/**
	 * If there is a system property set with this name, the NumberFormatter's static methods
	 * will delegate to an instance of the class specified by this system property.
	 */
	public static final String PROPERTY_KEY_NUMBER_FORMATTER_FACTORY = NumberFormatterFactory.class.getName();

	private static NumberFormatterFactory numberFormatterFactory = null;

	private synchronized static NumberFormatterFactory getNumberFormatterFactory()
	{
		if (numberFormatterFactory == null) {
			final String numberFormatterFactoryClassName = System.getProperty(PROPERTY_KEY_NUMBER_FORMATTER_FACTORY);
			if (numberFormatterFactoryClassName == null)
				return null;

			Class<?> numberFormatterFactoryClass;
			try {
				numberFormatterFactoryClass = Class.forName(numberFormatterFactoryClassName);
			} catch (final ClassNotFoundException e) {
				throw new RuntimeException("The system-property '" + PROPERTY_KEY_NUMBER_FORMATTER_FACTORY + "' was specified as '"+numberFormatterFactoryClassName+"' but this class cannot be found!", e);
			}

			try {
				numberFormatterFactory = (NumberFormatterFactory) numberFormatterFactoryClass.newInstance();
			} catch (final Exception e) {
				throw new RuntimeException("Instantiating the class '"+numberFormatterFactoryClassName+"' specified by the system-property '" + PROPERTY_KEY_NUMBER_FORMATTER_FACTORY + "' was found, but it could not be instantiated!", e);
			}
		}
		return numberFormatterFactory;
	}

	/**
	 * Set the numberFormatterFactory.
	 * @param numberFormatterFactory the numberFormatterFactory to set
	 */
	public static void setNumberFormatterFactory(final NumberFormatterFactory numberFormatterFactory) {
		GlobalNumberFormatter.numberFormatterFactory = numberFormatterFactory;
	}
}
