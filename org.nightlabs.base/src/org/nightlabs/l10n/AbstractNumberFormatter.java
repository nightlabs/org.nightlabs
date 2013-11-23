package org.nightlabs.l10n;

import java.text.NumberFormat;
import java.text.ParseException;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public abstract class AbstractNumberFormatter implements INumberFormatter {
	/**
	 * @param l The value.
	 */
	public String formatInt(final long l)
	{
		return formatInt(l, 1);
	}

	/**
	 * @param l The value.
	 * @param minIntegerDigitCount The minimal number of digits.
	 */
	public String formatInt(final long l, final int minIntegerDigitCount)
	{
		final NumberFormat numberFormat = getNumberFormatProvider().getIntegerFormat(
				minIntegerDigitCount);
		return numberFormat.format(l);
	}

	/**
	 * @param d The value.
	 * @param decimalDigitCount The number of digits on the right side of the decimal separator.
	 */
	public String formatFloat(final double d, final int decimalDigitCount)
	{
		return formatFloat(d, 1, decimalDigitCount, decimalDigitCount);
	}

	/**
	 * @param d The value.
	 * @param minIntegerDigitCount The minimal number of digits on the left side of the decimal separator.
	 * @param minDecimalDigitCount The minimal number of digits on the right side of the decimal separator.
	 * @param maxDecimalDigitCount The maximal number of digits on the right side of the decimal separator.
	 */
	public String formatFloat(final double d, final int minIntegerDigitCount, final int minDecimalDigitCount, final int maxDecimalDigitCount)
	{
		final NumberFormat numberFormat = getNumberFormatProvider().getFloatFormat(
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
	public String formatScientific(final double d, final int minIntegerDigitCount, final int minDecimalDigitCount, final int maxDecimalDigitCount, final int exponentDigitCount)
	{
		final NumberFormat numberFormat = getNumberFormatProvider().getScientificFormat(
				minIntegerDigitCount, minDecimalDigitCount, maxDecimalDigitCount, exponentDigitCount);
		return numberFormat.format(d);
	}

	// same method exists in org.nightlabs.base.ui.composite.CurrencyEdit
	private static long power(long base, int exponent) { // exponent >= 0
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
	public String formatCurrency(
			final long currencyValue, final Currency currency)
	{
		return formatCurrency(currencyValue, currency, true);
	}

	/**
	 * @param currencyValue The value.
	 * @param currency The currency.
	 * @param includeCurrencySymbol Whether or not to include the currency symbol in the result string.
	 */
	public String formatCurrency(
			final long currencyValue, final Currency currency, final boolean includeCurrencySymbol)
	{
		final int ddc = currency.getDecimalDigitCount();
		return formatCurrency(currencyValue, 1, ddc, ddc, currency,
				includeCurrencySymbol);
	}

	/**
	 * @param currencyValue The value.
	 * @param minIntegerDigitCount The minimal number of digits on the left side of the decimal separator.
	 * @param currency The currency.
	 */
	public String formatCurrency(
			final long currencyValue, final int minIntegerDigitCount,
			final Currency currency)
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
	public String formatCurrency(
			final long currencyValue, final int minIntegerDigitCount,
			final Currency currency, final boolean includeCurrencySymbol)
	{
		final int ddc = currency.getDecimalDigitCount();
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
	public String formatCurrency(
			final long currencyValue, final int minIntegerDigitCount,
			final int minDecimalDigitCount, final int maxDecimalDigitCount,
			final Currency currency)
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
	public String formatCurrency(
			final long currencyValue, final int minIntegerDigitCount,
			final int minDecimalDigitCount, final int maxDecimalDigitCount,
			final Currency currency, final boolean includeCurrencySymbol)
	{
		final NumberFormat numberFormat = getNumberFormatProvider().getCurrencyFormat(
				minIntegerDigitCount, minDecimalDigitCount, maxDecimalDigitCount,
				currency, includeCurrencySymbol);

		final double cvd = (double)currencyValue / power(10, currency.getDecimalDigitCount());
		return numberFormat.format(cvd);
	}

	public long parseCurrency(final String valueStr, final Currency currency, final boolean includeCurrencySymbol)
	throws ParseException
	{
		final NumberFormat numberFormat = getNumberFormatProvider().getCurrencyFormat(
				1, currency.getDecimalDigitCount(), currency.getDecimalDigitCount(),
				currency, includeCurrencySymbol);
		final double d = numberFormat.parse(valueStr).doubleValue();
		return Math.round(d * power(10, currency.getDecimalDigitCount()));
	}

	public double parseFloat(final String valueStr)
	throws ParseException
	{
		final NumberFormat numberFormat = getNumberFormatProvider().getFloatFormat(0, 0, 10);
		return numberFormat.parse(valueStr).doubleValue();
	}
}
