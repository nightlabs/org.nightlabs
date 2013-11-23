package org.nightlabs.l10n;

import java.text.ParseException;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public interface INumberFormatter {
	NumberFormatProvider getNumberFormatProvider();

	/**
	 * @param l The value.
	 */
	String formatInt(final long l);

	/**
	 * @param l The value.
	 * @param minIntegerDigitCount The minimal number of digits.
	 */
	String formatInt(final long l, final int minIntegerDigitCount);

	/**
	 * @param d The value.
	 * @param decimalDigitCount The number of digits on the right side of the decimal separator.
	 */
	String formatFloat(final double d, final int decimalDigitCount);

	/**
	 * @param d The value.
	 * @param minIntegerDigitCount The minimal number of digits on the left side of the decimal separator.
	 * @param minDecimalDigitCount The minimal number of digits on the right side of the decimal separator.
	 * @param maxDecimalDigitCount The maximal number of digits on the right side of the decimal separator.
	 */
	String formatFloat(
			final double d, final int minIntegerDigitCount, final int minDecimalDigitCount,
			final int maxDecimalDigitCount);

	/**
	 * @param d
	 * @param minIntegerDigitCount
	 * @param minDecimalDigitCount
	 * @param maxDecimalDigitCount
	 * @param exponentDigitCount
	 */
	String formatScientific(
			final double d, final int minIntegerDigitCount, final int minDecimalDigitCount,
			final int maxDecimalDigitCount, final int exponentDigitCount);

	/**
	 * @param currencyValue The value.
	 * @param currency The currency.
	 */
	String formatCurrency(
			final long currencyValue, final Currency currency);

	/**
	 * @param currencyValue The value.
	 * @param currency The currency.
	 * @param includeCurrencySymbol Whether or not to include the currency symbol in the result string.
	 */
	String formatCurrency(
			final long currencyValue, final Currency currency,
			final boolean includeCurrencySymbol);

	/**
	 * @param currencyValue The value.
	 * @param minIntegerDigitCount The minimal number of digits on the left side of the decimal separator.
	 * @param currency The currency.
	 */
	String formatCurrency(
			final long currencyValue, final int minIntegerDigitCount,
			final Currency currency);

	/**
	 * @param currencyValue The value.
	 * @param minIntegerDigitCount The minimal number of digits on the left side of the decimal separator.
	 * @param currency The currency.
	 * @param includeCurrencySymbol Whether or not to include the currency symbol in the result string.
	 */
	String formatCurrency(
			final long currencyValue, final int minIntegerDigitCount,
			final Currency currency, final boolean includeCurrencySymbol);

	/**
	 * @param currencyValue The value.
	 * @param minIntegerDigitCount The minimal number of digits on the left side of the decimal separator.
	 * @param minDecimalDigitCount The minimal number of digits on the right side of the decimal separator.
	 * @param maxDecimalDigitCount The maximal number of digits on the right side of the decimal separator.
	 * @param currency The currency.
	 */
	String formatCurrency(
			final long currencyValue, final int minIntegerDigitCount,
			final int minDecimalDigitCount, final int maxDecimalDigitCount,
			final Currency currency);

	/**
	 * @param currencyValue The value.
	 * @param minIntegerDigitCount The minimal number of digits on the left side of the decimal separator.
	 * @param minDecimalDigitCount The minimal number of digits on the right side of the decimal separator.
	 * @param maxDecimalDigitCount The maximal number of digits on the right side of the decimal separator.
	 * @param currency The currency.
	 * @param includeCurrencySymbol Whether or not to include the currency symbol in the result string.
	 */
	String formatCurrency(
			final long currencyValue, final int minIntegerDigitCount,
			final int minDecimalDigitCount, final int maxDecimalDigitCount,
			final Currency currency, final boolean includeCurrencySymbol);

	long parseCurrency(final String valueStr, final Currency currency, final boolean includeCurrencySymbol) throws ParseException;

	double parseFloat(final String valueStr) throws ParseException;
}
