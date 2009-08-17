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
package org.nightlabs.l10n;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import org.nightlabs.config.Config;
import org.nightlabs.config.ConfigException;
import org.nightlabs.i18n.NLLocale;



/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class NumberFormatter
{
	private static NumberFormatter _sharedInstance = null;

	/**
	 * In case there is no shared instance existing yet, it will be
	 * created with the shared instance of Config. Hence, this method
	 * fails if there is no shared instance of Config.
	 *
	 * @return Returns the shared instance of DateFormatter.
	 */
	public static NumberFormatter sharedInstance()
	{
		NumberFormatterFactory numberFormatterFactory = getNumberFormatterFactory();
		if (numberFormatterFactory != null)
			return numberFormatterFactory.sharedInstance();

		if (_sharedInstance == null)
			_sharedInstance = new NumberFormatter(Config.sharedInstance(), NLLocale.getDefault());

		return _sharedInstance;
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
			String numberFormatterFactoryClassName = System.getProperty(PROPERTY_KEY_NUMBER_FORMATTER_FACTORY);
			if (numberFormatterFactoryClassName == null)
				return null;

			Class<?> numberFormatterFactoryClass;
			try {
				numberFormatterFactoryClass = Class.forName(numberFormatterFactoryClassName);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException("The system-property '" + PROPERTY_KEY_NUMBER_FORMATTER_FACTORY + "' was specified as '"+numberFormatterFactoryClassName+"' but this class cannot be found!", e);
			}

			try {
				numberFormatterFactory = (NumberFormatterFactory) numberFormatterFactoryClass.newInstance();
			} catch (Exception e) {
				throw new RuntimeException("Instantiating the class '"+numberFormatterFactoryClassName+"' specified by the system-property '" + PROPERTY_KEY_NUMBER_FORMATTER_FACTORY + "' was found, but it could not be instantiated!", e);
			}
		}
		return numberFormatterFactory;
	}

	private Config config;
	private Locale locale;

	/**
	 * @param config
	 */
	public NumberFormatter(Config config, Locale locale)
	{
		this.config = config;
		this.locale = locale;
		try {
			config.createConfigModule(GlobalL10nSettings.class);
		} catch (ConfigException e) {
			throw new RuntimeException(e);
		}
	}

	public Locale getLocale() {
		return locale;
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

			String className = l10nFormatCfMod.getNumberFormatProvider();

			Class<?> clazz = Class.forName(className);
			if (!NumberFormatProvider.class.isAssignableFrom(clazz))
				throw new ClassCastException("class \""+className+"\" defined in config module \""+L10nFormatCfMod.class.getName()+"\" does not implement interface \""+NumberFormatProvider.class.getName()+"\"!");

			NumberFormatProvider nfp = (NumberFormatProvider) clazz.newInstance();
			nfp.init(config, locale.getLanguage(), locale.getCountry());

			this.numberFormatProvider = nfp;
			return numberFormatProvider;
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param l The value.
	 */
	public static String formatInt(long l)
	{
		return formatInt(l, 1);
	}

	/**
	 * @param l The value.
	 * @param minIntegerDigitCount The minimal number of digits.
	 */
	public static String formatInt(long l, int minIntegerDigitCount)
	{
		NumberFormat numberFormat = sharedInstance().getNumberFormatProvider().getIntegerFormat(
						minIntegerDigitCount);
		return numberFormat.format(l);
	}

	/**
	 * @param d The value.
	 * @param decimalDigitCount The number of digits on the right side of the decimal separator.
	 */
	public static String formatFloat(double d, int decimalDigitCount)
	{
		return formatFloat(d, 1, decimalDigitCount, decimalDigitCount);
	}

	/**
	 * @param d The value.
	 * @param minIntegerDigitCount The minimal number of digits on the left side of the decimal separator.
	 * @param minDecimalDigitCount The minimal number of digits on the right side of the decimal separator.
	 * @param maxDecimalDigitCount The maximal number of digits on the right side of the decimal separator.
	 */
	public static String formatFloat(double d, int minIntegerDigitCount, int minDecimalDigitCount, int maxDecimalDigitCount)
	{
		NumberFormat numberFormat = sharedInstance().getNumberFormatProvider().getFloatFormat(
						minIntegerDigitCount, minDecimalDigitCount, maxDecimalDigitCount);
		return numberFormat.format(d);
	}

	/**
	 * @param d
	 * @param minIntegerDigitCount
	 * @param minDecimalDigitCount
	 * @param maxDecimalDigitCount
	 * @param exponentDigitCount
	 */
	public static String formatScientific(double d, int minIntegerDigitCount, int minDecimalDigitCount, int maxDecimalDigitCount, int exponentDigitCount)
	{
		NumberFormat numberFormat = sharedInstance().getNumberFormatProvider().getScientificFormat(
						minIntegerDigitCount, minDecimalDigitCount, maxDecimalDigitCount, exponentDigitCount);
		return numberFormat.format(d);
	}

	public static long power(long base, int exponent) { // exponent >= 0
		if (exponent < 0)
			throw new IllegalArgumentException("exponent < 0!");

		long result = 1;
		while (exponent != 0) {
			if (exponent % 2 != 0)
				result = result * base;
			exponent = exponent / 2;
			base = base * base;
		}
		return result;
	}

	/**
	 * @param currencyValue The value.
	 * @param currency The currency.
	 */
	public static String formatCurrency(
			long currencyValue, Currency currency)
	{
		return formatCurrency(currencyValue, currency, true);
	}

	/**
	 * @param currencyValue The value.
	 * @param currency The currency.
	 * @param includeCurrencySymbol Whether or not to include the currency symbol in the result string.
	 */
	public static String formatCurrency(
			long currencyValue, Currency currency, boolean includeCurrencySymbol)
	{
		int ddc = currency.getDecimalDigitCount();
		return formatCurrency(currencyValue, 1, ddc, ddc, currency,
				includeCurrencySymbol);
	}

	/**
	 * @param currencyValue The value.
	 * @param minIntegerDigitCount The minimal number of digits on the left side of the decimal separator.
	 * @param currency The currency.
	 */
	public static String formatCurrency(
			long currencyValue, int minIntegerDigitCount,
			Currency currency)
	{
		return formatCurrency(
				currencyValue, minIntegerDigitCount,
				currency);
	}

	/**
	 * @param currencyValue The value.
	 * @param minIntegerDigitCount The minimal number of digits on the left side of the decimal separator.
	 * @param currency The currency.
	 * @param includeCurrencySymbol Whether or not to include the currency symbol in the result string.
	 */
	public static String formatCurrency(
			long currencyValue, int minIntegerDigitCount,
			Currency currency, boolean includeCurrencySymbol)
	{
		int ddc = currency.getDecimalDigitCount();
		return formatCurrency(
				currencyValue,
				minIntegerDigitCount, ddc, ddc, currency,
				includeCurrencySymbol);
	}

	/**
	 * @param currencyValue The value.
	 * @param minIntegerDigitCount The minimal number of digits on the left side of the decimal separator.
	 * @param minDecimalDigitCount The minimal number of digits on the right side of the decimal separator.
	 * @param maxDecimalDigitCount The maximal number of digits on the right side of the decimal separator.
	 * @param currency The currency.
	 */
	public static String formatCurrency(
			long currencyValue, int minIntegerDigitCount,
			int minDecimalDigitCount, int maxDecimalDigitCount,
			Currency currency)
	{
		return formatCurrency(
				currencyValue, minIntegerDigitCount,
				minDecimalDigitCount, maxDecimalDigitCount,
				currency);
	}

	/**
	 * @param currencyValue The value.
	 * @param minIntegerDigitCount The minimal number of digits on the left side of the decimal separator.
	 * @param minDecimalDigitCount The minimal number of digits on the right side of the decimal separator.
	 * @param maxDecimalDigitCount The maximal number of digits on the right side of the decimal separator.
	 * @param currency The currency.
	 * @param includeCurrencySymbol Whether or not to include the currency symbol in the result string.
	 */
	public static String formatCurrency(
			long currencyValue, int minIntegerDigitCount,
			int minDecimalDigitCount, int maxDecimalDigitCount,
			Currency currency, boolean includeCurrencySymbol)
	{
		String currencySymbol = currency.getCurrencySymbol();
		if (currencySymbol == null)
			throw new IllegalArgumentException("currency.getCurrencySymbol() returned null! currency.getCurrencyID=\"" + currency.getCurrencyID() + "\"");

		NumberFormat numberFormat = sharedInstance().getNumberFormatProvider().getCurrencyFormat(
						minIntegerDigitCount, minDecimalDigitCount, maxDecimalDigitCount,
						currencySymbol, includeCurrencySymbol);

		double cvd = (double)currencyValue / power(10, currency.getDecimalDigitCount());
		return numberFormat.format(cvd);
	}

	public static long parseCurrency(String valueStr, Currency currency, boolean includeCurrencySymbol)
	throws ParseException
	{
		String currencySymbol = currency.getCurrencySymbol();
		if (currencySymbol == null)
			throw new IllegalArgumentException("currency.getCurrencySymbol() returned null! currency.getCurrencyID=\"" + currency.getCurrencyID() + "\"");

		NumberFormat numberFormat = sharedInstance().getNumberFormatProvider().getCurrencyFormat(
				1, currency.getDecimalDigitCount(), currency.getDecimalDigitCount(),
				currencySymbol, includeCurrencySymbol);
		double d = numberFormat.parse(valueStr).doubleValue();
		return Math.round(d * power(10, currency.getDecimalDigitCount()));
	}

	public static double parseFloat(String valueStr)
	throws ParseException
	{
		NumberFormat numberFormat = sharedInstance().getNumberFormatProvider().getFloatFormat(0, 0, 10);
		return numberFormat.parse(valueStr).doubleValue();
	}
}
