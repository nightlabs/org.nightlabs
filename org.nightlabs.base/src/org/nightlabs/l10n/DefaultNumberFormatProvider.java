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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.nightlabs.config.Config;


/**
 * @author Marco Schulze - marco at nightlabs dot de
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class DefaultNumberFormatProvider implements NumberFormatProvider
{
	protected Config config;
	protected Locale locale;
	protected DefaultNumberFormatCfMod defaultNumberFormatCfMod;

	public DefaultNumberFormatProvider()
	{
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.l10n.NumberFormatProvider#init(org.nightlabs.config.Config, java.util.Locale)
	 */
	@Override
	public void init(final Config config, final Locale locale)
	{
		try {
			this.config = config;
			this.locale = locale;
			this.defaultNumberFormatCfMod = ConfigUtil.createConfigModule(config, DefaultNumberFormatCfMod.class, locale.getLanguage(), locale.getCountry());
		} catch (final RuntimeException x) {
			throw x;
		} catch (final Exception x) {
			throw new RuntimeException(x);
		}
	}

	/**
	 * key: String key (Long.toHexString(flags)+'_'+Integer.toHexString(decimalDigitCount))
	 */
	protected Map<String, DecimalFormat> numberFormats = new HashMap<String, DecimalFormat>();

	protected DecimalFormatSymbols createDecimalFormatSymbols()
	{
		final DecimalFormatSymbols dfs = new DecimalFormatSymbols(locale);
		dfs.setGroupingSeparator(defaultNumberFormatCfMod.getGroupingSeparator());
		dfs.setDecimalSeparator(defaultNumberFormatCfMod.getDecimalSeparator());
		return dfs;
	}

	protected String createIntegerDigitCountPattern(final int integerDigitCount)
	{
		final StringBuffer sb = new StringBuffer();
		final int groupingSize = defaultNumberFormatCfMod.getGroupingSize();
		for (int i = 1; i <= integerDigitCount || i <= groupingSize; ++i) {
			sb.insert(0, i <= integerDigitCount ? '0' : '#');
			if (i % groupingSize == 0)
				sb.insert(0, ',');
		}
		return sb.toString();
	}

	protected String createDecimalDigitCountPattern(final int minDecimalDigitCount, final int maxDecimalDigitCount)
	{
		final StringBuffer sb = new StringBuffer();
		for (int i = 1; i <= minDecimalDigitCount || i <= maxDecimalDigitCount; ++i) {
			sb.append(i <= minDecimalDigitCount ? '0' : '#');
		}
		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.l10n.NumberFormatProvider#getIntegerFormat(int)
	 */
	@Override
	public NumberFormat getIntegerFormat(final int minIntegerDigitCount)
	{
		final String key = "integer_"+Integer.toHexString(minIntegerDigitCount);
		DecimalFormat df;
		synchronized (numberFormats) {
			df = numberFormats.get(key);
		}
		if (df == null) {
			final DecimalFormatSymbols dfs = createDecimalFormatSymbols();
			final String integerDigitCountPattern = createIntegerDigitCountPattern(minIntegerDigitCount);

			df = new DecimalFormat(
					defaultNumberFormatCfMod.getPositivePrefix()
					+'#'+integerDigitCountPattern
					+defaultNumberFormatCfMod.getPositiveSuffix()
					+';'
					+defaultNumberFormatCfMod.getNegativePrefix()
					+'#'+integerDigitCountPattern
					+defaultNumberFormatCfMod.getNegativeSuffix(),
					dfs);

			synchronized (numberFormats) {
				numberFormats.put(key, df);
			}
		}

		return df;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.l10n.NumberFormatProvider#getFloatFormat(int, int, int)
	 */
	@Override
	public NumberFormat getFloatFormat(final int minIntegerDigitCount, final int minDecimalDigitCount, final int maxDecimalDigitCount)
	{
		final String key = "float_"+Integer.toHexString(minIntegerDigitCount)
		+'_'+Integer.toHexString(minDecimalDigitCount)
		+'_'+Integer.toHexString(maxDecimalDigitCount);
		DecimalFormat df;
		synchronized (numberFormats) {
			df = numberFormats.get(key);
		}
		if (df == null) {
			final DecimalFormatSymbols dfs = createDecimalFormatSymbols();
			final String integerDigitCountPattern = createIntegerDigitCountPattern(minIntegerDigitCount);
			final String decimalDigitCountPattern = createDecimalDigitCountPattern(minDecimalDigitCount, maxDecimalDigitCount);

			df = new DecimalFormat(
					defaultNumberFormatCfMod.getPositivePrefix()
					+'#'+integerDigitCountPattern
					+'.'+decimalDigitCountPattern
					+defaultNumberFormatCfMod.getPositiveSuffix()
					+';'
					+defaultNumberFormatCfMod.getNegativePrefix()
					+'#'+integerDigitCountPattern
					+'.'+decimalDigitCountPattern
					+defaultNumberFormatCfMod.getNegativeSuffix(),
					dfs);

			synchronized (numberFormats) {
				numberFormats.put(key, df);
			}
		}

		return df;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.l10n.NumberFormatProvider#getCurrencyFormat(int, int, int, org.nightlabs.l10n.Currency, boolean)
	 */
	@Override
	public NumberFormat getCurrencyFormat(final int minIntegerDigitCount, final int minDecimalDigitCount, final int maxDecimalDigitCount, final Currency currency, final boolean includeCurrencySymbol)
	{
		final String currencySymbol = getCurrencySymbol(currency, locale);

		final String key = "currency_"+Integer.toHexString(minIntegerDigitCount)
		+'_'+Integer.toHexString(minDecimalDigitCount)
		+'_'+Integer.toHexString(maxDecimalDigitCount)
		+'_'+currencySymbol
		+'_'+includeCurrencySymbol;
		DecimalFormat df;
		synchronized (numberFormats) {
			df = numberFormats.get(key);
		}
		if (df == null) {
			final DecimalFormatSymbols dfs = createDecimalFormatSymbols();
			//			dfs.setInternationalCurrencySymbol(null);
			dfs.setCurrencySymbol(currencySymbol);
			final String integerDigitCountPattern = createIntegerDigitCountPattern(minIntegerDigitCount);
			final String decimalDigitCountPattern = createDecimalDigitCountPattern(minDecimalDigitCount, maxDecimalDigitCount);

			String beginCurrSymbol;
			String endCurrSymbol;
			if (!includeCurrencySymbol) {
				beginCurrSymbol = "";
				endCurrSymbol = "";
			}
			else if (DefaultNumberFormatCfMod.CURRENCYSYMBOLPOSITION_BEGIN.equals(defaultNumberFormatCfMod.getCurrencySymbolPosition())) {
				beginCurrSymbol = "\u00A4 ";
				endCurrSymbol = "";
			}
			else {
				beginCurrSymbol = "";
				endCurrSymbol = " \u00A4";
			}

			df = new DecimalFormat(
					beginCurrSymbol
					+defaultNumberFormatCfMod.getPositivePrefix()
					+'#'+integerDigitCountPattern
					+'.'+decimalDigitCountPattern
					+defaultNumberFormatCfMod.getPositiveSuffix()
					+endCurrSymbol
					+';'
					+beginCurrSymbol
					+defaultNumberFormatCfMod.getNegativePrefix()
					+'#'+integerDigitCountPattern
					+'.'+decimalDigitCountPattern
					+defaultNumberFormatCfMod.getNegativeSuffix()
					+endCurrSymbol,
					dfs);

			synchronized (numberFormats) {
				numberFormats.put(key, df);
			}
		}

		return df;
	}

	/**
	 * Get the currency symbol for the given currency. This method tries
	 * to lookup the java.util.Currency to get a symbol for the given locale.
	 * If this fails, {@link Currency#getCurrencySymbol()} is used.
	 * @param currency The currency
	 * @return The currency symbol - never <code>null</code>
	 * @throws IllegalArgumentException if no symbol could be found for the given currency.
	 */
	private static String getCurrencySymbol(final Currency currency, final Locale locale) {
		String currencySymbol = null;
		try {
			final java.util.Currency javaCurrency = java.util.Currency.getInstance(currency.getCurrencyID());
			currencySymbol = javaCurrency.getSymbol(locale);
		} catch(final Exception e) {
			// ignore
		}
		if(currencySymbol == null) {
			currencySymbol = currency.getCurrencySymbol();
		}
		if (currencySymbol == null) {
			throw new IllegalArgumentException("currency.getCurrencySymbol() returned null an currency is unknown for Java! currency.getCurrencyID=\"" + currency.getCurrencyID() + "\"");
		}
		return currencySymbol;
	}

	/**
	 * @see org.nightlabs.l10n.NumberFormatProvider#getScientificFormat(int, int, int, int)
	 */
	public NumberFormat getScientificFormat(final int preferredIntegerDigitCount, final int minDecimalDigitCount, final int maxDecimalDigitCount, final int exponentDigitCount)
	{
		final String key = "scientific_"+Integer.toHexString(preferredIntegerDigitCount)
		+'_'+Integer.toHexString(minDecimalDigitCount)
		+'_'+Integer.toHexString(maxDecimalDigitCount)
		+'_'+Integer.toHexString(exponentDigitCount);
		DecimalFormat df;
		synchronized (numberFormats) {
			df = numberFormats.get(key);
		}
		if (df == null) {
			final DecimalFormatSymbols dfs = createDecimalFormatSymbols();

			final StringBuffer sb = new StringBuffer();
			for (int i = 1; i <= preferredIntegerDigitCount; ++i) {
				sb.append('0');
			}
			final String integerDigitCountPattern = sb.toString();
			final String decimalDigitCountPattern = createDecimalDigitCountPattern(minDecimalDigitCount, maxDecimalDigitCount);
			sb.setLength(0);
			for (int i = 1; i <= exponentDigitCount; ++i) {
				sb.append('0');
			}
			final String exponentDigitCountPattern = sb.toString();

			df = new DecimalFormat(
					defaultNumberFormatCfMod.getPositivePrefix()
					+'#'+integerDigitCountPattern
					+'.'+decimalDigitCountPattern
					+'E'+exponentDigitCountPattern
					+defaultNumberFormatCfMod.getPositiveSuffix()
					+';'
					+defaultNumberFormatCfMod.getNegativePrefix()
					+'#'+integerDigitCountPattern
					+'.'+decimalDigitCountPattern
					+'E'+exponentDigitCountPattern
					+defaultNumberFormatCfMod.getNegativeSuffix(),
					dfs);

			synchronized (numberFormats) {
				numberFormats.put(key, df);
			}
		}

		return null;
	}

}
