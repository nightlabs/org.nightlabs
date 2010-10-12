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

import java.math.BigDecimal;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public interface Currency
{
	/**
	 * Get the ISO currency ID.
	 * @return the international currency id (e.g. USD, EUR).
	 */
	String getCurrencyID();

	/**
	 * We manage currency values as integers (usually of type <code>long</code>) and therefore
	 * all values have to be divided (usually by 100) before being displayed.
	 * After parsing, the entered amount needs to be multiplied.
	 * The divisor/multiplicator is 10^decimalDigitCount.
	 *
	 * @return the number of digits after the decimal separator.
	 */
	int getDecimalDigitCount();

	/**
	 * Instead of displaying the international currency id, you might want to
	 * display the real currency symbol.
	 *
	 * @return the currency symbol (e.g. € or $).
	 */
	String getCurrencySymbol();

	/**
	 * <p>
	 * Returns the given amount in the double value of this currency.
	 * </p>
	 * <p>
	 *   amount / 10^(decimalDigitCount)
	 * <p>
	 *
	 * @param amount the amount to be converted.
	 * @return the approximate value as double - there might be rounding differences.
	 */
	double toDouble(long amount);

	/**
	 * <p>
	 * Convert the given amount to the long value of this currency.
	 * </p>
	 * <p>
	 *   amount * 10^(decimalDigitCount)
	 * <p>
	 *
	 * @param amount the amount to be converted.
	 * @return the approximate value as long - there might be rounding differences.
	 */
	long toLong(final double amount);

	/**
	 * <p>
	 * Convert the given amount to a {@link BigDecimal}.
	 * </p>
	 * <p>
	 * This method
	 * behaves like {@link #toDouble(long)} but without loosing
	 * information as a <code>BigDecimal</code> can be precise.
	 * </p>
	 * <p>
	 * The {@link BigDecimal#scale() result's scale} will be the same
	 * as the {@link #getDecimalDigitCount() decimalDigitCount} of this currency.
	 * </p>
	 *
	 * @param amount the amount to be converted (in the currency's sub-unit).
	 * @return floating-point value in the currency (e.g. 20.56 for the long value 2056 and the currency € [having a decimal digit count of 2]).
	 */
	BigDecimal toBigDecimal(long amount);

	/**
	 * <p>
	 * Convert the given amount from a {@link BigDecimal} to a <code>long</code>.
	 * </p>
	 * <p>
	 * This method behaves like {@link #toLong(double)}.
	 * </p>
	 *
	 * @param amount the amount to be converted.
	 * @return the approximate value as long (there might be rounding differences) in the currency's sub-unit (e.g. in Cent when using €).
	 */
	long toLong(BigDecimal amount);

}
