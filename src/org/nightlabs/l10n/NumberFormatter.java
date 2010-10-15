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

import java.text.ParseException;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 * @deprecated Use {@link INumberFormatter} and {@link GlobalNumberFormatter}.
 */
@Deprecated
public class NumberFormatter
{
	/**
	 * @param l The value.
	 * @deprecated Use the non-static methods in {@link INumberFormatter} via {@link GlobalNumberFormatter}.
	 */
	@Deprecated
	public static String formatInt(final long l)
	{
		return formatInt(l, 1);
	}

	/**
	 * @param l The value.
	 * @param minIntegerDigitCount The minimal number of digits.
	 * @deprecated Use the non-static methods in {@link INumberFormatter} via {@link GlobalNumberFormatter}.
	 */
	@Deprecated
	public static String formatInt(final long l, final int minIntegerDigitCount)
	{
		return GlobalNumberFormatter.sharedInstance().formatInt(l, minIntegerDigitCount);
	}

	/**
	 * @param d The value.
	 * @param decimalDigitCount The number of digits on the right side of the decimal separator.
	 * @deprecated Use the non-static methods in {@link INumberFormatter} via {@link GlobalNumberFormatter}.
	 */
	@Deprecated
	public static String formatFloat(final double d, final int decimalDigitCount)
	{
		return GlobalNumberFormatter.sharedInstance().formatFloat(d, decimalDigitCount);
	}

	/**
	 * @param d The value.
	 * @param minIntegerDigitCount The minimal number of digits on the left side of the decimal separator.
	 * @param minDecimalDigitCount The minimal number of digits on the right side of the decimal separator.
	 * @param maxDecimalDigitCount The maximal number of digits on the right side of the decimal separator.
	 * @deprecated Use the non-static methods in {@link INumberFormatter} via {@link GlobalNumberFormatter}.
	 */
	@Deprecated
	public static String formatFloat(final double d, final int minIntegerDigitCount, final int minDecimalDigitCount, final int maxDecimalDigitCount)
	{
		return GlobalNumberFormatter.sharedInstance().formatFloat(d, minIntegerDigitCount, minDecimalDigitCount, maxDecimalDigitCount);
	}

	/**
	 * @param d
	 * @param minIntegerDigitCount
	 * @param minDecimalDigitCount
	 * @param maxDecimalDigitCount
	 * @param exponentDigitCount
	 * @deprecated Use the non-static methods in {@link INumberFormatter} via {@link GlobalNumberFormatter}.
	 */
	@Deprecated
	public static String formatScientific(final double d, final int minIntegerDigitCount, final int minDecimalDigitCount, final int maxDecimalDigitCount, final int exponentDigitCount)
	{
		return GlobalNumberFormatter.sharedInstance().formatScientific(d, minIntegerDigitCount, minDecimalDigitCount, maxDecimalDigitCount, exponentDigitCount);
	}

	/**
	 * @param currencyValue The value.
	 * @param currency The currency.
	 * @deprecated Use the non-static methods in {@link INumberFormatter} via {@link GlobalNumberFormatter}.
	 */
	@Deprecated
	public static String formatCurrency(
			final long currencyValue, final Currency currency)
	{
		return GlobalNumberFormatter.sharedInstance().formatCurrency(currencyValue, currency);
	}

	/**
	 * @param currencyValue The value.
	 * @param currency The currency.
	 * @param includeCurrencySymbol Whether or not to include the currency symbol in the result string.
	 * @deprecated Use the non-static methods in {@link INumberFormatter} via {@link GlobalNumberFormatter}.
	 */
	@Deprecated
	public static String formatCurrency(
			final long currencyValue, final Currency currency, final boolean includeCurrencySymbol)
	{
		return GlobalNumberFormatter.sharedInstance().formatCurrency(currencyValue, currency, includeCurrencySymbol);
	}

	/**
	 * @param currencyValue The value.
	 * @param minIntegerDigitCount The minimal number of digits on the left side of the decimal separator.
	 * @param currency The currency.
	 * @deprecated Use the non-static methods in {@link INumberFormatter} via {@link GlobalNumberFormatter}.
	 */
	@Deprecated
	public static String formatCurrency(
			final long currencyValue, final int minIntegerDigitCount,
			final Currency currency)
	{
		return GlobalNumberFormatter.sharedInstance().formatCurrency(currencyValue, minIntegerDigitCount, currency);
	}

	/**
	 * @param currencyValue The value.
	 * @param minIntegerDigitCount The minimal number of digits on the left side of the decimal separator.
	 * @param currency The currency.
	 * @param includeCurrencySymbol Whether or not to include the currency symbol in the result string.
	 * @deprecated Use the non-static methods in {@link INumberFormatter} via {@link GlobalNumberFormatter}.
	 */
	@Deprecated
	public static String formatCurrency(
			final long currencyValue, final int minIntegerDigitCount,
			final Currency currency, final boolean includeCurrencySymbol)
	{
		return GlobalNumberFormatter.sharedInstance().formatCurrency(currencyValue, minIntegerDigitCount, currency, includeCurrencySymbol);
	}

	/**
	 * @param currencyValue The value.
	 * @param minIntegerDigitCount The minimal number of digits on the left side of the decimal separator.
	 * @param minDecimalDigitCount The minimal number of digits on the right side of the decimal separator.
	 * @param maxDecimalDigitCount The maximal number of digits on the right side of the decimal separator.
	 * @param currency The currency.
	 * @deprecated Use the non-static methods in {@link INumberFormatter} via {@link GlobalNumberFormatter}.
	 */
	@Deprecated
	public static String formatCurrency(
			final long currencyValue, final int minIntegerDigitCount,
			final int minDecimalDigitCount, final int maxDecimalDigitCount,
			final Currency currency)
	{
		return GlobalNumberFormatter.sharedInstance().formatCurrency(currencyValue, minIntegerDigitCount, minDecimalDigitCount, maxDecimalDigitCount, currency);
	}

	/**
	 * @param currencyValue The value.
	 * @param minIntegerDigitCount The minimal number of digits on the left side of the decimal separator.
	 * @param minDecimalDigitCount The minimal number of digits on the right side of the decimal separator.
	 * @param maxDecimalDigitCount The maximal number of digits on the right side of the decimal separator.
	 * @param currency The currency.
	 * @param includeCurrencySymbol Whether or not to include the currency symbol in the result string.
	 * @deprecated Use the non-static methods in {@link INumberFormatter} via {@link GlobalNumberFormatter}.
	 */
	@Deprecated
	public static String formatCurrency(
			final long currencyValue, final int minIntegerDigitCount,
			final int minDecimalDigitCount, final int maxDecimalDigitCount,
			final Currency currency, final boolean includeCurrencySymbol)
	{
		return GlobalNumberFormatter.sharedInstance().formatCurrency(currencyValue, minIntegerDigitCount, minDecimalDigitCount, maxDecimalDigitCount, currency, includeCurrencySymbol);
	}

	/**
	 * @deprecated Use the non-static methods in {@link INumberFormatter} via {@link GlobalNumberFormatter}.
	 */
	@Deprecated
	public static long parseCurrency(final String valueStr, final Currency currency, final boolean includeCurrencySymbol)
	throws ParseException
	{
		return GlobalNumberFormatter.sharedInstance().parseCurrency(valueStr, currency, includeCurrencySymbol);
	}

	/**
	 * @deprecated Use the non-static methods in {@link INumberFormatter} via {@link GlobalNumberFormatter}.
	 */
	@Deprecated
	public static double parseFloat(final String valueStr)
	throws ParseException
	{
		return GlobalNumberFormatter.sharedInstance().parseFloat(valueStr);
	}
}
